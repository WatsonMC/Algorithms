package updatingSearchAlgorithms;

import java.util.List;
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
	public static final int DJIRKSTA = 1;
	public static final int A_STAR_DIAGONAL = 2;
	public static final int A_STAR_EUCLIDIAN = 3;
	public static final int A_STAR_MANHATTAN = 4;
	/**
	 * Create a new search algorithm object with the graph to be searched
	 * @param graph: graph<String,Integer> object to be searched
	 * @param type: Integer, used to select required search algorithm
	 * @return
	 */
	public static UpdatingSearchAlgorithm newSearch(Graph<String> graph, Integer type) {
		//TODO implement factory for updatingsearchalgorithm
		switch(type) {
			case(DJIRKSTA):
				return new DjirkstaSearchUpdating(graph);
			case(A_STAR_DIAGONAL):
				return new AStarSearchUpdatingVariableHeuristic(graph,
						AStarSearchUpdatingVariableHeuristic.DIAGONAL);
			case(A_STAR_EUCLIDIAN):
				return new AStarSearchUpdatingVariableHeuristic(graph,
						AStarSearchUpdatingVariableHeuristic.EUCLIDIAN);
			case(A_STAR_MANHATTAN):
				return new AStarSearchUpdatingVariableHeuristic(graph,
						AStarSearchUpdatingVariableHeuristic.MANHATTAN);
			default:
				System.out.println("Incorrect argument to USA creator: " + type.toString());
				return null;
		}
	}
		
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
	public void searchWholeGraph(String source);
	
	
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
	public List<String> findPathToTarget(String source, String target);
	
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
	/**
	 * Push updates of a single vertex distance to queue
	 * @param vertex - the vertex label to be updated
	 * @param distance - the distance value to be pushed
	 * Use: for pushing individual updates as the algorthirm progresses
	 */
	public void pushUpdatedDistances(String vertex, Integer distance);
	
	/**
	 * Push entire distance map to update queue
	 * Use: for initialising the recieving application with the algo start state
	 * or generally doing a full update
	 */
	public void pushUpdatedDistances();
	
	/**
	 * Push end message to recieving application. Must push a GraphUpdate.END type object
	 * with a List<String> representing the path to target
	 * path to target rep:
	 * [target, vn,vn-1.....v1, source]
	 * @param pathFound - flag for end result of algo
	 * 
	 */
	public void pushEndMessage(boolean pathFound);
	
	/**
	 * Pushes start message to recieving application. Must be  GraphUpdate.START type
	 */
	public void pushStartMessage();

	
}
 