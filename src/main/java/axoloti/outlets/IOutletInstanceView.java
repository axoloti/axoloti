package axoloti.outlets;

import axoloti.mvc.AbstractView;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Point;

public interface IOutletInstanceView extends AbstractView {

    public void PostConstructor();

    public OutletInstance getOutletInstance();

    public void setHighlighted(boolean highlighted);

    public void disconnect();

    public void deleteNet();

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);


}
