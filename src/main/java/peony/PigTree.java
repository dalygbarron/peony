package peony;

import javax.swing.*;

/**
 * There is a bug in the vendor code maybe?
 * I dunno but I got this fix from stack overflow.
 */
public class PigTree extends JTree {
    @Override
    protected void firePropertyChange(
        String propertyName,
        Object oldValue,
        Object newValue
    ) {
        if (newValue != null || !"dropLocation".equals(propertyName)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
