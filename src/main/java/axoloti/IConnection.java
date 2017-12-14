package axoloti;

import axoloti.chunks.ChunkParser;
import static axoloti.dialogs.USBPortSelectionDlg.ErrorString;
import axoloti.mvc.IView;
import axoloti.targetprofile.axoloti_core;
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
public abstract class IConnection implements IView<TargetController> {
    
    public interface MemReadHandler
    {
        public void Done(ByteBuffer mem);
    }
    
    abstract public boolean isConnected();
    abstract public void disconnect();
    abstract public boolean connect(String cpuid);
    abstract public void SelectPort();
    abstract public void TransmitStop();
    abstract public void TransmitStart();
    abstract public void TransmitPing();
    abstract public void TransmitRecallPreset(int presetNo);
    abstract public void UploadFragment(byte[] buffer, int offset);
    abstract public void TransmitGetFileList();
    abstract public void TransmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3);
    abstract public void TransmitCreateFile(String filename, int size);
    abstract public void TransmitGetFileInfo(String filename);
    abstract public void TransmitCreateFile(String filename, int size, Calendar date);
    abstract public void TransmitCreateDirectory(String filename, Calendar date);
    abstract public void TransmitDeleteFile(String filename);
    abstract public void TransmitChangeWorkingDirectory(String path);
    abstract public void TransmitAppendFile(byte[] buffer);
    abstract public void TransmitCloseFile();
    abstract public void TransmitMemoryRead(int addr, int length, MemReadHandler handler);
    abstract public void TransmitMemoryRead(int addr, int length);
    abstract public void TransmitMemoryRead1Word(int addr);    
    abstract public void SendUpdatedPreset(byte[] b);
    abstract public void SendMidi(int cable, int m0, int m1, int m2);
    abstract public boolean AppendToQueue(QCmdSerialTask cmd);
    abstract public void TransmitGetFWVersion();
    abstract public void TransmitCopyToFlash();
    abstract public void BringToDFU();
    abstract public void ClearSync();
    abstract public boolean WaitSync(int msec);
    abstract public boolean WaitSync();
    abstract public void ClearReadSync();
    abstract public boolean WaitReadSync();
    abstract public void setPatch(PatchViewCodegen patchViewCodegen);
    abstract public axoloti_core getTargetProfile();
    abstract public ByteBuffer getMemReadBuffer();
    abstract public int getMemRead1Word();
    abstract public boolean GetSDCardPresent();
    abstract public void setDisplayAddr(int a, int l);
    abstract public ChunkParser GetFWChunks();
    abstract public String getFWID();

    private final static short bulkVID = (short) 0x16C0;
    private final static short bulkPID = (short) 0x0442;

    public static DeviceHandle OpenDeviceHandle(String _cpuid) {
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
                if (descriptor.idVendor() == bulkVID && descriptor.idProduct() == bulkPID) {
                    Logger.getLogger(IConnection.class.getName()).log(Level.INFO, "USB device found");
                    DeviceHandle h = new DeviceHandle();
                    result = LibUsb.open(d, h);
                    if (result < 0) {
                        Logger.getLogger(IConnection.class.getName()).log(Level.INFO, ErrorString(result));
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
                if (descriptor.idVendor() == bulkVID && descriptor.idProduct() == bulkPID) {
                    Logger.getLogger(IConnection.class.getName()).log(Level.INFO, "USB device found");
                    DeviceHandle h = new DeviceHandle();
                    result = LibUsb.open(d, h);
                    if (result < 0) {
                        Logger.getLogger(IConnection.class.getName()).log(Level.INFO, ErrorString(result));
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

    private ArrayList<ConnectionStatusListener> csls = new ArrayList<ConnectionStatusListener>();

    public void addConnectionStatusListener(ConnectionStatusListener csl) {
        if (isConnected()) {
            csl.ShowConnect();
        } else {
            csl.ShowDisconnect();            
        }
        csls.add(csl);
    }

    public void removeConnectionStatusListener(ConnectionStatusListener csl) {
        csls.remove(csl);
    }

    public void ShowDisconnect() {
        for (ConnectionStatusListener csl : csls) {
            csl.ShowDisconnect();
        }
        getController().getModel().setConnection(null);
        MainFrame.mainframe.WarnedAboutFWCRCMismatch = false;
    }

    public void ShowConnect() {
        for (ConnectionStatusListener csl : csls) {
            csl.ShowConnect();
        }
        getController().getModel().setConnection(this);
    }

    private ArrayList<SDCardMountStatusListener> sdcmls = new ArrayList<SDCardMountStatusListener>();

    public void addSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        if (GetSDCardPresent()) {
            sdcml.ShowSDCardMounted();
        } else {
            sdcml.ShowSDCardUnmounted();
        }
        sdcmls.add(sdcml);
    }

    public void removeSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        sdcmls.remove(sdcml);
    }

    public void ShowSDCardMounted() {
        for (SDCardMountStatusListener sdcml : sdcmls) {
            sdcml.ShowSDCardMounted();
        }
        getController().getModel().setSDCardMounted(true);
    }

    public void ShowSDCardUnmounted() {
        for (SDCardMountStatusListener sdcml : sdcmls) {
            sdcml.ShowSDCardUnmounted();
        }
        SDCardInfo.getInstance().SetInfo(0, 0, 0);
        getController().getModel().setSDCardMounted(false);
    }

    @Deprecated
    abstract public void writeBytes(byte[] data);

}
