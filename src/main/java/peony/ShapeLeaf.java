package peony;

/**
 * A leaf that consists of a shape.
 */
public class ShapeLeaf extends Leaf {
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
        return "shape";
    }
}
