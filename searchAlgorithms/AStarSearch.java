package searchAlgorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import graph.Graph;

public class AStarSearch implements SearchAlgorithm{
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
	
	
	
	public AStarSearch(Graph<String> graph) {
		this.graph = graph;
		WEIGHTING = 1;	//all other values initialised at search time
	}
	
	/**
	 * Searches from the specified source to the specified target
	 * @param source
	 * @param target
	 * @return true if target is found, false elsewise. Path to target is stored in predecessors map
	 * Call path to target getter method to find.
	 */
	public boolean searchForTarget(String source, String target) {
		initialise(source, target);
		String current ="";
		//main loop
		while(!pQ.isEmpty()) {
			current = pQ.poll().vertex;	//removes the lowest fScore vertex from the open set (pQ)
			if(current.equals(target)) {
				//finished, found source
				/**
				 * Do something
				 */
				this.pathToTarget = populatePathToTarget(source,target);
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
					fScore.put(neighbour, newF); 	// F score value is new distance + heuristic
					predecessors.put(neighbour, current); //update map of predecessors
					if(current.equals("1,1")) {
						System.out.println("puase_");}
				}
			}
		}
		
		this.pathToTarget = populatePathToTarget(source, getFurthestTravelledVertex());	//not longest
		return false; //target not found
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
		return tempList;
	}
	
	private void initialise(String source, String target) {
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
		this.target = target;
		yt = null;
		xt = null;
		
		pathToTarget = new LinkedList<>();
		
		gScore.put(source, 0);	//Define the distance of the start node as 0
		fScore.put(source,hScore(source)); //define h score of start as hscore only
		
		pQ.add(new VertexDistancePair(source, fScore.get(source)));	// add the starting node to the pQ
	}
	/**
	 * Standard diagonal (euclidean?) distance calc
	 * @param source
	 * @param target
	 * @return rounded integer of their distances
	 * 
	 */
	public Integer hScore(String vertex) {
		Integer xs = Integer.parseInt(vertex.split(",")[0]);
		Integer ys = Integer.parseInt(vertex.split(",")[1]);
		//first time round define the target
		if(yt ==null ||xt == null) {
			xt = Integer.parseInt(target.split(",")[0]);
			yt = Integer.parseInt(target.split(",")[1]);
		}
		double h = Math.sqrt((Math.pow(xt-xs, 2) + Math.pow(yt-ys, 2))*Math.pow(WEIGHTING, 2));
		return (int) Math.round(h);
	}
	
	/**
	 * Sets the weighting value, representing the distance between adjacent positions on the grid
	 * @param weight - integer, greater than 0
	 */
	public void setHWeight(Integer weight) {
		if(weight<=0) {
			System.out.println(String.format("Invalid weight setting for AStarSearch heuristic: %s" ,weight.toString()));
		}
		else {
			WEIGHTING = weight;
		}
		
	}
	
	/**
	 * Helper method to change priority of a given vertex in the priority queue
	 * @param vertex
	 * @param new score to apply to the VDP
	 */
	public void changePriority(String vertex, Integer newScore) {
		pQ.remove(new VertexDistancePair(vertex,fScore.get(vertex)));
		pQ.add(new VertexDistancePair(vertex,newScore));
	}
	
	public List<String> getPathToTarget(){
		return new LinkedList<>(pathToTarget);
	}

	@Override
	public void searchWholeGraph(String source) {
		// TODO Auto-generated method stub
		//this is just gpin
		
	}

	@Override
	public List<String> findPathToTarget(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer findDistanceToTarget(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}
	 
}
