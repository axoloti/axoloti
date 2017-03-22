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
public abstract class ParameterBin<T extends ParameterInstanceBin> extends Parameter<T> {

    public ParameterBin() {
    }

    public ParameterBin(String name) {
        super(name);
    }

    public abstract int getNBits();

}
