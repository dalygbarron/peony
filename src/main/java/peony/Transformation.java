package peony;

import org.json.JSONObject;

/**
 * Represents a transformation into or out of a coordinate space.
 */
public class Transformation implements Artefact {
    private final Point translation;
    private float rotation;
    private float scale;

    /**
     * Default constructor.
     */
    public Transformation() {
        this.translation = new Point();
        this.rotation = 0;
        this.scale = 1;
    }

    /**
     * Creates a transformation by providing the things that make one up.
     * @param translation is the the relative translation.
     * @param rotation    is the rotation is the relative rotation.
     * @param scale       is the scale relative scale.
     */
    public Transformation(Point translation, float rotation, float scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Gives you access to the translation.
     * @return the translation.
     */
    public Point getTranslation() {
        return this.translation;
    }

    /**
     * Gives you the amount of rotation in the transformation.
     * @return the rotation.
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Sets the rotation in the transformation.
     * @param rotation the rotation.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Gives you the relative scale of this coordinate space.
     * @return the scale.
     */
    public float getScale() {
        return this.scale;
    }

    /**
     * Sets the coordinate space's relative scale.
     * @param scale the scale.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Takes a point outside of this coordinate space and converts into it.
     * @param point is the point to convert which is not affected.
     * @return the version inside this coordinate space.
     */
    public Point in(Point point) {
        Point newPoint = point.minus(this.translation);
        return Point.fromAngle(
            newPoint.angle() + this.rotation,
            newPoint.length() / this.scale
        );
    }

    /**
     * Takes a point in the coordinate space of this transformation and
     * converts it out of it.
     * @param point is the point to convert which is not harmed.
     * @return the version outside of this coordinate space.
     */
    public Point out(Point point) {
        return Point.fromAngle(
            point.angle() - this.rotation,
            point.length() * this.scale
        ).plus(this.translation);
    }

    /**
     * Creates a transformation from json.
     * @param json the json to make into the transformation.
     * @return the transformation created or an error message.
     */
    public static Result<Transformation> fromJson(JSONObject json) {
        // TODO: this.
        return Result.fail("later");
    }

    @Override
    public JSONObject toJson() {
        // TODO: this.
        return null;
    }

    @Override
    public String toString() {
        return String.format(
            "(%s, %f, %f)",
            this.translation,
            this.rotation,
            this.scale
        );
    }
}
