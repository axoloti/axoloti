package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceController;

public interface IAttributeInstanceView extends IView<AttributeInstanceController> {

    public void Lock();

    public void UnLock();

    public String getName();

    public AttributeInstance getModel();

}
