package axoloti.object.parameter;

import axoloti.realunits.NativeToReal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterBin extends Parameter {

    public ParameterBin() {
    }

    public ParameterBin(String name) {
        super(name);
    }

    private static final NativeToReal convs[] = {};
    private static final List<NativeToReal> listConvs = Collections.unmodifiableList(Arrays.asList(convs));

    @Override
    public List<NativeToReal> getConversions() {
        return listConvs;
    }

    public abstract int getNBits();

    @Override
    public Integer getDefaultValue() {
        return 0;
    }

}
