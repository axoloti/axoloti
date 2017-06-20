package axoloti.parameters;

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
}
