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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.usb4java.*;

/**
 *
 * @author Johannes Taelman
 */
public class Usb {

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
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                Logger.getLogger(Usb.class.getName()).log(Level.INFO, descriptor.dump());
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }
    }

    public static boolean isDFUDeviceAvailable() {
        Device d = findDevice((short) 0x0483, (short) 0xDF11);
        if (d == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSerialDeviceAvailable() {
        Device d = findDevice((short) 0x0483, (short) 0x5740);
        if (d == null) {
            return false;
        } else {
            return true;
        }
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
