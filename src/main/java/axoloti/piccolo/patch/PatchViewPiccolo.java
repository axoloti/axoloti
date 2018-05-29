package axoloti.piccolo.patch;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.PatchView;
import axoloti.abstractui.PatchViewportView;
import axoloti.mvc.IModel;
import axoloti.mvc.array.ArrayView;
import axoloti.objectlibrary.AxoObjects;
import axoloti.patch.PatchModel;
import axoloti.patch.net.Net;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.piccolo.PObjectSearchFrame;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.components.PFocusable;
import axoloti.piccolo.components.control.PCtrlComponentAbstract;
import axoloti.piccolo.components.control.PDropDownComponent;
import axoloti.piccolo.patch.net.PNetView;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewFactory;
import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import org.piccolo2d.PCamera;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PBounds;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class PatchViewPiccolo extends PatchView {

    private PatchPCanvas canvas;

    public PatchViewPiccolo(PatchModel patchModel) {
        super(patchModel);
    }

    private PCtrlComponentAbstract focusedCtrl;

    public void setFocusedCtrl(PCtrlComponentAbstract ctrl) {
        focusedCtrl = ctrl;
        canvas.getScrollPane().setKeyActionsDisabled(ctrl != null);
    }

    public PBasicInputEventHandler inputEventHandler = new PBasicInputEventHandler() {
        @Override
        public void mousePressed(PInputEvent e) {
            e.getInputManager().setKeyboardFocus(this);
            setFocusedCtrl(null);
        }

        @Override
        public void mouseClicked(PInputEvent e) {
            if (e.isLeftMouseButton()) {
                if (e.getPickedNode() instanceof PCamera) {
                    if (e.getClickCount() == 2) {
                        PatchViewPiccolo.this.showClassSelector(e, null, null);
                    } else if ((osf != null) && osf.isVisible()) {
                        osf.accept();
                    }
                }
            } else if ((osf != null) && osf.isVisible()) {
                osf.cancel();
            }
            e.setHandled(true);
        }

        @Override
        public void mouseWheelRotated(PInputEvent e) {
            if (e.isControlDown() || e.isMetaDown()) {
                canvas.getScrollPane().setWheelScrollingEnabled(false);
            }
        }

        @Override
        public void keyReleased(PInputEvent e) {
            if (KeyUtils.isControlOrCommand(e.getKeyCode())
                    && Preferences.getPreferences().getMouseWheelPan()) {
                canvas.getScrollPane().setWheelScrollingEnabled(true);
            }
        }

        @Override
        public void keyPressed(PInputEvent e) {
            int xsteps = 1;
            int ysteps = 1;
            if (!e.isShiftDown()) {
                xsteps = Constants.X_GRID;
                ysteps = Constants.Y_GRID;
            }
            if ((e.getKeyCode() == KeyEvent.VK_SPACE)
                    || ((e.getKeyCode() == KeyEvent.VK_N) && !KeyUtils.isControlOrCommandDown(e))
                    || ((e.getKeyCode() == KeyEvent.VK_1) && KeyUtils.isControlOrCommandDown(e))) {
                e.setHandled(true);
                PatchViewPiccolo.this.showClassSelector(e, null, null);
            } else if (((e.getKeyCode() == KeyEvent.VK_C) && !KeyUtils.isControlOrCommandDown(e))
                    || ((e.getKeyCode() == KeyEvent.VK_5) && KeyUtils.isControlOrCommandDown(e))) {
                Point patchPosition = PUtils.asPoint(e.getInputManager().getCurrentCanvasPosition());
                getCanvas().getCamera().getViewTransform().inverseTransform(patchPosition, patchPosition);
                getDModel().getController().addObjectInstance(
                        AxoObjects.getAxoObjects().getAxoObjectFromName(patchComment, null).get(0), patchPosition);
                e.setHandled(true);
            } else if ((e.getKeyCode() == KeyEvent.VK_I) && !KeyUtils.isControlOrCommandDown(e)) {
                e.setHandled(true);
                PatchViewPiccolo.this.showClassSelector(e, null, patchInlet);
            } else if ((e.getKeyCode() == KeyEvent.VK_O) && !KeyUtils.isControlOrCommandDown(e)) {
                e.setHandled(true);
                PatchViewPiccolo.this.showClassSelector(e, null, patchOutlet);
            } else if ((e.getKeyCode() == KeyEvent.VK_D) && !KeyUtils.isControlOrCommandDown(e)) {
                e.setHandled(true);
                PatchViewPiccolo.this.showClassSelector(e, null, patchDisplay);
            } else if ((e.getKeyCode() == KeyEvent.VK_M) && !KeyUtils.isControlOrCommandDown(e)) {
                e.setHandled(true);
                if (e.isShiftDown()) {
                    PatchViewPiccolo.this.showClassSelector(e, null, patchMidiKey);
                } else {
                    PatchViewPiccolo.this.showClassSelector(e, null, patchMidi);
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_A) && !KeyUtils.isControlOrCommandDown(e)) {
                e.setHandled(true);
                if (e.isShiftDown()) {
                    PatchViewPiccolo.this.showClassSelector(e, null, patchAudioOut);
                } else {
                    PatchViewPiccolo.this.showClassSelector(e, null, patchAudio);
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_DELETE) || (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
                List<IAxoObjectInstance> selected = getDModel().getSelectedObjects();
                if (!selected.isEmpty()) {
                    getDModel().getController().addMetaUndo("delete objects");
                    for (IAxoObjectInstance o : selected) {
                        getDModel().getController().delete(o);
                    }
                }

                e.setHandled(true);
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                moveSelectedAxoObjInstances(Direction.UP, xsteps, ysteps);
                e.setHandled(true);
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                moveSelectedAxoObjInstances(Direction.DOWN, xsteps, ysteps);
                e.setHandled(true);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                moveSelectedAxoObjInstances(Direction.RIGHT, xsteps, ysteps);
                e.setHandled(true);
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                moveSelectedAxoObjInstances(Direction.LEFT, xsteps, ysteps);
                e.setHandled(true);
            }
        }
    };

    @Override
    public PatchViewportView getViewportView() {
        if (canvas == null) {
            canvas = new PatchPCanvas(this);

            canvas.addInputEventListener(inputEventHandler);
            canvas.setTransferHandler(TH);

            InputMap inputMap = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                    KeyUtils.CONTROL_OR_CMD_MASK), "cut");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                    KeyUtils.CONTROL_OR_CMD_MASK), "copy");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                    KeyUtils.CONTROL_OR_CMD_MASK), "paste");

            ActionMap map = canvas.getActionMap();
            map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                    TransferHandler.getCutAction());
            map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                    TransferHandler.getCopyAction());
            map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                    TransferHandler.getPasteAction());
            canvas.setEnabled(true);
            canvas.setFocusable(true);
            canvas.setFocusCycleRoot(true);
            canvas.setDropTarget(dt);
            canvas.getRoot().getDefaultInputManager().setKeyboardFocus(inputEventHandler);
        }
        return canvas;
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
                if (focusedCtrl != null) {
                    clip.setContents(new StringSelection(Double.toString(focusedCtrl.getValue())), (ClipboardOwner) null);
                } else {
                    clip.setContents(new StringSelection(""), null);
                }
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
                //deleteSelectedAxoObjectInstanceViews();
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
                        String transferData = (String) t.getTransferData(DataFlavor.stringFlavor);
                        if (focusedCtrl != null) {
                            try {
                                focusedCtrl.setValue(Double.parseDouble(transferData));
                                return true;
                            } catch (NumberFormatException e) {
                                // fall through to paste
                            }
                        }
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
    public Point getLocationOnScreen() {
        return getCanvas().getLocationOnScreen();
    }

    @Override
    public void postConstructor() {
        //modelChanged(false);
        getDModel().getController().promoteOverloading(true);
        showPreset(0);
    }

    @Override
    public void requestFocus() {

    }

    private PPatchBorder patchBorder;

    void clampLayerSize(Dimension s) {
        if (getCanvas().getParent() != null) {
            if (s.width < getCanvas().getParent().getWidth()) {
                s.width = getCanvas().getParent().getWidth();
            }
            if (s.height < getCanvas().getParent().getHeight()) {
                s.height = getCanvas().getParent().getHeight();
            }
        }
    }

    @Override
    public void updateSize() {
        int maxX = 0;
        int maxY = 0;
        for(int i = 0; i < getCanvas().getLayer().getChildrenCount(); i++) {
            PBounds bounds = getCanvas().getLayer().getChild(i).getBounds();
            int x = (int) (bounds.x + bounds.width);
            if (x > maxX) {
                maxX = x;
            }
            int y = (int) (bounds.y + bounds.height);
            if (y > maxY) {
                maxY = y;
            }
        }
        Dimension s = new Dimension(maxX, maxY);
        clampLayerSize(s);

        if (!getCanvas().getSize().equals(s)) {
            getCanvas().setSize(s);
        }
        if (!getCanvas().getPreferredSize().equals(s)) {
            getCanvas().setPreferredSize(s);
        }
    }

    private boolean cordsInBackground = false;

    public void updateNetZPosition() {
        if (cordsInBackground) {
            lowerNetsToBottom();
        } else {
            raiseNetsToTop();
        }
    }

    @Override
    public void setCordsInBackground(boolean cordsInBackground) {
        this.cordsInBackground = cordsInBackground;
        updateNetZPosition();
    }

    private void raiseNetsToTop() {
        for (INetView netView : netViews) {
            ((PNode) netView).raiseToTop();
        }
    }

    private void lowerNetsToBottom() {
        for (INetView netView : netViews) {
            ((PNode) netView).lowerToBottom();
        }
    }

    public void addFocusables(ListIterator<PNode> childrenIterator) {
        while (childrenIterator.hasNext()) {
            PNode child = childrenIterator.next();
            addFocusables(child.getChildrenIterator());
            if (child instanceof PFocusable
                    && !(child instanceof PDropDownComponent)) {
                addFocusable((PFocusable) child);
            }
        }
    }

    public void removeFocusables(ListIterator<PNode> childrenIterator) {
        while (childrenIterator.hasNext()) {
            PNode child = childrenIterator.next();
            removeFocusables(child.getChildrenIterator());
            if (child instanceof PFocusable) {
                removeFocusable((PFocusable) child);
            }
        }
    }

    private void add(IAxoObjectInstanceView v) {
        PatchPNode node = (PatchPNode) v;
        getCanvas().getLayer().addChild(node);
        v.resizeToGrid();
        updateSize();
        addFocusables(node.getChildrenIterator());
    }

    private void remove(IAxoObjectInstanceView v) {
        PatchPNode node = (PatchPNode) v;
        getCanvas().getLayer().removeChild(node);
        removeFocusables(node.getChildrenIterator());
    }

    private void remove(INetView view) {
        getCanvas().getLayer().removeChild((PatchPNode) view);
    }

    private void removeAllObjectViews() {
        for (IAxoObjectInstanceView objectView : objectInstanceViews) {
            getCanvas().getLayer().removeChild((PatchPNode) objectView);
        }
    }

    private void removeAllNetViews() {
        for (INetView netView : netViews) {
            getCanvas().getLayer().removeChild((PatchPNode) netView);
            for (IIoletInstanceView iiv : netView.getInletViews()) {
                iiv.repaint();
            }
            for (IIoletInstanceView iiv : netView.getOutletViews()) {
                iiv.repaint();
            }
        }
    }

    private void add(INetView v) {
        PatchPNode node = (PatchPNode) v;
        getCanvas().getLayer().addChild(node);
        if (cordsInBackground) {
            node.lowerToBottom();
        } else {
            node.raiseToTop();
        }
    }

    public void validate() {
        getCanvas().validate();
    }

    public PatchPCanvas getCanvas() {
        return (PatchPCanvas) getViewportView();
    }

    public void showClassSelector(PInputEvent e, IAxoObjectInstanceView o, String searchString) {
        try {
            Point2D p = e.getPosition();
            Point2D q = e.getCanvasPosition();
            showClassSelector(PUtils.asPoint(e.getPosition()), PUtils.asPoint(e.getCanvasPosition()), o, searchString);
        } catch (RuntimeException ex) {
            // if this is from a keyboard event
            Point canvasPosition = PUtils.asPoint(e.getInputManager().getCurrentCanvasPosition());
            Point patchPosition = (Point) canvasPosition.clone();
            getCanvas().getCamera().getViewTransform().inverseTransform(patchPosition, patchPosition);
            showClassSelector(patchPosition, canvasPosition, o, searchString);
        }
    }

    public void showClassSelector(Point patchPosition, Point canvasPosition, IAxoObjectInstanceView o, String searchString) {
        if (isLocked()) {
            return;
        }
        if (canvasPosition == null) {
            canvasPosition = PUtils.asPoint(getCanvas().getRoot().getDefaultInputManager().getCurrentCanvasPosition());
        }

        if (osf == null) {
            osf = new PObjectSearchFrame(getDModel(), this);
        }

        // TODO: piccolo: show patch selector at object position
        osf.launch(patchPosition, null, o, searchString);
        // obsolete code, review&remove
        //Point patchLocClipped = osf.clipToStayWithinScreen(canvasPosition);
        //osf.setLocation(patchLocClipped.x + ps.x, patchLocClipped.y + ps.y);
        //osf.setVisible(true);
    }

    @Override
    public void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        getCanvas().getCamera().getViewTransform().inverseTransform(pos, pos);
        getDModel().getController().paste(v,
                pos,
                restoreConnectionsToExternalOutlets);
    }

    private final List<PFocusable> focusables = new ArrayList<>();
    private int focusableIndex = 0;

    public void addFocusable(PFocusable focusable) {
        focusables.add(focusable);
        focusable.setFocusableIndex(focusableIndex);
        focusableIndex += 1;
    }

    public void removeFocusable(PFocusable focusable) {
        try {
            focusables.remove(focusable.getFocusableIndex());
        } catch (IndexOutOfBoundsException e) {

        }
        int focusableCount = focusables.size();
        for (int i = focusable.getFocusableIndex(); i < focusableCount; i++) {
            focusables.get(i).setFocusableIndex(i);
        }
        focusableIndex = focusableCount;
    }

    public void transferFocus(PFocusable focusable) {
        focusables.get((focusable.getFocusableIndex() + 1) % focusables.size()).grabFocus();
    }

    HashMap<IModel, IAxoObjectInstanceView> view_cache = new HashMap<>();

    ArrayView<IAxoObjectInstanceView, IAxoObjectInstance> objectInstanceViewSync = new ArrayView<IAxoObjectInstanceView, IAxoObjectInstance>() {
        @Override
        protected IAxoObjectInstanceView viewFactory(IAxoObjectInstance model) {
            IAxoObjectInstance model1 = (IAxoObjectInstance) model;
            IAxoObjectInstanceView view = view_cache.get(model1);
            if (view == null) {
                view = PAxoObjectInstanceViewFactory.createView(model1, PatchViewPiccolo.this);
            }
            add(view);
            return view;
        }

        @Override
        protected void updateUI(List<IAxoObjectInstanceView> views) {
            updateSize();
        }

        @Override
        protected void removeView(IAxoObjectInstanceView view) {
            view.dispose();
            remove(view);
            view_cache.put(view.getDModel(), view);
        }

    };

    ArrayView<INetView, Net> netViewSync = new ArrayView<INetView, Net>() {
        @Override
        protected INetView viewFactory(Net net) {
            INetView view = new PNetView(net, PatchViewPiccolo.this);
            net.getController().addView(view);
            add(view);
            view.repaint();
            return view;
        }

        @Override
        protected void updateUI(List<INetView> views) {
        }

        @Override
        protected void removeView(INetView view) {
            remove(view);
        }

    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (PatchModel.PATCH_OBJECTINSTANCES.is(evt)) {
            objectInstanceViews = objectInstanceViewSync.sync(objectInstanceViews, model.getObjectInstances());
        } else if (PatchModel.PATCH_NETS.is(evt)) {
            netViews = netViewSync.sync(netViews, model.getNets());
        } else if (PatchModel.PATCH_LOCKED.is(evt)) {
            if ((Boolean)evt.getNewValue() == false) {
                canvas.setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
            } else {
                canvas.setBackground(Theme.getCurrentTheme().Patch_Locked_Background);
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void scrollTo(Component c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
