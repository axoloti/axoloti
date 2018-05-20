package axoloti.piccolo.patch.object;

import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.IAxoObjectInstance;
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

    public PAxoObjectInstanceViewComment(IAxoObjectInstance objectInstance, PatchViewPiccolo p) {
        super(objectInstance, p);
        initComponents();
    }

    @Override
    public AxoObjectInstanceComment getDModel() {
        return (AxoObjectInstanceComment) super.getDModel();
    }

    @Override
    protected void handleInstanceNameEditorAction() {
        if(textFieldInstanceName != null) {
            String s = textFieldInstanceName.getText();
            removeChild(textFieldInstanceName);
            textFieldInstanceName = null;
            instanceLabel.setVisible(true);
            getDModel().getController().addMetaUndo("edit comment");
            getDModel().getController().changeComment(s);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        textFieldInstanceName = new PTextFieldComponent(getDModel().getCommentText());
        textFieldInstanceName.selectAll();
        PBasicInputEventHandler inputEventHandler = new PBasicInputEventHandler() {
                @Override
                public void keyboardFocusLost(PInputEvent e) {
                    handleInstanceNameEditorAction();
                }

                @Override
                public void keyboardFocusGained(PInputEvent e) {
                    textFieldInstanceName.selectAll();
                }

                @Override
                public void keyPressed(PInputEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        handleInstanceNameEditorAction();
                    }
                }
            };

        textFieldInstanceName.addInputEventListener(inputEventHandler);

        instanceLabel.setVisible(false);

        Dimension d = textFieldInstanceName.getSize();
        d.width = (int) getWidth();
        d.height = 15;
        textFieldInstanceName.setSize(d);

        addChild(1, textFieldInstanceName);
        textFieldInstanceName.raiseToTop();
        textFieldInstanceName.setTransform(instanceLabel.getTransform());
        textFieldInstanceName.grabFocus();
    }

    private void initComponents() {
        setDrawBorder(true);

        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));

        instanceLabel = new PLabelComponent(getDModel().getCommentText());
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
