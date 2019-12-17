package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.datatypes.ValueFrac32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32;
import axoloti.patch.object.parameter.preset.PresetDouble;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class PParameterInstanceViewFrac32 extends PParameterInstanceView {

    PParameterInstanceViewFrac32(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32 getDModel() {
        return (ParameterInstanceFrac32) super.getDModel();
    }

    @Override
    void updateUnit() {
        super.updateUnit();
        if (getDModel().getConversion() != null) {
            valuelbl.setText(getDModel().getConversion().convertToReal(new ValueFrac32(
                    getDModel().getValue())));
            invalidate();
        }
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenuItem m_default = new JMenuItem("Reset to default value");
        m_default.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getParameterInstanceController().addMetaUndo("Reset to default");
                getParameterInstanceController().applyDefaultValue();
            }
        });
        m.add(m_default);
    }

    @Override
    public boolean handleAdjustment() {
        // TODO: piccolo fix preset logic
        PresetDouble p = null; // getDModel().getPreset(presetEditActive);
        if (p != null) { // TODO: piccolo fix preset editing logic
            //p.setValue(getControlComponent().getValue());
        }
        if (getDModel().getValue() != getControlComponent().getValue()) {
            if (getParameterInstanceController() != null) {
                Double d = getControlComponent().getValue();
                getParameterInstanceController().changeValue(d);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstance.VALUE.is(evt)) {
            Double v = (Double) evt.getNewValue();
            ctrl.setValue(v);
            updateUnit();
        } else if (ParameterInstance.CONVERSION.is(evt)) {
            updateUnit();
        }
    }
}
