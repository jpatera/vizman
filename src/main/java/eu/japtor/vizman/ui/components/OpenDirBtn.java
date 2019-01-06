package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class OpenDirBtn extends Button {

    public OpenDirBtn(final ComponentEventListener itemAction) {
        super();
        this.setText("");
        this.setIcon(new Icon(VaadinIcon.FOLDER_OPEN));
//        this.addClassName("review__edit");
        this.getStyle().set("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
