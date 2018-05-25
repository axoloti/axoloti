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
    public DataType getDataType() {
        return new axoloti.datatypes.DTZombie();
    }

    static public final String TYPE_NAME = "zombie";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

}
