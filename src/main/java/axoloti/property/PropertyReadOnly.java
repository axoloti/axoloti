package axoloti.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public abstract class PropertyReadOnly<T extends Object> extends Property {

    final Method getter;

    public PropertyReadOnly(String name, Class propertyClass, Class containerClass, String friendlyName) {
        super(name, propertyClass, containerClass, friendlyName);
        Method g = null;
        try {
            g = containerClass.getMethod("get" + name, new Class[]{});
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, containerClass.getName() + ":" + name, ex);
        }
        getter = g;
    }

    @Override
    public T get(Object obj) {
        try {
            Object r = getter.invoke(obj);
            return (T) r;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, obj.getClass().getName() + ".get" + getName() + "()", ex);
        }
        return null;
    }

    @Override
    public void set(Object obj, Object val) {
        Logger.getLogger(Property.class.getName()).log(Level.SEVERE, obj.getClass().getName() + "::" + getName());
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

}
