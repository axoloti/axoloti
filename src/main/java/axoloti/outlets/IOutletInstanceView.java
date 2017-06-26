package axoloti.outlets;

import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Point;
import axoloti.mvc.IView;

public interface IOutletInstanceView extends IView {

    public void PostConstructor();

    public OutletInstance getModel();

    @Override
    public OutletInstanceController getController();

    public void setHighlighted(boolean highlighted);

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);

}
