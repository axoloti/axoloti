package axoloti.parameters;

import axoloti.property.IntegerProperty;
import axoloti.property.Property;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterInt32<T extends ParameterInstanceInt32> extends Parameter<T> {

    public ParameterInt32() {
    }

    public ParameterInt32(String name) {
        super(name);
    }

    public final static Property VALUE_MIN = new IntegerProperty("MinValue", ParameterInt32.class, "Minimum");
    public final static Property VALUE_MAX = new IntegerProperty("MaxValue", ParameterInt32.class, "Maximum");
    
    public abstract Integer getMinValue();
    public abstract void setMinValue(Integer v);
    public abstract Integer getMaxValue();
    public abstract void setMaxValue(Integer v);

    @Override
    public Integer getDefaultValue() {
        return 0;
    }
}
