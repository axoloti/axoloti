package axoloti.codegen.patch.object.display;

import axoloti.mvc.View;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.utils.CodeGeneration;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
public abstract class DisplayInstanceView extends View<DisplayInstanceController> {

    DisplayInstanceView(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        // OK, empty
    }

    public DisplayInstance getModel() {
        return getController().getModel();
    }

    public abstract String valueName(String vprefix);

    protected int index;
    protected int offset;


    public int getIndex() {
        return index;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setIndex(int i) {
        index = i;
    }

    public String GetCName() {
        return getModel().getModel().GetCName();
    }

    public String GenerateDisplayMetaInitializer() {
        String c = "{ display_type: " + getModel().getModel().GetCMetaType() + ", name: "
                + CodeGeneration.CPPCharArrayStaticInitializer(getModel().getModel().getName(), CodeGeneration.param_name_length)
                + ", displaydata: &displayVector[" + offset + "]},\n";
        return c;
    }

    public abstract void ProcessByteBuffer(ByteBuffer bb);

    public abstract String GenerateCodeInit(String vprefix);

}
