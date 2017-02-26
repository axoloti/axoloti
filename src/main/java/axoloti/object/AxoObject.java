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

import static axoloti.Axoloti.FIRMWARE_DIR;
import axoloti.Modulator;
import axoloti.Patch;
import axoloti.SDFileReference;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.attributedefinition.AxoAttributeInt32;
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.attributedefinition.AxoAttributeSDFile;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.displays.Display;
import axoloti.displays.DisplayBool32;
import axoloti.displays.DisplayFrac32SChart;
import axoloti.displays.DisplayFrac32SDial;
import axoloti.displays.DisplayFrac32UChart;
import axoloti.displays.DisplayFrac32UDial;
import axoloti.displays.DisplayFrac32VBar;
import axoloti.displays.DisplayFrac32VBarDB;
import axoloti.displays.DisplayFrac32VU;
import axoloti.displays.DisplayFrac4ByteVBar;
import axoloti.displays.DisplayFrac4UByteVBar;
import axoloti.displays.DisplayFrac4UByteVBarDB;
import axoloti.displays.DisplayFrac8S128VBar;
import axoloti.displays.DisplayFrac8U128VBar;
import axoloti.displays.DisplayInt32Bar16;
import axoloti.displays.DisplayInt32Bar32;
import axoloti.displays.DisplayInt32HexLabel;
import axoloti.displays.DisplayInt32Label;
import axoloti.displays.DisplayNoteLabel;
import axoloti.displays.DisplayVScale;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletBool32RisingFalling;
import axoloti.inlets.InletCharPtr32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Bipolar;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletFrac32BufferBipolar;
import axoloti.inlets.InletFrac32BufferPos;
import axoloti.inlets.InletFrac32Pos;
import axoloti.inlets.InletInt32;
import axoloti.inlets.InletInt32Bipolar;
import axoloti.inlets.InletInt32Pos;
import axoloti.objecteditor.AxoObjectEditor;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletBool32Pulse;
import axoloti.outlets.OutletCharPtr32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletFrac32BufferBipolar;
import axoloti.outlets.OutletFrac32BufferPos;
import axoloti.outlets.OutletFrac32Pos;
import axoloti.outlets.OutletInt32;
import axoloti.outlets.OutletInt32Bipolar;
import axoloti.outlets.OutletInt32Pos;
import axoloti.parameters.Parameter;
import axoloti.parameters.Parameter4LevelX16;
import axoloti.parameters.ParameterBin1;
import axoloti.parameters.ParameterBin12;
import axoloti.parameters.ParameterBin16;
import axoloti.parameters.ParameterBin1Momentary;
import axoloti.parameters.ParameterBin32;
import axoloti.parameters.ParameterFrac32SMap;
import axoloti.parameters.ParameterFrac32SMapKDTimeExp;
import axoloti.parameters.ParameterFrac32SMapKLineTimeExp;
import axoloti.parameters.ParameterFrac32SMapKLineTimeExp2;
import axoloti.parameters.ParameterFrac32SMapKPitch;
import axoloti.parameters.ParameterFrac32SMapLFOPitch;
import axoloti.parameters.ParameterFrac32SMapPitch;
import axoloti.parameters.ParameterFrac32SMapRatio;
import axoloti.parameters.ParameterFrac32SMapVSlider;
import axoloti.parameters.ParameterFrac32UMap;
import axoloti.parameters.ParameterFrac32UMapFilterQ;
import axoloti.parameters.ParameterFrac32UMapFreq;
import axoloti.parameters.ParameterFrac32UMapGain;
import axoloti.parameters.ParameterFrac32UMapGain16;
import axoloti.parameters.ParameterFrac32UMapGainSquare;
import axoloti.parameters.ParameterFrac32UMapKDecayTime;
import axoloti.parameters.ParameterFrac32UMapKDecayTimeReverse;
import axoloti.parameters.ParameterFrac32UMapKLineTimeReverse;
import axoloti.parameters.ParameterFrac32UMapRatio;
import axoloti.parameters.ParameterFrac32UMapVSlider;
import axoloti.parameters.ParameterInt32Box;
import axoloti.parameters.ParameterInt32BoxSmall;
import axoloti.parameters.ParameterInt32HRadio;
import axoloti.parameters.ParameterInt32VRadio;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class AxoObject extends AxoObjectAbstract {

    @Element(required = false)
    public String helpPatch;
    @Element(required = false)
    private Boolean providesModulationSource;
    @Element(required = false)
    private Boolean rotatedParams;
    @ElementList(required = false)
    public ArrayList<String> ModulationSources;
    @Path("inlets")
    @ElementListUnion({
        @ElementList(entry = InletBool32.TypeName, type = InletBool32.class, inline = true, required = false),
        @ElementList(entry = InletBool32Rising.TypeName, type = InletBool32Rising.class, inline = true, required = false),
        @ElementList(entry = InletBool32RisingFalling.TypeName, type = InletBool32RisingFalling.class, inline = true, required = false),
        @ElementList(entry = InletFrac32.TypeName, type = InletFrac32.class, inline = true, required = false),
        @ElementList(entry = InletFrac32Pos.TypeName, type = InletFrac32Pos.class, inline = true, required = false),
        @ElementList(entry = InletFrac32Bipolar.TypeName, type = InletFrac32Bipolar.class, inline = true, required = false),
        @ElementList(entry = InletCharPtr32.TypeName, type = InletCharPtr32.class, inline = true, required = false),
        @ElementList(entry = InletInt32.TypeName, type = InletInt32.class, inline = true, required = false),
        @ElementList(entry = InletInt32Pos.TypeName, type = InletInt32Pos.class, inline = true, required = false),
        @ElementList(entry = InletInt32Bipolar.TypeName, type = InletInt32Bipolar.class, inline = true, required = false),
        @ElementList(entry = InletFrac32Buffer.TypeName, type = InletFrac32Buffer.class, inline = true, required = false),
        @ElementList(entry = InletFrac32BufferPos.TypeName, type = InletFrac32BufferPos.class, inline = true, required = false),
        @ElementList(entry = InletFrac32BufferBipolar.TypeName, type = InletFrac32BufferBipolar.class, inline = true, required = false)
    })
    public ArrayList<Inlet> inlets;
    @Path("outlets")
    @ElementListUnion({
        @ElementList(entry = OutletBool32.TypeName, type = OutletBool32.class, inline = true, required = false),
        @ElementList(entry = OutletBool32Pulse.TypeName, type = OutletBool32Pulse.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32.TypeName, type = OutletFrac32.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32Pos.TypeName, type = OutletFrac32Pos.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32Bipolar.TypeName, type = OutletFrac32Bipolar.class, inline = true, required = false),
        @ElementList(entry = OutletCharPtr32.TypeName, type = OutletCharPtr32.class, inline = true, required = false),
        @ElementList(entry = OutletInt32.TypeName, type = OutletInt32.class, inline = true, required = false),
        @ElementList(entry = OutletInt32Pos.TypeName, type = OutletInt32Pos.class, inline = true, required = false),
        @ElementList(entry = OutletInt32Bipolar.TypeName, type = OutletInt32Bipolar.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32Buffer.TypeName, type = OutletFrac32Buffer.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32BufferPos.TypeName, type = OutletFrac32BufferPos.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32BufferBipolar.TypeName, type = OutletFrac32BufferBipolar.class, inline = true, required = false)
    })
    public ArrayList<Outlet> outlets;
    @Path("displays")
    @ElementListUnion({
        @ElementList(entry = DisplayBool32.TypeName, type = DisplayBool32.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32SChart.TypeName, type = DisplayFrac32SChart.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32UChart.TypeName, type = DisplayFrac32UChart.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32SDial.TypeName, type = DisplayFrac32SDial.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32UDial.TypeName, type = DisplayFrac32UDial.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32VU.TypeName, type = DisplayFrac32VU.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32VBar.TypeName, type = DisplayFrac32VBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32VBarDB.TypeName, type = DisplayFrac32VBarDB.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac4ByteVBar.TypeName, type = DisplayFrac4ByteVBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac4UByteVBar.TypeName, type = DisplayFrac4UByteVBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac4UByteVBarDB.TypeName, type = DisplayFrac4UByteVBarDB.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32Label.TypeName, type = DisplayInt32Label.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32HexLabel.TypeName, type = DisplayInt32HexLabel.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32Bar16.TypeName, type = DisplayInt32Bar16.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32Bar32.TypeName, type = DisplayInt32Bar32.class, inline = true, required = false),
        @ElementList(entry = DisplayVScale.TypeName, type = DisplayVScale.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac8S128VBar.TypeName, type = DisplayFrac8S128VBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac8U128VBar.TypeName, type = DisplayFrac8U128VBar.class, inline = true, required = false),
        @ElementList(entry = DisplayNoteLabel.TypeName, type = DisplayNoteLabel.class, inline = true, required = false)
    })
    public ArrayList<Display> displays; // readouts
    @Path("params")
    @ElementListUnion({
        @ElementList(entry = ParameterFrac32UMap.TypeName, type = ParameterFrac32UMap.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapFreq.TypeName, type = ParameterFrac32UMapFreq.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapKDecayTime.TypeName, type = ParameterFrac32UMapKDecayTime.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapKDecayTimeReverse.TypeName, type = ParameterFrac32UMapKDecayTimeReverse.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapKLineTimeReverse.TypeName, type = ParameterFrac32UMapKLineTimeReverse.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapGain.TypeName, type = ParameterFrac32UMapGain.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapGain16.TypeName, type = ParameterFrac32UMapGain16.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapGainSquare.TypeName, type = ParameterFrac32UMapGainSquare.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapRatio.TypeName, type = ParameterFrac32UMapRatio.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapFilterQ.TypeName, type = ParameterFrac32UMapFilterQ.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMap.TypeName, type = ParameterFrac32SMap.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapPitch.TypeName, type = ParameterFrac32SMapPitch.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKDTimeExp.TypeName, type = ParameterFrac32SMapKDTimeExp.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKPitch.TypeName, type = ParameterFrac32SMapKPitch.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapLFOPitch.TypeName, type = ParameterFrac32SMapLFOPitch.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKLineTimeExp.TypeName, type = ParameterFrac32SMapKLineTimeExp.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKLineTimeExp2.TypeName, type = ParameterFrac32SMapKLineTimeExp2.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapVSlider.TypeName, type = ParameterFrac32UMapVSlider.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapVSlider.TypeName, type = ParameterFrac32SMapVSlider.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapRatio.TypeName, type = ParameterFrac32SMapRatio.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32Box.TypeName, type = ParameterInt32Box.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32BoxSmall.TypeName, type = ParameterInt32BoxSmall.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32HRadio.TypeName, type = ParameterInt32HRadio.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32VRadio.TypeName, type = ParameterInt32VRadio.class, inline = true, required = false),
        @ElementList(entry = Parameter4LevelX16.TypeName, type = Parameter4LevelX16.class, inline = true, required = false),
        @ElementList(entry = ParameterBin12.TypeName, type = ParameterBin12.class, inline = true, required = false),
        @ElementList(entry = ParameterBin16.TypeName, type = ParameterBin16.class, inline = true, required = false),
        @ElementList(entry = ParameterBin32.TypeName, type = ParameterBin32.class, inline = true, required = false),
        @ElementList(entry = ParameterBin1.TypeName, type = ParameterBin1.class, inline = true, required = false),
        @ElementList(entry = ParameterBin1Momentary.TypeName, type = ParameterBin1Momentary.class, inline = true, required = false)
    })
    public ArrayList<Parameter> params; // variables
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = AxoAttributeObjRef.TypeName, type = AxoAttributeObjRef.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeTablename.TypeName, type = AxoAttributeTablename.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeComboBox.TypeName, type = AxoAttributeComboBox.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeInt32.TypeName, type = AxoAttributeInt32.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeSpinner.TypeName, type = AxoAttributeSpinner.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeSDFile.TypeName, type = AxoAttributeSDFile.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeTextEditor.TypeName, type = AxoAttributeTextEditor.class, inline = true, required = false)})
    public ArrayList<AxoAttribute> attributes; // literal constants
    @ElementList(name = "file-depends", entry = "file-depend", type = SDFileReference.class, required = false)
    public ArrayList<SDFileReference> filedepends;
    @ElementList(name = "includes", entry = "include", type = String.class, required = false)
    public HashSet<String> includes;
    @ElementList(name = "depends", entry = "depend", type = String.class, required = false)
    public HashSet<String> depends;

    @Element(name = "code.declaration", required = false, data = true)
    public String sLocalData;
    @Element(name = "code.init", required = false, data = true)
    public String sInitCode;
    @Element(name = "code.dispose", required = false, data = true)
    public String sDisposeCode;
    @Element(name = "code.krate", required = false, data = true)
    public String sKRateCode;
    @Element(name = "code.srate", required = false, data = true)
    public String sSRateCode;
    @Element(name = "code.midihandler", required = false, data = true)
    public String sMidiCode;

    @Element(name = "code.midicc", required = false, data = true)
    @Deprecated
    public String sMidiCCCode;
    @Element(name = "code.midinoteon", required = false, data = true)
    @Deprecated
    public String sMidiNoteOnCode;
    @Element(name = "code.midinoteoff", required = false, data = true)
    @Deprecated
    public String sMidiNoteOffCode;
    @Element(name = "code.midipbend", required = false, data = true)
    @Deprecated
    public String sMidiPBendCode;
    @Element(name = "code.midichannelpressure", required = false, data = true)
    @Deprecated
    public String sMidiChannelPressure;
    @Element(name = "code.midiallnotesoff", required = false, data = true)
    @Deprecated
    public String sMidiAllNotesOffCode;
    @Element(name = "code.midiresetcontrollers", required = false, data = true)
    @Deprecated
    public String sMidiResetControllersCode;

    public AxoObject() {
        inlets = new ArrayList<Inlet>();
        outlets = new ArrayList<Outlet>();
        displays = new ArrayList<Display>();
        params = new ArrayList<Parameter>();
        attributes = new ArrayList<AxoAttribute>();
        includes = new HashSet<String>();
    }

    public AxoObject(String id, String sDescription) {
        super(id, sDescription);
        inlets = new ArrayList<Inlet>();
        outlets = new ArrayList<Outlet>();
        displays = new ArrayList<Display>();
        params = new ArrayList<Parameter>();
        attributes = new ArrayList<AxoAttribute>();
        includes = new HashSet<String>();
    }

    ArrayList<ObjectModifiedListener> instances = new ArrayList<ObjectModifiedListener>();
    AxoObjectEditor editor;
    
    Rectangle editorBounds;
    Integer editorActiveTabIndex;
    
    private void setEditorBounds(Rectangle editorBounds) {
        if(editorBounds != null) {
            editor.setBounds(editorBounds);
        }
        else if(this.editorBounds != null) {
            editor.setBounds(this.editorBounds);
        }
        
    }
    
    private void setEditorActiveTabIndex(Integer editorActiveTabIndex) {
        if(editorActiveTabIndex != null) {
            editor.setActiveTabIndex(editorActiveTabIndex);
        }
        else if(this.editorActiveTabIndex != null) {
            editor.setActiveTabIndex(this.editorActiveTabIndex);
        }
    }
    
    public void OpenEditor(Rectangle editorBounds, Integer editorActiveTabIndex) {
        if (editor == null) {
            editor = new AxoObjectEditor(this);
        }
        
        setEditorBounds(editorBounds);
        setEditorActiveTabIndex(editorActiveTabIndex);
        
        editor.setState(java.awt.Frame.NORMAL);
        editor.setVisible(true);
    }

    public void CloseEditor() {
        FireObjectModified(this);
        editor = null;
    }

    @Override
    public void DeleteInstance(AxoObjectInstanceAbstract o) {
        if ((o != null) && (o instanceof AxoObjectInstance)) {
            instances.remove((AxoObjectInstance) o);
        }
    }

    @Override
    public AxoObjectInstance CreateInstance(Patch patch, String InstanceName1, Point location) {
        if (patch != null) {
            if ((sMidiCCCode != null)
                    || (sMidiAllNotesOffCode != null)
                    || (sMidiCCCode != null)
                    || (sMidiChannelPressure != null)
                    || (sMidiNoteOffCode != null)
                    || (sMidiNoteOnCode != null)
                    || (sMidiPBendCode != null)
                    || (sMidiResetControllersCode != null)) {
                Logger.getLogger(AxoObject.class.getName()).log(Level.SEVERE, "Object {0} uses obsolete midi handling. If it is a subpatch-generated object, open and save the original patch again!", InstanceName1);
            }
        }

        AxoObjectInstance o = new AxoObjectInstance(this, patch, InstanceName1, location);
//        System.out.println("object " + o);
//        Thread.dumpStack();
        if (patch != null) {
            patch.objectinstances.add(o);
        }
        o.PostConstructor();
        return o;
    }

    @Override
    public boolean providesModulationSource() {
        if ((ModulationSources != null) && (!ModulationSources.isEmpty())) {
            return true;
        }
        if (providesModulationSource == null) {
            return false;
        }
        return providesModulationSource;
    }

    public void SetProvidesModulationSource() {
        providesModulationSource = true;
    }

    @Override
    public Modulator[] getModulators() {
        if ((providesModulationSource != null) && (providesModulationSource)) {
            Modulator[] m = new Modulator[1];
            //m[0].objinst = this;
            m[0] = new Modulator();
            m[0].name = "";
            return m;
        } else if ((ModulationSources != null) && (!ModulationSources.isEmpty())) {
            Modulator[] m = new Modulator[ModulationSources.size()];
            for (int i = 0; i < ModulationSources.size(); i++) {
                String n = ModulationSources.get(i);
                m[i] = new Modulator();
                m[i].name = n;
            }
            return m;
        } else {
            return null;
        }
    }

    @Override
    public Inlet GetInlet(String n) {
        for (Inlet i : inlets) {
            if (i.getName().equals(n)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public Outlet GetOutlet(String n) {
        for (Outlet i : outlets) {
            if (i.getName().equals(n)) {
                return i;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Inlet> GetInlets() {
        return inlets;
    }

    @Override
    public ArrayList<Outlet> GetOutlets() {
        return outlets;
    }

    @Override
    public String getCName() {
        return id;
    }

    @Override
    public String GenerateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public Boolean getRotatedParams() {
        if (rotatedParams == null) {
            return false;
        } else {
            return rotatedParams;
        }
    }

    public void setRotatedParams(boolean rotatedParams) {
        this.rotatedParams = rotatedParams;
    }

    private static String getRelativePath(String baseDir, String targetPath) {
        String[] base = baseDir.replace('\\', '/').split("\\/");
        targetPath = targetPath.replace('\\', '/');
        String[] target = targetPath.split("\\/");

        // Count common elements and their length.
        int commonCount = 0, commonLength = 0, maxCount = Math.min(target.length, base.length);
        while (commonCount < maxCount) {
            String targetElement = target[commonCount];
            if (!targetElement.equals(base[commonCount])) {
                break;
            }
            commonCount++;
            commonLength += targetElement.length() + 1; // Directory name length plus slash.
        }
        if (commonCount == 0) {
            return targetPath; // No common path element.
        }
        int targetLength = targetPath.length();
        int dirsUp = base.length - commonCount;
        StringBuilder relative = new StringBuilder(dirsUp * 3 + targetLength - commonLength + 1);
        for (int i = 0; i < dirsUp; i++) {
            relative.append("../");
        }
        if (commonLength < targetLength) {
            relative.append(targetPath.substring(commonLength));
        }
        return relative.toString();
    }

    @Override
    public HashSet<String> GetIncludes() {
        if ((includes == null) || includes.isEmpty()) {
            return null;
        } else if (sPath != null) {
            HashSet<String> r = new HashSet<String>();
            for (String s : includes) {
                if (s.startsWith("./")) {
                    String strippedPath = sPath.substring(0, sPath.lastIndexOf(File.separatorChar));
                    File f = new File(strippedPath + "/" + s.substring(2));
                    String s2 = f.getAbsolutePath();
                    s2 = s2.replace('\\', '/');
                    r.add(s2);
                } else if (s.startsWith("../")) {
                    String strippedPath = sPath.substring(0, sPath.lastIndexOf(File.separatorChar));
                    File f = new File(strippedPath + "/" + s);
                    String s2 = f.getAbsolutePath();
                    s2 = s2.replace('\\', '/');
                    r.add(s2);
                } else if (s.startsWith("chibios/")) {
                    r.add((new File(System.getProperty(FIRMWARE_DIR))).getAbsolutePath() + "/../chibios" + s.substring(7));
                } else {
                    r.add(s);
                }
            }
            return r;
        } else if (includes.isEmpty()) {
            return null;
        } else {
            return includes;
        }
    }

    @Override
    public void SetIncludes(HashSet<String> includes) {
        this.includes = includes;
    }

    @Override
    public Set<String> GetDepends() {
        return depends;
    }

    public File GetHelpPatchFile() {
        if ((helpPatch == null) || (sPath == null) || sPath.isEmpty()) {
            return null;
        }
        File o = new File(sPath);
        String p = o.getParent() + File.separator + helpPatch;
        File f = new File(p);
        if (f.isFile() && f.canRead()) {
            return f;
        } else {
            return null;
        }
    }

    @Override
    public void FireObjectModified(Object src) {
        ArrayList<ObjectModifiedListener> c = new ArrayList<ObjectModifiedListener>(instances);
        for (ObjectModifiedListener oml : c) {
            oml.ObjectModified(src);
        }
    }

    @Override
    public void addObjectModifiedListener(ObjectModifiedListener oml) {
        if (!instances.contains(oml)) {
            instances.add(oml);
        }
    }

    @Override
    public void removeObjectModifiedListener(ObjectModifiedListener oml) {
        instances.remove(oml);
    }

    @Override
    public AxoObject clone() throws CloneNotSupportedException {
        AxoObject c = (AxoObject) super.clone();
        c.inlets = new ArrayList<Inlet>();
        for (Inlet i : inlets) {
            c.inlets.add(i.clone());
        }
        c.outlets = new ArrayList<Outlet>();
        for (Outlet i : outlets) {
            c.outlets.add(i.clone());
        }
        c.params = new ArrayList<Parameter>();
        for (Parameter i : params) {
            c.params.add(i.clone());
        }
        c.displays = new ArrayList<Display>();
        for (Display i : displays) {
            c.displays.add(i.clone());
        }
        c.attributes = new ArrayList<AxoAttribute>();
        for (AxoAttribute i : attributes) {
            c.attributes.add(i.clone());
        }
        return c;
    }

    public void copy(AxoObject o) throws CloneNotSupportedException {

        inlets = new ArrayList<Inlet>();
        for (Inlet i : o.inlets) {
            inlets.add(i.clone());
        }
        outlets = new ArrayList<Outlet>();
        for (Outlet i : o.outlets) {
            outlets.add(i.clone());
        }
        params = new ArrayList<Parameter>();
        for (Parameter i : o.params) {
            params.add(i.clone());
        }
        displays = new ArrayList<Display>();
        for (Display i : o.displays) {
            displays.add(i.clone());
        }
        attributes = new ArrayList<AxoAttribute>();
        for (AxoAttribute i : o.attributes) {
            attributes.add(i.clone());
        }

        helpPatch = o.helpPatch;
        providesModulationSource = o.providesModulationSource;
        rotatedParams = o.rotatedParams;
        if (o.ModulationSources != null) {
            ModulationSources = (ArrayList<String>) o.ModulationSources.clone();
        } else {
            ModulationSources = null;
        }
        if (o.includes != null) {
            includes = (HashSet<String>) o.includes.clone();
        } else {
            o.includes = null;
        }
        if (o.depends != null) {
            depends = (HashSet<String>) o.depends.clone();
        } else {
            o.depends = null;

        }
        sLocalData = o.sLocalData;
        sInitCode = o.sInitCode;
        sDisposeCode = o.sDisposeCode;
        sKRateCode = o.sKRateCode;
        sSRateCode = o.sSRateCode;
        sMidiCode = o.sMidiCode;
        sAuthor = o.sAuthor;
        sLicense = o.sLicense;
        sDescription = o.sDescription;
    }
}
