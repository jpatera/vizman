package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GridItemRemoveBtn extends Button {

    public GridItemRemoveBtn(final ComponentEventListener itemAction) {
        super();
//        this.setText("");
//        this.setIcon(new Icon("lumo", "edit"));
        this.setIcon(new Icon(VaadinIcon.CLOSE_SMALL));
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
