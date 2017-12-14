package axoloti.parameterviews;

import axoloti.parameters.ParameterInstance;
import javax.swing.JPopupMenu;
import axoloti.mvc.IView;

public interface IParameterInstanceView extends IView {

    public void PostConstructor();

    public void populatePopup(JPopupMenu m);

    public boolean handleAdjustment();

    public void ShowPreset(int i);

    public void IncludeInPreset();

    public void ExcludeFromPreset();

    public ParameterInstance getModel();

}
