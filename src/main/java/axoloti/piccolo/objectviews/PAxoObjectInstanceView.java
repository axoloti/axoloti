package axoloti.piccolo.objectviews;

import axoloti.patch.PatchViewPiccolo;
import axoloti.preferences.Theme;
import axoloti.abstractui.IAttributeInstanceView;
import axoloti.object.display.Display;
import axoloti.abstractui.IDisplayInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.object.inlet.Inlet;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectFromPatch;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.object.outlet.Outlet;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.piccolo.PatchPNode;
import axoloti.piccolo.attributeviews.PAttributeInstanceView;
import axoloti.piccolo.displayviews.PDisplayInstanceView;
import axoloti.piccolo.inlets.PInletInstanceView;
import axoloti.piccolo.outlets.POutletInstanceView;
import axoloti.piccolo.parameterviews.PParameterInstanceView;
import axoloti.swingui.patch.object.attribute.AttributeInstanceView;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.preferences.Preferences;
import axoloti.piccolo.components.PLabelComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.RIGHT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceView extends PAxoObjectInstanceViewAbstract implements IAxoObjectInstanceView {

    public static final int MIN_HEIGHT = 40;
    public static final int MIN_WIDTH = 80;

    private AxoObjectInstance model;

    PLabelComponent IndexLabel;

    public PatchPNode p_parameterViews;
    public PatchPNode p_displayViews;
    public PatchPNode p_ioletViews;
    public PatchPNode p_inletViews;
    public PatchPNode p_outletViews;
    boolean deferredObjTypeUpdate = false;

    //private ArrayView<IInletInstanceView> inletInstanceViews;
    //private ArrayView<IOutletInstanceView> outletInstanceViews;
    //private ArrayView<IParameterInstanceView> parameterInstanceViews;

    String tooltipText = "<html>";

    public PAxoObjectInstanceView(AxoObjectInstance model, PatchViewPiccolo patchView) {
        super(model, patchView);
        this.model = model;
    }

    public AxoObject getType() {
        return (AxoObject)model.getType();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        setPaint(Theme.getCurrentTheme().Object_Default_Background);
        //model.updateObj1();

        setLocation(model.getX(), model.getY());
        setDrawBorder(true);

        p_parameterViews = new PatchPNode(patchView);
        p_parameterViews.setLayout(new BoxLayout(p_parameterViews.getProxyComponent(),
                getType().getRotatedParams() ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS));
        p_parameterViews.setPickable(false);
        p_displayViews = new PatchPNode(patchView);
        p_displayViews.setLayout(new BoxLayout(p_displayViews.getProxyComponent(),
                getType().getRotatedParams() ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS));
        p_displayViews.setPickable(false);
        p_ioletViews = new PatchPNode(patchView);
        p_ioletViews.setLayout(new BoxLayout(p_ioletViews.getProxyComponent(), BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);
        p_ioletViews.setAlignmentY(TOP_ALIGNMENT);
        p_ioletViews.setPickable(false);

        p_inletViews = new PatchPNode(patchView);
        p_inletViews.setLayout(new BoxLayout(
                p_inletViews.getProxyComponent(),
                BoxLayout.PAGE_AXIS));
        p_inletViews.setAlignmentX(LEFT_ALIGNMENT);
        p_inletViews.setAlignmentY(TOP_ALIGNMENT);
        p_inletViews.setPickable(false);

        p_outletViews = new PatchPNode(patchView);
        p_outletViews.setLayout(new BoxLayout(p_outletViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_outletViews.setPickable(false);
        p_outletViews.setAlignmentX(RIGHT_ALIGNMENT);
        p_outletViews.setAlignmentY(TOP_ALIGNMENT);

        //ArrayModel<ParameterInstance> pParameterInstances = getModel().getParameterInstances();
        //ArrayModel<AttributeInstance> pAttributeInstances = getModel().getAttributeInstances();
        Collection<InletInstance> pInletInstances = getModel().getInletInstances();
        Collection<OutletInstance> pOutletInstances = getModel().getOutletInstances();

//        getModel().setParameterInstances(new ArrayModel<>());
//        getModel().setAttributeInstances(new ArrayModel<>());
//        getModel().setDisplayInstances(new ArrayModel<>());
//        getModel().setInletInstances(new ArrayModel<>());
//        getModel().setOutletInstances(new ArrayModel<>());

        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.PAGE_AXIS));

        titleBar.addChild(popupIcon);

        PLabelComponent titleBarLabel = new PLabelComponent(model.getController().getModel().getId());
        titleBarLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleBarLabel.setPickable(false);

        titleBar.addChild(titleBarLabel);
        titleBar.setAlignmentX(LEFT_ALIGNMENT);
        titleBar.setMinimumSize(TITLEBAR_MINIMUM_SIZE);
        titleBar.setMaximumSize(TITLEBAR_MAXIMUM_SIZE);
        titleBar.setPickable(false);

        addChild(titleBar);

        instanceLabel = new PLabelComponent(model.getInstanceName());
        instanceLabel.setAlignmentX(LEFT_ALIGNMENT);

        addChild(instanceLabel);

        initializeTooltipText();

        instanceLabel.setPickable(true);
        instanceLabel.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.setHandled(true);
                }
            }
        });

        p_parameterViews.setPickable(false);
        p_parameterViews.setAlignmentX(LEFT_ALIGNMENT);

        p_displayViews.setPickable(false);
        p_displayViews.setAlignmentX(LEFT_ALIGNMENT);

        p_displayViews.addToSwingProxy(Box.createHorizontalGlue());
        p_parameterViews.addToSwingProxy(Box.createHorizontalGlue());
        for (Inlet inlet : getType().getInlets()) {
            InletInstance inletInstanceP = null;
            for (InletInstance inletInstance : pInletInstances) {
                if (inletInstance.GetLabel().equals(inlet.getName())) {
                    inletInstanceP = inletInstance;
                }
            }
//            InletInstance inletInstance = new InletInstance(inlet, getModel());
            if (inletInstanceP != null) {
//                Net n = getPatchModel().GetNet(inletInstanceP);
//                if (n != null) {
//                    n.connectInlet(inletInstance);
//                }
            }
//            getModel().getInletInstances().add(inletInstance);
            // TODO: PICCOLO view factory
            PInletInstanceView view = null; // (PInletInstanceView) inletInstance.createView(this);
            view.setAlignmentX(LEFT_ALIGNMENT);
            //inletInstanceViews.add(view);
        }

        // disconnect stale inlets from nets
        for (InletInstance inletInstance : pInletInstances) {
//            getPatchModel().disconnect(inletInstance);
        }

        for (Outlet o : getType().getOutlets()) {
            OutletInstance outletInstanceP = null;
            for (OutletInstance outletInstance : pOutletInstances) {
                if (outletInstance.GetLabel().equals(o.getName())) {
                    outletInstanceP = outletInstance;
                }
            }
//            OutletInstance outletInstance = new OutletInstance(o, getModel());
            if (outletInstanceP != null) {
//                Net n = getPatchModel().GetNet(outletInstanceP);
//                if (n != null) {
//                    n.connectOutlet(outletInstance);
//                }
            }
//            getModel().getOutletInstances().add(outletInstance);
            POutletInstanceView view = null;
            // TODO: PICCOLO view factory
            // ... = (POutletInstanceView) outletInstance.createView(this);
            view.setAlignmentX(RIGHT_ALIGNMENT);
            //outletInstanceViews.add(view);
        }

        // disconnect stale outlets from nets
        for (OutletInstance outletInstance : pOutletInstances) {
//            getPatchModel().disconnect(outletInstance);
        }

        p_ioletViews.addChild(p_inletViews);
        p_ioletViews.addToSwingProxy(Box.createHorizontalGlue());
        p_ioletViews.addChild(p_outletViews);
        addChild(p_ioletViews);
/*
        for (AxoAttribute p : getType().attributes) {
            AttributeInstance attributeInstanceP = null;
            for (AttributeInstance attributeInstance : pAttributeInstances) {
                if (attributeInstance.getName().equals(p.getName())) {
                    attributeInstanceP = attributeInstance;
                }
            }
//            AttributeInstance attributeInstance1 = p.CreateInstance(getModel(), attributeInstanceP);
            PAttributeInstanceView attributeInstanceView = null;
            // TODO: implement PICCOLO view factory
            // ... = (PAttributeInstanceView) attributeInstance1.createView(this);
            attributeInstanceView.setAlignmentX(LEFT_ALIGNMENT);
            addChild(attributeInstanceView);
//            getModel().getAttributeInstances().add(attributeInstance1);
        }

        for (Parameter p : getType().params) {
            ParameterInstance pin = p.CreateInstance(getModel());
            for (ParameterInstance pinp : pParameterInstances) {
                if (pinp.getName().equals(pin.getName())) {
                    pin.CopyValueFrom(pinp);
                }
            }
            PParameterInstanceView view = null;
            // TODO: implement PICCOLO view factory
            // ... = (PParameterInstanceView) pin.createView(this);
            view.setAlignmentX(RIGHT_ALIGNMENT);
            getModel().getParameterInstances().add(pin);
        }
*/
        for (Display p : getType().displays) {
//            DisplayInstance pin = p.CreateInstance(getModel());
            PDisplayInstanceView view = null;
            // TODO: implement PICCOLO view factory
            // ... = (PDisplayInstanceView) pin.createView(this);
            view.setAlignmentX(RIGHT_ALIGNMENT);
//            getModel().getDisplayInstances().add(pin);
        }

        addChild(p_parameterViews);
        addChild(p_displayViews);
        addToSwingProxy(Box.createVerticalGlue());

        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mousePressed(PInputEvent e) {
                if (e.isPopupTrigger() && !overPickableChild(e)) {
                    ShowPopup(e);
                }
            }

            @Override
            public void mouseClicked(PInputEvent e) {
                if (e.getPickedNode() instanceof PAxoObjectInstanceView) {
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

        validate();
        resizeToGrid();
        translate(model.getX(), model.getY());
    }

    private void showReplaceClassSelector(PInputEvent e) {
        if (e.isLeftMouseButton() && e.getClickCount() == 2) {
            ((PatchViewPiccolo) getPatchView()).ShowClassSelector(PAxoObjectInstanceView.this.getLocation(), null, PAxoObjectInstanceView.this, null);
        }
    }

    private void initializeTooltipText() {
        if ((getType().getDescription() != null) && (!getType().getDescription().isEmpty())) {
            tooltipText += getType().getDescription();
        }
        if ((getType().getAuthor() != null) && (!getType().getAuthor().isEmpty())) {
            tooltipText += "<p>Author: " + getType().getAuthor();
        }
        if ((getType().getLicense() != null) && (!getType().getLicense().isEmpty())) {
            tooltipText += "<p>License: " + getType().getLicense();
        }
        if ((getType().getPath() != null) && (!getType().getPath().isEmpty())) {
            tooltipText += "<p>Path: " + getType().getPath();
        }
    }

    @Override
    JPopupMenu CreatePopupMenu() {
        JPopupMenu popup = super.CreatePopupMenu();
        JMenuItem popm_edit = new JMenuItem("edit object definition");
        popm_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                OpenEditor();
            }
        });
        popup.add(popm_edit);
        JMenuItem popm_editInstanceName = new JMenuItem("edit instance name");
        popm_editInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addInstanceNameEditor();
            }
        });
        popup.add(popm_editInstanceName);
        JMenuItem popm_substitute = new JMenuItem("replace");
        popm_substitute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((PatchViewPiccolo) getPatchView()).ShowClassSelector(PAxoObjectInstanceView.this.getLocation(), null, PAxoObjectInstanceView.this, null);
            }
        });
        popup.add(popm_substitute);
        if (getType().GetHelpPatchFile() != null) {
            JMenuItem popm_help = new JMenuItem("help");
            popm_help.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    PatchViewSwing.OpenPatch(getType().GetHelpPatchFile());
                }
            });
            popup.add(popm_help);
        }
        if (Preferences.getPreferences().getExpertMode()) {
            JMenuItem popm_adapt = new JMenuItem("adapt homonym");
            popm_adapt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getController().getParent().PromoteToOverloadedObj(getModel());
                }
            });
            popup.add(popm_adapt);
        }

        if (model.getType() instanceof AxoObjectFromPatch) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/patcher");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (!getPatchView().isLocked()) {
                        //model.ConvertToPatchPatcher();
                    }
                }
            });
            popup.add(popm_embed);
        } else if (!(this instanceof PAxoObjectInstanceViewPatcherObject)) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/object");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (!getPatchView().isLocked()) {
                        //getController().ConvertToEmbeddedObj();
                    }
                }
            });
            popup.add(popm_embed);
        }
        return popup;
    }

    public void refreshIndex() {
        if (getPatchView() != null && IndexLabel != null) {
//            IndexLabel.setText(" " + getPatchView().getObjectInstanceViews().getSubViews().indexOf(this));
        }
    }

    public void OpenEditor() {
        getType().OpenEditor(model.editorBounds, model.editorActiveTabIndex);
    }

    public ArrayList<AttributeInstanceView> attributeInstanceViews = new ArrayList<AttributeInstanceView>();

    @Override
    public void Lock() {
        super.Lock();
        for (AttributeInstanceView a : attributeInstanceViews) {
            a.Lock();
        }
    }

    @Override
    public void Unlock() {
        super.Unlock();
        for (AttributeInstanceView a : attributeInstanceViews) {
            a.UnLock();
        }
        if (deferredObjTypeUpdate) {
            //model.updateObj();
            deferredObjTypeUpdate = false;
        }
    }

    @Override
    public AxoObjectInstance getModel() {
        return model;
    }

    @Override
    public List<IIoletInstanceView> getInletInstanceViews() {
        return null;//inletInstanceViews;
    }

    @Override
    public List<IIoletInstanceView> getOutletInstanceViews() {
        return null;//outletInstanceViews;
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return null;//parameterInstanceViews;
    }

    @Override
    public void addParameterInstanceView(IParameterInstanceView view) {
        p_parameterViews.addChild((PParameterInstanceView) view);
    }

    @Override
    public void addAttributeInstanceView(IAttributeInstanceView view) {
        addChild((PAttributeInstanceView) view);
    }

    @Override
    public void addDisplayInstanceView(IDisplayInstanceView view) {
        p_displayViews.addChild((PDisplayInstanceView) view);
    }

    @Override
    public void addOutletInstanceView(IIoletInstanceView view) {
        p_outletViews.addChild((POutletInstanceView) view);
    }

    @Override
    public void addInletInstanceView(IIoletInstanceView view) {
        p_inletViews.addChild((PInletInstanceView) view);
    }
}
