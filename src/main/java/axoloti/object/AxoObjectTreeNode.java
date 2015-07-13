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
package axoloti.object;

import java.util.ArrayList;
import java.util.TreeMap;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class AxoObjectTreeNode implements Comparable {

    @Attribute
    String id;
    public TreeMap<String, AxoObjectTreeNode> SubNodes = new TreeMap<String, AxoObjectTreeNode>();
    public ArrayList<AxoObjectAbstract> Objects = new ArrayList<AxoObjectAbstract>();
    public AxoObjectAbstract NodeObject;
    public String description;

    public AxoObjectTreeNode(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(Object t) {
        String tn = ((AxoObjectAbstract) t).id;
        if (id.startsWith(tn)) {
            return -1;
        }
        if (tn.startsWith(id)) {
            return 1;
        }
        return id.compareTo(tn);
    }
}
