package algorithmsTests;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import graph.Graph;
import searchAlgorithms.DijkstraSearch;
import undirectedWeightedGraph.UndirectedWeightedGraph;

class DijkstraTest {
	String v1 = "v1";
	String v2 = "v2";
	String v3 = "v3";
	String v4 = "v4";
	String v5 = "v5";

	@Test
	public void testPQMethods() {
		DijkstraSearch djTest = new DijkstraSearch(); //Mock search object
		
		int p1 = 3; //second
		int p2 = 1;	//highest
		int p3 = 60; // third
		
		djTest.addVPToQueue(v1,p1);
		djTest.addVPToQueue(v2,p2);
		djTest.addVPToQueue(v3,p3);
		
		//test priorities correct
		assertTrue(djTest.nextVertexFromQueue().equals(v2));
		assertTrue(djTest.nextVertexFromQueue().equals(v1));
		assertTrue(djTest.nextVertexFromQueue().equals(v3));
		
		//check size is now zero
		assertTrue(djTest.getQSize()==0);
		
		//test prioirty changes
		djTest.addVPToQueue(v1,p1); // lowest prior
		djTest.addVPToQueue(v2,p2);	//highest prior
		
		//Check that changing priority works
		assertTrue(djTest.peekNextVertexFromQueue().equals(v2));
		djTest.changePriority(v1, 0);
		assertTrue(djTest.peekNextVertexFromQueue().equals(v1));
		//check that previous v1 object was correctly deleted
		assertTrue(djTest.getQSize()==2);

	}
	
	@Test
	public void testDijkstraFindAll() {
		Graph<String> gr1 = UndirectedWeightedGraph.empty();
		Map<String, Integer> gr1Distances = new HashMap<>();
		Map<String, String> gr1Pre = new HashMap<>();
		//add 4 vertoces
		gr1.add(v1);
		gr1.add(v2);
		gr1.add(v3);
		
		gr1.set(v1, v2, 1);
		gr1.set(v1, v3, 2);
		
		gr1.set(v2, v3, 3);
		
		/**
		 * V1 -1- v2 -3- v3
		 * 		--2--
		 */
		gr1Distances.put(v1, 0);
		gr1Distances.put(v2, 1);
		gr1Distances.put(v3, 2);
		
		gr1Pre.put(v2, v1);
		gr1Pre.put(v3, v1);
		
		DijkstraSearch djTest = new DijkstraSearch(gr1);
		djTest.searchWholeGraph(v1);
		
		assertTrue(djTest.getDistances().equals(gr1Distances));
		assertTrue(djTest.getPredecessors().equals(gr1Pre));
		
		gr1.add(v4);
		gr1.add(v5);
		
		gr1.set(v2,v4,3);
		gr1.set(v4, v5, 4);
		
		/**
		 * V1 -1- v2 -3- v3 -
		 * 		--2--
		 * 				-3- v4 -4- v5
		 */
		gr1Distances.put(v4, 4);
		gr1Distances.put(v5, 8);
		gr1Pre.put(v4,v2);
		gr1Pre.put(v5,v4);
		
		djTest = new DijkstraSearch(gr1);
		djTest.searchWholeGraph(v1);
		assertTrue(djTest.getDistances().equals(gr1Distances));
		assertTrue(djTest.getPredecessors().equals(gr1Pre));
	}
	
	public void testFindPath() {
		/*
		 * Test Parametrs
		 * 
		 * Path possible?
		 * 1. no path
		 * 2. one path 
		 * 3. multiple pathes
		 * 
		 * 
		 * 
		 *  
		 * 
		 */
		Graph<String> gr1 = UndirectedWeightedGraph.empty();
		Map<String, Integer> gr1Distances = new HashMap<>();
		List<String> gr1Path = new LinkedList<>();
		
		DijkstraSearch djTest = new DijkstraSearch(gr1);
		
		gr1.add(v1);
		gr1.add(v2);
		gr1.add(v3);
		gr1.add(v4);
		gr1.add(v5);
		/**
		 * v1--2--v2--3----------v3
		 * 		--1--v4--1--v5--1^
		 */
		gr1.set(v1, v2, 2);
		gr1.set(v2, v3, 3);
		gr1.set(v1, v4, 1);
		gr1.set(v4, v5, 1);
		gr1.set(v5, v3, 1);
		gr1Path.addAll(Arrays.asList(new String[]{v1,v4,v5,v3}));
		
		assertTrue(djTest.findPathToTarget(v1, v3).equals(gr1Path));
		assertTrue(djTest.findDistanceToTarget(v1, v3).equals(3));
		
		gr1.set(v4, v1, 5);
		gr1.set(v4, v2, 6);
		gr1.set(v4, v3, 7);
		
		gr1Path.clear();
		gr1Path.addAll(Arrays.asList(new String[] {v4,v1,v2}));
		
		assertTrue(djTest.findPathToTarget(v4, v2).equals(gr1Path));
		assertTrue(djTest.findPathToTarget(v4, v2).equals(gr1Path));
		assertTrue(djTest.findDistanceToTarget(v1, v3).equals(3));
		
		//remove v3 for testing no pathes
		gr1.remove(v3);
		gr1.add(v3);
		
		//clear path object, no pathes
		gr1Path.clear();
		assertTrue(djTest.findPathToTarget(v3, v1).equals(gr1Path));
		assertTrue(djTest.findPathToTarget(v3, v2).equals(gr1Path));
		assertTrue(djTest.findPathToTarget(v3, v4).equals(gr1Path));
		assertTrue(djTest.findPathToTarget(v3, v5).equals(gr1Path));
		assertTrue(djTest.findDistanceToTarget(v3, v4).equals(0));
		assertTrue(djTest.findDistanceToTarget(v3, v1).equals(0));
		assertTrue(djTest.findDistanceToTarget(v3, v2).equals(0));
		assertTrue(djTest.findDistanceToTarget(v3, v5).equals(0));
	}
	
}
