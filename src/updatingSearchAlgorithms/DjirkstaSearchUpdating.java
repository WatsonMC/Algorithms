package updatingSearchAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import graph.Graph;
import graph.VertexDistancePair;

public class DjirkstaSearchUpdating implements UpdatingSearchAlgorithm{
	/**
	 * RI:
	 * Graph: undirected graph containing string vertices and non-negative weights, represnting the graph to be traverssed
	 * Iteration: count of how many iterations have been performed in a given algorithm application
	 */
	
	private ArrayBlockingQueue<GraphUpdate> updateQueue;
	private Integer iteration;	//used only to inform updates of iteration number
	
	private PriorityQueue<VertexDistancePair> pQ;

	private Map<String,Integer> distances;
	private Map<String, String> predecessors;
	private Set<String> vertices;
	private Graph<String> graph;
	
	private List<String> pathToTarget;
	
	public  DjirkstaSearchUpdating(Graph<String> graph) {
		this.graph = graph;
	}
	
	/**
	 * Empty constructorfor testing mostly
	 */
	protected DjirkstaSearchUpdating() {
		
	}
	
	/**
	 * Initialising routine for starting a new search
	 * - resets priroty queue 
	 * - resets distance map
	 * - resets predecessors queue
	 * - sets distances to Max Val
	 * - starts  
	 * @param source
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
		if(updateQueue == null) {
			getUpdateQueue();
		}
		pushStartMessage();
	}
	
	/**
	 * Method to update the priority of an existing item in the priority queue. 
	 * removes the item and replaces it with a new item of the same vertex, but with new distance
	 * @param vertex
	 * @param newDistance: new integer distance between the source and the vertex
	 */
	private void changePriority(String vertex, Integer newDistance) {
		pQ.remove(new VertexDistancePair(vertex,distances.get(vertex)));
		pQ.add(new VertexDistancePair(vertex,newDistance));
		distances.put(vertex, newDistance);
	}
	
	@Override
	public Map<String, Integer> searchWholeGraph(String source) {
		initialise(source);
		pushUpdatedDistances();
		String prevVertex = source;
		String currVertex = source;
		while(!pQ.isEmpty()) {
			currVertex = pQ.remove().vertex; 
			vertices.remove(currVertex);
			int distance = distances.get(currVertex);
			if(distances.get(currVertex)== Integer.MAX_VALUE) {
				//this vertex was unreachable, exit routine, don't need to push since on updates
				createPathToTarget(source,prevVertex);
				break;
			}
			
			Map<String, Integer> neigbourDistances = graph.targets(currVertex);
			for(String neigbourVertex:neigbourDistances.keySet()) {
				if(vertices.contains(neigbourVertex)) {//iff vertex is not yet visited
					int tentDistance = distances.get(currVertex) + neigbourDistances.get(neigbourVertex);
					if(tentDistance<distances.get(neigbourVertex)) {
						changePriority(neigbourVertex,tentDistance);
						distances.put(neigbourVertex, tentDistance);
						predecessors.put(neigbourVertex,currVertex);
						//Pushing here provides update for every time the dinstance map is altered
						//pushUpdatedDistances();
						pushUpdatedDistances(neigbourVertex,tentDistance);
					}
				}
			}
			prevVertex = currVertex;
			//TODO If push update heree then we get only the update from each visited node		
			//TODO add setting/flag to the algorithm which allows choosing every update or just each node visit update
		}
		if(pathToTarget == null) {
			createPathToTarget(source, currVertex);
		}
		pushEndMessage(false);
		return new HashMap<String,Integer>(distances);
	}

	@Override
	public boolean searchForTarget(String source, String target) {
		initialise(source);
		pushUpdatedDistances();
		String prevVertex = source;
		while(!pQ.isEmpty()) {
			String currVertex = pQ.remove().vertex;	//get next highest vertex
			vertices.remove(currVertex);	//remove from vertex set -> this is probably not necessary, since queue is a set too

			if(distances.get(currVertex) == Integer.MAX_VALUE) {
				System.out.println("Ended search early with no route to target");
				createPathToTarget(source,prevVertex);
				break;	//next highest priority vertex has no path to destination
			}
			
			if(currVertex.equals(target)) {
				//have found destination, algorithm over, store result and return
				//find the closest of neighbours -> already done since closest unvisited is always checked first
				createPathToTarget(source,target);
				pushEndMessage(true);
				return true;
			}
			
			Map<String, Integer> neighbourDistances = graph.targets(currVertex);
			boolean endOfTheRoad = true;
			for(String neigbour: neighbourDistances.keySet()) {
				if(vertices.contains(neigbour)) {	//iff vertex is not yet visited
					endOfTheRoad = false;
					int tentDist = distances.get(currVertex)+ neighbourDistances.get(neigbour);//calculat new path distancev
					if(tentDist<distances.get(neigbour)) {
						changePriority(neigbour,tentDist);
						distances.put(neigbour, tentDist); //new distance for that node is the tentative distance
						predecessors.put(neigbour,currVertex);
						pushUpdatedDistances(neigbour,tentDist);
					}
				}
				//pushUpdatedDistances();
			}
			prevVertex = currVertex;
			//TODO pushUpdatedDistances if flag set for visited vertices update mode
		}
		pushEndMessage(false);
		return false;
	}

	private void createPathToTarget(String source, String target) {
		pathToTarget = new ArrayList<>();
		String currentVertex = target; 
		while(!currentVertex.equals(source)) {
			pathToTarget.add(currentVertex);
			currentVertex = predecessors.get(currentVertex);
		}
		pathToTarget.add(source);
	}
	
	@Override
	public LinkedList<String> findPathToTarget(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer findDistanceToTarget(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setUpdateQueue(ArrayBlockingQueue<GraphUpdate> updateQueue) {
		// TODO Auto-generated method stub
		if(this.updateQueue == null) {
			this.updateQueue = updateQueue;
			return true;
		}
		this.updateQueue = updateQueue;
		return false;
	}

	@Override
	public ArrayBlockingQueue<GraphUpdate> getUpdateQueue() {
		if(updateQueue == null) {
			this.updateQueue = new ArrayBlockingQueue<GraphUpdate>(10);
		}
		return this.updateQueue;
	}
	
	
	
	@Override
	public Map<String, String> getPredecessors() {
		Map<String,String> temp = new HashMap<>(predecessors);
		return temp;
	}

	@Override
	public Map<String, Integer> getDistances() {
		return new HashMap<String,Integer>(distances);
	}

	/**
	 * Method to push updated distances object to the queue for processing
	 * transfers the distances and the iteration number, and increments it
	 */
	private void pushUpdatedDistances() {
		Map<String, Integer> update = new HashMap<>(distances);
		try {
			updateQueue.put(new GraphUpdate(update, iteration));
		}
		catch(InterruptedException ie) {
			System.out.println("Interupted when attempting to push update ");
			ie.printStackTrace();
		}
		iteration++;
	}
	
	//Test method for only pushing the updated neighbour nodes
	private void pushUpdatedDistances(String vertex, Integer distance) {
		Map<String, Integer> update = new HashMap<>();
		update.put(vertex, distance);
		try {
			updateQueue.put(new GraphUpdate(update, iteration));
		}
		catch(InterruptedException ie) {
			System.out.println("Interupted when attempting to push update ");
			ie.printStackTrace();
			return;
		}
		iteration++;
	}
	
	
	/**
	 * creates and sends a graph update object to the queue
	 * resets iteration count
	 */
	private void pushStartMessage() {
		iteration = 0;
		try {
			updateQueue.put(new GraphUpdate(GraphUpdate.START));
		}
		catch(InterruptedException ie) {
			System.out.println("Interupted when attempting to push start message");
			ie.printStackTrace();
		}
	}
	
	private void pushEndMessage(boolean pathFound) {
		try {
			if(pathFound) {	//now redunant since we send the path regardless of whether 
				updateQueue.put(new GraphUpdate(GraphUpdate.END,pathToTarget));
			}
			else{
				updateQueue.put(new GraphUpdate(GraphUpdate.END,pathToTarget));
			}
		}
		catch(InterruptedException ie) {
			System.out.println("Interupted when attempting to push end message");
			ie.printStackTrace();
		}
	}
}
	
