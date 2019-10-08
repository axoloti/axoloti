package axoloti.connection;

/**
 *
 * @author jtaelman
 */
public interface IDevice {

    String getName();

    String getType();

    String getLocation();

    String getInfo();

    String getCPUID();

    boolean canConnect();

    // TODO: implement, remove CConnection singleton
    // IConnection connect();
}
