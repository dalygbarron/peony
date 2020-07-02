package peony;

/**
 * Represents anything that actually needs to be saved into the game file, and
 * thus needs to record when it has been edited.
 */
public class Artefact {
    private boolean dirty = true;

    /**
     * Tells you if this artefact has been changed since it was last saved. The
     * default implementation is just to return 
     * @return true if this artefact or any of it's dependents are dirty.
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * Sets this artefact as dirty.
     */
    protected void dirty() {
        this.dirty = true;
    }
}
