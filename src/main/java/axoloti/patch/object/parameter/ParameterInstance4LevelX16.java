/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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

import axoloti.object.parameter.Parameter4LevelX16;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.patch.object.parameter.preset.PresetInt;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstance4LevelX16 extends ParameterInstance<Parameter4LevelX16, Integer> {

    @Attribute(name = "value", required = false)
    Integer value = 0;

    @ElementListUnion({
        @ElementList(entry = "Preset", type = PresetInt.class, inline = false, required = false)
    })
    ArrayList<PresetInt> presets;

    public ParameterInstance4LevelX16() {
    }

    public ParameterInstance4LevelX16(Parameter4LevelX16 param, AxoObjectInstance axoObjInstance) {
        super(param, axoObjInstance);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        Integer oldvalue = this.value;
        this.value = (Integer) value;
        firePropertyChange(
                ParameterInstance.VALUE,
                oldvalue, value);
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
    public Preset presetFactory(int index, Integer value) {
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
}
