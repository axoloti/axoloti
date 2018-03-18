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
package components.control;

import axoloti.object.AxoObjectInstance;
import axoloti.utils.KeyUtils;
import axoloti.utils.Preferences;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

    protected AxoObjectInstance axoObj;
    protected Color customBackgroundColor;

    public ACtrlComponent() {
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
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

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

    public void addACtrlListener(ACtrlListener listener) {
        listenerList.add(ACtrlListener.class, listener);
    }

    public void removeACtrlListener(ACtrlListener listener) {
        listenerList.remove(ACtrlListener.class, listener);
    }

    void fireEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ACtrlListener.class) {
                ((ACtrlListener) listeners[i + 1]).ACtrlAdjusted(
                        new ACtrlEvent(this, getValue()));
            }
        }
    }

    void fireEventAdjustmentBegin() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ACtrlListener.class) {
                ((ACtrlListener) listeners[i + 1]).ACtrlAdjustmentBegin(
                        new ACtrlEvent(this, getValue()));
            }
        }
    }

    void fireEventAdjustmentFinished() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ACtrlListener.class) {
                ((ACtrlListener) listeners[i + 1]).ACtrlAdjustmentFinished(
                        new ACtrlEvent(this, getValue()));
            }
        }
    }

    void SetupTransferHandler() {
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
            public boolean importData(TransferHandler.TransferSupport support) {
                return super.importData(support);
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

    public void setParentAxoObjectInstance(AxoObjectInstance axoObj) {
        this.axoObj = axoObj;
    }

    Robot createRobot() {
        try {
            if (Preferences.LoadPreferences().getMouseDoNotRecenterWhenAdjustingControls()) {
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

    public void setCustomBackgroundColor(Color c) {
        this.customBackgroundColor = c;
    }
}
