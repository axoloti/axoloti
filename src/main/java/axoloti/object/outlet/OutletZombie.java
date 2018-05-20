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

    static public final String TypeName = "zombie";

    @Override
    public String getTypeName() {
        return TypeName;
    }

}
