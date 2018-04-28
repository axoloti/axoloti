package axoloti.swingui.patch.object;

import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.TextFieldComponent;
import axoloti.swingui.patch.PatchViewSwing;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

class AxoObjectInstanceViewComment extends AxoObjectInstanceViewAbstract {

    public AxoObjectInstanceViewComment(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
    }

    @Override
    public AxoObjectInstanceComment getModel() {
        return (AxoObjectInstanceComment) super.getModel();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        instanceLabel = new LabelComponent(getModel().getCommentText());
        instanceLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        instanceLabel.setAlignmentX(CENTER_ALIGNMENT);
        instanceLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    addInstanceNameEditor();
                }
                if (getPatchView() != null) {
                    if (me.getClickCount() == 1) {
                        if (me.isShiftDown()) {
                            getController().changeSelected(!getModel().getSelected());
                            me.consume();
                        } else if (!getModel().getSelected()) {
                            getController().getModel().getParent().getControllerFromModel().SelectNone();
                            getController().changeSelected(true);
                            me.consume();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                //////AxoObjectInstanceViewComment.this.mousePressed(me);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //////AxoObjectInstanceViewComment.this.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        instanceLabel.addMouseMotionListener(this);
        add(instanceLabel);
        setLocation(getModel().getX(), getModel().getY());

        resizeToGrid();
        setVisible(true);
    }

    @Override
    void handleInstanceNameEditorAction() {
        String s = InstanceNameTF.getText();
        String prev = (String) getController().getModelProperty(AxoObjectInstanceComment.COMMENT);
        if (!s.equals(prev)) {
            getController().addMetaUndo("edit comment");
            getController().changeComment(s);
        }
        if (InstanceNameTF != null && InstanceNameTF.getParent() != null) {
            InstanceNameTF.getParent().remove(InstanceNameTF);
        }
    }

    @Override
    public void addInstanceNameEditor() {
        InstanceNameTF = new TextFieldComponent(getModel().getCommentText());
        InstanceNameTF.selectAll();
//        InstanceNameTF.setInputVerifier(new AxoObjectInstanceNameVerifier());
        InstanceNameTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleInstanceNameEditorAction();
            }
        });
        InstanceNameTF.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                handleInstanceNameEditorAction();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        InstanceNameTF.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleInstanceNameEditorAction();
                }
            }
        });
        getParent().add(InstanceNameTF, 0);
        InstanceNameTF.setLocation(getLocation().x, getLocation().y + instanceLabel.getLocation().y);
        InstanceNameTF.setSize(getWidth(), 15);
        InstanceNameTF.setVisible(true);
        InstanceNameTF.requestFocus();
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
