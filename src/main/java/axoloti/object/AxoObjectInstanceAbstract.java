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
package axoloti.object;

import axoloti.MainFrame;
import axoloti.Patch;
import axoloti.PatchGUI;
import axoloti.SDFileReference;
import axoloti.Theme;
import axoloti.ZoomUI;
import axoloti.ZoomUtils;
import axoloti.attribute.AttributeInstance;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletInstance;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.CharEscape;
import axoloti.utils.Constants;
import components.LabelComponent;
import components.TextFieldComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj_abstr")
public abstract class AxoObjectInstanceAbstract extends JPanel implements Comparable<AxoObjectInstanceAbstract>, ObjectModifiedListener {

    @Attribute(name = "type")
    public String typeName;
    @Deprecated
    @Attribute(name = "sha", required = false)
    public String typeSHA;
    @Attribute(name = "uuid", required = false)
    public String typeUUID;
    @Attribute(name = "name", required = false)
    String InstanceName;
    @Attribute
    int x;
    @Attribute
    int y;
    public Patch patch;
    AxoObjectAbstract type;
    boolean dragging = false;
    int dX, dY;
    protected boolean Selected = false;
    private boolean Locked = false;
    private boolean typeWasAmbiguous = false;
    JPanel Titlebar;
    TextFieldComponent InstanceNameTF;
    LabelComponent InstanceLabel;
    MouseListener ml;
    MouseMotionListener mml;

    public AxoObjectInstanceAbstract() {
    }

    public AxoObjectInstanceAbstract(AxoObjectAbstract type, Patch patch1, String InstanceName1, Point location) {
        super();
        this.type = type;
        typeName = type.id;
        if (type.createdFromRelativePath && (patch1 != null)) {
            String pPath = patch1.getFileNamePath();
            String oPath = type.sPath;

            if (oPath.endsWith(".axp") || oPath.endsWith(".axo") || oPath.endsWith(".axs")) {
                oPath = oPath.substring(0, oPath.length() - 4);
            }
            pPath = pPath.replaceAll("\\\\", "/");
            oPath = oPath.replaceAll("\\\\", "/");
            String[] pPathA = pPath.split("/");
            String[] oPathA = oPath.split("/");
            int i = 0;
            while ((i < pPathA.length) && (i < oPathA.length) && (oPathA[i].equals(pPathA[i]))) {
                i++;
            }
            String rPath = "";
            for (int j = i; j < pPathA.length - 1; j++) {
                rPath += "../";
            }
            if (rPath.isEmpty()) {
                rPath = ".";
            } else {
                rPath = rPath.substring(0, rPath.length() - 1);
            }
            for (int j = i; j < oPathA.length; j++) {
                rPath += "/" + oPathA[j];
            }

            System.out.println(rPath);
            typeName = rPath;
        }

        typeUUID = type.getUUID();
        this.InstanceName = InstanceName1;
        this.x = location.x;
        this.y = location.y;
        this.patch = patch1;
    }

    public Patch getPatch() {
        return patch;
    }

    public PatchGUI getPatchGUI() {
        return (PatchGUI) patch;
    }

    public String getInstanceName() {
        return InstanceName;
    }

    public void setType(AxoObjectAbstract type) {
        this.type = type;
        typeUUID = type.getUUID();
    }

    public void setInstanceName(String InstanceName) {
        if (this.InstanceName.equals(InstanceName)) {
            return;
        }
        if (patch != null) {
            AxoObjectInstanceAbstract o1 = patch.GetObjectInstance(InstanceName);
            if ((o1 != null) && (o1 != this)) {
                Logger.getLogger(AxoObjectInstanceAbstract.class.getName()).log(Level.SEVERE, "Object name {0} already exists!", InstanceName);
                doLayout();
                return;
            }
        }
        this.InstanceName = InstanceName;
        if (InstanceLabel != null) {
            InstanceLabel.setText(InstanceName);
        }
        doLayout();
    }

    public AxoObjectAbstract getType() {
        return type;
    }

    public AxoObjectAbstract resolveType() {
        if (type != null) {
            return type;
        }
        if (typeUUID != null) {
            type = MainFrame.axoObjects.GetAxoObjectFromUUID(typeUUID);
            if (type != null) {
                System.out.println("restored from UUID:" + type.id);
                typeName = type.id;
            }
        }
        if (type == null) {
            ArrayList<AxoObjectAbstract> types = MainFrame.axoObjects.GetAxoObjectFromName(typeName, patch.GetCurrentWorkingDirectory());
            if (types == null) {
                Logger.getLogger(AxoObjectInstanceAbstract.class.getName()).log(Level.SEVERE, "Object name {0} not found", typeName);
            } else { // pick first
                if (types.size() > 1) {
                    typeWasAmbiguous = true;
                }
                type = types.get(0);
                if (type instanceof AxoObjectUnloaded) {
                    AxoObjectUnloaded aou = (AxoObjectUnloaded) type;
                    type = aou.Load();
                    return (AxoObject) type;
                }
            }
        }
        return type;
    }

    JPopupMenu popup;

    private final Dimension TitleBarMinimumSize = new Dimension(40, 12);
    private final Dimension TitleBarMaximumSize = new Dimension(32768, 12);

    public void PostConstructor() {
        removeAll();
        setMinimumSize(new Dimension(60, 40));
        //setMaximumSize(new Dimension(Short.MAX_VALUE,
        //        Short.MAX_VALUE));

        setLocation(x, y);
//        setFocusable(true);
        Titlebar = new TitleBarPanel(this);
        Titlebar.setLayout(new BoxLayout(Titlebar, BoxLayout.LINE_AXIS));
        Titlebar.setBackground(Theme.getCurrentTheme().Object_TitleBar_Background);
        Titlebar.setMinimumSize(TitleBarMinimumSize);
        Titlebar.setMaximumSize(TitleBarMaximumSize);
        setBorder(BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Unselected));
        setOpaque(true);
        resolveType();

        setBackground(Theme.getCurrentTheme().Object_Default_Background);

        setVisible(true);

        popup = new JPopupMenu();

        ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (patch != null) {
                    if (me.getClickCount() == 1) {
                        if (me.isShiftDown()) {
                            SetSelected(!GetSelected());
                        } else if (Selected == false) {
                            ((PatchGUI) patch).SelectNone();
                            SetSelected(true);
                        }
                    }
                    if (me.getClickCount() == 2) {
                        ((PatchGUI) patch).ShowClassSelector(AxoObjectInstanceAbstract.this.getLocation(), AxoObjectInstanceAbstract.this, null);
                    }
                }
            }

            /*
             ClassSelector cs = ((PatchGUI)patch).cs;
             cs.setText(getType().id);
             //                        getParent().add(cs, 0);
             cs.setLocation(getLocation());
             //                        newObjTF.setSize(400,300);
             cs.setVisible(true);
             cs.requestFocus();
             * /
             }
             } else {
             for (AxoObjectInstanceAbstract o : patch.objectinstances) {
             o.SetSelected(false);
             }
             SetSelected(true);
             }
             //patch.invalidate();
             }*/
            @Override
            public void mousePressed(MouseEvent me) {
                handleMousePressed(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                handleMouseReleased(me);
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        };

        Titlebar.addMouseListener(ml);
        addMouseListener(ml);

        mml = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent me) {
                if (patch != null) {
                    if (dragging) {
                        for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                            if (o.dragging) {
                                o.x = getZoomUI().removeZoomFactor(me.getLocationOnScreen().x) - o.dX;
                                o.y = getZoomUI().removeZoomFactor(me.getLocationOnScreen().y) - o.dY;
                                o.dX = getZoomUI().removeZoomFactor(me.getLocationOnScreen().x) - o.getX();
                                o.dY = getZoomUI().removeZoomFactor(me.getLocationOnScreen().y) - o.getY();
                                o.setLocation(o.x, o.y);
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
            }
        };

        Titlebar.addMouseMotionListener(mml);
        addMouseMotionListener(mml);

        addComponentListener(
                new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
                updateDummyDropTargets();
            }

            public void componentMoved(ComponentEvent e) {
                updateDummyDropTargets();
            }

            public void componentResized(ComponentEvent e) {
                updateDummyDropTargets();
            }

            public void componentShown(ComponentEvent e) {
                updateDummyDropTargets();
            }
        });
    }

    private void moveToDraggedLayer(AxoObjectInstanceAbstract o) {
        if (getPatchGUI().objectLayerPanel.isAncestorOf(o)) {
            getPatchGUI().draggedObjectLayerPanel.add(o);
            getPatchGUI().objectLayerPanel.remove(o);
        }
    }

    protected void handleMousePressed(MouseEvent me) {
        if (patch != null) {
            if (me.isPopupTrigger()) {

            } else if (!IsLocked()) {
                ArrayList<AxoObjectInstanceAbstract> toMove = new ArrayList<AxoObjectInstanceAbstract>();
                dX = getZoomUI().removeZoomFactor(me.getXOnScreen()) - getX();
                dY = getZoomUI().removeZoomFactor(me.getYOnScreen()) - getY();
                dragging = true;
                moveToDraggedLayer(this);
                if (IsSelected()) {
                    for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                        if (o.IsSelected()) {
                            moveToDraggedLayer(o);

                            o.dX = getZoomUI().removeZoomFactor(me.getXOnScreen()) - o.getX();
                            o.dY = getZoomUI().removeZoomFactor(me.getYOnScreen()) - o.getY();
                            o.dragging = true;
                        }
                    }
                }
            }
        }
    }

    private void moveToObjectLayer(AxoObjectInstanceAbstract o, int z) {
        if (getPatchGUI().draggedObjectLayerPanel.isAncestorOf(o)) {
            getPatchGUI().objectLayerPanel.add(o);
            getPatchGUI().draggedObjectLayerPanel.remove(o);
            getPatchGUI().objectLayerPanel.setComponentZOrder(o, z);
        }
    }

    protected void handleMouseReleased(MouseEvent me) {
        int maxZIndex = 0;
        if (dragging) {
            dragging = false;
            if (patch != null) {
                boolean setDirty = false;
                for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                    moveToObjectLayer(o, 0);
                    if (getPatchGUI().objectLayerPanel.getComponentZOrder(o) > maxZIndex) {
                        maxZIndex = getPatchGUI().objectLayerPanel.getComponentZOrder(o);
                    }
                    o.dragging = false;
                    int original_x = o.x;
                    int original_y = o.y;
                    o.x = ((o.x + (Constants.X_GRID / 2)) / Constants.X_GRID) * Constants.X_GRID;
                    o.y = ((o.y + (Constants.Y_GRID / 2)) / Constants.Y_GRID) * Constants.Y_GRID;
                    o.setLocation(o.x, o.y);
                    if(o.x != original_x || o.y != original_y) {
                        setDirty = true;                        
                    }
                }
                if (setDirty) {
                    patch.AdjustSize();
                    patch.SetDirty();
                }
            }
        }
        moveToObjectLayer(this, maxZIndex);
    }

    public ZoomUI getZoomUI() {
        return ((PatchGUI) patch).zoomUI;
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        this.x = x;
        this.y = y;
        if (patch != null) {
            patch.repaint();
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void addInstanceNameEditor() {
        InstanceNameTF = new TextFieldComponent(InstanceName);
        InstanceNameTF.selectAll();
        InstanceNameTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String s = InstanceNameTF.getText();
                setInstanceName(s);
                getParent().remove(InstanceNameTF);
            }
        });
        InstanceNameTF.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                String s = InstanceNameTF.getText();
                setInstanceName(s);
                getParent().remove(InstanceNameTF);
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        InstanceNameTF.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    String s = InstanceNameTF.getText();
                    setInstanceName(s);
                    getParent().remove(InstanceNameTF);
                }
            }
        });

        getParent().add(InstanceNameTF, 0);
        InstanceNameTF.setLocation(getLocation().x, getLocation().y + InstanceLabel.getLocation().y);
        InstanceNameTF.setSize(getWidth(), 15);
        InstanceNameTF.setVisible(true);
        InstanceNameTF.requestFocus();
    }

    /*
     public class AxoObjectInstanceNameVerifier extends InputVerifier {

     @Override
     public boolean verify(JComponent input) {
     String text = ((TextFieldComponent) input).getText();
     Pattern p = Pattern.compile("[^a-z0-9_]", Pattern.CASE_INSENSITIVE);
     Matcher m = p.matcher(text);
     boolean b = m.find();
     if (b) {
     System.out.println("reject instancename : special character found");
     return false;
     }
     if (patch != null) {
     for (AxoObjectInstanceAbstract o : patch.objectinstances) {
     if (o.InstanceName.equalsIgnoreCase(text) && (AxoObjectInstanceAbstract.this != o)) {
     System.out.println("reject instancename : exists");
     return false;
     }
     }
     }
     return true;
     }
     }
     */
    public String GenerateInstanceDataDeclaration2() {
        return null;
    }

    public String GenerateCodeMidiHandler(String vprefix) {
        return "";
    }

    public String GenerateCallMidiHandler() {
        return "";
    }

    public ArrayList<InletInstance> GetInletInstances() {
        return new ArrayList<InletInstance>();
    }

    public ArrayList<OutletInstance> GetOutletInstances() {
        return new ArrayList<OutletInstance>();
    }

    public ArrayList<ParameterInstance> getParameterInstances() {
        return new ArrayList<ParameterInstance>();
    }

    public ArrayList<AttributeInstance> getAttributeInstances() {
        return new ArrayList<AttributeInstance>();
    }

    public ArrayList<DisplayInstance> GetDisplayInstances() {
        return new ArrayList<DisplayInstance>();
    }

    public InletInstance GetInletInstance(String n) {
        return null;
    }

    public OutletInstance GetOutletInstance(String n) {
        return null;
    }

    public void refreshIndex() {
    }

    public boolean IsSelected() {
        return Selected;
    }

    public void SetSelected(boolean Selected) {
        if (this.Selected != Selected) {
            if (Selected) {
                setBorder(BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Selected));
            } else {
                setBorder(BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Unselected));
            }
        }
        this.Selected = Selected;
    }

    public boolean GetSelected() {
        return Selected;
    }

    public void Lock() {
        Locked = true;
    }

    public void Unlock() {
        Locked = false;
    }

    public boolean IsLocked() {
        return Locked;
    }

    public void SetLocation(int x1, int y1) {
        super.setLocation(x1, y1);
        x = x1;
        y = y1;
    }

    public boolean providesModulationSource() {
        return false;
    }

    @Override
    public int compareTo(AxoObjectInstanceAbstract o) {
        if (o.y == this.y) {
            if (o.x < x) {
                return 1;
            } else if (o.x == x) {
                return 0;
            } else {
                return -1;
            }
        }
        if (o.y < y) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getLegalName() {
        return CharEscape.CharEscape(InstanceName);
    }

    public String getCInstanceName() {
        String s = "instance" + getLegalName();
        return s;
    }

    public void PromoteToOverloadedObj() {
    }

    /*
     public String GenerateStructName() {
     return "";
     }

     public String GenerateDoFunctionName(){
     return "";
     }

     public String GenerateInitFunctionName(){
     return "";
     }
     */
    public String GenerateInitCodePlusPlus(String vprefix, boolean enableOnParent) {
        return "";
    }

    public String GenerateDisposeCodePlusPlus(String vprefix) {
        return "";
    }

    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        return "";
    }

    public boolean hasStruct() {
        return false;
    }

    public boolean hasInit() {
        return false;
    }

    public void resizeToGrid() {
        doLayout();
        Dimension d = getPreferredSize();
        d.width = ((d.width + Constants.X_GRID - 1) / Constants.X_GRID) * Constants.X_GRID;
        d.height = ((d.height + Constants.Y_GRID - 1) / Constants.Y_GRID) * Constants.Y_GRID;
        setSize(d);
    }

    @Override
    public void ObjectModified(Object src) {
    }

    public ArrayList<SDFileReference> GetDependendSDFiles() {
        return null;
    }

    public boolean isTypeWasAmbiguous() {
        return typeWasAmbiguous;
    }

    public void Close() {
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return ZoomUtils.getToolTipLocation(this, event, this);
    }

    public void updateDummyDropTargets() {
        for (InletInstance i : this.GetInletInstances()) {
            i.updateDummyDropTarget();
        }

        for (OutletInstance oi : this.GetOutletInstances()) {
            oi.updateDummyDropTarget();
        }
    }

    public void deleteDummyDropTargets() {
        for (InletInstance i : this.GetInletInstances()) {
            i.deleteDummyDropTarget();
        }

        for (OutletInstance oi : this.GetOutletInstances()) {
            oi.deleteDummyDropTarget();
        }
    }
}
