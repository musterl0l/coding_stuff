import java.util.ArrayList;
import java.util.LinkedList;

public class Knapsack {
	ArrayList<Item> items;
	int maxWeight, currentWeight, currentValue;

	public Knapsack(int maxWeight){
		this.maxWeight = maxWeight;
		this.currentValue = 0;
		this.currentWeight = 0;
		items = new ArrayList<Item>();
	}
	/**
	 *
	 * @param item Item to add
	 * @return true if the item was successfully added (the max. weight is not exceeded). False if the item could not be added.
	 */
	public boolean addItem(Item item){
		if (maxWeight >= (currentWeight + item.getWeight())){
			this.items.add(item);
			this.currentWeight += item.getWeight();
			this.currentValue += item.getValue();
			System.out.println("Added item " + item.getIndex() + " with value " + item.getValue() + " and weight " + item.getWeight() +". Current knapsack weight is " + this.getCurrentWeight());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes an item from the knapsack
	 * @param item
	 * @return true if the item was removed. false if not (the item was not in the knapsack already)
	 */
	public boolean removeItem(Item item){
		boolean succ = items.remove(item);
		if (succ){
			currentWeight -= item.getWeight();
			currentValue -= item.getValue();
		}
		return succ;
	}

	/**
	 *
	 * @return an ArrayList with all the item objects currently in the knapsack
	 */
	public ArrayList<Item> getItemsInKnapsack(){
		return this.items;
	}

	/**
	 *
	 * @return current weight of all items in the knapsack
	 */
	public int getCurrentWeight(){
		return this.currentWeight;
	}

	/**
	 *
	 * @return Maximum allowed weight in the knapsack
	 */
	public int getMaximumWeight(){
		return this.maxWeight;
	}
}

class Item{
	private int weight, value, index;

	public Item(int index, int weight, int value){
		this.weight = weight;
		this.value = value;
		this.index = index;
	}
	public int getWeight(){
		return this.weight;
	}
	public int getValue(){
		return this.value;
	}

	public int getIndex(){
		return this.index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + value;
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (index != other.index)
			return false;
		if (value != other.value)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
	
}

