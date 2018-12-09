package axoloti.connection;

import axoloti.chunks.ChunkParser;
import axoloti.job.IJobContext;
import axoloti.live.patch.PatchViewLive;
import axoloti.mvc.View;
import static axoloti.swingui.dialogs.USBPortSelectionDlg.convertErrorToString;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDCardMountStatusListener;
import axoloti.target.fs.SDFileInfo;
import axoloti.targetprofile.axoloti_core;
import axoloti.usb.LibUSBContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author jtaelman
 */
public abstract class IConnection extends View<TargetModel> {

    public IConnection(TargetModel targetModel) {
        super(targetModel);
    }

    abstract public boolean isConnected();
    abstract public void disconnect();
    abstract public boolean connect(String cpuid);
    abstract public void selectPort();

    abstract public void transmitPacket(byte[] b) throws IOException;

    abstract public void transmitStop() throws IOException;

    abstract public void transmitStart() throws IOException;

    abstract public void transmitStart(String patchName) throws IOException;

    abstract public void transmitStart(int patchIndex) throws IOException;

    abstract public void transmitPing() throws IOException;

    abstract public void transmitRecallPreset(int presetNo) throws IOException;

    abstract public void transmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3) throws IOException;

    abstract public ByteBuffer read(int addr, int length) throws IOException;
    abstract public void write(int addr, byte[] data) throws IOException;
    abstract public void write(int address, File f) throws FileNotFoundException, IOException;

    abstract public SDCardInfo getFileList() throws IOException;

    abstract public SDFileInfo getFileInfo(String filename) throws IOException;

    abstract public void upload(String filename, InputStream inputStream, Calendar cal, int size, IJobContext ctx) throws IOException;

    abstract public ByteBuffer download(String filename, IJobContext ctx) throws IOException;

    abstract public void createDirectory(String filename, Calendar date) throws IOException;

    abstract public void deleteFile(String filename) throws IOException;

    abstract public void transmitChangeWorkingDirectory(String path) throws IOException;

    abstract public void sendUpdatedPreset(byte[] b) throws IOException;

    abstract public void sendMidi(int cable, byte m0, byte m1, byte m2) throws IOException;

    abstract public void transmitGetFWVersion() throws IOException;

    abstract public void transmitCopyToFlash() throws IOException;

    abstract public void bringToDFU() throws IOException;

    abstract public void setPatch(PatchViewLive patchViewLive);
    abstract public axoloti_core getTargetProfile();
    abstract public boolean getSDCardPresent();
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
        getDModel().setSDCardInfo(new SDCardInfo(0, 0, 0, Collections.emptyList()));
        getDModel().setSDCardMounted(false);
    }

}
