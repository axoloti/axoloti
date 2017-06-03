package axoloti.parameters;

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

}
