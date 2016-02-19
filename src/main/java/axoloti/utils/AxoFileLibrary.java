package axoloti.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AxoFileLibrary
        extends AxolotiLibrary {

    public static String TYPE="local";
    
    public AxoFileLibrary() {
        super();
    }

    public AxoFileLibrary(String id, String type, String lloc, boolean e) {
        super(id, type, lloc, e, null, false);
    }

    @Override
    public void sync() {
        // NOP
    }
    
    public void reportStatus() {
        File f = new File(getLocalLocation()); 
        if(!f.exists()) {
           Logger.getLogger(AxoFileLibrary.class.getName()).log(Level.WARNING, "Status : {0} : local directory missing", getId());
        }
        Logger.getLogger(AxoFileLibrary.class.getName()).log(Level.INFO, "Status : {0} : OK", getId());
    }
    
    @Override
    public void init(boolean delete) {
        // NOP 
        // would be dangerous to delete local files
        // we should assume they are not backed up

        File ldir = new File(getLocalLocation());
        if (!ldir.exists()) {
            ldir.mkdirs();
        }

        // default directory structure
        File odir = new File(ldir, "objects");
        if (!odir.exists()) {
            odir.mkdirs();
        }
        File pdir = new File(ldir, "patches");
        if (!pdir.exists()) {
            pdir.mkdirs();
        }
    }
}
