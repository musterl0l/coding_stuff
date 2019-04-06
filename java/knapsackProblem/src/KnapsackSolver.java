import java.util.LinkedList;
import java.util.PriorityQueue;


/**
 * 
 * @author AlgoDat Team
 *
 */
public class KnapsackSolver {
	
	/**constructor for my jUnit tests
	 * 
	 */
//	public KnapsackSolver(){	
//	}
	
	/**
	 * Tries all possible item combination to solve the knapsack problem. Returns the optimal solution.
	 * @param k Empty knapsack with a maximum capacity to fill
	 * @param items a list of items each with a weight and a value
	 * @return the filled knapsack
	 */
	public Knapsack solveKnapsackOptimal(Knapsack k, LinkedList<Item> items){
		Knapsack kNew;
		Knapsack a, b;
		
		if(items.isEmpty()){
			//end of recursion
			return k;
		}
		
		//knapsack where a item will be added
		Knapsack kFill = new Knapsack(k.maxWeight);
		//add the same items to the buffer knapsack
		for (Item x : k.getItemsInKnapsack()){
			kFill.addItem(x);
		}
		
		//item lists for the next recursive step
		LinkedList<Item> listSafer = new LinkedList<Item>();
		LinkedList<Item> listSafer2 = new LinkedList<Item>();

		Item i = items.poll();	//get the next item
		
		//safe the item list
		for(Item x : items){
			listSafer.addLast(x);
			listSafer2.addLast(x);	
		}
		
		
		if(!kFill.addItem(i)){
			kNew = solveKnapsackOptimal(k,listSafer); //no space for  this item 
		}
		else{
			
			a = solveKnapsackOptimal(k, listSafer); //dont add item
			b = solveKnapsackOptimal(kFill, listSafer2); //item added
			
			//take the maximum
			if(a.currentValue > b.currentValue){
				kNew = a;
			}else{
				kNew = b;
			}
		}
	
		return kNew;

		
//		//ranked array
//		int[] s = new int[items.size()];
//		
//		for (Item i : items){
//			s[i.getIndex()] = i.getValue() / i.getWeight(); 
//		}
		
		
		
		
	}

	/**
	 * Uses the trivial greedy algorithm to solve the Knapsack problem. 
	 * @param k Empty knapsack with a maximum capacity to fill
	 * @param items a list of items each with a weight and a value
	 * @return the filled knapsack
	 */
	public Knapsack solveKnapsackGreedyStupid(Knapsack k, LinkedList<Item> items){
		
		//while loop unital the item list is empty
		while(!items.isEmpty()){
			//find maximum element
			int in = 0;		//index counter of the items list
			int index = 0;	//remembers the index of the maximum value item
			int valMax = 0;	//maximum value of items in the items list
			Item it = null;	//remember the item with maxValue
			
			//search the maximum value item in items list
			for (Item i : items){
				if (i.getValue() > valMax){
					valMax = i.getValue();
					it = i;
					index = in;
				}
				in ++;
			}
		
			if (it != null){
				//add the item to the list
				k.addItem(it);
			}	
			//remove item from the items list
			items.remove(index);
		}
		return k;
	}
	
	
	/**
	 * Uses a smarter greedy algorithm to solve the Knapsack problem. 
	 * @param k Empty knapsack with a maximum capacity to fill
	 * @param items a list of items each with a weight and a value
	 * @return the filled knapsack
	 */
	public Knapsack solveKnapsackGreedySmart(Knapsack k, LinkedList<Item> items){
	
		while(!items.isEmpty()){
			//find maximum element
			int in = 0;		//index counter of the items list
			int index = 0;	//remembers the index of the maximum value item
			int valPerW = 0;//maximum value/weight of items in the items list
			Item it = null;
			
			//search the maximum value/weight item in items list
			for (Item i : items){
				if (i.getValue()/i.getWeight() > valPerW){
					valPerW = i.getValue()/i.getWeight();
					it = i;
					index = in;
				}
				in ++;
			}
		
			if (it != null){
				//add the item to the list
				k.addItem(it);
				
			}
			//remove the item form items list
			items.remove(index);
		}
		return k;
	}
	
	//END
}



