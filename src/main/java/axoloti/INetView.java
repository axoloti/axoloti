package axoloti;

import axoloti.inlets.IInletInstanceView;
import axoloti.mvc.AbstractView;
import axoloti.outlets.IOutletInstanceView;
import java.util.ArrayList;

public interface INetView extends AbstractView {

    @Deprecated
    public void updateBounds();

    @Deprecated
    public Net getNet();

    public void PostConstructor();

    public void setSelected(boolean selected);

    public boolean getSelected();

    public ArrayList<IOutletInstanceView> getSourceViews();

    public ArrayList<IInletInstanceView> getDestinationViews();

    public void setVisible(boolean isVisible);

    @Deprecated
    public void repaint();

    public void validate();
}
