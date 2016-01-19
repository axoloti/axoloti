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
package axoloti.inlets;

import axoloti.Net;
import axoloti.NetDragging;
import axoloti.PatchGUI;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.outlets.OutletInstance;
import components.JackInputComponent;
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
@Root(name = "dest")
public class InletInstance<T extends Inlet> extends JPanel implements AtomInstance<T> {

    @Attribute(required = false)
    @Deprecated
    String name;
    @Attribute(name = "obj", required = false)
    public String objname;
    @Attribute(name = "inlet", required = false)
    public String inletname;

    private final T inlet;
    AxoObjectInstanceAbstract axoObj;
    JLabel lbl;
    JComponent jack;
    InletInstancePopupMenu popup = new InletInstancePopupMenu(this);
    NetDragging drag_net;

    public String GetCName() {
        return inlet.GetCName();
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

    public String getInletname() {
        if (inletname != null) {
            return inletname;
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
        return inlet;
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
                        //((PatchGUI)axoObj.getPatch()).SelectionRectLayer.remove(drag_net);

                        //System.out.println("drag exit");
                    }

                    @Override
                    public void dragDropEnd(DragSourceDropEvent dsde) {
                        setHighlighted(false);
                        ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.remove(drag_net);
                        ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.repaint();
                        //System.out.println("drag drop end");
                    }
                };
                event.startDrag(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR), t, dsl);
                if (drag_net == null) {
                    drag_net = new NetDragging(axoObj.getPatch());
                    drag_net.connectInlet(InletInstance.this);
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.add(drag_net);
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.setVisible(true);
                } else {
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.add(drag_net);
                    ((PatchGUI) axoObj.getPatch()).SelectionRectLayer.setVisible(true);
                }
            }
        }
    }

    public String dragString() {
        return axoObj.getInstanceName() + "::" + inlet.name;
    }

    public InletInstance() {
        this.inlet = null;
        this.axoObj = null;
    }

    public final void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));
        jack = new JackInputComponent(this);
        jack.setForeground(inlet.getDatatype().GetColor());
        add(jack);
        add(new SignalMetaDataIcon(inlet.GetSignalMetaData()));
        if (axoObj.getType().GetInlets().size() > 1) {
            add(Box.createHorizontalStrut(3));
            add(new LabelComponent(inlet.name));
        }
        add(Box.createHorizontalGlue());
//        invalidate();
//        doLayout();
        setComponentPopupMenu(popup);
        setToolTipText(inlet.description);

        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_LINK, new InletInstance.DragGestureListImp());

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
                            Point pl = new Point(dtde.getLocation().x + ps.x - 5, dtde.getLocation().y + ps.y - 5);
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
                        if ((ol = axoObj.patch.getOutletByReference(ss[0], ss[1])) != null) {
                            Net n1 = axoObj.patch.AddConnection(InletInstance.this, ol);
                            axoObj.patch.PromoteOverloading();
                            if (n1 != null) {
                                n1.setSelected(false);
                                n1.repaint();
                            }
                        } else if ((il = axoObj.patch.getInletByReference(ss[0], ss[1])) != null) {
                            Net n1 = axoObj.patch.AddConnection(InletInstance.this, il);
                            axoObj.patch.PromoteOverloading();
                            if (n1 != null) {
                                n1.setSelected(false);
                                n1.repaint();
                            }
                        }
                    } else {
                        System.out.println("spilled on inlet: " + s);
                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(InletInstance.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(InletInstance.class.getName()).log(Level.SEVERE, null, ex);
                }
                super.drop(dtde);
            }
        ;
        };
        setDropTarget(dt);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
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

    public InletInstance(T inlet, final AxoObjectInstance axoObj) {
        this.inlet = inlet;
        this.axoObj = axoObj;
        RefreshName();
        PostConstructor();
    }

    public DataType GetDataType() {
        return inlet.getDatatype();
    }

    public String GetLabel() {
        return inlet.name;
    }

    public void RefreshName() {
        name = axoObj.getInstanceName() + " " + inlet.name;
        objname = axoObj.getInstanceName();
        inletname = inlet.name;
        name = null;
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

    public Inlet getInlet() {
        return inlet;
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
        if (axoObj.patch != null) {
            Net n = axoObj.patch.GetNet(InletInstance.this);
            if (n != null) {
                n.setSelected(highlighted);
            }
        }
    }
}
