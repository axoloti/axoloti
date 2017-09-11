package midirouting;

import axoloti.CConnection;
import axoloti.IConnection;
import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.mvc.AbstractModel;

/**
 *
 * @author jtaelman
 */
public class MidiRoutingTables extends AbstractModel {

    MidiInputRoutingTable[] inputRoutingTables;
    public MidiInputRoutingTableController[] inputRoutingTablesController;
    MidiOutputRoutingTable[] outputRoutingTables;
    public MidiOutputRoutingTableController[] outputRoutingTablesController;

    public void readFromTarget() {
        IConnection conn = CConnection.GetConnection();
        ChunkData chunk_input = conn.GetFWChunks().GetOne(FourCCs.FW_MIDI_INPUT_ROUTING);
        chunk_input.data.rewind();
        int n_input_interfaces = chunk_input.data.remaining() / 4;
        MidiInputRoutingTable[] cirs = new MidiInputRoutingTable[n_input_interfaces];
        for (int i = 0; i < n_input_interfaces; i++) {
            cirs[i] = new MidiInputRoutingTable();
            int addr = chunk_input.data.getInt();
            cirs[i].retrieve(conn, addr);
        }
        setInputRoutingTable(cirs);

        ChunkData chunk_output = conn.GetFWChunks().GetOne(FourCCs.FW_MIDI_OUTPUT_ROUTING);
        chunk_output.data.rewind();
        int n_output_interfaces = chunk_output.data.remaining() / 4;
        MidiOutputRoutingTable[] cors = new MidiOutputRoutingTable[n_output_interfaces];
        for (int i = 0; i < n_output_interfaces; i++) {
            cors[i] = new MidiOutputRoutingTable();
            int addr = chunk_output.data.getInt();
            cors[i].retrieve(conn, addr);
        }
        setOutputRoutingTable(cors);
    }

    public void applyToTarget() {
        IConnection conn = CConnection.GetConnection();
        for (MidiInputRoutingTable mirt : inputRoutingTables) {
            mirt.apply(conn);
        }
        for (MidiOutputRoutingTable mort : outputRoutingTables) {
            mort.apply(conn);
        }
    }


    public final static String MRTS_INPUT = "InputRoutingTables";
    public final static String MRTS_OUTPUT = "OutputRoutingTable";

    public final static String[] PROPERTYNAMES = new String[]{
        MRTS_INPUT,
        MRTS_OUTPUT
    };

    @Override
    public String[] getPropertyNames() {
        return PROPERTYNAMES;
    }

    public MidiInputRoutingTable[] getInputRoutingTables() {
        return inputRoutingTables;
    }

    public void setInputRoutingTable(MidiInputRoutingTable[] routingTable) {
        this.inputRoutingTables = routingTable;
        if (routingTable != null) {
            inputRoutingTablesController = new MidiInputRoutingTableController[routingTable.length];
            for (int i = 0; i < routingTable.length; i++) {
                MidiInputRoutingTable mirt = routingTable[i];
                inputRoutingTablesController[i] = new MidiInputRoutingTableController(mirt, null, null);
            }
        } else {
            inputRoutingTablesController = null;
        }
        firePropertyChange(MRTS_INPUT,
                null, routingTable);
    }

    public MidiOutputRoutingTable[] getOutputRoutingTable() {
        return outputRoutingTables;
    }

    public void setOutputRoutingTable(MidiOutputRoutingTable[] routingTable) {
        this.outputRoutingTables = routingTable;
        if (routingTable != null) {
            outputRoutingTablesController = new MidiOutputRoutingTableController[routingTable.length];
            for (int i = 0; i < routingTable.length; i++) {
                MidiOutputRoutingTable mirt = routingTable[i];
                outputRoutingTablesController[i] = new MidiOutputRoutingTableController(mirt, null, null);
            }
        } else {
            inputRoutingTablesController = null;
        }
        firePropertyChange(MRTS_OUTPUT,
                null, routingTable);
    }

}
