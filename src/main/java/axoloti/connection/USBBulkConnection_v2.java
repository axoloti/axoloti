/**
 * Copyright (C) 2017 Johannes Taelman
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
import axoloti.job.IJobContext;
import axoloti.live.patch.PatchViewLive;
import axoloti.live.patch.parameter.ParameterInstanceLiveView;
import axoloti.patch.PatchModel;
import axoloti.preferences.Preferences;
import axoloti.swingui.dialogs.USBPortSelectionDlg;
import axoloti.target.TargetModel;
import axoloti.target.TargetRTInfo;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import axoloti.targetprofile.axoloti_core;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.usb4java.*;

/**
 *
 * @author Johannes Taelman
 */
public class USBBulkConnection_v2 extends IConnection {

    private PatchViewLive patch;
    private boolean disconnectRequested;
    private boolean connected;
    private Receiver receiver;
    private Thread receiverThread;
    private String cpuid;
    private axoloti_core targetProfile;
    private DeviceHandle handle;
    private final int interfaceNumber = 2;
    private String firmwareID;
    private boolean old_protocol = false;

    protected USBBulkConnection_v2(TargetModel targetModel) {
        super(targetModel);
        this.patch = null;
        disconnectRequested = false;
        connected = false;
    }

    @Override
    public void setPatch(PatchViewLive patchViewCodegen) {
        if ((patchViewCodegen == null)
                && (this.patch != null)) {
            patch.dispose();
        }
        this.patch = patchViewCodegen;
    }

    @Override
    public boolean isConnected() {
        return connected && (!disconnectRequested);
    }

    private void disconnect1() {
        goIdleState();
        if (connected) {
            disconnectRequested = true;
            connected = false;
            isSDCardPresent = null;
            if (this.patch != null) {
                patch.dispose();
                patch = null;
            }
            showDisconnect();
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Disconnect request");

            receiver.terminate();
            if (receiverThread.isAlive()) {
                receiverThread.interrupt();
                try {
                    receiverThread.join();
                } catch (InterruptedException ex) {
                    //Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            int result = LibUsb.releaseInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to release interface", result);
            }

            LibUsb.close(handle);
            handle = null;
            if (pinger != null) {
                pinger.terminate();
            }
        }
    }

    @Override
    public void disconnect() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                disconnect1();
            });
        } else {
            disconnect1();
        }
        for (CompletableFuture x : new CompletableFuture[]{
            futureByteBuffer,
            fileListFuture,
            sdFileInfoFuture
        }) {
            if (x != null && !x.isCancelled()) {
                x.cancel(true);
            }
        }

    }

    private byte[] bb2ba(ByteBuffer bb) {
        bb.rewind();
        byte[] r = new byte[bb.remaining()];
        bb.get(r, 0, r.length);
        return r;
    }

    private int cpuCode;
    private ByteBuffer cpuSerial;
    private ByteBuffer otpInfo;
    private ByteBuffer signature;

    @Override
    public boolean connect(String _cpuid) {
        disconnect();
        old_protocol = false;
        disconnectRequested = false;
        goIdleState();
        targetProfile = new axoloti_core();
        handle = openDeviceHandle(_cpuid);
        if (handle == null) {
            showDisconnect();
            return false;
        }

        try //devicePath = Usb.DeviceToPath(device);
        {
            int result = LibUsb.claimInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }

            goIdleState();
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "creating rx and tx thread...");
            receiver = new Receiver();
            receiverThread = new Thread(receiver);
            receiverThread.setName("Receiver");
            receiverThread.start();
            connected = true;
            transmitPing();
            transmitPing();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (old_protocol) {
//                throw new Error("old protocol");
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, "upgrading...");
                disconnect1();
                int r = JOptionPane.showConfirmDialog((Component) null, "Firmware version 1.0.12 detected, upgrade to experimental firmware?",
                        "alert", JOptionPane.OK_CANCEL_OPTION);
                if (r == JOptionPane.OK_OPTION) {
                    handle = openDeviceHandle(_cpuid);
                    FirmwareUpgrade_1_0_12 fwUpgrade = new FirmwareUpgrade_1_0_12(handle);
                }
                return false;
            }
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "connected");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            showConnect();

            pinger = new PeriodicPinger();
            pingerThread = new Thread(pinger);
            pingerThread.setName("PingerThread");
            pingerThread.start();
            return true;

        } catch (Exception ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            showDisconnect();
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

    @Override
    public void transmitPacket(byte[] data) throws IOException {
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
            if (data.length >= 4) {
                diagnostic_println(String.format("->  %c%c%c%c  %5d",
                        (char) data[0],
                        (char) data[1],
                        (char) data[2],
                        (char) data[3],
                        data.length - 4));
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
    public void transmitRecallPreset(int presetNo) throws IOException {
        synchronized (this) {
            byte[] data = {'A', 'x', 'o', 'T', (byte) presetNo};
            transmitPacket1(data);
        }
    }

    @Override
    public void bringToDFU() throws IOException {
        synchronized (this) {
            transmitPacket1(dfuPckt);
        }
    }

    @Override
    public void transmitGetFWVersion() throws IOException {
        synchronized (this) {
            transmitPacket1(fwVersionPckt);
        }
    }

    @Override
    public void sendMidi(int cable, byte m0, byte m1, byte m2) throws IOException {
        synchronized (this) {
            // CIN for everyting except sysex
            byte cin = (byte) ((m0 & 0xF0) >> 4);
            byte ph = (byte) (((cable & 0x0F) << 4) | cin);
            byte data[] = {'A', 'x', 'o', 'M', ph, m0, m1, m2};
            transmitPacket1(data);
        }
    }

    @Override
    public void sendUpdatedPreset(byte[] b) throws IOException {
        synchronized (this) {
            byte[] data = new byte[8];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'R';
            int len = b.length;
            data[4] = (byte) len;
            data[5] = (byte) (len >> 8);
            data[6] = (byte) (len >> 16);
            data[7] = (byte) (len >> 24);
            transmitPacket1(data);
            transmitPacket1(b);
        }
    }

    @Override
    public void selectPort() {
        USBPortSelectionDlg spsDlg = new USBPortSelectionDlg(null, true, cpuid, getDModel());
        spsDlg.setVisible(true);
        cpuid = spsDlg.getCPUID();
        String name = Preferences.getPreferences().getBoardName(cpuid);
        if (name == null) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "port: {0}", cpuid);
        } else {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "port: {0} name: {1}", new Object[]{cpuid, name});
        }
    }

    final private int timeOutMs = 2000;
    final private int timeOutFileListMs = 2000;

    /**
     * Get file info from target filesystem. Returns null if the file does not
     * exist or if an error happened.
     *
     * @param filename
     * @return
     * @throws IOException
     */
    @Override
    public SDFileInfo getFileInfo(String filename) throws IOException {
        synchronized (this) {
            final CompletableFuture<SDFileInfo> _sdFileInfoFuture = new CompletableFuture<>();
            sdFileInfoFuture = _sdFileInfoFuture;
            byte[] data = new byte[15 + filename.length()];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'C';
            data[4] = 0;
            data[5] = 0;
            data[6] = 0;
            data[7] = 0;
            data[8] = 0;
            data[9] = 'I';
            data[10] = 0;
            data[11] = 0;
            data[12] = 0;
            data[13] = 0;
            int i = 14;
            for (int j = 0; j < filename.length(); j++) {
                data[i++] = (byte) filename.charAt(j);
            }
            data[i] = 0;
            transmitPacket1(data);
            try {
                SDFileInfo sdfi = _sdFileInfoFuture.get(timeOutMs, TimeUnit.MILLISECONDS);
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
        // TODO: (low priority) implement IJobContext
        synchronized (this) {
            CompletableFuture<ByteBuffer> fbb = new CompletableFuture<>();
            futureByteBuffer = fbb;
            byte[] data = new byte[15 + filename.length()];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'C';
            data[4] = 0;
            data[5] = 0;
            data[6] = 0;
            data[7] = 0;
            data[8] = 0;
            data[9] = 'c';
            data[10] = 0;
            data[11] = 0;
            data[12] = 0;
            data[13] = 0;
            int i = 14;
            for (int j = 0; j < filename.length(); j++) {
                data[i++] = (byte) filename.charAt(j);
            }
            data[i] = 0;
            transmitPacket1(data);
            try {
                ByteBuffer bb = fbb.get(timeOutMs * 20, TimeUnit.MILLISECONDS);
                return bb;
            } catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public String getFWID() {
        return firmwareID;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void dispose() {
    }

    private volatile LinkedList<SDFileInfo> fileList;
    private volatile CompletableFuture<ByteBuffer> futureByteBuffer = null;
    private volatile CompletableFuture<SDCardInfo> fileListFuture = null;
    private volatile CompletableFuture<SDFileInfo> sdFileInfoFuture = null;

    @Override
    public SDCardInfo getFileList() throws IOException {
        synchronized (this) {
            fileList = new LinkedList<>();
            fileListFuture = new CompletableFuture<>();
            if (log_rx_diagnostics) {
                diagnostic_println("filelist req");
            }
            transmitPacket1(getFileListPckt);
            try {
                return fileListFuture.get(timeOutFileListMs, TimeUnit.MILLISECONDS);
            } catch (ExecutionException | InterruptedException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
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
        transmitCreateFile(filename, size, ts);
        int remLength = size;
        int MaxBlockSize = 32768;
        int pct = 0;
        do {
            byte[] buffer = new byte[MaxBlockSize];
            int nRead = inputStream.read(buffer);
            if (nRead == MaxBlockSize) {
                transmitAppendFile(buffer);
            } else if (nRead > 0) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, 0, nRead);
                byte[] b2 = new byte[nRead];
                bb.get(b2);
                transmitAppendFile(b2);
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
        transmitCloseFile();
    }

    private final byte[] startPckt = {'A', 'x', 'o', 's'};
    private final byte[] stopPckt = {'A', 'x', 'o', 'S'};
    private final byte[] pingPckt = {'A', 'x', 'o', 'p'};
    private final byte[] getFileListPckt = {'A', 'x', 'o', 'd'};
    private final byte[] copyToFlashPckt = {'A', 'x', 'o', 'F'};
    private final byte[] dfuPckt = {'A', 'x', 'o', 'D'};
    private final byte[] fwVersionPckt = {'A', 'x', 'o', 'V', '2'};

    @Override
    public void transmitStart() throws IOException {
        synchronized (this) {
            transmitPacket1(startPckt);
        }
    }

    @Override
    public void transmitStart(String patchName) throws IOException {
        synchronized (this) {
            byte b[] = new byte[4 + patchName.length() + 1];
            b[0] = startPckt[0];
            b[1] = startPckt[1];
            b[2] = startPckt[2];
            b[3] = startPckt[3];
            int i;
            for (i = 0; i < patchName.length(); i++) {
                b[i + 4] = (byte) patchName.charAt(i);
            }
            b[i + 4] = (byte) 0;
            transmitPacket1(b);
        }
    }

    @Override
    public void transmitStart(int patchIndex) throws IOException {
        synchronized (this) {
            byte b[] = new byte[8];
            b[0] = startPckt[0];
            b[1] = startPckt[1];
            b[2] = startPckt[2];
            b[3] = startPckt[3];
            int i;
            for (i = 0; i < 4; i++) {
                b[i + 4] = (byte) (patchIndex);
                patchIndex >>= 8;
            }
            transmitPacket1(b);
        }
    }

    @Override
    public void transmitStop() throws IOException {
        synchronized (this) {
            if (this.patch != null) {
                patch.dispose();
                patch = null;
            }
            transmitPacket1(stopPckt);
        }
    }

    @Override
    public void transmitPing() throws IOException {
        ByteBuffer mem = null;
        synchronized (this) {
            transmitPacket1(pingPckt);
        }
    }

    @Override
    public void transmitCopyToFlash() throws IOException {
        synchronized (this) {
            transmitPacket1(copyToFlashPckt);
        }
    }

    @Override
    public void write(int address, byte[] data) throws IOException {
        synchronized (this) {
            byte[] cmd = new byte[12];
            cmd[0] = 'A';
            cmd[1] = 'x';
            cmd[2] = 'o';
            cmd[3] = 'W';
            int tvalue = address;
            int nRead = data.length;
            cmd[4] = (byte) tvalue;
            cmd[5] = (byte) (tvalue >> 8);
            cmd[6] = (byte) (tvalue >> 16);
            cmd[7] = (byte) (tvalue >> 24);
            cmd[8] = (byte) (nRead);
            cmd[9] = (byte) (nRead >> 8);
            cmd[10] = (byte) (nRead >> 16);
            cmd[11] = (byte) (nRead >> 24);
            transmitPacket1(cmd);
            transmitPacket1(data);
            // Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "block uploaded @ 0x{0} length {1}", new Object[]{Integer.toHexString(address).toUpperCase(), Integer.toString(data.length)});
        }
    }

    @Override
    public void write(int address, File f) throws FileNotFoundException, IOException {
        int tlength = (int) f.length();
        try (FileInputStream inputStream = new FileInputStream(f)) {

            int offset = 0;
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
                int nRead = inputStream.read(buffer, 0, l);
                if (nRead != l) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
                }
                write(address + offset, buffer);
                offset += nRead;
            } while (tlength > 0);
        }
    }

    @Override
    public void transmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3)
            throws IOException {
        synchronized (this) {
            byte[] data = new byte[]{
                'A', 'x', 'o', 'B', b0, b1, b2, b3
            };
            transmitPacket1(data);
        }
    }

    private void transmitCreateFile(String filename, int size, Calendar date) throws IOException {
        synchronized (this) {
            byte[] data = new byte[15 + filename.length()];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'C';
            data[4] = (byte) size;
            data[5] = (byte) (size >> 8);
            data[6] = (byte) (size >> 16);
            data[7] = (byte) (size >> 24);
            data[8] = 0;
            data[9] = 'f';
            int dy = date.get(Calendar.YEAR);
            int dm = date.get(Calendar.MONTH) + 1;
            int dd = date.get(Calendar.DAY_OF_MONTH);
            int th = date.get(Calendar.HOUR_OF_DAY);
            int tm = date.get(Calendar.MINUTE);
            int ts = date.get(Calendar.SECOND);
            int t = ((dy - 1980) * 512) | (dm * 32) | dd;
            int d = (th * 2048) | (tm * 32) | (ts / 2);
            data[10] = (byte) (t & 0xff);
            data[11] = (byte) (t >> 8);
            data[12] = (byte) (d & 0xff);
            data[13] = (byte) (d >> 8);
            int i = 14;
            for (int j = 0; j < filename.length(); j++) {
                data[i++] = (byte) filename.charAt(j);
            }
            data[i] = 0;
            transmitPacket1(data);
        }
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        synchronized (this) {
            byte[] data = new byte[15 + filename.length()];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'C';
            data[4] = 0;
            data[5] = 0;
            data[6] = 0;
            data[7] = 0;
            data[8] = 0;
            data[9] = 'D';
            data[10] = 0;
            data[11] = 0;
            data[12] = 0;
            data[13] = 0;
            int i = 14;
            for (int j = 0; j < filename.length(); j++) {
                data[i++] = (byte) filename.charAt(j);
            }
            data[i] = 0;
            transmitPacket1(data);
        }
    }

    @Override
    public void transmitChangeWorkingDirectory(String path) throws IOException {
        synchronized (this) {
            byte[] data = new byte[15 + path.length()];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'C';
            data[4] = 0;
            data[5] = 0;
            data[6] = 0;
            data[7] = 0;
            data[8] = 0;
            data[9] = 'C';
            data[10] = 0;
            data[11] = 0;
            data[12] = 0;
            data[13] = 0;
            int i = 14;
            for (int j = 0; j < path.length(); j++) {
                data[i++] = (byte) path.charAt(j);
            }
            data[i] = 0;
            transmitPacket1(data);
        }
    }

    @Override
    public void createDirectory(String filename, Calendar date) throws IOException {
        synchronized (this) {
            byte[] data = new byte[15 + filename.length()];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'C';
            data[4] = 0;
            data[5] = 0;
            data[6] = 0;
            data[7] = 0;
            data[8] = 0;
            data[9] = 'd';
            data[10] = 0;
            data[11] = 0;
            data[12] = 0;
            data[13] = 0;

            int i = 14;
            for (int j = 0; j < filename.length(); j++) {
                data[i++] = (byte) filename.charAt(j);
            }
            data[i] = 0;
            transmitPacket1(data);
        }
    }

    private void transmitAppendFile(byte[] buffer) throws IOException {
        synchronized (this) {
            final int size = buffer.length;
            final byte[] data = new byte[]{
                'A', 'x', 'o', 'A',
                (byte) size,
                (byte) (size >> 8),
                (byte) (size >> 16),
                (byte) (size >> 24)
            };
            //        Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "append size: " + buffer.length);
            transmitPacket1(data);
            transmitPacket1(buffer);
        }
    }

    private void transmitCloseFile() throws IOException {
        synchronized (this) {
            byte[] data = new byte[]{
                'A', 'x', 'o', 'c'
            };
            transmitPacket1(data);
        }
    }

    @Override
    public ByteBuffer read(int addr, int length) throws IOException {
        assert (length > 0);
        if (((addr >= 0x00000000) && (addr < 0x00100000) && (addr + length >= 0x00100000))
                || ((addr >= 0x20000000) && (addr < 0x20100000) && (addr + length >= 0x20100000))
                || ((addr >= 0x08000000) && (addr < 0x08100000) && (addr + length >= 0x08100000))
                || ((addr >= 0x1FFF0000) && (addr < 0x1FFFFFFF) && (addr + length >= 0x1FFFFFFF))) {
            throw new IOException("address out of range : " + String.format("0x%08X", addr));
        }
        synchronized (this) {
            CompletableFuture<ByteBuffer> fbb = new CompletableFuture<>();
            futureByteBuffer = fbb;
            if (log_rx_diagnostics) {
                diagnostic_println(String.format("read %08X  %X", addr, length));
            }
            byte[] data = new byte[12];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'r';
            data[4] = (byte) addr;
            data[5] = (byte) (addr >> 8);
            data[6] = (byte) (addr >> 16);
            data[7] = (byte) (addr >> 24);
            data[8] = (byte) length;
            data[9] = (byte) (length >> 8);
            data[10] = (byte) (length >> 16);
            data[11] = (byte) (length >> 24);
            transmitPacket1(data);

            ByteBuffer result = null;
            try {
                result = fbb.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException ex) {
                throw new IllegalStateException(ex);
            } catch (TimeoutException ex) {
                throw new IOException(ex);
            }
            if (log_rx_diagnostics) {
                diagnostic_println(String.format("read result: %s", result));
            }
            return result;
        }
    }

    private class Receiver implements Runnable {

        final static int MAX_RX_SIZE = 4096;
        // larger than 4096 will give "WARN Event TRB for slot 1 ep 4 with no TDs queued" in linux kernel log

        volatile boolean terminating = false;

        public void terminate() {
            terminating = true;
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
                                if (sz >= 4) {
                                    diagnostic_println(String.format("<- %c%c%c%c           %4d",
                                            (char) packet.get(),
                                            (char) packet.get(),
                                            (char) packet.get(),
                                            (char) packet.get(),
                                            packet.limit() - 4
                                    ));
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
                        if (state != ReceiverState.header) {
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "timeout: {0}", state);
                        }
                        break;
                    default:
                        String err = LibUsb.errorName(result);
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "receive error: {0}", err);
                        disconnectRequested = true;
                        goIdleState();
                        terminate();
                        disconnect();
                        break;
                }
            }
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receiver: thread stopped");
            disconnect();
        }
    }

    private Boolean isSDCardPresent = null;

    public void setSDCardPresent(boolean i) {
        if ((isSDCardPresent != null) && (i == isSDCardPresent)) {
            return;
        }
        isSDCardPresent = i;
        if (isSDCardPresent) {
            showSDCardMounted();
        } else {
            showSDCardUnmounted();
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
        SwingUtilities.invokeLater(() -> {
                if (patch != null) {
                    if ((getPatchModel().getIID() != PatchID) && getPatchModel().getLocked()) {
                        if (PatchID != 0) {
                            patch.getDModel().getController().setLocked(false);
                        } else {
                            // TODO : verify!
                        }
                    } else {
                        patch.getDModel().getController().setDspLoad(DSPLoad);
                    }
                }
//                MainFrame.mainframe.showPatchIndex(patchIndex);
                setSDCardPresent(sdcardPresent != 0);
        });
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
            int underruns
    ) {
        SwingUtilities.invokeLater(() -> {
                if (patch != null) {
                    if ((getPatchModel().getIID() != PatchID) && getPatchModel().getLocked()) {
                        // TODO: verify patchID
//                       patch.getDModel().getController().setLocked(false);
                    } else {
                        patch.getDModel().getController().setDspLoad(DSPLoad);
                    }
                }

                TargetRTInfo rtinfo = new TargetRTInfo();
                rtinfo.inLevel1 = inLevel1;
                rtinfo.inLevel2 = inLevel2;
                rtinfo.outLevel1 = outLevel1;
                rtinfo.outLevel2 = outLevel2;
                rtinfo.underruns = underruns;
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
                getDModel().setRTInfo(rtinfo);
                getDModel().setPatchIndex(patchIndex);
                setSDCardPresent(sdcardPresent != 0);
        });
    }

    void RPacketParamChange(final int index, final int value, final int patchID) {
        SwingUtilities.invokeLater(() -> {
                if (patch == null) {
                    //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "Rx paramchange patch null {0} {1}", new Object[]{index, value});
                    return;
                }
                if (!getPatchModel().getLocked()) {
                    return;
                }
                if (getPatchModel().getIID() != patchID) {
                    getPatchModel().getController().setLocked(false);
                    return;
                }
                if (index >= patch.getParameterInstances().size()) {
                    Logger.getLogger(USBBulkConnection_v2.class
                            .getName()).log(Level.INFO, "Rx paramchange index out of range{0} {1}", new Object[]{index, value});

                    return;
                }
                ParameterInstanceLiveView pi = patch.getParameterInstances().get(index);

                if (pi == null) {
                    Logger.getLogger(USBBulkConnection_v2.class
                            .getName()).log(Level.INFO, "Rx paramchange parameterInstance null{0} {1}", new Object[]{index, value});
                    return;
                }

                if (!pi.getNeedsTransmit()) {
                    pi.getDModel().setValue(pi.getDModel().int32ToVal(value));
                }

//                System.out.println("rcv ppc objname:" + pi.axoObj.getInstanceName() + " pname:"+ pi.name);
        });

    }

    private enum ReceiverState {

        header,
        ackPckt, // general acknowledge
        paramchangePckt, // parameter changed
        lcdPckt, // lcd screen bitmap readback
        displayPcktHdr, // object display readbac
        displayPckt, // object display readback
        textPckt, // text message to display in log
        sdinfo, // sdcard info
        fileinfo, // file listing entry
        memread, // one-time programmable bytes
        memread1word, // one-time programmable bytes
        fwversion
    };
    /*
     Protocol documentation:
     "AxoP" + bb + vvvv -> parameter change index bb (16bit), value vvvv (32bit)
     */
    private ReceiverState state = ReceiverState.header;
    private int dataLength = 0; // in bytes
    private final CharBuffer textRcvBuffer = CharBuffer.allocate(256);

    private int memReadAddr;
    private int memReadLength;
    private int memReadValue;
    private final byte[] fwversion = new byte[4];
    private int fw_chunkaddr;

    void displayPackHeader(int i1, int i2) {
        if (i2 > 1024) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.FINE, "Lots of data coming! {0} / {1}", new Object[]{Integer.toHexString(i1), Integer.toHexString(i2)});
        } else {
//            Logger.getLogger(SerialConnection.class.getName()).info("OK! " + Integer.toHexString(i1) + " / " + Integer.toHexString(i2));
        }
        if (i2 > 0) {
            dataLength = i2 * 4;
            dispData = ByteBuffer.allocate(dataLength);
            dispData.order(ByteOrder.LITTLE_ENDIAN);
            state = ReceiverState.displayPckt;
        } else {
            goIdleState();
        }
    }

    void goIdleState() {
        state = ReceiverState.header;
    }
    private ByteBuffer dispData;

    final int tx_hdr_acknowledge = 0x416F7841;  // "AxoA"
    final int tx_hdr_fwid = 0x566f7841;         // "AxoV"
    final int tx_hdr_log = 0x546F7841;          // "AxoT"
    final int tx_hdr_memrd32 = 0x796f7841;      // "Axoy"
    final int tx_hdr_memrdx = 0x726f7841;       // "Axor"
    final int rx_hdr_displaypckt = 0x446F7841;  // "AxoD"
    final int rx_hdr_paramchange = 0x516F7841;  // "AxoQ"
    final int rx_hdr_sdcardinfo = 0x646F7841;   // "Axod"
    final int rx_hdr_fileinfo = 0x666F7841;     // "Axof"
    final int tx_hdr_filecontents = 0x466F7841; // "AxoF"

    final boolean log_rx_diagnostics = false;
    private boolean receiving_full_filelist = false;

    void processPacket(ByteBuffer rbuf, int size) {
        if (size == 0) {
            goIdleState();
            return;
        }
        rbuf.rewind();
        switch (state) {
            case header: {
                if (size >= 4) {
                    int header = rbuf.getInt();
                    switch (header) {
                        case tx_hdr_acknowledge: {
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
                                //                            System.out.println(String.format("vu %08X",i0));
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
                                acknowledge_v2(i1, i2, i3, i4, i5, vuIn1, vuIn2, vuOut1, vuOut2, underruns);
                            }
                            goIdleState();
                        }
                        break;
                        case tx_hdr_memrd32: {
                            if (log_rx_diagnostics) {
                                diagnostic_println("rx hdr memrd32");
                            }
                            memReadAddr = rbuf.getInt();
                            memReadValue = rbuf.getInt();
                            goIdleState();
                        }
                        break;
                        case tx_hdr_fwid: {
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
                            goIdleState();
                        }
                        break;
                        case tx_hdr_log: {
                            if (log_rx_diagnostics) {
                                diagnostic_println("rx hdr log");
                            }
                            textRcvBuffer.rewind();
                            textRcvBuffer.limit(textRcvBuffer.capacity());
                            if (size == 4) {
                                state = ReceiverState.textPckt;
                                break;
                            }
                            while (rbuf.remaining() > 0) {
                                byte b = rbuf.get();
                                if (b == 0) {
                                    break;
                                }
                                textRcvBuffer.append((char) b);
                            }
                            textRcvBuffer.limit(textRcvBuffer.position());
                            textRcvBuffer.rewind();
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                        }
                        break;
                        case tx_hdr_memrdx:
                            memReadAddr = rbuf.getInt();
                            memReadLength = rbuf.getInt();
                            if (log_rx_diagnostics) {
                                System.out.print("rx memrd addr=" + String.format("0x%08X", memReadAddr) + " le=" + memReadLength + " [");
                                for (int i = 12; i < size; i++) {
                                    // this would be unexpected extra data...
                                    System.out.print("|" + (char) rbuf.get(i));
                                    if (i > 100) {
                                        System.out.println("|...truncated");
                                        break;
                                    }
                                }
                                System.out.println("]");
                            }
                            state = ReceiverState.memread;
                            break;
                        case tx_hdr_filecontents:
                            if (log_rx_diagnostics) {
                                diagnostic_println("tx_hdr_filecontents");
                            }
                            memReadLength = rbuf.getInt();
                            state = ReceiverState.memread;
                            break;
                        case rx_hdr_displaypckt:
                            if (log_rx_diagnostics) {
                                //System.out.println("rx displaypckt deprecated");
                            }
                            int z = rbuf.getInt(); // expected 0
                            int n = rbuf.getInt(); // size in number of 32 bit words
                            break;
                        case rx_hdr_paramchange: {
                            int patchID = rbuf.getInt();
                            int value = rbuf.getInt();
                            int index = rbuf.getInt();
                            RPacketParamChange(index, value, patchID);
                        }
                        break;
                        case rx_hdr_sdcardinfo: {
                            int fname = rbuf.getInt(); // ???
                            int sz = rbuf.getInt();
                            int timestamp = rbuf.getInt();
                            receiving_full_filelist = true;
                        }
                        break;
                        case rx_hdr_fileinfo: {
                            int sz = rbuf.getInt();
                            int timestamp = rbuf.getInt();
                            CharBuffer cb = Charset.forName("ISO-8859-1").decode(rbuf);
                            String fname = cb.toString();
                            // strip trailing null
                            if (fname.charAt(fname.length() - 1) == (char) 0) {
                                fname = fname.substring(0, fname.length() - 1);
                            }
                            if (log_rx_diagnostics) {
                                diagnostic_println("rx_hdr_fileinfo fn:[" + fname + "], sz:" + sz);
                            }
                            if (receiving_full_filelist) {
                                if (fname.equals("/")) {
                                    // terminates the list
                                    SDCardInfo sdci = new SDCardInfo(0, 0, 0, fileList);
                                    fileListFuture.complete(sdci);
                                    receiving_full_filelist = false;
                                } else {
                                    SDFileInfo f = new SDFileInfo(fname, sz, timestamp);
                                    fileList.add(f);
                                }
                            } else {
                                final CompletableFuture<SDFileInfo> sdfi = sdFileInfoFuture;
                                sdFileInfoFuture = null;
                                sdfi.complete(new SDFileInfo(fname, sz, timestamp));
                            }
                        }
                        break;
                        case 0x00416f78: {
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Old version of firmware (1.x) detected");
                            while (rbuf.hasRemaining()) {
                                byte b = rbuf.get();
                                diagnostic_println(String.format("   fw 1.x data %02x (%c)", b, (char) b));
                            }
                            old_protocol = true;
                            disconnectRequested = true;
                        }
                        break;
                        default:
                            diagnostic_println(String.format("lost header %08x (%c%c%c%c)", header,
                                    (char) (byte) (header), (char) (byte) (header >> 8), (char) (byte) (header >> 16), (char) (byte) (header >> 24)));
                            while (rbuf.hasRemaining()) {
                                byte b = rbuf.get();
                                diagnostic_println(String.format("   data %02x (%c)", b, (char) b));
                            }
                    }
                }
            }
            break;
            case textPckt: {
                while (rbuf.remaining() > 0) {
                    byte b = rbuf.get();
                    if (b == 0) {
                        textRcvBuffer.limit(textRcvBuffer.position());
                        textRcvBuffer.rewind();
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                        goIdleState();
                    } else {
                        if (textRcvBuffer.position() < textRcvBuffer.limit()) {
                            textRcvBuffer.append((char) b);
                        } else {
                            diagnostic_println("textRcvBuffer overflow :" + (char) b);
                            textRcvBuffer.limit(textRcvBuffer.position());
                            textRcvBuffer.rewind();
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                            textRcvBuffer.limit(textRcvBuffer.capacity());
                            textRcvBuffer.rewind();
                            textRcvBuffer.append((char) b);
                        }
                    }
                }
                goIdleState();
            }
            break;
            case memread: {
                if (memReadLength != size) {
                    diagnostic_println("memread barf:" + memReadLength + ":" + size + "<");
//                    rbuf.position(memReadLength);
                    rbuf.rewind();
                    int i = 0;
                    while (rbuf.hasRemaining()) {
                        System.out.print("|" + (char) rbuf.get());
                        i++;
                        if (i > 100) {
                            System.out.println("|...truncated");
                            break;
                        }
                    }
                    System.out.println(">");
                }
                if (log_rx_diagnostics) {
//                  diagnostic_println("rx memrd recv'd sz=" + size + " " + memReadHandler);
                }
                byte memr[] = new byte[memReadLength];
                rbuf.get(memr, 0, memReadLength);
                ByteBuffer mrb = ByteBuffer.wrap(memr);
                mrb.order(ByteOrder.LITTLE_ENDIAN);
                mrb.rewind();
                CompletableFuture<ByteBuffer> cfbb = futureByteBuffer;
                futureByteBuffer = null;
                cfbb.complete(mrb);
                goIdleState();
            }
            break;
            default:
                diagnostic_println("state?" + state);
        }
    }

    private PeriodicPinger pinger;
    private Thread pingerThread;

    private class PeriodicPinger implements Runnable {

        private boolean terminating = false;

        public void terminate() {
            terminating = true;
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
    }

    @Override
    public axoloti_core getTargetProfile() {
        return targetProfile;
    }

    public PatchModel getPatchModel() {
        return patch.getDModel();
    }

}
