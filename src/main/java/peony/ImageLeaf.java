package peony;

import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A leaf that consists of a whole image file.
 */
public class ImageLeaf extends Leaf {
    public static final String TITLE = "image";
    public static final int SELECT_RADIUS = 16;
    private File file;
    private Image image;

    /**
     * Default constructor.
     */
    public ImageLeaf() {
        super(ImageLeaf.TITLE);
    }

    /**
     * Sets the file and image that this image leaf uses.
     * @param file is the file to set.
     */
    public void setFile(File file) {
        this.file = file;
        try {
            this.image = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            this.file = null;
            this.image = null;
            return;
        }
    }

    /**
     * Creates an imageleaf from json.
     * @param json is the thingy to turn into an image leaf.
     * @return the result containing the image leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        ImageLeaf leaf = new ImageLeaf();
        try {
            if (json.has("file")) {
                File file = new File(json.getString("file"));
                leaf.setFile(file);
            }
        } catch (JSONException e) {
            return Result.fail("Invalid json for imageleaf: %s", json);
        }
        return Result.ok(leaf);
    }

    @Override
    public boolean insideLocal(Point point) {
        if (this.image == null) return point.length() < ImageLeaf.SELECT_RADIUS;
        return Renderer.getDimensions(this.image).contains(point);
    }

    @Override
    public void renderParticular(Renderer r) {
        this.normalColour(r);
        if (this.image != null) {
            r.drawImage(this.image);
            if (r.isLeafSelected(this)) {
                r.drawRectangle(Renderer.getDimensions(this.image));
            }
        }
    }

    @Override
    public String generateBaseName() {
        // TODO: should be based on filename.
        return "image";
    }

    @Override
    public JSONObject toJson(Path root) {
        JSONObject json = super.toJson(root);
        json.put("type", ImageLeaf.TITLE);
        if (this.file != null) {
            Path imagePath = this.file.toPath();
            Path relative = root.getParent().relativize(imagePath);
            json.put("file", relative.toString());
        }
        return json;
    }
}
