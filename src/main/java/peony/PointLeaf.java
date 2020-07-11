package peony;

import org.json.JSONObject;

import java.awt.*;

/**
 * A leaf that is actually just a point. It can still be transformed and shiet
 * though because then that transformation can be used for other stuff. I guess
 * mathematically you can't really rotate or scale a point locally but
 * whatever, think of it as potential rotation energy or something.
 */
public class PointLeaf extends Leaf {
    public static final String TITLE = "point";
    public static final float SELECT_DISTANCE = 16;
    public static final float POINT_SIZE = 10;

    /**
     * Default constructor.
     */
    public PointLeaf() {
        super(PointLeaf.TITLE);
    }

    /**
     * Creates an pointleaf from json.
     * @param json is the thingy to turn into an point leaf.
     * @return the result containing the point leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        return Result.ok(new PointLeaf());
    }

    @Override
    public boolean insideLocal(Point point) {
        return point.length() < PointLeaf.SELECT_DISTANCE;
    }

    @Override
    public void renderParticular(Renderer r) {
        Point top = new Point(0, -PointLeaf.POINT_SIZE);
        Point bottom = new Point(0, PointLeaf.POINT_SIZE);
        Point left = new Point(-PointLeaf.POINT_SIZE, 0);
        Point right = new Point(PointLeaf.POINT_SIZE, 0);
        this.normalColour(r);
        r.drawLine(top, bottom);
        r.drawLine(left, right);
    }

    @Override
    public String generateBaseName() {
        return "point";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", PointLeaf.TITLE);
        return json;
    }
}
