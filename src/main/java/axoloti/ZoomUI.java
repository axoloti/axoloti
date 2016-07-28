package axoloti;

import axoloti.object.AxoObjectInstance;
import axoloti.object.TitleBarPanel;
import axoloti.utils.Constants;
import axoloti.utils.KeyUtils;
import components.JackInputComponent;
import components.JackOutputComponent;
import components.LabelComponent;
import components.control.ACtrlComponent;
import components.control.DialComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.LayerUI;

public class ZoomUI extends LayerUI<JComponent> {

    public static final String ZOOM_OUT_CHANGE_MESSAGE = "zoomOut";
    public static final String ZOOM_IN_CHANGE_MESSAGE = "zoomIn";
    private final double zoomAmount;
    private final double zoomMin;
    private final double zoomMax;
    private final double startingScale;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private double zoom = 1;
    private Component previous = null;
    private Component previousDial = null;

    private boolean dragging = false;
    private Component dragged;

    private final PatchGUI patch;

    private boolean anyMouseDown = false;
    private boolean button2down = false;

    public ZoomUI(double startingScale, double zoomAmount, double zoomMax, double zoomMin,
            PatchGUI patch) {
        zoom = startingScale;
        this.startingScale = startingScale;
        this.zoomAmount = zoomAmount;
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
        this.patch = patch;
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }

    private final Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHints(hints);
        g2.scale(zoom, zoom);
        super.paint(g2, c);
    }

    private Component getTargetComponent(Component component) {
        if (component instanceof JackOutputComponent) {
            component = ((JackOutputComponent) component).getOutlet();
        }
        if (component instanceof JackInputComponent) {
            component = ((JackInputComponent) component).getInlet();
        }
        if ((component != null
                && component.getName() != null
                && component.getName().equals(Constants.OBJECT_LAYER_PANEL))
                || patch.selectionrectangle.isVisible()
                || button2down) {
            component = patch.Layers;
        }
        if (component instanceof LabelComponent && component.getParent() instanceof TitleBarPanel) {
            component = component.getParent();
        }
        return component;
    }

    private void handleEnterExited(Component component, MouseEvent localEvent) {
        if (component != previous) {
            if (previous != null) {
                for (MouseListener listener : previous.getListeners(MouseListener.class)) {
                    listener.mouseExited(getNewMouseEvent(component, localEvent, MouseEvent.MOUSE_EXITED));
                }
            }
            for (MouseListener listener : component.getListeners(MouseListener.class)) {
                listener.mouseEntered(getNewMouseEvent(component, localEvent, MouseEvent.MOUSE_ENTERED));
            }
        }
    }

    private void dispatchMouseEvent(MouseListener[] mouseListeners, MouseEvent localEvent, MouseEvent transformedEvent) {
        for (MouseListener listener : mouseListeners) {
            if (localEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                listener.mousePressed(transformedEvent);
                anyMouseDown = true;
            } else if (localEvent.getID() == MouseEvent.MOUSE_RELEASED) {
                ZoomUI.this.button2down = false;
                anyMouseDown = false;
                dragging = false;
                patch.patchframe.getRootPane().setCursor(Cursor.getDefaultCursor());
                listener.mouseReleased(transformedEvent);
            } else if (localEvent.getID() == MouseEvent.MOUSE_CLICKED) {
                listener.mouseClicked(transformedEvent);
            }
        }
        if (localEvent.getID() == MouseEvent.MOUSE_PRESSED
                && transformedEvent.getComponent() instanceof DialComponent) {
            anyMouseDown = true;
            // ensure that dial focus highlights are repainted
            if (previousDial != null
                    && previousDial != transformedEvent.getComponent()) {
                previousDial.repaint();
                ZoomUtils.paintObjectLayer(previousDial);
            }
            if (transformedEvent.getComponent() instanceof DialComponent) {
                previousDial = transformedEvent.getComponent();
            }
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JComponent> l) {
        MouseEvent layerEvent = translateToLayerCoordinates(e, l);
        Component component = getTargetComponent(getComponentClickedOn(layerEvent));

        if (component != null) {
            Container axoObjectParent = SwingUtilities.getAncestorOfClass(AxoObjectInstance.class, component);
            MouseEvent localEvent = translateToComponentCoordinates(layerEvent, component);

            MouseListener[] mouseListeners = component.getListeners(MouseListener.class);
            MouseEvent transformedEvent = getNewMouseEvent(component, localEvent);

            if (localEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                if (localEvent.getButton() == MouseEvent.BUTTON2) {
                    ZoomUI.this.button2down = true;
                    layerEvent = translateToLayerCoordinates(e, l);
                    component = getTargetComponent(getComponentClickedOn(layerEvent));
                    localEvent = translateToComponentCoordinates(layerEvent, component);
                    mouseListeners = component.getListeners(MouseListener.class);
                    transformedEvent = getNewMouseEvent(component, localEvent);
                }

                for (ActionListener listener : component.getListeners(ActionListener.class)) {
                    ActionEvent actionEvent = new ActionEvent(component, localEvent.getID(), "CLICK");
                    listener.actionPerformed(actionEvent);
                }
            }

            dispatchMouseEvent(mouseListeners, localEvent, transformedEvent);

            if (mouseListeners.length == 0 && axoObjectParent != null) {
                layerEvent = translateToLayerCoordinates(e, l);
                localEvent = translateToComponentCoordinates(layerEvent, axoObjectParent);
                transformedEvent = getNewMouseEvent(axoObjectParent, localEvent);

                mouseListeners = axoObjectParent.getListeners(MouseListener.class);

                dispatchMouseEvent(mouseListeners, localEvent, transformedEvent);
            }

            this.handleEnterExited(component, localEvent);

            previous = component;
            e.consume();
            if (!button2down) {
                ZoomUtils.paintObjectLayer(l,
                        axoObjectParent != null ? axoObjectParent : component,
                        this, false);
            }
        }

        if (component == null) {
            // avoid transparent cursor if release occurs outside patch bounds
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                anyMouseDown = false;
                patch.patchframe.getRootPane().setCursor(Cursor.getDefaultCursor());

            }
            // avoid escaping patch bounds during robot drag
            if (dragging
                    && e.getID() == MouseEvent.MOUSE_EXITED
                    && dragged != null
                    && dragged instanceof ACtrlComponent) {
                ((ACtrlComponent) dragged).robotMoveToCenter();
            }
        }
    }

    private void dispatchMotionToAxoObjectParent(Container axoObjectParent, MouseEvent localEvent) {
        MouseMotionListener[] listeners = axoObjectParent.getListeners(MouseMotionListener.class);
        localEvent = translateToComponentCoordinates(localEvent, axoObjectParent);
        for (MouseMotionListener listener : listeners) {
            if (localEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
                listener.mouseDragged(getNewMouseEvent(axoObjectParent, localEvent));
            } else if (localEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
                listener.mouseMoved(getNewMouseEvent(axoObjectParent, localEvent));

            }
        }
    }

    private void dispatchMouseMotionEvent(MouseMotionListener[] motionListeners, MouseEvent localEvent, Component component) {
        for (MouseMotionListener listener : motionListeners) {
            if (localEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
                listener.mouseDragged(getNewMouseEvent(component, localEvent));
            } else if (localEvent.getID() == MouseEvent.MOUSE_MOVED) {
                listener.mouseMoved(getNewMouseEvent(component, localEvent));
            }
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JComponent> l) {
        MouseEvent localEvent = translateToLayerCoordinates(e, l);
        Component component = getTargetComponent(getComponentClickedOn(localEvent));
        if (component != null) {
            Container axoObjectParent = SwingUtilities.getAncestorOfClass(AxoObjectInstance.class, component);

            localEvent = translateToComponentCoordinates(localEvent, component);
            if (!dragging && localEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                dragged = component;
            }

            Component listenerComponent = component;
            if (dragging) {
                listenerComponent = dragged;
            }
            MouseMotionListener[] listeners = listenerComponent.getListeners(MouseMotionListener.class);

            dispatchMouseMotionEvent(listeners, localEvent, listenerComponent);

            if (listeners.length == 0 && axoObjectParent != null) {
                localEvent = translateToLayerCoordinates(e, l);
                dispatchMotionToAxoObjectParent(axoObjectParent, localEvent);
                if (dragging) {
                    dragged = axoObjectParent;
                }
            }

            this.handleEnterExited(component, localEvent);
            previous = component;

            e.consume();
            if (!button2down) {
                ZoomUtils.paintObjectLayer(l,
                        axoObjectParent != null ? axoObjectParent : component,
                        this, false);
            }
        }
    }

    @Override
    protected void processKeyEvent(KeyEvent ke,
            JLayer<? extends JComponent> l) {
        if(KeyUtils.isKeyCodeControlOrCommand(ke) && 
                !anyMouseDown) {
            KeyListener[] listeners = patch.Layers.getListeners(KeyListener.class);
            for(KeyListener kl : listeners) {
                if(ke.getID() == KeyEvent.KEY_PRESSED) {
                    kl.keyPressed(getNewKeyEvent(ke));
                }
                else if(ke.getID() == KeyEvent.KEY_RELEASED) {
                    kl.keyReleased(getNewKeyEvent(ke));
                }
            }
        }
        ZoomUtils.paintObjectLayer(l, ke.getComponent(), this);
    }

    @Override
    protected void processFocusEvent(FocusEvent e,
            JLayer<? extends JComponent> l) {
        ZoomUtils.paintObjectLayer(l, e.getComponent(), this);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer<? extends JComponent> jlayer = (JLayer<? extends JComponent>) c;
        jlayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK
                | AWTEvent.KEY_EVENT_MASK
                | AWTEvent.FOCUS_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer<? extends JComponent> jlayer = (JLayer<? extends JComponent>) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void zoomOut() {
        if (canZoomOut()) {
            double old = zoom;
            zoom -= zoomAmount;
            changeSupport.firePropertyChange(ZOOM_OUT_CHANGE_MESSAGE, old, zoom);
        }
    }

    private Component getComponentClickedOn(MouseEvent e) {
        Point coordinates = zoomedXY(e);
        Component clickedOn = patch.draggedObjectLayerPanel.findComponentAt(coordinates.x, coordinates.y);
        if (clickedOn == null
                || (clickedOn.getName() != null
                && clickedOn.getName().equals(Constants.DRAGGED_OBJECT_LAYER_PANEL))) {
            clickedOn = patch.objectLayerPanel.findComponentAt(coordinates.x, coordinates.y);
        }
        return clickedOn;
    }

    private Point zoomedXY(MouseEvent e) {
        return new Point(removeZoomFactor(e.getX()), removeZoomFactor(e.getY()));
    }

    public int removeZoomFactor(int x) {
        return (int) Math.round(x / zoom);
    }

    public void scale(Rectangle r) {
        r.x = scale(r.x);
        r.y = scale(r.y);
        r.width = scale(r.width);
        r.height = scale(r.height);
    }

    public int scale(int x) {
        return (int) Math.round(x * zoom);
    }

    public Point removeZoomFactor(Point p) {
        p.x = (int) Math.round(p.x / zoom);
        p.y = (int) Math.round(p.y / zoom);
        return p;
    }

    private MouseEvent removeZoom(MouseEvent m) {
        m.translatePoint(removeZoomFactor(m.getX()) - m.getX(), removeZoomFactor(m.getY()) - m.getY());
        return m;
    }

    private MouseEvent translateToLayerCoordinates(MouseEvent e, JLayer<? extends JComponent> layer) {
        return SwingUtilities.convertMouseEvent(e.getComponent(), e, layer);
    }

    private MouseEvent translateToComponentCoordinates(MouseEvent e, Component component) {
        return SwingUtilities.convertMouseEvent(e.getComponent(), removeZoom(e), component);
    }

    private MouseEvent getNewMouseEvent(Component component, MouseEvent mouseEvent) {
        return getNewMouseEvent(component, mouseEvent, mouseEvent.getID());
    }

    private MouseEvent getNewMouseEvent(Component component, MouseEvent mouseEvent, int eventId) {
        return new MouseEvent(component, eventId, mouseEvent.getWhen(), mouseEvent.getModifiers(),
                mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(),
                mouseEvent.getButton());
    }
    
    private KeyEvent getNewKeyEvent(KeyEvent keyEvent) {
        return new KeyEvent(patch.Layers, keyEvent.getID(), keyEvent.getWhen(), keyEvent.getModifiers(), keyEvent.getKeyCode(), keyEvent.getKeyChar());
    }

    public double getScale() {
        return zoom;
    }

    public boolean canZoomOut() {
        return zoom - zoomAmount >= zoomMin;
    }

    public void cancelDrag() {
        dragging = false;
    }

    public void zoomIn() {
        if (canZoomIn()) {
            double old = zoom;
            zoom += zoomAmount;
            changeSupport.firePropertyChange(ZOOM_IN_CHANGE_MESSAGE, old, zoom);
        }
    }

    public void zoomToDefault() {
        double old = zoom;
        zoom = startingScale;
        changeSupport.firePropertyChange(ZOOM_IN_CHANGE_MESSAGE, old, zoom);
    }

    public boolean canZoomIn() {
        return zoom + zoomAmount <= zoomMax;
    }

    public void startPan() {
        this.button2down = true;
    }

    public void stopPan() {
        this.button2down = false;
    }
}
