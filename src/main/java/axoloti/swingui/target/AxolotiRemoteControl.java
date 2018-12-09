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
package axoloti.swingui.target;

import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.swingui.components.RControlButtonWithLed;
import axoloti.swingui.components.RControlColorLed;
import axoloti.swingui.components.RControlEncoder;
import axoloti.target.TargetModel;
import axoloti.target.remote.VirtualInputEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Johannes Taelman
 */
public class AxolotiRemoteControl extends TJFrame {

    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private final List<JButton> buttons = new LinkedList<>();
    private Runnable poller;

    /**
     * Creates new form AxolotiRemoteControl
     *
     * TODO: (low priority) add virtual LEDs
     *
     */
    public AxolotiRemoteControl(TargetModel targetModel) {
        super(targetModel);
        initComponents();
        initComponents2();
    }

    private void initComponents2() {
        JPanel jPanelY = new JPanel();
        jPanelY.setLayout(new BoxLayout(jPanelY, BoxLayout.Y_AXIS));
        JPanel jPanelX1 = new JPanel();
        jPanelX1.setLayout(new BoxLayout(jPanelX1, BoxLayout.X_AXIS));
        jPanel1 = new JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        jPanel2 = new JPanel();
        jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));
        JButton btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_UP, VirtualInputEvent.QUADRANT_TOPLEFT));
        jPanel2.add(btn);
        buttons.add(btn);
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_DOWN, VirtualInputEvent.QUADRANT_TOPLEFT));
        jPanel2.add(btn);
        buttons.add(btn);
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_UP, VirtualInputEvent.QUADRANT_BOTTOMLEFT));
        jPanel2.add(btn);
        buttons.add(btn);
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_DOWN, VirtualInputEvent.QUADRANT_BOTTOMLEFT));
        jPanel2.add(btn);
        buttons.add(btn);
        jPanel3 = new JPanel();
        jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.Y_AXIS));
        ImageIcon ii = new ImageIcon(bImageScaled) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                if (dirty) {
                    dirty = false;
                    g2d.drawImage(bImage, 0, 0, 256, 128, null);
                }
                super.paintIcon(c, g, x, y);
            }

        };
        jPanel3.add(new JLabel(ii));
        jPanel4 = new JPanel();
        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_UP, VirtualInputEvent.QUADRANT_TOPRIGHT));
        jPanel4.add(btn);
        buttons.add(btn);
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_DOWN, VirtualInputEvent.QUADRANT_TOPRIGHT));
        jPanel4.add(btn);
        buttons.add(btn);
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_UP, VirtualInputEvent.QUADRANT_BOTTOMRIGHT));
        jPanel4.add(btn);
        buttons.add(btn);
        btn = new JButton("");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_DOWN, VirtualInputEvent.QUADRANT_BOTTOMRIGHT));
        jPanel4.add(btn);
        buttons.add(btn);
        jPanel5 = new JPanel();
        jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));

//        jPanelLCD.doLayout();
//        jPanelLCD.setVisible(true);
        //jPanelLCD.setFocusable(true);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                VirtualInputEvent evt = convertKeyToVirtualEvent(e, true);
                if (evt != null) {
                    transmitVirtualInputEvent(evt);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                VirtualInputEvent evt = convertKeyToVirtualEvent(e, false);
                if (evt != null) {
                    transmitVirtualInputEvent(evt);
                }
            }
        });
        // InputMap inputmap = jPanel3.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        encoders[0] = new RControlEncoder() {
            @Override
            public void fireRotation(int ticks) {
                transmitVirtualInputEvent(
                        new VirtualInputEvent(
                                VirtualInputEvent.BTN_ENCODER,
                                (byte) 0, // TODO: (low priority) implement modifiers (shift)
                                (byte) ticks,
                                VirtualInputEvent.QUADRANT_TOPLEFT
                        )
                );
            }
        };
        encoders[1] = new RControlEncoder() {
            @Override
            public void fireRotation(int ticks) {
                transmitVirtualInputEvent(
                        new VirtualInputEvent(
                                VirtualInputEvent.BTN_ENCODER,
                                (byte) 0, // TODO: (low priority) implement modifiers (shift)
                                (byte) ticks,
                                VirtualInputEvent.QUADRANT_BOTTOMLEFT
                        )
                );
            }
        };
        encoders[2] = new RControlEncoder() {
            @Override
            public void fireRotation(int ticks) {
                transmitVirtualInputEvent(
                        new VirtualInputEvent(
                                VirtualInputEvent.BTN_ENCODER,
                                (byte) 0, // TODO: (low priority) implement modifiers (shift)
                                (byte) ticks,
                                VirtualInputEvent.QUADRANT_TOPRIGHT
                        )
                );
            }
        };
        encoders[3] = new RControlEncoder() {
            @Override
            public void fireRotation(int ticks) {
                transmitVirtualInputEvent(
                        new VirtualInputEvent(
                                VirtualInputEvent.BTN_ENCODER,
                                (byte) 0, // TODO: (low priority) implement modifiers (shift)
                                (byte) ticks,
                                VirtualInputEvent.QUADRANT_BOTTOMRIGHT
                        )
                );
            }
        };

        jPanel1.add(encoders[0]);
        jPanel1.add(encoders[1]);
        jPanel5.add(encoders[2]);
        jPanel5.add(encoders[3]);

        jPanelX1.add(jPanel1);
        jPanelX1.add(jPanel2);
        jPanelX1.add(jPanel3);
        jPanelX1.add(jPanel4);
        jPanelX1.add(jPanel5);
        jPanelY.add(jPanelX1);
        JPanel jPanelX2 = new JPanel();
        jPanelX2.setLayout(new BoxLayout(jPanelX2, BoxLayout.X_AXIS));
        btn = new JButton("↑");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_UP, VirtualInputEvent.QUADRANT_MAIN));
        buttons.add(btn);
        jPanelX2.add(btn);
        btn = new JButton("↓");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_DOWN, VirtualInputEvent.QUADRANT_MAIN));
        buttons.add(btn);
        jPanelX2.add(btn);
        btn = new JButton("F");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_F, VirtualInputEvent.QUADRANT_MAIN));
        buttons.add(btn);
        jPanelX2.add(btn);
        btn = new JButton("⇧");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_S, VirtualInputEvent.QUADRANT_MAIN));
        buttons.add(btn);
        jPanelX2.add(btn);
        btn = new JButton("✗");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_X, VirtualInputEvent.QUADRANT_MAIN));
        buttons.add(btn);
        jPanelX2.add(btn);
        btn = new JButton("✓");
        btn.addMouseListener(new MouseListerTxer(VirtualInputEvent.BTN_E, VirtualInputEvent.QUADRANT_MAIN));
        buttons.add(btn);
        jPanelX2.add(btn);
        jPanelY.add(jPanelX2);
        JPanel jPanelX3 = new JPanel();
        jPanelX3.setLayout(new BoxLayout(jPanelX3, BoxLayout.X_AXIS));
        for (int i = 0; i < 16; i++) {
            buttonsWithLeds[i] = new RControlButtonWithLed();
            buttonsWithLeds[i].addMouseListener(new MouseListerTxer((byte) (VirtualInputEvent.BTN_1 + i), VirtualInputEvent.QUADRANT_MAIN));
            jPanelX3.add(buttonsWithLeds[i]);
        }
        jPanelY.add(jPanelX3);

        for (JButton btn1 : buttons) {
            btn1.setFocusable(false);
        }

        add(jPanelY);

        poller = () -> {
            refreshFB();
        };

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setPolling(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setPolling(false);
            }
        });
    }

    void setPolling(boolean b) {
        if (b) {
            TargetModel.getTargetModel().addPoller(poller);
        } else {
            TargetModel.getTargetModel().removePoller(poller);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        setPolling(false);
    }

    VirtualInputEvent convertKeyToVirtualEvent(KeyEvent e, boolean pressed) {
        byte modifiers = e.isShiftDown() ? VirtualInputEvent.MODIFIER_SHIFT : 0;
        byte value = pressed ? (byte) 1 : 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                return new VirtualInputEvent(VirtualInputEvent.BTN_UP, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            case KeyEvent.VK_DOWN:
                return new VirtualInputEvent(VirtualInputEvent.BTN_DOWN, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            case KeyEvent.VK_ENTER:
                return new VirtualInputEvent(VirtualInputEvent.BTN_E, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            case KeyEvent.VK_ESCAPE:
                return new VirtualInputEvent(VirtualInputEvent.BTN_X, modifiers, value, VirtualInputEvent.QUADRANT_MAIN);
            default:
                return null;
        }
    }

    private static void transmitVirtualInputEvent(VirtualInputEvent evt) {
        try {
            IConnection conn = CConnection.getConnection();
            conn.transmitVirtualInputEvent(evt.getQuadrant(), evt.getModifiers(), evt.getValue(), evt.getButton());
        } catch (IOException ex) {
            Logger.getLogger(AxolotiRemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (TargetModel.CONNECTION.is(evt)) {
            boolean b = evt.getNewValue() != null;
            for (JButton btn : buttons) {
                btn.setEnabled(b);
            }
        }
    }

    private static class MouseListerTxer implements MouseListener {

        final byte button;
        final byte quadrant;

        MouseListerTxer(byte button, byte quadrant) {
            this.button = button;
            this.quadrant = quadrant;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            transmitVirtualInputEvent(
                    new VirtualInputEvent(
                            button,
                            e.isShiftDown() ? VirtualInputEvent.MODIFIER_SHIFT : (byte) 0,
                            (byte) 1,
                            quadrant
                    )
            );
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            transmitVirtualInputEvent(
                    new VirtualInputEvent(
                            button,
                            e.isShiftDown() ? VirtualInputEvent.MODIFIER_SHIFT : (byte) 0,
                            (byte) 0,
                            quadrant
                    )
            );
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    private final RControlEncoder[] encoders = new RControlEncoder[4];
    private final RControlColorLed[] leds = new RControlColorLed[4];
    private final RControlButtonWithLed[] buttonsWithLeds = new RControlButtonWithLed[16];

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Axoloti Remote Control");
        setMinimumSize(new java.awt.Dimension(512, 280));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    public void refreshFB() {
        try {
            ChunkData framebuffer = CConnection.getConnection().getFWChunks().getOne(FourCCs.FW_LCD_FRAMEBUFFER);
            ByteBuffer data = framebuffer.getData();
            int width = data.getInt();
            int height = data.getInt();
            int pixeltype = data.getInt();
            int addr = data.getInt();
            ByteBuffer mem = CConnection.getConnection().read(addr, 128 * 64 / 8);
            updateFB(mem);
        } catch (IOException ex) {
            Logger.getLogger(AxolotiRemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private final BufferedImage bImage = new BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY);
    private final BufferedImage bImageScaled = new BufferedImage(256, 128, BufferedImage.TYPE_BYTE_BINARY);
    private final Graphics2D g2d = bImageScaled.createGraphics();

    private boolean dirty = false;

    public void updateFB(final ByteBuffer lcdRcvBuffer) {
        byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
        for (int row = 0; row < 8; row++) {
            for (int i = 0; i < 128; i++) {
                int y = i / 16;
                int j = 1 << y;
                int x = 8 * (i % 16) + row * 128;
                pixels[i + row * 128] = (byte) ((((lcdRcvBuffer.get(x) & j) > 0) ? 0 : 128)
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
        jPanel3.repaint(10);
    }
}
