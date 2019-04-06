import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;

public class GraphTest {

	// test if the MinSpanTree has the correct number of edges
	@Test
	public void testEdgeCount() {
		Graph g1;
		try {
			g1 = GraphIO.loadGraph("tests/testgraphen/test_kruskal01.txt");

			g1.setShowSteps(true);
			VisualGraph v = new VisualGraph(g1);
			
			
			// get mst
			Graph mst = g1.toMinSpanTree();
			int edgeCount = 0;
			for (Node n : mst.getNodes()) {
				for (Edge e : n.getIncidentEdges()) {
					edgeCount++;
				}
			}
			mst.setShowSteps(true);
			VisualGraph m = new VisualGraph(mst);
			
			Assert.assertEquals(
					"Tree does not have the correct number of edges. Watch out for backward edges!",
					12, edgeCount);
			// number of edges should be 2*(|V|-1) because there are two
			// directed edges in
			// the tree for each undirected edge in the MST

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// Test correct total edge weight of MST
	@Test
	public void testWeight() {
		Graph g1;
		try {
			g1 = GraphIO.loadGraph("tests/testgraphen/test_kruskal01.txt");

			// get mst
			Graph mst = g1.toMinSpanTree();

			// get actual weight
			int actual = 0;
			for (Node n : mst.getNodes()) {
				for (Edge e : n.getIncidentEdges()) {

					// Skip return edges (we will mark them with status 5)
					if (e.status == 5)
						continue;

					actual += e.getWeight();

					// Mark edge (a,b)
					e.status = 5;

					// Mark edge (b,a) too
					Node target = e.getEndnode();
					for (Edge f : target.getIncidentEdges()) {
						if (f.getEndnode() == n)
							f.status = 5;
					}
				}
			}

			// get correct weight
			int expected = 12;

			Assert.assertEquals("This is not the minimum spanning tree",
					expected, actual);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// You can implement the following tests yourself.
	// We recommend to add more test than just these examples.

	// Test if every node has at least one edge (in an MST with more than 1
	// node)
	@Test
	public void testEveryNodeHasEdges() {
	}

	// Test if the MinSpanTree actually connects all nodes
	@Test
	public void testConnectivity() {
		Graph g1;
		try {
			g1 = GraphIO.loadGraph("tests/testgraphen/test_kruskal03.txt");

			g1.setShowSteps(true);
			VisualGraph v = new VisualGraph(g1);
			
			
			// get mst
			Graph mst = g1.toMinSpanTree();
			
			
		
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (RuntimeException e2){
			System.out.println("Exception: "+e2.getMessage());
		}
		
	}

}

