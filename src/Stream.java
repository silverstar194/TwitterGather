import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vdurmont.emoji.EmojiParser;

import database.DataBase;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


/**
 * @author Admin
 *
 */

public class Stream {
	/**Search Query*/
	private String q;
	
	/**Number of Query to run at max*/
	private int count;

	public Stream(String q, int count){

		//make sure query amount is within limits
		if(count > 100){
			throw new IllegalArgumentException("Must be 100 or less");
		}

		this.q = q;
		this.count = count;
	}


	public void getStream() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		//built twitter connection
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(Config.TWITTER_CONSUMER_KEY)
		.setOAuthConsumerSecret(Config.TWITTER_SECRET_KEY)
		.setOAuthAccessToken(Config.TWITTER_ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(Config.TWITTER_ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
			
			Query query = new Query(q);
			query.setCount(count);
			QueryResult result;
			do {
				DataBase dataBase = new DataBase();

				Connection dataBaseConn = dataBase.getConnection();

				//query Twitter
				result = twitter.search(query);

				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {

					//strip out emojis replacing with plain text
					String content = EmojiParser.parseToAliases(tweet.getText()).replace("'", "''");

					//insert into MYSQL
					String command = "INSERT INTO `twitterDB`.`Tweet`(`search`,`content`,`isTruc`,`retweetCount`,`favoriteCount`,`lang`,`createdAt`)VALUES('"+q.replace("'", "''")+"','"+content+"',"+tweet.isTruncated()+",'"+tweet.getRetweetCount()+"','"+tweet.getFavoriteCount()+"','"+tweet.getLang()+"','"+tweet.getCreatedAt()+"')";

					System.out.println("Tweet Added for "+ q);

					try{
						dataBase.executeUpdate(dataBaseConn, command);
					}catch(SQLException e){
						
						//if some emojis were missed catch them here
						System.out.println(e.getMessage());
					}
				}
			} while ((query = result.nextQuery()) != null);

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}
}