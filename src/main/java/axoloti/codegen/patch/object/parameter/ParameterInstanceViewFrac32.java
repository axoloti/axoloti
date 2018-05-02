package axoloti.codegen.patch.object.parameter;

import axoloti.object.parameter.ParameterFrac32;
import axoloti.patch.object.parameter.ParameterInstanceController;

/**
 *
 * @author jtaelman
 */
abstract class ParameterInstanceViewFrac32 extends ParameterInstanceView {

    ParameterInstanceViewFrac32(ParameterInstanceController controller) {
        super(controller);
    }

    @Override
    public String GenerateParameterInitializer() {
// { type: param_type_frac, unit: param_unit_abstract, signals: 0, pfunction: 0, d: { frac: { finalvalue:0,  0,  0,  0,  0}}},
//        String pname = GetUserParameterName();
        ParameterFrac32 parameter = (ParameterFrac32) (getModel().getModel());
        String s = "{ type: " + parameter.GetCType()
                + ", unit: " + parameter.GetCUnit()
                + ", signals: 0"
                + ", pfunction: " + ((getModel().GetPFunction() == null) ? "0" : getModel().GetPFunction());
        Double v1 = (double) getModel().getValue();
        double v = v1;
        s += ", d: { frac: { finalvalue: 0"
                + ", value: " + getModel().valToInt32(v)
                + ", modvalue: " + getModel().valToInt32(v)
                + ", offset: " + getModel().GetCOffset()
                + ", multiplier: " + getModel().GetCMultiplier()
                + "}}},\n";
        return s;
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getModel().getOnParent() && (enableOnParent)) {
            return "%" + getModel().ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.frac.finalvalue";
        }
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".value";
    }

}
