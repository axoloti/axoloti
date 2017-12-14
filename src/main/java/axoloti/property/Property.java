package axoloti.property;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 * @param <T>
 */
public abstract class Property<T> {

    final String name;
    final String friendlyName;

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

    public abstract T get(Object o);

    public abstract String getAsString(Object o);

    public abstract T StringToObj(String v);

    public abstract void set(Object obj, Object val);

    public String getFriendlyName() {
        return friendlyName;
    }

    public abstract Class getType();

    public abstract boolean allowNull();
    
    public boolean is(PropertyChangeEvent evt) {
        return (getName().equals(evt.getPropertyName()));
    }

    public abstract boolean isReadOnly();

}
