package axoloti.connection;

import static axoloti.connection.USBDeviceLister.convertErrorToString;
import axoloti.usb.LibUSBContext;
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
public class AxolotiDevice implements IDevice {

    private final String name;
    private final String location;
    private final String cpuID;

    public AxolotiDevice(String name, String location, String cpuID) {
        this.name = name;
        this.location = location;
        this.cpuID = cpuID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return cpuID;
    }

    @Override
    public String getType() {
        return "Axoloti Core";
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public boolean canConnect() {
        return true;
    }

    public DeviceHandle getDeviceHandle() {
        return openDeviceHandle(cpuID);
    }

    final static short BULK_VID = (short) 0x16C0;
    final static short BULK_PID = (short) 0x0442;

    static DeviceHandle openDeviceHandle(String _cpuid) {
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
            LibUsb.freeDeviceList(list, true);
        }
        Logger.getLogger(IConnection.class.getName()).log(Level.SEVERE, "No available USB device found with matching PID/VID");
        // Device not found
        return null;
    }

    @Override
    public String getCPUID() {
        return cpuID;
    }

}
