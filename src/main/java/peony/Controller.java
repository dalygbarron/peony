package peony;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import java.io.File;

/**
 * Does the logic stuff in the program.
 */
public class Controller {
    private final View view;
    private final Model model;

    /**
     * Injects it's dependencies.
     * @param view  is the view with which stuff is displayed.
     * @param model is the model which we are manipulating.
     */
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        // Renaming currently selected leaf.
        this.view.addLeafNameListener((ActionEvent event) -> {
            Leaf leaf = this.model.getSelected();
            if (leaf == null) return;
            String name = this.view.getLeafName();
            if (name.equals("")) {
                this.view.displayError("Name cannot be blank");
                return;
            }
            if (this.model.getLeafByName(name) != null) {
                this.view.displayError(String.format(
                    "The name '%s' is already in use",
                    name
                ));
                return;
            }
            leaf.setName(name);
            this.view.updateLeafList(this.model.getSelectedIndex(), name);
            this.view.setSaveEnabled(true);
        });
        // Game properties button.
        this.view.addGamePropertiesListener((ActionEvent event) -> {
            this.view.displayError(String.format(
                "The game is called %s",
                this.model.getName()
            ));
        });
        // Loading.
        this.view.addLoadListener((ActionEvent event) -> {
            File file = this.view.chooseGameFile();
            if (file != null) {
                try {
                    this.model.load(file);
                } catch (IllegalArgumentException e) {
                    this.view.displayError(
                        "Loaded game not viable: " + e.getMessage()
                    );
                }
            }
        });
        // Saving.
        this.view.addSaveListener((ActionEvent event) -> {
            File file = this.model.getFile();
            if (file == null) {
                file = this.view.chooseGameFile();
                if (file == null) return;
                model.setFile(file);
            }
            // TODO: this is where the actual saving stuff is done.
            this.view.displayError("It's saving man, I swear");
            this.view.setSaveEnabled(false);
        });
        // Quitting.
        this.view.addQuitListener((ActionEvent event) -> {
            this.view.dispatchEvent(new WindowEvent(this.view, WindowEvent.WINDOW_CLOSING));
        });
        // Adding an image to the composition.
        this.view.addAddImageListener((ActionEvent event) -> {
            this.addLeaf(new ImageLeaf());
        });
        // Adding a sprite to the composition.
        this.view.addAddSpriteListener((ActionEvent event) -> {
            this.addLeaf(new SpriteLeaf());
        });
        // Adding a shape to the composition.
        this.view.addAddShapeListener((ActionEvent event) -> {
            this.addLeaf(new ShapeLeaf());
        });
        // Adding a point to the composition.
        this.view.addAddPointListener((ActionEvent event) -> {
            this.addLeaf(new PointLeaf());
        });
        // Selecting a leaf in the list.
        this.view.addSelectLeafListener((ListSelectionEvent event) -> {
            this.model.setSelected(this.view.getSelectedLeaf());
            this.view.setLeaf(this.model.getSelected());
        });
        // Changing leaf position by form.
        this.view.addChangeXPositionListener((ChangeEvent event) -> {
            Leaf leaf = this.model.getSelected();
            if (leaf == null) return;
            leaf.getPosition().setX(this.view.getPosition().getX());
            this.view.setSaveEnabled(true);
        });
        this.view.addChangeYPositionListener((ChangeEvent event) -> {
            Leaf leaf = this.model.getSelected();
            if (leaf == null) return;
            leaf.getPosition().setY(this.view.getPosition().getY());
            this.view.setSaveEnabled(true);
        });
        // Changing leaf scale by form.
        this.view.addChangeXScaleListener((ChangeEvent event) -> {
            Leaf leaf = this.model.getSelected();
            if (leaf == null) return;
            leaf.getScale().setX(this.view.getScale().getX());
        });
        this.view.addChangeYScaleListener((ChangeEvent event) -> {
            Leaf leaf = this.model.getSelected();
            if (leaf == null) return;
            leaf.getScale().setY(this.view.getScale().getY());
        });
        // Changing leaf rotation by form.
        this.view.addChangeRotationListener((ChangeEvent event) -> {
            Leaf leaf = this.model.getSelected();
            if (leaf == null) return;
            leaf.setRotation(this.view.getRotation());
        });
    }

    /**
     * Gives a name to a leaf that no other leaf in the model has.
     * @param leaf is the leaf to give a name to.
     */
    private void nameLeaf(Leaf leaf) {
        String base = leaf.generateBaseName();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            String name = String.format("%s%d", base, i);
            Leaf owner = this.model.getLeafByName(name);
            if (owner == null) {
                leaf.setName(name);
                return;
            }
        }
        this.view.displayError("You have enough leaves already I think");
    }


    /**
     * Goes through all the motions of adding a new leaf to the view and the
     * model.
     * @param leaf is the leaf to add to everything.
     */
    private void addLeaf(Leaf leaf) {
        this.nameLeaf(leaf);
        this.model.appendLeaf(leaf);
        this.view.appendLeafList(leaf.getName());
        this.view.setSaveEnabled(true);
    }
}
