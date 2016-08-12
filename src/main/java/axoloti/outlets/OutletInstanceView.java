package axoloti.outlets;

import axoloti.MainFrame;
import axoloti.NetView;
import axoloti.Theme;
import axoloti.iolet.IoletAbstract;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class OutletInstanceView extends IoletAbstract {

    OutletInstancePopupMenu popup = new OutletInstancePopupMenu(this);

    OutletInstance outletInstance;

    public OutletInstanceView(OutletInstance outletInstance, AxoObjectInstanceViewAbstract axoObj) {
        this.outletInstance = outletInstance;
        this.axoObj = axoObj;
        this.setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    public final void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(Box.createHorizontalGlue());
        if (axoObj.getObjectInstance().getType().GetOutlets().size() > 1) {
            add(new LabelComponent(outletInstance.getOutlet().getName()));
            add(Box.createHorizontalStrut(2));
        }
        add(new SignalMetaDataIcon(outletInstance.getOutlet().GetSignalMetaData()));
        jack = new components.JackOutputComponent(this);
        jack.setForeground(outletInstance.getOutlet().getDatatype().GetColor());
        add(jack);
        setToolTipText(outletInstance.getOutlet().getDescription());

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return new OutletInstancePopupMenu(this);
    }

    public OutletInstance getOutletInstance() {
        return this.outletInstance;
    }
    
    @Override
    public void setHighlighted(boolean highlighted) {
        if ((getRootPane() == null
                || getRootPane().getCursor() != MainFrame.transparentCursor)
                && axoObj != null
                && axoObj.getPatchView() != null) {
            NetView netView = axoObj.getPatchView().GetNetView(this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }
    
    public void disconnect() {
        getPatchView().getPatchController().disconnect(this);
    }

    public void deleteNet() {
        getPatchView().getPatchController().deleteNet(this);
    }
}