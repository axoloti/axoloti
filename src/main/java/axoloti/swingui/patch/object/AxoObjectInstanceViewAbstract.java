package axoloti.swingui.patch.object;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.patch.PatchModel;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.preferences.Theme;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.TextFieldComponent;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.utils.Constants;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class AxoObjectInstanceViewAbstract extends ViewPanel<IAxoObjectInstance>
        implements MouseListener, MouseMotionListener, // TODO: code cleanup: move MouseListener, MouseMotionListener into mouseAdapter
        IAxoObjectInstanceView {

    protected final JPanel titlebar = new JPanel();
    protected LabelComponent instanceLabel;
    protected TextFieldComponent textFieldInstanceName;
    private Point dragLocation = null;
    private Point dragAnchor = null;
    private boolean locked = false;

    AxoObjectInstanceViewAbstract(IAxoObjectInstance objectInstance, PatchViewSwing patchView) {
        super(objectInstance);
        this.patchView = patchView;
        initComponents();
    }

    @Override
    public IAxoObjectInstance getDModel() {
        return model;
    }

    @Override
    public void lock() {
        locked = true;
    }

    @Override
    public void unlock() {
        locked = false;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    private static class BoxLayoutGrid extends BoxLayout {

        BoxLayoutGrid(Container target, int axis) {
            super(target, axis);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            Dimension d = super.preferredLayoutSize(target);
            d.width = ((d.width + Constants.X_GRID - 1) / Constants.X_GRID) * Constants.X_GRID;
            d.height = ((d.height + Constants.Y_GRID - 1) / Constants.Y_GRID) * Constants.Y_GRID;
            System.out.println("pref layout size = " + d);
            return d;
        }

    }

    private static final Dimension TITLEBAR_MINIMUM_SIZE = new Dimension(40, 12);
    private static final Dimension TITLEBAR_MAXIMUM_SIZE = new Dimension(32768, 12);

    private void initComponents() {
        setVisible(false);
        removeAll();
        setMinimumSize(new Dimension(60, 40));
        //setMaximumSize(new Dimension(Short.MAX_VALUE,
        //        Short.MAX_VALUE));

        setFocusable(true);
        titlebar.removeAll();
        titlebar.setLayout(new BoxLayout(titlebar, BoxLayout.LINE_AXIS));
        titlebar.setBackground(Theme.getCurrentTheme().Object_TitleBar_Background);
        titlebar.setMinimumSize(TITLEBAR_MINIMUM_SIZE);
        titlebar.setMaximumSize(TITLEBAR_MAXIMUM_SIZE);

        setBorder(BORDER_UNSELECTED);

        setBackground(Theme.getCurrentTheme().Object_Default_Background);

        titlebar.addMouseListener(this);
        addMouseListener(this);

        titlebar.addMouseMotionListener(this);
        addMouseMotionListener(this);
    }

    JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        return popup;
    }

    Point localToPatchLocation(Point p, Component sourceComponent) {
        PatchViewSwing pv = getPatchView();
        if (pv == null) {
            return p;
        }
        return SwingUtilities.convertPoint(sourceComponent, p, pv.getViewportView().getComponent());
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (getPatchView() != null) {
            getPatchView().requestFocus();
            if (me.getClickCount() == 1) {
                if (me.isShiftDown()) {
                    model.getController().changeSelected(!getDModel().getSelected());
                    me.consume();
                } else if (!getDModel().getSelected()) {
                    model.getParent().getController().selectNone();
                    model.getController().changeSelected(true);
                    me.consume();
                }
            }
            if (me.getClickCount() == 2) {
                getPatchView().showClassSelector(AxoObjectInstanceViewAbstract.this.getLocation(), getLocationOnScreen(), AxoObjectInstanceViewAbstract.this, null);
                me.consume();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        handleMousePressed(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        handleMouseReleased(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (patchView == null) {
            // we're in the ObjectSectorFrame
            return;
        }
        Point locInPatch = localToPatchLocation(me.getPoint(), me.getComponent());
        if (draggingObjects != null) {
            int dx = locInPatch.x - dragAnchor.x;
            int dy = locInPatch.y - dragAnchor.y;
            for (AxoObjectInstanceViewAbstract o : draggingObjects) {
                int nx = o.dragLocation.x + dx;
                int ny = o.dragLocation.y + dy;
                if (!me.isShiftDown()) {
                    nx = ((nx + (Constants.X_GRID / 2)) / Constants.X_GRID) * Constants.X_GRID;
                    ny = ((ny + (Constants.Y_GRID / 2)) / Constants.Y_GRID) * Constants.Y_GRID;
                }
                o.getDModel().getController().changeLocation(nx, ny);
            }
        }
        PatchViewSwing pv = getPatchView();
        if (pv != null) {
            Rectangle r = new Rectangle(locInPatch, new Dimension(1, 1));
            pv.scrollTo(r);
        }

    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    private LinkedList<AxoObjectInstanceViewAbstract> draggingObjects = null;

    protected void handleMousePressed(MouseEvent me) {
        if (patchView == null) {
            return;
        }
        patchView.requestFocus();
        if (me.isPopupTrigger()) {
            JPopupMenu p = createPopupMenu();
            p.show(titlebar, 0, titlebar.getHeight());
            me.consume();
        } else if (!patchView.isLocked()) {
            draggingObjects = new LinkedList<>();
            dragAnchor = localToPatchLocation(me.getPoint(), me.getComponent());
            getPatchView().moveToDraggedLayer(this);
            draggingObjects.add(this);
            dragLocation = getLocation();
            model.getController().addMetaUndo("move");
            if (getDModel().getSelected()) {
                for (IAxoObjectInstanceView o : getPatchView().getObjectInstanceViews()) {
                    if (o.getDModel().getSelected()) {
                        AxoObjectInstanceViewAbstract oa = (AxoObjectInstanceViewAbstract) o;
                        getPatchView().moveToDraggedLayer(oa);
                        draggingObjects.add(oa);
                        oa.dragLocation = oa.getLocation();
                    }
                }
            }
            me.consume();
        }
    }

    protected void handleMouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
            JPopupMenu p = createPopupMenu();
            p.show(titlebar, 0, titlebar.getHeight());
            me.consume();
            return;
        }
        int maxZIndex = 0;
        if (draggingObjects != null) {
            if (getPatchModel() != null) {
                for (AxoObjectInstanceViewAbstract o : draggingObjects) {
                    getPatchView().moveToObjectLayer(o, 0);
                    if (getPatchView().objectLayerPanel.getComponentZOrder(o) > maxZIndex) {
                        maxZIndex = getPatchView().objectLayerPanel.getComponentZOrder(o);
                    }
                    o.repaint();
                }
                draggingObjects = null;
                getPatchView().updateSize();
                model.getParent().getController().fixNegativeObjectCoordinates();
            }
            me.consume();
        }
    }

    private final PatchViewSwing patchView;

    @Override
    public PatchViewSwing getPatchView() {
        return patchView;
    }

    @Override
    public PatchModel getPatchModel() {
        return patchView.getDModel();
    }

    @Override
    public List<IInletInstanceView> getInletInstanceViews() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IOutletInstanceView> getOutletInstanceViews() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return Collections.EMPTY_LIST;
    }

    void handleInstanceNameEditorAction() {
        if (textFieldInstanceName == null) {
            throw new Error("textFieldInstanceName is null");
        }
        String s = textFieldInstanceName.getText();
        model.getController().addMetaUndo("edit object name");
        model.getController().changeInstanceName(s);
        if (textFieldInstanceName.getParent() != null) {
            textFieldInstanceName.getParent().remove(textFieldInstanceName);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        model.getController().addMetaUndo("edit object instance name");
        textFieldInstanceName = new TextFieldComponent(getDModel().getInstanceName());
        textFieldInstanceName.selectAll();
        textFieldInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleInstanceNameEditorAction();
            }
        });
        textFieldInstanceName.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                handleInstanceNameEditorAction();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        textFieldInstanceName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInstanceNameEditorAction();
                }
            }
        });

        getParent().add(textFieldInstanceName, 0);
        textFieldInstanceName.setLocation(getLocation().x, getLocation().y + instanceLabel.getLocation().y);
        textFieldInstanceName.setSize(getWidth(), 15);
        textFieldInstanceName.setVisible(true);
        textFieldInstanceName.requestFocus();
    }

    public void showInstanceName(String InstanceName) {
        instanceLabel.setText(InstanceName);
        resizeToGrid();
    }

    public static final Border BORDER_SELECTED = BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Selected);
    public static final Border BORDER_UNSELECTED = BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Unselected);

    public void showSelected(boolean Selected) {
        if (Selected) {
            setBorder(BORDER_SELECTED);
        } else {
            setBorder(BORDER_UNSELECTED);
        }
    }

    @Override
    public void moveToFront() {
        getPatchView().objectLayerPanel.setComponentZOrder(this, 0);
    }

    @Override
    public void resizeToGrid() {
        revalidate();
        Dimension d = getPreferredSize();
        d.width = ((d.width + Constants.X_GRID - 1) / Constants.X_GRID) * Constants.X_GRID;
        d.height = ((d.height + Constants.Y_GRID - 1) / Constants.Y_GRID) * Constants.Y_GRID;
        setSize(d);
        revalidate();
    }

    @Override
    public JComponent getCanvas() {
        return patchView.getViewportView().getComponent();
    }

    @Override
    public IInletInstanceView getInletInstanceView(InletInstance inletInstance) {
        return null;
    }

    @Override
    public IOutletInstanceView getOutletInstanceView(OutletInstance outletInstance) {
        return null;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {

        if (AxoObjectInstanceAbstract.OBJ_LOCATION.is(evt)) {
            Point newValue = (Point) evt.getNewValue();
            setLocation(newValue.x, newValue.y);
            Set<INetView> netViewsToUpdate = new HashSet<>();
            if (getPatchView() != null) {
                if (getInletInstanceViews() != null) {
                    for (IInletInstanceView i : getInletInstanceViews()) {
                        INetView n = getPatchView().findNetView(i);
                        if (n != null) {
                            netViewsToUpdate.add(n);
                        }
                    }
                }
                if (getOutletInstanceViews() != null) {
                    for (IOutletInstanceView i : getOutletInstanceViews()) {
                        INetView n = getPatchView().findNetView(i);
                        if (n != null) {
                            netViewsToUpdate.add(n);
                        }
                    }
                }
                for (INetView n : netViewsToUpdate) {
                    n.updateBounds();
                }
            }
        } else if (AxoObjectInstance.OBJ_INSTANCENAME.is(evt)) {
            String s = (String) evt.getNewValue();
            showInstanceName(s);
        } else if (AxoObjectInstance.OBJ_SELECTED.is(evt)) {
            showSelected((Boolean)evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }

}
