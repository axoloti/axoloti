package axoloti.swingui.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.datatypes.ValueFrac32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.patch.object.parameter.preset.PresetDouble;
import axoloti.preferences.Theme;
import axoloti.realunits.NativeToReal;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class ParameterInstanceViewFrac32 extends ParameterInstanceView {

    ParameterInstanceViewFrac32(ParameterInstance parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32 getDModel() {
        return (ParameterInstanceFrac32) super.getDModel();
    }

    @Override
    void updateUnit(Double value) {
        super.updateUnit(value);
        NativeToReal conv = getDModel().getConversion();
        if (conv != null) {
            String n = conv.convertToReal(new ValueFrac32(
                    value));
            valuelbl.setText(n);
        }
    }

    @Override
    public void update() {
        int i = getPresetEditActive();
        double valueShown;
        Color bg;
        if (i > 0) {
            Preset p = getDModel().getPreset(i);
            if (p != null) {
                bg = Theme.getCurrentTheme().Parameter_Preset_Highlight;
                valueShown = (Double) p.getValue();
            } else {
                bg = Theme.getCurrentTheme().Parameter_Default_Background;
                valueShown = getDModel().getValue();
            }
        } else {
            bg = Theme.getCurrentTheme().Parameter_Default_Background;
            valueShown = getDModel().getValue();
        }
        getControlComponent().setValue(valueShown);
        setBackground(bg);
        updateUnit(valueShown);
        /*
         if ((presets != null) && (!presets.isEmpty())) {
         lblPreset.setVisible(true);
         } else {
         lblPreset.setVisible(false);
         }
         */
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenuItem m_default = new JMenuItem("Reset to default value");
        m_default.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getDModel().getController().addMetaUndo("Reset to default", getFocusEdit());
                getDModel().getController().applyDefaultValue();
            }
        });
        m.add(m_default);
    }

    @Override
    public boolean handleAdjustment() {
        int presetEdit = getPresetEditActive();
        double value = getControlComponent().getValue();
        PresetDouble p = getDModel().getPreset(presetEdit);
        if (p != null) {
            getDModel().getController().addPreset(presetEdit, value);
        } else if (getDModel().getValue() != value) {
            if (getDModel().getController() != null) {
                Double d = getControlComponent().getValue();
                getDModel().getController().changeValue(d);
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
            update();
        } else if (ParameterInstance.CONVERSION.is(evt)) {
            update();
        } else if (ParameterInstance.PRESETS.is(evt)) {
            update();
        }
    }
}
