package combinationsPermutations;

import java.util.LinkedList;
import java.util.List;

public class SimpleCombination {
	//return all combinations of objects Obect n with size r
	
	public static List<List<Object>> combinations(List<Object> objects, int size, List<Object> currentList){
		//base case size == 0
		if (size == 0) {
			List<List<Object>> tmp =  new LinkedList<>();
			tmp.add(new LinkedList<Object>(currentList));
			return tmp;
		}
		else{
			return new LinkedList<List<Object>>();
		}
	}
}
