package test;

import static org.junit.Assert.assertEquals;
import gameClient.Robot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import Server.Game_Server;
import Server.game_service;
import utils.Point3D;
import dataStructure.DGraph;
import dataStructure.Vertex;
import dataStructure.node_data;

public class RobotTest {

	int level;
	game_service game1;
	DGraph gd1;

	@Before
	public void initiat() throws JSONException{
		level = (int)(Math.random()*24);
		game1 = Game_Server.getServer(level);
		gd1 = new DGraph();
		gd1.init(game1.getGraph());
	}
	
	@Test
	public void getId(){
		Point3D p1 = new Point3D(1.25, 2.5, 0);
		Robot r1 = new Robot(2, 5, 9, p1,gd1);
		Robot r2 = new Robot(2, 5, 9, p1,gd1);
		
		assertEquals(r1.getId(), r2.getId());
	}
	
	@Test
	public void getLocation(){
		Point3D p1 = new Point3D(1.25, 2.5, 0);
		Robot r1 = new Robot(2, 5, 9, p1,gd1);
		Robot r2 = new Robot(2, 5, 9, p1,gd1);
		
		boolean diff = r1.getLocation().x()!= r2.getLocation().x();
		diff = r1.getLocation().y() != r2.getLocation().y();
		
		assertEquals(diff,false);
	}
	
	@Test
	public void getSrcDest(){
		Point3D p1 = new Point3D(1.25, 2.5, 0);
		Robot r1 = new Robot(2, 5, 9, p1,gd1);
		Robot r2 = new Robot(2, 5, 9, p1,gd1);
		
		boolean diff = r1.getSrc() != r2.getSrc();
		diff = r1.getDest() != r2.getDest();
		
		assertEquals(diff,false);
	}
	
	@Test
	public void sizePath(){
		Point3D p1 = new Point3D(1.25, 2.5, 0);
		Robot r1 = new Robot(2, 5, 9, p1,gd1);
		Robot r2 = new Robot(2, 5, 9, p1,gd1);
		
		List<node_data> pa1 = new ArrayList<>();
		List<node_data> pa2 = new ArrayList<>();
		pa1.add(new Vertex(1,2,3));
		pa2.add(new Vertex(1,2,3));
		pa1.add(new Vertex(2,4,1));
		pa2.add(new Vertex(2,4,1));
		pa1.add(new Vertex(3,5,3));
		
		r1.setPath(pa1);
		r2.setPath(pa2);
		
		Iterator<node_data> path1 = r1.getPath().iterator();
		Iterator<node_data> path2 = r2.getPath().iterator();
		
		boolean diff = false;
		if(r1.sizeOfPath() != r2.sizeOfPath()){
			diff = true;	
		}
		
		assertEquals(diff,true);
		
	}
	
	
	@Test
	public void getPath(){
		Point3D p1 = new Point3D(1.25, 2.5, 0);
		Robot r1 = new Robot(2, 5, 9, p1,gd1);
		Robot r2 = new Robot(2, 5, 9, p1,gd1);
			
		List<node_data> pa1 = new ArrayList<>();
		List<node_data> pa2 = new ArrayList<>();
		pa1.add(new Vertex(1,2,3));
		pa2.add(new Vertex(1,2,3));
		pa1.add(new Vertex(2,4,1));
		pa2.add(new Vertex(2,4,1));
			
		r1.setPath(pa1);
		r2.setPath(pa2);
		
		Iterator<node_data> path1 = r1.getPath().iterator();
		Iterator<node_data> path2 = r2.getPath().iterator();
			
		boolean diff = false;
		if(r1.sizeOfPath() != r2.sizeOfPath()){
			diff = true;		
		}
		
		while(path1.hasNext() && path2.hasNext() && !diff){
			node_data n1 = path1.next();
			node_data n2 = path2.next();
			
			if(n1.getKey() != n2.getKey() && !diff)
				diff = true;
			
			if(n1.getLocation().x() != n2.getLocation().x() && !diff)
				diff = true;
			

			if(n1.getLocation().y() != n2.getLocation().y() && !diff)
				diff = true;
		}
		
		assertEquals(diff, false);
	}
}
