package peony;

public class Rectangle {
    private final Point pos;
    private final Point size;

    /**
     * Default to no size and at origin.
     */
    public Rectangle() {
        this.pos = new Point();
        this.size = new Point();
    }

    /**
     * Centred around origin with a given size.
     * @param size is the size to give it.
     */
    public Rectangle(Point size) {
        this.pos = Point.ORIGIN.minus(size.times(0.5f));
        this.size = new Point(size);
    }

    /**
     * Sets everything explicitly.
     * @param pos  is the position of the top left corner.
     * @param size is the size of each side.
     */
    public Rectangle(Point pos, Point size) {
        this.pos = pos;
        this.size = size;
    }

    /**
     * Gives you access to the position.
     * @return the position point.
     */
    public Point getPos() {
        return this.pos;
    }

    /**
     * Gives you access to the size point.
     * @return the size.
     */
    public Point getSize() {
        return this.size;
    }

    /**
     * Gives you the top right corner of this rectangle.
     * @return the position of the top right corner.
     */
    public Point getTopRightCorner() {
        return new Point(this.pos.getX() + this.size.getX(), this.pos.getY());
    }

    /**
     * Sets the rectangle to be centred around 0 with a given size.
     * @param size is the size to give to it.
     */
    public void set(Point size) {
        this.pos.set(Point.ORIGIN.minus(size.times(0.5f)));
        this.size.set(size);
    }

    /**
     * Tells you if the given point is inside this rectangle.
     * @param pos is the point to look at which must be in the same
     *            coordinate space already.
     * @return true iff it's in there.
     */
    public boolean contains(Point pos) {
        return pos.getX() >= this.pos.getX() &&
            pos.getY() >= this.pos.getY() &&
            pos.getX() < this.pos.getX() + this.size.getX() &&
            pos.getY() < this.pos.getY() + this.size.getY();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.pos, this.size);
    }
}
