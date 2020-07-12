package peony;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;
import java.util.*;

/**
 * Meant to rip off the libgdx texture atlas format.
 */
public class TextureAtlas {
    /**
     * A texture atlas region which as you can see is pretty minimalistic
     * compared to the libgdx version.
     */
    public static class Region implements Comparable<Region> {
        public final Rectangle dimensions = new Rectangle();
        public final Image image;
        public final String name;

        /**
         * Creates a region and sets the stuff that must be set.
         * @param image is the image to do it with.
         * @param name  is it's name.
         */
        public Region(Image image, String name, Point pos, Point size) {
            this.image = image;
            this.name = name;
            this.dimensions.getPos().set(pos);
            this.dimensions.getSize().set(size);
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

    private final List<Image> images = new ArrayList<>();
    private final Map<String, Region> regions = new HashMap<>();

    /**
     * Creates the texture atlas by giving it the file to read from.
     * @param file is the file to load it from.
     * @throws IOException if shit fucks up.
     */
    public TextureAtlas(File file) throws IOException
    {
        Toolkit t = Toolkit.getDefaultToolkit();
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
                current = t.getImage(line);
                images.add(current);
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
                Region region = new Region(current, line, pos, size);
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
