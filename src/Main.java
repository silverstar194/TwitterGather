import java.sql.SQLException;

/**
 * @author Admin
 *
 */
public class Main {

	public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{

		for(int i =0; i<50; i++){
			
			//start stream in twitter feed for each search term restricting reach to 45 queries
			//total of 180 (to comply with Twitter API limits)
			Stream trump = new Stream("trump", 45);
			trump.getStream();
			Thread.sleep(3*1000);
			
			
			Stream hilery = new Stream("hillary", 45);
			hilery.getStream();
			Thread.sleep(3*1000);
			
			Stream berine = new Stream("berine", 45);
			berine.getStream();
			Thread.sleep(3*1000);
			
			Stream cruz = new Stream("cruz", 45);
			cruz.getStream();

			
			System.out.println("Iterations "+i +" of 50");
	
			//run every 5 minute 
			Thread.sleep(15*60*1000);
		}

	}

}
