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
package axoloti.swingui.patch;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.PatchView;
import axoloti.abstractui.PatchViewportView;
import axoloti.mvc.IModel;
import axoloti.mvc.array.ArrayView;
import axoloti.objectlibrary.AxoObjects;
import axoloti.patch.PatchModel;
import axoloti.patch.net.Net;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.preferences.Theme;
import axoloti.swingui.patch.net.NetView;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.AxoObjectInstanceViewFactory;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
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
public class PatchViewSwing extends PatchView {

    private static class JPanelAbsoluteLayout extends JPanel {

        JPanelAbsoluteLayout() {
            super(null);
        }

        @Override
        public void remove(Component comp) {
            // a null layout does not cause a repaint when removing a component?
            super.remove(comp);
            repaint(comp.getBounds());
        }
    }

    public PatchLayeredPane layers = new PatchLayeredPane();

    public JPanel objectLayerPanel = new JPanelAbsoluteLayout();
    private JPanel draggedObjectLayerPanel = new JPanelAbsoluteLayout();
    private JPanel netLayerPanel = new JPanelAbsoluteLayout();
    public JPanel selectionRectLayerPanel = new JPanelAbsoluteLayout();

    private SelectionRectangle selectionrectangle = new SelectionRectangle();
    private Point selectionRectStart;
    private Point panOrigin;

    public PatchViewSwing(PatchModel patchModel) {
        super(patchModel);

        layers.setLayout(null);
        layers.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        layers.setLocation(0, 0);

        JComponent[] layerComponents = {
            objectLayerPanel, draggedObjectLayerPanel, netLayerPanel,
            selectionRectLayerPanel};
        for (JComponent c : layerComponents) {
            c.setLayout(null);
            c.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
            c.setLocation(0, 0);
            c.setOpaque(false);
        }

        layers.add(objectLayerPanel, Integer.valueOf(1));
        layers.add(netLayerPanel, Integer.valueOf(2));
        layers.add(draggedObjectLayerPanel, Integer.valueOf(3));
        layers.add(selectionRectLayerPanel, Integer.valueOf(4));

        netLayerPanel.setName("netLayerPanel");
        selectionRectLayerPanel.setName("selectionRectLayerPanel");

        objectLayerPanel.setName(Constants.OBJECT_LAYER_PANEL);
        draggedObjectLayerPanel.setName(Constants.DRAGGED_OBJECT_LAYER_PANEL);

        selectionrectangle.setLocation(100, 100);
        selectionrectangle.setSize(100, 100);
        selectionrectangle.setOpaque(false);

        layers.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        layers.setVisible(true);
        layers.setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
        layers.setOpaque(true);
        layers.revalidate();

        layers.setTransferHandler(TH);

        InputMap inputMap = layers.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                KeyUtils.CONTROL_OR_CMD_MASK), "cut");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                KeyUtils.CONTROL_OR_CMD_MASK), "copy");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                KeyUtils.CONTROL_OR_CMD_MASK), "paste");

        ActionMap map = layers.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        layers.setEnabled(true);
        layers.setFocusable(true);
        layers.setFocusCycleRoot(true);
        layers.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                int xsteps = 1;
                int ysteps = 1;
                if (!ke.isShiftDown()) {
                    xsteps = Constants.X_GRID;
                    ysteps = Constants.Y_GRID;
                }
                if ((ke.getKeyCode() == KeyEvent.VK_SPACE)
                        || ((ke.getKeyCode() == KeyEvent.VK_N) && !KeyUtils.isControlOrCommandDown(ke))
                        || ((ke.getKeyCode() == KeyEvent.VK_1) && KeyUtils.isControlOrCommandDown(ke))) {
                    Point p = layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, null);
                    }
                } else if (((ke.getKeyCode() == KeyEvent.VK_C) && !KeyUtils.isControlOrCommandDown(ke))
                        || ((ke.getKeyCode() == KeyEvent.VK_5) && KeyUtils.isControlOrCommandDown(ke))) {
                    model.getController().addMetaUndo("add comment");
                    model.getController().addObjectInstance(AxoObjects.getAxoObjects().getAxoObjectFromName(patchComment, null).get(0), layers.getMousePosition());
                    ke.consume();
                } else if ((ke.getKeyCode() == KeyEvent.VK_I) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchInlet);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_O) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchOutlet);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_D) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchDisplay);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_M) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        if (ke.isShiftDown()) {
                            showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchMidiKey);
                        } else {
                            showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchMidi);
                        }
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_A) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        if (ke.isShiftDown()) {
                            showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchAudioOut);
                        } else {
                            showClassSelector(p, MouseInfo.getPointerInfo().getLocation(), null, patchAudio);
                        }
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_DELETE) || (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                    List<IAxoObjectInstance> selected = model.getSelectedObjects();
                    if (!selected.isEmpty()) {
                        model.getController().addMetaUndo("delete objects");
                        for (IAxoObjectInstance o : selected) {
                            model.getController().delete(o);
                        }
                    }
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_UP) {
                    model.getController().addMetaUndo("move up");
                    moveSelectedAxoObjInstances(Direction.UP, xsteps, ysteps);
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
                    model.getController().addMetaUndo("move down");
                    moveSelectedAxoObjInstances(Direction.DOWN, xsteps, ysteps);
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                    model.getController().addMetaUndo("move right");
                    moveSelectedAxoObjInstances(Direction.RIGHT, xsteps, ysteps);
                    ke.consume();
                } else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                    model.getController().addMetaUndo("move left");
                    moveSelectedAxoObjInstances(Direction.LEFT, xsteps, ysteps);
                    ke.consume();
                }
            }

        });

        layers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    model.getController().selectNone();
                    if (me.getClickCount() == 2) {
                        Point locOnScreen = me.getLocationOnScreen();
                        showClassSelector(me.getPoint(), locOnScreen, null, null);
                    } else {
                        if ((osf != null) && osf.isVisible()) {
                            osf.accept();
                        }
                        layers.requestFocusInWindow();
                    }
                    me.consume();
                } else {
                    if ((osf != null) && osf.isVisible()) {
                        osf.cancel();
                    }
                    layers.requestFocusInWindow();
                    me.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    selectionRectStart = me.getPoint();
                    selectionrectangle.setBounds(me.getX(), me.getY(), 1, 1);
                    selectionRectLayerPanel.add(selectionrectangle);
                    selectionrectangle.setVisible(true);
                    layers.requestFocusInWindow();
                    me.consume();
                } else {
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (selectionrectangle.isVisible() || me.getButton() == MouseEvent.BUTTON1) {
                    Rectangle r = selectionrectangle.getBounds();
                    for (IAxoObjectInstanceView o : objectInstanceViews) {
                        Rectangle bounds = new Rectangle(o.getLocation().x, o.getLocation().y, o.getSize().width, o.getSize().height);
                        o.getDModel().getController().changeSelected(bounds.intersects(r));
                    }
                    selectionrectangle.setVisible(false);
                    layers.getRootPane().setCursor(Cursor.getDefaultCursor());
                    me.consume();
                }
            }

        });

        layers.setVisible(true);

        layers.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent ev) {
                int x1 = selectionRectStart.x;
                int y1 = selectionRectStart.y;
                int x2 = ev.getX();
                int y2 = ev.getY();
                int xmin = x1 < x2 ? x1 : x2;
                int xmax = x1 > x2 ? x1 : x2;
                int ymin = y1 < y2 ? y1 : y2;
                int ymax = y1 > y2 ? y1 : y2;
                int width = xmax - xmin;
                int height = ymax - ymin;
                selectionrectangle.setBounds(xmin, ymin, width, height);
                ev.consume();
            }
        });

        layers.setDropTarget(dt);
        layers.setVisible(true);
    }

    public void scrollTo(Rectangle rect) {
        getViewportView().getComponent().scrollRectToVisible(rect);
    }

    private TransferHandler TH = new TransferHandler() {
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
            PatchModel p = getSelectedObjects();
            if (p.getObjectInstances().isEmpty()) {
                clip.setContents(new StringSelection(""), null);
                return;
            }
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
                model.getController().addMetaUndo("cut");
                for (IAxoObjectInstance o : p.getObjectInstances()) {
                    model.getController().delete(o);
                }
            }
        }


        @Override
        public boolean importData(JComponent comp, Transferable t) {
            try {
                if (!isLocked()) {
                    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {

                        paste((String) t.getTransferData(DataFlavor.stringFlavor), comp.getMousePosition(), false);
                    }
                }
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(PatchViewSwing.class.getName()).log(Level.SEVERE, "paste", ex);
            } catch (IOException ex) {
                Logger.getLogger(PatchViewSwing.class.getName()).log(Level.SEVERE, "paste", ex);
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

    @Override
    public void postConstructor() {
        layers.setPreferredSize(new Dimension(Constants.PATCH_SIZE, Constants.PATCH_SIZE));
        showPreset(0);
    }

    @Override
    public void setCordsInBackground(boolean b) {
        if (b) {
            layers.removeAll();
            layers.add(netLayerPanel, new Integer(1));
            layers.add(objectLayerPanel, new Integer(2));
            layers.add(draggedObjectLayerPanel, new Integer(3));
            layers.add(selectionRectLayerPanel, new Integer(4));
        } else {
            layers.removeAll();
            layers.add(objectLayerPanel, new Integer(1));
            layers.add(netLayerPanel, new Integer(2));
            layers.add(draggedObjectLayerPanel, new Integer(3));
            layers.add(selectionRectLayerPanel, new Integer(4));
        }
    }

    void clampLayerSize(Dimension s) {
        if (layers.getParent() != null) {
            if (s.width < layers.getParent().getWidth()) {
                s.width = layers.getParent().getWidth();
            }
            if (s.height < layers.getParent().getHeight()) {
                s.height = layers.getParent().getHeight();
            }
        }
    }

    @Override
    public void updateSize() {
        int maxX = 0;
        int maxY = 0;
        for (Component c : objectLayerPanel.getComponents()) {
            Rectangle r = c.getBounds();
            int x = r.x + r.width;
            if (x > maxX) {
                maxX = x;
            }
            int y = r.y + r.height;
            if (y > maxY) {
                maxY = y;
            }
        }
        Dimension s = new Dimension(maxX, maxY);
        clampLayerSize(s);
        if (!layers.getSize().equals(s)) {
            layers.setSize(s);
        }
        if (!layers.getPreferredSize().equals(s)) {
            layers.setPreferredSize(s);
        }
    }

    public void moveToDraggedLayer(AxoObjectInstanceViewAbstract o) {
        if (objectLayerPanel.isAncestorOf(o)) {
            objectLayerPanel.remove(o);
            draggedObjectLayerPanel.add(o);
        }
    }

    public void moveToObjectLayer(AxoObjectInstanceViewAbstract o, int zOrder) {
        if (draggedObjectLayerPanel.isAncestorOf(o)) {
            draggedObjectLayerPanel.remove(o);
            objectLayerPanel.add(o);
            objectLayerPanel.setComponentZOrder(o, zOrder);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (PatchModel.PATCH_OBJECTINSTANCES.is(evt)) {
            objectInstanceViews = objectInstanceViewSync.sync(objectInstanceViews, model.getObjectInstances());
        } else if (PatchModel.PATCH_NETS.is(evt)) {
            netViews = netViewSync.sync(netViews, model.getNets());
        } else if (PatchModel.PATCH_LOCKED.is(evt)) {
            if ((Boolean)evt.getNewValue() == false) {
                layers.setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
            } else {
                layers.setBackground(Theme.getCurrentTheme().Patch_Locked_Background);
            }
        }
    }

    @Override
    public PatchViewportView getViewportView() {
        return layers;
    }

    @Override
    public Point getLocationOnScreen() {
        return objectLayerPanel.getLocationOnScreen();
    }

    @Override
    public void requestFocus() {
        layers.requestFocus();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private final HashMap<IModel, AxoObjectInstanceViewAbstract> view_cache = new HashMap<>();

    private ArrayView<IAxoObjectInstanceView, IAxoObjectInstance> objectInstanceViewSync = new ArrayView<IAxoObjectInstanceView, IAxoObjectInstance>() {
        @Override
        protected IAxoObjectInstanceView viewFactory(IAxoObjectInstance model) {
            AxoObjectInstanceViewAbstract view = view_cache.get(model);
            if (view == null) {
                view = AxoObjectInstanceViewFactory.createView(model, PatchViewSwing.this);
            }
            if (objectLayerPanel != null) {
                objectLayerPanel.add(view);
                view.resizeToGrid();
                updateSize();
                view.repaint();
            }
            return view;
        }

        @Override
        protected void updateUI(List<IAxoObjectInstanceView> views) {
            updateSize();
        }

        @Override
        protected void removeView(IAxoObjectInstanceView view) {
            view.dispose();
            objectLayerPanel.remove((AxoObjectInstanceViewAbstract) view);
            objectLayerPanel.repaint(((AxoObjectInstanceViewAbstract) view).getBounds());
            view_cache.put(view.getDModel(), (AxoObjectInstanceViewAbstract) view);
        }

    };

    private ArrayView<INetView, Net> netViewSync = new ArrayView<INetView, Net>() {
        @Override
        protected INetView viewFactory(Net net) {
            NetView view = new NetView(net, PatchViewSwing.this);
            net.getController().addView(view);
            if (netLayerPanel != null) {
                netLayerPanel.add(view);
            }
            view.repaint();
            return view;
        }

        @Override
        protected void updateUI(List<INetView> views) {
        }

        @Override
        protected void removeView(INetView view) {
            netLayerPanel.remove((NetView) view);
            netLayerPanel.repaint(((NetView) view).getBounds());
        }

    };

}
