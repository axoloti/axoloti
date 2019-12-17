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

import axoloti.object.attribute.AxoAttribute;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.object.attribute.AxoAttributeInt32;
import axoloti.object.attribute.AxoAttributeMidiInPort;
import axoloti.object.attribute.AxoAttributeMidiOutPort;
import axoloti.object.attribute.AxoAttributeObjRef;
import axoloti.object.attribute.AxoAttributeSDFile;
import axoloti.object.attribute.AxoAttributeSpinner;
import axoloti.object.attribute.AxoAttributeTablename;
import axoloti.object.attribute.AxoAttributeTextEditor;
import axoloti.object.display.Display;
import axoloti.object.display.DisplayBool32;
import axoloti.object.display.DisplayFrac32SChart;
import axoloti.object.display.DisplayFrac32SDial;
import axoloti.object.display.DisplayFrac32UChart;
import axoloti.object.display.DisplayFrac32UDial;
import axoloti.object.display.DisplayFrac32VBar;
import axoloti.object.display.DisplayFrac32VBarDB;
import axoloti.object.display.DisplayFrac32VU;
import axoloti.object.display.DisplayFrac4ByteVBar;
import axoloti.object.display.DisplayFrac4UByteVBar;
import axoloti.object.display.DisplayFrac4UByteVBarDB;
import axoloti.object.display.DisplayFrac8S128VBar;
import axoloti.object.display.DisplayFrac8U128VBar;
import axoloti.object.display.DisplayInt32Bar16;
import axoloti.object.display.DisplayInt32Bar32;
import axoloti.object.display.DisplayInt32HexLabel;
import axoloti.object.display.DisplayInt32Label;
import axoloti.object.display.DisplayNoteLabel;
import axoloti.object.display.DisplayVScale;
import axoloti.object.inlet.Inlet;
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletBool32Rising;
import axoloti.object.inlet.InletBool32RisingFalling;
import axoloti.object.inlet.InletCharPtr32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Bipolar;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletFrac32BufferBipolar;
import axoloti.object.inlet.InletFrac32BufferPos;
import axoloti.object.inlet.InletFrac32Pos;
import axoloti.object.inlet.InletInt32;
import axoloti.object.inlet.InletInt32Bipolar;
import axoloti.object.inlet.InletInt32Pos;
import axoloti.object.outlet.Outlet;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletBool32Pulse;
import axoloti.object.outlet.OutletCharPtr32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Bipolar;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletFrac32BufferBipolar;
import axoloti.object.outlet.OutletFrac32BufferPos;
import axoloti.object.outlet.OutletFrac32Pos;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.outlet.OutletInt32Bipolar;
import axoloti.object.outlet.OutletInt32Pos;
import axoloti.object.parameter.Parameter;
import axoloti.object.parameter.Parameter4LevelX16;
import axoloti.object.parameter.ParameterBin1;
import axoloti.object.parameter.ParameterBin12;
import axoloti.object.parameter.ParameterBin16;
import axoloti.object.parameter.ParameterBin1Momentary;
import axoloti.object.parameter.ParameterBin32;
import axoloti.object.parameter.ParameterFrac32SMap;
import axoloti.object.parameter.ParameterFrac32SMapKLineTimeExp;
import axoloti.object.parameter.ParameterFrac32SMapKLineTimeExp2;
import axoloti.object.parameter.ParameterFrac32SMapKPitch;
import axoloti.object.parameter.ParameterFrac32SMapLFOPitch;
import axoloti.object.parameter.ParameterFrac32SMapPitch;
import axoloti.object.parameter.ParameterFrac32SMapRatio;
import axoloti.object.parameter.ParameterFrac32SMapVSlider;
import axoloti.object.parameter.ParameterFrac32UMap;
import axoloti.object.parameter.ParameterFrac32UMapFilterQ;
import axoloti.object.parameter.ParameterFrac32UMapFreq;
import axoloti.object.parameter.ParameterFrac32UMapGain;
import axoloti.object.parameter.ParameterFrac32UMapGain16;
import axoloti.object.parameter.ParameterFrac32UMapGainSquare;
import axoloti.object.parameter.ParameterFrac32UMapKDecayTime;
import axoloti.object.parameter.ParameterFrac32UMapKDecayTimeReverse;
import axoloti.object.parameter.ParameterFrac32UMapKLineTimeReverse;
import axoloti.object.parameter.ParameterFrac32UMapRatio;
import axoloti.object.parameter.ParameterFrac32UMapVSlider;
import axoloti.object.parameter.ParameterInt32Box;
import axoloti.object.parameter.ParameterInt32BoxSmall;
import axoloti.object.parameter.ParameterInt32HRadio;
import axoloti.object.parameter.ParameterInt32VRadio;
import axoloti.parameters.ParameterFrac32SMapKDTimeExp;
import axoloti.property.BooleanProperty;
import axoloti.property.ListProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.property.StringPropertyNull;
import axoloti.target.fs.SDFileReference;
import axoloti.utils.ListUtils;
import axoloti.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class AxoObject extends AxoObjectAbstract {

    @Element(required = false)
    public String helpPatch;
    @Element(required = false)
    private Boolean providesModulationSource = false;
    @Element(required = false)
    private Boolean rotatedParams = false;
    @ElementList(required = false)
    public ArrayList<String> ModulationSources;
    @Path("inlets")
    @ElementListUnion({
        @ElementList(entry = InletBool32.TYPE_NAME, type = InletBool32.class, inline = true, required = false),
        @ElementList(entry = InletBool32Rising.TYPE_NAME, type = InletBool32Rising.class, inline = true, required = false),
        @ElementList(entry = InletBool32RisingFalling.TYPE_NAME, type = InletBool32RisingFalling.class, inline = true, required = false),
        @ElementList(entry = InletFrac32.TYPE_NAME, type = InletFrac32.class, inline = true, required = false),
        @ElementList(entry = InletFrac32Pos.TYPE_NAME, type = InletFrac32Pos.class, inline = true, required = false),
        @ElementList(entry = InletFrac32Bipolar.TYPE_NAME, type = InletFrac32Bipolar.class, inline = true, required = false),
        @ElementList(entry = InletCharPtr32.TYPE_NAME, type = InletCharPtr32.class, inline = true, required = false),
        @ElementList(entry = InletInt32.TYPE_NAME, type = InletInt32.class, inline = true, required = false),
        @ElementList(entry = InletInt32Pos.TYPE_NAME, type = InletInt32Pos.class, inline = true, required = false),
        @ElementList(entry = InletInt32Bipolar.TYPE_NAME, type = InletInt32Bipolar.class, inline = true, required = false),
        @ElementList(entry = InletFrac32Buffer.TYPE_NAME, type = InletFrac32Buffer.class, inline = true, required = false),
        @ElementList(entry = InletFrac32BufferPos.TYPE_NAME, type = InletFrac32BufferPos.class, inline = true, required = false),
        @ElementList(entry = InletFrac32BufferBipolar.TYPE_NAME, type = InletFrac32BufferBipolar.class, inline = true, required = false)
    })
    public List<Inlet> inlets;
    @Path("outlets")
    @ElementListUnion({
        @ElementList(entry = OutletBool32.TYPE_NAME, type = OutletBool32.class, inline = true, required = false),
        @ElementList(entry = OutletBool32Pulse.TYPE_NAME, type = OutletBool32Pulse.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32.TYPE_NAME, type = OutletFrac32.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32Pos.TYPE_NAME, type = OutletFrac32Pos.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32Bipolar.TYPE_NAME, type = OutletFrac32Bipolar.class, inline = true, required = false),
        @ElementList(entry = OutletCharPtr32.TYPE_NAME, type = OutletCharPtr32.class, inline = true, required = false),
        @ElementList(entry = OutletInt32.TYPE_NAME, type = OutletInt32.class, inline = true, required = false),
        @ElementList(entry = OutletInt32Pos.TYPE_NAME, type = OutletInt32Pos.class, inline = true, required = false),
        @ElementList(entry = OutletInt32Bipolar.TYPE_NAME, type = OutletInt32Bipolar.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32Buffer.TYPE_NAME, type = OutletFrac32Buffer.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32BufferPos.TYPE_NAME, type = OutletFrac32BufferPos.class, inline = true, required = false),
        @ElementList(entry = OutletFrac32BufferBipolar.TYPE_NAME, type = OutletFrac32BufferBipolar.class, inline = true, required = false)
    })
    public List<Outlet> outlets;
    @Path("displays")
    @ElementListUnion({
        @ElementList(entry = DisplayBool32.TYPE_NAME, type = DisplayBool32.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32SChart.TYPE_NAME, type = DisplayFrac32SChart.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32UChart.TYPE_NAME, type = DisplayFrac32UChart.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32SDial.TYPE_NAME, type = DisplayFrac32SDial.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32UDial.TYPE_NAME, type = DisplayFrac32UDial.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32VU.TYPE_NAME, type = DisplayFrac32VU.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32VBar.TYPE_NAME, type = DisplayFrac32VBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac32VBarDB.TYPE_NAME, type = DisplayFrac32VBarDB.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac4ByteVBar.TYPE_NAME, type = DisplayFrac4ByteVBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac4UByteVBar.TYPE_NAME, type = DisplayFrac4UByteVBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac4UByteVBarDB.TYPE_NAME, type = DisplayFrac4UByteVBarDB.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32Label.TYPE_NAME, type = DisplayInt32Label.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32HexLabel.TYPE_NAME, type = DisplayInt32HexLabel.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32Bar16.TYPE_NAME, type = DisplayInt32Bar16.class, inline = true, required = false),
        @ElementList(entry = DisplayInt32Bar32.TYPE_NAME, type = DisplayInt32Bar32.class, inline = true, required = false),
        @ElementList(entry = DisplayVScale.TYPE_NAME, type = DisplayVScale.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac8S128VBar.TYPE_NAME, type = DisplayFrac8S128VBar.class, inline = true, required = false),
        @ElementList(entry = DisplayFrac8U128VBar.TYPE_NAME, type = DisplayFrac8U128VBar.class, inline = true, required = false),
        @ElementList(entry = DisplayNoteLabel.TYPE_NAME, type = DisplayNoteLabel.class, inline = true, required = false)
    })
    public List<Display> displays; // readouts
    @Path("params")
    @ElementListUnion({
        @ElementList(entry = ParameterFrac32UMap.TYPE_NAME, type = ParameterFrac32UMap.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapFreq.TYPE_NAME, type = ParameterFrac32UMapFreq.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapKDecayTime.TYPE_NAME, type = ParameterFrac32UMapKDecayTime.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapKDecayTimeReverse.TYPE_NAME, type = ParameterFrac32UMapKDecayTimeReverse.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapKLineTimeReverse.TYPE_NAME, type = ParameterFrac32UMapKLineTimeReverse.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapGain.TYPE_NAME, type = ParameterFrac32UMapGain.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapGain16.TYPE_NAME, type = ParameterFrac32UMapGain16.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapGainSquare.TYPE_NAME, type = ParameterFrac32UMapGainSquare.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapRatio.TYPE_NAME, type = ParameterFrac32UMapRatio.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapFilterQ.TYPE_NAME, type = ParameterFrac32UMapFilterQ.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMap.TYPE_NAME, type = ParameterFrac32SMap.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapPitch.TYPE_NAME, type = ParameterFrac32SMapPitch.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKDTimeExp.TYPE_NAME, type = ParameterFrac32SMapKDTimeExp.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKPitch.TYPE_NAME, type = ParameterFrac32SMapKPitch.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapLFOPitch.TYPE_NAME, type = ParameterFrac32SMapLFOPitch.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKLineTimeExp.TYPE_NAME, type = ParameterFrac32SMapKLineTimeExp.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapKLineTimeExp2.TYPE_NAME, type = ParameterFrac32SMapKLineTimeExp2.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32UMapVSlider.TYPE_NAME, type = ParameterFrac32UMapVSlider.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapVSlider.TYPE_NAME, type = ParameterFrac32SMapVSlider.class, inline = true, required = false),
        @ElementList(entry = ParameterFrac32SMapRatio.TYPE_NAME, type = ParameterFrac32SMapRatio.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32Box.TYPE_NAME, type = ParameterInt32Box.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32BoxSmall.TYPE_NAME, type = ParameterInt32BoxSmall.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32HRadio.TYPE_NAME, type = ParameterInt32HRadio.class, inline = true, required = false),
        @ElementList(entry = ParameterInt32VRadio.TYPE_NAME, type = ParameterInt32VRadio.class, inline = true, required = false),
        @ElementList(entry = Parameter4LevelX16.TYPE_NAME, type = Parameter4LevelX16.class, inline = true, required = false),
        @ElementList(entry = ParameterBin12.TYPE_NAME, type = ParameterBin12.class, inline = true, required = false),
        @ElementList(entry = ParameterBin16.TYPE_NAME, type = ParameterBin16.class, inline = true, required = false),
        @ElementList(entry = ParameterBin32.TYPE_NAME, type = ParameterBin32.class, inline = true, required = false),
        @ElementList(entry = ParameterBin1.TYPE_NAME, type = ParameterBin1.class, inline = true, required = false),
        @ElementList(entry = ParameterBin1Momentary.TYPE_NAME, type = ParameterBin1Momentary.class, inline = true, required = false)
    })
    public List<Parameter> params; // variables
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = AxoAttributeObjRef.TYPE_NAME, type = AxoAttributeObjRef.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeTablename.TYPE_NAME, type = AxoAttributeTablename.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeComboBox.TYPE_NAME, type = AxoAttributeComboBox.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeMidiInPort.TYPE_NAME, type = AxoAttributeMidiInPort.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeMidiOutPort.TYPE_NAME, type = AxoAttributeMidiOutPort.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeInt32.TYPE_NAME, type = AxoAttributeInt32.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeSpinner.TYPE_NAME, type = AxoAttributeSpinner.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeSDFile.TYPE_NAME, type = AxoAttributeSDFile.class, inline = true, required = false),
        @ElementList(entry = AxoAttributeTextEditor.TYPE_NAME, type = AxoAttributeTextEditor.class, inline = true, required = false)})
    public List<AxoAttribute> attributes; // literal constants

    @Path("file-depends")
    @ElementList(entry = "file-depend", type = SDFileReference.class, inline = true, required = false)
    public List<SDFileReference> filedepends;

    @Path("includes")
    @ElementList(entry = "include", type = String.class, inline = true, required = false)
    public List<String> includes;

    @Path("depends")
    @ElementList(entry = "depend", type = String.class, inline = true, required = false)
    public List<String> depends;

    @Path("modules")
    @ElementList(entry = "module", type = String.class, inline = true, required = false)
    public List<String> modules;

    @Element(name = "code.declaration", required = false, data = true)
    public String sLocalData = "";
    @Element(name = "code.init", required = false, data = true)
    public String sInitCode = "";
    @Element(name = "code.dispose", required = false, data = true)
    public String sDisposeCode = "";
    @Element(name = "code.krate", required = false, data = true)
    public String sKRateCode = "";
    @Element(name = "code.srate", required = false, data = true)
    public String sSRateCode = "";
    @Element(name = "code.midihandler", required = false, data = true)
    public String sMidiCode = "";

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
    }

    public AxoObject(String id, String sDescription) {
        super(id, sDescription);
    }

    @Commit
    void commit() {
        if (inlets != null) {
            for (Inlet o : inlets) {
                o.setParent(this);
            }
        }
        if (outlets != null) {
            for (Outlet o : outlets) {
                o.setParent(this);
            }
        }
        if (attributes != null) {
            for (AxoAttribute o : attributes) {
                o.setParent(this);
            }
        }
        if (params != null) {
            for (Parameter o : params) {
                o.setParent(this);
            }
        }
        if (displays != null) {
            for (Display o : displays) {
                o.setParent(this);
            }
        }
    }

    @Persist
    public void presist() {
        if (providesModulationSource != null && providesModulationSource == false) {
            providesModulationSource = null;
        }
        if (rotatedParams != null && rotatedParams == false) {
            rotatedParams = null;
        }
    }

    /*
    @Override
    public AxoObjectInstance CreateInstance(PatchController patchController, String InstanceName1, Point location) {
        PatchModel patchModel = null;
        if (patchController != null) {
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
            patchModel = patchController.getModel();
        }
        ObjectController ctrl = createController(null, null);
        AxoObjectInstance o = new AxoObjectInstance(ctrl, patchModel, InstanceName1, location);
        ctrl.addView(o);
        return o;
    }*/
    public void setProvidesModulationSource() {
        providesModulationSource = true;
    }

    public void setModulators(List<String> modulators) {
        if (modulators == null) {
            this.ModulationSources = null;
        } else {
            this.ModulationSources = new ArrayList<>(modulators);
        }
        firePropertyChange(OBJ_MODULATORS, null, this.ModulationSources);
    }

    @Override
    public List<String> getModulators() {
        if (ModulationSources != null) {
            return ListUtils.export(ModulationSources);
        }
        if ((providesModulationSource != null) && (providesModulationSource == true)) {
            return Collections.singletonList(null);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getCName() {
        return id;
    }

    @Override
    public String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
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
    public List<String> getProcessedIncludes() {
        if ((includes == null) || includes.isEmpty()) {
            return null;
        } else if (getPath() != null) {
            List<String> r = new LinkedList<>();
            for (String s : includes) {
                if (s.startsWith("./")) {
                    String strippedPath = getPath().substring(0, getPath().lastIndexOf(File.separatorChar));
                    File f = new File(strippedPath + "/" + s.substring(2));
                    String s2 = f.getAbsolutePath();
                    s2 = s2.replace('\\', '/');
                    r.add(s2);
                } else if (s.startsWith("../")) {
                    String strippedPath = getPath().substring(0, getPath().lastIndexOf(File.separatorChar));
                    File f = new File(strippedPath + "/" + s);
                    String s2 = f.getAbsolutePath();
                    s2 = s2.replace('\\', '/');
                    r.add(s2);
                } else if (s.startsWith("chibios/")) {
                    // r.add((new File(System.getProperty(CHIBIOS_DIR))).getAbsolutePath() + s.substring(7));
                } else {
                    r.add(s);
                }
            }
            return Collections.unmodifiableList(r);
        } else if (includes.isEmpty()) {
            return Collections.emptyList();
        } else {
            return ListUtils.export(includes);
        }
    }

    @Override
    public List<String> getIncludes() {
        return ListUtils.export(includes);
    }

    @Override
    public void setIncludes(List<String> includes) {
        this.includes = ListUtils.importList(includes);
        firePropertyChange(OBJ_INCLUDES, null, includes);
    }

    @Override
    public List<String> getDepends() {
        return ListUtils.export(depends);
    }

    public void setDepends(List<String> depends) {
        this.depends = ListUtils.importList(depends);
        firePropertyChange(OBJ_DEPENDS, null, depends);
    }

    @Override
    public List<String> getModules() {
        return ListUtils.export(modules);
    }

    public void setModules(List<String> modules) {
        this.modules = ListUtils.importList(modules);
        firePropertyChange(OBJ_MODULES, null, modules);
    }

    @Override
    public File getHelpPatchFile() {
        if ((helpPatch == null) || (getPath() == null) || getPath().isEmpty()) {
            return null;
        }
        File o = new File(getPath());
        String p = o.getParent() + File.separator + helpPatch;
        File f = new File(p);
        if (f.isFile() && f.canRead()) {
            return f;
        } else {
            return null;
        }
    }

    public AxoObject createDeepClone() throws CloneNotSupportedException {
        try {
            // clone by serialization/deserialization...
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            Strategy strategy = new AnnotationStrategy();
            Serializer serializer = new Persister(strategy);
            serializer.write(this, os);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            AxoObject o = serializer.read(AxoObject.class, is);
            return o;
        } catch (Exception ex) {
            throw new CloneNotSupportedException();
        }
    }

    /* MVC code */

    public static final Property OBJ_ID = new StringProperty("Id", AxoObject.class);
    public static final Property OBJ_DESCRIPTION = new StringPropertyNull("Description", AxoObject.class);
    public static final Property OBJ_LICENSE = new StringPropertyNull("License", AxoObject.class);
    public static final Property OBJ_PATH = new StringPropertyNull("Path", AxoObject.class);
    public static final Property OBJ_AUTHOR = new StringPropertyNull("Author", AxoObject.class);
    public static final Property OBJ_HELPPATCH = new StringPropertyNull("HelpPatch", AxoObject.class);
    public static final Property OBJ_ROTATEDPARAMS = new BooleanProperty("RotatedParams", AxoObject.class, "bbb");

    public static final Property OBJ_INIT_CODE = new StringPropertyNull("InitCode", AxoObject.class);
    public static final Property OBJ_DISPOSE_CODE = new StringPropertyNull("DisposeCode", AxoObject.class);
    public static final Property OBJ_LOCAL_DATA = new StringPropertyNull("LocalData", AxoObject.class);
    public static final Property OBJ_KRATE_CODE = new StringPropertyNull("KRateCode", AxoObject.class);
    public static final Property OBJ_SRATE_CODE = new StringPropertyNull("SRateCode", AxoObject.class);
    public static final Property OBJ_MIDI_CODE = new StringPropertyNull("MidiCode", AxoObject.class);

    public static final ListProperty OBJ_INCLUDES = new ListProperty("Includes", AxoObject.class);
    public static final ListProperty OBJ_DEPENDS = new ListProperty("Depends", AxoObject.class);
    public static final ListProperty OBJ_MODULES = new ListProperty("Modules", AxoObject.class);
    public static final ListProperty OBJ_MODULATORS = new ListProperty("Modulators", AxoObject.class);

    private static final Property[] PROPERTIES = {
        OBJ_ID,
        OBJ_DESCRIPTION,
        OBJ_LICENSE,
        OBJ_PATH,
        OBJ_AUTHOR,
        OBJ_HELPPATCH,
        OBJ_INLETS,
        OBJ_OUTLETS,
        OBJ_ATTRIBUTES,
        OBJ_PARAMETERS,
        OBJ_DISPLAYS,
        OBJ_INIT_CODE,
        OBJ_DISPOSE_CODE,
        OBJ_LOCAL_DATA,
        OBJ_KRATE_CODE,
        OBJ_SRATE_CODE,
        OBJ_MIDI_CODE,
        OBJ_MODULATORS
    };

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>(Arrays.asList(PROPERTIES));
        return l;
    }

    @Override
    public String getHelpPatch() {
        return StringUtils.denullString(helpPatch);
    }

    public void setHelpPatch(String helpPatch) {
        String prev_val = this.helpPatch;
        this.helpPatch = helpPatch;
        firePropertyChange(OBJ_HELPPATCH, prev_val, helpPatch);
    }

    @Override
    public List<Inlet> getInlets() {
        return ListUtils.export(inlets);
    }

    @Override
    public void setInlets(List<Inlet> inlets) {
        List<Inlet> old_val = this.inlets;
        this.inlets = inlets;
        firePropertyChange(OBJ_INLETS, old_val, inlets);
    }

    @Override
    public List<Outlet> getOutlets() {
        return ListUtils.export(outlets);
    }

    @Override
    public void setOutlets(List<Outlet> outlets) {
        List<Outlet> old_val = this.outlets;
        this.outlets = outlets;
        firePropertyChange(OBJ_OUTLETS, old_val, outlets);
    }

    @Override
    public List<Parameter> getParameters() {
        return ListUtils.export(params);
    }

    @Override
    public void setParameters(List<Parameter> parameters) {
        List<Parameter> old_val = this.params;
        this.params = parameters;
        firePropertyChange(OBJ_PARAMETERS, old_val, parameters);
    }

    @Override
    public List<AxoAttribute> getAttributes() {
        return ListUtils.export(attributes);
    }

    @Override
    public void setAttributes(List<AxoAttribute> attributes) {
        List<AxoAttribute> old_val = this.attributes;
        this.attributes = attributes;
        firePropertyChange(OBJ_ATTRIBUTES, old_val, attributes);
    }

    @Override
    public List<Display> getDisplays() {
        return ListUtils.export(displays);
    }

    @Override
    public void setDisplays(List<Display> displays) {
        List<Display> old_val = this.displays;
        this.displays = displays;
        firePropertyChange(OBJ_DISPLAYS, old_val, displays);
    }

    @Override
    public Boolean getRotatedParams() {
        if (rotatedParams == null) {
            return false;
        } else {
            return rotatedParams;
        }
    }

    public void setRotatedParams(Boolean rotatedParams) {
        Boolean prev_value = this.rotatedParams;
        this.rotatedParams = rotatedParams;
        firePropertyChange(OBJ_ROTATEDPARAMS, prev_value, rotatedParams);
    }

    @Override
    public String getInitCode() {
        return StringUtils.denullString(sInitCode);
    }

    public void setInitCode(String sInitCode) {
        String prev_val = this.sInitCode;
        this.sInitCode = sInitCode;
        firePropertyChange(OBJ_INIT_CODE, prev_val, sInitCode);
    }

    @Override
    public String getDisposeCode() {
        return StringUtils.denullString(sDisposeCode);
    }

    public void setDisposeCode(String sDisposeCode) {
        String prev_val = this.sDisposeCode;
        this.sDisposeCode = sDisposeCode;
        firePropertyChange(OBJ_DISPOSE_CODE, prev_val, sDisposeCode);
    }

    @Override
    public String getLocalData() {
        return StringUtils.denullString(sLocalData);
    }

    public void setLocalData(String sLocalData) {
        String prev_val = this.sLocalData;
        this.sLocalData = sLocalData;
        firePropertyChange(OBJ_LOCAL_DATA, prev_val, sLocalData);
    }

    @Override
    public String getKRateCode() {
        return StringUtils.denullString(sKRateCode);
    }

    public void setKRateCode(String sKRateCode) {
        String prev_val = this.sKRateCode;
        this.sKRateCode = sKRateCode;
        firePropertyChange(OBJ_KRATE_CODE, prev_val, sKRateCode);
    }

    @Override
    public String getSRateCode() {
        return StringUtils.denullString(sSRateCode);
    }

    public void setSRateCode(String sSRateCode) {
        String prev_val = this.sSRateCode;
        this.sSRateCode = sSRateCode;
        firePropertyChange(OBJ_SRATE_CODE, prev_val, sSRateCode);
    }

    @Override
    public String getMidiCode() {
        return StringUtils.denullString(sMidiCode);
    }

    public void setMidiCode(String sMidiCode) {
        String prev_val = this.sMidiCode;
        this.sMidiCode = sMidiCode;
        firePropertyChange(OBJ_MIDI_CODE, prev_val, sMidiCode);
    }

    @Override
    public List<SDFileReference> getFileDepends() {
        return ListUtils.export(filedepends);
    }

}
