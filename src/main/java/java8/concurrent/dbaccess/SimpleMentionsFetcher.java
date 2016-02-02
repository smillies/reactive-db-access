package java8.concurrent.dbaccess;

import static java.util.Collections.emptyList;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.toList;
import static util.Threads.newThreadFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jooq.SQLDialect;

import java8.concurrent.dbaccess.backend.Database;
import java8.concurrent.dbaccess.backend.H2Database;
import java8.concurrent.dbaccess.backend.MentionsDAO;
import java8.concurrent.dbaccess.backend.TwitterSearch;
import java8.concurrent.dbaccess.model.Mention;
import java8.concurrent.dbaccess.model.User;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

// Version without Akks
public class SimpleMentionsFetcher {
    
    private static Logger log = Logger.getLogger("SimpleMentionsFetcher");

    // in-memory database
    private static final Database DB = new H2Database();
    
    // we use the common F/J-pool for accessing Twitter, but need two additional pools, one for DB access and one for scheduling queries
    private static final int DB_THREADS = 5;
    private static final Executor DB_THREAD_POOL = Executors.newFixedThreadPool(DB_THREADS, newThreadFactory("mentions-storage-%d", true));
    
    private static final int TIMER_THREADS = 1;
    private static final ScheduledExecutorService SCHEDULER = createThreadPool();

    private static ScheduledExecutorService createThreadPool() {
        ScheduledThreadPoolExecutor scheduledThreadPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(TIMER_THREADS,
                newThreadFactory("mentions-fetcher-%d", true));
        scheduledThreadPool.setRemoveOnCancelPolicy(true); // make sure cancelled tasks are removed from queue immediately
        return scheduledThreadPool;
    }
    
    public static void main(String[] args) throws Exception {
        new SimpleMentionsFetcher(args[0]);
        Thread.sleep(900_000); // terminate after 15 minutes
    }
    
    private final String userHandle;
    private Date lastSeen = new Date(System.currentTimeMillis() - 6_000_000) ;
    
    public SimpleMentionsFetcher(String userHandle) {
        this.userHandle = userHandle;
       // schedule to check mentions after 5 seconds every minute
        SCHEDULER.scheduleAtFixedRate(() -> checkMentions(), 5, 60, TimeUnit.SECONDS);
    }

    private void checkMentions() {
        Date time = lastSeen;
        lastSeen = new Date();
        fetchMentions(userHandle, time).exceptionally(t -> emptyList()).thenCompose(this::storeMentions);
    }
    
    // fetch Twitter data asynchronously
    private CompletableFuture<List<Mention>> fetchMentions(String query, Date time) {
        log.log(INFO, "Fetching mentions after {0}", time.toString());
        CompletableFuture<List<Mention>> futureQuery = CompletableFuture.supplyAsync(() -> {
            try {
                List<Mention> mentions = searchTwitter(query, time);
                log.log(INFO, "Fetched {0} mentions: {1}", new Object[]{mentions.size(), mentions});
                return mentions;
            }
            catch (TwitterException | RuntimeException e) {
                log.log(SEVERE, "Could not fetch mentions: {0}", e);
                throw new CompletionException(e);
            }
        });
        return futureQuery;
    }

    // simple blocking method to retrieve data from Twitter
    private List<Mention> searchTwitter(String query, Date time) throws TwitterException {
        TwitterSearch twitterSearch = new TwitterSearch();
        QueryResult result = twitterSearch.search(query);
        List<Mention> mentions =
            result.getTweets().stream()
                .filter( s -> s.getCreatedAt().after(time))
                .map(s -> new Mention(s.getId(), s.getCreatedAt(), s.getText(), new User(s.getUser().getId(), s.getUser().getScreenName()), 
                                        Arrays.stream(s.getUserMentionEntities())
                                            .map(u -> new User(u.getId(), u.getScreenName()))
                                            .collect(toList())))
                .collect(toList());
        return mentions;
    }

    // asynchronous DB access
    private CompletableFuture<Void> storeMentions(List<Mention> mentions) {
        CompletableFuture<Void> futureStorage = CompletableFuture.runAsync(() -> {
            log.log(INFO, "Storing mentions");
            try {
                updateDB(mentions);
            }
            catch (SQLException | RuntimeException e) {
                log.log(SEVERE, "Could not store mentions: {0}", e);
                throw new CompletionException(e);
            }
        } , DB_THREAD_POOL);

        return futureStorage;
    }

    // DB-access with JDBC (blocking)
    private void updateDB(List<Mention> mentions) throws SQLException {
        MentionsDAO dao = new MentionsDAO(DB, SQLDialect.H2);
        dao.insertMentions(mentions);
    }
}
