package peony;

import org.json.JSONObject;

/**
 * A leaf that displays a sprite from the game's spritesheet.
 */
public class SpriteLeaf extends Leaf {
    public static final String TITLE = "sprite";

    /**
     * Creates an spriteleaf from json.
     * @param json is the thingy to turn into an sprite leaf.
     * @return the result containing the sprite leaf or error.
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
        return "sprite";
    }
}

