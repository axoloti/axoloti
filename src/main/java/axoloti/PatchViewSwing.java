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

import axoloti.object.AxoObjects;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY_OR_MOVE;
import static javax.swing.TransferHandler.MOVE;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "patch-1.0")
public class PatchViewSwing extends PatchView {

    public PatchLayeredPane Layers = new PatchLayeredPane();

    public JPanel objectLayerPanel = new JPanel();
    public JPanel draggedObjectLayerPanel = new JPanel();
    public JPanel netLayerPanel = new JPanel();
    public JPanel selectionRectLayerPanel = new JPanel();

    JLayer<JComponent> objectLayer = new JLayer<JComponent>(objectLayerPanel);
    JLayer<JComponent> draggedObjectLayer = new JLayer<JComponent>(draggedObjectLayerPanel);
    JLayer<JComponent> netLayer = new JLayer<JComponent>(netLayerPanel);
    JLayer<JComponent> selectionRectLayer = new JLayer<JComponent>(selectionRectLayerPanel);

    SelectionRectangle selectionrectangle = new SelectionRectangle();
    Point selectionRectStart;
    Point panOrigin;

    public PatchViewSwing(PatchController patchController) {
        super(patchController);

        Layers.setLayout(null);
        Layers.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        Layers.setLocation(0, 0);

        JComponent[] layerComponents = {
            objectLayer, objectLayerPanel, draggedObjectLayerPanel, netLayerPanel,
            selectionRectLayerPanel, draggedObjectLayer, netLayer, selectionRectLayer};
        for (JComponent c : layerComponents) {
            c.setLayout(null);
            c.setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
            c.setLocation(0, 0);
            c.setOpaque(false);
            c.validate();
        }

        Layers.add(objectLayer, new Integer(1));
        Layers.add(netLayer, new Integer(2));
        Layers.add(draggedObjectLayer, new Integer(3));
        Layers.add(selectionRectLayer, new Integer(4));

        objectLayer.setName("objectLayer");
        draggedObjectLayer.setName("draggedObjectLayer");
        netLayer.setName("netLayer");
        netLayerPanel.setName("netLayerPanel");
        selectionRectLayerPanel.setName("selectionRectLayerPanel");
        selectionRectLayer.setName("selectionRectLayer");

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
        Layers.revalidate();

        Layers.setTransferHandler(TH);

        InputMap inputMap = Layers.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                KeyUtils.CONTROL_OR_CMD_MASK), "cut");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                KeyUtils.CONTROL_OR_CMD_MASK), "copy");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                KeyUtils.CONTROL_OR_CMD_MASK), "paste");

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
                        || ((ke.getKeyCode() == KeyEvent.VK_N) && !KeyUtils.isControlOrCommandDown(ke))
                        || ((ke.getKeyCode() == KeyEvent.VK_1) && KeyUtils.isControlOrCommandDown(ke))) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, null);
                    }
                } else if (((ke.getKeyCode() == KeyEvent.VK_C) && !KeyUtils.isControlOrCommandDown(ke))
                        || ((ke.getKeyCode() == KeyEvent.VK_5) && KeyUtils.isControlOrCommandDown(ke))) {
                    getPatchController().AddObjectInstance(MainFrame.axoObjects.GetAxoObjectFromName(patchComment, null).get(0), Layers.getMousePosition());
                    ke.consume();
                } else if ((ke.getKeyCode() == KeyEvent.VK_I) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, patchInlet);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_O) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, patchOutlet);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_D) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        ShowClassSelector(p, null, patchDisplay);
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_M) && !KeyUtils.isControlOrCommandDown(ke)) {
                    Point p = Layers.getMousePosition();
                    ke.consume();
                    if (p != null) {
                        if (ke.isShiftDown()) {
                            ShowClassSelector(p, null, patchMidiKey);
                        } else {
                            ShowClassSelector(p, null, patchMidi);
                        }
                    }
                } else if ((ke.getKeyCode() == KeyEvent.VK_A) && !KeyUtils.isControlOrCommandDown(ke)) {
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
                    deleteSelectedAxoObjectInstanceViews();

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
                    for (IAxoObjectInstanceView o : objectInstanceViews) {
                        o.setSelected(false);
                    }
                    if (me.getClickCount() == 2) {
                        ShowClassSelector(me.getPoint(), null, null);
                    } else {
                        if ((osf != null) && osf.isVisible()) {
                            osf.Accept();
                        }
                        Layers.requestFocusInWindow();
                    }
                    me.consume();
                } else {
                    if ((osf != null) && osf.isVisible()) {
                        osf.Cancel();
                    }
                    Layers.requestFocusInWindow();
                    me.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    selectionRectStart = me.getPoint();
                    selectionrectangle.setBounds(me.getX(), me.getY(), 1, 1);
                    selectionrectangle.setVisible(true);

                    Layers.requestFocusInWindow();
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
                        o.setSelected(bounds.intersects(r));
                    }
                    selectionrectangle.setVisible(false);
                    me.consume();
                }
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });

        Layers.setVisible(true);

        Layers.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent ev) {
                if (selectionrectangle.isVisible()) {
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
                    selectionrectangle.setVisible(true);
                    ev.consume();
                }
            }
        });

        Layers.setDropTarget(dt);
        Layers.setVisible(true);
    }

    TransferHandler TH = new TransferHandler() {
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
                deleteSelectedAxoObjectInstanceViews();
            }
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            return super.importData(support);
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
    public void PostConstructor() {
        getPatchController().patchModel.PostContructor();
        Layers.setPreferredSize(new Dimension(5000, 5000));
        modelChanged(false);
        getPatchController().patchModel.PromoteOverloading(true);
        ShowPreset(0);
        SelectNone();
        getPatchController().patchModel.pushUndoState(false);
    }

    @Override
    void setCordsInBackground(boolean b) {
        if (b) {
            Layers.removeAll();
            Layers.add(netLayer, new Integer(1));
            Layers.add(objectLayer, new Integer(2));
            Layers.add(draggedObjectLayer, new Integer(3));
            Layers.add(selectionRectLayer, new Integer(4));
        } else {
            Layers.removeAll();
            Layers.add(objectLayer, new Integer(1));
            Layers.add(netLayer, new Integer(2));
            Layers.add(draggedObjectLayer, new Integer(3));
            Layers.add(selectionRectLayer, new Integer(4));
        }
    }

    @Override
    public void repaint() {
        if (Layers != null) {
            Layers.repaint();
        }
    }

    public void clampLayerSize(Dimension s) {
        if (Layers.getParent() != null) {
            if (s.width < Layers.getParent().getWidth()) {
                s.width = Layers.getParent().getWidth();
            }
            if (s.height < Layers.getParent().getHeight()) {
                s.height = Layers.getParent().getHeight();
            }
        }
    }

    public void AdjustSize() {
        Dimension s = getPatchController().GetSize();
        clampLayerSize(s);
        if (!Layers.getSize().equals(s)) {
            Layers.setSize(s);
        }
        if (!Layers.getPreferredSize().equals(s)) {
            Layers.setPreferredSize(s);
        }
    }

    @Override
    public void Lock() {
        super.Lock();
        Layers.setBackground(Theme.getCurrentTheme().Patch_Locked_Background);
    }

    @Override
    public void Unlock() {
        super.Unlock();
        Layers.setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
    }

    @Override
    public void validate() {
        Layers.validate();
    }

    @Override
    public void validateObjects() {
        objectLayerPanel.validate();
    }

    @Override
    public void validateNets() {
        netLayerPanel.validate();
    }

    public PatchViewportView getViewportView() {
        return Layers;
    }

    public Point getLocationOnScreen() {
        return objectLayerPanel.getLocationOnScreen();
    }

    @Override
    public void requestFocus() {
        Layers.requestFocus();
    }

    @Override
    public void remove(IAxoObjectInstanceView v) {
        objectLayerPanel.remove((AxoObjectInstanceViewAbstract) v);
        objectInstanceViews.remove(v);
    }

    @Override
    public void add(IAxoObjectInstanceView v) {
        objectLayerPanel.add((AxoObjectInstanceViewAbstract) v);
        objectInstanceViews.add(v);
    }

    @Override
    public void removeAllObjectViews() {
        objectLayerPanel.removeAll();
        objectInstanceViews.clear();
    }

    @Override
    public void removeAllNetViews() {
        netViews.clear();
        netLayerPanel.removeAll();
    }

    public void add(INetView view) {
        netViews.add(view);
        netLayerPanel.add((NetView) view);
    }
}
