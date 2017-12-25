package axoloti.swingui.patch.object.iolet;

import axoloti.abstractui.INetView;
import axoloti.mvc.AbstractController;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.patch.net.NetDrag;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.swingui.TransparentCursor;
import axoloti.swingui.mvc.ViewPanel;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patch.net.NetDragging;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.inlet.InletInstanceView;
import axoloti.swingui.patch.object.outlet.OutletInstanceView;
import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.simpleframework.xml.Attribute;

public abstract class IoletAbstract<T extends AbstractController> extends ViewPanel<T> implements MouseListener, MouseMotionListener {

    @Deprecated
    @Attribute(required = false)
    public String name;
    @Attribute(name = "obj", required = false)
    public String objname;

    public AxoObjectInstanceViewAbstract axoObj;
    public JLabel lbl;
    public JComponent jack;

    public IoletAbstract(T controller) {
        super(controller);
    }

    @Deprecated
    public String getName() {
        return name;
    }

    public String getObjname() {
        if (objname != null) {
            return objname;
        } else {
            int sepIndex = name.lastIndexOf(' ');
            return name.substring(0, sepIndex);
        }
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
            PatchViewSwing p = getPatchView();
            if (p != null) {
                if (!axoObj.isValid()) axoObj.validate();
                return SwingUtilities.convertPoint(jack, 5, 5, getPatchView().Layers);
            } else {
                return getJackLocInCanvasHidden();
            }
        } catch (IllegalComponentStateException e) {
            return getJackLocInCanvasHidden();
        } catch (NullPointerException e) {
            return getJackLocInCanvasHidden();
        }
    }

    abstract public JPopupMenu getPopup();

    public PatchViewSwing getPatchView() {
        return axoObj.getPatchView();
    }

    NetDragging dragnet = null;
    IoletAbstract dragtarget = null;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            getPopup().show(IoletAbstract.this, 0, getHeight() - 1);
            e.consume();
        } else {
            setHighlighted(true);
//            if (!axoObj.isLocked()) {
//                if (dragnet == null) {
                    Net dnet = new NetDrag();
                    NetController dragNetController = new NetController(dnet, null, getPatchView().getController());
                    dragtarget = null;
                    if (this instanceof InletInstanceView) {
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
        if (e.isPopupTrigger()) {
            getPopup().show(this, 0, getHeight() - 1);
            e.consume();
        } else if ((dragnet != null) && (getPatchView() != null)) {
            dragnet.repaint();
            getPatchView().selectionRectLayerPanel.remove(dragnet);
            dragnet = null;
            Net n = null;
            if (dragtarget == null) {
                Point p = SwingUtilities.convertPoint(IoletAbstract.this, e.getPoint(), getPatchView().selectionRectLayerPanel);
                Component c = getPatchView().objectLayerPanel.findComponentAt(p);
                while ((c != null) && !(c instanceof IoletAbstract)) {
                    c = c.getParent();
                }

                if (this != c) {
                    if (IoletAbstract.this instanceof InletInstanceView) {
                        getPatchView().getController().addMetaUndo("disconnect inlet");
                        n = getPatchView().getController().disconnect((InletInstance) getController().getModel());
                    } else {
                        getPatchView().getController().addMetaUndo("disconnect outlet");
                        n = getPatchView().getController().disconnect((OutletInstance) getController().getModel());
                    }
                }
            } else {
                if (this instanceof InletInstanceView) {
                    if (dragtarget instanceof InletInstanceView) {
                        getPatchView().getController().addMetaUndo("connect");
                        n = getPatchView().getController().AddConnection(
                            (InletInstance) getController().getModel(),
                            (InletInstance) ((InletInstanceView) dragtarget).getController().getModel());
                    } else if (dragtarget instanceof OutletInstanceView) {
                        getPatchView().getController().addMetaUndo("connect");
                        n = getPatchView().getController().AddConnection(
                            (InletInstance) getController().getModel(),
                            (OutletInstance) ((OutletInstanceView) dragtarget).getController().getModel());
                    }
                } else if (this instanceof OutletInstanceView) {
                    if (dragtarget instanceof InletInstanceView) {
                        getPatchView().getController().addMetaUndo("connect");
                        n = getPatchView().getController().AddConnection(
                            (InletInstance) ((InletInstanceView) dragtarget).getController().getModel(),
                            (OutletInstance) ((OutletInstanceView) IoletAbstract.this).getController().getModel());
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
        if (!axoObj.isLocked()) {
            Point p = SwingUtilities.convertPoint(IoletAbstract.this, e.getPoint(), getPatchView().objectLayerPanel);
            Component c = getPatchView().objectLayerPanel.findComponentAt(p);
            while ((c != null) && !(c instanceof IoletAbstract)) {
                c = c.getParent();
            }
            if ((c != null)
                    && (c != this)
                    && (!((this instanceof OutletInstanceView) && (c instanceof OutletInstanceView)))) {
                // different target and not myself?
                if (c != dragtarget) {
                    // new target
                    dragtarget = (IoletAbstract) c;
                    Point jackLocation = dragtarget.getJackLocInCanvas();
                    dragnet.SetDragPoint(jackLocation);
                }
            } else if (dragnet != null) {
                dragnet.SetDragPoint(p);
                dragtarget = null;
            }
        }
        e.consume();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public abstract void setHighlighted(boolean highlighted);

    public IoletInstance getModel() {
        return (IoletInstance) getController().getModel();
    }
}
