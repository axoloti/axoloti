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
import axoloti.attribute.AttributeInstance;
import axoloti.inlets.InletInstance;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.CharEscape;
import axoloti.utils.Constants;
import components.LabelComponent;
import components.TextFieldComponent;
import displays.DisplayInstance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.PopupMenu;
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
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj_abstr")
public abstract class AxoObjectInstanceAbstract extends JPanel implements Comparable<AxoObjectInstanceAbstract> {

    @Attribute(name = "type")
    public String typeName;
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
    private boolean Selected = false;
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
//            File f = new File();
//            f.ge
//            typeName = 
        }

        typeSHA = type.getSHA();
        typeUUID = type.getUUID();
        this.InstanceName = InstanceName1;
        this.x = location.x;
        this.y = location.y;
        this.patch = patch1;
    }

    public Patch getPatch() {
        return patch;
    }

    public String getInstanceName() {
        return InstanceName;
    }

    public void setType(AxoObjectAbstract type) {
        this.type = type;
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
                repaint();
                return;
            }
        }
        this.InstanceName = InstanceName;
        if (InstanceLabel != null) {
            InstanceLabel.setText(InstanceName);
        }
        doLayout();
        if (getParent() != null) {
            getParent().repaint();
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
        if ((type == null) && (typeSHA != null)) {
            type = MainFrame.axoObjects.GetAxoObjectFromSHA(typeSHA);
            if (type != null) {
                System.out.println("restored from SHA:" + type.id);
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
                typeSHA = type.getSHA();
            }
        }
        return type;
    }

    PopupMenu popup;

    private final Dimension TitleBarMinimumSize = new Dimension(40, 12);
    private final Dimension TitleBarMaximumSize = new Dimension(32768, 12);

    public void PostConstructor() {
        removeAll();
        setMinimumSize(new Dimension(60, 40));
        //setMaximumSize(new Dimension(Short.MAX_VALUE,
        //        Short.MAX_VALUE));
        setLocation(x, y);
//        setFocusable(true);
        Titlebar = new JPanel();
        Titlebar.setLayout(new BoxLayout(Titlebar, BoxLayout.LINE_AXIS));
        Titlebar.setBackground(Color.getHSBColor(0.f, 0.0f, 0.6f));
        Titlebar.setMinimumSize(TitleBarMinimumSize);
        Titlebar.setMaximumSize(TitleBarMaximumSize);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        setOpaque(true);
        resolveType();
        setVisible(true);
//        revalidate();

        popup = new PopupMenu();

        ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (patch != null) {
                    if (me.getClickCount() == 1) {
                        if (me.isShiftDown()) {
                            SetSelected(!GetSelected());
                            ((PatchGUI) patch).repaint();
                        } else if (Selected == false) {
                            ((PatchGUI) patch).SelectNone();
                            SetSelected(true);
                            ((PatchGUI) patch).repaint();
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
                if (me.isPopupTrigger()) {

                } else if (!IsLocked()) {
                    dX = me.getXOnScreen() - getX();
                    dY = me.getYOnScreen() - getY();
                    dragging = true;
                    if (IsSelected()) {
                        for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                            if (o.IsSelected()) {
                                o.dX = me.getXOnScreen() - o.getX();
                                o.dY = me.getYOnScreen() - o.getY();
                                o.dragging = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (dragging) {
                    dragging = false;
                    if (patch != null) {
                        for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                            o.dragging = false;
                        }
                        patch.AdjustSize();
                    }
                }
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
                    if (dragging) {/*
                         x = me.getLocationOnScreen().x - dX;
                         y = me.getLocationOnScreen().y - dY;
                         setLocation(x, y);
                         dX = me.getLocationOnScreen().x - getX();
                         dY = me.getLocationOnScreen().y - getY();*/

                        for (AxoObjectInstanceAbstract o : patch.objectinstances) {
                            if (o.dragging) {
                                o.x = me.getLocationOnScreen().x - o.dX;
                                o.y = me.getLocationOnScreen().y - o.dY;
                                o.dX = me.getLocationOnScreen().x - o.getX();
                                o.dY = me.getLocationOnScreen().y - o.getY();
                                if (!me.isShiftDown()) {
                                    o.x = ((o.x + (Constants.xgrid / 2)) / Constants.xgrid) * Constants.xgrid;
                                    o.y = ((o.y + (Constants.ygrid / 2)) / Constants.ygrid) * Constants.ygrid;
                                }
                                o.setLocation(o.x, o.y);
                            }
                        }
                        //patch.invalidate();
                        patch.repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
            }
        };

        Titlebar.addMouseMotionListener(mml);
        addMouseMotionListener(mml);
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
//        InstanceNameTF.setInputVerifier(new AxoObjectInstanceNameVerifier());
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
                patch.repaint();
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
                    patch.repaint();
                }
            }
        });

        getParent().add(InstanceNameTF, 0);
        InstanceNameTF.setLocation(getLocation().x, getLocation().y + InstanceLabel.getLocation().y);
        InstanceNameTF.setSize(getWidth(), 15);
        InstanceNameTF.setVisible(true);
        InstanceNameTF.requestFocus();
        //patch.repaint();
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
                setBorder(BorderFactory.createLineBorder(Color.BLACK));
            } else {
                setBorder(BorderFactory.createLineBorder(Color.WHITE));
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
        d.width = ((d.width + Constants.xgrid - 1) / Constants.xgrid) * Constants.xgrid;
        d.height = ((d.height + Constants.ygrid - 1) / Constants.ygrid) * Constants.ygrid;
        setSize(d);
    }
}
