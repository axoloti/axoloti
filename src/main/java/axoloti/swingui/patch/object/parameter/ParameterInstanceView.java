package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.mvc.FocusEdit;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.preferences.Theme;
import axoloti.property.Property;
import axoloti.swingui.components.AssignMidiCCComponent;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.control.ACtrlComponent;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.patch.object.parameter.preset.AssignPresetMenuItems;
import axoloti.swingui.property.menu.ViewFactory;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

public abstract class ParameterInstanceView extends ViewPanel<ParameterInstance> implements ActionListener, IParameterInstanceView {

    LabelComponent valuelbl = new LabelComponent("123456789");
    LabelComponent label = new LabelComponent("");

    AssignMidiCCComponent midiAssign;

    private final IAxoObjectInstanceView axoObjectInstanceView;

    ParameterInstanceView(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance);
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    protected void scrollTo() {
        if (axoObjectInstanceView == null) {
            return;
        }
        PatchView pv = axoObjectInstanceView.getPatchView();
        if (pv == null) {
            return;
        }
        pv.scrollTo(this);
    }

    final void initCtrlComponent(ACtrlComponent ctrl) {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel lbls = new JPanel();
        lbls.setOpaque(false);
        lbls.setLayout(new BoxLayout(lbls, BoxLayout.Y_AXIS));
        this.add(lbls);
        label.setText(getDModel().getDModel().getName());
        lbls.add(label);

        if (!getDModel().getConvs().isEmpty()) {
            lbls.add(valuelbl);
            Dimension d = new Dimension(50, 10);
            valuelbl.setMinimumSize(d);
            valuelbl.setPreferredSize(d);
            valuelbl.setSize(d);
            valuelbl.setMaximumSize(d);
            valuelbl.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getDModel().cycleConversions();
                }
            });
        }
        update();

        //ACtrlComponent ctrl = getControlComponent();
        add(ctrl);
        ctrl.addMouseListener(popupMouseListener);

        ctrl.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                    model.getController().addMetaUndo("change parameter " + getDModel().getName(), getFocusEdit());
                } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                    boolean changed = handleAdjustment();
                }
            }
        });
    }

    public FocusEdit getFocusEdit() {
        return new FocusEdit() {
            @Override
            protected void focus() {
                scrollTo();
                getControlComponent().requestFocusInWindow();
            }
        };
    }

    void showOnParent(Boolean onParent) {
        if (onParent == null || onParent == false) {
            setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
        } else {
            setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        }
    }

    public void doPopup(MouseEvent e) {
        JPopupMenu m = new JPopupMenu();
        populatePopup(m);
        m.show(this, 0, getHeight());
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        List<Property> ps = getDModel().getEditableFields();
        for (Property p : ps) {
            Component mi = ViewFactory.createMenuItemView(getDModel(), p);
            if (mi != null) {
                m.add(mi);
            }
        }
        JMenu m_preset = new JMenu("Preset");
        // AssignPresetMenuItems, does stuff in ctor
        AssignPresetMenuItems assignPresetMenuItems = new AssignPresetMenuItems(this, m_preset);
        m.add(m_preset);
    }

    /**
     *
     * @return control component
     */
    abstract public ACtrlComponent getControlComponent();

    @Override
    abstract public boolean handleAdjustment();

    public abstract ACtrlComponent createControl();

    MouseListener popupMouseListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
                e.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
                e.consume();
            }
        }

    };

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    void updateUnit(Double value) {
//        if (getModel().getConvs() != null) {
//            valuelbl.setText(getModel().getConversion().ToReal(
//                    getModel().getValue().));
//        }
    }

    public int getPresetEditActive() {
        IAxoObjectInstanceView objView = getAxoObjectInstanceView();
        if (objView == null) {
            return 0;
        }
        PatchView pv = objView.getPatchView();
        if (pv == null) {
            return 0;
        }
        return pv.getPresetEditActive();
    }

    public IAxoObjectInstanceView getAxoObjectInstanceView() {
        return axoObjectInstanceView;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (ParameterInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (ParameterInstance.DESCRIPTION.is(evt)) {
            String s = (String) evt.getNewValue();
            if ((s != null) && (s.isEmpty())) {
                s = null;
            }
            setToolTipText(s);
        } else if (ParameterInstance.ON_PARENT.is(evt)) {
            showOnParent((Boolean) evt.getNewValue());
        } else if (ParameterInstance.MIDI_CC.is(evt)) {
            Integer v = (Integer) evt.getNewValue();
            if (midiAssign != null) {
                if (v != null) {
                    midiAssign.setCC(v);
                } else {
                    midiAssign.setCC(-1);
                }
            }
        } else if (ParameterInstance.NOLABEL.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b == null) {
                b = false;
            }
            label.setVisible(!b);
        }
    }

    @Override
    public void dispose() {
    }

}
