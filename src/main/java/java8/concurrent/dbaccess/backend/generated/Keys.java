/**
 * This class is generated by jOOQ
 */
package java8.concurrent.dbaccess.backend.generated;


import java8.concurrent.dbaccess.backend.generated.tables.Mentions;
import java8.concurrent.dbaccess.backend.generated.tables.TwitterUser;
import java8.concurrent.dbaccess.backend.generated.tables.records.MentionsRecord;
import java8.concurrent.dbaccess.backend.generated.tables.records.TwitterUserRecord;

import javax.annotation.Generated;

import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>PUBLIC</code> 
 * schema
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final UniqueKey<TwitterUserRecord> CONSTRAINT_5 = UniqueKeys0.CONSTRAINT_5;
	public static final UniqueKey<MentionsRecord> CONSTRAINT_A = UniqueKeys0.CONSTRAINT_A;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class UniqueKeys0 extends AbstractKeys {
		public static final UniqueKey<TwitterUserRecord> CONSTRAINT_5 = createUniqueKey(TwitterUser.TWITTER_USER, TwitterUser.TWITTER_USER.USER_ID);
		public static final UniqueKey<MentionsRecord> CONSTRAINT_A = createUniqueKey(Mentions.MENTIONS, Mentions.MENTIONS.TWEET_ID);
	}
}
