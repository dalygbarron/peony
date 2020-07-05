package peony;

import org.json.JSONObject;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an overall game.
 */
public class Game extends Artefact implements TreeModel {
    private List<TreeModelListener> treeModelListeners = new ArrayList<>();
    private String name;
    private String version;
    private TextureAtlas textureAtlas;
    private Map<String, String> options;
    private Layout firstLayout;

    /**
     * Default constructor.
     */
    public Game() {
        this.name = "untitled";
        this.version = "1.0.0";
        this.firstLayout = new Layout();
    }

    /**
     * Gives you the game's name.
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the game name.
     * @param name is the name to give the game.
     */
    public void setName(String name) {
        this.name = name;
        this.dirty();
    }

    /**
     * Gives you the toplevel layout of the game.
     * @return the top level layout.
     */
    public Layout getFirstLayout() {
        return this.firstLayout;
    }

    /**
     * Finds a layout using a tree path.
     * @param path is the path by which to find it.
     * @return the found layout if found, otherwise null but if the path is
     *         legit that should not really happen.
     */
    public Layout getLayoutFromPath(TreePath path) {
        Object[] points = path.getPath();
        return null;
    }

    /**
     * Moves a layout around in the tree of layouts.
     * @param path   is the path to where it must get inserted.
     * @param layout is the thing to insert.
     * @param index  is the index at which to add this layout.
     */
    public void moveLayout(
        Layout from,
        TreePath path,
        Layout layout,
        int index
    ) {
        System.out.println(index);
        Layout parent = (Layout)path.getLastPathComponent();
        layout.setParent(parent);
        if (index == -1) {
            parent.getChildren().add(layout);
        } else {
            parent.getChildren().add(index, layout);
        }
        if (from == parent) {
            int i = 0;
            int kill = -1;
            for (Layout child: from.getChildren()) {
                if (child == layout && i != index) {
                    kill = i;
                    break;
                }
                i++;
            }
            if (kill >= 0) from.getChildren().remove(kill);
        } else if (from != null) {
            from.getChildren().remove(layout);
        }
        this.changeEvent(this.firstLayout);
    }

    /**
     * Creates a child of the given layout.
     * @param parent is the parent to create it under.
     */
    public void createLayout(Layout parent) {
        Layout child = parent.createChild();
        this.changeEvent(parent);
    }

    /**
     * Tries to rename a layout.
     * @param layout is the layout to rename.
     * @param name   is the name to give it.
     * @return true on success and false if there is already one with that name.
     */
    public boolean renameLayout(Layout layout, String name) {
        Layout parent = layout.getParent();
        if (parent != null) {
            Layout owner = parent.getChildByName(name);
            if (owner != null && owner != layout) {
                return false;
            }
        }
        layout.setName(name);
        this.changeEvent(layout);
        return true;
    }

    /**
     * Removes a layout from it's parent and then triggers a change event.
     * @param layout is the layout to orphan.
     */
    public void removeLayout(Layout layout) {
        Layout parent = layout.getParent();
        if (parent != null) {
            layout.setParent(null);
            parent.getChildren().remove(layout);
            this.changeEvent(parent);
        } else {
            System.out.println("retard alert");
        }
    }

    /**
     * Creates a game from a json representation of one.
     * @param json is the json to create it from.
     * @return a result containing the game unless it fucks up.
     */
    public static Result<Game> fromJson(JSONObject json) {
        return Result.fail("not implmented");
    }

    /**
     * Tells the listeners that something beautiful has happened.
     * @param point the point in the tree where the change has occurred.
     */
    private void changeEvent(Layout point) {
        TreeModelEvent event = new TreeModelEvent(
            this,
            point.getLineage()
        );
        for (TreeModelListener listener: this.treeModelListeners) {
            listener.treeStructureChanged(event);
        }
    }

    @Override
    public Object getRoot() {
        return this.firstLayout;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Layout layout = (Layout)parent;
        return layout.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Layout layout = (Layout)parent;
        return layout.getChildren().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        Layout layout = (Layout)node;
        return layout.getChildren().isEmpty();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        Layout layout = (Layout)path.getLastPathComponent();
        this.renameLayout(layout, (String)newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Layout layout = (Layout)parent;
        int i = 0;
        for (Layout childLayout: layout.getChildren()) {
            if (childLayout == child) return i;
            i++;
        }
        return -1;
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
