package peony;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * One layout thingy
 */
public class Layout implements Artefact {
    private String name;
    private List<Leaf> leaves;
    private List<Layout> children;
    private Layout parent;

    /**
     * default constructor which sets it how it should be if the program is
     * opened up without a given game.
     */
    public Layout() {
        this.name = "start";
        this.leaves = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    /**
     * Slightly less default constructor
     * @param name is the name to give to the layout.
     */
    public Layout(String name) {
        this.name = name;
        this.leaves = new ArrayList<>();
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
     * Gives you the name of this layout and all of it's parents like a file
     * heirachy type of deal.
     * @return the full name.
     */
    public String getFullName() {
        if (this.parent == null) return "/" + this.name;
        return this.parent.getFullName() + "/" + this.name;
    }

    /**
     * Sets the name of the layout.
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gives you all the layout's children.
     * @return the children list.
     */
    public List<Layout> getChildren() {
        return this.children;
    }

    /**
     * Finds a child layout of this layout by it's name.
     * @param name is the name we are looking for.
     * @return the child layout if found or null.
     */
    public Layout getChildByName(String name) {
        for (Layout child: this.children) {
            if (child.getName().equals(name)) return child;
        }
        return null;
    }

    /**
     * Adds a child layout to this layout and sets this layout as that
     * layout's child.
     * @param child is the child to add.
     */
    public void addChild(Layout child) {
        this.children.add(child);
        child.setParent(this);
    }

    /**
     * Creates a child layout with a non clashing name.
     * @return the created child for you to use if you want.
     */
    public Layout createChild() {
        String base = "layout";
        String name = base;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            Layout existing = this.getChildByName(name);
            if (existing == null) {
                Layout child = new Layout(name);
                child.setParent(this);
                this.children.add(child);
                return child;
            }
            name = String.format("%s%d", base, i);
        }
        return null;
    }

    /**
     * Gives you this layout's parent.
     * @return the parent.
     */
    public Layout getParent() {
        return this.parent;
    }

    /**
     * Sets the parent of this layout.
     * @param parent is the parent to give it.
     */
    public void setParent(Layout parent) {
        this.parent = parent;
    }

    /**
     * Gives you the full treepath to this layout through the heirachy it
     * exists in.
     * @return the tree path.
     */
    public TreePath getLineage() {
        Deque<Layout> path = new LinkedList<>();
        Layout layout = this;
        do {
            path.addFirst(layout);
            layout = layout.getParent();
        } while (layout != null);
        return new TreePath(path.toArray());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public JSONObject toJson() {
        JSONArray leaves = new JSONArray();
        JSONArray children = new JSONArray();
        for (Layout child: this.getChildren()) children.put(child.toJson());
        for (Leaf leaf: this.getLeaves()) leaves.put(leaf.toJson());
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("leaves", leaves);
        json.put("children", children);
        return json;
    }

    /**
     * Creates a map from a json object.
     * @param json is the json object to use.
     * @return the map in a result unless the json is malformed in which case
     *          you are gonna get an error.
     */
    public static Result<Layout> fromJson(JSONObject json) {
        String name;
        JSONArray leaves;
        JSONArray children;
        try {
            name = json.getString("name");
            leaves = json.getJSONArray("leaves");
            children = json.getJSONArray("children");
        } catch (JSONException e) {
            return Result.fail("Invalid json for layout object.");
        }
        Layout layout = new Layout(name);
        for (int i = 0; i < leaves.length(); i++) {
            Result<Leaf> leaf = Leaf.fromJson(leaves.getJSONObject(i));
            if (leaf.success()) layout.getLeaves().add(leaf.value());
            else return Result.fail(leaf.message());
        }
        for (int i = 0; i < children.length(); i++) {
            Result<Layout> child = Layout.fromJson(children.getJSONObject(i));
            if (child.success()) layout.addChild(child.value());
            else return Result.fail(child.message());
        }
        return Result.ok(layout);
    }
}
