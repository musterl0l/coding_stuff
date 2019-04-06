import java.util.LinkedList;
import java.util.List;
import java.lang.RuntimeException;

/**
 * The class <code>Node</code> implements a node in a network.
 * 
 * @author AlgoDat team
 */
public class AtmSolution {

	public LinkedList<Integer> denominations;

    /**
	 * Initializes the banknote denominations available to the ATM
	 *
	 * @param name
	 *            the drawn value in visualization
	 **/
	public AtmSolution() {
		// initialize list of available denominations
		denominations = new LinkedList<Integer>();
		//Add denominations in a sorted order, highest value first:
		denominations.add(200);
		denominations.add(100);
		denominations.add(50);
		denominations.add(20);
		denominations.add(10);
		denominations.add(5);
	}

	/**
	 * Computes the the number of banknotes for each denomination
	 * 
	 * @param total
	 *            Amount of money requested
	 *            End point of this edge.
	 * @return List<int> 
	 *            Amount of banknotes for each denomination, 
	 *            as a list in the same order as the list denominations
	 *            Example: [0,1,0,0,0,0]: one 100EUR banknote
	 */
	public LinkedList<Integer> getDivision(int total) {
		LinkedList<Integer> division = new LinkedList<Integer>();
		int remaining = total;
		for ( Integer denomination : denominations ) { //greedy #1: go through highest denominations first
			int count = 0; //keep track of how many banknotes of the current denomination we have disbursed
			while (remaining  >= denomination) { //greedy #2: try to use as many banknotes as possible
			    count++;
			    remaining -= denomination;
			}
			division.add(count);    
		}
		
		if (remaining != 0) { //assert that we could disburse the exact amount
			throw new RuntimeException("I cannot divide the requested total!");
		}
		return division;
	}
}

