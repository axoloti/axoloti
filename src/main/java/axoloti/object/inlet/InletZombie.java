package axoloti.object.inlet;

import axoloti.datatypes.DataType;

/**
 *
 * @author jtaelman
 */
public class InletZombie extends Inlet {

    public InletZombie(String name) {
        super(name, "");
    }

    @Override
    public DataType getDatatype() {
        return new axoloti.datatypes.DTZombie();
    }

    static public final String TypeName = "zombie";

    @Override
    public String getTypeName() {
        return TypeName;
    }

}
