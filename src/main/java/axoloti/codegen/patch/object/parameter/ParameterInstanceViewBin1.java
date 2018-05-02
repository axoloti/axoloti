package axoloti.codegen.patch.object.parameter;
import axoloti.patch.object.parameter.ParameterInstanceController;

/**
 *
 * @author jtaelman
 */
class ParameterInstanceViewBin1 extends ParameterInstanceViewBin {

    ParameterInstanceViewBin1(ParameterInstanceController controller) {
        super(controller);
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, "(data2>0)");
    }

}
