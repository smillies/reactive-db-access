package java8.concurrent.dbaccess.backend;

import static java8.concurrent.dbaccess.backend.generated.tables.Mentions.MENTIONS;
import static java8.concurrent.dbaccess.backend.generated.tables.TwitterUser.TWITTER_USER;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java8.concurrent.dbaccess.model.Mention;
import java8.concurrent.dbaccess.model.User;

/** DB-specific data access. */
public class MentionsDAO {

    private final Database db;
    private final SQLDialect sqlDialect;

    public MentionsDAO(Database db, SQLDialect sqlDialect) {
        this.db = db;
        this.sqlDialect = sqlDialect;
    }
    
    public void insertMentions(List<Mention> mentions) throws SQLException {
        try(Connection connection = db.getConnection()) {
            DSLContext context = DSL.using(connection, sqlDialect);
           
            mentions.forEach( mention -> {   
               
               // the mentioning user  
                insertUser(context, mention.getFromUser());
           
                // the mentioned users
                mention.getUsers().forEach( user -> insertUser(context, user) );
           
                // the mention
                context.insertInto(MENTIONS, MENTIONS.TWEET_ID, MENTIONS.CREATED_ON, MENTIONS.TEXT, MENTIONS.USER_NAME)
                    .values(mention.getId(), Timestamp.from(Instant.now()), mention.getText(), mention.getFromUser().getName())
                    .onDuplicateKeyIgnore()
                    .execute();
              });
        }
    }
    
    
    private void insertUser(DSLContext context, User user) {
        context.insertInto(TWITTER_USER, TWITTER_USER.USER_ID, TWITTER_USER.USER_NAME, TWITTER_USER.CREATED_ON)
            .values(user.getId(), user.getName(), Timestamp.from(Instant.now()))
            .onDuplicateKeyIgnore()
            .execute();
    }
}
