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
package axoloti.swingui.preferences;

import axoloti.objectlibrary.AxoFileLibrary;
import axoloti.objectlibrary.AxoGitLibrary;
import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.preferences.Preferences;
import axoloti.swingui.MainFrame;
import axoloti.swingui.dialogs.AxolotiLibraryEditor;
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
public class PreferencesFrame extends javax.swing.JDialog {

    /**
     * Creates new form PreferencesFrame
     *
     */
    public PreferencesFrame() {
        initComponents();
        initComponents2();
    }

    private void initComponents2() {
        Preferences prefs = Preferences.getPreferences();
        jTextFieldPollInterval.setText(Integer.toString(prefs.getPollInterval()));

        txtFavDir.setText(prefs.getFavouriteDir());
        jTextFieldPath.setText(prefs.getPath());
        populateLibrary();

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

        mouseWheelComboBox.setSelectedItem(
                mouseWheelComboBox.getModel().getElementAt(prefs.getMouseWheelPan() ? 1 : 0));

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
        setModal(true);
        setVisible(true);
    }

    void apply() {
        Preferences prefs = Preferences.getPreferences();
        prefs.setPath(jTextFieldPath.getText());
        prefs.setPollInterval(Integer.parseInt(jTextFieldPollInterval.getText()));
        prefs.setMouseDialAngular(jComboBox1.getSelectedItem().equals("Angular"));
        prefs.setFavouriteDir(txtFavDir.getText());
        prefs.setMouseWheelPan(mouseWheelComboBox.getSelectedItem().equals("Pan"));
    }

    final void populateLibrary() {
        DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
        model.setRowCount(0);
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        for (AxolotiLibrary lib : Preferences.getPreferences().getLibraries()) {
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
        jLabel7 = new javax.swing.JLabel();
        txtFavDir = new javax.swing.JLabel();
        btnFavDir = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLibraryTable = new javax.swing.JTable();
        jAddLibBtn = new javax.swing.JButton();
        jDelLibBtn = new javax.swing.JButton();
        jResetLib = new javax.swing.JButton();
        jEditLib = new javax.swing.JButton();
        jLibStatus = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        themeEditButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        mouseWheelComboBox = new javax.swing.JComboBox();
        jCheckBoxNoMouseReCenter = new javax.swing.JCheckBox();
        jTextFieldPath = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        jLabel4.setText("Favourite Dir");

        jLabel7.setText("Additional path");

        txtFavDir.setText("jLabel6");

        btnFavDir.setText("Select");
        btnFavDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFavDirActionPerformed(evt);
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

        jLabel9.setText("Mouse Wheel behavior");

        mouseWheelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Zoom", "Pan" }));
        mouseWheelComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mouseWheelComboBoxActionPerformed(evt);
            }
        });

        jCheckBoxNoMouseReCenter.setText("Do not re-center cursor (for touchscreens)");
        jCheckBoxNoMouseReCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxNoMouseReCenterActionPerformed(evt);
            }
        });

        jTextFieldPath.setText("jTextField1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxNoMouseReCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(37, 37, 37)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldPollInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(114, 114, 114)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mouseWheelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(themeEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(389, 389, 389))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtFavDir, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnFavDir, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10))
                                    .addComponent(jTextFieldPath)))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFavDir)
                    .addComponent(jLabel4)
                    .addComponent(btnFavDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxNoMouseReCenter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(themeEditButton)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(mouseWheelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSave)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        apply();
        Preferences.getPreferences().savePrefs();
        setVisible(false);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void btnFavDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFavDirActionPerformed
        JFileChooser chooser = new JFileChooser(Preferences.getPreferences().getCurrentFileDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String dir;
            try {
                dir = chooser.getSelectedFile().getCanonicalPath();
                Preferences.getPreferences().setFavouriteDir(dir);
                txtFavDir.setText(dir);
            } catch (IOException ex) {
                Logger.getLogger(PreferencesFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnFavDirActionPerformed

    private void jAddLibBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddLibBtnActionPerformed

        AxolotiLibraryEditor d = new AxolotiLibraryEditor(null, true);
        AxolotiLibrary lib = d.getLibrary();

        AxolotiLibrary newlib;
        if (AxoGitLibrary.TYPE.equals(lib.getType())) {
            newlib = new AxoGitLibrary();
        } else {
            newlib = new AxoFileLibrary();
        }
        newlib.clone(lib);
        Preferences.getPreferences().updateLibrary(lib.getId(), newlib);
        populateLibrary();
    }//GEN-LAST:event_jAddLibBtnActionPerformed

    private void jDelLibBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDelLibBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
        int idx = jLibraryTable.getSelectedRow();
        if (idx >= 0) {
            String id = (String) model.getValueAt(idx, 1);
            Preferences.getPreferences().removeLibrary(id);
        }

        populateLibrary();
    }//GEN-LAST:event_jDelLibBtnActionPerformed

    private void jResetLibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetLibActionPerformed

        int options = JOptionPane.OK_CANCEL_OPTION;
        int res = JOptionPane.showConfirmDialog(this, "Reset will delete existing factory and contrib directories\n Continue?", "Warning", options);
        if (res == JOptionPane.CANCEL_OPTION) {
            return;
        }
        boolean delete = (res == JOptionPane.OK_OPTION);

        Preferences.getPreferences().resetLibraries(delete);
        populateLibrary();
    }//GEN-LAST:event_jResetLibActionPerformed

    private void jEditLibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditLibActionPerformed
        DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
        int idx = jLibraryTable.getSelectedRow();
        if (idx >= 0) {
            editLibraryRow(idx);
        }
    }//GEN-LAST:event_jEditLibActionPerformed

    private void jLibStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLibStatusActionPerformed
        for (AxolotiLibrary lib : Preferences.getPreferences().getLibraries()) {
            lib.reportStatus();
        }
    }//GEN-LAST:event_jLibStatusActionPerformed

    private void themeEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themeEditButtonActionPerformed
        JFrame frame = MainFrame.mainframe.getThemeEditor();
        frame.setVisible(true);
        frame.setState(java.awt.Frame.NORMAL);
        frame.toFront();
    }//GEN-LAST:event_themeEditButtonActionPerformed

    private void mouseWheelComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mouseWheelComboBoxActionPerformed

    }//GEN-LAST:event_mouseWheelComboBoxActionPerformed

    private void jCheckBoxNoMouseReCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxNoMouseReCenterActionPerformed
        Preferences.getPreferences().setMouseDoNotRecenterWhenAdjustingControls(jCheckBoxNoMouseReCenter.isSelected());
    }//GEN-LAST:event_jCheckBoxNoMouseReCenterActionPerformed

    private void editLibraryRow(int idx) {
        if (idx >= 0) {
            DefaultTableModel model = (DefaultTableModel) jLibraryTable.getModel();
            String id = (String) model.getValueAt(idx, 1);
            AxolotiLibrary lib = Preferences.getPreferences().getLibrary(id);
            if (lib != null) {
                String type = lib.getType();
                AxolotiLibraryEditor d = new AxolotiLibraryEditor(null, true, lib);
                AxolotiLibrary updlib = lib;
                if (!lib.getType().equals(type)) {
                    if (AxoGitLibrary.TYPE.equals(lib.getType())) {
                        updlib = new AxoGitLibrary();
                    } else {
                        updlib = new AxoFileLibrary();
                    }
                    updlib.clone(lib);
                }
                Preferences.getPreferences().updateLibrary(lib.getId(), updlib);
                populateLibrary();
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFavDir;
    private javax.swing.JButton jAddLibBtn;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxNoMouseReCenter;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JButton jDelLibBtn;
    private javax.swing.JButton jEditLib;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLibStatus;
    private javax.swing.JTable jLibraryTable;
    private javax.swing.JButton jResetLib;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldPath;
    private javax.swing.JTextField jTextFieldPollInterval;
    private javax.swing.JComboBox mouseWheelComboBox;
    private javax.swing.JButton themeEditButton;
    private javax.swing.JLabel txtFavDir;
    // End of variables declaration//GEN-END:variables
}
