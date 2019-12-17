/**
 * Copyright (C) 2019 Johannes Taelman
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
package axoloti.connection;

/**
 * Replaces the old packets-over-bytestream protocol on vendor-specific usb bulk
 * transport with usb packets on vendor-specific usb bulk transport.
 */
import axoloti.HWSignature;
import axoloti.chunks.ChunkParser;
import axoloti.chunks.FourCC;
import axoloti.connection.rcvpacket.FResult;
import axoloti.connection.rcvpacket.IRcvPacketConsumer;
import axoloti.connection.rcvpacket.RcvCString;
import axoloti.connection.rcvpacket.RcvFRead;
import axoloti.connection.rcvpacket.RcvFResult;
import axoloti.connection.rcvpacket.RcvFileDir;
import axoloti.connection.rcvpacket.RcvFileInfo;
import axoloti.connection.rcvpacket.RcvMemRead;
import axoloti.connection.rcvpacket.RcvPatchDisp;
import axoloti.connection.rcvpacket.RcvPtr;
import axoloti.job.IJobContext;
import axoloti.preferences.Preferences;
import axoloti.target.TargetModel;
import axoloti.target.TargetRTInfo;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import axoloti.targetprofile.axoloti_core;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import org.usb4java.*;

/**
 *
 * @author Johannes Taelman
 */
public class USBBulkConnection_v2 implements IConnection {

    private final Map<Integer, ILivePatch> patchMap;
    private boolean connected;
    private Receiver receiver;
    private axoloti_core targetProfile;
    private DeviceHandle handle;
    static final int interfaceNumber = 2;
    private String firmwareID;
    private boolean old_protocol = false;
    private final IConnectionCB callbacks;

    protected USBBulkConnection_v2(IConnectionCB callbacks) {
        super();
        this.callbacks = callbacks;
        this.patchMap = new HashMap<>();
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    private void disconnect1(boolean do_not_close_usb) {
        TargetModel.getTargetModel().removeAllPollers();
        rcvPacketConsumer = null;
        if (connected) {
            if (!do_not_close_usb) {
                connected = false;
            }
            isSDCardPresent = null;
            Collection<ILivePatch> patches = patchMap.values();
            for (ILivePatch patch : patches) {
                IPatchCB pcb = patch.getCallbacks();
                pcb.patchStopped();
            }
            patchMap.clear();
            callbacks.showDisconnect();
            callbacks.patchListChanged(Collections.emptyList());
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Disconnect request");
            if (pinger != null) {
                pinger.terminate();
            }
            receiver.terminate();
            if (handle != null) {
                int result = LibUsb.releaseInterface(handle, interfaceNumber);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to release interface", result);
                }
            }
            if (!do_not_close_usb) {
                LibUsb.close(handle);
                handle = null;
            }
        }
    }

    @Override
    public void disconnect() {
        disconnect1(false);
    }

    private static byte[] bb2ba(ByteBuffer bb) {
        bb.rewind();
        byte[] r = new byte[bb.remaining()];
        bb.get(r, 0, r.length);
        return r;
    }

    private static void putCStringInByteBuffer(ByteBuffer dest, String str) {
        for (int j = 0; j < str.length(); j++) {
            dest.put((byte) str.charAt(j));
        }
        dest.put((byte) 0);
    }

    private int cpuCode;
    private ByteBuffer cpuSerial;
    private ByteBuffer otpInfo;
    private ByteBuffer signature;

    @Override
    public boolean connect(IDevice connectable) {
        disconnect();
        old_protocol = false;
        rcvPacketConsumer = null;
        targetProfile = new axoloti_core();

        if (connectable == null) {
            connectable = USBDeviceLister.getInstance().getDefaultDevice();
        }
        if (connectable == null) {
            callbacks.showDisconnect();
            return false;
        }
        handle = ((AxolotiDevice) connectable).getDeviceHandle();
        if (handle == null) {
            callbacks.showDisconnect();
            return false;
        }

        try {
            int result = LibUsb.claimInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }

            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "creating rx and tx thread...");
            receiver = new Receiver();
            receiver.start();
            connected = true;
            transmitPing();
            transmitPing();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (old_protocol) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, "old protocol...");
                disconnect1(true);
                callbacks.fwupgrade_from_1012(handle);
                LibUsb.close(handle);
                handle = null;
                connected = false;
                return false;
            }
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "connected");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            tx_get_patch_list();
            /*
            jobThread = new JobThread();
            jobThread.schedule((x) -> {
                transmitGetFWVersion();
                cpuCode = transmitMemoryRead1Word(targetProfile.getCPUIDCodeAddr());
            });

            jobThread.waitQueueFinished();
            targetProfile.setCPUIDCode(cpuCode);

            jobThread.schedule((x) -> {
                cpuSerial = transmitMemoryReadNOCB(targetProfile.getCPUSerialAddr(), targetProfile.getCPUSerialLength());
                otpInfo = transmitMemoryReadNOCB(targetProfile.getOTPAddr(), 32);
                signature = transmitMemoryReadNOCB(targetProfile.getOTPAddr() + 32, HWSignature.KEY_LENGTH);
            });

            jobThread.waitQueueFinished();
            targetProfile.setCPUSerial(cpuSerial);
             */
            transmitGetFWVersion();
            cpuCode = 0;
//transmitMemoryRead1Word(targetProfile.getCPUIDCodeAddr());
            targetProfile.setCPUIDCode(cpuCode);
            cpuSerial = read(targetProfile.getCPUSerialAddr(), targetProfile.getCPUSerialLength());
            otpInfo = read(targetProfile.getOTPAddr(), 32);
            signature = read(targetProfile.getOTPAddr() + 32, HWSignature.KEY_LENGTH);
            targetProfile.setCPUSerial(cpuSerial);

            boolean signaturevalid = false;
            if (signature == null) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Can''t obtain signature, upgrade firmware?");
            } else if ((signature.getInt(0) == 0xFFFFFFFF) && (signature.getInt(1) == 0xFFFFFFFF)) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Cannot validate authenticity, no signature present.");
            } else {
                signaturevalid = HWSignature.verify(targetProfile.getCPUSerial(), otpInfo, bb2ba(signature));
                if (signaturevalid) {
                    String s = "";
                    otpInfo.rewind();
                    byte c = otpInfo.get();
                    while (c != 0) {
                        s += (char) (c & 0xFF);
                        c = otpInfo.get();
                    }
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Authentic {0}", s);
                } else {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "Can''t validate authenticity, signature invalid!");
                }
            }

            boolean signing = false;
//            boolean signaturevalid = false;
            if (signing && !signaturevalid) {
                ByteBuffer writeotpinfo = targetProfile.createOTPInfo();
                byte[] sign = HWSignature.sign(targetProfile.getCPUSerial(), writeotpinfo);
                write(targetProfile.getBKPSRAMAddr(), bb2ba(writeotpinfo));
                write(targetProfile.getBKPSRAMAddr() + 32, sign);
                CRC32 zcrc = new CRC32();
                writeotpinfo.rewind();
                zcrc.update(bb2ba(writeotpinfo));
                zcrc.update(sign);
                int zcrcv = (int) zcrc.getValue();
                diagnostic_println(String.format("key crc: %08X", zcrcv));
                byte crc[] = new byte[4];
                crc[0] = (byte) (zcrcv & 0xFF);
                crc[1] = (byte) ((zcrcv >> 8) & 0xFF);
                crc[2] = (byte) ((zcrcv >> 16) & 0xFF);
                crc[3] = (byte) ((zcrcv >> 24) & 0xFF);
                write(targetProfile.getBKPSRAMAddr() + 32 + 256, crc);

                // validate from bkpsram
                ByteBuffer otpInfo2 = read(targetProfile.getBKPSRAMAddr(), 32);
                ByteBuffer signature2 = read(targetProfile.getBKPSRAMAddr() + 32, 256);

                boolean signaturevalid2 = HWSignature.verify(targetProfile.getCPUSerial(), otpInfo2, bb2ba(signature2));
                if (signaturevalid2) {
                    diagnostic_println("bpksram signature valid");
                } else {
                    diagnostic_println("bpksram signature INvalid");
                    return false;
                }
                diagnostic_println("<otpinfo>");
                HWSignature.printByteArray(bb2ba(otpInfo2));
                diagnostic_println("</otpinfo>");

                diagnostic_println("<signature>");
                HWSignature.printByteArray(sign);
                diagnostic_println("</signature>");

            }

            ByteBuffer bb = read(fw_chunkaddr, 8);
            int fw_chunks_hdr = bb.getInt();
            int fw_chunks_size = bb.getInt();
            diagnostic_println("fw chunks hdr " + FourCC.format(fw_chunks_hdr) + ", size " + fw_chunks_size);
            ByteBuffer bb2 = read(fw_chunkaddr, fw_chunks_size + 8);
            fw_chunks = new ChunkParser(bb2);
            callbacks.showConnect();

            pinger = new PeriodicPinger();
            pinger.start();
            return true;

        } catch (Exception ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            callbacks.showDisconnect();
            return false;
        }
    }

    private ChunkParser fw_chunks;

    @Override
    public ChunkParser getFWChunks() {
        return fw_chunks;
    }

    static final byte OUT_ENDPOINT = 0x02;
    static final byte IN_ENDPOINT = (byte) 0x82;
    static final int TIMEOUT = 1000;

    private static void diagnostic_println(String s) {
        String s1 = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS").format(Calendar.getInstance().getTime()) + " : " + s;
        System.out.println(s1);
    }

    private static byte[] createPacket(int header) {
        byte[] b = new byte[4];
        b[0] = (byte) header;
        b[1] = (byte) (header >> 8);
        b[2] = (byte) (header >> 16);
        b[3] = (byte) (header >> 24);
        return b;
    }

    private static byte[] createPacket(int header, Object... data) {
        int length = 4;
        for (Object o : data) {
            if (o instanceof Integer) {
                length += 4;
            } else if (o instanceof Short) {
                length += 2;
            } else if (o instanceof Byte) {
                length += 1;
            } else if (o instanceof String) {
                length += ((String) o).length() + 1;
            } else if (o instanceof byte[]) {
                length += ((byte[]) o).length;
            } else {
                throw new UnsupportedOperationException(o.getClass().toString());
            }
        }
        byte b[] = new byte[length];
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(header);
        for (Object o : data) {
            if (o == null) {
                throw new NullPointerException();
            }
            if (o instanceof Integer) {
                bb.putInt((Integer) o);
            } else if (o instanceof Short) {
                bb.putShort((Short) o);
            } else if (o instanceof Byte) {
                bb.put((Byte) o);
            } else if (o instanceof String) {
                String s = (String) o;
                putCStringInByteBuffer(bb, s);
            } else if (o instanceof byte[]) {
                byte[] ob = (byte[]) o;
                bb.put(ob);
            } else {
                throw new UnsupportedOperationException(o.toString());
            }
        }
        return b;
    }

    void transmitPacket(byte[] data) throws IOException {
        synchronized (this) {
            transmitPacket1(data);
        }
    }

    private void transmitPacket1(byte[] data) throws IOException {

        if (!Thread.holdsLock(this)) {
            throw new IllegalStateException("no lock");
        }

        boolean dump_tx_headers = false;
        if (dump_tx_headers) {
            boolean dump_all_tx_headers = false;
            boolean dump_patch_tx_headers = true;
            if (data.length >= 4) {
                if (dump_all_tx_headers
                        || (dump_patch_tx_headers && (data[2] == 'P'))) {
                    diagnostic_println(String.format("->  %c%c%c%c  %5d",
                            (char) data[0],
                            (char) data[1],
                            (char) data[2],
                            (char) data[3],
                            data.length - 4));
                }
            }
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        IntBuffer transfered = IntBuffer.allocate(1);
        if (handle == null) {
            throw new IOException("Not connected");
        }
        int result = LibUsb.bulkTransfer(handle, OUT_ENDPOINT, buffer, transfered, 1000);
        if (result != LibUsb.SUCCESS) {
            if (result == LibUsb.ERROR_NO_DEVICE) {
                disconnect();
                throw new IOException("Device disconnected");
            } else if (result == LibUsb.ERROR_TIMEOUT) {
                disconnect();
                throw new IOException("Timeout");
            } else {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "Control transfer failed: {0}", result);
                disconnect();
            }
        }
        //System.out.println(transfered.get() + " bytes sent");
    }

    @Override
    public void bringToDFU() throws IOException {
        byte[] pckt = createPacket(tx_hdr_activate_dfu);
        transmitPacket(pckt);
    }

    @Override
    public void transmitGetFWVersion() throws IOException {
        byte[] pckt = createPacket(tx_hdr_getfwid, (byte) 0);
        transmitPacket(pckt);
    }

    @Override
    public void sendMidi(int cable, byte m0, byte m1, byte m2) throws IOException {
        byte cin = (byte) ((m0 & 0xF0) >> 4);
        byte ph = (byte) (((cable & 0x0F) << 4) | cin);
        byte[] pckt = createPacket(tx_hdr_midi, ph, m0, m1, m2);
        transmitPacket(pckt);
    }

    void applyPreset(int patchRef, int presetNo) throws IOException {
        byte[] pckt = createPacket(tx_hdr_patch_preset_apply, patchRef, presetNo);
        transmitPacket(pckt);
    }

    void sendUpdatedPreset(int patchRef, byte[] b) throws IOException {
        byte[] pckt = createPacket(tx_hdr_patch_preset_write, patchRef, b.length, b);
        transmitPacket(pckt);
    }

    void transmitParameterChange(int patchRef, byte[] data) throws IOException {
        byte[] pckt = createPacket(tx_hdr_patch_paramchange, patchRef, data);
        transmitPacket(pckt);
    }

    @Override
    public void transmitExtraCommand(int arg) throws IOException {
        byte[] pckt = createPacket(rcv_hdr_extra, arg);
        transmitPacket(pckt);
    }

    final private int timeOutMs = 2000;
    final private int timeOutFileListMs = 2000;

    /**
     * Get file info from target filesystem. Returns null if the file does not
     * exist.
     *
     * @param filename
     * @return
     * @throws IOException
     */
    @Override
    public SDFileInfo getFileInfo(String filename) throws IOException {
        synchronized (this) {
            RcvFileInfo rcvFileInfo = new RcvFileInfo();
            rcvPacketConsumer = rcvFileInfo;
            byte[] txdata = createPacket(tx_hdr_f_getinfo, filename);
            transmitPacket1(txdata);
            try {
                SDFileInfo sdfi = rcvFileInfo.get(timeOutMs, TimeUnit.MILLISECONDS);
                rcvPacketConsumer = null;
                if (sdfi == null) {
                    return null;
                }
                if (sdfi.getSize() >= 0) {
                    return sdfi;
                } else {
                    return null;
                }
            } catch (TimeoutException | InterruptedException | ExecutionException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public ByteBuffer download(String filename, IJobContext ctx) throws IOException {
        if (ctx != null) {
            ctx.setNote("downloading: " + filename);
            ctx.setMaximum(101);
        }
        SDFileInfo fileInfo = getFileInfo(filename);
        int sz = fileInfo.getSize();
        ByteBuffer bb = ByteBuffer.allocate(sz);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        FileReference fref = f_open(filename);
        int remaining = sz;
        int bs = 1024;
        while (remaining > 0) {
            int btr;
            if (remaining > bs) {
                btr = bs;
            } else {
                btr = remaining;
            }
            byte fragment[] = f_read(fref, btr);
            remaining -= bs;
            bb.put(fragment);
            int pct = (100 * (sz - remaining)) / sz;
            if (ctx != null) {
                ctx.setProgress(pct + 1);
            }
        }
        f_close(fref);
        bb.rewind();
        return bb;
    }

    @Override
    public String getFWID() {
        return firmwareID;
    }

    @Override
    public SDCardInfo getFileList(String path) throws IOException {
        synchronized (this) {
            if (log_rx_diagnostics) {
                diagnostic_println("filelist req");
            }
            if (path == null) {
                path = "/";
            }
            if (!path.equals("/")) {
                // strip trailing slash
                if (path.charAt(path.length() - 1) == '/') {
                    path = path.substring(0, path.length() - 1);
                }
            }
            byte b[] = createPacket(tx_hdr_f_dirlist, path);
            RcvFileDir rcvFileDir = new RcvFileDir();
            rcvPacketConsumer = rcvFileDir;
            transmitPacket1(b);
            try {
                return rcvFileDir.get(timeOutFileListMs, TimeUnit.MILLISECONDS);
            } catch (ExecutionException | InterruptedException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    private void createDirIfNonExistant(String path) throws IOException {
        SDFileInfo fi = getFileInfo(path);
        if ((fi == null) || (!fi.isDirectory())) {
            createDirectory(path, Calendar.getInstance());
        }
    }

    @Override
    public void upload(String filename, InputStream inputStream, Calendar cal, int size, IJobContext ctx) throws IOException {
        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "uploading: {0}", filename);
        if (ctx != null) {
            ctx.setNote("uploading: " + filename);
            ctx.setMaximum(101);
        }
        Calendar ts;
        if (cal != null) {
            ts = cal;
        } else {
            ts = Calendar.getInstance();
        }
        for (int i = 1; i < filename.length(); i++) {
            if (filename.charAt(i) == '/') {
                createDirIfNonExistant(filename.substring(0, i));
            }
        }

        FileReference fref = f_open_write(filename);
        int remLength = size;
        final int MaxBlockSize = 1024;
        int pct = 0;
        do {
            byte[] buffer = new byte[MaxBlockSize];
            int nRead = inputStream.read(buffer);
            if (nRead == MaxBlockSize) {
                f_write(fref, buffer);
            } else if (nRead > 0) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, 0, nRead);
                byte[] b2 = new byte[nRead];
                bb.get(b2);
                f_write(fref, b2);
            } else {
                break;
            }
            int newpct;
            if (size == 0) {
                newpct = pct + 1;
            } else {
                newpct = (100 * (size - remLength) / size);
            }
            if (ctx != null) {
                ctx.setProgress(pct + 1);
            } else if (newpct != pct) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "uploading : {0}%", newpct);
            }
            pct = newpct;
            remLength -= nRead;
        } while (true);
        inputStream.close();
        f_close(fref);
        f_setTimestamp(filename, ts);
    }

    private static final int tx_hdr_ping = 0x706f7841; // "Axop"
    private static final int tx_hdr_getfwid = 0x566f7841; // "AxoV"
    private static final int tx_hdr_activate_dfu = 0x446f7841; // "AxoD"
    private static final int rcv_hdr_extra = 0x586f7841; // "AxoX"
    private static final int tx_hdr_midi = 0x4D6f7841; // "AxoM"
    private static final int tx_hdr_virtual_input_event = 0x426f7841; // "AxoB"

    private static final int tx_hdr_mem_read = 0x724d7841; // "AxMr"
    private static final int tx_hdr_mem_write = 0x774d7841; // "AxMw"
    private static final int tx_hdr_mem_alloc = 0x614d7841; // "AxMa"
    private static final int tx_hdr_mem_free = 0x664d7841; // "AxMf"
    private static final int tx_hdr_mem_write_flash = 0x464d7841; // "AxMF"

    private static final int tx_hdr_patch_stop = 0x53507841; // "AxPS"
    private static final int tx_hdr_patch_start = 0x73507841; // "AxPs"
    private static final int tx_hdr_patch_paramchange = 0x70507841; // "AxPp"
    private static final int tx_hdr_patch_get_disp = 0x64507841; // "AxPd"
    private static final int tx_hdr_patch_preset_apply = 0x54507841; // "AxPT"
    private static final int tx_hdr_patch_preset_write = 0x52507841; // "AxPR"
    private static final int tx_hdr_patch_get_name = 0x6E507841; // "AxPn"
    private static final int tx_hdr_patch_get_list = 0x6C507841; // "AxPl"
    private static final int tx_hdr_patch_get_error = 0x65507841; // "AxPe"

    private static final int tx_hdr_f_open = 0x6f467841; // "AxFo"
    private static final int tx_hdr_f_open_write = 0x4f467841; // "AxFO"
    private static final int tx_hdr_f_close = 0x63467841; // "AxFc"
    private static final int tx_hdr_f_seek = 0x73467841; // "AxFs"
    private static final int tx_hdr_f_read = 0x72467841; // "AxFr"
    private static final int tx_hdr_f_write = 0x77467841; // "AxFw"
    private static final int tx_hdr_f_dirlist = 0x64467841; // "AxFd"
    private static final int tx_hdr_f_getinfo = 0x69467841; // "AxFi"
    private static final int tx_hdr_f_setinfo = 0x49467841; // "AxFI"
    private static final int tx_hdr_f_delete = 0x52467841; // "AxFR"
    private static final int tx_hdr_f_mkdir = 0x6D467841; // "AxFm"

    private int sendAndGetReplyPtr(byte[] txdata) throws IOException {
        synchronized (this) {
            RcvPtr rcvPtr = new RcvPtr();
            rcvPacketConsumer = rcvPtr;
            transmitPacket1(txdata);
            try {
                int result = rcvPtr.get(5000, TimeUnit.MILLISECONDS);
                if (log_rx_diagnostics) {
                    diagnostic_println(String.format("read result: %s", result));
                }
                return result;
            } catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public ILivePatch transmitStartLive(byte[] elf, String patchName, IPatchCB patchCB, IJobContext ctx) throws IOException, PatchLoadFailedException {
        int addr = mem_alloc(elf.length);
        write(addr, elf);
        String vpatchname = String.format("@%8X:%s", addr, patchName);
        //System.out.println("vpatchname: " + vpatchname);
        return transmitStart(vpatchname, patchCB);
    }

    @Override
    public LivePatch transmitStart(String patchName, IPatchCB patchCB) throws IOException, PatchLoadFailedException {
        byte b[] = createPacket(tx_hdr_patch_start, patchName);
        int addr = sendAndGetReplyPtr(b);
        if (addr == 0) {
            String msg = patchGetError();
            throw new PatchLoadFailedException("Patch failed to load : " + msg);
        }
        if (patchCB == null) {
            return null;
        } else {
            LivePatch lp = new LivePatch(addr, patchCB, patchName, this);
            patchMap.put(addr, lp);
            return lp;
        }
    }

    @Override
    public LivePatch transmitStart(int patchIndex) throws IOException {
        byte b[] = createPacket(tx_hdr_patch_start, patchIndex);
        int addr = sendAndGetReplyPtr(b);
        if (addr == 0) {
            throw new IOException("addr is 0");
        }
        return null; // TODO : (low priority) parameter editor for running binaries
    }

    @Override
    public void transmitStop() throws IOException {
        patchMap.clear();
        transmitStop(0);
    }

    void transmitStop(int patchRef) throws IOException {
        byte b[] = createPacket(tx_hdr_patch_stop, patchRef);
        transmitPacket(b);
    }

    String patchGetName(int patchRef) throws IOException {
        synchronized (this) {
            byte b[] = createPacket(tx_hdr_patch_get_name, patchRef);
            RcvCString rcvCString = new RcvCString();
            rcvPacketConsumer = rcvCString;
            transmitPacket1(b);
            try {
                return rcvCString.get(timeOutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            } catch (ExecutionException ex) {
                throw new IOException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    String patchGetError() throws IOException {
        synchronized (this) {
            byte b[] = createPacket(tx_hdr_patch_get_error);
            RcvCString rcvCString = new RcvCString();
            rcvPacketConsumer = rcvCString;
            transmitPacket1(b);
            try {
                return rcvCString.get(timeOutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            } catch (ExecutionException ex) {
                throw new IOException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public void transmitPing() throws IOException {
        byte[] pckt = createPacket(tx_hdr_ping);
        transmitPacket(pckt);
    }

    @Override
    public void write(int address, byte[] data) throws IOException {
        int length = data.length;
        byte cmd[] = createPacket(tx_hdr_mem_write, address, length);
        synchronized (this) {
            transmitPacket1(cmd);
            transmitPacket1(data);
            // Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "block uploaded @ 0x{0} length {1}", new Object[]{Integer.toHexString(address).toUpperCase(), Integer.toString(data.length)});
        }
    }

    private int mem_alloc(int size) throws IOException {
        int alignment = 4;
        final int mem_type_hint_large = 1 << 17;
        int typeflags = mem_type_hint_large;
        byte pckt[] = createPacket(tx_hdr_mem_alloc, size, typeflags, alignment);
        int ptr = sendAndGetReplyPtr(pckt);
        return ptr;
    }

    private void tx_get_patch_list() throws IOException {
        // result is offered through IConnectionCB::patchListChanged()
        byte pckt[] = createPacket(tx_hdr_patch_get_list);
        transmitPacket(pckt);
    }

    private void mem_free(int ptr) throws IOException {
        byte pckt[] = createPacket(tx_hdr_mem_free, ptr);
        transmitPacket(pckt);
    }

    private void mem_write_flash(int pdest, int psrc, int size) throws IOException {
        byte pckt[] = createPacket(tx_hdr_mem_write_flash, pdest, psrc, size);
        int reply = sendAndGetReplyPtr(pckt);
        if (reply != 0) {
            throw new IOException("reply = " + reply);
        }
    }

    @Override
    public void uploadPatchToFlash(byte[] elf, String patchName) throws IOException {
        if (elf.length > 0x080000) {
            throw new IOException("patch larger than reserved space in flash");
        }
        int addr = mem_alloc(elf.length);
        if (addr == 0) {
            throw new IOException("not enough free sdram for flashing");
        }
        write(addr, elf);
        int PATCHFLASHLOC = 0x08080000;
        mem_write_flash(PATCHFLASHLOC, addr, elf.length);
        mem_free(addr);
    }

    @Override
    public void uploadFirmware(byte[] fwimage) throws IOException {
        byte b[] = new byte[4 + fwimage.length];
        ByteBuffer bb = ByteBuffer.wrap(b);
        CRC32 zcrc = new CRC32();
        zcrc.update(fwimage);
        int zcrcv = (int) zcrc.getValue();
        Logger.getLogger(TargetModel.class.getName()).log(Level.INFO, "firmware crc: 0x{0}", Integer.toHexString(zcrcv).toUpperCase());
        bb.put((byte) (zcrcv));
        bb.put((byte) (zcrcv >> 8));
        bb.put((byte) (zcrcv >> 16));
        bb.put((byte) (zcrcv >> 24));
        bb.put(fwimage);
        int addr = mem_alloc(b.length);
        if (addr == 0) {
            throw new IOException("addr = 0");
        }
        write(addr, b);
        int FWFLASHLOC = 0x08000000;
        try {
            mem_write_flash(FWFLASHLOC, addr, b.length);
        } catch (IOException ex) {
            // silence ex,
        }
    }

    @Override
    public void transmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3)
            throws IOException {
        byte[] data = createPacket(tx_hdr_virtual_input_event, b0, b1, b2, b3);
        transmitPacket(data);
    }

    FileReference f_open(String filename) throws IOException {
        synchronized (this) {
            byte[] data = createPacket(tx_hdr_f_open, filename);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(data);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                fResult.throwErr();
                rcvPacketConsumer = null;
                return fResult.getFileRef();
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    FileReference f_open_write(String filename) throws IOException {
        synchronized (this) {
            byte[] data = createPacket(tx_hdr_f_open_write, filename);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(data);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                fResult.throwErr();
                rcvPacketConsumer = null;
                return fResult.getFileRef();
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    void f_close(FileReference fref) throws IOException {
        synchronized (this) {
            byte[] data = createPacket(tx_hdr_f_close, fref.id);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(data);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                fResult.throwErr();
                rcvPacketConsumer = null;
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    void f_write(FileReference fref, byte[] data) throws IOException {
        synchronized (this) {
            byte txdata[] = new byte[data.length + 12];
            ByteBuffer bb = ByteBuffer.wrap(txdata);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(tx_hdr_f_write);
            bb.putInt(fref.id);
            bb.putInt(data.length);
            bb.put(data);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(txdata);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                fResult.throwErr();
                rcvPacketConsumer = null;
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    byte[] f_read(FileReference fref, int bytes_to_read) throws IOException {
        synchronized (this) {
            byte txdata[] = createPacket(tx_hdr_f_read, fref.id, bytes_to_read);
            RcvFRead rcvFRead = new RcvFRead();
            rcvPacketConsumer = rcvFRead;
            transmitPacket1(txdata);
            try {
                ByteBuffer bbr = rcvFRead.get(timeOutMs, TimeUnit.MILLISECONDS);
                rcvPacketConsumer = null;
                byte br[] = new byte[bbr.limit()];
                bbr.get(br);
                return br;
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    private void f_setTimestamp(String filename, Calendar date) throws IOException {
        synchronized (this) {
            int dy = date.get(Calendar.YEAR);
            int dm = date.get(Calendar.MONTH) + 1;
            int dd = date.get(Calendar.DAY_OF_MONTH);
            int th = date.get(Calendar.HOUR_OF_DAY);
            int tm = date.get(Calendar.MINUTE);
            int ts = date.get(Calendar.SECOND);
            int t = ((dy - 1980) * 512) | (dm * 32) | dd;
            int d = (th * 2048) | (tm * 32) | (ts / 2);
            byte[] b = createPacket(tx_hdr_f_setinfo, (short) t, (short) d, filename);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(b);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                fResult.throwErr();
                rcvPacketConsumer = null;
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        synchronized (this) {
            byte[] txdata = createPacket(tx_hdr_f_delete, filename);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(txdata);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                fResult.throwErr();
                rcvPacketConsumer = null;
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public void createDirectory(String filename, Calendar date) throws IOException {
        synchronized (this) {
            byte[] txdata = createPacket(tx_hdr_f_mkdir, filename);
            RcvFResult rcvFResult = new RcvFResult();
            rcvPacketConsumer = rcvFResult;
            transmitPacket1(txdata);
            try {
                FResult fResult = rcvFResult.get(timeOutMs, TimeUnit.MILLISECONDS);
                if (fResult.err != FResult.FR_DISK_ERR) {
                    fResult.throwErr();
                }
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new IOException();
            }
        }
    }

    @Override
    public ByteBuffer read(int addr, int length) throws IOException {
        assert (length > 0);
        if (((addr >= 0x00000000) && (addr < 0x00100000) && (addr + length >= 0x00100000))
                || ((addr >= 0x20000000) && (addr < 0x20300000) && (addr + length >= 0x20300000))
                || ((addr >= 0x08000000) && (addr < 0x08100000) && (addr + length >= 0x08100000))
                || ((addr >= 0x1FFF0000) && (addr < 0x1FFFFFFF) && (addr + length >= 0x1FFFFFFF))) {
            throw new IOException("address out of range : " + String.format("0x%08X", addr));
        }
        synchronized (this) {
            RcvMemRead rcvMemRead = new RcvMemRead();
            rcvPacketConsumer = rcvMemRead;
            if (log_rx_diagnostics) {
                diagnostic_println(String.format("read %08X  %X", addr, length));
            }
            byte[] data = createPacket(tx_hdr_mem_read, addr, length);
            transmitPacket1(data);

            try {
                ByteBuffer result = rcvMemRead.get(1000, TimeUnit.MILLISECONDS);
                if (log_rx_diagnostics) {
                    diagnostic_println(String.format("read result: %s", result));
                }
                return result;
            } catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    private class Receiver implements Runnable {
        private Thread receiverThread;

        final static int MAX_RX_SIZE = 4096;
        // larger than 4096 will give "WARN Event TRB for slot 1 ep 4 with no TDs queued" in linux kernel log

        volatile boolean terminating = false;

        public void terminate() {
            terminating = true;
            if (receiverThread.isAlive()) {
                receiverThread.interrupt();
                try {
                    receiverThread.join();
                } catch (InterruptedException ex) {
                    //Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public void run() {
            ByteBuffer packet = null;
            while (!terminating) {
                ByteBuffer recvbuffer = ByteBuffer.allocateDirect(MAX_RX_SIZE);
                recvbuffer.order(ByteOrder.LITTLE_ENDIAN);
                IntBuffer transfered = IntBuffer.allocate(1);
                recvbuffer.rewind();
                int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, recvbuffer, transfered, 1000);
                switch (result) {
                    case LibUsb.SUCCESS:
                        int sz = transfered.get(0);
                        recvbuffer.limit(sz);
                        recvbuffer.rewind();
                        if (sz < MAX_RX_SIZE) {
                            // terminates a packet
                            if (packet == null) {
                                packet = recvbuffer;
                            } else {
                                int pl = packet.position();
                                if (packet.capacity() > pl + sz) {
                                    packet.put(recvbuffer);
                                    packet.limit(pl + sz);
                                    packet.rewind();
                                } else {
                                    ByteBuffer new_packet = ByteBuffer.allocate(packet.position() + sz);
                                    new_packet.order(ByteOrder.LITTLE_ENDIAN);
                                    packet.limit(packet.position());
                                    packet.rewind();
                                    new_packet.put(packet);
                                    new_packet.put(recvbuffer);
                                    new_packet.limit(packet.position() + recvbuffer.position());
                                    new_packet.rewind();
                                    packet = new_packet;
                                }
                            }
                            // diagnostics
                            boolean dump_rx_headers = false;
                            if (dump_rx_headers) {
                                boolean dump_all_rx_headers = false;
                                boolean dump_patch_rx_headers = true;
                                if (sz >= 4) {
                                    char h1 = (char) packet.get();
                                    char h2 = (char) packet.get();
                                    char h3 = (char) packet.get();
                                    char h4 = (char) packet.get();
                                    if (dump_all_rx_headers
                                            || (dump_patch_rx_headers && h3 == 'P')) {
                                        diagnostic_println(
                                                String.format(
                                                        "<- %c%c%c%c           %4d",
                                                        h1, h2, h3, h4,
                                                        packet.limit() - 4
                                        ));
                                    }
                                    packet.rewind();
                                }
                            }
                            processPacket(packet, packet.remaining());
                            packet = null;
                        } else {
                            // packet to be continued in the future...
                            if (packet == null) {
                                packet = ByteBuffer.allocate(MAX_RX_SIZE * 8);
                            }
                            int pl = packet.position();
                            if (log_rx_diagnostics) {
                                diagnostic_println("pos=" + pl + " cap=" + packet.capacity() + " sz=" + sz);
                            }
                            if (packet.capacity() > pl + sz) {
                                // fits
                                packet.put(recvbuffer);
                                packet.position(pl + sz);
                            } else {
                                // extend
                                ByteBuffer new_packet = ByteBuffer.allocate(packet.capacity() * 2 + sz);
                                new_packet.order(ByteOrder.LITTLE_ENDIAN);
                                packet.limit(packet.position());
                                packet.rewind();
                                new_packet.put(packet);
                                new_packet.put(recvbuffer);
                                new_packet.position(packet.position() + recvbuffer.position());
                                packet = new_packet;
                            }
                        }
                        break;
                    case LibUsb.ERROR_TIMEOUT:
                        break;
                    default:
                        String err = LibUsb.errorName(result);
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "receive error: {0}", err);
                        terminating = true;
                        disconnect();
                        break;
                }
            }
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receiver: thread stopped");
            if (!old_protocol) {
                disconnect();
            }
        }

        private void start() {
            receiverThread = new Thread(receiver);
            receiverThread.setName("Receiver");
            receiverThread.start();
        }
    }

    private Boolean isSDCardPresent = null;

    public void setSDCardPresent(boolean i) {
        if ((isSDCardPresent != null) && (i == isSDCardPresent)) {
            return;
        }
        isSDCardPresent = i;
        if (isSDCardPresent) {
            callbacks.showSDCardMounted();
        } else {
            callbacks.showSDCardUnmounted();
        }
    }

    @Override
    public boolean getSDCardPresent() {
        if (isSDCardPresent == null) {
            return false;
        }
        return isSDCardPresent;
    }

    private int fwcrc = -1;

    void acknowledge(final int DSPLoad, final int PatchID, final int Voltages, final int patchIndex, final int sdcardPresent) {
        setSDCardPresent(sdcardPresent != 0);
    }

    void acknowledge_v2(
            int DSPLoad,
            int PatchID,
            int Voltages,
            int patchIndex,
            int sdcardPresent,
            float inLevel1,
            float inLevel2,
            float outLevel1,
            float outLevel2,
            int underruns,
            int sram1_free,
            int sram3_free,
            int ccmsram_free,
            int sdram_free
    ) {
        TargetRTInfo rtinfo = new TargetRTInfo();
        rtinfo.inLevel1 = inLevel1;
        rtinfo.inLevel2 = inLevel2;
        rtinfo.outLevel1 = outLevel1;
        rtinfo.outLevel2 = outLevel2;
        rtinfo.underruns = underruns;
        rtinfo.dsp = DSPLoad;
        rtinfo.sram1_free = sram1_free;
        rtinfo.sram3_free = sram3_free;
        rtinfo.ccmsram_free = ccmsram_free;
        rtinfo.sdram_free = sdram_free;
        int vref = Voltages & 0xFFFF;
        int v50i = (Voltages >> 16) & 0xFFFF;
        if (vref != 0) {
            rtinfo.vdd = 1.21f * (float) (4096) / (float) (vref);
            rtinfo.v50 = 2.0f * rtinfo.vdd * (float) (v50i + 1) / 4096.0f;
            rtinfo.voltageAlert = false;
            if ((rtinfo.vdd < 3.0) || (rtinfo.vdd > 3.6)) {
                rtinfo.voltageAlert = true;
            }
            if ((rtinfo.v50 > 5.5) || (rtinfo.v50 < 4.5)) {
                rtinfo.voltageAlert = true;
            }
        }
        callbacks.setRTInfo(rtinfo);
        setSDCardPresent(sdcardPresent != 0);
    }

    private final byte[] fwversion = new byte[4];
    private int fw_chunkaddr;

    ByteBuffer patchGetDisp(int patchRef) throws IOException {
        synchronized (this) {
            byte[] txdata = createPacket(tx_hdr_patch_get_disp, patchRef);
            RcvPatchDisp rcvPatchDisp = new RcvPatchDisp();
            rcvPacketConsumer = rcvPatchDisp;
            transmitPacket1(txdata);
            try {
                ByteBuffer result = rcvPatchDisp.get(1000, TimeUnit.MILLISECONDS);
                return result;
            } catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    private static final int rx_hdr_acknowledge = 0x416F7841;  // "AxoA"
    private static final int rx_hdr_fwid = 0x566f7841;         // "AxoV"
    private static final int rx_hdr_log = 0x546F7841;          // "AxoT"
    public static final int rx_hdr_memrdx = 0x726f7841;       // "Axor"
    public static final int rx_hdr_patch_disp = 0x64507841;   // "AxPd"
    private static final int rx_hdr_patch_paramchange = 0x71507841;   // "AxPq"
    private static final int rx_hdr_patch_list = 0x6C507841;   // "AxPl"
    public static final int rx_hdr_result_ptr = 0x536F7841;   // "AxoS"

    public static final int rx_hdr_f_info = 0x69467841;    // "AxFi"
    public static final int rx_hdr_f_read = 0x72467841;    // "AxFr"
    public static final int rx_hdr_f_dir = 0x64467841;     // "AxFd"
    public static final int rx_hdr_f_dir_end = 0x44467841; // "AxFD"
    public static final int rx_hdr_f_result = 0x65467841;  // "AxFe"

    public static final int rx_hdr_cstring = 0x736F7841;   // "Axos"

    private final boolean log_rx_diagnostics = false;

    private void process_pckt_fwid(ByteBuffer rbuf) {
        if (log_rx_diagnostics) {
            diagnostic_println("rx hdr fwid");
        }
        fwversion[0] = rbuf.get();
        fwversion[1] = rbuf.get();
        fwversion[2] = rbuf.get();
        fwversion[3] = rbuf.get();
        int fwcrc1 = rbuf.getInt();
        fwcrc = fwcrc1;
        fw_chunkaddr = rbuf.getInt();
        String sFwcrc = String.format("%08X", fwcrc);
        Logger.getLogger(USBBulkConnection_v2.class.getName()).info(String.format("Firmware version: %d.%d.%d.%d, crc=0x%s",
                fwversion[0], fwversion[1], fwversion[2], fwversion[3], sFwcrc));
        firmwareID = sFwcrc;
    }

    private void process_pckt_log(ByteBuffer rbuf, int size) {
        if (log_rx_diagnostics) {
            diagnostic_println("rx hdr log");
        }
        CharBuffer textRcvBuffer = CharBuffer.allocate(rbuf.remaining());
        while (rbuf.remaining() > 0) {
            byte b = rbuf.get();
            if (b == 0) {
                break;
            }
            textRcvBuffer.append((char) b);
        }
        textRcvBuffer.limit(textRcvBuffer.position());
        textRcvBuffer.rewind();
        callbacks.showLogText(textRcvBuffer.toString());
    }

    private void process_pckt_patch_list(ByteBuffer rbuf) {
        List<Integer> patchIDs = new LinkedList<>();
        while (rbuf.remaining() >= 4) {
            int patchID = rbuf.getInt();
            patchIDs.add(patchID);
        }
        List<ILivePatch> lpStopped = new LinkedList<>();
        List<Integer> lpidStopped = new LinkedList<>();
        for (int patchID : patchMap.keySet()) {
            if (!patchIDs.contains(patchID)) {
                // it's stopped
                ILivePatch p = patchMap.get(patchID);
                lpStopped.add(p);
                lpidStopped.add(patchID);
            }
        }
        for (ILivePatch stoppedPatch : lpStopped) {
            stoppedPatch.getCallbacks().patchStopped();
        }
        for (Integer patchID : lpidStopped) {
            patchMap.remove(patchID);
        }
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            List<ILivePatch> resolvedPatches = new LinkedList<>();
            for (int patchID : patchIDs) {
                ILivePatch lp = patchMap.get(patchID);
                if (lp != null) {
                    resolvedPatches.add(lp);
                } else {
                    String patchName;
                    try {
                        patchName = patchGetName(patchID);
                    } catch (IOException ex) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                        patchName = "???";
                    }
                    try {
                        lp = new LivePatch(patchID, new IPatchCB() {
                            @Override
                            public void patchStopped() {
                            }

                            @Override
                            public void setDspLoad(int dspLoad) {
                            }

                            @Override
                            public void paramChange(int index, int value) {
                            }

                            @Override
                            public void distributeDataToDisplays(ByteBuffer dispData) {
                            }

                            @Override
                            public void openEditor() {
                            }
                        }, patchName, this);
                    } catch (IOException ex) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    resolvedPatches.add(lp);
                    patchMap.put(patchID, lp);
                }
            }

            callbacks.patchListChanged(Collections.unmodifiableList(resolvedPatches));
        });
        t.start();
    }

    private void process_pckt_paramchange(ByteBuffer rbuf) {
        int patchID = rbuf.getInt();
        int value = rbuf.getInt();
        int index = rbuf.getInt();
        ILivePatch patch = patchMap.get(patchID);
        IPatchCB pcb = patch.getCallbacks();
        if (pcb != null) {
            pcb.paramChange(index, value);
        } else {
            throw new IllegalStateException("patchID not found");
        }
    }

    private void process_pckt_ack(ByteBuffer rbuf) {
        if (log_rx_diagnostics) {
            diagnostic_println("rx hdr ack");
        }
        int ackversion = rbuf.getInt();
        if (ackversion == 0) {
            int i1 = rbuf.getInt();
            int i2 = rbuf.getInt();
            int i3 = rbuf.getInt();
            int i4 = rbuf.getInt();
            int i5 = rbuf.getInt();
            //  System.out.println(String.format("vu %08X",i0));
            acknowledge(i1, i2, i3, i4, i5);
        } else if (ackversion == 1) {
            int i1 = rbuf.getInt();
            int i2 = rbuf.getInt();
            int i3 = rbuf.getInt();
            int i4 = rbuf.getInt();
            int i5 = rbuf.getInt();
            float vuIn1 = rbuf.getFloat();
            float vuIn2 = rbuf.getFloat();
            float vuOut1 = rbuf.getFloat();
            float vuOut2 = rbuf.getFloat();
            int underruns = rbuf.getInt();
            int sram1_free = 0;
            int sram3_free = 0;
            int ccmsram_free = 0;
            int sdram_free = 0;
            if (rbuf.remaining() >= 16) {
                sram1_free = rbuf.getInt();
                sram3_free = rbuf.getInt();
                ccmsram_free = rbuf.getInt();
                sdram_free = rbuf.getInt();
            }
            acknowledge_v2(i1, i2, i3, i4, i5, vuIn1, vuIn2, vuOut1, vuOut2, underruns,
                    sram1_free, sram3_free, ccmsram_free, sdram_free);
        }
    }

    volatile IRcvPacketConsumer rcvPacketConsumer;

    void processPacket(ByteBuffer rbuf, int size) {
        rbuf.rewind();
        IRcvPacketConsumer rcvPacketConsumer1 = rcvPacketConsumer;
        if (rcvPacketConsumer1 != null) {
            try {
                boolean isConsumed = rcvPacketConsumer1.processPacket(rbuf);
                if (isConsumed) {
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        rbuf.rewind();
        if (size >= 4) {
            int header = rbuf.getInt();
            switch (header) {
                case rx_hdr_acknowledge:
                    process_pckt_ack(rbuf);
                    break;
                case rx_hdr_fwid:
                    process_pckt_fwid(rbuf);
                    break;
                case rx_hdr_log:
                    process_pckt_log(rbuf, size);
                    break;
                case rx_hdr_patch_list:
                    process_pckt_patch_list(rbuf);
                    break;
                case rx_hdr_patch_paramchange:
                    process_pckt_paramchange(rbuf);
                    break;
                case 0x00416f78: {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Old version of firmware (1.x) detected");
                    while (rbuf.hasRemaining()) {
                        byte b = rbuf.get();
                        diagnostic_println(String.format("   fw 1.x data %02x (%c)", b, (char) b));
                    }
                    old_protocol = true;
                }
                break;
                default: {
                    diagnostic_println(String.format("lost header %08x (%c%c%c%c)", header,
                            (char) (byte) (header), (char) (byte) (header >> 8), (char) (byte) (header >> 16), (char) (byte) (header >> 24)));
                    while (rbuf.hasRemaining()) {
                        byte b = rbuf.get();
                        diagnostic_println(String.format("   data %02x (%c)", b, (char) b));
                    }
                }
            }
        }
    }

    private PeriodicPinger pinger;

    private class PeriodicPinger implements Runnable {

        private Thread pingerThread;
        private boolean terminating = false;

        public void terminate() {
            terminating = true;
            pingerThread.interrupt();
        }

        @Override
        public void run() {
            while (!terminating) {
                try {
                    transmitPing();
                    Runnable pollers[] = TargetModel.getTargetModel().getPollers().toArray(new Runnable[0]);
                    for (Runnable poller : pollers) {
                        poller.run();
                    }
                } catch (IOException ex) {
                    if (!terminating) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "disconnecting, reason: {0}", ex.getMessage());
                    }
                    disconnect();
                }
                try {
                    Thread.sleep(Preferences.getPreferences().getPollInterval());
                } catch (InterruptedException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }

        private void start() {
            pingerThread = new Thread(pinger);
            pingerThread.setName("PingerThread");
            pingerThread.start();
        }
    }

    @Override
    public axoloti_core getTargetProfile() {
        return targetProfile;
    }

}
