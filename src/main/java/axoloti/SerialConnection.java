/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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

import axoloti.dialogs.SerialPortSelectionDlg;
import axoloti.parameters.ParameterInstance;
import axoloti.targetprofile.axoloti_core;
import displays.DisplayInstance;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

import jssc.*;
import jssc.SerialPortException;
import qcmds.QCmd;
import qcmds.QCmdSerialTask;
import qcmds.QCmdSerialTaskNull;
import qcmds.QCmdShowDisconnect;

/**
 *
 * @author Johannes Taelman
 */
public class SerialConnection {

    Patch patch;
    private SerialPort serialPort;
    boolean disconnectRequested;
    boolean connected;
    Thread transmitterThread;
    BlockingQueue<QCmdSerialTask> queueSerialTask;
    private BlockingQueue<QCmd> queueResponse;
    String portName;
    private axoloti_core targetProfile = new axoloti_core();

    public SerialConnection(Patch patch, BlockingQueue<QCmd> queueResponse) {
        this.sync = new Sync();
        this.patch = patch;
        this.queueResponse = queueResponse;
        disconnectRequested = false;
        connected = false;
        queueSerialTask = new ArrayBlockingQueue<QCmdSerialTask>(10);
    }

    void SelectSerialPort() {
        SerialPortSelectionDlg spsDlg = new SerialPortSelectionDlg(null, true, portName);
        spsDlg.setVisible(true);
        portName = spsDlg.getPort();
        Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "port: " + portName);
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
    }

    public void Panic() {
        queueSerialTask.clear();
        disconnect();
    }

    public boolean isConnected() {
        return connected && (!disconnectRequested);
    }

    public boolean AppendToQueue(QCmdSerialTask cmd) {
        return queueSerialTask.add(cmd);
    }

    public void disconnect() {
        if (connected) {
            disconnectRequested = true;
            MainFrame.mainframe.ShowDisconnect();
            queueSerialTask.clear();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            queueSerialTask.add(new QCmdSerialTaskNull());
            queueSerialTask.add(new QCmdSerialTaskNull());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "Disconnect request");
            try {
                serialPort.purgePort(jssc.SerialPort.PURGE_RXCLEAR | jssc.SerialPort.PURGE_TXCLEAR);
                serialPort.closePort();
            } catch (SerialPortException ex) {
                Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = false;
            CpuId0 = 0;
            CpuId1 = 0;
            CpuId2 = 0;
        }
    }

    public Boolean connect() {
        disconnect();
        disconnectRequested = false;
        synchronized (sync) {
            sync.Acked = 1;
            sync.notifyAll();
        }
        GoIdleState();
        if (portName == null) {
            portName = MainFrame.prefs.getComPortName();
        }
        List<String> pl = Arrays.asList(SerialPortList.getPortNames());
        if ((portName == null) || (portName.isEmpty()) || (!pl.contains(portName))) {
            SelectSerialPort();
            if (portName == null) {
                return false;
            }
            if (portName.isEmpty()) {
                return false;
            }
            MainFrame.prefs.setComPortName(portName);
            MainFrame.prefs.SavePrefs();
        }
        try {
            Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "Initiating connect to " + portName);
            serialPort = new SerialPort(portName);
            serialPort.openPort();

            serialPort.setParams(115200, 8, 1, 0);//Set params
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent spe) {
                    if (spe.isRXCHAR()) {
                        try {
                            byte[] r = serialPort.readBytes();
                            if (r != null) {
                                for (byte b : r) {
                                    processByte(b);
                                }
                            }
                        } catch (SerialPortException ex) {
                            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            });
            GoIdleState();
            TransmitPing();
            TransmitPing();
            Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "creating tx thread...");
            transmitterThread = new Thread(new SerialTransmitter());
            transmitterThread.setName("SerialTransmitter");
            transmitterThread.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

            connected = true;
            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "connected");
            MainFrame.mainframe.ShowConnect();
            return true;

        } catch (SerialPortException ex) {
            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        MainFrame.mainframe.ShowDisconnect();
        return false;
    }

    public void writeBytes(byte[] data) throws SerialPortException {
        serialPort.writeBytes(data);
    }

    public void TransmitRecallPreset(int presetNo) throws SerialPortException {
        byte[] data = new byte[5];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'T';
        data[4] = (byte) presetNo;
        serialPort.writeBytes(data);
    }

    public void BringToDFU() throws SerialPortException {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'D';
        serialPort.writeBytes(data);
    }

    public void SendMidi(int m0, int m1, int m2) throws SerialPortException {
        byte[] data = new byte[7];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'M';
        data[4] = (byte) m0;
        data[5] = (byte) m1;
        data[6] = (byte) m2;
        serialPort.writeBytes(data);
    }

    public void SendUpdatedPreset(byte[] b) throws SerialPortException {
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
        serialPort.writeBytes(data);
        serialPort.writeBytes(b);
    }

    class Sync {

        int Acked = 0;
    }
    final Sync sync;

    public void ClearSync() {
        synchronized (sync) {
            sync.Acked = 0;
        }
    }

    public boolean WaitSync() {
        synchronized (sync) {
            try {
                sync.wait(1000);
            } catch (InterruptedException ex) {
                //              Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "Sync wait interrupted");
            }
            if (sync.Acked == 1) {
                return true;
            } else {
                return false;
            }
        }
    }
    private final byte[] startPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('s')};
    private final byte[] stopPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('S')};
    private final byte[] pingPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('p')};
    private final byte[] getFileListPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('d')};
    private final byte[] copyToFlashPckt = new byte[]{(byte) ('A'), (byte) ('x'), (byte) ('o'), (byte) ('F')};

    public void TransmitStart() throws SerialPortException {
        serialPort.writeBytes(startPckt);
    }

    public void TransmitStop() throws SerialPortException {
        serialPort.writeBytes(stopPckt);
    }

    public void TransmitGetFileList() throws SerialPortException {
        serialPort.writeBytes(getFileListPckt);
    }

    public void TransmitPing() throws SerialPortException {
        serialPort.writeBytes(pingPckt);
    }

    public void TransmitCopyToFlash() throws SerialPortException {
        serialPort.writeBytes(copyToFlashPckt);
    }

    public void UploadFragment(byte[] buffer, int offset) throws SerialPortException {
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
        Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "block uploaded @ 0x" + Integer.toHexString(offset) + " length " + buffer.length);
    }

    public void TransmitVirtualButton(int b_or, int b_and, int enc1, int enc2, int enc3, int enc4) throws SerialPortException {
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

    public void TransmitCreateFile(String filename, int size) throws SerialPortException {
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

    public void TransmitAppendFile(byte[] buffer) throws SerialPortException {
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

    public void TransmitCloseFile() throws SerialPortException {
        byte[] data = new byte[4];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'c';
        ClearSync();
        writeBytes(data);
        WaitSync();
    }

    class SerialTransmitter implements Runnable {

        @Override
        public void run() {
//            Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO,"transmitter: thread started");
            while (!disconnectRequested) {
                try {
                    QCmdSerialTask cmd = queueSerialTask.take();
//                    Logger.getLogger(ShellProcessor.class.getName()).log(Level.INFO, "SerialConnection : "+ cmd.GetStartMessage());                
                    queueResponse.add(cmd.Do(SerialConnection.this));
                } catch (InterruptedException ex) {
                    Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "transmitter: thread stopped");
            MainFrame.mainframe.qcmdprocessor.Abort();
            MainFrame.mainframe.qcmdprocessor.AppendToQueue(new QCmdShowDisconnect());
        }
    }
    int CpuId0 = 0;
    int CpuId1 = 0;
    int CpuId2 = 0;
    int FirmwareId = -1;

    void Acknowledge(int FirmwareId, int DSPLoad, int PatchID, int CpuId0, int CpuId1, int CpuId2) {
        synchronized (sync) {
            sync.Acked = 1;
            sync.notify();
        }
        if ((CpuId0 != this.CpuId0) || (CpuId1 != this.CpuId1) || (CpuId2 != this.CpuId2)) {
            this.CpuId0 = CpuId0;
            this.CpuId1 = CpuId1;
            this.CpuId2 = CpuId2;
            MainFrame.mainframe.setCpuID(String.format("%08X%08X%08X", CpuId0, CpuId1, CpuId2));
        }
        if (FirmwareId != this.FirmwareId) {
            this.FirmwareId = FirmwareId;
            MainFrame.mainframe.setFirmwareID((String.format("%08X", FirmwareId)));
        }
        if (patch != null) {
            patch.SetDSPLoad(DSPLoad);
        }
    }

    void RPacketParamChange(final int index, final int value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (patch == null) {
                    Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "Rx paramchange patch null" + index + " " + value);
                    return;
                }
                if (index >= patch.ParameterInstances.size()) {
                    Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "Rx paramchange index out of range" + index + " " + value);
                    return;
                }
                ParameterInstance pi = patch.ParameterInstances.get(index);
                if (pi == null) {
                    Logger.getLogger(SerialConnection.class.getName()).log(Level.INFO, "Rx paramchange parameterInstance null" + index + " " + value);
                    return;
                }
//                System.out.println("rcv ppc objname:" + pi.axoObj.getInstanceName() + " pname:"+ pi.name);
                pi.SetValueRaw(value);
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
        fileinfo // file listing entry
    };
    /*
     Protocol documentation:
     "AxoP" + bb + vvvv -> parameter change index bb (16bit), value vvvv (32bit)
     */
    ReceiverState state = ReceiverState.header;
    int headerstate;
    int paramchangePcktState;
    int[] packetData = new int[64];
    int dataIndex = 0; // in bytes
    int dataLength = 0; // in bytes
    CharBuffer textRcvBuffer = CharBuffer.allocate(256);
    ByteBuffer lcdRcvBuffer = ByteBuffer.allocate(256);
    ByteBuffer sdinfoRcvBuffer = ByteBuffer.allocate(12);
    ByteBuffer fileinfoRcvBuffer = ByteBuffer.allocate(256);

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
            Logger.getLogger(SerialConnection.class.getName()).fine("Lots of data coming! " + Integer.toHexString(i1) + " / " + Integer.toHexString(i2));
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

    void DistributeToDisplays() {
//        Logger.getLogger(SerialConnection.class.getName()).info("Distr1");
        if (patch == null) {
            return;
        }
        if (patch.DisplayInstances == null) {
            return;
        }
//        Logger.getLogger(SerialConnection.class.getName()).info("Distr2");
        dispData.rewind();
        for (DisplayInstance d : patch.DisplayInstances) {
            d.ProcessByteBuffer(dispData);
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
                            case 'P':
                                state = ReceiverState.paramchangePckt;
                                //System.out.println("param packet start");
                                dataIndex = 0;
                                dataLength = 8;
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
                                dataLength = 4;
                                break;
                            default:
                                GoIdleState();
                                break;
                        }
                        break;
                    default:
                        Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "receiver: invalid header");
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
                    RPacketParamChange(packetData[1], packetData[0]);
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
                    Acknowledge(packetData[0], packetData[1], packetData[2], packetData[3], packetData[4], packetData[5]);
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
                    MainFrame.mainframe.remote.updateRow(LCDPacketRow, lcdRcvBuffer);
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
                    DistributeToDisplays();
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
                    Logger.getLogger(SerialConnection.class.getName()).info("Axoloti says: " + textRcvBuffer.toString());
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
                    MainFrame.mainframe.filemanager.ShowSDInfo(sdinfoRcvBuffer.asIntBuffer().get(0), sdinfoRcvBuffer.asIntBuffer().get(1), sdinfoRcvBuffer.asIntBuffer().get(2));
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
                    CharBuffer cb = Charset.forName("ISO-8859-1").decode(fileinfoRcvBuffer);
                    MainFrame.mainframe.filemanager.AddFile(cb.toString(), size);
//                    Logger.getLogger(SerialConnection.class.getName()).info("fileinfo: " + cb.toString());
                    GoIdleState();
                }
                break;
            default:
                GoIdleState();
                break;
        }
    }

    public axoloti_core getTargetProfile() {
        return targetProfile;
    }

}
