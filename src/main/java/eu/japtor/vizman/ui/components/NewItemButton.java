package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;

public class NewItemButton extends Button {

    public NewItemButton(final String btnText, final ComponentEventListener openNewItemDialog) {
        super();
//        this.addClassName("view-toolbar__button");
//        this.getStyle().set("theme", "small");
        this.getElement().setAttribute("theme", "small secondary");
//        this.getElement().setAttribute("theme", "small");
////        this.getStyle().set("theme", "Small Primary");
////        this.getStyle()
////                .set("height", "var(--lumo-size-s)")
////                .set("font-size", "var(--lumo-font-size-s)");
//
        this.setText(btnText);

        Icon icoPlus = new Icon("lumo", "plus");
////        icoPlus.setSize("--lumo-icon-size-s");
////        icoPlus.getStyle().set("theme", "small");
        icoPlus.getElement().setAttribute("theme", "small");
        icoPlus.getStyle()
                .set("padding-right", "0.5em");
        this.setIcon(icoPlus);

        this.addClickListener(openNewItemDialog);

    }
}
