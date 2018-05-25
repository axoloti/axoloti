package axoloti.connection;

import axoloti.chunks.ChunkParser;
import axoloti.live.patch.PatchViewLive;
import axoloti.mvc.View;
import static axoloti.swingui.dialogs.USBPortSelectionDlg.convertErrorToString;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardMountStatusListener;
import axoloti.targetprofile.axoloti_core;
import axoloti.usb.LibUSBContext;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import qcmds.QCmdSerialTask;

/**
 *
 * @author jtaelman
 */
public abstract class IConnection extends View<TargetModel> {

    public IConnection(TargetModel targetModel) {
        super(targetModel);
    }

    public interface MemReadHandler
    {
        public void done(ByteBuffer mem);
    }

    abstract public boolean isConnected();
    abstract public void disconnect();
    abstract public boolean connect(String cpuid);
    abstract public void selectPort();
    abstract public void transmitStop();
    abstract public void transmitStart();
    abstract public void transmitStart(String patchName);
    abstract public void transmitStart(int patchIndex);
    abstract public void transmitPing();
    abstract public void transmitRecallPreset(int presetNo);
    abstract public void uploadFragment(byte[] buffer, int offset);
    abstract public void transmitGetFileList();
    abstract public void transmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3);
    abstract public void transmitCreateFile(String filename, int size);
    abstract public void transmitGetFileInfo(String filename);
    abstract public void transmitGetFileContents(String filename, MemReadHandler handler);
    abstract public void transmitCreateFile(String filename, int size, Calendar date);
    abstract public void transmitCreateDirectory(String filename, Calendar date);
    abstract public void transmitDeleteFile(String filename);
    abstract public void transmitChangeWorkingDirectory(String path);
    abstract public void transmitAppendFile(byte[] buffer);
    abstract public void transmitCloseFile();
    abstract public void transmitMemoryRead(int addr, int length, MemReadHandler handler);
    abstract public void transmitMemoryRead(int addr, int length);
    abstract public void transmitMemoryRead1Word(int addr);
    abstract public void sendUpdatedPreset(byte[] b);
    abstract public void sendMidi(int cable, int m0, int m1, int m2);
    abstract public boolean appendToQueue(QCmdSerialTask cmd);
    abstract public void transmitGetFWVersion();
    abstract public void transmitCopyToFlash();
    abstract public void bringToDFU();
    abstract public void clearSync();
    abstract public boolean waitSync(int msec);
    abstract public boolean waitSync();
    abstract public void clearReadSync();
    abstract public boolean waitReadSync();
    abstract public void setPatch(PatchViewLive patchViewLive);
    abstract public axoloti_core getTargetProfile();
    abstract public ByteBuffer getMemReadBuffer();
    abstract public int getMemRead1Word();
    abstract public boolean getSDCardPresent();
    abstract public void setDisplayAddr(int a, int l);
    abstract public ChunkParser getFWChunks();
    abstract public String getFWID();

    private final static short BULK_VID = (short) 0x16C0;
    private final static short BULK_PID = (short) 0x0442;

    public static DeviceHandle openDeviceHandle(String _cpuid) {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(LibUSBContext.getContext(), list);
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
                if (descriptor.idVendor() == BULK_VID && descriptor.idProduct() == BULK_PID) {
                    Logger.getLogger(IConnection.class.getName()).log(Level.INFO, "USB device found");
                    DeviceHandle h = new DeviceHandle();
                    result = LibUsb.open(d, h);
                    if (result < 0) {
                        Logger.getLogger(IConnection.class.getName()).log(Level.INFO, convertErrorToString(result));
                    } else {
                        String serial = LibUsb.getStringDescriptor(h, descriptor.iSerialNumber());
                        if (_cpuid != null) {
                            if (serial.equals(_cpuid)) {
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
                if (descriptor.idVendor() == BULK_VID && descriptor.idProduct() == BULK_PID) {
                    Logger.getLogger(IConnection.class.getName()).log(Level.INFO, "USB device found");
                    DeviceHandle h = new DeviceHandle();
                    result = LibUsb.open(d, h);
                    if (result < 0) {
                        Logger.getLogger(IConnection.class.getName()).log(Level.INFO, convertErrorToString(result));
                    } else {
                        return h;
                    }
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            //LibUsb.freeDeviceList(list, true);
        }
        Logger.getLogger(IConnection.class.getName()).log(Level.SEVERE, "No available USB device found with matching PID/VID");
        // Device not found
        return null;
    }

    private final ArrayList<ConnectionStatusListener> csls = new ArrayList<>();

    public void addConnectionStatusListener(ConnectionStatusListener csl) {
        if (isConnected()) {
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
        getDModel().setConnection(null);
        getDModel().setWarnedAboutFWCRCMismatch(false);
    }

    public void showConnect() {
        for (ConnectionStatusListener csl : csls) {
            csl.showConnect();
        }
        getDModel().setConnection(this);
    }

    private final ArrayList<SDCardMountStatusListener> sdcmls = new ArrayList<>();

    public void addSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        if (getSDCardPresent()) {
            sdcml.showSDCardMounted();
        } else {
            sdcml.showSDCardUnmounted();
        }
        sdcmls.add(sdcml);
    }

    public void removeSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        sdcmls.remove(sdcml);
    }

    public void showSDCardMounted() {
        for (SDCardMountStatusListener sdcml : sdcmls) {
            sdcml.showSDCardMounted();
        }
        getDModel().setSDCardMounted(true);
    }

    public void showSDCardUnmounted() {
        for (SDCardMountStatusListener sdcml : sdcmls) {
            sdcml.showSDCardUnmounted();
        }
        getDModel().getSDCardInfo().setInfo(0, 0, 0);
        getDModel().setSDCardMounted(false);
    }

    @Deprecated
    abstract public void writeBytes(byte[] data);

}
