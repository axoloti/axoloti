package axoloti.live.patch.parameter;

import axoloti.live.patch.PatchViewLive;
import axoloti.mvc.View;
import axoloti.patch.object.parameter.ParameterInstance;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceLiveView extends View<ParameterInstance> {

    private boolean needsTransmit = false;
    private final int index;
    private final PatchViewLive patchViewLive;

    public ParameterInstanceLiveView(PatchViewLive patchViewLive, ParameterInstance parameterInstance, int index) {
        super(parameterInstance);
        this.index = index;
        this.patchViewLive = patchViewLive;
    }

    public byte[] TXData() {
        needsTransmit = false;
        byte[] data = new byte[8];
        int tvalue = getDModel().valToInt32(getDModel().getValue());
        data[0] = (byte) (index);
        data[1] = (byte) (index >> 8);
        data[2] = (byte) (index >> 16);
        data[3] = (byte) (index >> 24);
        data[4] = (byte) tvalue;
        data[5] = (byte) (tvalue >> 8);
        data[6] = (byte) (tvalue >> 16);
        data[7] = (byte) (tvalue >> 24);
        return data;
    }

    public boolean getNeedsTransmit() {
        return needsTransmit;
    }

    public void clearNeedsTransmit() {
        needsTransmit = false;
    }

    public void setNeedsTransmit(boolean needsTransmit) {
        this.needsTransmit = needsTransmit;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (ParameterInstance.VALUE.is(evt)) {
            needsTransmit = true;
        } else if (ParameterInstance.PRESETS.is(evt)) {
            patchViewLive.setNeedsPresetUpdate();
        }
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
