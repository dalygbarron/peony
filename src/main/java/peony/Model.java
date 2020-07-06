package peony;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The state of the data we are actually meant to be modifying with this here
 * program.
 */
public class Model {
    private int selectedLeaf = -1;
    private Layout selectedLayout;
    private File file = null;
    private Game game;

    /**
     * Default constructor when nothing else is going on.
     */
    Model() {
        this.game = new Game();
        this.selectedLayout = this.game.getFirstLayout();
    }

    /**
     * Gives you the currently selected leaf.
     * @return the currently selected leaf or null if there is not one.
     */
    public Leaf getSelectedLeaf() {
        if (this.selectedLeaf >= 0 &&
            this.selectedLeaf < this.selectedLayout.getLeaves().size()
        ) {
            return this.selectedLayout.getLeaves().get(this.selectedLeaf);
        }
        return null;
    }

    /**
     * Gives you the currently selected leaf index.
     * @return the index, which might be invalid if nothing is selected.
     */
    public int getSelectedLeafIndex() {
        return this.selectedLeaf;
    }

    /**
     * Sets the currently selected leaf by it's index in the list of leaves.
     * @param selected is the index of the leaf to set as the selected one.
     *                 invalid values are interpreted as meaning no selection
     *                 but since the list can grow, it's best to use a negative
     *                 number for that.
     */
    public void setSelectedLeaf(int selected) {
        this.selectedLeaf = selected;
    }

    /**
     * Gives you the currently selected layout.
     * @return the layout which should never be null.
     */
    public Layout getSelectedLayout() {
        return this.selectedLayout;
    }

    /**
     * Sets the selected layout and sets the selected leaf to nothing.
     * @param layout is the layout to set as selected.
     */
    public void setSelectedLayout(Layout layout) {
        this.selectedLayout = layout;
        this.selectedLeaf = -1;
    }

    /**
     * Gives you the model's file.
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the model's file.
	 * @param file the file to set.
	 */
	public void setFile(File file) {
		this.file = file;
	}

    /**
     * Gives you the game that this model is holding.
     * @return the game.
     */
	public Game getGame() {
	    return this.game;
    }

    /**
     * Makes the model load the game from the given file and makes it reflect
     * that instead of what it has currently.
     * @param file is the file to load from and it should not be null.
     * @return result thingy which has an error message on fail.
     */
    public Result<Void> load(File file) {
        Result<JSONObject> json = Util.readJson(file);
        if (!json.success()) return Result.fail(json.message());
        Result<Game> newGame = Game.fromJson(json.value());
        if (!newGame.success()) return Result.fail(newGame.message());
        this.game = newGame.value();
        return Result.ok();
    }

    /**
     * Saves the model to it's configured file. If it does not have a configured
     * file right now it just causes a nuisance for you instead.
     */
    public Result<Void> save() {
        if (this.file == null) return Result.fail("There is no game file.");
        try {
            FileWriter writer = new FileWriter(this.file);
            writer.write(this.game.toJson().toString());
            writer.close();
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
        return Result.ok();
    }
}
