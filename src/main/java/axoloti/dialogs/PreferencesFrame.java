/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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

import axoloti.MainFrame;
import axoloti.utils.AxoFileLibrary;
import axoloti.utils.AxoGitLibrary;
import axoloti.utils.AxolotiLibrary;
import axoloti.utils.Preferences;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Johannes Taelman
 */
public class PreferencesFrame extends javax.swing.JFrame {

    static PreferencesFrame singleton = null;

    public static PreferencesFrame GetPreferencesFrame() {
        if (singleton == null) {
            singleton = new PreferencesFrame();
        }
        return singleton;
    }

    /**
     * Creates new form PreferencesFrame
     *
     */
    private PreferencesFrame() {
        initComponents();
        Preferences prefs = Preferences.LoadPreferences();
        jTextFieldPollInterval.setText(Integer.toString(prefs.getPollInterval()));

        txtFavDir.setText(prefs.getFavouriteDir());
        txtFirmwareDir.setText(System.getProperty(axoloti.Axoloti.FIRMWARE_DIR));
        txtRuntimeDir.setText(System.getProperty(axoloti.Axoloti.RUNTIME_DIR));
        jControllerEnabled.setSelected(prefs.isControllerEnabled());
        jTextFieldController.setText(prefs.getControllerObject());
        jTextFieldController.setEnabled(prefs.isControllerEnabled());
        PopulateLibrary();

        //double click to edit library
        jLibraryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int idx = table.rowAtPoint(p);
                if (me.getClickCount() == 2) {
                    if (idx >= 0) {
                        editLibraryRow(idx);
                    }
                }
            }
        });

//        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
//                int r = jTable1.getSelectedRow();
//                if (r >= 0) {
//                    String devName = (String) model.getValueAt(r, 1);
//                    if (devName.equals(sAxolotiCore)) {
//                        jButtonOK.setEnabled(true);
//                        cpuid = (String) model.getValueAt(r, 3);
//                    } else {
//                        jButtonOK.setEnabled(false);
//                    }
//                } else {
//                    cpuid = null;
//                }
//            }
//        });
//        
//        jTable1.getModel().addTableModelListener(new TableModelListener() {
//
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                int row = e.getFirstRow();
//                int column = e.getColumn();
//                if(column!=0) return;
//                
//                TableModel model = (TableModel)e.getSource();
//                String name = (String) model.getValueAt(row, column);
//                String cpuid = (String) ((DefaultTableModel) jTable1.getModel()).getValueAt(row, 3);
//                Preferences prefs = MainFrame.mainframe.prefs;
//                prefs.setBoardName(cpuid,name);
//                prefs.SavePrefs();
//            }
//        });
    }

    void Apply() {
        Preferences prefs = Preferences.LoadPreferences();
        prefs.setPollInterval(Integer.parseInt(jTextFieldPollInterval.getText()));
        prefs.setMouseDialAngular(jComboBox1.getSelectedItem().equals("Angular"));
        prefs.setFavouriteDir(txtFavDir.getText());
        prefs.setControllerObject(jTextFieldController.getText().trim());
        prefs.setControllerEnabled(jControllerEnabled.isSelected());
    }

    final void PopulateLibrary() {
        DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
        model.setRowCount(0);
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        for (AxolotiLibrary lib : Preferences.LoadPreferences().getLibraries()) {
            model.addRow(new Object[]{lib.getType(), lib.getId(), lib.getLocalLocation(), lib.getEnabled()});
        }
        jLibraryTable.setCellSelectionEnabled(false);
        jLibraryTable.setRowSelectionAllowed(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldPollInterval = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFirmwareDir = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtRuntimeDir = new javax.swing.JLabel();
        btnFirmwareDir = new javax.swing.JButton();
        btnRuntimeDir = new javax.swing.JButton();
        txtFavDir = new javax.swing.JLabel();
        btnFavDir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldController = new javax.swing.JTextField();
        jControllerEnabled = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLibraryTable = new javax.swing.JTable();
        jAddLibBtn = new javax.swing.JButton();
        jDelLibBtn = new javax.swing.JButton();
        jResetLib = new javax.swing.JButton();
        jEditLib = new javax.swing.JButton();
        jLibStatus = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        themeEditButton = new javax.swing.JButton();
        jCheckBoxNoMouseReCenter = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jTextFieldPollInterval.setText("jTextField1");
        jTextFieldPollInterval.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jLabel1.setText("Libraries");

        jLabel2.setText("PollInterval (milliseconds)");

        jButtonSave.setText("OK");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jLabel3.setText("Dial mouse behavior");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Vertical", "Angular" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Favourite Dir");

        jLabel5.setText("Firmware Dir");

        txtFirmwareDir.setText("jLabel6");

        jLabel7.setText("Runtime Dir");

        txtRuntimeDir.setText("jLabel6");

        btnFirmwareDir.setText("Select");
        btnFirmwareDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirmwareDirActionPerformed(evt);
            }
        });

        btnRuntimeDir.setText("Select");
        btnRuntimeDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRuntimeDirActionPerformed(evt);
            }
        });

        txtFavDir.setText("jLabel6");

        btnFavDir.setText("Select");
        btnFavDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFavDirActionPerformed(evt);
            }
        });

        jLabel6.setText("Controller Object");

        jTextFieldController.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jControllerEnabled.setText("Enabled");
        jControllerEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jControllerEnabledActionPerformed(evt);
            }
        });

        jLibraryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Id", "Location", "Enabled"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jLibraryTable.setColumnSelectionAllowed(true);
        jLibraryTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jLibraryTable);
        jLibraryTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jLibraryTable.getColumnModel().getColumnCount() > 0) {
            jLibraryTable.getColumnModel().getColumn(0).setResizable(false);
            jLibraryTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            jLibraryTable.getColumnModel().getColumn(1).setResizable(false);
            jLibraryTable.getColumnModel().getColumn(1).setPreferredWidth(50);
            jLibraryTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            jLibraryTable.getColumnModel().getColumn(3).setResizable(false);
            jLibraryTable.getColumnModel().getColumn(3).setPreferredWidth(5);
        }

        jAddLibBtn.setText("+");
        jAddLibBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddLibBtnActionPerformed(evt);
            }
        });

        jDelLibBtn.setText("-");
        jDelLibBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDelLibBtnActionPerformed(evt);
            }
        });

        jResetLib.setText("Reset All");
        jResetLib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetLibActionPerformed(evt);
            }
        });

        jEditLib.setText("Edit");
        jEditLib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditLibActionPerformed(evt);
            }
        });

        jLibStatus.setText("Status");
        jLibStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLibStatusActionPerformed(evt);
            }
        });

        jLabel8.setText("Theme");
        jLabel8.setEnabled(false);

        themeEditButton.setText("Edit");
        themeEditButton.setEnabled(false);
        themeEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themeEditButtonActionPerformed(evt);
            }
        });

        jCheckBoxNoMouseReCenter.setText("Do not re-center cursor (for touchscreens)");
        jCheckBoxNoMouseReCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNoMouseReCenterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtFirmwareDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(288, 288, 288))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtRuntimeDir, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFavDir, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRuntimeDir, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFirmwareDir, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFavDir, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jResetLib, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLibStatus))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jAddLibBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDelLibBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jEditLib, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(14, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldController, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jControllerEnabled)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(191, 191, 191)
                                .addComponent(themeEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxNoMouseReCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldPollInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(130, 130, 130)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRuntimeDir)
                    .addComponent(txtRuntimeDir)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtFirmwareDir)
                    .addComponent(btnFirmwareDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFavDir)
                    .addComponent(jLabel4)
                    .addComponent(btnFavDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jAddLibBtn)
                            .addComponent(jDelLibBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEditLib)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jResetLib)
                            .addComponent(jLibStatus)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextFieldPollInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxNoMouseReCenter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextFieldController, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jControllerEnabled))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(themeEditButton)
                            .addComponent(jLabel8))
                        .addContainerGap(28, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSave)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        Apply();
        Preferences.LoadPreferences().SavePrefs();
        setVisible(false);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btnFirmwareDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirmwareDirActionPerformed
        JFileChooser chooser = new JFileChooser(Preferences.LoadPreferences().getCurrentFileDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String dir;
            try {
                dir = chooser.getSelectedFile().getCanonicalPath();
                Preferences.LoadPreferences().SetFirmwareDir(dir);
                txtFirmwareDir.setText(dir);
            } catch (IOException ex) {
                Logger.getLogger(PreferencesFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnFirmwareDirActionPerformed

    private void btnRuntimeDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRuntimeDirActionPerformed
        JFileChooser chooser = new JFileChooser(Preferences.LoadPreferences().getCurrentFileDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String dir;
            try {
                dir = chooser.getSelectedFile().getCanonicalPath();
                Preferences.LoadPreferences().SetRuntimeDir(dir);
                txtRuntimeDir.setText(dir);
            } catch (IOException ex) {
                Logger.getLogger(PreferencesFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnRuntimeDirActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        txtFirmwareDir.setText(System.getProperty(axoloti.Axoloti.FIRMWARE_DIR));
        txtRuntimeDir.setText(System.getProperty(axoloti.Axoloti.RUNTIME_DIR));
    }//GEN-LAST:event_formWindowActivated

    private void btnFavDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFavDirActionPerformed
        JFileChooser chooser = new JFileChooser(Preferences.LoadPreferences().getCurrentFileDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String dir;
            try {
                dir = chooser.getSelectedFile().getCanonicalPath();
                Preferences.LoadPreferences().setFavouriteDir(dir);
                txtFavDir.setText(dir);
            } catch (IOException ex) {
                Logger.getLogger(PreferencesFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnFavDirActionPerformed

    private void jControllerEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jControllerEnabledActionPerformed
        jTextFieldController.setEnabled(jControllerEnabled.isSelected());
    }//GEN-LAST:event_jControllerEnabledActionPerformed

    private void jAddLibBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddLibBtnActionPerformed

        AxolotiLibraryEditor d = new AxolotiLibraryEditor(this, true);
        AxolotiLibrary lib = d.getLibrary();

        AxolotiLibrary newlib;
        if (AxoGitLibrary.TYPE.equals(lib.getType())) {
            newlib = new AxoGitLibrary();
        } else {
            newlib = new AxoFileLibrary();
        }
        newlib.clone(lib);
        Preferences.LoadPreferences().updateLibrary(lib.getId(), newlib);
        PopulateLibrary();
    }//GEN-LAST:event_jAddLibBtnActionPerformed

    private void jDelLibBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDelLibBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
        int idx = jLibraryTable.getSelectedRow();
        if (idx >= 0) {
            String id = (String) model.getValueAt(idx, 1);
            Preferences.LoadPreferences().removeLibrary(id);
        }

        PopulateLibrary();
    }//GEN-LAST:event_jDelLibBtnActionPerformed

    private void jResetLibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetLibActionPerformed
        boolean delete = false;

        int options = JOptionPane.OK_CANCEL_OPTION;
        int res = JOptionPane.showConfirmDialog(this, "Reset will delete existing factory and contrib directories\n Continue?", "Warning", options);
        if (res == JOptionPane.CANCEL_OPTION) {
            return;
        }
        delete = (res == JOptionPane.OK_OPTION);

        Preferences.LoadPreferences().ResetLibraries(delete);
        PopulateLibrary();
    }//GEN-LAST:event_jResetLibActionPerformed

    private void jEditLibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditLibActionPerformed
        DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
        int idx = jLibraryTable.getSelectedRow();
        if (idx >= 0) {
            editLibraryRow(idx);
        }
    }//GEN-LAST:event_jEditLibActionPerformed

    private void jLibStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLibStatusActionPerformed
        for (AxolotiLibrary lib : Preferences.LoadPreferences().getLibraries()) {
            lib.reportStatus();
        }
    }//GEN-LAST:event_jLibStatusActionPerformed

    private void themeEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeEditButtonActionPerformed
        JFrame frame = MainFrame.mainframe.getThemeEditor();
        frame.setVisible(true);
        frame.setState(java.awt.Frame.NORMAL);
        frame.toFront();
    }//GEN-LAST:event_themeEditButtonActionPerformed

    private void jCheckBoxNoMouseReCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxNoMouseReCenterActionPerformed
        Preferences.LoadPreferences().setMouseDoNotRecenterWhenAdjustingControls(jCheckBoxNoMouseReCenter.isSelected());
    }//GEN-LAST:event_jCheckBoxNoMouseReCenterActionPerformed

    private void editLibraryRow(int idx) {
        if (idx >= 0) {
            DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
            String id = (String) model.getValueAt(idx, 1);
            AxolotiLibrary lib = Preferences.LoadPreferences().getLibrary(id);
            if (lib != null) {
                String type = lib.getType();
                AxolotiLibraryEditor d = new AxolotiLibraryEditor(this, true, lib);
                AxolotiLibrary updlib = lib;
                if(!lib.getType().equals(type)) {
                  if (AxoGitLibrary.TYPE.equals(lib.getType())) {
                       updlib = new AxoGitLibrary();
                   } else {
                       updlib = new AxoFileLibrary();
                   }
                  updlib.clone(lib);
                }
                Preferences.LoadPreferences().updateLibrary(lib.getId(), updlib);
                PopulateLibrary();
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFavDir;
    private javax.swing.JButton btnFirmwareDir;
    private javax.swing.JButton btnRuntimeDir;
    private javax.swing.JButton jAddLibBtn;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxNoMouseReCenter;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JCheckBox jControllerEnabled;
    private javax.swing.JButton jDelLibBtn;
    private javax.swing.JButton jEditLib;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JButton jLibStatus;
    private javax.swing.JTable jLibraryTable;
    private javax.swing.JButton jResetLib;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldController;
    private javax.swing.JTextField jTextFieldPollInterval;
    private javax.swing.JButton themeEditButton;
    private javax.swing.JLabel txtFavDir;
    private javax.swing.JLabel txtFirmwareDir;
    private javax.swing.JLabel txtRuntimeDir;
    // End of variables declaration//GEN-END:variables
}
