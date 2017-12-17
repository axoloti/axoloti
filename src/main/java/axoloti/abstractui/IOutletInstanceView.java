package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.outlet.OutletInstanceController;
import java.awt.Point;

public interface IOutletInstanceView extends IView<OutletInstanceController> {

    public void PostConstructor();

    public OutletInstance getModel();

    public void setHighlighted(boolean highlighted);

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);

}
