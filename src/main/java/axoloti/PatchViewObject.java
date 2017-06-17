package axoloti;

import axoloti.atom.AtomDefinition;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.ObjectInstanceController;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import java.beans.PropertyChangeEvent;

/**
 * The {@code PatchViewObject} class is a view of a Patch that collects the Atoms
 * that escalate to a parent object.
 * 
 * TODO: parameter-on-parent
 * TODO: track order
 * 
 * @author jtaelman
 */
public class PatchViewObject extends PatchAbstractView {

    final AxoObjectPatcher targetObj;

    class ObjectInstanceViewParenting implements AbstractView<ObjectInstanceController> {

        final ObjectInstanceController controller;
        final AtomDefinition atom[];

        public ObjectInstanceViewParenting(ObjectInstanceController controller) {
            this.controller = controller;
            String typeName = controller.getModel().typeName;
            if (typeName.equals("patch/inlet a")) {
                Inlet i = new InletFrac32Buffer(controller.getModel().getInstanceName(), "");
                targetObj.getInlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/inlet b")) {
                Inlet i = new InletBool32(controller.getModel().getInstanceName(), "");
                targetObj.getInlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/inlet f")) {
                Inlet i = new InletFrac32(controller.getModel().getInstanceName(), "");
                targetObj.getInlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/inlet i")) {
                Inlet i = new InletInt32(controller.getModel().getInstanceName(), "");
                targetObj.getInlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/outlet a")) {
                Outlet i = new OutletFrac32Buffer(controller.getModel().getInstanceName(), "");
                targetObj.getOutlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/outlet b")) {
                Outlet i = new OutletBool32(controller.getModel().getInstanceName(), "");
                targetObj.getOutlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/outlet f")) {
                Outlet i = new OutletFrac32(controller.getModel().getInstanceName(), "");
                targetObj.getOutlets().add(i);
                atom = new AtomDefinition[]{i};
            } else if (typeName.equals("patch/outlet i")) {
                Outlet i = new OutletInt32(controller.getModel().getInstanceName(), "");
                targetObj.getOutlets().add(i);
                atom = new AtomDefinition[]{i};
            } else {
                atom = null;
            }
        }

        @Override
        public void modelPropertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ObjectInstanceController.OBJ_INSTANCENAME)) {
                String typeName = controller.getModel().typeName;
                if (typeName.equals("patch/inlet a")
                        || typeName.equals("patch/inlet b")
                        || typeName.equals("patch/inlet f")
                        || typeName.equals("patch/inlet i")
                        || typeName.equals("patch/outlet a")
                        || typeName.equals("patch/outlet b")
                        || typeName.equals("patch/outlet f")
                        || typeName.equals("patch/outlet i")) {
                    atom[0].setName((String) evt.getNewValue());
                }
            }
        }

        @Override
        public ObjectInstanceController getController() {
            return controller;
        }
    }
    
    ArrayView<ObjectInstanceViewParenting> objectInstanceViews;

    public PatchViewObject(PatchController controller, AxoObjectPatcher targetObj) {
        super(controller);
        this.targetObj = targetObj;

        objectInstanceViews = new ArrayView<ObjectInstanceViewParenting>(controller.objectInstanceControllers) {

            @Override
            public ObjectInstanceViewParenting viewFactory(AbstractController ctrl) {
                return new ObjectInstanceViewParenting((ObjectInstanceController) ctrl);
            }

            @Override
            public void updateUI() {
            }

            @Override
            public void removeView(ObjectInstanceViewParenting view) {
                if (view.atom != null) {
                    for (AtomDefinition a : view.atom) {
                        if (a instanceof Inlet) {
                            targetObj.getInlets().remove((Inlet) a);
                        } else if (a instanceof Outlet) {
                            targetObj.getOutlets().remove((Outlet) a);
                        }
                    }
                }
            }
        };
        controller.objectInstanceControllers.addView(objectInstanceViews);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

}
