package axoloti.abstractui;

import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.mvc.IView;

public interface IAttributeInstanceView extends IView<AttributeInstanceController> {

    public void Lock();

    public void UnLock();

    public String getName();

    public AttributeInstance getModel();

}
