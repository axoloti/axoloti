package axoloti.object.outlet;

import axoloti.datatypes.DataType;

/**
 *
 * @author jtaelman
 */
public class OutletZombie extends Outlet {

    public OutletZombie(String name) {
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
