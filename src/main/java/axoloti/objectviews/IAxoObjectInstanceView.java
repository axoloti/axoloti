package axoloti.objectviews;

import axoloti.PatchModel;
import axoloti.PatchView;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.displayviews.IDisplayInstanceView;
import axoloti.inlets.IInletInstanceView;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstance;
import axoloti.parameterviews.IParameterInstanceView;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;

public interface IAxoObjectInstanceView extends AbstractView {

    public AxoObjectInstanceAbstract getModel();

    public void Lock();

    public void Unlock();

    public boolean isLocked();

    public void PostConstructor();

    public PatchView getPatchView();

    public PatchModel getPatchModel();

    public IInletInstanceView getInletInstanceView(InletInstance inletInstance);

    public IOutletInstanceView getOutletInstanceView(OutletInstance ouletInstance);

    public ArrayView<IInletInstanceView> getInletInstanceViews();

    public ArrayView<IOutletInstanceView> getOutletInstanceViews();

    public ArrayView<IParameterInstanceView> getParameterInstanceViews();

    public void setLocation(int x, int y);

    public void addInstanceNameEditor();

    public void setInstanceName(String InstanceName);

    public void setSelected(boolean selected);

    public Boolean isSelected();

    public void moveToFront();

    public void resizeToGrid();

    public AxoObjectInstanceAbstract getObjectInstance();

    public Point getLocation();

    public void repaint();

    public Dimension getPreferredSize();

    public Dimension getSize();

    public void addParameterInstanceView(IParameterInstanceView view);

    public void addAttributeInstanceView(IAttributeInstanceView view);

    public void addDisplayInstanceView(IDisplayInstanceView view);

    public void addOutletInstanceView(IOutletInstanceView view);

    public void addInletInstanceView(IInletInstanceView view);

    public JComponent getCanvas();

    public boolean isZombie();

    public void validate();

}
