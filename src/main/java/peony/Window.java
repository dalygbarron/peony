package peony;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;

/**
 * Displays the visualisation of the current layout and lets you interact
 * with it to move stuff and all that.
 */
public class Window extends JPanel {
    public static final int NORMAL_WIDTH = 640;
    public static final int NORMAL_HEIGHT = 640;
    public static final int POINT_SIZE = 8;
    private Point camera = new Point(
        (float)Window.NORMAL_WIDTH / 2,
        (float)Window.NORMAL_HEIGHT / 2
    );
    private float zoom = 0.5f;
    private Layout layout = null;

    /**
     * Sets the layout that the window is drawing.
     * @param layout is the layout to be drawing and stuff.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
        this.repaint();
    }

    /**
     * Converts a point from world coordinates to screen coordinates.
     * @param point is the point to convert which should not be modified.
     * @return the converted version.
     */
    Point toScreen(Point point) {
        Point bounds = new Point(
            (float)this.getWidth() / 2,
            (float)this.getHeight() / 2
        );
        return point.minus(this.camera)
            .times(this.zoom)
            .plus(bounds);
    }

    /**
     * Converts a point from screen coordinates to world coordinates.
     * @param point is the point to convert which should not be modified.
     * @return the converted version.
     */
    Point fromScreen(Point point) {
        // TODO: this.
        return point;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Point bounds = new Point(
            (float)this.getWidth() / 2,
            (float)this.getHeight() / 2
        );
        this.drawRectangle(
            new Point(),
            new Point(Window.NORMAL_HEIGHT, Window.NORMAL_HEIGHT),
            g
        );
        if (this.layout == null) return;
        for (Leaf leaf: this.layout.getLeaves()) {
            Point origin = this.toScreen(leaf.getPosition());
            g.drawLine(
                origin.getXi() - Window.POINT_SIZE,
                origin.getYi(),
                origin.getXi() + Window.POINT_SIZE,
                origin.getYi()
            );
            g.drawLine(
                origin.getXi(),
                origin.getYi() - Window.POINT_SIZE,
                origin.getXi(),
                origin.getYi() + Window.POINT_SIZE
            );
            g.drawString(leaf.getName(), (int)origin.getX(), (int)origin.getY());
        }
        Point origin = this.toScreen(new Point());
        g.drawString(layout.getFullName(), origin.getXi(), origin.getYi());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
            (int)(Window.NORMAL_WIDTH * 1.5),
            (int)(Window.NORMAL_HEIGHT * 1.5)
        );
    }

    private void drawRectangle(Point origin, Point dimensions, Graphics g) {
        origin = this.toScreen(origin);
        Point end = this.toScreen(origin.plus(dimensions));
        g.drawLine(
            origin.getXi(),
            origin.getYi(),
            end.getXi(),
            origin.getYi()
        );
        g.drawLine(
            end.getXi(),
            origin.getYi(),
            end.getXi(),
            end.getYi()
        );
        g.drawLine(
            end.getXi(),
            end.getYi(),
            origin.getXi(),
            end.getYi()
        );
        g.drawLine(
            origin.getXi(),
            end.getYi(),
            origin.getXi(),
            origin.getYi()
        );
    }
}
