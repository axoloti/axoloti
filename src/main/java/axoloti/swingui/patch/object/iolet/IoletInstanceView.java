package axoloti.swingui.patch.object.iolet;

import axoloti.abstractui.PatchView;
import axoloti.mvc.FocusEdit;
import axoloti.patch.PatchController;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.patch.net.NetDrag;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patch.net.NetDragging;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.inlet.InletInstanceView;
import axoloti.swingui.patch.object.outlet.OutletInstanceView;
import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public abstract class IoletInstanceView<T extends IoletInstance> extends ViewPanel<T> {

    protected final AxoObjectInstanceViewAbstract axoObj;
    protected LabelComponent label = new LabelComponent("");
    protected JComponent jack;

    private boolean saved_connected_state;

    public IoletInstanceView(T iolet, AxoObjectInstanceViewAbstract axoObj) {
        super(iolet);
        this.axoObj = axoObj;
        initComponents();
    }

    private void initComponents() {

        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                PatchViewSwing pv = getPatchView();
                if (pv == null) {
                    // probably in object selector
                    e.consume();
                    return;
                }
                if (e.isPopupTrigger()) {
                    getPopup().show(IoletInstanceView.this, 0, getHeight() - 1);
                    e.consume();
                } else {
                    setHighlighted(true);
                    //            if (!axoObj.isLocked()) {
                    //                if (dragnet == null) {
                    Net dnet = new NetDrag();
                    NetController dragNetController = dnet.getController();
                    dragtarget = null;
                    saved_connected_state = model.getConnected();
                    if (IoletInstanceView.this instanceof InletInstanceView) {
                        dragNetController.connectInlet((InletInstance) getDModel());
                    } else {
                        dragNetController.connectOutlet((OutletInstance) getDModel());
                    }
                    dragnet = new NetDragging(dnet, getPatchView());
                    dragNetController.addView(dragnet);
                    //                }
                    dragnet.setVisible(true);
                    getPatchView().selectionRectLayerPanel.removeAll();
                    getPatchView().selectionRectLayerPanel.add(dragnet);
                    e.consume();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                PatchViewSwing pv = getPatchView();
                if (pv == null) {
                    // probably in object selector
                    e.consume();
                    return;
                }
                if (e.isPopupTrigger()) {
                    getPopup().show(IoletInstanceView.this, 0, getHeight() - 1);
                    e.consume();
                }
                if (dragnet != null) {
                    dragnet.repaint();
                    pv.selectionRectLayerPanel.remove(dragnet);
                    getDModel().getController().changeConnected(saved_connected_state);
                    PatchController pc = pv.getDModel().getController();
                    dragnet = null;
                    if (dragtarget == null) {
                        Point p = SwingUtilities.convertPoint(IoletInstanceView.this, e.getPoint(), pv.selectionRectLayerPanel);
                        Component c = getPatchView().objectLayerPanel.findComponentAt(p);
                        while ((c != null) && !(c instanceof IoletInstanceView)) {
                            c = c.getParent();
                        }

                        if (IoletInstanceView.this != c) {
                            if (IoletInstanceView.this instanceof InletInstanceView) {
                                pc.addMetaUndo("disconnect inlet", focusEdit);
                                pc.disconnect((InletInstance) getDModel());
                            } else {
                                pc.addMetaUndo("disconnect outlet", focusEdit);
                                pc.disconnect((OutletInstance) getDModel());
                            }
                        }
                    } else {
                        if (IoletInstanceView.this instanceof InletInstanceView) {
                            if (dragtarget instanceof InletInstanceView) {
                                pc.addMetaUndo("connect", focusEdit);
                                pc.addConnection(
                                        (InletInstance) getDModel(),
                                        ((InletInstanceView) dragtarget).getDModel());
                            } else if (dragtarget instanceof OutletInstanceView) {
                                pc.addMetaUndo("connect", focusEdit);
                                pc.addConnection(
                                        (InletInstance) getDModel(),
                                        ((OutletInstanceView) dragtarget).getDModel());
                            }
                        } else if (IoletInstanceView.this instanceof OutletInstanceView) {
                            if (dragtarget instanceof InletInstanceView) {
                                pc.addMetaUndo("connect", focusEdit);
                                pc.addConnection(((InletInstanceView) dragtarget).getDModel(),
                                        ((OutletInstanceView) IoletInstanceView.this).getDModel());
                            }
                        }
                        pc.promoteOverloading(false);
                    }
                    pv.selectionRectLayerPanel.repaint();
                    e.consume();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setHighlighted(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHighlighted(false);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                PatchViewSwing pv = getPatchView();
                if (pv == null) {
                    // probably in object selector
                    e.consume();
                    return;
                }
                if (true) {
                    //!axoObj.isLocked()) {
                    Point p = SwingUtilities.convertPoint(IoletInstanceView.this, e.getPoint(), pv.objectLayerPanel);
                    Component c = pv.objectLayerPanel.findComponentAt(p);
                    while ((c != null) && !(c instanceof IoletInstanceView)) {
                        c = c.getParent();
                    }
                    if ((c != null)
                            && (c != IoletInstanceView.this)
                            && (!((IoletInstanceView.this instanceof OutletInstanceView) && (c instanceof OutletInstanceView)))) {
                        // different target and not myself?
                        if (c != dragtarget) {
                            // new target
                            dragtarget = (IoletInstanceView) c;
                            Point jackLocation = dragtarget.getJackLocInCanvas();
                            dragnet.setDragPoint(jackLocation);
                        }
                    } else if (dragnet != null) {
                        dragnet.setDragPoint(p);
                        dragtarget = null;
                    }
                }
                e.consume();
            }

        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public AxoObjectInstanceViewAbstract getObjectInstanceView() {
        return axoObj;
    }

    private Point getJackLocInCanvasHidden() {
        Point p1 = new Point(5, 5);
        Component p = (Component) jack;
        while (p != null) {
            p1.x += p.getX();
            p1.y += p.getY();
            if (p == axoObj) {
                break;
            }
            p = (Component) p.getParent();
        }
        return p1;
    }

    public Point getJackLocInCanvas() {
        try {
            PatchViewSwing pv = getPatchView();
            if (pv == null) {
                return getJackLocInCanvasHidden();
            }
            if (!axoObj.isValid()) {
                axoObj.validate();
            }
            return SwingUtilities.convertPoint(jack, 5, 5, pv.layers);
        } catch (IllegalComponentStateException e) {
            return getJackLocInCanvasHidden();
        } catch (NullPointerException e) {
            return getJackLocInCanvasHidden();
        }
    }

    abstract protected JPopupMenu getPopup();

    public PatchViewSwing getPatchView() {
        return axoObj.getPatchView();
    }

    private void scrollTo() {
        if (axoObj == null) {
            return;
        }
        PatchView pv = axoObj.getPatchView();
        if (pv == null) {
            return;
        }
        pv.scrollTo(this);
    }

    protected FocusEdit focusEdit = new FocusEdit() {

        @Override
        protected void focus() {
            scrollTo();
        }

    };

    protected NetDragging dragnet = null;
    protected IoletInstanceView dragtarget = null;


    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (InletInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
            axoObj.doLayout();
        } else if (InletInstance.DESCRIPTION.is(evt)) {
            String s = (String) evt.getNewValue();
            if ((s != null) && (s.isEmpty())) {
                s = null;
            }
            setToolTipText(s);
        }
    }

    public abstract void setHighlighted(boolean highlighted);

    @Override
    public T getDModel() {
        return model;
    }
}
