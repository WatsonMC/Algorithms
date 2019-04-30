package updatingSearchAlgorithms;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import graph.Graph;

/**
 * A mutable search algorithm which provides updates to the search graph
 * as it proresses.
 * The algorithm allows searching through a graph of String vertices with non-negative
 * non-zero Integer edgeweights.
 * Searching can be source to target or source to entire graph.
 * Updating provides a distance map of each vertex distance from source as the algorithm runs
 * 
 * This Interface is not required to be threadsafe
 * @author watso
 * @date 25/02/2019
 */

public interface UpdatingSearchAlgorithm{
	public static final Integer PATH_NOT_FOUND = -1;
	/**
	 * Create a new search algorithm object with the graph to be searched
	 * @param graph: graph<String,Integer> object to be searched
	 * @param type: Integer, used to select required search algorithm
	 * @return
	 */
	//public static UpdatingSearchAlgorithm newSearch(Graph<String,Integer> graph, Integer type) {
		//TODO implement factory for updatingsearchalgorithm 
//	}
		
	/**
	 * Empty factory for when graph is not supplied, mostly testing
	 * @return
	 */
	public static UpdatingSearchAlgorithm empty() {
		return new DjirkstaSearchUpdating();
	}
	
	/**
	 * Empty consturctor when graph is supplied, general case
	 * @param graph
	 * @return
	 */
	public static UpdatingSearchAlgorithm empty(Graph<String> graph) {
		return new DjirkstaSearchUpdating(graph);
	}
	

	/**
	 * execute algorithm on whole graph, with no target
	 * @param source: source vertex to begin search from
	 * @return Map<String,Integer>: distances from each vertex to the source node
	 * if a node cannot be reached from the source, distance should be -1
	 * this method should push a start type GraphUpdate to the updateQueue each call,
	 * then push a distance type GraphUpdate object to the queue for each iteration
	 * and push a end type GraphUpdate object to the updateQueue on completion
	 */
	public Map<String, Integer> searchWholeGraph(String source);
	
	
	/**
	 * execute algorithm on graph from source to target
	 * @param source: source vertex to begin search from
	 * @param target: target vertex to search to
	 * @return boolean: true if path is found, else false
	 */
	public boolean searchForTarget(String source, String target);
	/**
	 * Sets the shared update queue through which alogrithm progress is shared
	 * @param updateQueue: 
	 * @return boolean: true if no update queue already, false if set queue replaces existing queue
	 */
	public boolean setUpdateQueue(ArrayBlockingQueue<GraphUpdate> updateQueue);
	
	/**
	 * getter method for the update queue. if no UpdateQueue exists, creates one and returns it
	 * @return
	 */
	public ArrayBlockingQueue<GraphUpdate> getUpdateQueue();

	/**
	 * Returns the path from source to target as an ordered list of vertices
	 * This method should not update the updateQueue
	 * @param source: source vertex
	 * @param target: target vertex
	 * @return LinkedList<String>: path from source to target, represented as an ordered set of strings
	 * if no path is found, an empty List is returned 
	 */
	public LinkedList<String> findPathToTarget(String source, String target);
	
	/**
	 * Returns the distance from the source to the target, or an indicator number if it cannot be found
	 * Indicator = PATH_NOT_FOUND static variable
	 * This method should not update the updateQueue
	 * @param source: source vertex
	 * @param target: target vertex
	 * @return
	 * Smallest distance from path to target, or PATH_NOT_FOUND value if no path can be found
	 */
	public Integer findDistanceToTarget(String source, String target);
	
	/**
	 * Getter method for the predecessors map for the previous search
	 * @return: copy of the predecessors map from the last serach
	 */
	public Map<String,String> getPredecessors();
	
	/**
	 * Getter method for the distance map from the previous search
	 * @return
	 */
	public Map<String,Integer> getDistances();
	
}
 