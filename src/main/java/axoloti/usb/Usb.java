/**
 * Copyright (C) 2015 Johannes Taelman
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
package axoloti.usb;

import axoloti.utils.OSDetect;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.usb4java.*;

/**
 *
 * @author Johannes Taelman
 */
public class Usb {

    static final short VID_STM = 0x0483;
    static final short PID_STM_DFU = (short) 0xDF11;
    static final short PID_STM_CDC = (short) 0x5740;

    public Usb() {
    }

    static Context context;

    public static void initialize() {
        if (context == null) {
            context = new Context();
            int result = LibUsb.init(context);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to initialize libusb.", result);
            }
        }
    }

    public static void listDevices() {
        initialize();
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }
        try {
            Logger.getLogger(Usb.class.getName()).log(Level.INFO, "Relevant USB Devices currently attached:");
            boolean hasOne = false;
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result == LibUsb.SUCCESS) {
                    if (descriptor.idVendor() == VID_STM) {
                        if (descriptor.idProduct() == PID_STM_CDC) {
                            hasOne = true;
                            Logger.getLogger(Usb.class.getName()).log(Level.INFO, "* USB Serial port device");
                        } else if (descriptor.idProduct() == PID_STM_DFU) {
                            hasOne = true;
                            Logger.getLogger(Usb.class.getName()).log(Level.INFO, "* DFU device");
                            // try to open it to check if correct driver is installed
                            DeviceHandle handle = new DeviceHandle();
                            result = LibUsb.open(device, handle);
                            if (result < 0) {
                                Logger.getLogger(Usb.class.getName()).log(Level.INFO, "  but can't get access : "
                                        + LibUsb.strError(result));
                            } else {
                                Logger.getLogger(Usb.class.getName()).log(Level.INFO, "  driver ok");
                                LibUsb.close(handle);
                            }
                        }
                    }
                } else {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
            }
            if (!hasOne) {
                Logger.getLogger(Usb.class.getName()).log(Level.INFO, "none found...");
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }
    }

    public static boolean isDFUDeviceAvailable() {
        initialize();
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) {
            Logger.getLogger(Usb.class.getName()).log(Level.SEVERE, "Unable to get device list");
            return false;
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if (descriptor.idVendor() == VID_STM && descriptor.idProduct() == PID_STM_DFU) {
                    DeviceHandle handle = new DeviceHandle();
                    result = LibUsb.open(device, handle);
                    if (result < 0) {
                        Logger.getLogger(Usb.class.getName()).log(Level.SEVERE, "DFU device found, but can't get access : "
                                + LibUsb.strError(result));
                        switch (axoloti.utils.OSDetect.getOS()) {
                            case WIN:
                                Logger.getLogger(Usb.class.getName()).log(Level.SEVERE, "Please install the \"STM32 Bootloader WinUSB\" driver.");
                                break;
                            case LINUX:
                                Logger.getLogger(Usb.class.getName()).log(Level.SEVERE, "Probably need to add a udev rule.");
                                break;
                            default:
                        }
                        return false;
                    } else {
                        LibUsb.close(handle);
                        return true;
                    }
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }
        return false;
    }

    public static boolean isSerialDeviceAvailable() {
        Device d = findDevice(VID_STM, PID_STM_CDC);
        return d != null;
    }

    public static Device findDevice(short vendorId, short productId) {
        initialize();
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) {
                    return device;
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

}
