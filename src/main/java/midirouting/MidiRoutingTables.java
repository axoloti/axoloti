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
    MidiOutputRoutingTable outputRoutingTable = new MidiOutputRoutingTable();
    public MidiOutputRoutingTableController outputRoutingTableController = new MidiOutputRoutingTableController(outputRoutingTable, null, null);

    public void readFromTarget() {
        IConnection conn = CConnection.GetConnection();
        ChunkData chunk_input = conn.GetFWChunks().GetOne(FourCCs.FW_MIDI_INPUT_ROUTING);
        chunk_input.data.rewind();
        int n_interfaces = chunk_input.data.remaining()/4;
        MidiInputRoutingTable[] cirs = new MidiInputRoutingTable[n_interfaces];
        for (int i = 0; i < n_interfaces; i++) {
            cirs[i] = new MidiInputRoutingTable();
            int addr = chunk_input.data.getInt();
            cirs[i].retrieve(conn, addr);
        }
        setInputRoutingTable(cirs);
        outputRoutingTable.readOutputMapping();
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
        firePropertyChange(MidiRoutingTablesController.MRTS_INPUT,
                null, routingTable);
    }

    public MidiOutputRoutingTable getOutputRoutingTable() {
        return outputRoutingTable;
    }

    public void setOutputRoutingTable(MidiOutputRoutingTable routingTable) {
        this.outputRoutingTable = routingTable;
        if (routingTable != null) {
            outputRoutingTableController = new MidiOutputRoutingTableController(routingTable, null, null);
        } else {
            inputRoutingTablesController = null;
        }
        firePropertyChange(MidiRoutingTablesController.MRTS_OUTPUT,
                null, routingTable);
    }

}
