package axoloti.target.midimonitor;

import axoloti.utils.MidiControllerNames;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
public class MidiMessage {

    private final byte b0;
    private final byte b1;
    private final byte b2;
    private final byte ph;

    public MidiMessage() {
        ph = 0;
        b0 = 0;
        b1 = 0;
        b2 = 0;
    }

    public MidiMessage(ByteBuffer bb) {
        ph = bb.get();
        b0 = bb.get();
        b1 = bb.get();
        b2 = bb.get();
    }

    public byte getB0() {
        return b0;
    }

    public byte getB1() {
        return b1;
    }

    public byte getB2() {
        return b2;
    }

    public byte getPh() {
        return ph;
    }

    private static final String midiData[][] = {
        {"Note Off", "note", "velocity"},
        {"Note On", "note", "velocity"},
        {"Key Pressure", "note", "pressure"},
        {"Control Change", "number", "value"},
        {"Program Change", "number", null},
        {"Channel Pressure", "pressure", null},
        {"Pitch Bend", "lsb", "msb"}
    };

    private static final String midiRTNames[] = {
        "System Exclusive",
        "MIDI Time Code Quarter Frame",
        "Song Position Pointer",
        "Song Select",
        "Undefined (F4)",
        "Undefined (F5)",
        "Tune Request",
        "End of Exclusive",
        "Timing Clock",
        "Undefined (F9)",
        "Start",
        "Continue",
        "Stop",
        "Undefined (FD)",
        "Active Sensing",
        "Reset"};

    final static int CIN_0_RESERVED = 0;
    final static int CIN_1_RESERVED = 1;
    final static int CIN_2_2BYTE_SYSTEM_COMMON = 2;
    final static int CIN_3_3BYTE_SYSTEM_COMMON = 3;
    final static int CIN_4_SYSEX_START_OR_CONTINUE = 4;
    final static int CIN_5_SYSEX_END_1BYTE = 5;
    final static int CIN_6_SYSEX_END_2BYTE = 6;
    final static int CIN_7_SYSEX_END_3BYTE = 7;
    final static int CIN_8_NOTEOFF = 8;
    final static int CIN_9_NOTEON = 9;
    final static int CIN_A_KEYPRESSURE = 0xA;
    final static int CIN_B_CONTROLCHANGE = 0xB;
    final static int CIN_C_PROGRAMCHANGE = 0xC;
    final static int CIN_D_CHANNELPRESSURE = 0xD;
    final static int CIN_E_PITCHBEND = 0xE;
    final static int CIN_F_SINGLE_BYTE = 0xF;

    public int getNumDataBytes() {
        switch (getCin()) {
            case CIN_0_RESERVED:
                return 3;
            case CIN_1_RESERVED:
                return 3;
            case CIN_2_2BYTE_SYSTEM_COMMON:
                return 2;
            case CIN_3_3BYTE_SYSTEM_COMMON:
                return 3;
            case CIN_4_SYSEX_START_OR_CONTINUE:
                return 3;
            case CIN_5_SYSEX_END_1BYTE:
                return 1;
            case CIN_6_SYSEX_END_2BYTE:
                return 2;
            case CIN_7_SYSEX_END_3BYTE:
                return 3;
            case CIN_8_NOTEOFF:
                return 3;
            case CIN_9_NOTEON:
                return 3;
            case CIN_A_KEYPRESSURE:
                return 3;
            case CIN_B_CONTROLCHANGE:
                return 3;
            case CIN_C_PROGRAMCHANGE:
                return 2;
            case CIN_D_CHANNELPRESSURE:
                return 2;
            case CIN_E_PITCHBEND:
                return 3;
            case CIN_F_SINGLE_BYTE:
                return 1;
        }
        return 0;
    }

    public int getPortNumber() {
        return (ph & 0xF0) >> 4;
    }

    public int getChannelNumber() {
        return b0 & 0x0F;
    }

    public int getCin() {
        return ph & 0x0F;
    }

    public String getEventName() {
        switch (getCin()) {
            case CIN_0_RESERVED:
                return "Reserved";
            case CIN_1_RESERVED:
                return "Reserved";
            case CIN_2_2BYTE_SYSTEM_COMMON:
            case CIN_3_3BYTE_SYSTEM_COMMON:
                return midiRTNames[((0xFF & b0) - 0xF0)];
            case CIN_4_SYSEX_START_OR_CONTINUE:
                return "SysEx start/cont";
            case CIN_5_SYSEX_END_1BYTE:
                return "SysEx end";
            case CIN_6_SYSEX_END_2BYTE:
                return "SysEx end";
            case CIN_7_SYSEX_END_3BYTE:
                return "SysEx end";
            case CIN_8_NOTEOFF:
                return String.format("NoteOff note=%3d velo=%3d", b1 & 0xFF, b2 & 0xFF);
            case CIN_9_NOTEON:
                return String.format("NoteOn  note=%3d velo=%3d", b1 & 0xFF, b2 & 0xFF);
            case CIN_A_KEYPRESSURE:
                return String.format("KeyPres note=%3d pres=%3d", b1 & 0xFF, b2 & 0xFF);
            case CIN_B_CONTROLCHANGE:
                return String.format("CtrlChng  cc=%3d val =%3d (%s)", b1 & 0xFF, b2 & 0xFF, MidiControllerNames.getNameFromCC(b1));
            case CIN_C_PROGRAMCHANGE:
                return "PgmChng pgm=" + (b1 & 0xFF);
            case CIN_D_CHANNELPRESSURE:
                return "ChanPres val=" + (b1 & 0xFF);
            case CIN_E_PITCHBEND:
                return String.format("Bend     val= %5d", ((b1 & 0xFF) + ((b2 & 0xFF) << 7) - (1 << 13)));
            case CIN_F_SINGLE_BYTE:
                return midiRTNames[((0xFF & b0) - 0xF0)];
        }
        return "?";
    }

    String getFirstMidiDataByteName() {
        switch (getCin()) {
            case 4:
                return "";
            case 6:
                return "";
            case 7:
                return "";
        }
        if (b0 == 0) {
            return "";
        } else if ((b0 & 0xF0) < 0xF0) {
            return midiData[((b0 & 0xF0) >> 4) - 8][1];
        } else {
            return midiRTNames[((0xFF & b0) - 0xF0)];
        }
    }

    String getSecondMidiDataByteName() {
        switch (getCin()) {
            case 4:
                return "";
            case 6:
                return "";
            case 7:
                return "";
        }
        if (b0 == 0) {
            return "";
        } else if ((b0 & 0xF0) < 0xF0) {
            if (midiData[((b0 & 0xF0) >> 4) - 8][2] == null) {
                return "null";
            } else {
                if (((b0 & 0xF0) >> 4) == 0x0B) {
                    return MidiControllerNames.getNameFromCC(b1) + ", " + midiData[((b0 & 0xF0) >> 4) - 8][2];
                } else {
                    return midiData[((b0 & 0xF0) >> 4) - 8][2];
                }
            }
        } else {
            return midiRTNames[((0xFF & b0) - 0xF0)];
        }
    }

}
