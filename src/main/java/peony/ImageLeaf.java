package peony;

import org.json.JSONObject;

import java.awt.*;
import java.io.File;

/**
 * A leaf that consists of a whole image file.
 */
public class ImageLeaf extends Leaf {
    public static final String TITLE = "image";
    private File file;
    public Image image;

    /**
     * Creates an imageleaf from json.
     * @param json is the thingy to turn into an image leaf.
     * @return the result containing the image leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        // TODO: stuff.
        return Result.ok(new ImageLeaf());
    }

    @Override
    public boolean insideLocal(Point point) {
        // TODO: check the pixels and shiet.
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
        // TODO: should be based on filename.
        return "image";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", ImageLeaf.TITLE);
        // TODO: other image stuff.
        return json;
    }
}
