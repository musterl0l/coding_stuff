import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import java.util.LinkedList;
import java.util.List;

/**
 * A data structure to store a graph consisting of a set of unlabeled nodes and
 * a set of directed, weighted edges connecting the nodes.
 * 
 * The interface provides methods to add nodes and edges. Both methods can be
 * called at all time and the data structure should not assume a limitation on
 * the number of nodes or edges.
 * 
 * 
 * Furthermore, there are methods to access a single edge or to check whether
 * two nodes are connected as well as methods to access all nodes connected with
 * a specific node and thus all outgoing edges.
 * 
 * @author Arne Sieverling
 */
public class TokenGridGraph extends GridGraph {

	/**
	 * All tokens (green pixels) the tour must contain
	 */
	public ArrayList<CellNode> tokens;

	RGBColor green = new RGBColor(0, 255, 0);

	public TokenGridGraph() {
		super();
		tokens = new ArrayList<CellNode>();
	}

	/**
	 * Takes a Picture object to construct the graph from, pixel colors have the following meaning:
	 * 
	 * white: pixel is a free space
	 * green: pixel is a token/waypoint
	 * everything else: obstacle  
	 */
	public TokenGridGraph(Picture p) {

		super(p); //construct a graph containing all white pixels
		
		tokens = new ArrayList<CellNode>();

		// Add all green pixels to the graph:
		RGBColor[][] pixels = p.getImageMatrix();
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				RGBColor pixel = pixels[i][j];
				if (pixel.getGreen() == 255 && pixel.getBlue() == 0 && pixel.getRed() == 0) {
					// Add green node to graph and to tokens
					tokens.add((CellNode) this.addNode(j, i));
				}
			}
		}

		// connect all green nodes (tokens) to their neighbours if they exist
		for (CellNode n : tokens) {
			int i = n.getRow();
			int j = n.getColumn();

			// Connect every node to its 4 direct neighbors:
			CellNode neighbor;
			neighbor = getCellNode(i - 1, j);
			if (neighbor != null) {
				n.addEdge(neighbor, 1);
				neighbor.addEdge(n,1);
			}
			neighbor = getCellNode(i + 1, j);
			if (neighbor != null) {
				n.addEdge(neighbor, 1);
				neighbor.addEdge(n,1);
			}
			neighbor = getCellNode(i, j - 1);
			if (neighbor != null) {
				n.addEdge(neighbor, 1);
				neighbor.addEdge(n,1);
			}
			neighbor = getCellNode(i, j + 1);
			if (neighbor != null) {
				n.addEdge(neighbor, 1);
				neighbor.addEdge(n,1);
			}
		}

	}

	Picture toPicture(List<Node> path) {
		Picture out = super.toPicture(path);
		for (CellNode t : tokens)
			out.setPixel(t.getColumn(), t.getRow(), green);
		return out;
	}

	ArrayList<Node> computeTour(CellNode startNode) {
		
		ArrayList<Node> completePath = new ArrayList<Node>();

		Node tokenFirst = startNode;
		CellNode tokenBefore = startNode;
		Node tokenLast = startNode;
		
/*
		//Iterate over the tokens in given order
		for(CellNode token : tokens)
		{
			if(tokenBefore != null)
			{
				//Find shortest path from this to the next token
				List<Node> pathToNextToken = this.getShortestPathDijkstra(tokenBefore, token);
				
				//Now append all the nodes to the complete path - skip the First token two avoid adding double nodes
				completePath.addAll(pathToNextToken.subList(1, pathToNextToken.size()));
			} else {
				completePath.add(token);				
				tokenFirst = token;	
				
			}
			tokenBefore = token;
			tokenLast = token;
		}
*/
		/**
		 * Verwendung der Nearest-Insertion-Heuristik, weil einfach zu implementieren.
		 * Laufzeit ist länger als zuvor, Ergebnis jedoch besser -> Vorgabe wurde verbessert
		 * Quelle: https://de.wikipedia.org/wiki/Nearest-Insertion-Heuristik
		 */
		
		int min;
		CellNode next = null; //for iterating through the graph
		
		//in each step, one node is added to the tour
		for(int i = 0; i < this.tokens.size(); i++){
			//just for unexpected errors
			if(tokenBefore == null){
				throw new RuntimeException();
			}
			
			//compute shortest path to every node from current node
			this.populateBellmanFordFrom(tokenBefore);
			
			//find next nearest node
			min = Integer.MAX_VALUE;
			for(CellNode c : tokens){
				if(c.distance < min && !completePath.contains(c)){
					next = c;
					min = c.distance;
				}
			}
			
			LinkedList<Node> path = new LinkedList<Node>();
						
			// now we go backward from targetNode and insert nodes into our path
			Node reversePathNode = next;
			
			while (reversePathNode != null && reversePathNode.getID() != tokenBefore.getID()) {
				path.addFirst(reversePathNode);
				reversePathNode = reversePathNode.predecessor;
			}
			path.addFirst(reversePathNode);

			//Now append all the nodes to the complete path - skip the First token two avoid adding double nodes
			completePath.addAll(path.subList(1, path.size()));
			
			tokenLast = next;
			tokenBefore = next;
			next = null;
		}
			
		//Find shortest path back to the first token
		List<Node> pathBackToFirstToken = this.getShortestPathDijkstra(tokenLast,tokenFirst);			
		completePath.addAll(pathBackToFirstToken.subList(1, pathBackToFirstToken.size()-1));



		resetState();//clean up 		
		return completePath;
	}
}
