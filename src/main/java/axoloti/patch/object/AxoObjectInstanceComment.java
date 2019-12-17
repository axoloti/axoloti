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
package axoloti.patch.object;

import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import axoloti.property.Property;
import axoloti.property.StringPropertyNull;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "comment")
public class AxoObjectInstanceComment extends AxoObjectInstance0 {

    private static int nextInstanceNumber = 0;

    @Attribute(name = "text", required = false)
    private String commentText;

    public final static Property COMMENT = new StringPropertyNull("CommentText", AxoObjectInstanceComment.class);

    AxoObjectInstanceComment() {
        if (InstanceName != null) {
            commentText = InstanceName;
            InstanceName = getGeneratedInstanceName();
        }
    }

    AxoObjectInstanceComment(IAxoObject obj, PatchModel patch1, String InstanceName1, Point location) {
        super(obj, patch1, InstanceName1, location);
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

    public boolean isLocked() {
        return false;
    }

    @Override
    public String getCInstanceName() {
        return "";
    }

    @Override
    public boolean setInstanceName(String InstanceName) {
        return false;
    }

    public String getCommentText() {
        if (commentText == null) {
            return "";
        }
        return commentText;
    }

    public void setCommentText(String commentText) {
        String oldvalue = this.commentText;
        this.commentText = commentText;
        firePropertyChange(
                COMMENT,
                oldvalue, commentText);
    }

    @Override
    public String getInstanceName() {
        if (InstanceName == null) {
            InstanceName = getGeneratedInstanceName();
        }
        return InstanceName;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        // a comment object is not supposed to mutate
    }

    @Override
    public void applyValues(IAxoObjectInstance unlinked_object_instance) {
        if (unlinked_object_instance instanceof AxoObjectInstanceComment) {
            setCommentText(((AxoObjectInstanceComment) unlinked_object_instance).getCommentText());
        }
    }

    @Override
    public void dispose() {
    }

}
