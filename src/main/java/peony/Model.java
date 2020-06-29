package peony;

import java.util.List;
import java.util.ArrayList;

/**
 * The state of the data we are actually meant to be modifying with this here
 * program.
 */
public class Model extends Artefact {
    private List<Leaf> leaves = new ArrayList<>();
    private int selected = -1;
    private String name;
    private String path;

    /**
     * Gives you the currently selected leaf.
     * @return the currently selected leaf or null if there is not one.
     */
    public Leaf getSelected() {
        if (this.selected >= 0 && this.selected < this.leaves.size()) {
            return this.leaves.get(this.selected);
        }
        return null;
    }

    /**
     * Gives you the currently selected leaf index.
     * @return the index, which might be invalid if nothing is selected.
     */
    public int getSelectedIndex() {
        return this.selected;
    }

    /**
     * Sets the currently selected leaf by it's index in the list of leaves.
     * @param selected is the index of the leaf to set as the selected one.
     *                 invalid values are interpreted as meaning no selection
     *                 but since the list can grow, it's best to use a negative
     *                 number for that.
     */
    public void setSelected(int selected) {
        this.selected = selected;
    }

    /**
     * Adds a leaf to the end of the list of leaves.
     * @param leaf is the leaf to add.
     */
    public void appendLeaf(Leaf leaf) {
        this.leaves.add(leaf);
    }

    /**
     * Gives you the full list of leaves in the model.
     * @return the leaves.
     */
    public List<Leaf> getLeaves() {
        return this.leaves;
    }

    /**
     * Finds a leaf in the list of leaves that matches the given name and
     * returns it.
     * @param name is the name of the leaf to find.
     * @return the found leaf if one is found or null otherwise
     */
    public Leaf getLeafByName(String name) {
        for (Leaf leaf: this.leaves) {
            if (leaf.getName().equals(name)) return leaf;
        }
        return null;
    }

    /**
     * Gives you the name of the game.
     * @return the name of the game.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the game.
     * @param name is the name to give the game.
     */
    public void setName(String name) {
        this.name = name;
        this.dirty();
    }

    @Override
    public boolean isDirty() {
        for (Leaf leaf: this.leaves) {
            if (leaf.isDirty()) return true;
        }
        return super.isDirty();
    }
}
