package peony;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A leaf that consists of a shape.
 */
public class ShapeLeaf extends Leaf {
    public static final String TITLE = "shape";
    public static final float DEFAULT_RADIUS = 64;
    public static final int POINT_RADIUS = 5;
    public static final int MIN_POINTS = 3;
    private final List<Point> points = new ArrayList<>();

    /**
     * Creates a default shape.
     */
    public ShapeLeaf() {
        super(ShapeLeaf.TITLE);
        float increment = (float)Math.PI * 2 / ShapeLeaf.MIN_POINTS;
        for (int i = 0; i < ShapeLeaf.MIN_POINTS; i++) {
            this.points.add(Point.fromAngle(
                i * increment,
                ShapeLeaf.DEFAULT_RADIUS
            ));
        }
    }

    /**
     * Gives you a point in the shape that the given location hits if any.
     * @param pos the position to look at which should be in our coordinate
     *            space.
     * @return the found point if any.
     */
    public Point getPointByPosition(Point pos) {
        for (Point point: this.points) {
            if (point.minus(pos).length() <= ShapeLeaf.POINT_RADIUS) {
                return point;
            }
        }
        return null;
    }

    /**
     * Removes the given point from this shape.
     * @param point is the point to remove.
     * @return the previous point in the shape unless the given point could
     *         not be removed for whatever reason in which case null is
     *         returned.
     */
    public Point removePoint(Point point) {
        int n = this.points.size();
        if (n <= ShapeLeaf.MIN_POINTS) return null;
        int kill = -1;
        for (int i = 0; i < n; i++) {
            if (this.points.get(i) == point) {
                kill = i;
                break;
            }
        }
        if (kill != -1) {
            this.points.remove(kill);
            return this.points.get(kill == 0 ? n - 2 : kill - 1);
        }
        return null;
    }

    /**
     * Splits the edge after the given point by inserting a new point after
     * it that is half way between it and the next one.
     * @param point the point to add a node after.
     * @return the new node or null if you gave a point not in this shape.
     */
    public Point splitEdge(Point point) {
        int n = this.points.size();
        int index = -1;
        for (int i = 0; i < n; i++) {
            if (this.points.get(i) == point) {
                index = i;
                break;
            }
        }
        if (index == -1) return null;
        Point next = this.points.get(index == n - 1 ? 0 : index + 1);
        Point mid = point.plus(next).times(0.5f);
        this.points.add(index + 1, mid);
        return mid;
    }

    /**
     * Moves all the nodes then moves the leaf itself so it's nodes are in
     * the same spot but the centre of the leaf is in the middle of the nodes.
     */
    public void recentre() {
        Point mid = new Point();
        for (Point point: this.points) {
            mid.add(point);
        }
        mid.multiply(1.0f / this.points.size());
        this.getTransformation().getTranslation().add(mid);
        for (Point point: this.points) {
            point.subtract(mid);
        }
    }

    /**
     * Creates an shapeleaf from json.
     * @param json is the thingy to turn into an shape leaf.
     * @param root is the location of the gmae file.
     * @return the result containing the shape leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json, Path root) {
        // TODO: stuff.
        return Result.ok(new ShapeLeaf());
    }

    @Override
    public Pair<Leaf, Point> hit(Point point) {
        Point t = this.getTransformation().in(point);
        for (Leaf child: this.getChildren()) {
            Pair<Leaf, Point> found = child.hit(t);
            if (found.getA() != null) return found;
        }
        if (this.insideLocal(t) && !this.getLocked()) {
            return new Pair<>(this, this.getPointByPosition(t));
        }
        return new Pair<>(null, null);
    }

    @Override
    public boolean insideLocal(Point point) {
        int hits = 0;
        for (int i = 0; i < this.points.size(); i++) {
            Point current = this.points.get(i);
            if (current.minus(point).length() < ShapeLeaf.POINT_RADIUS) {
                return true;
            }
            Point next = this.points.get(
                i == this.points.size() - 1 ? 0 : i + 1
            );
            boolean intersect = ((current.getY() > point.getY()) !=
                (next.getY() > point.getY())) &&
                (point.getX() < (next.getX() - current.getX()) *
                (point.getY() - current.getY()) /
                (next.getY() - current.getY()) +
                    current.getX());
            if (intersect) hits++;
        }
        return hits % 2 == 1;
    }

    @Override
    public void renderParticular(Renderer r) {
        if (this.getLocked()) {
            r.setColour(r.isLeafSelected(this) ? Color.MAGENTA : Color.RED);
        }
        for (int i = 0; i < this.points.size(); i++) {
            Point point = this.points.get(i);
            Point next = this.points.get(
                i == this.points.size() - 1 ? 0 : i + 1
            );
            if (!this.getLocked()) {
                if (r.isPointSelected(point)) {
                    r.setColour(Color.GREEN);
                } else if (r.isLeafSelected(this)) {
                    r.setColour(Color.BLUE);
                } else {
                    r.setColour(Color.BLACK);
                }
                r.drawCircle(point, ShapeLeaf.POINT_RADIUS);
            }
            r.drawLine(point, next);
        }
    }

    @Override
    public String generateBaseName() {
        return "shape";
    }

    @Override
    public JSONObject toJson(Path path) {
        JSONObject json = super.toJson(path);
        json.put("type", ShapeLeaf.TITLE);
        JSONArray pointList = new JSONArray();
        for (Point point: this.points) pointList.put(point.toJson(path));
        json.put("points", pointList);
        return json;
    }
}
