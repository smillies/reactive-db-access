CREATE TEMPORARY TABLE twitter_user (
  user_id bigint NOT NULL PRIMARY KEY,
  user_name varchar(20) NOT NULL,
  created_on timestamp NOT NULL,
);
 
CREATE TEMPORARY TABLE mentions (
  tweet_id bigint NOT NULL PRIMARY KEY,
  created_on timestamp NOT NULL,
  text varchar(160) NOT NULL,
  user_name varchar(20) NOT NULL
);
