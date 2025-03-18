package edu.neu.coe.info6205.admin;

import java.util.*;

/**
 * The Ticket class is designed to generate a specified number of tickets,
 * each being a product of a randomly selected common factor and a subset of prime numbers.
 * The tickets are shuffled before being returned.
 * The purpose of the tickets is to ensure that quizzes are taken in-class and not online.
 * Students can copy their ticket number to their off-line friend,
 * but that will trigger a duplicate ticket on submission.
 */
public class Ticket {

    /**
     * When you run this program, you need to provide a value of n, the number of tickets you need.
     *
     * @param args n: how many tickets
     */
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        Random r = new Random();
        int factor = getFactor(r);
        System.out.println("Common factor: " + factor);
        Collection<Ticket> tickets = getTickets(n, factor, r);
        System.out.println(Arrays.deepToString(tickets.toArray()));
    }

    /**
     * Selects a random element from the predefined list of prime numbers using the provided Random instance.
     *
     * @param r the Random instance used to generate a random index for selecting a prime number.
     * @return a randomly selected prime number from the list of predefined primes.
     */
    static int getFactor(Random r) {
        return primes[r.nextInt(primes.length)];
    }

    /**
     * Generates a shuffled list of ticket numbers.
     * <p>
     * The ticket numbers are calculated as the product of a given factor and a contiguous subset
     * of prime numbers from a predefined list.
     * The first prime number to be chosen is determined at random.
     * Using a contiguous subset is more predictable, but we don't need to worry about duplicates.
     *
     * @param n      the number of ticket numbers to generate.
     * @param factor the common factor to be multiplied with the primes.
     * @param random an instance of {@link Random} for generating random indices and shuffling the tickets.
     * @return a list of shuffled ticket numbers.
     */
    static List<Ticket> getTickets(int n, int factor, Random random) {
        Ticket[] products = new Ticket[n];
        int x = random.nextInt(primes.length - n);
        for (int i = 0; i < n; i++)
            products[i] = new Ticket((long) factor * primes[i + x]);

        List<Ticket> tickets = new ArrayList<>(Arrays.asList(products));
        Collections.shuffle(tickets, random);
        return tickets;
    }

    public Ticket(long number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("0x%08X", number);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ticket ticket)) return false;
        return number == ticket.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }

    private final long number;

    /**
     * A predefined list of the first 180 prime numbers (skipping the first ten)
     * to be used in various computational contexts.
     * <p>
     * The list excludes the first 10 primes to simplify factoring in certain operations.
     * These prime numbers are ordered sequentially and are utilized in methods such as
     * random prime selection or ticket generation.
     * <p>
     * This array is declared as static and final to ensure immutability and consistent
     * access throughout the program.
     */
    private static final int[] primes = {
            // NOTE we don't include the first 10 primes (too easy to factor out).
            31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
            73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
            179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
            283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409,
            419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541,
            547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659,
            661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809,
            811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941,
            947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013, 1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069
    };

}