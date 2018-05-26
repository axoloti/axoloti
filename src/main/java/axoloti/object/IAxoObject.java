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
import java.util.List;

/**
 *
 * @author jtaelman
 */
public interface IAxoObject extends IModel {

    List<Inlet> getInlets();

    List<Outlet> getOutlets();

    List<AxoAttribute> getAttributes();

    List<Parameter> getParameters();

    List<Display> getDisplays();

    String getId();

    String getDescription();

    String getAuthor();

    String getLicense();

    String getHelpPatch();

    File getHelpPatchFile();

    String getPath();

    String getUUID();

    String getDefaultInstanceName();

    @Override
    ObjectController getController();

    List<String> getIncludes();

    void setIncludes(List<String> includes);

    List<String> getDepends();

    List<String> getModules();

    Modulator[] getModulators();

    boolean isCreatedFromRelativePath();

    String getInitCode();

    String getDisposeCode();

    String getLocalData();

    String getKRateCode();

    String getSRateCode();

    String getMidiCode();

    Boolean getRotatedParams();

    void openEditor();

    List<SDFileReference> getFileDepends();
}
