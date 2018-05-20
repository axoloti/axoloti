package axoloti.object.parameter;

import axoloti.property.IntegerProperty;
import axoloti.property.Property;
import axoloti.realunits.NativeToReal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterInt32 extends Parameter {

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

    private static final NativeToReal convs[] = {};
    private static final List<NativeToReal> listConvs = Collections.unmodifiableList(Arrays.asList(convs));

    @Override
    public List<NativeToReal> getConversions() {
        return listConvs;
    }

    @Override
    public Integer getDefaultValue() {
        return 0;
    }
}
