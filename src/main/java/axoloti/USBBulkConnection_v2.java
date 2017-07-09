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
package axoloti;

/**
 * Replaces the old packet-over-serial protocol with vendor-specific usb bulk
 * transport
 */
import axoloti.chunks.ChunkParser;
import axoloti.chunks.FourCC;
import axoloti.dialogs.USBPortSelectionDlg;
import static axoloti.dialogs.USBPortSelectionDlg.ErrorString;
import axoloti.displays.DisplayInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.targetprofile.axoloti_core;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
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

    private PatchViewCodegen patch;
    private boolean disconnectRequested;
    private boolean connected;
    private Thread transmitterThread;
    private Thread receiverThread;
    private final BlockingQueue<QCmdSerialTask> queueSerialTask;
    private String cpuid;
    private axoloti_core targetProfile;
    private final Context context;
    private DeviceHandle handle;
    private final short bulkVID = (short) 0x16C0;
    private final short bulkPID = (short) 0x0442;
    private final int interfaceNumber = 2;

    protected USBBulkConnection_v2() {
        this.sync = new Sync();
        this.readsync = new Sync();
        this.patch = null;
        disconnectRequested = false;
        connected = false;
        queueSerialTask = new ArrayBlockingQueue<QCmdSerialTask>(10);
        context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }
    }

    @Override
    public void setPatch(PatchViewCodegen patchViewCodegen) {
        this.patch = patchViewCodegen;
    }

    public void Panic() {
        queueSerialTask.clear();
        disconnect();
    }

    @Override
    public boolean isConnected() {
        return connected && (!disconnectRequested);
    }

    @Override
    public boolean AppendToQueue(QCmdSerialTask cmd) {
        return queueSerialTask.add(cmd);
    }

    @Override
    public void disconnect() {
        disp_addr = 0;
        if (connected) {
            disconnectRequested = true;
            connected = false;
            isSDCardPresent = null;
            ShowDisconnect();
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
            ClearSync();
            ClearReadSync();

            if (receiverThread.isAlive()) {
                receiverThread.interrupt();
                try {
                    receiverThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            int result = LibUsb.releaseInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to release interface", result);
            }

            LibUsb.close(handle);
            handle = null;
            CpuId0 = 0;
            CpuId1 = 0;
            CpuId2 = 0;
        }
    }

    public DeviceHandle OpenDeviceHandle() {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device d : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(d, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if (descriptor.idVendor() == bulkVID && descriptor.idProduct() == bulkPID) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "USB device found");
                    DeviceHandle h = new DeviceHandle();
                    result = LibUsb.open(d, h);
                    if (result < 0) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, ErrorString(result));
                    } else {
                        String serial = LibUsb.getStringDescriptor(h, descriptor.iSerialNumber());
                        if (cpuid != null) {
                            if (serial.equals(cpuid)) {
                                return h;
                            }
                        } else {
                            return h;
                        }
                        LibUsb.close(h);
                    }
                }
            }
            // or else pick the first one
            for (Device d : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(d, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if (descriptor.idVendor() == bulkVID && descriptor.idProduct() == bulkPID) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "USB device found");
                    DeviceHandle h = new DeviceHandle();
                    result = LibUsb.open(d, h);
                    if (result < 0) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, ErrorString(result));
                    } else {
                        return h;
                    }
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            //LibUsb.freeDeviceList(list, true);
        }
        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "No available USB device found with matching PID/VID");
        // Device not found
        return null;
    }

    private byte[] bb2ba(ByteBuffer bb) {
        bb.rewind();
        byte[] r = new byte[bb.remaining()];
        bb.get(r, 0, r.length);
        return r;
    }

    @Override
    public boolean connect() {
        disconnect();
        disconnectRequested = false;
        synchronized (sync) {
            sync.Acked = true;
            sync.notifyAll();
        }
        GoIdleState();
        if (cpuid == null) {
            cpuid = MainFrame.prefs.getComPortName();
        }
        targetProfile = new axoloti_core();
        handle = OpenDeviceHandle();
        if (handle == null) {
            return false;
        }

        try //devicePath = Usb.DeviceToPath(device);
        {

            int result = LibUsb.claimInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }

            GoIdleState();
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "creating rx and tx thread...");
            transmitterThread = new Thread(new Transmitter());
            transmitterThread.setName("Transmitter");
            transmitterThread.start();
            receiverThread = new Thread(new Receiver());
            receiverThread.setName("Receiver");
            receiverThread.start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }

            connected = true;
            ClearSync();
            TransmitPing();
            WaitSync();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "connected");

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            QCmdProcessor qcmdp = MainFrame.mainframe.getQcmdprocessor();

            qcmdp.AppendToQueue(new QCmdTransmitGetFWVersion());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
            qcmdp.WaitQueueFinished();

            QCmdMemRead1Word q1 = new QCmdMemRead1Word(targetProfile.getCPUIDCodeAddr());
            qcmdp.AppendToQueue(q1);
            targetProfile.setCPUIDCode(q1.getResult());
            QCmdMemRead q;

            q = new QCmdMemRead(targetProfile.getCPUSerialAddr(), targetProfile.getCPUSerialLength());
            qcmdp.AppendToQueue(q);
            targetProfile.setCPUSerial(q.getResult());

            q = new QCmdMemRead(targetProfile.getOTPAddr(), 32);
            qcmdp.AppendToQueue(q);
            ByteBuffer otpInfo = q.getResult();

            q = new QCmdMemRead(targetProfile.getOTPAddr() + 32, 256);
            qcmdp.AppendToQueue(q);
            ByteBuffer signature = q.getResult();
            boolean signaturevalid = false;
            if (signature == null) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Can''t obtain signature, upgrade firmware?");
            } else if ((signature.getInt(0) == 0xFFFFFFFF) && (signature.getInt(1) == 0xFFFFFFFF)) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "Cannot validate authenticity, no signature present.");
            } else {
                signaturevalid = HWSignature.Verify(targetProfile.getCPUSerial(), otpInfo, bb2ba(signature));
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
                qcmdp.WaitQueueFinished();
                ByteBuffer writeotpinfo = targetProfile.CreateOTPInfo();
                byte[] sign = HWSignature.Sign(targetProfile.getCPUSerial(), writeotpinfo);
                qcmdp.AppendToQueue(new QCmdWriteMem(targetProfile.getBKPSRAMAddr(), bb2ba(writeotpinfo)));
                qcmdp.AppendToQueue(new QCmdWriteMem(targetProfile.getBKPSRAMAddr() + 32, sign));
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
                qcmdp.AppendToQueue(new QCmdWriteMem(targetProfile.getBKPSRAMAddr() + 32 + 256, crc));

                // validate from bkpsram
                qcmdp.WaitQueueFinished();
                q = new QCmdMemRead(targetProfile.getBKPSRAMAddr(), 32);
                qcmdp.AppendToQueue(q);
                ByteBuffer otpInfo2 = q.getResult();

                q = new QCmdMemRead(targetProfile.getBKPSRAMAddr() + 32, 256);
                qcmdp.AppendToQueue(q);
                ByteBuffer signature2 = q.getResult();

                boolean signaturevalid2 = HWSignature.Verify(targetProfile.getCPUSerial(), otpInfo2, bb2ba(signature2));
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
            
            ClearReadSync();
            TransmitMemoryRead(patchentrypoint, 8);
            WaitReadSync();
            int fw_chunks_hdr = readsync.memReadBuffer.getInt();
            int fw_chunks_size = readsync.memReadBuffer.getInt();
            System.out.println("fw chunks hdr " + FourCC.Format(fw_chunks_hdr) + ", size " + fw_chunks_size);
            ClearReadSync();
            TransmitMemoryRead(patchentrypoint, fw_chunks_size + 8);
            WaitReadSync();
            fw_chunks = new ChunkParser(readsync.memReadBuffer);            
            ShowConnect();
            return true;

        } catch (Exception ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            ShowDisconnect();
            return false;
        }
    }

    ChunkParser fw_chunks;
    
    @Override
    public ChunkParser GetFWChunks() {
        return fw_chunks;
    }
    
    static final byte OUT_ENDPOINT = 0x02;
    static final byte IN_ENDPOINT = (byte) 0x82;
    static final int TIMEOUT = 1000;

    @Override
    public void writeBytes(byte[] data) {
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
    public void TransmitRecallPreset(int presetNo) {
        byte[] data = new byte[5];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'T';
        data[4] = (byte) presetNo;
        writeBytes(data);
    }

    @Override
    public void BringToDFU() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'D';
        writeBytes(data);
    }

    @Override
    public void TransmitGetFWVersion() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'V';
        writeBytes(data);
    }

    @Override
    public void SendMidi(int m0, int m1, int m2) {
        if (isConnected()) {
            byte[] data = new byte[7];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'M';
            data[4] = (byte) m0;
            data[5] = (byte) m1;
            data[6] = (byte) m2;
            writeBytes(data);
        }
    }

    @Override
    public void SendUpdatedPreset(byte[] b) {
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
    public void SelectPort() {
        USBPortSelectionDlg spsDlg = new USBPortSelectionDlg(null, true, cpuid);
        spsDlg.setVisible(true);
        cpuid = spsDlg.getCPUID();
        String name = MainFrame.prefs.getBoardName(cpuid);
        if (name == null) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "port: {0}", cpuid);
        } else {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "port: {0} name: {1}", new Object[]{cpuid, name});
        }
    }

    static private USBBulkConnection_v2 conn = null;

    public static IConnection GetConnection() {
        if (conn == null) {
            conn = new USBBulkConnection_v2();
        }
        return conn;
    }

    @Override
    public void TransmitGetFileInfo(String filename) {
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    class Sync {
        boolean Acked = false;
        boolean Pending = false;
        ByteBuffer memReadBuffer;
    }
    
    final Sync sync;
    final Sync readsync;

    @Override
    public void ClearSync() {
        synchronized (sync) {
            sync.Acked = false;
        }
    }

    @Override
    public boolean WaitSync(int msec) {
        synchronized (sync) {
            if (sync.Acked) {
                return sync.Acked;
            }
            try {
                sync.wait(msec);
            } catch (InterruptedException ex) {
                //              Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "Sync wait interrupted");
            }
            return sync.Acked;
        }
    }

    @Override
    public boolean WaitSync() {
        return WaitSync(1000);
    }

    @Override
    public void ClearReadSync() {
        synchronized (readsync) {
            readsync.Acked = false;
            readsync.memReadBuffer = null;
        }
    }

    @Override
    public boolean WaitReadSync() {
        synchronized (readsync) {
            if (readsync.Acked) {
                //System.out.println("nowait");
                // TODO: should not need to sleep to obtain correct results...
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
                return readsync.Acked;
            }
        }
        for(int i=0;i<5;i++) {
            synchronized (readsync) {
                try {
                    //System.out.println("wait");
                    readsync.wait(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, "Sync wait interrupted");
                }
                if (readsync.Acked) break;
            }
        }
        // TODO: should not need to sleep to obtain correct results...
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!readsync.Acked) {
            System.out.println("sync: not ready!");
        }
        return readsync.Acked;
    }

    private final byte[] startPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('s')};
    private final byte[] stopPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('S')};
    private final byte[] pingPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('p')};
    private final byte[] getFileListPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('d')};
    private final byte[] copyToFlashPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('F')};

    @Override
    public void TransmitStart() {
        writeBytes(startPckt);
    }

    @Override
    public void TransmitStop() {
        writeBytes(stopPckt);
    }

    @Override
    public void TransmitGetFileList() {
        writeBytes(getFileListPckt);
    }

    @Override
    public void TransmitPing() {
        writeBytes(pingPckt);
        if (disp_addr != 0)
            TransmitMemoryRead(disp_addr, disp_length*4, new MemReadHandler() {
            @Override
            public void Done(ByteBuffer mem) {
                DistributeToDisplays(mem);
            }
        });
    }

    @Override
    public void TransmitCopyToFlash() {
        writeBytes(copyToFlashPckt);
    }

    @Override
    public void UploadFragment(byte[] buffer, int offset) {
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
        ClearSync();
        writeBytes(data);
        writeBytes(buffer);
        WaitSync();
        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.INFO, "block uploaded @ 0x{0} length {1}", new Object[]{Integer.toHexString(offset).toUpperCase(), Integer.toString(buffer.length)});
    }

    @Override
    public void TransmitVirtualButton(int b_or, int b_and, int enc1, int enc2, int enc3, int enc4) {
        byte[] data = new byte[16];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'B';
        data[4] = (byte) b_or;
        data[5] = (byte) (b_or >> 8);
        data[6] = (byte) (b_or >> 16);
        data[7] = (byte) (b_or >> 24);
        data[8] = (byte) b_and;
        data[9] = (byte) (b_and >> 8);
        data[10] = (byte) (b_and >> 16);
        data[11] = (byte) (b_and >> 24);
        data[12] = (byte) (enc1);
        data[13] = (byte) (enc2);
        data[14] = (byte) (enc3);
        data[15] = (byte) (enc4);
        writeBytes(data);
    }

    @Override
    public void TransmitCreateFile(String filename, int size) {
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitCreateFile(String filename, int size, Calendar date) {
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitDeleteFile(String filename) {
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitChangeWorkingDirectory(String path) {
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitCreateDirectory(String filename, Calendar date) {
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitAppendFile(byte[] buffer) {
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
        ClearSync();
        writeBytes(data);
        writeBytes(buffer);
        WaitSync();
    }

    @Override
    public void TransmitCloseFile() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'c';
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitMemoryRead(int addr, int length) {
        while (readsync.Pending) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        readsync.Pending = true;
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    @Override
    public void TransmitMemoryRead(int addr, int length, MemReadHandler handler) {
        while (readsync.Pending) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        readsync.Pending = true;
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
        ClearSync();
        writeBytes(data);
        WaitSync();
    }
    
    @Override
    public void TransmitMemoryRead1Word(int addr) {
        byte[] data = new byte[8];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'y';
        data[4] = (byte) addr;
        data[5] = (byte) (addr >> 8);
        data[6] = (byte) (addr >> 16);
        data[7] = (byte) (addr >> 24);
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    class Receiver implements Runnable {

        @Override
        public void run() {
            ByteBuffer recvbuffer = ByteBuffer.allocateDirect(32768);
            recvbuffer.order(ByteOrder.LITTLE_ENDIAN);
            IntBuffer transfered = IntBuffer.allocate(1);
            while (!disconnectRequested) {
                int result = LibUsb.bulkTransfer(handle, (byte) IN_ENDPOINT, recvbuffer, transfered, 1000);
                if (result != LibUsb.SUCCESS) {
                    //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receive: " + result);
                }
                {
                    int sz = transfered.get(0);
                    processPacket(recvbuffer, sz);
                }
            }
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receiver: thread stopped");
            MainFrame.mainframe.qcmdprocessor.Abort();
            MainFrame.mainframe.qcmdprocessor.AppendToQueue(new QCmdShowDisconnect());
        }
    }

    class Transmitter implements Runnable {

        @Override
        public void run() {
            while (!disconnectRequested) {
                try {
                    QCmdSerialTask cmd = queueSerialTask.take();
                    QCmd response = cmd.Do(USBBulkConnection_v2.this);
                    if (response != null) {
                        QCmdProcessor.getQCmdProcessor().getQueueResponse().add(response);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "transmitter: thread stopped");
            MainFrame.mainframe.qcmdprocessor.Abort();
            MainFrame.mainframe.qcmdprocessor.AppendToQueue(new QCmdShowDisconnect());
        }
    }

    private Boolean isSDCardPresent = null;

    public void SetSDCardPresent(boolean i) {
        if ((isSDCardPresent != null) && (i == isSDCardPresent)) {
            return;
        }
        isSDCardPresent = i;
        if (isSDCardPresent) {
            ShowSDCardMounted();
        } else {
            ShowSDCardUnmounted();
        }
    }

    @Override
    public boolean GetSDCardPresent() {
        if (isSDCardPresent == null) {
            return false;
        }
        return isSDCardPresent;
    }

    int CpuId0 = 0;
    int CpuId1 = 0;
    int CpuId2 = 0;
    int fwcrc = -1;

    void Acknowledge(final int DSPLoad, final int PatchID, final int Voltages, final int patchIndex, final int sdcardPresent) {
        synchronized (sync) {
            sync.Acked = true;
            sync.notifyAll();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (patch != null) {
                    if ((getPatchModel().GetIID() != PatchID) && getPatchModel().getLocked()) {
                        patch.getController().setLocked(false);
                    } else {
                        patch.getController().setDspLoad(DSPLoad);
                    }
                }
                MainFrame.mainframe.showPatchIndex(patchIndex);
                targetProfile.setVoltages(Voltages);
                SetSDCardPresent(sdcardPresent != 0);
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
                if (getPatchModel().GetIID() != patchID) {
                    patch.getController().setLocked(false);
                    return;
                }
                if (index >= patch.ParameterInstances.size()) {
                    Logger.getLogger(USBBulkConnection_v2.class
                            .getName()).log(Level.INFO, "Rx paramchange index out of range{0} {1}", new Object[]{index, value});

                    return;
                }
                ParameterInstance pi = patch.ParameterInstances.get(index);

                if (pi == null) {
                    Logger.getLogger(USBBulkConnection_v2.class
                            .getName()).log(Level.INFO, "Rx paramchange parameterInstance null{0} {1}", new Object[]{index, value});
                    return;
                }

                if (!pi.getNeedsTransmit()) {
                    pi.SetValueRaw(value);
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
    private int headerstate;
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
    private int patchentrypoint;

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

    void storeDataByte(int c) {
        switch (dataIndex & 0x3) {
            case 0:
                packetData[dataIndex >> 2] = c;
                break;
            case 1:
                packetData[dataIndex >> 2] += (c << 8);
                break;
            case 2:
                packetData[dataIndex >> 2] += (c << 16);
                break;
            case 3:
                packetData[dataIndex >> 2] += (c << 24);
                break;
        }
//            System.out.println("s " + dataIndex + "  v=" + Integer.toHexString(packetData[dataIndex>>2]) + " c=");
        dataIndex++;

    }

    void DisplayPackHeader(int i1, int i2) {
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
            GoIdleState();
        }
    }

    void DistributeToDisplays(final ByteBuffer dispData) {
//        Logger.getLogger(SerialConnection.class.getName()).info("Distr1");
        try {
            if (patch == null) {
                return;
            }
            if (!getPatchModel().getLocked()) {
                return;
            }
            if (patch.DisplayInstances == null) {
                return;
            }
            if (!SwingUtilities.isEventDispatchThread()) {
                //        Logger.getLogger(SerialConnection.class.getName()).info("Distr2");
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        dispData.rewind();
                        for (DisplayInstance d : patch.DisplayInstances) {
                            d.ProcessByteBuffer(dispData);
                        }
                    }
                });
            } else  {
                        dispData.rewind();
                        for (DisplayInstance d : patch.DisplayInstances) {
                            d.ProcessByteBuffer(dispData);
                        }                
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void GoIdleState() {
        headerstate = 0;
        state = ReceiverState.header;
    }
    ByteBuffer dispData;

    int LCDPacketRow = 0;

    ReceiverState rstate=ReceiverState.ackPckt;
    
    final int tx_hdr_acknowledge = 0x416F7841;  // "AxoA"
    final int tx_hdr_fwid = 0x566f7841;         // "AxoV"
    final int tx_hdr_log = 0x546F7841;          // "AxoT"
    final int tx_hdr_memrd32 = 0x796f7841;      // "Axoy"
    final int tx_hdr_memrdx = 0x726f7841;       // "Axor"
    final int rx_hdr_displaypckt = 0x446F7841;  // "AxoD"
    final int rx_hdr_paramchange = 0x516F7841;  // "AxoQ" 
    
    final boolean log_rx_diagnostics = true;
    
    void processPacket(ByteBuffer rbuf, int size) {
        if (size == 0) {
            GoIdleState();
            return;
        }
        rbuf.rewind();
        switch (state) {
            case header: {
                if (size >= 4) {
                    int header = rbuf.getInt();
                    switch (header) {
                        case tx_hdr_acknowledge: {
                            int i0 = rbuf.getInt();
                            int i1 = rbuf.getInt();
                            int i2 = rbuf.getInt();
                            int i3 = rbuf.getInt();
                            int i4 = rbuf.getInt();
                            int i5 = rbuf.getInt();
                            Acknowledge(i1, i2, i3, i4, i5);
                            GoIdleState();
                        }
                        break;
                        case tx_hdr_memrd32: {
                            memReadAddr = rbuf.getInt();
                            memReadValue = rbuf.getInt();
                            synchronized (readsync) {
                                readsync.Acked = true;
                                readsync.Pending = false;
                                readsync.notifyAll();
                            }
                            GoIdleState();
                        }
                        break;
                        case tx_hdr_fwid: {
                            fwversion[0] = rbuf.get();
                            fwversion[1] = rbuf.get();
                            fwversion[2] = rbuf.get();
                            fwversion[3] = rbuf.get();
                            int fwcrc1 = (rbuf.get() & 0xFF) << 24;
                            fwcrc1 += (rbuf.get() & 0xFF) << 16;
                            fwcrc1 += (rbuf.get() & 0xFF) << 8;
                            fwcrc1 += (rbuf.get() & 0xFF);
                            fwcrc = fwcrc1;
                            int patchentrypoint1 = (rbuf.get() & 0xFF) << 24;
                            patchentrypoint1 += (rbuf.get() & 0xFF) << 16;
                            patchentrypoint1 += (rbuf.get() & 0xFF) << 8;
                            patchentrypoint1 += (rbuf.get() & 0xFF);
                            patchentrypoint = patchentrypoint1;
                            String sFwcrc = String.format("%08X", fwcrc);
                            Logger.getLogger(USBBulkConnection.class.getName()).info(String.format("Firmware version: %d.%d.%d.%d, crc=0x%s, entrypoint=0x%08X",
                                    fwversion[0], fwversion[1], fwversion[2], fwversion[3], sFwcrc, patchentrypoint));
                            MainFrame.mainframe.setFirmwareID(sFwcrc);
                            GoIdleState();
                        }
                        break;
                        case tx_hdr_log: {
                            while (rbuf.remaining() > 0) {
                                byte b =  rbuf.get();
                                textRcvBuffer.append((char)b);
                                if (b==0) break;
                            }
                            textRcvBuffer.limit(textRcvBuffer.position());
                            textRcvBuffer.rewind();
                            Logger.getLogger(USBBulkConnection.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                        }
                        break;
                        case tx_hdr_memrdx:
                            memReadAddr = rbuf.getInt();
                            memReadLength = rbuf.getInt();
                            if (false && log_rx_diagnostics) {
                                System.out.print("rx memrd addr=" + String.format("0x%08X", memReadAddr) + " le=" + memReadLength +" [");
                                for(int i= 12;i<size;i++) {
                                    // this would be unexpected extra data...
                                    System.out.print("|"+(char)rbuf.get(i));
                                }
                                System.out.println("]");
                            }
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
                        default:
                            System.out.println(String.format("lost header %8x", header));
                    }
                }
            }
            break;
            case memread: {                
                if (memReadLength < size) {
                    System.out.print("memread barf:<");
                    rbuf.position(memReadLength);
                    while(rbuf.hasRemaining()) {
                        System.out.print("|"+(char)rbuf.get());
                    }
                    System.out.println(">");
                }
                //System.out.println("rx memrd recv'd sz=" + size);
                byte memr[] = new byte[memReadLength];
                rbuf.get(memr,0,memReadLength);
                synchronized (readsync) {
                    readsync.memReadBuffer = ByteBuffer.wrap(memr);
                    readsync.memReadBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    readsync.memReadBuffer.rewind();
                    readsync.Acked = true;
                    readsync.Pending = false;
                    readsync.notifyAll();
                }
                if (memReadHandler != null) {
                    try {
//                        System.out.println("handler: " + memReadHandler.toString());
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                memReadHandler.Done(readsync.memReadBuffer);
                            }
                        });
                    } catch (InterruptedException ex) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(USBBulkConnection_v2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                memReadHandler = null;
                GoIdleState();
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
        return patch.getController().getModel();
    }
}