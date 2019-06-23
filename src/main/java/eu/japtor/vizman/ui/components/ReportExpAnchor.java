package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Anchor;
import org.vaadin.reports.PrintPreviewReport;

public class ReportExpAnchor extends Anchor {

    private PrintPreviewReport.Format expFormat;

    public ReportExpAnchor(PrintPreviewReport.Format expFormat) {
        this.expFormat = expFormat;
        this.setId(expFormat.name());
        this.setText(expFormat.name());
        this.setTitle("Export reportu do " + expFormat.name());
        this.getElement().setAttribute("download", true);
    }

    public PrintPreviewReport.Format getExpFormat() {
        return expFormat;
    }
}
