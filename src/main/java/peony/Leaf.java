package peony;

import org.json.JSONObject;

/**
 * A thing that can be overlaid into a composition and has a form in 2d space.
 */
public abstract class Leaf implements Artefact {
    private String name = null;
    private Point position = new Point();
    private float scale = 0;
    private float rotation = 0;

    /**
     * Gives you the leaf's name.
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the leaf's name and sets it dirty.
     * @param name is the name to give it.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gives you the leaf's position which is mutable.
     * @return the position.
     */
    public Point getPosition() {
        return this.position;
    }

    /**
     * Gives you the leaf's scale which is mutable.
     * @return the scale.
     */
    public float getScale() {
        return this.scale;
    }

    /**
     * Sets the leaf's scale.
     * @param scale is the scale to set it to.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gives you the leaf's rotation.
     * @return the rotation value.
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Sets the leaf's rotation.
     * @param rotation is the rotation to give it.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Takes a point in world coordinates and tells you whether that point is
     * in or on this leaf.
     * @param point is the point.
     * @return true if it is inside and false otherwise.
     */
    public boolean inside(Point point) {
        float x = point.getX() - this.position.getX();
        float y = point.getY() - this.position.getY();
        float distance = (float)Math.sqrt(x * x + y * y);
        float angle = (float)Math.atan2(y, x) - this.rotation;
        x = (float)Math.cos(angle) * distance * this.scale;
        y = (float)Math.sin(angle) * distance * this.scale;
        return this.insideLocal(new Point(x, y));
    }

    /**
     * Takes a point in the coordinate system of this leaf (scaled, rotated and
     * moved such that this leaf is unrotated, unscaled, and at the centre of
     * the universe), and tells you if that point is inside this leaf.
     * @param point is the point.
     * @return true if it's inside and false otherwise.
     */
    public abstract boolean insideLocal(Point point);

    /**
     * Draws this leaf onto the screen.
     */
    public abstract void render();

    /**
     * Gives you the root part of the default name this leaf should have. The
     * value is dependent both on the type of the leaf, and on other things
     * for some types. The reason this does not give the entire name is that
     * it's possible two leaves might get the same base name, in which case the
     * two will need to be differentiated before the second one is added.
     * @return String the base of the name the leaf should be given
     */
    public abstract String generateBaseName();

    /**
     * Creates a leaf from json, and handles the polymorphism and that.
     * @param json is the json to turn into a leaf.
     * @return the created leaf in a result thing unless it fucks up.
     */
    public static Result<Leaf> fromJson(JSONObject json) {
        return Result.ok(new PointLeaf());
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("position", this.position.toJson());
        json.put("scale", this.scale);
        json.put("rotation", this.rotation);
        return json;
    }
}
