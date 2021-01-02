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
	private static int links;
	private static int cuts;
	
	public static final double goldenRatio = (Math.sqrt(5.0)+1)/2; 
	
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
    	HeapNode x = this.min;
    	//attach x's children to linked list of roots
    	x.getPrev().setNext(x.getChild());
    	x.getChild().getPrev().setNext(x.getNext());
    	x.getChild().setParent(null);
    	if (this.first == x)
    		this.first = x.getChild();
    	
    	this.size-- ;

    	this.min = consolidate(this.first);
    	
    }
    /*
     * links x to y (x being the parent) and returns x
     */
    public HeapNode link (HeapNode x, HeapNode y) {
    	if (x == null && y == null)
    		return null;
    	else if (y == null)
    		return x;
    	else if (x == null)
    		return y;
    
    	if (x.getKey() > y.getKey()) { //x must have the smaller key to remain the root
    		HeapNode temp = x;
    		x = y;
    		y = temp;
    	}
    	
    	HeapNode xc = x.getChild();
    	if (xc != null) {
	    	y.setNext(xc);
	    	y.setPrev(xc.getPrev());
	    	xc.getPrev().setNext(y);
	    	xc.setPrev(y);
    	}
    	y.setParent(x);    	
    	x.setChild(y);
    	x.setRank(x.getRank()+1);
    	x.setNext(x);
    	x.setPrev(x);
    	
    	links++; //static field
    	
    	return x;
    }
    
    public HeapNode consolidate (HeapNode x) {
    	HeapNode[] fullBuckets = toBuckets(x);
    	
    	HeapNode node = null;
    	for (int i=0; i<fullBuckets.length; i++) {
    		if (fullBuckets[i] != null) { //there's a Binomial tree with rank i
    			if (node == null) { //reached the first Binomial tree in list
    				node = fullBuckets[i];
    				node.setNext(node);
    				node.setPrev(node);
    				this.first = node;
    			}
    			else { //not the first Binomial tree in list
    				insertAfter(node,fullBuckets[i]);
    				//make sure node holds the new minimum
    				if (fullBuckets[i].getKey()<node.getKey())
    					node = fullBuckets[i];
    				
    			}
    		}
    	}
    	
    	return node;
    }
    
    /*
     * helper function - insert root y as root x's 'next'
     * only called from 'consolidate'
     * y is the now the last root of the list
     */
    public void insertAfter (HeapNode x , HeapNode y) {
    	x.getNext().setPrev(y);
    	y.setNext(x.getNext());
    	x.setNext(y);
    	y.setPrev(x);
    }
    
    
    public HeapNode[] toBuckets(HeapNode x) {
    	//initialize array - all cells are null
    	HeapNode[] buckets = new HeapNode[CalcMaxRank()];
    	
    	x.getPrev().setNext(null);
    	HeapNode y;
    	while (x != null) {
    		y = x;
    		x = x.getNext();
    		while(buckets[y.getRank()] != null) {
    			y = link(y , buckets[y.getRank()]);
    			buckets[y.getRank() - 1] = null;
    		}
    		buckets[y.getRank()] = y;
    	}
    	
    	return buckets;
    }
    
    
    /*
     * helper function - calculate log of num with base 2
     */
    public static int log2 (int num) {
    	int res = (int)(Math.log(num) / Math.log(2.0));
    	return res;
    }
    
    /*
     * helper functions - creates a HeapNode array sized ~ log_golden ratio(n) 
     */
    public  int CalcMaxRank () {
    	int n = this.size;
    	int len = (int) Math.ceil(log2(n)*1.4404);
    	return len;
    	}

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
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
    	int[] arr = new int[CalcMaxRank()]; //all cells are 0
    	
    	HeapNode x = this.first;
    	if (x != null ) {
        	x.getPrev().setNext(null); //need to fix this at the end
        	int rank;
        	
        	while (x != null) {
        		rank = x.getRank();
        		arr[rank]++;
        		x = x.getNext();
        	}
        	//fix what we changed at the beginning
        	this.first.getPrev().setNext(this.first);
    	}

        return arr;
    }

   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
    *
    */
    public void delete(HeapNode x)
    {
    	decreaseKey(x,Integer.MAX_VALUE);
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	x.setKey(x.getKey() - delta);
    	if (x.getParent() != null) {
    		HeapNode parent = x.getParent();
    		if (x.getKey() > parent.getKey()) //everything OK
    			return;
    		else { //x.getKey() < parent.getKey()
    			cascadingCut(x, parent);
    		}
    	}
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
    	return links;
    }

   /**
    * public static int totalCuts()
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
    */
    public static int totalCuts()
    {
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k)
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)).
    * You are not allowed to change H.
    */
    //TODO
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
        this.marked--;
        // reduce y rank
        y.decreaseRank();
        if (x.getNext() == x) { // node doesn't have siblings
            y.setChild(null);
        } else {
            y.setChild(x.getNext());
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
        }
        
        
        // Adding this subtree at the start of the list
        // TODO: Might be better to just meld when we write it, but need to make sure size etc. remains same
       HeapNode first = this.first;
       HeapNode last = this.first.getPrev();
       this.first = x; //set x as the first tree in heap
       last.setNext(x); //set last item's 'next' to x
       first.setPrev(x); //set previous first item's 'prev' to x
       x.setNext(first); //set x's 'next' to previous first
       x.setPrev(last); //set x's 'prev' to last item

        // Checking if x should be minimum pointer
        // TODO: Should also consider it in meld
        if (x.getKey() < this.min.getKey()) {
            this.min = x;
        }
        
        cuts++; //static field, so no use of 'this'
    }

    /**
     * Perform a cascading-cut process starting at x
     */
    public void cascadingCut(HeapNode x, HeapNode y){
        cut(x,y);
        if (y.getParent()!=null){
            if (!y.isMarked()) {
                y.setMark(true);
                this.marked++;
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
