package axoloti.attributeviews;

import axoloti.PatchView;
import axoloti.attribute.AttributeInstance;
import axoloti.mvc.IView;

public interface IAttributeInstanceView extends IView {

    public void Lock();

    public void UnLock();

    public String getName();

    @Deprecated
    public PatchView getPatchView();

    public AttributeInstance getModel();

}
