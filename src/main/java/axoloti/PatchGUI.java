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

import axoloti.datatypes.DataType;
import axoloti.inlets.InletInstance;
import axoloti.iolet.IoletAbstract;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectFromPatch;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjects;
import axoloti.outlets.OutletInstance;
import axoloti.utils.Constants;
import java.awt.Component;
import java.awt.Cursor;
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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.plaf.LayerUI;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import qcmds.QCmdProcessor;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "patch-1.0")
public class PatchGUI extends Patch {

    // shortcut patch names
    final static String patchComment = "patch/comment";
    final static String patchInlet = "patch/inlet";
    final static String patchOutlet = "patch/outlet";
    final static String patchAudio = "audio/";
    final static String patchAudioOut = "audio/out stereo";
    final static String patchMidi = "midi";
    final static String patchMidiKey = "midi/in/keyb";
    final static String patchDisplay = "disp/";

    public JLayeredPane Layers = new JLayeredPane();

    public JPanel objectLayerPanel = new JPanel();
    public JPanel draggedObjectLayerPanel = new JPanel();

    public ZoomUI zoomUI = new ZoomUI(Constants.INITIAL_ZOOM,
            Constants.ZOOM_STEP,
            Constants.MAXIMUM_ZOOM,
            Constants.MINIMUM_ZOOM,
            this);
    JLayer<JComponent> objectLayer = new JLayer<JComponent>(objectLayerPanel, zoomUI);
    JLayer<JComponent> draggedObjectLayer = new JLayer<JComponent>(draggedObjectLayerPanel, zoomUI);

    public JPanel netLayerPanel = new JPanel();
    JLayer<JComponent> netLayer = new JLayer<JComponent>(netLayerPanel, zoomUI);

    public JPanel selectionRectLayerPanel = new JPanel();
    JLayer<JComponent> selectionRectLayer = new JLayer<JComponent>(selectionRectLayerPanel, zoomUI);

    public JPanel unzoomedLayerPanel = new JPanel();
    JLayer<JComponent> unzoomedLayer = new JLayer<JComponent>(unzoomedLayerPanel, new LayerUI<JComponent>());

    SelectionRectangle selectionrectangle = new SelectionRectangle();
    Point selectionRectStart;
    Point panOrigin;
    Boolean Button1down = false;
    Boolean Button2down = false;
    public AxoObjectFromPatch ObjEditor;

    public PatchGUI() {
        super();

        JComponent[] layerComponents = {
            Layers, objectLayerPanel, draggedObjectLayerPanel, netLayerPanel,
            selectionRectLayerPanel, unzoomedLayerPanel,
            objectLayer, draggedObjectLayer, netLayer, selectionRectLayer,
            unzoomedLayer};
        for (JComponent c : layerComponents) {
            c.setLayout(null);
            c.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
            c.setLocation(0, 0);
            c.setOpaque(false);
        }

        Layers.add(objectLayer, new Integer(1));
        Layers.add(draggedObjectLayer, new Integer(2));
        Layers.add(netLayer, new Integer(3));
        Layers.add(selectionRectLayer, new Integer(4));
        Layers.add(unzoomedLayer, new Integer(5));

        objectLayerPanel.setName(Constants.OBJECT_LAYER_PANEL);
        draggedObjectLayerPanel.setName(Constants.DRAGGED_OBJECT_LAYER_PANEL);

        selectionRectLayerPanel.add(selectionrectangle);
        selectionrectangle.setLocation(100, 100);
        selectionrectangle.setSize(100, 100);
        selectionrectangle.setOpaque(false);
        selectionrectangle.setVisible(false);

        Layers.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        Layers.setVisible(true);
        Layers.setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
        Layers.setOpaque(true);
        Layers.invalidate();
        Layers.doLayout();

        TransferHandler TH = new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            @Override
            public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
                Patch p = GetSelectedObjects();
                if (p.objectinstances.isEmpty()) {
                    clip.setContents(new StringSelection(""), null);
                    return;
                }
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
                return super.importData(support);
            }

            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    if (!locked) {
                        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {

                            paste((String) t.getTransferData(DataFlavor.stringFlavor), comp.getMousePosition(), false);
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
                return new StringSelection("copy");
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                boolean r = super.canImport(support);
                return r;
            }

        };

        Layers.setTransferHandler(TH);

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
                    xsteps = Constants.X_GRID;
                    ysteps = Constants.Y_GRID;
                }
                if ((ke.getKeyCode() == KeyEvent.VK_SPACE)
                        || ((ke.getKeyCode() == KeyEvent.VK_N) && (!ke.isControlDown()) && (!ke.isMetaDown()))
                        || ((ke.getKeyCode() == KeyEvent.VK_1) && (ke.isControlDown()))) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, null);
                    }
                } else if (((ke.getKeyCode() == KeyEvent.VK_C) && (!ke.isControlDown()) && (!ke.isMetaDown()))
                        || ((ke.getKeyCode() == KeyEvent.VK_5) && (ke.isControlDown()))) {
                    AxoObjectInstanceAbstract ao = AddObjectInstance(MainFrame.axoObjects.GetAxoObjectFromName(patchComment, null).get(0), Layers.getMousePosition());
                    ao.addInstanceNameEditor();
                    ke.consume();
                } else if ((ke.getKeyCode() == KeyEvent.VK_I) && (!ke.isControlDown()) && (!ke.isMetaDown())) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, patchInlet);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_O) && (!ke.isControlDown()) && (!ke.isMetaDown())) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, patchOutlet);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_D) && (!ke.isControlDown()) && (!ke.isMetaDown())) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, patchDisplay);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_M) && (!ke.isControlDown()) && (!ke.isMetaDown())) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        if (ke.isShiftDown()) {
                            ShowClassSelector(p, null, patchMidiKey);
                        } else {
                            ShowClassSelector(p, null, patchMidi);
                        }
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_A) && (!ke.isControlDown()) && (!ke.isMetaDown())) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        if (ke.isShiftDown()) {
                            ShowClassSelector(p, null, patchAudioOut);
                        } else {
                            ShowClassSelector(p, null, patchAudio);
                        }
                    }
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
                } else if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                    panOrigin = Layers.getMousePosition();
                    if (panOrigin != null) {
                        zoomUI.removeZoomFactor(panOrigin);
                        PatchGUI.this.patchframe.getRootPane().setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        Button2down = true;
                        zoomUI.startPan();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ALT) {
                    PatchGUI.this.patchframe.getRootPane().setCursor(Cursor.getDefaultCursor());
                    Button2down = false;
                    zoomUI.stopPan();
                }
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
                        ShowClassSelector(me.getPoint(), null, null);
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
                    PatchGUI.this.selectionRectLayer.revalidate();
                    PatchGUI.this.selectionRectLayer.repaint();
                } else if (me.getButton() == MouseEvent.BUTTON2) {
                    PatchGUI.this.patchframe.getRootPane().setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    panOrigin = me.getPoint();
                    Button2down = true;
                } else {
                    Button1down = false;
                    Button2down = false;
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
                    PatchGUI.this.selectionRectLayer.revalidate();
                    PatchGUI.this.selectionRectLayer.repaint();
                }
                Button1down = false;
                Button2down = false;
                if (me.getButton() == MouseEvent.BUTTON2) {
                    PatchGUI.this.patchframe.getRootPane().setCursor(Cursor.getDefaultCursor());
                }

                zoomUI.cancelDrag();
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
                    int width = xmax - xmin;
                    int height = ymax - ymin;
                    selectionrectangle.setSize(width, height);
                    selectionrectangle.setVisible(true);
                    selectionRectLayer.repaint(new Rectangle(xmin, ymin, width, height));
                } else if (Button2down) {
                    handlePan(ev);
                }
            }

            @Override
            public void mouseMoved(MouseEvent ev) {
                if (ev.isAltDown()) {
                    handlePan(ev);
                }
            }
        });
        Layers.setVisible(true);

        DropTarget dt;
        dt = new DropTarget() {

            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                for (Component cmp : selectionRectLayerPanel.getComponents()) {
                    if (cmp instanceof NetDragging) {
                        NetDragging nd = (NetDragging) cmp;
                        nd.SetDragPoint(dtde.getLocation());
                        selectionRectLayerPanel.repaint();
                        Rectangle bounds = nd.getBounds();
                        zoomUI.scale(bounds);
                        selectionRectLayerPanel.repaint(bounds);
                        break;
                    }
                }
            }

            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                Transferable t = dtde.getTransferable();
                if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        List<File> flist = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        for (File f : flist) {
                            if (f.exists() && f.canRead()) {
                                AxoObjectAbstract o = new AxoObjectFromPatch(f);
                                String fn = f.getCanonicalPath();
                                if (GetCurrentWorkingDirectory() != null && fn.startsWith(GetCurrentWorkingDirectory())) {
                                    o.createdFromRelativePath = true;
                                }
                                AddObjectInstance(o, dtde.getLocation());
                            }
                        }
                        dtde.dropComplete(true);
                    } catch (UnsupportedFlavorException ex) {
                        Logger.getLogger(PatchGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PatchGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    zoomUI.cancelDrag();
                    return;
                }
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    String ss[] = s.split("::");
                    if (ss.length == 2) {
                        OutletInstance ol;
                        InletInstance il;
                        if ((ol = getOutletByReference(ss[0], ss[1])) != null) {
                            disconnect(ol);
                        } else if ((il = getInletByReference(ss[0], ss[1])) != null) {
                            disconnect(il);
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                zoomUI.cancelDrag();
                super.drop(dtde);
            }
        ;
        };

        Layers.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isShiftDown()) {
                    int notches = e.getWheelRotation();
                    Point origin = e.getPoint();
                    Constants.ZOOM_ACTION action = notches < 0 ? Constants.ZOOM_ACTION.IN : Constants.ZOOM_ACTION.OUT;
                    handleZoom(action, origin);
                }
            }
        });

        Layers.setDropTarget(dt);

        Dimension LayersSize = new Dimension(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        Layers.setPreferredSize(LayersSize);
        Layers.setSize(Layers.getPreferredSize());
        Layers.setVisible(true);
        Layers.setLocation(0, 0);
        Layers.setPreferredSize(LayersSize);
    }

    public void handlePan(MouseEvent ev) {
        int dx = panOrigin.x - ev.getX();
        int dy = panOrigin.y - ev.getY();
        JScrollBar horizontal = PatchGUI.this.getPatchframe().getScrollPane().getHorizontalScrollBar();
        JScrollBar vertical = PatchGUI.this.getPatchframe().getScrollPane().getVerticalScrollBar();
        int newHorizontalValue = Math.max(horizontal.getValue() + dx, 0);
        int newVerticalValue = Math.max(vertical.getValue() + dy, 0);
        if (!(horizontal.getValue() == 0 && newHorizontalValue == 0)) {
            horizontal.setValue(newHorizontalValue);
        }
        if (!(vertical.getValue() == 0 && newVerticalValue == 0)) {
            vertical.setValue(newVerticalValue);
        }
    }

    public void handleZoom(Constants.ZOOM_ACTION action, Point origin) {
        // this method implements zoom to mouse functionality
        // we perform the scale change
        // and then translate the viewport such that the point
        // under the mouse remains the same
        double oldScale = zoomUI.getScale();
        double pre_x = origin.x / oldScale;
        double pre_y = origin.y / oldScale;

        JScrollBar horizontalScrollBar = PatchGUI.this.getPatchframe().getScrollPane().getHorizontalScrollBar();
        JScrollBar verticalScrollBar = PatchGUI.this.getPatchframe().getScrollPane().getVerticalScrollBar();

        int horizOldValue = horizontalScrollBar.getValue();
        int vertOldValue = verticalScrollBar.getValue();

        if (action == Constants.ZOOM_ACTION.DEFAULT) {
            zoomUI.zoomToDefault();
        } else if (action == Constants.ZOOM_ACTION.IN) {
            zoomUI.zoomIn();
        } else if (action == Constants.ZOOM_ACTION.OUT) {
            zoomUI.zoomOut();
        }

        double newScale = zoomUI.getScale();

        PatchGUI.this.AdjustSize();

        double post_x = origin.x / newScale;
        double post_y = origin.y / newScale;
        double dx = (post_x - pre_x) * newScale;
        double dy = (post_y - pre_y) * newScale;

        int newHorizontalValue = (int) Math.round(horizOldValue - dx);
        int newVerticalValue = (int) Math.round(vertOldValue - dy);

        // adjust scrollbar extent to avoid jitter 
        // when fully zoomed out
        if (horizontalScrollBar.getMaximum() == horizontalScrollBar.getVisibleAmount()) {
            horizontalScrollBar.setVisibleAmount(horizontalScrollBar.getMaximum() - newHorizontalValue);
        }

        if (verticalScrollBar.getMaximum() == verticalScrollBar.getVisibleAmount()) {
            verticalScrollBar.setVisibleAmount(verticalScrollBar.getMaximum() - newVerticalValue);
        }

        horizontalScrollBar.setValue(newHorizontalValue);
        verticalScrollBar.setValue(newVerticalValue);
    }

    void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        SelectNone();
        pos = zoomUI.removeZoomFactor(pos);
        if (v.isEmpty()) {
            return;
        }
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchGUI p = serializer.read(PatchGUI.class, v);
            HashMap<String, String> dict = new HashMap<String, String>();
            for (AxoObjectInstanceAbstract o : p.objectinstances) {
                o.patch = this;
                AxoObjectAbstract obj = o.resolveType();
                Modulator[] m = obj.getModulators();
                if (m != null) {
                    if (Modulators == null) {
                        Modulators = new ArrayList<Modulator>();
                    }
                    for (Modulator mm : m) {
                        mm.objinst = o;
                        Modulators.add(mm);
                    }
                }

            }
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            for (AxoObjectInstanceAbstract o : p.objectinstances) {
                String original_name = o.getInstanceName();
                if (original_name != null) {
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
                }
                if (o.getX() < minX) {
                    minX = o.getX();
                }
                if (o.getY() < minY) {
                    minY = o.getY();
                }
                o.patch = this;
                objectinstances.add(o);
                objectLayerPanel.add(o, 0);
                o.PostConstructor();
                int newposx = o.getX();
                int newposy = o.getY();

                if (pos != null) {
                    // paste at cursor position, with delta snapped to grid
                    newposx += Constants.X_GRID * ((pos.x - minX + Constants.X_GRID / 2) / Constants.X_GRID);
                    newposy += Constants.Y_GRID * ((pos.y - minY + Constants.Y_GRID / 2) / Constants.Y_GRID);
                }
                while (getObjectAtLocation(newposx, newposy) != null) {
                    newposx += Constants.X_GRID;
                    newposy += Constants.Y_GRID;
                }
                o.setLocation(newposx, newposy);
                o.SetSelected(true);
            }
            for (Net n : p.nets) {
                InletInstance connectedInlet = null;
                OutletInstance connectedOutlet = null;
                if (n.source != null) {
                    ArrayList<OutletInstance> source2 = new ArrayList<OutletInstance>();
                    for (OutletInstance o : n.source) {
                        String objname = o.getObjname();
                        String outletname = o.getOutletname();
                        if ((objname != null) && (outletname != null)) {
                            String on2 = dict.get(objname);
                            if (on2 != null) {
//                                o.name = on2 + " " + r[1];
                                OutletInstance i = new OutletInstance();
                                i.outletname = outletname;
                                i.objname = on2;
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
                        String objname = o.getObjname();
                        String inletname = o.getInletname();
                        if ((objname != null) && (inletname != null)) {
                            String on2 = dict.get(objname);
                            if (on2 != null) {
                                InletInstance i = new InletInstance();
                                i.inletname = inletname;
                                i.objname = on2;
                                dest2.add(i);
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
                        netLayerPanel.add(n);
                    } else if (connectedInlet != null) {
                        for (InletInstance o : n.dest) {
                            InletInstance o2 = getInletByReference(o.getObjname(), o.getInletname());
                            if ((o2 != null) && (o2 != connectedInlet)) {
                                AddConnection(connectedInlet, o2);
                            }
                        }
                        for (OutletInstance o : n.source) {
                            OutletInstance o2 = getOutletByReference(o.getObjname(), o.getOutletname());
                            if (o2 != null) {
                                AddConnection(connectedInlet, o2);
                            }
                        }
                    } else if (connectedOutlet != null) {
                        for (InletInstance o : n.dest) {
                            InletInstance o2 = getInletByReference(o.getObjname(), o.getInletname());
                            if (o2 != null) {
                                AddConnection(o2, connectedOutlet);
                            }
                        }
                    }
                }
            }
            AdjustSize();
            SetDirty();
        } catch (javax.xml.stream.XMLStreamException ex) {
            // silence
        } catch (Exception ex) {
            Logger.getLogger(PatchGUI.class.getName()).log(Level.SEVERE, null, ex);
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

    public void ShowClassSelector(Point p, AxoObjectInstanceAbstract o, String searchString) {
        if (IsLocked()) {
            return;
        }
        if (osf == null) {
            osf = new ObjectSearchFrame(this);
        }
        osf.Launch(p, o, searchString);
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
            NotesFrame = new TextEditor(new StringRef(), getPatchframe());
            NotesFrame.setTitle("notes");
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
                if (i.GetObjectInstance().IsSelected()) {
                    sel++;
                }
            }
            for (OutletInstance i : n.source) {
                if (i.GetObjectInstance().IsSelected()) {
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
                SetDirty();
            }
        } else {
            Logger.getLogger(PatchGUI.class.getName()).log(Level.INFO, "can't move: locked");
        }
    }

    @Override
    public void PostContructor() {
        super.PostContructor();
        objectLayerPanel.removeAll();
        netLayerPanel.removeAll();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            objectLayerPanel.add(o);
        }
        for (Net n : nets) {
            netLayerPanel.add(n);
        }
        Layers.setPreferredSize(new Dimension(5000, 5000));
        AdjustSize();
        Layers.revalidate();
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
            netLayerPanel.add(n);
        }
        return n;
    }

    @Override
    public Net AddConnection(InletInstance il, InletInstance ol) {
        Net n = super.AddConnection(il, ol);
        if (n != null) {
            netLayerPanel.add(n);
        }
        return n;
    }

    @Override
    public Net disconnect(IoletAbstract io) {
        Net n = super.disconnect(io);
        netLayerPanel.repaint();
        return n;
    }

    @Override
    public Net delete(Net n) {
        Net nn = super.delete(n);
        if (nn != null) {
            netLayerPanel.remove(n);
        }
        netLayerPanel.repaint();
        return nn;
    }

    @Override
    public void delete(AxoObjectInstanceAbstract o) {
        super.delete(o);
        objectLayerPanel.remove(o);
        this.repaint();
        AdjustSize();
    }

    @Override
    public AxoObjectInstanceAbstract AddObjectInstance(AxoObjectAbstract obj, Point loc) {
        AxoObjectInstanceAbstract objinst = super.AddObjectInstance(obj, loc);
        if (objinst != null) {
            objectLayerPanel.add(objinst);
            SelectNone();
            objinst.SetSelected(true);
            objinst.doLayout();
            AdjustSize();
            Layers.revalidate();
        }
        return objinst;
    }

    void SetCordsInBackground(boolean b) {
        if (b) {
            Layers.removeAll();
            Layers.add(netLayer, new Integer(1));
            Layers.add(objectLayer, new Integer(2));
            Layers.add(draggedObjectLayer, new Integer(3));
            Layers.add(selectionRectLayer, new Integer(4));
            Layers.add(unzoomedLayer, new Integer(5));
        } else {
            Layers.removeAll();
            Layers.add(objectLayer, new Integer(1));
            Layers.add(draggedObjectLayer, new Integer(2));
            Layers.add(netLayer, new Integer(3));
            Layers.add(selectionRectLayer, new Integer(4));
            Layers.add(unzoomedLayer, new Integer(5));
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
        Layers.setBackground(Theme.getCurrentTheme().Patch_Locked_Background);
    }

    @Override
    public void Unlock() {
        super.Unlock();
        patchframe.SetLive(false);
        Layers.setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
    }

    @Override
    void invalidate() {
        super.invalidate();
        Layers.invalidate();
    }

    @Override
    public void repaint() {
        Layers.repaint();
    }

    @Override
    void SetDSPLoad(int pct) {
        patchframe.ShowDSPLoad(pct);
    }

    Dimension GetInitialSize() {
        int mx = 100; // min size
        int my = 100;
        for (AxoObjectInstanceAbstract i : objectinstances) {

            Dimension s = i.getPreferredSize();

            int ox = i.getX() + (int) s.getWidth();
            int oy = i.getY() + (int) s.getHeight();

            if (ox > mx) {
                mx = ox;
            }
            if (oy > my) {
                my = oy;
            }
        }
        // adding more, as getPreferredSize is not returning true dimension of
        // object
        return new Dimension(mx + 300, my + 300);
    }

    public void clampLayerSize(Dimension s) {
        if (s.width < Layers.getParent().getWidth()) {
            s.width = Layers.getParent().getWidth();
        }
        if (s.height < Layers.getParent().getHeight()) {
            s.height = Layers.getParent().getHeight();
        }
    }

    @Override
    public void AdjustSize() {
        Dimension s = GetSize();
        clampLayerSize(s);
        double zoom = zoomUI.getScale();
        s.height *= zoom;
        s.width *= zoom;
        clampLayerSize(s);
        Layers.setSize(s);
        Layers.setPreferredSize(s);
        Layers.repaint();
    }

    @Override
    void PreSerialize() {
        super.PreSerialize();
        if (NotesFrame != null) {
            this.notes = NotesFrame.GetText();
        }
        windowPos = patchframe.getBounds();
    }

    @Override
    boolean save(File f) {
        boolean b = super.save(f);
        if (ObjEditor != null) {
            ObjEditor.UpdateObject();
        }
        return b;
    }

    public static void OpenPatch(String name, InputStream stream) {
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchGUI patch1 = serializer.read(PatchGUI.class, stream);
            PatchFrame pf = new PatchFrame(patch1, QCmdProcessor.getQCmdProcessor());
            patch1.setFileNamePath(name);
            patch1.PostContructor();
            patch1.setFileNamePath(name);
            pf.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PatchFrame OpenPatchInvisible(File f) {
        for (DocumentWindow dw : DocumentWindowList.GetList()) {
            if (f.equals(dw.getFile())) {
                JFrame frame1 = dw.GetFrame();
                if (frame1 instanceof PatchFrame) {
                    return (PatchFrame) frame1;
                } else {
                    return null;
                }
            }
        }

        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchGUI patch1 = serializer.read(PatchGUI.class, f);
            PatchFrame pf = new PatchFrame(patch1, QCmdProcessor.getQCmdProcessor());
            patch1.setFileNamePath(f.getAbsolutePath());
            patch1.PostContructor();
            patch1.setFileNamePath(f.getPath());
            return pf;
        } catch (java.lang.reflect.InvocationTargetException ite) {
            if (ite.getTargetException() instanceof Patch.PatchVersionException) {
                Patch.PatchVersionException pve = (Patch.PatchVersionException) ite.getTargetException();
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Patch produced with newer version of Axoloti {0} {1}",
                        new Object[]{f.getAbsoluteFile(), pve.getMessage()});
            } else {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ite);
            }
            return null;
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static PatchFrame OpenPatch(File f) {
        PatchFrame pf = OpenPatchInvisible(f);
        pf.setVisible(true);
        pf.setState(java.awt.Frame.NORMAL);
        pf.toFront();
        return pf;
    }
    
    private Map<DataType, Boolean> cableTypeEnabled = new HashMap<DataType, Boolean>();
    
    public void setCableTypeEnabled(DataType type, boolean enabled) {
        cableTypeEnabled.put(type, enabled);
    }
    
    public Boolean isCableTypeEnabled(DataType type) {
        if(cableTypeEnabled.containsKey(type)) {
            return cableTypeEnabled.get(type);
        }
        else {
            return true;
        }
    }
    
    public void updateNetVisibility() {
        for(Net n : this.nets) {
            DataType d = n.GetDataType();
            if(d != null) {
                n.setVisible(isCableTypeEnabled(d));
            }
        }
        Layers.repaint();
    }
}
