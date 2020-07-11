package peony;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A renderer that allows you to stack transformations and then let rendering
 * functions use that by default.
 */
public class Renderer {
    private static final Stroke DASH = new BasicStroke(
        1,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0,
        new float[]{2},
        0
    );
    private final Deque<Transformation> transformations = new ArrayDeque<>();
    private final Graphics2D g;
    private final Leaf selectedLeaf;
    private final Point selectedPoint;

    /**
     * Creates the renderer with the graphics object it will use internally.
     * @param g             is the graphics object to render with.
     * @param selectedLeaf  is the leaf that is selected if any.
     * @param selectedPoint is the point that is selected if any.
     */
    public Renderer(Graphics2D g, Leaf selectedLeaf, Point selectedPoint) {
        this.g = g;
        this.selectedLeaf = selectedLeaf;
        this.selectedPoint = selectedPoint;
    }

    /**
     * Tells you if a leaf is the selected leaf.
     * @param selected is the leaf to check.
     * @return true iff the given leaf is selected.
     */
    public boolean isLeafSelected(Leaf selected) {
        return this.selectedLeaf == selected;
    }

    /**
     * Tells you if a point is the selected point.
     * @param selected is the point that is selected.
     * @return true iff the given point is selected.
     */
    public boolean isPointSelected(Point selected) {
        return this.selectedPoint == selected;
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

    public void drawText(Point pos, String text) {
        Point t = this.transform(pos);
        g.drawString(text, t.getXi(), t.getYi());
    }

    /**
     * Draws a line.
     * @param a is the starting point.
     * @param b is the finishing point.
     */
    public void drawLine(Point a, Point b) {
        Point newA = this.transform(a);
        Point newB = this.transform(b);
        this.g.drawLine(newA.getXi(), newA.getYi(), newB.getXi(), newB.getYi());
    }

    /**
     * Draws a dotted line which is nice.
     * @param a is the start of the line.
     * @param b is the end of the line.
     */
    public void drawDottedLine(Point a, Point b) {
        Stroke old = this.g.getStroke();
        this.g.setStroke(Renderer.DASH);
        this.drawLine(a, b);
        this.g.setStroke(old);
    }

    /**
     * Draws a rectangle.
     * @param pos is the position in the current coordinates of the renderer.
     * @param size is the width and height of the rectangle.
     */
    public void drawRectangle(Point pos, Point size) {
        Point tl = this.transform(pos);
        Point tr = this.transform(pos.plus(new Point(size.getX(), 0)));
        Point br = this.transform(pos.plus(size));
        Point bl = this.transform(pos.plus(new Point(0, size.getY())));
        g.drawLine(tl.getXi(), tl.getYi(), tr.getXi(), tr.getYi());
        g.drawLine(tr.getXi(), tr.getYi(), br.getXi(), br.getYi());
        g.drawLine(br.getXi(), br.getYi(), bl.getXi(), bl.getYi());
        g.drawLine(bl.getXi(), bl.getYi(), tl.getXi(), tl.getYi());
    }

    /**
     * Draws a circle.
     * @param pos    is the place for the centre of the circle.
     * @param radius is the radius of the circle.
     */
    public void drawCircle(Point pos, float radius) {
        Point t = this.transform(pos);
        g.drawOval(
            (int)(t.getX() - radius),
            (int)(t.getY() - radius),
            (int)(radius * 2),
            (int)(radius * 2)
        );
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
