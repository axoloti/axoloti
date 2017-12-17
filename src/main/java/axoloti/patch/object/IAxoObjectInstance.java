package axoloti.patch.object;

import axoloti.patch.PatchModel;
import axoloti.target.fs.SDFileReference;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.mvc.IModel;
import axoloti.object.IAxoObject;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
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
