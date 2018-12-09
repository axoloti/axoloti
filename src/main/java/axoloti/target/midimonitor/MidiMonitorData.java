package axoloti.target.midimonitor;

import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.connection.IConnection;
import axoloti.target.TargetModel;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

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
        try {
            int length = 256;
            ChunkData chunk_midibuff = conn.getFWChunks().getOne(FourCCs.FW_MIDI_INPUT_BUFFER);
            ByteBuffer data = chunk_midibuff.getData();
            int addr = data.getInt();

            ByteBuffer mem = conn.read(addr, length);
            if (mem != null) {
                int readIndex = mem.getInt();
                int writeIndex = mem.getInt();
                MidiMessage msgs1[] = new MidiMessage[32];
                for (int i = 0; i < msgs1.length; i++) {
                    msgs1[i] = new MidiMessage(mem);
                }
                MidiMonitorData midiMonitorData = new MidiMonitorData(msgs1, readIndex);
                SwingUtilities.invokeAndWait(() -> {
                    TargetModel.getTargetModel().setMidiMonitor(midiMonitorData);
                });
            }
        } catch (IOException ex) {
            Logger.getLogger(MidiMonitorData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MidiMonitorData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MidiMonitorData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
