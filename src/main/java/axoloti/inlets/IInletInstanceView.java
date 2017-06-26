package axoloti.inlets;

import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Point;
import axoloti.mvc.IView;

public interface IInletInstanceView extends IView {

    public void PostConstructor();

    public InletInstance getModel();

    @Override
    public InletInstanceController getController();

    public void setHighlighted(boolean highlighted);

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);
}
