package peony;

import org.json.JSONObject;

import java.nio.file.Path;

/**
 * A leaf that displays a sprite from the game's spritesheet.
 */
public class SpriteLeaf extends Leaf {
    public static final String TITLE = "sprite";
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
    public static Result<Leaf> fromJson(JSONObject json) {
        // TODO: stuff.
        return Result.ok(new SpriteLeaf());
    }

    @Override
    public boolean insideLocal(Point point) {
        // TODO: this.
        return true;
    }

    @Override
    public void renderParticular(Renderer r) {
        this.normalColour(r);
        if (this.sprite != null) {
            r.drawImage(this.sprite.image, new Rectangle(new Point(200, 200)));
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

