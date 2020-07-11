package peony;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * Displays the visualisation of the current layout and lets you interact
 * with it to move stuff and all that.
 */
public class Window extends JPanel
    implements MouseListener, MouseWheelListener, MouseMotionListener,
    TreeModelListener
{
    public static final int NORMAL_WIDTH = 640;
    public static final int NORMAL_HEIGHT = 640;
    public static final int HALF_WIDTH = Window.NORMAL_WIDTH / 2;
    public static final int HALF_HEIGHT = Window.NORMAL_HEIGHT / 2;
    public static final float MARGIN = 50;
    public static final float MIN_ZOOM = 0.1f;
    public static final float SCALE_POWER = 0.066f;
    private final List<WindowListener> listeners = new ArrayList<>();
    private final Point mouse = new Point();
    private final Transformation camera = new Transformation(new Point(), 0, 1);
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
        int height = this.getHeight();
        if (height > 0) {
            float scale = (float)this.getHeight() /
                (Window.NORMAL_HEIGHT + Window.MARGIN);
            this.camera.getTranslation().set(
                (Window.HALF_WIDTH + Window.MARGIN / 2) * scale,
                (Window.HALF_HEIGHT + Window.MARGIN / 2) * scale
            );
            this.camera.setScale(scale);
        }
        layout.addTreeModelListener(this);
        this.repaint();
    }

    /**
     * Gives you the currently selected leaf.
     * @return the selected leaf if any.
     */
    public Leaf getSelected() {
        return this.selected;
    }

    /**
     * Sets the leaf that this window will have as the selected leaf.
     * @param leaf is the leaf to select.
     */
    public void setSelected(Leaf leaf) {
        this.selected = leaf;
    }

    /**
     * Gives you the selected point.
     * @return the selected point if any.
     */
    public Point getSelectedPoint() {
        return this.selectedPoint;
    }

    /**
     * Sets the window's selected point.
     * @param point is the point to set it to. You can set it to null if you
     *              want it to be nothing if you want man.
     */
    public void setSelectedPoint(Point point) {
        this.selectedPoint = point;
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
     * Adds a listener to the list of window listeners.
     * @param listener is the thing that listens.
     */
    public void addListener(WindowListener listener) {
        this.listeners.add(listener);
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
        if (!(g instanceof Graphics2D)) return;
        Renderer r = new Renderer(
            (Graphics2D) g,
            this.selected,
            this.selectedPoint
        );
        r.push(this.camera);
        if (this.layout != null) {
            Leaf root = (Leaf)this.layout.getRoot();
            if (root != null) root.render(r);
        }
        g.setColor(Color.BLACK);
        Point corner = new Point(
            (float)Window.NORMAL_WIDTH / -2,
            (float)Window.NORMAL_HEIGHT / -2
        );
        if (this.layout != null) {
            r.drawText(corner, layout.getFullName());
        }
        r.drawRectangle(
            corner,
            new Point(Window.NORMAL_WIDTH, Window.NORMAL_HEIGHT)
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
            Point pos = this.camera.in(this.mouse);
            Pair<Leaf, Point> hit = ((Leaf)this.layout.getRoot()).hit(pos);
            this.selected = hit.getA();
            this.selectedPoint = hit.getB();
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
        if (e.getButton() == MouseEvent.BUTTON1 && this.selected != null) {
            Transformation t = new Transformation(this.camera);
            Leaf transformer = this.selectedPoint != null ?
                this.selected : this.selected.getParent();
            Point tMouse = t.in(this.mouse);
            Point tNewMouse = t.in(newMouse);
            if (transformer != null) {
                tMouse = transformer.globalToLocal(tMouse);
                tNewMouse = transformer.globalToLocal(tNewMouse);
            }
            Point delta = tNewMouse.minus(tMouse);
            if (this.selectedPoint != null) {
                selectedPoint.add(delta);
            } else {
                selected.getTransformation().getTranslation().add(delta);
            }
            this.repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            this.camera.getTranslation().add(newMouse);
            this.camera.getTranslation().subtract(mouse);
            this.repaint();
        }
        this.mouse.set(newMouse);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouse.set(e.getX(), e.getY());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.mouse.set(e.getX(), e.getY());
        this.camera.setScale(
            this.camera.getScale() - (float)e.getPreciseWheelRotation() *
                Window.SCALE_POWER
        );
        if (this.camera.getScale() < Window.MIN_ZOOM) {
            this.camera.setScale(Window.MIN_ZOOM);
        }
        this.repaint();
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        this.repaint();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        this.repaint();
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        this.repaint();
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        this.repaint();
    }
}
