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

import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceBin1Momentary extends ParameterInstanceBin {

    public ParameterInstanceBin1Momentary() {
        super();
    }

    public ParameterInstanceBin1Momentary(@Attribute(name = "value") int v) {
        super(v);
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        // Hmmm how to deal with this?
        // Normal behavious would be generating a pulse triggered by any incoming CC value > 0 ?
        //
        // Hi, MIDI specialists, how common is this needed, how well is it supported in sequencers etc?
        //
        // Do we need to extend the parameter model to objects writing a new parameter value themselves?
        return "";
    }

}
