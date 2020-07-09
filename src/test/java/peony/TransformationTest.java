package peony;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests that transformations are working like they should.
 */
public class TransformationTest {
    @Test
    public void testIn() {
        Transformation t = new Transformation(
            new Point(5, 5),
            (float)(Math.PI / 2),
            0.5f
        );
        Point a = new Point(0, 2);
        Point b = new Point(4, 4);
        Point tA = t.in(a);
        Point tB = t.in(b);
        // Test that the transformed versions are correct.
        assertEquals("transform in correctly", 6, tA.getX(), 0.01);
        assertEquals("transform in correctly", -10, tA.getY(), 0.01);
        assertEquals("transform in correctly", 2, tB.getX(), 0.01);
        assertEquals("transform in correctly", -2, tB.getY(), 0.01);
        // Also quickly make sure it does not modify the input points.
        assertEquals("Don't modify input", 0, a.getX(), 0);
        assertEquals("Don't modify input", 2, a.getY(), 0);
        assertEquals("Don't modify input", 4, b.getX(), 0);
        assertEquals("Don't modify input", 4, b.getY(), 0);
    }

    @Test
    public void testOut() {
        Transformation t = new Transformation(
            new Point(5, 5),
            (float)(Math.PI / 2),
            0.5f
        );
        Point a = new Point(6, -10);
        Point b = new Point(2, -2);
        Point tA = t.out(a);
        Point tB = t.out(b);
        // Test that the transformed versions are correct.
        assertEquals("transform in correctly", 0, tA.getX(), 0.01);
        assertEquals("transform in correctly", 2, tA.getY(), 0.01);
        assertEquals("transform in correctly", 4, tB.getX(), 0.01);
        assertEquals("transform in correctly", 4, tB.getY(), 0.01);
        // Also quickly make sure it does not modify the input points.
        assertEquals("Don't modify input", 6, a.getX(), 0);
        assertEquals("Don't modify input", -10, a.getY(), 0);
        assertEquals("Don't modify input", 2, b.getX(), 0);
        assertEquals("Don't modify input", -2, b.getY(), 0);
    }
}
