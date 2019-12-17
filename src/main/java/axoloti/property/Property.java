package axoloti.property;

import axoloti.mvc.IModel;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 * @param <T>
 */
public abstract class Property<T> {

    private final String name;
    private final String friendlyName;

    public Property(String name, Class propertyClass, Class containerClass, String friendlyName) {
        this.name = name;
        if (friendlyName == null) {
            this.friendlyName = name;
        } else {
            this.friendlyName = friendlyName;
        }
    }

    public String getName() {
        return name;
    }

    public T cast(Object obj) {
        return (T) obj;
    }

    public T getNewValue(PropertyChangeEvent evt) {
        return (T)evt.getNewValue();
    }
    
    public abstract T get(IModel o);

    public abstract String getAsString(IModel o);

    public abstract T convertStringToObj(String v);

    public abstract void set(IModel obj, Object val);

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getTechyName() {
        return name;
    }

    public abstract Class getType();

    public abstract boolean allowNull();

    public boolean is(PropertyChangeEvent evt) {
        return (getName().equals(evt.getPropertyName()));
    }

    public abstract boolean isReadOnly();

}
