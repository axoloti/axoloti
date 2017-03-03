package axoloti.inlets;

import axoloti.INetView;
import axoloti.MainFrame;
import axoloti.Theme;
import axoloti.iolet.IoletAbstract;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.JackInputComponent;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class InletInstanceView extends IoletAbstract implements IInletInstanceView {

    InletInstancePopupMenu popup = new InletInstancePopupMenu(this);

    InletInstance inletInstance;

    public InletInstanceView(InletInstance inletInstance, AxoObjectInstanceViewAbstract axoObj) {
        this.inletInstance = inletInstance;
        this.axoObj = axoObj;
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        setMaximumSize(new Dimension(32767, 14));
        jack = new JackInputComponent(this);
        jack.setForeground(inletInstance.getInlet().getDatatype().GetColor());
        jack.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(jack);
        add(new SignalMetaDataIcon(inletInstance.getInlet().GetSignalMetaData()));
        if (axoObj.getObjectInstance().getType().GetInlets().size() > 1) {
            add(Box.createHorizontalStrut(3));
            add(new LabelComponent(inletInstance.getInlet().getName()));
        }
        add(Box.createHorizontalGlue());
        setToolTipText(inletInstance.getInlet().getDescription());

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return popup;
    }

    public String getInletname() {
        int sepIndex = name.lastIndexOf(' ');
        return name.substring(sepIndex + 1);
    }

    public InletInstance getInletInstance() {
        return inletInstance;
    }

    public void disconnect() {
        getPatchView().getPatchController().disconnect(this);
    }

    public void deleteNet() {
        getPatchView().getPatchController().deleteNet(this);
    }

    public void setHighlighted(boolean highlighted) {
        if ((getRootPane() == null
                || getRootPane().getCursor() != MainFrame.transparentCursor)
                && axoObj != null
                && axoObj.getPatchView() != null) {
            INetView netView = axoObj.getPatchView().GetNetView((IInletInstanceView) this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }
}
