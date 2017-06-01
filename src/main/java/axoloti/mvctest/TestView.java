package axoloti.mvctest;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.mvc.UndoUI;
import axoloti.mvc.array.ArrayViewPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 *
 * @author jtaelman
 */
public class TestView extends JFrame implements AbstractView, UndoableEditListener {

    JSpinner spin1;
    TestController controller;
    UndoUI undoUi;

    public TestView(TestController controller) {
        super();
        this.controller = controller;
        initComponents();
        setVisible(true);
        controller.getDocumentRoot().addUndoListener(this);
    }

    void initComponents() {
        undoUi = new UndoUI(getUndoManager());
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        spin1 = new JSpinner();
        spin1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
//                controller.changeSpinValue((Integer)(spin1.getValue()));
                controller.changeSpinValue((int) (spin1.getValue()));
            }
        });
        p1.add(spin1);

        ArrayViewPanel avp1 = new ArrayViewPanel(controller.paramControllers);
        p1.add(avp1);

        ArrayViewPanel avp2 = new ArrayViewPanel(controller.attrControllers);
        p1.add(avp2);

        ArrayViewPanel avp3 = new ArrayViewPanel(controller.inletControllers);
        p1.add(avp3);

        add(p1);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem menuItemNewView = new JMenuItem("New view");
        menuItemNewView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TestView tv = new TestView(controller);
            }
        });
        menuFile.add(menuItemNewView);

        JMenu menuEdit = new JMenu("Edit");
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuEdit.add(undoUi.createMenuItemUndo());
        menuEdit.add(undoUi.createMenuItemRedo());
        setJMenuBar(menuBar);
        setSize(400, 200);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(
                TestController.ELEMENT_SPIN_VALUE)) {
            Integer newValue = (Integer) evt.getNewValue();
            spin1.setValue(newValue);
        }
    }

    UndoManager getUndoManager() {
        return controller.getUndoManager();
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        undoUi.undoableEditHappened(e);
    }

    @Override
    public AbstractController getController() {
        return controller;
    }

}
