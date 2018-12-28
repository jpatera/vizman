package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;

public class EditItemGridButton extends Button {

    public EditItemGridButton(final ComponentEventListener openItemEditDialog) {
        super();
        this.setText("");
        this.setIcon(new Icon("lumo", "edit"));
//        this.addClassName("review__edit");
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(openItemEditDialog);
    }

}
