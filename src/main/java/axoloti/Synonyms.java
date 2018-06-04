package axoloti;

import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.preferences.Preferences;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class Synonyms {

    public static Synonyms instance() {
        if (instance == null) {
//            instance = new Synonyms();
//            instance.inlet("pitchm", "pitch");
//            instance.outlet("out", "o");
//            save();
            load();
        }
        return instance;
    }

    public String inlet(String a) {
        return inlets.get(a);
    }

    public void inlet(String a, String b) {
        inlets.put(a,b);
    }

    public String outlet(String a) {
        return outlets.get(a);
    }

    public void outlet(String a, String b) {
        outlets.put(a, b);
    }

    static void load() {
        Serializer serializer = new Persister();
        try {
            AxolotiLibrary lib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
            if(lib != null) {
                instance = serializer.read(Synonyms.class, new File(lib.getLocalLocation() + FILENAME));
            } else {
                Logger.getLogger(Synonyms.class.getName()).log(Level.WARNING,"not loading synonyms cannot find factory library");
            }


        } catch (Exception ex) {
            Logger.getLogger(Synonyms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void save() {
        Serializer serializer = new Persister();
        try {
            serializer.write(instance, new File(FILENAME));
        } catch (Exception ex) {
            Logger.getLogger(Synonyms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Synonyms instance = null;
    private static final String FILENAME = "objects/synonyms.xml";

    protected Synonyms() {
        inlets = new HashMap<>();
        outlets= new HashMap<>();
    }

    @ElementMap(entry = "inlet", key = "a", value = "b", attribute=true ,inline = false)
    private HashMap<String, String> inlets;
    @ElementMap(entry = "outlet", key = "a", value = "b", attribute=true ,inline = false)
    private HashMap<String, String> outlets;
}
