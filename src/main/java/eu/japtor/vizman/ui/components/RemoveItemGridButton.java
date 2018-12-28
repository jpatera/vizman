package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class RemoveItemGridButton extends Button {

    public RemoveItemGridButton(final ComponentEventListener removeItemEditDialog) {
        super();
        this.setText("");
//        this.setIcon(new Icon("lumo", "edit"));
        this.setIcon(new Icon(VaadinIcon.FILE_REMOVE));
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(removeItemEditDialog);
    }

}
