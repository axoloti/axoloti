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
package axoloti.object.parameter;

import axoloti.realunits.DecayTime;
import axoloti.realunits.NativeToReal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterFrac32UMapKDecayTime extends ParameterFrac32UMap {

    public ParameterFrac32UMapKDecayTime() {
        super();
    }

    public ParameterFrac32UMapKDecayTime(String name) {
        super(name);
    }

    private static final NativeToReal convs[] = {new DecayTime()};
    private static final List<NativeToReal> listConvs = Collections.unmodifiableList(Arrays.asList(convs));

    @Override
    public List<NativeToReal> getConversions() {
        return listConvs;
    }

    static public final String TYPE_NAME = "frac32.u.map.kdecaytime";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }
}
