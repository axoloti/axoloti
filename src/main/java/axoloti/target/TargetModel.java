package axoloti.target;

import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.connection.ConnectionStatusListener;
import axoloti.connection.IConnection;
import axoloti.connection.ILivePatch;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.property.BooleanProperty;
import axoloti.property.ListProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.target.fs.SDCardMountStatusListener;
import axoloti.target.midimonitor.MidiMonitorData;
import axoloti.target.midirouting.MidiInputRoutingTable;
import axoloti.target.midirouting.MidiOutputRoutingTable;
import axoloti.utils.FirmwareID;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class TargetModel extends AbstractModel {

    private static TargetModel targetModel;

    public static TargetModel getTargetModel() {
        // TODO: remove TargetModel singleton
        if (targetModel == null) {
            targetModel = new TargetModel();
        }
        return targetModel;
    }

    public TargetModel() {
        updateLinkFirmwareID();
    }

    private IConnection connection;

    private String linkFirmwareID;
    private List<MidiInputRoutingTable> inputRoutingTables = Collections.emptyList();
    private List<MidiOutputRoutingTable> outputRoutingTables = Collections.emptyList();
    private boolean sDCardMounted;
    private TargetRTInfo RTInfo;
    private MidiMonitorData midiMonitor;
    private List<ILivePatch> patchList;
    public boolean warnedAboutFWCRCMismatch = false;

    void readInputMapFromTarget() throws IOException {
        ChunkData chunk_input = connection.getFWChunks().getOne(FourCCs.FW_MIDI_INPUT_ROUTING);
        ByteBuffer data = chunk_input.getData();
        int n_input_interfaces = data.remaining() / 4;
        List<MidiInputRoutingTable> cirs = new ArrayList<>(n_input_interfaces);
        for (int i = 0; i < n_input_interfaces; i++) {
            int addr = data.getInt();
            MidiInputRoutingTable mirt = new MidiInputRoutingTable(addr);
            mirt.retrieve(connection);
            cirs.add(mirt);
        }
        setInputRoutingTable(cirs);
    }

    void readOutputMapFromTarget() throws IOException {
        ChunkData chunk_output = connection.getFWChunks().getOne(FourCCs.FW_MIDI_OUTPUT_ROUTING);
        ByteBuffer data = chunk_output.getData();
        int n_output_interfaces = data.remaining() / 4;
        List<MidiOutputRoutingTable> cors = new ArrayList<>(n_output_interfaces);
        for (int i = 0; i < n_output_interfaces; i++) {
            int addr = data.getInt();
            MidiOutputRoutingTable mort = new MidiOutputRoutingTable(addr);
            mort.retrieve(connection);
            cors.add(mort);
        }
        setOutputRoutingTable(cors);
    }

    public void readFromTarget() {
        try {
            readInputMapFromTarget();
            readOutputMapFromTarget();
        } catch (IOException ex) {
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
    public final static Property MRTS_INPUT = new ListProperty("InputRoutingTable", TargetModel.class);
    public final static Property MRTS_OUTPUT = new ListProperty("OutputRoutingTable", TargetModel.class);
    public final static Property HAS_SDCARD = new BooleanProperty("SDCardMounted", TargetModel.class);
    public final static Property RTINFO = new ObjectProperty("RTInfo", TargetRTInfo.class, TargetModel.class);
    public final static Property WARNEDABOUTFWCRCMISMATCH = new BooleanProperty("WarnedAboutFWCRCMismatch", TargetModel.class);
    public final static Property MIDIMONITOR = new ObjectProperty("MidiMonitor", MidiMonitorData.class, TargetModel.class);
    public final static Property PATCHLIST = new ListProperty("PatchList", TargetModel.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(CONNECTION);
        l.add(FIRMWARE_LINK_ID);
        l.add(MRTS_INPUT);
        l.add(MRTS_OUTPUT);
        l.add(RTINFO);
        l.add(PATCHLIST);
        return l;
    }

    public List<MidiInputRoutingTable> getInputRoutingTable() {
        return Collections.unmodifiableList(inputRoutingTables);
    }

    public void setInputRoutingTable(List<MidiInputRoutingTable> routingTable) {
        this.inputRoutingTables = routingTable;
        firePropertyChange(MRTS_INPUT,
                null, routingTable);
    }

    public List<MidiOutputRoutingTable> getOutputRoutingTable() {
        return Collections.unmodifiableList(outputRoutingTables);
    }

    public void setOutputRoutingTable(List<MidiOutputRoutingTable> routingTable) {
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

    public final void updateLinkFirmwareID() {
        setFirmwareLinkID(FirmwareID.getFirmwareID());
    }

    public void flashUsingSDRam(String fname_fw) throws IOException {
        Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "Firmware flashing in progress, do not unplug the board until the leds stop blinking! You can connect again after the leds stop blinking.");
        updateLinkFirmwareID();
        File p = new File(fname_fw);
        byte[] fwImage = Files.readAllBytes(p.toPath());
        connection.uploadFirmware(fwImage);
    }

    public Boolean getSDCardMounted() {
        return sDCardMounted;
    }

    public void setSDCardMounted(Boolean SDCardMounted) {
        this.sDCardMounted = SDCardMounted;
        firePropertyChange(HAS_SDCARD,
                null, SDCardMounted);
        if (SDCardMounted) {
            for (SDCardMountStatusListener sdcml : sdcmls) {
                sdcml.showSDCardMounted();
            }
        } else {
            for (SDCardMountStatusListener sdcml : sdcmls) {
                sdcml.showSDCardUnmounted();
            }
        }
    }

    public TargetRTInfo getRTInfo() {
        return RTInfo;
    }

    public void setRTInfo(TargetRTInfo RTInfo) {
        this.RTInfo = RTInfo;
        firePropertyChange(RTINFO,
                null, RTInfo);
    }

    public Boolean getWarnedAboutFWCRCMismatch() {
        return warnedAboutFWCRCMismatch;
    }

    public void setWarnedAboutFWCRCMismatch(Boolean WarnedAboutFWCRCMismatch) {
        this.warnedAboutFWCRCMismatch = WarnedAboutFWCRCMismatch;
        firePropertyChange(WARNEDABOUTFWCRCMISMATCH, null, WarnedAboutFWCRCMismatch);
    }

    public MidiMonitorData getMidiMonitor() {
        return midiMonitor;
    }

    public void setMidiMonitor(MidiMonitorData midiMonitor) {
        this.midiMonitor = midiMonitor;
        firePropertyChange(MIDIMONITOR, null, midiMonitor);
    }

    public List getPatchList() {
        return patchList;
    }

    public void setPatchList(List<ILivePatch> patchRefs) {
        if (patchRefs == null) {
            patchRefs = Collections.emptyList();
        }
        this.patchList = patchRefs;
        firePropertyChange(PATCHLIST, null, patchRefs);
    }

    private final List<Runnable> pollers = new LinkedList<>();

    public List<Runnable> getPollers() {
        return Collections.unmodifiableList(pollers);
    }

    public void addPoller(Runnable poller) {
        if (!pollers.contains(poller)) {
            pollers.add(poller);
        }
    }

    public void removePoller(Runnable poller) {
        pollers.remove(poller);
    }

    public void removeAllPollers() {
        pollers.clear();
    }

    @Override
    protected TargetController createController() {
        return new TargetController(this);
    }

    @Override
    public IModel getParent() {
        return null;
    }

    private final List<ConnectionStatusListener> csls = new LinkedList<>();

    public void addConnectionStatusListener(ConnectionStatusListener csl) {
        if ((connection != null) && connection.isConnected()) {
            csl.showConnect();
        } else {
            csl.showDisconnect();
        }
        csls.add(csl);
    }

    public void removeConnectionStatusListener(ConnectionStatusListener csl) {
        csls.remove(csl);
    }

    public void showDisconnect() {
        for (ConnectionStatusListener csl : csls) {
            csl.showDisconnect();
        }
        setConnection(null);
        setWarnedAboutFWCRCMismatch(false);
    }

    public void showConnect(IConnection connection) {
        this.connection = connection;
        for (ConnectionStatusListener csl : csls) {
            csl.showConnect();
        }
        setConnection(connection);
    }

    private final List<SDCardMountStatusListener> sdcmls = new LinkedList<>();

    public void addSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        if ((connection != null) && connection.getSDCardPresent()) {
            sdcml.showSDCardMounted();
        } else {
            sdcml.showSDCardUnmounted();
        }
        sdcmls.add(sdcml);
    }

    public void removeSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        sdcmls.remove(sdcml);
    }

}
