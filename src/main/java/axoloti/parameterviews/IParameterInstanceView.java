package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.datatypes.Value;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstance;
import javax.swing.JPopupMenu;
import axoloti.mvc.IView;

public interface IParameterInstanceView extends IView {

    public void PostConstructor();

    public void populatePopup(JPopupMenu m);

    public boolean handleAdjustment();

    public String getName();

//    public void updateV();

//    public void SetValueRaw(int v);
    public void ShowPreset(int i);

    public void IncludeInPreset();

    public void ExcludeFromPreset();

    public ParameterInstance getModel();

    public Preset AddPreset(int index, Value value);

    public void RemovePreset(int index);

}
