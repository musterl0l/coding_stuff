import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

public class KnapsackTest {
	
	Knapsack k = new Knapsack(50);
	LinkedList<Item> waren = new LinkedList<Item>();
	
	KnapsackSolver kso = new KnapsackSolver();
	
	@Test
	public void testKnapsackSolverOptimal() {
		
		Item w1 = new Item(1, 10, 60);
		Item w2 = new Item(2, 20, 100);	
		Item w3 = new Item(3, 30, 120);
		Item w4 = new Item(4, 10, 80);

		waren.add(w1);
		waren.add(w2);
		waren.add(w3);
		waren.add(w4);

		Knapsack kOpt = kso.solveKnapsackOptimal(k, waren);
		
		assertEquals("expects a optimal knapsack",260 , kOpt.currentValue);
		
	}
	
	@Test
	public void testKnapsackSolverGreedyStupid() {
		
		Item w1 = new Item(1, 10, 60);
		Item w2 = new Item(2, 20, 100);	
		Item w3 = new Item(3, 30, 120);
		Item w4 = new Item(4, 10, 80);
		
		
		waren.add(w1);
		waren.add(w2);
		waren.add(w3);
		waren.add(w4);
	
		Knapsack kOpt = kso.solveKnapsackGreedyStupid(k, waren);
		
		assertEquals("expects a optimal knapsack",220 , kOpt.currentValue);
		
	}
	@Test
	public void testKnapsackSolverGreedySmart() {
		
		Item w1 = new Item(1, 10, 60);
		Item w2 = new Item(2, 20, 100);	
		Item w3 = new Item(3, 30, 120);
		Item w4 = new Item(4, 10, 80);
		
		
		waren.add(w1);
		waren.add(w2);
		waren.add(w3);
		waren.add(w4);
		
		Knapsack kOpt = kso.solveKnapsackGreedySmart(k, waren);
		
		assertEquals("expects a optimal knapsack",240 , kOpt.currentValue);
		
	}

}
