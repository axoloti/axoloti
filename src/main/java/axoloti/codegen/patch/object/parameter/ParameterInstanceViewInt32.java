package axoloti.codegen.patch.object.parameter;

import axoloti.object.parameter.Parameter;
import axoloti.object.parameter.ParameterInt32;
import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewInt32 extends ParameterInstanceView {

    ParameterInstanceViewInt32(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public String generateParameterInitializer() {
        ParameterInt32 parameter = (ParameterInt32) (getDModel().getDModel());
        Integer v1 = (Integer) getDModel().getValue();
        int v = v1;
        String s = "{ type: " + parameter.getCType()
            + ", unit: " + parameter.getCUnit()
            + ", signals: 0"
                + ", pfunction: " + ((getDModel().getPFunction() == null) ? "0" : getDModel().getPFunction())
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
        if (getDModel().getOnParent() && (enableOnParent)) {
            return "%" + getDModel().getControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.intt.finalvalue";
        }
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".t_int.value";
    }

    @Override
    public String generateCodeMidiHandler(String vprefix) {
        Parameter parameter = model.getDModel();
        // TODO: midi mapping: validate minimum value...
        // hmm this is only one possible behavior - could also map to full MIDI range...
        int max = ((ParameterInt32) parameter).getMaxValue();
        return generateMidiCCCodeSub(vprefix, "(data2<" + max + ")?data2:" + (max - 1));
    }

}
