package axoloti.codegen.patch.object.display;

import axoloti.codegen.CodeGeneration;
import axoloti.mvc.View;
import axoloti.patch.object.display.DisplayInstance;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
public abstract class DisplayInstanceView extends View<DisplayInstance> {

    DisplayInstanceView(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        // OK, empty
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

    public String getCName() {
        return model.getDModel().getCName();
    }

    public String generateDisplayMetaInitializer() {
        String c = "{ display_type: " + getDModel().getDModel().getCMetaType() + ", name: "
                + CodeGeneration.CPPCharArrayStaticInitializer(getDModel().getDModel().getName(), CodeGeneration.PARAM_NAME_LENGTH)
                + ", displaydata: &displayVector[" + offset + "]},\n";
        return c;
    }

    public abstract void processByteBuffer(ByteBuffer bb);

    public abstract String generateCodeInit(String vprefix);

}
