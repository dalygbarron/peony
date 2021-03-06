package peony;

import org.json.JSONObject;

import java.awt.geom.AffineTransform;
import java.nio.file.Path;

/**
 * A leaf that displays a sprite from the game's spritesheet.
 */
public class SpriteLeaf extends Leaf {
    public static final String TITLE = "sprite";
    public static final float SELECT_RADIUS = 16;
    TextureAtlas.Region sprite;

    /**
     * Default constructor.
     */
    public SpriteLeaf() {
        super(SpriteLeaf.TITLE);
    }

    /**
     * Gives you the sprite that this leaf is currently using.
     * @return the sprite which might be null.
     */
    public TextureAtlas.Region getSprite() {
        return this.sprite;
    }

    /**
     * Sets the leaf's sprite.
     * @param sprite is the sprite to set it to.
     */
    public void setSprite(TextureAtlas.Region sprite) {
        this.sprite = sprite;
    }

    /**
     * Creates an spriteleaf from json.
     * @param json is the thingy to turn into an sprite leaf.
     * @return the result containing the sprite leaf or error.
     */
    public static Result<Leaf> fromJson(JSONObject json, Path root) {
        // TODO: This is not ideal for how it works currently. It really
        //  needs to get access to the game object or something so that it
        //  can set itself to have the sprite it wants from the game's
        //  texture atlas.
        return Result.ok(new SpriteLeaf());
    }

    @Override
    public boolean insideLocal(Point point) {
        if (this.sprite == null) {
            return point.length() < SpriteLeaf.SELECT_RADIUS;
        }
        return Renderer.getDimensions(this.sprite.image).contains(point);
    }

    @Override
    public void renderParticular(Renderer r) {
        this.normalColour(r);
        if (this.sprite != null) {
            r.drawImage(this.sprite.image);
            if (r.isLeafSelected(this)) {
                r.drawRectangle(Renderer.getDimensions(this.sprite.image));
            }
        }
    }

    @Override
    public String generateBaseName() {
        return "sprite";
    }

    @Override
    public JSONObject toJson(Path path) {
        JSONObject json = super.toJson(path);
        json.put("type", SpriteLeaf.TITLE);
        // TODO: other sprite stuff.
        return json;
    }
}

