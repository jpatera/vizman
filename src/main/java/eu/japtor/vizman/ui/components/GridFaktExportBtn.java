package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GridFaktExportBtn extends Button {

    public GridFaktExportBtn(final ComponentEventListener itemAction) {
        super();
        this.setText("");
//        this.setIcon(new Icon("lumo", "edit"));
        this.setIcon(new Icon(VaadinIcon.ARROW_FORWARD));
        this.getElement().setAttribute("theme", "tertiary");
        this.addClickListener(itemAction);
    }
}
