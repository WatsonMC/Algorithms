package updatingSearchAlgorithmTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

import graph.Graph;
import undirectedWeightedGraph.UndirectedWeightedGraph;
import updatingSearchAlgorithms.GraphUpdate;
import updatingSearchAlgorithms.UpdatingSearchAlgorithm;

public class UpdatingSearchAlgorithmTest {
	String v1 = "v1";
	String v2 = "v2";
	String v3 = "v3";
	String v4 = "v4";
	String v5 = "v5";

	
	@Test
	public void testQueueMessages() {
		/**
		 * Test:
		 * - Correct number and order of messages in queue
		 * - Message content is correct
		 * -TODO Test that the path is correctly sent in the event that it is found
		 */
		Graph<String> gr1 = UndirectedWeightedGraph.empty();
		gr1.add(v1);
		gr1.add(v2);
		gr1.add(v3);
		gr1.add(v4);
		
		gr1.set(v1, v2, 1);
		gr1.set(v2, v3, 1);
		gr1.set(v2, v4, 1);
		
		UpdatingSearchAlgorithm testQueue = UpdatingSearchAlgorithm.empty(gr1);
		
		ArrayBlockingQueue<GraphUpdate> updateQueue = new ArrayBlockingQueue<>(10);
		testQueue.setUpdateQueue(updateQueue);
		
		// Test update queue order
		testQueue.searchForTarget(v1, v4);
		try {
			GraphUpdate currentUp = updateQueue.take();
			assertTrue(currentUp.type == GraphUpdate.START);
			int iterationCounter = 0;
			while(!updateQueue.isEmpty()) {
				currentUp = updateQueue.take();
				if(currentUp.type != GraphUpdate.UPDATE) {
					assertTrue(updateQueue.isEmpty());
				}
				else {
					assertTrue(currentUp.type == GraphUpdate.UPDATE);
					System.out.println("current it number = " + iterationCounter + " current update it is "  + currentUp.iterationNumber);
					assertTrue( currentUp.iterationNumber == iterationCounter);
					iterationCounter++;
				}
			}
			
		}
		catch(InterruptedException ie) {
		
		}
		
	}
	
	public void getsFindAll() {
		Graph<String> gr1 = Graph.empty();
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
		
		UpdatingSearchAlgorithm testFinAll = UpdatingSearchAlgorithm.empty(gr1);
		Map<String,Integer> resultDistances = testFinAll.searchWholeGraph(v1);
		
		
		assertTrue(resultDistances.equals(gr1Distances));
		assertTrue(testFinAll.getPredecessors().equals(gr1Pre));
		
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
		
		testFinAll = UpdatingSearchAlgorithm.empty(gr1);
		testFinAll.searchWholeGraph(v1);
		assertTrue(testFinAll.getDistances().equals(gr1Distances));
		assertTrue(testFinAll.getPredecessors().equals(gr1Pre));
	}
	
	@Test
	public void testPathDeliveries() {
		//test:
		// Path send for source-target when no path exists
		// Path sent when search whole graph and path blocked
		// path sent when search whole graph and no path blocked exit
		Graph<String> gr1 = Graph.empty();
		Map<String, Integer> gr1Distances = new HashMap<>();
		List<String> gr1Path = new LinkedList<>();
		
		UpdatingSearchAlgorithm testAlgo = UpdatingSearchAlgorithm.empty(gr1);
		ArrayBlockingQueue<GraphUpdate> testQueue = new ArrayBlockingQueue<>(1000);
		testAlgo.setUpdateQueue(testQueue);
		String v1 = "1";
		String v2 = "2";
		String v3 = "3";
		String v4 = "4";
		String v5 = "5";

		gr1.add(v1);
		gr1.add(v2);
		gr1.add(v3);
		gr1.add(v4);
		gr1.add(v5);
		
		//test 1, source to whole graph, check end path is generated
		gr1.set(v1, v2, 1);
		gr1.set(v1, v4, 1);
		gr1.set(v1, v3, 1);
		gr1.set(v2, v4, 1);
		gr1.set(v3, v4, 1);
		gr1.set(v4, v5, 1);
		
		testAlgo.searchWholeGraph(v1);
		
		try {
			GraphUpdate currUpdate = testQueue.take();
			while(currUpdate.type!= GraphUpdate.END) {
				currUpdate = testQueue.take();
			}
			assertTrue(currUpdate.getPathToTarget()!= null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//test 2, no path to all nodes should still return a path
		testAlgo = UpdatingSearchAlgorithm.empty(gr1);
		testQueue = new ArrayBlockingQueue<>(1000);
		testAlgo.setUpdateQueue(testQueue);
		testAlgo.searchWholeGraph(v1);

		gr1.set(v4, v5, 0);
		try {
			GraphUpdate currUpdate = testQueue.take();
			while(currUpdate.type!= GraphUpdate.END) {
				currUpdate = testQueue.take();
			}
			assertTrue(currUpdate.getPathToTarget()!= null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//test 3, source to target should still provide a path even if target cannot be found
		testAlgo = UpdatingSearchAlgorithm.empty(gr1);
		testQueue = new ArrayBlockingQueue<>(1000);
		testAlgo.setUpdateQueue(testQueue);
		testAlgo.searchForTarget(v1,v5);
		try {
			GraphUpdate currUpdate = testQueue.take();
			while(currUpdate.type!= GraphUpdate.END) {
				currUpdate = testQueue.take();
			}
			assertTrue(currUpdate.getPathToTarget()!= null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
		 */
		Graph<String> gr1 = Graph.empty();
		Map<String, Integer> gr1Distances = new HashMap<>();
		List<String> gr1Path = new LinkedList<>();
		
		UpdatingSearchAlgorithm findPathTest = UpdatingSearchAlgorithm.empty(gr1);
		
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
		
		assertTrue(findPathTest.findPathToTarget(v1, v3).equals(gr1Path));
		assertTrue(findPathTest.findDistanceToTarget(v1, v3).equals(3));
		
		gr1.set(v4, v1, 5);
		gr1.set(v4, v2, 6);
		gr1.set(v4, v3, 7);
		
		gr1Path.clear();
		gr1Path.addAll(Arrays.asList(new String[] {v4,v1,v2}));
		
		assertTrue(findPathTest.findPathToTarget(v4, v2).equals(gr1Path));
		assertTrue(findPathTest.findPathToTarget(v4, v2).equals(gr1Path));
		assertTrue(findPathTest.findDistanceToTarget(v1, v3).equals(3));
		
		//remove v3 for testing no pathes
		gr1.remove(v3);
		gr1.add(v3);
		
		//clear path object, no pathes
		gr1Path.clear();
		assertTrue(findPathTest.findPathToTarget(v3, v1).equals(gr1Path));
		assertTrue(findPathTest.findPathToTarget(v3, v2).equals(gr1Path));
		assertTrue(findPathTest.findPathToTarget(v3, v4).equals(gr1Path));
		assertTrue(findPathTest.findPathToTarget(v3, v5).equals(gr1Path));
		assertTrue(findPathTest.findDistanceToTarget(v3, v4).equals(0));
		assertTrue(findPathTest.findDistanceToTarget(v3, v1).equals(0));
		assertTrue(findPathTest.findDistanceToTarget(v3, v2).equals(0));
		assertTrue(findPathTest.findDistanceToTarget(v3, v5).equals(0));
	}
	
}
