package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceController;

public interface IAttributeInstanceView extends IView<AttributeInstanceController> {

    void Lock();

    void UnLock();

    String getName();

    AttributeInstance getModel();

}
