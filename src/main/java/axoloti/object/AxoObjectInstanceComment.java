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

import axoloti.PatchModel;
import axoloti.PatchView;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.objectviews.AxoObjectInstanceViewComment;
import java.awt.Point;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "comment")
public class AxoObjectInstanceComment extends AxoObjectInstanceAbstract {

    static int nextInstanceNumber = 0;

    @Attribute(name = "text", required = false)
    private String commentText;

    public AxoObjectInstanceComment() {
        if (InstanceName != null) {
            commentText = InstanceName;
            InstanceName = getGeneratedInstanceName();
        }
    }

    public AxoObjectInstanceComment(AxoObjectAbstract type, PatchModel patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
        if (InstanceName != null) {
            commentText = InstanceName;
            InstanceName = getGeneratedInstanceName();
        }
    }

    private String getGeneratedInstanceName() {
        String instanceName = Integer.toString(nextInstanceNumber);
        nextInstanceNumber++;
        return instanceName;
    }

    public boolean IsLocked() {
        return false;
    }

    @Override
    public String getCInstanceName() {
        return "";
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public AxoObjectInstanceViewComment ViewFactory(PatchView patchView) {
        return new AxoObjectInstanceViewComment(this, patchView);
    }

    @Override
    public AxoObjectInstanceViewAbstract CreateView(PatchView patchView) {
        AxoObjectInstanceViewComment pi = ViewFactory(patchView);
        pi.PostConstructor();
        return pi;
    }

    @Override
    public String getInstanceName() {
        if (InstanceName == null) {
            InstanceName = getGeneratedInstanceName();
        }
        return InstanceName;
    }
}
