package axoloti.object;

import axoloti.abstractui.IAbstractEditor;
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

    String getSHA();

    String getDefaultInstanceName();

    @Override
    ObjectController getController();

    /**
     * Resolve the includes.
     *
     * @return an unmodifiable list of resolved includes.
     */
    List<String> getProcessedIncludes();

    List<String> getIncludes();

    void setIncludes(List<String> includes);

    List<String> getDepends();

    List<String> getModules();

    List<String> getModulators();

    boolean isCreatedFromRelativePath();

    String getInitCode();

    String getDisposeCode();

    String getLocalData();

    String getKRateCode();

    String getSRateCode();

    String getMidiCode();

    Boolean getRotatedParams();

    IAbstractEditor getEditor();

    void setEditor(IAbstractEditor editor);

    List<SDFileReference> getFileDepends();
}
