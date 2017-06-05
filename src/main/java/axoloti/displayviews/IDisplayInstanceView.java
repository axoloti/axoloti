package axoloti.displayviews;

import axoloti.mvc.AbstractView;

public interface IDisplayInstanceView extends AbstractView {
    public void PostConstructor();
    public void updateV();
}