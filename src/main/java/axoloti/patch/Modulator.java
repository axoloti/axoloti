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
package axoloti.patch;

import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.property.ListProperty;
import axoloti.property.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Johannes Taelman
 */
public class Modulator extends AbstractModel<ModulatorController> {

    private String name;
    private IAxoObjectInstance objinst;
    private List<Modulation> modulations = new ArrayList<>();

    public static final ListProperty MODULATIONS = new ListProperty("Modulations", Modulator.class);

    public Modulator() {
    }

    public Modulator(IAxoObjectInstance objectInstance, String name) {
        this.objinst = objectInstance;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCName() {
        if ((name != null) && (!name.isEmpty())) {
            return "MODULATOR_" + objinst.getCInstanceName() + "_" + name;
        } else {
            return "MODULATOR_" + objinst.getCInstanceName();
        }
    }

    public IAxoObjectInstance getObjectInstance() {
        return objinst;
    }

    public List<Modulation> getModulations() {
        return Collections.unmodifiableList(modulations);
    }

    public void setModulations(List<Modulation> modulations) {
        this.modulations = Collections.unmodifiableList(modulations);
    }

    @Override
    protected ModulatorController createController() {
        return new ModulatorController(this);
    }

    @Override
    public IModel getParent() {
        return objinst;
    }

    @Override
    public List<Property> getProperties() {
        return Collections.singletonList(MODULATIONS);
    }

}
