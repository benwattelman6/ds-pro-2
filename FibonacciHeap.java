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
        return this.first !=null;
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
    	return new HeapNode(key); // should be replaced by student code
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
    	  return; // should be replaced by student code
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *
    */
    public int size()
    {
    	return 0; // should be replaced by student code
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
    	return 0; // should be replaced by student code
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
       private int rank;
       private boolean mark;
       private HeapNode child;
       private HeapNode next;
       private HeapNode prev;

       private HeapNode parent;


       public HeapNode(int key) {
           this.key = key;
           this.mark = false;
       }

       public int getKey() {
           return this.key;
       }

       public void setMark(boolean m) {
           this.mark = m;
       }

       /**
        * Unmark a node
        */
       public void unMark(){
           this.mark = false;
       }

       /**
        * Returns mark
        */
       public boolean isMarked() {
           return this.mark;
       }

       public HeapNode getParent() {
           return parent;
       }

       public void setParent(HeapNode p) {
           this.parent = p;
       }

       public void decreaseRank() {
           this.rank--;
       }

       public HeapNode getNext() {
           return this.next;
       }

       public HeapNode getPrev() {
           return this.prev;
       }

       public void setNext(HeapNode next) {
           this.next = next;
       }

       public void setPrev(HeapNode prev) {
           this.prev = prev;
       }

       public void setChild(HeapNode c) {
           this.child = c;
       }


   }
}
