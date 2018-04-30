package axoloti.swingui.patch.object;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.patch.PatchModel;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.preferences.Theme;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.TextFieldComponent;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.utils.Constants;
import java.awt.Component;
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

public class AxoObjectInstanceViewAbstract extends ViewPanel<ObjectInstanceController> implements MouseListener, MouseMotionListener, IAxoObjectInstanceView {

    protected final JPanel titlebar = new JPanel();
    protected LabelComponent instanceLabel;
    protected TextFieldComponent InstanceNameTF;
    private Point dragLocation = null;
    private Point dragAnchor = null;
    private boolean locked = false;

    AxoObjectInstanceViewAbstract(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller);
        this.patchView = patchView;
        initComponents();
    }

    @Override
    public IAxoObjectInstance getModel() {
        return getController().getModel();
    }

    @Override
    public void Lock() {
        locked = true;
    }

    @Override
    public void Unlock() {
        locked = false;
    }

    @Override
    public boolean isLocked() {
        return locked;
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

    JPopupMenu CreatePopupMenu() {
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
                    getController().changeSelected(!getModel().getSelected());
                    me.consume();
                } else if (!getModel().getSelected()) {
                    getController().getModel().getParent().getControllerFromModel().SelectNone();
                    getController().changeSelected(true);
                    me.consume();
                }
            }
            if (me.getClickCount() == 2) {
                getPatchView().ShowClassSelector(AxoObjectInstanceViewAbstract.this.getLocation(), AxoObjectInstanceViewAbstract.this, null);
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
        if (getPatchModel() == null) {
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
                o.getController().changeLocation(nx, ny);
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

    private void moveToDraggedLayer(AxoObjectInstanceViewAbstract o) {
        if (getPatchView().objectLayerPanel.isAncestorOf(o)) {
            getPatchView().objectLayerPanel.remove(o);
            getPatchView().draggedObjectLayerPanel.add(o);
        }
    }

    private LinkedList<AxoObjectInstanceViewAbstract> draggingObjects = null;

    protected void handleMousePressed(MouseEvent me) {
        getPatchView().requestFocus();
        if (getPatchView() != null) {
            if (me.isPopupTrigger()) {
                JPopupMenu p = CreatePopupMenu();
                p.show(titlebar, 0, titlebar.getHeight());
                me.consume();
            } else if (!patchView.isLocked()) {
                draggingObjects = new LinkedList<>();
                dragAnchor = localToPatchLocation(me.getPoint(), me.getComponent());
                moveToDraggedLayer(this);
                draggingObjects.add(this);
                dragLocation = getLocation();
                getController().addMetaUndo("move");
                if (getModel().getSelected()) {
                    for (IAxoObjectInstanceView o : getPatchView().getObjectInstanceViews()) {
                        if (o.getModel().getSelected()) {
                            AxoObjectInstanceViewAbstract oa = (AxoObjectInstanceViewAbstract) o;
                            moveToDraggedLayer(oa);
                            draggingObjects.add(oa);
                            oa.dragLocation = oa.getLocation();
                        }
                    }
                }
                me.consume();
            }
        }
    }

    private void moveToObjectLayer(AxoObjectInstanceViewAbstract o, int z) {
        if (getPatchView().draggedObjectLayerPanel.isAncestorOf(o)) {
            getPatchView().draggedObjectLayerPanel.remove(o);
            getPatchView().objectLayerPanel.add(o);
            getPatchView().objectLayerPanel.setComponentZOrder(o, z);
        }
    }

    protected void handleMouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
            JPopupMenu p = CreatePopupMenu();
            p.show(titlebar, 0, titlebar.getHeight());
            me.consume();
            return;
        }
        int maxZIndex = 0;
        if (draggingObjects != null) {
            if (getPatchModel() != null) {
                for (AxoObjectInstanceViewAbstract o : draggingObjects) {
                    moveToObjectLayer(o, 0);
                    if (getPatchView().objectLayerPanel.getComponentZOrder(o) > maxZIndex) {
                        maxZIndex = getPatchView().objectLayerPanel.getComponentZOrder(o);
                    }
                    o.repaint();
                }
                draggingObjects = null;
                getPatchView().updateSize();
                getController().getModel().getParent().getControllerFromModel().fixNegativeObjectCoordinates();
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
        return patchView.getController().getModel();
    }

    @Override
    public List<IIoletInstanceView> getInletInstanceViews() {
        return null;
    }

    @Override
    public List<IIoletInstanceView> getOutletInstanceViews() {
        return null;
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return null;
    }

    void handleInstanceNameEditorAction() {
        String s = InstanceNameTF.getText();
        getController().addMetaUndo("edit object name");
        getController().changeInstanceName(s);
        if (InstanceNameTF != null && InstanceNameTF.getParent() != null) {
            InstanceNameTF.getParent().remove(InstanceNameTF);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        getController().addMetaUndo("edit object instance name");
        InstanceNameTF = new TextFieldComponent(getModel().getInstanceName());
        InstanceNameTF.selectAll();
        InstanceNameTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleInstanceNameEditorAction();
            }
        });
        InstanceNameTF.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                handleInstanceNameEditorAction();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        InstanceNameTF.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInstanceNameEditorAction();
                }
            }
        });

        getParent().add(InstanceNameTF, 0);
        InstanceNameTF.setLocation(getLocation().x, getLocation().y + instanceLabel.getLocation().y);
        InstanceNameTF.setSize(getWidth(), 15);
        InstanceNameTF.setVisible(true);
        InstanceNameTF.requestFocus();
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
    public boolean isZombie() {
        return false;
    }

    @Override
    public IIoletInstanceView getInletInstanceView(InletInstance inletInstance) {
        return null;
    }

    @Override
    public IIoletInstanceView getOutletInstanceView(OutletInstance outletInstance) {
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
                    for (IIoletInstanceView i : getInletInstanceViews()) {
                        INetView n = getPatchView().findNetView(i);
                        if (n != null) {
                            netViewsToUpdate.add(n);
                        }
                    }
                }
                if (getOutletInstanceViews() != null) {
                    for (IIoletInstanceView i : getOutletInstanceViews()) {
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
