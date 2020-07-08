package peony;

/**
 * Listens for when the window wants to do things.
 */
public interface WindowListener {
    /**
     * Called when a leaf has been selected by the window.
     * @param window is the window that the leaf was selected in.
     * @param leaf   is the leaf that has been selected.
     */
    public void leafSelected(Window window, Leaf leaf);
}
