package axoloti.mvc.array;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.mvctest.TestViewFactory;
import axoloti.parameters.Parameter;
import axoloti.parameters.ParameterFrac32SMapPitch;
import axoloti.parameters.ParameterInstance;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author jtaelman
 */
public class ArrayViewPanel extends JPanel implements ArrayView {

    ArrayController controller;
    JPanel jp; // panel containing array elements...
    ArrayList<AbstractView> subviews = new ArrayList<>();

    public ArrayViewPanel(ArrayController controller) {

        // some test buttons
        JButton btnAdd = new JButton("add");
        btnAdd.addActionListener(addParam);
        JButton btnRemove = new JButton("remove");
        btnRemove.addActionListener(removeParam);
        add(btnAdd);
        add(btnRemove);

        // generic
        jp = new JPanel();
        this.controller = controller;
        for (AbstractController c : controller.subcontrollers) {
            AbstractView v = TestViewFactory.createView(c);
            subviews.add(v);
            if (v==null) {
                System.out.println("createView returned null, controller class = " + c.getClass().getCanonicalName());
            }
            jp.add((Component) v);
        }
        add(jp);
        controller.addView(this);
    }

    int i = 0;

    ActionListener addParam = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Parameter param = (new ParameterFrac32SMapPitch("param_" + i));
            ParameterInstance pi = param.InstanceFactory();
            i++;
            pi.parameter = param;
            controller.add(pi);
        }
    };

    ActionListener removeParam = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.removeLast();
        }
    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ArrayController.ARRAY)) {
            ArrayList<AbstractView> subviews2 = (ArrayList<AbstractView>) subviews.clone();
            for (AbstractView view : subviews) {
                if (!controller.subcontrollers.contains(view.getController())) {
                    subviews2.remove(view);
                }
            }
            jp.removeAll();
            subviews = new ArrayList<>();
            for (AbstractController ctrl : controller.subcontrollers) {
                // do we have a view already?
                AbstractView view = null;
                for (AbstractView view2 : subviews2) {
                    if (ctrl == view2.getController()) {
                        view = view2;
                        break;
                    }
                }
                if (view == null) {
                    view = TestViewFactory.createView(ctrl);
                }
                subviews.add(view);
                jp.add((Component) view);
            }
            jp.revalidate();
        }
    }

    @Override
    public AbstractController getController() {
        return controller;
    }

}
