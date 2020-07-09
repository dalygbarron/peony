package peony;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A renderer that allows you to stack transformations and then let rendering
 * functions use that by default.
 */
public class Renderer {
    private final Deque<Transformation> transformations = new ArrayDeque<>();
    private final Graphics2D g;

    /**
     * Creates the renderer with the graphics object it will use internally.
     * @param g is the graphics object to render with.
     */
    public Renderer(Graphics2D g) {
        this.g = g;
    }

    /**
     * Pushes a transformation onto the stack so that it will modify the
     * rendering functions.
     * @param t is the transformation to add.
     */
    public void push(Transformation t) {
        this.transformations.push(t);
    }

    /**
     * Removes the latest transformation from the stack after use.
     */
    public void pop() {
        this.transformations.pop();
    }

    /**
     * Sets the colour for drawing operations
     * @param colour is the colour to draw with.
     */
    public void setColour(Color colour) {
        this.g.setColor(colour);
    }

    /**
     * Draws a line.
     * @param a is the starting point.
     * @param b is the finishing point.
     */
    public void drawLine(Point a, Point b) {
        Point newA = this.transform(a);
        Point newB = this.transform(b);
        g.drawLine(newA.getXi(), newA.getYi(), newB.getXi(), newB.getYi());
    }

    /**
     * Takes a point in the currently constructed coordinate space and
     * converts it into natural coordinates.
     * @param in is the point to convert which will not be harmed.
     * @return the natural version.
     */
    private Point transform(Point in) {
        Point transformed = new Point(in);
        for (Transformation t: this.transformations) {
            transformed = t.out(transformed);
        }
        return transformed;
    }
}
