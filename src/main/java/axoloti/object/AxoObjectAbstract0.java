package axoloti.object;

import axoloti.SDFileReference;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.displays.Display;
import axoloti.inlets.Inlet;
import axoloti.outlets.Outlet;
import axoloti.parameters.Parameter;
import axoloti.property.Property;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
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
        return new ArrayList<>();
    }

    @Override
    public List<Outlet> getOutlets() {
        return new ArrayList<>();
    }

    @Override
    public List<AxoAttribute> getAttributes() {
        return new ArrayList<>();
    }

    @Override
    public List<Parameter> getParameters() {
        return new ArrayList<>();
    }

    @Override
    public List<Display> getDisplays() {
        return new ArrayList<>();
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
    public File GetHelpPatchFile() {
        return null;
    }

    @Override
    public void OpenEditor(Rectangle editorBounds, Integer editorActiveTabIndex) {        
    }

    @Override
    public ArrayList<SDFileReference> getFileDepends() {
        return new ArrayList<>();
    }
}
