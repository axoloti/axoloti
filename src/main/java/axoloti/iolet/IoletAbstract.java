package axoloti.iolet;

import axoloti.INetView;
import axoloti.MainFrame;
import axoloti.Net;
import axoloti.NetDragging;
import axoloti.PatchModel;
import axoloti.PatchViewSwing;
import axoloti.inlets.IInletInstanceView;
import axoloti.inlets.InletInstanceView;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstanceView;
import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.simpleframework.xml.Attribute;

public abstract class IoletAbstract extends JPanel implements MouseListener, MouseMotionListener, IIoletAbstract {

    @Deprecated
    @Attribute(required = false)
    public String name;
    @Attribute(name = "obj", required = false)
    public String objname;

    public AxoObjectInstanceViewAbstract axoObj;
    public JLabel lbl;
    public JComponent jack;

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

    public PatchModel getPatchModel() {
        return axoObj.getPatchModel();
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
            if (!axoObj.isLocked()) {
                if (dragnet == null) {
                    dragnet = new NetDragging(getPatchView());
                    dragtarget = null;
                    if (this instanceof InletInstanceView) {
                        dragnet.connectInlet((IInletInstanceView) this);
                    } else {
                        dragnet.connectOutlet((IOutletInstanceView) this);
                    }
                }
                dragnet.setVisible(true);
                if (getPatchView() != null) {
                    getPatchView().selectionRectLayerPanel.add(dragnet);
                }
                e.consume();
            }
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
                        n = getPatchView().getPatchController().disconnect((InletInstanceView) IoletAbstract.this);
                    } else {
                        n = getPatchView().getPatchController().disconnect((OutletInstanceView) IoletAbstract.this);
                    }
                }
            } else {
                if (this instanceof InletInstanceView) {
                    if (dragtarget instanceof InletInstanceView) {
                        n = getPatchView().getPatchController().AddConnection(((InletInstanceView) IoletAbstract.this), ((InletInstanceView) dragtarget));
                    } else if (dragtarget instanceof OutletInstanceView) {
                        n = getPatchView().getPatchController().AddConnection(((InletInstanceView) IoletAbstract.this), ((OutletInstanceView) dragtarget));
                    }
                } else if (this instanceof OutletInstanceView) {
                    if (dragtarget instanceof InletInstanceView) {
                        n = getPatchView().getPatchController().AddConnection(((InletInstanceView) dragtarget), ((OutletInstanceView) IoletAbstract.this));
                    }
                }
                if (axoObj.getPatchModel().PromoteOverloading(false)) {
                    getPatchView().getPatchController().popUndoState();
                    getPatchView().getPatchController().pushUndoState();
                }
            }
            if (n != null) {
                getPatchModel().setDirty();
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

    public void setHighlighted(boolean highlighted) {
        if ((getRootPane() == null
                || getRootPane().getCursor() != MainFrame.transparentCursor)
                && axoObj != null
                && axoObj.getPatchView() != null) {
            INetView netView = axoObj.getPatchView().GetNetView(this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }
}
