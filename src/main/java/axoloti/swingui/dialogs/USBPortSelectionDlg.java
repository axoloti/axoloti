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
package axoloti.swingui.dialogs;

import axoloti.connection.IDevice;
import axoloti.connection.USBDeviceLister;
import axoloti.preferences.Preferences;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Johannes Taelman
 */
public class USBPortSelectionDlg extends javax.swing.JDialog {

    /**
     * Creates new form USBPortSelectionDlg
     *
     * @param parent parent frame
     * @param modal is modal
     */
    public USBPortSelectionDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        populate();
        initComponents2();
    }

    private final Runnable hotplugCallback = () -> {
        populate();
    };

    private void initComponents2() {
        getRootPane().setDefaultButton(jButtonOK);

        jTable1.setModel(new AbstractTableModel() {
            private final String[] columnNames = {"Name", "Device", "Location", "Info"};

            @Override
            public int getRowCount() {
                return devices.size();
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Object getValueAt(int row, int column) {
                IDevice device = devices.get(row);
                switch (column) {
                    case 0: // Name
                        return Preferences.getPreferences().getBoardName(device.getCPUID());
                    case 1: // Device
                        return device.getType();
                    case 2: // Location
                        return device.getLocation();
                    case 3: // Info
                        return device.getInfo();
                    default:
                        return "?";
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    IDevice device = devices.get(rowIndex);
                    Preferences.getPreferences().setBoardName(device.getCPUID(), (String) aValue);
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return (columnIndex == 0);
            }

            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }
        });

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int r = jTable1.getSelectedRow();
                if (r >= 0) {
                    IDevice device = devices.get(r);
                    jButtonOK.setEnabled(device.canConnect());
                } else {
                    jButtonOK.setEnabled(false);
                }
            }
        });

        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column != 0) {
                    return;
                }

                TableModel model = (TableModel) e.getSource();
                String name = (String) model.getValueAt(row, column);
                String cpuid = (String) jTable1.getModel().getValueAt(row, 3);
                Preferences prefs = Preferences.getPreferences();
                prefs.setBoardName(cpuid, name);
                prefs.savePrefs();
            }
        });
        USBDeviceLister.getInstance().registerHotplugCallback(hotplugCallback);
    }

    List<IDevice> devices;

    final void populate() {
        devices = USBDeviceLister.getInstance().getConnectables();
        ((AbstractTableModel) jTable1.getModel()).fireTableDataChanged();
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
        jButtonRefresh = new javax.swing.JButton();
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

        jButtonRefresh.setText("refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonRefresh)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButtonRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        onSelect();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        populate();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            onSelect();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void onSelect() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }
        if (jTable1.getSelectedRowCount() != 1) {
            return;
        }
        IDevice dev = devices.get(row);
        if (!dev.canConnect()) {
            return;
        }
        USBDeviceLister.getInstance().setDefaultDevice(dev);
        setVisible(false);
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        USBDeviceLister.getInstance().unregisterHotplugCallback(hotplugCallback);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
