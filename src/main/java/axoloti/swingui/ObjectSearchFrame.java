/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.swingui;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.AxoObjectTreeNode;
import axoloti.object.AxoObjects;
import axoloti.object.IAxoObject;
import axoloti.object.ObjectController;
import axoloti.patch.PatchController;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.AxoObjectInstanceFactory;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.ObjectInstancePatcherController;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.AxoObjectInstanceViewFactory;
import axoloti.utils.Constants;
import axoloti.utils.OSDetect;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Johannes Taelman
 */
public class ObjectSearchFrame extends ResizableUndecoratedFrame {

    DefaultMutableTreeNode root;
    DefaultTreeModel tm;
    public IAxoObject type;
    protected final PatchController patchController;
    public IAxoObjectInstance target_object;
    private AxoObjectTreeNode objectTree;

    /**
     * Creates new form ObjectSearchFrame
     *
     * @param p parent
     */
    public ObjectSearchFrame(PatchController patchController) {
        super();
        initComponents();

        if (OSDetect.getOS() == OSDetect.OS.MAC) {
            // buttons w    ith a text label use huge margins on macos
            // or when forced, will substitute the label with '...',
            // while buttons with just an icon can have a tight margin (want!)
            // We're using a single unicode character as a label
            // but do not want it to be treated as a label...
            jButtonCancel.setIcon(new StringIcon(jButtonCancel.getText()));
            jButtonCancel.setText(null);
            jButtonAccept.setIcon(new StringIcon(jButtonAccept.getText()));
            jButtonAccept.setText(null);
            // Alternative approach: use real icons
            // Unfortunately macos does not provide icons. with appropriate
            // semantics..
            //
            // Toolkit toolkit = Toolkit.getDefaultToolkit();
            // Image image = toolkit.getImage("NSImage://NSStopProgressTemplate");
            // image = toolkit.getImage("NSImage://NSMenuOnStateTemplate");
            //
            // prettify buttons - macos exclusive
            jButtonCancel.putClientProperty("JButton.buttonType", "segmented");
            jButtonAccept.putClientProperty("JButton.buttonType", "segmented");
            jButtonCancel.putClientProperty("JButton.segmentPosition", "first");
            jButtonAccept.putClientProperty("JButton.segmentPosition", "last");
        }
        jButtonAccept.setEnabled(false);

        this.patchController = patchController;
        DefaultMutableTreeNode root1 = new DefaultMutableTreeNode();
        this.objectTree = AxoObjects.getAxoObjects().ObjectTree;
        this.root = PopulateJTree(AxoObjects.getAxoObjects().ObjectTree, root1);
        tm = new DefaultTreeModel(this.root);
        jTree1.setModel(tm);
        jTree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                if (node.getUserObject() instanceof AxoObjectTreeNode) {
                    AxoObjectTreeNode anode = (AxoObjectTreeNode) node.getUserObject();
                    jPanel1.removeAll();
                    jPanel1.repaint();
                    jTextPane1.setText(anode.description);
                    jTextPane1.setCaretPosition(0);
                    previewObj = null;
                }
                Object nodeInfo = node.getUserObject();
                if (nodeInfo instanceof IAxoObject) {
                    SetPreview((IAxoObject) nodeInfo);
                    if (!jTextFieldObjName.hasFocus()) {
                        jTextFieldObjName.setText(((IAxoObject) nodeInfo).getId());
                    }
                }
            }
        });
        jTree1.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
                    if (node != null) {
                        if (node.isLeaf()) {
                            Accept();
                            e.consume();
                        } else {
                            jTree1.expandPath(jTree1.getLeadSelectionPath());
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Cancel();
                    e.consume();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        jTree1.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
                    Accept();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        jList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object o = ObjectSearchFrame.this.jList1.getSelectedValue();
                if (o instanceof IAxoObject) {
                    SetPreview((IAxoObject) o);
                    if (!jTree1.hasFocus()) {
                        ExpandJTreeToEl((IAxoObject) o);
                    }
                    if (!jTextFieldObjName.hasFocus()) {
                        jTextFieldObjName.setText(((IAxoObject) o).getId());
                    }
                } else if (o == null) {
                } else {
                    System.out.println("different obj?" + o.toString());
                }
            }
        });
        jList1.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Accept();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    type = null;
                    Cancel();
                    e.consume();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        jList1.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
                    Accept();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        jPanel1.setVisible(true);
        jScrollPane1.setVisible(true);
        jScrollPane4.setVisible(true);
        jSplitPane1.setVisible(true);
        jSplitPane2.setVisible(true);
        jTextPane1.setVisible(true);
        jTextPane1.setContentType("text/html");

        jTextFieldObjName.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Accept();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    type = null;
                    Cancel();
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Search(jTextFieldObjName.getText());
            }
        });
    }
    protected IAxoObject previewObj;
    int patchLocX;
    int patchLocY;
    
    private Point snapToGrid(Point p) {
        p.x = Constants.X_GRID * (p.x / Constants.X_GRID);
        p.y = Constants.Y_GRID * (p.y / Constants.Y_GRID);
        return p;
    }

    public Point clipToStayWithinScreen(Point patchLoc) {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        Rectangle allScreenBounds = new Rectangle();

        for (GraphicsDevice curGs : gs) {
            GraphicsConfiguration[] gc = curGs.getConfigurations();
            for (GraphicsConfiguration curGc : gc) {
                Rectangle bounds = curGc.getBounds();
                allScreenBounds = allScreenBounds.union(bounds);
            }
        }

        Point patchFrameOnScreen = patchController.getViewLocationOnScreen();

        if (patchFrameOnScreen.getX() + patchLoc.getX() + getWidth() > allScreenBounds.getWidth() + allScreenBounds.getX()) {
            patchLoc.x = (int) (allScreenBounds.getWidth() + allScreenBounds.getX() - patchFrameOnScreen.getX() - getWidth());
        }
        if (patchFrameOnScreen.getY() + patchLoc.getY() + getHeight() > allScreenBounds.getHeight() + allScreenBounds.getY()) {
            patchLoc.y = (int) (allScreenBounds.getHeight() + allScreenBounds.getY() - patchFrameOnScreen.getY() - getHeight());
        }

        return patchLoc;
    }

    public void Launch(Point patchLoc, IAxoObjectInstanceView o, String searchString) {
        Launch(patchLoc, o, searchString, true);
    }

    public void Launch(Point patchLoc, IAxoObjectInstanceView o, String searchString, boolean setVisible) {
        if (this.objectTree != AxoObjects.getAxoObjects().ObjectTree) {
            DefaultMutableTreeNode root1 = new DefaultMutableTreeNode();
            this.objectTree = AxoObjects.getAxoObjects().ObjectTree;
            this.root = PopulateJTree(AxoObjects.getAxoObjects().ObjectTree, root1);
            tm = new DefaultTreeModel(this.root);
            jTree1.setModel(tm);
        }

        MainFrame.mainframe.setGrabFocusOnSevereErrors(false);
        accepted = false;
        snapToGrid(patchLoc);
        patchLocX = patchLoc.x;
        patchLocY = patchLoc.y;
        Point ps = patchController.getViewLocationOnScreen();
        Point patchLocClipped = clipToStayWithinScreen(patchLoc);

        setLocation(patchLocClipped.x + ps.x, patchLocClipped.y + ps.y);
        if (o != null) {
            target_object = o.getModel();
        } else {
            target_object = null;
        }
        if (o != null) {
            IAxoObject oa = o.getModel().getType();
            if (oa != null) {
                Search(oa.getId());
                SetPreview(oa);
                ExpandJTreeToEl(oa);
            }
            jTextFieldObjName.setText(o.getModel().getType().getId());
        } else if (searchString != null) {
            Search(searchString);
            jTextFieldObjName.setText(searchString);
        }
        jTextFieldObjName.grabFocus();
        jTextFieldObjName.setSelectionStart(0);
        jTextFieldObjName.setSelectionEnd(jTextFieldObjName.getText().length());
        if (setVisible) {
            setVisible(true);
        }
    }

    public void SetPreview(IAxoObject o) {
        if (o == null) {
            previewObj = null;
            type = null;
            jPanel1.removeAll();
            jPanel1.repaint();
            jButtonAccept.setEnabled(false);
            return;
        }
        else {
            accepted = true;
            jButtonAccept.setEnabled(true);            
        }
        if (o != previewObj) {
            previewObj = o;
            type = o;
            ExpandJTreeToEl(o);
            jList1.setSelectedValue(o, true);
            if (jList1.getSelectedValue() != o) {
            }
            ObjectController oc = o.createController(null, null);
            AxoObjectInstanceAbstract objectInstance = AxoObjectInstanceFactory.createView(oc, null, "dummy", new Point(5, 5));
            ObjectInstanceController c;

            if (objectInstance instanceof AxoObjectInstancePatcher) {
                c = new ObjectInstancePatcherController((AxoObjectInstancePatcher) objectInstance, null, null);
            } else {
                c = new ObjectInstanceController(objectInstance, null, null);
            }

            IAxoObjectInstanceView objectInstanceView = AxoObjectInstanceViewFactory.getInstance().createView(c, null);
            jPanel1.removeAll();
            jPanel1.add((AxoObjectInstanceViewAbstract) objectInstanceView);
            objectInstanceView.resizeToGrid();
            jPanel1.repaint();  //reqd, as removed object may be smaller than new object

            IAxoObject t = objectInstanceView.getModel().getType();
            if (t != null) {
                String description = t.getDescription() == null || t.getDescription().isEmpty() ? o.getDescription() : t.getDescription();
                String path = t.getPath() == null ? o.getPath() : t.getPath();
                String author = t.getAuthor() == null ? o.getAuthor() : t.getAuthor();
                String license = t.getLicense() == null ? o.getLicense() : t.getLicense();
                String txt = description;
                if ((path != null) && (!path.isEmpty())) {
                    txt += "\n<p>\nPath: " + path;
                }
                if ((author != null) && (!author.isEmpty())) {
                    txt += "\n<p>\nAuthor: " + author;
                }
                if ((license != null) && (!license.isEmpty())) {
                    txt += "\n<p>\nLicense: " + license;
                }
                jTextPane1.setText(txt);
            }
            jTextPane1.setCaretPosition(0);
        }
    }

    static DefaultMutableTreeNode PopulateJTree(AxoObjectTreeNode anode, DefaultMutableTreeNode root) {
        for (String n : anode.SubNodes.keySet()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(anode.SubNodes.get(n));
            root.add(PopulateJTree(anode.SubNodes.get(n), node));
        }
        for (IAxoObject n : anode.Objects) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(n);
            root.add(node);
        }
        return root;
    }

    protected void ExpandJTreeToEl(IAxoObject s) {
        Enumeration e = root.depthFirstEnumeration();
        DefaultMutableTreeNode n = null;
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) o;
            if (s.equals(dmtn.getUserObject())) {
                n = (DefaultMutableTreeNode) o;
                break;
            }
        }
        if (n != null) {
            if ((jTree1.getSelectionPath() != null) && (jTree1.getSelectionPath().getLastPathComponent() != n)) {
                jTree1.scrollPathToVisible(new TreePath(n.getPath()));
                jTree1.setSelectionPath(new TreePath(n.getPath()));
            }
        } else {
            jTree1.clearSelection();
        }
    }

    public void Search(String s) {
        ArrayList<IAxoObject> listData = new ArrayList<IAxoObject>();
        if ((s == null) || s.isEmpty()) {
            for (IAxoObject o : AxoObjects.getAxoObjects().ObjectList) {
                listData.add(o);
            }
            jList1.setListData(listData.toArray());
//            jList1.doLayout();
//            jList1.revalidate();
        } else {
            // exact match first
            for (IAxoObject o : AxoObjects.getAxoObjects().ObjectList) {
                if (o.getId().equals(s)) {
                    listData.add(o);
                }
            }
            for (IAxoObject o : AxoObjects.getAxoObjects().ObjectList) {
                if (o.getId().startsWith(s)) {
                    if (!listData.contains(o)) {
                        listData.add(o);
                    }
                }
            }
            for (IAxoObject o : AxoObjects.getAxoObjects().ObjectList) {
                if (o.getId().contains(s)) {
                    if (!listData.contains(o)) {
                        listData.add(o);
                    }
                }
            }
            for (IAxoObject o : AxoObjects.getAxoObjects().ObjectList) {
                if (o.getDescription() != null && o.getDescription().contains(s)) {
                    if (!listData.contains(o)) {
                        listData.add(o);
                    }
                }
            }
            jList1.setListData(listData.toArray());
            if (!listData.isEmpty()) {
                type = listData.get(0);
                jList1.setSelectedIndex(0);
                jList1.ensureIndexIsVisible(0);
                ExpandJTreeToEl(listData.get(0));
                SetPreview(type);
            } else {
                List<IAxoObject> objs = AxoObjects.getAxoObjects().GetAxoObjectFromName(s, patchController.GetCurrentWorkingDirectory());
                if ((objs != null) && (objs.size() > 0)) {
                    jList1.setListData(objs.toArray());
                    SetPreview(objs.get(0));
                }
            }
        }
    }

    boolean accepted = false;

    public void Cancel() {
        accepted = false;
        MainFrame.mainframe.setGrabFocusOnSevereErrors(true);
        setVisible(false);
    }

    public void Accept() {
        MainFrame.mainframe.setGrabFocusOnSevereErrors(true);
        setVisible(false);
        IAxoObject x = type;
        if (x == null) {
            List<IAxoObject> objs = AxoObjects.getAxoObjects().GetAxoObjectFromName(jTextFieldObjName.getText(), patchController.GetCurrentWorkingDirectory());
            if ((objs != null) && (!objs.isEmpty())) {
                x = objs.get(0);
                jTextFieldObjName.setText("");
            }
        }
        if (x != null) {
            if (target_object == null) {
                    patchController.addMetaUndo("add object");
                    patchController.AddObjectInstance(x, new Point(patchLocX, patchLocY));
            } else {
                    patchController.addMetaUndo("change object type");
                    AxoObjectInstanceAbstract oi = patchController.ChangeObjectInstanceType(target_object, x);
            }
        }
        setVisible(false);
        accepted = false;
    }

    protected JPanel getMainView() {
        return jPanel1;
    }

    protected JList getListView() {
        return jList1;
    }

    protected JTextPane getTextPane() {
        return jTextPane1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jTextFieldObjName = new javax.swing.JTextField();
        jButtonCancel = new javax.swing.JButton();
        jButtonAccept = new javax.swing.JButton();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();

        setForeground(java.awt.SystemColor.control);
        setIconImages(null);
        setName(""); // NOI18N
        setUndecorated(true);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(186);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(83, 50));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(600, 365));

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel4.setAlignmentY(0.0F);
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldObjName.setAlignmentX(0.0F);
        jTextFieldObjName.setMaximumSize(new java.awt.Dimension(2147483647, 26));
        jTextFieldObjName.setMinimumSize(new java.awt.Dimension(40, 26));
        jTextFieldObjName.setPreferredSize(new java.awt.Dimension(800, 20));
        jTextFieldObjName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldObjNameActionPerformed(evt);
            }
        });
        jPanel4.add(jTextFieldObjName);

        jButtonCancel.setText("✗");
        jButtonCancel.setToolTipText("Cancel");
        jButtonCancel.setActionCommand("");
        jButtonCancel.setDefaultCapable(false);
        jButtonCancel.setFocusable(false);
        jButtonCancel.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButtonCancel.setMinimumSize(new java.awt.Dimension(24, 24));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonCancel);

        jButtonAccept.setText("✓");
        jButtonAccept.setToolTipText("Accept");
        jButtonAccept.setActionCommand("");
        jButtonAccept.setDefaultCapable(false);
        jButtonAccept.setFocusable(false);
        jButtonAccept.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButtonAccept.setMinimumSize(new java.awt.Dimension(24, 24));
        jButtonAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAcceptActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonAccept);

        jPanel2.add(jPanel4);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setResizeWeight(0.5);
        jSplitPane3.setAlignmentX(0.5F);
        jSplitPane3.setAlignmentY(1.0F);
        jSplitPane3.setMinimumSize(new java.awt.Dimension(126, 95));

        jScrollPane3.setMinimumSize(new java.awt.Dimension(24, 64));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setAlignmentX(0.0F);
        jList1.setMinimumSize(new java.awt.Dimension(100, 50));
        jList1.setVisibleRowCount(6);
        jScrollPane3.setViewportView(jList1);

        jSplitPane3.setTopComponent(jScrollPane3);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(76, 224));

        jTree1.setAlignmentX(0.0F);
        jTree1.setDragEnabled(true);
        jTree1.setMinimumSize(new java.awt.Dimension(100, 50));
        jTree1.setRootVisible(false);
        jTree1.setShowsRootHandles(true);
        jScrollPane1.setViewportView(jTree1);

        jSplitPane3.setBottomComponent(jScrollPane1);

        jPanel2.add(jSplitPane3);

        jSplitPane1.setLeftComponent(jPanel2);

        jSplitPane2.setDividerLocation(120);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.5);
        jSplitPane2.setPreferredSize(new java.awt.Dimension(350, 271));

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setMinimumSize(new java.awt.Dimension(6, 63));

        jTextPane1.setEditable(false);
        jTextPane1.setFocusCycleRoot(false);
        jTextPane1.setFocusable(false);
        jTextPane1.setRequestFocusEnabled(false);
        jScrollPane4.setViewportView(jTextPane1);

        jSplitPane2.setTopComponent(jScrollPane4);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setEnabled(false);
        jPanel1.setFocusable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel1);

        jSplitPane1.setRightComponent(jSplitPane2);

        jPanel3.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        getRootPane().setCursor(Cursor.getDefaultCursor());
        if ((evt.getOppositeWindow() == null)
                || !(evt.getOppositeWindow() instanceof axoloti.swingui.patch.PatchFrame)) {
            Cancel();
        } else {
            if (accepted) {
                Accept();
            } else {
                Cancel();
            }
        }
    }//GEN-LAST:event_formWindowLostFocus

    private void jTextFieldObjNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldObjNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldObjNameActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        Cancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAcceptActionPerformed
        Accept();
    }//GEN-LAST:event_jButtonAcceptActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAccept;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTextField jTextFieldObjName;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables


    class StringIcon implements Icon {

        final String str;
        final int w, h;

        public StringIcon(String str) {
            this(str, 20, 20);
        }

        public StringIcon(String str, int w, int h) {
            this.str = str;
            this.w = w;
            this.h = h;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            FontMetrics metrics = g2.getFontMetrics(g2.getFont());
            Rectangle2D bounds = metrics.getStringBounds(str, g2);
            int xc = (w / 2) + x;
            int yc = (h / 2) + y;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawString(str, xc - (int) bounds.getCenterX(), yc - (int) bounds.getCenterY());
//          g.fillOval(xm, ym, 1, 1);
        }

        @Override
        public int getIconWidth() {
            return w;
        }

        @Override
        public int getIconHeight() {
            return h;
        }
    }
}
