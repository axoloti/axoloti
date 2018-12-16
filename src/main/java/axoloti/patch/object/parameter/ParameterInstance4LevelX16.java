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
import axoloti.utils.ListUtils;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstance4LevelX16 extends ParameterInstance<Parameter4LevelX16, Integer> {

    @Attribute(name = "value", required = false)
    Integer value = 0;

    @Path("presets")
    @ElementListUnion({
        @ElementList(entry = "preset", type = PresetInt.class, inline = true, required = false)
    })
    List<Preset> presets;

    public ParameterInstance4LevelX16() {
    }

    public ParameterInstance4LevelX16(@Attribute(name = "value") int v) {
        this.value = v;
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
    public PresetInt getPreset(int i) {
        return (PresetInt) super.getPreset(i);
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
    public void copyValueFrom(ParameterInstance p) {
        super.copyValueFrom(p);
        if (p instanceof ParameterInstance4LevelX16) {
            ParameterInstance4LevelX16 p1 = (ParameterInstance4LevelX16) p;
            setValue(p1.getValue());
        }
    }

}
