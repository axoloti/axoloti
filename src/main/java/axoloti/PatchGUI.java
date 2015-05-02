/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import axoloti.inlets.InletInstance;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectFromPatch;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjects;
import axoloti.outlets.OutletInstance;
import axoloti.utils.Constants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "patch-1.0")
public class PatchGUI extends Patch {

    JLayeredPane Layers = new JLayeredPane();
    JPanel ObjectLayer = new JPanel();
    JPanel NetLayer = new JPanel();
    public JPanel SelectionRectLayer = new JPanel();
    SelectionRectangle selectionrectangle = new SelectionRectangle();
    Point selectionRectStart;
    Boolean Button1down = false;
    public AxoObjectFromPatch ObjEditor;

    public PatchGUI() {
        super();
        Layers.setLayout(null);
        Layers.setSize(5000, 5000);
        Layers.setLocation(0, 0);
        ObjectLayer.setLayout(null);
        ObjectLayer.setSize(5000, 5000);
        ObjectLayer.setLocation(0, 0);
        NetLayer.setLayout(null);
        NetLayer.setSize(5000, 5000);
        NetLayer.setLocation(0, 0);
        SelectionRectLayer.setLayout(null);
        SelectionRectLayer.setSize(5000, 5000);
        SelectionRectLayer.setLocation(0, 0);

        Layers.add(ObjectLayer, new Integer(1));
        Layers.add(NetLayer, new Integer(2));
        Layers.add(SelectionRectLayer, new Integer(3));
        SelectionRectLayer.add(selectionrectangle);
        selectionrectangle.setLocation(100, 100);
        selectionrectangle.setSize(100, 100);
        selectionrectangle.setOpaque(false);
        selectionrectangle.setVisible(false);
        ObjectLayer.setOpaque(false);
        NetLayer.setOpaque(false);
        SelectionRectLayer.setOpaque(false);
        Layers.setSize(5000, 5000);
        Layers.setVisible(true);
//        ObjectLayer.setFocusable(true);
        Layers.setBackground(Color.LIGHT_GRAY);
        Layers.setOpaque(true);
        Layers.invalidate();
        Layers.repaint();
        Layers.doLayout();
        //add(Layers);
//        SelectionRectLayer.add(cs);

        TransferHandler TH = new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                System.out.println("COPY_OR_MOVE");
                return COPY_OR_MOVE;
            }

            @Override
            public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
                Patch p = GetSelectedObjects();
                p.PreSerialize();
                Serializer serializer = new Persister();
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    serializer.write(p, baos);
                    StringSelection s = new StringSelection(baos.toString());
                    clip.setContents(s, (ClipboardOwner) null);
                } catch (Exception ex) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (action == MOVE) {
                    deleteSelectedAxoObjInstances();
                }
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                //System.out.println("importdata 1" + support.get);
                return super.importData(support);
            }

            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    //System.out.println("importdata 2 " + t.getTransferData(DataFlavor.stringFlavor));
                    if (!locked) {
                        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            paste((String) t.getTransferData(DataFlavor.stringFlavor), false);
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PatchGUI.class.getName()).log(Level.SEVERE, "paste", ex);
                } catch (IOException ex) {
                    Logger.getLogger(PatchGUI.class.getName()).log(Level.SEVERE, "paste", ex);
                }
                return true;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                System.out.println("createTransferable");
                return new StringSelection("copy");
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                boolean r = super.canImport(support);
                return r;
            }

        };
        Layers.setTransferHandler(TH);
        /*
         Layers.setDropTarget(new DropTarget(ObjectLayer, new DropTargetListener() {
         @Override
         public void dragEnter(DropTargetDragEvent dtde) {
         }

         @Override
         public void dragOver(DropTargetDragEvent dtde) {
         for (Component cmp : SelectionRectLayer.getComponents()) {
         if (cmp instanceof NetDragging) {
         NetDragging nd = (NetDragging) cmp;
         Point ps = SelectionRectLayer.getLocationOnScreen();
         Point pl = new Point(dtde.getLocation().x - ps.x, dtde.getLocation().y - ps.y);
         nd.SetDragPoint(dtde.getLocation());
         SelectionRectLayer.repaint();
         }
         }
         }

         @Override
         public void dropActionChanged(DropTargetDragEvent dtde) {

         }

         @Override
         public void dragExit(DropTargetEvent dte) {

         }

         @Override
         public void drop(DropTargetDropEvent dtde) {

         }
         }));
         */
        InputMap inputMap = Layers.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "cut");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "copy");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "paste");

        ActionMap map = Layers.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        Layers.setEnabled(true);
        Layers.setFocusable(true);
        Layers.setFocusCycleRoot(true);
        Layers.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int xsteps = 1;
                int ysteps = 1;
                if (!ke.isShiftDown()) {
                    xsteps = Constants.xgrid;
                    ysteps = Constants.ygrid;
                }
                if ((ke.getKeyCode() == KeyEvent.VK_SPACE)
                        || ((ke.getKeyCode() == KeyEvent.VK_N) && (!ke.isControlDown()) && (!ke.isMetaDown()))
                        || ((ke.getKeyCode() == KeyEvent.VK_1) && (ke.isControlDown()))) {
                    Point p = Layers.getMousePosition();
                    if (p != null) {
                        ke.consume();
                        ShowClassSelector(p, null);
                    }
                } else if (((ke.getKeyCode() == KeyEvent.VK_C) && (!ke.isControlDown()) && (!ke.isMetaDown()))
                        || ((ke.getKeyCode() == KeyEvent.VK_5) && (ke.isControlDown()))) {
                    AxoObjectInstanceAbstract ao = AddObjectInstance(MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/comment", null).get(0), Layers.getMousePosition());
                    ao.addInstanceNameEditor();
                    ke.consume();
                } else if ((ke.getKeyCode() == KeyEvent.VK_DELETE) || (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                    deleteSelectedAxoObjInstances();
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_UP) {
                    MoveSelectedAxoObjInstances(Direction.UP, xsteps, ysteps);
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
                    MoveSelectedAxoObjInstances(Direction.DOWN, xsteps, ysteps);
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                    MoveSelectedAxoObjInstances(Direction.RIGHT, xsteps, ysteps);
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                    MoveSelectedAxoObjInstances(Direction.LEFT, xsteps, ysteps);
                    ke.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        });

        Layers.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    for (AxoObjectInstanceAbstract o : objectinstances) {
                        o.SetSelected(false);
                    }
                    if (me.getClickCount() == 2) {
                        ShowClassSelector(me.getPoint(), null);
                        me.consume();
                    } else {
                        me.consume();
                        if ((osf != null) && osf.isVisible()) {
                            osf.Accept();
                        }
                        Layers.requestFocusInWindow();
                    }
                } else {
                    me.consume();
                    if ((osf != null) && osf.isVisible()) {
                        osf.Cancel();
                    }
                    Layers.requestFocusInWindow();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    selectionRectStart = me.getPoint();
                    Button1down = true;
                    Layers.requestFocusInWindow();
                } else {
                    Button1down = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    Rectangle r = selectionrectangle.getBounds();
                    for (AxoObjectInstanceAbstract o : objectinstances) {
                        o.SetSelected(o.getBounds().intersects(r));
                    }
                    selectionrectangle.setVisible(false);
                }
                Button1down = false;
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });

        Layers.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent ev) {
                if (Button1down) {
                    int x1 = selectionRectStart.x;
                    int y1 = selectionRectStart.y;
                    int x2 = ev.getX();
                    int y2 = ev.getY();
                    int xmin = x1 < x2 ? x1 : x2;
                    int xmax = x1 > x2 ? x1 : x2;
                    int ymin = y1 < y2 ? y1 : y2;
                    int ymax = y1 > y2 ? y1 : y2;
                    selectionrectangle.setLocation(xmin, ymin);
                    selectionrectangle.setSize(xmax - xmin, ymax - ymin);
                    selectionrectangle.setVisible(true);
                }
            }
        });
        Layers.setVisible(true);
        Layers.invalidate();
        Layers.repaint();

        DropTarget dt = new DropTarget() {

            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                for (Component cmp : SelectionRectLayer.getComponents()) {
                    if (cmp instanceof NetDragging) {
                        NetDragging nd = (NetDragging) cmp;
                        nd.SetDragPoint(dtde.getLocation());
                        SelectionRectLayer.repaint();
                        break;
                    }
                }
            }

            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                Transferable t = dtde.getTransferable();
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    OutletInstance ol;
                    InletInstance il;
                    if ((ol = getOutletByReference(s)) != null) {
                        disconnect(ol);
                    } else if ((il = getInletByReference(s)) != null) {
                        disconnect(il);
                    }
                    /*
                     AxoObjectAbstract obj = MainFrame.axoObjects.GetAxoObject(s);
                     if (obj != null) {
                     AddObjectInstance(obj, dtde.getLocation());
                     } else {
                     System.out.println("spilled on patch: " + s);
                     }
                     */
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                super.drop(dtde);
            }
        ;
        };
        Layers.setDropTarget(dt);

        Layers.setPreferredSize(new Dimension(5000, 5000));
        Layers.setSize(Layers.getPreferredSize());
        Layers.setVisible(true);
        Layers.setLocation(0, 0);
        Layers.setPreferredSize(new Dimension(5000, 5000));
    }

    void paste(String v, boolean restoreConnectionsToExternalOutlets) {
        SelectNone();
        Serializer serializer = new Persister();
        try {
            PatchGUI p = serializer.read(PatchGUI.class, v);
            HashMap<String, String> dict = new HashMap<String, String>();
            for (AxoObjectInstanceAbstract o : p.objectinstances) {
                String original_name = o.getInstanceName();
                String new_name = original_name;
                String ss[] = new_name.split("_");
                boolean hasNumeralSuffix = false;
                try {
                    if ((ss.length > 1) && (Integer.toString(Integer.parseInt(ss[ss.length - 1]))).equals(ss[ss.length - 1])) {
                        hasNumeralSuffix = true;
                    }
                } catch (NumberFormatException e) {
                }
                if (hasNumeralSuffix) {
                    int n = Integer.parseInt(ss[ss.length - 1]) + 1;
                    String bs = original_name.substring(0, original_name.length() - ss[ss.length - 1].length());
                    while (GetObjectInstance(new_name) != null) {
                        new_name = bs + n++;
                    }
                    while (dict.containsKey(new_name)) {
                        new_name = bs + n++;
                    }
                } else {
                    while (GetObjectInstance(new_name) != null) {
                        new_name = new_name + "_";
                    }
                    while (dict.containsKey(new_name)) {
                        new_name = new_name + "_";
                    }
                }
                if (!new_name.equals(original_name)) {
                    o.setInstanceName(new_name);
                }
                dict.put(original_name, new_name);
                while (getObjectAtLocation(o.getX(), o.getY()) != null) {
                    o.setLocation(o.getX() + Constants.xgrid, o.getY() + Constants.ygrid);
                }
                o.patch = this;
                objectinstances.add(o);
                ObjectLayer.add(o, 0);
                o.PostConstructor();
                o.SetSelected(true);
            }
            for (Net n : p.nets) {
                InletInstance connectedInlet = null;
                OutletInstance connectedOutlet = null;
                if (n.source != null) {
                    ArrayList<OutletInstance> source2 = new ArrayList<OutletInstance>();
                    for (OutletInstance o : n.source) {
                        //String r[] = o.name.split(" ");
                        int sepIndex = o.name.lastIndexOf(' ');
                        String objname = o.name.substring(0, sepIndex);
                        String outletname = o.name.substring(sepIndex + 1);
                        if ((objname.length() > 1) && (outletname.length() > 1)) {
                            String on2 = dict.get(objname);
                            if (on2 != null) {
//                                o.name = on2 + " " + r[1];
                                OutletInstance i = new OutletInstance();
                                i.name = on2 + " " + outletname;
                                source2.add(i);
                            } else if (restoreConnectionsToExternalOutlets) {
                                AxoObjectInstanceAbstract obj = GetObjectInstance(objname);
                                if ((obj != null) && (connectedOutlet == null)) {
                                    OutletInstance oi = obj.GetOutletInstance(outletname);
                                    if (oi != null) {
                                        connectedOutlet = oi;
                                    }
                                }
                            }
                        }
                    }
                    n.source = source2;
                }
                if (n.dest != null) {
                    ArrayList<InletInstance> dest2 = new ArrayList<InletInstance>();
                    for (InletInstance o : n.dest) {
                        int sepIndex = o.name.lastIndexOf(' ');
                        String objname = o.name.substring(0, sepIndex);
                        String inletname = o.name.substring(sepIndex + 1);
                        if ((objname.length() > 1) && (inletname.length() > 1)) {
                            String on2 = dict.get(objname);
                            if (on2 != null) {
                                InletInstance i = new InletInstance();
                                i.name = on2 + " " + inletname;
                                dest2.add(i);
                            } else {/*
                                 AxoObjectInstanceAbstract obj = GetObjectInstance(r[0]);
                                 if ((obj != null) && (connectedInlet == null)) {
                                 InletInstance ii = obj.GetInletInstance(r[1]);
                                 if (ii != null) {
                                 connectedInlet = ii;
                                 }
                                 }*/

                            }
                        }
                    }
                    n.dest = dest2;
                }
                if (n.source.size() + n.dest.size() > 1) {
                    if ((connectedInlet == null) && (connectedOutlet == null)) {
                        n.patch = this;
                        n.PostConstructor();
                        nets.add(n);
                        NetLayer.add(n);
                    } else if (connectedInlet != null) {
                        for (InletInstance o : n.dest) {
                            InletInstance o2 = getInletByReference(o.name);
                            if ((o2 != null) && (o2 != connectedInlet)) {
                                AddConnection(connectedInlet, o2);
                            }
                        }
                        for (OutletInstance o : n.source) {
                            OutletInstance o2 = getOutletByReference(o.name);
                            if (o2 != null) {
                                AddConnection(connectedInlet, o2);
                            }
                        }
                    } else if (connectedOutlet != null) {
                        for (InletInstance o : n.dest) {
                            InletInstance o2 = getInletByReference(o.name);
                            if (o2 != null) {
                                AddConnection(o2, connectedOutlet);
                            }
                        }
                        for (OutletInstance o : n.source) {
                            OutletInstance o2 = getOutletByReference(o.name);
                            if ((o2 != null) && (o2 != connectedOutlet)) {
                                AddConnection(connectedOutlet, o2);
                            }
                        }
                    }
                }
            }
            AdjustSize();
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    AxoObjectInstanceAbstract getObjectAtLocation(int x, int y) {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if ((o.getX() == x) && (o.getY() == y)) {
                return o;
            }
        }
        return null;
    }
    public ObjectSearchFrame osf;

    public void ShowClassSelector(Point p, AxoObjectInstanceAbstract o) {
        if (IsLocked()) {
            return;
        }
        if (osf == null) {
            osf = new ObjectSearchFrame(this);
        }
        osf.Launch(p, o);
    }

    void SelectAll() {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            o.SetSelected(true);
        }
    }

    public void SelectNone() {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            o.SetSelected(false);
        }
    }
    TextEditor NotesFrame;

    void ShowNotesFrame() {
        if (NotesFrame == null) {
            NotesFrame = new TextEditor(new StringRef());
            NotesFrame.setTitle(patchframe.getTitle() + ":notes");
            NotesFrame.SetText(notes);
            NotesFrame.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    notes = NotesFrame.GetText();
                }
            });
        }
        NotesFrame.setVisible(true);
        NotesFrame.toFront();
    }

    enum Direction {

        UP, LEFT, DOWN, RIGHT
    }

    Patch GetSelectedObjects() {
        Patch p = new Patch();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.IsSelected()) {
                p.objectinstances.add(o);
            }
        }
        p.nets = new ArrayList<Net>();
        for (Net n : nets) {
            int sel = 0;
            for (InletInstance i : n.dest) {
                if (i.axoObj.IsSelected()) {
                    sel++;
                }
            }
            for (OutletInstance i : n.source) {
                if (i.axoObj.IsSelected()) {
                    sel++;
                }
            }
            if (sel > 0) {
                p.nets.add(n);
            }
        }
        p.PreSerialize();
        return p;
    }

    void MoveSelectedAxoObjInstances(Direction dir, int xsteps, int ysteps) {
        if (!locked) {
            int xgrid = 1;
            int ygrid = 1;
            int xstep = 0;
            int ystep = 0;
            switch (dir) {
                case DOWN:
                    ystep = ysteps;
                    ygrid = ysteps;
                    break;
                case UP:
                    ystep = -ysteps;
                    ygrid = ysteps;
                    break;
                case LEFT:
                    xstep = -xsteps;
                    xgrid = xsteps;
                    break;
                case RIGHT:
                    xstep = xsteps;
                    xgrid = xsteps;
                    break;
            }
            boolean isUpdate = false;
            for (AxoObjectInstanceAbstract o : objectinstances) {
                if (o.IsSelected()) {
                    isUpdate = true;
                    Point p = o.getLocation();
                    p.x = p.x + xstep;
                    p.y = p.y + ystep;
                    p.x = xgrid * (p.x / xgrid);
                    p.y = ygrid * (p.y / ygrid);
                    o.SetLocation(p.x, p.y);
                }
            }
            if (isUpdate) {
                AdjustSize();
                Layers.repaint();
            }
        } else {
            Logger.getLogger(PatchGUI.class.getName()).log(Level.INFO, "can't move: locked");
        }
    }

    @Override
    public void PostContructor() {
        super.PostContructor();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            ObjectLayer.add(o);
        }
        for (Net n : nets) {
            NetLayer.add(n);
        }
        Layers.setPreferredSize(new Dimension(5000, 5000));
        AdjustSize();
    }

    @Override
    public void setFileNamePath(String FileNamePath) {
        super.setFileNamePath(FileNamePath);
        patchframe.setTitle(FileNamePath);
    }

    @Override
    public Net AddConnection(InletInstance il, OutletInstance ol) {
        Net n = super.AddConnection(il, ol);
        if (n != null) {
            NetLayer.add(n);
        }
        return n;
    }

    @Override
    public Net AddConnection(OutletInstance il, OutletInstance ol) {
        Net n = super.AddConnection(il, ol);
        if (n != null) {
            NetLayer.add(n);
        }
        return n;
    }

    @Override
    public Net AddConnection(InletInstance il, InletInstance ol) {
        Net n = super.AddConnection(il, ol);
        if (n != null) {
            NetLayer.add(n);
        }
        return n;
    }

    @Override
    public Net disconnect(InletInstance ii) {
        Net n = super.disconnect(ii);
        Layers.repaint();
        return n;
    }

    @Override
    public Net disconnect(OutletInstance oi) {
        Net n = super.disconnect(oi);
        Layers.repaint();
        return n;
    }

    @Override
    public Net delete(Net n) {
        Net nn = super.delete(n);
        if (nn != null) {
            NetLayer.remove(n);
            Layers.repaint();
        }
        return nn;
    }

    @Override
    public void delete(AxoObjectInstanceAbstract o) {
        super.delete(o);
        ObjectLayer.remove(o);
        Layers.repaint();
    }

    @Override
    public AxoObjectInstanceAbstract AddObjectInstance(AxoObjectAbstract obj, Point loc) {
        AxoObjectInstanceAbstract objinst = super.AddObjectInstance(obj, loc);
        if (objinst != null) {
            ObjectLayer.add(objinst);
            objinst.doLayout();
            AdjustSize();
            Layers.revalidate();
        }
        return objinst;
    }

    void SetCordsInBackground(boolean b) {
        if (b) {
            Layers.removeAll();
            Layers.add(ObjectLayer, new Integer(2));
            Layers.add(NetLayer, new Integer(1));
            Layers.add(SelectionRectLayer, new Integer(3));
        } else {
            Layers.removeAll();
            Layers.add(ObjectLayer, new Integer(1));
            Layers.add(NetLayer, new Integer(2));
            Layers.add(SelectionRectLayer, new Integer(3));
        }
    }

    @Override
    void GoLive() {
        Patch p = GetQCmdProcessor().getPatch();
        if (p != null) {
            p.Unlock();
        }
        super.GoLive();
    }

    @Override
    public void Lock() {
        super.Lock();
        patchframe.SetLive(true);
        Layers.setBackground(Color.DARK_GRAY);
    }

    @Override
    public void Unlock() {
        super.Unlock();
        patchframe.SetLive(false);
        Layers.setBackground(Color.LIGHT_GRAY);
    }

    @Override
    void invalidate() {
        super.invalidate();
        Layers.invalidate();
    }

    @Override
    public void repaint() {
        super.repaint();
        Layers.repaint();
    }

    @Override
    void SetDSPLoad(int pct) {
        patchframe.ShowDSPLoad(pct);
    }

    @Override
    public void AdjustSize() {
        Dimension s = GetSize();
        if (s.width < Layers.getParent().getWidth()) {
            s.width = Layers.getParent().getWidth();
        }
        if (s.height < Layers.getParent().getHeight()) {
            s.height = Layers.getParent().getHeight();
        }
        Layers.setSize(s);
        Layers.setPreferredSize(s);
    }

    @Override
    void PreSerialize() {
        super.PreSerialize();
        if (NotesFrame != null) {
            this.notes = NotesFrame.GetText();
        }
    }

    @Override
    void save(File f) {
        super.save(f);
        if (ObjEditor != null) {
            ObjEditor.UpdateObject();
        }
    }
}
