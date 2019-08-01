package updatingSearchAlgorithms;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import graph.Graph;
import searchAlgorithms.AStarSearch;
import graph.VertexDistancePair;
import queues.MinDistanceQueue;

public class AStarSearchUpdatingVariableHeuristic  implements UpdatingSearchAlgorithm {
	private PriorityQueue<VertexDistancePair> pQ;
	private Map<String, Integer> gScore;
	private Map<String, Integer> fScore;
	private Map<String, String> predecessors;
	private Set<String> vertices;
	private Graph<String> graph;
	
	private List<String> pathToTarget;
	
	private String source;
	private String target;
	private Integer xt;
	private Integer yt;
	
	private static Integer WEIGHTING;
	private static final Integer INF = Integer.MAX_VALUE;
	
	private ArrayBlockingQueue<GraphUpdate> updateQueue;
	private Integer iteration;	//used only to inform updates of iteration number

	//Heuristics
	public static final int DIAGONAL = 1;
	public static final int EUCLIDIAN = 2;
	public static final int MANHATTAN = 3;
	
	public static final Map<Integer, String> HEURISTICS;
	static {
		Map<Integer, String> tempMap = new HashMap<>();
		tempMap.put(DIAGONAL,"DIAGONAL");
		tempMap.put(EUCLIDIAN,"EUCLIDIAN");
		tempMap.put(MANHATTAN,"MANHATTAN");
		HEURISTICS = tempMap;
	}

	private final Integer heuristicType; 
	/**
	 * Usage:
	 * This algorithm assumes that vertex labes are of the form:
	 * "x,y" - where x and y are the respective cartesian co-ordinates of the vertex
	 *
	 * It should only be used for searches of cartesian grids.
	 * 
	 *  HEURISTIC FUNCTION:
	 * The Heuristic function for this algorithm is diagonal distance by cartesian co-ordinates.
	 * Where xs,ys = source x,y,
	 * xt,yt = target x,y and
	 * Weighting= weighting factor for the function. By default, this is 1, indicating a single unit of distance 
	 * between any adjacent grid positions. 
	 * 	 * 
	 * h(source,target) = Sqrt([{xs-xt}^2 + {xs-xt^2}]*Weighting^2), rounded to nearest integer
	 */
	
	public AStarSearchUpdatingVariableHeuristic(Graph<String> graph, Integer heuristic) {
		this.graph = graph;
		WEIGHTING = 100;	//all other values initialised at search time
		//default weighting for SAV is 100
		if(!HEURISTICS.keySet().contains(heuristic)) {
			System.out.println("ERROR, no heuristic of that type: " + heuristic.toString());
		}
		this.heuristicType = heuristic;
	}
	
	
	private void initialise(String source, String target) {
		this.target = target;
		initialise(source, true);
	}
	
	private void initialise(String source, boolean searchForTarget) {
		this.pQ = new PriorityQueue<>();
		this.fScore = new HashMap<>();
		this.gScore = new HashMap<>();
		this.predecessors = new HashMap<>();
		this.vertices = graph.vertices();
		for(String vertex: vertices) {
			if(!vertex.equals(source)) {
				gScore.put(vertex,INF); //initlialise max
				fScore.put(vertex,INF);	// initialse max
			}
		}
		this.source = source;
		yt = null;
		xt = null;
		
		pathToTarget = new LinkedList<>();
		
		gScore.put(source, 0);	//Define the distance of the start node as 0
		if(searchForTarget) {
			fScore.put(source,hScore(source)); //define h score of start as hscore only
		}
		else {
			fScore.put(source, gScore.get(source));
		}
		
		pQ.add(new VertexDistancePair(source, fScore.get(source)));	// add the starting node to the pQ
		if(updateQueue == null) {
			getUpdateQueue();
		}
		pushStartMessage();
	}
	
	@Override
	public void searchWholeGraph(String source) {
		// TODO Auto-generated method stub
		//basically just Djirskas since h(x) == 0 for all x
		initialise(source, false);
		pushUpdatedDistances();
		String current ="";
		//main loop
		while(!pQ.isEmpty()) {
			current = pQ.remove().vertex;	//removes the lowest fScore vertex from the open set (pQ)
//			System.out.println(String.format("Selected vertex %S with f score %s", current, fScore.get(current)));
			System.out.println("Currently searching from: " + current);
			Map<String, Integer> neighbourMap = graph.targets(current);	
			//TODO Check if this needs to be neighbours
			for(String neighbour:neighbourMap.keySet()) {
				//loop through all neighbours of current vertex
				Integer newG =gScore.get(current) + neighbourMap.get(neighbour);//add or alter the VDP in the pQ for new values
				if(newG<gScore.get(neighbour)) {
					changePriority(neighbour,newG); //add or alter the VDP in the pQ for new values
					gScore.put(neighbour, newG);	//set new distance value as tentative g
					predecessors.put(neighbour, current); //update map of predecessors
//					System.out.println(String.format("new distance pushed: %s = %s",neighbour,newG));
//					System.out.println(String.format("new fScore pushed: %s = %s",neighbour,newF));
					pushUpdatedDistances(neighbour, newG);	
				}
			}
		}
		
		this.pathToTarget = populatePathToTarget(source, getFurthestTravelledVertex());	//not longest
		pushEndMessage(false);
	}

	@Override
	public boolean searchForTarget(String source, String target) {
		initialise(source,target 
				);
		pushUpdatedDistances();
		String current ="";
		//main loop
		while(!pQ.isEmpty()) {
			current = pQ.remove().vertex;	//removes the lowest fScore vertex from the open set (pQ)
//			System.out.println(String.format("Selected vertex %S with f score %s", current, fScore.get(current)));
			if(current.equals(target)) {
				//finished, found source
				/**
				 * Do something
				 */
				this.pathToTarget = populatePathToTarget(source,target);
				pushEndMessage(true);
				return true;
			}
			System.out.println("Currently searching from: " + current);
			Map<String, Integer> neighbourMap = graph.targets(current);	
			//TODO Check if this needs to be neighbours
			for(String neighbour:neighbourMap.keySet()) {
				//loop through all neighbours of current vertex
				Integer newG =gScore.get(current) + neighbourMap.get(neighbour);//add or alter the VDP in the pQ for new values
				if(newG<gScore.get(neighbour)) {
					Integer newF = newG +hScore(neighbour);	//need to reset priority first
					changePriority(neighbour,newF); //add or alter the VDP in the pQ for new values
					gScore.put(neighbour, newG);	//set new distance value as tentative g
					predecessors.put(neighbour, current); //update map of predecessors
//					System.out.println(String.format("new distance pushed: %s = %s",neighbour,newG));
//					System.out.println(String.format("new fScore pushed: %s = %s",neighbour,newF));
					pushUpdatedDistances(neighbour, newG);	
				}
			}
		}
		
		this.pathToTarget = populatePathToTarget(source, getFurthestTravelledVertex());	//not longest
		pushEndMessage(false);
		return false; //target not found
	}
	
	/**
	 * Returns the hScore value for the respective heurstic type 
	 * as defined by the heuristicType field
	 * @param source
	 * @param target
	 * @return rounded integer of their distances
	 * 
	 */
	public Integer hScore(String vertex) {
		if(yt ==null ||xt == null) {
			xt = Integer.parseInt(target.split(",")[0]);
			yt = Integer.parseInt(target.split(",")[1]);
		}
		switch(heuristicType){
			case DIAGONAL:
				return hScoreDiagonal(vertex);
			case EUCLIDIAN:
				return hScoreEuc(vertex);
			case MANHATTAN:
				return hScoreManhattan(vertex);
			default:
				return 0;
		}
	}
	
	public Integer hScoreDiagonal(String vertex) {
		Integer xs = Integer.parseInt(vertex.split(",")[0]);
		Integer ys = Integer.parseInt(vertex.split(",")[1]);
		int dx = Math.abs(xt-xs);
		int dy = Math.abs(ys-yt);
		double diag = WEIGHTING*Math.max(dx,dy) + (WEIGHTING*1.41)*Math.min(dx, dy);
//		double dig = WEIGHTING*Math.max(dx, dy);
		return (int)Math.ceil(diag);
	}
	
	public Integer hScoreEuc(String vertex) {
		Integer xs = Integer.parseInt(vertex.split(",")[0]);
		Integer ys = Integer.parseInt(vertex.split(",")[1]);
		return(WEIGHTING*(Math.abs(xt-xs)+Math.abs(yt-ys)));
	}
	
	public Integer hScoreManhattan(String vertex) {
		Integer xs = Integer.parseInt(vertex.split(",")[0]);
		Integer ys = Integer.parseInt(vertex.split(",")[1]);
		return(WEIGHTING*(Math.abs(xt-xs) +Math.abs(yt-ys)));
	}
	
	
	/**
	 * Helper method to change priority of a given vertex in the priority queue
	 * @param vertex
	 * @param new score to apply to the VDP
	 */
	public void changePriority(String vertex, Integer newScore) {
		pQ.remove(new VertexDistancePair(vertex,fScore.get(vertex)));
		pQ.add(new VertexDistancePair(vertex,newScore));
		fScore.put(vertex, newScore);
	}
	/**
	 * Returns the vertex label of the longest travelled distance - use to return
	 * correct path when no path is found
	 */
	protected String getFurthestTravelledVertex() {
		Integer highestDistance = 0;
		String furthestVertex  ="";
		for(String vertex: gScore.keySet()) {
			if(gScore.get(vertex)!= INF &&gScore.get(vertex)>highestDistance) {
				highestDistance = gScore.get(vertex);
				furthestVertex = vertex;
			}
		}
		return furthestVertex;
	}
	
	/**
	 * Called after search has completed, creates the path to target list
	 * @return
	 */
	protected List<String> populatePathToTarget(String source, String target){
		String currVertex = target;
		List<String> tempList = new LinkedList<>();
		
		while(!currVertex.equals(source)) {
			tempList.add(0,currVertex);
			currVertex = predecessors.get(currVertex);
			if(currVertex == null) {
				System.out.println(String.format("Current vertex %s does not have a predeccessor, no path to target", tempList.get(0)));
			}
		}
		tempList.add(0,currVertex);
		Collections.reverse(tempList);		
		return tempList;
	}

	@Override
	public boolean setUpdateQueue(ArrayBlockingQueue<GraphUpdate> updateQueue) {
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
	public List<String> findPathToTarget(String source, String target) {
		// TODO Auto-generated method stub
		searchForTarget(source, target);
		return this.pathToTarget;
	}

	@Override
	public Integer findDistanceToTarget(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getPredecessors() {
		return new HashMap<String,String>(predecessors);
	}

	@Override
	public Map<String, Integer> getDistances() {
		return new HashMap<String,Integer>(gScore);
	}

	 //Distance = gScore remember
	@Override
	public void pushUpdatedDistances(String vertex, Integer distance) {
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
	
	//Distance = gScore remember
	@Override
	public void pushUpdatedDistances() {
		Map<String, Integer> update = new HashMap<>(gScore);
		try {
			updateQueue.put(new GraphUpdate(update, iteration));
		}
		catch(InterruptedException ie) {
			System.out.println("Interupted when attempting to push update ");
			ie.printStackTrace();
		}
		iteration++;
	}

	@Override
	public void pushEndMessage(boolean pathFound) {
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

	@Override
	public void pushStartMessage() {
		iteration = 0;
		try {
			updateQueue.put(new GraphUpdate(GraphUpdate.START));
		}
		catch(InterruptedException ie) {
			System.out.println("Interupted when attempting to push start message");
			ie.printStackTrace();
		}
	}
}
