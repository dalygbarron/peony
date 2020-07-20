package peony;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Draws a leaf tree using special icons for each type of thing to be fancy.
 */
public class LeafTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";
    private final ImageIcon pointIcon;
    private final ImageIcon shapeIcon;
    private final ImageIcon imageIcon;
    private final ImageIcon spriteIcon;

    /**
     * Creates the renderer and loads some resources.
     */
    public LeafTreeCellRenderer() {
        this.pointIcon = new ImageIcon(Leaf.class.getResource(
            "/pointIcon.png")
        );
        this.shapeIcon = new ImageIcon(Leaf.class.getResource(
            "/shapeIcon.png"
        ));
        this.imageIcon = new ImageIcon(Leaf.class.getResource(
            "/imageIcon.png"
        ));
        this.spriteIcon = new ImageIcon(Leaf.class.getResource(
            "/spriteIcon.png"
        ));
    }

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean sel,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus
    ) {
        super.getTreeCellRendererComponent(
            tree,
            value,
            sel,
            expanded,
            leaf,
            row,
            hasFocus
        );
        Leaf node = (Leaf)value;
        if (node instanceof PointLeaf) {
            this.setIcon(this.pointIcon);
        } else if (node instanceof ShapeLeaf) {
            this.setIcon(this.shapeIcon);
        } else if (node instanceof ImageLeaf) {
            this.setIcon(this.imageIcon);
        } else if (node instanceof SpriteLeaf) {
            this.setIcon(this.spriteIcon);
        }
        return this;
    }
}
