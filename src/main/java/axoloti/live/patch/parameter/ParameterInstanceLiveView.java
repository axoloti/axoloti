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
        byte[] data = new byte[14];
        data[0] = 'A';
        data[1] = 'x';
        data[2] = 'o';
        data[3] = 'P';
        int pid = model.getObjectInstance().getParent().getIID();
        data[4] = (byte) pid;
        data[5] = (byte) (pid >> 8);
        data[6] = (byte) (pid >> 16);
        data[7] = (byte) (pid >> 24);
        int tvalue = getDModel().valToInt32(getDModel().getValue());
        data[8] = (byte) tvalue;
        data[9] = (byte) (tvalue >> 8);
        data[10] = (byte) (tvalue >> 16);
        data[11] = (byte) (tvalue >> 24);
        data[12] = (byte) (index);
        data[13] = (byte) (index >> 8);
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
