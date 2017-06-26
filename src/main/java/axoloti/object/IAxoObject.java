package axoloti.object;

import axoloti.Modulator;
import axoloti.inlets.Inlet;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IModel;
import axoloti.mvc.array.ArrayModel;
import axoloti.outlets.Outlet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jtaelman
 */
public interface IAxoObject extends IModel {
    
    public ArrayModel<Inlet> getInlets();
        
    public ArrayModel<Outlet> getOutlets();
    
    public String getId();

    public String getDescription();

    public String getAuthor();

    public String getLicense();

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
}
