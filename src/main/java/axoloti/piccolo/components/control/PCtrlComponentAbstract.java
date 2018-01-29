package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.piccolo.components.PFocusable;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_BEGIN;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE_ADJ_END;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventListener;


public abstract class PCtrlComponentAbstract extends PatchPNode implements PFocusable {

    protected IAxoObjectInstanceView axoObjectInstanceView;
    protected PInputEventListener inputEventListener = new PBasicInputEventHandler() {
        @Override
        public void keyboardFocusGained(PInputEvent event) {
            PCtrlComponentAbstract.this.keyboardFocusGained(event);
            repaint();
        }

        @Override
        public void keyboardFocusLost(PInputEvent event) {
            PCtrlComponentAbstract.this.keyboardFocusLost(event);
            repaint();
        }

        @Override
        public void mousePressed(PInputEvent event) {
            if (!event.isMiddleMouseButton()) {
                PCtrlComponentAbstract.this.mousePressed(event);
            }
        }

        @Override
        public void mouseReleased(PInputEvent event) {
            if (!event.isMiddleMouseButton()) {
                PCtrlComponentAbstract.this.mouseReleased(event);
            }
        }

        @Override
        public void mouseDragged(PInputEvent event) {
            if (!event.isMiddleMouseButton()) {
                event.getInputManager().setMouseFocus(event.getPath());
                PCtrlComponentAbstract.this.mouseDragged(event);
            }
        }

        @Override
        public void keyTyped(PInputEvent event) {
            PCtrlComponentAbstract.this.keyTyped(event);
        }

        @Override
        public void keyPressed(PInputEvent event) {
            PCtrlComponentAbstract.this.keyPressed(event);
            if (event.getKeyCode() == KeyEvent.VK_TAB) {
                getPatchViewPiccolo().transferFocus(PCtrlComponentAbstract.this);
            }
        }

        @Override
        public void keyReleased(PInputEvent event) {
            PCtrlComponentAbstract.this.keyReleased(event);

        }

        @Override
        public void mouseEntered(PInputEvent event) {
            if (event.getInputManager().getMouseFocus() == null) {
                axoObjectInstanceView.getCanvas().setToolTipText(getToolTipText());
                PCtrlComponentAbstract.this.mouseEntered(event);
            }
        }

        @Override
        public void mouseExited(PInputEvent event) {
            if (event.getInputManager().getMouseFocus() == null) {
                axoObjectInstanceView.getCanvas().setToolTipText(null);
                PCtrlComponentAbstract.this.mouseExited(event);
            }
        }

        @Override
        public void mouseMoved(PInputEvent event) {
            if (event.getInputManager().getMouseFocus() == null) {
                PCtrlComponentAbstract.this.mouseMoved(event);
            }
        }

        @Override
        public void mouseClicked(PInputEvent event) {
            if (!event.isMiddleMouseButton()) {
                PCtrlComponentAbstract.this.mouseClicked(event);
                grabFocus();
            }
        }
    };

    private PatchViewPiccolo getPatchViewPiccolo() {
        return (PatchViewPiccolo) axoObjectInstanceView.getPatchView();
    }

    public PCtrlComponentAbstract(IAxoObjectInstanceView view) {
        super(view.getPatchView());
        this.axoObjectInstanceView = view;
        addInputEventListener(inputEventListener);
    }

    @Override
    public void grabFocus() {
        if (getRoot() != null && getRoot().getDefaultInputManager() != null) {
            getRoot().getDefaultInputManager().setKeyboardFocus(inputEventListener);
            getPatchViewPiccolo().setFocusedCtrl(this);
        }
    }

    public boolean isFocusOwner() {
        return getRoot().getDefaultInputManager().getKeyboardFocus() == inputEventListener;
    }

    abstract public double getValue();

    abstract public void setValue(double value);

    void mouseDragged(PInputEvent e) {

    }

    void mousePressed(PInputEvent e) {

    }

    void mouseReleased(PInputEvent e) {

    }

    void mouseEntered(PInputEvent e) {

    }

    void mouseExited(PInputEvent e) {

    }

    void mouseMoved(PInputEvent e) {

    }

    void mouseClicked(PInputEvent e) {

    }

    void keyPressed(PInputEvent ke) {

    }

    void keyReleased(PInputEvent ke) {

    }

    void keyTyped(PInputEvent ke) {

    }

    void keyboardFocusGained(PInputEvent event) {
    }

    void keyboardFocusLost(PInputEvent event) {
    }

    List<PCtrlListener> listenerList = new ArrayList<>();

    public void addPCtrlListener(PCtrlListener listener) {
        listenerList.add(listener);
    }

    public void removeACtrlListener(PCtrlListener listener) {
        listenerList.remove(listener);
    }

    void fireEvent() {
        for (PCtrlListener listener : listenerList) {
            listener.PCtrlAdjusted(new PCtrlEvent(this, getValue()));
        }
        firePropertyChange(0, PROP_VALUE, null, (Double) getValue());
    }

    void fireEventAdjustmentBegin() {
        for (PCtrlListener listener : listenerList) {
            listener.PCtrlAdjustmentBegin(new PCtrlEvent(this, getValue()));
        }
        firePropertyChange(0, PROP_VALUE_ADJ_BEGIN, null, null);
    }

    void fireEventAdjustmentFinished() {
        for (PCtrlListener listener : listenerList) {
            listener.PCtrlAdjustmentFinished(new PCtrlEvent(this, getValue()));
        }
        firePropertyChange(0, PROP_VALUE_ADJ_END, null, null);
    }

    public void robotMoveToCenter(PInputEvent e) {
    }

    private JComponent presetCanvas;

    public void setPresetCanvas(JComponent presetCanvas) {
        this.presetCanvas = presetCanvas;
    }

    protected JComponent getCanvas() {
        if (presetCanvas != null) {
            return presetCanvas;
        } else {
            return axoObjectInstanceView.getCanvas();
        }
    }

    private int focusableIndex;

    @Override
    public void setFocusableIndex(int index) {
        focusableIndex = index;
    }

    @Override
    public int getFocusableIndex() {
        return focusableIndex;
    }
}
