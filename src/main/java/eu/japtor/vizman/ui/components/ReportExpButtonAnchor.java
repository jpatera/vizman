package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;

public class ReportExpButtonAnchor extends Anchor {

    private ReportExporter.Format expFormat;
    private Image xlsImg = new Image("img/xls_down_24b.png", "");

    public ReportExpButtonAnchor(ReportExporter.Format expFormat, ComponentEventListener exportAction) {

        this.expFormat = expFormat;
        this.setId(expFormat.name());
        this.setTitle("Export reportu do " + expFormat.name());
        this.getElement().setAttribute("download", true);

//        xlsImg.setMaxHeight("2.0em");
//        xlsImg.setMaxWidth("2.0em");
        Button anchorButton = new Button(xlsImg);
        anchorButton.getElement().setAttribute("theme", "icon secondary small");

        anchorButton.addClickListener(exportAction);
        this.add(anchorButton);
    }

    public ReportExporter.Format getExpFormat() {
        return expFormat;
    }
}
