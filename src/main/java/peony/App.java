package peony;

import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;

public class App {
    /**
     * Start of the program.
     * @param args commandline arguments which are currently ignored completely.
     */
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
        History history = new History();
        // Game properties button.
        view.addGamePropertiesListener((ActionEvent event) -> {
            view.displayGameProperties();
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
            if (result.success()) {
                history.addToHistory(model.getFile().toPath().toAbsolutePath());
                view.displayError("Saved nicely.");
            } else {
                view.displayError(result.message());
            }
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
        // Changing leaf lock value.
        view.addChangeLockedListener((ActionEvent event) -> {
            Leaf leaf = model.getSelectedLeaf();
            if (leaf == null) return;
            leaf.setLocked(view.getLocked());
            view.getWindow().repaint();
        });
        // Changing image leaf image
        view.addSelectImageListener((ActionEvent event) -> {
            File file = view.chooseImageFile();
            Leaf leaf = model.getSelectedLeaf();
            if (file != null && leaf instanceof ImageLeaf) {
                ((ImageLeaf)leaf).setFile(file);
            }
        });
        // Changing sprite leaf sprite.
        view.addSelectSpriteListener((ActionEvent event) -> {
            TextureAtlas atlas = model.getGame().getTextureAtlas();
            Leaf leaf = model.getSelectedLeaf();
            if (atlas == null) {
                view.displayError("You need to select a texture atlas first.");
            } else if (leaf instanceof SpriteLeaf) {
                TextureAtlas.Region sprite = view.chooseSprite(atlas);
                if (sprite != null) {
                    ((SpriteLeaf)leaf).setSprite(sprite);
                }
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
                model.setSelectedLayout(layout);
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
        // Changing the game name.
        view.addGameNameListener((ActionEvent event) -> {
            model.getGame().setName(view.getGameName());
        });
        // Changing the game texture atlas.
        view.addGameAtlasListener((ActionEvent event) -> {
            File atlasFile = view.chooseAtlasFile();
            if (atlasFile != null) {
                try {
                    model.getGame().setTextureAtlas(new TextureAtlas(atlasFile));
                } catch (Exception e) {
                    view.displayError(String.format(
                        "Error loading texture atlas: %s",
                        e.getMessage()
                    ));
                }
            }
        });
        // Editing the script.
        view.addScriptListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                Document document = event.getDocument();
                try {
                    model.getSelectedLayout().setScript(document.getText(
                        0,
                        document.getLength()
                    ));
                } catch (BadLocationException e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                Document document = event.getDocument();
                try {
                    model.getSelectedLayout().setScript(document.getText(
                        0,
                        document.getLength()
                    ));
                } catch (BadLocationException e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                Document document = event.getDocument();
                try {
                    model.getSelectedLayout().setScript(document.getText(
                        0,
                        document.getLength()
                    ));
                } catch (BadLocationException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
        // Window selecting a leaf.
        view.getWindow().addListener((Window window, Leaf leaf) -> {
            model.setSelectedLeaf(leaf);
            view.setLeaf(leaf);
        });
        // History selecting.
        for (Path path: history.getHistory()) {
            view.addRecentButtonAndListen(path, (ActionEvent event) -> {
                Result<Void> result = model.load(path.toFile());
                if (!result.success()) view.displayError(result.message());
                else view.setGame(model.getGame());
            });
        }
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
