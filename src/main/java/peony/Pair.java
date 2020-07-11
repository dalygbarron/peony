package peony;

/**
 * Two objects together.
 * @param <A> is the type of the first thing.
 * @param <B> is the type of the second thing.
 */
public class Pair<A, B> {
    private final A a;
    private final B b;

    /**
     * Creates a pair.
     * @param a is the first thing.
     * @param b is the second thing.
     */
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Gives you the first thing.
     * @return the first thing.
     */
    public A getA() {
        return this.a;
    }

    /**
     * Gives you the second thing.
     * @return the second thing.
     */
    public B getB() {
        return this.b;
    }
}
