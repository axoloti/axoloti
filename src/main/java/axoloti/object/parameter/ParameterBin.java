package axoloti.object.parameter;

import axoloti.datatypes.ValueInt32;
import axoloti.patch.object.parameter.ParameterInstanceBin;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterBin<T extends ParameterInstanceBin> extends Parameter<T> {

    public ParameterBin() {
    }

    public ParameterBin(String name) {
        super(name);
    }

    public abstract int getNBits();

    @Override
    public ValueInt32 getDefaultValue() {
        return new ValueInt32(0);
    }

}
