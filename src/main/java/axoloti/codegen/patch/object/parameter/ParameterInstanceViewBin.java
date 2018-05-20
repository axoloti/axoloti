package axoloti.codegen.patch.object.parameter;

import axoloti.object.parameter.ParameterBin;
import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
abstract class ParameterInstanceViewBin extends ParameterInstanceView {

    ParameterInstanceViewBin(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".d.bin.value";
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getDModel().getOnParent() && (enableOnParent)) {
            return "%" + getDModel().getControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.bin.finalvalue";
        }
    }

    @Override
    public String generateParameterInitializer() {
// { type: param_type_frac, unit: param_unit_abstract, signals: 0, pfunction: 0, d: { frac: { finalvalue:0,  0,  0,  0,  0}}},
//        String pname = GetUserParameterName();
        ParameterBin parameter = (ParameterBin) (getDModel().getDModel());
        Integer v1 = (Integer) getDModel().getValue();
        int v = v1;
        String s = "{ type: " + parameter.getCType()
            + ", unit: " + parameter.getCUnit()
            + ", signals: 0"
                + ", pfunction: " + ((getDModel().getPFunction() == null) ? "0" : getDModel().getPFunction())
                + ", d: { bin: { finalvalue: 0"
            + ", value: " + v
            + ", modvalue: " + v
            + ", nbits: " + parameter.getNBits()
            + "}}},\n";
        return s;
    }

    @Override
    public String generateCodeMidiHandler(String vprefix) {
        return "";
    }
}
