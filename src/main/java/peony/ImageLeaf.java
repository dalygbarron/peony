package peony;

import java.awt.Image;

/**
 * A leaf that consists of a whole image file.
 */
public class ImageLeaf extends Leaf {
    public Image image = null;

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
