package peony;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * Manages the program's user interface.
 */
public class View extends JFrame {
    private final PigTree leafTree = new PigTree();
    private final PigTree mapTree = new PigTree();
    private final JButton seeAllButton = View.makeIconButton(
        "/seeAllIcon.png",
        "See the whole layout"
    );
    private final JButton centreViewButton = View.makeIconButton(
        "/centreViewIcon.png",
        "Centre leaf in view"
    );
    private final JButton rotateButton = View.makeIconButton(
        "/rotateIcon.png",
        "Interactively rotate the selected leaf"
    );
    private final JButton scaleButton = View.makeIconButton(
        "/scaleIcon.png",
        "Interactively scale the selected leaf"
    );
    private final JButton removeButton = View.makeIconButton(
        "/removeIcon.png",
        "Remove the selected leaf"
    );
    private final JButton splitButton = View.makeIconButton(
        "/splitIcon.png",
        "Split the currently selected edge"
    );
    private final JButton removeNodeButton = View.makeIconButton(
        "/removeNodeIcon.png",
        "Remove the currently selected node from the shape"
    );
    private final JButton recentreButton = View.makeIconButton(
        "/recentreIcon.png",
        "Make the centre of the shape be it's origin without moving anything"
    );
    private final JButton previousNodeButton = View.makeIconButton(
        "/leftIcon.png",
        "Select the previous node in the shape"
    );
    private final JButton nextNodeButton = View.makeIconButton(
        "/rightIcon.png",
        "Select the next node in the shape"
    );
    private final JToolBar toolbar = new JToolBar("Leaf Tools");
    private final JToolBar shapeToolbar = new JToolBar("Shape Tools");
    private final JSplitPane verticalSplit;
    private final JFileChooser imageChooser = new JFileChooser();
    private final JFileChooser gameChooser = new JFileChooser();
    private final JFileChooser atlasChooser = new JFileChooser();
    private final JPanel leafPropertiesPanel = new JPanel();
    private final JPanel leafMainPropertiesPanel = new JPanel(new GridLayout(0, 2));
    private final JPanel leafImagePropertiesPanel = new JPanel(new GridLayout(0, 2));
    private final JPanel leafSpritePropertiesPanel = new JPanel(new GridLayout(0, 2));
    private final JPanel leafShapePropertiesPanel = new JPanel(new GridLayout(0, 2));
    private final JMenuItem gamePropertiesButton = new JMenuItem("Game Properties");
    private final JMenu recentFilesButton = new JMenu("Recent Files");
    private final JMenuItem loadButton = new JMenuItem("Load");
    private final JMenuItem saveButton = new JMenuItem("Save");
    private final JMenuItem quitButton = new JMenuItem("Exit");
    private final JMenuItem addImageButton = new JMenuItem("Image");
    private final JMenuItem addSpriteButton = new JMenuItem("Sprite");
    private final JMenuItem addShapeButton = new JMenuItem("Shape");
    private final JMenuItem addPointButton = new JMenuItem("Point");
    private final JMenuItem addLayoutButton = new JMenuItem("Layout");
    private final SpinnerNumberModel xPositionModel = View.makePositionModel();
    private final SpinnerNumberModel yPositionModel = View.makePositionModel();
    private final SpinnerNumberModel scaleModel = View.makeScaleModel();
    private final SpinnerNumberModel rotationModel = View.makeRotationModel();
    private final JSpinner xPosition = new JSpinner(this.xPositionModel);
    private final JSpinner yPosition = new JSpinner(this.yPositionModel);
    private final JSpinner scale = new JSpinner(this.scaleModel);
    private final JSpinner rotation = new JSpinner(this.rotationModel);
    private final JCheckBox locked = new JCheckBox();
    private final JButton sprite = new JButton("Select Sprite");
    private final JButton image = new JButton("Select Image");
    private final JButton splitPointButton = new JButton("Split");
    private final JButton removePointButton = new JButton("Remove");
    private final JButton recentrePointsButton = new JButton("Recentre");
    private final Window window = new Window();
    private final RSyntaxTextArea script = new RSyntaxTextArea(20, 60);
    private final JTextField gameName = new JTextField(10);
    private final JButton gameAtlas = new JButton("Select Atlas");
    private final JPanel gamePanel = new JPanel(new GridLayout(0, 2));
    private final JDialog gameDialog = new JDialog(
        this,
        "Game Properties",
        true
    );

    /**
     * Creates and sets up the view.
     */
    public View() {
        this.setTitle("Peony");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(640, 480);
        this.script.setMarginLineEnabled(true);
        this.script.setSyntaxEditingStyle(
            SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT
        );
        this.toolbar.add(this.seeAllButton);
        this.toolbar.add(this.centreViewButton);
        this.toolbar.add(this.rotateButton);
        this.toolbar.add(this.scaleButton);
        this.toolbar.add(this.removeButton);
        this.shapeToolbar.add(this.splitButton);
        this.shapeToolbar.add(this.removeNodeButton);
        this.shapeToolbar.add(this.recentreButton);
        this.shapeToolbar.add(this.previousNodeButton);
        this.shapeToolbar.add(this.nextNodeButton);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(this.recentFilesButton);
        fileMenu.add(this.loadButton);
        fileMenu.add(this.saveButton);
        fileMenu.add(this.gamePropertiesButton);
        fileMenu.add(this.quitButton);
        JMenu addMenu = new JMenu("Add");
        addMenu.add(this.addImageButton);
        addMenu.add(this.addSpriteButton);
        addMenu.add(this.addShapeButton);
        addMenu.add(this.addPointButton);
        addMenu.add(this.addLayoutButton);
        menuBar.add(fileMenu);
        menuBar.add(addMenu);
        menuBar.add(this.toolbar);
        menuBar.add(this.shapeToolbar);
        this.atlasChooser.setFileFilter(new FileNameExtensionFilter(
            "LibGDX texture atlas files",
            "atlas"
        ));
        this.atlasChooser.setAcceptAllFileFilterUsed(false);
        this.gameChooser.setFileFilter(new FileNameExtensionFilter(
            "Readable game files",
            "json"
        ));
        this.gameChooser.setAcceptAllFileFilterUsed(false);
        this.imageChooser.setFileFilter(new FileNameExtensionFilter(
            "Usable image files",
            "png"
        ));
        this.imageChooser.setAcceptAllFileFilterUsed(false);
        JTabbedPane propertiesTabs = new JTabbedPane();
        this.leafPropertiesPanel.setLayout(
            new BoxLayout(leafPropertiesPanel, BoxLayout.Y_AXIS)
        );
        this.leafMainPropertiesPanel.add(new JLabel("X Position"));
        this.leafMainPropertiesPanel.add(this.xPosition);
        this.leafMainPropertiesPanel.add(new JLabel("Y Position"));
        this.leafMainPropertiesPanel.add(this.yPosition);
        this.leafMainPropertiesPanel.add(new JLabel("Scale"));
        this.leafMainPropertiesPanel.add(this.scale);
        this.leafMainPropertiesPanel.add(new JLabel("Rotation"));
        this.leafMainPropertiesPanel.add(this.rotation);
        this.leafMainPropertiesPanel.add(new JLabel("Lock"));
        this.leafMainPropertiesPanel.add(this.locked);
        this.leafImagePropertiesPanel.add(new JLabel("Image"));
        this.leafImagePropertiesPanel.add(this.image);
        this.leafSpritePropertiesPanel.add(new JLabel("Sprite"));
        this.leafSpritePropertiesPanel.add(this.sprite);
        this.splitPointButton.setMnemonic(KeyEvent.VK_ENTER);
        this.removePointButton.setMnemonic(KeyEvent.VK_BACK_SPACE);
        this.leafShapePropertiesPanel.add(this.splitPointButton);
        this.leafShapePropertiesPanel.add(this.removePointButton);
        this.leafShapePropertiesPanel.add(this.recentrePointsButton);
        this.leafImagePropertiesPanel.setVisible(false);
        this.leafSpritePropertiesPanel.setVisible(false);
        this.leafShapePropertiesPanel.setVisible(false);
        this.leafPropertiesPanel.add(this.leafMainPropertiesPanel);
        this.leafPropertiesPanel.add(this.leafImagePropertiesPanel);
        this.leafPropertiesPanel.add(this.leafSpritePropertiesPanel);
        this.leafPropertiesPanel.add(this.leafShapePropertiesPanel);
        JPanel mapPropertiesPanel = new JPanel(new GridLayout(0, 2));
        mapPropertiesPanel.add(new JLabel("Name"));
        this.mapTree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION
        );
        this.mapTree.setEditable(true);
        this.mapTree.setDragEnabled(true);
        this.mapTree.setDropMode(DropMode.ON_OR_INSERT);
        propertiesTabs.addTab("Leaf", leafPropertiesPanel);
        propertiesTabs.addTab("Layout", mapPropertiesPanel);
        this.leafTree.setCellRenderer(new LeafTreeCellRenderer());
        this.leafTree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION
        );
        this.leafTree.setEditable(true);
        this.leafTree.setDragEnabled(true);
        this.leafTree.setDropMode(DropMode.ON_OR_INSERT);
        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab("Layout", this.window);
        RTextScrollPane scriptPane = new RTextScrollPane(this.script);
        mainTabs.addTab("Script", scriptPane);
        JTabbedPane listTabs = new JTabbedPane();
        listTabs.addTab("Leaves", new JScrollPane(this.leafTree));
        listTabs.addTab("Layouts", new JScrollPane(this.mapTree));
        this.verticalSplit = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            propertiesTabs,
            listTabs
        );
        JSplitPane horizontalSplit = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            this.verticalSplit,
            mainTabs
        );
        this.add(horizontalSplit);
        this.setJMenuBar(menuBar);
        this.gamePanel.add(new JLabel("Game Name"));
        this.gamePanel.add(this.gameName);
        this.gamePanel.add(new JLabel("Texture Atlas"));
        this.gamePanel.add(this.gameAtlas);
        this.gameDialog.getContentPane().add(this.gamePanel);
    }

    /**
     * Sets the game that the view is reflecting.
     * @param game is the game.
     */
    public void setGame(Game game) {
        this.mapTree.setTransferHandler(new LayoutTransferHandler(game));
        this.mapTree.setModel(game);
        this.gameName.setText(game.getName());
        this.setLayout((Layout)game.getRoot());
    }

    /**
     * Gives you the currently selected leaf.
     * @return the currently selected leaf if any.
     */
    public Leaf getSelectedLeaf() {
        return (Leaf)leafTree.getLastSelectedPathComponent();
    }

    /**
     * Gives you the currently selected node in the map tree thingy.
     * @return the currently selected node if any.
     */
    public Layout getSelectedLayout() {
        return (Layout)mapTree.getLastSelectedPathComponent();
    }

    /**
     * Gives you the full position that is currently set on the leaf properties
     * thingy as a point.
     * @return the point.
     */
    public Point getPosition() {
        return new Point(
            this.xPositionModel.getNumber().floatValue(),
            this.yPositionModel.getNumber().floatValue()
        );
    }

    /**
     * Sets the currently configured leaf position.
     * @param position is the position.
     */
    public void setPosition(Point position) {
        this.xPositionModel.setValue(position.getX());
        this.yPositionModel.setValue(position.getY());
    }

    /**
     * Gives you the currently configured leaf scale.
     * @return the scale.
     */
    public float getScale() {
        return this.scaleModel.getNumber().floatValue();
    }

    /**
     * Sets the currently configured leaf scale.
     * @param scale is the scale to set it to.
     */
    public void setScale(float scale) {
        this.scaleModel.setValue(scale);
    }

    /**
     * Gives you the currently configured leaf rotation.
     * @return the rotation.
     */
    public float getRotation() {
        return this.rotationModel.getNumber().floatValue();
    }

    /**
     * Sets the currently configured leaf rotation.
     * @param rotation is the rotation to set.
     */
    public void setRotation(float rotation) {
        this.rotationModel.setValue(rotation);
    }

    /**
     * Tells you if the locked checkbox is set rn.
     * @return the status of the locked checkbox.
     */
    public boolean getLocked() {
        return this.locked.isSelected();
    }

    /**
     * Sets the value of the locked checkbox.
     * @param locked is the value to set it to.
     */
    public void setLocked(boolean locked) {
        this.locked.setSelected(locked);
    }

    /**
     * Tells you the name in the game name box.
     * @return the name.
     */
    public String getGameName() {
        return this.gameName.getText();
    }

    /**
     * Sets all of the leaf related stuff based on an actual leaf.
     * @param leaf is the leaf to set stuff based on.
     */
    public void setLeaf(Leaf leaf) {
        this.window.setSelected(leaf);
        if (leaf == null) {
            this.leafTree.clearSelection();
            this.setPosition(new Point());
            this.setScale(0);
            this.setRotation(0);
            this.setLocked(false);
            this.leafImagePropertiesPanel.setVisible(false);
            this.leafSpritePropertiesPanel.setVisible(false);
            this.leafShapePropertiesPanel.setVisible(false);
        } else {
            TreePath path = this.leafTree.getSelectionPath();
            if (path != null) {
                Leaf current = (Leaf)path.getLastPathComponent();
                if (leaf != current) {
                    this.leafTree.setSelectionPath(leaf.getLineage());
                }
            } else {
                this.leafTree.setSelectionPath(leaf.getLineage());
            }
            this.setPosition(leaf.getTransformation().getTranslation());
            this.setScale(leaf.getTransformation().getScale());
            this.setRotation(leaf.getTransformation().getRotation());
            this.setLocked(leaf.getLocked());
            if (leaf instanceof ImageLeaf) {
                this.leafImagePropertiesPanel.setVisible(true);
                this.leafSpritePropertiesPanel.setVisible(false);
                this.leafShapePropertiesPanel.setVisible(false);
                this.verticalSplit.resetToPreferredSizes();
            } else if (leaf instanceof SpriteLeaf) {
                this.leafImagePropertiesPanel.setVisible(false);
                this.leafSpritePropertiesPanel.setVisible(true);
                this.leafShapePropertiesPanel.setVisible(false);
                this.verticalSplit.resetToPreferredSizes();
            } else if (leaf instanceof ShapeLeaf) {
                this.leafImagePropertiesPanel.setVisible(false);
                this.leafSpritePropertiesPanel.setVisible(false);
                this.leafShapePropertiesPanel.setVisible(true);
                this.verticalSplit.resetToPreferredSizes();
            } else {
                this.leafImagePropertiesPanel.setVisible(false);
                this.leafSpritePropertiesPanel.setVisible(false);
                this.leafShapePropertiesPanel.setVisible(false);
            }
        }
    }

    /**
     * Recreates the leaf list and stuff for a new layout selection.
     * @param layout is the layout selected.
     */
    public void setLayout(Layout layout) {
        this.leafTree.setModel(layout);
        this.leafTree.setTransferHandler(new LeafTransferHandler(layout));
        this.leafTree.revalidate();
        this.window.setLayout(layout);
        this.setTitle(layout.getFullName());
        this.script.setText(layout.getScript());
    }

    /**
     * Gives you access to the drawing window.
     * @return the window.
     */
    public Window getWindow() {
        return this.window;
    }

    /**
     * Adds a listener to find out when an item is chosen in the map tree.
     * @param listener is the listener to add.
     */
    public void addMapTreeListener(TreeSelectionListener listener) {
        this.mapTree.addTreeSelectionListener(listener);
    }

    /**
     * Adds a listener to find out when an item is chosen in the leaf tree.
     * @param listener is the one who listens.
     */
    public void addLeafTreeListener(TreeSelectionListener listener) {
        this.leafTree.addTreeSelectionListener(listener);
    }

    /**
     * Adds an action listener to the game properties button.
     * @param listener is the listener to add.
     */
    public void addGamePropertiesListener(ActionListener listener) {
        this.gamePropertiesButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the load button.
     * @param listener is the listener to add.
     */
    public void addLoadListener(ActionListener listener) {
        this.loadButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the save button.
     * @param listener is the listener to add.
     */
    public void addSaveListener(ActionListener listener) {
        this.saveButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the quit button.
     * @param listener is the listener to add.
     */
    public void addQuitListener(ActionListener listener) {
        this.quitButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the add image button.
     * @param listener is the listener to add.
     */
    public void addAddImageListener(ActionListener listener) {
        this.addImageButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the add sprite button.
     * @param listener is the listener to add.
     */
    public void addAddSpriteListener(ActionListener listener) {
        this.addSpriteButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the add shape button.
     * @param listener is the listener to add.
     */
    public void addAddShapeListener(ActionListener listener) {
        this.addShapeButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the add point button.
     * @param listener is the listener to add.
     */
    public void addAddPointListener(ActionListener listener) {
        this.addPointButton.addActionListener(listener);
    }

    /**
     * Adds a listener to listen to the add layout button.
     * @param listener is the listener to add.
     */
    public void addAddLayoutListener(ActionListener listener) {
        this.addLayoutButton.addActionListener(listener);
    }

    /**
     * Adds an action listener to the remove leaf button.
     * @param listener is the listener to add.
     */
    public void addRemoveListener(ActionListener listener) {
        this.removeButton.addActionListener(listener);
    }

    /**
     * Adds a listener onto the leaf x position spinner.
     * @param listener is the listener.
     */
    public void addChangeXPositionListener(ChangeListener listener) {
        this.xPosition.addChangeListener(listener);
    }

    /**
     * Adds a listener onto the leaf y position spinner.
     * @param listener is the listener.
     */
    public void addChangeYPositionListener(ChangeListener listener) {
        this.yPosition.addChangeListener(listener);
    }

    /**
     * Adds a listener onto the leaf scale spinner.
     * @param listener is the listener.
     */
    public void addChangeScaleListener(ChangeListener listener) {
        this.scale.addChangeListener(listener);
    }

    /**
     * Adds a listener onto the leaf rotation spinner.
     * @param listener is the listener.
     */
    public void addChangeRotationListener(ChangeListener listener) {
        this.rotation.addChangeListener(listener);
    }

    /**
     * Adds a listener onto the shape display name box.
     * @param listener is the listener to add.
     */
    public void addChangeLockedListener(ActionListener listener) {
        this.locked.addActionListener(listener);
    }

    /**
     * Adds a listener on the image select button.
     * @param listener is the listener to add.
     */
    public void addSelectImageListener(ActionListener listener) {
        this.image.addActionListener(listener);
    }

    /**
     * adds a listener on the sprite select button.
     * @param listener is the listener to add.
     */
    public void addSelectSpriteListener(ActionListener listener) {
        this.sprite.addActionListener(listener);
    }

    /**
     * Adds a listener for mouse events on the window.
     * @param listener is the listener to add.
     */
    public void addWindowMouseListener(MouseListener listener) {
        this.window.addMouseListener(listener);
    }

    /**
     * Adds a listener to the split point button.
     * @param listener is the listener to add.
     */
    public void addSplitPointListener(ActionListener listener) {
        this.splitPointButton.addActionListener(listener);
    }

    /**
     * Adds a listener to the remove point button.
     * @param listener is the listener to add.
     */
    public void addRemovePointListener(ActionListener listener) {
        this.removePointButton.addActionListener(listener);
    }

    /**
     * Adds a listener to the recentre points button.
     * @param listener is the listener to add.
     */
    public void addRecentrePointsListener(ActionListener listener) {
        this.recentrePointsButton.addActionListener(listener);
    }

    /**
     * Adds a listener for when people change the text in the game name box.
     * @param listener is the listener to listen.
     */
    public void addGameNameListener(ActionListener listener) {
        this.gameName.addActionListener(listener);
    }

    /**
     * Adds a listener to the game set texture atlas button.
     * @param listener is the listener.
     */
    public void addGameAtlasListener(ActionListener listener) {
        this.gameAtlas.addActionListener(listener);
    }

    /**
     * Adds a listener to when the script changes.
     * @param listener is the listener to add to listen.
     */
    public void addScriptListener(DocumentListener listener) {
        this.script.getDocument().addDocumentListener(listener);
    }

    /**
     * Adds a recent file button to the recent files list and adds a listener
     * onto the button at the same time.
     * @param path     is the path to the file.
     * @param listener is the listener to hear when it is clicked.
     */
    public void addRecentButtonAndListen(Path path, ActionListener listener) {
        JMenuItem item = new JMenuItem(path.toString());
        item.addActionListener(listener);
        this.recentFilesButton.add(item);
    }

    /**
     * Opens a dialog that lets you choose a game file, and then returns the
     * result.
     * @return the file found or null if they cancelled or something.
     */
    public File chooseAtlasFile() {
        int result = this.atlasChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return this.atlasChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Opens a dialog that lets you choose a game file, and then returns the
     * result.
     * @return the file found or null if they cancelled or something.
     */
    public File chooseGameFile() {
        int result = this.gameChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return this.gameChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Opens a dialog that lets you choose an image file, and returns the
     * result.
     * @return the file found or null if you are an idiot.
     */
    public File chooseImageFile() {
        int result = this.imageChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return this.imageChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Lets you select a sprite out of the texture atlas.
     * @param atlas is the texture atlas containing the sprites you can choose.
     * @return the chosen sprite if any.
     */
    public TextureAtlas.Region chooseSprite(TextureAtlas atlas) {
        Collection<TextureAtlas.Region> spritesCollection = atlas.getRegions();
        TextureAtlas.Region[] sprites = atlas.getRegions().toArray(
            new TextureAtlas.Region[spritesCollection.size()]
        );
        Arrays.sort(sprites);
        return (TextureAtlas.Region)JOptionPane.showInputDialog(
            this,
            "Choose the sprite",
            "Sprites",
            JOptionPane.PLAIN_MESSAGE,
            null,
            sprites,
            null
        );
    }

    /**
     * Displays a modal with the properties of the game which can be edited.
     */
    public void displayGameProperties() {
        this.gameDialog.pack();
        this.gameDialog.setVisible(true);
    }

    /**
     * Displays an arbitrary error message to the user.
     * @param message is the message to show them.
     */
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Creates a button that has got not but an icon.
     * @param icon is the name of the icon in the resources thing to add.
     * @param tip  is the tooltip to show when you hover it.
     * @return the created buttonen.
     */
    public static JButton makeIconButton(String icon, String tip) {
        JButton button = new JButton();
        button.setToolTipText(tip);
        button.setIcon(new ImageIcon(View.class.getResource(icon)));
        return button;
    }

    /**
     * Makes a position spinner model. This is just a helper since I have to do
     * it multiple times and it's bulky and repetitive.
     * @return the fresh new model straight outta the womb.
     */
    private static SpinnerNumberModel makePositionModel() {
        return new SpinnerNumberModel(
            0.0,
            -400.0,
            400.0,
            1.0
        );
    }

    /**
     * Makes a scale spinner model.
     * @return the model made.
     */
    private static SpinnerNumberModel makeScaleModel() {
        return new SpinnerNumberModel(
            1.0,
            0.0001,
            8.0,
            0.25
        );
    }

    /**
     * Makes a rotation spinner model.
     * @return the model.
     */
    private static SpinnerNumberModel makeRotationModel() {
        return new SpinnerNumberModel(0, -Math.PI, Math.PI, Math.PI / 10);
    }
}
