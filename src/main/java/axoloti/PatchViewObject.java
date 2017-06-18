package axoloti;

import axoloti.atom.AtomDefinition;
import axoloti.inlets.Inlet;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import axoloti.mvc.array.ArrayModel;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.ObjectInstanceController;
import axoloti.objectviews.AxoObjectInstanceViewParenting;
import axoloti.outlets.Outlet;
import axoloti.parameters.Parameter;
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

    ArrayView<AxoObjectInstanceViewParenting> objectInstanceViews;

    ArrayModel<AtomDefinition> atomDefinitionsOnParent;
    ArrayController<AbstractController, AtomDefinition, AbstractController> atomDefinitionsOnParentController;
    ArrayView<AbstractView> atomDefinitionsOnParentView;
    
    public PatchViewObject(PatchController controller, AxoObjectPatcher targetObj) {
        super(controller);
        this.targetObj = targetObj;
        atomDefinitionsOnParent = new ArrayModel<>();
        atomDefinitionsOnParentController = new ArrayController<AbstractController, AtomDefinition, AbstractController>(atomDefinitionsOnParent, null, null) {

            @Override
            public AbstractController createController(AtomDefinition model, AbstractDocumentRoot documentRoot, AbstractController parent) {
                return new AbstractController(model, documentRoot, parent) {};
            }
        };

        objectInstanceViews = new ArrayView<AxoObjectInstanceViewParenting>(controller.objectInstanceControllers) {

            @Override
            public AxoObjectInstanceViewParenting viewFactory(AbstractController ctrl) {
                return new AxoObjectInstanceViewParenting((ObjectInstanceController) ctrl, atomDefinitionsOnParent);
            }

            @Override
            public void updateUI() {
            }

            @Override
            public void removeView(AxoObjectInstanceViewParenting view) {
                if (view.getAtomDefinitions() != null) {
                    for (AtomDefinition a : view.getAtomDefinitions()) {
                        atomDefinitionsOnParent.remove(a);
                    }
                }
            }
        };

        controller.objectInstanceControllers.addView(objectInstanceViews);

        atomDefinitionsOnParentView = new ArrayView<AbstractView>(atomDefinitionsOnParentController) {
            
            @Override
            public void updateUI() {
            }
            
            @Override
            public AbstractView viewFactory(AbstractController ctrl1) {
                AtomDefinition m = (AtomDefinition)ctrl1.getModel();
                if (m instanceof Inlet) {
                    targetObj.getInlets().add((Inlet) m);
                } else if (m instanceof Outlet) {
                    targetObj.getOutlets().add((Outlet) m);
                } else if (m instanceof Parameter) {
                    targetObj.getParameters().add((Parameter) m);
                }
                return new AbstractView() {
                    @Override
                    public void modelPropertyChange(PropertyChangeEvent evt) {
                        System.out.println("event >" + evt.getPropertyName() + " >" + evt.getNewValue());
                    }

                    @Override
                    public AbstractController getController() {
                        return ctrl1;
                    }
                };
            }
            
            @Override
            public void removeView(AbstractView view) {
                AtomDefinition m = (AtomDefinition)view.getController().getModel();
                if (m instanceof Inlet) {
                    targetObj.getInlets().remove((Inlet) m);
                } else if (m instanceof Outlet) {
                    targetObj.getOutlets().remove((Outlet) m);
                } else if (m instanceof Parameter) {
                    targetObj.getParameters().remove((Parameter) m);
                }                
            }
        };
        atomDefinitionsOnParentController.addView(atomDefinitionsOnParentView);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

}
