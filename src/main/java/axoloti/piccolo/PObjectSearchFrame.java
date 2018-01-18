package axoloti.piccolo;

import java.awt.Dimension;
import java.awt.Point;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.IAxoObject;
import axoloti.object.ObjectController;
import axoloti.patch.PatchController;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.AxoObjectInstanceFactory;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.ObjectInstancePatcherController;
import axoloti.piccolo.patch.PatchPCanvas;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewAbstract;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewFactory;
import axoloti.swingui.ObjectSearchFrame;

public class PObjectSearchFrame extends ObjectSearchFrame {

    private double scale = 1.0;
    private PatchViewPiccolo patchView;

    public PObjectSearchFrame(PatchController patchController, PatchViewPiccolo patchView) {
        super(patchController);
        this.patchView = patchView;
        this.scale = patchView.getViewportView().getViewScale();
    }

    @Override
    public void SetPreview(IAxoObject o) {
        SetPreview(o, false);
    }

    private void SetPreview(IAxoObject o, boolean scaleChanged) {
        if (o == null) {
            previewObj = null;
            type = null;
            getMainView().removeAll();
            getMainView().repaint();
            return;
        }
        if (o != previewObj || scaleChanged) {
            previewObj = o;
            type = o;
            ExpandJTreeToEl(o);
            getListView().setSelectedValue(o, true);
            if (getListView().getSelectedValue() != o) {
            }
            ObjectController oc = o.createController(null, null);
            AxoObjectInstanceAbstract objectInstance = AxoObjectInstanceFactory.createView(oc, null, "dummy", new Point(5, 5));
            ObjectInstanceController c;

            if (objectInstance instanceof AxoObjectInstancePatcher) {
                c = new ObjectInstancePatcherController((AxoObjectInstancePatcher) objectInstance, null, null);
            } else {
                c = new ObjectInstanceController(objectInstance, null, null);
            }

            PAxoObjectInstanceViewAbstract objectInstanceView = (
                (PAxoObjectInstanceViewAbstract)
                PAxoObjectInstanceViewFactory.getInstance().createView(c, patchView));

            getMainView().removeAll();
            PatchPCanvas container = new PatchPCanvas();
            container.setVisible(true);
            getMainView().setLayout(null);

            container.setEnabled(false);
            container.getCamera().scale(scale);
            container.getLayer().addChild(objectInstanceView);

            Dimension preferredSize = new Dimension(
                (int) ((objectInstanceView.getBounds().width + 10) * scale),
                (int) ((objectInstanceView.getBounds().height + 10) * scale));
            container.setBounds(
                0, 0,
                (int) ((preferredSize.width + 10) * scale),
                (int) ((preferredSize.height + 10) * scale));
            container.setPreferredSize(preferredSize);
            getMainView().add(container);

            Dimension oldPreferredSize = getMainView().getPreferredSize();
            getMainView().setPreferredSize(preferredSize);

            objectInstanceView.resizeToGrid();
            objectInstanceView.repaint();
            getMainView().revalidate();
            getMainView().repaint();

            IAxoObject t = objectInstanceView.getModel().getType();
            if (t != null) {
                String description = t.getDescription() == null || t.getDescription().isEmpty() ? o.getDescription() : t.getDescription();
                String path = t.getPath() == null ? o.getPath() : t.getPath();
                String author = t.getAuthor() == null ? o.getAuthor() : t.getAuthor();
                String license = t.getLicense() == null ? o.getLicense() : t.getLicense();
                String txt = description;
                if ((path != null) && (!path.isEmpty())) {
                    txt += "\n<p>\nPath: " + path;
                }
                if ((author != null) && (!author.isEmpty())) {
                    txt += "\n<p>\nAuthor: " + author;
                }
                if ((license != null) && (!license.isEmpty())) {
                    txt += "\n<p>\nLicense: " + license;
                }
                getTextPane().setText(txt);
            }
            getTextPane().setCaretPosition(0);
        }
    }

    @Override
    public void Launch(Point patchLoc, IAxoObjectInstanceView o, String searchString) {
        super.Launch(patchLoc, o, searchString, false);

        if (scale != patchView.getViewportView().getViewScale()) {
            this.scale = patchView.getViewportView().getViewScale();
            SetPreview(previewObj, true);
        }
    }
}
