package axoloti.codegen.patch.object.parameter;

import axoloti.object.parameter.ParameterFrac32;
import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
abstract class ParameterInstanceViewFrac32 extends ParameterInstanceView {

    ParameterInstanceViewFrac32(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public String generateParameterInitializer() {
// { type: param_type_frac, unit: param_unit_abstract, signals: 0, pfunction: 0, d: { frac: { finalvalue:0,  0,  0,  0,  0}}},
//        String pname = GetUserParameterName();
        ParameterFrac32 parameter = (ParameterFrac32) (getDModel().getDModel());
        Double v1 = (Double) getDModel().getValue();
        double v = v1;
        String s = "{ type: " + parameter.getCType()
                + ", unit: " + parameter.getCUnit()
                + ", signals: 0"
                + ", pfunction: " + ((getDModel().getPFunction() == null) ? "0" : getDModel().getPFunction())
                + ", d: { frac: { finalvalue: 0"
                + ", value: " + getDModel().valToInt32(v)
                + ", modvalue: " + getDModel().valToInt32(v)
                + ", offset: " + getDModel().getCOffset()
                + ", multiplier: " + getDModel().getCMultiplier()
                + "}}},\n";
        return s;
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getDModel().getOnParent() && (enableOnParent)) {
            return "%" + getDModel().getControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.frac.finalvalue";
        }
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".value";
    }

}
