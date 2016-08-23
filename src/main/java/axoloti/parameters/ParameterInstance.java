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
package axoloti.parameters;

import axoloti.Modulation;
import axoloti.Preset;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.Value;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameterviews.IParameterInstanceView;
import axoloti.realunits.NativeToReal;
import axoloti.utils.CharEscape;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "param")
public abstract class ParameterInstance<T extends Parameter> implements AtomInstance<T> {

    @Attribute
    String name;
    @Attribute(required = false)
    private Boolean onParent;
    protected int index;
    public T parameter;
    @ElementList(required = false)
    ArrayList<Preset> presets;
    protected boolean needsTransmit = false;
    AxoObjectInstance axoObjectInstance;
    NativeToReal convs[];
    int selectedConv = 0;
    @Attribute(required = false)
    Integer MidiCC = null;

    public ParameterInstance() {
    }

    public ParameterInstance(T param, AxoObjectInstance axoObjInstance) {
        super();
        parameter = param;
        this.axoObjectInstance = axoObjInstance;
        name = parameter.name;
    }

    public String GetCName() {
        return parameter.GetCName();
    }

    public void CopyValueFrom(ParameterInstance p) {
        if (p.onParent != null) {
            setOnParent(p.onParent);
        }
        setMidiCC(p.MidiCC);
    }

    public void applyDefaultValue() {
    }

    public boolean getNeedsTransmit() {
        return needsTransmit;
    }

    public void ClearNeedsTransmit() {
        needsTransmit = false;
    }

    public void setNeedsTransmit(boolean needsTransmit) {
        this.needsTransmit = needsTransmit;
    }

    public byte[] TXData() {
        needsTransmit = false;
        byte[] data = new byte[14];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'P';
        int pid = getObjectInstance().getPatchModel().GetIID();
        data[4] = (byte) pid;
        data[5] = (byte) (pid >> 8);
        data[6] = (byte) (pid >> 16);
        data[7] = (byte) (pid >> 24);
        int tvalue = GetValueRaw();
        data[8] = (byte) tvalue;
        data[9] = (byte) (tvalue >> 8);
        data[10] = (byte) (tvalue >> 16);
        data[11] = (byte) (tvalue >> 24);
        data[12] = (byte) (index);
        data[13] = (byte) (index >> 8);
        return data;
    }

    public Preset GetPreset(int i) {
        if (presets == null) {
            return null;
        }
        for (Preset p : presets) {
            if (p.index == i) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Preset> getPresets() {
        return presets;
    }

    public void setPresets(ArrayList<Preset> presets) {
        this.presets = presets;
    }

    public Preset AddPreset(int index, Value value) {
        Preset p = GetPreset(index);
        if (p != null) {
            p.value = value;
            return p;
        }
        if (presets == null) {
            presets = new ArrayList<Preset>();
        }
        p = new Preset(index, value);
        presets.add(p);
        return p;
    }

    public void RemovePreset(int index) {
        Preset p = GetPreset(index);
        if (p != null) {
            presets.remove(p);
        }
    }

    public abstract Value getValue();

    public void setValue(Value value) {
        if (axoObjectInstance != null) {
            if (axoObjectInstance.getPatchModel() != null) {
                axoObjectInstance.getPatchModel().setDirty();
            }
        }
    }

    public void SetValueRaw(int v) {
        getValue().setRaw(v);
    }

    public int GetValueRaw() {
        return getValue().getRaw();
    }

    public String indexName() {
        return "PARAM_INDEX_" + axoObjectInstance.getLegalName() + "_" + getLegalName();
    }

    public String getLegalName() {
        return CharEscape.CharEscape(name);
    }

    public String KVPName(String vprefix) {
        return "KVP_" + axoObjectInstance.getCInstanceName() + "_" + getLegalName();
    }

    public String PExName(String vprefix) {
        return vprefix + "PExch[" + indexName() + "]";
    }

    public String valueName(String vprefix) {
        return PExName(vprefix) + ".value";
    }

    public String ControlOnParentName() {
        if (axoObjectInstance.parameterInstances.size() == 1) {
            return axoObjectInstance.getInstanceName();
        } else {
            return axoObjectInstance.getInstanceName() + ":" + parameter.name;
        }
    }

    public String variableName(String vprefix, boolean enableOnParent) {
        if ((onParent != null) && (onParent) && (enableOnParent)) {
            return "%" + ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".finalvalue";
        }
    }

    public String signalsName(String vprefix) {
        return PExName(vprefix) + ".signals";
    }

    public String GetPFunction() {
        return "";
    }

    public String GenerateCodeDeclaration(String vprefix) {
        return "";//("#define " + indexName() + " " + index + "\n");
    }

    public abstract String GenerateCodeInit(String vprefix, String StructAccces);

    public abstract String GenerateCodeMidiHandler(String vprefix);

    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    String GenerateMidiCCCodeSub(String vprefix, String value) {
        if (MidiCC != null) {
            return "        if ((status == attr_midichannel + MIDI_CONTROL_CHANGE)&&(data1 == " + MidiCC + ")) {\n"
                    + "            PExParameterChange(&parent->" + PExName(vprefix) + "," + value + ", 0xFFFD);\n"
                    + "        }\n";
        } else {
            return "";
        }
    }

    public Parameter getParameterForParent() {
        Parameter pcopy = parameter.getClone();
        pcopy.name = ControlOnParentName();
        pcopy.noLabel = null;
        pcopy.PropagateToChild = axoObjectInstance.getLegalName() + "_" + getLegalName();
        return pcopy;
    }

    public void setMidiCC(Integer cc) {
        if ((cc != null) && (cc >= 0)) {
            MidiCC = cc;
        } else {
            MidiCC = null;
        }
    }

    public int getMidiCC() {
        if (MidiCC == null) {
            return -1;
        } else {
            return MidiCC;
        }
    }

    @Override
    public AxoObjectInstance getObjectInstance() {
        return axoObjectInstance;
    }

    @Override
    public T getDefinition() {
        return parameter;
    }

    public String GenerateCodeInitModulator(String vprefix, String StructAccces) {
        return "";
    }

    public boolean isOnParent() {
        if (onParent == null) {
            return false;
        } else {
            return onParent;
        }
    }

    public void setOnParent(Boolean b) {
        if (b == null) {
            return;
        }
        if (isOnParent() == b) {
            return;
        }
        if (b) {
            onParent = true;
        } else {
            onParent = null;
        }
    }

    public ArrayList<Modulation> getModulators() {
        return null;
    }

    public NativeToReal[] getConvs() {
        return convs;
    }

    public int getSelectedConv() {
        return selectedConv;
    }

    public void setSelectedConv(int selectedConv) {
        this.selectedConv = selectedConv;
    }

    public T getParameter() {
        return parameter;
    }

    public String getName() {
        return name;
    }

    public abstract IParameterInstanceView getViewInstance(IAxoObjectInstanceView o);

    public IParameterInstanceView createView(IAxoObjectInstanceView o) {
        IParameterInstanceView pi = getViewInstance(o);
        pi.PostConstructor();
        o.addParameterInstanceView(pi);
        return pi;
    }

    public void SetDirty() {
        // propagate dirty flag to patch if there is one
        if (getObjectInstance().getPatchModel() != null) {
            getObjectInstance().getPatchModel().setDirty();
        }
    }
}
