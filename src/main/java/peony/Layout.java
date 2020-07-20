package peony;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * One layout thingy
 */
public class Layout implements Artefact, TreeModel {
    private List<TreeModelListener> treeModelListeners = new ArrayList<>();
    private String name;
    private String script;
    private Leaf root;
    private List<Layout> children;
    private Layout parent;

    /**
     * default constructor which sets it how it should be if the program is
     * opened up without a given game.
     */
    public Layout() {
        this.name = "start";
        this.children = new ArrayList<>();
        this.root = new PointLeaf();
        this.root.setName("root");
    }

    /**
     * Creates a layout with a name and other than that default stuff.
     * @param name is the name to give it.
     */
    public Layout(String name) {
        this.name = name;
        this.children = new ArrayList<>();
        this.root = new PointLeaf();
        this.root.setName("root");
    }

    /**
     * Slightly less default constructor
     * @param name is the name to give to the layout.
     */
    public Layout(String name, Leaf root) {
        this.children = new ArrayList<>();
        this.name = name;
        this.root = root;
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
     * Gives you the layout's current script.
     * @return the script.
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the layout's script.
     * @param script is the script to give it.
     */
    public void setScript(String script) {
        this.script = script;
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

    /**
     * Moves a leaf from one place to another in the tree of leaves.
     * @param from  is the place to move it out of.
     * @param path  is the new path from the root to the leaf.
     * @param leaf  is the leaf we are moving.
     * @param index is the index to add it at the new home.
     */
    public void moveLeaf(
        Leaf from,
        TreePath path,
        Leaf leaf,
        int index
    ) {
        Leaf parent = (Leaf)path.getLastPathComponent();
        leaf.setParent(parent);
        if (index == -1) {
            parent.getChildren().add(leaf);
        } else {
            parent.getChildren().add(index, leaf);
        }
        if (from == parent) {
            int i = 0;
            int kill = -1;
            for (Leaf child: from.getChildren()) {
                if (child == leaf && i != index) {
                    kill = i;
                    break;
                }
                i++;
            }
            if (kill >= 0) from.getChildren().remove(kill);
        } else if (from != null) {
            from.getChildren().remove(leaf);
        }
        this.changed(this.root);
    }

    /**
     * When you change shit that is relevant to the leaf tree and you want
     * the world to know about it you must call this method.
     * @param leaf is the leaf that changed.
     */
    public void changed(Leaf leaf) {
        TreeModelEvent event = new TreeModelEvent(
            this,
            leaf.getLineage()
        );
        for (TreeModelListener listener: this.treeModelListeners) {
            listener.treeStructureChanged(event);
        }
    }

    /**
     * Creates a map from a json object.
     * @param json is the json object to use.
     * @param path is the path to the game file.
     * @return the map in a result unless the json is malformed in which case
     *          you are gonna get an error.
     */
    public static Result<Layout> fromJson(JSONObject json, Path path) {
        String name;
        JSONObject rootJson;
        JSONArray children;
        try {
            name = json.getString("name");
            rootJson = json.getJSONObject("root");
            children = json.getJSONArray("children");
        } catch (JSONException e) {
            return Result.fail("Invalid json for layout object.");
        }
        Result<Leaf> root = Leaf.fromJson(rootJson, path);
        if (!root.success()) return Result.fail(root.message());
        Layout layout = new Layout(name, root.value());
        if (json.has("script")) layout.setScript(json.getString("script"));
        for (int i = 0; i < children.length(); i++) {
            Result<Layout> child = Layout.fromJson(
                children.getJSONObject(i),
                path
            );
            if (child.success()) layout.addChild(child.value());
            else return Result.fail(child.message());
        }
        return Result.ok(layout);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public JSONObject toJson(Path path) {
        JSONArray children = new JSONArray();
        for (Layout child: this.getChildren()) children.put(child.toJson(path));
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("root", this.root.toJson(path));
        json.put("children", children);
        json.put("script", this.script);
        return json;
    }

    @Override
    public Object getRoot() {
        return this.root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((Leaf)parent).getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((Leaf)parent).getChildren().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        Leaf leaf = (Leaf)node;
        return leaf.getChildren().isEmpty();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        Leaf leaf = (Leaf)path.getLastPathComponent();
        Leaf parent = leaf.getParent();
        if (parent == null || parent.getChildByName((String)newValue) == null) {
            leaf.setName((String)newValue);
            this.changed(leaf);
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int index =  ((Leaf)parent).getChildren().indexOf(child);
        return index;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        this.treeModelListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        this.treeModelListeners.remove(l);
    }
}
