package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class OpenDirBtn extends Button {

    public OpenDirBtn(final Icon btnIcon, final ComponentEventListener itemAction) {
        super();
        this.setHeight("25px");
        this.setText("");
        this.setIcon(btnIcon);
//        this.addClassName("review__edit");
        this.getStyle()
//                .set("theme", "tertiary");
                .set("theme", "icon small primary")
                .set("margin", "0");
        this.addClickListener(itemAction);
    }
}
