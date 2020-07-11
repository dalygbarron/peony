package peony;

import org.json.JSONObject;

import java.nio.file.Path;

/**
 * Represents anything that actually needs to be saved into the game file.
 * Originally this recorded when they were edited but then I was like who
 * cares when it was edited so now all it does is mandates a function for
 * converting to json.
 */
public interface Artefact {
    /**
     * Converts this artefact into a json object.
     * @return the created json object representing this artefact.
     */
    public JSONObject toJson(Path root);
}
