package peony;

import org.json.JSONObject;

/**
 * A leaf that is actually just a point. It can still be transformed and shiet
 * though because then that transformation can be used for other stuff. I guess
 * mathematically you can't really rotate or scale a point locally but
 * whatever, think of it as potential rotation energy or something.
 */
public class PointLeaf extends Leaf {
    public static final String TITLE = "point";
    public static final float SELECT_DISTANCE = 32;

    /**
     * Creates an pointleaf from json.
     * @param json is the thingy to turn into an point leaf.
     * @return the result containing the point leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        return Result.fail("not implemented");
    }

    @Override
    public boolean insideLocal(Point point) {
        return point.length() < PointLeaf.SELECT_DISTANCE;
    }

    @Override
    public void render() {
        // TODO: this.
    }

    @Override
    public String generateBaseName() {
        return "point";
    }
}
