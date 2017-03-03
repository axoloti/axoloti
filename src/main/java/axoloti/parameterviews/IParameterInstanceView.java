package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.datatypes.Value;
import axoloti.parameters.ParameterInstance;
import javax.swing.JPopupMenu;

public interface IParameterInstanceView {

    public void PostConstructor();

    public void populatePopup(JPopupMenu m);

    public boolean handleAdjustment();

    public String getName();

    public void updateV();

    public void SetMidiCC(Integer cc);

    public void SetValueRaw(int v);

    public void ShowPreset(int i);

    public void IncludeInPreset();

    public void ExcludeFromPreset();

    public void setValue(Value value);

    public ParameterInstance getParameterInstance();

    public Preset AddPreset(int index, Value value);

    public void RemovePreset(int index);
}
