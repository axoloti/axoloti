package axoloti.parameterviews;

import axoloti.Modulation;
import axoloti.Preset;
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueFrac32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import static axoloti.parameters.ParameterInstanceController.ELEMENT_PARAM_VALUE;
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
    public void PostConstructor() {
        super.PostConstructor();
        // FIXME: does not belong in view
        if (getModel().getModulators() != null) {
            List<Modulation> modulators = getModel().getModulators();
            for (Modulation m : modulators) {
                System.out.println("mod amount " + m.getValue().getDouble());
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
                getController().applyDefaultValue();
            }
        });
        m.add(m_default);
    }

    @Override
    public boolean handleAdjustment() {
        // FIXME: cleanup preset logic
        Preset p = getModel().GetPreset(presetEditActive);
        if (p != null) {
            p.value = new ValueFrac32(getControlComponent().getValue());
        }
        if (getModel().getValue().getDouble() != getControlComponent().getValue()) {
            if (getController() != null) {
                ValueFrac32 vf32 = new ValueFrac32(getControlComponent().getValue());
                getController().setModelUndoableProperty(ELEMENT_PARAM_VALUE, vf32);
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
        if (evt.getPropertyName().equals(ParameterInstanceController.ELEMENT_PARAM_VALUE)) {
            Value v = (Value)evt.getNewValue();
            ctrl.setValue(v.getDouble());
        }
    }    
}
