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

import static axoloti.parameters.ParameterInstance.MIDI_CC;
import axoloti.property.Property;
import java.util.List;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceInt32HRadio extends ParameterInstanceInt32 {

    public ParameterInstanceInt32HRadio() {
    }

    @Override
    public List<Property> getEditableFields() {
        List<Property> l = super.getEditableFields();
        l.add(MIDI_CC);
        return l;
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        // hmm this is only one possible behavior - could also map to full MIDI range...
        int max = ((ParameterInt32HRadio) parameter).MaxValue.getInt();
        return GenerateMidiCCCodeSub(vprefix, "(data2<" + max + ")?data2:" + (max - 1));
    }

}
