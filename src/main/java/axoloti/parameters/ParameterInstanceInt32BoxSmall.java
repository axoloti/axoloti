/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
package axoloti.parameters;

import axoloti.MainFrame;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameterviews.IParameterInstanceView;
import axoloti.parameterviews.ParameterInstanceViewInt32BoxSmall;
import axoloti.piccolo.parameterviews.PParameterInstanceViewInt32BoxSmall;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceInt32BoxSmall extends ParameterInstanceInt32Box {

    public ParameterInstanceInt32BoxSmall() {
    }

    public ParameterInstanceInt32BoxSmall(@Attribute(name = "value") int v) {
        super(v);
    }

    @Override
    public IParameterInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PParameterInstanceViewInt32BoxSmall(this, o);
        } else {
            return new ParameterInstanceViewInt32BoxSmall(this, o);
        }
    }
}
