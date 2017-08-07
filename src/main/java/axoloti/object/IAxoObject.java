package axoloti.object;

import axoloti.Modulator;
import axoloti.SDFileReference;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.displays.Display;
import axoloti.inlets.Inlet;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IModel;
import axoloti.outlets.Outlet;
import axoloti.parameters.Parameter;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
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

    public String getPath();

    public String getUUID();

    public String getDefaultInstanceName();

    public ObjectController createController(AbstractDocumentRoot documentRoot, AbstractController parent);

    public Set<String> GetIncludes();

    public void SetIncludes(HashSet<String> includes);

    public Set<String> GetDepends();

    public Set<String> GetModules();

    public Modulator[] getModulators();

    public boolean isCreatedFromRelativePath();

    public String getInitCode();

    public String getDisposeCode();

    public String getLocalData();

    public String getKRateCode();

    public String getSRateCode();

    public String getMidiCode();

    public Boolean getRotatedParams();

    public File GetHelpPatchFile();

    public void OpenEditor(Rectangle editorBounds, Integer editorActiveTabIndex);

    public ArrayList<SDFileReference> getFileDepends();
}
