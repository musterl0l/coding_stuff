import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class implementing a Union-Find-data structure with representatives.
 * 
 * @author AlgoDat-Team
 */
public class UnionFindSet<T>{

	//You can use this map to store the representant for each element of the Union find structure
    private HashMap<T,LinkedList<T>> element2representative;

	public UnionFindSet() {
		element2representative = new HashMap<>();
	}

	/**
	 * Takes a collection of n elements and adds 
	 * n disjoint partitions each with one element in it.
	 * 
	 * @param set
	 *            The set to be partitioned.
	 */
	public void add(List<T> elements) {
		// TODO Homework 2.1
		for (T e : elements){
				LinkedList<T> set = new LinkedList<T>();
				set.add(e);
				element2representative.put(e, set);
				
	// oder: this.add(e);
		}
	}

	/**
	 * Creates one disjoint partition with the element in it 
	 * and adds it to the Union-Find data structure
	 * 
	 * @param element
	 *            The element to put in the partition.
	 */
	public void add(T element) {
		// TODO Homework 2.1
			LinkedList<T> set = new LinkedList<T>();
			set.add(element);
			element2representative.put(element, set);
		
	}

	/**
	 * Retrieves the partition which contains the wanted element.
	 * 
	 * A partition is identified by a single, representative element.
	 * This function returns the representative of the partition
	 * that contains x. 
	 * 
	 * @param x
	 *            The element whose partition we want to know
	 * @return 
	 *            The representative element of the partition
	 */
	public T getRepresentative(T x) {
		// TODO Homework 2.1
		T r = null; //representative for returning
		
		//show all linked lists in the hashmap
		Collection<LinkedList<T>> part = element2representative.values();
		//go through the linked lists and look for the element x
		for (LinkedList<T> p : part){
			if(p.contains(x)){
				r = p.peekFirst();
//				System.out.println("found rep: "+r);
				return r;
			}
		}
		
		return r;
		
	}

	/**
	 * Joins two partitions. First it retrieves the partitions containing the
	 * given elements. Removes one of the joined partitions from
	 * <code>partitions</code>.
	 * 
	 * @param x
	 *            One element whose partition is to be joined.
	 * @param y
	 *            The other element whose partition is to be joined.
	 */
	public void union(T x, T y) {
		// TODO Homework 2.1
		T repX = this.getRepresentative(x);
		T repY = this.getRepresentative(y);
		
		//check if the represants are not the same
		if (repX != repY){
			LinkedList<T> listX = element2representative.get(repX);
			LinkedList<T> listY = element2representative.get(repY);
			
			for (T e : listY){
				listX.add(e);
			}
			element2representative.remove(repY);
		}
	}
}

