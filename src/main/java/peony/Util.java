package peony;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Just contains some static functions and that kind of crap.
 */
public class Util {
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
}
