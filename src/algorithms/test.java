package algorithms;

import java.util.List;

import dataStructure.DGraph;
import dataStructure.Vertex;
import dataStructure.graph;
import dataStructure.node_data;

public class test {

	public static void main(String[] args){
		
		graph g = new DGraph();
		Graph_Algo algo = new Graph_Algo();
		
		node_data v1 = new Vertex(1,Math.random(),Math.random());
		node_data v2 = new Vertex(2,Math.random(),Math.random());
		node_data v3 = new Vertex(3,Math.random(),Math.random());
		node_data v4 = new Vertex(4,Math.random(),Math.random());
		node_data v5 = new Vertex(5,Math.random(),Math.random());
		node_data v6 = new Vertex(6,Math.random(),Math.random());
		node_data v7 = new Vertex(7,Math.random(),Math.random());
		
		g.addNode(v1);
		g.addNode(v2);
		g.addNode(v3);
		g.addNode(v4);
		g.addNode(v5);
		g.addNode(v6);
		g.addNode(v7);
		
		g.connect(1,2,0.5);
		g.connect(1,3,0.5);
		g.connect(2,4,0.6);
		g.connect(3,4,0.7);
		g.connect(3,5,0.6);
		g.connect(4,5,0.3);
		g.connect(4,6,0.3);
		g.connect(4,7,0.6);
		g.connect(5,7,0.2);
		g.connect(6,7,0.4);
		
		algo.init(g);
		System.out.println(algo.shortestPathDist(1, 7));
		List<node_data> dis = algo.shortestPath(1, 7);
		for(node_data v : dis){
			System.out.println(v.getKey()+" ");
		}
	}
}
