package peony;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
    private Point middle = new Point();

    /**
     * Sets the file and image that this image leaf uses.
     * @param file is the file to set.
     */
    public void setFile(File file) {
        this.file = file;
        try {
            this.image = ImageIO.read(file);
        } catch (IOException e) {
            this.file = null;
            this.image = null;
            return;
        }
        int width = this.image.getWidth(this);
        int height = this.image.getHeight(this);
        if (width != -1) this.middle.setX((float)width / 2);
        if (height != -1) this.middle.setY((float)height / 2);
    }

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
        if (this.image == null) return point.length() < ImageLeaf.SELECT_RADIUS;
        return point.getX() >= 0 && point.getX() < this.middle.getX() * 2 &&
            point.getY() >= 0 && point.getY() < this.middle.getY() * 2;
    }

    @Override
    public void render(
        Graphics g,
        Point pos,
        float scale,
        boolean selected
    ) {
        super.render(g, pos, scale, selected);
        if (this.image != null) {
            AffineTransform tx = AffineTransform.getRotateInstance(
                this.getRotation(),
                pos.getX() + this.middle.getX() * scale,
                pos.getY() + this.middle.getY() * scale
            );
            tx.translate(pos.getX(), pos.getY());
            tx.scale(scale, scale);
            ((Graphics2D)g).drawImage(this.image, tx, null);
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
        // TODO: other image stuff.
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
        if ((infoflags & ImageObserver.WIDTH) != 0) {
            this.middle.setX((float)width / 2);
        }
        if ((infoflags & ImageObserver.HEIGHT) != 0) {
            this.middle.setY((float)height / 2);
        }
        return false;
    }
}
