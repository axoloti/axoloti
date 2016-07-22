package axoloti.iolet;

import axoloti.MainFrame;
import axoloti.Net;
import axoloti.NetDragging;
import axoloti.PatchGUI;
import axoloti.ZoomUtils;
import axoloti.inlets.InletInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.outlets.OutletInstance;
import axoloti.utils.Constants;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.simpleframework.xml.Attribute;

public abstract class IoletAbstract extends JPanel {

    @Deprecated
    @Attribute(required = false)
    public String name;
    @Attribute(name = "obj", required = false)
    public String objname;

    public AxoObjectInstanceAbstract axoObj;
    public JLabel lbl;
    public JComponent jack;
    public NetDragging drag_net;

    public JPanel dropTargetDummyComponent;
    protected DropTarget dt;

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

    public AxoObjectInstanceAbstract GetObjectInstance() {
        return axoObj;
    }

    public void deleteDummyDropTarget() {
        PatchGUI patchGui = getPatchGui();
        if (patchGui != null) {
            if (this.dropTargetDummyComponent != null) {
                patchGui.unzoomedLayerPanel.remove(this.dropTargetDummyComponent);
            }
        }
    }

    public void updateDummyDropTarget() {
        try {
            PatchGUI patchGui = getPatchGui();
            if (patchGui != null) {
                if (this.dropTargetDummyComponent == null) {
                    this.dropTargetDummyComponent = new JPanel();
                    this.dropTargetDummyComponent.setBackground(Constants.TRANSPARENT);

                    patchGui.unzoomedLayerPanel.add(this.dropTargetDummyComponent);
                    patchGui.unzoomedLayerPanel.setComponentZOrder(this.dropTargetDummyComponent, 0);
                    if (dt != null) {
                        dt.setComponent(this.dropTargetDummyComponent);
                    }

                    patchGui.zoomUI.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            String propertyName = evt.getPropertyName();
                            if (propertyName.equals(axoloti.ZoomUI.ZOOM_IN_CHANGE_MESSAGE)
                                    || propertyName.equals(axoloti.ZoomUI.ZOOM_OUT_CHANGE_MESSAGE)) {
                                IoletAbstract.this.updateDummyDropTarget();
                            }
                        }
                    });
                }
                Point p = SwingUtilities.convertPoint(this.jack.getParent(), this.jack.getLocation(), patchGui.unzoomedLayerPanel);
                double zoom = patchGui.zoomUI.getScale();

                int x = (int) (p.x * zoom);
                int y = (int) (p.y * zoom);
                this.dropTargetDummyComponent.setLocation(x, y);
                this.dropTargetDummyComponent.setVisible(true);
                int width = (int) (this.getWidth() * zoom);
                int height = (int) (this.getHeight() * zoom);
                Dimension size = new Dimension(width, height);
                this.dropTargetDummyComponent.setPreferredSize(size);
                this.dropTargetDummyComponent.setMinimumSize(size);
                this.dropTargetDummyComponent.setBounds(x, y, width, height);
            }
        } catch (ClassCastException e) {

        }
    }

    public Point getJackLocInCanvas() {
        Point jackLocation = jack.getLocationOnScreen();
        jackLocation.x += 5;
        jackLocation.y += 5;
        SwingUtilities.convertPointFromScreen(jackLocation, getPatchGui().Layers);
        return jackLocation;
    }

    public DropTarget createDropTarget() {
        return new DropTarget() {
            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                PatchGUI p = getPatchGui();
                for (Component cmp : p.selectionRectLayerPanel.getComponents()) {
                    if (cmp instanceof NetDragging) {
                        NetDragging nd = (NetDragging) cmp;
                        Point ps = getJackLocInCanvas();
                        double zoom = p.zoomUI.getScale();
                        ps.x = (int) (ps.x * zoom);
                        ps.y = (int) (ps.y * zoom);
                        if (nd != drag_net) {
                            nd.SetDragPoint(ps);
                        } else {
                            Point jackLocation = jack.getLocationOnScreen();
                            SwingUtilities.convertPointFromScreen(jackLocation, p.Layers);
                            jackLocation.x *= zoom;
                            jackLocation.y *= zoom;

                            Point pl = new Point(dtde.getLocation().x + jackLocation.x, dtde.getLocation().y + jackLocation.y);
                            drag_net.SetDragPoint(pl);
                        }
                        p.selectionRectLayerPanel.repaint();
                    }
                }
            }

            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                Transferable t = dtde.getTransferable();
                try {
                    if (axoObj.patch == null) {
                        return;
                    }
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    String ss[] = s.split("::");
                    if (ss.length == 2) {
                        OutletInstance ol;
                        InletInstance il;
                        if (IoletAbstract.this instanceof InletInstance) {

                            if ((ol = axoObj.patch.getOutletByReference(ss[0], ss[1])) != null) {
                                Net n1 = axoObj.patch.AddConnection((InletInstance) IoletAbstract.this, ol);
                                axoObj.patch.PromoteOverloading(false);
                                if (n1 != null) {
                                    n1.setSelected(false);
                                    n1.repaint();
                                }
                            } else if ((il = axoObj.patch.getInletByReference(ss[0], ss[1])) != null) {
                                Net n1 = axoObj.patch.AddConnection((InletInstance) IoletAbstract.this, il);
                                axoObj.patch.PromoteOverloading(false);
                                if (n1 != null) {
                                    n1.setSelected(false);
                                    n1.repaint();
                                }
                            }
                        } else if ((il = axoObj.patch.getInletByReference(ss[0], ss[1])) != null) {
                            Net n = axoObj.patch.AddConnection(il, (OutletInstance) IoletAbstract.this);
                            axoObj.patch.PromoteOverloading(false);
                            if (n != null) {
                                n.setSelected(false);
                                n.repaint();
                            }
                        }
                    } else {
                        System.out.println("spilled on inlet: " + s);
                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                getPatchGui().zoomUI.cancelDrag();
                super.drop(dtde);
            }

        };
    }

    abstract public JPopupMenu getPopup();

    public PatchGUI getPatchGui() {
        try {
            return (PatchGUI) axoObj.getPatch();
        } catch (ClassCastException e) {
            return null;
        }
    }

    public MouseListener createMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    ZoomUtils.showZoomedPopupMenu(IoletAbstract.this, axoObj, getPopup());
                }
                setHighlighted(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setHighlighted(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHighlighted(false);
            }
        };
    }

    public abstract String dragString();

    public class DragGestureListImp implements DragGestureListener {

        @Override
        public void dragGestureRecognized(DragGestureEvent event) {
            if (!axoObj.IsLocked()) {
                final PatchGUI patchGUI = getPatchGui();
                Transferable t = new StringSelection(dragString());
                DragSourceListener dsl = new DragSourceListener() {
                    @Override
                    public void dragEnter(DragSourceDragEvent dsde) {
                    }

                    @Override
                    public void dragOver(DragSourceDragEvent dsde) {
                    }

                    @Override
                    public void dropActionChanged(DragSourceDragEvent dsde) {
                    }

                    @Override
                    public void dragExit(DragSourceEvent dse) {
                    }

                    @Override
                    public void dragDropEnd(DragSourceDropEvent dsde) {
                        setHighlighted(false);
                        patchGUI.selectionRectLayerPanel.remove(drag_net);
                        patchGUI.selectionRectLayerPanel.repaint();
                    }
                };
                event.startDrag(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR), t, dsl);
                drag_net = new NetDragging(patchGUI);
                if (IoletAbstract.this instanceof InletInstance) {
                    drag_net.connectInlet((InletInstance) IoletAbstract.this);
                } else {
                    drag_net.connectOutlet((OutletInstance) IoletAbstract.this);
                }

                patchGUI.selectionRectLayerPanel.add(drag_net);
                patchGUI.selectionRectLayerPanel.setVisible(true);
                setHighlighted(true);
            }
        }
    }

    public boolean isConnected() {
        if (axoObj == null) {
            return false;
        }
        if (axoObj.patch == null) {
            return false;
        }
        return (axoObj.patch.GetNet(this) != null);
    }

    public void setHighlighted(boolean highlighted) {
        if (getRootPane().getCursor() != MainFrame.transparentCursor
                && axoObj.patch != null) {
            Net n = axoObj.patch.GetNet(this);
            if (n != null
                    && n.getSelected() != highlighted) {
                n.setSelected(highlighted);

                final PatchGUI patchGUI = getPatchGui();
                Rectangle bounds = n.getBounds();
                patchGUI.zoomUI.scale(bounds);
                patchGUI.netLayerPanel.repaint(bounds);
            }
        }
    }

    public void disconnect() {
        axoObj.patch.disconnect(this);
    }

    public void deleteNet() {
        Net n = axoObj.patch.GetNet(this);
        axoObj.patch.delete(n);
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return ZoomUtils.getToolTipLocation(this, event, axoObj);
    }

    public ComponentListener createComponentListener() {
        return new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent e) {
                IoletAbstract.this.updateDummyDropTarget();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                IoletAbstract.this.updateDummyDropTarget();

            }

            @Override
            public void componentResized(ComponentEvent e) {
                IoletAbstract.this.updateDummyDropTarget();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                IoletAbstract.this.updateDummyDropTarget();
            }
        };
    }
}
