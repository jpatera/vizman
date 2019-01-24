package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GridFakturovatBtn extends Button {
    static Icon fakturovatIcon = new Icon(VaadinIcon.COINS);
    static Icon stornoIcon = new Icon(VaadinIcon.LEVEL_LEFT);


    public GridFakturovatBtn(final ComponentEventListener itemAction, boolean isFakturovano) {
        super();
        this.setText("");
//        this.setIcon(new Icon("lumo", "edit"));
        this.setIcon(isFakturovano ? new Icon(VaadinIcon.LEVEL_LEFT) : new Icon(VaadinIcon.COINS));
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
