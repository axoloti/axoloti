package axoloti.object;

import axoloti.inlets.Inlet;
import axoloti.mvc.array.ArrayModel;
import axoloti.outlets.Outlet;

/**
 *
 * @author jtaelman
 */
public interface IAxoObject {
    
    public ArrayModel<Inlet> getInlets();
    public ArrayModel<Outlet> getOutlets();

    public String getId();

    public void setId(String id);

    public String getDescription();

    public void setDescription(String sDescription);

    public String getLicense();

    public void setLicense(String sLicense);

    public String getPath();

    public void setPath(String sPath);
}
