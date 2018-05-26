package axoloti.piccolo.components;

import axoloti.datatypes.ValueFrac32;
import axoloti.datatypes.ValueInt32;
import axoloti.patch.PatchModel;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.piccolo.components.control.PCheckboxComponent;
import axoloti.piccolo.components.control.PCtrlComponentAbstract;
import axoloti.piccolo.components.control.PCtrlEvent;
import axoloti.piccolo.components.control.PCtrlListener;
import axoloti.piccolo.patch.PatchPCanvas;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.object.parameter.PParameterInstanceView;
import axoloti.preferences.Theme;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BoxLayout;

public class PAssignPresetPanel extends PatchPCanvas {

    final PParameterInstanceView parameterInstanceView;
    ArrayList<PCtrlComponentAbstract> ctrls;

    public PAssignPresetPanel(PParameterInstanceView parameterInstanceView) {
        this.parameterInstanceView = parameterInstanceView;
        initComponent();
    }

    private void initComponent() {
        int n = parameterInstanceView.getDModel().getObjectInstance().getParent().getNPresets();

        removeInputEventListener(zoomEventHandler);
        removeInputEventListener(selectionEventHandler);

        PatchPNode container = new PatchPNode();
        container.setLayout(new BoxLayout(container.getProxyComponent(), BoxLayout.PAGE_AXIS));
        setLocation(0, 0);
        double scale = parameterInstanceView.getPatchView().getViewportView().getViewScale();
        container.setEnabled(false);
        getCamera().scaleViewAboutPoint(scale, 0, 0);

        ctrls = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            PatchPNode row = new PatchPNode();
            row.setLayout(new BoxLayout(row.getProxyComponent(), BoxLayout.LINE_AXIS));

            PCheckboxComponent cb = new PCheckboxComponent(0, 1, parameterInstanceView.getObjectInstanceView());
            cb.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
            cb.addPActionListener(cbActionListener);
            cb.setCallbackData(Integer.toString(i + 1));
            cb.setPresetCanvas(this);
            PLabelComponent label = new PLabelComponent("Preset " + (i + 1));

            row.addChild(cb);
            row.addChild(label);
            PCtrlComponentAbstract ctrl = parameterInstanceView.createControl();
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
            ctrl.setPresetCanvas(this);
            ctrls.add(ctrl);
            ctrl.addPCtrlListener(ctrlListener);
            Preset p = parameterInstanceView.getDModel().getPreset(i + 1);
            if (p != null) {
                cb.setValue(1);
                ctrl.setValue((Double)p.getValue());
            } else {
                cb.setValue(0);
                ctrl.setEnabled(false);
                //ctrl.setValue(parameterInstanceView.getModel().getValue());
            }
            row.addChild(ctrl);
            container.addChild(row);
        }

        container.getProxyComponent().doLayout();
        getLayer().addChild(container);
        setBounds(0, 0, (int) (container.getPreferredSize().width * scale),
                (int) (container.getPreferredSize().height * scale));
        Dimension preferredSize = new Dimension(getBounds().width,
                getBounds().height);
        setPreferredSize(preferredSize);
    }

    PActionListener cbActionListener = new PActionListener() {

        @Override
        public void actionPerformed(String data) {
            String[] s = data.split(" ");
            int i = Integer.parseInt(s[0]) - 1;
            boolean selected = Boolean.parseBoolean(s[1]);
            System.out.println(data);
            if (selected) {
                //parameterInstanceView.AddPreset(i + 1, parameterInstanceView.getModel().getValue());
                ctrls.get(i).setEnabled(true);
                ctrls.get(i).setValue((Double) parameterInstanceView.getDModel().getPreset(i + 1).getValue()); //
            } else {
                ctrls.get(i).setEnabled(false);
                parameterInstanceView.getDModel().getController().removePreset(i + 1);
            }
        }

    };

    double valueBeforeAdjustment;

    PCtrlListener ctrlListener = new PCtrlListener() {

        @Override
        public void PCtrlAdjusted(PCtrlEvent e) {
            int i = ctrls.indexOf(e.getSource());
            if (i >= 0) {
                if (ctrls.get(i).isEnabled()) {
                    if (parameterInstanceView.getDModel().getValue() instanceof ValueInt32) {
                        parameterInstanceView.getDModel().getController().addPreset(i + 1, new ValueInt32((int) ctrls.get(i).getValue()));
                    } else if (parameterInstanceView.getDModel().getValue() instanceof ValueFrac32) {
                        parameterInstanceView.getDModel().getController().addPreset(i + 1, new ValueFrac32(ctrls.get(i).getValue()));
                    }
                }
            }
        }

        @Override
        public void PCtrlAdjustmentBegin(PCtrlEvent e) {
            int i = ctrls.indexOf(e.getSource());
            if (i >= 0) {
                valueBeforeAdjustment = ctrls.get(i).getValue();
            }
        }

        @Override
        public void PCtrlAdjustmentFinished(PCtrlEvent e) {
            int i = ctrls.indexOf(e.getSource());
            if (i >= 0) {
                if (valueBeforeAdjustment != ctrls.get(i).getValue()) {
                    PatchModel patchModel = parameterInstanceView.getDModel().getObjectInstance().getParent();
                }
            }
        }
    };
}
