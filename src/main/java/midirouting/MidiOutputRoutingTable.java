package midirouting;

import axoloti.CConnection;
import axoloti.IConnection;
import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.mvc.AbstractModel;
import java.nio.ByteBuffer;
import qcmds.QCmdMemRead;

/**
 *
 * @author jtaelman
 */
public class MidiOutputRoutingTable extends AbstractModel {

    public class MidiDestination {
        public int device;
        public int port;
    }

    public class MidiDestinations {

        public MidiDestination destinations[];
    }

    MidiDestinations vports[];

    void readOutputMapping() {
        IConnection conn = CConnection.GetConnection();
        if (conn.isConnected()) {
            ChunkData chunk_output = conn.GetFWChunks().GetOne(FourCCs.FW_MIDI_OUTPUT_ROUTING);
            chunk_output.data.rewind();
            int ntargets = chunk_output.data.getInt();
            int table_addr = chunk_output.data.getInt();
            conn.AppendToQueue(new QCmdMemRead(table_addr, ntargets * 16 * 2, new IConnection.MemReadHandler() {
                @Override
                public void Done(ByteBuffer mem) {
                    if (mem == null) {
                        return;
                    }
                    MidiDestinations vports1[] = new MidiDestinations[16];
                    for(int i=0;i<16;i++) {
                        vports1[i] = new MidiDestinations();
                    }
                    for (MidiDestinations vport : vports1) {
                        vport.destinations = new MidiDestination[4];
                        for(int i=0;i<4;i++) {
                            vport.destinations[i] = new MidiDestination();
                        }                        
                        for (MidiDestination d : vport.destinations) {
                            d.device = mem.get();
                            d.port = mem.get();
                        }
                    }
                    setVPorts(vports1);
                }
            }));
        } else {
            setVPorts(null);
        }
    }

    public MidiDestinations[] getVPorts() {
        return vports;
    }

    public void setVPorts(MidiDestinations[] vports) {
        this.vports = vports;
        firePropertyChange(
                MidiOutputRoutingTableController.MORT_VPORTS,
                null, vports);
    }

}
