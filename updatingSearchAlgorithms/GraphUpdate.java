package updatingSearchAlgorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GraphUpdate {
	/**
	 * Rep Safety:
	 * - Type is final
	 * - iterationNumber is a primitive type
	 * - Mutable distance object is obscured behind Optional(which in hindsight is useless), accessed by getter which returns a copy
	 * RI:
	 * Integer type: public but final, used to indicate whether a start, update or end message
	 * int iterationNumber: public, used to track how many iterations the algorithm took to complete the search, and to keep track of messages
	 * Optional(Map<String, Integer>): Private, used to securely store the distances for this update
	 * List<String> path to target,  only used in end message to send the final path taken to the target
	 *  	
	 * AF:
	 * Represents a status update from the updating search algorithm
	 * can be either a start, end or update message.
	 * Message is intended as a one off once processed data telegram.
	 */
	public static final int START = 1;
	public static final int END = 2;
	public static final int UPDATE = 0;
	
	public static final int ERROR = 3;
	
	public final int type;
	public int iterationNumber;
	private Optional<Map<String, Integer>> updatedDistances;
	private List<String> pathToTarget;
	
	/**
	 * Constructor for the start and end messages
	 *TODO change this to have a source and target field, to comms back.. maybe...
	 * @param type
	 */
	public GraphUpdate(int type) {
		if(type != START & type!= END & type != UPDATE) {
			System.out.println("Error on creating GraphUpdate object, invalid type passed");
			this.type = ERROR;
			IllegalArgumentException iae = new IllegalArgumentException("type is illegal");
			iae.printStackTrace();
		}
		else if(type == UPDATE) {
			System.out.println("Error on creating GraphUpdate object, no update passed for update type");
			this.type = ERROR;
			System.exit(1);
		}
		else {
			this.type = type;
		}
	}
	
	public GraphUpdate(int type, List<String>pathToTarget) {
		if(type!=END) {
			IllegalArgumentException iae = new IllegalArgumentException("type is illegal");
			iae.printStackTrace();
		}
		this.pathToTarget=pathToTarget;
		this.type = END;
	}
	
	/**
	 * Constructor for distance update for graph
	 * @param updatedDistances
	 */
	public GraphUpdate(Map<String, Integer> updatedDistances, int iteration) {
		this.type = UPDATE;
		this.updatedDistances = Optional.of(updatedDistances);
		this.iterationNumber = iteration;
	}
	
	/**
	 * getter method for the optionally stored distances map
	 * if no distances are present, return empty map
	 * @return
	 */
	public Map<String,Integer> getDistances(){
		if(updatedDistances.isPresent()) {
			return new HashMap<String,Integer>(updatedDistances.get());
		}
		else return new HashMap<String,Integer>();
	}
	
	public List<String> getPathToTarget(){
		return pathToTarget;
	}
	
}
