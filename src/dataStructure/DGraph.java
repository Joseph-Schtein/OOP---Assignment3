package dataStructure;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;



public class DGraph implements graph{

	private HashMap<node_data, Collection<edge_data>> graph;
    private HashMap<Integer, node_data> indexs;
    private Collection<node_data> VertexSet;
    private int numOfVertex;
    private int numOfEdges;
	private int changePreform; 
    
	public DGraph(){
		graph = new HashMap<node_data, Collection<edge_data>>();
		indexs = new HashMap<Integer, node_data>();
		VertexSet = new LinkedList<node_data>();
		numOfVertex = 0;
		numOfEdges = 0;
		changePreform = 0;
		
		
	}
	
	public void init(String json) throws JSONException{
		JSONObject graph = new JSONObject(json);
		JSONArray nodes = graph.getJSONArray("Nodes");
		JSONArray edges = graph.getJSONArray("Edges");
		
		for(int i = 0; i < nodes.length(); i++){
			int id = nodes.getJSONObject(i).getInt("id");
			String pos = nodes.getJSONObject(i).getString("pos");
			Point3D tmp = new Point3D(pos);
			node_data tmpNode = new Vertex(i,tmp.x(),tmp.y());
			addNode(tmpNode);
		}
		
		for(int i = 0; i < edges.length(); i++){
			int src = edges.getJSONObject(i).getInt("src");
			int dest = edges.getJSONObject(i).getInt("dest");
			double w = edges.getJSONObject(i).getDouble("w");
			connect(src,dest,w);
		}
	}
	
	/**
	 * getting a key and return the Vertex with the key
	 * @param which key you want to get
	 * @return returning the node with key like the parameter 
	 */
	
	@Override
	public node_data getNode(int key) {
		return indexs.get(key);
	}

	
	/**
	 * returning the edge that he start from src and getting to dest
	 * @param the two side of the edge 
	 * @return the edge so it exit from src and getting into dest
	 */
	@Override
	public edge_data getEdge(int src, int dest) {
		if(src!=dest) {
			node_data source = indexs.get(src);
			if(source!=null) {
				Collection<edge_data> edges = graph.get(source);
				edge_data output =  null;
				for(edge_data e : edges) {
					if(e.getDest() == dest) {
						output = e;
					}
				}
				
				return output;
			}
			
			else {
				System.out.println("the source is not exist in the graph");
			}
			
		}
		else
			System.out.println("you can't get an edge that his source equals to his destination, so you got null inted of error");
			return null;
	}
	
	
	/**
	 * add a node to the graph
	 * adding to graph field as a key the value we get is a collection of edges
	 * so those edges are getting out from this node
	 * adding to index field as value when there key is the key of the node
	 * and adding the node to a set of node
	 * @param geting a node, check if there is a node with the same key.
	 * if yes returning it is not add else add it like I described 
	 */
	@Override
	public void addNode(node_data n) {	
		node_data check = indexs.get(n.getKey());
		if(check == null) {
			indexs.put(n.getKey(), n);
			Collection<edge_data> tmp = new LinkedList<edge_data>();
			graph.put(n,tmp);
			VertexSet.add(n);
			numOfVertex++;
			changePreform++;
		}
		else {
			System.out.println("there is a Vertex with this key");
		}
	}

	/**
	 * getting tow key edges and add to the graph an edge with given weight,
	 * if there is an edge a->b we will not add another edge a->b
	 * because it's not an multi-graph implementation
	 * @param src source of the edge
	 * @param dest destination of the edge
	 * @param w the weight of this specific edge 
	 */
	@Override
	public void connect(int src, int dest, double w) {
		edge_data check = getEdge(src, dest);
		if(check == null) {// if there isn't an edge add it else print this is not a multi-graph
			node_data source = indexs.get(src);
			node_data destination = indexs.get(dest);
			if(source!=null && destination!=null) {
				Collection<edge_data>edges = graph.get(source);
				edge_data newEdge = new Edge(source, destination, w);
				edges.add(newEdge);
				numOfEdges++;
				changePreform++;
			}
			
			else
				System.out.println("one of the node does not exist");
		}
		
		else
			System.out.println("this is not a multi-graph");
	}
	
	/**
	 * giving a set of all the vertices
	 * @return VertexSet a set of vertices that currently in the graph 
	 */
	
	@Override
	public Collection<node_data> getV() {
		return VertexSet;
	}

	/**
	 * returning edge set when node_id is there source
	 * @param node_id is a key for a node in the graph
	 * @return Collection<edge_data> if node_id exist in the graph it will return the
	 * all the edges so that node_id is there source 
	 * if node_id isn't exist it will return error
	 */
	
	@Override
	public Collection<edge_data> getE(int node_id) {
		node_data tmp = indexs.get(node_id); 
		Collection<edge_data> output = graph.get(tmp);
		return output;
	}

	/**
	 * removing a vertex from the graph and all the vertices that key is there source
	 * after ward we will check if there exist edges that key is there destination
	 * and will remove those edges 
	 * @param key the key of the node we want to delete from the graph
	 * @return returning the node we delete if the node is not exist return an error 
	 */
	
	@Override
	public node_data removeNode(int key) {
		node_data toRemove = indexs.get(key); 
		if(toRemove!=null) {	
			indexs.remove(key,toRemove);
			VertexSet.remove(toRemove);
			Collection<edge_data> tmp = graph.remove(toRemove);
			numOfEdges = numOfEdges - tmp.size();
			changePreform = changePreform+tmp.size()+1; 
			numOfVertex--;
		
			for(node_data ver : VertexSet) {		
				List<edge_data> curSrc = (List<edge_data>) graph.get(ver);
			
				for(int i = 0; i<curSrc.size();i++) {
					edge_data e = curSrc.get(i);
					if(e.getDest()==toRemove.getKey()) {
						curSrc.remove(e);
						numOfEdges--;
						changePreform++;
					}	
				}	
			}
		
			return toRemove;
		}
		
		else {
			System.out.println("there isn't a vertex with this id: "+key);
			System.out.println("so you will get a null insted of a vertex");
			return null;
		}
	}
	
	/**
	 * remove the edge from the graph with a specific keys
	 * @param src the source of the edge we need to remove
	 * @param dest the destination of the edge we need to remove
	 */

	@Override
	public edge_data removeEdge(int src, int dest) {
		node_data source = indexs.get(src); 
		if(source != null) {
			List<edge_data> edges = (List<edge_data>) graph.get(source);
			edge_data output = null;
			for(int i = 0; i<edges.size();i++) {
				edge_data e = edges.get(i);
				if(e.getDest()==dest) {
					output=e;
					edges.remove(e);
					numOfEdges--;
					changePreform++;
				}	
			}
			if(output == null) {
				System.out.println("we don't find a edge for your input ");
			}
			return output;
		}
		else {
			System.out.println("there is not Vertex with the Id: "+src+" so returning to you null");
			return null;
		}
	}
	
	/**
	 * return the number of vertices in the graph
	 * @return  |V|
	 */
	@Override
	public int nodeSize() {
		return numOfVertex;
	}
	
	/**
	 * return the number of edges in the graph
	 * @return |E|  
	 */
	@Override
	public int edgeSize() {
		return numOfEdges;
	}

	/**
	 * return the sum of change that perform on the graph lie adding node/edge or remove node/edge
	 * @return the number of changes
	 */
	@Override
	public int getMC() {
		return changePreform;
	}
	
	
	
}	

	
