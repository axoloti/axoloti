package axoloti.piccolo.patch.object;

import axoloti.abstractui.IAttributeInstanceView;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IDisplayInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectFromPatch;
import axoloti.object.IAxoObject;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.piccolo.patch.object.attribute.PAttributeInstanceViewFactory;
import axoloti.piccolo.patch.object.display.PDisplayInstanceViewFactory;
import axoloti.piccolo.patch.object.inlet.PInletInstanceViewFactory;
import axoloti.piccolo.patch.object.outlet.POutletInstanceViewFactory;
import axoloti.piccolo.patch.object.parameter.PParameterInstanceViewFactory;
import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patch.object.attribute.AttributeInstanceView;
import axoloti.swingui.patch.object.inlet.InletInstanceView;
import axoloti.swingui.patch.object.outlet.OutletInstanceView;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceView extends PAxoObjectInstanceViewAbstract implements IAxoObjectInstanceView {

    public static final int MIN_HEIGHT = 40;
    public static final int MIN_WIDTH = 80;

    PLabelComponent IndexLabel;

    PatchPNode p_attributeViews;
    PatchPNode p_parameterViews;
    PatchPNode p_displayViews;
    PatchPNode p_ioletViews;
    PatchPNode p_inletViews;
    PatchPNode p_outletViews;
    boolean deferredObjTypeUpdate = false;

    String tooltipText = "<html>";

    public PAxoObjectInstanceView(ObjectInstanceController controller, PatchViewPiccolo patchView) {
        super(controller, patchView);
        instanceLabel = new PLabelComponent(controller.getModel().getInstanceName());
        p_parameterViews = new PatchPNode(patchView);
        p_attributeViews = new PatchPNode(patchView);
        p_inletViews = new PatchPNode(patchView);
        p_outletViews = new PatchPNode(patchView);
        p_displayViews = new PatchPNode(patchView);
        p_ioletViews = new PatchPNode(patchView);
        initComponents();
    }

    private void initComponents() {
        p_ioletViews.setLayout(new BoxLayout(p_ioletViews.getProxyComponent(), BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);

        p_inletViews.setLayout(new BoxLayout(p_inletViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_inletViews.setAlignmentY(TOP_ALIGNMENT);

        p_attributeViews.setLayout(new BoxLayout(p_attributeViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_attributeViews.setAlignmentX(LEFT_ALIGNMENT);

        p_outletViews.setLayout(new BoxLayout(p_outletViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_outletViews.setAlignmentY(TOP_ALIGNMENT);

        p_parameterViews.setAlignmentX(LEFT_ALIGNMENT);
        p_displayViews.setAlignmentX(LEFT_ALIGNMENT);

        initComponents2();
    }

    @Override
    public AxoObjectInstance getModel() {
        return (AxoObjectInstance) super.getModel();
    }

    public IAxoObject getType() {
        return getModel().getType();
    }

    List<IIoletInstanceView> inletInstanceViews;
    List<IIoletInstanceView> outletInstanceViews;
    List<IAttributeInstanceView> attributeInstanceViews;
    List<IParameterInstanceView> parameterInstanceViews;
    List<IDisplayInstanceView> displayInstanceViews;

    private void initComponents2() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.PAGE_AXIS));

        setPaint(Theme.getCurrentTheme().Object_Default_Background);
        setLocation(getModel().getX(), getModel().getY());
        setDrawBorder(true);

        p_parameterViews.setPickable(false);
        p_displayViews.setPickable(false);
        p_ioletViews.setPickable(false);
        p_inletViews.setPickable(false);
        p_attributeViews.setPickable(false);
        p_outletViews.setPickable(false);

        titleBar.addChild(popupIcon);

        PLabelComponent titleBarLabel = new PLabelComponent(getModel().getTypeName());
        titleBarLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleBarLabel.setPickable(false);

        titleBar.addChild(titleBarLabel);
        titleBar.setAlignmentX(LEFT_ALIGNMENT);
        titleBar.setPickable(false);

        addChild(titleBar);

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

        p_ioletViews.setLayout(new BoxLayout(p_ioletViews.getProxyComponent(), BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);

        p_inletViews.setLayout(new BoxLayout(p_inletViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_inletViews.setAlignmentY(TOP_ALIGNMENT);

        p_outletViews.setLayout(new BoxLayout(p_outletViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        p_outletViews.setAlignmentY(TOP_ALIGNMENT);
        if (getType().getRotatedParams()) {
            p_parameterViews.setLayout(new BoxLayout(p_parameterViews.getProxyComponent(), BoxLayout.LINE_AXIS));
        } else {
            p_parameterViews.setLayout(new BoxLayout(p_parameterViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        }

        if (getType().getRotatedParams()) {
            p_displayViews.setLayout(new BoxLayout(p_displayViews.getProxyComponent(), BoxLayout.LINE_AXIS));
        } else {
            p_displayViews.setLayout(new BoxLayout(p_displayViews.getProxyComponent(), BoxLayout.PAGE_AXIS));
        }

        p_ioletViews.addChild(p_inletViews);
        p_ioletViews.addChild(p_outletViews);
        addChild(p_ioletViews);
        addChild(p_attributeViews);
        addChild(p_parameterViews);
        addChild(p_displayViews);
        p_parameterViews.setAlignmentX(LEFT_ALIGNMENT);
        p_displayViews.setAlignmentX(LEFT_ALIGNMENT);

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

        resizeToGrid();
        setVisible(true);
        invalidate();
    }

    private void showReplaceClassSelector(PInputEvent e) {
        if (e.isLeftMouseButton() && e.getClickCount() == 2) {
            ((PatchViewPiccolo) getPatchView()).ShowClassSelector(PAxoObjectInstanceView.this.getLocation(), null, PAxoObjectInstanceView.this, null);
        }
    }

    private void initializeTooltipText() {
        StringBuilder tooltipTextBuilder = new StringBuilder();
        if ((getType().getDescription() != null) && (!getType().getDescription().isEmpty())) {
            tooltipTextBuilder.append(getType().getDescription());
        }
        if ((getType().getAuthor() != null) && (!getType().getAuthor().isEmpty())) {
            tooltipTextBuilder.append("<p>Author: " + getType().getAuthor());
        }
        if ((getType().getLicense() != null) && (!getType().getLicense().isEmpty())) {
            tooltipTextBuilder.append("<p>License: " + getType().getLicense());
        }
        if ((getType().getPath() != null) && (!getType().getPath().isEmpty())) {
            tooltipTextBuilder.append("<p>Path: " + getType().getPath());
        }
        if(tooltipTextBuilder.length() > 0) {
            tooltipText = tooltipTextBuilder.toString();
        }
    }

    HashMap<AbstractController, IView> view_cache = new HashMap<>();

    ArrayView<IIoletInstanceView> inletInstanceViewSync = new ArrayView<IIoletInstanceView>() {
        @Override
        protected IIoletInstanceView viewFactory(AbstractController ctrl) {
            IIoletInstanceView view = (InletInstanceView) view_cache.get(ctrl);
            if (view == null) {
                view = PInletInstanceViewFactory.createView((IoletInstanceController) ctrl, PAxoObjectInstanceView.this);
            }
            return view;
        }

            @Override
        protected void updateUI(List<IIoletInstanceView> views) {
                p_inletViews.removeAllChildren();
                for (IIoletInstanceView c : views) {
                    p_inletViews.addChild((PatchPNode) c);
                }
            }

            @Override
        protected void removeView(IIoletInstanceView view) {
                view_cache.put(view.getController(), view);
            }
        };

    ArrayView<IIoletInstanceView> outletInstanceViewSync = new ArrayView<IIoletInstanceView>() {
        @Override
        protected IIoletInstanceView viewFactory(AbstractController ctrl) {
            IIoletInstanceView view = (OutletInstanceView) view_cache.get(ctrl);
            if (view == null) {
                view = POutletInstanceViewFactory.createView((IoletInstanceController) ctrl, PAxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IIoletInstanceView> views) {
            p_outletViews.removeAllChildren();
            for (IIoletInstanceView c : views) {
                p_outletViews.addChild((PatchPNode) c);
            }
        }

        @Override
        protected void removeView(IIoletInstanceView view) {
                view_cache.put(view.getController(), view);
            }
        };

    ArrayView<IAttributeInstanceView> attributeInstanceViewSync = new ArrayView<IAttributeInstanceView>() {

        @Override
        protected IAttributeInstanceView viewFactory(AbstractController ctrl) {
            IAttributeInstanceView view = (AttributeInstanceView) view_cache.get(ctrl);
            if (view == null) {
                view = PAttributeInstanceViewFactory.createView((AttributeInstanceController) ctrl, PAxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IAttributeInstanceView> views) {
            p_attributeViews.removeAllChildren();
            for (IAttributeInstanceView c : views) {
                p_attributeViews.addChild((PatchPNode) c);
            }
        }

        @Override
        protected void removeView(IAttributeInstanceView view) {
            view_cache.put(view.getController(), view);
        }
    };

    ArrayView<IParameterInstanceView> parameterInstanceViewSync = new ArrayView<IParameterInstanceView>() {
        @Override
        protected IParameterInstanceView viewFactory(AbstractController ctrl) {
            IParameterInstanceView view = (IParameterInstanceView) view_cache.get(ctrl);
            if (view == null) {
                view = PParameterInstanceViewFactory.createView((ParameterInstanceController) ctrl, PAxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IParameterInstanceView> views) {
            p_parameterViews.removeAllChildren();
            for (IParameterInstanceView c : views) {
                p_parameterViews.addChild((PatchPNode) c);
            }
        }

        @Override
        protected void removeView(IParameterInstanceView view) {
            view_cache.put(view.getController(), view);
        }
    };

    ArrayView<IDisplayInstanceView> displayInstanceViewSync = new ArrayView<IDisplayInstanceView>() {
        @Override
        protected IDisplayInstanceView viewFactory(AbstractController ctrl) {
            IDisplayInstanceView view = (IDisplayInstanceView) view_cache.get(ctrl);
            if (view == null) {
                view = PDisplayInstanceViewFactory.createView((DisplayInstanceController) ctrl, PAxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IDisplayInstanceView> views) {
            p_displayViews.removeAllChildren();
            for (IDisplayInstanceView c : views) {
                p_displayViews.addChild((PatchPNode) c);
            }
        }

        @Override
        protected void removeView(IDisplayInstanceView view) {
            view_cache.put(view.getController(), view);
        }
    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoObjectInstance.OBJ_INLET_INSTANCES.is(evt)) {
            inletInstanceViews = inletInstanceViewSync.Sync(inletInstanceViews, getController().getModel().getInletInstances());
        } else if (AxoObjectInstance.OBJ_OUTLET_INSTANCES.is(evt)) {
            outletInstanceViews = outletInstanceViewSync.Sync(outletInstanceViews, getController().getModel().getOutletInstances());
        } else if (AxoObjectInstance.OBJ_ATTRIBUTE_INSTANCES.is(evt)) {
            attributeInstanceViews = attributeInstanceViewSync.Sync(attributeInstanceViews, getController().getModel().getAttributeInstances());
        } else if (AxoObjectInstance.OBJ_PARAMETER_INSTANCES.is(evt)) {
            parameterInstanceViews = parameterInstanceViewSync.Sync(parameterInstanceViews, getController().getModel().getParameterInstances());
        } else if (AxoObjectInstance.OBJ_DISPLAY_INSTANCES.is(evt)) {
            displayInstanceViews = displayInstanceViewSync.Sync(displayInstanceViews, getController().getModel().getDisplayInstances());
        } else if (AxoObject.OBJ_DESCRIPTION.is(evt)
                || AxoObject.OBJ_AUTHOR.is(evt)
                || AxoObject.OBJ_LICENSE.is(evt)) {
            // updateTooltext(); // TODO: implement for piccolo
        }
        resizeToGrid();
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
        if (getType().getHelpPatchFile() != null) {
            JMenuItem popm_help = new JMenuItem("help");
            popm_help.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    PatchViewSwing.OpenPatch(getType().getHelpPatchFile());
                }
            });
            popup.add(popm_help);
        }
        if (Preferences.getPreferences().getExpertMode()) {
            JMenuItem popm_adapt = new JMenuItem("adapt homonym");
            popm_adapt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getController().getModel().getParent().getControllerFromModel().PromoteToOverloadedObj(getModel());
                }
            });
            popup.add(popm_adapt);
        }

        if (getModel().getType() instanceof AxoObjectFromPatch) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/patcher");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (!getPatchView().isLocked()) {
                        getController().addMetaUndo("embed");
                        getController().getModel().getParent().getControllerFromModel().ConvertToPatchPatcher(getModel());
                    }
                }
            });
            popup.add(popm_embed);
        } else if (!(this instanceof PAxoObjectInstanceViewPatcherObject)) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/object");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getController().addMetaUndo("embed");
                    getController().getModel().getParent().getControllerFromModel().ConvertToEmbeddedObj(getModel());
                }
            });
            popup.add(popm_embed);
        }
        return popup;
    }

    public void OpenEditor() {
        getType().OpenEditor();
    }

    @Override
    public void Lock() {
        super.Lock();
        for (IAttributeInstanceView a : attributeInstanceViews) {
            a.Lock();
        }
    }

    @Override
    public void Unlock() {
        super.Unlock();
        for (IAttributeInstanceView a : attributeInstanceViews) {
            a.UnLock();
        }
        if (deferredObjTypeUpdate) {
            //model.updateObj();
            deferredObjTypeUpdate = false;
        }
    }

    @Override
    public List<IIoletInstanceView> getInletInstanceViews() {
        return inletInstanceViews;
    }

    @Override
    public List<IIoletInstanceView> getOutletInstanceViews() {
        return outletInstanceViews;
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return parameterInstanceViews;
    }
}
