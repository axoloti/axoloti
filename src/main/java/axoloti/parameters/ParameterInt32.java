/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public abstract int getMinimum();

    public abstract int getMaximum();
}
