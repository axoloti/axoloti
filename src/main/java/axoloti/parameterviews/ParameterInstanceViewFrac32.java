package axoloti.parameterviews;

import axoloti.Modulation;
import axoloti.PresetDouble;
import axoloti.datatypes.ValueFrac32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstanceFrac32;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class ParameterInstanceViewFrac32 extends ParameterInstanceView {

    ParameterInstanceViewFrac32(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32 getModel() {
        return (ParameterInstanceFrac32) super.getModel();
    }

    @Override
    void UpdateUnit() {
        super.UpdateUnit();
        if (getModel().getConversion() != null) {
            valuelbl.setText(getModel().getConversion().ToReal(new ValueFrac32(
                    getModel().getValue())));
        }
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        // FIXME: does not belong in view
        if (getModel().getModulators() != null) {
            List<Modulation> modulators = getModel().getModulators();
            for (Modulation m : modulators) {
                System.out.println("mod amount " + m.getValue());
                m.PostConstructor(getModel());
            }
        }
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenuItem m_default = new JMenuItem("Reset to default value");
        m_default.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getController().addMetaUndo("Reset to default");
                getController().applyDefaultValue();
            }
        });
        m.add(m_default);
    }

    @Override
    public boolean handleAdjustment() {
        // FIXME: cleanup preset logic
        PresetDouble p = getModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue(getControlComponent().getValue());
        }
        if (getModel().getValue() != getControlComponent().getValue()) {
            if (getController() != null) {
                Double d = getControlComponent().getValue();
                getController().setModelUndoableProperty(ParameterInstance.VALUE, d);
            }
        } else {
            return false;
        }
        return true;
    }

    public void updateModulation(int index, double amount) {
        getModel().updateModulation(index, amount);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstance.VALUE.is(evt)) {
            Double v = (Double) evt.getNewValue();
            ctrl.setValue(v);
            UpdateUnit();
        } else if (ParameterInstance.CONVERSION.is(evt)) {
            UpdateUnit();
        }
    }
}
