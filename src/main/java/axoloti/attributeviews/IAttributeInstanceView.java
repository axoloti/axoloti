package axoloti.attributeviews;

import axoloti.PatchView;
import axoloti.attribute.AttributeInstance;
import axoloti.mvc.AbstractView;

public interface IAttributeInstanceView extends AbstractView {

    public void Lock();

    public void UnLock();

    public void PostConstructor();

    public String getName();

    @Deprecated
    public PatchView getPatchView();

    public AttributeInstance getAttributeInstance();

}
