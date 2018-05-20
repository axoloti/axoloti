
package axoloti.patch;

import axoloti.datatypes.ValueFrac32;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.UndoManager1;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.IAxoObject;
import axoloti.object.inlet.Inlet;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.outlet.Outlet;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.parameter.Parameter;
import axoloti.object.parameter.ParameterFrac32UMap;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import java.awt.Point;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jtaelman
 */
public class PatchModelTest {

    public PatchModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Two object instances in a patch should not have the same name
     */
    @Test
    public void testObjectInstanceNaming() {
        PatchModel patch = new PatchModel();
        IAxoObject obj = new AxoObject("obj", "description");
        IAxoObjectInstance obji_1 = patch.getController().addObjectInstance(obj, new Point(10, 10));
        IAxoObjectInstance obji_2 = patch.getController().addObjectInstance(obj, new Point(50, 10));
        String objname = "duplicate_name";
        assert (obji_1.getController().changeInstanceName(objname));
        assertFalse(obji_2.getController().changeInstanceName(objname));
        assertThat(obji_1.getInstanceName(), equalTo(objname));
        assertThat(obji_2.getInstanceName(), not(equalTo(objname)));
    }

    /**
     * Parameter instances gets default parameter value
     */
    @Test
    public void testParamDefaultValue() {
        PatchModel patch = new PatchModel();
        IAxoObject obj = new AxoObject("obj", "description");
        Double v = 3.125;
        Parameter param = new ParameterFrac32UMap("param", new ValueFrac32(v));
        obj.getController().addParameter(param);
        IAxoObjectInstance obji_1 = patch.getController().addObjectInstance(obj, new Point(10, 10));
        ParameterInstance parami = obji_1.getParameterInstances().get(0);
        Double v2 = (Double) parami.getValue();
        assertThat(v2, equalTo(v));
    }

    /*
     * Removing an object with parameter-on-parent removes parameter on parent
     */
    @Test
    public void testParamOnParentRemoval() {
        PatchModel patch = new PatchModel();
        IAxoObject patcher_obj = new AxoObjectPatcher();
        AxoObjectInstancePatcher patcher_obji = (AxoObjectInstancePatcher) patch.getController().addObjectInstance(patcher_obj, new Point(10, 10));

        PatchModel subpatch = patcher_obji.getSubPatchModel();
        IAxoObject obj = new AxoObject("obj", "description");
        IAxoObjectInstance obji_1 = subpatch.getController().addObjectInstance(obj, new Point(10, 10));

        Parameter param = new ParameterFrac32UMap("param", new ValueFrac32(0));
        obj.getController().addParameter(param);

        ParameterInstance parami = obji_1.getParameterInstances().get(0);
        assertNotNull(parami);
        parami.getController().changeOnParent(true);

        ParameterInstance parent_parami = patcher_obji.getParameterInstances().get(0);
        assertNotNull(parent_parami);

        subpatch.getController().delete(obji_1);
        assert (patcher_obji.getParameterInstances().isEmpty());
    }

    /*
     * Parameter from parameter-on-parent gets default value from parameter
     */
    @Test
    public void testParamOnParentDefault() {
        PatchModel patch = new PatchModel();
        IAxoObject patcher_obj = new AxoObjectPatcher();
        AxoObjectInstancePatcher patcher_obji = (AxoObjectInstancePatcher) patch.getController().addObjectInstance(patcher_obj, new Point(10, 10));

        PatchModel subpatch = patcher_obji.getSubPatchModel();
        IAxoObject obj = new AxoObject("obj", "description");
        IAxoObjectInstance obji_1 = subpatch.getController().addObjectInstance(obj, new Point(10, 10));

        Parameter param = new ParameterFrac32UMap("param", new ValueFrac32(0));
        obj.getController().addParameter(param);

        ParameterInstance parami = obji_1.getParameterInstances().get(0);
        assertNotNull(parami);
        Double v = 3.25;
        parami.getController().changeValue(v);
        parami.getController().changeOnParent(true);

        ParameterInstance parent_parami = patcher_obji.getParameterInstances().get(0);
        assertNotNull(parent_parami);
        Double v2 = (Double) parent_parami.getValue();
        assertThat(v2, equalTo(v));

        // and changes default when param-on-parent changes value
        v = 4.75;
        parami.getController().changeValue(v);
        parent_parami.getController().applyDefaultValue();
        v2 = (Double) parent_parami.getValue();
        assertThat(v2, equalTo(v));
    }

    Double getValueOfFirstParamOfFirstObject(PatchModel m) {
        IAxoObjectInstance obji = m.getObjectInstances().get(0);
        assertNotNull(obji);
        ParameterInstance parami = obji.getParameterInstances().get(0);
        assertNotNull(parami);
        return (Double) parami.getValue();
    }

    void undo_and_redo(UndoManager1 u, int count) {
        for (int i = 0; i < count; i++) {
            u.undo();
        }
        for (int i = 0; i < count; i++) {
            u.redo();
        }
    }

    /*
     * Complex undo scenarion:
     * create paramOnParent
     * change parent param value
     * undo
     * undo
     * redo
     * redo
     */
    @Test
    public void testParamOnParentUndo() {
        PatchModel patch = new PatchModel();
        patch.setDocumentRoot(new AbstractDocumentRoot());
        PatchController pc = patch.getController();
        pc.addMetaUndo("undo 6");
        IAxoObject patcher_obj = new AxoObjectPatcher();
        AxoObjectInstancePatcher patcher_obji = (AxoObjectInstancePatcher) patch.getController().addObjectInstance(patcher_obj, new Point(10, 10));
        pc.addMetaUndo("undo 5");
        PatchModel subpatch = patcher_obji.getSubPatchModel();
        IAxoObject obj = new AxoObject("obj", "description");
        IAxoObjectInstance obji_1 = subpatch.getController().addObjectInstance(obj, new Point(10, 10));
        pc.addMetaUndo("undo 4");
        Parameter param = new ParameterFrac32UMap("param", new ValueFrac32(0));
        obj.getController().addParameter(param);
        pc.addMetaUndo("undo 3");
        ParameterInstance parami = obji_1.getParameterInstances().get(0);
        assertNotNull(parami);
        Double v1 = 3.25;
        parami.getController().changeValue(v1);
        pc.addMetaUndo("undo 2");
        parami.getController().changeOnParent(true);
        pc.addMetaUndo("undo 1");

        ParameterInstance parent_parami = patcher_obji.getParameterInstances().get(0);
        assertNotNull(parent_parami);
        Double v2 = 7.0;
        parent_parami.getController().changeValue(v2);

        UndoManager1 u = pc.getUndoManager();
        u.undo();
        assertThat(v1, equalTo(getValueOfFirstParamOfFirstObject(patch)));
        u.redo();
        undo_and_redo(u, 1);
        assertThat(v2, equalTo(getValueOfFirstParamOfFirstObject(patch)));
        undo_and_redo(u, 2);
        assertThat(v2, equalTo(getValueOfFirstParamOfFirstObject(patch)));
        undo_and_redo(u, 3);
        assertThat(v2, equalTo(getValueOfFirstParamOfFirstObject(patch)));
        undo_and_redo(u, 4);
        assertThat(v2, equalTo(getValueOfFirstParamOfFirstObject(patch)));
        undo_and_redo(u, 5);
        assertThat(v2, equalTo(getValueOfFirstParamOfFirstObject(patch)));
        undo_and_redo(u, 6);
        assertThat(v2, equalTo(getValueOfFirstParamOfFirstObject(patch)));
    }


    /*
     * Test "inlet f" objects in subpatch
     */
    @Test
    public void testInletObjectInSubpatch() {
        PatchModel patch = new PatchModel();
        patch.setDocumentRoot(new AbstractDocumentRoot());
        PatchController pc = patch.getController();
        IAxoObject patcher_obj = new AxoObjectPatcher();
        AxoObjectInstancePatcher patcher_obji = (AxoObjectInstancePatcher) patch.getController().addObjectInstance(patcher_obj, new Point(10, 10));
        PatchModel subpatch = patcher_obji.getSubPatchModel();
        IAxoObject obj_1 = new AxoObject("patch/inlet f", "description");
        IAxoObjectInstance obji_1 = subpatch.getController().addObjectInstance(obj_1, new Point(10, 10));
        String inletname = "inletname";
        obji_1.getController().changeInstanceName(inletname);

        IAxoObject obj_2 = new AxoObject("patch/outlet f", "description");
        IAxoObjectInstance obji_2 = subpatch.getController().addObjectInstance(obj_2, new Point(10, 10));
        String outletname = "outletname";
        obji_2.getController().changeInstanceName(outletname);

        // verify that the inlet appears in parent
        InletInstance inlet = patcher_obji.getInletInstances().get(0);
        assertNotNull(inlet);
        assertThat(inletname, equalTo(inlet.getName()));
        // verify that the outlet appears in parent
        OutletInstance outlet = patcher_obji.getOutletInstances().get(0);
        assertNotNull(outlet);
        assertThat(outletname, equalTo(outlet.getName()));
        // verify that the changing the inlet object instancename
        // also changes the inlet name
        String inletname2 = "inletname2";
        obji_1.getController().changeInstanceName(inletname2);
        assertThat(inletname2, equalTo(inlet.getName()));
        // verify that the changing the outlet object instancename
        // also changes the outlet name
        String outletname2 = "outletname2";
        obji_2.getController().changeInstanceName(outletname2);
        assertThat(outletname2, equalTo(outlet.getName()));

        pc.addMetaUndo("undo 1");
        // delete the inlet/outlet objects
        // verify that also the corresponding inlets/outlets disappear
        pc.delete(obji_1);
        pc.delete(obji_2);
        assert (patcher_obji.getInletInstances().isEmpty());
        assert (patcher_obji.getOutletInstances().isEmpty());

        // verify that also the same inlets/outlets re-appear
        // after undo
        pc.getUndoManager().undo();
        assertEquals(patcher_obji.getInletInstances().get(0), inlet);
        assertEquals(patcher_obji.getOutletInstances().get(0), outlet);
    }

    /**
     * Create net, delete inlet, net should be gone
     */
    @Test
    public void testNetDisconnectsWhenInletIsDeleted() {
        PatchModel patch = new PatchModel();

        IAxoObject obj_1 = new AxoObject("obj", "description");
        Inlet inlet = new InletFrac32("in", "");
        obj_1.getController().addInlet(inlet);
        IAxoObjectInstance obji_1 = patch.getController().addObjectInstance(obj_1, new Point(10, 10));
        InletInstance inleti_1 = obji_1.getInletInstances().get(0);

        IAxoObject obj_2 = new AxoObject("obj", "description");
        Outlet outlet = new OutletFrac32("out", "");
        obj_2.getController().addOutlet(outlet);
        IAxoObjectInstance obji_2 = patch.getController().addObjectInstance(obj_2, new Point(10, 10));
        OutletInstance outleti_1 = obji_2.getOutletInstances().get(0);

        patch.getController().addConnection(inleti_1, outleti_1);

        assertThat(patch.getNets().size(), equalTo(1));
        obj_1.getController().removeInlet(inlet);
        assertThat(patch.getNets().size(), equalTo(0));
    }

    /**
     * Create net, delete outlet, net should be gone
     */
    @Test
    public void testNetDisconnectsWhenOutletIsDeleted() {
        PatchModel patch = new PatchModel();

        IAxoObject obj_1 = new AxoObject("obj", "description");
        Inlet inlet = new InletFrac32("in", "");
        obj_1.getController().addInlet(inlet);
        IAxoObjectInstance obji_1 = patch.getController().addObjectInstance(obj_1, new Point(10, 10));
        InletInstance inleti_1 = obji_1.getInletInstances().get(0);

        IAxoObject obj_2 = new AxoObject("obj", "description");
        Outlet outlet = new OutletFrac32("out", "");
        obj_2.getController().addOutlet(outlet);
        IAxoObjectInstance obji_2 = patch.getController().addObjectInstance(obj_2, new Point(10, 10));
        OutletInstance outleti_1 = obji_2.getOutletInstances().get(0);

        patch.getController().addConnection(inleti_1, outleti_1);

        assertThat(patch.getNets().size(), equalTo(1));
        obj_2.getController().removeOutlet(outlet);
        assertThat(patch.getNets().size(), equalTo(0));
    }

    /**
     * Create net, delete object, net should be gone
     */
    @Test
    public void testNetDisconnectsWhenObjectIsDeleted() {
        PatchModel patch = new PatchModel();

        IAxoObject obj_1 = new AxoObject("obj", "description");
        Inlet inlet = new InletFrac32("in", "");
        obj_1.getController().addInlet(inlet);
        IAxoObjectInstance obji_1 = patch.getController().addObjectInstance(obj_1, new Point(10, 10));
        InletInstance inleti_1 = obji_1.getInletInstances().get(0);

        IAxoObject obj_2 = new AxoObject("obj", "description");
        Outlet outlet = new OutletFrac32("out", "");
        obj_2.getController().addOutlet(outlet);
        IAxoObjectInstance obji_2 = patch.getController().addObjectInstance(obj_2, new Point(10, 10));
        OutletInstance outleti_1 = obji_2.getOutletInstances().get(0);

        patch.getController().addConnection(inleti_1, outleti_1);

        assertThat(patch.getNets().size(), equalTo(1));
        patch.getController().delete(obji_2);
        assertThat(patch.getNets().size(), equalTo(0));
    }

}
