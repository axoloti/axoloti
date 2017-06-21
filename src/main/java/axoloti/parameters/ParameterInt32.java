package axoloti.parameters;

import axoloti.datatypes.ValueInt32;

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

    public abstract Integer getMinValue();

    public abstract Integer getMaxValue();

    @Override
    public ValueInt32 getDefaultValue() {
        return new ValueInt32(0);
    }
}
