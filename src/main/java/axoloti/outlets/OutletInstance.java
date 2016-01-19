/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
package axoloti.outlets;

import axoloti.MainFrame;
import axoloti.Net;
import axoloti.NetDragging;
import axoloti.PatchGUI;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.inlets.InletInstance;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "source")
public class OutletInstance<T extends Outlet> extends JPanel implements Comparable<OutletInstance>, AtomInstance<T> {

    @Deprecated
    @Attribute(required = false)
    String name;
    @Attribute(name = "obj", required = false)
    public String objname;
    @Attribute(name = "outlet", required = false)
    public String outletname;

    private final T outlet;
    AxoObjectInstanceAbstract axoObj;
    OutletInstancePopupMenu popup = new OutletInstancePopupMenu(this);
    JLabel lbl;
    JComponent jack;
    NetDragging drag_net;

    @Override
    public int compareTo(OutletInstance t) {
        return axoObj.compareTo(t.axoObj);
    }

    public String getObjname() {
        if (objname != null) {
            return objname;
        } else {
            int sepIndex = name.lastIndexOf(' ');
            return name.substring(0, sepIndex);
        }
    }

    public String getOutletname() {
        if (outletname != null) {
            return outletname;
        } else {
            int sepIndex = name.lastIndexOf(' ');
            return name.substring(sepIndex + 1);
        }
    }

    @Override
    public AxoObjectInstanceAbstract GetObjectInstance() {
        return axoObj;
    }

    @Override
    public T GetDefinition() {
        return outlet;
    }

    class DragGestureListImp implements DragGestureListener {

        @Override
        public void dragGestureRecognized(DragGestureEvent event) {
            if (!axoObj.IsLocked()) {
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
                        ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.remove(drag_net);
                        ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.repaint();
                    }
                };
                event.startDrag(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR), t, dsl);
                if (drag_net == null) {
                    drag_net = new NetDragging(axoObj.getPatch());
                    drag_net.connectOutlet(OutletInstance.this);
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.add(drag_net);
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.setVisible(true);
                } else {
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.add(drag_net);
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.setVisible(true);
                }
                setHighlighted(true);
            }
        }
    }

    public void setHighlighted(boolean highlighted) {
        if (axoObj.patch != null) {
            Net n = axoObj.patch.GetNet(OutletInstance.this);
            if (n != null) {
                n.setSelected(highlighted);
            }
        }
    }

    public String dragString() {
        return axoObj.getInstanceName() + "::" + outlet.name;
    }

    public OutletInstance() {
        this.outlet = null;
        this.axoObj = null;
    }

    public OutletInstance(T outlet, AxoObjectInstance axoObj) {
        this.outlet = outlet;
        this.axoObj = axoObj;
        RefreshName();
        PostConstructor();
    }

    public void RefreshName() {
        name = axoObj.getInstanceName() + " " + outlet.name;
        objname = axoObj.getInstanceName();
        outletname = outlet.name;
        name = null;
    }

    public DataType GetDataType() {
        return outlet.getDatatype();
    }

    public String GetLabel() {
        return outlet.name;
    }

    public String GetCName() {
        return outlet.GetCName();
    }

    public final void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));
        add(Box.createHorizontalGlue());
        if (axoObj.getType().GetOutlets().size() > 1) {
            add(new LabelComponent(outlet.name));
            add(Box.createHorizontalStrut(2));
        }
        add(new SignalMetaDataIcon(outlet.GetSignalMetaData()));
        jack = new components.JackOutputComponent(this);
        jack.setForeground(outlet.getDatatype().GetColor());
        add(jack);
        setComponentPopupMenu(popup);
        setToolTipText(outlet.description);
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_LINK, new DragGestureListImp());

        DropTarget dt = new DropTarget() {
            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                PatchGUI p = (PatchGUI) axoObj.getPatch();
                for (Component cmp : p.SelectionRectLayer.getComponents()) {
                    if (cmp instanceof NetDragging) {
                        NetDragging nd = (NetDragging) cmp;
                        if (nd != drag_net) {
                            nd.SetDragPoint(getJackLocInCanvas());
                        } else {
                            Point ps = getJackLocInCanvas();
                            Point jp = jack.getLocation();
                            Point pl = new Point(dtde.getLocation().x + ps.x - 5 - jp.x, dtde.getLocation().y + ps.y - 5 - jp.y);
                            drag_net.SetDragPoint(pl);
                        }
                        ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.repaint();
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
//                    if ((ol = axoObj.patch.getOutletByReference(s)) != null) {
//                        Net n = axoObj.patch.AddConnection(OutletInstance.this, ol);
//                        axoObj.patch.PromoteOverloading();
//                        if (n != null) {
//                            n.setSelected(false);
//                            n.repaint();
//                        }
//                    } else 
                        if ((il = axoObj.patch.getInletByReference(ss[0], ss[1])) != null) {
                            Net n = axoObj.patch.AddConnection(il, OutletInstance.this);
                            axoObj.patch.PromoteOverloading();
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
                super.drop(dtde);
            }
        };
        setDropTarget(dt);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
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
        });
    }

    public Point getJackLocInCanvas() {
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

    void disconnect() {
        axoObj.patch.disconnect(this);
    }

    void deleteNet() {
        Net n = axoObj.patch.GetNet(this);
        axoObj.patch.delete(n);
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
}
