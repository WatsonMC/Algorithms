package searchAlgorithms;

import java.util.List;

public interface SearchAlgorithm {
	/**
	 * A search algorithm for travsersing graphs with String vertices
	 * 
	 */
	//find shortest path
	//find all pathes from source
	
	//TODO determine whether this interface handles sourc=target
	public boolean searchForTarget(String source, String target);
	
	public void searchWholeGraph(String source);
	
	
	public List<String> findPathToTarget(String source, String target);
	
	public Integer findDistanceToTarget(String source, String target);
	
	//public
	
	
	//get predecessors
	//get distances
	//get shortest path
	
	
	
	
}
