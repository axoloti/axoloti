package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.attribute.AttributeInstance;

public interface IAttributeInstanceView extends IView<AttributeInstance> {

    void lock();

    void unlock();

    String getName();

}
