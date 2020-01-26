package gameClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import algorithms.Graph_Algo;
import Server.Game_Server;
import utils.Point3D;
import utils.StdDraw;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Vertex;
import dataStructure.edge_data;
import dataStructure.node_data;





public class MyGameGUI implements Runnable{
	
	private game_service game;
	private int le;
	private DGraph arena;
	private Graph_Algo algo;
	
	private List<Fruit> fruits;
	
	private int robotNumber;
	private List<Robot> robots;
	
	private String mode;
	private Thread play;
	private TmpAutoGame auto;
	private manualGame manu;
	private Timer t;

	private KML_Logger kml;
	int id;
	int counter = 0;
	
	/**
	 * This class implements the GUI of the robot game.
	 * we used stdDraw as a base.
	 * @author Yossi Stein & Reuven Klavan
	 */
	
	public MyGameGUI() throws JSONException{
		
		JFrame f = new JFrame(" The Maze Waze Game ");
		mode = null;
		String level;
		
		JFrame login = new JFrame(" The Maze Waze Game  - Login");
		
		id = Integer.parseInt(JOptionPane.showInputDialog(login, " Enter ID Number:"));
		Game_Server.login(id);
        
		do{//only get a or A or m or m else will asking again and again
			mode = JOptionPane.showInputDialog(f, "Enter \"M\" for Manual or \"A\" for auto or \"B\" for your stats and \"R\" for your rank: ");
		}while(!mode.equals("A") && !mode.equals("M") && !mode.equals("a") && !mode.equals("m") && !mode.equals("B") && !mode.equals("b") && !mode.equals("R") && !mode.equals("r"));
		
		
		if(mode.equals("A") || mode.equals("a") || mode.equals("M") || mode.equals("m")){

			this.le=-1;
			do{//get number between [0-23] else will ask again and again
				level = JOptionPane.showInputDialog(f, "Selcet level [0,23]: ");
				le = Integer.parseInt(level);
			}while(le<0 && le>23);
        
        
			kml = new KML_Logger(le, game, id);// initiate KML_Logger
		
		
			try {
				game = Game_Server.getServer(Integer.parseInt(level));//initiate the game
				
				String graph = game.getGraph();
				this.arena = new DGraph();
				this.algo = new Graph_Algo();
		
				arena.init(graph);
				algo.init(arena);
		
				fruits = convertStringToFruit(game.getFruits());
			
		
				String info = game.toString();
				JSONObject line;
			
				try{
					line = new JSONObject(info);
					JSONObject ttt = line.getJSONObject("GameServer");
					robotNumber = ttt.getInt("robots");//getting the number of robots
				}
		
				catch (JSONException e) {e.printStackTrace();}
			
			
			
			
			
				auto = new TmpAutoGame(game,le);//create automatic and manual game for each case
				manu = new manualGame(game);
				
			
			
			
			
			
				StdDraw.setPenRadius(0.03);
				StdDraw.setPenColor(Color.MAGENTA);
				StdDraw.setCanvasSize(1000, 500);
				StdDraw.setXscale(35.186,35.214); 
				StdDraw.setYscale(32.099,32.110); 
				
				init(graph);
				paintingFruit(fruits);
			
			
				if(mode.equals("m") || mode.equals("M"))//initiate the robot in the game (put tham on the graph)
					manu.initiatRobot(robotNumber);
				
				else if (mode.equals("a") || mode.equals("A"))
					auto.initiatRobot();
			
			
			
			
				paintingFruit(fruits);
				robots = convertStringToRobot(game.getRobots()); 
				paintingRobot(robots);
			
			
			}
		
			catch (Exception e){
				e.printStackTrace();
				System.out.println("non valid input. input of level is [0,23]");
			}
        
			creatKMLGraph();
		}	
        witchMode();
	}

	/**
	 * this method Initiates the nodes and edges to a JSONarray from String from game info
	 * @param String g
	 */
	
	public void init(String g) {

		try {
			JSONObject jo = new JSONObject(g);
			JSONArray Jnodes = jo.getJSONArray("Nodes");
			JSONArray Jedges = jo.getJSONArray("Edges");
			DGraph dg = new DGraph();

			for (int i = 0; i < Jnodes.length(); i++) {
				JSONObject nody= (JSONObject) Jnodes.get(i);
				String location = (String) nody.getString("pos");
				String[] points = location.split(",");
				double x = Double.parseDouble(points[0]);
				double y = Double.parseDouble(points[1]);	
				

				int id = nody.getInt("id");
				StdDraw.setPenColor(Color.BLUE);
				StdDraw.setPenRadius(0.03);
				StdDraw.point(x, y);
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(0.1);
				Font font = new Font("Arial", Font.BOLD, 12);
				StdDraw.setFont(font);

				StdDraw.text(x, y+0.0002, Integer.toString(id));
				node_data n = new Vertex(id, x, y);
				addVertex(dg, n);
			
			}

			for (int i = 0; i < Jedges.length(); i++) {
				JSONObject edgeE = (JSONObject) Jedges.get(i);
				int src = edgeE.getInt("src");
				int dest = edgeE.getInt("dest");
				double weight = edgeE.getDouble("w");
				
				dg.connect(src, dest, weight);
				addEdge(dg , src, dest, weight);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch");
		} 
	}
	
	
	/**
	 * In the constructor we choose witch game to play here the choice has performed,
	 * by typing 'A' or 'M' in question box.
	 * sends the choice to the getAuto or to the getMenue methods in correlation.
	 * @throws JSONException
	 */
	
	public void witchMode() throws JSONException{
		if(mode.equals("m") || mode.equals("m"))
			getManu();
		
		else if(mode.equals("a") || mode.equals("A")){
			getAuto();
		}
		
		else if(mode.equals("b") || mode.equals("B")){
			myBestScore();
		}
		
		else if(mode.equals("r") || mode.equals("R")){
			myRank();
		}
	}
	
	
	private void myBestScore(){
		int[] best = SimpleDB.myStats(id);
		StringBuilder bs = new StringBuilder();
		for(int i = 0; i <= 23 ; i++){
			bs.append("for level:  " + i + ",  you got:  " + best[i]+"\n");
		}
		int gamePlayed = SimpleDB.gamePlayed(id);
		bs.append("and you have played: "+ gamePlayed);
		if(best[24] != 23)
			JOptionPane.showMessageDialog(null, "here is your game scores and the number of timed you played :\n"+bs.toString()+"games\n"+
		"you didn't won yet your highest level is: " + best[24]);
		
		else if(best[24] == 23){
			JOptionPane.showMessageDialog(null, "here is your game scores and the number of timed you played :\n"+bs.toString()+"games\n"+
					"you had won the game congrats");
		}
	}
	
	
	
	
	private void myRank(){
		int[] stages = {0,1,3,5,9,11,13,16,19,20,23};
		int[] ranks = SimpleDB.myRank(id);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < ranks.length; i++){
			sb.append("your position in level:  " + stages[i] + "  , is: " + ranks[i]+"\n");
		}
		JOptionPane.showMessageDialog(null, "here is your game scores rank: \n"+sb.toString());
	}

	/**
	 * This method starts the game as Manual mode.
	 * @throws JSONException
	 */
	
	private void getManu() throws JSONException{
		paintingRobot(convertStringToRobot(game.getRobots()));
		game.startGame();//starting manual game
		play = new Thread(this);
		play.start();//starting the thread
		
		ActionListener something = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					manu.moveRobot();
				}
				catch(JSONException e1){
					e1.printStackTrace();
				}
				
				if(!game.isRunning()){
					try {
						gameOver();
					} catch (InterruptedException | JSONException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		};
		
		t = new Timer(20, something);// for every 20 milliseconds the action above will repeat itself
		t.start();
	}
	
	/**
	 * This method starts the game as Automatic mode.
	 * @throws JSONException
	 */
	
	public void getAuto() throws JSONException{
		
		game.startGame();
		play = new Thread(this);
		play.start();
		
		
		ActionListener something = new ActionListener(){
			public void actionPerformed(ActionEvent e){
			
				try {
					
					auto.moveRobots();						
				} catch (JSONException e2) {
					e2.printStackTrace();
				}//the main logic action for moving the robots
				
						
			
			
				
				if(game.timeToEnd()<=0){
					
					try {
						String kmlInfo = gameOver();
						
						String info = game.toString();
						JSONObject line = new JSONObject(info);
						JSONObject ttt = line.getJSONObject("GameServer");
						int points = ttt.getInt("grade");
						int moves = ttt.getInt("moves");
						
						
						int[] myResult = SimpleDB.myStats(id);
						int[][] minRe = {{125,436,0,713,0,570,0,0,0,480,0,1050,0,310,0,0,235,0,0,250,200,0,0,1000}
						,{290,580,0,580,0,500,0,0,0,580,0,580,0,580,0,0,0,290,0,0,580,290,0,0,1140}};

						if(minRe[0][le]<=points && minRe[1][le]>=moves){
//							if(myResult[le] <= points){
							game.sendKML(kmlInfo);
//							}
						}
					}
					
					catch (InterruptedException | JSONException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		};
			
		int delay;
		if(this.le !=16)
			delay =115;
		
		else
			delay = 110;
			
		t = new Timer(delay,something);//every millisecond it will repeat itself this action
		t.start();
	}
	/**
	 * This method prints the score in the end of the game.
	 * the info is from game.toString().
	 * @throws InterruptedException
	 * @throws JSONException 
	 */

	private String gameOver() throws InterruptedException, JSONException{
		
		
		
		game.stopGame();
		t.stop();
		play.join();
		JFrame Show = new JFrame();
		JOptionPane.showMessageDialog(Show,"your stats is :" + game.toString() );
		String kmlFile = null;
		try {
			kmlFile = kml.endFile();
		} 
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return kmlFile;
	}
	/**
	 * This method adds the vertices to the data structure.	
	 * @param a
	 * @param n
	 */
	
	public static void addVertex(DGraph a, node_data n){
		a.addNode(n);
	}

	/**
	 * This method draws the edges on the graph.
	 * It gets a data structure, vertex source, vertex destination and weight.
	 * The direction of the edge is shown by an arrow at the edge (used by stdDraw).
	 * @param a
	 * @param src
	 * @param dest
	 * @param weight
	 */
	
	public static void addEdge(DGraph a, int src, int dest, double weight) {
		
		node_data tmp1 = a.getNode(src);
		double x1 =  tmp1.getLocation().x();
		double y1 = tmp1.getLocation().y();

		node_data tmp2 = a.getNode(dest);
		double x2 = tmp2.getLocation().x();
		double y2 = tmp2.getLocation().y();
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.setPenRadius(0.002);
		StdDraw.drawArrowLine(x1, y1, x2, y2);
		edge_data tmp3 = a.getEdge(src, dest);
		double text = tmp3.getWeight();
		text = (double)((int)(text*100)/100.0);
		StdDraw.text((x1+x2*2)/3, (y1+y2*2)/3, String.valueOf(text));
		
	}
	
	
	
	
	/**
	 * This method converts String from the game.getFruit().
	 * It gets a String from Json  and converts it into a fruit with:
	 * Value: the value of the fruit - the amount of points you get by eating the fruit.
	 * Type: if the fruit type is "1" the robot can eat it on the regular direction.
	 * 		 if the fruit type is "-1" the robot can eat it on the counter direction.
	 * Pos: the position of the given fruit.
	 * @param str
	 * @return
	 * @throws JSONException
	 */

	private List<Fruit> convertStringToFruit(List<String> str) throws JSONException{
		JSONObject f = new JSONObject();
		List<Fruit> output = new ArrayList<>();
		int index = 0;
		for(String s : str){
			f = new JSONObject(s);
			if(f != null){	
				JSONObject tmp = f.getJSONObject("Fruit");
				double value = tmp.getDouble("value");
				int type = tmp.getInt("type");
				String pos = tmp.getString("pos");
				Point3D point = new Point3D(pos);
				
				Fruit fru = new Fruit(point,value,type);
				output.add(index,fru);
				fruitKML(fru);
			}
				
		}	
		return output;
	}
	
	/**
	 * This method turns the fruit in to kml String.
	 * @param fru
	 */
	
	public void fruitKML(Fruit fru){
		if(kml != null){
			LocalDateTime date = LocalDateTime.now();
			if(fru.getType()>0)
				kml.addAMark(date, "apple", fru.getLocation());
			
			else
				kml.addAMark(date, "banana", fru.getLocation());
		}
	}
	

	/**
	 * This method converts String from the game.getRobot().
	 * It gets a String from Json  and converts it into a robot with:
	 * id, src, speed, dest, value & Pos.
	 * @param str
	 * @return
	 * @throws JSONException
	 */
	
	private List<Robot> convertStringToRobot(List<String> json) throws JSONException{
		List<Robot> output = new ArrayList<>();
		for(String r : json){
			JSONObject robots = new JSONObject(r);
			JSONObject robot = robots.getJSONObject("Robot");

			int id = robot.getInt("id");
			int src = robot.getInt("src");
			int dest = robot.getInt("dest");
			String pos = robot.getString("pos");
			Point3D location = new Point3D(pos);
			Robot tmp = new Robot(id,src,dest,location, arena);
			robotKML(tmp);
			output.add(tmp);
		}
		
		
		return output;
	}
	
	/**
	 * This method turns the robots in to kml String.
	 * @param bot
	 */
	
	public void robotKML(Robot bot){
		if(kml != null){
			LocalDateTime date = LocalDateTime.now();
			kml.addAMark(date, "robot", bot.getLocation());
		}
	}
	
	/**
	 * This method gets a list of robots and paints them on the graph in color CYAN.
	 * @param r
	 */
	
	public void paintingRobot(List<Robot> r){
		Iterator<Robot> iter = r.iterator();
		while(iter.hasNext()){
			Robot next = iter.next();
			StdDraw.setPenRadius(0.00003);
			StdDraw.setPenColor(Color.CYAN);
			Point3D l = next.getLocation();
			StdDraw.filledCircle(l.x(), l.y(), 0.0001);
		}
	}
	
	/**
	 * This method gets a list of fruit and paints them on the graph
	 * if the type of the fruit is "-1" in color yellow.
	 * and if the type of the fruit is"1" in color red.
	 * @param f
	 */
	
	public  void paintingFruit(List<Fruit> f){
		Iterator<Fruit> iter = f.iterator();
		while(iter.hasNext()){
			Fruit next = iter.next();
			StdDraw.setPenRadius(0.00003);
			if(next.getType() == -1){
				StdDraw.setPenColor(Color.yellow);
				Point3D l = next.getLocation();
				StdDraw.filledCircle(l.x(), l.y(), 0.0001);
			}
			
			else{
				StdDraw.setPenColor(Color.RED);
				Point3D l = next.getLocation();
				StdDraw.filledCircle(l.x(), l.y(), 0.0001);
			}
		}
	}
	
	
		
	/**
	 * This method paints the new graph every time it gets updated.
	 * it uses "DoubleBuffering" so it doesn't "flicker"
	 * so it checks what are the updates and repaints only them.
	 * @throws JSONException
	 */
	
	public void updatePainting() throws JSONException{
		StdDraw.enableDoubleBuffering();
		Collection<node_data> vertices = arena.getV();
		for(node_data v : vertices){
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.03);
			StdDraw.point(v.getLocation().x(), v.getLocation().y());
			StdDraw.text(v.getLocation().x(), v.getLocation().y()+0.0002, Integer.toString(v.getKey()));
			StdDraw.setPenColor(Color.BLACK);
			StdDraw.setPenRadius(0.1);
			Font font = new Font("Arial", Font.BOLD, 12);
			StdDraw.setFont(font);

			StdDraw.text(v.getLocation().x(), v.getLocation().y()+0.0002, Integer.toString(v.getKey()));
			
		}	
			
		for(node_data src : vertices){
			Collection<edge_data> edges = arena.getE(src.getKey());
			for(edge_data e : edges){ 
				StdDraw.setPenRadius(0.003);
				StdDraw.setPenColor(Color.BLACK);
				node_data dest = arena.getNode(e.getDest());
				StdDraw.drawArrowLine(src.getLocation().x(), src.getLocation().y(), dest.getLocation().x(), dest.getLocation().y());
				addEdge(arena , src.getKey(), dest.getKey(), e.getWeight());

			}
		}
		StdDraw.text(35.19, 32.100, "time to end " + String.valueOf(game.timeToEnd()/1000));
		fruits = convertStringToFruit(game.getFruits());
		robots = convertStringToRobot(game.getRobots());
		
		paintingRobot(robots);
		paintingFruit(fruits);
		
		
		StdDraw.pause(150);
		StdDraw.show();
		StdDraw.clear();
	}
	

	/**
	 * This method turns the graph in to a mkl String.
	 */
	
	public void creatKMLGraph(){
		
		if(kml != null){
			
			Collection<node_data> vertices = arena.getV();
			for(node_data v : vertices){
				LocalDateTime date = LocalDateTime.now();
				kml.addAMark(date, "node", v.getLocation());//adding for the kml file all the nodes location
			}
			
			
			for(node_data src : vertices){
				Collection<edge_data> edges = arena.getE(src.getKey());
				for(edge_data e : edges){
					node_data dest = arena.getNode(e.getDest());
					kml.addAPaths(src.getLocation(), dest.getLocation());//adding for the kml file all the edges location
				}
			}
		}
	}
	
	/**
	 * This method starts the thread of painting the graph, robots and fruits.
	 */
	@Override
	public void run() {
		while(game.isRunning()){
			try {
				
				updatePainting();
				
				
			}
			
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
}