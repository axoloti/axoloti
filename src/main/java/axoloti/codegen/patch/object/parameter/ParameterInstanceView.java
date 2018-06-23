package axoloti.codegen.patch.object.parameter;

import axoloti.mvc.View;
import axoloti.patch.object.parameter.ParameterInstance;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterInstanceView extends View<ParameterInstance> {

    protected int index;

    ParameterInstanceView(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    abstract public String valueName(String vprefix);

    abstract public String variableName(String vprefix, boolean enableOnParent);

    abstract public String generateParameterInitializer();

    public String signalsName(String vprefix) {
        return PExName(vprefix) + ".signals";
    }

    public String indexName() {
        return "PARAM_INDEX_" + getDModel().getParent().getLegalName() + "_" + getDModel().getLegalName();
    }

    public String PExName(String vprefix) {
        return vprefix + "PExch[" + indexName() + "]";
    }

    public String controlOnParentName() {
        if (getDModel().getParent().getParameterInstances().size() == 1) {
            return getDModel().getParent().getInstanceName();
        } else {
            return getDModel().getParent().getInstanceName() + ":" + getDModel().getDModel().getName();
        }
    }

    public abstract String generateCodeMidiHandler(String vprefix);

    String generateMidiCCCodeSub(String vprefix, String value) {
        Integer midicc = getDModel().getMidiCC();
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
