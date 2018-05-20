package axoloti.piccolo.patch.object;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.patch.PatchModel;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.PPopupIcon;
import axoloti.piccolo.components.PTextFieldComponent;
import axoloti.piccolo.patch.PatchPCanvas;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceViewAbstract extends PatchPNode implements IAxoObjectInstanceView {

    protected MouseListener ml;
    protected MouseMotionListener mml;
    protected boolean dragging = false;
    protected PatchPNode titleBar;
    protected PTextFieldComponent textFieldInstanceName;
    protected PLabelComponent instanceLabel;
    private boolean locked = false;

    protected final IAxoObjectInstance objectInstance;

    protected final Set popupMenuNodes = new HashSet();
    protected final PPopupIcon popupIcon = new PPopupIcon(this);

    public void showPopup(PInputEvent e) {
        PatchPCanvas canvas = getPatchPCanvas();
        if (!canvas.isPopupVisible()) {
            JPopupMenu popup = createPopupMenu();
            Point popupLocation = PUtils.getPopupLocation(popupIcon, e);
            popup.show(canvas, popupLocation.x, popupLocation.y);
            canvas.setPopupParent(popupIcon);
        } else {
            canvas.clearPopupParent();
        }
    }

    public boolean overPickableChild(PInputEvent e) {
        ArrayList picked = new ArrayList();
        Rectangle2D.Double location = new Rectangle2D.Double(e.getPosition().getX(), e.getPosition().getY(), 1, 1);
        e.getPickedNode().findIntersectingNodes(location, picked);
        Set pickedSet = new HashSet(picked);
        pickedSet.retainAll(popupMenuNodes);
        return pickedSet.isEmpty();
    }

    PAxoObjectInstanceViewAbstract(IAxoObjectInstance objectInstance, PatchViewPiccolo patchView) {
        super(patchView);
        this.objectInstance = objectInstance;
        titleBar = new PatchPNode(patchView);
        initComponents();
    }

    @Override
    public IAxoObjectInstance getDModel() {
        return objectInstance;
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

    JPopupMenu popup;

    protected static final Dimension TITLEBAR_MINIMUM_SIZE = new Dimension(40, 12);
    protected static final Dimension TITLEBAR_MAXIMUM_SIZE = new Dimension(32768, 12);

    private void initComponents() {
        setMinimumSize(new Dimension(60, 40));

        titleBar.removeAllChildren();
        titleBar.setLayout(new BoxLayout(titleBar.getProxyComponent(), BoxLayout.LINE_AXIS));

        titleBar.setPickable(false);
        titleBar.setPaint(Theme.getCurrentTheme().Object_TitleBar_Background);
        setBorder(BORDER_UNSELECTED);
        titleBar.setMinimumSize(TITLEBAR_MINIMUM_SIZE);
        titleBar.setMaximumSize(TITLEBAR_MAXIMUM_SIZE);

        // define child nodes that should show object popup
        popupMenuNodes.add(this);
        popupMenuNodes.add(titleBar);

        popupIcon.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mousePressed(PInputEvent e) {
                showPopup(e);
            }
        });

        setPaint(Theme.getCurrentTheme().Object_Default_Background);
        setVisible(false);
    }

    JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        return popup;
    }

    @Override
    public Dimension getSize() {
        return getProxyComponent().getSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return getProxyComponent().getPreferredSize();
    }

    @Override
    public Point getLocation() {
        return new Point(getDModel().getX(), getDModel().getY());
    }

    @Override
    public PatchView getPatchView() {
        return patchView;
    }

    @Override
    public PatchModel getPatchModel() {
        return patchView.getDModel();
    }

    @Override
    public List<IInletInstanceView> getInletInstanceViews() {
        return null;
    }

    @Override
    public List<IOutletInstanceView> getOutletInstanceViews() {
        return null;
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return null;
    }

    @Override
    public void setLocation(int x, int y) {
        objectInstance.getController().changeLocation(x, y);
        setOffset(x, y);
    }

    protected void handleInstanceNameEditorAction() {
        if(textFieldInstanceName != null) {
            String s = textFieldInstanceName.getText();
            removeChild(textFieldInstanceName);
            textFieldInstanceName = null;
            instanceLabel.setVisible(true);
            objectInstance.getController().addMetaUndo("edit object name");
            objectInstance.getController().changeInstanceName(s);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        objectInstance.getController().addMetaUndo("edit object instance name");
        textFieldInstanceName = new PTextFieldComponent(getDModel().getInstanceName());
        textFieldInstanceName.selectAll();
        PBasicInputEventHandler inputEventHandler = new PBasicInputEventHandler() {
            @Override
            public void keyboardFocusLost(PInputEvent e) {
                handleInstanceNameEditorAction();
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                textFieldInstanceName.selectAll();
            }

            @Override
            public void keyPressed(PInputEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInstanceNameEditorAction();
                }
            }
        };

        textFieldInstanceName.addInputEventListener(inputEventHandler);

        Dimension d = textFieldInstanceName.getSize();
        d.width = (int) getWidth() - 1;
        d.height = 15;
        textFieldInstanceName.setMaximumSize(d);
        textFieldInstanceName.setMinimumSize(d);
        textFieldInstanceName.setPreferredSize(d);
        textFieldInstanceName.setSize(d);

        addChild(1, textFieldInstanceName);
        textFieldInstanceName.raiseToTop();
        textFieldInstanceName.setTransform(instanceLabel.getTransform());
        textFieldInstanceName.grabFocus();
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
        repaint();
    }

    @Override
    public void moveToFront() {
        // new objects added to front by default
    }

    @Override
    public void resizeToGrid() {
        invalidate();
        Dimension d = getPreferredSize();
        d.width = ((d.width + Constants.X_GRID - 1) / Constants.X_GRID) * Constants.X_GRID;
        d.height = ((d.height + Constants.Y_GRID - 1) / Constants.Y_GRID) * Constants.Y_GRID;
        setSize(d);
    }

    @Override
    public JComponent getCanvas() {
        return patchView.getViewportView().getComponent();
    }

    protected PatchPCanvas getPatchPCanvas() {
        return (PatchPCanvas) getCanvas();
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
            if (getPatchView() != null) {
                if (getInletInstanceViews() != null) {
                    for (IInletInstanceView i : getInletInstanceViews()) {
                        INetView n = getPatchView().findNetView(i);
                        if (n != null) {
                            n.updateBounds();
                            n.repaint();
                        }
                    }
                }
                if (getOutletInstanceViews() != null) {
                    for (IOutletInstanceView i : getOutletInstanceViews()) {
                        INetView n = getPatchView().findNetView(i);
                        if (n != null) {
                            n.updateBounds();
                            n.repaint();
                        }
                    }
                }
            }
        } else if (AxoObjectInstance.OBJ_INSTANCENAME.is(evt)) {
            String s = (String) evt.getNewValue();
            showInstanceName(s);
        } else if (AxoObjectInstance.OBJ_SELECTED.is(evt)) {
            showSelected((Boolean) evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public Boolean isSelected() {
        return getDModel().getSelected();
    }
}
