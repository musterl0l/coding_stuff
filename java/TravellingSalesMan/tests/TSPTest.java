import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import java.lang.Math;


public class TSPTest {

	private TokenGridGraph g1;
	Picture picture;
	
	@Before
	public void setUp() throws Exception, IOException {
		// read graph from file
		
		picture = new Picture("tests/testgrids/supermarket.png");
//		picture = new Picture("tests/testgrids/16x16_supersimple.png");
//		picture = new Picture("tests/testgrids/16x16_waypoints.png");
//		picture = new Picture("tests/testgrids/floorplan_waypoints1.png");
//		picture = new Picture("tests/testgrids/floorplan_waypoints2.png");
	}
    
	@Test
	public void testTSPDrawPathPicture() throws IOException {		
		// This is not an automatic test. It will always succeed. You have to visually inspect the written pictures 
		// found_path_tsp.png written in the project directory.
		// Grey pixels are visited nodes, white pixels are unvisited nodes, red pixels are nodes on the path
		// (black pixels are empty areas with no nodes) 
		// The start node is in the lower-left of the picture
		g1 = new TokenGridGraph(picture);
		g1.setShowSteps(false);

		//This start and end node can be connected in a straight line:
		CellNode startNode = g1.getCellNode(0,0);
		//CellNode startNode = null; //if startNode is set to null, computeTour should use any existing token to start from
		
		System.out.print("Running TSP...");
		List<Node> tour = g1.computeTour(startNode);
		
		
		//Check if all tokens were visited
		HashMap<Integer, Boolean> tokenWasVisited = new HashMap<Integer, Boolean>();
		
		//Add pair (id, visited) to map
		for(CellNode t:g1.tokens)
			tokenWasVisited.put(t.getID(), false);

		//Iterate over tour and check validity
		CellNode lastNode = startNode;
		int i = 0;
		for(Node n: tour)
		{
//			i++;
//			System.out.println("i "+i);
			CellNode cn = (CellNode)n;
			
			//If token from map was found, set its flag to true
			if(tokenWasVisited.containsKey(cn.getID()))
				tokenWasVisited.put(cn.getID(), true);			
			
			//Check if all tour nodes have a valid edge between them
			Assert.assertTrue("Tour contains two unconnected nodes.", lastNode.hasEdgeTo(cn));
			lastNode = cn;
		}
		
		//Check if tour connects back to start
		Assert.assertTrue("Tour does not end at start node", lastNode.hasEdgeTo(startNode));
		
		//Check if all tokens were visited
		for(Map.Entry<Integer, Boolean> b:tokenWasVisited.entrySet())
			Assert.assertTrue("Tour did not visit token "+b.getKey(), b.getValue());		
		
		//Write tour to file
		System.out.println("finished. Tour length: "+tour.size()+" steps.");
		g1.toPicture(tour).savePicture("found_path_tsp.png");

	}
}





