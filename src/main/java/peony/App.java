package peony;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

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
        view.setLayout(model.getSelectedLayout());
        view.setLayouts(model.getGame().getLayouts());
    	// Renaming currently selected leaf.
        view.addLeafNameListener((ActionEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            String name = view.getLeafName();
            if (name.equals("")) {
                view.displayError("Name cannot be blank");
                return;
            }
            if (model.getSelectedLayout().getLeafByName(name) != null) {
                view.displayError(String.format(
                    "The name '%s' is already in use",
                    name
                ));
                return;
            }
            leaf.setName(name);
            view.updateLeafList(model.getSelectedLeafIndex(), name);
            view.setSaveEnabled(true);
        });
        // Game properties button.
        view.addGamePropertiesListener((ActionEvent event) -> {
            view.displayError(String.format(
                "The game is called %s",
                model.getGame().getName()
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
        // Adding a new layout.
        view.addAddLayoutListener((ActionEvent event) -> {
            DefaultMutableTreeNode node = view.getSelectedMapNode();
            Object content = node.getUserObject();
            if (content instanceof Layout) {
                Layout layout = (Layout)content;
                // TODO: we need a function to create new child layouts, and
                //  we also need functions for renaming layouts within their
                //  group because we can not allow name clashes. Also, I just
                //  had a cool idea, I will make it that all levels are
                //  children of one top level level which is the first level
                //  of the game to run every time and should be the menu or
                //  whatever That means naming will always be taken care of
                //  by a member function of layout.
                layout.getChildren().add(newLayout);
            }
        });
        // Selecting a leaf in the list.
        view.addSelectLeafListener((ListSelectionEvent event) -> {
            model.setSelectedLeaf(view.getSelectedLeaf());
            view.setLeaf(model.getSelectedLeaf());
        });
        // Changing leaf position by form.
        view.addChangeXPositionListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.getPosition().setX(view.getPosition().getX());
            view.setSaveEnabled(true);
        });
        view.addChangeYPositionListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.getPosition().setY(view.getPosition().getY());
            view.setSaveEnabled(true);
        });
        // Changing leaf scale by form.
        view.addChangeScaleListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.setScale(view.getScale());
        });
        // Changing leaf rotation by form.
        view.addChangeRotationListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.setRotation(view.getRotation());
        });
        // Selecting a layout in the map list.
        view.addMapTreeListener((TreeSelectionEvent event) -> {
            DefaultMutableTreeNode node = view.getSelectedMapNode();
            Object content = node.getUserObject();
            if (content instanceof Layout) {
                model.setSelectedLayout((Layout)content);
                view.setLayout((Layout)content);
            } else {
                System.out.println("yeah nah who cares");
            }
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
            Leaf owner = model.getSelectedLayout().getLeafByName(name);
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
        model.getSelectedLayout().getLeaves().add(leaf);
        view.appendLeafList(leaf.getName());
        view.setSaveEnabled(true);
    }
}
