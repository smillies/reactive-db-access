/**
 * This class is generated by jOOQ
 */
package java8.concurrent.dbaccess.backend.generated.tables.records;


import java.sql.Timestamp;

import java8.concurrent.dbaccess.backend.generated.tables.TwitterUser;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TwitterUserRecord extends UpdatableRecordImpl<TwitterUserRecord> implements Record3<Long, String, Timestamp> {

	private static final long serialVersionUID = 240736686;

	/**
	 * Setter for <code>PUBLIC.TWITTER_USER.USER_ID</code>.
	 */
	public void setUserId(Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>PUBLIC.TWITTER_USER.USER_ID</code>.
	 */
	public Long getUserId() {
		return (Long) getValue(0);
	}

	/**
	 * Setter for <code>PUBLIC.TWITTER_USER.USER_NAME</code>.
	 */
	public void setUserName(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>PUBLIC.TWITTER_USER.USER_NAME</code>.
	 */
	public String getUserName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>PUBLIC.TWITTER_USER.CREATED_ON</code>.
	 */
	public void setCreatedOn(Timestamp value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>PUBLIC.TWITTER_USER.CREATED_ON</code>.
	 */
	public Timestamp getCreatedOn() {
		return (Timestamp) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Long> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Long, String, Timestamp> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Long, String, Timestamp> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field1() {
		return TwitterUser.TWITTER_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return TwitterUser.TWITTER_USER.USER_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return TwitterUser.TWITTER_USER.CREATED_ON;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long value1() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getUserName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getCreatedOn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TwitterUserRecord value1(Long value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TwitterUserRecord value2(String value) {
		setUserName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TwitterUserRecord value3(Timestamp value) {
		setCreatedOn(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TwitterUserRecord values(Long value1, String value2, Timestamp value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TwitterUserRecord
	 */
	public TwitterUserRecord() {
		super(TwitterUser.TWITTER_USER);
	}

	/**
	 * Create a detached, initialised TwitterUserRecord
	 */
	public TwitterUserRecord(Long userId, String userName, Timestamp createdOn) {
		super(TwitterUser.TWITTER_USER);

		setValue(0, userId);
		setValue(1, userName);
		setValue(2, createdOn);
	}
}
