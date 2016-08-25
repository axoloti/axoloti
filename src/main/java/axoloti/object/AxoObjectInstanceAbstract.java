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
import axoloti.Net;
import axoloti.Patch;
import axoloti.PatchGUI;
import axoloti.SDFileReference;
import axoloti.Theme;
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
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj_abstr")
public abstract class AxoObjectInstanceAbstract extends JPanel implements Comparable<AxoObjectInstanceAbstract>, ObjectModifiedListener, MouseListener, MouseMotionListener {

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
    private Point dragLocation = null;
    private Point dragAnchor = null;
    protected boolean Selected = false;
    private boolean Locked = false;
    private boolean typeWasAmbiguous = false;
    final JPanel Titlebar = new JPanel();
    TextFieldComponent InstanceNameTF;
    LabelComponent InstanceLabel;

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
                repaint();
                return;
            }
        }
        this.InstanceName = InstanceName;
        if (InstanceLabel != null) {
            InstanceLabel.setText(InstanceName);
        }
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

    private final Dimension TitleBarMinimumSize = new Dimension(40, 12);
    private final Dimension TitleBarMaximumSize = new Dimension(32768, 12);

    public void PostConstructor() {
        removeAll();
        setMinimumSize(new Dimension(60, 40));
        //setMaximumSize(new Dimension(Short.MAX_VALUE,
        //        Short.MAX_VALUE));

//        setFocusable(true);
        Titlebar.removeAll();
        Titlebar.setLayout(new BoxLayout(Titlebar, BoxLayout.LINE_AXIS));
        Titlebar.setBackground(Theme.getCurrentTheme().Object_TitleBar_Background);
        Titlebar.setMinimumSize(TitleBarMinimumSize);
        Titlebar.setMaximumSize(TitleBarMaximumSize);

        setBorder(borderUnselected);
        resolveType();

        setBackground(Theme.getCurrentTheme().Object_Default_Background);

        setVisible(true);

        Titlebar.addMouseListener(this);
        addMouseListener(this);

        Titlebar.addMouseMotionListener(this);
        addMouseMotionListener(this);
    }

    JPopupMenu CreatePopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        return popup;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (patch != null) {
            if (me.getClickCount() == 1) {
                if (me.isShiftDown()) {
                    SetSelected(!GetSelected());
                    me.consume();
                } else if (Selected == false) {
                    ((PatchGUI) patch).SelectNone();
                    SetSelected(true);
                    me.consume();
                }
            }
            if (me.getClickCount() == 2) {
                ((PatchGUI) patch).ShowClassSelector(AxoObjectInstanceAbstract.this.getLocation(), AxoObjectInstanceAbstract.this, null);
                me.consume();
            }
        }
    }

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

    @Override
    public void mouseDragged(MouseEvent me) {
        if ((patch != null) && (draggingObjects != null)) {
            Point locOnScreen = me.getLocationOnScreen();
            int dx = locOnScreen.x - dragAnchor.x;
            int dy = locOnScreen.y - dragAnchor.y;
            for (AxoObjectInstanceAbstract o : draggingObjects) {
                int nx = o.dragLocation.x + dx;
                int ny = o.dragLocation.y + dy;
                if (!me.isShiftDown()) {
                    nx = ((nx + (Constants.X_GRID / 2)) / Constants.X_GRID) * Constants.X_GRID;
                    ny = ((ny + (Constants.Y_GRID / 2)) / Constants.Y_GRID) * Constants.Y_GRID;
                }
                if (o.x != nx || o.y != ny) {
                    o.setLocation(nx, ny);
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    private void moveToDraggedLayer(AxoObjectInstanceAbstract o) {
        if (getPatchGUI().objectLayerPanel.isAncestorOf(o)) {
            getPatchGUI().objectLayerPanel.remove(o);
            getPatchGUI().draggedObjectLayerPanel.add(o);
        }
    }

    ArrayList<AxoObjectInstanceAbstract> draggingObjects = null;

    protected void handleMousePressed(MouseEvent me) {
        if (patch != null) {
            if (me.isPopupTrigger()) {
                JPopupMenu p = CreatePopupMenu();
                p.show(Titlebar, 0, Titlebar.getHeight());
                me.consume();
            } else if (!IsLocked()) {
                draggingObjects = new ArrayList<AxoObjectInstanceAbstract>();
                dragAnchor = me.getLocationOnScreen();
                moveToDraggedLayer(this);
                draggingObjects.add(this);
                dragLocation = getLocation();
                if (IsSelected()) {
                    for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                        if (o.IsSelected()) {
                            moveToDraggedLayer(o);
                            draggingObjects.add(o);
                            o.dragLocation = o.getLocation();
                        }
                    }
                }
                me.consume();
            }
        }
    }

    private void moveToObjectLayer(AxoObjectInstanceAbstract o, int z) {
        if (getPatchGUI().draggedObjectLayerPanel.isAncestorOf(o)) {
            getPatchGUI().draggedObjectLayerPanel.remove(o);
            getPatchGUI().objectLayerPanel.add(o);
            getPatchGUI().objectLayerPanel.setComponentZOrder(o, z);
        }
    }

    protected void handleMouseReleased(MouseEvent me) {
        if (me.isPopupTrigger()) {
            JPopupMenu p = CreatePopupMenu();
            p.show(Titlebar, 0, Titlebar.getHeight());
            me.consume();
            return;
        }
        int maxZIndex = 0;
        if (draggingObjects != null) {
            if (patch != null) {
                boolean dirtyOnRelease = false;
                for (AxoObjectInstanceAbstract o : draggingObjects) {
                    moveToObjectLayer(o, 0);
                    if (getPatchGUI().objectLayerPanel.getComponentZOrder(o) > maxZIndex) {
                        maxZIndex = getPatchGUI().objectLayerPanel.getComponentZOrder(o);
                    }
                    if (o.x != dragLocation.x || o.y != dragLocation.y) {
                        dirtyOnRelease = true;
                    }
                    o.repaint();
                }
                draggingObjects = null;
                if (dirtyOnRelease) {
                    patch.SetDirty();
                }
                patch.AdjustSize();
            }
            me.consume();
        }
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        this.x = x;
        this.y = y;
        if (patch != null) {
            repaint();
            for (InletInstance i : GetInletInstances()) {
                Net n = getPatch().GetNet(i);
                if (n != null) {
                    n.updateBounds();
                    n.repaint();
                }
            }
            for (OutletInstance i : GetOutletInstances()) {
                Net n = getPatch().GetNet(i);
                if (n != null) {
                    n.updateBounds();
                    n.repaint();
                }
            }
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
                repaint();
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
                    repaint();
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

    static Border borderSelected = BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Selected);
    static Border borderUnselected = BorderFactory.createLineBorder(Theme.getCurrentTheme().Object_Border_Unselected);

    public void SetSelected(boolean Selected) {
        if (this.Selected != Selected) {
            if (Selected) {
                setBorder(borderSelected);
            } else {
                setBorder(borderUnselected);
            }
            repaint();
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

        if (patch != null) {
            for (Net n : patch.nets) {
                n.updateBounds();
            }
        }
    }

    public void moveToFront() {
        getPatchGUI().objectLayerPanel.setComponentZOrder(this, 0);
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
        validate();
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
        AxoObjectAbstract t = getType();
        if (t != null) {
            t.removeObjectModifiedListener(this);
        }
    }
}
