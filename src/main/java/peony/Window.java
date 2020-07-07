package peony;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;

/**
 * Displays the visualisation of the current layout and lets you interact
 * with it to move stuff and all that.
 */
public class Window extends JPanel
    implements MouseListener, MouseWheelListener, MouseMotionListener
{
    public static final int NORMAL_WIDTH = 640;
    public static final int NORMAL_HEIGHT = 640;
    public static final int MARGIN = 50;
    public static final int POINT_SIZE = 8;
    public static final float MIN_ZOOM = 0.1f;
    private Point mouse = new Point();
    private Point camera = new Point(
        (float)Window.NORMAL_WIDTH / 2,
        (float)Window.NORMAL_HEIGHT / 2
    );
    private float zoom = 1;
    private Layout layout = null;

    /**
     * Creates it and makes it it's own listener.
     */
    public Window() {
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Sets the layout that the window is drawing.
     * @param layout is the layout to be drawing and stuff.
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
        this.camera = new Point(
            (float)Window.NORMAL_WIDTH / 2,
            (float)Window.NORMAL_HEIGHT / 2
        );
        this.zoom =
            (float)this.getHeight() /  (Window.NORMAL_HEIGHT + Window.MARGIN);
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
        return new Dimension(Window.NORMAL_WIDTH * 2, Window.NORMAL_HEIGHT * 2);
    }

    /**
     * Draws a rectangle from world coordinates in screen coordinates.
     * @param origin     top left corner in world coordinates.
     * @param dimensions width and height in world coordinates.
     * @param g          graphics drawing object.
     */
    private void drawRectangle(Point origin, Point dimensions, Graphics g) {
        Point end = this.toScreen(origin.plus(dimensions));
        origin = this.toScreen(origin);
        g.drawLine(origin.getXi(), origin.getYi(), end.getXi(), origin.getYi());
        g.drawLine(end.getXi(), origin.getYi(), end.getXi(), end.getYi());
        g.drawLine(end.getXi(), end.getYi(), origin.getXi(), end.getYi());
        g.drawLine(origin.getXi(), end.getYi(), origin.getXi(), origin.getYi());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point newMouse = new Point(e.getX(), e.getY());
        Point delta = this.mouse.minus(newMouse);
        this.camera.add(delta.times(1 / this.zoom));
        this.repaint();
        this.mouse.set(newMouse);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.mouse.set(e.getX(), e.getY());
        this.zoom -= e.getPreciseWheelRotation() / 15;
        if (this.zoom < Window.MIN_ZOOM) this.zoom = Window.MIN_ZOOM;
        this.repaint();
    }
}
