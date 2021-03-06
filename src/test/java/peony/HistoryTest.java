/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package peony;

import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.*;

public class HistoryTest {
    @Test
    public void testGetHistoryFromString() {
        String history = "tango@@gmail.com;erg/ergreg.json;bongo@d.png";
        Path[] parsed = History.getHistoryFromString(history);
        assertEquals("three items", 3, parsed.length);
        assertEquals("parse right", "tango@gmail.com", parsed[0].toString());
        assertEquals("parse right", "erg/ergreg.json", parsed[1].toString());
        assertEquals("parse right", "bongo;.png", parsed[2].toString());
    }

    @Test
    public void testAddToHistoryString() {
        assertEquals(
            "when it's small",
            "bongo.json;tango.csv",
            History.addToHistoryString(Path.of("bongo.json"), "tango.csv")
        );
        assertEquals(
            "Big one",
            "@@.jpg;a.csv;b@d.json;c.html",
            History.addToHistoryString(
                Path.of("@.jpg"),
                "a.csv;b@d.json;c.html;d.rtf"
            )
        );
    }

    @Test
    public void testStringUncensor() {
        String censored = "jo@@hnny@d.jpg";
        String uncensored = History.uncensorString(censored);
        assertEquals(
            "should uncensor rightly",
            "jo@hnny;.jpg",
            uncensored
        );
    }

    @Test
    public void testStringCensor() {
        String uncensored = "tango;5@gmail.com";
        String censored = History.censorString(uncensored);
        assertEquals(
            "should censor rightly",
            "tango@d5@@gmail.com",
            censored
        );
    }
}
