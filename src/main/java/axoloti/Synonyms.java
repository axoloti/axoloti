package axoloti;

import axoloti.utils.AxolotiLibrary;
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
            AxolotiLibrary lib = MainFrame.prefs.getLibrary(AxolotiLibrary.FACTORY_ID);
            if(lib != null) {
                instance = serializer.read(Synonyms.class, new File(lib.getLocalLocation() + filename));
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
            serializer.write(instance, new File(filename));
        } catch (Exception ex) {
            Logger.getLogger(Synonyms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static Synonyms instance = null;
    static String filename = "objects/synonyms.xml";

    protected Synonyms() {
        inlets = new HashMap<String, String>();
        outlets= new HashMap<String, String>();
    }

    @ElementMap(entry = "inlet", key = "a", value = "b", attribute=true ,inline = false)
    HashMap<String, String> inlets;
    @ElementMap(entry = "outlet", key = "a", value = "b", attribute=true ,inline = false)
    HashMap<String, String> outlets;
}
