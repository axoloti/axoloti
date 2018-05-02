package axoloti.swingui.patch.object.iolet;

import axoloti.abstractui.PatchView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.FocusEdit;
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

public abstract class IoletInstanceView<T extends AbstractController> extends ViewPanel<T> {

    protected AxoObjectInstanceViewAbstract axoObj;
    protected LabelComponent label = new LabelComponent("");
    protected JComponent jack;

    public IoletInstanceView(T controller) {
        super(controller);
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
                    NetController dragNetController = dnet.getControllerFromModel();
                    dragtarget = null;
                    if (IoletInstanceView.this instanceof InletInstanceView) {
                        dragNetController.connectInlet((InletInstance) getController().getModel());
                    } else {
                        dragNetController.connectOutlet((OutletInstance) getController().getModel());
                    }
                    dragnet = new NetDragging(dragNetController, getPatchView());
                    dragNetController.addView(dragnet);
                    //                }
                    dragnet.setVisible(true);
                    if (getPatchView() != null) {
                        getPatchView().selectionRectLayerPanel.add(dragnet);
                    }
                    e.consume();
                    //            }
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
                } else if (dragnet != null) {
                    dragnet.repaint();
                    pv.selectionRectLayerPanel.remove(dragnet);
                    dragnet = null;
                    Net n = null;
                    if (dragtarget == null) {
                        Point p = SwingUtilities.convertPoint(IoletInstanceView.this, e.getPoint(), pv.selectionRectLayerPanel);
                        Component c = getPatchView().objectLayerPanel.findComponentAt(p);
                        while ((c != null) && !(c instanceof IoletInstanceView)) {
                            c = c.getParent();
                        }

                        if (IoletInstanceView.this != c) {
                            if (IoletInstanceView.this instanceof InletInstanceView) {
                                getPatchView().getController().addMetaUndo("disconnect inlet", focusEdit);
                                n = getPatchView().getController().disconnect((InletInstance) getController().getModel());
                            } else {
                                getPatchView().getController().addMetaUndo("disconnect outlet", focusEdit);
                                n = getPatchView().getController().disconnect((OutletInstance) getController().getModel());
                            }
                        }
                    } else {
                        if (IoletInstanceView.this instanceof InletInstanceView) {
                            if (dragtarget instanceof InletInstanceView) {
                                getPatchView().getController().addMetaUndo("connect", focusEdit);
                                n = getPatchView().getController().AddConnection(
                                        (InletInstance) getController().getModel(),
                                        (InletInstance) ((InletInstanceView) dragtarget).getController().getModel());
                            } else if (dragtarget instanceof OutletInstanceView) {
                                getPatchView().getController().addMetaUndo("connect", focusEdit);
                                n = getPatchView().getController().AddConnection(
                                        (InletInstance) getController().getModel(),
                                        (OutletInstance) ((OutletInstanceView) dragtarget).getController().getModel());
                            }
                        } else if (IoletInstanceView.this instanceof OutletInstanceView) {
                            if (dragtarget instanceof InletInstanceView) {
                                getPatchView().getController().addMetaUndo("connect", focusEdit);
                                n = getPatchView().getController().AddConnection((InletInstance) ((InletInstanceView) dragtarget).getController().getModel(),
                                        (OutletInstance) ((OutletInstanceView) IoletInstanceView.this).getController().getModel());
                            }
                        }
                        getPatchView().getController().PromoteOverloading(false);
                    }
                    getPatchView().selectionRectLayerPanel.repaint();
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
                if (!axoObj.isLocked()) {
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
            p1.x = p1.x + p.getX();
            p1.y = p1.y + p.getY();
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
            return SwingUtilities.convertPoint(jack, 5, 5, pv.Layers);
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

    public IoletInstance getModel() {
        return (IoletInstance) getController().getModel();
    }
}
