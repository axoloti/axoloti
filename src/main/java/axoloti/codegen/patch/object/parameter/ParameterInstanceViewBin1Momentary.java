package axoloti.codegen.patch.object.parameter;

import axoloti.patch.object.parameter.ParameterInstance;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceViewBin1Momentary extends ParameterInstanceViewBin {

    ParameterInstanceViewBin1Momentary(ParameterInstance parameterInstance) {
        super(parameterInstance);
    }

    /*
    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        // Hmmm how to deal with this?
        // Normal behavious would be generating a pulse triggered by any incoming CC value > 0 ?
        //
        // Hi, MIDI specialists, how common is this needed, how well is it supported in sequencers etc?
        //
        // Do we need to extend the parameter model to objects writing a new parameter value themselves?
        return "";
    }
     */
}
