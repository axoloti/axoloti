package axoloti.swingui.patch.object;

import axoloti.abstractui.IAbstractEditor;
import axoloti.abstractui.IAttributeInstanceView;
import axoloti.abstractui.IDisplayInstanceView;
import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.mvc.IView;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectFromPatch;
import axoloti.object.IAxoObject;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.atom.AtomInstance;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.PopupIcon;
import axoloti.swingui.objecteditor.ObjectEditorFactory;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patch.object.attribute.AttributeInstanceView;
import axoloti.swingui.patch.object.attribute.AttributeInstanceViewFactory;
import axoloti.swingui.patch.object.display.DisplayInstanceViewFactory;
import axoloti.swingui.patch.object.inlet.InletInstanceView;
import axoloti.swingui.patch.object.inlet.InletInstanceViewFactory;
import axoloti.swingui.patch.object.outlet.OutletInstanceView;
import axoloti.swingui.patch.object.outlet.OutletInstanceViewFactory;
import axoloti.swingui.patch.object.parameter.ParameterInstanceViewFactory;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;

public class AxoObjectInstanceView extends AxoObjectInstanceViewAbstract {

    LabelComponent indexLabel;
    final JPanel p_attributeViews = new JPanel();
    final JPanel p_parameterViews = new JPanel();
    final JPanel p_displayViews = new JPanel();
    final JPanel p_ioletViews = new JPanel();
    final JPanel p_inletViews = new JPanel();
    final JPanel p_outletViews = new JPanel();

    public AxoObjectInstanceView(IAxoObjectInstance objectInstance, PatchViewSwing patchView) {
        super(objectInstance, patchView);
        initComponents();
    }

    public IAxoObject getType() {
        return getDModel().getDModel();
    }

    private void initComponents() {
        p_ioletViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_ioletViews.setLayout(new BoxLayout(p_ioletViews, BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);
        p_ioletViews.setAlignmentY(TOP_ALIGNMENT);

        p_inletViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_inletViews.setLayout(new BoxLayout(p_inletViews, BoxLayout.PAGE_AXIS));
        p_inletViews.setAlignmentY(TOP_ALIGNMENT);

        p_attributeViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_attributeViews.setLayout(new BoxLayout(p_attributeViews, BoxLayout.PAGE_AXIS));
        p_attributeViews.setAlignmentX(LEFT_ALIGNMENT);

        p_outletViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_outletViews.setLayout(new BoxLayout(p_outletViews, BoxLayout.PAGE_AXIS));
        p_outletViews.setAlignmentY(TOP_ALIGNMENT);

        p_parameterViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_parameterViews.setAlignmentX(LEFT_ALIGNMENT);

        p_displayViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_displayViews.setAlignmentX(LEFT_ALIGNMENT);

        initComponents2();
    }

    List<IInletInstanceView> inletInstanceViews = Collections.emptyList();
    List<IOutletInstanceView> outletInstanceViews = Collections.emptyList();
    List<IAttributeInstanceView> attributeInstanceViews = Collections.emptyList();
    List<IParameterInstanceView> parameterInstanceViews = Collections.emptyList();
    List<IDisplayInstanceView> displayInstanceViews = Collections.emptyList();

    void updateTooltext() {
        StringBuilder tooltiptxt = new StringBuilder("<html>");
        if ((getType().getDescription() != null) && (!getType().getDescription().isEmpty())) {
            tooltiptxt.append(getType().getDescription());
        }
        if ((getType().getAuthor() != null) && (!getType().getAuthor().isEmpty())) {
            tooltiptxt.append("<p>Author: ");
            tooltiptxt.append(getType().getAuthor());
        }
        if ((getType().getLicense() != null) && (!getType().getLicense().isEmpty())) {
            tooltiptxt.append("<p>License: ");
            tooltiptxt.append(getType().getLicense());
        }
        if ((getType().getPath() != null) && (!getType().getPath().isEmpty())) {
            tooltiptxt.append("<p>Path: ");
            tooltiptxt.append(getType().getPath());
        }
        titlebar.setToolTipText(tooltiptxt.toString());
    }

    private void initComponents2() {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final PopupIcon popupIcon = new PopupIcon();
        popupIcon.setPopupIconListener(new PopupIcon.PopupIconListener() {
            @Override
            public void showPopup() {
                JPopupMenu popup = createPopupMenu();
                popupIcon.add(popup);
                popup.show(popupIcon,
                        0, popupIcon.getHeight());
            }
        });
        titlebar.add(popupIcon);

        LabelComponent idlbl = new LabelComponent(model.getTypeName());
        idlbl.setAlignmentX(LEFT_ALIGNMENT);
        idlbl.setForeground(Theme.getCurrentTheme().Object_TitleBar_Foreground);
        titlebar.add(idlbl);
        updateTooltext();

        /*
         h.add(Box.createHorizontalStrut(3));
         h.add(Box.createHorizontalGlue());
         h.add(new JSeparator(SwingConstants.VERTICAL));*/
        ////IndexLabel not shown, maybe useful later...
        //IndexLabel.setSize(IndexLabel.getMinimumSize());
        //IndexLabel = new LabelComponent("");
        //refreshIndex();
        //h.add(IndexLabel);
        //IndexLabel.setAlignmentX(RIGHT_ALIGNMENT);
        titlebar.setAlignmentX(LEFT_ALIGNMENT);
        add(titlebar);
        instanceLabel = new LabelComponent(getDModel().getInstanceName());
        instanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        instanceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.consume();
                }
            }
        });
        add(instanceLabel);

        // without this invisible border, initial size is incorrect
        // for patch/patcher inlets/outlets?
        p_ioletViews.setBorder(new LineBorder(Theme.getCurrentTheme().Object_Default_Background));

//        p_outletViews.setBorder(new LineBorder(Color.red));
//        p_ioletViews.setBorder(new LineBorder(Color.green));
//        p_inletViews.setBorder(new LineBorder(Color.yellow));

        if (getType().getRotatedParams()) {
            p_parameterViews.setLayout(new BoxLayout(p_parameterViews, BoxLayout.LINE_AXIS));
        } else {
            p_parameterViews.setLayout(new BoxLayout(p_parameterViews, BoxLayout.PAGE_AXIS));
        }

        if (getType().getRotatedParams()) {
            p_displayViews.setLayout(new BoxLayout(p_displayViews, BoxLayout.LINE_AXIS));
        } else {
            p_displayViews.setLayout(new BoxLayout(p_displayViews, BoxLayout.PAGE_AXIS));
        }
        p_displayViews.add(Box.createHorizontalGlue());
        p_parameterViews.add(Box.createHorizontalGlue());

        p_ioletViews.add(p_inletViews);
        p_ioletViews.add(p_outletViews);
        add(p_ioletViews);
        add(p_attributeViews);
        add(p_parameterViews);
        add(p_displayViews);

        resizeToGrid();
        setVisible(true);
        repaint();
        revalidate();
    }

    @Override
    public void resizeToGrid() {
        boolean dbg_print = false;
        if (dbg_print) {
            System.out.println("inlets  : ");
            for (Component c : p_inletViews.getComponents()) {
                System.out.println("    - " + c.toString());
            }
            System.out.println("outlets  : ");
            for (Component c : p_outletViews.getComponents()) {
                System.out.println("    - " + c.toString());
            }
            System.out.println("params: ");
            for (Component c : p_parameterViews.getComponents()) {
                System.out.println("    - " + c.toString());
            }
        }
        super.resizeToGrid();
    }

    HashMap<AtomInstance, IView> view_cache = new HashMap<>();

    ArrayView<IInletInstanceView, InletInstance> inletInstanceViewSync = new ArrayView<IInletInstanceView, InletInstance>() {
        @Override
        protected InletInstanceView viewFactory(InletInstance inlet) {
            InletInstanceView view = (InletInstanceView) view_cache.get(inlet);
            if (view == null) {
                view = InletInstanceViewFactory.createView(inlet, AxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IInletInstanceView> views) {
            p_inletViews.removeAll();
            for (IIoletInstanceView c : views) {
                p_inletViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        protected void removeView(IInletInstanceView view) {
            view_cache.put(view.getDModel(), view);
        }
    };

    ArrayView<IOutletInstanceView, OutletInstance> outletInstanceViewSync = new ArrayView<IOutletInstanceView, OutletInstance>() {
        @Override
        protected OutletInstanceView viewFactory(OutletInstance outlet) {
            OutletInstanceView view = (OutletInstanceView) view_cache.get(outlet);
            if (view == null) {
                view = OutletInstanceViewFactory.createView(outlet, AxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IOutletInstanceView> views) {
            p_outletViews.removeAll();
            for (IIoletInstanceView c : views) {
                p_outletViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        protected void removeView(IOutletInstanceView view) {
            view_cache.put(view.getDModel(), view);
        }
    };

    ArrayView<IAttributeInstanceView, AttributeInstance> attributeInstanceViewSync = new ArrayView<IAttributeInstanceView, AttributeInstance>() {
        @Override
        protected AttributeInstanceView viewFactory(AttributeInstance attribute) {
            AttributeInstanceView view = (AttributeInstanceView) view_cache.get(attribute);
            if (view == null) {
                view = AttributeInstanceViewFactory.createView(attribute, AxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IAttributeInstanceView> views) {
            p_attributeViews.removeAll();
            for (IAttributeInstanceView c : views) {
                p_attributeViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        protected void removeView(IAttributeInstanceView view) {
            view_cache.put(view.getDModel(), view);
        }
    };

    ArrayView<IParameterInstanceView, ParameterInstance> parameterInstanceViewSync = new ArrayView<IParameterInstanceView, ParameterInstance>() {
        @Override
        protected IParameterInstanceView viewFactory(ParameterInstance parameter) {
            IParameterInstanceView view = (IParameterInstanceView) view_cache.get(parameter);
            if (view == null) {
                view = ParameterInstanceViewFactory.createView(parameter, AxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IParameterInstanceView> views) {
            p_parameterViews.removeAll();
            for (IParameterInstanceView c : views) {
                p_parameterViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        protected void removeView(IParameterInstanceView view) {
            view_cache.put(view.getDModel(), view);
        }
    };

    ArrayView<IDisplayInstanceView, DisplayInstance> displayInstanceViewSync = new ArrayView<IDisplayInstanceView, DisplayInstance>() {
        @Override
        protected IDisplayInstanceView viewFactory(DisplayInstance display) {
            IDisplayInstanceView view = (IDisplayInstanceView) view_cache.get(display);
            if (view == null) {
                view = DisplayInstanceViewFactory.createView(display, AxoObjectInstanceView.this);
            }
            return view;
        }

        @Override
        protected void updateUI(List<IDisplayInstanceView> views) {
            p_displayViews.removeAll();
            for (IDisplayInstanceView c : views) {
                p_displayViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        protected void removeView(IDisplayInstanceView view) {
            view_cache.put(view.getDModel(), view);
        }
    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoObjectInstance.OBJ_INLET_INSTANCES.is(evt)) {
            inletInstanceViews = inletInstanceViewSync.sync(inletInstanceViews, getDModel().getInletInstances());
        } else if (AxoObjectInstance.OBJ_OUTLET_INSTANCES.is(evt)) {
            outletInstanceViews = outletInstanceViewSync.sync(outletInstanceViews, getDModel().getOutletInstances());
        } else if (AxoObjectInstance.OBJ_ATTRIBUTE_INSTANCES.is(evt)) {
            attributeInstanceViews = attributeInstanceViewSync.sync(attributeInstanceViews, getDModel().getAttributeInstances());
        } else if (AxoObjectInstance.OBJ_PARAMETER_INSTANCES.is(evt)) {
            parameterInstanceViews = parameterInstanceViewSync.sync(parameterInstanceViews, getDModel().getParameterInstances());
        } else if (AxoObjectInstance.OBJ_DISPLAY_INSTANCES.is(evt)) {
            displayInstanceViews = displayInstanceViewSync.sync(displayInstanceViews, getDModel().getDisplayInstances());
        } else if (AxoObject.OBJ_DESCRIPTION.is(evt)
                || AxoObject.OBJ_AUTHOR.is(evt)
                || AxoObject.OBJ_LICENSE.is(evt)) {
            updateTooltext();
        }
    }

    @Override
    JPopupMenu createPopupMenu() {
        JPopupMenu popup = super.createPopupMenu();
        JMenuItem popm_edit = new JMenuItem("edit object definition");
        popm_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                openEditor();
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
                getPatchView().showClassSelector(AxoObjectInstanceView.this.getLocation(), null, AxoObjectInstanceView.this, null);
            }
        });
        popup.add(popm_substitute);
        if (getType().getHelpPatchFile() != null) {
            JMenuItem popm_help = new JMenuItem("help");
            popm_help.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    PatchViewSwing.openPatch(getType().getHelpPatchFile());
                }
            });
            popup.add(popm_help);
        }
        if (Preferences.getPreferences().getExpertMode()) {
            JMenuItem popm_adapt = new JMenuItem("adapt homonym");
            popm_adapt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getDModel().getParent().getController().promoteToOverloadedObj(getDModel());
                }
            });
            popup.add(popm_adapt);
        }

        if (getDModel().getDModel() instanceof AxoObjectFromPatch) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/patcher");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (!getPatchView().isLocked()) {
                        model.getController().addMetaUndo("embed");
                        getDModel().getParent().getController().convertToPatchPatcher(getDModel());
                    }
                }
            });
            popup.add(popm_embed);
        } else if (!(this instanceof AxoObjectInstanceViewPatcherObject)) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/object");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    model.getController().addMetaUndo("embed");
                    model.getParent().getController().convertToEmbeddedObj(getDModel());
                }
            });
            popup.add(popm_embed);
        }
        return popup;
    }

    public void openEditor() {
        IAbstractEditor editor = getType().getEditor();
        if (editor == null) {
            editor = ObjectEditorFactory.createObjectEditor(getType());
            getType().setEditor(editor);
        }
        editor.toFront();
    }

    @Override
    public void lock() {
        super.lock();
        for (IAttributeInstanceView a : attributeInstanceViews) {
            a.lock();
        }
    }

    @Override
    public void unlock() {
        super.unlock();
        for (IAttributeInstanceView a : attributeInstanceViews) {
            a.unlock();
        }
    }

    @Override
    public List<IInletInstanceView> getInletInstanceViews() {
        return Collections.unmodifiableList(inletInstanceViews);
    }

    @Override
    public List<IOutletInstanceView> getOutletInstanceViews() {
        return Collections.unmodifiableList(outletInstanceViews);
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return Collections.unmodifiableList(parameterInstanceViews);
    }

}
