package axoloti.codegen.patch.object.parameter;

import axoloti.mvc.View;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterInstanceView extends View<ParameterInstanceController> {

    protected int index;

    ParameterInstanceView(ParameterInstanceController controller) {
        super(controller);
    }

    public ParameterInstance getModel() {
        return getController().getModel();
    }

    abstract public String valueName(String vprefix);

    abstract public String variableName(String vprefix, boolean enableOnParent);

    abstract public String GenerateParameterInitializer();

    public String signalsName(String vprefix) {
        return PExName(vprefix) + ".signals";
    }

    public String indexName() {
        return "PARAM_INDEX_" + getModel().getParent().getLegalName() + "_" + getModel().getLegalName();
    }

    public String PExName(String vprefix) {
        return vprefix + "params[" + indexName() + "]";
    }

    public String ControlOnParentName() {
        if (getModel().getParent().getParameterInstances().size() == 1) {
            return getModel().getParent().getInstanceName();
        } else {
            return getModel().getParent().getInstanceName() + ":" + getModel().getModel().getName();
        }
    }

    public abstract String GenerateCodeMidiHandler(String vprefix);

    String GenerateMidiCCCodeSub(String vprefix, String value) {
        Integer midicc = getModel().getMidiCC();
        if (midicc != null) {
            return "        if ((status == attr_midichannel + MIDI_CONTROL_CHANGE)&&(data1 == " + midicc + ")) {\n"
                    + "            ParameterChange(&parent->" + PExName(vprefix) + "," + value + ", 0xFFFD);\n"
                    + "        }\n";
        } else {
            return "";
        }
    }

    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

}
