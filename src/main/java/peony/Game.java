package peony;

import org.json.JSONObject;

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
     * Creates a game from a json representation of one.
     * @param json is the json to create it from.
     * @return a result containing the game unless it fucks up.
     */
    public static Result<Game> fromJson(JSONObject json) {
        return Result.fail("not implmented");
    }

    private void changeEvent(Layout point) {

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

        System.out.println((String)newValue);
        System.out.println("value for path changed.");
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
