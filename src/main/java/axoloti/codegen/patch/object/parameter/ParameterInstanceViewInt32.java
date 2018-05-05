package axoloti.codegen.patch.object.parameter;

import axoloti.object.parameter.Parameter;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstanceController;
/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewInt32 extends ParameterInstanceView {

    ParameterInstanceViewInt32(ParameterInstanceController controller) {
        super(controller);
    }

    @Override
    public String GenerateParameterInitializer() {
        ParameterInt32 parameter = (ParameterInt32) (getModel().getModel());
        Integer v1 = (Integer) getModel().getValue();
        int v = v1;
        String s = "{ type: " + parameter.GetCType()
            + ", unit: " + parameter.GetCUnit()
            + ", signals: 0"
            + ", pfunction: " + ((getModel().GetPFunction() == null) ? "0" : getModel().GetPFunction())
            + ", d: { intt: { finalvalue: 0"
            + ", value: " + v
            + ", modvalue: " + v
            + ", minimum: " + parameter.getMinValue()
            + ", maximum: " + parameter.getMaxValue()
            + "}}},\n";
        return s;
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getModel().getOnParent() && (enableOnParent)) {
            return "%" + getModel().ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.intt.finalvalue";
        }
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".t_int.value";
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        Parameter parameter = getController().getModel().getModel();
        // TODO: midi mapping: validate minimum value...
        // hmm this is only one possible behavior - could also map to full MIDI range...
        int max = ((ParameterInt32) parameter).getMaxValue();
        return GenerateMidiCCCodeSub(vprefix, "(data2<" + max + ")?data2:" + (max - 1));
    }

}
