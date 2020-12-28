import javax.sound.midi.Synthesizer;
import java.awt.*;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode min;
	private HeapNode first;
	private int size;
	private int marked;

	public FibonacciHeap () {
		this.min = null;
		this.first = null;
		this.size = 0;
		this.marked = 0;
	}
	
	public HeapNode getMin () {
		return this.min;
	}
	
	public HeapNode getFirst () {
		return this.first;
	}
	
	public void setMin (HeapNode m) {
		this.min = m;
	}
	
	public void setFirst (HeapNode f) {
		this.first = f;
	}
	
	public void setSize (int s) {
		this.size = s;
	}
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    *
    * The method returns true if and only if the heap
    * is empty.
    *
    */
    public boolean isEmpty()
    {
    	return (this.size == 0);
    }

   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    *
    * Returns the new node created.
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode (key);
    	if (this.isEmpty()) {
    		this.first = newNode;
    		this.min = newNode;
    	}
    	else {
        	HeapNode first = this.first; //not null because heap not empty
        	this.first = newNode;
        	newNode.setNext(first);
        	newNode.setPrev(first.getPrev()); //could be null
        	first.getPrev().setNext(newNode);
        	first.setPrev(newNode);
        	if (key < this.min.getKey())
        		this.min = newNode;
    	}
    	
    	this.size++;
    	
    	return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	return; // should be replaced by student code

    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal.
    *
    */
    public HeapNode findMin()
    {
    	return new HeapNode(0);// should be replaced by student code
    }

   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  if (heap2.isEmpty())
    		  return;
    	  else if (this.isEmpty()) {
    		  this.first = heap2.getFirst();
    		  this.min = heap2.getMin();
    		  this.size = heap2.size();
    	  }
    	  else { //both heaps are non-empty
    		  this.size += heap2.size();
    		  if (heap2.getMin().getKey() < this.min.getKey())
    			  this.min = heap2.getMin();
    		  HeapNode last = this.first.getPrev(); //could be 'first' if only one node in heap
    		  last.setNext(heap2.getFirst());
    		  heap2.getFirst().setPrev(last);
    		  
    	  }
    	  
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *
    */
    public int size()
    {
    	return this.size;
    }

    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
    *
    */
    public int[] countersRep()
    {
	int[] arr = new int[42];
        return arr; //	 to be replaced by student code
    }

   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
    *
    */
    public void delete(HeapNode x)
    {
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	return; // should be replaced by student code
    }

   /**
    * public int potential()
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
    */
    public int potential() 
    {    
    	return (this.size + (2*this.marked));
    }

   /**
    * public static int totalLinks()
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
    * in its root.
    */
    public static int totalLinks()
    {
    	return 0; // should be replaced by student code
    }

   /**
    * public static int totalCuts()
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
    */
    public static int totalCuts()
    {
    	return 0; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k)
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)).
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] arr = new int[42];
        return arr; // should be replaced by student code
    }

    /**
     * Cut x from its parent y
     */
    public void cut(HeapNode x, HeapNode y) {
        // remove parent and clean key
        x.setParent(null);
        x.unMark();
        // reduce y rank
        y.decreaseRank();
        if (x.getNext() == x) { // node doesn't have siblings
            y.setChild(null);
        } else {
            y.setChild(x.getNext());
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
        }

        // Adding this subtree at the end of the list
        // TODO: Might be better to just meld when we write it, but need to make sure size etc. remains same
        this.first.getPrev().setNext(x); // put list as next of last element
        x.setPrev(this.first.getPrev()); // setting x prev to be the former last
        x.setNext(this.first); // setting x next to be the first element
        this.first.setPrev(x); // setting the prev element of the first to be x

        // Checking if x should be minimum pointer
        // TODO: Should also consider it in meld
        if (x.getKey() < this.min.getKey()) {
            this.min = x;
        }
    }

    /**
     * Perform a cascading-cut process starting at x
     */
    public void cascadingCut(HeapNode x, HeapNode y){
        cut(x,y);
        if (y.getParent()!=null){
            if (!y.isMarked()) {
                y.setMark(true);
            } else {
                cascadingCut(y, y.getParent());
            }
        }
    }

   /**
    * public class HeapNode
    *
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in
    * another file
    *
    */
   public class HeapNode {

    	private int key;
    	private int rank; //number of children
    	private boolean mark;
    	private HeapNode child;
    	private HeapNode next;
    	private HeapNode prev;
    	private HeapNode parent;

	  	public HeapNode(int key) {
		    this.key = key;
		    this.rank = 0;
		    this.mark = false;
		    this.child = null;
		    this.next = this;
		    this.prev = this;
		    this.parent = null;
		    
	      }
	
	  	public int getKey() {
		    return this.key;
	      }
	  	
	  	public int getRank() {
	  		return this.rank;
	  	}
	  	
	  	public boolean isMarked () {
	  		return this.mark;
	  	}
	  	
	  	public HeapNode getChild () {
	  		return this.child;
	  	}
	  	
		public HeapNode getNext () {
	  		return this.next;
	  	}
		
		public HeapNode getPrev () {
	  		return this.prev;
	  	}

		public HeapNode getParent () {
	  		return this.parent;
	  	}
		
		public void setKey (int key) {
			this.key = key;
		}
		
		public void setMark (boolean b) {
			this.mark = b;
		}
		
		public void setRank (int r) {
			this.rank = r;
		}
		
		public void setChild (HeapNode node) {
			this.child = node;
		}
		
		public void setNext (HeapNode node) {
			this.next = node;
		}
		
		public void setPrev (HeapNode node) {
			this.prev = node;
		}
		
		public void setParent (HeapNode node) {
			this.parent = node;
		}
		/**
	     * Unmark a node
	     */
	    public void unMark(){
	          this.mark = false;
	       }


	    public void decreaseRank() {
	           this.rank--;
	       }

    }

   
}
