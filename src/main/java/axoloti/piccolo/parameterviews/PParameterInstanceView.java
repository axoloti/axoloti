package axoloti.piccolo.parameterviews;

import axoloti.PatchView;
import axoloti.Preset;
import axoloti.datatypes.Value;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameterviews.IParameterInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.PatchPNode;
import components.piccolo.PAssignMidiCCComponent;
import components.piccolo.PAssignPresetMenuItems;
import components.piccolo.PLabelComponent;
import components.piccolo.control.PCtrlComponentAbstract;
import components.piccolo.control.PCtrlEvent;
import components.piccolo.control.PCtrlListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public abstract class PParameterInstanceView extends PatchPNode implements ActionListener, IParameterInstanceView {

    ParameterInstance parameterInstance;
    PLabelComponent valuelbl = new PLabelComponent("123456789");
    PCtrlComponentAbstract ctrl;

    ParameterInstanceController controller;
    
    PAssignMidiCCComponent midiAssign;

    protected IAxoObjectInstanceView axoObjectInstanceView;

    Color backgroundColor;

    PParameterInstanceView(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.parameterInstance = parameterInstance;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    public void PostConstructor() {
        setPickable(false);
        removeAllChildren();
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));

        PatchPNode lbls = null;
        if ((((parameterInstance.getModel().noLabel == null)
                || (parameterInstance.getModel().noLabel == false)))
                && (parameterInstance.getConvs() != null)) {
            lbls = new PatchPNode(getPatchView());
            lbls.setLayout(new BoxLayout(lbls.getProxyComponent(), BoxLayout.Y_AXIS));
            this.addChild(lbls);
        }

        if ((parameterInstance.getModel().noLabel == null) || (parameterInstance.getModel().noLabel == false)) {
            if (lbls != null) {
                lbls.addChild(new PLabelComponent(parameterInstance.getModel().getName()));
            } else {
                addChild(new PLabelComponent(parameterInstance.getModel().getName()));
            }
        }

        if (parameterInstance.getConvs() != null) {
            if (lbls != null) {
                lbls.addChild(valuelbl);
            } else {
                addChild(valuelbl);
            }
            valuelbl.setPickable(true);
            Dimension d = new Dimension(50, 10);
            valuelbl.setMinimumSize(d);
            valuelbl.setMaximumSize(d);
            valuelbl.setPreferredSize(d);
            valuelbl.setSize(d);
            valuelbl.addInputEventListener(new PBasicInputEventHandler() {
                @Override
                public void mouseClicked(PInputEvent e) {
//                    parameterInstance.setSelectedConv(parameterInstance.getSelectedConv() + 1);
//                    if (parameterInstance.getSelectedConv() >= parameterInstance.getConvs().length) {
//                        parameterInstance.setSelectedConv(0);
//                    }
                    UpdateUnit();

                }
            });
            UpdateUnit();
        }

        ctrl = CreateControl();
        if (parameterInstance.getModel().description != null) {
            ctrl.setToolTipText(parameterInstance.getModel().description);
        } else {
            ctrl.setToolTipText(parameterInstance.getModel().getName());
        }
        addChild(getControlComponent());
        getControlComponent().addInputEventListener(popupMouseListener);
        getControlComponent().addPCtrlListener(new PCtrlListener() {
            @Override
            public void PCtrlAdjusted(PCtrlEvent e) {
                boolean changed = handleAdjustment();
            }

            @Override
            public void PCtrlAdjustmentBegin(PCtrlEvent e) {
                valueBeforeAdjustment = getControlComponent().getValue();
            }

            @Override
            public void PCtrlAdjustmentFinished(PCtrlEvent e) {
            }
        });
        updateV();
        parameterInstance.setMidiCC(parameterInstance.getMidiCC());
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

    public void populatePopup(JPopupMenu m) {
        final JCheckBoxMenuItem m_onParent = new JCheckBoxMenuItem("parameter on parent");
        boolean op = parameterInstance.getOnParent();
        m_onParent.setSelected(op);
        m.add(m_onParent);
        m_onParent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                getController().setModelUndoableProperty(ParameterInstance.ON_PARENT, !op);
            }
        });

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

    abstract public boolean handleAdjustment();

    public abstract PCtrlComponentAbstract CreateControl();

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
        String s = e.getActionCommand();
        if (s.startsWith("CC")) {
            int i = Integer.parseInt(s.substring(2));
            if (i != parameterInstance.getMidiCC()) {
                SetMidiCC(i);
            }
        } else if (s.equals("none")) {
            if (-1 != parameterInstance.getMidiCC()) {
                SetMidiCC(-1);
            }
        }
    }

    @Override
    public String getName() {
        if (parameterInstance != null) {
            return parameterInstance.getName();
        } else {
            return super.getName();
        }
    }

    void UpdateUnit() {
        if (parameterInstance.getConvs() != null) {
            valuelbl.setText("");//parameterInstance.getConvs()[parameterInstance.getSelectedConv()].ToReal(parameterInstance.getValue()));
        }
    }

    public void updateV() {
        UpdateUnit();
    }

    public void SetMidiCC(Integer cc) {
        parameterInstance.setMidiCC(cc);
        if ((cc != null) && (cc >= 0)) {
            if (midiAssign != null) {
                midiAssign.setCC(cc);
            }
        } else if (midiAssign != null) {
            midiAssign.setCC(-1);
        }
    }

//    public void SetValueRaw(int v) {
//        parameterInstance.SetValueRaw(v);
//        updateV();
//    }

    public abstract void ShowPreset(int i);

    public boolean isOnParent() {
        return parameterInstance.getOnParent();
    }

    public int presetEditActive = 0;

    public void IncludeInPreset() {
        if (presetEditActive > 0) {
            Preset p = parameterInstance.getPreset(presetEditActive);
            if (p != null) {
                return;
            }
            if (parameterInstance.getPresets() == null) {
                parameterInstance.setPresets(new ArrayList<Preset>());
            }
            p = getModel().presetFactory(presetEditActive, parameterInstance.getValue());
            parameterInstance.getPresets().add(p);
        }
        ShowPreset(presetEditActive);
    }

    public void ExcludeFromPreset() {
        if (presetEditActive > 0) {
            Preset p = parameterInstance.getPreset(presetEditActive);
            if (p != null) {
                parameterInstance.getPresets().remove(p);
                if (parameterInstance.getPresets().isEmpty()) {
                    parameterInstance.setPresets(null);
                }
            }
        }
        ShowPreset(presetEditActive);
    }

    public void CopyValueFrom(PParameterInstanceView p) {
        parameterInstance.CopyValueFrom(p.parameterInstance);
    }

    public void setValue(Value value) {
        parameterInstance.setValue(value);
        updateV();
    }

    public ParameterInstance getModel() {
        return parameterInstance;
    }

    public Preset AddPreset(int index, Object value) {
        return getController().AddPreset(index, value);
    }

    public void RemovePreset(int index) {
        getController().RemovePreset(index);
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
        updateV();
    }

    @Override
    public ParameterInstanceController getController() {
        return controller;
    }
    @Override
    public void dispose() {
    }

}
