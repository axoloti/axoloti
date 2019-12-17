
package axoloti.property;

import axoloti.mvc.IModel;
import axoloti.patch.object.outlet.OutletInstance;

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
    public String getAsString(IModel o) {
        return null;
    }

    @Override
    public OutletInstance[] convertStringToObj(String v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
