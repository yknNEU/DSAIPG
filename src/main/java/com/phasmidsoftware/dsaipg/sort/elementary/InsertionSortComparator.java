/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;

/**
 * A class for performing insertion sort using a comparator, extending functionality from SortWithHelper.
 * This includes methods for initialization and invocation of insertion sort,
 * along with specific utilities like counting inversions.
 *
 * @param <X> the type of elements to be sorted, which can be compared using a provided comparator.
 */
public class InsertionSortComparator<X> extends SortWithHelper<X> {
    /**
     * Constructor for InsertionSortComparator, which initializes the comparator with the provided helper.
     *
     * @param helper the Helper object to be used for managing the sorting process.
     */
    public InsertionSortComparator(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for any subclasses to use.
     *
     * @param description the description.
     * @param comparator  the comparator to use.
     * @param N           the number of elements expected.
     * @param nRuns       the number of runs to be expected (this is only significant when instrumenting).
     * @param config      the configuration.
     */
    protected InsertionSortComparator(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        super(description, comparator, N, nRuns, config);
    }

    /**
     * Constructor for InsertionSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to be expected (this is only significant when instrumenting).
     * @param config the configuration.
     */
    public InsertionSortComparator(Comparator<X> comparator, int N, int nRuns, Config config) {
        this(DESCRIPTION, comparator, N, nRuns, config);
    }

    /**
     * Sort the sub-array xs:from:to using insertion sort.
     *
     * @param xs   sort the array xs from "from" to "to".
     * @param from the index of the first element to sort
     * @param to   the index of the first element not to sort
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        for (int i = from + 1; i < to; i++) {
            X key = xs[i];
            int j = i - 1;
            while (j >= from && helper.compare(xs[j], key) > 0) {
                xs[j + 1] = xs[j];
                j--;
            }
            xs[j + 1] = key;
        }
    }

    public static final String DESCRIPTION = "Insertion sort";

    /**
     * Sorts the given array in-place using the provided insertion sort comparator.
     *
     * @param <T> the generic type parameter that extends Comparable.
     * @param ts  the array of elements to be sorted, where elements must implement {@code Comparable}.
     *            The method modifies this array directly to produce the sorted order.
     * @throws RuntimeException if an IOException occurs during the sorting process.
     */
    public static <T extends Comparable<T>> void sort(T[] ts) {
        try (InsertionSortComparator<T> sort = new InsertionSortComparator<>(DESCRIPTION, Comparable::compareTo, ts.length, 1, Config.load(InsertionSortComparator.class))) {
            sort.mutatingSort(ts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a case-insensitive string sorter using an insertion sort comparator.
     *
     * @param n      the expected number of elements to be sorted.
     * @param config the configuration object containing necessary settings.
     * @return a {@code SortWithHelper<String>} instance configured for case-insensitive string sorting.
     */
    public static Sort<String> stringSorterCaseInsensitive(int n, Config config) {
        return new InsertionSortComparator<>(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, n, getRunsConfig(config), config);
    }

    /**
     * This method is designed to count inversions in quadratic time, using insertion sort.
     *
     * @param ts  an array of comparable T elements.
     * @param <T> the underlying type of the elements.
     * @return the number of inversions in ts, which remains unchanged.
     */
    public static <T> long countInversions(T[] ts, Comparator<T> comparator) {
        final Config config = Config_Benchmark.setupConfigFixes();
        try (InsertionSortComparator<T> sorter = new InsertionSortComparator<>(comparator, ts.length, getRunsConfig(config), config)) {
            Helper<T> helper = sorter.getHelper();
            sorter.sort(ts, true);
            return helper.getFixes();
        }
    }

    public static void main(String[] args) {
        // Benchmarking insertion sort with integers
        Random random = new Random();
        int[] n_times = {1000, 2000, 4000, 8000, 16000, 32000, 64000};

        // Init warm up JVM
        long startTime = System.nanoTime();
        int temp = 0;
        for (int i = 0; i < 1000000; i++) {
            temp = random.nextInt();
        }
        long endTime = System.nanoTime();
        System.out.println("Warm up Finished: " + temp);

        for (int n : n_times) {
            // Generate an array of n random integers
            int[] inputRandom = new int[n];   // Ramdon array to be sorted
            int[] inputOrdered = new int[n];  // Already ordered array to be sorted
            int[] inputPartlyOrdered = new int[n];  // Half part of this array is ordered
            int[] inputReversed = new int[n];  // Reversed array to be sorted

            for (int i = 0; i < n; i++) {
                inputRandom[i] = random.nextInt();
                inputOrdered[i] = i;
                if (i < n / 2) {
                    inputPartlyOrdered[i] = i;
                } else {
                    inputPartlyOrdered[i] = Math.abs(random.nextInt() / 2) + (n / 2);
                }
                inputReversed[i] = n - i;
            }

            // Warm up after data generation
            startTime = System.nanoTime();
            for (int i = 0; i < 1000000; i++) {
                temp = random.nextInt();
            }
            endTime = System.nanoTime();

            // Benchmarking insertion sort with random integers
            startTime = System.nanoTime();
            // Do 10 times sort to warm up the JVM
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputRandom).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            // Do 10 times sort to get the average time
            startTime = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputRandom).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            System.out.println("Insertion sort with random integers, n = " + n + ": " + (endTime - startTime) / 10 + " ns");

            // Benchmarking insertion sort with ordered integers
            startTime = System.nanoTime();
            // Do 10 times sort to warm up the JVM
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputOrdered).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            // Do 10 times sort to get the average time
            startTime = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputOrdered).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            System.out.println("Insertion sort with ordered integers, n = " + n + ": " + (endTime - startTime) / 10 + " ns");

            // Benchmarking insertion sort with partly ordered integers
            startTime = System.nanoTime();
            // Do 10 times sort to warm up the JVM
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputPartlyOrdered).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            // Do 10 times sort to get the average time
            startTime = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputPartlyOrdered).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            System.out.println("Insertion sort with partly ordered integers, n = " + n + ": " + (endTime - startTime) / 10 + " ns");

            // Benchmarking insertion sort with reversed integers
            startTime = System.nanoTime();
            // Do 10 times sort to warm up the JVM
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputReversed).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            // Do 10 times sort to get the average time
            startTime = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                InsertionSortComparator.sort(Arrays.stream(inputReversed).boxed().toArray(Integer[]::new));
            }
            endTime = System.nanoTime();
            System.out.println("Insertion sort with reversed integers, n = " + n + ": " + (endTime - startTime) / 10 + " ns");
        }
    }

}