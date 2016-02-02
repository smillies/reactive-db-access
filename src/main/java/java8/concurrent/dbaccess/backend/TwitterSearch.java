package java8.concurrent.dbaccess.backend;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;

public class TwitterSearch {
    
    // The factory instance is re-useable and thread safe.
    // twitter4j.properties must be on the root of the classpath (project dir)
    private static final TwitterFactory tf = new TwitterFactory();

    public QueryResult search(String query) throws TwitterException {
        Twitter twitter = tf.getInstance();
        @SuppressWarnings("unused") // need to get token to authenticate
        OAuth2Token token = twitter.getOAuth2Token();
        Query q = new Query(query);
        QueryResult result = twitter.search(q);
        return result;
    }

    // test driver
    public static void main(String[] args) throws TwitterException {
        TwitterSearch twitterSearch = new TwitterSearch();
        QueryResult result = twitterSearch.search("@randfish");
        System.out.println("Found " + result.getCount() + " tweets");
        for (Status status : result.getTweets()) {
            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }
    }
}
