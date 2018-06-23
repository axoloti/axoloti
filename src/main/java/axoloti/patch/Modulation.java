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
package axoloti.patch;

import axoloti.datatypes.ValueFrac32;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.parameter.ParameterInstanceFrac32;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class Modulation {

    @Attribute
    private String sourceName; // object instance name

    @Attribute(required = false)
    private String modName; // name of modulation (can be null or empty)

    @Attribute(name = "value", required = false)
    public double getValuex() {
        return value.getDouble();
    }

    private ValueFrac32 value;

    public Modulation(@Attribute(name = "value") double v) {
        value = new ValueFrac32(v);
    }

    public Modulation() {
        value = null;
    }

    public Modulation(Modulator source, ParameterInstanceFrac32 destination) {
        this.source = source;
        this.destination = destination;

        source.getController().addModulation(this);
    }

    @Persist
    public void persist() {
        if (source != null) {
            sourceName = source.getObjectInstance().getInstanceName();
            modName = source.getName();
        }
    }

    public Double getValue() {
        return value.getDouble();
    }

    public void setValue(Double value) {
        this.value = new ValueFrac32(value);
    }

    public Modulation createClone() {
        Modulation m = new Modulation();
        m.sourceName = sourceName;
        m.modName = modName;
        m.value = new ValueFrac32(value);
        return m;
    }

    private Modulator source;
    private ParameterInstanceFrac32 destination;

    public Modulator getModulator() {
        if (source == null) {
            IAxoObjectInstance obj = destination.getParent().getParent().findObjectInstance(sourceName);
            if (obj == null) {
                return null;
            }
            List<Modulator> modulators = obj.getModulators();
            for (Modulator m : modulators) {
                if (((m.getName() == null) && ((modName == null) || (modName.isEmpty())))
                        || ((m.getName() != null) && (m.getName().equals(modName)))) {
                    source = m;
                    m.getController().addModulation(this);
                    return source;
                }
            }
        }
        return source;
    }

    public ParameterInstanceFrac32 getParameter() {
        return destination;
    }

    public void setParameter(ParameterInstanceFrac32 param) {
        destination = param;
    }

}
