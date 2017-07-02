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
import axoloti.PatchModel;
import axoloti.Preset;
import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueFrac32;
import axoloti.mvc.AbstractModel;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.AxoObjectPatcher;
import axoloti.realunits.NativeToReal;
import axoloti.utils.CharEscape;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Complete;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "param")
public abstract class ParameterInstance<T extends Parameter> extends AbstractModel implements AtomInstance<T> {

    @Attribute
    String name;
    @Attribute(required = false)
    private Boolean onParent = false;
    @ElementList(required = false)
    ArrayList<Preset> presets;
    @Attribute(required = false)
    private Integer MidiCC = null;
    
    protected int index;
    public T parameter;
    protected boolean needsTransmit = false;
    AxoObjectInstance axoObjectInstance;
    NativeToReal convs[];
    int selectedConv = 0;

    AtomDefinitionController controller;

    public ParameterInstance() {
    }

    public ParameterInstance(T param, AxoObjectInstance axoObjInstance) {
        super();
        parameter = param;
        this.axoObjectInstance = axoObjInstance;
        name = parameter.getName();
    }

    @Complete
    public void Complete() {
        if (onParent == null) {
            onParent = false;
        }
        if (MidiCC == null) {
            MidiCC = -1;
        }
    }

    @Persist
    public void Persist() {
        // called prior to serialization
        if (onParent != null && onParent == false) {
            onParent = null;
        }
        if (MidiCC != null && MidiCC < 0) {
            MidiCC = null;
        }
        if (presets != null && presets.isEmpty()) {
            presets = null;
        }
    }

    public String GetCName() {
        return parameter.GetCName();
    }

    public void CopyValueFrom(ParameterInstance p) {
        if (p.onParent != null) {
            setOnParent(p.onParent);
        }
        setPresets(p.getPresets());
        setMidiCC(p.MidiCC);
    }

    @Deprecated // TODO: move live parameter tweaking in a separate view
    public boolean getNeedsTransmit() {
        return needsTransmit;
    }

    @Deprecated // TODO: move live parameter tweaking in a separate view
    public void ClearNeedsTransmit() {
        needsTransmit = false;
    }

    @Deprecated // TODO: move live parameter tweaking in a separate view
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

    public abstract Value getValue();

    public void setValue(Value value) {

        firePropertyChange(ParameterInstanceController.ELEMENT_PARAM_VALUE, null, value);
    }

    public void SetValueRaw(int v) {
        // FIXME, different types possible
        ValueFrac32 v1 = new ValueFrac32();
        v1.setRaw(v);
        setValue(v1);
    }

    public int GetValueRaw() {
        return getValue().getRaw();
    }

    public String indexName() {
        return "PARAM_INDEX_" + axoObjectInstance.getLegalName() + "_" + getLegalName();
    }

    public String getLegalName() {
        return CharEscape.CharEscape(getName());
    }

    public String PExName(String vprefix) {
        return vprefix + "params[" + indexName() + "]";
    }

    abstract public String valueName(String vprefix);

    public String ControlOnParentName() {
        if (axoObjectInstance.getParameterInstances().size() == 1) {
            return axoObjectInstance.getInstanceName();
        } else {
            return axoObjectInstance.getInstanceName() + ":" + parameter.getName();
        }
    }

    abstract public String variableName(String vprefix, boolean enableOnParent);

    public String signalsName(String vprefix) {
        return PExName(vprefix) + ".signals";
    }

    public String GetPFunction() {
        return "0";
    }

    public abstract String GenerateCodeMidiHandler(String vprefix);

    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }
    
    // review!
    public String GetUserParameterName() {
        if (axoObjectInstance.getParameterInstances().size() == 1) {
            return axoObjectInstance.getInstanceName();
        } else {
            return getName();
        }
    }

    abstract public String GenerateParameterInitializer();

    public String GetCMultiplier() {
        return "0";
    }

    public String GetCOffset() {
        return "0";
    }

    String GenerateMidiCCCodeSub(String vprefix, String value) {
        if (MidiCC != null) {
            return "        if ((status == attr_midichannel + MIDI_CONTROL_CHANGE)&&(data1 == " + MidiCC + ")) {\n"
                    + "            ParameterChange(&parent->" + PExName(vprefix) + "," + value + ", 0xFFFD);\n"
                    + "        }\n";
        } else {
            return "";
        }
    }

    public T createParameterForParent() {
        T pcopy = (T)parameter.getClone();
        pcopy.setName(ControlOnParentName());
        pcopy.noLabel = null;
        pcopy.PropagateToChild = axoObjectInstance.getLegalName() + "_" + getLegalName();
        return pcopy;
    }

    public AxoObjectInstance getObjectInstance() {
        return axoObjectInstance;
    }

    @Override
    public T getModel() {
        return (T) getController().getModel();
    }

    public String GenerateCodeInitModulator(String vprefix, String StructAccces) {
        return "";
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

    @Override
    public AtomDefinitionController getController() {
        return controller;
    }

    void setController(AtomDefinitionController controller) {
        this.controller = controller;
    }

    /* MVC getters and setters */

    /* View personality */    
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        // triggered by a model definition change, triggering instance view changes
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(AtomDefinitionController.ATOM_NAME)) {
            updateParamOnParent();
            firePropertyChange(
                    evt.getPropertyName(),
                    evt.getOldValue(),
                    evt.getNewValue());
        } else if (propertyName.equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            firePropertyChange(
                    evt.getPropertyName(),
                    evt.getOldValue(),
                    evt.getNewValue());
        }
    }

    /* Model personality */
    public Integer getMidiCC() {
        if (MidiCC == null) {
            return -1;
        } else {
            return MidiCC;
        }
    }

    public void setMidiCC(Integer MidiCC) {
        Integer prevValue = this.MidiCC;
        if ((MidiCC != null) && (MidiCC >= 0)) {
            this.MidiCC = MidiCC;
        } else {
            this.MidiCC = null;
        }
        this.MidiCC = MidiCC;
        firePropertyChange(ParameterInstanceController.ELEMENT_PARAM_MIDI_CC, prevValue, MidiCC);
    }

    public Boolean getOnParent() {
        if (onParent == null) {
            return false;
        } else {
            return onParent;
        }
    }

    T paramOnParent;

    public T getParamOnParent() {
        return paramOnParent;
    }
    
    public void updateParamOnParent() {
        if (paramOnParent != null) {
            paramOnParent.setName(ControlOnParentName());
            paramOnParent.noLabel = null;
            paramOnParent.PropagateToChild = axoObjectInstance.getLegalName() + "_" + getLegalName();
        }
    }

    void setParamOnParent(T paramOnParent) {
        Parameter prev_value = this.paramOnParent;
        this.paramOnParent = paramOnParent;
//      firePropertyChange(ParameterInstanceController.ELEMENT_PARAM_PARAM_ON_PARENT, prev_value, paramOnParent);
//      PatchController pc = getParent().getParent();
        PatchModel pm = getObjectInstance().getPatchModel();
        if (pm == null) return;
        if (pm.getContainer() == null) return;
        AxoObjectInstancePatcher aoip = pm.getContainer();
        AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();
        ArrayList<Parameter> ps = new ArrayList<>(aop.getParameters());
        if (paramOnParent != null) {
            ps.add(paramOnParent);
            // TODO: sort
        }
        if (prev_value != null) {
            ps.remove((Parameter) prev_value);                
        }
        aop.setParameters(ps);
    }
    
    public void setOnParent(Boolean onParent) {
        if (onParent == null) {
            return;
        }
        if (getOnParent() == onParent) {
            return;
        }
        Boolean oldValue = this.onParent;
        this.onParent = onParent;
        if (onParent) {
            setParamOnParent(createParameterForParent());
        } else {
            setParamOnParent(null);
        }
        firePropertyChange(
                ParameterInstanceController.ELEMENT_PARAM_ON_PARENT,
                oldValue, onParent);
    }

    public ArrayList<Preset> getPresets() {
        if (presets != null) {
            return presets;
        } else {
            return new ArrayList<>();
        }
    }

    public void setPresets(ArrayList<Preset> presets) {
        ArrayList<Preset> prevValue = this.presets;
        this.presets = presets;
        firePropertyChange(ParameterInstanceController.ELEMENT_PARAM_PRESETS, prevValue, this.presets);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String prevValue = this.name;
        this.name = name;
        firePropertyChange(AtomDefinitionController.ATOM_NAME, prevValue, name);
    }

    public void Remove(){
        if (paramOnParent!=null) {
            setParamOnParent(null);
        }
    }
    
}
