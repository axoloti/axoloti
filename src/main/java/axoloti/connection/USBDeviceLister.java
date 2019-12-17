package axoloti.connection;

import axoloti.preferences.Preferences;
import axoloti.usb.LibUSBContext;
import static axoloti.usb.Usb.PID_AXOLOTI;
import static axoloti.usb.Usb.PID_AXOLOTI_SDCARD;
import static axoloti.usb.Usb.PID_STM_DFU;
import static axoloti.usb.Usb.VID_AXOLOTI;
import static axoloti.usb.Usb.VID_STM;
import static axoloti.usb.Usb.deviceToPath;
import axoloti.utils.OSDetect;
import static axoloti.utils.OSDetect.getOS;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.HotplugCallback;
import org.usb4java.HotplugCallbackHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author jtaelman
 */
public class USBDeviceLister {

    private static USBDeviceLister instance;

    public static USBDeviceLister getInstance() {
        // singleton
        if (instance == null) {
            instance = new USBDeviceLister();
        }
        return instance;
    }

    private USBDeviceLister() {
        LibUSBContext.getContext();
        init2();
    }

    private void init2() {
        // Initialize the libusb context
        int result;

        // Check if hotplug is available
        if (!LibUsb.hasCapability(LibUsb.CAP_HAS_HOTPLUG)) {
            System.err.println("libusb doesn't support hotplug on this system");
        } else {

            // Start the event handling thread
            EventHandlingThread thread = new EventHandlingThread();
            thread.start();

            // Register the hotplug callback
            HotplugCallbackHandle callbackHandle = new HotplugCallbackHandle();
            result = LibUsb.hotplugRegisterCallback(null,
                    LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED,
                    LibUsb.HOTPLUG_ENUMERATE,
                    VID_AXOLOTI,
                    PID_AXOLOTI,
                    LibUsb.CLASS_VENDOR_SPEC,
                    new Callback(), null, callbackHandle);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to register hotplug callback",
                        result);
            }
        }
//        // Unregister the hotplug callback and stop the event handling thread
//        thread.abort();
//        LibUsb.hotplugDeregisterCallback(null, callbackHandle);
//        thread.join();
//
//        // Deinitialize the libusb context
//        LibUsb.exit(null);
    }

    static String convertErrorToString(int result) {
        if (result < 0) {
            if (getOS() == OSDetect.OS.WIN) {
                if (result == LibUsb.ERROR_NOT_FOUND) {
                    return "not accesseable : driver not installed";
                } else if (result == LibUsb.ERROR_ACCESS) {
                    return "not accesseable : busy?";
                } else {
                    return "not accesseable : " + result;
                }
            } else if (getOS() == OSDetect.OS.LINUX) {
                if (result == LibUsb.ERROR_ACCESS) {
                    return "insufficient permissions";
                    // log message:  - install udev rules by running axoloti/platform/linux/add_udev_rules.sh"
                } else {
                    return "not accesseable : " + result;
                }
            } else {
                return "not accesseable : " + result;
            }
        } else {
            return null;
        }
    }

    private static IDevice deviceFactory(Device device) {
        DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result == LibUsb.SUCCESS) {
            if (descriptor.idVendor() == VID_STM) {
                if (descriptor.idProduct() == PID_STM_DFU) {
                    DeviceHandle handle = new DeviceHandle();
                    result = LibUsb.open(device, handle);
                    if (result < 0) {
                        if (getOS() == OSDetect.OS.WIN) {
                            if (result == LibUsb.ERROR_NOT_SUPPORTED) {
                                return new BootloaderDevice("not accesseable : wrong driver installed");
                            } else if (result == LibUsb.ERROR_ACCESS) {
                                return new BootloaderDevice("not accesseable : busy?");
                            } else {
                                return new BootloaderDevice("not accesseable : " + result);
                            }
                        } else {
                            return new BootloaderDevice("not accesseable : " + result);
                        }
                    } else {
                        LibUsb.close(handle);
                        return new BootloaderDevice("driver OK, CPU ID indeterminate");
                    }
                }
            } else if (descriptor.idVendor() == VID_AXOLOTI && descriptor.idProduct() == PID_AXOLOTI) {
                DeviceHandle handle = new DeviceHandle();
                result = LibUsb.open(device, handle);
                if (result < 0) {
                    return new AxolotiDevice("", deviceToPath(device), convertErrorToString(result));
                } else {
                    String serial = LibUsb.getStringDescriptor(handle, descriptor.iSerialNumber());
                    String name = Preferences.getPreferences().getBoardName(serial);
                    if (name == null) {
                        name = "";
                    }
                    LibUsb.close(handle);
                    return new AxolotiDevice(name, deviceToPath(device), serial);
                }
            } else if (descriptor.idVendor() == VID_AXOLOTI && descriptor.idProduct() == PID_AXOLOTI_SDCARD) {
                return new AxolotiDeviceSDCard(null, "", deviceToPath(device), "unmount disk to connect");
            }
        } else {
            throw new LibUsbException("Unable to read device descriptor", result);
        }
        return null;
    }

    public List<IDevice> getConnectables() {
        List<IDevice> connectableList = new LinkedList<>();

        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }
        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                IDevice dev = deviceFactory(device);
                if (dev != null) {
                    connectableList.add(dev);
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        return Collections.unmodifiableList(connectableList);
    }

    /**
     * This is the event handling thread. libusb doesn't start threads by its
     * own so it is our own responsibility to give libusb time to handle the
     * events in our own thread.
     */
    private static class EventHandlingThread extends Thread {

        /**
         * If thread should abort.
         */
        private volatile boolean abort;

        /**
         * Aborts the event handling thread.
         */
        public void abort() {
            this.abort = true;
        }

        @Override
        public void run() {
            while (!this.abort) {
                // Let libusb handle pending events. This blocks until events
                // have been handled, a hotplug callback has been deregistered
                // or the specified time of 1 second (Specified in
                // Microseconds) has passed.
                int result = LibUsb.handleEventsTimeout(null, 1000000);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to handle events", result);
                }
            }
        }
    }

    private static List<Runnable> callbacks = new LinkedList<>();

    public void registerHotplugCallback(Runnable r) {
        callbacks.add(r);
    }

    public void unregisterHotplugCallback(Runnable r) {
        System.out.println("unregister hotplug");
        callbacks.remove(r);
    }

    private static Runnable cb = () -> {
        for (Runnable r : callbacks) {
            r.run();
        }
    };

    /**
     * The hotplug callback handler
     */
    static class Callback implements HotplugCallback {

        @Override
        public int processEvent(Context context, Device device, int event,
                Object userData) {
            DeviceDescriptor descriptor = new DeviceDescriptor();

            int result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to read device descriptor",
                        result);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(USBDeviceLister.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.format("%s: %04x:%04x%n",
            //        event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED ? "Connected"
            //                : "Disconnected",
            //        descriptor.idVendor(), descriptor.idProduct());
            Runnable r = cb;
            if (r != null) {
                SwingUtilities.invokeLater(r);
            }
            return 0;
        }
    }

    private IDevice defaultDevice;

    public IDevice getDefaultDevice() {
        List<IDevice> devlist = getConnectables();
        if (defaultDevice == null) {
            for (IDevice dev : devlist) {
                if (dev.canConnect()) {
                    defaultDevice = dev;
                    return dev;
                }
            }
            return null;
        }
        for (IDevice dev : devlist) {
            if (dev.canConnect()) {
                if (dev.getCPUID() == null) {
                    return null; // TODO: USBDeviceLister: check NP cause?
                }
                if (dev.getCPUID().equals(defaultDevice.getCPUID())) {
                    return dev;
                }
            }
        }
        return null;
    }

    public void setDefaultDevice(IDevice defaultDevice) {
        this.defaultDevice = defaultDevice;
    }

}
