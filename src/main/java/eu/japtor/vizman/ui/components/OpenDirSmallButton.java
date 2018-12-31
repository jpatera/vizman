package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class OpenDirSmallButton extends Button {

    public OpenDirSmallButton(final ComponentEventListener itemAction) {
        super();
        this.setText("");
        this.setIcon(new Icon(VaadinIcon.BOMB));
//        this.addClassName("review__edit");
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
