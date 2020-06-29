package peony;

/**
 * A leaf that displays a sprite from the game's spritesheet.
 */
public class SpriteLeaf extends Leaf {
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

