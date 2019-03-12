package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.awt.*;

public class GridItemEditBtn extends Button {

    public GridItemEditBtn(final ComponentEventListener openItemEditDialog) {
        this(openItemEditDialog, null);
    }

    public GridItemEditBtn(final ComponentEventListener openItemEditDialog, String icoColor) {
        super();
//        this.setText("");
        Icon ico = new Icon(VaadinIcon.EDIT);
        if (null != icoColor) {
            ico.setColor(icoColor);
        }
        this.setIcon(ico);
//        this.addClassName("review__edit");
        this.getElement().setAttribute("theme", "tertiary small");
        this.addClickListener(openItemEditDialog);
    }

}
