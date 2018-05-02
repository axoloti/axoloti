package axoloti.codegen.patch.object.parameter;
import axoloti.patch.object.parameter.ParameterInstanceController;

/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewFrac32S extends ParameterInstanceViewFrac32 {

    ParameterInstanceViewFrac32S(ParameterInstanceController controller) {
        super(controller);
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, "(data2!=127)?(data2-64)<<21:0x07FFFFFF");
    }

}
