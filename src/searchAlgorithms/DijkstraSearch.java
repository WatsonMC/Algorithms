package searchAlgorithms;

/**
 * Implementation of Dijkrstra's search algorithm using a minimum priority queue
 * uses nested class VertexDistancePair to contain both vertex label and tentative
 * distance to vertex in single object in queue. 
 * Author: Malcolm Watson
 * Date: 29.01.2019 
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import graph.Graph;	//locally created graph 
import graph.VertexDistancePair;

public class DijkstraSearch implements SearchAlgorithm{
	
	/***
	 *	//TODO Complete java docs
	 * Abstraction function
	 * 
	 * RI
	 * 	Vertex labels stored as strings specifically
	 * 	
	 * 	distances	 - map of string vertices to integers representing tentative distances from source to the given vertex
	 * 				 - Distance = Integer.MAX_VALUE represent non-existant path
	 * 	predecessors - map of string to string, key representing vertex labels, value representing predecessor of that vertex
	 *  Graph - Graph implementation to be traverssed. Graph must be weighted and acyclic, with no negative edges
	 * 
	 * Rep safety argument
	 */
	
	private PriorityQueue<VertexDistancePair> pQ;
	private Map<String, Integer> distances;
	private Map<String, String> predecessors;
	private Set<String> vertices;
	private Graph<String> graph;
	
	public DijkstraSearch(Graph<String> graph) {
		this.vertices = graph.vertices();
		this.graph = graph;
	}
	
	/**
	 * Initialise the algorithm by creating the distance map and adding all the 
	 * vertexDistancePairs to the queue, relative to the requested source
	 */
	private void initialise(String source) {
		this.vertices = new HashSet<>(graph.vertices());
		this.pQ = new PriorityQueue<>();
		this.distances = new HashMap<>();
		this.predecessors = new HashMap<>();
		distances.put(source,0);
		pQ.add(new VertexDistancePair(source, 0));
		for(String vertex:vertices) {
			if(!vertex.equals(source)) {
				distances.put(vertex, Integer.MAX_VALUE);
				pQ.add(new VertexDistancePair(vertex,distances.get(vertex)));
			}
		}
	}
	
	/**
	 * Returns a sorted set of vertices representing path from source to target
	 * @param source: String, vertex source
	 * @param target: String, vertex target
	 * @return ordered listset of vertex labels, starting at source ending at target. if no path is found, return empty list
	 */
	public LinkedList<String> findPathToTarget(String source, String target){
		//TODO memoization of search results to improve efficiency
		if(searchForTarget(source,target)) {
			LinkedList<String> path = new LinkedList<>();
			path.addFirst(target);
			String tempVertex = target;
			
			//loop through entire path
			while (tempVertex!=source){
				path.addFirst(predecessors.get(tempVertex));
				tempVertex = predecessors.get(target);
			}
			return path;
		}
		else {
			return new LinkedList<String>();
		}
	}
	
	/**
	 * Returns total distance to target from source, or 0 if target cannot be reached
	 * @param source: String, vertex source
	 * @param target: String, vertex target
	 * @return Integer: distance to target
	 */
	public Integer findDistanceToTarget(String source, String target) {
		if(searchForTarget(source,target)) {
			return distances.get(target);
		}
		else return 0;
	}
	
	/**
	 * Search for shortest path to the target from the source.
	 * If path cannot be found,  terminates and returns false
	 * 
	 * @param target
	 * Returns true if destination found, else false
	 */
	public boolean searchForTarget(String source, String target) {
		initialise(source);
		while(!pQ.isEmpty()) {
			String u = pQ.remove().vertex;	//get next highest vertex
			vertices.remove(u);	//remove from vertex set -> this is probably not necessary, since queue is a set too

			if(distances.get(u) == Integer.MAX_VALUE) {
				break;	//next highest priority vertex has no path to destination
			}
			
			if(u.equals(target)) {
				//have found destination, algorithm over, store result and return
				//find the closest of neighbours -> already done since closest unvisited is always checked first
				return true;
			}
			
			Map<String, Integer> neighbours = graph.targets(u);
			for(String v: neighbours.keySet()) {
				if(vertices.contains(v)) {	//iff vertex is not yet visited
					int tentDist = distances.get(u)+ neighbours.get(v);//calculat new path distancev
					if(tentDist<distances.get(v)) {
						changePriority(v,tentDist);
						distances.put(v, tentDist); //new distance for that node is the tentative distance
						predecessors.put(v,u);
					}
				}
			}

		}
		return false;
	}
	
	/**
	 * Main loop of algorithm, finds shortest distances from destination to each node
	 * @return True if all nodes can be found found from source, false otherwise
	 */
	public void searchWholeGraph(String source) {
		initialise(source);
		while(!pQ.isEmpty()) {
			String u = pQ.remove().vertex;	//get next highest vertex
			vertices.remove(u);	//remove from vertex set -> this is probably not necessary, since queue is a set too
			
			Map<String, Integer> neighbours = graph.targets(u);
			for(String v: neighbours.keySet()) {
				if(vertices.contains(v)) {	//iff vertex is not yet visited
					int tentDist = distances.get(u)+ neighbours.get(v);//calculat new path distancev
					if(tentDist<distances.get(v)) {
						changePriority(v,tentDist);
						distances.put(v, tentDist); //new distance for that node is the tentative distance
						predecessors.put(v,u);
					}
				}
			}
		}
	}
	
	/**
	 * getter for predecessors map
	 * @return
	 */
	public Map<String,String> getPredecessors(){
		return new HashMap<>(predecessors);
	}
	
	/**
	 * Getter for distance map
	 * @return
	 */
	public Map<String,Integer> getDistances(){
		return new HashMap<>(distances);
	}
	
	
	
	
	

	/**
	 * Helper method to change priority of a given vertex in the priority queue
	 * @param vertex
	 * @param newDistance
	 */
	public void changePriority(String vertex, Integer newDistance) {
		pQ.remove(new VertexDistancePair(vertex,distances.get(vertex)));
		pQ.add(new VertexDistancePair(vertex,newDistance));
		distances.put(vertex, newDistance);
	}
	

	
/***** TEST METHODS *******/
	
	/**
	 * Testing method, to check that PQ and VDP are functioning as intended
	 * @param vertex
	 * @param distance
	 */
	public void addVPToQueue(String vertex, Integer distance) {
		if(!vertices.contains(vertex)) {
			vertices.add(vertex);
			distances.put(vertex,distance);
		}
		pQ.add(new VertexDistancePair(vertex, distance));
		//TODO 1. change distances to be map of vertex label to VDP,
		//2. change (if vertex is in unvisited vertex set) to (if VDP mapped from distance is in Q)
		//3. vertex set now obsolete, replaced with hashmap
	}
	
	/**
	 * Testing method, pops next VDP from queue and returns label
	 * @return
	 * Vertex string label
	 */
	public String nextVertexFromQueue() {
		return pQ.remove().vertex;
	}
	
	/**
	 * Testing method, peeks next VPD from queue and returns label
	 * @return
	 */
	public String peekNextVertexFromQueue() {
		return pQ.peek().vertex;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getQSize() {
		return pQ.size();
	}
	
	/** Mock constructor for testing**/
	public DijkstraSearch() {
		this.pQ = new PriorityQueue<>();
		this.vertices = new HashSet<>();
		this.distances = new HashMap<>();
	}
	
/***** TEST METHODS *******/
	
	/**
	 * Internal class to store a vertex and it's tentative distance in a single object
	 * used to allow the priority queue to easily store vertices in order of their distance
	 * compareTo function priortizes verticies with minimum distance, since these
	 * are the vertices that should be popped during Dijkstra's
	 * @author SP194E
	 *
	 */
	
	
}
