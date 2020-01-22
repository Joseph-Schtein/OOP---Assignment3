package gameClient;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.node_data;
import utils.Point3D;

/**
 * This class manages the manual mode.
 * the game can not start without creating all of the robots of
 * that level so there is a method that initiates all of the required robots.
 * the manual mode lets the player decide to what vertex the robot will go to
 * and where to  place them.
 * @author Yossi and Reuven
 *
 */

public class manuelGame {

	private List<Fruit> fruits;
	private DGraph arena;
	private Graph_Algo algo;
	private game_service game;
	private List<Robot> robots;
	private int robotNumber;
	private List<List<node_data>> paths;
	
	public manuelGame(game_service game) throws JSONException{
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
		try{
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			robotNumber = ttt.getInt("robots");
		
		}
		
		catch (JSONException e) {e.printStackTrace();}

		
	}
	
	/**
	 * moves the robot to the chosen vertex by the most efficient way.
	 * same as the auto mode.
	 * @throws JSONException
	 */
	
	public void moveRobot() throws JSONException{
		fruits = sortFruit(game.getFruits());
		paths = new ArrayList<>();
		List<String> log = game.move();
		if(log!=null) {
			
			for(int j = 0; j < robotNumber && robots.size()!=0 && robots != null; j++){
				paths.add(robots.get(j).getPath());
			}
			
			robots = convertStringToRobot(log);
			
			for(int j = 0; j < robotNumber && robots.size()!=0 && robots != null; j++){
				if(paths.size()>0 && paths.get(j) != null){
					robots.get(j).setPath(paths.get(j));
				}	
			}
			
			long t = game.timeToEnd();
			for(int i=0;i<robots.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					
					if( robots.get(i).getPath() == null || robots.get(i).getPath().size()==1) {
						String choosenNode = JOptionPane.showInputDialog(null, "Enter a node key for id number : " +robots.get(i).getId());
						int newDetination = Integer.parseInt(choosenNode);
						robots.get(i).setPath(algo.shortestPath(robots.get(i).getSrc(), newDetination));
					}
					
					List<node_data> check = robots.get(i).getPath();
					if(check != null && check.size()>1){
						game.chooseNextEdge(robots.get(i).getId(), robots.get(i).getPath().get(1).getKey());
						if(check.get(1).getKey() == robots.get(i).getSrc()){	
							check.remove(1);
						}	
							
						robots.get(i).setPath(check);
						System.out.println("Turn to node: "+robots.get(i).getDest()+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
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
	
	public void initiatRobot(int robotNumber) throws JSONException{
		
		for(int i = 0; i < robotNumber; i++){
        	JFrame tmp = new JFrame("where to put the robot with the id of: " + i);
			int numNodes = arena.nodeSize();
			String number = String.valueOf(numNodes-1);
        	String place = JOptionPane.showInputDialog(tmp, "Selcet a node between 0 to " + number);
        	int choosen = Integer.parseInt(place);
        	if(choosen >= 0 && choosen <= numNodes-1){
      			game.addRobot(choosen);
        
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
			double speed = robot.getDouble("speed");
			int dest = robot.getInt("dest");
			double points = robot.getDouble("value");
			String pos = robot.getString("pos");
			Point3D location = new Point3D(pos);
			Robot tmp = new Robot(id,src,dest,speed,location,points, arena);
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
					return -1;
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
