package gameClient;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;




import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;	
import algorithms.RobotAlgo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

	/**
	 * This class manages the automatic mode.
	 * the game can not start without creating all of the robots of
	 * that level so there is a method that initiates all of the required robots.
	 * @author Yossi & Reuven
	 *
	 */


	public class TmpAutoGame{
		
		private List<Fruit> fruits;
		private DGraph arena;
		private Graph_Algo algo;
		private game_service game;
		private List<Robot> robots;
		private int robotNumber;
		private double[][] dis;
		private FloydWarshallSolver solver;
		private List<Integer> robotPath;
		private List<List<Integer>> robotsPath;
		private int level;
		/**
		 * This method initiates the game.
		 * Parses the "game String" in to a Json object.
		 * initiates the fruits and robots array lists.
		 * @param game
		 * @throws JSONException
		 */
		
		
	public TmpAutoGame(game_service game, int level) throws JSONException{
		this.game = game;
		this.level = level;
		String g = game.getGraph();
		arena = new DGraph();
		arena.init(g);
			
		double[][] tmp1 = new double[arena.nodeSize()][arena.nodeSize()];
		dis = new double[arena.nodeSize()][arena.nodeSize()];

		dis = preperingForFW(tmp1);
		solver = new FloydWarshallSolver(dis);
		dis = solver.getApspMatrix();
		
		algo = new Graph_Algo();
		algo.init(arena);
			
		List<Integer> robot0 = new ArrayList<>();
		List<Integer> robot1 = new ArrayList<>();
		List<Integer> robot2 = new ArrayList<>();

		robotsPath = new ArrayList<>();
		
		robotsPath.add(robot0);
		robotsPath.add(robot1);
		robotsPath.add(robot2);

		
		
		robotPath = new ArrayList<>();
		fruits = new ArrayList<>();
		robots = new ArrayList<>();
			
		String info = game.toString();
		JSONObject line;
		try{// getting the number of robot in this level
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			robotNumber = ttt.getInt("robots");
		
		}
			
		catch (JSONException e) {e.printStackTrace();}
			
	}
		
		
	public double[][] preperingForFW(double[][] dis){
		
		for(int i = 0; i <dis.length; i++){
			dis[i][i] = 0;
		}
		
		for(int i = 0; i <dis.length; i++){
			for(int j = 0; j <dis.length; j++){
				if(i != j){
					dis[i][j] = Double.MAX_VALUE;
				}	
			}	
		}
		
		Collection<node_data> list = arena.getV();
		
		for(node_data src: list){
			Collection<edge_data> edges = arena.getE(src.getKey());
			for(edge_data e : edges){
				node_data dest = arena.getNode(e.getDest());
				dis[src.getKey()][dest.getKey()] = e.getWeight(); 
			}
		}
		
		return dis;
	}	
	
	public void jamming(){
		if(robotPath.get(robotPath.size()-1) == robotPath.get(robotPath.size()-3) && robotPath.get(robotPath.size()-1) == robotPath.get(robotPath.size()-5)) 
			if(robotPath.get(robotPath.size()-2) == robotPath.get(robotPath.size()-4) && robotPath.get(robotPath.size()-2) == robotPath.get(robotPath.size()-6)){
				Fruit tmp = fruits.get(0);
				fruits.remove(tmp);
				fruits.add(0, fruits.get(fruits.size()-1));
				fruits.add(tmp);
				
			}
	}			
	
	public void exteamJam(List<Integer> rp, Robot i){
		if(rp.get(rp.size()-1) == rp.get(rp.size()-3) && rp.get(rp.size()-2) == rp.get(rp.size()-4)){
			if(rp.get(rp.size()-1) == 32){
				Fruit tmp = fruits.get(0);
				fruits.set(0, fruits.get(fruits.size()-3));
				fruits.add(tmp);
			}
				
			else{
				List<node_data> tmp = new ArrayList<>();
				tmp.add(arena.getNode(32));
				tmp.add(arena.getNode(33));
				tmp.add(arena.getNode(34));
				i.setPath(tmp);
					
			}	
		}
	}
	
	public List<Fruit> findRatio(List<Fruit> fruitSet, Robot tmp){
		int robotSrc = tmp.getSrc();
		List<edge_data> edges = new ArrayList<>();
		for(Fruit f : fruitSet){
			edge_data e = RobotAlgo.findEdge(arena, f);
			edges.add(e);
		}
		
		Iterator<Fruit> iter1 = fruitSet.iterator();
		Iterator<edge_data> iter2 = edges.iterator();

		
		while(iter1.hasNext()){
			Fruit f1 = iter1.next();
			edge_data e1 = iter2.next();
			if(robotSrc != e1.getSrc()){
				double distance = dis[robotSrc][e1.getSrc()];
				f1.setRatio(f1.getValue()/distance);
			}
			
			else{
				double distance = dis[robotSrc][e1.getDest()];
				f1.setRatio(f1.getValue()/distance);
			}
		}
		fruitComperator<Fruit> sort = new fruitComperator<>();
		fruitSet.sort(sort);
		return fruitSet;
	}
		
	/**
	 * This method moves the robots every step to the next closest fruit.
	 * it  uses the "Triangle inequality" to find what is the closest fruits.
	 * it determines what vertex to go to next.
	 * @throws JSONException
	 */
		
		
	public void moveRobots() throws JSONException {
		List<String> log = null;
		if(game.isRunning()){	
			fruits = convertStringToFruit(game.getFruits());
			log = game.move();
		}	
		if(log!=null) {
			robots = convertStringToRobot(log);//sending String and getting list of robot
			for(int i=0;i<robots.size();i++) {
				
				fruits = findRatio(fruits, robots.get(i));
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					
					if(robots.get(i).getDest()==-1 && (robots.size()==1 || robots.get(i).getPath() == null)) {//check if any robot need to update his path to the fruit 
						if(robotPath.size()>6 && level != 23)
								jamming();
						
						else if(robotsPath.get(i).size()>4 && level == 23)
							exteamJam(robotsPath.get(i), robots.get(i));
						
					
						edge_data nextEdge = RobotAlgo.findEdge(arena, fruits.get(0));// getting the edge from the algorithm
						
						if(robots.get(i).getSrc() != nextEdge.getSrc()){//if the robot is on the source of the edge
							List<Integer> pathInt = solver.reconstructShortestPath(robots.get(i).getSrc(), nextEdge.getSrc());
							List<node_data> path = new ArrayList<>();
							for(Integer j : pathInt){
								node_data tmp = arena.getNode(j);
								path.add(tmp);
							}	
							if(robots.get(i).getPath()!=null && robots.get(i).getPath().get(0).getKey() == 32){
								if(robots.get(i).getPath().get(1).getKey() == 33 || robots.get(i).getPath().get(1).getKey() == 34)
									System.out.println("I got here");
							}
							
							else
								robots.get(i).setPath(path);

						}		
						else{
							List<Integer> pathInt = new ArrayList<>();
							pathInt = solver.reconstructShortestPath(robots.get(i).getSrc(), nextEdge.getDest());
							List<node_data> path = new ArrayList<>();
						
							for(Integer j : pathInt){
								node_data tmp = arena.getNode(j);
								path.add(tmp);
							}	
							if(robots.get(i).getPath() != null && robots.get(i).getPath().get(0).getKey() == 32){
								if(robots.get(i).getPath().get(1).getKey() == 33 || robots.get(i).getPath().get(1).getKey() == 34)
									System.out.println("I got here");
							}
							
							else
								robots.get(i).setPath(path);
						}
					}
					
					
					
					
					List<node_data> check = robots.get(i).getPath();//tmp variable for easy work
					if(check != null && check.size()>1){// only if there isn't a path search for a new one 
						if(level != 23)
							robotPath.add(robots.get(i).getPath().get(1).getKey());
						
						
						else if(level == 23 )
							robotsPath.get(i).add(robots.get(i).getPath().get(1).getKey());
						
						
						game.chooseNextEdge(robots.get(i).getId(), robots.get(i).getPath().get(1).getKey());
						check.remove(1);
						robots.get(i).setPath(check);
						System.out.println(ttt);
					}
						
					fruits.remove(0);
				} 
				catch (Exception e) {
					System.out.println("there is bloody problem here");
				}
			}
		}	
	}
		
	/**
	 * The game could not start until all of the robots are placed.
	 * This method creates all of the robots so the game could start.
	 * it locates the robots on the vertex that is the closest to one of the fruits.
	 * @throws JSONException
	 */

	public void initiatRobot() throws JSONException{
		List<Fruit> tmpl = new ArrayList<>();
		fruits = convertStringToFruit(game.getFruits());
		for(Fruit copyf : fruits){
			Fruit tmp = new Fruit(copyf.getLocation(),copyf.getValue(),copyf.getType());
			tmpl.add(tmp);
		}
		
		valueComperator<Fruit> sort = new valueComperator<>();
		tmpl.sort(sort);
		for(Fruit f : tmpl){
			edge_data e = RobotAlgo.findEdge(arena,f);
			game.addRobot(e.getSrc()+5);
			
		}
	}




	/**
	* This method parses the Json String to a robot object.
	* id, src, speed, dest, value and pos.
	* @param json
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
			output.add(tmp);
		}
		return output;
	}
		
	/**
	 * This method parses the Json String to a robot object.
	 * type, value and pos.
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
			}
					
		}	
		return output;
	}
		
	/**
	 * This class sorts the fruit by value		 
	 * from high to low
	 * @author Yossi and Reuven
	 *
	 * @param <T>
	 */
		
	private class fruitComperator<T> implements Comparator<T>{

		@Override
		public int compare(T o1, T o2) {
			if(o1 instanceof Fruit && o2 instanceof Fruit){
				Fruit tmp1 = (Fruit)o1, tmp2 = (Fruit)o2;
				if(tmp1.getRatio()>tmp2.getRatio()){
					return -1;//it is intentional  -1 here because for sorting from the highest to the lowest
				}
				else if(tmp1.getRatio()<tmp2.getRatio()){
					return 1;
				}
					
				else
					return 0;
			}
			else
				throw new IllegalArgumentException();	
		}
	}
	
	private class valueComperator<T> implements Comparator<T>{

		@Override
		public int compare(T o1, T o2) {
			if(o1 instanceof Fruit && o2 instanceof Fruit){
				Fruit tmp1 = (Fruit)o1, tmp2 = (Fruit)o2;
				if(tmp1.getValue()>tmp2.getValue()){
					return -1;//it is intentional  -1 here because for sorting from the highest to the lowest
				}
				else if(tmp1.getRatio()<tmp2.getRatio()){
					return 1;
				}
					
				else
					return 0;
			}
			else
				throw new IllegalArgumentException();	
		}	
		}
}

