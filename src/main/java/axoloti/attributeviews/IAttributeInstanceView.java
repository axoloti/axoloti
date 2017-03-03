package axoloti.attributeviews;

import axoloti.PatchView;
import axoloti.attribute.AttributeInstance;

public interface IAttributeInstanceView {

    public void Lock();

    public void UnLock();

    public void PostConstructor();

    public String getName();

    public PatchView getPatchView();

    public AttributeInstance getAttributeInstance();
}
