package axoloti.connection;

import org.usb4java.DeviceHandle;

/**
 *
 * @author jtaelman
 */
public class AxolotiDeviceSDCard implements IDevice {

    final DeviceHandle handle;
    final String name;
    final String location;
    final String err;

    public AxolotiDeviceSDCard(DeviceHandle handle, String name, String location, String err) {
        this.handle = handle;
        this.name = name;
        this.location = location;
        this.err = err;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return err;
    }

    @Override
    public String getType() {
        return "Axoloti Core (in card reader mode)";
    }

    @Override
    public String getLocation() {
        return "";
    }

    @Override
    public boolean canConnect() {
        return false;
    }

    @Override
    public String getCPUID() {
        return null;
    }

}
