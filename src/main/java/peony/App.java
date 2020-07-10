package peony;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.*;
import java.io.File;

public class App {
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        view.setGame(model.getGame());
        view.setLayout(model.getSelectedLayout());
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
            Leaf leaf = view.getSelectedLeaf();
            if (leaf == null) return;
            String name = view.getLeafName();
            if (name.equals("")) {
                view.displayError("Name cannot be blank");
                return;
            }
            if (true/* TODO: fix: model.getSelectedLayout().getLeafByName(name)
             != null */) {
                view.displayError(String.format(
                    "The name '%s' is already in use",
                    name
                ));
                return;
            }
            leaf.setName(name);
            // TODO: fix: view.updateLeafList(model.getSelectedLeafIndex(),
            //  name);
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
                Result<Void> result = model.load(file);
                if (!result.success()) view.displayError(result.message());
                else view.setGame(model.getGame());
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
            Result<Void> result = model.save();
            if (result.success()) view.displayError("Saved nicely.");
            else view.displayError(result.message());
        });
        // Quitting.
        view.addQuitListener((ActionEvent event) -> {
            view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
        });
        // Adding an image to the composition.
        view.addAddImageListener((ActionEvent event) -> {
            Leaf parent = model.getSelectedLeaf();
            if (parent != null) {
                parent.addChild(new ImageLeaf());
                model.getSelectedLayout().changed(parent);
            }
        });
        // Adding a sprite to the composition.
        view.addAddSpriteListener((ActionEvent event) -> {
            Leaf parent = model.getSelectedLeaf();
            if (parent != null) {
                parent.addChild(new SpriteLeaf());
                model.getSelectedLayout().changed(parent);
            }
        });
        // Adding a shape to the composition.
        view.addAddShapeListener((ActionEvent event) -> {
            Leaf parent = model.getSelectedLeaf();
            if (parent != null) {
                parent.addChild(new ShapeLeaf());
                model.getSelectedLayout().changed(parent);
            }
        });
        // Adding a point to the composition.
        view.addAddPointListener((ActionEvent event) -> {
            Leaf parent = model.getSelectedLeaf();
            if (parent != null) {
                parent.addChild(new PointLeaf());
                model.getSelectedLayout().changed(parent);
            }
        });
        // Adding a new layout.
        view.addAddLayoutListener((ActionEvent event) -> {
            Layout layout = model.getSelectedLayout();
            if (layout != null) {
                model.getGame().createLayout(layout);
            } else {
                view.displayError("No selected layout");
            }
        });
        // Changing leaf position by form.
        view.addChangeXPositionListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.getTransformation()
                .getTranslation()
                .setX(view.getPosition().getX());
            view.getWindow().repaint();
        });
        view.addChangeYPositionListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.getTransformation()
                .getTranslation()
                .setY(view.getPosition().getY());
            view.getWindow().repaint();
        });
        // Changing leaf scale by form.
        view.addChangeScaleListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.getTransformation().setScale(view.getScale());
            view.getWindow().repaint();
        });
        // Changing leaf rotation by form.
        view.addChangeRotationListener((ChangeEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.getTransformation().setRotation(view.getRotation());
            view.getWindow().repaint();
        });
        // Changing image leaf image
        view.addSelectImageListener((ActionEvent event) -> {
            File file = view.chooseImageFile();
            Leaf leaf = view.getSelectedLeaf();
            if (file != null && leaf instanceof ImageLeaf) {
                ((ImageLeaf)leaf).setFile(file);
            }
        });
        // Selecting a leaf in the list.
        view.addLeafTreeListener((TreeSelectionEvent event) -> {
            Leaf leaf = view.getSelectedLeaf();
            model.setSelectedLeaf(leaf);
            view.setLeaf(leaf);
            view.getWindow().repaint();
        });
        // Selecting a layout in the map list.
        view.addMapTreeListener((TreeSelectionEvent event) -> {
            Layout layout = view.getSelectedLayout();
            if (layout != null) {
                view.setLayout(layout);
            }
        });
        // Splitting points on a shape.
        view.addSplitPointListener((ActionEvent event) -> {
            Window window = view.getWindow();
            Leaf selected = window.getSelected();
            Point selectedPoint = window.getSelectedPoint();
            if (selectedPoint != null && selected instanceof ShapeLeaf) {
                Point newly = ((ShapeLeaf)selected).splitEdge(selectedPoint);
                if (newly != null) window.setSelectedPoint(newly);
                window.repaint();
            }
        });
        // removing points on a shape.
        view.addRemovePointListener((ActionEvent event) -> {
            Window window = view.getWindow();
            Leaf selected = window.getSelected();
            Point selectedPoint = window.getSelectedPoint();
            if (selectedPoint != null && selected instanceof ShapeLeaf) {
                Point newly = ((ShapeLeaf)selected).removePoint(selectedPoint);
                window.setSelectedPoint(newly);
                window.repaint();
            }
        });
        // recentring a shape.
        view.addRecentrePointsListener((ActionEvent event) -> {
            Window window = view.getWindow();
            Leaf selected = window.getSelected();
            if (selected instanceof ShapeLeaf) {
                ((ShapeLeaf)selected).recentre();
                window.repaint();
            }
        });
        // Window selecting a leaf.
        view.getWindow().addListener((Window window, Leaf leaf) -> {
            model.setSelectedLeaf(leaf);
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
            Leaf owner = null;// TODO: fix: model.getSelectedLayout().getLeafByName(name);
            if (owner == null) {
                leaf.setName(name);
                view.getWindow().repaint();
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
        // TODO: fix: model.getSelectedLayout().getLeaves().add(leaf);
        // TODO: fix: view.appendLeafList(leaf.getName());
        view.getWindow().repaint();
    }
}
