package peony;

import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

/**
 * A leaf that consists of a whole image file.
 */
public class ImageLeaf extends Leaf implements ImageObserver {
    public static final String TITLE = "image";
    public static final int SELECT_RADIUS = 16;
    private File file;
    private Image image;
    private final Rectangle dimensions = new Rectangle();

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
            System.err.println(e);
            this.file = null;
            this.image = null;
            return;
        }
        float width = this.image.getWidth(this);
        float height = this.image.getHeight(this);
        if (width == -1) width = this.dimensions.getSize().getX();
        if (height == -1) height = this.dimensions.getSize().getY();
        this.dimensions.set(new Point(width, height));
    }

    /**
     * Creates an imageleaf from json.
     * @param json is the thingy to turn into an image leaf.
     * @return the result containing the image leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        ImageLeaf leaf = new ImageLeaf();
        try {
            File file = new File(json.getString("file"));
            leaf.setFile(file);
        } catch (JSONException e) {
            return Result.fail("Invalid json for imageleaf: %s", json);
        }
        return Result.ok(leaf);
    }

    @Override
    public boolean insideLocal(Point point) {
        if (this.image == null) return point.length() < ImageLeaf.SELECT_RADIUS;
            return this.dimensions.contains(point);
    }

    @Override
    public void renderParticular(Renderer r) {
        this.normalColour(r);
        if (this.image != null) {
            r.drawImage(this.image, this.dimensions);
        }
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
        json.put("file", this.file.toString());
        return json;
    }

    @Override
    public boolean imageUpdate(
        Image img,
        int infoflags,
        int x,
        int y,
        int width,
        int height
    ) {
        float newWidth = this.dimensions.getSize().getX();
        float newHeight = this.dimensions.getSize().getY();
        if ((infoflags & ImageObserver.WIDTH) != 0) {
            newWidth = width;
        }
        if ((infoflags & ImageObserver.HEIGHT) != 0) {
            newHeight = height;
        }
        this.dimensions.set(new Point(newWidth, newHeight));
        return false;
    }
}
