package algorithms;

import gameClient.Fruit;

import java.util.*;
import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;



import dataStructure.DGraph;
import dataStructure.Vertex;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import utils.StdDraw;

/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.
 * @author 
 *
 */
public class Graph_Algo implements graph_algorithms{
	
	private graph algo;
	private int source;
	private boolean reset;
	
	
	public Graph_Algo() {
		algo = new DGraph();
		reset=false;
		source=0;
	}

	public Graph_Algo(graph g) {
		if(g instanceof DGraph) {
			algo = new DGraph();
			reset=false;
			init(g);
		}
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * initiate those set of algorithm for graph with the name g 
	 * @param the graph we want to create on him those algorithm 
	 */
	@Override
	public void init(graph g) {
		if(g instanceof DGraph) {
			algo = g;
			reset=false;
		}
		else
			throw new IllegalArgumentException();
	}

	/**
	 * we getting a file that has been saved by the next method and initiate those set of algorithm
	 * @param name of file that we convert it to a graph for perform on him those algorithm 
	 */
	@Override
	public void init(String file_name) throws IOException {
		reset = false;
		String line = "";
		boolean firstLine = true;
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(file_name));			
			while((line = reader.readLine())!= null) {
				if(firstLine) {// in the first line there is a description of all the vertices there key and location
					line = line.replace("(", "");	
					line = line.replace(")", "");
					String[] str = line.split(",");
					for(int i = 0; i < str.length && firstLine; i=i+3) {//convert packs of 3 to a vertex (key,location on x axis, location on y axis) 
						node_data toAdd = new Vertex(Integer.parseInt(str[i]),Double.parseDouble(str[i+1]),Double.parseDouble(str[i+2]));
						algo.addNode(toAdd);
					}
				}
				
				if(!firstLine) {//the other lines start with the key of the node and after ward is two packs of (destination,weight) 
					int cumma = line.indexOf(',');// check if there is any edges that this vertex is there source
					if(cumma!=-1){//if yes convert any packs to an edge and add it to the graph	
						String sou =  line.substring(0, cumma);
						int src = Integer.parseInt(sou);
						line = line.substring(cumma+1);
						while(0 < line.length()) {	
							int start = line.indexOf('(');
							int separate = line.indexOf(',');
							int end = line.indexOf(')');
							int dest = Integer.parseInt(line.substring(start+1,separate));
							double w = Double.parseDouble(line.substring(separate+1,end));
							algo.connect(src, dest, w);
							if(end+1 != line.length())
								line = line.substring(end+2);
							
							else
								line = line.substring(end+1);
						}
					}//else skip to the next line
				}	
				firstLine = false;
			}
			reader.close();
		}		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * saving a graph type to a file of text so there is in the first line all the vertices and there location
	 * in 3 packs for each vertex (ID,location on the x axis, location on the y axis).
	 * saving in the begging of the line the ID of the vertex and in packs of 2 his edges so it will
	 * look like (destination, weight) it is for the second line and so on
	 *  @param the name of the file
	 */
	@Override
	public void save(String file_name) {
		LinkedList<node_data> trans = (LinkedList<node_data>) algo.getV();
		StringBuilder stf = new StringBuilder();
		Iterator<node_data> iter1 = trans.iterator();
		while(iter1.hasNext()) {//creating the first line for every iteration we save a vertex
			node_data next = iter1.next();
			Point3D tmpNext = next.getLocation();
			stf.append('(');
			stf.append(next.getKey());
			stf.append(',');
			stf.append(tmpNext.x());
			stf.append(',');
			stf.append(tmpNext.y());
			stf.append(')');
			if(iter1.hasNext())
				stf.append(',');	
			
			else
				stf.append('\n');
		}
		
		iter1 = trans.iterator();
		
		while(iter1.hasNext()) {//saving the lines that described the edges in the graph
			node_data nextNode = iter1.next();
			stf.append(nextNode.getKey());
			LinkedList<edge_data> source = (LinkedList<edge_data>) algo.getE(nextNode.getKey());
			Iterator<edge_data> iter2 = source.iterator();
			if(iter2.hasNext()) {
				while(iter2.hasNext()){
					edge_data nextEdge = iter2.next();
					stf.append(',');
					stf.append('(');
					stf.append(nextEdge.getDest());
					stf.append(',');
					stf.append(nextEdge.getWeight());
					stf.append(')');
				
					if(!iter2.hasNext()) {	
						stf.append('\n');
					}
				}
			}
			
			else{
				stf.append('\n');
			}	
		}
		try{//create the file that represent the graph that is initiate those set of algorithm 
			PrintWriter pw = new PrintWriter(new File(file_name));//create the file it self
			pw.write(stf.toString());
			pw.close();
		} 
		
		catch (FileNotFoundException e){
			e.printStackTrace();
			return;
		}
	}

	/**
	 * checking if the directed graph is a strongly connected
	 * the check that perform saying that if we can get from random vertex to the all other vertices
	 * and for the same vertex we start with we can get for all the vertices for the reveres graph 
	 * (a mirror graph that all the edges change their direction from source -> destination to destination -> source)
	 * the graph is connected.
	 * using a private method that doing the search
	 * @return return if the graph is strongly connected
	 */
	
	@Override
	public boolean isConnected() {
		if(algo.nodeSize()==0 || algo.nodeSize()==1)
			return true;
		
		boolean first = false;
		node_data start = null;
		for(int i = 1; i< algo.nodeSize() && !first;i++) {
			start = algo.getNode(i);
			if(start != null) {
				first = true;
			}
		}
		
		int visitedAll= DFS(algo,start.getKey(), 0);//checking if we can get from random vertex to all the other vertices 
		if(visitedAll != algo.nodeSize()) {// if not return false;
			reset = true;
			resetWithoutColors();
			return false;
		}
		
		Collection<node_data> ver = algo.getV(); 
		graph reverse = new DGraph();// if the normal graph is strongly connect we create a reverse graph
		
		for(node_data v : ver) {
			v.setTag(0);
			reverse.addNode(v);
		}
		
		for(node_data v : ver) {
			LinkedList<edge_data> source = (LinkedList<edge_data>) algo.getE(v.getKey());
			for(edge_data e: source) {
				reverse.connect(e.getDest(), e.getSrc(), e.getWeight());
			}
		}
		
		visitedAll = DFS(reverse,start.getKey(), 0);//checking if the reveres graph is strongly connected from the same vertex
		if(visitedAll != reverse.nodeSize()) {
			reset = true;
			resetWithoutColors();
			return false;
		
		}
		resetWithoutColors();
		reset = true;
		return true;
	}

	
	/**
	 * checking if there is a path from src to dest on directed graph
	 * if there is returning the minimal distance of this path
	 * @param src the source of this path
	 * @param dest the destination of this path
	 * @return a double number that represent the minimal distance 
	 * if there isn't a path the method will return infinity (= Double.MAX_VALUE) 
	 */
	
	@Override
	public double shortestPathDist(int src, int dest) {
		
		reset();
		node_data source  = algo.getNode(src);
		source.setWeight(0);
		source.setTag(1);
		
		Collection<edge_data> srcEdges = algo.getE(src);
		for(edge_data e : srcEdges){
			node_data tmp = algo.getNode(e.getDest());
			if(tmp.getWeight()>source.getWeight() + e.getWeight()){
				tmp.setWeight(source.getWeight() + e.getWeight());
				String s = String.valueOf(src);
				tmp.setInfo(s);
			}
		}
		
		ArrayList<node_data> minHeap = new ArrayList<>();
		for(node_data v : algo.getV()){
			if(v.getKey() != src)
				minHeap.add(v);
		}	
		
		
		
		while(!minHeap.isEmpty()){
			int min = findMinWeight(minHeap);
			node_data minNode = algo.getNode(min);
			minNode.setTag(1);
			Collection<edge_data> edges = algo.getE(minNode.getKey());
			for(edge_data e : edges){
				node_data tmp = algo.getNode(e.getDest());
				if(tmp.getTag() == 0 && tmp.getWeight()> minNode.getWeight() + e.getWeight()){
					tmp.setWeight(minNode.getWeight() + e.getWeight());
					String t = String.valueOf(minNode.getKey());
					tmp.setInfo(t);
				}
			}
		}
		node_data output = algo.getNode(dest);
		return output.getWeight();
	}
	
	
	
	/**
	 * check if there is a path from src to dest if yes return the list that represent the path 
	 * while using the method shortestPathDist to get this minimal path if there isn't a path it
	 * will return a null
	 * @param src start of the path
	 * @param dest end of the path
	 * @return if there is a path between src to dest it will return a list of vertex that represent this path
	 * else it will return null
	 */
	
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		reset();// reset the graph for perform the algorithm on it 	

		double check = shortestPathDist(src,dest);
		if(check < Double.MAX_VALUE) {//check if there is a path using shortestPathDist method
			node_data destination = algo.getNode(dest);
			int previous = Integer.parseInt(destination.getInfo());
			List<node_data> output = new LinkedList<>();
			output.add(0,destination);
		
			while(src != previous) {//create the list that represent the path 
				node_data pre = algo.getNode(previous);
				output.add(0,pre);
				previous = Integer.parseInt(pre.getInfo());
				pre = algo.getNode(previous);
			}
			node_data source = algo.getNode(src);
			output.add(0,source);			
			return output;
		}
		
		return null;//if there isn't a path
	}
	
	
	
	/**
	 * check if there is a path between all the vertex that Id are in the targets list like shortestPath
	 * @param targets a list of integer that represent the keys of node we need to pass in any kind of way
	 * @return if there is a path between all those vertices it will return a list that represent the path else it will return a null
	 */

	@Override
	public List<node_data> TSP(List<Integer> targets) {
		reset();
		
		List<node_data> test = new LinkedList<>();
		for(Integer Int: targets) {// it is for check if all the vertices are in the end
			node_data ad = algo.getNode(Int);
			test.add(ad);
		}
		
		List<node_data> tmp = new LinkedList<>();
		TSPSolution solution = new TSPSolution();//default inner class only getr's and setr's
		boolean getNull = false;
		double distance = 0;
		
		for(int i = 1; (i <= 100 && i <=targets.size()*targets.size()) || (i<=1000 && solution.path.size()==0); i++) {//check for 100 or the square of target list size if after 100 time not found 
			// we will try 900 times more or we will found a path and this path return if after 1000 time we dosn't found path it is likely we didn't found 
			//while after every try the target list is shuffle
			if(i!=1) {
				Collections.shuffle(targets);
			}
			if(getNull = true) {
				getNull = false;
				
			}
			Iterator<Integer> iter = targets.iterator();
			if(targets.size()>=2) {// path must to be it least between 2 vertex
				int source = (int)iter.next();
				while(iter.hasNext() && !getNull) {
					int nextDest = (int)iter.next();
					tmp = shortestPath(source, nextDest);// try to check if there is a path between two close by Integer on the targets list  
					
					if(tmp!=null && tmp.get(tmp.size()-1).getWeight()!=Double.MAX_VALUE) {//if there isn't a path stop and shuffle the list
						source = nextDest;//changing position
						
						node_data src = algo.getNode(source);
						distance = src.getWeight()+solution.getDistance();//update the distance we done so far
						solution.setTmpPath(tmp);//and path
						solution.setDistance(distance);	
						
						reset();
					}
					
					else {// if there isn't a path between two neighbors in target list stop the search and shuffle and restart the search
						getNull = true;
						distance =0;
						solution.setTmpPath(null);
						solution.distance = 0;
						reset();
					}	
				}
				
					if(solution.tmpPath!=null && solution.tmpPath.containsAll(test)) {// if we got to the end update it if we didn't found a solution or we got a better solution 
						if(distance < solution.getDistance() || solution.path.size()==0) {	
							solution.setPath(solution.tmpPath);
							solution.setTmpPath(null);
							solution.setDistance(0.0);
							distance = 0;
							reset();
						}	
					}
				}
			
			else 
				return null;
					
		}
		
		if(solution.path!=null && solution.path.size()>1) {//this segment of code is for removing duplicate of the node because the end of one part is the start of the next path 
			node_data current = solution.path.get(0);
			int index = 1;
			while(index < solution.path.size()) {
				node_data next = solution.path.get(index);
				if(current.getKey() == next.getKey()) {
					solution.path.remove(next);
				}
			
				else {
					current = next;
					index++;
				}	
			}	
		}
		
		return solution.getPath();
	}
	
	

	/**
	 * create a new graph with a deep copy to the graph that initiate this set of algorithm
	 * @return a deep copy of a graph in algo field 
	 */
	@Override
	public graph copy() {
		graph clone = new DGraph();
		Collection<node_data> tmp =  algo.getV();
		
		for(node_data ver : tmp) {//clone all the vertices
			int key = ver.getKey();
			Point3D place = ver.getLocation();
			node_data toAdd = new Vertex(key,place.x(),place.y());
			clone.addNode(toAdd);
		}
		
		for(node_data ver : tmp) {// clone all edges
			Collection<edge_data> source = algo.getE(ver.getKey());
			for(edge_data e : source) {
				clone.connect(e.getSrc(), e.getDest(), e.getWeight());
			}
		}
		return clone;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private int DFS(graph check,int start,int visited) {// it is private method for is connected
		if(check.nodeSize()==visited) {
			return visited;
		}
		
		else {
			
			visited++;
			node_data startNode = check.getNode(start); 
			startNode.setTag(1);
			Collection<edge_data> edges = check.getE(start);
			
			for(edge_data e : edges) {
				node_data des = check.getNode(e.getDest());
				if(des.getTag() == 0) {
					visited = DFS(check,e.getDest(), visited);
				}
			}
			startNode.setTag(2);
			
		}
	return visited;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void resetWithoutColors() {//reset the graph partial for so we can use the relevant data to perform on the gui window 
		Collection<node_data> ver = algo.getV();
		for(node_data tmp : ver) {
			Vertex v = (Vertex)tmp;
			v.setInfo("");
			v.setWeight(Double.MAX_VALUE);	
		}
	}
	
	
	private void reset() {//fully reset on all the graph
		Collection<node_data> ver = algo.getV();
		for(node_data tmp : ver) {
			Vertex v = (Vertex)tmp;
			v.setTag(0);
			v.setInfo("");
			v.setWeight(Double.MAX_VALUE);	
		}
	}
	
	
	
	
	
	private class TSPSolution{// trivial private class
		
		private List<node_data> path;
		private List<node_data> tmpPath;
		private double distance;
		
		public TSPSolution() {
			path = new LinkedList<>();
			tmpPath = new LinkedList<>();
			distance = 0;
		}

		public List<node_data> getPath() {
			return path;
		}

		public void setPath(List<node_data> output) {
			if(output!=null)
				this.path.addAll(output);
			
			else
				this.path.removeAll(path);
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double d) {
			this.distance = d;
		}

		public List<node_data> getTmpPath() {
			return tmpPath;
		}

		public void setTmpPath(List<node_data> tmpPath) {
			if(tmpPath!=null)
				this.tmpPath.addAll(tmpPath);
			
			else
				this.tmpPath.removeAll(this.tmpPath);
		}
	}
	
	private int findMinWeight(ArrayList<node_data> minHeap){
		
		int minVertexKey = 0, index = 0;
		double minWeight = Double.MAX_VALUE;
		
		for(int i = 0; i < minHeap.size(); i++){
			if(minWeight > minHeap.get(i).getWeight()){
				minWeight = minHeap.get(i).getWeight();
				minVertexKey = minHeap.get(i).getKey();
				index = i ;
			}			
		}
		
		minHeap.remove(index);
		return minVertexKey;
	}
}	
	