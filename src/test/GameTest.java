package test;

import static org.junit.Assert.assertEquals;
import gameClient.Fruit;
import gameClient.Robot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import utils.Point3D;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import Server.Game_Server;
import Server.game_service;

public class GameTest {


	
	int level;
	game_service game1;
	game_service game2;
	DGraph gd1;

	@Before
	public void initiat() throws JSONException{
		level = (int)(Math.random()*24);
		game1 = Game_Server.getServer(level);
		gd1 = new DGraph();
		gd1.init(game1.getGraph());
	}
	
	@Test
	public void getGraphTest() throws JSONException{
		game2 = Game_Server.getServer(level);
		String g1 = game1.getGraph();
		String g2 = game2.getGraph();
		DGraph dg1 = new DGraph();
		DGraph dg2 = new DGraph();
		dg1.init(g1);
		dg2.init(g2);
		boolean diff=false;
		Iterator<node_data> iter1 = dg1.getV().iterator();
		Iterator<node_data> iter2 = dg2.getV().iterator();
		while(iter1.hasNext() && iter2.hasNext() && !diff){
			node_data n1 = iter1.next();
			node_data n2 = iter2.next();
			if(n1.getKey()!=n2.getKey())
				diff = true;
			
			if(n1.getLocation().x() != n2.getLocation().x() && !diff)
				diff = true;
			
			if(n1.getLocation().y() != n2.getLocation().y() && !diff)
				diff = true;
		}
		
		 iter1 = dg1.getV().iterator();
		 iter2 = dg2.getV().iterator();
		 
		while(iter1.hasNext() && iter2.hasNext() && !diff){
			node_data n1 = iter1.next();
			node_data n2 = iter2.next();
			Iterator<edge_data> iedge1 = dg1.getE(n1.getKey()).iterator();
			Iterator<edge_data> iedge2 = dg2.getE(n2.getKey()).iterator();
			
			
			while(iedge1.hasNext() && iedge2.hasNext() && diff){
				edge_data e1 = iedge1.next();
				edge_data e2 = iedge2.next();
				
				
				if(e1.getSrc()!=e2.getSrc() && !diff)
					diff = true;
				
				if(e1.getDest() != e2.getDest() && !diff)
					diff = true;
				
				if(e1.getWeight() != e2.getWeight() && !diff)
					diff = true;
			}
		}
		 
		assertEquals(diff, false);
	}
	
	
	@Test
	public void getFruit() throws JSONException{
		game2 = Game_Server.getServer(level);
		List<Fruit> f1 = convertStringToFruit(game1.getFruits());
		List<Fruit> f2 = convertStringToFruit(game2.getFruits());
		boolean diff = false;
		Iterator<Fruit> iter1 = f1.iterator();
		Iterator<Fruit> iter2 = f2.iterator();
		
		while(iter1.hasNext() && iter2.hasNext() && !diff){
			Fruit n1 = iter1.next();
			Fruit n2 = iter2.next();
			
			if(n1.getType() != n2.getType() && !diff)
				diff = true;
			
			if(n1.getValue() != n2.getValue() && !diff)
				diff = true;
			
			if(n1.getLocation().x() != n2.getLocation().x() && !diff)
				diff = true;
			
			if(n1.getLocation().y() != n2.getLocation().y() && !diff)
				diff = true;
		}
		
		assertEquals(diff, false);

	}
	
	
	
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
}	