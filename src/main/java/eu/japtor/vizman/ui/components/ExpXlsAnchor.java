package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Anchor;
import org.vaadin.reports.PrintPreviewReport;

public class ExpXlsAnchor extends Anchor {

    private PrintPreviewReport.Format expFormat;

    public ExpXlsAnchor() {
        this.setId("exp-xls-anchor-id");
        this.setText("Invisible link for export to XLS");
        this.getStyle().set("display", "none");
        this.getElement().setAttribute("download", true);
    }
}
