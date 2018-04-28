/**
 * Copyright (C) 2018 Johannes Taelman
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
* Provides a firmware upgrade method from 1.0.12 firmware to current firmware.
 * Does not support multiple boards connected.
 *
 */
import axoloti.Axoloti;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author Johannes Taelman
 */
public class FirmwareUpgrade_1_0_12 {

    private boolean disconnectRequested;
    private Thread transmitterThread;
    private Thread receiverThread;
    private DeviceHandle handle;
    private final int interfaceNumber = 2;

    public FirmwareUpgrade_1_0_12(DeviceHandle handle) {
        this.sync = new Sync();
        this.readsync = new Sync();
        disconnectRequested = false;
        GoIdleState();
        if (handle == null) {
            return;
        }
        this.handle = handle;

        try //devicePath = Usb.DeviceToPath(device);
        {

            int result = LibUsb.claimInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to claim interface", result);
            }

            GoIdleState();
            //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "creating rx and tx thread...");
            receiverThread = new Thread(new Receiver());
            receiverThread.setName("Receiver");
            receiverThread.start();
            transmitterThread = new Thread(new Transmitter());
            transmitterThread.setName("Transmitter");
            transmitterThread.start();
        } catch (Exception ex) {
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static final byte OUT_ENDPOINT = 0x02;
    static final byte IN_ENDPOINT = (byte) 0x82;
    static final int TIMEOUT = 1000;

    public void writeBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        IntBuffer transfered = IntBuffer.allocate(1);
        int result = LibUsb.bulkTransfer(handle, (byte) OUT_ENDPOINT, buffer, transfered, 1000);
        if (result != LibUsb.SUCCESS) {
            if (result == LibUsb.ERROR_NO_DEVICE) {
                Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "Device disconnected");
            } else if (result == LibUsb.ERROR_TIMEOUT) {
                Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "USB transmit timeout");
            } else {
                Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "Control transfer failed: {0}", result);
            }
        }
        //System.out.println(transfered.get() + " bytes sent");
    }

    public void TransmitGetFWVersion() {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'V';
        writeBytes(data);
    }

    public void SelectPort() {
        // stripped
    }

    class Sync {
        boolean Acked = false;
    }
    final Sync sync;
    final Sync readsync;

    public void ClearSync() {
        synchronized (sync) {
            sync.Acked = false;
        }
    }

    public boolean WaitSync(int msec) {
        synchronized (sync) {
            if (sync.Acked) {
                return sync.Acked;
            }
            try {
                sync.wait(msec);
            } catch (InterruptedException ex) {
            }
            return sync.Acked;
        }
    }

    public boolean WaitSync() {
        return WaitSync(1000);
    }

    public void ClearReadSync() {
        synchronized (readsync) {
            readsync.Acked = false;
        }
    }

    public boolean WaitReadSync() {
        synchronized (readsync) {
            if (readsync.Acked) {
                return readsync.Acked;
            }
            try {
                readsync.wait(1000);
            } catch (InterruptedException ex) {
            }
            return readsync.Acked;
        }
    }

    private final byte[] startPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('s')};
    private final byte[] stopPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('S')};
    private final byte[] pingPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('p')};

    public void TransmitStart() {
        writeBytes(startPckt);
    }

    public void TransmitStop() {
        writeBytes(stopPckt);
    }

    public void TransmitPing() {
        writeBytes(pingPckt);
    }

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
        Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.INFO, "block uploaded @ 0x{0} length {1}", new Object[]{Integer.toHexString(offset).toUpperCase(), Integer.toString(buffer.length)});
    }

    class Receiver implements Runnable {

        @Override
        public void run() {
            ByteBuffer recvbuffer = ByteBuffer.allocateDirect(32768);
            IntBuffer transfered = IntBuffer.allocate(1);
            while (!disconnectRequested) {
                int result = LibUsb.bulkTransfer(handle, (byte) IN_ENDPOINT, recvbuffer, transfered, 1000);
                if (result != LibUsb.SUCCESS) {
                    //Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receive: " + result);
                }
                {
                    int sz = transfered.get(0);
                    if (sz != 0) {
//                        Logger.getLogger(USBBulkConnection.class.getName()).log(Level.INFO, "receive sz: " + sz);
                    }
                    for (int i = 0; i < sz; i++) {
                        processByte(recvbuffer.get(i));
                    }
                }
            }
        }
    }

    int getSDRAMAddr() {
        return 0xC0000000;
    }

    public void uploadFWSDRam(File firmwareFile) {
        ClearSync();
        try {
            if (firmwareFile == null) {
                String buildDir = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build";
                firmwareFile = new File(buildDir + "/axoloti.bin");
            }
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.INFO, "firmware file path: {0}", firmwareFile.getAbsolutePath());
            int tlength = (int) firmwareFile.length();
            FileInputStream inputStream = new FileInputStream(firmwareFile);

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
                Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
            }
            inputStream.close();
            inputStream = new FileInputStream(firmwareFile);
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.INFO, "firmware file size: {0}", tlength);
//            bb.order(ByteOrder.LITTLE_ENDIAN);
            CRC32 zcrc = new CRC32();
            zcrc.update(bb);
            int zcrcv = (int) zcrc.getValue();
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.INFO, "firmware crc: 0x{0}", Integer.toHexString(zcrcv).toUpperCase());
            header[12] = (byte) (zcrcv);
            header[13] = (byte) (zcrcv >> 8);
            header[14] = (byte) (zcrcv >> 16);
            header[15] = (byte) (zcrcv >> 24);
            UploadFragment(header, getSDRAMAddr() + offset);
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
                nRead = inputStream.read(buffer, 0, l);
                if (nRead != l) {
                    Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
                }
                UploadFragment(buffer, getSDRAMAddr() + offset);
                offset += nRead;
            } while (tlength > 0);
            inputStream.close();
            return;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "FileNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }

    void uploadFirmwareFlasher(File f) throws FileNotFoundException, IOException {
        int tlength = (int) f.length();
        FileInputStream inputStream = new FileInputStream(f);
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
                Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
            }
            UploadFragment(buffer, 0x20011000 + offset);
            offset += nRead;
        } while (tlength > 0);
        inputStream.close();
    }

    class Transmitter implements Runnable {

        @Override
        public void run() {
            try {
                TransmitStop();
                TransmitPing();
                TransmitPing();
                WaitSync();
                uploadFWSDRam(null);
                ClearSync();
                TransmitPing();
                WaitSync();
                String pname = System.getProperty(Axoloti.RELEASE_DIR) + "/old_firmware/firmware-1.0.12/flasher.bin";
                File firmwareFile = new File(pname);
                uploadFirmwareFlasher(firmwareFile);
                ClearSync();
                TransmitPing();
                WaitSync();
                TransmitStart();
            } catch (Exception ex) {
                Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, null, ex);
            }
            disconnectRequested = true;
            if (receiverThread.isAlive()) {
                receiverThread.interrupt();
                try {
                    receiverThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            int result = LibUsb.releaseInterface(handle, interfaceNumber);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to release interface", result);
            }

            LibUsb.close(handle);

            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "Firmware flashing in progress, do not unplug the board until the leds stop blinking! You can connect again after the leds stop blinking.");
        }
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
    private ByteBuffer memReadBuffer = ByteBuffer.allocate(16 * 4);
    private int memReadAddr;
    private int memReadLength;
    private int memReadValue;
    private byte[] fwversion = new byte[4];
    private int patchentrypoint;

    public ByteBuffer getMemReadBuffer() {
        return memReadBuffer;
    }

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
            Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.FINE, "Lots of data coming! {0} / {1}", new Object[]{Integer.toHexString(i1), Integer.toHexString(i2)});
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

    void GoIdleState() {
        headerstate = 0;
        state = ReceiverState.header;
    }
    ByteBuffer dispData;

    int LCDPacketRow = 0;

    void processByte(byte cc) {
//            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE,"AxoP c="+(char)c+"="+Integer.toHexString(c)+" s="+Integer.toHexString(state));
        int c = cc & 0xff;
//            System.out.println("AxoP c="+(char)c+"="+Integer.toHexString(c));
        switch (state) {
            case header:
                switch (headerstate) {
                    case 0:
                        if (c == 'A') {
                            headerstate = 1;
                        }
                        break;
                    case 1:
                        if (c == 'x') {
                            headerstate = 2;
                        } else {
                            GoIdleState();
                        }
                        break;
                    case 2:
                        if (c == 'o') {
                            headerstate = 3;
                        } else {
                            GoIdleState();
                        }
                        break;
                    case 3:
                        switch (c) {
                            case 'Q':
                                state = ReceiverState.paramchangePckt;
                                //System.out.println("param packet start");
                                dataIndex = 0;
                                dataLength = 12;
                                break;
                            case 'A':
                                state = ReceiverState.ackPckt;
                                //System.out.println("ack packet start");
                                dataIndex = 0;
                                dataLength = 24;
                                break;
                            case 'D':
                                state = ReceiverState.displayPcktHdr;
                                //System.out.println("display packet start");
                                dataIndex = 0;
                                dataLength = 8;
                                break;
                            case 'T':
                                state = ReceiverState.textPckt;
                                //System.out.println("text packet start");
                                textRcvBuffer.clear();
                                dataIndex = 0;
                                dataLength = 255;
                                break;
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                                LCDPacketRow = c - '0';
                                state = ReceiverState.lcdPckt;
                                //System.out.println("text packet start");
                                lcdRcvBuffer.rewind();
                                dataIndex = 0;
                                dataLength = 128;
                                break;
                            case 'd':
                                state = ReceiverState.sdinfo;
                                sdinfoRcvBuffer.rewind();
                                dataIndex = 0;
                                dataLength = 12;
                                break;
                            case 'f':
                                state = ReceiverState.fileinfo;
                                fileinfoRcvBuffer.clear();
                                dataIndex = 0;
                                dataLength = 8;
                                break;
                            case 'r':
                                state = ReceiverState.memread;
                                memReadBuffer.clear();
                                dataIndex = 0;
                                break;
                            case 'y':
                                state = ReceiverState.memread1word;
                                dataIndex = 0;
                                break;
                            case 'V':
                                state = ReceiverState.fwversion;
                                dataIndex = 0;
                                break;
                            default:
                                GoIdleState();
                                break;
                        }
                        break;
                    default:
                        Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.SEVERE, "receiver: invalid header");
                        GoIdleState();

                        break;
                }
                break;
            case paramchangePckt:
                if (dataIndex < dataLength) {
                    storeDataByte(c);
                }
//                    System.out.println("pch packet i=" +dataIndex + " v=" + c + " c="+ (char)(cc));
                if (dataIndex == dataLength) {
                    //System.out.println("param packet complete 0x" + Integer.toHexString(packetData[1]) + "    0x" + Integer.toHexString(packetData[0]));
                    GoIdleState();
                }
                break;
            case ackPckt:
                if (dataIndex < dataLength) {
                    //System.out.println("ack packet i=" +dataIndex + " v=" + c + " c="+ (char)(cc));
                    storeDataByte(c);
                }
                if (dataIndex == dataLength) {
                    //System.out.println("ack packet complete");
                    Acknowledge(packetData[1], packetData[2], packetData[3], packetData[4], packetData[5]);
                    GoIdleState();
                }
                break;
            case lcdPckt:
                if (dataIndex < dataLength) {
                    //System.out.println("lcd packet i=" +dataIndex + " v=" + c + " c="+ (char)(cc));
                    lcdRcvBuffer.put(cc);
                    dataIndex++;
                }
                if (dataIndex == dataLength) {
                    lcdRcvBuffer.rewind();
                    GoIdleState();
                }
                break;
            case displayPcktHdr:
                if (dataIndex < dataLength) {
                    storeDataByte(c);
                }
//                    System.out.println("pch packet i=" +dataIndex + " v=" + c + " c="+ (char)(cc));
                if (dataIndex == dataLength) {
                    DisplayPackHeader(packetData[0], packetData[1]);
                }
                break;
            case displayPckt:
                if (dataIndex < dataLength) {
                    dispData.put(cc);
                    dataIndex++;
                }
                if (dataIndex == dataLength) {
                    GoIdleState();
                }
                break;
            case textPckt:
                if (c != 0) {
                    textRcvBuffer.append((char) cc);
                } else {
                    //textRcvBuffer.append((char) cc);
                    textRcvBuffer.limit(textRcvBuffer.position());
                    textRcvBuffer.rewind();
                    Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).log(Level.WARNING, "{0}", textRcvBuffer.toString());
                    GoIdleState();
                }
                break;
            case sdinfo:
                if (dataIndex < dataLength) {
                    sdinfoRcvBuffer.put(cc);
                    dataIndex++;
                }
                if (dataIndex == dataLength) {
                    sdinfoRcvBuffer.rewind();
                    sdinfoRcvBuffer.order(ByteOrder.LITTLE_ENDIAN);
//                    Logger.getLogger(SerialConnection.class.getName()).info("sdinfo: "
//                            + sdinfoRcvBuffer.asIntBuffer().get(0) + " "
//                            + sdinfoRcvBuffer.asIntBuffer().get(1) + " "
//                            + sdinfoRcvBuffer.asIntBuffer().get(2));
                    GoIdleState();
                }
                break;
            case fileinfo:
                if ((dataIndex < dataLength) || (c != 0)) {
                    fileinfoRcvBuffer.put(cc);
//                    System.out.println("fileinfo \'" + (char) c + "\' = " + c);
                    dataIndex++;
                } else {
                    fileinfoRcvBuffer.put((byte) c);
                    fileinfoRcvBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    fileinfoRcvBuffer.limit(fileinfoRcvBuffer.position());
                    fileinfoRcvBuffer.rewind();
                    int size = fileinfoRcvBuffer.getInt();
                    int timestamp = fileinfoRcvBuffer.getInt();
                    CharBuffer cb = Charset.forName("ISO-8859-1").decode(fileinfoRcvBuffer);
                    String fname = cb.toString();
                    // strip trailing null
                    if (fname.charAt(fname.length() - 1) == (char) 0) {
                        fname = fname.substring(0, fname.length() - 1);
                    }
//                    Logger.getLogger(USBBulkConnection.class.getName()).info("fileinfo: " + cb.toString());
                    GoIdleState();
                    if (fname.equals("/")) {
                        // end of index
//                        System.out.println("sdfilelist done");
                        synchronized (readsync) {
                            readsync.Acked = true;
                            readsync.notifyAll();
                        }
                    }
                }
                break;
            case memread:
                switch (dataIndex) {
                    case 0:
                        memReadAddr = (cc & 0xFF);
                        break;
                    case 1:
                        memReadAddr += (cc & 0xFF) << 8;
                        break;
                    case 2:
                        memReadAddr += (cc & 0xFF) << 16;
                        break;
                    case 3:
                        memReadAddr += (cc & 0xFF) << 24;
                        break;
                    case 4:
                        memReadLength = (cc & 0xFF);
                        break;
                    case 5:
                        memReadLength += (cc & 0xFF) << 8;
                        break;
                    case 6:
                        memReadLength += (cc & 0xFF) << 16;
                        break;
                    case 7:
                        memReadLength += (cc & 0xFF) << 24;
                        break;
                    case 8:
                        memReadBuffer = ByteBuffer.allocate(memReadLength);
                        memReadBuffer.rewind();
                    default:
                        memReadBuffer.put(cc);
                        if (dataIndex == memReadLength + 7) {
                            memReadBuffer.rewind();
                            memReadBuffer.order(ByteOrder.LITTLE_ENDIAN);
                            /*
                             System.out.println("memread offset 0x" + Integer.toHexString(memReadAddr));
                             int i = 0;
                             while (memReadBuffer.hasRemaining()) {
                             System.out.print(" " + String.format("%02X", memReadBuffer.get()));
                             i++;
                             //if ((i % 4) == 0) {
                             //    System.out.print(" ");
                             //}
                             if ((i % 32) == 0) {
                             System.out.println();
                             }
                             }
                             System.out.println();
                             */
                            synchronized (readsync) {
                                readsync.Acked = true;
                                readsync.notifyAll();
                            }
                            GoIdleState();
                        }
                }
                dataIndex++;
                break;

            case memread1word:
                switch (dataIndex) {
                    case 0:
                        memReadAddr = (cc & 0xFF);
                        break;
                    case 1:
                        memReadAddr += (cc & 0xFF) << 8;
                        break;
                    case 2:
                        memReadAddr += (cc & 0xFF) << 16;
                        break;
                    case 3:
                        memReadAddr += (cc & 0xFF) << 24;
                        break;
                    case 4:
                        memReadValue = (cc & 0xFF);
                        break;
                    case 5:
                        memReadValue += (cc & 0xFF) << 8;
                        break;
                    case 6:
                        memReadValue += (cc & 0xFF) << 16;
                        break;
                    case 7:
                        memReadValue += (cc & 0xFF) << 24;
                        //System.out.println(String.format("addr %08X value %08X", memReadAddr, memReadValue));
                        synchronized (readsync) {
                            readsync.Acked = true;
                            readsync.notifyAll();
                        }
                        GoIdleState();
                }
                dataIndex++;
                break;

            case fwversion:
                switch (dataIndex) {
                    case 0:
                        fwversion[0] = cc;
                        break;
                    case 1:
                        fwversion[1] = cc;
                        break;
                    case 2:
                        fwversion[2] = cc;
                        break;
                    case 3:
                        fwversion[3] = cc;
                        break;
                    case 4:
                        fwcrc = (cc & 0xFF) << 24;
                        break;
                    case 5:
                        fwcrc += (cc & 0xFF) << 16;
                        break;
                    case 6:
                        fwcrc += (cc & 0xFF) << 8;
                        break;
                    case 7:
                        fwcrc += (cc & 0xFF);
                        break;
                    case 8:
                        patchentrypoint = (cc & 0xFF) << 24;
                        break;
                    case 9:
                        patchentrypoint += (cc & 0xFF) << 16;
                        break;
                    case 10:
                        patchentrypoint += (cc & 0xFF) << 8;
                        break;
                    case 11:
                        patchentrypoint += (cc & 0xFF);
                        String sFwcrc = String.format("%08X", fwcrc);
                        Logger.getLogger(FirmwareUpgrade_1_0_12.class.getName()).info(String.format("Firmware version: %d.%d.%d.%d, crc=0x%s, entrypoint=0x%08X",
                                fwversion[0], fwversion[1], fwversion[2], fwversion[3], sFwcrc, patchentrypoint));
                        //MainFrame.mainframe.setFirmwareID(sFwcrc);
                        GoIdleState();
                        break;
                }
                dataIndex++;
                break;

            default:
                GoIdleState();
                break;
        }
    }

}
