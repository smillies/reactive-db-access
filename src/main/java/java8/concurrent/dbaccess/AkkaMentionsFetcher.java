package java8.concurrent.dbaccess;

import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import org.jooq.SQLDialect;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import java8.concurrent.dbaccess.backend.Database;
import java8.concurrent.dbaccess.backend.H2Database;
import java8.concurrent.dbaccess.backend.MentionsDAO;
import java8.concurrent.dbaccess.backend.TwitterSearch;
import java8.concurrent.dbaccess.model.Mention;
import java8.concurrent.dbaccess.model.User;
import scala.concurrent.duration.Duration;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

public class AkkaMentionsFetcher extends AbstractActor implements PipeToEnabled {

    // in-memory database
    private static final Database DB = new H2Database();
    
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final String userHandle;
    private Date lastSeen = new Date(System.currentTimeMillis() - 6_000_000) ;
    
    public AkkaMentionsFetcher(String userHandle) {
        this.userHandle = userHandle;
      //@formatter:off
        // define the actor's behavior
        receive(ReceiveBuilder
                .match(CheckMentions.class, _m -> checkMentions())
                .match(MentionsReceived.class, received -> storeMentions(received.mentions))
                .build());
    //@formatter:on 

    }

    // schedule to send the CheckMentions message to ourselves after 5 seconds every minute
    // cf. http://doc.akka.io/docs/akka/snapshot/java/howto.html
    private final Cancellable check = getContext().system().scheduler()
            .schedule(Duration.create(5, SECONDS), Duration.create(1, MINUTES),  self(), new CheckMentions(), getContext().dispatcher(), null);

    @Override
    public void postStop() {
        check.cancel();
    }

    private void checkMentions() {
        Date time = lastSeen;
        lastSeen = new Date();
        CompletableFuture<List<Mention>> futureQuery = fetchMentions(userHandle, time);
        pipeTo(self(), futureQuery.exceptionally(t -> emptyList()).thenApply(received -> new MentionsReceived(received)));
    }

    // fetch Twitter data asynchronously
    private CompletableFuture<List<Mention>> fetchMentions(String query, Date time) {
        log.info("Fetching mentions after {}", time.toString());
        Executor executionContext = context().dispatcher();
        CompletableFuture<List<Mention>> futureQuery = CompletableFuture.supplyAsync(() -> {
            try {
                List<Mention> mentions = searchTwitter(query, time);
                log.info("Fetched {} mentions: {}", mentions.size(), mentions);
                return mentions;
            }
            catch (TwitterException | RuntimeException e) {
                log.error(e, "Could not fetch mentions");
                throw new CompletionException(e);
            }
        }, executionContext);
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
        Executor executionContext =  context().system().dispatchers().lookup("contexts.database"); 
        CompletableFuture<Void> futureStorage = CompletableFuture.runAsync(() -> {
            // synchronous DB update
           log.info("Storing mentions");
           try {
               updateDB(mentions);
           }
           catch (SQLException | RuntimeException e) {
               log.error(e, "Could not store mentions");
               throw new CompletionException(e);
           }
        }, executionContext);
        
        return futureStorage;
    }
    
    // DB-access with JDBC (blocking)
    private void updateDB(List<Mention> mentions) throws SQLException {
        MentionsDAO dao = new MentionsDAO(DB, SQLDialect.H2);
        dao.insertMentions(mentions);
    }
}

/* The following classes constitute the protocol (message inventory) of the MentionsFetcher actor */

class CheckMentions {
}

class MentionsReceived {
    List<Mention> mentions;

    MentionsReceived(List<Mention> mentions) {
        this.mentions = mentions;
    }
}
