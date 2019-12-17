package axoloti.swingui.mvc;

import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.DocumentWindowList;
import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                registerDocumentWindow();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                unregisterDocumentWindow();
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
            parent.addChildDocument(this);
        }
    }

    private void unregisterDocumentWindow() {
        if (parent == null) {
            DocumentWindowList.unregisterWindow(this);
        } else {
            parent.removeChildDocument(this);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        model.getController().removeView(this);
        unregisterDocumentWindow();
    }

    @Override
    public void bringToFront() {
        if (!isVisible()) {
            setVisible(true);
        }
        setState(java.awt.Frame.NORMAL);
        toFront();
    }

    @Override
    public void addChildDocument(DocumentWindow dw) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void removeChildDocument(DocumentWindow dw) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
