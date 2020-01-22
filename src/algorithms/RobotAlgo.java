package algorithms;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import gameClient.Fruit;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

public class RobotAlgo {
	
	public static final double EPS1 = 0.001, EPS2 = EPS1*EPS1;
	
	public RobotAlgo(){
		
	}
	
	public static  boolean onTheEdge(Point3D f, Point3D src, Point3D dest){	
		double dist1 = src.distance2D(dest); 
		double dist2 = src.distance2D(f) + f.distance2D(dest);
		if(dist1 >= dist2 - EPS2)
			return true;
		
		else
			return false;
			
	}
	
	public static  boolean checkEdge(Point3D f, edge_data e, int type, graph g){
		if(e == null){
			return false;
		}
		
		if(type>0 && e.getDest()<e.getSrc()){
			return false;
		}
		
		if(type<0 && e.getDest()>e.getSrc()){
			return false;
		}
		
		node_data src = g.getNode(e.getSrc());
		node_data dest = g.getNode(e.getDest());
		return onTheEdge(f,src.getLocation(), dest.getLocation());
	}
	
	public static  edge_data findEdge(graph g, Fruit f){
		Collection<node_data> vertices = g.getV();
		Iterator<node_data> ver = vertices.iterator();
		boolean found = false;
		edge_data output = null;
		
		while(ver.hasNext() && !found){
			node_data nextVertex = ver.next();
			Collection<edge_data> edges = g.getE(nextVertex.getKey());
			Iterator<edge_data> edg = edges.iterator();
			while(edg.hasNext() && !found){
				edge_data nextEdge = edg.next();
				found = checkEdge(f.getLocation(), nextEdge, f.getType(),g);
				if(found){
					output = nextEdge;
				}
			}
		}
		return output;
	}
}
