package axoloti.piccolo.patch.object;

import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceViewZombie extends PAxoObjectInstanceViewAbstract {

    public PatchPNode p_ioletViews;
    public PatchPNode p_inletViews;
    public PatchPNode p_outletViews;

    private String tooltipText = "<html>" + "Unresolved object!";

    public PAxoObjectInstanceViewZombie(IAxoObjectInstance objectInstance, PatchViewPiccolo p) {
        super(objectInstance, p);
        initComponents();
    }

    private void initComponents() {
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

        instanceLabel = new PLabelComponent(getDModel().getInstanceName());
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
                    showPopup(e);
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
            ((PatchViewPiccolo) getPatchView()).showClassSelector(PAxoObjectInstanceViewZombie.this.getLocation(),
                    null, PAxoObjectInstanceViewZombie.this, null);
        }
    }

    @Override
    JPopupMenu createPopupMenu() {
        JPopupMenu popup = super.createPopupMenu();
        JMenuItem popm_substitute = new JMenuItem("replace");
        popm_substitute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((PatchViewPiccolo) getPatchView()).showClassSelector(PAxoObjectInstanceViewZombie.this.getLocation(), null, PAxoObjectInstanceViewZombie.this, null);
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
    public List<IInletInstanceView> getInletInstanceViews() {
        return null; // TODO: piccolo zombie inletInstanceViews;
    }

    @Override
    public List<IOutletInstanceView> getOutletInstanceViews() {
        return null; // TODO: piccolo zombie outletInstanceViews;
    }

    private Map<InletInstance, IInletInstanceView> inletInstanceViews = new HashMap<>();
    private Map<OutletInstance, IOutletInstanceView> outletInstanceViews = new HashMap<>();

    private void finishLayout() {
        p_ioletViews.addChild(p_inletViews);
        p_ioletViews.addChild(p_outletViews);
        addChild(p_ioletViews);

        resizeToGrid();
    }

    private boolean layoutFinished = false;

    public void validate() {
        if (!layoutFinished) {
            finishLayout();
            layoutFinished = true;
        }
    }

    @Override
    public IInletInstanceView getInletInstanceView(InletInstance inletInstance) {
        return inletInstanceViews.get(inletInstance);
    }

    @Override
    public IOutletInstanceView getOutletInstanceView(OutletInstance outletInstance) {
        return outletInstanceViews.get(outletInstance);
    }
}
