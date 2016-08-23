package axoloti.piccolo;

import axoloti.ObjectSearchFrame;
import axoloti.PatchController;
import axoloti.PatchViewPiccolo;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewAbstract;
import java.awt.Dimension;
import java.awt.Point;

public class PObjectSearchFrame extends ObjectSearchFrame {

    double scale = 1.0;

    public PObjectSearchFrame(PatchController patchController) {
        super(patchController);
    }

    @Override
    public void SetPreview(AxoObjectAbstract o) {
        SetPreview(o, false);
    }

    private void SetPreview(AxoObjectAbstract o, boolean scaleChanged) {
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
            AxoObjectInstanceAbstract objectInstance = o.CreateInstance(null, "dummy", new Point(0, 0));
            PAxoObjectInstanceViewAbstract objectInstanceView = (PAxoObjectInstanceViewAbstract) objectInstance.createView(
                    (PatchViewPiccolo) patchController.getPatchView());

            getMainView().removeAll();
            PatchPCanvas container = new PatchPCanvas();
            container.setVisible(true);
            getMainView().setLayout(null);
            scale = patchController.getPatchView().getViewportView().getViewScale();
            container.setEnabled(false);
            container.getCamera().scale(scale);
            container.getLayer().addChild(objectInstanceView);
            container.setBounds(0, 0, (int) (objectInstanceView.getBounds().width * scale),
                    (int) (objectInstanceView.getBounds().height * scale));
            Dimension preferredSize = new Dimension(container.getBounds().width,
                    container.getBounds().height);
            container.setPreferredSize(preferredSize);
            getMainView().setPreferredSize(preferredSize);
            getMainView().add(container);
            objectInstanceView.repaint();
            getMainView().revalidate();
            getMainView().repaint();

            AxoObjectAbstract t = objectInstanceView.getObjectInstance().getType();
            if (t != null) {
                String description = t.sDescription == null || t.sDescription.isEmpty() ? o.sDescription : t.sDescription;
                String path = t.sPath == null ? o.sPath : t.sPath;
                String author = t.sAuthor == null ? o.sAuthor : t.sAuthor;
                String license = t.sLicense == null ? o.sLicense : t.sLicense;
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

        if (patchController.getPatchView().getViewportView().getViewScale() != scale) {
            SetPreview(previewObj, true);
        }
    }
}
