package axoloti.mvctest;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.array.ArrayController;

/**
 *
 * @author jtaelman
 */
public class TestController extends AbstractController<TestModel, TestView> {

    public static final String ELEMENT_SPIN_VALUE = "Spin";

    ArrayController paramControllers;
    ArrayController attrControllers;
    ArrayController inletControllers;

    public TestController(TestModel model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        paramControllers = new ArrayController(model.paramInstances, documentRoot);
        attrControllers = new ArrayController(model.attributeInstances, documentRoot);
        inletControllers = new ArrayController(model.inletInstances, documentRoot);
    }

    public void changeSpinValue(int newX) {
        setModelUndoableProperty(ELEMENT_SPIN_VALUE, (Integer) newX);
    }

}
