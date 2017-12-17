package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.net.NetController;
import java.util.ArrayList;

public interface INetView extends IView<NetController> {

    public void updateBounds();

    public void PostConstructor();

    public void setSelected(boolean selected);

    public boolean getSelected();

    public ArrayList<IOutletInstanceView> getSourceViews();

    public ArrayList<IInletInstanceView> getDestinationViews();

    public void setVisible(boolean isVisible);

    public void repaint();
}
