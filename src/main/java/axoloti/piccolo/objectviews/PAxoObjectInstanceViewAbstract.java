package axoloti.piccolo.objectviews;

import axoloti.abstractui.INetView;
import axoloti.patch.PatchModel;
import axoloti.abstractui.PatchView;
import axoloti.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import axoloti.abstractui.IAttributeInstanceView;
import axoloti.abstractui.IDisplayInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.PatchPCanvas;
import axoloti.piccolo.PatchPNode;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.utils.Constants;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.PPopupIcon;
import axoloti.piccolo.components.PTextFieldComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    protected AxoObjectInstanceAbstract model;
    protected MouseListener ml;
    protected MouseMotionListener mml;
    protected boolean dragging = false;
    final PatchPNode titleBar;
    PTextFieldComponent InstanceNameTF;
    public PLabelComponent instanceLabel;
    private boolean Locked = false;

    protected final Set popupMenuNodes = new HashSet();
    protected final PPopupIcon popupIcon = new PPopupIcon(this);

    public void ShowPopup(PInputEvent e) {
        PatchPCanvas canvas = getPatchPCanvas();
        if (!canvas.isPopupVisible()) {
            JPopupMenu popup = CreatePopupMenu();
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
        return pickedSet.size() == 0;
    }

    PAxoObjectInstanceViewAbstract(AxoObjectInstanceAbstract model, PatchViewPiccolo patchView) {
        super(patchView);
        this.model = model;
        titleBar = new PatchPNode(patchView);
    }

    @Override
    public AxoObjectInstanceAbstract getModel() {
        return model;
    }

    @Override
    public void Lock() {
        Locked = true;
    }

    @Override
    public void Unlock() {
        Locked = false;
    }

    @Override
    public boolean isLocked() {
        return Locked;
    }

    JPopupMenu popup;

    protected static final Dimension TITLEBAR_MINIMUM_SIZE = new Dimension(40, 12);
    protected static final Dimension TITLEBAR_MAXIMUM_SIZE = new Dimension(32768, 12);

    @Override
    public void PostConstructor() {
        removeAllChildren();
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
                ShowPopup(e);
            }
        });

        setPaint(Theme.getCurrentTheme().Object_Default_Background);
    }

    JPopupMenu CreatePopupMenu() {
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
        return new Point(model.getX(), model.getY());
    }

    @Override
    public PatchView getPatchView() {
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

    @Override
    public void setLocation(int x, int y) {
        //model.setX(x);
        //model.setY(y);
        setOffset(x, y);
        if (getPatchView() != null) {
            repaint();
            for (IIoletInstanceView i : getInletInstanceViews()) {
                INetView n = getPatchView().GetNetView(i);
                if (n != null) {
                    n.updateBounds();
                    n.repaint();
                }
            }
            for (IIoletInstanceView i : getOutletInstanceViews()) {
                INetView n = getPatchView().GetNetView(i);
                if (n != null) {
                    n.updateBounds();
                    n.repaint();
                }
            }
        }
    }

    protected void handleInstanceNameEditorAction() {
        showInstanceName(InstanceNameTF.getText());
        removeChild(InstanceNameTF);
        instanceLabel.setVisible(true);
        repaint();
    }

    public void addInstanceNameEditor() {
        InstanceNameTF = new PTextFieldComponent(instanceLabel.getText());
        InstanceNameTF.selectAll();
        PBasicInputEventHandler inputEventHandler = new PBasicInputEventHandler() {
            @Override
            public void keyboardFocusLost(PInputEvent e) {
                handleInstanceNameEditorAction();
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                InstanceNameTF.selectAll();
            }

            @Override
            public void keyPressed(PInputEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInstanceNameEditorAction();
                }
            }
        };

        InstanceNameTF.addInputEventListener(inputEventHandler);

        InstanceNameTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent aef) {
                handleInstanceNameEditorAction();
            }
        });

        instanceLabel.setVisible(false);

        addChild(1, InstanceNameTF);
        InstanceNameTF.raiseToTop();
        InstanceNameTF.setSize(new Dimension((int) getWidth() - 1, 15));
        InstanceNameTF.setTransform(instanceLabel.getTransform());
        InstanceNameTF.grabFocus();
    }

    @Override
    public void showInstanceName(String InstanceName) {
        if (model.setInstanceName(InstanceName)) {
        }
    }

    public static final Border BORDER_SELECTED = BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Selected);
    public static final Border BORDER_UNSELECTED = BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Unselected);

    public void SetLocation(int x1, int y1) {
        setLocation(x1, y1);
    }

    @Override
    public void moveToFront() {
        // new objects added to front by default
    }

    public void resizeToGrid() {
        Dimension d = getPreferredSize();
        d.width = ((d.width + Constants.X_GRID - 1) / Constants.X_GRID) * Constants.X_GRID;
        d.height = ((d.height + Constants.Y_GRID - 1) / Constants.Y_GRID) * Constants.Y_GRID;
        setSize(d);
    }

    @Override
    public void addParameterInstanceView(IParameterInstanceView view) {
    }

    @Override
    public void addAttributeInstanceView(IAttributeInstanceView view) {

    }

    @Override
    public void addDisplayInstanceView(IDisplayInstanceView view) {

    }

    @Override
    public void addOutletInstanceView(IIoletInstanceView view) {

    }

    @Override
    public void addInletInstanceView(IIoletInstanceView view) {

    }

    @Override
    public JComponent getCanvas() {
        return patchView.getViewportView().getComponent();
    }

    protected PatchPCanvas getPatchPCanvas() {
        return (PatchPCanvas) getCanvas();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ObjectInstanceController getController() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
