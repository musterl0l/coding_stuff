import java.util.Comparator;

/**
 * This class implements a heuristic based on the "Euclidean" distance metric (L2 Norm)
 * 
 **/
 public class HeuristicEuclidean implements Comparator<CellNode> {
	
	    /*
	    This is the goal node the heuristic uses to estimate the remaining distance
	    */
		CellNode goal;
		
		public HeuristicEuclidean(CellNode goal) {
			this.goal = goal;
		}
		
		/**
		 * Computes an estimate of the remaining distance from node n to the goal node and
		 * updates the node attribute distanceRemainingEstimate
		 * 
		 *  This class implements the L2 norm (euclidean distance)
		 *  as the permissible heuristic
		 *  
		 * @param n
		 * 		node to estimate the remaining distance from
		 */
		public void estimateDistanceToGoal(CellNode n) {
        	//Your implementation here
			
			//Setting this to 0.0 effectively turns A* into Dijkstra
			//but this is the formula for the euclidean heuristic
			n.distanceToGoalEstimate = Math.sqrt(Math.pow((n.i - goal.i), 2) + Math.pow((n.j - goal.j), 2)); 
			
			
        	//Hint: you only need to compute the estimate once for each node.
        	 
        	return;
		}
		
		
		
		/*
		 * compares two nodes based on an estimate of the path length to goal
		 * Computes the function cost(n) = d(start, n)+h(n, goal), 
		 * where d(start, n) is the distance from the start to n \
		 * and h(n, goal) is an estimate of the distance from n to the goal based on the Euclidean distance.
		 *  
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */		
        public int compare(CellNode n1, CellNode n2) {
        	//Your implementation here

        	int c1, c2;
        	int cMin = 0;
        	
        	//calculate heuristics
        	this.estimateDistanceToGoal(n1);
        	this.estimateDistanceToGoal(n2);
        	
        	//calculate the costs of the two Nodes
        	c1 = n1.distance + (int) n1.distanceToGoalEstimate;
        	c2 = n2.distance + (int) n2.distanceToGoalEstimate;
        	
        	//set cMin to the smaller costs
        	if(c1 < c2){
        		cMin = -1;
        	}else if (c1 > c2){
        		cMin = 1;
        	}
        	return cMin;
        	
        }	
}

