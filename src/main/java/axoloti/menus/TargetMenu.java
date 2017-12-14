package axoloti.menus;

import axoloti.Axoloti;
import axoloti.IConnection;
import axoloti.MainFrame;
import axoloti.TargetController;
import axoloti.TargetModel;
import axoloti.dialogs.AJFrame;
import axoloti.dialogs.AxolotiRemoteControl;
import axoloti.dialogs.FileManagerFrame;
import axoloti.dialogs.KeyboardFrame;
import axoloti.dialogs.MidiRouting;
import axoloti.mvc.IView;
import axoloti.usb.Usb;
import axoloti.utils.Preferences;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import qcmds.QCmdBringToDFUMode;
import qcmds.QCmdPing;
import qcmds.QCmdProcessor;
import qcmds.QCmdStartMounter;
import qcmds.QCmdStop;
import qcmds.QCmdUploadPatch;

/**
 *
 * @author jtaelman
 */
public class TargetMenu extends JMenu implements IView<TargetController> {

    final TargetController controller;

    KeyboardFrame keyboard;
    FileManagerFrame filemanager;
    AxolotiRemoteControl remote;
    MidiRouting midirouting;

    private JMenuItem jMenuItemSelectCom;
    private JMenuItem jMenuItemEnterDFU;
    private JMenuItem jMenuItemFCompile;
    private JMenuItem jMenuItemFConnect;
    private JMenuItem jMenuItemFDisconnect;
    private JMenuItem jMenuItemFlashDFU;
    private JMenuItem jMenuItemFlashDefault;
    private JMenuItem jMenuItemFlashSDR;
    private JMenuItem jMenuItemMemViewer;
    private JMenuItem jMenuItem1;
    private JMenuItem jMenuItemMount;
    private JMenuItem jMenuItemPanic;
    private JMenuItem jMenuItemPing;
    private JMenuItem jMenuItemRefreshFWID;
    private JSeparator jDevSeparator;
    private JMenu jMenuFirmware;

    public TargetMenu(TargetController controller) {
        super("Board");
        this.controller = controller;

        jMenuItemSelectCom = new JMenuItem("Select Device...");
        jMenuItemSelectCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectComActionPerformed(evt);
            }
        });
        add(jMenuItemSelectCom);

        jMenuItemFConnect = new JMenuItem("Connect");
        jMenuItemFConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFConnectActionPerformed(evt);
            }
        });
        add(jMenuItemFConnect);

        jMenuItemFDisconnect = new JMenuItem("Disconnect");
        jMenuItemFDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFDisconnectActionPerformed(evt);
            }
        });
        add(jMenuItemFDisconnect);

        jMenuItemPing = new JMenuItem("Ping");
        jMenuItemPing.setEnabled(false);
        jMenuItemPing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPingActionPerformed(evt);
            }
        });
        add(jMenuItemPing);

        jMenuItemPanic = new JMenuItem("Panic");
        jMenuItemPanic.setEnabled(false);
        jMenuItemPanic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPanicActionPerformed(evt);
            }
        });
        add(jMenuItemPanic);

        jMenuItemMemViewer = new JMenuItem("New memory viewer");
        jMenuItemMemViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMemViewerActionPerformed(evt);
            }
        });
        add(jMenuItemMemViewer);

        jMenuItem1 = new JMenuItem("MIDI input monitor");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        add(jMenuItem1);

        jMenuItemMount = new JMenuItem("Enter card reader mode (disconnects editor)");
        jMenuItemMount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMountActionPerformed(evt);
            }
        });
        add(jMenuItemMount);

        jMenuFirmware = new javax.swing.JMenu();
        jMenuFirmware.setText("Firmware");

        jMenuItemFlashDefault = new JMenuItem("Flash");
        jMenuItemFlashDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDefaultActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDefault);

        jMenuItemFlashDFU = new JMenuItem("Flash (Rescue)");
        jMenuItemFlashDFU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDFUActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDFU);

        jMenuItemRefreshFWID = new JMenuItem("Refresh Firmware ID");
        jMenuItemRefreshFWID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRefreshFWIDActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemRefreshFWID);

        jDevSeparator = new JSeparator();
        jMenuFirmware.add(jDevSeparator);

        jMenuItemFCompile = new JMenuItem("Compile");
        jMenuItemFCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFCompileActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFCompile);

        jMenuItemEnterDFU = new JMenuItem("Enter Rescue mode");
        jMenuItemEnterDFU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEnterDFUActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemEnterDFU);

        jMenuItemFlashSDR = new JMenuItem("Flash (User)");
        jMenuItemFlashSDR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashSDRActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashSDR);

        if (!Preferences.getPreferences().getExpertMode()) {
            jMenuItemRefreshFWID.setVisible(false);
        }

        jMenuItemEnterDFU.setVisible(Axoloti.isDeveloper());
        jMenuItemFlashSDR.setVisible(Axoloti.isDeveloper());
        jMenuItemFCompile.setVisible(Axoloti.isDeveloper());
        jDevSeparator.setVisible(Axoloti.isDeveloper());

        add(jMenuFirmware);
    }

    private void jMenuItemFDisconnectActionPerformed(java.awt.event.ActionEvent evt) {
        getController().getModel().getConnection().disconnect();
    }

    private void jMenuItemFConnectActionPerformed(java.awt.event.ActionEvent evt) {
        getController().getModel().getConnection().connect(null);
    }

    private void jMenuItemSelectComActionPerformed(java.awt.event.ActionEvent evt) {
        getController().getModel().getConnection().SelectPort();
    }

    private void jMenuItemPanicActionPerformed(java.awt.event.ActionEvent evt) {
        QCmdProcessor.getQCmdProcessor().Panic();
    }

    private void jMenuItemPingActionPerformed(java.awt.event.ActionEvent evt) {
        QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdPing());
    }

    private void jMenuItemMemViewerActionPerformed(java.awt.event.ActionEvent evt) {
        AJFrame F = new axoloti.dialogs.Memory(getController());
        getController().addView(F);
        F.setVisible(true);
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        AJFrame F = new axoloti.dialogs.MidiMonitor(getController());
        getController().addView(F);
        F.setVisible(true);
    }

    private void jMenuItemRefreshFWIDActionPerformed(java.awt.event.ActionEvent evt) {
        getController().getModel().updateLinkFirmwareID();
    }

    private void jMenuItemFlashDFUActionPerformed(java.awt.event.ActionEvent evt) {
        if (Usb.isDFUDeviceAvailable()) {
            getController().getModel().updateLinkFirmwareID();
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new qcmds.QCmdStop());
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new qcmds.QCmdDisconnect());
            QCmdProcessor.getQCmdProcessor().AppendToQueue(new qcmds.QCmdFlashDFU());
        } else {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "No devices in DFU mode detected. To bring Axoloti Core in DFU mode, remove power from Axoloti Core, and then connect the micro-USB port to your computer while holding button S1. The LEDs will stay off when in DFU mode.");
        }
    }

    private void jMenuItemFlashSDRActionPerformed(java.awt.event.ActionEvent evt) {
        String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/flasher/flasher_build/flasher";
        String pname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build/axoloti.bin";
        getController().getModel().flashUsingSDRam(fname, pname);
    }

    private void jMenuItemFCompileActionPerformed(java.awt.event.ActionEvent evt) {
        QCmdProcessor.getQCmdProcessor().AppendToQueue(new qcmds.QCmdCompileFirmware());
    }

    private void jMenuItemEnterDFUActionPerformed(java.awt.event.ActionEvent evt) {
        QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdBringToDFUMode());
    }

    private void jMenuItemFlashDefaultActionPerformed(java.awt.event.ActionEvent evt) {
        String curFirmwareDir = System.getProperty(Axoloti.FIRMWARE_DIR);
        String sysFirmwareDir = System.getProperty(Axoloti.RELEASE_DIR) + "/firmware";

        if (!curFirmwareDir.equals(sysFirmwareDir)) {
            // if we are using the factory firmware, then we must switch back the firmware dir
            // as this is where we pick up axoloti.elf from when building a patch
            Preferences.getPreferences().SetFirmwareDir(sysFirmwareDir);
            Preferences.getPreferences().SavePrefs();
        }

        String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/flasher/flasher_build/flasher";
        String pname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build/axoloti.bin";
        getController().getModel().flashUsingSDRam(fname, pname);
    }

    private void jMenuItemMountActionPerformed(java.awt.event.ActionEvent evt) {
        String fname = System.getProperty(Axoloti.FIRMWARE_DIR) + "/mounter/mounter_build/mounter";
        QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdStop());
        QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdUploadPatch(fname));
        QCmdProcessor.getQCmdProcessor().AppendToQueue(new QCmdStartMounter());
        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "will disconnect, unmount sdcard to go back to normal mode (required to connect)");
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (TargetModel.CONNECTION.is(evt)) {
            IConnection connection = (IConnection) evt.getNewValue();
            boolean connect = connection != null;
            jMenuItemFDisconnect.setEnabled(connect);
            jMenuItemFConnect.setEnabled(!connect);
            jMenuItemSelectCom.setEnabled(!connect);
            jMenuItemEnterDFU.setEnabled(connect);
            jMenuItemMount.setEnabled(connect);
            jMenuItemFlashDefault.setEnabled(connect && connection.getTargetProfile().hasSDRAM());
            jMenuItemFlashSDR.setEnabled(connect && connection.getTargetProfile().hasSDRAM());
        } else if (TargetModel.HAS_SDCARD.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            jMenuItemMount.setEnabled(b);
        }

    }

    @Override
    public TargetController getController() {
        return controller;
    }

    @Override
    public void dispose() {
        getController().removeView(this);
    }

}
