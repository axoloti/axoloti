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
package axoloti;

import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectTreeNode;
import axoloti.utils.Constants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JRootPane;
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
public class ObjectSearchFrame extends javax.swing.JFrame {

    DefaultMutableTreeNode root;
    DefaultTreeModel tm;
    public AxoObjectAbstract type;
    private final PatchGUI p;
    public AxoObjectInstanceAbstract target_object;
    private AxoObjectTreeNode objectTree;

    private AxoObjectAbstract previewObj;
    private AxoObjectInstanceAbstract inst;
    private int patchLocX;
    private int patchLocY;

    /**
     * Creates new form ObjectSearchFrame
     *
     * @param p parent
     */
    public ObjectSearchFrame(PatchGUI p) {
        initComponents();
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        this.p = p;
        DefaultMutableTreeNode root1 = new DefaultMutableTreeNode();
        this.objectTree = MainFrame.axoObjects.ObjectTree;
        this.root = PopulateJTree(MainFrame.axoObjects.ObjectTree, root1);
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
                    jPanel1.setVisible(false);
                    jTextPane1.setText(anode.description);
                    jTextPane1.setCaretPosition(0);
                    previewObj = null;
                }
                Object nodeInfo = node.getUserObject();
                if (nodeInfo instanceof AxoObjectAbstract) {
                    SetPreview((AxoObjectAbstract) nodeInfo);
                    if (!jTextFieldObjName.hasFocus()) {
                        jTextFieldObjName.setText(((AxoObjectAbstract) nodeInfo).id);
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
                if (o instanceof AxoObjectAbstract) {
                    SetPreview((AxoObjectAbstract) o);
                    if (!jTree1.hasFocus()) {
                        ExpandJTreeToEl((AxoObjectAbstract) o);
                    }
                    if (!jTextFieldObjName.hasFocus()) {
                        jTextFieldObjName.setText(((AxoObjectAbstract) o).id);
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

        JLayer<JComponent> objectPreviewLayer = new JLayer<JComponent>(jPanel1, p.zoomUI);
        jLayeredPane1.remove(jPanel1);
        jLayeredPane1.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        jLayeredPane1.add(objectPreviewLayer, new Integer(1));
        jLayeredPane1.setOpaque(true);
        jPanel1.setBackground(Color.DARK_GRAY);

        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                /* code run when component hidden*/
            }

            public void componentShown(ComponentEvent e) {
                if (inst != null) {
                    ObjectSearchFrame.this.scalePreviewPanel(inst.getWidth(), inst.getHeight());
                }
            }
        });
    }

    private Point snapToGrid(Point p) {
        p.x = Constants.X_GRID * (p.x / Constants.X_GRID);
        p.y = Constants.Y_GRID * (p.y / Constants.Y_GRID);
        return p;
    }

    void Launch(Point patchLoc, AxoObjectInstanceAbstract o, String searchString) {
        if (this.objectTree != MainFrame.axoObjects.ObjectTree) {
            DefaultMutableTreeNode root1 = new DefaultMutableTreeNode();
            this.objectTree = MainFrame.axoObjects.ObjectTree;
            this.root = PopulateJTree(MainFrame.axoObjects.ObjectTree, root1);
            tm = new DefaultTreeModel(this.root);
            jTree1.setModel(tm);
        }

        accepted = false;
        snapToGrid(patchLoc);
        patchLocX = patchLoc.x;
        patchLocY = patchLoc.y;
        Point ps = p.objectLayerPanel.getLocationOnScreen();

        double zoom = p.zoomUI.getScale();
        Point patchLocZoomed = snapToGrid(new Point(
                (int) Math.round(patchLoc.x * zoom),
                (int) Math.round(patchLoc.y * zoom)));

        setLocation(patchLocZoomed.x + ps.x, patchLocZoomed.y + ps.y);

        target_object = o;
        if (o != null) {
            AxoObjectAbstract oa = o.getType();
            if (oa != null) {
                Search(oa.id);
                SetPreview(oa);
                ExpandJTreeToEl(oa);
            }
            jTextFieldObjName.setText(o.typeName);
        } else if (searchString != null) {
            Search(searchString);
            jTextFieldObjName.setText(searchString);
        }
        jTextFieldObjName.grabFocus();
        jTextFieldObjName.setSelectionStart(0);
        jTextFieldObjName.setSelectionEnd(jTextFieldObjName.getText().length());
        setVisible(true);
    }

    void SetPreview(AxoObjectAbstract o) {
        if (o == null) {
            previewObj = null;
            type = null;
            jPanel1.removeAll();
            jPanel1.setVisible(false);
            return;
        }
        if (o != previewObj) {
            previewObj = o;
            type = o;
            ExpandJTreeToEl(o);
            jList1.setSelectedValue(o, true);
            if (jList1.getSelectedValue() != o) {
            }
            inst = o.CreateInstance(null, "dummy", new Point(5, 5));
            jPanel1.removeAll();
            jPanel1.add(inst);
            jPanel1.setVisible(true);
            jPanel1.doLayout();
            scalePreviewPanel(inst.getWidth(), inst.getHeight());
            AxoObjectAbstract t = inst.getType();
            if (t != null) {
                String description = t.sDescription == null || t.sDescription.isEmpty() ? o.sDescription : t.sDescription;
                String path = t.sPath == null ? o.sPath : t.sPath;
                String author = t.sAuthor == null ? o.sAuthor : t.sAuthor;
                String license = t.sLicense == null ? o.sLicense : t.sLicense;
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

    private void scalePreviewPanel(int originalWidth, int originalHeight) {
        Dimension panelSize = new Dimension();
        panelSize.width = (int) Math.round(originalWidth * p.zoomUI.getScale());
        panelSize.height = (int) Math.round(originalHeight * p.zoomUI.getScale());
        jPanel1.setPreferredSize(panelSize);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    static DefaultMutableTreeNode PopulateJTree(AxoObjectTreeNode anode, DefaultMutableTreeNode root) {
        for (String n : anode.SubNodes.keySet()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(anode.SubNodes.get(n));
            root.add(PopulateJTree(anode.SubNodes.get(n), node));
        }
        for (AxoObjectAbstract n : anode.Objects) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(n);
            root.add(node);
        }
        return root;
    }

    void ExpandJTreeToEl(AxoObjectAbstract s) {
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
        ArrayList<AxoObjectAbstract> listData = new ArrayList<AxoObjectAbstract>();
        if ((s == null) || s.isEmpty()) {
            for (AxoObjectAbstract o : MainFrame.axoObjects.ObjectList) {
                listData.add(o);
            }
            jList1.setListData(listData.toArray());
//            jList1.doLayout();
//            jList1.revalidate();
        } else {
            // exact match first
            for (AxoObjectAbstract o : MainFrame.axoObjects.ObjectList) {
                if (o.id.equals(s)) {
                    listData.add(o);
                }
            }
            for (AxoObjectAbstract o : MainFrame.axoObjects.ObjectList) {
                if (o.id.startsWith(s)) {
                    if (!listData.contains(o)) {
                        listData.add(o);
                    }
                }
            }
            for (AxoObjectAbstract o : MainFrame.axoObjects.ObjectList) {
                if (o.id.contains(s)) {
                    if (!listData.contains(o)) {
                        listData.add(o);
                    }
                }
            }
            for (AxoObjectAbstract o : MainFrame.axoObjects.ObjectList) {
                if (o.sDescription != null && o.sDescription.contains(s)) {
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
                ArrayList<AxoObjectAbstract> objs = MainFrame.axoObjects.GetAxoObjectFromName(s, p.GetCurrentWorkingDirectory());
                if ((objs != null) && (objs.size() > 0)) {
                    jList1.setListData(objs.toArray());
                    SetPreview(objs.get(0));
                }
            }
        }
    }

    boolean accepted = false;

    void Cancel() {
        accepted = false;
        setVisible(false);
    }

    void Accept() {
        if (!accepted) {
            accepted = true;
            setVisible(false);
            AxoObjectAbstract x = type;
            if (x == null) {
                ArrayList<AxoObjectAbstract> objs = MainFrame.axoObjects.GetAxoObjectFromName(jTextFieldObjName.getText(), p.GetCurrentWorkingDirectory());
                if ((objs != null) && (!objs.isEmpty())) {
                    x = objs.get(0);
                    jTextFieldObjName.setText("");
                }
            }
            if (x != null) {
                if (target_object == null) {
                    p.AddObjectInstance(x, new Point(patchLocX, patchLocY));
                } else {
                    p.ChangeObjectInstanceType(target_object, x);
                    p.cleanUpIntermediateChangeStates(2);
                }
            }
            setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jTextFieldObjName = new javax.swing.JTextField();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

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

        jSplitPane1.setDividerLocation(186);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(83, 50));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(600, 365));

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jTextFieldObjName.setAlignmentX(0.0F);
        jTextFieldObjName.setMaximumSize(new java.awt.Dimension(10000, 20));
        jTextFieldObjName.setMinimumSize(new java.awt.Dimension(100, 20));
        jTextFieldObjName.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldObjName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldObjNameActionPerformed(evt);
            }
        });
        jPanel2.add(jTextFieldObjName);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setResizeWeight(0.5);
        jSplitPane3.setMinimumSize(new java.awt.Dimension(126, 95));

        jScrollPane3.setMinimumSize(new java.awt.Dimension(24, 64));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setAlignmentX(0.0F);
        jList1.setMaximumSize(null);
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

        jScrollPane2.setBackground(java.awt.Color.lightGray);

        jLayeredPane1.setBackground(java.awt.Color.lightGray);
        jLayeredPane1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jPanel1.setBackground(java.awt.Color.lightGray);
        jPanel1.setEnabled(false);
        jPanel1.setFocusable(false);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        jLayeredPane1.add(jPanel1);

        jScrollPane2.setViewportView(jLayeredPane1);

        jSplitPane2.setRightComponent(jScrollPane2);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setMinimumSize(new java.awt.Dimension(6, 63));

        jTextPane1.setEditable(false);
        jTextPane1.setFocusCycleRoot(false);
        jTextPane1.setFocusable(false);
        jTextPane1.setRequestFocusEnabled(false);
        jScrollPane4.setViewportView(jTextPane1);

        jSplitPane2.setTopComponent(jScrollPane4);

        jSplitPane1.setRightComponent(jSplitPane2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
//        System.out.println("Source = " + evt.getSource());
//        Accept();
    }//GEN-LAST:event_formWindowLostFocus

    private void jTextFieldObjNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldObjNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldObjNameActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTextField jTextFieldObjName;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables

}
