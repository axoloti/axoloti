package axoloti.swingui.patch.object;

import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.TextFieldComponent;
import axoloti.swingui.patch.PatchViewSwing;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

class AxoObjectInstanceViewComment extends AxoObjectInstanceViewAbstract {

    AxoObjectInstanceViewComment(AxoObjectInstanceComment objectInstance, PatchViewSwing patchView) {
        super(objectInstance, patchView);
        initComponents();
    }

    @Override
    public AxoObjectInstanceComment getDModel() {
        return (AxoObjectInstanceComment) super.getDModel();
    }

    private void initComponents() {
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        instanceLabel = new LabelComponent(getDModel().getCommentText());
        instanceLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        instanceLabel.setAlignmentX(CENTER_ALIGNMENT);
        instanceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    addInstanceNameEditor();
                }
                if (getPatchView() != null) {
                    if (me.getClickCount() == 1) {
                        if (me.isShiftDown()) {
                            model.getController().changeSelected(!getDModel().getSelected());
                            me.consume();
                        } else if (!getDModel().getSelected()) {
                            getDModel().getParent().getController().selectNone();
                            model.getController().changeSelected(true);
                            me.consume();
                        }
                    }
                }
            }

        });
        instanceLabel.addMouseMotionListener(this);
        add(instanceLabel);
        setLocation(getDModel().getX(), getDModel().getY());

        resizeToGrid();
        setVisible(true);
    }

    @Override
    void handleInstanceNameEditorAction() {
        if (textFieldInstanceName == null) {
            throw new Error("textFieldInstanceName is null");
        }
        String s = textFieldInstanceName.getText();
        String prev = (String) model.getController().getModelProperty(AxoObjectInstanceComment.COMMENT);
        if (!s.equals(prev)) {
            model.getController().addMetaUndo("edit comment");
            model.getController().changeComment(s);
        }
        if (textFieldInstanceName.getParent() != null) {
            textFieldInstanceName.getParent().remove(textFieldInstanceName);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        textFieldInstanceName = new TextFieldComponent(getDModel().getCommentText());
        textFieldInstanceName.selectAll();
//        InstanceNameTF.setInputVerifier(new AxoObjectInstanceNameVerifier());
        textFieldInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleInstanceNameEditorAction();
            }
        });
        textFieldInstanceName.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                handleInstanceNameEditorAction();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        textFieldInstanceName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInstanceNameEditorAction();
                }
            }
        });
        getParent().add(textFieldInstanceName, 0);
        textFieldInstanceName.setLocation(getLocation().x, getLocation().y + instanceLabel.getLocation().y);
        textFieldInstanceName.setSize(getWidth(), 15);
        textFieldInstanceName.setVisible(true);
        textFieldInstanceName.requestFocus();
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
