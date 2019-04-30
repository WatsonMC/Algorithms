package searchAlgorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.Graph;


public class DFSForTopologicalSortedDAGs {
	
	/**
	 * Returns the longest distance path from the top of a graph through it
	 * Graph must be topologically sorted, directed and acyclic (DAG)
	 * @param graph
	 * Topological, DAG graph, with no negative weighted edges
	 * @return
	 * Integer value of longest path through the graph
	 */
	public static int longestPath(Graph<String> graph){
		//uses DFS to find longest path through graph
		Set<String> visited = new HashSet<>();
		Map<String, Integer> distance = new HashMap<>();
		Map<String, List<String>> graphPathes = new HashMap<>();
		//initialise maps
		
		Set<String> verts = graph.vertices();	//DEBUG REMOVE
		
		for(String v: graph.vertices()) {
			graphPathes.put(v, new ArrayList<String>());
			distance.put(v, Integer.MIN_VALUE);
		}
		
		int maxPathDist = 0;
		String maxPathVertex = "";
		for(String v:graph.vertices()) {
			visited.add(v);
			graphPathes.get(v).add(v);
			for(String e: graph.targets(v).keySet()) {
				//new target = e.getTarget
				if(distance.get(e) < distance.get(v)+graph.targets(v).get(e)) {
					//new path is longer
					distance.put(e,distance.get(v)+graph.targets(v).get(e));
					graphPathes.put(e, new ArrayList<>(graphPathes.get(v)));
					if(distance.get(e)>maxPathDist) {
						maxPathDist = distance.get(e);
						maxPathVertex = e;
					}
				}
			}
		}
		//
		List<String> maxPath = graphPathes.get(maxPathVertex);
		for(String vertex: maxPath) {
			System.out.println(Integer.toString(distance.get(vertex)));
		}
		return Collections.max(distance.values());
	}
}
