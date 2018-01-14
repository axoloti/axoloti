package axoloti.piccolo.iolet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.net.Net;
import axoloti.patch.PatchModel;
import axoloti.patch.PatchViewPiccolo;
import axoloti.piccolo.PNetDragging;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.PatchPNode;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.inlets.PInletInstanceView;
import axoloti.piccolo.outlets.POutletInstanceView;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JPopupMenu;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;

public abstract class PIoletAbstract extends PatchPNode {

    public IAxoObjectInstanceView axoObjectInstanceView;
    public PLabelComponent lbl;
    public PatchPNode jack;

    public PIoletAbstract(IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView.getPatchView());
        this.axoObjectInstanceView = axoObjectInstanceView;
        PInputEventFilter eventFilter = new PInputEventFilter();
        eventFilter.setOrMask(InputEvent.BUTTON1_MASK
                | InputEvent.BUTTON3_MASK);
        this.inputEventListener.setEventFilter(eventFilter);
    }

    public IAxoObjectInstanceView getObjectInstanceView() {
        return axoObjectInstanceView;
    }

    private final Stroke stroke = new BasicStroke(1f);

    public Point getJackLocInCanvas() {
        Point2D p = localToGlobal(jack.getFullBounds().getCenter2D());
        return new Point((int) p.getX(), (int) p.getY());
    }

    abstract public JPopupMenu getPopup();

    public PatchViewPiccolo getPatchView() {
        return (PatchViewPiccolo) axoObjectInstanceView.getPatchView();
    }

    public PatchModel getPatchModel() {
        return axoObjectInstanceView.getPatchModel();
    }

    PNetDragging dragnet = null;
    PIoletAbstract dragtarget = null;

    private PBasicInputEventHandler inputEventListener = new PBasicInputEventHandler() {

        @Override
        public void mousePressed(PInputEvent e) {
            if (e.isPopupTrigger()) {
                Point popupLocation = PUtils.getPopupLocation(e);
                getPopup().show(PIoletAbstract.this.getPatchView().getCanvas(),
                        popupLocation.x,
                        popupLocation.y);
                e.setHandled(true);
            } else {
                setHighlighted(true);
                if (!axoObjectInstanceView.isLocked()) {
                    if (dragnet == null) {
                        dragnet = new PNetDragging(getPatchView());
                        dragtarget = null;
                        if (PIoletAbstract.this instanceof PInletInstanceView) {
                            //dragnet.connectInlet((IInletInstanceView) PIoletAbstract.this);
                        } else {
                            //dragnet.connectOutlet((IOutletInstanceView) PIoletAbstract.this);
                        }
                    }
                    dragnet.setVisible(true);
                    if (getPatchView() != null) {
                        getPatchView().getCanvas().getLayer().addChild(dragnet);
                    }
                    e.setHandled(true);
                }
            }
        }

        @Override
        public void mouseReleased(PInputEvent e) {
            if (e.isPopupTrigger()) {
                Point popupLocation = PUtils.getPopupLocation(e);
                getPopup().show(PIoletAbstract.this.getPatchView().getCanvas(),
                        popupLocation.x,
                        popupLocation.y);
                e.setHandled(true);
            } else if ((dragnet != null) && (getPatchView() != null)) {
                dragnet.repaint();
                getPatchView().getCanvas().getLayer().removeChild(dragnet);
                dragnet = null;
                Net n = null;
                if (dragtarget == null) {
                    if (!PIoletAbstract.this.getBoundsReference().contains(globalToLocal(e.getPosition()))) {
                        if (PIoletAbstract.this instanceof PInletInstanceView) {
                            //n = getPatchView().getController().disconnect((PInletInstanceView) PIoletAbstract.this);
                        } else {
                            //n = getPatchView().getController().disconnect((POutletInstanceView) PIoletAbstract.this);
                        }
                    }
                } else {
                    if (PIoletAbstract.this instanceof PInletInstanceView) {
                        if (dragtarget instanceof PInletInstanceView) {
                            //n = getPatchView().getController().AddConnection(((PInletInstanceView) PIoletAbstract.this), ((PInletInstanceView) dragtarget));
                        } else if (dragtarget instanceof POutletInstanceView) {
                            //n = getPatchView().getController().AddConnection(((PInletInstanceView) PIoletAbstract.this), ((POutletInstanceView) dragtarget));
                        }
                    } else if (PIoletAbstract.this instanceof POutletInstanceView) {
                        if (dragtarget instanceof PInletInstanceView) {
                            //n = getPatchView().getController().AddConnection(((PInletInstanceView) dragtarget), ((POutletInstanceView) PIoletAbstract.this));
                        }
                    }
//                    if (axoObjectInstanceView.getPatchModel().PromoteOverloading(false)) {
//                        getPatchView().getPatchController().popUndoState();
//                        getPatchView().getPatchController().pushUndoState();
//                    }
                    dragtarget.repaint();
                }
                if (n != null) {
                }
                PIoletAbstract.this.repaint();
                e.setHandled(true);
            }
        }

        @Override
        public void mouseEntered(PInputEvent e) {
            if (e.getInputManager().getMouseFocus() == null) {
                setHighlighted(true);
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            if (e.getInputManager().getMouseFocus() == null) {
                setHighlighted(false);
            }
        }

        @Override
        public void mouseDragged(PInputEvent e) {
            if (!axoObjectInstanceView.isLocked()) {

                Point2D p = e.getPosition();

                ArrayList intersectingNodes = new ArrayList<>();
                getPatchView().getCanvas().getLayer().findIntersectingNodes(new Rectangle((int) p.getX(), (int) p.getY(), 1, 1), intersectingNodes);
                PNode c = null;
                for (Object o : intersectingNodes) {
                    PNode node = (PNode) o;
                    if (o instanceof PIoletAbstract) {
                        c = node;
                    }
                }

                if ((c != null)
                        && (c != PIoletAbstract.this)
                        && (!((PIoletAbstract.this instanceof POutletInstanceView) && (c instanceof POutletInstanceView)))) {
                    // different target and not myself?
                    if (c != dragtarget) {
                        // new target
                        dragtarget = (PIoletAbstract) c;
                        Point jackLocation = dragtarget.getJackLocInCanvas();
                        dragnet.SetDragPoint(jackLocation);
                    }
                } else // floating
                {
                    if (dragnet != null) {
                        dragnet.SetDragPoint(new Point((int) p.getX(), (int) p.getY()));
                        dragtarget = null;
                    }
                }
            }
            e.setHandled(true);
        }
    };

    public abstract void setHighlighted(boolean highlighted);

    public PBasicInputEventHandler getInputEventHandler() {
        return inputEventListener;
    }
}
