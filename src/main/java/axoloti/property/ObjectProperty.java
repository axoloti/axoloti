
package axoloti.property;

import axoloti.mvc.IModel;

/**
 *
 * @author jtaelman
 */
public class ObjectProperty extends PropertyReadWrite<Object> {

    final Class clazz;

    public ObjectProperty(String name, Class propertyClass, Class containerClass) {
        super(name, propertyClass, containerClass, name);
        this.clazz = propertyClass;
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
    public Object convertStringToObj(String v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
