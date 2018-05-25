package axoloti.target.midimonitor;

import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.connection.IConnection;
import axoloti.target.TargetModel;
import java.nio.ByteBuffer;
import qcmds.QCmdMemRead;

/**
 *
 * @author jtaelman
 */
public class MidiMonitorData {

    public final MidiMessage msgs[];
    public final int readIndex;

    public MidiMonitorData() {
        MidiMessage msgs1[] = new MidiMessage[32];
        for (int i = 0; i < msgs1.length; i++) {
            msgs1[i] = new MidiMessage();
        }
        msgs = msgs1;
        readIndex = 0;
    }

    public MidiMonitorData(MidiMessage[] msgs, int readIndex) {
        this.msgs = msgs;
        this.readIndex = readIndex;
    }

    public static void refresh(IConnection conn) {
        int length = 256;
        ChunkData chunk_midibuff = conn.getFWChunks().getOne(FourCCs.FW_MIDI_INPUT_BUFFER);
        ByteBuffer data = chunk_midibuff.getData();
        int addr = data.getInt();
        conn.appendToQueue(new QCmdMemRead(addr, length, new IConnection.MemReadHandler() {
            @Override
            public void done(ByteBuffer mem) {
                if (mem != null) {
                    int readIndex = mem.getInt();
                    int writeIndex = mem.getInt();
                    MidiMessage msgs1[] = new MidiMessage[32];
                    for (int i = 0; i < msgs1.length; i++) {
                        msgs1[i] = new MidiMessage(mem);
                    }
                    TargetModel.getTargetModel().setMidiMonitor(new MidiMonitorData(msgs1, readIndex));
                    //msgs = msgs1;
                }
            }
        }));
    }
}
