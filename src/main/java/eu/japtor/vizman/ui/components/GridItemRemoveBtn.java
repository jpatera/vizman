package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GridItemRemoveBtn extends Button {

    public GridItemRemoveBtn(final ComponentEventListener itemAction) {
        super();
        Icon icon = VaadinIcon.CLOSE_SMALL.create();
        icon.getStyle().set("theme", "small icon secondary");
//        icon.setSize("0.8em");
        icon.setColor("crimson");
        this.setIcon(icon);
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
