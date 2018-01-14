package axoloti.piccolo.objectviews;

import axoloti.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.AxoObjectInstanceZombie;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.PatchPNode;
import axoloti.piccolo.inlets.PInletInstanceView;
import axoloti.piccolo.outlets.POutletInstanceView;
import axoloti.piccolo.components.PLabelComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.RIGHT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceViewZombie extends PAxoObjectInstanceViewAbstract {

    AxoObjectInstanceZombie model;

    public PatchPNode p_ioletViews;
    public PatchPNode p_inletViews;
    public PatchPNode p_outletViews;

    private String tooltipText = "<html>" + "Unresolved object!";

    public PAxoObjectInstanceViewZombie(AxoObjectInstanceZombie model, PatchViewPiccolo p) {
        super(model, p);
        this.model = model;
        System.out.println(model.getInletInstances().size());
        System.out.println(model.getOutletInstances().size());
    }

    public void PostConstructor() {
        super.PostConstructor();

        p_ioletViews = new PatchPNode(patchView);
        p_ioletViews.setPickable(false);
        p_ioletViews.setLayout(new BoxLayout(p_ioletViews.getProxyComponent(), BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);
        p_ioletViews.setAlignmentY(TOP_ALIGNMENT);

        p_inletViews = new PatchPNode(patchView);
        p_inletViews.setLayout(new BoxLayout(
                p_inletViews.getProxyComponent(),
                BoxLayout.PAGE_AXIS));
        p_inletViews.setAlignmentX(LEFT_ALIGNMENT);
        p_inletViews.setAlignmentY(TOP_ALIGNMENT);
        p_inletViews.setPickable(false);

        p_outletViews = new PatchPNode(patchView);

        p_outletViews.setPickable(false);
        p_outletViews.setLayout(new BoxLayout(p_outletViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_outletViews.setAlignmentX(RIGHT_ALIGNMENT);
        p_outletViews.setAlignmentY(TOP_ALIGNMENT);

        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.PAGE_AXIS));

        PLabelComponent titleBarLabel = new PLabelComponent("zombie");//model.typeName);
        titleBarLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleBarLabel.setPickable(false);

        titleBar.addChild(popupIcon);
        titleBar.addChild(titleBarLabel);
        titleBar.setPickable(false);
        titleBar.setAlignmentX(LEFT_ALIGNMENT);

        addChild(titleBar);

        setPaint(Theme.getCurrentTheme().Object_Zombie_Background);

        instanceLabel = new PLabelComponent(model.getInstanceName());
        instanceLabel.setAlignmentX(LEFT_ALIGNMENT);

        instanceLabel.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.setHandled(true);
                }
            }
        });

        instanceLabel.setPickable(false);

        addChild(instanceLabel);

        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mousePressed(PInputEvent e) {
                if (e.isPopupTrigger() && !overPickableChild(e)) {
                    ShowPopup(e);
                }
            }

            @Override
            public void mouseClicked(PInputEvent e) {
                if (e.getPickedNode() instanceof PAxoObjectInstanceViewZombie) {
                    showReplaceClassSelector(e);
                }
            }

            @Override
            public void mouseEntered(PInputEvent e) {
                if (e.getInputManager().getMouseFocus() == null && !overPickableChild(e)) {
                    getCanvas().setToolTipText(tooltipText);
                }
            }

            @Override
            public void mouseExited(PInputEvent e) {
                if (e.getInputManager().getMouseFocus() == null) {
                    getCanvas().setToolTipText(null);
                }
            }
        });
    }

    private void showReplaceClassSelector(PInputEvent e) {
        if (e.isLeftMouseButton() && e.getClickCount() == 2) {
            ((PatchViewPiccolo) getPatchView()).ShowClassSelector(PAxoObjectInstanceViewZombie.this.getLocation(),
                    null, PAxoObjectInstanceViewZombie.this, null);
        }
    }

    @Override
    JPopupMenu CreatePopupMenu() {
        JPopupMenu popup = super.CreatePopupMenu();
        JMenuItem popm_substitute = new JMenuItem("replace");
        popm_substitute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((PatchViewPiccolo) getPatchView()).ShowClassSelector(PAxoObjectInstanceViewZombie.this.getLocation(), null, PAxoObjectInstanceViewZombie.this, null);
            }
        });
        popup.add(popm_substitute);
        JMenuItem popm_editInstanceName = new JMenuItem("edit instance name");
        popm_editInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addInstanceNameEditor();
            }
        });
        popup.add(popm_editInstanceName);
        return popup;
    }

    @Override
    public void showInstanceName(String s) {
        super.showInstanceName(s);
        resizeToGrid();
        repaint();
    }

    @Override
    public List<IIoletInstanceView> getInletInstanceViews() {
        return null; //inletInstanceViews;
    }

    @Override
    public List<IIoletInstanceView> getOutletInstanceViews() {
        return null; //outletInstanceViews;
    }

    private Map<InletInstance, IIoletInstanceView> inletInstanceViews = new HashMap<>();
    private Map<OutletInstance, IIoletInstanceView> outletInstanceViews = new HashMap<>();

    @Override
    public void addInletInstanceView(IIoletInstanceView view) {
        inletInstanceViews.put((InletInstance) view.getModel(), view);
        p_inletViews.addChild((PInletInstanceView) view);
        view.setAlignmentX(LEFT_ALIGNMENT);

    }

    @Override
    public void addOutletInstanceView(IIoletInstanceView view) {
        outletInstanceViews.put((OutletInstance) view.getModel(), view);
        p_outletViews.addChild((POutletInstanceView) view);
        view.setAlignmentX(RIGHT_ALIGNMENT);
    }

    private void finishLayout() {
        p_ioletViews.addChild(p_inletViews);
        p_ioletViews.addToSwingProxy(Box.createHorizontalGlue());
        p_ioletViews.addChild(p_outletViews);
        addChild(p_ioletViews);

        addToSwingProxy(Box.createVerticalGlue());

        resizeToGrid();
        translate(model.getX(), model.getY());
    }

    private boolean layoutFinished = false;

    public void validate() {
        if (!layoutFinished) {
            finishLayout();
            layoutFinished = true;
        }
    }

    @Override
    public boolean isZombie() {
        return true;
    }

    @Override
    public IIoletInstanceView getInletInstanceView(InletInstance inletInstance) {
        return inletInstanceViews.get(inletInstance);
    }

    @Override
    public IIoletInstanceView getOutletInstanceView(OutletInstance outletInstance) {
        return outletInstanceViews.get(outletInstance);
    }
}
