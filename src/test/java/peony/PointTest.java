package peony;

import org.junit.Test;
import static org.junit.Assert.*;

public class PointTest {
    @Test
    public void testConstructors() {
        Point a = new Point();
        assertEquals("x should be 0", 0, a.getX(), 0);
        assertEquals("y should be 0", 0, a.getY(), 0);
        Point b = new Point(5);
        assertEquals("x should be 5", 5, b.getX(), 0);
        assertEquals("y should be 5", 5, b.getY(), 0);
        Point c = new Point(6, 3);
        assertEquals("x should be 6", 6, c.getX(), 0);
        assertEquals("y should be 3", 3, c.getY(), 0);
    }

    @Test
    public void testSet() {
        Point a = new Point();
        a.set(1, 2);
        assertEquals("x should be 1", 1, a.getX(), 0);
        assertEquals("y should be 1", 2, a.getY(), 0);
        a.set(new Point(4, 3));
        assertEquals("x should be 1", 4, a.getX(), 0);
        assertEquals("y should be 1", 3, a.getY(), 0);
    }
}
