package peony;

import org.json.JSONException;
import org.json.JSONObject;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an overall game.
 */
public class Game implements Artefact, TreeModel {
    private final List<TreeModelListener> treeModelListeners = new ArrayList<>();
    private String name;
    private String version;
    private TextureAtlas textureAtlas;
    private Map<String, String> options;
    private final Layout firstLayout;

    /**
     * Default constructor.
     */
    public Game() {
        this.name = "untitled";
        this.version = "1.0.0";
        this.firstLayout = new Layout();
    }

    /**
     * Creates a game by setting all it's crap.
     * @param name        is the name of the game.
     * @param version     is the game's version.
     * @param firstLayout is the top layout of the game.
     */
    public Game(
        String name,
        String version,
        Layout firstLayout,
        TextureAtlas atlas
    ) {
        this.name = name;
        this.version = version;
        this.firstLayout = firstLayout;
        this.textureAtlas = atlas;
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
    }

    /**
     * Gives you the texture atlas the game is using.
     * @return the texture atlas.
     */
    public TextureAtlas getTextureAtlas() {
        return this.textureAtlas;
    }

    /**
     * Sets the game's texture atlas.
     * @param textureAtlas is the texture atlas to set.
     */
    public void setTextureAtlas(TextureAtlas textureAtlas) {
        // TODO: this is quite likely to fuck shit up because leaves are
        //  going to have references to sprites that no longer exist.
        this.textureAtlas = textureAtlas;
    }

    /**
     * Gives you the toplevel layout of the game.
     * @return the top level layout.
     */
    public Layout getFirstLayout() {
        return this.firstLayout;
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
        parent.createChild();
        this.changeEvent(parent);
    }

    /**
     * Tries to rename a layout.
     * @param layout is the layout to rename.
     * @param name   is the name to give it.
     */
    public void renameLayout(Layout layout, String name) {
        if (!Util.validateName(name)) return;
        Layout parent = layout.getParent();
        if (parent != null) {
            Layout owner = parent.getChildByName(name);
            if (owner != null && owner != layout) {
                return;
            }
        }
        layout.setName(name);
        this.changeEvent(layout);
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

    /**
     * Recreates the game based on a json object.
     * @param json is the json object to become.
     * @return result with the game on success.
     */
    public static Result<Game> fromJson(JSONObject json, Path root) {
        TextureAtlas atlas = null;
        JSONObject atlasJson = null;
        String name;
        String version;
        JSONObject layoutJson;
        try {
            name = json.getString("name");
            version = json.getString("version");
            layoutJson = json.getJSONObject("layout");
            if (json.has("atlas")) atlasJson = json.getJSONObject("atlas");
        } catch (JSONException e) {
            return Result.fail("Invalid json for game object.");
        }
        if (atlasJson != null) {
            Result<TextureAtlas> atlasResult = TextureAtlas.fromJson(
                atlasJson,
                root
            );
            if (atlasResult.success()) atlas = atlasResult.value();
            else return Result.fail(atlasResult.message());
        }
        Result<Layout> layout = Layout.fromJson(layoutJson, root);
        if (layout.success()) {
            return Result.ok(new Game(name, version, layout.value(), atlas));
        }
        return Result.fail(layout.message());
    }

    @Override
    public JSONObject toJson(Path root) {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("version", this.version);
        json.put("layout", this.firstLayout.toJson(root));
        if (this.textureAtlas != null) {
            json.put("atlas", this.textureAtlas.toJson(root));
        }
        return json;
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
        return ((Layout)parent).getChildren().indexOf(child);
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
