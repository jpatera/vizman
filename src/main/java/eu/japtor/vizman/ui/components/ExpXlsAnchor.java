package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Anchor;

public class ExpXlsAnchor extends Anchor {

    public ExpXlsAnchor() {
        this.setId("exp-xls-anchor-id");
        this.setText("Invisible link for export to XLS");
        this.getStyle().set("display", "none");
        this.getElement().setAttribute("download", true);
    }
}
