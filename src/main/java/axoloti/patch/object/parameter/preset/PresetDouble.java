package axoloti.patch.object.parameter.preset;

import axoloti.datatypes.ValueFrac32;
import org.simpleframework.xml.Element;

/**
 *
 * @author jtaelman
 */
public class PresetDouble extends Preset<Double> {

    @Element(name = "f", required = true)
    public ValueFrac32 getValuex() {
        return new ValueFrac32(getValue());
    }

    public PresetDouble() {
    }

    public PresetDouble(@Element(name = "f") ValueFrac32 x) {
        super(x.getDouble());
    }

    public PresetDouble(int index, Double value) {
        super(index, value);
    }

}
