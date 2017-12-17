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

import org.simpleframework.xml.Element;

/**
 *
 * @author jtaelman
 */
public class PatchSettings {

    @Element
    SubPatchMode subpatchmode = SubPatchMode.no;
    @Element(required = false)
    Integer MidiChannel;
    @Element(required = false)
    Boolean HasMidiChannelSelector;
    @Element(required = false)
    Integer NPresets;
    @Element(required = false)
    Integer NPresetEntries;
    @Element(required = false)
    Integer NModulationSources;
    @Element(required = false)
    Integer NModulationTargetsPerSource;
    @Element(required = false)
    String Author;
    @Element(required = false)
    String License;
    @Element(required = false)
    String Attributions;
    @Element(required = false)
    Boolean Saturate;
}
