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
import axoloti.live.patch.PatchViewLive;
import axoloti.live.patch.parameter.ParameterInstanceLiveView;
import axoloti.patch.PatchModel;
import axoloti.preferences.Preferences;
import axoloti.swingui.dialogs.USBPortSelectionDlg;
import axoloti.target.TargetModel;
import axoloti.target.TargetRTInfo;
import axoloti.target.fs.SDCardInfo;
import axoloti.targetprofile.axoloti_core;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.usb4java.*;
import qcmds.QCmd;
import qcmds.QCmdMemRead;
import qcmds.QCmdMemRead1Word;
import qcmds.QCmdProcessor;
import qcmds.QCmdSerialTask;
import qcmds.QCmdSerialTaskNull;
import qcmds.QCmdShowDisconnect;
import qcmds.QCmdTransmitGetFWVersion;
import qcmds.QCmdWriteMem;

/**
 *
 * @author Johannes Taelman
 */
public class USBBulkConnection_v2 extends IConnection {

    private PatchViewLive patch;
    private boolean disconnectRequested;
    private boolean connected;
    private Thread transmitterThread;
    private Thread receiverThread;
    private final BlockingQueue<QCmdSerialTask> queueSerialTask;
    private String cpuid;
    private axoloti_core targetProfile;
    private DeviceHandle handle;
    private final int interfaceNumber = 2;
    private String firmwareID;
    private boolean old_protocol = false;

    protected USBBulkConnection_v2(TargetModel targetModel) {
        super(targetModel);
        this.sync = new Sync();
        this.readsync = new Sync();
        this.patch = null;
        disconnectRequested = false;
        connected = false;
        queueSerialTask = new ArrayBlockingQueue<>(10);

    }

    @Override
    public void setPatch(PatchViewLive patchViewCodegen) {
        if ((patchViewCodegen == null)
                && (this.patch != null)) {
            patch.dispose();
        }
        this.patch = patchViewCodegen;
    }

    public void panic() {
        queueSerialTask.clear();
        disconnect();
    }

    @Override
    public boolean isConnected() {
        return connected && (!disconnectRequested);
    }

    @Override
    public boolean appendToQueue(QCmdSerialTask cmd) {
        return queueSerialTask.add(cmd);
    }

    private void disconnect1() {
        disp_addr = 0;
        clearSync();
        clearReadSync();
                    memReadHandler = null;
        goIdleState();
                    if (connected) {
                        disconnectRequested = true;
                        connected = false;
                        isSDCardPresent = null;
                        showDisconnect();
                        queueSerialTask.clear();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        queueSerialTask.add(new QCmdSerialTaskNull());
                        queueSerialTask.add(new QCmdSerialTaskNull());
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Disconnect request");
                        clearSync();
                        clearReadSync();
                        QCmdProcessor.getQCmdProcessor().panic();

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
                        cpuId0 = 0;
                        cpuId1 = 0;
                        cpuId2 = 0;
                    }
    }

    @Override
    public void disconnect() {
        {
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        disconnect1();
                    }
                });
            } else {
                disconnect1();
            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private byte[] bb2ba(ByteBuffer bb) {
        bb.rewind();
        byte[] r = new byte[bb.remaining()];
        bb.get(r, 0, r.length);
        return r;
    }

    @Override
    public boolean connect(String _cpuid) {
        disconnect();
        old_protocol = false;
        disconnectRequested = false;
        synchronized (sync) {
            sync.ready = true;
            sync.notifyAll();
        }
        goIdleState();
        targetProfile = new axoloti_core();
        handle = openDeviceHandle(_cpuid);
        if (handle == null) {
            showDisconnect();
            return false;
        }

        try //devicePath = Usb.DeviceToPath(device);
        {
            QCmdProcessor qcmdp = QCmdProcessor.getQCmdProcessor();
            qcmdp.panic();
            int result = LibUsb.claimInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }

            goIdleState();
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "creating rx and tx thread...");
            receiverThread = new Thread(new Receiver());
            receiverThread.setName("Receiver");
            receiverThread.start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }

            connected = true;
            clearSync();
            transmitPing();
            transmitPing();
            waitSync();
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
            transmitterThread = new Thread(new Transmitter());
            transmitterThread.setName("Transmitter");
            transmitterThread.start();

            qcmdp.appendToQueue(new QCmdTransmitGetFWVersion());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            qcmdp.waitQueueFinished();

            QCmdMemRead1Word q1 = new QCmdMemRead1Word(targetProfile.getCPUIDCodeAddr());
            qcmdp.appendToQueue(q1);
            targetProfile.setCPUIDCode(q1.getResult());
            QCmdMemRead q;

            q = new QCmdMemRead(targetProfile.getCPUSerialAddr(), targetProfile.getCPUSerialLength());
            qcmdp.appendToQueue(q);
            targetProfile.setCPUSerial(q.getResult());

            q = new QCmdMemRead(targetProfile.getOTPAddr(), 32);
            qcmdp.appendToQueue(q);
            ByteBuffer otpInfo = q.getResult();

            q = new QCmdMemRead(targetProfile.getOTPAddr() + 32, 256);
            qcmdp.appendToQueue(q);
            ByteBuffer signature = q.getResult();
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
                qcmdp.waitQueueFinished();
                ByteBuffer writeotpinfo = targetProfile.createOTPInfo();
                byte[] sign = HWSignature.sign(targetProfile.getCPUSerial(), writeotpinfo);
                qcmdp.appendToQueue(new QCmdWriteMem(targetProfile.getBKPSRAMAddr(), bb2ba(writeotpinfo)));
                qcmdp.appendToQueue(new QCmdWriteMem(targetProfile.getBKPSRAMAddr() + 32, sign));
                CRC32 zcrc = new CRC32();
                writeotpinfo.rewind();
                zcrc.update(bb2ba(writeotpinfo));
                zcrc.update(sign);
                int zcrcv = (int) zcrc.getValue();
                System.out.println(String.format("key crc: %08X", zcrcv));
                byte crc[] = new byte[4];
                crc[0] = (byte) (zcrcv & 0xFF);
                crc[1] = (byte) ((zcrcv >> 8) & 0xFF);
                crc[2] = (byte) ((zcrcv >> 16) & 0xFF);
                crc[3] = (byte) ((zcrcv >> 24) & 0xFF);
                qcmdp.appendToQueue(new QCmdWriteMem(targetProfile.getBKPSRAMAddr() + 32 + 256, crc));

                // validate from bkpsram
                qcmdp.waitQueueFinished();
                q = new QCmdMemRead(targetProfile.getBKPSRAMAddr(), 32);
                qcmdp.appendToQueue(q);
                ByteBuffer otpInfo2 = q.getResult();

                q = new QCmdMemRead(targetProfile.getBKPSRAMAddr() + 32, 256);
                qcmdp.appendToQueue(q);
                ByteBuffer signature2 = q.getResult();

                boolean signaturevalid2 = HWSignature.verify(targetProfile.getCPUSerial(), otpInfo2, bb2ba(signature2));
                if (signaturevalid2) {
                    System.out.println("bpksram signature valid");
                } else {
                    System.out.println("bpksram signature INvalid");
                    return false;
                }
                System.out.println("<otpinfo>");
                HWSignature.printByteArray(bb2ba(otpInfo2));
                System.out.println("</otpinfo>");

                System.out.println("<signature>");
                HWSignature.printByteArray(sign);
                System.out.println("</signature>");

            }

            clearReadSync();
            transmitMemoryRead(fw_chunkaddr, 8);
            waitReadSync();
            int fw_chunks_hdr = readsync.memReadBuffer.getInt();
            int fw_chunks_size = readsync.memReadBuffer.getInt();
            System.out.println("fw chunks hdr " + FourCC.format(fw_chunks_hdr) + ", size " + fw_chunks_size);
            clearReadSync();
            transmitMemoryRead(fw_chunkaddr, fw_chunks_size + 8);
            waitReadSync();
            fw_chunks = new ChunkParser(readsync.memReadBuffer);
            showConnect();
            return true;

        } catch (Exception ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            showDisconnect();
            return false;
        }
    }

    ChunkParser fw_chunks;

    @Override
    public ChunkParser getFWChunks() {
        return fw_chunks;
    }

    static final byte OUT_ENDPOINT = 0x02;
    static final byte IN_ENDPOINT = (byte) 0x82;
    static final int TIMEOUT = 1000;

    @Override
    public void writeBytes(byte[] data) {
        boolean dump_tx_headers = false;
        if (dump_tx_headers) {
            if (data.length >= 4) {
                System.out.println(String.format("%s        ->  %c%c%c%c  %5d",
                        new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS").format(Calendar.getInstance().getTime()),
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
        int result = LibUsb.bulkTransfer(handle, (byte) OUT_ENDPOINT, buffer, transfered, 1000);
        if (result != LibUsb.SUCCESS) {
            if (result == LibUsb.ERROR_NO_DEVICE) {
                disconnect();
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "Device disconnected");
            } else if (result == LibUsb.ERROR_TIMEOUT) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "USB transmit timeout");
                disconnect();
            } else {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "Control transfer failed: {0}", result);
                disconnect();
            }
        }
        //System.out.println(transfered.get() + " bytes sent");
    }

    @Override
    public void transmitRecallPreset(int presetNo) {
        byte[] data = new byte[5];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'T';
        data[4] = (byte) presetNo;
        writeBytes(data);
    }

    @Override
    public void bringToDFU() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'D';
        writeBytes(data);
    }

    @Override
    public void transmitGetFWVersion() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'V';
        writeBytes(data);
    }

    @Override
    public void sendMidi(int cable, int m0, int m1, int m2) {
        if (isConnected()) {
            byte[] data = new byte[8];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'M';
            // CIN for everyting except sysex
            int cin  = (m0 & 0xF0 ) >> 4;
            int ph = ((cable & 0x0F) << 4) | cin;
            data[4] = (byte) ph;
            data[5] = (byte) m0;
            data[6] = (byte) m1;
            data[7] = (byte) m2;
            writeBytes(data);
        }
    }

    @Override
    public void sendUpdatedPreset(byte[] b) {
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
        writeBytes(data);
        writeBytes(b);
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

    @Override
    public void transmitGetFileInfo(String filename) {
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
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitGetFileContents(String filename, MemReadHandler handler) {
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
        waitReadSync();
        readsync.ready = false;
        memReadHandler = handler;
        writeBytes(data);
        waitReadSync();
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

    static class Sync {
        boolean ready = true;
        ByteBuffer memReadBuffer;
    }

    final Sync sync;
    final Sync readsync;

    @Override
    public void clearSync() {
        synchronized (sync) {
            sync.ready = true;
        }
    }

    @Override
    public boolean waitSync(int msec) {
        synchronized (sync) {
            if (sync.ready) {
                return sync.ready;
            }
            try {
                sync.wait(msec);
            } catch (InterruptedException ex) {
                //              Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "Sync wait interrupted");
            }
            return sync.ready;
        }
    }

    @Override
    public boolean waitSync() {
        return waitSync(1000);
    }

    @Override
    public void clearReadSync() {
        synchronized (readsync) {
            readsync.ready = true;
            readsync.memReadBuffer = null;
            readsync.notifyAll();
        }
    }

    @Override
    public boolean waitReadSync() {
        int i = 5;
        while (!readsync.ready) {
            try {
                synchronized (readsync) {
                    readsync.wait(1000);
                }
                i--;
                if (i==0) break;
            } catch (InterruptedException ex) {
                return true;
            }
        }
        if (!readsync.ready) {
            System.out.println("sync: not ready!");
            new Exception().printStackTrace(System.out);
        }
        return readsync.ready;
    }

    private final byte[] startPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('s')};
    private final byte[] stopPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('S')};
    private final byte[] pingPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('p')};
    private final byte[] getFileListPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('d')};
    private final byte[] copyToFlashPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('F')};

    @Override
    public void transmitStart() {
        writeBytes(startPckt);
    }

    @Override
    public void transmitStart(String patchName) {
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
        writeBytes(b);
    }

    @Override
    public void transmitStart(int patchIndex) {
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
        writeBytes(b);
    }

    @Override
    public void transmitStop() {
        writeBytes(stopPckt);
    }

    @Override
    public void transmitGetFileList() {
        writeBytes(getFileListPckt);
    }

    @Override
    public void transmitPing() {
        writeBytes(pingPckt);
        if ((disp_addr != 0) && (disp_length!=0))
            transmitMemoryRead(disp_addr, disp_length * 4, new MemReadHandler() {
            @Override
                public void done(ByteBuffer mem) {
                    distributeToDisplays(mem);
            }
        });
    }

    @Override
    public void transmitCopyToFlash() {
        writeBytes(copyToFlashPckt);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void uploadFragment(byte[] buffer, int offset) {
        byte[] data = new byte[12];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'W';
        int tvalue = offset;
        int nRead = buffer.length;
        data[4] = (byte) tvalue;
        data[5] = (byte) (tvalue >> 8);
        data[6] = (byte) (tvalue >> 16);
        data[7] = (byte) (tvalue >> 24);
        data[8] = (byte) (nRead);
        data[9] = (byte) (nRead >> 8);
        data[10] = (byte) (nRead >> 16);
        data[11] = (byte) (nRead >> 24);
        clearSync();
        writeBytes(data);
        writeBytes(buffer);
        waitSync();
        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "block uploaded @ 0x{0} length {1}", new Object[]{Integer.toHexString(offset).toUpperCase(), Integer.toString(buffer.length)});
    }

    @Override
    public void transmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3) {
        byte[] data = new byte[8];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'B';
        data[4] = b0;
        data[5] = b1;
        data[6] = b2;
        data[7] = b3;
        writeBytes(data);
    }

    @Override
    public void transmitCreateFile(String filename, int size) {
        byte[] data = new byte[9 + filename.length()];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'C';
        data[4] = (byte) size;
        data[5] = (byte) (size >> 8);
        data[6] = (byte) (size >> 16);
        data[7] = (byte) (size >> 24);
        int i = 8;
        for (int j = 0; j < filename.length(); j++) {
            data[i++] = (byte) filename.charAt(j);
        }
        data[i] = 0;
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitCreateFile(String filename, int size, Calendar date) {
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
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitDeleteFile(String filename) {
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
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitChangeWorkingDirectory(String path) {
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
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitCreateDirectory(String filename, Calendar date) {
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
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitAppendFile(byte[] buffer) {
        byte[] data = new byte[8];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'A';
        int size = buffer.length;
//        Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "append size: " + buffer.length);
        data[4] = (byte) size;
        data[5] = (byte) (size >> 8);
        data[6] = (byte) (size >> 16);
        data[7] = (byte) (size >> 24);
        clearSync();
        writeBytes(data);
        writeBytes(buffer);
        waitSync();
    }

    @Override
    public void transmitCloseFile() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'c';
        clearSync();
        writeBytes(data);
        waitSync();
    }

    @Override
    public void transmitMemoryRead(int addr, int length) {
        if (length == 0) {
            System.out.println("memrd size 0?");
        }
        waitReadSync();
        readsync.ready = false;
        memReadHandler = null;
        System.out.println(String.format("tx memrd addr=0x%08X le=%d",addr,length));
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
        writeBytes(data);
        waitReadSync();
    }

    @Override
    public void transmitMemoryRead(int addr, int length, MemReadHandler handler) {
        if (length == 0) {
            System.out.println("memrd size 0?");
        }
        waitReadSync();
        readsync.ready = false;
        memReadHandler = handler;
        //System.out.println(String.format("tx memrd addr=0x%08X le=%d",addr,length));
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
        writeBytes(data);
        waitReadSync();
    }

    @Override
    public void transmitMemoryRead1Word(int addr) {
        waitReadSync();
        readsync.ready = false;
        byte[] data = new byte[8];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'y';
        data[4] = (byte) addr;
        data[5] = (byte) (addr >> 8);
        data[6] = (byte) (addr >> 16);
        data[7] = (byte) (addr >> 24);
        writeBytes(data);
        waitReadSync();
    }

    class Receiver implements Runnable {

        final static int MAX_RX_SIZE = 4096;
        // larger than 4096 will give "WARN Event TRB for slot 1 ep 4 with no TDs queued" in linux kernel log

        @Override
        public void run() {
            ByteBuffer packet = null;
            while (!disconnectRequested) {
                ByteBuffer recvbuffer = ByteBuffer.allocateDirect(MAX_RX_SIZE);
                recvbuffer.order(ByteOrder.LITTLE_ENDIAN);
                IntBuffer transfered = IntBuffer.allocate(1);
                recvbuffer.rewind();
                int result = LibUsb.bulkTransfer(handle, (byte) IN_ENDPOINT, recvbuffer, transfered, 1000);
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
                                    System.out.println(String.format("%s <- %c%c%c%c           %4d",
                                            new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS").format(Calendar.getInstance().getTime()),
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
                            System.out.println("pos=" + pl + " cap=" + packet.capacity() + " sz=" + sz);
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
                        }   break;
                    default:
                        String err = LibUsb.errorName(result);
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "receive error: {0}", err);
                        disconnectRequested = true;
                        goIdleState();
                        disconnect();
                        break;
                }
            }
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receiver: thread stopped");
            QCmdProcessor.getQCmdProcessor().abort();
            QCmdProcessor.getQCmdProcessor().appendToQueue(new QCmdShowDisconnect());
            disconnect();
        }
    }

    class Transmitter implements Runnable {

        @Override
        public void run() {
            while (!disconnectRequested) {
                try {
                    QCmdSerialTask cmd = queueSerialTask.take();
                    QCmd response = cmd.performAction(USBBulkConnection_v2.this);
                    if (response != null) {
                        QCmdProcessor.getQCmdProcessor().getQueueResponse().add(response);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "transmitter: thread stopped");
            QCmdProcessor.getQCmdProcessor().abort();
            QCmdProcessor.getQCmdProcessor().appendToQueue(new QCmdShowDisconnect());
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

    int cpuId0 = 0;
    int cpuId1 = 0;
    int cpuId2 = 0;
    int fwcrc = -1;

    void acknowledge(final int DSPLoad, final int PatchID, final int Voltages, final int patchIndex, final int sdcardPresent) {
        synchronized (sync) {
            sync.ready = true;
            sync.notifyAll();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (patch != null) {
                    if ((getPatchModel().getIID() != PatchID) && getPatchModel().getLocked()) {
                        patch.getDModel().getController().setLocked(false);
                    } else {
                        patch.getDModel().getController().setDspLoad(DSPLoad);
                    }
                }
//                MainFrame.mainframe.showPatchIndex(patchIndex);
                setSDCardPresent(sdcardPresent != 0);
            }
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
        synchronized (sync) {
            sync.ready = true;
            sync.notifyAll();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (patch != null) {
                    if ((getPatchModel().getIID() != PatchID) && getPatchModel().getLocked()) {
                        patch.getDModel().getController().setLocked(false);
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
            }
        });
    }

    void RPacketParamChange(final int index, final int value, final int patchID) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });

    }

    enum ReceiverState {

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
    private int[] packetData = new int[64];
    private int dataIndex = 0; // in bytes
    private int dataLength = 0; // in bytes
    private CharBuffer textRcvBuffer = CharBuffer.allocate(256);
    private ByteBuffer lcdRcvBuffer = ByteBuffer.allocate(256);
    private ByteBuffer sdinfoRcvBuffer = ByteBuffer.allocate(12);
    private ByteBuffer fileinfoRcvBuffer = ByteBuffer.allocate(256);

    private int memReadAddr;
    private int memReadLength;
    private int memReadValue;
    private MemReadHandler memReadHandler;
    private byte[] fwversion = new byte[4];
    private int fw_chunkaddr;

    @Override
    public ByteBuffer getMemReadBuffer() {
        synchronized (readsync) {
            return readsync.memReadBuffer;
        }
    }

    @Override
    public int getMemRead1Word() {
        return memReadValue;
    }

    void displayPackHeader(int i1, int i2) {
        if (i2 > 1024) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.FINE, "Lots of data coming! {0} / {1}", new Object[]{Integer.toHexString(i1), Integer.toHexString(i2)});
        } else {
//            Logger.getLogger(SerialConnection.class.getName()).info("OK! " + Integer.toHexString(i1) + " / " + Integer.toHexString(i2));
        }
        if (i2 > 0) {
            dataLength = i2 * 4;
            dataIndex = 0;
            dispData = ByteBuffer.allocate(dataLength);
            dispData.order(ByteOrder.LITTLE_ENDIAN);
            state = ReceiverState.displayPckt;
        } else {
            goIdleState();
        }
    }

    void distributeToDisplays(final ByteBuffer dispData) {
        if (patch == null) {
            return;
        }
        try {
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        patch.distributeDataToDisplays(dispData);
                    }
                });
            } else  {
                patch.distributeDataToDisplays(dispData);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void goIdleState() {
        state = ReceiverState.header;
    }
    ByteBuffer dispData;

    int LCDPacketRow = 0;

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
                                System.out.println("rx hdr ack");
                            }
                            int ackversion = rbuf.getInt();
                            if (ackversion==0) {
                                int i1 = rbuf.getInt();
                                int i2 = rbuf.getInt();
                                int i3 = rbuf.getInt();
                                int i4 = rbuf.getInt();
                                int i5 = rbuf.getInt();
    //                            System.out.println(String.format("vu %08X",i0));
                                acknowledge(i1, i2, i3, i4, i5);
                            } else if (ackversion==1) {
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
                                System.out.println("rx hdr memrd32");
                            }
                            memReadAddr = rbuf.getInt();
                            memReadValue = rbuf.getInt();
                            synchronized (readsync) {
                                readsync.ready = true;
                                readsync.notifyAll();
                            }
                            goIdleState();
                        }
                        break;
                        case tx_hdr_fwid: {
                            if (log_rx_diagnostics) {
                                System.out.println("rx hdr fwid");
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
                                System.out.println("rx hdr log");
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
                            System.out.println("tx_hdr_filecontents");
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
                            try {
                                SwingUtilities.invokeAndWait(new Runnable() {
                                    @Override
                                    public void run() {
                                        SDCardInfo sdcardinfo = TargetModel.getTargetModel().getSDCardInfo();
                                        int fname = rbuf.getInt();
                                        int sz = rbuf.getInt();
                                        int timestamp = rbuf.getInt();
                                        sdcardinfo.setInfo(fname, sz, timestamp);
                                        TargetModel.getTargetModel().setSDCardInfo(sdcardinfo);
                                    }
                                });
                            } catch (InterruptedException ex) {
                                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                        case rx_hdr_fileinfo: {
                            try {
                                SwingUtilities.invokeAndWait(new Runnable() {
                                    @Override
                                    public void run() {
                                        int sz = rbuf.getInt();
                                        int timestamp = rbuf.getInt();
                                        CharBuffer cb = Charset.forName("ISO-8859-1").decode(rbuf);
                                        String fname = cb.toString();
                                        // strip trailing null
                                        if (fname.charAt(fname.length() - 1) == (char) 0) {
                                            fname = fname.substring(0, fname.length() - 1);
                                        }
                                        SDCardInfo sdcardinfo = TargetModel.getTargetModel().getSDCardInfo();
                                        sdcardinfo.addFile(fname, sz, timestamp);
                                        TargetModel.getTargetModel().setSDCardInfo(sdcardinfo);
                                    }
                                });
                            } catch (InterruptedException ex) {
                                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvocationTargetException ex) {
                                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                        case 0x00416f78: {
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Old version of firmware (1.x) detected");
                            while (rbuf.hasRemaining()) {
                                byte b = rbuf.get();
                                System.out.println(String.format("   data %02x (%c)", b, (char) b));
                            }
                            old_protocol = true;
                            disconnectRequested = true;
                        }
                        break;
                        default:
                            System.out.println(String.format("lost header %08x (%c%c%c%c)", header,
                                    (char) (byte) (header), (char) (byte) (header >> 8), (char) (byte) (header >> 16), (char) (byte) (header >> 24)));
                            while (rbuf.hasRemaining()) {
                                byte b = rbuf.get();
                                System.out.println(String.format("   data %02x (%c)", b, (char) b));
                            }
                    }
                }
            }
            break;
            case textPckt: {
                while (rbuf.remaining() > 0) {
                    byte b =  rbuf.get();
                    if (b==0) {
                        textRcvBuffer.limit(textRcvBuffer.position());
                        textRcvBuffer.rewind();
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                        goIdleState();
                    } else {
                        if (textRcvBuffer.position() < textRcvBuffer.limit()) {
                            textRcvBuffer.append((char)b);
                        } else {
                            System.out.println("textRcvBuffer overflow :" + (char)b);
                            textRcvBuffer.limit(textRcvBuffer.position());
                            textRcvBuffer.rewind();
                            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                            textRcvBuffer.limit(textRcvBuffer.capacity());
                            textRcvBuffer.rewind();
                            textRcvBuffer.append((char)b);
                        }
                    }
                }
                goIdleState();
            } break;
            case memread: {
                if (memReadLength != size) {
                    System.out.print("memread barf:" + memReadLength + ":" + size + "<");
//                    rbuf.position(memReadLength);
                    rbuf.rewind();
                    int i = 0;
                    while(rbuf.hasRemaining()) {
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
                    System.out.println("rx memrd recv'd sz=" + size + " " + memReadHandler);
                }
                byte memr[] = new byte[memReadLength];
                rbuf.get(memr,0,memReadLength);
                ByteBuffer mrb = ByteBuffer.wrap(memr);
                mrb.order(ByteOrder.LITTLE_ENDIAN);
                mrb.rewind();
                MemReadHandler mrh = memReadHandler;
                if (mrh != null) {
                    try {
                        if (log_rx_diagnostics) {
                                System.out.println("handler: " + mrh.toString());
                        }
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                mrh.done(mrb);
                            }
                        });
                    } catch (InterruptedException ex) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                synchronized (readsync) {
                    readsync.memReadBuffer = mrb;
                    readsync.ready = true;
                    readsync.notifyAll();
                }
                memReadHandler = null;
                goIdleState();
            } break;
            default:
                System.out.println("state?" + state);
        }
    }

    int disp_addr;
    int disp_length;

    @Deprecated
    @Override
    public void  setDisplayAddr(int a, int l) {
        disp_addr = a;
        disp_length = l;
    }

    @Override
    public axoloti_core getTargetProfile() {
        return targetProfile;
    }

    public PatchModel getPatchModel() {
        return patch.getDModel();
    }
}
