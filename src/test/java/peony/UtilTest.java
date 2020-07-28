package peony;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the util class.
 */
public class UtilTest {
     @Test
    public void testValidateName() {
         assertTrue(Util.validateName("tango"));
         assertTrue(Util.validateName("TangoBarron"));
         assertTrue(Util.validateName("namrer3"));
         assertTrue(Util.validateName("idiot_world"));
         assertFalse(Util.validateName("tango is a nerd"));
         assertFalse(Util.validateName("3tango"));
         assertFalse(Util.validateName("frigger!"));
    }
}