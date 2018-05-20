
package axoloti.patch.object;

/**
 *
 * @author jtaelman
 */
public class ObjectInstancePatcherController extends ObjectInstanceController {

    @Override
    protected AxoObjectInstancePatcher getModel() {
        return (AxoObjectInstancePatcher)super.getModel();
    }

    public ObjectInstancePatcherController(AxoObjectInstancePatcher model) {
        super(model);
    }

}
