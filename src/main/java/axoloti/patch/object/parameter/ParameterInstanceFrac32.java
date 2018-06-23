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

import axoloti.datatypes.ValueFrac32;
import axoloti.object.parameter.ParameterFrac32;
import axoloti.patch.Modulation;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.patch.object.parameter.preset.PresetDouble;
import axoloti.utils.ListUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterInstanceFrac32<Tx extends ParameterFrac32> extends ParameterInstance<Tx, Double> {

    @Attribute(name = "value", required = false)
    public double getValuex() {
        return value;
    }
    @ElementList(required = false)
    ArrayList<Modulation> modulators;

    @Path("presets")
    @ElementListUnion({
        @ElementList(entry = "preset", type = PresetDouble.class, inline = true, required = false)
    })
    List<Preset> presets = new ArrayList<>();

    Double value = 0.0;

    public ParameterInstanceFrac32(@Attribute(name = "value") double v) {
        value = v;
    }

    public ParameterInstanceFrac32() {
        //value = new ValueFrac32();
    }

    public ParameterInstanceFrac32(Tx param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    public abstract double getMin();

    public abstract double getMax();

    public abstract double getTick();

    @Override
    public int valToInt32(Double v) {
        int f2i = 1 << 21;
        return (int) Math.round(v * f2i);
    }

    @Override
    public Double int32ToVal(int v) {
        double i2f = 1.0 / (1 << 21);
        return v * i2f;
    }

    @Override
    public PresetDouble presetFactory(int index, Double value) {
        return new PresetDouble(index, value);
    }

    @Override
    public List<Preset> getPresets() {
        return ListUtils.export(presets);
    }

    @Override
    public void setPresets(List<Preset> presets) {
        List<Preset> prevValue = getPresets();
        this.presets = presets;
        firePropertyChange(PRESETS, prevValue, this.presets);
    }

    @Override
    public PresetDouble getPreset(int i) {
        return (PresetDouble) super.getPreset(i);
    }


    @Override
    public List<Modulation> getModulations() {
        return ListUtils.export(modulators);
    }

    @Override
    public void setModulations(List<Modulation> modulations) {
        List<Modulation> old_val = this.modulators;
        if ((modulations == null) || modulations.isEmpty()) {
            this.modulators = null;
        } else {
            this.modulators = new ArrayList<>(modulations);
        }
        firePropertyChange(MODULATIONS, old_val, this.modulators);
    }

    @Override
    public void copyValueFrom(ParameterInstance p) {
        super.copyValueFrom(p);
        if (p instanceof ParameterInstanceFrac32) {
            ParameterInstanceFrac32 p1 = (ParameterInstanceFrac32) p;
            List<Modulation> new_modulations = new LinkedList<>();
            List<Modulation> orig_modulations = p1.getModulations();

            for (Modulation m : orig_modulations) {
                Modulation new_m = m.createClone();
                new_m.setParameter(this);
                new_modulations.add(new_m);
            }
            setModulations(new_modulations);
            setValue(p1.getValue());
        }
    }

    @Override
    public Tx createParameterForParent() {
        Tx p = super.createParameterForParent();
        p.DefaultValue = new ValueFrac32(value);
        return p;
    }

    @Override
    public Double getValue() {
        if (value == null) {
            return 0.0;
        } else {
            return value;
        }
    }

    @Override
    public void setValue(Object value) {
        Double oldvalue = this.value;
        this.value = (Double)value;
        firePropertyChange(
                VALUE,
                oldvalue, value);
        if (paramOnParent != null) {
            paramOnParent.setDefaultValue((Double)value);
        }
    }

}
