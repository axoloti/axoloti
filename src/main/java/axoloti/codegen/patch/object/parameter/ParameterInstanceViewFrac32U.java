package axoloti.codegen.patch.object.parameter;
import axoloti.patch.object.parameter.ParameterInstanceController;

/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewFrac32U extends ParameterInstanceViewFrac32 {

    ParameterInstanceViewFrac32U(ParameterInstanceController controller) {
        super(controller);
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, "(data2!=127)?data2<<20:0x07FFFFFF");
    }

}
