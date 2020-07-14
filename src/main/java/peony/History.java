package peony;

import java.nio.file.Path;

/**
 * Takes care of keeping track of file history.
 */
public class History {
    public static final String HISTORY = "history";
    public static final int HISTORY_LENGTH = 4;

    /**
     * Gives you in order of recency the most recently saved games.
     * @return an array of paths to the most recently saved games up to the
     *         number that is at the top of this class in length.
     */
    public static Path[] getHistory() {
        return History.getHistoryFromString(History.getHistoryString());
    }

    /**
     * Adds a path to the saved path history and if the list is already at
     * maximum length it drops one off the end.
     */
    public static void addToHistory(Path path) {
        History.setHistoryString(History.addToHistoryString(
            path,
            History.getHistoryString()
        ));
    }

    /**
     * Takes a history string and converts it into an array of paths.
     * @param history is the full history string.
     * @return the list of paths.
     */
    public static Path[] getHistoryFromString(String history) {
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
        int n = 0;
        for (int i = 0; i < history.length(); i++) {
            if (history.charAt(i) == ';') n++;
        }
        int end = history.length();
        if (n >= History.HISTORY_LENGTH - 1) {
            end = history.lastIndexOf(';');
        }
        return String.format(
            "%s;%s",
            History.censorString(path.toString()),
            history.substring(0, end)
        );
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
    private static String getHistoryString() {
        return App.PREFERENCES.get(History.HISTORY, "");
    }

    /**
     * Sets the value of the history string in the preferences store thing.
     * @param history is the string to set.
     */
    private static void setHistoryString(String history) {
        App.PREFERENCES.put(History.HISTORY, history);
    }
}
