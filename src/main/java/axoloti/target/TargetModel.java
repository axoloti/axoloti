package axoloti.target;

import axoloti.Axoloti;
import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.job.IJobContext;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.property.BooleanProperty;
import axoloti.property.IntegerProperty;
import axoloti.property.ListProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import axoloti.target.midimonitor.MidiMonitorData;
import axoloti.target.midirouting.MidiInputRoutingTable;
import axoloti.target.midirouting.MidiOutputRoutingTable;
import axoloti.utils.FirmwareID;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author jtaelman
 */
public class TargetModel extends AbstractModel {

    private static TargetModel targetModel;

    public static TargetModel getTargetModel() {
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
    private SDCardInfo sdcardInfo = new SDCardInfo(0, 0, 0, Collections.emptyList());
    private MidiMonitorData midiMonitor;
    private String patchName;
    private int patchIndex;
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
    public final static Property PATCHINDEX = new IntegerProperty("PatchIndex", TargetModel.class);
    public final static Property WARNEDABOUTFWCRCMISMATCH = new BooleanProperty("WarnedAboutFWCRCMismatch", TargetModel.class);
    public final static Property SDCARDINFO = new ObjectProperty("SDCardInfo", SDCardInfo.class, TargetModel.class);
    public final static Property MIDIMONITOR = new ObjectProperty("MidiMonitor", MidiMonitorData.class, TargetModel.class);
    public final static Property PATCHNAME = new StringProperty("PatchName", TargetModel.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(CONNECTION);
        l.add(FIRMWARE_LINK_ID);
        l.add(MRTS_INPUT);
        l.add(MRTS_OUTPUT);
        l.add(RTINFO);
        l.add(PATCHINDEX);
        l.add(PATCHNAME);
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
        if ((connection != null) && (this.connection == null)) {
            setSDCardInfo(new SDCardInfo(0, 0, 0, Collections.emptyList()));
        }
        this.connection = connection;
        firePropertyChange(CONNECTION,
                null, connection);
    }

    public void refreshFiles() {
        IConnection conn = CConnection.getConnection();
        if (conn.isConnected()) {
            try {
                SDCardInfo sdci = conn.getFileList();
                setSDCardInfo(sdci);
            } catch (IOException ex) {
                Logger.getLogger(SDCardInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    private void uploadFWToSDRam(File f) {
        Logger.getLogger(TargetModel.class.getName()).log(Level.INFO, "firmware file path: {0}", f.getAbsolutePath());
        int tlength = (int) f.length();
        try (FileInputStream inputStream = new FileInputStream(f)) {
            int offset = 0;
            byte[] header = new byte[16];
            header[0] = 'f';
            header[1] = 'l';
            header[2] = 'a';
            header[3] = 's';
            header[4] = 'c';
            header[5] = 'o';
            header[6] = 'p';
            header[7] = 'y';
            header[8] = (byte) (tlength);
            header[9] = (byte) (tlength >> 8);
            header[10] = (byte) (tlength >> 16);
            header[11] = (byte) (tlength >> 24);
            byte[] bb = new byte[tlength];
            int nRead = inputStream.read(bb, 0, tlength);
            if (nRead != tlength) {
                Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
            }
            Logger.getLogger(TargetModel.class.getName()).log(Level.INFO, "firmware file size: {0}", tlength);
//            bb.order(ByteOrder.LITTLE_ENDIAN);
            CRC32 zcrc = new CRC32();
            zcrc.update(bb);
            int zcrcv = (int) zcrc.getValue();
            Logger.getLogger(TargetModel.class.getName()).log(Level.INFO, "firmware crc: 0x{0}", Integer.toHexString(zcrcv).toUpperCase());
            header[12] = (byte) (zcrcv);
            header[13] = (byte) (zcrcv >> 8);
            header[14] = (byte) (zcrcv >> 16);
            header[15] = (byte) (zcrcv >> 24);
            connection.write(connection.getTargetProfile().getSDRAMAddr() + offset, header);
            inputStream.close();

            try (FileInputStream inputStream2 = new FileInputStream(f)) {
                offset += header.length;
                int MaxBlockSize = 32768;
                do {
                    int l;
                    if (tlength > MaxBlockSize) {
                        l = MaxBlockSize;
                        tlength -= MaxBlockSize;
                    } else {
                        l = tlength;
                        tlength = 0;
                    }
                    byte[] buffer = new byte[l];
                    nRead = inputStream2.read(buffer, 0, l);
                    if (nRead != l) {
                        Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
                    }
                    connection.write(connection.getTargetProfile().getSDRAMAddr() + offset, buffer);
                    offset += nRead;
                } while (tlength > 0);
                inputStream2.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "FileNotFoundException", ex);
            } catch (IOException ex) {
                Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "IOException", ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "FileNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }

    public void flashUsingSDRam(String fname_flasher, String fname_fw) {

        try {
            updateLinkFirmwareID();
            File p = new File(fname_fw);
            if (p.canRead()) {
                IConnection conn = CConnection.getConnection();
                conn.transmitStop();
                uploadFWToSDRam(p);
                TargetModel.this.uploadPatchToMemory(fname_flasher);
                conn.transmitStart();
                Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "Firmware flashing: do not unplug the board until the leds stop blinking! You can connect again after the leds stop blinking.");
            } else {
                Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, "can''t read firmware, please compile firmware! (file: {0} )", fname_fw);
            }
        } catch (IOException ex) {
            Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, null, ex);
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
        if (this.patchIndex == patchIndex) {
            return;
        }
        this.patchIndex = patchIndex;
        readPatchName();
        firePropertyChange(PATCHINDEX, null, this.patchIndex);
    }

    public Boolean getWarnedAboutFWCRCMismatch() {
        return warnedAboutFWCRCMismatch;
    }

    public void setWarnedAboutFWCRCMismatch(Boolean WarnedAboutFWCRCMismatch) {
        this.warnedAboutFWCRCMismatch = WarnedAboutFWCRCMismatch;
        firePropertyChange(WARNEDABOUTFWCRCMISMATCH, null, WarnedAboutFWCRCMismatch);
    }

    public SDCardInfo getSDCardInfo() {
        return sdcardInfo;
    }

    public void setSDCardInfo(SDCardInfo sDCardInfo) {
        this.sdcardInfo = sDCardInfo;
        firePropertyChange(SDCARDINFO, null, sDCardInfo);
    }

    public MidiMonitorData getMidiMonitor() {
        return midiMonitor;
    }

    public void setMidiMonitor(MidiMonitorData midiMonitor) {
        this.midiMonitor = midiMonitor;
        firePropertyChange(MIDIMONITOR, null, midiMonitor);
    }

    public void readPatchName() {
        try {
            if ((connection == null) || (connection.getFWChunks() == null)) {
                setPatchName("disconnected");
                return;
            }
            ChunkData chunk_output = connection.getFWChunks().getOne(FourCCs.FW_PATCH_NAME);
            if (chunk_output == null) {
                setPatchName("???");
                return;
            }
            ByteBuffer data = chunk_output.getData();
            int addr = data.getInt();

            ByteBuffer mem = connection.read(addr, 32);
            if (mem == null) {
                setPatchName("failed");
                return;
            }
            String s = "";
            while (mem.hasRemaining()) {
                char c = (char) mem.get();
                if (c == 0) {
                    break;
                }
                s += c;
            }
            setPatchName(s);
        } catch (IOException ex) {
            Logger.getLogger(TargetModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPatchName() {
        return patchName;
    }

    public void setPatchName(String patchName) {
        this.patchName = patchName;
        firePropertyChange(PATCHNAME, null, patchName);
    }

    private final List<Runnable> pollers = new LinkedList<>();

    public List<Runnable> getPollers() {
        return Collections.unmodifiableList(pollers);
    }

    public void addPoller(Runnable poller) {
        pollers.add(poller);
    }

    public void removePoller(Runnable poller) {
        pollers.remove(poller);
    }

    @Override
    protected TargetController createController() {
        return new TargetController(this);
    }

    @Override
    public IModel getParent() {
        return null;
    }

    public void uploadPatchToMemory(String basepath) throws FileNotFoundException, IOException {
        // from now on there can be multiple segments!
        File f1 = new File(basepath + ".sram1.bin");
        File f2 = new File(basepath + ".sram3.bin");
        File f3 = new File(basepath + ".sdram.bin");
        //Logger.getLogger(TargetModel.class.getName()).log(Level.INFO, "bin path: {0}", f.getAbsolutePath() + " " + f2.getAbsolutePath());
        connection.write(connection.getTargetProfile().getPatchAddr(), f1);
        if (f2.length() > 0) {
            connection.write(connection.getTargetProfile().getSRAM3Addr(), f2);
        }
        if (f3.length() > 0) {
            connection.write(connection.getTargetProfile().getSDRAMAddr(), f3);
        }
//      log("Done uploading patch");
    }

    public void uploadPatchToMemory() throws FileNotFoundException, IOException {
        String basepath = System.getProperty(Axoloti.HOME_DIR) + "/build/xpatch";
        TargetModel.this.uploadPatchToMemory(basepath);
    }

    public void deleteFile(String filename) throws IOException {
        getConnection().deleteFile(filename);
        LinkedList<SDFileInfo> files = new LinkedList<>(getSDCardInfo().getFiles());
        SDFileInfo f1 = null;
        for (SDFileInfo f : files) {
            String fname = f.getFilename();
            if (fname.equalsIgnoreCase(filename)
                    || fname.equalsIgnoreCase(filename + "/")) {
                f1 = f;
                break;
            }
        }
        if (f1 != null) {
            files.remove(f1);
        }
        SDCardInfo sdci = new SDCardInfo(0, 0, 0, files);
        setSDCardInfo(sdci);
    }

    public void createDirectory(String filename, Calendar date, IJobContext ctx) throws IOException {
        String fn1 = filename;
        if (!fn1.endsWith("/")) {
            fn1 += "/";
        }
        final String fn = fn1;

        getConnection().createDirectory(filename, date);

        ctx.doInSync(() -> {
            LinkedList<SDFileInfo> files = new LinkedList<>();
            SDCardInfo sdci0 = getSDCardInfo();
            if (sdci0 != null) {
                files.addAll(sdci0.getFiles());
                SDFileInfo sdfi = null;
                for (SDFileInfo f : files) {
                    if (f.getFilename().equalsIgnoreCase(fn)) {
                        // already present
                        sdfi = f;
                    }
                }
                if (sdfi != null) {
                    files.remove(sdfi);
                }
            }
            files.add(new SDFileInfo(fn, 0, 0));
            SDCardInfo sdci = new SDCardInfo(0, 0, 0, files);
            setSDCardInfo(sdci);
        });
    }

    public void createDirectory(String filename, IJobContext ctx) throws IOException {
        createDirectory(filename, Calendar.getInstance(), ctx);
    }

    public void upload(String filename, InputStream inputStream, Calendar cal, int size, IJobContext ctx) throws IOException {
        for (int i = 1; i < filename.length(); i++) {
            if (filename.charAt(i) == '/') {
                createDirectory(filename.substring(0, i), ctx);
            }
        }
        getConnection().upload(filename, inputStream, cal, size, ctx);
        ctx.doInSync(() -> {
            LinkedList<SDFileInfo> files = new LinkedList<>();
            SDCardInfo sdci0 = getSDCardInfo();
            if (sdci0 != null) {
                files.addAll(sdci0.getFiles());
                SDFileInfo sdfi = null;
                for (SDFileInfo f : files) {
                    if (f.getFilename().equalsIgnoreCase(filename)) {
                        // already present
                        sdfi = f;
                    }
                }
                if (sdfi != null) {
                    files.remove(sdfi);
                }
            }
            files.add(new SDFileInfo(filename, size, cal));
            SDCardInfo sdci = new SDCardInfo(0, 0, 0, files);
            setSDCardInfo(sdci);
        });
    }

    public void upload(String filename, InputStream inputStream, IJobContext ctx) throws IOException {
        upload(filename, inputStream, Calendar.getInstance(), 0, ctx);
    }

    public void upload(String filename, File file, IJobContext ctx) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        int size = (int) file.length();
        FileInputStream inputStream = new FileInputStream(file);
        upload(filename, inputStream, cal, size, ctx);
    }

}
