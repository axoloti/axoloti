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
package axoloti.patch.object.parameter;

import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.utils.ListUtils;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterInstanceInt32<T extends ParameterInt32> extends ParameterInstance<T, Integer> {

    @Attribute(name = "value", required = false)
    Integer value = 0;

    @Path("presets")
    @ElementListUnion({
        @ElementList(entry = "preset", type = PresetInt.class, inline = true, required = false)
    })
    List<Preset> presets = new ArrayList<>();

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
    public List<Preset> getPresets() {
        return ListUtils.export(presets);
    }

    @Override
    public void setPresets(List<Preset> presets) {
        List<Preset> prevValue = getPresets();
        this.presets = presets;
        firePropertyChange(ParameterInstance.PRESETS, prevValue, this.presets);
    }

    @Override
    public PresetInt getPreset(int i) {
        return (PresetInt) super.getPreset(i);
    }

    @Override
    public void copyValueFrom(ParameterInstance p) {
        super.copyValueFrom(p);
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
        firePropertyChange(
                ParameterInstance.VALUE,
                oldvalue, value);
    }

    public Integer getMinValue() {
        return getDModel().getMinValue();
    }

    public Integer getMaxValue() {
        return getDModel().getMaxValue();
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
