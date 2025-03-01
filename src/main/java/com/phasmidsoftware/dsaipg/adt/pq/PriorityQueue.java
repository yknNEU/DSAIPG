package com.phasmidsoftware.dsaipg.adt.pq;

import org.jetbrains.annotations.NotNull;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Priority Queue Data Structure which uses a binary heap.
 * <p/>
 * It is unlimited in capacity, although there is no code to grow it after it has been constructed.
 * It can serve as a minPQ or a maxPQ (define "max" as either false or true, respectively).
 * <p/>
 * It can support the root at index 1 or the root at index 0 variants.
 * <p/>
 * It operates on arbitrary Object types which implies that it requires a Comparator to be passed in.
 * <p/>
 * For all details on usage, please see PriorityQueueTest.java
 *
 * @param <K>
 */
public class PriorityQueue<K> implements Iterable<K> {

    /**
     * @return true if the current size is zero.
     */
    public boolean isEmpty() {
        return m == 0;
    }

    /**
     * @return the number of elements actually stored in this Priority Queue
     */
    public int size() {
        return m;
    }

    /**
     * Adds a key to the priority queue. If the priority queue is at its capacity,
     * the last element is removed to make space for the new key.
     * CONSIDER we can prevent the new element displacing a higher-priority element but at the cost of an extra comparison.
     * Is that worth it?
     *
     * @param key the element to be inserted into the priority queue
     */
    public void give(K key) {
        if (m == binHeap.length - first) m--;
        binHeap[++m + first - 1] = key;
        swimUp(m + first - 1);
    }

    /**
     * Remove the root element from this Priority Queue and adjust the binary heap accordingly.
     * If max is true, then the result will be the maximum element, else the minimum element.
     * NOTE that this method is sometimes called DelMax (or DelMin).
     *
     * @return If max is true, then the maximum element, otherwise the minimum element.
     * @throws PQException if this priority queue is empty
     */
    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        return doTake(floyd ? this::snake : this::sink);
    }

    /**
     * Package-private method to remove the root element from the priority queue,
     * reorganizes the heap to maintain the priority queue properties,
     * and applies the provided function to the root index.
     *
     * @param f a consumer function that manipulates the root index to maintain the heap order.
     * @return the root element of the priority queue before reorganization.
     */
    K doTake(Consumer<Integer> f) {
        K result = binHeap[first]; // get the root element (the largest or smallest, according to field max)
        swap(first, m-- + first - 1); // swap the root element with the last element
        f.accept(first); // invoke the function f so that it is ordered again
        binHeap[m + first] = null; // prevent loitering
        return result;
    }

    /**
     * Sink the element at index k down
     */
    void sink(@SuppressWarnings("SameParameterValue") int k) {
        doHeapifyStandard(k);
    }

    /**
     * Special sink method that sinks the element and then swim the element back
     *
     * @param k the starting index of the element in the heap to be adjusted.
     */
    void snake(@SuppressWarnings("SameParameterValue") int k) {
        swimUp(doHeapify(k, (a, b) -> false));
    }

    /**
     * Swim the element at index k up
     */
    void swimUp(int k) {
        int i = k;
        while (i > first && inverted(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Compare the elements at indices i and j.
     * We expect the first index (the smaller one) to be greater than the second, assuming that max is true.
     * In this case, we return false.
     *
     * @param i the lower index, numerically
     * @param j the higher index, numerically
     * @return true if the values are out of order.
     */
    boolean inverted(int i, int j) {
        return (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max;
    }

    /**
     * Non-mutating iterator over all values of this PriorityQueue.
     * NOTE: after the first element, there is no definite ordering of the remaining elements.
     * NOTE: this method is really not truly a method of the PriorityQueue API.
     * It is here only for convenience.
     *
     * @return an iterator based on a copy of the underlying array.
     */
    @NotNull
    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(binHeap, m + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0) result.next(); // strip off the leading null value.
        return result;
    }

    /**
     * Primary constructor that takes the max value, an actual array of elements, and a comparator.
     *
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param binHeap    a pre-formed array with length sufficient to accommodate all required elements plus any unused array slots.
     * @param first      the index of the root element.
     * @param m       the number of elements in binHeap
     * @param comparator a comparator for the type K
     * @param floyd      true if we use Floyd's trick (aka snake).
     */
    public PriorityQueue(boolean max, Object[] binHeap, int first, int m, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.m = m;
        //noinspection unchecked
        this.binHeap = (K[]) binHeap;
        this.floyd = floyd;
    }

    /**
     * Secondary constructor which takes only the priority queue's maximum capacity and a comparator
     *
     * @param n          the desired maximum capacity.
     * @param first      the index to use for the first (root) element.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     * @param floyd      true if we use Floyd's trick (aka snake).
     */
    public PriorityQueue(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(max, new Object[n + first], first, 0, comparator, floyd);
    }

    /**
     * Constructs a priority queue with specified capacity, type (max or min), a comparator,
     * and an option to use Floyd's heap construction algorithm.
     *
     * @param n          the desired maximum capacity of the priority queue.
     * @param max        if true, this is a Maximum Priority Queue; if false, it is a Minimum Priority Queue.
     * @param comparator a comparator for the type K to define the priority order.
     * @param floyd      if true, Floyd's heap construction algorithm will be used.
     */
    public PriorityQueue(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(n, 1, max, comparator, floyd);
    }

    /**
     * Secondary constructor which takes only the priority queue's maximum capacity and a comparator.
     * Floyd is false and first is always 1.
     *
     * @param n          the desired maximum capacity.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(int n, boolean max, Comparator<K> comparator) {
        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(n, max, comparator, false);
    }

    /**
     * Secondary constructor which takes only the priority queue's maximum capacity and a comparator.
     * Other parameter values: max = true; first = 0; floyd = true.
     *
     * @param n          the desired maximum capacity.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(int n, Comparator<K> comparator) {
        this(n, 0, true, comparator, true);
    }

    /**
     * Secondary constructor which takes a Collection to be added immediately and a comparator.
     * Other parameter values: n = ks.size; max = true; first = 0; floyd = true.
     * This constructor uses the O(n) heap construction method (sometimes also known as "Floyd's Trick.").
     * This constructor is suitable for use by HeapSort.
     *
     * @param ks         a Collection of K elements.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue(Collection<K> ks, Comparator<K> comparator) {
        this(ks.size(), comparator);
        int i = 0;
        for (K k : ks) binHeap[i++] = k;
        m = ks.size();
        int k = (m + 1) / 2 - 1;
        for (; k >= 0; k--) sink(k);
    }

    /**
     * Adjusts a subtree rooted at index k to ensure it satisfies the heap property.
     * The method reorganizes the binary heap by comparing parent and child nodes,
     * swapping their positions if necessary, until the correct heap order is maintained.
     *
     * @param k the starting index of the element in the heap that needs to be adjusted.
     *          That's to say, the root of the sub-heap.
     * @param p a predicate that determines the heap condition to be satisfied.
     *          It takes two indices (parent and child) and returns true if the parent satisfies the heap property relative to the child.
     *          When the predicate is satisfied, we break out of the loop.
     * @return the final position of the element originally at index k after reorganization.
     */
    protected int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (true) {
            int firstChild = firstChild(i);
            if (!(firstChild <= m + first - 1)) break;
            int j = firstChild;
            if (j < m + first - 1 && inverted(j, j + 1)) j++;
            if (p.test(i, j)) break;
            swap(i, j);
            i = j;
        }
        return i;
    }

    /**
     * Adjusts a subtree rooted at index k to ensure it satisfies the heap property.
     * The method reorganizes the binary heap by comparing parent and child nodes,
     * swapping their positions if necessary, until the correct heap order is maintained.
     *
     * @param k the starting index of the element in the heap that needs to be adjusted.
     *          That's to say, the root of the sub-heap.
     * @return the final position of the element originally at index k after reorganization.
     */
    private int doHeapifyStandard(int k) {
        return doHeapify(k, (a, b) -> !inverted(a, b));
    }

    /**
     * Exchange the values at indices i and j
     */
    protected void swap(int i, int j) {
        K tmp = binHeap[i];
        binHeap[i] = binHeap[j];
        binHeap[j] = tmp;
    }

    /**
     * Get the index of the parent of the element at index k
     */
    protected int parent(int k) {
        return (k + 1 - first) / 2 + first - 1;
    }

    /**
     * Get the index of the first child of the element at index k.
     * The index of the second child will be one greater than the result.
     */
    protected int firstChild(int k) {
        return (k + 1 - first) * 2 + first - 1;
    }

    /**
     * The following methods are for unit testing ONLY!!
     */

    @SuppressWarnings("unused")
    private K peek(int k) {
        return binHeap[k];
    }

    @SuppressWarnings("unused")
    private boolean getMax() {
        return max;
    }

    /**
     * Indicates whether this Priority Queue is configured as a Maximum Priority Queue.
     * If true, the Priority Queue will prioritize higher values, making the maximum element
     * the first to be removed. If false, the Priority Queue will act as a Minimum Priority Queue,
     * prioritizing lower values instead.
     */
    protected final boolean max;
    /**
     * The index of the root element of the priority queue.
     * This field indicates the position of the root element in the binary heap array.
     * Its value is determined during the construction of the priority queue and is used
     * throughout to maintain the priority queue's structural and logical properties.
     * NOTE that only values 0 and 1 are tested in PriorityQueueTest.java
     */
    protected final int first;
    /**
     * A comparator used to define the order of elements in the PriorityQueue.
     * It determines the relative priority of two elements of type K.
     * This comparator is passed during the construction of the PriorityQueue
     * and is used throughout its operations to maintain the desired heap order.
     */
    protected final Comparator<K> comparator;
    /**
     * The binary heap array used to represent the internal structure of the priority queue.
     * This array is structured to maintain the properties of a binary heap,
     * either as a max-heap or a min-heap, depending on the configuration of the priority queue.
     * The first index may not always contain an element, as it depends on the initialization parameters.
     * The array has a capacity determined at the creation of the PriorityQueue and
     * may include a single extra space for reorganization purposes.
     */
    protected final K[] binHeap;
    /**
     * The current number of elements in the binary heap used by this priority queue.
     * This variable represents the dynamic size of the priority queue, and
     * is incremented or decremented as elements are added or removed.
     */
    protected int m;
    /**
     * A boolean flag that indicates whether Floyd's optimization method, known as "Floyd's Trick" or
     * "Floyd's snake method", is enabled or disabled during the execution of the take method.
     * When enabled, this optimization adjusts the binary heap to enhance performance in specific scenarios.
     */
    protected final boolean floyd;

    private static class FourAryHeap<K> extends PriorityQueue<K> {

        public FourAryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
            super(max, new Object[n + 1], 1, 0, comparator, floyd);
        }

        @Override
        protected int doHeapify(int k, BiPredicate<Integer, Integer> p) {
            int i = k;
            while (firstChild(i) <= m + first - 1) {
                int j = firstChild(i);
                int maxChild = j;
                for (int l = 1; l < 4; l++) {
                    if (j + l <= m + first - 1 && inverted(maxChild, j + l)) {
                        maxChild = j + l;
                    }
                }
                if (p.test(i, maxChild)) break;
                swap(i, maxChild);
                i = maxChild;
            }
            return i;
        }

        @Override
        protected int parent(int k) {
            return (k - first - 1) / 4 + first;
        }

        @Override
        protected int firstChild(int k) {
            return (k - first) * 4 + first + 1;
        }
    }

    private static class FibonacciHeap<K> extends PriorityQueue<K> {
        private static class Node<K> {
            K key;
            Node<K> parent;
            Node<K> child;
            Node<K> left;
            Node<K> right;
            int degree;
            boolean marked;

            Node(K key) {
                this.key = key;
                this.left = this;
                this.right = this;
            }
        }

        private Node<K> minNode;
        private int n;

        public FibonacciHeap(int n, boolean max, Comparator<K> comparator) {
            super(max, null, 1, 0, comparator, false);
            this.minNode = null;
            this.n = n;
        }

        @Override
        public void give(K key) {
            if (m >= n) {
                return;
            }
            Node<K> node = new Node<>(key);
            if (minNode == null) {
                minNode = node;
            } else {
                node.left = minNode;
                node.right = minNode.right;
                minNode.right = node;
                node.right.left = node;
                if (comparator.compare(node.key, minNode.key) < 0) {
                    minNode = node;
                }
            }
            m++;
        }

        @Override
        public K take() throws PQException {
            if (isEmpty()) throw new PQException("Priority queue is empty");
            Node<K> z = minNode;
            if (z.child != null) {
                Node<K> x = z.child;
                do {
                    Node<K> nextX = x.right;
                    x.left = minNode;
                    x.right = minNode.right;
                    minNode.right.left = x;
                    minNode.right = x;
                    x.parent = null;
                    x = nextX;
                } while (x != z.child);
                z.child = null;
            }
            z.left.right = z.right;
            z.right.left = z.left;
            if (z == z.right) {
                minNode = null;
            } else {
                minNode = z.right;
                consolidate();
            }
            m--;
            return z.key;
        }

        private void consolidate() {
            int maxDegree = (int) Math.ceil(Math.log(m) / Math.log(2)) + 1;
            Node<K>[] arr = new Node[maxDegree];
            Node<K> current = minNode;
            List<Node<K>> nodes = new ArrayList<>();
            if (current != null) {
                do {
                    nodes.add(current);
                    current = current.right;
                } while (current != minNode);
            }
            for (Node<K> node : nodes) {
                int d = node.degree;
                while (arr[d] != null) {
                    Node<K> y = arr[d];
                    if (compare(node.key, y.key) > 0) {
                        Node<K> temp = node;
                        node = y;
                        y = temp;
                    }
                    link(y, node);
                    arr[d] = null;
                    d++;
                }
                arr[d] = node;
            }
            minNode = null;
            for (Node<K> a : arr) {
                if (a != null) {
                    if (minNode == null) {
                        minNode = a;
                        a.left = a;
                        a.right = a;
                    } else {
                        a.left = minNode;
                        a.right = minNode.right;
                        minNode.right.left = a;
                        minNode.right = a;
                        if (compare(a.key, minNode.key) < 0) {
                            minNode = a;
                        }
                    }
                }
            }
        }

        private void link(Node<K> child, Node<K> parent) {
            child.left.right = child.right;
            child.right.left = child.left;
            child.parent = parent;
            if (parent.child == null) {
                parent.child = child;
                child.left = child;
                child.right = child;
            } else {
                child.left = parent.child;
                child.right = parent.child.right;
                parent.child.right.left = child;
                parent.child.right = child;
            }
            parent.degree++;
            child.marked = false;
        }

        private int compare(K a, K b) {
            int cmp = comparator.compare(a, b);
            return max ? -cmp : cmp;
        }

        @Override
        public Iterator<K> iterator() {
            List<K> keys = new ArrayList<>();
            if (minNode != null) {
                Node<K> current = minNode;
                do {
                    keys.add(current.key);
                    current = current.right;
                } while (current != minNode);
            }
            return keys.iterator();
        }
    }

    public static <K> PriorityQueue<K> newBinaryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        return new PriorityQueue<>(n, max, comparator, floyd);
    }

    public static <K> PriorityQueue<K> newFourAryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        return new FourAryHeap<>(n, max, comparator, floyd);
    }

    public static <K> PriorityQueue<K> newFibonacciHeap(int n, boolean max, Comparator<K> comparator) {
        return new FibonacciHeap<>(n, max, comparator);
    }

    public static void main(String[] args) {
        doMain();
    }

    static void doMain() {
        int heapSize = 4095;
        boolean max = false;
        boolean floyd = true;
        PriorityQueue<Integer> PQ_int_floyd = new PriorityQueue<>(heapSize, max, Comparator.comparing(Integer::intValue), floyd);
        PriorityQueue<Integer> PQ_int_nofloyd = new PriorityQueue<>(heapSize, max, Comparator.comparing(Integer::intValue), false);
        PriorityQueue<Integer> FAH_int_floyd = newFourAryHeap(heapSize, max, Comparator.comparing(Integer::intValue), floyd);
        PriorityQueue<Integer> FAH_int_nofloyd = newFourAryHeap(heapSize, max, Comparator.comparing(Integer::intValue), false);
        PriorityQueue<Integer> FH_int = newFibonacciHeap(heapSize, max, Comparator.comparing(Integer::intValue));

        Random random = new Random();
        List<Integer> testData = new ArrayList<>();
        for (int i = 0; i < 16000; i++) {
            testData.add(random.nextInt());
        }
        benchmarkHeap("PQ_int_floyd", PQ_int_floyd, testData);
        benchmarkHeap("PQ_int_nofloyd", PQ_int_nofloyd, testData);
        benchmarkHeap("FAH_int_floyd", FAH_int_floyd, testData);
        benchmarkHeap("FAH_int_nofloyd", FAH_int_nofloyd, testData);
        benchmarkHeap("FH_int", FH_int, testData);
    }

    private static <T> void benchmarkHeap(String name, PriorityQueue<T> pq, List<T> testData) {        
        Benchmark_Timer<PriorityQueue<T>> timer = new Benchmark_Timer<>(name, heap -> {
            try {
                for (T data : testData) {
                    heap.give(data);
                }

                T highestPriority = null;
                for (int i = 0; i < 4000; i++) {
                    T removed = heap.take();
                    if (highestPriority == null || heap.comparator.compare(removed, highestPriority) > 0) {
                        highestPriority = removed;
                    }
                }
                System.out.println("Highest priority: " + highestPriority);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        double time = timer.runFromSupplier(() -> pq, 2);
        System.out.println(name + " time: " + time + "ms, log2(time): " + Math.log(time) / Math.log(2));
    }
}
