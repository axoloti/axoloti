package axoloti.object;

import axoloti.Modulator;
import axoloti.mvc.IModel;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.display.Display;
import axoloti.object.inlet.Inlet;
import axoloti.object.outlet.Outlet;
import axoloti.object.parameter.Parameter;
import axoloti.target.fs.SDFileReference;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jtaelman
 */
public interface IAxoObject extends IModel {

    public List<Inlet> getInlets();

    public List<Outlet> getOutlets();

    public List<AxoAttribute> getAttributes();

    public List<Parameter> getParameters();

    public List<Display> getDisplays();

    public String getId();

    public String getDescription();

    public String getAuthor();

    public String getLicense();

    public String getHelpPatch();

    public File getHelpPatchFile();

    public String getPath();

    public String getUUID();

    public String getDefaultInstanceName();

    @Override
    public ObjectController getControllerFromModel();

    public Set<String> getIncludes();

    public void setIncludes(HashSet<String> includes);

    public Set<String> getDepends();

    public Set<String> getModules();

    public Modulator[] getModulators();

    public boolean isCreatedFromRelativePath();

    public String getInitCode();

    public String getDisposeCode();

    public String getLocalData();

    public String getKRateCode();

    public String getSRateCode();

    public String getMidiCode();

    public Boolean getRotatedParams();

    public void OpenEditor();

    public List<SDFileReference> getFileDepends();
}
