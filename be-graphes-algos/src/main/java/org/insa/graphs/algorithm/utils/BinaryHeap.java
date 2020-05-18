package org.insa.graphs.algorithm.utils;
import java.util.ArrayList;

/**
 * Implements a binary heap containing elements of type E.
 *
 * Note that all comparisons are based on the compareTo method, hence E must
 * implement Comparable
 * 
 * @author Mark Allen Weiss
 * @author DLB
 */
public class BinaryHeap<E extends Comparable<E>> implements PriorityQueue<E> {

    // Number of elements in heap.
    private int currentSize;

    // The heap array.
    protected final ArrayList<E> array;

    /**
     * Construct a new empty binary heap.
     */
    public BinaryHeap() {
        this.currentSize = 0;
        this.array = new ArrayList<E>();
    }

    /**
     * Construct a copy of the given heap.
     * 
     * @param heap Binary heap to copy.
     */
    public BinaryHeap(BinaryHeap<E> heap) {
        this.currentSize = heap.currentSize;
        this.array = new ArrayList<E>(heap.array);
    }
    /**
     * 
     * @return current size of the heap
     */
    public int getCurrentSize(){
        return this.currentSize;
    }
    /**
     * Set an element at the given index.
     * 
     * @param index Index at which the element should be set.
     * @param value Element to set.
     */
    private void arraySet(int index, E value) {
        if (index == this.array.size()) {
            this.array.add(value);
        }
        else {
            this.array.set(index, value);
        }
    }

    /**
     * @return Index of the parent of the given index.
     */
    protected int indexParent(int index) {
        return (index - 1) / 2;
    }

    /**
     * @return Index of the left child of the given index.
     */
    protected int indexLeft(int index) {
        return index * 2 + 1;
    }

    /**
     * Internal method to percolate up in the heap.
     * 
     * @param index Index at which the percolate begins.
     */
    private void percolateUp(int index) {
        E x = this.array.get(index);

        for (; index > 0
                && x.compareTo(this.array.get(indexParent(index))) < 0; index = indexParent(
                        index)) {
            E moving_val = this.array.get(indexParent(index));
            this.arraySet(index, moving_val);
        }

        this.arraySet(index, x);
    }

    /**
     * Internal method to percolate down in the heap.
     * 
     * @param index Index at which the percolate begins.
     */
    private void percolateDown(int index) {
        int ileft = indexLeft(index);
        int iright = ileft + 1;

        if (ileft < this.currentSize) {
            E current = this.array.get(index);
            E left = this.array.get(ileft);
            boolean hasRight = iright < this.currentSize;
            E right = (hasRight) ? this.array.get(iright) : null;

            if (!hasRight || left.compareTo(right) < 0) {
                // Left is smaller
                if (left.compareTo(current) < 0) {
                    this.arraySet(index, left);
                    this.arraySet(ileft, current);
                    this.percolateDown(ileft);
                }
            }
            else {
                // Right is smaller
                if (right.compareTo(current) < 0) {
                    this.arraySet(index, right);
                    this.arraySet(iright, current);
                    this.percolateDown(iright);
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.currentSize == 0;
    }

    @Override
    public int size() {
        return this.currentSize;
    }

    @Override
    public void insert(E x) {
        int index = this.currentSize++;
        this.arraySet(index, x);
        this.percolateUp(index);
    }

    @Override
    public void remove(E x) throws ElementNotFoundException {
        if(this.isEmpty()==true) {//Le tas est-il vide ?
        	throw new ElementNotFoundException(x);
        }
        int index_elem = 0;
        while (index_elem < this.currentSize && this.array.get(index_elem).equals(x)==false) {
        	index_elem ++;
        }
        if(index_elem >= this.currentSize) {//x est-il dans le tas ?
        	throw new ElementNotFoundException(x);
        }
        E fin = this.array.get(this.currentSize-1);
        if(fin.equals(x) == true) {//Si il n'y a que cet élément
        	this.currentSize--;
        	return;
        }
        else {
        	this.arraySet(index_elem	, fin);
        	this.currentSize--;
			this.percolateDown(index_elem);
			this.percolateUp(index_elem);
        }
        
    }

    @Override
    public E findMin() throws EmptyPriorityQueueException {
        if (isEmpty())/*1124 -> 1240 bug parfois*/
            throw new EmptyPriorityQueueException();
        return this.array.get(0);
    }

    @Override
    public E deleteMin() throws EmptyPriorityQueueException {
        E minItem = findMin();
        E lastItem = this.array.get(--this.currentSize);
        this.arraySet(0, lastItem);
        this.percolateDown(0);
        return minItem;
    }

    /**
     * Creates a multi-lines string representing a sorted view of this binary heap.
     * 
     * @return a string containing a sorted view this binary heap.
     */
    public String toStringSorted() {
        return BinaryHeapFormatter.toStringSorted(this, -1);
    }

    /**
     * Creates a multi-lines string representing a sorted view of this binary heap.
     * 
     * @param maxElement Maximum number of elements to display. or {@code -1} to
     *                   display all the elements.
     * 
     * @return a string containing a sorted view this binary heap.
     */
    public String toStringSorted(int maxElement) {
        return BinaryHeapFormatter.toStringSorted(this, maxElement);
    }

    /**
     * Creates a multi-lines string representing a tree view of this binary heap.
     * 
     * @return a string containing a tree view of this binary heap.
     */
    public String toStringTree() {
        return BinaryHeapFormatter.toStringTree(this, Integer.MAX_VALUE);
    }

    /**
     * Creates a multi-lines string representing a tree view of this binary heap.
     * 
     * @param maxDepth Maximum depth of the tree to display.
     * 
     * @return a string containing a tree view of this binary heap.
     */
    public String toStringTree(int maxDepth) {
        return BinaryHeapFormatter.toStringTree(this, maxDepth);
    }

    @Override
    public String toString() {
        return BinaryHeapFormatter.toStringTree(this, 8);
    }
    public boolean isValid() {
    	boolean cbon = true;
    	int i = 0;
    	while((i<this.currentSize)&&(cbon!=false)) {
    		i++;
    		cbon = cbon && this.verification_element(i);
        	if(cbon == false) {
        		System.out.println("Tas non valide");
        		return false;
        	}
		}
    	// System.out.println("Tas valide");
    	return true;
    }
    private boolean verification_element(int index) {
    	int index_fils_gauche = this.indexLeft(index);
    	if(index_fils_gauche<this.currentSize) {
    		if(this.array.get(index_fils_gauche).compareTo(this.array.get(index))<0) {
    			return false;
    		}
    	}
    	int index_fils_droit = index_fils_gauche+1;
    	if(index_fils_droit<this.currentSize) {
    		if(this.array.get(index_fils_droit).compareTo(this.array.get(index))<0) {
    			return false;
    		}
    	}
    	return true;
    }

}
