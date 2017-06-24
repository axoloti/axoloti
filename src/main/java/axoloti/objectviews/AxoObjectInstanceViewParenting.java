package axoloti.objectviews;

import axoloti.atom.AtomDefinition;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayModel;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.ObjectInstanceController;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import axoloti.parameters.Parameter;
import axoloti.parameters.ParameterInstanceController;
import java.beans.PropertyChangeEvent;


/*
 * @author jtaelman
 */
public class AxoObjectInstanceViewParenting implements AbstractView<ObjectInstanceController> {

    final ObjectInstanceController controller;
    final AtomDefinition[] atom;

    class ParameterOnParentView implements AbstractView {

        final ArrayModel<AtomDefinition> atomDefinitionsOnParent;
        final ParameterInstanceController pic;
        final AbstractController ctrl;
        Parameter p;

        public ParameterOnParentView(AbstractController ctrl, ParameterInstanceController pic, ArrayModel<AtomDefinition> atomDefinitionsOnParent) {
            this.pic = pic;
            this.atomDefinitionsOnParent = atomDefinitionsOnParent;
            this.ctrl = ctrl;
            p = pic.getModel().getParameterForParent();
            if (pic.getModel().getOnParent()) {
                atomDefinitionsOnParent.add(p);
            }
        }

        @Override
        public void modelPropertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ParameterInstanceController.ELEMENT_PARAM_ON_PARENT)) {
                if ((Boolean) evt.getNewValue() == true) {
                    atomDefinitionsOnParent.add(p);
                } else {
                    atomDefinitionsOnParent.remove(p);
                }
            } else if (evt.getPropertyName().equals(ParameterInstanceController.ELEMENT_PARAM_VALUE)) {
                // TODO : set default value
            }
        }

        @Override
        public AbstractController getController() {
            return ctrl;
        }
    }

    public AxoObjectInstanceViewParenting(ObjectInstanceController controller, ArrayModel<AtomDefinition> atomDefinitionsOnParent) {
        this.controller = controller;
        AxoObjectInstanceAbstract objInst = controller.getModel();
        String typeName = objInst.typeName;
        if (typeName.equals("patch/inlet a")) {
            Inlet i = new InletFrac32Buffer(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/inlet b")) {
            Inlet i = new InletBool32(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/inlet f")) {
            Inlet i = new InletFrac32(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/inlet i")) {
            Inlet i = new InletInt32(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/outlet a")) {
            Outlet i = new OutletFrac32Buffer(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/outlet b")) {
            Outlet i = new OutletBool32(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/outlet f")) {
            Outlet i = new OutletFrac32(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else if (typeName.equals("patch/outlet i")) {
            Outlet i = new OutletInt32(controller.getModel().getInstanceName(), "");
            atomDefinitionsOnParent.add(i);
            atom = new AtomDefinition[]{i};
        } else {
            atom = null;
            ArrayView<AbstractView> paramOnParentObserver = new ArrayView<AbstractView>(controller.parameterInstanceControllers) {
                @Override
                public void updateUI() {
                }

                @Override
                public AbstractView viewFactory(AbstractController ctrl1) {
                    ParameterInstanceController pic = (ParameterInstanceController) ctrl1;
                    return new ParameterOnParentView(ctrl1, pic, atomDefinitionsOnParent);
                }

                @Override
                public void removeView(AbstractView view) {
                    atomDefinitionsOnParent.remove(((ParameterOnParentView) view).p);
                }
            };
            controller.parameterInstanceControllers.addView(paramOnParentObserver);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ObjectInstanceController.OBJ_INSTANCENAME)) {
            String typeName = controller.getModel().getType().id;
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

    public AtomDefinition[] getAtomDefinitions() {
        return atom;
    }
}
