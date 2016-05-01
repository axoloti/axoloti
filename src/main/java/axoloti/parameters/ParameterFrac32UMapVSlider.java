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
package axoloti.parameters;

import axoloti.datatypes.ValueFrac32;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterFrac32UMapVSlider extends ParameterFrac32 {

    @Element
    ValueFrac32 MinValue;
    @Element
    ValueFrac32 MaxValue;

    public ParameterFrac32UMapVSlider() {
        MinValue = new ValueFrac32(0);
        MaxValue = new ValueFrac32(64);
    }

    public ParameterFrac32UMapVSlider(String name) {
        super(name);
        MinValue = new ValueFrac32(0);
        MaxValue = new ValueFrac32(64);
    }

    @Override
    public ParameterInstance InstanceFactory() {
        return new ParameterInstanceFrac32UMapVSlider();
    }

    static public final String TypeName = "frac32.u.mapvsl";

    @Override
    public String getTypeName() {
        return TypeName;
    }
}
