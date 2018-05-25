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
package axoloti.swingui.components.control;

import axoloti.preferences.Preferences;
import axoloti.utils.KeyUtils;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ACtrlComponent extends JComponent {

    public final static String PROP_VALUE_ADJ_BEGIN = "prop_value_begin";
    public final static String PROP_VALUE_ADJ_END = "prop_value_end";
    public final static String PROP_VALUE = "prop_value";

    protected Color customBackgroundColor = null;

    public ACtrlComponent() {
        super();
        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent fe) {
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ACtrlComponent.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ACtrlComponent.this.mouseReleased(e);
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                ACtrlComponent.this.mouseDragged(e);
            }
        });
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                ACtrlComponent.this.keyPressed(ke);
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                ACtrlComponent.this.keyReleased(ke);
            }
        });
    }

    abstract public double getValue();

    abstract public void setValue(double value);

    abstract void mouseDragged(MouseEvent e);

    abstract void mousePressed(MouseEvent e);

    abstract void mouseReleased(MouseEvent e);

    abstract void keyPressed(KeyEvent ke);

    abstract void keyReleased(KeyEvent ke);

    void fireEvent() {
        firePropertyChange(PROP_VALUE, null, (Double) getValue());
    }

    void fireEventAdjustmentBegin() {
        firePropertyChange(PROP_VALUE_ADJ_BEGIN, null, null);
    }

    void fireEventAdjustmentFinished() {
        firePropertyChange(PROP_VALUE_ADJ_END, null, null);
    }

    void setupTransferHandler() {
        TransferHandler TH = new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY_OR_MOVE;
            }

            @Override
            public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
                System.out.println("export to clip " + Double.toString(getValue()));
                clip.setContents(new StringSelection(Double.toString(getValue())), (ClipboardOwner) null);
            }

            @Override
            public boolean importData(JComponent comp, Transferable t) {
                if (isEnabled()) {
                    try {
                        String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                        System.out.println("paste on control: " + s);
                        setValue(Double.parseDouble(s));
                    } catch (UnsupportedFlavorException ex) {
                        Logger.getLogger(NumberBoxComponent.class.getName()).log(Level.SEVERE, "paste", ex);
                    } catch (IOException ex) {
                        Logger.getLogger(NumberBoxComponent.class.getName()).log(Level.SEVERE, "paste", ex);
                    }
                    return true;
                }
                return false;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                System.out.println("createTransferable");
                return new StringSelection("copy");
            }
        };
        setTransferHandler(TH);
        InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                KeyUtils.CONTROL_OR_CMD_MASK), "cut");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                KeyUtils.CONTROL_OR_CMD_MASK), "copy");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                KeyUtils.CONTROL_OR_CMD_MASK), "paste");

        ActionMap map = getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

    }

    Robot createRobot() {
        try {
            if (Preferences.getPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
                return null;
            } else {
                return new Robot(MouseInfo.getPointerInfo().getDevice());
            }
        } catch (AWTException ex) {
            Logger.getLogger(NumberBoxComponent.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void robotMoveToCenter() {

    }
}
