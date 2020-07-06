package peony;

import org.json.JSONObject;

/**
 * Represents a point or vector or whatever in 2d space.
 */
public class Point implements Artefact {
    private float x;
    private float y;

    /**
     * Creates a point at the origin.
     */
    public Point() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Creates a point that has the same x and y values.
     * @param value is the value to give to both.
     */
    public Point(float value) {
        this.x = value;
        this.y = value;
    }

    /**
     * Creates a point by fully specifying it.
     * @param x is the x component.
     * @param y is the y component.
     */
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gives you the point's x component.
     * @return the x component.
     */
    public float getX() {
        return this.x;
    }

    /**
     * Sets the point's x component to some value.
     * @param x is the value to set it to.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gives you the point's y component.
     * @return the y component.
     */
    public float getY() {
        return this.y;
    }

    /**
     * Sets the point's y component to some value.
     * @param y is the value to set it to.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Sets the point to be the same as some other point.
     * @param point is the point to make this point like.
     */
    public void set(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    /**
     * Sets the point fully in one go.
     * @param x is the x value to give it.
     * @param y is the y value to give it.
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gives you the distance from the location (0, 0) to this point.
     * @return the distance.
     */
    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Converts a json object to a point unless it fucks up.
     * @param json is the object to convert.
     * @return a result that has the created object if the input was valid.
     */
    public static Result<Point> fromJson(JSONObject json) {
        return Result.fail("Not implemented it yet.");
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("x", this.x);
        json.put("y", this.y);
        return json;
    }
}
