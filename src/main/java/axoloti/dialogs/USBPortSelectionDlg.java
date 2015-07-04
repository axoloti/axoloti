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
package axoloti.dialogs;

import axoloti.usb.Usb;
import static axoloti.usb.Usb.DeviceToPath;
import static axoloti.usb.Usb.PID_AXOLOTI;
import static axoloti.usb.Usb.PID_STM_CDC;
import static axoloti.usb.Usb.PID_STM_DFU;
import static axoloti.usb.Usb.PID_STM_STLINK;
import static axoloti.usb.Usb.VID_AXOLOTI;
import static axoloti.usb.Usb.VID_STM;
import axoloti.utils.OSDetect;
import static axoloti.utils.OSDetect.getOS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author Johannes Taelman
 */
public class USBPortSelectionDlg extends javax.swing.JDialog {

    private String port;
    private final String defPortName;

    /**
     * Creates new form SerialPortSelectionDlg
     *
     * @param parent parent frame
     * @param modal is modal
     * @param defPortName default port name
     */
    public USBPortSelectionDlg(java.awt.Frame parent, boolean modal, String defPortName) {
        super(parent, modal);
        initComponents();
        System.out.println("default port: " + defPortName);
        this.defPortName = defPortName;
        port = defPortName;
        Populate();
        getRootPane().setDefaultButton(jButtonOK);
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                int r = jTable1.getSelectedRow();
                if (r >= 0) {

                } else {
                    port = null;
                }
            }
        });
    }

    final void Populate() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

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
                if (result == LibUsb.SUCCESS) {
                    if (descriptor.idVendor() == VID_STM) {
                        if (descriptor.idProduct() == PID_STM_DFU) {
                            DeviceHandle handle = new DeviceHandle();
                            result = LibUsb.open(device, handle);
                            if (result < 0) {
                                if (getOS() == OSDetect.OS.WIN) {
                                    if (result == LibUsb.ERROR_NOT_SUPPORTED) {
                                        model.addRow(new String[]{"STM DFU Bootloader", DeviceToPath(device), "not accesseable : wrong driver installed"});
                                    } else if (result == LibUsb.ERROR_ACCESS) {
                                        model.addRow(new String[]{"STM DFU Bootloader", DeviceToPath(device), "not accesseable : busy?"});
                                    } else {
                                        model.addRow(new String[]{"STM DFU Bootloader", DeviceToPath(device), "not accesseable : " + result});
                                    }
                                } else {
                                    model.addRow(new String[]{"STM DFU Bootloader", DeviceToPath(device), "not accesseable : " + result});
                                }
                            } else {
                                String serial = LibUsb.getStringDescriptor(handle, descriptor.iSerialNumber());
                                model.addRow(new String[]{"STM DFU Bootloader", DeviceToPath(device), "driver OK, CPU ID undeterminate"});
                                LibUsb.close(handle);
                            }
                        } else if (descriptor.idProduct() == PID_STM_STLINK) {
                            /*
                             Logger.getLogger(Usb.class.getName()).log(Level.INFO, "* STM STLink");
                             hasOne = true;
                             */
                        } else {
                            /*
                             Logger.getLogger(Usb.class.getName()).log(Level.INFO, "* other STM device:\n" + descriptor.dump());
                             hasOne = true;
                             */
                        }
                    } else if (descriptor.idVendor() == VID_AXOLOTI && descriptor.idProduct() == PID_AXOLOTI) {
                        DeviceHandle handle = new DeviceHandle();
                        result = LibUsb.open(device, handle);
                        if (result < 0) {
                            if (getOS() == OSDetect.OS.WIN) {
                                if (result == LibUsb.ERROR_NOT_FOUND) {
                                    model.addRow(new String[]{"Axoloti Core", DeviceToPath(device), "not accesseable : driver not installed"});
                                } else if (result == LibUsb.ERROR_ACCESS) {
                                    model.addRow(new String[]{"Axoloti Core", DeviceToPath(device), "not accesseable : busy?"});
                                } else {
                                    model.addRow(new String[]{"Axoloti Core", DeviceToPath(device), "not accesseable : " + result});
                                }
                            } else {
                                model.addRow(new String[]{"Axoloti Core", DeviceToPath(device), "not accesseable : " + result});
                            }
                        } else {
                            String serial = LibUsb.getStringDescriptor(handle, descriptor.iSerialNumber());
                            model.addRow(new String[]{"Axoloti Core", DeviceToPath(device), serial});
                            LibUsb.close(handle);
                        }
                    }
                } else {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }
    }

    public String getPort() {
        return port;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOK = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonCancel = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("Serial port selection"); // NOI18N

        jButtonOK.setText("OK");
        jButtonOK.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jLabel1.setText("Select device:");

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButton1.setText("refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Device", "Location", "CPU ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(146, 146, 146)
                        .addComponent(jButton1)
                        .addGap(0, 88, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        port = (String) model.getValueAt(0, 1);
        setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        //port = null;
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Populate();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
