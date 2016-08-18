/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.inlets;

import axoloti.Theme;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.iolet.IoletAbstract;
import axoloti.object.AxoObjectInstance;
import components.JackInputComponent;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "dest")
public class InletInstance<T extends Inlet> extends IoletAbstract implements AtomInstance<T> {

    @Attribute(name = "inlet", required = false)
    public String inletname;

    private final T inlet;

    public String getInletname() {
        if (inletname != null) {
            return inletname;
        } else {
            int sepIndex = name.lastIndexOf(' ');
            return name.substring(sepIndex + 1);
        }
    }

    @Override
    public T GetDefinition() {
        return inlet;
    }

    public InletInstance() {
        this.inlet = null;
        this.axoObj = null;
        this.setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    public InletInstance(T inlet, final AxoObjectInstance axoObj) {
        this.inlet = inlet;
        this.axoObj = axoObj;
        RefreshName();
        PostConstructor();
    }

    public final void RefreshName() {
        name = axoObj.getInstanceName() + " " + inlet.name;
        objname = axoObj.getInstanceName();
        inletname = inlet.name;
        name = null;
    }

    public DataType GetDataType() {
        return inlet.getDatatype();
    }

    public String GetCName() {
        return inlet.GetCName();
    }

    public String GetLabel() {
        return inlet.name;
    }

    public final void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        setMaximumSize(new Dimension(32767, 14));
        jack = new JackInputComponent(this);
        jack.setForeground(inlet.getDatatype().GetColor());
        jack.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(jack);
        add(new SignalMetaDataIcon(inlet.GetSignalMetaData()));
        if (axoObj.getType().GetInlets().size() > 1) {
            add(Box.createHorizontalStrut(3));
            add(new LabelComponent(inlet.name));
        }
        add(Box.createHorizontalGlue());
        setToolTipText(inlet.description);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public Inlet getInlet() {
        return inlet;
    }

    @Override
    public JPopupMenu getPopup() {
        return new InletInstancePopupMenu(this);
    }
}
