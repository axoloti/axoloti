package axoloti.object;

import axoloti.object.attribute.AxoAttribute;
import axoloti.object.display.Display;
import axoloti.object.inlet.Inlet;
import axoloti.object.outlet.Outlet;
import axoloti.object.parameter.Parameter;
import axoloti.property.Property;
import axoloti.target.fs.SDFileReference;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * "decorative" AxoObjectAbstract without any code
 *
 * @author jtaelman
 */
public abstract class AxoObjectAbstract0 extends AxoObjectAbstract {

    public AxoObjectAbstract0() {
    }

    public AxoObjectAbstract0(String id, String sDescription) {
        super(id, sDescription);
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
//        l.add()
        return l;
    }

    @Override
    public List<Inlet> getInlets() {
        return Collections.emptyList();
    }

    @Override
    public List<Outlet> getOutlets() {
        return Collections.emptyList();
    }

    @Override
    public List<AxoAttribute> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    public List<Parameter> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public List<Display> getDisplays() {
        return Collections.emptyList();
    }

    @Override
    public String getInitCode() {
        return "";
    }

    @Override
    public String getDisposeCode() {
        return "";
    }

    @Override
    public String getLocalData() {
        return "";
    }

    @Override
    public String getKRateCode() {
        return "";
    }

    @Override
    public String getSRateCode() {
        return "";
    }

    @Override
    public String getMidiCode() {
        return "";
    }

    @Override
    public Boolean getRotatedParams() {
        return false;
    }

    @Override
    public final String getHelpPatch() {
        return "";
    }

    @Override
    public File getHelpPatchFile() {
        return null;
    }

    @Override
    public List<SDFileReference> getFileDepends() {
        return Collections.emptyList();
    }
}
