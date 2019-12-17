package axoloti.swingui.target;

import axoloti.Axoloti;
import axoloti.connection.CConnection;
import axoloti.connection.ConnectionTest;
import axoloti.connection.IConnection;
import axoloti.connection.IPatchCB;
import axoloti.connection.PatchLoadFailedException;
import axoloti.job.GlobalJobProcessor;
import axoloti.job.IJobContext;
import axoloti.mvc.IView;
import axoloti.shell.UploadFirmwareDFU;
import axoloti.swingui.dialogs.USBPortSelectionDlg;
import axoloti.target.TargetModel;
import axoloti.usb.Usb;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

/**
 *
 * @author jtaelman
 */
public class TargetMenu extends JMenu implements IView<TargetModel> {

    final TargetModel targetModel;

    private JMenuItem jMenuItemSelectCom;
    private JMenuItem jMenuItemEnterDFU;
    private JMenuItem jMenuItemFConnect;
    private JMenuItem jMenuItemFDisconnect;
    private JMenuItem jMenuItemFlashDFU;
    private JMenuItem jMenuItemFlashDefault;
    private JMenuItem jMenuItemFlashDowngrade;
    private JMenuItem jMenuItemMount;
    private JMenuItem jMenuItemEraseStart;
    private JMenuItem jMenuItemPing;
    private JSeparator jDevSeparator;
    private JMenu jMenuFirmware;
    private JSeparator jSeparator1;
    private JMenuItem jMenuItemKeyboard;
    private JMenuItem jMenuItemMidiMonitor;
    private JMenuItem jMenuItemMidiRouting;
    private JMenuItem jMenuItemFileManager;
    private JMenuItem jMenuItemRemote;
    private JMenuItem jMenuItemMemoryViewer;
    private JMenuItem jMenuItemTestProtocol;
    private JMenuItem jMenuItemTestExtra;

    public TargetMenu(TargetModel targetModel) {
        super("Board");
        this.targetModel = targetModel;
        initComponents();
    }

    private void initComponents() {
        jMenuItemSelectCom = new JMenuItem("Select Device...");
        jMenuItemSelectCom.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectComActionPerformed(evt);
            }
        });
        add(jMenuItemSelectCom);

        jMenuItemFConnect = new JMenuItem("Connect");
        jMenuItemFConnect.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFConnectActionPerformed(evt);
            }
        });
        add(jMenuItemFConnect);

        jMenuItemFDisconnect = new JMenuItem("Disconnect");
        jMenuItemFDisconnect.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFDisconnectActionPerformed(evt);
            }
        });
        add(jMenuItemFDisconnect);

        jMenuItemPing = new JMenuItem("Ping");
        jMenuItemPing.setEnabled(false);
        jMenuItemPing.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPingActionPerformed(evt);
            }
        });
        add(jMenuItemPing);

        jMenuItemMount = new JMenuItem("Enter card reader mode (disconnects editor)");
        jMenuItemMount.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMountActionPerformed(evt);
            }
        });
        add(jMenuItemMount);

        jMenuItemEraseStart = new JMenuItem("Erase startup patch in flash");
        jMenuItemEraseStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEraseStartActionPerformed(evt);
            }
        });
        add(jMenuItemEraseStart);

        jMenuFirmware = new javax.swing.JMenu();
        jMenuFirmware.setText("Firmware");

        jMenuItemFlashDefault = new JMenuItem("Flash");
        jMenuItemFlashDefault.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDefaultActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDefault);

        jMenuItemFlashDowngrade = new JMenuItem("Flash downgrade to 1.0.12");
        jMenuItemFlashDowngrade.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDowngradeActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDowngrade);

        jMenuItemFlashDFU = new JMenuItem("Flash (Rescue)");
        jMenuItemFlashDFU.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlashDFUActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemFlashDFU);

        jDevSeparator = new JSeparator();
        jMenuFirmware.add(jDevSeparator);

        jMenuItemEnterDFU = new JMenuItem("Enter Rescue mode");
        jMenuItemEnterDFU.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEnterDFUActionPerformed(evt);
            }
        });
        jMenuFirmware.add(jMenuItemEnterDFU);

        jMenuItemEnterDFU.setVisible(true);
        jDevSeparator.setVisible(true);

        add(jMenuFirmware);

        jSeparator1 = new JSeparator();
        add(jSeparator1);
        jMenuItemKeyboard = new JMenuItem("Keyboard");
        jMenuItemKeyboard.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetViews.getTargetViews().showKeyboard();
            }
        });
        add(jMenuItemKeyboard);
        jMenuItemMidiMonitor = new JMenuItem("MIDI Input Monitor");
        jMenuItemMidiMonitor.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetViews.getTargetViews().showMidiMonitor();
            }
        });
        add(jMenuItemMidiMonitor);
        jMenuItemMidiRouting = new JMenuItem("MIDI Routing");
        jMenuItemMidiRouting.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetViews.getTargetViews().showMidiRouting();
            }
        });
        add(jMenuItemMidiRouting);
        jMenuItemFileManager = new JMenuItem("File manager");
        jMenuItemFileManager.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetViews.getTargetViews().showFilemanager();
            }
        });
        add(jMenuItemFileManager);
        jMenuItemRemote = new JMenuItem("Remote");
        jMenuItemRemote.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetViews.getTargetViews().showRemote();
            }
        });
        add(jMenuItemRemote);
        jMenuItemMemoryViewer = new JMenuItem("Memory Viewer");
        jMenuItemMemoryViewer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetViews.getTargetViews().showMemoryViewer();
            }
        });
        add(jMenuItemMemoryViewer);

        jMenuItemTestProtocol = new JMenuItem("Run protocol tests...");
        jMenuItemTestProtocol.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    ConnectionTest.doAllTests(CConnection.getConnection());
                } catch (IOException ex) {
                    Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(jMenuItemTestProtocol);

        jMenuItemTestExtra = new JMenuItem("send \"extra\" command...");
        jMenuItemTestExtra.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String arg = JOptionPane.showInputDialog(null, "Extra argument?");
                if (arg == null) {
                    return;
                }
                int iarg = Integer.parseInt(arg);
                IConnection conn = CConnection.getConnection();
                try {
                    conn.transmitExtraCommand(iarg);
                } catch (IOException ex) {
                    Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        add(jMenuItemTestExtra);
    }

    private void jMenuItemFDisconnectActionPerformed(java.awt.event.ActionEvent evt) {
        getDModel().getConnection().disconnect();
    }

    private void jMenuItemFConnectActionPerformed(java.awt.event.ActionEvent evt) {
        getDModel().getConnection().connect(null);
    }

    private void jMenuItemSelectComActionPerformed(java.awt.event.ActionEvent evt) {
        USBPortSelectionDlg spsDlg = new USBPortSelectionDlg(null, true);
        spsDlg.setVisible(true);
    }

    private void jMenuItemPingActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            IConnection conn = CConnection.getConnection();
            conn.transmitPing();
        } catch (IOException ex) {
            Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jMenuItemFlashDFUActionPerformed(java.awt.event.ActionEvent evt) {
        if (Usb.isDFUDeviceAvailable()) {
            getDModel().updateLinkFirmwareID();
            UploadFirmwareDFU.doit();
        } else {
            Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, "No devices in DFU mode detected. To bring Axoloti Core in DFU mode, remove power from Axoloti Core, and then connect the micro-USB port to your computer while holding button S1. The LEDs will stay off when in DFU mode.");
        }
    }

    private void jMenuItemEnterDFUActionPerformed(java.awt.event.ActionEvent evt) {
        final String msg = "Done enabling DFU. The regular USB communication will now abort, " + "and Axoloti Core will restart itself in \"rescue\" (DFU) mode."
                + "\"Flash (rescue)\" will restart Axoloti Core again in normal mode when completed."
                + "To leave \"rescue\" mode, power-cycle your Axoloti Core.";
        try {
            IConnection conn = CConnection.getConnection();
            conn.bringToDFU();
        } catch (IOException ex) {
            Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jMenuItemFlashDefaultActionPerformed(java.awt.event.ActionEvent evt) {
        int s = JOptionPane.showConfirmDialog(this,
                "Do you want to flash the firmware?\n"
                + "This process will cause a disconnect, "
                + "the leds will blink for a minute, "
                + "do not interrupt until the leds "
                + "stop blinking.\n"
                + "When the leds stop blinking, you can connect again.\n",
                "Firmware update...",
                JOptionPane.YES_NO_OPTION);
        if (s == 0) {
            String pname = Axoloti.getFirmwareFilename();
            try {
                getDModel().flashUsingSDRam(pname);
            } catch (IOException ex) {
                Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void jMenuItemFlashDowngradeActionPerformed(java.awt.event.ActionEvent evt) {
        int s = JOptionPane.showConfirmDialog(this,
                "Do you want to downgrade the firmware to version 1.0.12?\n"
                + "This process will cause a disconnect, "
                + "the leds will blink for a minute, "
                + "do not interrupt until the leds "
                + "stop blinking.\n"
                + "When the leds stop blinking, you can connect again.\n",
                "Firmware update...",
                JOptionPane.YES_NO_OPTION);
        if (s == 0) {
            String pname = Axoloti.getReleaseDir() + "/old_firmware/firmware-1.0.12/axoloti.bin";
            try {
                getDModel().flashUsingSDRam(pname);
            } catch (IOException ex) {
                Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void uploadFile(IConnection conn, File f, String targetFilename, IJobContext ctx) throws FileNotFoundException, IOException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(f.lastModified());
        int size = (int) f.length();
        FileInputStream inputStream = new FileInputStream(f);
        conn.upload(targetFilename, inputStream, cal, size, ctx);
    }

    private void jMenuItemMountActionPerformed(java.awt.event.ActionEvent evt) {
        GlobalJobProcessor.getJobProcessor().exec(ctx -> {
            String elfname = "mounter.elf";
            String fname = Axoloti.getReleaseDir() + "/firmware/mounter/mounter_build/" + elfname;
            Path fileLocation = Paths.get(fname);
            byte[] data;
            try {
                data = Files.readAllBytes(fileLocation);
            } catch (IOException ex) {
                Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            IConnection conn = CConnection.getConnection();
            try {
                conn.transmitStartLive(data, "mounter", new IPatchCB() {
                    @Override
                    public void patchStopped() {
                    }

                    @Override
                    public void setDspLoad(int dspLoad) {
                    }

                    @Override
                    public void paramChange(int index, int value) {
                    }

                    @Override
                    public void distributeDataToDisplays(ByteBuffer dispData) {
                    }

                    @Override
                    public void openEditor() {
                    }
                }, ctx);
            } catch (IOException ex) {
                Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, "SDCard mounter active, editor connection lost. Unmount/eject the sdcard volume in Explorer or Finder to enable editor connection again.");
            } catch (PatchLoadFailedException ex) {
                Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        });
    }

    private void jMenuItemEraseStartActionPerformed(java.awt.event.ActionEvent evt) {
        byte bb[] = new byte[4];
        bb[0] = (byte) 0xFF;
        bb[1] = (byte) 0xFF;
        bb[2] = (byte) 0xFF;
        bb[3] = (byte) 0xFF;
        IConnection conn = CConnection.getConnection();
        try {
            conn.uploadPatchToFlash(bb, "flash patch x");
        } catch (IOException ex) {
            Logger.getLogger(TargetMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            jMenuItemTestProtocol.setEnabled(connect);
            jMenuItemFlashDefault.setEnabled(
                    (connection != null)
                    && connection.getTargetProfile().hasSDRAM());
            jMenuItemFlashDowngrade.setEnabled(
                    (connection != null)
                    && connection.getTargetProfile().hasSDRAM());
        } else if (TargetModel.HAS_SDCARD.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            jMenuItemMount.setEnabled(b);
        }

    }

    @Override
    public TargetModel getDModel() {
        return targetModel;
    }

    @Override
    public void dispose() {
        targetModel.getController().removeView(this);
    }

}
