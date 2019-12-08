package axoloti.connection;

import axoloti.Version;
import axoloti.mvc.View;
import axoloti.target.TargetModel;
import axoloti.target.TargetRTInfo;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.usb4java.DeviceHandle;

/**
 *
 * @author jtaelman
 */
public class ConnectionCB extends View<TargetModel> implements IConnectionCB {

    private IConnection connection;

    public ConnectionCB(TargetModel targetModel) {
        super(targetModel);
    }

    @Override
    public void setConnection(IConnection connection) {
        this.connection = connection;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void showDisconnect() {
        SwingUtilities.invokeLater(() -> {
            getDModel().showDisconnect();
        });
    }

    @Override
    public void showConnect() {
        SwingUtilities.invokeLater(() -> {
            getDModel().showConnect(connection);
        });
    }

    @Override
    public void showSDCardMounted() {
        SwingUtilities.invokeLater(() -> {
            getDModel().setSDCardMounted(true);
        });
    }

    @Override
    public void showSDCardUnmounted() {
        SwingUtilities.invokeLater(() -> {
            getDModel().setSDCardMounted(false);
        });
    }

    @Override
    public void setRTInfo(TargetRTInfo rtinfo) {
        SwingUtilities.invokeLater(() -> {
            getDModel().setRTInfo(rtinfo);
        });
    }

    @Override
    public void patchListChanged(List<ILivePatch> patchRefs) {
        SwingUtilities.invokeLater(() -> {
            getDModel().setPatchList(patchRefs);
        });
    }

    String remaining_string = "";

    @Override
    public void showLogText(String s) {
        int line_end;
        while ((line_end = s.indexOf('\n')) >= 0) {
            String line = s.substring(0, line_end);
            line = remaining_string + line;
            remaining_string = "";
            Logger.getLogger(ConnectionCB.class.getName()).log(Level.WARNING, "{0}", line);
            s = s.substring(line_end + 1);
        }
        remaining_string = remaining_string + s;
    }

    @Override
    public void fwupgrade_from_1012(DeviceHandle handle) {
        int r = JOptionPane.showConfirmDialog((Component) null, "Firmware version 1.0.12 detected, upgrade to firmware version " + Version.AXOLOTI_VERSION + " ?",
                "alert", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            FirmwareUpgrade_1_0_12 fwUpgrade = new FirmwareUpgrade_1_0_12(handle);
        }
    }

}
