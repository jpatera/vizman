package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class InfoIconForColumn extends Icon {

    public InfoIconForColumn(final String color) {
        super(VaadinIcon.INFO_CIRCLE_O);
        this.setSize("10px");
        this.setColor(color);
        this.getStyle().set("float", "left");
        this.getStyle().set("margin-right", "3px");
    }
}
