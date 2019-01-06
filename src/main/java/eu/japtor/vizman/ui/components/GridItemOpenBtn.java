package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GridItemOpenBtn extends Button {

    public GridItemOpenBtn(final ComponentEventListener itemAction) {
        super();
        this.setText("");
//        this.setIcon(new Icon("lumo", "edit"));
        this.setIcon(new Icon(VaadinIcon.EDIT));
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
