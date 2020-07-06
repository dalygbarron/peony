package peony;

import org.json.JSONObject;

import java.awt.Image;
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
        return Result.fail("not implemented");
    }

    @Override
    public boolean insideLocal(Point point) {
        // TODO: check the pixels and shiet.
        return true;
    }

    @Override
    public void render() {
        // TODO: some shit.
    }

    @Override
    public String generateBaseName() {
        // TODO: should be based on filename.
        return "image";
    }
}
