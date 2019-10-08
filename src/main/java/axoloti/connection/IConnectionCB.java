package axoloti.connection;

import axoloti.target.TargetRTInfo;
import java.util.List;
import org.usb4java.DeviceHandle;

/**
 *
 * @author jtaelman
 */
public interface IConnectionCB {
    void setConnection(IConnection connection);
    void showConnect();
    void showDisconnect();
    void showSDCardMounted();
    void showSDCardUnmounted();
    void setRTInfo(TargetRTInfo rtinfo);
    void patchListChanged(List<ILivePatch> patchRefs);
    void showLogText(String s);
    void fwupgrade_from_1012(DeviceHandle handle);
}
