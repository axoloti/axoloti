package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.Theme;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attributeviews.AttributeInstanceView;
import axoloti.attributeviews.AttributeInstanceViewFactory;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.displays.DisplayInstanceController;
import axoloti.displayviews.DisplayInstanceViewFactory;
import axoloti.displayviews.IDisplayInstanceView;
import axoloti.inlets.IInletInstanceView;
import axoloti.inlets.InletInstanceController;
import axoloti.inlets.InletInstanceView;
import axoloti.inlets.InletInstanceViewFactory;
import axoloti.mvc.AbstractController;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObjectFromPatch;
import axoloti.object.AxoObjectInstance;
import axoloti.object.IAxoObject;
import axoloti.object.ObjectInstanceController;
import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstanceController;
import axoloti.outlets.OutletInstanceView;
import axoloti.outlets.OutletInstanceViewFactory;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameterviews.IParameterInstanceView;
import axoloti.parameterviews.ParameterInstanceViewFactory;
import axoloti.utils.Preferences;
import components.LabelComponent;
import components.PopupIcon;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class AxoObjectInstanceView extends AxoObjectInstanceViewAbstract implements IAxoObjectInstanceView {

    LabelComponent IndexLabel;
    public final JPanel p_attributeViews = new JPanel();
    public final JPanel p_parameterViews = new JPanel();
    public final JPanel p_displayViews = new JPanel();
    public final JPanel p_ioletViews = new JPanel();
    public final JPanel p_inletViews = new JPanel();
    public final JPanel p_outletViews = new JPanel();

    public AxoObjectInstanceView(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
        init1();
    }

    @Override
    public AxoObjectInstance getModel() {
        return (AxoObjectInstance) super.getModel();
    }

    public IAxoObject getType() {
        return getModel().getType();
    }

    final void init1() {
        p_ioletViews.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_ioletViews.setLayout(new BoxLayout(p_ioletViews, BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);

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
    }

    List<IInletInstanceView> inletInstanceViews;
    List<IOutletInstanceView> outletInstanceViews;
    List<IAttributeInstanceView> attributeInstanceViews;
    List<IParameterInstanceView> parameterInstanceViews;
    List<IDisplayInstanceView> displayInstanceViews;

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));/* {
            @Override
            public Dimension preferredLayoutSize(Container target) {
                Dimension d = super.preferredLayoutSize(target);
                d.width = ((d.width + Constants.X_GRID - 1) / Constants.X_GRID) * Constants.X_GRID;
                d.height = ((d.height + Constants.Y_GRID - 1) / Constants.Y_GRID) * Constants.Y_GRID;
                return d;
            }
            
        });*/

        final PopupIcon popupIcon = new PopupIcon();
        popupIcon.setPopupIconListener(new PopupIcon.PopupIconListener() {
            @Override
            public void ShowPopup() {
                JPopupMenu popup = CreatePopupMenu();
                popupIcon.add(popup);
                popup.show(popupIcon,
                        0, popupIcon.getHeight());
            }
        });
        Titlebar.add(popupIcon);

        LabelComponent idlbl = new LabelComponent(getModel().getTypeName());
        idlbl.setAlignmentX(LEFT_ALIGNMENT);
        idlbl.setForeground(Theme.getCurrentTheme().Object_TitleBar_Foreground);
        Titlebar.add(idlbl);

        String tooltiptxt = "<html>";
        if ((getType().getDescription() != null) && (!getType().getDescription().isEmpty())) {
            tooltiptxt += getType().getDescription();
        }
        if ((getType().getAuthor() != null) && (!getType().getAuthor().isEmpty())) {
            tooltiptxt += "<p>Author: " + getType().getAuthor();
        }
        if ((getType().getLicense() != null) && (!getType().getLicense().isEmpty())) {
            tooltiptxt += "<p>License: " + getType().getLicense();
        }
        if ((getType().getPath() != null) && (!getType().getPath().isEmpty())) {
            tooltiptxt += "<p>Path: " + getType().getPath();
        }
        Titlebar.setToolTipText(tooltiptxt);

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
        Titlebar.setAlignmentX(LEFT_ALIGNMENT);
        add(Titlebar);
        InstanceLabel = new LabelComponent(getModel().getInstanceName());
        InstanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        InstanceLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        add(InstanceLabel);

        p_ioletViews.setLayout(new BoxLayout(p_ioletViews, BoxLayout.LINE_AXIS));
        p_ioletViews.setAlignmentX(LEFT_ALIGNMENT);

        p_inletViews.setLayout(new BoxLayout(p_inletViews, BoxLayout.PAGE_AXIS));
        p_inletViews.setAlignmentY(TOP_ALIGNMENT);
//        p_inletViews.setBorder(new LineBorder(Color.yellow));

        p_outletViews.setLayout(new BoxLayout(p_outletViews, BoxLayout.PAGE_AXIS));
        p_outletViews.setAlignmentY(TOP_ALIGNMENT);
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
        p_parameterViews.setAlignmentX(LEFT_ALIGNMENT);
        p_displayViews.setAlignmentX(LEFT_ALIGNMENT);

        resizeToGrid();
        setVisible(true);
        revalidate();
    }
    
    ArrayView<IInletInstanceView> inletInstanceViewSync = new ArrayView<IInletInstanceView>() {
        @Override
        public InletInstanceView viewFactory(AbstractController ctrl) {
            return InletInstanceViewFactory.createView((InletInstanceController) ctrl, AxoObjectInstanceView.this);
        }

        @Override
        public void updateUI(List<IInletInstanceView> views) {
            p_inletViews.removeAll();
            for (IInletInstanceView c : views) {
                p_inletViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        public void removeView(IInletInstanceView view) {
        }
    };

    ArrayView<IOutletInstanceView> outletInstanceViewSync = new ArrayView<IOutletInstanceView>() {
        @Override
        public OutletInstanceView viewFactory(AbstractController ctrl) {
            return OutletInstanceViewFactory.createView((OutletInstanceController) ctrl, AxoObjectInstanceView.this);
        }

        @Override
        public void updateUI(List<IOutletInstanceView> views) {
            p_outletViews.removeAll();
            for (IOutletInstanceView c : views) {
                p_outletViews.add((Component) c);
            }
            resizeToGrid();
        }

        @Override
        public void removeView(IOutletInstanceView view) {
        }
    };

    ArrayView<IAttributeInstanceView> attributeInstanceViewSync = new ArrayView<IAttributeInstanceView>() {
            @Override
            public AttributeInstanceView viewFactory(AbstractController ctrl) {
                return AttributeInstanceViewFactory.createView((AttributeInstanceController) ctrl, AxoObjectInstanceView.this);
            }

            @Override
            public void updateUI(List<IAttributeInstanceView> views) {
                p_attributeViews.removeAll();
                for (IAttributeInstanceView c : views) {
                    p_attributeViews.add((Component) c);
                }
                resizeToGrid();
            }

            @Override
            public void removeView(IAttributeInstanceView view) {
            }
        };

    ArrayView<IParameterInstanceView> parameterInstanceViewSync = new ArrayView<IParameterInstanceView>() {
            @Override
            public IParameterInstanceView viewFactory(AbstractController ctrl) {
                return ParameterInstanceViewFactory.createView((ParameterInstanceController) ctrl, AxoObjectInstanceView.this);
            }

            @Override
            public void updateUI(List<IParameterInstanceView> views) {
                p_parameterViews.removeAll();
                for (IParameterInstanceView c : views) {
                    p_parameterViews.add((Component) c);
                }
                resizeToGrid();
            }

            @Override
            public void removeView(IParameterInstanceView view) {
            }
        };

    ArrayView<IDisplayInstanceView> displayInstanceViewSync = new ArrayView<IDisplayInstanceView>() {
            @Override
            public IDisplayInstanceView viewFactory(AbstractController ctrl) {
                return DisplayInstanceViewFactory.createView((DisplayInstanceController) ctrl, AxoObjectInstanceView.this);
            }

            @Override
            public void updateUI(List<IDisplayInstanceView> views) {
                p_displayViews.removeAll();
                for (IDisplayInstanceView c : views) {
                    p_displayViews.add((Component) c);
                }
                resizeToGrid();
            }

            @Override
            public void removeView(IDisplayInstanceView view) {
            }
        };
    
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoObjectInstance.OBJ_INLET_INSTANCES.is(evt)) {
            inletInstanceViews = inletInstanceViewSync.Sync(inletInstanceViews, getController().inletInstanceControllers);
        } else if (AxoObjectInstance.OBJ_OUTLET_INSTANCES.is(evt)) {
            outletInstanceViews = outletInstanceViewSync.Sync(outletInstanceViews, getController().outletInstanceControllers);
        } else if (AxoObjectInstance.OBJ_ATTRIBUTE_INSTANCES.is(evt)) {
            attributeInstanceViews = attributeInstanceViewSync.Sync(attributeInstanceViews, getController().attributeInstanceControllers);
        } else if (AxoObjectInstance.OBJ_PARAMETER_INSTANCES.is(evt)) {
            parameterInstanceViews = parameterInstanceViewSync.Sync(parameterInstanceViews, getController().parameterInstanceControllers);
        } else if (AxoObjectInstance.OBJ_DISPLAY_INSTANCES.is(evt)) {
            displayInstanceViews = displayInstanceViewSync.Sync(displayInstanceViews, getController().displayInstanceControllers);
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
                getPatchView().ShowClassSelector(AxoObjectInstanceView.this.getLocation(), AxoObjectInstanceView.this, null);
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

        if (getModel().getType() instanceof AxoObjectFromPatch) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/patcher");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (!getPatchView().isLocked()) {
                        getController().addMetaUndo("embed");
                        getController().getParent().ConvertToPatchPatcher(getModel());
                    }
                }
            });
            popup.add(popm_embed);
        } else if (!(this instanceof AxoObjectInstanceViewPatcherObject)) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/object");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getController().addMetaUndo("embed");
                    getController().getParent().ConvertToEmbeddedObj(getModel());
                }
            });
            popup.add(popm_embed);
        }
        return popup;
    }

    public void refreshIndex() {
        if (getPatchView() != null && IndexLabel != null) {
            IndexLabel.setText(" " + getPatchView().getObjectInstanceViews().indexOf(this));
        }
    }

    public void OpenEditor() {
        getType().OpenEditor(getModel().editorBounds, getModel().editorActiveTabIndex);
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
    }

    @Override
    public List<IInletInstanceView> getInletInstanceViews() {
        return inletInstanceViews;
    }

    @Override
    public List<IOutletInstanceView> getOutletInstanceViews() {
        return outletInstanceViews;
    }

    @Override
    public List<IParameterInstanceView> getParameterInstanceViews() {
        return parameterInstanceViews;
    }

}
