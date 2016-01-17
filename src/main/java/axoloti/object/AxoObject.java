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

import axoloti.Modulator;
import axoloti.Patch;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.attributedefinition.AxoAttributeInt32;
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.attributedefinition.AxoAttributeWavefile;
import axoloti.dialogs.AxoObjectEditor;
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
import displays.Display;
import displays.DisplayBool32;
import displays.DisplayFrac32SChart;
import displays.DisplayFrac32SDial;
import displays.DisplayFrac32UChart;
import displays.DisplayFrac32UDial;
import displays.DisplayFrac32VBar;
import displays.DisplayFrac32VBarDB;
import displays.DisplayFrac32VU;
import displays.DisplayFrac4ByteVBar;
import displays.DisplayFrac4UByteVBar;
import displays.DisplayFrac4UByteVBarDB;
import displays.DisplayFrac8S128VBar;
import displays.DisplayFrac8U128VBar;
import displays.DisplayInt32Bar16;
import displays.DisplayInt32Bar32;
import displays.DisplayInt32HexLabel;
import displays.DisplayInt32Label;
import displays.DisplayNoteLabel;
import displays.DisplayVScale;
import java.awt.Point;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
        @ElementList(entry = "bool32", type = InletBool32.class, inline = true, required = false),
        @ElementList(entry = "bool32.rising", type = InletBool32Rising.class, inline = true, required = false),
        @ElementList(entry = "bool32.risingfalling", type = InletBool32RisingFalling.class, inline = true, required = false),
        @ElementList(entry = "frac32", type = InletFrac32.class, inline = true, required = false),
        @ElementList(entry = "frac32.positive", type = InletFrac32Pos.class, inline = true, required = false),
        @ElementList(entry = "frac32.bipolar", type = InletFrac32Bipolar.class, inline = true, required = false),
        @ElementList(entry = "charptr32", type = InletCharPtr32.class, inline = true, required = false),
        @ElementList(entry = "int32", type = InletInt32.class, inline = true, required = false),
        @ElementList(entry = "int32.positive", type = InletInt32Pos.class, inline = true, required = false),
        @ElementList(entry = "int32.bipolar", type = InletInt32Bipolar.class, inline = true, required = false),
        @ElementList(entry = "frac32buffer", type = InletFrac32Buffer.class, inline = true, required = false),
        @ElementList(entry = "frac32buffer.positive", type = InletFrac32BufferPos.class, inline = true, required = false),
        @ElementList(entry = "frac32buffer.bipolar", type = InletFrac32BufferBipolar.class, inline = true, required = false)
    })
    public ArrayList<Inlet> inlets;
    @Path("outlets")
    @ElementListUnion({
        @ElementList(entry = "bool32", type = OutletBool32.class, inline = true, required = false),
        @ElementList(entry = "bool32.pulse", type = OutletBool32Pulse.class, inline = true, required = false),
        @ElementList(entry = "frac32", type = OutletFrac32.class, inline = true, required = false),
        @ElementList(entry = "frac32.positive", type = OutletFrac32Pos.class, inline = true, required = false),
        @ElementList(entry = "frac32.bipolar", type = OutletFrac32Bipolar.class, inline = true, required = false),
        @ElementList(entry = "charptr32", type = OutletCharPtr32.class, inline = true, required = false),
        @ElementList(entry = "int32", type = OutletInt32.class, inline = true, required = false),
        @ElementList(entry = "int32.positive", type = OutletInt32Pos.class, inline = true, required = false),
        @ElementList(entry = "int32.bipolar", type = OutletInt32Bipolar.class, inline = true, required = false),
        @ElementList(entry = "frac32buffer", type = OutletFrac32Buffer.class, inline = true, required = false),
        @ElementList(entry = "frac32buffer.positive", type = OutletFrac32BufferPos.class, inline = true, required = false),
        @ElementList(entry = "frac32buffer.bipolar", type = OutletFrac32BufferBipolar.class, inline = true, required = false)
    })
    public ArrayList<Outlet> outlets;
    @Path("displays")
    @ElementListUnion({
        @ElementList(entry = "bool32", type = DisplayBool32.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.chart", type = DisplayFrac32SChart.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.chart", type = DisplayFrac32UChart.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.dial", type = DisplayFrac32SDial.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.dial", type = DisplayFrac32UDial.class, inline = true, required = false),
        @ElementList(entry = "frac32.vu", type = DisplayFrac32VU.class, inline = true, required = false),
        @ElementList(entry = "frac32.vbar", type = DisplayFrac32VBar.class, inline = true, required = false),
        @ElementList(entry = "frac32.vbar.db", type = DisplayFrac32VBarDB.class, inline = true, required = false),
        @ElementList(entry = "frac4byte.vbar", type = DisplayFrac4ByteVBar.class, inline = true, required = false),
        @ElementList(entry = "frac4ubyte.vbar", type = DisplayFrac4UByteVBar.class, inline = true, required = false),
        @ElementList(entry = "frac4ubyte.vbar.db", type = DisplayFrac4UByteVBarDB.class, inline = true, required = false),
        @ElementList(entry = "int32.label", type = DisplayInt32Label.class, inline = true, required = false),
        @ElementList(entry = "int32.hexlabel", type = DisplayInt32HexLabel.class, inline = true, required = false),
        @ElementList(entry = "int32.bar16", type = DisplayInt32Bar16.class, inline = true, required = false),
        @ElementList(entry = "int32.bar32", type = DisplayInt32Bar32.class, inline = true, required = false),
        @ElementList(entry = "vscale", type = DisplayVScale.class, inline = true, required = false),
        @ElementList(entry = "int8array128.vbar", type = DisplayFrac8S128VBar.class, inline = true, required = false),
        @ElementList(entry = "uint8array128.vbar", type = DisplayFrac8U128VBar.class, inline = true, required = false),
        @ElementList(entry = "note.label", type = DisplayNoteLabel.class, inline = true, required = false)
    })
    public ArrayList<Display> displays; // readouts
    @Path("params")
    @ElementListUnion({
        @ElementList(entry = "frac32.u.map", type = ParameterFrac32UMap.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.freq", type = ParameterFrac32UMapFreq.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.kdecaytime", type = ParameterFrac32UMapKDecayTime.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.kdecaytime.reverse", type = ParameterFrac32UMapKDecayTimeReverse.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.klineartime.reverse", type = ParameterFrac32UMapKLineTimeReverse.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.gain", type = ParameterFrac32UMapGain.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.gain16", type = ParameterFrac32UMapGain16.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.squaregain", type = ParameterFrac32UMapGainSquare.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.ratio", type = ParameterFrac32UMapRatio.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.map.filterq", type = ParameterFrac32UMapFilterQ.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map", type = ParameterFrac32SMap.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map.pitch", type = ParameterFrac32SMapPitch.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map.kpitch", type = ParameterFrac32SMapKPitch.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map.lfopitch", type = ParameterFrac32SMapLFOPitch.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map.klineartime.exp", type = ParameterFrac32SMapKLineTimeExp.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map.klineartime.exp2", type = ParameterFrac32SMapKLineTimeExp2.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.mapvsl", type = ParameterFrac32UMapVSlider.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.mapvsl", type = ParameterFrac32SMapVSlider.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map.ratio", type = ParameterFrac32SMapRatio.class, inline = true, required = false),
        @ElementList(entry = "int32", type = ParameterInt32Box.class, inline = true, required = false),
        @ElementList(entry = "int32.mini", type = ParameterInt32BoxSmall.class, inline = true, required = false),
        @ElementList(entry = "int32.hradio", type = ParameterInt32HRadio.class, inline = true, required = false),
        @ElementList(entry = "int32.vradio", type = ParameterInt32VRadio.class, inline = true, required = false),
        @ElementList(entry = "int2x16", type = Parameter4LevelX16.class, inline = true, required = false),
        @ElementList(entry = "bin12", type = ParameterBin12.class, inline = true, required = false),
        @ElementList(entry = "bin16", type = ParameterBin16.class, inline = true, required = false),
        @ElementList(entry = "bin32", type = ParameterBin32.class, inline = true, required = false),
        @ElementList(entry = "bool32.tgl", type = ParameterBin1.class, inline = true, required = false),
        @ElementList(entry = "bool32.mom", type = ParameterBin1Momentary.class, inline = true, required = false)
    })
    public ArrayList<Parameter> params; // variables
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = "objref", type = AxoAttributeObjRef.class, inline = true, required = false),
        @ElementList(entry = "table", type = AxoAttributeTablename.class, inline = true, required = false),
        @ElementList(entry = "combo", type = AxoAttributeComboBox.class, inline = true, required = false),
        @ElementList(entry = "int", type = AxoAttributeInt32.class, inline = true, required = false),
        @ElementList(entry = "spinner", type = AxoAttributeSpinner.class, inline = true, required = false),
        @ElementList(entry = "file", type = AxoAttributeWavefile.class, inline = true, required = false),
        @ElementList(entry = "text", type = AxoAttributeTextEditor.class, inline = true, required = false)})
    public ArrayList<AxoAttribute> attributes; // literal constants
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

    ArrayList<AxoObjectInstance> instances;
    AxoObjectEditor editor;

    public void OpenEditor() {
        if (editor == null) {
            editor = new AxoObjectEditor(this);
        }
        editor.setState(java.awt.Frame.NORMAL);
        editor.setVisible(true);
    }

    @Override
    public void DeleteInstance(AxoObjectInstanceAbstract o) {
        if ((o != null) && (o instanceof AxoObjectInstance)) {
            if (instances != null) {
                instances.remove((AxoObjectInstance) o);
            }
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
        if (patch != null) {
            patch.objectinstances.add(o);
        }
        o.PostConstructor();

        if (patch != null) {
            if (instances == null) {
                instances = new ArrayList<AxoObjectInstance>();
            }
            instances.add(o);
        }
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
    public String GenerateSHA() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            for (Inlet i : inlets) {
                i.updateSHA(md);
            }
            for (Outlet i : outlets) {
                i.updateSHA(md);
            }
            for (Parameter i : params) {
                i.updateSHA(md);
            }
            for (AxoAttribute i : attributes) {
                i.updateSHA(md);
            }
            for (Display i : displays) {
                i.updateSHA(md);
            }
            if (sLocalData != null) {
                md.update(sLocalData.getBytes());
            }
            if (sInitCode != null) {
                md.update(sInitCode.getBytes());
            }
            if (sKRateCode != null) {
                md.update(sKRateCode.getBytes());
            }
            if (sSRateCode != null) {
                md.update(sSRateCode.getBytes());
            }
            if (sMidiCode != null) {
                md.update(sMidiCode.getBytes());
            }
            return (new BigInteger(1, md.digest())).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AxoObject.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }

    @Override
    public String GenerateUUID() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(id.getBytes());
            for (Inlet i : inlets) {
                i.updateSHA(md);
            }
            for (Outlet i : outlets) {
                i.updateSHA(md);
            }
            for (Parameter i : params) {
                i.updateSHA(md);
            }
            for (AxoAttribute i : attributes) {
                i.updateSHA(md);
            }
            for (Display i : displays) {
                i.updateSHA(md);
            }
            return  (new BigInteger(1, md.digest())).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AxoObject.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
                    //Logger.getLogger(AxoObject.class.getName()).log(Level.SEVERE, "\"../\" prefix in object include not implemented...");
                } else if (s.startsWith("chibios/")) {
                    r.add((new File("chibios/")).getAbsolutePath() + s.substring(7));
                } else {
                    r.add(s);
                }
            }
            return r;
        } else {
            if (includes.isEmpty()) {
                return null;
            } else {
                return includes;
            }
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
        if (helpPatch == null) {
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

}
