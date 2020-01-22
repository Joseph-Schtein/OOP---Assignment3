package gameClient;


import java.util.ArrayList;
import java.util.Comparator;
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


public class automaticGame{
	
	private List<Fruit> fruits;
	private DGraph arena;
	private Graph_Algo algo;
	private game_service game;
	private List<Robot> robots;
	private int robotNumber;
	
	
	/**
	 * This method initiates the game.
	 * Parses the "game String" in to a Json object.
	 * initiates the fruits and robots array lists.
	 * @param game
	 * @throws JSONException
	 */
	
	
	public automaticGame(game_service game) throws JSONException{
		this.game = game;
		
		String g = game.getGraph();
		arena = new DGraph();
		arena.init(g);
		
		algo = new Graph_Algo();
		algo.init(arena);
		
		
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
	
	
	/**
	 * This method moves the robots every step to the next closest fruit.
	 * it  uses the "Triangle inequality" to find what is the closest fruits.
	 * it determines what vertex to go to next.
	 * @throws JSONException
	 */
	
	
	public void moveRobots() throws JSONException {
		List<String> log = null;
		if(game.isRunning()){	
			fruits = sortFruit(game.getFruits());
			log = game.move();
		}	
		if(log!=null) {
			robots = convertStringToRobot(log);//sending String and getting list of robot
			long t = game.timeToEnd();
			for(int i=0;i<robots.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					
					if(robots.get(i).getDest()==-1 && (robots.size()==1 || robots.get(i).getPath() == null)) {//check if any robot need to update his path to the fruit 
						edge_data nextEdge = RobotAlgo.findEdge(arena, fruits.get(0));// getting the edge from the algorithm
						if(robots.get(i).getSrc() != nextEdge.getSrc()){//if the robot is on the source of the edge
							robots.get(i).setPath(algo.shortestPath(robots.get(i).getSrc(), nextEdge.getSrc()));	
						}	
						else{
							robots.get(i).setPath(algo.shortestPath(robots.get(i).getSrc(), nextEdge.getDest()));
						}
					}
					
					List<node_data> check = robots.get(i).getPath();//tmp variable for easy work
					if(check != null && check.size()>1){// only if there isn't a path search for a new one 
						game.chooseNextEdge(robots.get(i).getId(), robots.get(i).getPath().get(1).getKey());
						System.out.println("Turn to node: "+robots.get(i).getPath().get(1).getKey()+"  time to end:"+(t/1000));
						System.out.println(ttt);
						check.remove(1);
						robots.get(i).setPath(check);
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
		int counter =0;
		for(int i = 0; counter <robotNumber; i++){
			if(i < tmpl.size()){
				edge_data near = RobotAlgo.findEdge(arena, tmpl.get(i));
				if(tmpl.get(i).getType()>0){
					boolean put1 = game.addRobot(near.getSrc());
					if(put1)
						counter++;
				}
				
				else{
					boolean put2 = game.addRobot(near.getSrc());
					if(put2)
						counter++;
				}	
			}
			else{
				boolean put3 = game.addRobot(i);
				if(put3)
					counter++;
			}
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
	 * This method sorts the fruit by value from high to low.
	 * this way the robots know to witch fruit to give higher priority. 
	 * @param tmp
	 * @return
	 * @throws JSONException
	 */
	
	private List<Fruit> sortFruit(List<String> tmp) throws JSONException{//sort the fruit by there value
		List<Fruit> fruits = convertStringToFruit(tmp);
		fruitComperator<Fruit> com = new fruitComperator<>();
		fruits.sort(com);
		return fruits;
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
				if(tmp1.getValue()>tmp2.getValue()){
					return -1;//it is intentional  -1 here because for sorting from the highest to the lowest
				}
				else if(tmp1.getValue()<tmp2.getValue()){
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


