package axoloti;

import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.mvc.AbstractModel;
import axoloti.property.BooleanProperty;
import axoloti.property.IntegerProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.utils.FirmwareID;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import midirouting.MidiInputRoutingTable;
import midirouting.MidiOutputRoutingTable;
import qcmds.QCmdProcessor;
import qcmds.QCmdStartFlasher;
import qcmds.QCmdStop;
import qcmds.QCmdUploadFWSDRam;
import qcmds.QCmdUploadPatch;

/**
 *
 * @author jtaelman
 */
public class TargetModel extends AbstractModel {

    private static TargetModel targetModel;
    private static TargetController targetController;

    public static TargetController getTargetController() {
        if (targetModel == null) {
            targetModel = new TargetModel();
            targetController = new TargetController(targetModel, null, null);
        }
        return targetController;
    }

    public TargetModel() {
        updateLinkFirmwareID();
    }

    IConnection connection;

//    QCmdProcessor qCmdProcessor = QCmdProcessor.getQCmdProcessor();

    String linkFirmwareID;
    MidiInputRoutingTable[] inputRoutingTables;
    MidiOutputRoutingTable[] outputRoutingTables;
    boolean sDCardMounted;
    TargetRTInfo RTInfo;
    int patchIndex;

    public void readFromTarget() {
        ChunkData chunk_input = connection.GetFWChunks().GetOne(FourCCs.FW_MIDI_INPUT_ROUTING);
        chunk_input.data.rewind();
        int n_input_interfaces = chunk_input.data.remaining() / 4;
        MidiInputRoutingTable[] cirs = new MidiInputRoutingTable[n_input_interfaces];
        for (int i = 0; i < n_input_interfaces; i++) {
            cirs[i] = new MidiInputRoutingTable();
            int addr = chunk_input.data.getInt();
            cirs[i].retrieve(connection, addr);
        }

        try {
            QCmdProcessor.getQCmdProcessor().WaitQueueFinished();
            setInputRoutingTable(cirs);
        } catch (Exception ex) {
            Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        ChunkData chunk_output = connection.GetFWChunks().GetOne(FourCCs.FW_MIDI_OUTPUT_ROUTING);
        chunk_output.data.rewind();
        int n_output_interfaces = chunk_output.data.remaining() / 4;
        MidiOutputRoutingTable[] cors = new MidiOutputRoutingTable[n_output_interfaces];
        for (int i = 0; i < n_output_interfaces; i++) {
            cors[i] = new MidiOutputRoutingTable();
            int addr = chunk_output.data.getInt();
            cors[i].retrieve(connection, addr);
        }

        try {
            QCmdProcessor.getQCmdProcessor().WaitQueueFinished();
            setOutputRoutingTable(cors);
        } catch (Exception ex) {
            Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void applyToTarget() {
        for (MidiInputRoutingTable mirt : inputRoutingTables) {
            mirt.apply(connection);
        }
        for (MidiOutputRoutingTable mort : outputRoutingTables) {
            mort.apply(connection);
        }
    }

    public final static Property CONNECTION = new ObjectProperty("Connection", IConnection.class, TargetModel.class);
    public final static Property FIRMWARE_LINK_ID = new StringProperty("FirmwareLinkID", TargetModel.class);
    public final static Property MRTS_INPUT = new ObjectProperty("InputRoutingTable", MidiInputRoutingTable[].class, TargetModel.class);
    public final static Property MRTS_OUTPUT = new ObjectProperty("OutputRoutingTable", MidiOutputRoutingTable[].class, TargetModel.class);
    public final static Property HAS_SDCARD = new BooleanProperty("SDCardMounted", TargetModel.class);
    public final static Property RTINFO = new ObjectProperty("RTInfo", TargetRTInfo.class, TargetModel.class);
    public final static Property PATCHINDEX = new IntegerProperty("PatchIndex", TargetModel.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(CONNECTION);
        l.add(FIRMWARE_LINK_ID);
        l.add(MRTS_INPUT);
        l.add(MRTS_OUTPUT);
        l.add(RTINFO);
        l.add(PATCHINDEX);
        return l;
    }

    public MidiInputRoutingTable[] getInputRoutingTable() {
        return inputRoutingTables;
    }

    public void setInputRoutingTable(MidiInputRoutingTable[] routingTable) {
        this.inputRoutingTables = routingTable;
        firePropertyChange(MRTS_INPUT,
                null, routingTable);
    }

    public MidiOutputRoutingTable[] getOutputRoutingTable() {
        return outputRoutingTables;
    }

    public void setOutputRoutingTable(MidiOutputRoutingTable[] routingTable) {
        this.outputRoutingTables = routingTable;
        firePropertyChange(MRTS_OUTPUT,
                null, routingTable);
    }

    public IConnection getConnection() {
        return connection;
    }

    public void setConnection(IConnection connection) {
        this.connection = connection;
        firePropertyChange(CONNECTION,
                null, connection);
    }

    public String getFirmwareLinkID() {
        return linkFirmwareID;
    }

    public void setFirmwareLinkID(String linkFirmwareID) {
        this.linkFirmwareID = linkFirmwareID;
        firePropertyChange(FIRMWARE_LINK_ID,
                null, linkFirmwareID);
    }

    public void updateLinkFirmwareID() {
        setFirmwareLinkID(FirmwareID.getFirmwareID());
    }

    public void flashUsingSDRam(String fname_flasher, String pname) {
        updateLinkFirmwareID();
        File p = new File(pname);
        if (p.canRead()) {
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdStop());
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdUploadFWSDRam(p));
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdUploadPatch(fname_flasher));
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdStartFlasher());
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "can''t read firmware, please compile firmware! (file: {0} )", pname);
        }
    }

    public Boolean getSDCardMounted() {
        return sDCardMounted;
    }

    public void setSDCardMounted(Boolean SDCardMounted) {
        this.sDCardMounted = SDCardMounted;
        firePropertyChange(HAS_SDCARD,
                null, SDCardMounted);
    }

    public TargetRTInfo getRTInfo() {
        return RTInfo;
    }

    public void setRTInfo(TargetRTInfo RTInfo) {
        this.RTInfo = RTInfo;
        firePropertyChange(RTINFO,
                null, RTInfo);
    }

    public int getPatchIndex() {
        return patchIndex;
    }

    public void setPatchIndex(Integer patchIndex) {
        this.patchIndex = patchIndex;
        firePropertyChange(PATCHINDEX, null, this.patchIndex);
    }

}
