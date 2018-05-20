package axoloti.codegen.patch.object.parameter;
import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewBin1 extends ParameterInstanceViewBin {

    ParameterInstanceViewBin1(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public String generateCodeMidiHandler(String vprefix) {
        return generateMidiCCCodeSub(vprefix, "(data2>0)");
    }

}
