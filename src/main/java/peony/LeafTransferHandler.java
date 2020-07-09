package peony;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Handles transferring leaves around their tree.
 */
public class LeafTransferHandler extends TransferHandler {
    /**
     * Transferable that stores a layout.
     */
    public static class LeafTransferable implements Transferable {
        private Leaf leaf;

        /**
         * Creates it by putting in it's transferable.
         * @param leaf is the leaf to put in it.
         */
        public LeafTransferable(Leaf leaf) {
            this.leaf = leaf;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {LeafTransferHandler.LEAF_FLAVOUR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(LeafTransferHandler.LEAF_FLAVOUR);
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
        {
            return this.leaf;
        }
    }

    public static final DataFlavor LEAF_FLAVOUR = new DataFlavor(
        Leaf.class,
        "Leaf"
    );
    private Layout layout;

    /**
     * Creates it and links it to the layout.
     * @param layout is the layout whose leaf tree to work on.
     */
    public LeafTransferHandler(Layout layout) {
        this.layout = layout;
    }

    @Override
    public int getSourceActions(JComponent component) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent component) {
        JTree tree = (JTree)component;
        return new LeafTransferHandler.LeafTransferable(
            (Leaf)tree.getLastSelectedPathComponent()
        );
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (
            !support.isDataFlavorSupported(LeafTransferHandler.LEAF_FLAVOUR)
        ) {
            return false;
        }
        Transferable t = support.getTransferable();
        Leaf leaf;
        try {
            leaf = (Leaf)t.getTransferData(LeafTransferHandler.LEAF_FLAVOUR);
        } catch (IOException | UnsupportedFlavorException e) {
            System.err.println("If you are reading this you are dead.");
            return false;
        }
        JTree.DropLocation location =
            (JTree.DropLocation)support.getDropLocation();
        TreePath path = location.getPath();
        for (Object node: path.getPath()) {
            if (node == leaf) return false;
        }
        return true;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) return false;
        Transferable t = support.getTransferable();
        Leaf leaf;
        try {
            leaf = (Leaf)t.getTransferData(
                LeafTransferHandler.LEAF_FLAVOUR
            );
        } catch (IOException | UnsupportedFlavorException e) {
            System.err.println("If you are reading this you are dead.");
            return false;
        }
        JTree.DropLocation location =
            (JTree.DropLocation)support.getDropLocation();
        this.layout.moveLeaf(
            leaf.getParent(),
            location.getPath(),
            leaf,
            location.getChildIndex()
        );
        return true;
    }
}
