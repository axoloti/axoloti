package axoloti.piccolo.patch.object;

import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.PTextFieldComponent;
import axoloti.piccolo.patch.PatchViewPiccolo;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceViewComment extends PAxoObjectInstanceViewAbstract {

    public PAxoObjectInstanceViewComment(ObjectInstanceController controller, PatchViewPiccolo p) {
        super(controller, p);
        initComponents();
    }

    @Override
    public AxoObjectInstanceComment getModel() {
        return (AxoObjectInstanceComment) super.getModel();
    }

    @Override
    protected void handleInstanceNameEditorAction() {
        if(InstanceNameTF != null) {
            String s = InstanceNameTF.getText();
            removeChild(InstanceNameTF);
            InstanceNameTF = null;
            instanceLabel.setVisible(true);
            getController().addMetaUndo("edit comment");
            getController().changeComment(s);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        InstanceNameTF = new PTextFieldComponent(getModel().getCommentText());
        InstanceNameTF.selectAll();
        PBasicInputEventHandler inputEventHandler = new PBasicInputEventHandler() {
                @Override
                public void keyboardFocusLost(PInputEvent e) {
                    handleInstanceNameEditorAction();
                }

                @Override
                public void keyboardFocusGained(PInputEvent e) {
                    InstanceNameTF.selectAll();
                }

                @Override
                public void keyPressed(PInputEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        handleInstanceNameEditorAction();
                    }
                }
            };

        InstanceNameTF.addInputEventListener(inputEventHandler);

        instanceLabel.setVisible(false);

        Dimension d = InstanceNameTF.getSize();
        d.width = (int) getWidth();
        d.height = 15;
        InstanceNameTF.setSize(d);

        addChild(1, InstanceNameTF);
        InstanceNameTF.raiseToTop();
        InstanceNameTF.setTransform(instanceLabel.getTransform());
        InstanceNameTF.grabFocus();
    }

    private void initComponents() {
        setDrawBorder(true);

        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));

        instanceLabel = new PLabelComponent(getModel().getCommentText());
        instanceLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        instanceLabel.setAlignmentX(CENTER_ALIGNMENT);

        addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                }
            }
        });

        addChild(instanceLabel);
        resizeToGrid();
        setVisible(true);
    }

    @Override
    public void showInstanceName(String s) {
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoObjectInstanceComment.COMMENT.is(evt)) {
            instanceLabel.setText((String) evt.getNewValue());
            resizeToGrid();
        }
    }
}
