package peony;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Manages the program's user interface.
 */
public class View extends JFrame {
    private JSplitPane verticalSplit;
    private JFileChooser imageChooser = new JFileChooser();
    private JFileChooser gameChooser = new JFileChooser();
    private JDialog spriteChooser = new JDialog(this, "brexit", true);
    private JPanel leafPropertiesPanel = new JPanel();
    private JPanel leafMainPropertiesPanel = new JPanel(new GridLayout(0, 2));
    private JPanel leafImagePropertiesPanel = new JPanel(new GridLayout(0, 2));
    private JPanel leafSpritePropertiesPanel = new JPanel(new GridLayout(0, 2));
    private JPanel leafShapePropertiesPanel = new JPanel(new GridLayout(0, 2));
    private JMenuItem gamePropertiesButton = new JMenuItem("Game Properties");
    private JMenuItem loadButton = new JMenuItem("Load");
    private JMenuItem saveButton = new JMenuItem("Save");
    private JMenuItem quitButton = new JMenuItem("Exit");
    private JMenuItem addImageButton = new JMenuItem("Image");
    private JMenuItem addSpriteButton = new JMenuItem("Sprite");
    private JMenuItem addShapeButton = new JMenuItem("Shape");
    private JMenuItem addPointButton = new JMenuItem("Point");
    private JMenuItem removeButton = new JMenuItem("Remove");
    private DefaultListModel<String> leafListModel = new DefaultListModel<>();
    private SpinnerNumberModel xPositionModel = View.makePositionModel();
    private SpinnerNumberModel yPositionModel = View.makePositionModel();
    private SpinnerNumberModel scaleModel = View.makeScaleModel();
    private SpinnerNumberModel rotationModel = View.makeRotationModel();
    private JTextField leafName = new JTextField(10);
    private JSpinner xPosition = new JSpinner(this.xPositionModel);
    private JSpinner yPosition = new JSpinner(this.yPositionModel);
    private JSpinner scale = new JSpinner(this.scaleModel);
    private JSpinner rotation = new JSpinner(this.rotationModel);
    private JButton sprite = new JButton("Select Sprite");
    private JButton image = new JButton("Select Image");
    private JTextField displayName = new JTextField(10);
    private JList<String> leafList = new JList<>(this.leafListModel);
    private JList<String> mapList = new JList<>();
    private JPanel layout = new JPanel();
    private RSyntaxTextArea script = new RSyntaxTextArea(20, 60);

    /**
     * Makes a position spinner model. This is just a helper since I have to do
     * it multiple times and it's bulky and repetitive.
     * @return the fresh new model straight outta the womb.
     */
    public static SpinnerNumberModel makePositionModel() {
        return new SpinnerNumberModel(0.0, -400.0, 400.0, 1.0);
    }

    /**
     * Makes a scale spinner model.
     * @return the model made.
     */
    public static SpinnerNumberModel makeScaleModel() {
        return new SpinnerNumberModel(1.0, 0.0001, 8.0, 0.25);
    }

    /**
     * Makes a rotation spinner model.
     * @return the model.
     */
    public static SpinnerNumberModel makeRotationModel() {
        return new SpinnerNumberModel(0, -Math.PI, Math.PI, 0.3);
    }

    /**
     * Creates and sets up the view.
     */
    public View() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(640, 480);
        this.script.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(this.loadButton);
        fileMenu.add(this.saveButton);
        fileMenu.add(this.gamePropertiesButton);
        fileMenu.add(this.quitButton);
        JMenu addMenu = new JMenu("Add");
        addMenu.add(this.addImageButton);
        addMenu.add(this.addSpriteButton);
        addMenu.add(this.addShapeButton);
        addMenu.add(this.addPointButton);
        menuBar.add(fileMenu);
        menuBar.add(addMenu);
        FileNameExtensionFilter gameFilter = new FileNameExtensionFilter(
            "Readable game files",
            "json"
        );
        this.gameChooser.setFileFilter(gameFilter);
        this.gameChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Usable image files",
            "png"
        );
        this.imageChooser.setFileFilter(filter);
        this.imageChooser.setAcceptAllFileFilterUsed(false);
        this.image.addActionListener((ActionEvent event) -> {
            int result = this.imageChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                System.out.println(this.imageChooser.getSelectedFile().getName());
            }
        });
        this.sprite.addActionListener((ActionEvent event) -> {
            this.spriteChooser.setVisible(true);
        });
        JTabbedPane propertiesTabs = new JTabbedPane();
        this.leafPropertiesPanel.setLayout(
            new BoxLayout(leafPropertiesPanel, BoxLayout.Y_AXIS)
        );
        this.leafMainPropertiesPanel.add(new JLabel("Name"));
        this.leafMainPropertiesPanel.add(this.leafName);
        this.leafMainPropertiesPanel.add(new JLabel("X Position"));
        this.leafMainPropertiesPanel.add(this.xPosition);
        this.leafMainPropertiesPanel.add(new JLabel("Y Position"));
        this.leafMainPropertiesPanel.add(this.yPosition);
        this.leafMainPropertiesPanel.add(new JLabel("Scale"));
        this.leafMainPropertiesPanel.add(this.scale);
        this.leafMainPropertiesPanel.add(new JLabel("Rotation"));
        this.leafMainPropertiesPanel.add(this.rotation);
        this.leafImagePropertiesPanel.add(new JLabel("Image"));
        this.leafImagePropertiesPanel.add(this.image);
        this.leafSpritePropertiesPanel.add(new JLabel("Sprite"));
        this.leafSpritePropertiesPanel.add(this.sprite);
        this.leafShapePropertiesPanel.add(new JLabel("Display Name"));
        this.leafShapePropertiesPanel.add(this.displayName);
        this.leafImagePropertiesPanel.setVisible(false);
        this.leafSpritePropertiesPanel.setVisible(false);
        this.leafShapePropertiesPanel.setVisible(false);
        this.leafPropertiesPanel.add(this.leafMainPropertiesPanel);
        this.leafPropertiesPanel.add(this.leafImagePropertiesPanel);
        this.leafPropertiesPanel.add(this.leafSpritePropertiesPanel);
        this.leafPropertiesPanel.add(this.leafShapePropertiesPanel);
        JPanel mapPropertiesPanel = new JPanel(new GridLayout(0, 2));
        mapPropertiesPanel.add(new JLabel("Name"));
        propertiesTabs.addTab("Leaf", leafPropertiesPanel);
        propertiesTabs.addTab("Layout", mapPropertiesPanel);
        this.leafList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab("Layout", this.layout);
        mainTabs.addTab("Script", this.script);
        JTabbedPane listTabs = new JTabbedPane();
        listTabs.addTab("Leaves", this.leafList);
        listTabs.addTab("Layouts", this.mapList);
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
    }

    /**
     * Gives you the text that is currently written in the leaf name box.
     * @return the written text.
     */
    public String getLeafName() {
        return this.leafName.getText();
    }

    /**
     * Changes whether the save button is turned on.
     * @param enabled is whether for it to be on or off.
     */
    public void setSaveEnabled(boolean enabled) {
        this.saveButton.setEnabled(enabled);
    }

    /**
     * Gives you the current selected index in the leaf list if any.
     * @return the leaf list's selection index which is -1 when nothing is
     *         selected.
     */
    public int getSelectedLeaf() {
        return this.leafList.getSelectedIndex();
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
     * Sets all of the leaf related stuff based on an actual leaf.
     * @param leaf is the leaf to set stuff based on.
     */
    public void setLeaf(Leaf leaf) {
        this.setPosition(leaf.getPosition());
        this.setScale(leaf.getScale());
        this.setRotation(leaf.getRotation());
        this.leafName.setText(leaf.getName());
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

    /**
     * Adds a leaf name onto the end of the leaf list.
     * @param name is the leaf name to add.
     */
    public void appendLeafList(String name) {
        this.leafListModel.addElement(name);
    }

    /**
     * Updates the name of a leaf in the leaf list.
     * @param index is the index in the list to update.
     * @param leaf  is the leaf to update it to respect.
     */
    public void updateLeafList(int index, String name) {
        this.leafListModel.set(index, name);
    }

    /**
     * Adds an action event listener to the input box for the currently
     * selected leaf's name.
     * @param listener is the listener to add.
     */
    public void addLeafNameListener(ActionListener listener) {
        this.leafName.addActionListener(listener);
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
     * Adds an action listener to the remove leaf button.
     * @param listener is the listener to add.
     */
    public void addRemoveListener(ActionListener listener) {
        this.removeButton.addActionListener(listener);
    }

    /**
     * Adds a listener onto the leaf list for when something is selected in it.
     * @param listener is the listener to add.
     */
    public void addSelectLeafListener(ListSelectionListener listener) {
        this.leafList.addListSelectionListener(listener);
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
    public void addChangeDisplayNameListener(ActionListener listener) {
        this.displayName.addActionListener(listener);
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
     * Displays an arbitrary error message to the user.
     * @param message is the message to show them.
     */
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
