package axoloti.utils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents a location for objects and patches to be picked up
 */
// this will probably be subclasses later for different types 
// e.g. local, git, http
@Root(name = "library")
public class AxolotiLibrary {

    @Element(required = true)
    private String Id;
    @Element(required = true)
    private String Type;
    @Element(required = true)
    private String LocalLocation;
    @Element(required = true)
    private Boolean Enabled;
    @Element(required = false)
    private String RemoteLocation;
    @Element(required = false)
    private String UserId;
    @Element(required = false)
    private String Password;
    @Element(required = false)
    private boolean AutoSync;

    public AxolotiLibrary() {
        Id = "";
        Type = "local";
        Enabled = true;
        LocalLocation = "";
        RemoteLocation = "";
        UserId = "";
        Password = "";
        AutoSync = true;
    }
    
    public AxolotiLibrary(String id, String type, String lloc, boolean e, String rloc) {
        Id = id;
        Type = type;
        LocalLocation = lloc;
        Enabled = e;
        RemoteLocation = rloc;
        UserId = "";
        Password = "";
        AutoSync = true;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public void setLocalLocation(String LocalLocation) {
        this.LocalLocation = LocalLocation;
    }

    public void setEnabled(Boolean Enabled) {
        this.Enabled = Enabled;
    }

    public void setRemoteLocation(String RemoteLocation) {
        this.RemoteLocation = RemoteLocation;
    }

    public String getId() {
        return Id;
    }

    public String getType() {
        return Type;
    }

    public String getLocalLocation() {
        return LocalLocation;
    }

    public Boolean getEnabled() {
        return Enabled;
    }

    public String getRemoteLocation() {
        return RemoteLocation;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public boolean isAutoSync() {
        return AutoSync;
    }

    public void setAutoSync(boolean AutoSync) {
        this.AutoSync = AutoSync;
    }

}
