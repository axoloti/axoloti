package axoloti.swingui.mvc;

import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.DocumentWindowList;
import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import java.awt.HeadlessException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author jtaelman
 */
public abstract class AJFrame<T extends IModel> extends JFrame implements DocumentWindow, IView<T> {

    final DocumentWindow parent;

    final protected T model;

    public AJFrame(T model, DocumentWindow parent) throws HeadlessException {
        super();
        this.model = model;
        this.parent = parent;
        initComponent();
    }

    private void initComponent() {
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                unregisterDocumentWindow();
            }

            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                registerDocumentWindow();
            }
        });

        registerDocumentWindow();
    }

    @Override
    public T getDModel() {
        return model;
    }

    private void registerDocumentWindow() {
        if (parent == null) {
            DocumentWindowList.registerWindow(this);
        } else if (!parent.getChildDocuments().contains(this)) {
            parent.getChildDocuments().add(this);
        }
    }

    private void unregisterDocumentWindow() {
        if (parent == null) {
            DocumentWindowList.unregisterWindow(this);
        } else {
            parent.getChildDocuments().remove(this);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        model.getController().removeView(this);
        unregisterDocumentWindow();
    }

    @Override
    public void toFront() {
        if (!isVisible()) {
            setVisible(true);
        }
        setState(java.awt.Frame.NORMAL);
        super.toFront();
    }

}
