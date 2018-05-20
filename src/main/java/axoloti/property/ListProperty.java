
package axoloti.property;

import axoloti.mvc.IModel;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class ListProperty extends PropertyReadWrite<Object> {

    final Class clazz;

    public ListProperty(String name, Class containerClass) {
        super(name, java.util.List.class, containerClass, name);
        this.clazz = java.util.List.class;
    }

    @Override
    public Class getType() {
        return clazz;
    }

    @Override
    public boolean allowNull() {
        return true;
    }

    @Override
    public String getAsString(IModel o) {
        return get(o).toString();
    }

    @Override
    public List convertStringToObj(String v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}