package axoloti;

import axoloti.inlets.IInletInstanceView;
import axoloti.outlets.IOutletInstanceView;
import java.util.ArrayList;

public interface INetView {

    public void connectInlet(IInletInstanceView inlet);

    public void connectOutlet(IOutletInstanceView outlet);

    public void updateBounds();

    public Net getNet();

    public void PostConstructor();

    public void setSelected(boolean selected);

    public boolean getSelected();

    public ArrayList<IOutletInstanceView> getSourceViews();

    public ArrayList<IInletInstanceView> getDestinationViews();

    public void setVisible(boolean isVisible);

    public void repaint();

    public void validate();
}
