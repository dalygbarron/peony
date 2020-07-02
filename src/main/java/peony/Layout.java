package peony;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * One layout thingy
 */
public class Layout extends Artefact {
    private List<Leaf> leaves;
    private String name;
    private List<Layout> children;

    /**
     * default constructor which sets it how it should be if the program is
     * opened up without a given game.
     */
    public Layout() {
        this.leaves = new ArrayList<>();
        this.name = "Some Joint";
        this.children = new ArrayList<>();
    }

    /**
     * Gives you the list of leaves.
     * @return the list of leaves.
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
     * Gives you the name of the layout.
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the layout.
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
        this.dirty();
    }

    /**
     * Gives you all the layout's children.
     * @return the children list.
     */
    public List<Layout> getChildren() {
        return this.children;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Creates a map from a json object.
     * @param json is the json object to use.
     * @return the map in a result unless the json is malformed in which case
     *          you are gonna get an error.
     */
    public static Result<Layout> fromJson(JSONObject json) {
        return Result.fail("not implemented");
    }
}
