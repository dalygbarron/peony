package peony;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an overall game.
 */
public class Game extends Artefact {
    private String name;
    private String version;
    private TextureAtlas textureAtlas;
    private Map<String, String> options;
    private List<Layout> layouts;

    /**
     * Default constructor.
     */
    public Game() {
        this.name = "untitled";
        this.version = "1.0.0";
        this.layouts = new ArrayList<>();
        this.layouts.add(new Layout());
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
     * Gives you the game's list of layouts.
     * @return the list.
     */
    public List<Layout> getLayouts() {
        return this.layouts;
    }

    /**
     * Creates a game from a json representation of one.
     * @param json is the json to create it from.
     * @return a result containing the game unless it fucks up.
     */
    public static Result<Game> fromJson(JSONObject json) {
        return Result.fail("not implmented");
    }
}
