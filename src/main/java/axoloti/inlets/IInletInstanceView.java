package axoloti.inlets;

import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Point;

public interface IInletInstanceView {

    public void PostConstructor();

    public InletInstance getInletInstance();

    public void setHighlighted(boolean highlighted);

    public void disconnect();

    public void deleteNet();

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);
}
