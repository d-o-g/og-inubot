package com.inubot.bot.ui;

import com.inubot.Bot;
import com.inubot.api.methods.Game;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.api.util.InterfaceComponentConfig;
import com.inubot.api.util.Paintable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Created by luckruns0ut on 03/05/15.
 */
public class InterfaceExplorer extends JFrame implements Paintable {

    private JPanel treePanel;
    private JPanel infoPanel;
    private JPanel searchPanel;
    private JPanel mainPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JTree explorer;
    private JScrollPane explorerScroll;
    private DefaultMutableTreeNode root;
    private JTextPane infoPane;
    private JCheckBox drawChildren;
    private boolean drawComponentChildren = false;
    private long lastRefresh = 0;
    private Object selected = null;

    public InterfaceExplorer() {
        super("InterfaceComponent Explorer");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(640, 660));
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        initComponents();
        pack();
    }

    private void initComponents() {
        root = new DefaultMutableTreeNode("Interfaces");
        explorer = new JTree(root);
        explorer.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                nodeSelected(treeSelectionEvent);
            }
        });
        explorerScroll = new JScrollPane(explorer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        infoPane = new JTextPane();
        infoPane.setEditable(false);
        infoPane.setContentType("text/html");
        infoPane.setDoubleBuffered(true);

        searchField = new JTextField();
        searchField.setText("Search");
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchFieldActionPerformed(actionEvent);
            }
        });

        drawChildren = new JCheckBox();
        drawChildren.setSelected(drawComponentChildren);
        drawChildren.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                drawComponentChildren = drawChildren.isSelected();
            }
        });
        drawChildren.setText("Draw component children?");

        searchButton = new JButton();
        searchButton.setText("Update");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                searchButtonActionPerformed(actionEvent);
            }
        });

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        treePanel = new JPanel();
        treePanel.setLayout(new GridLayout(1, 1));
        treePanel.add(explorerScroll);

        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 1));
        infoPanel.add(infoPane);
        infoPanel.setDoubleBuffered(true);

        searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(1, 3));
        searchPanel.add(drawChildren);
        searchPanel.add(searchButton);

        mainPanel.add(treePanel, BorderLayout.WEST);
        mainPanel.add(infoPanel, BorderLayout.EAST);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(searchPanel, BorderLayout.SOUTH);

        //EventManager.subscribeListener(this);
        Bot.getInstance().getCanvas().paintables.add(this);
    }


    public void refresh() {
        root.removeAllChildren();
        DefaultTreeModel model = (DefaultTreeModel) explorer.getModel();
        model.reload();

        if (!Game.isLoggedIn())
            return;

        InterfaceComponent[][] interfaces = Interfaces.getAll();

        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] != null) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("Interface: " + i);

                for (int j = 0; j < interfaces[i].length; j++) {
                    InterfaceComponent c = interfaces[i][j];

                    if (c != null) {
                        DefaultMutableTreeNode subNode = new DefaultMutableTreeNode("Component: " + j);
                        for (int k = 0; k < c.getComponents().length; k++) {
                            InterfaceComponent child = c.getComponents()[k];

                            if (child != null) {
                                subNode.add(new DefaultMutableTreeNode("Child: " + k));
                            }
                        }

                        node.add(subNode);
                    }
                }

                root.add(node);
            }
        }

        model = (DefaultTreeModel) explorer.getModel();
        model.reload();
    }

    private void searchFieldActionPerformed(ActionEvent e) {

    }

    private void searchButtonActionPerformed(ActionEvent e) {
        refresh();
    }

    @Override
    public void render(Graphics2D g) {
        if (selected == null || !this.isVisible())
            return;
        if (selected instanceof InterfaceComponent[]) {
            InterfaceComponent[] i = (InterfaceComponent[]) selected;
            if (i.length > 0) {
                for (InterfaceComponent c : i) {
                    if (c != null) {
                        if (c.getComponents().length > 0 && drawComponentChildren) {
                            g.setColor(Color.green);
                            if (c.getComponents() != null) {
                                for (InterfaceComponent child : c.getComponents()) {
                                    if (child != null) {
                                        g.drawRect(child.getX(), child.getY(), child.getWidth(), child.getHeight());
                                    }
                                }
                            }
                        }

                        g.setColor(Color.blue);
                        g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
                    }
                }
                setText(i);
            }
        }

        if (selected instanceof InterfaceComponent) {
            InterfaceComponent c = (InterfaceComponent) selected;

            if (c != null) {
                g.setColor(Color.green);
                if (drawComponentChildren && c.getComponents().length > 0) {
                    for (InterfaceComponent child : c.getComponents()) {
                        if (child != null) {
                            g.drawRect(child.getX(), child.getY(), child.getWidth(), child.getHeight());
                        }
                    }
                }

                g.setColor(Color.blue);
                g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
                setText(c);
            }
        }

        if (System.currentTimeMillis() - lastRefresh > 200) {
            if (selected instanceof InterfaceComponent[]) {
                setText((InterfaceComponent[]) selected);
            }
            if (selected instanceof InterfaceComponent) {
                setText((InterfaceComponent) selected);
            }
            lastRefresh = System.currentTimeMillis();
        }
    }

    private void nodeSelected(TreeSelectionEvent e) {
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) explorer.getLastSelectedPathComponent();

            if ((node.getUserObject()).equals("Interfaces"))
                selected = null;

            if (node != null) {
                String nodeName = (String) node.getUserObject();

                if (nodeName.contains("Interface: ")) {
                    int id = Integer.parseInt(nodeName.replace("Interface: ", ""));

                    InterfaceComponent[] i = Interfaces.componentsFor(id);
                    selected = i;
                    selectedInterface(i);
                }

                if (nodeName.contains("Component: ")) {
                    String parentName = node.getParent().toString();
                    int parentId = Integer.parseInt(parentName.replace("Interface: ", ""));

                    int id = Integer.parseInt(nodeName.replace("Component: ", ""));
                    InterfaceComponent c = Interfaces.getComponent(parentId, id);
                    selected = c;
                    selectedComponent(c);
                }

                if (nodeName.contains("Child: ")) {
                    String parentOfParentName = node.getParent().getParent().toString();
                    String parentName = node.getParent().toString();

                    int parentOfParentId = Integer.parseInt(parentOfParentName.replace("Interface: ", ""));
                    int parentId = Integer.parseInt(parentName.replace("Component: ", ""));
                    int id = Integer.parseInt(nodeName.replace("Child: ", ""));
                    InterfaceComponent c = Interfaces.getComponent(parentOfParentId, parentId).getComponents()[id];
                    selected = c;
                    selectedComponent(c);
                }
            }
        } catch (Exception ex) {

        }
    }

    private void selectedInterface(InterfaceComponent[] i) {
        setText(i);
    }

    private void selectedComponent(InterfaceComponent c) {
        setText(c);
    }

    private void setText(final InterfaceComponent[] i) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (i == null)
                    infoPane.setText("This object is null.");
                else {
                    infoPane.setText("<font face=\"Helvetica\" color=\"black\">"
                            + "<b> Component count: </b>" + i.length + "<br>");
                }
            }
        });
    }

    private void setText(final InterfaceComponent c) {
        //231 = continue
        //219 = options

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (c == null)
                    infoPane.setText("This object is null.");
                else {
                    infoPane.setText("<font face=\"Helvetica\" color=\"black\">"
                            + "<b> Child count: </b>" + c.getComponents().length + "<br>"
                            + "<b> Absolute X: </b>" + c.getX() + "<br>"
                            + "<b> Absolute Y: </b>" + c.getY() + "<br>"
                            + "<b> Width: </b>" + c.getWidth() + "<br>"
                            + "<b> Height: </b>" + c.getHeight() + "<br>"
                            + "<b> Material Id: </b>" + c.getMaterialId() + "<br>"
                            + "<b> Item Id: </b>" + c.getItemId() + "<br>"
                            + "<b> Item quantity: </b>" + c.getItemQuantity() + "<br>"
                            + "<b> Item Ids: </b>" + Arrays.toString(c.getItemIds()) + "<br>"
                            + "<b> Item quantities: </b>" + Arrays.toString(c.getItemQuantities()) + "<br>"
                            + "<b> Actions: </b>" + Arrays.toString(c.getActions()) + "<br>"
                            + "<b> Visible: </b>" + c.isVisible() + "<br>"
                            + "<b> Text: </b>" + c.getText() + "<br>"
                            + "<b> Id: </b>" + c.getId() + "<br>"
                            + "<b> Type: </b>" + c.getType() + "<br>"
                            + "<b> Content type: </b>" + c.getContentType() + "<br>"
                            + "<b> Padding: </b>" + c.getXPadding() + ", " + c.getYPadding() + "<br>"
                            + "<b> Sprite Id: </b>" + c.getSpriteId() + "<br>"
                            + "<b> Model Id: </b>" + c.getModelId() + "<br>"
                            + "<b> Font Id: </b>" + c.getFontId() + "<br>"
                            + "<b> Text color: </b>" + c.getTextColor() + "<br>"
                            + "<b> Shadow color: </b>" + c.getShadowColor() + "<br>"
                            + "<b> Selected action: </b>" + c.getSelectedAction() + "<br>"
                            + "<b> Border thickness: </b>" + c.getBorderThickness() + "<br>"
                            + "<b> Config: </b>" + c.getConfig() + "<br>"
                            + "<b> Config Application Targets: </b>" + InterfaceComponentConfig.getApplicationTargets(c.getConfig()) + "<br>"
                            + "<b> Script depth: </b>" + InterfaceComponentConfig.getScriptEventDepth(c.getConfig()) + "<br>"
                            + "<b> Is dialog option: </b>" + InterfaceComponentConfig.isDialogOption(c.getConfig()) + "<br>"
                            + "<b> Hidden: </b>" + c.isHidden() + "<br>"
                            + "<b> Explicitly Hidden: </b>" + c.isExplicitlyHidden() + "<br>"
                            + "<b> Item Id: </b>" + c.getItemId() + "<br>");
                }
            }
        });
    }
}
