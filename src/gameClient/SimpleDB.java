package gameClient;
		
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
/**
 * This class represents a MySQL Data-Base.
 * @author Yossi Schtien & Reuven Klavan
 *
 */
public class SimpleDB {
	public static final String jdbcUrl="jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser="student";
	public static final String jdbcUserPassword="OOP2020student";
	public static HashMap<Integer, Integer[]> results = new HashMap<>();
	public static boolean initiate = false; 
	
	private static void initMinResult(){
		if(!initiate){
			results.put((Integer)0, new Integer[]{125, 290});
			results.put((Integer)1, new Integer[]{436, 580});
			results.put((Integer)3, new Integer[]{713, 580});
			results.put((Integer)5, new Integer[]{570, 500});
			results.put((Integer)9, new Integer[]{480, 580});
			results.put((Integer)11, new Integer[]{1050, 580});
			results.put((Integer)13, new Integer[]{310, 580});
			results.put((Integer)16, new Integer[]{235, 290});
			results.put((Integer)19, new Integer[]{250, 580});
			results.put((Integer)20, new Integer[]{200, 290});
			results.put((Integer)23, new Integer[]{1000, 1140});
			initiate = true;
		}	
	}
	
	
	/**
	 * Simple main for demonstrating the use of the Data-base
	 * @param args
	 */
	public static void main(String[] args) {
			int id1 = 205416365;  // "real" existing ID & KML
			int id2 = 206128613;
			int level = 16;//1,2,3
			//printLog(id2);
			//allUsers();	
			String kml1 = getKML(id1,level);
			System.out.println("***** KML1 file example: ******");
			System.out.println(kml1);
		}
	/** simply prints all the games as played by the users (in the database).
	 * 
	 */
		public static void printLog(int id) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				String allCustomersQuery = "SELECT * FROM Logs where userID="+id;
			
				ResultSet resultSet = statement.executeQuery(allCustomersQuery);
				int ind =0;
				while(resultSet.next())
				{
					System.out.println(ind+") Id: " + resultSet.getInt("UserID")+", level: "+resultSet.getInt("levelID")+", score: "+resultSet.getInt("score")+", moves: "+resultSet.getInt("moves")+", time: "+resultSet.getDate("time"));
					ind++;
				}
				resultSet.close();
				statement.close();		
				connection.close();		
			}
			
			catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	/**
	 * this function returns the KML string as stored in the database (userID, level);
	 * @param id
	 * @param level
	 * @return
	 */
			public static String getKML(int id, int level) {
				String ans = null;
				String allCustomersQuery = "SELECT * FROM Users where userID="+id+";";
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
					Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(allCustomersQuery);
					if(resultSet!=null && resultSet.next()) {
						ans = resultSet.getString("kml_"+level);
					}
				}
				catch (SQLException sqle) {
					System.out.println("SQLException: " + sqle.getMessage());
					System.out.println("Vendor Error: " + sqle.getErrorCode());
				}
				
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return ans;
			}
			
			
		public static int allUsers() {
			int ans = 0;
			String allCustomersQuery = "SELECT * FROM Users;";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(allCustomersQuery);
				while(resultSet.next()) {
					System.out.println("Id: " + resultSet.getInt("UserID")+", max_level:"+resultSet.getInt("levelNum"));
					ans++;
				}
				resultSet.close();
				statement.close();		
				connection.close();
			}
			catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}
			
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return ans;
		}
		
		/**
		 * This method counts the amount of games the player have played
		 * @param id
		 * @return
		 */
		
	public static int gamePlayed(int id){	
		int counter =0;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where userID="+id;
		
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while(resultSet.next()){
				counter++;
			}
			
			resultSet.close();
			statement.close();		
			connection.close();	
			

		}
		
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return counter;
	}
	
	/**
	 * This method  gets ID and returns the players highest level he reached.
	 * it returns all of the highest scores of all of the played levels.
	 * it shows the highest score on the levels that are not in the game as well but
	 * without a low amount of steps. 
	 * @param id
	 * @return
	 */
	
	public static int[] myStats(int id) {
		int[] myBest = new int[25];
		initMinResult();
		boolean minRe = false;
		int maxLevel = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where userID="+id;
		
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while(resultSet.next()){
				
				Integer stageNumber = resultSet.getInt("levelID");
				if(stageNumber != null){
					Integer[] requirement = results.get(stageNumber);
					int score = resultSet.getInt("score");
					int move = resultSet.getInt("moves");
					
					if(requirement[0] <= score && requirement[1] >= move){
						minRe = true;
					}
					
					if(stageNumber>maxLevel && minRe){
						maxLevel = stageNumber;
					}
					
					if(myBest[stageNumber] < score && minRe){
						myBest[stageNumber] = score;
					}
					minRe = false;

				}	
			}
			myBest[24] = maxLevel;
			resultSet.close();
			statement.close();		
			connection.close();		
		}
		
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return myBest;
	}
	
	/**
	 * This methods shows the players rank among all of the other players (witch past) best scores.
	 * @param id
	 * @return
	 */
	
	public static int[] myRank(int id){
		
		int[] stageScore = new int[11];
		int[] myStats = myStats(id);
		int[] stages = {0,1,3,5,9,11,13,16,19,20,23};
		int currentStage=0;
		boolean found = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where levelID="+currentStage+" order by score DESC;";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			int i = 0;
			for(currentStage = 0; i < stages.length; currentStage = stages[i++]){
				
				found = false;
				allCustomersQuery = "SELECT * FROM Logs where levelID="+currentStage+" order by score DESC;";
				resultSet = statement.executeQuery(allCustomersQuery);
				int place = 1;
				while(resultSet.next() && !found){
					int score = resultSet.getInt("score");
					int move = resultSet.getInt("moves");
					int stealingID = resultSet.getInt("UserID");
					Integer[] tmp = results.get(currentStage);
					if(tmp[0] <= score && tmp[1] >= move && !found){
						
						if(id == stealingID){
							found =true;
							stageScore[i] = place;
						}
						
						else{
							place++;
						}
					}
				}
				
			}
			
			resultSet.close();
			statement.close();		
			connection.close();		
		}
		
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return stageScore;
	}
}
		
