package peony;

import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Meant to rip off the libgdx texture atlas format.
 */
public class TextureAtlas implements Artefact {
    /**
     * A texture atlas region which as you can see is pretty minimalistic
     * compared to the libgdx version.
     */
    public static class Region implements Comparable<Region> {
        public final Image image;
        public final String name;

        /**
         * Creates a region and sets the stuff that must be set.
         * @param image is the image to do it with.
         * @param name  is it's name.
         */
        public Region(Image image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public int compareTo(Region o) {
            if (this.name == null) return -1;
            if (o.name == null) return 1;
            return this.name.compareTo(o.name);
        }
    }

    private static final String[] tuple = new String[4];
    private final Path source;
    private final Map<String, Region> regions = new HashMap<>();

    /**
     * Creates the texture atlas by giving it the file to read from.
     * @param file is the file to load it from.
     * @throws IOException if shit fucks up.
     */
    public TextureAtlas(File file) throws IOException
    {
        this.source = file.toPath();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Image current = null;
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            if (line.trim().length() == 0) {
                current = null;
            } else if (current == null) {
                if (TextureAtlas.readTuple(reader) == 2) {
                    TextureAtlas.readTuple(reader);
                }
                TextureAtlas.readTuple(reader);
                TextureAtlas.readValue(reader);
                Result<Image> loadCurrent = TextureAtlas.loadImage(
                    file.toPath(),
                    Path.of(line)
                );
                if (loadCurrent.success()) current = loadCurrent.value();
            } else {
                TextureAtlas.readValue(reader);
                TextureAtlas.readTuple(reader);
                Point pos = new Point(
                    Integer.parseInt(tuple[0]),
                    Integer.parseInt(tuple[1])
                );
                TextureAtlas.readTuple(reader);
                Point size = new Point(
                    Integer.parseInt(tuple[0]),
                    Integer.parseInt(tuple[1])
                );
                Image portion = TextureAtlas.subImage(
                    current,
                    new Rectangle(pos, size)
                );
                Region region = new Region(portion, line);
                this.regions.put(region.name, region);
                if (TextureAtlas.readTuple(reader) == 4) {
                    if (TextureAtlas.readTuple(reader) == 4) {
                        TextureAtlas.readTuple(reader);
                    }
                }
                TextureAtlas.readTuple(reader);
                TextureAtlas.readValue(reader);
            }
        }
    }

    /**
     * Gives you a region by name.
     * @param name is the name of the region to find.
     * @return the region or null if none by that name exists.
     */
    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    /**
     * Gets all of the regions in the atlas as a collection.
     * @return the collection of regions.
     */
    public Collection<Region> getRegions() {
        return this.regions.values();
    }

    /**
     * Creates a new image using a part of an existing image.
     * @param full is the image to get the part out of.
     * @param part is the part to get from the full image.
     * @return the partial image as a new image.
     */
    public static Image subImage(Image full, Rectangle part) {
        BufferedImage newImage = new BufferedImage(
            part.getSize().getXi(),
            part.getSize().getYi(),
            BufferedImage.TYPE_4BYTE_ABGR
        );
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(
            full,
            0,
            0,
            part.getSize().getXi(),
            part.getSize().getYi(),
            part.getPos().getXi(),
            part.getPos().getYi(),
            part.getPos().getXi() + part.getSize().getXi(),
            part.getPos().getYi() + part.getSize().getYi(),
            null
        );
        return newImage;
    }

    /**
     * Loads an image with a filename that is relative to another file.
     * @param root is the place where the first file is.
     * @param file is where the file to open is relative to the first file.
     * @return a result with the loaded file if it could be got.
     */
    public static Result<Image> loadImage(Path root, Path file) {
        File relative = root.getParent().resolve(file).toFile();
        try {
            return Result.ok(ImageIO.read(relative));
        } catch (IOException e) {
            return Result.fail(String.format(
                "Texture atlas image failed to load: %s",
                e.getMessage()
            ));
        }
    }

    /**
     * Loads a texture atlas from json.
     * @param json is the json to laod from.
     * @param root is the location of the game file.
     * @return a result with the atlas in it if it worked.
     */
    public static Result<TextureAtlas> fromJson(JSONObject json, Path root) {
        String path;
        try {
            path = json.getString("path");
        } catch (JSONException e) {
            return Result.fail(String.format(
                "Invalid json for texture atlas: %s",
                e.getMessage()
            ));
        }
        try {
            Path filePath = root.resolve(Path.of(path));
            return Result.ok(new TextureAtlas(filePath.toFile()));
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public JSONObject toJson(Path root) {
        JSONObject json = new JSONObject();
        Path relative = root.getParent().relativize(this.source);
        json.put("path", relative.toString());
        return json;
    }

    /**
     * Reads a single value line from a texture atlas
     * @param reader is the thingy that is doing the reading from the point
     *               that it is currently up to.
     * @return the read value.
     * @throws IOException if the reading fails.
     */
    private static String readValue(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int colon = line.indexOf(':');
        if (colon == -1) {
            throw new IllegalArgumentException(String.format(
                "Invalid atlas value: '%s'",
                line
            ));
        }
        return line.substring(colon + 1).trim();
    }

    /**
     * Reads in a tuple and stores it in a special little place for you to
     * enjoy.
     * @param reader is the reader to use to read the tuple.
     * @return the number of values read.
     * @throws IOException if the read fails.
     */
    private static int readTuple(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int colon = line.indexOf(':');
        if (colon == -1) {
            throw new IllegalArgumentException(String.format(
                "Invalid atlas tuple: '%s'",
                line
            ));
        }
        int i;
        int lastMatch = colon + 1;
        for (i = 0; i < 3; i++) {
            int comma = line.indexOf(',', lastMatch);
            if (comma == -1) break;
            tuple[i] = line.substring(lastMatch, comma).trim();
            lastMatch = comma + 1;
        }
        tuple[i] = line.substring(lastMatch).trim();
        return i + 1;
    }
}
