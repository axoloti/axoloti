/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.parameters;

import axoloti.PresetInt;
import axoloti.object.AxoObjectInstance;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterInstanceInt32<T extends ParameterInt32> extends ParameterInstance<T, Integer> {

    @Attribute(name = "value", required = false)
    Integer value = 0;

    @ElementListUnion({
        @ElementList(entry = "Preset", type = PresetInt.class, inline = false, required = false)
    })
    ArrayList<PresetInt> presets;

    public ParameterInstanceInt32() {
    }

    public ParameterInstanceInt32(T param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public int valToInt32(Integer v) {
        return (int) v;
    }

    @Override
    public Integer int32ToVal(int v) {
        return v;
    }

    @Override
    public PresetInt presetFactory(int index, Integer value) {
        return new PresetInt(index, value);
    }

    @Override
    public ArrayList<PresetInt> getPresets() {
        if (presets != null) {
            return presets;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setPresets(Object presets) {
        ArrayList<PresetInt> prevValue = getPresets();
        this.presets = (ArrayList<PresetInt>) presets;
        firePropertyChange(ParameterInstance.PRESETS, prevValue, this.presets);
    }

    @Override
    public PresetInt getPreset(int i) {
        return (PresetInt) super.getPreset(i);
    }

    @Override
    public String GenerateParameterInitializer() {
        String s = "{ type: " + parameter.GetCType()
                + ", unit: " + parameter.GetCUnit()
                + ", signals: 0"
                + ", pfunction: " + ((GetPFunction() == null) ? "0" : GetPFunction());
        int v = getValue();
        s += ", d: { intt: { finalvalue: 0"
                + ", value: " + v
                + ", modvalue: " + v
                + ", minimum: " + parameter.getMinValue()
                + ", maximum: " + parameter.getMaxValue()
                + "}}},\n";
        return s;
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getOnParent() && (enableOnParent)) {
            return "%" + ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.intt.finalvalue";
        }
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".t_int.value";
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceInt32) {
            ParameterInstanceInt32 p1 = (ParameterInstanceInt32) p;
            setValue(p1.getValue());
        }
    }

    /**
     * **
     */
    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        Integer oldvalue = this.value;
        this.value = (Integer)value;
        needsTransmit = true;
        firePropertyChange(
                ParameterInstance.VALUE,
                oldvalue, value);
    }

    public Integer getMinValue() {
        return getModel().getMinValue();
    }

    public Integer getMaxValue() {
        return getModel().getMaxValue();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInt32.VALUE_MIN.is(evt)) {
            firePropertyChange(ParameterInt32.VALUE_MIN, evt.getOldValue(), evt.getNewValue());
        } else if (ParameterInt32.VALUE_MAX.is(evt)) {
            firePropertyChange(ParameterInt32.VALUE_MAX, evt.getOldValue(), evt.getNewValue());
        }
    }
}
