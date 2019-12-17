
package axoloti.property;

import axoloti.mvc.IModel;
import axoloti.patch.object.inlet.InletInstance;

/**
 *
 * @author jtaelman
 */
public class DestinationArrayProperty extends PropertyReadWrite<InletInstance[]> {

    public DestinationArrayProperty(String name, Class c) {
        super(name, InletInstance[].class, c, name);
    }

    @Override
    public Class getType() {
        return InletInstance[].class;
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
    public InletInstance[] convertStringToObj(String v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
