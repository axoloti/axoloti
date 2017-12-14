package axoloti.attributeviews;

import axoloti.attribute.AttributeInstance;
import axoloti.mvc.IView;

public interface IAttributeInstanceView extends IView {

    public void Lock();

    public void UnLock();

    public String getName();

    public AttributeInstance getModel();

}
