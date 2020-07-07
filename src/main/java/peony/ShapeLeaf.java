package peony;

import org.json.JSONObject;

import java.awt.*;

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
        // TODO: stuff.
        return Result.ok(new ShapeLeaf());
    }

    @Override
    public boolean insideLocal(Point point) {
        // TODO: this.
        return true;
    }

    @Override
    public void render(
        Graphics g,
        Point pos,
        float scale,
        boolean selected
    ) {
        super.render(g, pos, scale, selected);
    }

    @Override
    public String generateBaseName() {
        return "shape";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", ShapeLeaf.TITLE);
        // TODO: other shape stuff.
        return json;
    }
}
