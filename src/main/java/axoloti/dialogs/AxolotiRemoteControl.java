/**
 * Copyright (C) 2013 - 2017 Johannes Taelman
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
import axoloti.CConnection;
import axoloti.IConnection;
import axoloti.VirtualInputEvent;
import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.menus.StandardMenubar;
import components.RControlButtonWithLed;
import components.RControlColorLed;
import components.RControlEncoder;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import qcmds.QCmdMemRead;
import qcmds.QCmdProcessor;
import qcmds.QCmdVirtualInputEvent;

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
        setJMenuBar(new StandardMenubar());
        CConnection.GetConnection().addConnectionStatusListener(this);
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
        //jPanelLCD.setFocusable(true);

        setFocusable(true);        
        setFocusTraversalKeysEnabled(false);
        
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                VirtualInputEvent evt = KeyToVirtualEvent(e, true);
                if (evt!=null) {
                    tx(evt);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                VirtualInputEvent evt = KeyToVirtualEvent(e, false);
                if (evt!=null) {
                    tx(evt);
                }
            }
        });
        InputMap inputmap = jPanelLCD.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        encoders[0] = new RControlEncoder() {
            @Override
            public void DoRotation(int ticks) {
//                QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
//                processor.AppendToQueue(new QCmdVirtualInputEvent(ticks, 0, 0, 0));
            }
        };
        encoders[1] = new RControlEncoder() {
            @Override
            public void DoRotation(int ticks) {
//                QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
//                processor.AppendToQueue(new QCmdVirtualInputEvent(0, ticks, 0, 0));
            }
        };
        encoders[2] = new RControlEncoder() {
            @Override
            public void DoRotation(int ticks) {
//                QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
//                processor.AppendToQueue(new QCmdVirtualInputEvent(0, 0, ticks, 0));
            }
        };
        encoders[3] = new RControlEncoder() {
            @Override
            public void DoRotation(int ticks) {
//                QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
//                processor.AppendToQueue(new QCmdVirtualInputEvent(0, 0, 0, ticks));
            }
        };

        jPanelRight.add(encoders[0]);
        jPanelRight.add(encoders[1]);
        jPanelRight.add(encoders[2]);
        jPanelRight.add(encoders[3]);

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
    
    VirtualInputEvent KeyToVirtualEvent(KeyEvent e, boolean pressed){        
        byte modifiers = e.isShiftDown()?VirtualInputEvent.MODIFIER_SHIFT:0;
        byte value = pressed?(byte)1:0;
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP :
                return new VirtualInputEvent(VirtualInputEvent.BTN_UP, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            case KeyEvent.VK_DOWN :
                return new VirtualInputEvent(VirtualInputEvent.BTN_DOWN, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            case KeyEvent.VK_ENTER :
                return new VirtualInputEvent(VirtualInputEvent.BTN_E, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            case KeyEvent.VK_ESCAPE :
                return new VirtualInputEvent(VirtualInputEvent.BTN_X, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            default:
                return null;
        }
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
            //tx_pressed(1 << (index));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //tx_released(1 << (index));
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
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jButtonRefresh = new javax.swing.JButton();
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
        jPanelNav.add(filler16);
        jPanelNav.add(filler17);

        jButtonRefresh.setText("refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });
        jPanelNav.add(jButtonRefresh);

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

    void tx(VirtualInputEvent evt) {
        QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
        processor.AppendToQueue(new QCmdVirtualInputEvent(evt));
    }
    
    public void refreshFB() {
        QCmdProcessor processor = MainFrame.mainframe.getQcmdprocessor();
        ChunkData framebuffer = CConnection.GetConnection().GetFWChunks().GetOne(FourCCs.FW_LCD_FRAMEBUFFER);
        framebuffer.data.rewind();
        int width = framebuffer.data.getInt();
        int height = framebuffer.data.getInt();
        int pixeltype = framebuffer.data.getInt();
        int addr = framebuffer.data.getInt();
        processor.AppendToQueue(new QCmdMemRead(addr, 128*64/8, new IConnection.MemReadHandler() {
            @Override
            public void Done(ByteBuffer mem) {
                updateFB(mem);
            }
        }));
    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        //tx_clicked(K_BACK);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeftActionPerformed
        //tx_clicked(K_LEFT);
    }//GEN-LAST:event_jButtonLeftActionPerformed

    private void jButtoneEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtoneEnterActionPerformed
        //tx_clicked(K_ENTER);
    }//GEN-LAST:event_jButtoneEnterActionPerformed

    private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed
        //tx_clicked(K_UP);
    }//GEN-LAST:event_jButtonUpActionPerformed

    private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed
        //tx_clicked(K_DOWN);
    }//GEN-LAST:event_jButtonDownActionPerformed

    private void jButtonRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRightActionPerformed
        //tx_clicked(K_RIGHT);
    }//GEN-LAST:event_jButtonRightActionPerformed

    private void jButtonShiftMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShiftMousePressed
        //tx_pressed(K_SHIFT);
    }//GEN-LAST:event_jButtonShiftMousePressed

    private void jButtonShiftMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonShiftMouseReleased
        //tx_pressed(K_SHIFT);
    }//GEN-LAST:event_jButtonShiftMouseReleased

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        refreshFB();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
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
    private javax.swing.JButton jButtonRefresh;
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

    public void updateFB(final ByteBuffer lcdRcvBuffer) {
        byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
        for (int row = 0;row<8;row++) {
            for (int i = 0; i < 128; i++) {
                //int j = 1<<(i%8);
                int k = i - (i % 8);
                int y = i / 16;
                int j = 1 << y;
                int x = 8 * (i % 16) + row*128;
                pixels[i + row*128] = (byte) ((((lcdRcvBuffer.get(x) & j) > 0) ? 0 : 128)
                        + (((lcdRcvBuffer.get(x + 1) & j) > 0) ? 0 : 64)
                        + (((lcdRcvBuffer.get(x + 2) & j) > 0) ? 0 : 32)
                        + (((lcdRcvBuffer.get(x + 3) & j) > 0) ? 0 : 16)
                        + (((lcdRcvBuffer.get(x + 4) & j) > 0) ? 0 : 8)
                        + (((lcdRcvBuffer.get(x + 5) & j) > 0) ? 0 : 4)
                        + (((lcdRcvBuffer.get(x + 6) & j) > 0) ? 0 : 2)
                        + (((lcdRcvBuffer.get(x + 7) & j) > 0) ? 0 : 1));
            }
        }
        dirty = true;
        jPanelLCD.repaint(10);
    }
}
