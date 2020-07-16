package peony;

import java.nio.file.Path;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Takes care of keeping track of file history.
 */
public class History {
    public static final String HISTORY = "history";
    public static final int HISTORY_LENGTH = 4;
    public static final String PACKAGE = "com/liquidpig/peony/History";
    private final Preferences preferences;

    /**
     * Creates a history object and opens a connection to the preferences
     * thing which it uses to save stuff.
     */
    public History() {
        this.preferences = Preferences.userRoot().node(History.PACKAGE);
    }

    /**
     * Gives you in order of recency the most recently saved games.
     * @return an array of paths to the most recently saved games up to the
     *         number that is at the top of this class in length.
     */
    public Path[] getHistory() {
        return History.getHistoryFromString(this.getHistoryString());
    }

    /**
     * Adds a path to the saved path history and if the list is already at
     * maximum length it drops one off the end.
     */
    public void addToHistory(Path path) {
        this.setHistoryString(History.addToHistoryString(
            path,
            this.getHistoryString()
        ));
    }

    /**
     * Takes a history string and converts it into an array of paths.
     * @param history is the full history string.
     * @return the list of paths.
     */
    public static Path[] getHistoryFromString(String history) {
        if (history.length() == 0) return new Path[0];
        String[] parts = history.split(";");
        Path[] paths = new Path[parts.length];
        for (int i = 0; i < parts.length; i++) {
            paths[i] = Path.of(History.uncensorString(parts[i]));
        }
        return paths;
    }

    /**
     * Adds a new path onto the start of a history string.
     * @param path    is the path to add.
     * @param history is the existing string.
     * @return the new string with the new path on the start and potentially
     *         one taken off the end.
     */
    public static String addToHistoryString(Path path, String history) {
        String censored = History.censorString(path.toString());
        if (history.contains(censored)) return history;
        int n = 0;
        for (int i = 0; i < history.length(); i++) {
            if (history.charAt(i) == ';') n++;
        }
        int end = history.length();
        if (n >= History.HISTORY_LENGTH - 1) {
            end = history.lastIndexOf(';');
        }
        return String.format("%s;%s", censored, history.substring(0, end));
    }

    /**
     * Turns @ into @@ and ; into @d so that we can delimit filenames with a
     * ; character.
     * @param string is the string to censor.
     * @return the censored version.
     */
    public static String censorString(String string) {
        return string.replaceAll("@", "@@").replaceAll(";", "@d");
    }

    /**
     * Turns @d into ; and @@ into @. The opposite of the censor function.
     * @param string is the string to uncensor.
     * @return the uncensored version.
     */
    public static String uncensorString(String string) {
        return string.replaceAll("@d", ";").replaceAll("@@", "@");
    }

    /**
     * Loads the history string from the preferences store thingo.
     * @return the found string or an empty string if there is not one.
     */
    private String getHistoryString() {
        return this.preferences.get(History.HISTORY, "");
    }

    /**
     * Sets the value of the history string in the preferences store thing.
     * @param history is the string to set.
     */
    private void setHistoryString(String history) {
        this.preferences.put(History.HISTORY, history);
        try {
            this.preferences.sync();
        } catch (BackingStoreException e) {
            System.err.println(e.getMessage());
        }
    }
}
