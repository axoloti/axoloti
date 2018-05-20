package axoloti.codegen.patch.object.parameter;
import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewFrac32U extends ParameterInstanceViewFrac32 {

    ParameterInstanceViewFrac32U(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public String generateCodeMidiHandler(String vprefix) {
        return generateMidiCCCodeSub(vprefix, "(data2!=127)?data2<<20:0x07FFFFFF");
    }

}
