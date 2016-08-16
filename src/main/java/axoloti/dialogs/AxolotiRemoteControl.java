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

import axoloti.ConnectionStatusListener;
import axoloti.MainFrame;
import axoloti.USBBulkConnection;
import components.RControlButtonWithLed;
import components.RControlColorLed;
import components.RControlEncoder;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import qcmds.QCmdProcessor;
import qcmds.QCmdVirtualButton;

/**
 *
 * @author Johannes Taelman
 */
public class AxolotiRemoteControl extends javax.swing.JFrame implements ConnectionStatusListener {

    /**
     * Creates new form AxolotiRemoteControl
     */
    public AxolotiRemoteControl() {
        initComponents();
        USBBulkConnection.GetConnection().addConnectionStatusListener(this);
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());
        jPanelLCD.setLayout(new FlowLayout());
        ImageIcon ii = new ImageIcon(bImageScaled) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                if (dirty) {
                    dirty = false;
                    g2d.drawImage(bImage, 0, 0, 256, 128, null);
                }
                super.paintIcon(c, g, x, y); //To change body of generated methods, choose Tools | Templates.
            }

        };
        jPanelLCD.add(new JLabel(ii));
        jPanelLCD.doLayout();
        jPanelLCD.setVisible(true);
        jPanelLCD.setFocusable(true);
        jPanelLCD.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyCode()) {

                    default:
                        break;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        tx_clicked(K_UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        tx_clicked(K_DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        tx_clicked(K_LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        tx_clicked(K_RIGHT);
                        break;
                    case KeyEvent.VK_ENTER:
                        tx_clicked(K_ENTER);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        tx_clicked(K_CANCEL);
                        break;
                    case KeyEvent.VK_SHIFT:
                        tx_pressed(K_SHIFT);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT:
                        tx_released(K_SHIFT);
                        break;
                    default:
                        break;
                }
            }
        });

        for (int i = 0; i < 4; i++) {
            encoders[i] = new RControlEncoder() {
                @Override
                public void DoRotation(int ticks) {
                    QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
                    processor.AppendToQueue(new QCmdVirtualButton(ticks, 0, 0, 0));
                }
            };
            jPanelRight.add(encoders[i]);
        }

        for (int i = 0; i < 4; i++) {
            leds[i] = new RControlColorLed();
            jPanelRight.add(leds[i]);
        }

        for (int i = 0; i < 16; i++) {
            buttonsWithLeds[i] = new RControlButtonWithLed();
            buttonsWithLeds[i].addMouseListener(new MouseListerTxer(i));
            jPanelRight.add(buttonsWithLeds[i]);
        }

        jButtonCancel.setFocusable(false);
        jButtonDown.setFocusable(false);
        jButtonLeft.setFocusable(false);
        jButtonRight.setFocusable(false);
        jButtonUp.setFocusable(false);
        jButtoneEnter.setFocusable(false);
        jButtonShift.setFocusable(false);
    }

    @Override
    public void ShowConnect() {
        jButtonCancel.setEnabled(true);
        jButtonDown.setEnabled(true);
        jButtonLeft.setEnabled(true);
        jButtonRight.setEnabled(true);
        jButtonUp.setEnabled(true);
        jButtoneEnter.setEnabled(true);
        jButtonShift.setEnabled(true);
    }

    @Override
    public void ShowDisconnect() {
        jButtonCancel.setEnabled(false);
        jButtonDown.setEnabled(false);
        jButtonLeft.setEnabled(false);
        jButtonRight.setEnabled(false);
        jButtonUp.setEnabled(false);
        jButtoneEnter.setEnabled(false);
        jButtonShift.setEnabled(false);
    }

    class MouseListerTxer implements MouseListener {

        int index;

        public MouseListerTxer(int index) {
            this.index = index;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            tx_pressed(1 << (index + 16));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            tx_released(1 << (index + 16));
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    RControlEncoder[] encoders = new RControlEncoder[4];
    RControlColorLed[] leds = new RControlColorLed[4];
    RControlButtonWithLed[] buttonsWithLeds = new RControlButtonWithLed[16];

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanelLCD = new javax.swing.JPanel();
        jPanelNav = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtoneEnter = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtonUp = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtonLeft = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtonRight = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtonDown = new javax.swing.JButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtonShift = new javax.swing.JButton();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jPanelRight = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Axoloti Remote Control");
        setMinimumSize(new java.awt.Dimension(512, 280));
        setPreferredSize(new java.awt.Dimension(512, 280));
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(192, 192, 192));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setBackground(new java.awt.Color(192, 192, 192));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelLCD.setBackground(new java.awt.Color(192, 192, 192));
        jPanelLCD.setMaximumSize(new java.awt.Dimension(266, 138));
        jPanelLCD.setMinimumSize(new java.awt.Dimension(266, 138));
        jPanelLCD.setPreferredSize(new java.awt.Dimension(266, 138));

        javax.swing.GroupLayout jPanelLCDLayout = new javax.swing.GroupLayout(jPanelLCD);
        jPanelLCD.setLayout(jPanelLCDLayout);
        jPanelLCDLayout.setHorizontalGroup(
            jPanelLCDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );
        jPanelLCDLayout.setVerticalGroup(
            jPanelLCDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        jPanel2.add(jPanelLCD);

        jPanelNav.setBackground(new java.awt.Color(192, 192, 192));
        jPanelNav.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanelNav.setMaximumSize(new java.awt.Dimension(266, 200));
        jPanelNav.setMinimumSize(new java.awt.Dimension(266, 80));
        jPanelNav.setPreferredSize(new java.awt.Dimension(266, 80));
        jPanelNav.setLayout(new java.awt.GridLayout(5, 5, 5, 5));

        jButtonCancel.setText("X");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtonCancel);
        jPanelNav.add(filler1);
        jPanelNav.add(filler2);
        jPanelNav.add(filler3);

        jButtoneEnter.setText("O");
        jButtoneEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoneEnterActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtoneEnter);
        jPanelNav.add(filler4);
        jPanelNav.add(filler5);

        jButtonUp.setText("<html>&uarr;");
        jButtonUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtonUp);
        jPanelNav.add(filler6);
        jPanelNav.add(filler7);
        jPanelNav.add(filler8);

        jButtonLeft.setText("<html>&larr;");
        jButtonLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeftActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtonLeft);
        jPanelNav.add(filler9);

        jButtonRight.setText("<html>&rarr;");
        jButtonRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRightActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtonRight);
        jPanelNav.add(filler10);
        jPanelNav.add(filler11);
        jPanelNav.add(filler12);

        jButtonDown.setText("<html>&darr;");
        jButtonDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtonDown);
        jPanelNav.add(filler13);
        jPanelNav.add(filler14);

        jButtonShift.setText("<html>&Delta;");
        jButtonShift.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonShiftMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonShiftMouseReleased(evt);
            }
        });
        jPanelNav.add(jButtonShift);
        jPanelNav.add(filler15);

        jPanel2.add(jPanelNav);

        jPanel1.add(jPanel2);

        jPanelRight.setBackground(new java.awt.Color(192, 192, 192));
        jPanelRight.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanelRight.setMinimumSize(new java.awt.Dimension(256, 256));
        jPanelRight.setLayout(new java.awt.GridLayout(6, 4, 5, 5));
        jPanel1.add(jPanelRight);

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void tx(int b_or, int b_and) {
        QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
        processor.AppendToQueue(new QCmdVirtualButton(b_or, b_and));
    }

    void tx_pressed(int k) {
        tx(k, ~0);
    }

    void tx_released(int k) {
        tx(0, ~k);
    }

    void tx_clicked(int k) {
        tx(k, ~k);
    }
// button masks
    final int K_UP = 1;
    final int K_DOWN = 2;
    final int K_LEFT = 4;
    final int K_RIGHT = 8;
    final int K_ENTER = 16;
    final int K_SHIFT = 32;
    final int K_CANCEL = 64;
    final int K_1 = 1 << 16;
    final int K_2 = 1 << 17;
    final int K_3 = 1 << 18;
    final int K_4 = 1 << 19;
    final int K_5 = 1 << 20;
    final int K_6 = 1 << 21;
    final int K_7 = 1 << 22;
    final int K_8 = 1 << 23;
    final int K_9 = 1 << 24;
    final int K_10 = 1 << 25;
    final int K_11 = 1 << 26;
    final int K_12 = 1 << 27;
    final int K_13 = 1 << 28;
    final int K_14 = 1 << 29;
    final int K_15 = 1 << 30;
    final int K_16 = 1 << 31;

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        tx_clicked(K_CANCEL);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeftActionPerformed
        tx_clicked(K_LEFT);
    }//GEN-LAST:event_jButtonLeftActionPerformed

    private void jButtoneEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtoneEnterActionPerformed
        /*
         // test bitmap
         byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
         int i;
         for (i = 0; i < (128); i++) {
         pixels[i] = (byte) (i);
         }
         for (; i < (128 * 8); i++) {
         pixels[i] = (byte) (0xAA);
         }
         jPanelLCD.repaint();
         */
        tx_clicked(K_ENTER);
    }//GEN-LAST:event_jButtoneEnterActionPerformed

    private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed
        tx_clicked(K_UP);
    }//GEN-LAST:event_jButtonUpActionPerformed

    private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed
        tx_clicked(K_DOWN);
    }//GEN-LAST:event_jButtonDownActionPerformed

    private void jButtonRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRightActionPerformed
        tx_clicked(K_RIGHT);
    }//GEN-LAST:event_jButtonRightActionPerformed

    private void jButtonShiftMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShiftMousePressed
        tx_pressed(K_SHIFT);
    }//GEN-LAST:event_jButtonShiftMousePressed

    private void jButtonShiftMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShiftMouseReleased
        tx_pressed(K_SHIFT);
    }//GEN-LAST:event_jButtonShiftMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDown;
    private javax.swing.JButton jButtonLeft;
    private javax.swing.JButton jButtonRight;
    private javax.swing.JButton jButtonShift;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JButton jButtoneEnter;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelLCD;
    private javax.swing.JPanel jPanelNav;
    private javax.swing.JPanel jPanelRight;
    // End of variables declaration//GEN-END:variables
    private final BufferedImage bImage = new BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY);
    private final BufferedImage bImageScaled = new BufferedImage(256, 128, BufferedImage.TYPE_BYTE_BINARY);
    private final Graphics2D g2d = (Graphics2D) bImageScaled.createGraphics();

    boolean dirty = false;

    public void updateRow(final int LCDPacketRow, final ByteBuffer lcdRcvBuffer) {
        if (false) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (LCDPacketRow < 8) {

                        byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
                        for (int i = 0; i < (128); i++) {
                            //int j = 1<<(i%8);
                            int k = i - (i % 8);
                            int y = i / 16;
                            int j = 1 << y;
                            int x = 8 * (i % 16);
                            pixels[i + (LCDPacketRow * 128)] = (byte) ((((lcdRcvBuffer.get(x) & j) > 0) ? 0 : 128)
                                    + (((lcdRcvBuffer.get(x + 1) & j) > 0) ? 0 : 64)
                                    + (((lcdRcvBuffer.get(x + 2) & j) > 0) ? 0 : 32)
                                    + (((lcdRcvBuffer.get(x + 3) & j) > 0) ? 0 : 16)
                                    + (((lcdRcvBuffer.get(x + 4) & j) > 0) ? 0 : 8)
                                    + (((lcdRcvBuffer.get(x + 5) & j) > 0) ? 0 : 4)
                                    + (((lcdRcvBuffer.get(x + 6) & j) > 0) ? 0 : 2)
                                    + (((lcdRcvBuffer.get(x + 7) & j) > 0) ? 0 : 1));
                        }
                        dirty = true;
                        jPanelLCD.repaint(10);

                    } else {
                        // row 8 is for all the leds
                        for (int i = 0; i < 16; i++) {
                            buttonsWithLeds[i].setIlluminated(lcdRcvBuffer.get(i) != 0);
                        }
                    }
                }
            });
        }
    }
}
