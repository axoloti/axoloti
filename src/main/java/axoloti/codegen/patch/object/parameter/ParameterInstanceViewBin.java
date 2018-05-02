package axoloti.codegen.patch.object.parameter;

import axoloti.object.parameter.ParameterBin;
import axoloti.patch.object.parameter.ParameterInstanceController;

/**
 *
 * @author jtaelman
 */
abstract class ParameterInstanceViewBin extends ParameterInstanceView {

    ParameterInstanceViewBin(ParameterInstanceController controller) {
        super(controller);
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".d.bin.value";
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getModel().getOnParent() && (enableOnParent)) {
            return "%" + getModel().ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.bin.finalvalue";
        }
    }

    @Override
    public String GenerateParameterInitializer() {
// { type: param_type_frac, unit: param_unit_abstract, signals: 0, pfunction: 0, d: { frac: { finalvalue:0,  0,  0,  0,  0}}},
//        String pname = GetUserParameterName();
        ParameterBin parameter = (ParameterBin) (getModel().getModel());
        String s = "{ type: " + parameter.GetCType()
                + ", unit: " + parameter.GetCUnit()
                + ", signals: 0"
                + ", pfunction: " + ((getModel().GetPFunction() == null) ? "0" : getModel().GetPFunction());
        Integer v1 = (Integer) getModel().getValue();
        int v = v1;
        s += ", d: { bin: { finalvalue: 0"
                + ", value: " + v
                + ", modvalue: " + v
                + ", nbits: " + parameter.getNBits()
                + "}}},\n";
        return s;
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return "";
    }
}
