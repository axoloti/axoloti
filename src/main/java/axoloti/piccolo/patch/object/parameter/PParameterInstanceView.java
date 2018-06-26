package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IParameterInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.components.PAssignMidiCCComponent;
import axoloti.piccolo.components.PAssignPresetMenuItems;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.control.PCtrlComponentAbstract;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.property.Property;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_BEGIN;
import axoloti.swingui.property.menu.ViewFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public abstract class PParameterInstanceView extends PatchPNode implements ActionListener, IParameterInstanceView {

    PLabelComponent valuelbl = new PLabelComponent("123456789", false);
    PCtrlComponentAbstract ctrl;
    PLabelComponent label = new PLabelComponent("");

    ParameterInstance parameterInstance;

    ParameterInstanceController getParameterInstanceController() {
        return parameterInstance.getController();
    }

    PAssignMidiCCComponent midiAssign;

    protected IAxoObjectInstanceView axoObjectInstanceView;

    Color backgroundColor;

    PParameterInstanceView(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.parameterInstance = parameterInstance;
        this.axoObjectInstanceView = axoObjectInstanceView;
        setPickable(false);
        initComponent();
    }

    private void initComponent() {
        removeAllChildren();
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));

        PatchPNode lbls = new PatchPNode(getPatchView());
        lbls.setLayout(new BoxLayout(lbls.getProxyComponent(), BoxLayout.Y_AXIS));

        label.setText(getDModel().getDModel().getName());
        lbls.addChild(label);

        if (getDModel().getConvs() != null) {
            lbls.addChild(valuelbl);
            Dimension d = new Dimension(50, 10);
            valuelbl.setMinimumSize(d);
            valuelbl.setPreferredSize(d);
            valuelbl.setSize(d);
            valuelbl.setMaximumSize(d);
            valuelbl.setPickable(true);
            valuelbl.addInputEventListener(new PBasicInputEventHandler() {
                @Override
                public void mouseClicked(PInputEvent e) {
                    getDModel().cycleConversions();
                }
            });
            updateUnit();
        }

        this.addChild(lbls);

        ctrl = createControl();

        addChild(getControlComponent());
        getControlComponent().addInputEventListener(popupMouseListener);

        getControlComponent().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PROP_VALUE_ADJ_BEGIN)) {
                    getParameterInstanceController().addMetaUndo("change parameter " + getDModel().getName());
                } else if (evt.getPropertyName().equals(PROP_VALUE)) {
                    boolean changed = handleAdjustment();
                }
            }
        });
        invalidate();
    }

    @Override
    public ParameterInstance getDModel() {
        return parameterInstance;
    }

    double valueBeforeAdjustment;

    public void doPopup(PInputEvent e) {
        JPopupMenu m = new JPopupMenu();
        populatePopup(m);
        Point popupLocation = PUtils.getPopupLocation(e);
        m.show(getCanvas(),
                popupLocation.x,
                popupLocation.y);
    }

//     public void populatePopup(JPopupMenu m) {
//         final JCheckBoxMenuItem m_onParent = new JCheckBoxMenuItem("parameter on parent");
// 	// TODO: piccolo ???
// //        boolean op = parameterInstance.getOnParent();
// 	boolean op = false;
//         m_onParent.setSelected(op);
//         m.add(m_onParent);
//         m_onParent.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent ae) {
//                 getController().setModelUndoableProperty(ParameterInstance.ON_PARENT, !op);
//             }
//         });

//         JMenu m_preset = new JMenu("Preset");
//         // AssignPresetMenuItems, does stuff in ctor
//         PAssignPresetMenuItems assignPresetMenuItems = new PAssignPresetMenuItems(this, m_preset);
//         m.add(m_preset);
//     }

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
        PAssignPresetMenuItems assignPresetMenuItems = new PAssignPresetMenuItems(this, m_preset);
        m.add(m_preset);
    }

    /**
     *
     * @return control component
     */
    abstract public PCtrlComponentAbstract getControlComponent();

    @Override
    abstract public boolean handleAdjustment();

    public abstract PCtrlComponentAbstract createControl();

    PBasicInputEventHandler popupMouseListener = new PBasicInputEventHandler() {
        @Override
        public void mousePressed(PInputEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
                e.setHandled(true);
            }
        }

        @Override
        public void mouseReleased(PInputEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
                e.setHandled(true);
            }
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void update() {
    }

    void updateUnit() {
    }

    public boolean isOnParent() {
        return false;
//        return parameterInstance.getOnParent();
    }

    public Component getCanvas() {
        return axoObjectInstanceView.getCanvas();
    }

    public PatchView getPatchView() {
        return axoObjectInstanceView.getPatchView();
    }

    public IAxoObjectInstanceView getObjectInstanceView() {
        return axoObjectInstanceView;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (ParameterInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            getProxyComponent().doLayout();
        } else if (ParameterInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        } else if (ParameterInstance.ON_PARENT.is(evt)) {
// TODO: piccolo
//            showOnParent((Boolean) evt.getNewValue());
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
