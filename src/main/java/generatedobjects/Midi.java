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
package generatedobjects;

import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletFrac32Bipolar;
import axoloti.inlets.InletFrac32Pos;
import axoloti.inlets.InletInt32Pos;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletBool32Pulse;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Pos;
import axoloti.outlets.OutletInt32;
import axoloti.outlets.OutletInt32Pos;
import axoloti.parameters.ParameterFrac32UMap;
import static generatedobjects.gentools.WriteAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
public class Midi extends gentools {

    static void GenerateAll() {
        String catName = "midi/in";
        WriteAxoObject(catName, Create_ctlin3());
        WriteAxoObject(catName, Create_ctlin_any());
        WriteAxoObject(catName, Create_ctlini_any());
        WriteAxoObject(catName, Create_ctlin3i());
        WriteAxoObject(catName, Create_ctlin4i());
        WriteAxoObject(catName, Create_ctlin4ii());
        WriteAxoObject(catName, Create_keyb());
        WriteAxoObject(catName, Create_keyb_mod());
        WriteAxoObject(catName, Create_keyb_touch());
        WriteAxoObject(catName, Create_keybzone());
        WriteAxoObject(catName, Create_keybzone_touch());
        WriteAxoObject(catName, Create_keybzoneLRU());
        WriteAxoObject(catName, Create_keybnote());
        WriteAxoObject(catName, Create_bendin());
        WriteAxoObject(catName, Create_bendin2());
        WriteAxoObject(catName, Create_bendin_ch());
        WriteAxoObject(catName, Create_touchin());
        WriteAxoObject(catName, Create_midiscript());
        WriteAxoObject(catName, Create_clockin());
        WriteAxoObject(catName, Create_pgmin());

        catName = "midi/out";
        WriteAxoObject(catName, Create_noteout());
        WriteAxoObject(catName, Create_ctlout());
        WriteAxoObject(catName, Create_ctloutauto());
        WriteAxoObject(catName, Create_ctlout_any());
        WriteAxoObject(catName, Create_bendout());
        WriteAxoObject(catName, Create_pgmout());
        WriteAxoObject(catName, Create_clockgen());
        WriteAxoObject(catName, Create_queuestate());
        WriteAxoObject(catName, Create_polytouchout());
        WriteAxoObject(catName, Create_channeltouchout());

        catName = "midi/intern";
        WriteAxoObject(catName, Create_intern_noteout());
        WriteAxoObject(catName, Create_intern_ctlout());
        WriteAxoObject(catName, Create_intern_ctloutauto());
        WriteAxoObject(catName, Create_intern_ctlout_any());
        WriteAxoObject(catName, Create_intern_bendout());
        WriteAxoObject(catName, Create_intern_clockgen());
        WriteAxoObject(catName, Create_intern_polytouchout());
        WriteAxoObject(catName, Create_intern_channeltouchout());

        catName = "midi/ctrl";
        WriteAxoObject(catName, Create_mpe());
    }
    /*
     static AxoObject Create_ctlin() {
     AxoObject o = new AxoObject("ctlin", "Receives Midi Continuous Controller messages");
     o.outlets.add(new OutletFrac32("midiCC", "midi CC 0-127"));
     o.attributes.add(new AxoAttributeInt32("CC", 0, 127, 0));
     o.attributes.add(new AxoAttributeInt32("Value", 0, 127, 0));
     o.sLocalData = "KeyValuePair kvp; \n"
     + "const char NAME[] = \"\";\n";
     o.sInitCode = "controller[%CC%] = %Value%;"
     + "  kvp.kvptype = KVP_TYPE_IVP;\n"
     + "  kvp.keyname = NAME;\n"
     + "  kvp.ivp.value = &controller[%CC%]; \n"
     + "  kvp.ivp.minvalue = " + 0 + ";\n"
     + "  kvp.ivp.maxvalue = " + 127 + ";\n"
     + "  kvp.parent =  ObjectKvpRoot;\n"
     + "  KVP_RegisterObject(&kvp);\n";
     o.sKRateCode = "%midiCC%= controller[%CC%]<<20;";
     return o;
     }

     static AxoObject Create_ctlin2() {
     AxoObject o = new AxoObject("ctlin2", "Receives Midi Continuous Controller messages, new GUI version");
     o.outlets.add(new OutletFrac32("midiCC", "midi CC 0-127"));
     o.attributes.add(new AxoAttributeSpinner("CC", 0, 127, 0));
     o.attributes.add(new AxoAttributeSpinner("Value", 0, 127, 0));
     o.sLocalData = "KeyValuePair kvp; \n"
     + "const char NAME[] = \"\";\n";
     o.sInitCode = "controller[%CC%] = %Value%;"
     + "  kvp.kvptype = KVP_TYPE_IVP;\n"
     + "  kvp.keyname = NAME;\n"
     + "  kvp.ivp.value = &controller[%CC%]; \n"
     + "  kvp.ivp.minvalue = " + 0 + ";\n"
     + "  kvp.ivp.maxvalue = " + 127 + ";\n"
     + "  kvp.parent =  ObjectKvpRoot;\n"
     + "  KVP_RegisterObject(&kvp);\n";
     o.sKRateCode = "%midiCC%= controller[%CC%]<<20;";
     return o;
     }

     static AxoObject Create_ctlin3() {
     AxoObject o = new AxoObject("ctlin", "Receives Midi Continuous Controller messages, new GUI version");
     o.outlets.add(new OutletFrac32("midiCC", "midi CC 0-127"));
     o.outlets.add(new OutletBool32("trig", "trigger output"));
     o.attributes.add(new AxoAttributeSpinner("CC", 0, 127, 0));
     o.attributes.add(new AxoAttributeSpinner("Value", 0, 127, 0));
     o.sLocalData = "KeyValuePair kvp;\n"
     + "const char NAME[] = \"\";\n"
     + "int32_t cc;\n"
     + "int32_t ntrig;\n";
     o.sInitCode = "cc = %Value%;"
     + "  kvp.kvptype = KVP_TYPE_IVP;\n"
     + "  kvp.keyname = NAME;\n"
     + "  kvp.ivp.value = &cc; \n"
     + "  kvp.ivp.minvalue = " + 0 + ";\n"
     + "  kvp.ivp.maxvalue = " + 127 + ";\n"
     + "  kvp.parent =  ObjectKvpRoot;\n"
     + "  KVP_RegisterObject(&kvp);\n";
     o.sMidiCCCode = "if (cc == %CC%) { cc = val; ntrig = 1;}\n";
     o.sKRateCode = "%midiCC%= cc<<20;\n"
     + "%trig% = ntrig;\n"
     + "ntrig = 0;\n";
     return o;
     }
     */

    static AxoObject Create_ctlin3() {
        AxoObject o = new AxoObject("cc", "Receives Midi Continuous Controller messages");
        o.outlets.add(new OutletFrac32Pos("midiCC", "midi CC 0-63.5"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("default", 0, 127, 0));
        o.sLocalData = "int32_t ccv;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "ccv = %default% << 20;\n";
        o.sMidiCode = "if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == %cc%)) { ccv = data2<<20; ntrig = 1;}\n";
        o.sKRateCode = "%midiCC%= ccv;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_ctlin_any() {
        AxoObject o = new AxoObject("cc any", "Receives Midi Continuous Controller messages from any CC number and channel.");
        o.outlets.add(new OutletFrac32Pos("value", "midi CC value 0..63.5"));
        o.outlets.add(new OutletInt32Pos("cc", "midi CC number 0..127"));
        o.outlets.add(new OutletInt32Pos("channel", "midi channel 1..16"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.sLocalData = "int32_t _value;\n"
                + "int32_t _cc;\n"
                + "int32_t _channel;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "_cc = 0;\n"
                + "_channel = 1;\n"
                + "_value = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if ((status&0xF0) == MIDI_CONTROL_CHANGE) {\n"
                + "  _value = data2<<20;;\n"
                + "  _cc = data1;\n"
                + "  _channel = (status & 0x0F) + 1;\n"
                + "  ntrig = 1;\n"
                + "}\n";
        o.sKRateCode = "%value%= _value;\n"
                + "%cc% = _cc;\n"
                + "%channel% = _channel;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_ctlini_any() {
        AxoObject o = new AxoObject("cc i any", "Receives Midi Continuous Controller messages from any CC number and channel.");
        o.outlets.add(new OutletInt32Pos("value", "midi CC value 0..127"));
        o.outlets.add(new OutletInt32Pos("cc", "midi CC number 0..127"));
        o.outlets.add(new OutletInt32Pos("channel", "midi channel 1..16"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.sLocalData = "int32_t _value;\n"
                + "int32_t _cc;\n"
                + "int32_t _channel;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "_cc = 0;\n"
                + "_channel = 1;\n"
                + "_value = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if ((status&0xF0) == MIDI_CONTROL_CHANGE) {\n"
                + "  _value = data2;\n"
                + "  _cc = data1;\n"
                + "  _channel = (status & 0x0F) + 1;\n"
                + "  ntrig = 1;\n"
                + "}\n";
        o.sKRateCode = "%value%= _value;\n"
                + "%cc% = _cc;\n"
                + "%channel% = _channel;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_ctlin3i() {
        AxoObject o = new AxoObject("cc i", "Receives Midi Continuous Controller messages, integer output (0-127)");
        o.outlets.add(new OutletInt32Pos("midiCC", "midi CC 0-127"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("default", 0, 127, 0));
        o.sLocalData = "int32_t ccv;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "ccv = %default%;\n";
        o.sMidiCode = "if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == %cc%)) { ccv = data2; ntrig = 1;}\n";
        o.sKRateCode = "%midiCC%= ccv;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_ctlin4i() {
        AxoObject o = new AxoObject("cc hr i", "Receives Midi Continuous Controller messages, 14 bit, float output");
        o.sAuthor = "Mark Harris";
        o.outlets.add(new OutletFrac32Pos("midiCC", "midi CC 0.0-64.0"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("default", 0, 127, 0));
        o.sLocalData = "uint32_t ccl;\n"
                + "uint32_t _ccv;\n"
                + "uint8_t ntrig;\n";
        o.sInitCode = "_ccv = %default%;ccl=0;ntrig=0;\n";
        o.sMidiCode = "if ((status == %midichannel% + MIDI_CONTROL_CHANGE) && (data1 == %cc%)) {\n"
                + "  _ccv = (data2 << 20) + ccl ;\n"
                + "  ntrig = 1;\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE) && (data1 == (%cc%+32))) {\n"
                + "   ccl = data2 << 13;\n"
                + "}\n";
        o.sKRateCode
                = " %midiCC%= _ccv;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_ctlin4ii() {
        AxoObject o = new AxoObject("cc hr ii", "Receives Midi Continuous Controller messages, 14 bit, float output");
        o.sAuthor = "Mark Harris";
        o.outlets.add(new OutletFrac32Pos("midiCC", "midi CC 0.0-64.0"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("ccl", 0, 127, 32));
        o.attributes.add(new AxoAttributeSpinner("default", 0, 127, 0));
        o.sLocalData = "uint32_t ccl;\n"
                + "uint32_t _ccv;\n"
                + "uint8_t ntrig;\n";
        o.sInitCode = "_ccv = %default%;ccl=0;ntrig=0;\n";
        o.sMidiCode = "if ((status == %midichannel% + MIDI_CONTROL_CHANGE) && (data1 == %cc%)) {\n"
                + "  _ccv = (data2 << 20) + ccl ;\n"
                + "  ntrig = 1;\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE) && (data1 == (%ccl%))) {\n"
                + "   ccl = data2 << 13;\n"
                + "}\n";
        o.sKRateCode
                = " %midiCC%= _ccv;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_keyb() {
        AxoObject o = new AxoObject("keyb", "Monophonic MIDI keyboard note input, gate, velocity and release velocity");
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number (-64..63)"));
        o.outlets.add(new OutletBool32("gate", "key pressed, no retrigger legato"));
        o.outlets.add(new OutletBool32("gate2", "key pressed, retrigger on legato"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.sLocalData = "int8_t _note;\n"
                + "int32_t _gate;\n"
                + "int32_t _gate2;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n";
        o.sInitCode = "_gate = 0;\n"
                + "_note = 0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  _velo = data2;\n"
                + "  _note = data1-64;\n"
                + "  _gate = 1<<27;\n"
                + "  _gate2 = 0;\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (_note == data1-64) {\n"
                + "    _rvelo = data2;\n"
                + "    _gate = 0;\n"
                + "  }\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "  _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%note%= _note<<21;\n"
                + "%gate%= _gate;\n"
                + "%gate2%= _gate2;\n"
                + "_gate2 = _gate;\n"
                + "%velocity%= _velo<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_keyb_mod() {
        AxoObject o = new AxoObject("keyb mod", "Monophonic MIDI keyboard note input, gate, velocity and release velocity modulation source");
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number (-64..63)"));
        o.outlets.add(new OutletBool32("gate", "key pressed, no retrigger legato"));
        o.outlets.add(new OutletBool32("gate2", "key pressed, retrigger on legato"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.ModulationSources = new ArrayList<String>();
        o.ModulationSources.add("note");
        o.ModulationSources.add("velocity");
        o.ModulationSources.add("releasevelocity");
        o.sLocalData = "int8_t _note;\n"
                + "int32_t _gate;\n"
                + "int32_t _gate2;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n";
        o.sInitCode = "_gate = 0;\n"
                + "_note = 0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  _velo = data2;\n"
                + "  _note = data1-64;\n"
                + "  _gate = 1<<27;\n"
                + "  _gate2 = 0;\n"
                + "  PExModulationSourceChange(\n"
                + "    &parent->GetModulationTable()[parent->MODULATOR_attr_name_velocity*NMODULATIONTARGETS],\n"
                + "    NMODULATIONTARGETS,\n"
                + "    &parent->PExch[0],\n"
                + "    &parent->PExModulationPrevVal[parent->polyIndex][parent->MODULATOR_attr_name_velocity],\n"
                + "    _velo<<20);\n"
                + "  PExModulationSourceChange(\n"
                + "    &parent->GetModulationTable()[parent->MODULATOR_attr_name_note*NMODULATIONTARGETS],\n"
                + "    NMODULATIONTARGETS,\n"
                + "    &parent->PExch[0],\n"
                + "    &parent->PExModulationPrevVal[parent->polyIndex][parent->MODULATOR_attr_name_note],\n"
                + "    _note<<21);\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (_note == data1-64) {\n"
                + "    _rvelo = data2;\n"
                + "    _gate = 0;\n"
                + "  PExModulationSourceChange(\n"
                + "    &parent->GetModulationTable()[parent->MODULATOR_attr_name_releasevelocity*NMODULATIONTARGETS],\n"
                + "    NMODULATIONTARGETS,\n"
                + "    &parent->PExch[0],\n"
                + "    &parent->PExModulationPrevVal[parent->polyIndex][parent->MODULATOR_attr_name_releasevelocity],\n"
                + "    _rvelo<<20);\n"
                + "  }\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "  _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%note%= _note<<21;\n"
                + "%gate%= _gate;\n"
                + "%gate2%= _gate2;\n"
                + "_gate2 = _gate;\n"
                + "%velocity%= _velo<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_keyb_touch() {
        AxoObject o = new AxoObject("keyb touch", "Monophonic MIDI keyboard note input, gate, velocity and release velocity. Polyphonic touch output.");
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number (-64..63)"));
        o.outlets.add(new OutletBool32("gate", "key pressed, no retrigger legato"));
        o.outlets.add(new OutletBool32("gate2", "key pressed, retrigger on legato"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.outlets.add(new OutletFrac32Pos("touch", "polyphonic aftertouch"));
        o.sLocalData = "int8_t _note;\n"
                + "int32_t _gate;\n"
                + "int32_t _gate2;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n"
                + "uint8_t _touch;\n";
        o.sInitCode = "_gate = 0;\n"
                + "_note = 0;\n"
                + "_touch = 0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  _velo = data2;\n"
                + "  _note = data1-64;\n"
                + "  _gate = 1<<27;\n"
                + "  _gate2 = 0;\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (_note == data1-64) {\n"
                + "    _rvelo = data2;\n"
                + "    _gate = 0;\n"
                + "  }\n"
                + "} else if ((status == %midichannel% + MIDI_POLY_PRESSURE)&&(data1-64 == _note)) {\n"
                + "  _touch = data2;\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "  _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%note%= _note<<21;\n"
                + "%gate%= _gate;\n"
                + "%gate2%= _gate2;\n"
                + "_gate2 = _gate;\n"
                + "%velocity%= _velo<<20;\n"
                + "%touch% = _touch<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_keybzone() {
        AxoObject o = new AxoObject("keyb zone", "Monophonic MIDI keyboard note input, gate, velocity and release velocity, only responding to a range of notes");
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number"));
        o.outlets.add(new OutletBool32("gate", "key pressed?"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.attributes.add(new AxoAttributeSpinner("startNote", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("endNote", 0, 127, 127));
        o.sLocalData = "int8_t _note;\n"
                + "uint8_t _gate;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n";
        o.sInitCode = "_gate = 0;\n"
                + "_note = 0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  if ((data1 >= %startNote%)&&(data1 <= %endNote%)) {\n"
                + "    _velo = data2;\n"
                + "    _note = data1-64;\n"
                + "    _gate = 1;\n"
                + "  }\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (_note == data1-64) {\n"
                + "    _rvelo = data2;\n"
                + "    _gate = 0;\n"
                + "  }\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "   _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%note%= _note<<21;\n"
                + "%gate%= _gate<<27;\n"
                + "%velocity%= _velo<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_keybzone_touch() {
        AxoObject o = new AxoObject("keyb zone touch", "Monophonic MIDI keyboard note input, gate, velocity and release velocity, only responding to a range of notes, with polyphonic aftertouch");
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number"));
        o.outlets.add(new OutletBool32("gate", "key pressed?"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.outlets.add(new OutletFrac32Pos("touch", "polyphonic aftertouch"));
        o.attributes.add(new AxoAttributeSpinner("startNote", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("endNote", 0, 127, 127));
        o.sLocalData = "int8_t _note;\n"
                + "uint8_t _gate;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n"
                + "uint8_t _touch;\n";
        o.sInitCode = "_gate = 0;\n"
                + "_note = 0;\n"
                + "_touch = 0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  if ((data1 >= %startNote%)&&(data1 <= %endNote%)) {\n"
                + "    _velo = data2;\n"
                + "    _note = data1-64;\n"
                + "    _gate = 1;\n"
                + "  }\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (_note == data1-64) {\n"
                + "    _rvelo = data2;\n"
                + "    _gate = 0;\n"
                + "  }\n"
                + "} else if ((status == %midichannel% + MIDI_POLY_PRESSURE)&&(data1-64 == _note)) {\n"
                + "  _touch = data2;\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "   _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%note%= _note<<21;\n"
                + "%gate%= _gate<<27;\n"
                + "%velocity%= _velo<<20;\n"
                + "%touch% = _touch<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_keybzoneLRU() {
        AxoObject o = new AxoObject("keyb zone lru", "Monophonic MIDI keyboard note input, gate, velocity and release velocity, least recently used");
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number"));
        o.outlets.add(new OutletBool32("gate", "key pressed - holds on legato notes"));
        o.outlets.add(new OutletBool32("gate2", "key pressed - retriggers on legato notes"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.attributes.add(new AxoAttributeSpinner("startNote", 0, 127, 0));
        o.attributes.add(new AxoAttributeSpinner("endNote", 0, 127, 127));
        o.sLocalData = "int8_t _note;\n"
                + "int32_t _gate;\n"
                + "int32_t _gate2;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n"
                + "uint32_t np[%endNote%-%startNote%+1];\n"
                + "uint32_t p;\n";
        o.sInitCode = "_gate = 0;\n"
                + "_gate2 = 0;\n"
                + "_note = 0;\n"
                + "p = 1;\n"
                + "int j;\n"
                + "for(j=0;j<%endNote%-%startNote%+1;j++) np[j]=0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  if ((data1 >= %startNote%)&&(data1 <= %endNote%)) {\n"
                + "    _velo = data2;\n"
                + "    _note = data1-64;\n"
                + "    _gate = 1<<27;\n"
                + "    _gate2 = 0;\n"
                + "    np[data1-%startNote%]=p++;\n"
                + "  }\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "if ((data1 >= %startNote%)&&(data1 <= %endNote%)) {\n"
                + "   _rvelo = data2;\n"
                + "np[data1-%startNote%]=0;\n"
                + "int j;\n"
                + "int np2 = 0;\n"
                + "int n2 = 0;\n"
                + "for(j=0;j<%endNote%-%startNote%+1;j++){\n"
                + "   if (np[j]>np2) {\n"
                + "      np2=np[j];\n"
                + "      n2 = j;\n"
                + "   }\n"
                + "}\n"
                + "if (n2>0) _note = n2+%startNote%-64;\n"
                + "else _gate = 0;\n"
                + "}\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "  _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%note%= _note<<21;\n"
                + "%gate%= _gate;\n"
                + "%gate2%= _gate2;\n"
                + "_gate2 = _gate;\n"
                + "%velocity%= _velo<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_keybnote() {
        AxoObject o = new AxoObject("keyb note", "Monophonic MIDI keyboard note input, gate, velocity and release velocity");
        o.outlets.add(new OutletBool32("gate", "key pressed?"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.attributes.add(new AxoAttributeSpinner("note", 0, 127, 64));
        o.sLocalData = "uint8_t _gate;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n";
        o.sInitCode = "_gate = 0;\n";
        o.sMidiCode = "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {"
                + "if (data1 == %note%) {\n"
                + "   _velo = data2;\n"
                + "   _gate = 1;\n"
                + "}\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (data1 == %note%) {\n"
                + "    _rvelo = data2;\n"
                + "    _gate = 0;\n"
                + "  }\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE)&&(data1 == MIDI_C_ALL_NOTES_OFF)) {\n"
                + "  _gate = 0;\n"
                + "}\n";
        o.sKRateCode = "%gate%= _gate<<27;\n"
                + "%velocity%= _velo<<20;\n"
                + "%releaseVelocity%= _rvelo<<20;\n";
        return o;
    }

    static AxoObject Create_bendin() {
        AxoObject o = new AxoObject("bend", "Midi pitch bend input");
        o.outlets.add(new OutletFrac32Bipolar("bend", "pitch bend, -64..64"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.sLocalData = "int32_t _bend;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "_bend = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if (status == MIDI_PITCH_BEND + %midichannel%) {"
                + "  _bend = ((int)((data2<<7)+data1)-0x2000)<<14;\n"
                + "  ntrig = 1;\n"
                + "}";
        o.sKRateCode = "%bend% = _bend;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_bendin2() {
        AxoObject o = new AxoObject("bend hr", "Midi pitch bend input hi res");
        o.sAuthor = "Mark Harris";
        o.outlets.add(new OutletFrac32Bipolar("bend", "pitch bend, -64..64"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.attributes.add(new AxoAttributeSpinner("ccl", 0, 127, 85));
        o.sLocalData = "int32_t _bend,bendl;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "_bend = 0;bendl = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if (status == MIDI_PITCH_BEND + %midichannel%) {"
                + "  _bend = (((int)((data2<<7)+data1)-0x2000)<<14) + bendl;\n"
                + "  ntrig = 1;\n"
                + "} else if ((status == %midichannel% + MIDI_CONTROL_CHANGE) && (data1 == %ccl%)) {\n"
                + "   bendl = data2 << 7;\n"
                + "}\n";
        o.sKRateCode = "%bend% = _bend;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_bendin_ch() {
        AxoObject o = new AxoObject("bend ch", "Midi pitch bend input on specified channel");
        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.outlets.add(new OutletFrac32Bipolar("bend", "pitch bend, -64..64"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.sLocalData = "int32_t _bend;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "_bend = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if (status == MIDI_PITCH_BEND + %channel% - 1) {"
                + "  _bend = ((int)((data2<<7)+data1)-0x2000)<<14;\n"
                + "  ntrig = 1;\n"
                + "}";
        o.sKRateCode = "%bend% = _bend;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_touchin() {
        AxoObject o = new AxoObject("touch", "Midi channel pressure input");
        o.outlets.add(new OutletFrac32Pos("o", "channel pressure, 0..64"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.sLocalData = "int32_t _press;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "_press = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if (status == MIDI_CHANNEL_PRESSURE + %midichannel%) {"
                + "  _press = (data1)<<20;\n"
                + "  ntrig = 1;\n"
                + "}";
        o.sKRateCode = "%o% = _press;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_pgmin() {
        AxoObject o = new AxoObject("pgm", "Midi program change");
        o.outlets.add(new OutletInt32("o", "program"));
        o.outlets.add(new OutletBool32Pulse("trig", "trigger output"));
        o.sLocalData = "int8_t _pgm;\n"
                + "int8_t ntrig;\n";
        o.sInitCode = "_pgm = 0;\n"
                + "ntrig = 0;\n";
        o.sMidiCode = "if (status == MIDI_PROGRAM_CHANGE + %midichannel%) {"
                + "  _pgm = data1;\n"
                + "  ntrig = 1;\n"
                + "}";
        o.sKRateCode = "%o% = _pgm;\n"
                + "%trig% = ntrig;\n"
                + "ntrig = 0;\n";
        return o;
    }

    static AxoObject Create_clockin() {
        AxoObject o = new AxoObject("clock", "Midi clock slave");
        o.outlets.add(new OutletBool32("active", "Song is playing"));
        o.outlets.add(new OutletInt32("pos4ppq", "Position in 4 counts per quarter"));
        o.outlets.add(new OutletInt32("pos24ppq", "Position in 24 counts per quarter"));
        o.sLocalData = "int32_t _active;\n"
                + "int32_t _pos;\n"
                + "int32_t _pos_shadow;\n";
        o.sInitCode = "_active = 0;\n"
                + "_pos = 0;\n"
                + "_pos_shadow = 0;\n";
        o.sMidiCode = "if (status == MIDI_TIMING_CLOCK) {\n"
                + "  _pos_shadow++;\n"
                + "  _pos = _pos_shadow;\n"
                + "} else if (status == MIDI_START) {\n"
                + "  _active = 1;\n"
                + "  _pos = 0;\n"
                + "  _pos_shadow = -1;\n"
                + "} else if (status == MIDI_STOP) {\n"
                + "  _active = 0;\n"
                + "  _pos = -1;\n"
                + "} else if (status == MIDI_CONTINUE) {\n"
                + "  _active = 1;\n"
                + "} else if (status == MIDI_SONG_POSITION) {\n"
                + "  _pos_shadow = 6*((data2<<7)+data1)-1;\n"
                + "}\n";
        o.sKRateCode = "%active% = _active;\n"
                + "%pos4ppq% = _pos/6;\n"
                + "%pos24ppq% = _pos;\n";
        return o;
    }

    static String cdev[] = {"MIDI_DEVICE_DIN, 1",
        "MIDI_DEVICE_USB_HOST, 1",
        "MIDI_DEVICE_USB_HOST, 2",
        "MIDI_DEVICE_USB_HOST, 3",
        "MIDI_DEVICE_USB_HOST, 4",
        "MIDI_DEVICE_INTERNAL, 1",
        "MIDI_DEVICE_INTERNAL, 2",
        "MIDI_DEVICE_USB_DEVICE, 1"};
    static String udev[] = {
        "din",
        "usb host port 1",
        "usb host port 2",
        "usb host port 3",
        "usb host port 4",
        "internal port 1",
        "internal port 2",
        "usb device port 1"
    };

    static AxoObject Create_clockgen() {
        AxoObject o = new AxoObject("clock", "Midi clock master, als outputs Midi clock, start, stop, and continue messages");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));
        o.inlets.add(new InletBool32("run", "Run"));
        o.inlets.add(new InletBool32Rising("rst", "Reset"));
        o.params.add(new ParameterFrac32UMap("bpm"));
        o.outlets.add(new OutletBool32("active", "Song is playing"));
        o.outlets.add(new OutletInt32("pos4ppq", "Position in 4 counts per quarter"));
        o.outlets.add(new OutletInt32("pos24ppq", "Position in 24 counts per quarter"));
        o.sLocalData = "bool _active;\n"
                + "int32_t _posfrac;\n"
                + "int32_t _pos24ppq;\n"
                + "bool rstp;\n";
        o.sInitCode = "_active = 0;\n"
                + "_posfrac = 0;\n"
                + "_pos24ppq = 0;\n"
                + "rstp = 0;";
        o.sKRateCode = ""
                + "if (%rst% & !rstp){\n"
                + "   rstp = 1;\n"
                + "   _posfrac = 0;\n"
                + "   _pos24ppq = 0;\n"
                + "} else if (!%rst%){\n"
                + "   rstp = 0;\n"
                + "}\n"
                + "if (%run% && !_active) {\n"
                + "  _active = 1;\n"
                + "  if (_pos24ppq) "
                + "    MidiSend1((midi_device_t) %device%, MIDI_START);\n"
                + "  else "
                + "    MidiSend1((midi_device_t) %device%, MIDI_CONTINUE);\n"
                + "} else if (!%run% && _active){\n"
                + "  _active = 0;\n"
                + "  MidiSend1((midi_device_t) %device%, MIDI_STOP);\n"
                + "}"
                + "if (_active) {\n"
                + "  _posfrac += %bpm%;\n"
                + "  if (_posfrac & 1<<31) {\n"
                + "    _posfrac &= (1<<31)-1;\n"
                + "    _pos24ppq++;\n"
                + "    MidiSend1((midi_device_t) %device%,MIDI_TIMING_CLOCK);\n"
                + "  }\n"
                + "}\n"
                + "%pos4ppq% = _pos24ppq/6;\n"
                + "%pos24ppq% = _pos24ppq;\n";
        return o;
    }

    static AxoObject Create_noteout() {
        AxoObject o = new AxoObject("note", "Midi note output");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Bipolar("note", "note (-64..63)"));
        o.inlets.add(new InletFrac32Pos("velo", "velocity"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n"
                + "int lastnote;";
        o.sInitCode = "ntrig=0;\n";
        o.sKRateCode = ""
                + "if ((%trig%>0) && !ntrig) {\n"
                + "lastnote = (64+(%note%>>21))&0x7F;\n"
                + "MidiSend3((midi_device_t) %device%, MIDI_NOTE_ON + (%channel%-1),lastnote,%velo%>>20);  ntrig=1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {MidiSend3((midi_device_t) %device%, MIDI_NOTE_OFF + (%channel%-1),lastnote,__USAT(%velo%>>20,7)); ntrig=0;}\n";
        return o;
    }

    static AxoObject Create_ctlout() {
        AxoObject o = new AxoObject("cc", "Midi controller output");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));

        o.inlets.add(new InletFrac32Pos("v", "value"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {MidiSend3((midi_device_t) %device%, MIDI_CONTROL_CHANGE + (%channel%-1),%cc%,__USAT(%v%>>20,7));  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_ctlout_any() {
        AxoObject o = new AxoObject("cc any", "Midi controller output to any CC number and channel");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));

        o.inlets.add(new InletFrac32Pos("v", "value"));
        o.inlets.add(new InletInt32Pos("cc", "midi Continous Controller number 0-127"));
        o.inlets.add(new InletInt32Pos("chan", "channel 1..16"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {MidiSend3((midi_device_t) %device%, MIDI_CONTROL_CHANGE + ((%chan%-1)&0xF),%cc%,__USAT(%v%>>20,7));  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_ctloutauto() {
        AxoObject o = new AxoObject("cc thin", "Midi controller output, automatic thinning");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.inlets.add(new InletFrac32Pos("v", "value"));
//        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int32_t lsend;\n"
                + "int timer;\n";
        o.sInitCode = "timer = 0;\n";
        o.sKRateCode = "if (((lsend>%v%+(1<<19))||(%v%>lsend+(1<<19))) && (timer>30)) {\n"
                + "   lsend = %v%;\n"
                + "   MidiSend3((midi_device_t) %device%, MIDI_CONTROL_CHANGE + (%channel%-1),%cc%,__USAT(%v%>>20,7));\n"
                + "   timer = 0;\n"
                + "} else timer++;\n";
        return o;
    }

    static AxoObject Create_bendout() {
        AxoObject o = new AxoObject("bend", "Midi pitch bend output");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Bipolar("bend", "pitch bend"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {MidiSend3((midi_device_t) %device% , MIDI_PITCH_BEND + (%channel%-1),(%bend%>>14)&0x7F,(%bend%>>21)+64);  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_pgmout() {
        AxoObject o = new AxoObject("pgm", "Midi program change output");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));
        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletInt32Pos("pgm", "prgram number (0-127)"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {MidiSend2((midi_device_t) %device% , MIDI_PROGRAM_CHANGE + (%channel%-1),%pgm%&0x7F); ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_polytouchout() {
        AxoObject o = new AxoObject("poly touch", "Midi poly pressure output");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));
        o.sAuthor = "Mark Harris";

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Bipolar("note", "note (-64..63)"));
        o.inlets.add(new InletFrac32Pos("pressure", "pressure"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n"
                + "int note;";
        o.sInitCode = "note=0;ntrig=0;\n";
        o.sKRateCode = ""
                + "if ((%trig%>0) && !ntrig) {\n"
                + "note = (64+(%note%>>21))&0x7F;\n"
                + "MidiSend3((midi_device_t) %device%, MIDI_POLY_PRESSURE + (%channel%-1),note,%pressure%>>20);  ntrig=1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {ntrig=0;}\n";
        return o;
    }

    static AxoObject Create_channeltouchout() {
        AxoObject o = new AxoObject("channel touch", "Midi channel pressure output");
        o.attributes.add(new AxoAttributeComboBox("device", udev, cdev));
        o.sAuthor = "Mark Harris";

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Pos("pressure", "pressure"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n"
                + "int note;";
        o.sInitCode = "note=0;ntrig=0;\n";
        o.sKRateCode = ""
                + "if ((%trig%>0) && !ntrig) {\n"
                + "MidiSend2((midi_device_t) %device%, MIDI_CHANNEL_PRESSURE + (%channel%-1),%pressure%>>20);  ntrig=1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {ntrig=0;}\n";
        return o;
    }

    static AxoObject Create_midiscript() {
        AxoObject o = new AxoObject("script", "script with 2 outputs, triggered by MIDI input");
        o.outlets.add(new OutletFrac32("out1_", "out1"));
        o.outlets.add(new OutletFrac32("out2_", "out2"));
        o.attributes.add(new AxoAttributeTextEditor("script"));
        o.sLocalData = "int32_t out1,out2;\n";
        o.sInitCode = "out1=0;out2=0;\n";
        o.sMidiCode = "%script%";
        o.sKRateCode = "%out1_% = this->out1;\n"
                + "%out2_% = this->out2;\n";
        return o;
    }

    static AxoObject Create_queuestate() {
        AxoObject o = new AxoObject("queuestate", "Gets the number of pending bytes in the midi output queue. Useful to prevent midi data flooding. Zero at rest.");
        String cdev1[] = {"1", "3", "15"};
        String udev1[] = {"din", "usb host", "internal"};
        o.attributes.add(new AxoAttributeComboBox("device", udev1, cdev1));
        o.outlets.add(new OutletInt32("length", "number of pending bytes in queue"));

        o.sKRateCode = "%length% = MidiGetOutputBufferPending((midi_device_t) %device%);\n";
        return o;
    }

    static AxoObject Create_intern_noteout() {
        AxoObject o = new AxoObject("note", "Midi note output. Sends to midi/in/* objects only.");
        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Bipolar("note", "note (-64..63)"));
        o.inlets.add(new InletFrac32Pos("velo", "velocity"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n"
                + "int lastnote;";
        o.sInitCode = "ntrig=0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {\n"
                + "lastnote = (64+(%note%>>21))&0x7F;\n"
                + "PatchMidiInHandler(MIDI_DEVICE_INTERNAL,0,MIDI_NOTE_ON + (%channel%-1),lastnote,%velo%>>20);  ntrig=1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {PatchMidiInHandler((midi_device_t) 0,0,MIDI_NOTE_OFF + (%channel%-1),lastnote,__USAT(%velo%>>20,7)); ntrig=0;}\n";
        return o;
    }

    static AxoObject Create_intern_ctlout() {
        AxoObject o = new AxoObject("cc", "Midi controller output. Sends to midi/in/* objects only.");
        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.inlets.add(new InletFrac32Pos("v", "value"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_CONTROL_CHANGE + (%channel%-1),%cc%,__USAT(%v%>>20,7));  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_intern_ctlout_any() {
        AxoObject o = new AxoObject("cc any", "Midi controller output to any CC number and channel. Sends to midi/in/* objects only.");
        o.inlets.add(new InletFrac32Pos("v", "value"));
        o.inlets.add(new InletInt32Pos("cc", "midi Continous Controller number 0-127"));
        o.inlets.add(new InletInt32Pos("chan", "channel 1..16"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_CONTROL_CHANGE + ((%chan%-1)&0xF),%cc%,__USAT(%v%>>20,7));  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_intern_ctloutauto() {
        AxoObject o = new AxoObject("cc thin", "Midi controller output, automatic thinning. Sends to midi/in/* objects only.");
        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.attributes.add(new AxoAttributeSpinner("cc", 0, 127, 0));
        o.inlets.add(new InletFrac32Pos("v", "value"));
//        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int32_t lsend;\n"
                + "int timer;\n";
        o.sInitCode = "timer = 0;\n";
        o.sKRateCode = "if (((lsend>%v%+(1<<19))||(%v%>lsend+(1<<19))) && (timer>30)) {\n"
                + "   lsend = %v%;\n"
                + "   PatchMidiInHandler(MIDI_DEVICE_INTERNAL ,0,MIDI_CONTROL_CHANGE + (%channel%-1),%cc%,__USAT(%v%>>20,7));\n"
                + "   timer = 0;\n"
                + "} else timer++;\n";
        return o;
    }

    static AxoObject Create_intern_bendout() {
        AxoObject o = new AxoObject("bend", "Midi pitch bend output. Sends to midi/in/* objects only.");
        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Bipolar("bend", "pitch bend"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_PITCH_BEND + (%channel%-1),(%bend%>>14)&0x7F,(%bend%>>21)+64);  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject Create_intern_clockgen() {
        AxoObject o = new AxoObject("clock", "Midi clock master, als outputs Midi clock, start, stop, and continue messages. Sends to midi/in/* objects only.");
        o.inlets.add(new InletBool32("run", "Run"));
        o.inlets.add(new InletBool32Rising("rst", "Reset"));
        o.params.add(new ParameterFrac32UMap("bpm"));
        o.outlets.add(new OutletBool32("active", "Song is playing"));
        o.outlets.add(new OutletInt32("pos4ppq", "Position in 4 counts per quarter"));
        o.outlets.add(new OutletInt32("pos24ppq", "Position in 24 counts per quarter"));
        o.sLocalData = "bool _active;\n"
                + "int32_t _posfrac;\n"
                + "int32_t _pos24ppq;\n"
                + "bool rstp;\n";
        o.sInitCode = "_active = 0;\n"
                + "_posfrac = 0;\n"
                + "_pos24ppq = 0;\n"
                + "rstp = 0;";
        o.sKRateCode = ""
                + "if (%rst% & !rstp){\n"
                + "   rstp = 1;\n"
                + "   _posfrac = 0;\n"
                + "   _pos24ppq = 0;\n"
                + "} else if (!%rst%){\n"
                + "   rstp = 0;\n"
                + "}\n"
                + "if (%run% && !_active) {\n"
                + "  _active = 1;\n"
                + "  if (_pos24ppq) "
                + "    PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_START,0,0);\n"
                + "  else "
                + "    PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_CONTINUE,0,0);\n"
                + "} else if (!%run% && _active){\n"
                + "  _active = 0;\n"
                + "  PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_STOP,0,0);\n"
                + "}"
                + "if (_active) {\n"
                + "  _posfrac += %bpm%;\n"
                + "  if (_posfrac & 1<<31) {\n"
                + "    _posfrac &= (1<<31)-1;\n"
                + "    _pos24ppq++;\n"
                + "    PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_TIMING_CLOCK,0,0);\n"
                + "  }\n"
                + "}\n"
                + "%pos4ppq% = _pos24ppq/6;\n"
                + "%pos24ppq% = _pos24ppq;\n";
        return o;
    }
    static AxoObject Create_intern_polytouchout() {
        AxoObject o = new AxoObject("poly touch", "Midi poly pressure output");
        o.sAuthor = "Mark Harris";

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Bipolar("note", "note (-64..63)"));
        o.inlets.add(new InletFrac32Pos("pressure", "pressure"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n"
                + "int note;";
        o.sInitCode = "note=0;ntrig=0;\n";
        o.sKRateCode = ""
                + "if ((%trig%>0) && !ntrig) {\n"
                + "note = (64+(%note%>>21))&0x7F;\n"
                + "PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0,MIDI_POLY_PRESSURE + (%channel%-1),note,%pressure%>>20);  ntrig=1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {ntrig=0;}\n";
        return o;
    }

    static AxoObject Create_intern_channeltouchout() {
        AxoObject o = new AxoObject("channel touch", "Midi channel pressure output");
        o.sAuthor = "Mark Harris";

        o.attributes.add(new AxoAttributeSpinner("channel", 1, 16, 0));
        o.inlets.add(new InletFrac32Pos("pressure", "pressure"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n"
                + "int note;";
        o.sInitCode = "note=0;ntrig=0;\n";
        o.sKRateCode = ""
                + "if ((%trig%>0) && !ntrig) {\n"
                + "PatchMidiInHandler(MIDI_DEVICE_INTERNAL, 0, MIDI_CHANNEL_PRESSURE + (%channel%-1),%pressure%>>20,0);  ntrig=1;\n"
                + "}\n"
                + "if (!(%trig%>0) && ntrig) {ntrig=0;}\n";
        return o;
    }
    
    static AxoObject Create_mpe() {
        AxoObject o = new AxoObject("mpe", "Controller input for MIDI Polyphonic Expression");
        o.sAuthor = "Mark Harris";
        o.outlets.add(new OutletFrac32Bipolar("note", "midi note number (-64..63)"));
        o.outlets.add(new OutletBool32("gate", "key pressed, no retrigger legato"));
        o.outlets.add(new OutletBool32("gate2", "key pressed, retrigger on legato"));
        o.outlets.add(new OutletFrac32Pos("velocity", "note-on velocity"));
        o.outlets.add(new OutletFrac32Pos("releaseVelocity", "note-off velocity"));
        o.outlets.add(new OutletFrac32Pos("pressure", "continuous pressure"));
        o.outlets.add(new OutletFrac32("bend", "continuous pitchbend (-64..63)"));
        o.outlets.add(new OutletFrac32("timbre", "continuous timbre (-64..63)"));
        o.outlets.add(new OutletFrac32("pitch", "pitch including pitchbend"));
        o.sLocalData = "\n"
                + "int8_t _note;\n"
                + "int32_t _gate;\n"
                + "int32_t _gate2;\n"
                + "uint8_t _velo;\n"
                + "uint8_t _rvelo;\n"
                + "uint32_t _pressure;\n"
                + "int32_t _bend;\n"
                + "int32_t _timbre;\n"
                + "uint8_t _lastRPN_LSB;\n"
                + "uint8_t _lastRPN_MSB;\n"
                + "uint8_t _bendRange;\n"
                + "int32_t _pitch;\n"
                + "int32_t _xl,_yl,_zl;"
                + "static const uint8_t xccl = 85;"
                + "static const uint8_t yccl = 87;"
                + "static const uint8_t zccl = 86;";

        o.sInitCode = "\n"
                + "_gate = 0;\n"
                + "_note = 0;\n"
                + "_pressure = 0;\n"
                + "_bend = 0;\n"
                + "_timbre = 0;\n"
                + "_bendRange = 48;\n"
                + "_xl=_yl=_zl=0;\n";

        o.sMidiCode = "\n"
                + "if ((status == MIDI_NOTE_ON + %midichannel%) && (data2)) {\n"
                + "  _velo = data2<<20;\n"
                + "  _note = data1-64;\n"
                + "  _gate = 1<<27;\n"
                + "  _gate2 = 0;\n"
                + "  _pitch = (_note << 21) + ((_bend >> 6)* _bendRange );\n"
                + "} else if (((status == MIDI_NOTE_ON + %midichannel%) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + %midichannel%)) {\n"
                + "  if (_note == data1-64) {\n"
                + "    _rvelo = data2<<20;\n"
                + "    _gate = 0;\n"
                + "    _pressure = 0;\n"
                + "  }\n"
                + "} else if (status == %midichannel% + MIDI_CHANNEL_PRESSURE) {\n"
                + "  _pressure = (data1<<20) + _zl;\n"
                + "} else if (status == %midichannel% + MIDI_PITCH_BEND) {\n"
                + "  _bend = (((int)((data2<<7)+data1)-0x2000)<<14) + _xl;\n"
                + "  _pitch = (_note << 21) + ((_bend >> 6)* _bendRange );\n"
                + "} else if (status == %midichannel% + MIDI_CONTROL_CHANGE) {\n"
                + "  if (data1 == MIDI_C_TIMBRE) {\n"
                + "    _timbre = (((int)(data2<<7)-0x2000)<<14) + _yl;\n"
                + "  } else if (data1 == zccl) {\n"
                + "   _zl = data2 << 13;"
                + "  } else if (data1 == xccl) {\n"
                + "   _xl = data2 << 7;"
                + "  } else if (data1 == yccl) {\n"
                + "   _yl = data2 << 14;"
                + "  } else if(data1 == MIDI_C_ALL_NOTES_OFF) {\n"
                + "    _gate = 0;\n"
                + "  } else if (data1 == MIDI_C_RPN_MSB || data1 == MIDI_C_RPN_LSB || data1 == MIDI_C_DATA_ENTRY) {\n"
                + "    switch(data1) {\n"
                + "         case MIDI_C_RPN_LSB: _lastRPN_LSB = data2; break;\n"
                + "         case MIDI_C_RPN_MSB: _lastRPN_MSB = data2; break;\n"
                + "         case MIDI_C_DATA_ENTRY: {\n"
                + "              if (_lastRPN_LSB == 0 && _lastRPN_MSB == 0) {\n"
                + "                _bendRange = data2;\n"
                + "              }\n"
                + "            }\n"
                + "            break;\n"
                + "        default: break;\n"
                + "    }\n"
                + "  }\n"
                + "}\n";

        o.sKRateCode = "\n"
                + "outlet_note= _note<<21;\n"
                + "outlet_gate= _gate;\n"
                + "outlet_gate2= _gate2;\n"
                + "_gate2 = _gate;\n"
                + "outlet_velocity= _velo;\n"
                + "outlet_releaseVelocity= _rvelo;\n"
                + "outlet_pressure = _pressure;\n"
                + "outlet_bend = _bend;\n"
                + "outlet_timbre = _timbre;\n"
                + "outlet_pitch = _pitch;\n";

        return o;
    }
}
