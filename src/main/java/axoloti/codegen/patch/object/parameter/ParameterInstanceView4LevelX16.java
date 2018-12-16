package axoloti.codegen.patch.object.parameter;

import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceView4LevelX16 extends ParameterInstanceViewInt32 {

    ParameterInstanceView4LevelX16(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    @Override
    public String generateParameterInitializer() {
        return "";
    }

    @Override
    public String generateCodeMidiHandler(String vprefix) {
        return "";
    }

}
