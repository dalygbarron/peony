package peony;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Takes care of transferring layouts around the game layout heirachy and the
 * visual incarnation of that.
 */
public class LayoutTransferHandler extends TransferHandler {
    /**
     * Transferable that stores a layout.
     */
    public static class LayoutTransferable implements Transferable {
        private Layout layout;

        /**
         * Creates it by putting in it's transferable.
         * @param layout is the layout to put in it.
         */
        public LayoutTransferable(Layout layout) {
            this.layout = layout;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {LayoutTransferHandler.LAYOUT_FLAVOUR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(LayoutTransferHandler.LAYOUT_FLAVOUR);
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
        {
            return this.layout;
        }
    }

    public static final DataFlavor LAYOUT_FLAVOUR = new DataFlavor(
        Layout.class,
        "Layout"
    );
    private Game game;

    /**
     * Creates it and links it to the game.
     * @param game is the game whose layout tree to use it on.
     */
    public LayoutTransferHandler(Game game) {
        this.game = game;
    }

    @Override
    public int getSourceActions(JComponent component) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent component) {
        JTree tree = (JTree)component;
        return new LayoutTransferable(
            (Layout)tree.getLastSelectedPathComponent()
        );
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (
            !support.isDataFlavorSupported(LayoutTransferHandler.LAYOUT_FLAVOUR)
        ) {
            return false;
        }
        Transferable t = support.getTransferable();
        Layout layout;
        try {
            layout = (Layout)t.getTransferData(
                LayoutTransferHandler.LAYOUT_FLAVOUR
            );
        } catch (IOException | UnsupportedFlavorException e) {
            System.out.println("If you are reading this you are dead.");
            return false;
        }
        JTree.DropLocation location =
            (JTree.DropLocation)support.getDropLocation();
        TreePath path = location.getPath();
        for (Object node: path.getPath()) {
            if (node == layout) return false;
        }
        return true;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) return false;
        Transferable t = support.getTransferable();
        Layout layout;
        try {
            layout = (Layout)t.getTransferData(
                LayoutTransferHandler.LAYOUT_FLAVOUR
            );
        } catch (IOException | UnsupportedFlavorException e) {
            System.out.println("If you are reading this you are dead.");
            return false;
        }
        JTree.DropLocation location =
            (JTree.DropLocation)support.getDropLocation();
        this.game.moveLayout(
            layout.getParent(),
            location.getPath(),
            layout,
            location.getChildIndex()
        );
        return true;
    }
}
