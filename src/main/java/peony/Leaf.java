package peony;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.tree.TreePath;
import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * A thing that can be overlaid into a composition and has a form in 2d space.
 */
public abstract class Leaf implements Artefact {
    private final List<Leaf> children = new ArrayList<>();
    private Transformation transformation = new Transformation();
    private String name;
    private Leaf parent = null;

    /**
     * Leaves should never be unnamed.
     * @param name the name to start it off with.
     */
    public Leaf(String name) {
        this.name = name;
    }

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
     * Gives you the leaf's transformation.
     * @return the transformation.
     */
    public Transformation getTransformation() {
        return this.transformation;
    }

    /**
     * Gives you the leaf's parent.
     * @return the parent if any.
     */
    public Leaf getParent() {
        return this.parent;
    }

    /**
     * Sets the leaf's parent in the lheirachy.
     * @param parent is the leaf to make the parent.
     */
    public void setParent(Leaf parent) {
        this.parent = parent;
    }

    /**
     * Gives you access to the child list.
     * @return the list of children.
     */
    public List<Leaf> getChildren() {
        return this.children;
    }

    /**
     * Finds the child with the specified name if there is one.
     * @param name is the name to look for.
     * @return the found thingy.
     */
    public Leaf getChildByName(String name) {
        for (Leaf leaf: this.children) {
            if (leaf.getName().equals(name)) return leaf;
        }
        return null;
    }

    /**
     * Adds a child node to the leaf, and makes sure that it's name is unique.
     * @param child is the leaf to add.
     */
    public void addChild(Leaf child) {
        String test = child.getName();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (this.getChildByName(test) == null) break;
            test = String.format("%s%d", child.getName(), i);
        }
        child.setName(test);
        child.setParent(this);
        this.children.add(child);
    }

    /**
     * Gives you the full treepath to this leaf through the heirachy it
     * exists in.
     * @return the tree path.
     */
    public TreePath getLineage() {
        Deque<Leaf> path = new LinkedList<>();
        Leaf leaf = this;
        do {
            path.addFirst(leaf);
            leaf = leaf.getParent();
        } while (leaf != null);
        return new TreePath(path.toArray());
    }

    /**
     * Takes a point from the world space and converts it into this leaf's
     * coordinate space by getting all of this leaf's ancestors to transform
     * it in sequence.
     * @param in is the world space point which is not harmed in this process.
     * @return the leaf space point.
     */
    public Point globalToLocal(Point in) {
        if (this.parent != null) {
            return this.transformation.in(this.parent.globalToLocal(in));
        }
        return this.transformation.in(in);
    }

    /**
     * Tells you if this leaf or a child of it contains the given point,
     * favouring leaves that are lower in the heirachy.
     * @param point is the point to look at.
     * @return the lowest node fulfilling these conditions.
     */
    public Pair<Leaf, Point> hit(Point point) {
        Point t = this.transformation.in(point);
        for (Leaf child: this.children) {
            Pair<Leaf, Point> found = child.hit(t);
            if (found.getA() != null) return found;
        }
        if (this.insideLocal(t)) return new Pair<>(this, null);
        return new Pair<>(null, null);
    }

    /**
     * Makes the renderer start drawingin the normal selected or not colours.
     * @param r is the renderer to make start doing that.
     */
    public void normalColour(Renderer r) {
        r.setColour(r.isLeafSelected(this) ? Color.BLUE : Color.BLACK);
    }

    /**
     * Does generic rendering stuff that all leaves do.
     * @param r is the renderer to use.
     */
    public final void render(Renderer r) {
        r.push(this.transformation);
        this.renderParticular(r);
        this.normalColour(r);
        r.drawText(Point.ORIGIN, this.name);
        for (Leaf child: this.children) {
            r.drawDottedLine(
                Point.ORIGIN,
                child.getTransformation().getTranslation()
            );
        }
        for (Leaf child: this.children) child.render(r);
        r.pop();
    }

    /**
     * Does the rendering stuff that is unique to a certain type of leaf.
     * @param r is the renderer.
     */
    public abstract void renderParticular(Renderer r);

    /**
     * Takes a point in the coordinate system of this leaf (scaled, rotated and
     * moved such that this leaf is unrotated, unscaled, and at the centre of
     * the universe), and tells you if that point is inside this leaf.
     * @param point is the point.
     * @return true if it's inside and false otherwise.
     */
    public abstract boolean insideLocal(Point point);

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
        String type;
        String name;
        JSONObject transformation;
        float scale;
        float rotation;
        try {
            type = json.getString("type");
            name = json.getString("name");
            transformation = json.getJSONObject("transformation");
        } catch (JSONException e) {
            return Result.fail(String.format(
                "Invalid json for leaf object: %s",
                json
            ));
        }
        Result<Transformation> transformationResult =
            Transformation.fromJson(transformation);
        if (!transformationResult.success()) {
            return Result.fail(transformationResult.message());
        }
        Result<Leaf> leaf;
        switch (type) {
            case PointLeaf.TITLE:
                leaf = PointLeaf.fromJson(json);
                break;
            case ShapeLeaf.TITLE:
                leaf = ShapeLeaf.fromJson(json);
                break;
            case SpriteLeaf.TITLE:
                leaf = SpriteLeaf.fromJson(json);
                break;
            case ImageLeaf.TITLE:
                leaf = ImageLeaf.fromJson(json);
                break;
            default:
                return Result.fail(String.format(
                    "Invalid leaf type: %s",
                    type
                ));
        }
        if (leaf.success()) {
            Leaf actualLeaf = leaf.value();
            actualLeaf.name = name;
            actualLeaf.transformation = transformationResult.value();
        }
        return leaf;
    }

    @Override
    public JSONObject toJson(Path root) {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("transformation", this.transformation.toJson(root));
        JSONArray children = new JSONArray();
        for (Leaf child: this.children) children.put(child.toJson(root));
        json.put("children", children);
        return json;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
