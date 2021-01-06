import java.util.Iterator;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */

public class FibonacciHeap {
    private HeapNode min;
    private HeapNode first;
    private int size;
    private int marked; // CHANGE ONLY FROM NODE MARK FUNCTIONS!
    private static int links;
    private static int cuts;

    public static final double goldenRatio = (Math.sqrt(5.0) + 1) / 2;

    /**
     * public FibonacciHeap()
     * Fibonacci Heap constructor
     * O(1)
     */
    public FibonacciHeap() {
        this.min = null;
        this.first = null;
        this.size = 0;
        this.marked = 0;
    }

    /**
     * public HeapNode getMin()
     * Returns the minimum root of the heap.
     * Complexity: O(1)
     */
    public HeapNode getMin() {
        return this.min;
    }
    /**
     * public HeapNode getFirst()
     * Returns the first root of the heap.
     * Complexity: O(1)
     */
    public HeapNode getFirst() {
        return this.first;
    }
    /**
     * public void setMin(HeapNode m)
     * Sets the pointer to minimum root of the heap.
     * Complexity: O(1)
     */
    public void setMin(HeapNode m) {
        this.min = m;
    }
    /**
     * public void setFirst(HeapNode f)
     * Sets the pointer to first root of the heap.
     * Complexity: O(1)
     */
    public void setFirst(HeapNode f) {
        this.first = f;
    }
    /**
     * public void setSize(int s)
     * Sets the size of the heap.
     * Complexity: O(1)
     */
    public void setSize(int s) {
        this.size = s;
    }

    /**
     * public boolean isEmpty()
     * precondition: none
     * The method returns true if and only if the heap is empty.
     * Complexity: O(1)
     */
    public boolean isEmpty() {
        return (this.size == 0);
    }

    /**
     * public HeapNode insert(int key)
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap - as the first node.
     * Updates relevant pointers and min if necessary.
     * Returns the new node created.
     * Complexity: O(1)
     */
    public HeapNode insert(int key) {
        HeapNode newNode = new HeapNode(key);
        if (this.isEmpty()) {
            this.first = newNode;
            this.min = newNode;
        } else {
            HeapNode first = this.first; //not null because heap not empty
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
     * Delete the node containing the minimum key.
     * deleteMin triggers consolidation using "buckets".
     * Complexity: amortized O(logn), worst case O(n)
     */
    public void deleteMin() {
        if (isEmpty()) return; // can't delete from empty list
        this.size--; // decrease the number of entries
        HeapNode x = this.min; // grabbing the minimum element for later!
        /*
        Two delete cases:
            1. The min is the only root element. In this case, we shall make the child the first element and by that we make all the children roots.
            2. There are more root elements. in this case, we shall put the child in the roots list
        In any case we will set randomly child
        If we are deleting the first element, it would be taken care of during consolidation
        */
        if (x.next == x) { // 1st case
            this.min = null;
        } else { // 2nd case
            this.min.prev.next = this.min.next;
            this.min.next.prev = this.min.prev;
            this.min = min.next;
        }

        if (x.child != null) {
            for (HeapNode n : x.child) { //using iterator
                n.parent = null;// remove parent pointer, next prev pointers are still good
            }
        }

        // now we want to merge the children (if any) to the existing list;
        // if x was the only root, min is null. if no children, x.child is null
        HeapNode starter = mergeNodes(x.child, this.min);
        if (starter == null) return; // no elements so we are done.
        // otherwise, we have to consolidate
        this.min = consolidate(starter);
    }

    /**
     * private static HeapNode mergeNodes(HeapNode o1, HeapNode o2)
     * Merges two  nodes into one, with no more side effects.
     * Returns a pointer to the smaller node between o1 and o2 (which isn't necessarily the smallest in list)
     * Complexity: O(1)
     */
    private static HeapNode mergeNodes(HeapNode o1, HeapNode o2) {
        if (o1 == null && o2 == null) return null;
        if (o1 == null) return o2; // then o2 is not null
        if (o2 == null) return o1; // then o1 is not null
        // then both o1,o2 not null
        HeapNode tmp = o1.next;
        o1.next = o2.next;
        o1.next.prev = o1;
        o2.next = tmp;
        o2.next.prev = o2;
        return o1.getKey() < o2.getKey() ? o1 : o2;

    }

    /**
     * public HeapNode link(HeapNode x, HeapNode y)
     * links x to y (x being the parent) and returns x
     * Complexity: O(1)
     */
    public HeapNode link(HeapNode x, HeapNode y) {
        if (x == null && y == null)
            return null;
        else if (y == null)
            return x;
        else if (x == null)
            return y;

        HeapNode newParent, newChild, child;
        // node with smaller key is the parent
        if (x.getKey() > y.getKey()) {
            newParent = y;
            newChild = x;
        } else {
            newParent = x;
            newChild = y;
        }

        /*
        If there are children to newParent, our new child is the new sibling
        So need to route it properly.
         */
        child = newParent.child;
        if (child != null) {
            newChild.next = child;
            newChild.prev = child.prev;
            child.prev.next = newChild;
            child.prev = newChild;
        }
        newChild.parent = newParent;
        newParent.child = newChild; // also covers the case when no children at first
        links++; // static field
        newParent.rank++;
        return newParent;
    }

    /**
     * public HeapNode consolidate(HeapNode x)
     * Consolidate tree using "buckets", starting from x
     * @return the new minimum
     * Complexity: amortized O(logn), worst case O(n)
     */
    public HeapNode consolidate(HeapNode x) {
        HeapNode[] fullBuckets = toBuckets(x);
        HeapNode node = null;
        for (int i = 0; i < fullBuckets.length; i++) {
            if (fullBuckets[i] != null) { //there's a Binomial tree with rank i
                if (node == null) { //reached the first Binomial tree in list
                    node = fullBuckets[i];
                    node.next = node;
                    node.prev = node;
                    this.first = node;
                } else { //not the first Binomial tree in list
                    insertAfter(node, fullBuckets[i]);
                    // make sure node holds the new minimum
                    if (fullBuckets[i].getKey() < node.getKey())
                        node = fullBuckets[i];

                }
            }
        }

        return node;
    }

    /**
     * public void insertAfter(HeapNode x, HeapNode y)
     * helper function - insert root y as root x's 'next'
     * only called from 'consolidate'
     * y is the now the last root of the list
     * Complexity: O(1)
     */
    public void insertAfter(HeapNode x, HeapNode y) {
        x.getNext().setPrev(y);
        y.setNext(x.getNext());
        x.setNext(y);
        y.setPrev(x);
    }

    /**
     * public HeapNode[] toBuckets(HeapNode x)
     * recieves HeapNode to start iterating from.
     * inserts all trees of the heap to an array of "buckets" according to their ranks.
     * If two equally-ranked trees are present - merge them and insert the new tree to correct bucket.
     * return array of buckets - each bucket i containing a single i-ranked tree or is null.
     * Complexity: amortized O(logn), worst case O(n)
     */
    public HeapNode[] toBuckets(HeapNode x) {
        //initialize array - all cells are null
        HeapNode[] buckets = new HeapNode[CalcMaxRank()];
        x.prev.next = null; // breaking the chain
        HeapNode y;
        while (x != null) {
            y = x;
            x = x.next; // continue to next
            // Making the node standalone
            y.next = y;
            y.prev = y;
            while (buckets[y.getRank()] != null) {
                y = link(y, buckets[y.getRank()]);
                buckets[y.getRank() - 1] = null; // clean cell in buckets
            }

            buckets[y.getRank()] = y;
        }

        return buckets;
    }


    /**
     * public static int log2(int num) {
     * helper function - calculate log of num with base 2
     * Complexity: O(1)
     */
    public static int log2(int num) {
        int res = (int) (Math.log(num) / Math.log(2.0));
        return res;
    }

    /**
     * public int CalcMaxRank()
     * helper functions - creates a HeapNode array sized ~ log_golden ratio(n)
     * Complexity: O(1)
     */
    public int CalcMaxRank() {
        int n = this.size;
        int len = (int) Math.ceil(log2(n) * 1.4404);
        return len + 1;
    }

    /**
     * public HeapNode findMin()
     * Return the node of the heap whose key is minimal.
     * Complexity: O(1)
     */
    public HeapNode findMin() {
        return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * Meld the heap with heap2, using lazy meld.
     * Complexity: O(1)
     */
    public void meld(FibonacciHeap heap2) {
        if (heap2.isEmpty()) return;
        if (this.isEmpty()) {
            this.first = heap2.first;
            this.min = heap2.min;
            this.size = heap2.size;
            return;
        }
        if (heap2.min.key < this.min.key) this.min = heap2.min;
        mergeNodes(heap2.first, this.first);
        this.size += heap2.size;
    }

    /**
     * public int size()
     * Return the number of elements in the heap
     * Complexity: O(1)
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     * Complexity: worst case O(n), amortized O(logn)
     */
    public int[] countersRep() {
        int[] arr = new int[CalcMaxRank()]; //all cells are 0

        HeapNode x = this.first;
        if (x != null) {
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
     * Deletes the node x from the heap.
     * Complexity:
     */
    public void delete(HeapNode x) {
        decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     * Complexity:
     */
    public void decreaseKey(HeapNode x, int delta) {
        x.key -= delta;
        if (x.getParent() != null) {
            HeapNode parent = x.getParent();

            if (x.key < parent.key) { // this is case when we need to cut it away
                cascadingCut(x, parent);
            }
        }
        // Checking if minimum changed
        if (x.key < min.key) {
            min = x;
        }
    }

    /**
     * public int potential()
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     * Complexity: worst case O(n), amortized O(logn)
     */
    public int potential() {
        if (isEmpty()) return 0;
        // Count how many trees
        int counter = 0;
        for (HeapNode n : this.first) { //use iterator
            counter++;
        }
        return (counter + (2 * this.marked));
    }



    /**
     * public static int totalLinks()
     * This static function returns the total number of link operations made during the run-time of the program.
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
     * in its root.
     * Complexity: O(1)
     */
    public static int totalLinks() {
        return links;
    }

    /**
     * public static int totalCuts()
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     * Complexity: O(1)
     */
    public static int totalCuts() {
        return cuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * This static function returns the k minimal elements in a binomial tree H.
     * The function should run in O(k*deg(H)).
     * You are not allowed to change H.
     * Complexity: O(k*deg(H))
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        // only one binomial tree https://moodle.tau.ac.il/mod/forum/discuss.php?d=55586
        // k is smaller then H.size()
        if (H.isEmpty() || k <= 0) return new int[0]; // nothing to do if empty
        int[] arr = new int[k];
        /*
        Steps to success:
            1. Create a new Heap p
            2. for k iteration, starting with H root:
                i.      insert current starter level to heap p. For each inserted node, save a pointer to node in H
                ii.     get the min pointer from p, store it at the output array.
                iii.    Set the min's child as the new starter. If no child, we will pick from existing elements in p.

         */
        HeapNode starter = H.min;
        FibonacciHeap hipo = new FibonacciHeap();

        for (int i = 0; i < k; i++) {

            if (starter != null) {
                for (HeapNode n : starter) { // at most deg(H)
                    HeapNode l = hipo.insert(n.getKey());
                    l.pointer = n;
                }
            }
            // get the child to be new starter.
            starter = hipo.min.pointer.child;
            // save the value
            arr[i] = hipo.min.key;
            // remove from heap
            hipo.deleteMin(); // O(logn) so worst would be O(log(k*deg(H)))
        }
        // Complexity summary:
        // O(k*(deg(H) + log(k*deg(H)))) = O(k(deg(H) + log(k))) = O(kdeg(H))

        return arr;
    }

    /**
     * public void cut(HeapNode x, HeapNode y)
     * Cut x from its parent y
     * Complexity: O(1)
     */
    public void cut(HeapNode x, HeapNode y) {
        cuts++; // static field, so no use of 'this'
        // remove parent and make sure it's unmarked
        x.parent = null;
        x.unmark();
        // reduce y rank
        y.decreaseRank();
        if (x.getNext() == x) { // node doesn't have siblings
            y.setChild(null);
        } else {
            if (y.child == x) {
                y.child = x.next;
            }
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }


        // Adding this subtree at the start of the list
        // TODO: Might be better to just meld when we write it, but need to make sure size etc. remains same
        HeapNode first = this.first;
        HeapNode last = this.first.getPrev();
        this.first = x; // set x as the first tree in heap
        last.setNext(x); // set last item's 'next' to x
        first.setPrev(x); // set previous first item's 'prev' to x
        x.setNext(first); // set x's 'next' to previous first
        x.setPrev(last); // set x's 'prev' to last item

    }

    public void resetCounters() {
        cuts = 0;
        links = 0;
    }

    /**
     * public void cascadingCut(HeapNode x, HeapNode y)
     * Perform a cascading-cut process starting at x
     * Complexity: O(logn) worst case, O(1) amortized
     */
    public void cascadingCut(HeapNode x, HeapNode y) {
        cut(x, y);
        if (y.getParent() != null) { // not a root node..
            if (y.isMarked()) { //we need to recursively call this function
                cascadingCut(y, y.getParent());
            } else {
                y.setMark(true);
            }
        }
    }

    /**
     * public class HeapNode
     * An implementation of a Fibonacci heap node over integers
     */
    public class HeapNode implements Iterable<HeapNode> {

        private int key;
        private int rank; //number of children
        private boolean mark;
        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        private HeapNode pointer; // used in k min to point to heap node in another heap

        /*
         * public HeapNode(int key)
         *HeapNode constructor - receives key and generates HeapNode instance with that key
         *O(1)
         */
        public HeapNode(int key) {
            this.key = key;
            this.rank = 0;
            this.mark = false;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;

        }
        /**
         * public int getKey()
         * Returns key of this node
         * Complexity: O(1)
         */
        public int getKey() {
            return this.key;
        }
        /**
         * public int getRank()
         * Returns rank of this node
         * Complexity: O(1)
         */
        public int getRank() {
            return this.rank;
        }
        /**
         * public boolean isMarked()
         * Returns mark-status of this node
         * Complexity: O(1)
         */
        public boolean isMarked() {
            return this.mark;
        }
        /**
         * public HeapNode getChild()
         * Returns left-most child of this node
         * Complexity: O(1)
         */
        public HeapNode getChild() {
            return this.child;
        }
        /**
         * public HeapNode getNext()
         * Returns right-hand brother of this node
         * Complexity: O(1)
         */
        public HeapNode getNext() {
            return this.next;
        }
        /**
         * public HeapNode getPrev()
         * Returns left-hand brother of this node
         * Complexity: O(1)
         */
        public HeapNode getPrev() {
            return this.prev;
        }
        /**
         * public HeapNode getParent()
         * Returns parent of this node
         * Complexity: O(1)
         */
        public HeapNode getParent() {
            return this.parent;
        }
        /**
         * public void setKey (int key)
         * Receives int key and sets the instance's 'key' field to it.
         * Complexity: O(1)
         */
        public void setKey(int key) {
            this.key = key;
        }
        /**
         * public void setMark (boolean b)
         * Receives boolean b and sets the instance's 'mark' field to it.
         * Complexity: O(1)
         */
        public void setMark(boolean b) {
            if (b && !this.mark) marked++; // should raise the mark
            else if (!b && this.mark) marked--; // should lower the mark
            this.mark = b;
        }
        /**
         * public void setRank (int r)
         * Receives int r and sets the instance's 'rank' field to it.
         * Complexity: O(1)
         */
        public void setRank(int r) {
            this.rank = r;
        }
        /**
         * public HeapNode setChild (HeapNode node)
         * Receives HeapNode node and sets the instance's 'child' field to it.
         * Complexity: O(1)
         */
        public void setChild(HeapNode node) {
            this.child = node;
        }
        /**
         * public HeapNode setNext (HeapNode node)
         * Receives HeapNode node and sets the instance's 'next' field to it.
         * Complexity: O(1)
         */
        public void setNext(HeapNode node) {
            this.next = node;
        }
        /**
         * public HeapNode setPrev (HeapNode node)
         * Receives HeapNode node and sets the instance's 'prev' field to it.
         * Complexity: O(1)
         */
        public void setPrev(HeapNode node) {
            this.prev = node;
        }
        /**
         * public HeapNode setParent (HeapNode node)
         * Receives HeapNode node and sets the instance's 'parent' field to it.
         * Complexity: O(1)
         */
        public void setParent(HeapNode node) {
            this.parent = node;
        }

        /**
         * public void unmark()
         * Unmarks the node.
         * Complexity: O(1)
         */
        public void unmark() {
            if (this.mark) marked--;
            this.mark = false;
        }

        /**
         * public void decreaseRank()
         * Decreases the node's rank by 1.
         * Complexity: O(1)
         */
        public void decreaseRank() {
            this.rank--;
        }

        /**
         * public Iterator<HeapNode> iterator()
         * returns new NodeIterator for this node.
         * Complexity: O(1)
         */
        @Override
        public Iterator<HeapNode> iterator() {
            return new NodeIterator(this);
        }
        /**
         * public String toString()
         * prints the key.
         * Complexity: O(1)
         */
        @Override
        public String toString() {
            return "Node: " + this.getKey();
        }
    }


    /**
     * This iterates over all next keys, starting from start point
     */
    public class NodeIterator implements Iterator<HeapNode> {
        HeapNode start, current;

        /**
         * public NodeIterator(HeapNode start)
         * NodeIterator constructor - receives node to start iterating from
         * Complexity: O(1)
         */
        public NodeIterator(HeapNode start) {
            this.start = start;
        }

        /**
         * public boolean hasNext()
         * returns true iff iterator has another non-null item
         * Complexity: O(1)
         */
        @Override
        public boolean hasNext() {
            if (current == null) return true;
            return current.getNext() != this.start;
        }

        /**
         * public boolean next()
         * returns next non-null item
         * Complexity: O(1)
         */
        @Override
        public HeapNode next() {
            if (current == null) {
                current = start;
            } else {
                current = current.getNext();
            }
            return current;
        }
    }

}

