package axoloti.utils;

import java.io.File;

public class AxoFileLibrary
        extends AxolotiLibrary {

    public AxoFileLibrary() {
        super();
    }

    public AxoFileLibrary(String id, String type, String lloc, boolean e) {
        super(id, type, lloc, e, null);
    }

    @Override
    public void sync() {
        // NOP
    }

    @Override
    public void init() {
        // NOP 
        // would be dangerous to delete local files
        // we should assume they are not backed up
        
        File ldir = new File(getLocalLocation());
        if(!ldir.exists()) {
            ldir.mkdirs();
        }
    }
}
