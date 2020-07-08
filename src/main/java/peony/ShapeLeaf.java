package peony;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Graphics;
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
     * @param pos the position to look at.
     * @return the found point if any.
     */
    public Point getPointByPosition(Point pos) {
        Point local = pos.minus(this.getPosition());
        float distance = local.length();
        float angle = local.angle() - this.getRotation();
        local = Point.fromAngle(angle, distance * this.getScale());
        for (Point point: this.points) {
            if (point.minus(local).length() <= ShapeLeaf.POINT_RADIUS) {
                return point;
            }
        }
        return null;
    }

    /**
     * Creates an shapeleaf from json.
     * @param json is the thingy to turn into an shape leaf.
     * @return the result containing the shape leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        // TODO: stuff.
        return Result.ok(new ShapeLeaf());
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
    public void render(
        Graphics g,
        Point pos,
        float scale,
        boolean selected
    ) {
        super.render(g, pos, scale, selected);
        Point point = this.points.get(0);
        float firstDistance = point.length() * scale * this.getScale();
        float firstAngle = point.angle() + this.getRotation();
        Point firstPointPos = Point.fromAngle(
            firstAngle,
            firstDistance
        ).plus(pos);
        for (Point nextPoint: this.points) {
            float distance = nextPoint.length() * scale * this.getScale();
            float angle = nextPoint.angle() + this.getRotation();
            Point nextPointPos = Point.fromAngle(angle, distance).plus(pos);
            g.drawOval(
                nextPointPos.getXi() - ShapeLeaf.POINT_RADIUS,
                nextPointPos.getYi() - ShapeLeaf.POINT_RADIUS,
                ShapeLeaf.POINT_RADIUS * 2,
                ShapeLeaf.POINT_RADIUS * 2
            );
            g.drawLine(
                firstPointPos.getXi(),
                firstPointPos.getYi(),
                nextPointPos.getXi(),
                nextPointPos.getYi()
            );
            firstPointPos = nextPointPos;
            firstDistance = distance;
            firstAngle = angle;
        }
        {
            float distance = point.length() * scale * this.getScale();
            float angle = point.angle() + this.getRotation();
            Point nextPointPos = Point.fromAngle(angle, distance).plus(pos);
            g.drawLine(
                firstPointPos.getXi(),
                firstPointPos.getYi(),
                nextPointPos.getXi(),
                nextPointPos.getYi()
            );
        }
    }

    @Override
    public String generateBaseName() {
        return "shape";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", ShapeLeaf.TITLE);
        JSONArray pointList = new JSONArray();
        for (Point point: this.points) pointList.put(point.toJson());
        json.put("points", pointList);
        return json;
    }
}
