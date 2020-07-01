package peony;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

public class App {
    public static void main(String[] args) {
        View view = new View();
        Model model = new Model();
        App.controller(view, model);
    }
    
    /**
     * Sets up the controller's listeners and stuff.
     * @param view  is the view object which renders all the stuff and receives user input.
     * @param model is the actual data stuff that is 
     */
    private static void controller(View view, Model model) {
    	// Renaming currently selected leaf.
        view.addLeafNameListener((ActionEvent event) -> {
            Leaf leaf = model.getSelected();
            if (leaf == null) return;
            String name = view.getLeafName();
            if (name.equals("")) {
                view.displayError("Name cannot be blank");
                return;
            }
            if (model.getLeafByName(name) != null) {
                view.displayError(String.format(
                    "The name '%s' is already in use",
                    name
                ));
                return;
            }
            leaf.setName(name);
            view.updateLeafList(model.getSelectedIndex(), name);
            view.setSaveEnabled(true);
        });
        // Game properties button.
        view.addGamePropertiesListener((ActionEvent event) -> {
            view.displayError(String.format(
                "The game is called %s",
                model.getName()
            ));
        });
        // Loading.
        view.addLoadListener((ActionEvent event) -> {
            File file = view.chooseGameFile();
            if (file != null) {
                try {
                    model.load(file);
                } catch (IllegalArgumentException e) {
                    view.displayError(
                        "Loaded game not viable: " + e.getMessage()
                    );
                }
            }
        });
        // Saving.
        view.addSaveListener((ActionEvent event) -> {
            File file = model.getFile();
            if (file == null) {
                file = view.chooseGameFile();
                if (file == null) return;
                model.setFile(file);
            }
            // TODO: this is where the actual saving stuff is done.
            view.displayError("It's saving man, I swear");
            view.setSaveEnabled(false);
        });
        // Quitting.
        view.addQuitListener((ActionEvent event) -> {
            view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
        });
        // Adding an image to the composition.
        view.addAddImageListener((ActionEvent event) -> {
            App.addLeaf(view, model, new ImageLeaf());
        });
        // Adding a sprite to the composition.
        view.addAddSpriteListener((ActionEvent event) -> {
            App.addLeaf(view, model, new SpriteLeaf());
        });
        // Adding a shape to the composition.
        view.addAddShapeListener((ActionEvent event) -> {
            App.addLeaf(view, model, new ShapeLeaf());
        });
        // Adding a point to the composition.
        view.addAddPointListener((ActionEvent event) -> {
            App.addLeaf(view, model, new PointLeaf());
        });
        // Selecting a leaf in the list.
        view.addSelectLeafListener((ListSelectionEvent event) -> {
            model.setSelected(view.getSelectedLeaf());
            view.setLeaf(model.getSelected());
        });
        // Changing leaf position by form.
        view.addChangeXPositionListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelected();
            if (leaf == null) return;
            leaf.getPosition().setX(view.getPosition().getX());
            view.setSaveEnabled(true);
        });
        view.addChangeYPositionListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelected();
            if (leaf == null) return;
            leaf.getPosition().setY(view.getPosition().getY());
            view.setSaveEnabled(true);
        });
        // Changing leaf scale by form.
        view.addChangeXScaleListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelected();
            if (leaf == null) return;
            leaf.getScale().setX(view.getScale().getX());
        });
        view.addChangeYScaleListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelected();
            if (leaf == null) return;
            leaf.getScale().setY(view.getScale().getY());
        });
        // Changing leaf rotation by form.
        view.addChangeRotationListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelected();
            if (leaf == null) return;
            leaf.setRotation(view.getRotation());
        });
    	view.setVisible(true);
    }
    
    /**
     * Gives a leaf a name.
     * @param view is the app view.
     * @param model is the app model.
     * @param leaf is the leaf to give a name to.
     */
    private static void nameLeaf(View view, Model model, Leaf leaf) {
    	String base = leaf.generateBaseName();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            String name = String.format("%s%d", base, i);
            Leaf owner = model.getLeafByName(name);
            if (owner == null) {
                leaf.setName(name);
                return;
            }
        }
        view.displayError("You have enough leaves already I think");
    }
    
    /**
     * Performs the required functionality in adding a leaf both to the view and model.
     * @param view is the app view.
     * @param model is the app model.
     * @param leaf is the leaf to add.
     */
    private static void addLeaf(View view, Model model, Leaf leaf) {
    	App.nameLeaf(view, model, leaf);
        model.appendLeaf(leaf);
        view.appendLeafList(leaf.getName());
        view.setSaveEnabled(true);
    }
}
