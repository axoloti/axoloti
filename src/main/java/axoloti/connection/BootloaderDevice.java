package axoloti.connection;

/**
 *
 * @author jtaelman
 */
public class BootloaderDevice implements IDevice {

    private final static String sDFUBootloader = "STM DFU Bootloader";

    final String info;

    public BootloaderDevice(String info) {
        this.info = info;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getType() {
        return sDFUBootloader;
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
