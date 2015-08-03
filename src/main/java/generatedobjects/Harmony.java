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
package generatedobjects;

import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32Bipolar;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.parameters.ParameterBin12;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Harmony extends gentools {

    static void GenerateAll() {
        String catName = "harmony";
        WriteAxoObject(catName, CreateNoteQuantizer());

    }

    static AxoObject CreateNoteQuantizer() {
        AxoObject o = new AxoObject("note quantizer", "quantize note input to a scale");
        o.inlets.add(new InletFrac32Bipolar("note", "note number (-64..63)"));
        o.inlets.add(new InletInt32("tonic", "tonic note number (0-11)"));
        o.inlets.add(new InletInt32("offset", "note input offset (0-128)"));
        o.inlets.add(new InletBool32("latch", "latch to record scale notes"));
        o.outlets.add(new OutletFrac32Bipolar("note", "note number (-64..63)"));
        o.params.add(new ParameterBin12("b12"));
        o.sAuthor = "Mark Harris";
        o.sLocalData
                = "    int32_t _scaleVal;\n"
                + "    int8_t  _scale[12];\n"
                + "    int8_t  _nscale;\n"
                + "    int32_t  _note;\n"
                + "    int32_t  _tonic;\n"
                + "    int32_t  _offset;\n"
                + "    int32_t  _out;\n"
                + "    int32_t  _latch;\n";

        o.sInitCode
                = "    _note = 0;\n"
                + "    _scaleVal = 0;\n"
                + "    _nscale = 0;\n"
                + "    _tonic = 0;\n"
                + "    _offset = 0;\n"
                + "    for(int i=0;i<12;i++) {\n"
                + "        _scale[i] = 0;\n"
                + "    }";

        o.sKRateCode
                = "    _latch = inlet_latch;\n"
                + "    if (_scaleVal != param_b12) {\n"
                + "        // calculate new scale parameters as they changed\n"
                + "        // optimize for evaluation\n"
                + "        int x=0;\n"
                + "        for(int i=0;i<12;i++) {\n"
                + "            if(param_b12 & (1 << i)) {\n"
                + "                _scale[x++] = i;\n"
                + "            }\n"
                + "        }\n"
                + "        _nscale = x;\n"
                + "    }\n"
                + "    if (_note != inlet_note || _offset != inlet_offset || _scaleVal != param_b12\n"
                + "        || _tonic != inlet_tonic) {\n"
                + "        _note = inlet_note;\n"
                + "        _tonic = inlet_tonic;\n"
                + "        _offset = inlet_offset;\n"
                + "        _scaleVal = param_b12;\n"
                + "        int mn = (inlet_note  >> 21) + 64 - _offset;\n"
                + "        int8_t oct = mn / _nscale;\n"
                + "        int8_t n = mn  % _nscale;\n"
                + "        _out = ((oct * 12 + _scale[n] + _tonic )  - 64 ) << 21;\n"
                + "    }\n"
                + "    outlet_note = _out;\n";
        o.sMidiCode
                = "if (_latch && (status == MIDI_NOTE_ON + %midichannel% ) && (data2)) {\n"
                + "  int16_t note = data1 % 12;\n"
                + "  int16_t mask = 1 << note;\n"
                + "  int16_t nval = _scaleVal ^ mask;\n"
                + "  PExParameterChange(&parent->PExch[PARAM_INDEX_attr_legal_name_b12],nval,0xFFFD);\n"
                + "}\n";
        return o;
    }

}
