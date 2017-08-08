package axoloti;

import axoloti.inlets.IInletInstanceView;
import axoloti.outlets.IOutletInstanceView;
import java.util.ArrayList;
import axoloti.mvc.IView;

public interface INetView extends IView {

    public void updateBounds();

    @Deprecated
    public Net getNet();

    public void PostConstructor();

    public void setSelected(boolean selected);

    public boolean getSelected();

    public ArrayList<IOutletInstanceView> getSourceViews();

    public ArrayList<IInletInstanceView> getDestinationViews();

    public void setVisible(boolean isVisible);

    public void repaint();
}
