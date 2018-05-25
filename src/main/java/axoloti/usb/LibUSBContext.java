package axoloti.usb;

import org.usb4java.Context;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author jtaelman
 */
public class LibUSBContext {

    private LibUSBContext() {
    }

    private static Context context;

    public static Context getContext() {
        context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }
        return context;
    }
}
