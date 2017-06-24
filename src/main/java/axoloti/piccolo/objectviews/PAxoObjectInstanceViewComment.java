package axoloti.piccolo.objectviews;

import axoloti.PatchViewPiccolo;
import axoloti.object.AxoObjectInstanceComment;
import components.piccolo.PLabelComponent;
import static java.awt.Component.CENTER_ALIGNMENT;
import javax.swing.BoxLayout;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAxoObjectInstanceViewComment extends PAxoObjectInstanceViewAbstract {

    AxoObjectInstanceComment model;

    public PAxoObjectInstanceViewComment(AxoObjectInstanceComment model, PatchViewPiccolo p) {
        super(model, p);
        this.model = model;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        setDrawBorder(true);

        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));

        instanceLabel = new PLabelComponent(model.getCommentText());
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
        translate(model.getX(), model.getY());
    }

    @Override
    public void showInstanceName(String s) {
        if (!model.getCommentText().equals(s)) {
            model.setCommentText(s);
        }
    }
}
