package peony;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
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
    public static final float MIN_ZOOM = 0.1f;
    private List<WindowListener> listeners = new ArrayList<>();
    private Point mouse = new Point();
    private Point camera = new Point(
        (float)Window.NORMAL_WIDTH / 2,
        (float)Window.NORMAL_HEIGHT / 2
    );
    private float zoom = 1;
    private Layout layout = null;
    private Leaf selected = null;
    private Point selectedPoint = null;

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
        this.selected = null;
        this.camera = new Point(
            (float)Window.NORMAL_WIDTH / 2,
            (float)Window.NORMAL_HEIGHT / 2
        );
        this.zoom =
            (float)this.getHeight() /  (Window.NORMAL_HEIGHT + Window.MARGIN);
        this.repaint();
    }

    /**
     * Sets the leaf that this window will have as the selected leaf.
     * @param leaf is the leaf to select.
     */
    public void setSelected(Leaf leaf) {
        this.selected = leaf;
    }

    /**
     * Gives you the middle point of the current drawing area size in screen
     * coordinates.
     * @return the middle point.
     */
    public Point getMiddle() {
        return new Point(
            (float)this.getWidth() / 2,
            (float)this.getHeight() / 2
        );
    }

    /**
     * Converts a point from world coordinates to screen coordinates.
     * @param point is the point to convert which should not be modified.
     * @return the converted version.
     */
    Point toScreen(Point point) {
        return point.minus(this.camera)
            .times(this.zoom)
            .plus(this.getMiddle());
    }

    /**
     * Converts a point from screen coordinates to world coordinates.
     * @param point is the point to convert which should not be modified.
     * @return the converted version.
     */
    public Point fromScreen(Point point) {
        return point
            .minus(this.getMiddle())
            .times(1 / this.zoom)
            .plus(this.camera);
    }

    /**
     * Adds a listener to the list of window listeners.
     * @param listener is the thing that listens.
     */
    public void addListener(WindowListener listener) {
        this.listeners.add(listener);
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

    /**
     * Fires the event for when a leaf got selected to all listeners.
     * @param leaf is the thing that got selected.
     */
    private void fireEvent(Leaf leaf) {
        for (WindowListener listener: this.listeners) {
            listener.leafSelected(this, leaf);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.layout != null) {
            for (Leaf leaf: this.layout.getLeaves()) {
                Point origin = this.toScreen(leaf.getPosition());
                leaf.render(g, origin, this.zoom, leaf == this.selected);
            }
        }
        g.setColor(Color.BLACK);
        if (this.layout != null) {
            Point origin = this.toScreen(new Point());
            g.drawString(layout.getFullName(), origin.getXi(), origin.getYi());
        }
        this.drawRectangle(
            new Point(),
            new Point(Window.NORMAL_HEIGHT, Window.NORMAL_HEIGHT),
            g
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Window.NORMAL_WIDTH * 2, Window.NORMAL_HEIGHT * 2);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (this.layout == null) return;
            Point pos = this.fromScreen(this.mouse);
            this.selected = this.layout.getLeafByPosition(pos);
            this.selectedPoint = null;
            if (this.selected instanceof ShapeLeaf) {
                this.selectedPoint = ((ShapeLeaf)this.selected)
                    .getPointByPosition(pos);
            }
            this.repaint();
            this.fireEvent(this.selected);
        }
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
        this.mouse.set(newMouse);
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (this.selectedPoint != null) {
                selectedPoint.subtract(delta.times(1 / this.zoom));
                this.repaint();
            } else if (this.selected != null) {
                selected.getPosition().subtract(delta.times(1 / this.zoom));
                this.repaint();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            this.camera.add(delta.times(1 / this.zoom));
            this.repaint();
        }
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
