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
    public static final float POINT_SIZE = 20;

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
    public void render(
        Graphics g,
        Point pos,
        float scale,
        boolean selected
    ) {
        super.render(g, pos, scale, selected);
        Point top = Point.fromAngle(
            this.getRotation(),
            PointLeaf.POINT_SIZE / 2
        ).plus(pos);
        Point right = Point.fromAngle(
            (float)(Math.PI / 2) + this.getRotation(),
            PointLeaf.POINT_SIZE / 2
        ).plus(pos);
        Point bottom = Point.fromAngle(
            (float)Math.PI + this.getRotation(),
            PointLeaf.POINT_SIZE / 2
        ).plus(pos);
        Point left = Point.fromAngle(
            (float)(Math.PI * 3 / 2) + this.getRotation(),
            PointLeaf.POINT_SIZE / 2
        ).plus(pos);
        g.drawLine(top.getXi(), top.getYi(), bottom.getXi(), bottom.getYi());
        g.drawLine(left.getXi(), left.getYi(), right.getXi(), right.getYi());
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
