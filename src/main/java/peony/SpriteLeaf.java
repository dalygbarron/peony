package peony;

import org.json.JSONObject;

import java.awt.*;

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
        // TODO: stuff.
        return Result.ok(new SpriteLeaf());
    }

    @Override
    public boolean insideLocal(Point point) {
        // TODO: this.
        return true;
    }

    @Override
    public void render(Renderer r) {
        // TODO: something.
    }

    @Override
    public String generateBaseName() {
        return "sprite";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", SpriteLeaf.TITLE);
        // TODO: other sprite stuff.
        return json;
    }
}

