package axoloti.object;

import axoloti.PatchModel;
import axoloti.SDFileReference;
import axoloti.attribute.AttributeInstance;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletInstance;
import axoloti.mvc.IModel;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public interface IAxoObjectInstance extends Comparable<IAxoObjectInstance>, IModel {

    public IAxoObject getType();

    public String getInstanceName();

    public String getCInstanceName();

    public String getLegalName();

    public List<InletInstance> getInletInstances();

    public List<OutletInstance> getOutletInstances();

    public List<ParameterInstance> getParameterInstances();

    public List<AttributeInstance> getAttributeInstances();

    public List<DisplayInstance> getDisplayInstances();

    public InletInstance GetInletInstance(String n);

    public OutletInstance GetOutletInstance(String n);

    public int getX();

    public int getY();

    public Boolean getSelected();

    public void setSelected(Boolean selected);

    public PatchModel getPatchModel();

    public Point getLocation();

    public void setLocation(Point p);

    public ArrayList<SDFileReference> getFileDepends();

    public boolean isTypeWasAmbiguous();

    public boolean setInstanceName(String InstanceName);

    public IAxoObject resolveType(String directory);

    public void Remove();
}
