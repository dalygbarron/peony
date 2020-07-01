package peony;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meant to rip off the libgdx texture atlas format.
 */
public class TextureAtlas {
    /**
     * A texture atlas region which as you can see is pretty minimalistic
     * compared to the libgdx version.
     */
    public static class Region {
        public Image image;
        public String name;
        public int x;
        public int y;
        public int w;
        public int h;
    }

    private static final String[] tuple = new String[4];

    private final List<Image> images = new ArrayList<>();
    private final Map<String, Region> regions = new HashMap<>();

    /**
     * Creates the texture atlas by giving it the file to read from.
     * @param filename is the name of the file that we are reading.
     * @throws IOException if shit fucks up.
     * @throws FileNotFoundException if the reading from file is not found
     */
    public TextureAtlas(String filename) throws IOException,
        FileNotFoundException
    {
        Toolkit t = Toolkit.getDefaultToolkit();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
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
                Image page = t.getImage(line);
                images.add(page);
            } else {
                Region region = new Region();
                region.name = line;
                region.image = current;
                TextureAtlas.readValue(reader);
                TextureAtlas.readTuple(reader);
                region.x = Integer.parseInt(tuple[0]);
                region.y = Integer.parseInt(tuple[1]);
                TextureAtlas.readTuple(reader);
                region.w = Integer.parseInt(tuple[0]);
                region.h = Integer.parseInt(tuple[1]);
                this.regions.put(region.name, region);
                System.out.println("Adding region " + region.name);
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

    private static String readValue(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int colon = line.indexOf(':');
        if (colon == -1) {
            throw new IllegalArgumentException("Invalid atlas line: " + line);
        }
        return line.substring(colon + 1).trim();
    }

    /**
     * Reads in a tuple and stores it in a special little place for you to
     * enjoy.
     * @param reader is the reader to use to read the tuple.
     * @return the number of values read.
     */
    private static int readTuple(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int colon = line.indexOf(':');
        if (colon == -1) {
            throw new IllegalArgumentException("Invalid atlas line: " + line);
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
