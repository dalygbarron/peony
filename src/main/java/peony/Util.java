package peony;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Just contains some static functions and that kind of crap.
 */
public class Util {
    private static Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z][0-9A-Za-z_]*$"
    );

    /**
     * Just reads the entire content of a file into a string.
     * @param file is the file to read from.
     * @return the content of the file or null if the file could not be opened.
     */
    public static Result<String> readFile(File file) {
        try {
            return Result.ok(new Scanner(file).useDelimiter("\\Z").next());
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        } catch (NoSuchElementException e) {
            return Result.fail("File is empty.");
        }
    }

    /**
     * Reads in a file as a json object.
     * @param file is the file to read.
     * @return a result object that contains the json object unless there was a
     *         problem.
     */
    public static Result<JSONObject> readJson(File file) {
        Result<String> content = Util.readFile(file);
        if (!content.success()) return Result.fail(content.message());
        return Result.ok(new JSONObject(content.value()));
    }

    /**
     * Tells you if the given string is valid as a name for a leaf or layout.
     * @param name is the name which must conform.
     * @return true if the name is all good and false if not. Sadly it cannot
     *         tell you why it fails because it does it with a regex.
     */
    public static boolean validateName(String name) {
        return Util.NAME_PATTERN.matcher(name).matches();
    }
}
