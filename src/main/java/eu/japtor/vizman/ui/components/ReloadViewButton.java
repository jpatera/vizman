package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ReloadViewButton extends Button {

    public ReloadViewButton(final String text, final ComponentEventListener reloadView) {
        super();
        this.getElement().setAttribute("theme", "small icon secondary");
        this.getElement().setProperty("title", text);
        this.setText("");
        this.setIcon(new Icon(VaadinIcon.REFRESH));

////        icoPlus.setSize("--lumo-icon-size-s");
////        icoPlus.getStyle().set("theme", "small");
//        icoPlus.getElement().setAttribute("theme", "small");
//        icoPlus.getStyle()
//                .set("padding-right", "0.5em");
//        this.setIcon(icoPlus);

        this.addClickListener(reloadView);

    }
}
