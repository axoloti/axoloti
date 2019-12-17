package axoloti.piccolo.patch.object.iolet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.PatchModel;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.patch.net.NetDrag;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.piccolo.patch.net.PNetDragging;
import axoloti.piccolo.patch.object.inlet.PInletInstanceView;
import axoloti.piccolo.patch.object.outlet.POutletInstanceView;
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
    public PatchPNode jack;

// TODO: piccolo implement cfr. axoloti.swingui.patch.object.iolet.IoletInstanceView
    private boolean saved_connected_state;

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

    public abstract IoletInstance getDModel();

    private final PBasicInputEventHandler inputEventListener = new PBasicInputEventHandler() {

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
		    Net dnet = new NetDrag();
                    NetController dragNetController = dnet.getController();
                    dragtarget = null;
		    if (PIoletAbstract.this instanceof PInletInstanceView) {
                        dragNetController.connectInlet((InletInstance) getDModel());
                    } else {
                        dragNetController.connectOutlet((OutletInstance) getDModel());
                    }
                    dragnet = new PNetDragging(dnet, getPatchView());
                    dragNetController.addView(dragnet);

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
                if (dragtarget == null) {
                    if (!PIoletAbstract.this.getBoundsReference().contains(globalToLocal(e.getPosition()))) {
                        if (PIoletAbstract.this instanceof PInletInstanceView) {
                            getPatchView().getDModel().getController().addMetaUndo("disconnect inlet");
                            getPatchView().getDModel().getController().disconnect((InletInstance) getDModel());
                        } else {
                            getPatchView().getDModel().getController().addMetaUndo("disconnect outlet");
                            getPatchView().getDModel().getController().disconnect((OutletInstance) getDModel());
                        }
                    }
                } else {
                    if (PIoletAbstract.this instanceof PInletInstanceView) {
                        if (dragtarget instanceof PInletInstanceView) {
                            getPatchView().getDModel().getController().addMetaUndo("connect");
                            getPatchView().getDModel().getController().addConnection((InletInstance) getDModel(),
                                    (InletInstance) ((PInletInstanceView) dragtarget).getDModel());
                        } else if (dragtarget instanceof POutletInstanceView) {
                            getPatchView().getDModel().getController().addMetaUndo("connect");
                            getPatchView().getDModel().getController().addConnection((InletInstance) getDModel(),
                                    (OutletInstance) ((POutletInstanceView) dragtarget).getDModel());
                        }
                    } else if (PIoletAbstract.this instanceof POutletInstanceView) {
                        if (dragtarget instanceof PInletInstanceView) {
                            getPatchView().getDModel().getController().addMetaUndo("connect");
                            getPatchView().getDModel().getController().addConnection(
                                    (InletInstance) ((PInletInstanceView) dragtarget).getDModel(),
                                    (OutletInstance) ((POutletInstanceView) PIoletAbstract.this).getDModel());
                        }
                    }
                    getPatchView().getDModel().getController().promoteOverloading(false);
                    dragtarget.repaint();
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
                        dragnet.setDragPoint(jackLocation);
                    }
                } else // floating
                {
                    if (dragnet != null) {
                        dragnet.setDragPoint(new Point((int) p.getX(), (int) p.getY()));
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
