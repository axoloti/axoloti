package axoloti.connection;

import axoloti.mvc.View;
import axoloti.target.TargetModel;
import axoloti.target.TargetRTInfo;
import axoloti.target.fs.SDCardMountStatusListener;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
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

    private final ArrayList<ConnectionStatusListener> csls = new ArrayList<>();

    public void addConnectionStatusListener(ConnectionStatusListener csl) {
        if (connection.isConnected()) {
            csl.showConnect();
        } else {
            csl.showDisconnect();
        }
        csls.add(csl);
    }

    public void removeConnectionStatusListener(ConnectionStatusListener csl) {
        csls.remove(csl);
    }

    @Override
    public void showDisconnect() {
        SwingUtilities.invokeLater(() -> {
            for (ConnectionStatusListener csl : csls) {
                csl.showDisconnect();
            }
            getDModel().setConnection(null);
            getDModel().setWarnedAboutFWCRCMismatch(false);
        });
    }

    @Override
    public void showConnect() {
        SwingUtilities.invokeLater(() -> {
            for (ConnectionStatusListener csl : csls) {
                csl.showConnect();
            }
            getDModel().setConnection(connection);
        });
    }

    private final ArrayList<SDCardMountStatusListener> sdcmls = new ArrayList<>();

    public void addSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        if (connection.getSDCardPresent()) {
            sdcml.showSDCardMounted();
        } else {
            sdcml.showSDCardUnmounted();
        }
        sdcmls.add(sdcml);
    }

    public void removeSDCardMountStatusListener(SDCardMountStatusListener sdcml) {
        sdcmls.remove(sdcml);
    }

    @Override
    public void showSDCardMounted() {
        SwingUtilities.invokeLater(() -> {
            for (SDCardMountStatusListener sdcml : sdcmls) {
                sdcml.showSDCardMounted();
            }
            getDModel().setSDCardMounted(true);
        });
    }

    @Override
    public void showSDCardUnmounted() {
        SwingUtilities.invokeLater(() -> {
            for (SDCardMountStatusListener sdcml : sdcmls) {
                sdcml.showSDCardUnmounted();
            }
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
        int r = JOptionPane.showConfirmDialog((Component) null, "Firmware version 1.0.12 detected, upgrade to experimental firmware?",
                "alert", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            FirmwareUpgrade_1_0_12 fwUpgrade = new FirmwareUpgrade_1_0_12(handle);
        }
    }

}
