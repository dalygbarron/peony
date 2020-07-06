package peony;

import org.json.JSONObject;

/**
 * A leaf that consists of a shape.
 */
public class ShapeLeaf extends Leaf {
    public static final String TITLE = "shape";

    /**
     * Creates an shapeleaf from json.
     * @param json is the thingy to turn into an shape leaf.
     * @return the result containing the shape leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        return Result.fail("not implemented");
    }

    @Override
    public boolean insideLocal(Point point) {
        // TODO: this.
        return true;
    }

    @Override
    public void render() {
        // TODO: this.
    }

    @Override
    public String generateBaseName() {
        return "shape";
    }
}
