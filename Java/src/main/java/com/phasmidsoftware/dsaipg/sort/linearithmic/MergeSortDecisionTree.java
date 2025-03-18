package com.phasmidsoftware.dsaipg.sort.linearithmic;


/**
 * MergeSortDecisionTree is a class that implements the Merge Sort algorithm.
 * This algorithm is a divide-and-conquer sorting technique that splits an array into halves,
 * recursively sorts them, and then merges the sorted halves.
 */
public class MergeSortDecisionTree {

    /**
     * Merges two sorted subarrays of the given array into a single sorted subarray.
     * The first subarray is defined by the range from index l to m (inclusive),
     * and the second subarray is defined by the range from index m+1 to r (inclusive).
     *
     * @param arr The array containing the subarrays to be merged.
     * @param l   The starting index of the first subarray.
     * @param m   The ending index of the first subarray and the midpoint of the merge.
     * @param r   The ending index of the second subarray.
     */
    void merge(int[] arr, int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int[] L = new int[n1];
        int[] R = new int[n2];

        /*Copy data to temp arrays*/
        System.arraycopy(arr, l, L, 0, n1);
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    /**
     * Sorts a portion of the array using the merge sort algorithm.
     * This method recursively divides the array into two halves, sorts each half,
     * and then merges the sorted halves.
     *
     * @param arr The array to be sorted.
     * @param l   The starting index of the portion of the array to sort.
     * @param r   The ending index of the portion of the array to sort.
     */
    void sort(int[] arr, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l + r) / 2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    /**
     * A utility method to print all elements of the given integer array.
     * Each element of the array is printed on the same line, separated by spaces.
     *
     * @param arr The array of integers to be printed.
     */
    static void printArray(int[] arr) {
        int n = arr.length;
        for (int anArr : arr) System.out.print(anArr + " ");
        System.out.println();
    }

    /**
     * The main method serves as the driver for executing the program. It performs the following tasks:
     * 1. Initializes an array of integers.
     * 2. Prints the initial state of the array.
     * 3. Creates an instance of the MergeSortDecisionTree class and sorts the array using merge sort.
     * 4. Prints the sorted array.
     *
     * @param args The command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6, 7};

        System.out.println("Given Array");
        printArray(arr);

        MergeSortDecisionTree ob = new MergeSortDecisionTree();
        ob.sort(arr, 0, arr.length - 1);

        System.out.println("\nSorted array");
        printArray(arr);
    }
}