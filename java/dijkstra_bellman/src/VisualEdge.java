/**
 * An edge object. Contains the pointer to the adjacent nodes and
 * the weight.
 * 
 * @author Joerg Schneider <komm@cs.tu-berlin.de>
 */
public class VisualEdge{
	VisualNode start;
	VisualNode target;
	double weight = 0;

	/**
	 * Creates an edge.
	 */
	VisualEdge(VisualNode start, VisualNode target, double weight) {
		this.start = start;
		this.target = target;
		this.weight = weight;
	}
	
}

