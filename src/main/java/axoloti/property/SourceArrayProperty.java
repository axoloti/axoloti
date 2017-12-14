
package axoloti.property;

import axoloti.outlets.OutletInstance;

/**
 *
 * @author jtaelman
 */
public class SourceArrayProperty extends PropertyReadWrite<OutletInstance[]> {

    public SourceArrayProperty(String name, Class c) {
        super(name, OutletInstance[].class, c, name);
    }

    @Override
    public Class getType() {
        return OutletInstance[].class;
    }

    @Override
    public boolean allowNull() {
        return true;
    }

    @Override
    public String getAsString(Object o) {
        return null;
    }

    @Override
    public OutletInstance[] StringToObj(String v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
