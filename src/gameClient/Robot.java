package gameClient;


import java.util.List;

import dataStructure.DGraph;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;


/**
 * This class represents the the robots class and the fields.
 * @author Yossi and Reuven
 *
 */



public class Robot {

	private Point3D pos;
	
	private int id;
	private int src;
	private graph g;
	private List<node_data> path;
	private int dest;
	
	public Robot(int id, int src,int dest,Point3D pos , DGraph g){
		this.g =g;
		node_data tmp = this.g.getNode(src);
		this.pos = tmp.getLocation();
		this.id = id;
		
		this.src = src;
		this.dest = dest;
		
	}
		
	public int sizeOfPath(){
		return path.size();
	}
	
	
	public List<node_data> getPath(){
		return path;
	}
	
	public void setPath(List<node_data> path){
		this.path = path;
	}
	
	public Point3D getLocation(){
		return pos;
	}
	
	public void setLocation(Point3D pos){
		this.pos = pos;
	}


	public int getId() {
		return id;
	}


	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}
	
	public int getDest(){
		return dest;
	}
	
	public void setDest(int dest){
		this.dest = dest;
	}
}
