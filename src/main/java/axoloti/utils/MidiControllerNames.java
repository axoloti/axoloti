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
package axoloti.utils;

/**
 *
 * @author Johannes Taelman
 */
public class MidiControllerNames {

    static String[] ccnames = {
        "Bank select", // 0
        "Modulation",
        "Breath",
        "",
        "Foot",
        "Portamento time",
        "Data entry",
        "Main volume",
        "Balance", // 8
        "",
        "Pan", // 10
        "Expression", // 11
        "FX 1",
        "FX 2",
        "",
        "",
        "General 1", // 16
        "General 2", // 17
        "General 3", // 18
        "General 4", // 19
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "", // 32
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "", // 48
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "Sustain", // 64
        "Portamento",
        "Sostenuto",
        "Soft",
        "Legato",
        "Hold 2",
        "Controller 1",
        "Controller 2",
        "Controller 3",
        "Controller 4",
        "Controller 5",
        "Controller 6",
        "Controller 7",
        "Controller 8",
        "Controller 9",
        "Controller 10",
        "Switch 1",
        "Switch 2",
        "Switch 3",
        "Switch 4",
        "Portamento amt",
        "",
        "",
        "",
        "",
        "",
        "",
        "Effect 1 Depth",
        "Effect 2 Depth",
        "Effect 3 Depth",
        "Effect 4 Depth",
        "Effect 5 Depth",
        "Data inc", // 96
        "Data dec",
        "NRPN LSB",
        "NRPN MSB",
        "RPN LSB",
        "RPN MSB",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "All Sound Off",
        "Reset Controllers",
        "Local on/off",
        "All Notes Off",
        "Omni off",
        "Omni on",
        "Monophonic",
        "Polyphonic"
    };

    public static String GetNameFromCC(int cc) {
        if ((cc > 0) && (cc < 128)) {
            return ccnames[cc];
        } else {
            return "";
        }
    }
}
