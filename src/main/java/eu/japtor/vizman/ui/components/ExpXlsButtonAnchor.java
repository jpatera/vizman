package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;

public class ExpXlsButtonAnchor extends Anchor {

    private Image xlsImg = new Image("img/xls_down_24b.png", "");

    public ExpXlsButtonAnchor(ComponentEventListener exportAction) {

        this.setId("exp-xls-btn-anchor-id");
        this.setTitle("Export do XLS");
        this.getElement().setAttribute("download", true);

        Button anchorButton = new Button(xlsImg);
        anchorButton.getElement().setAttribute("theme", "icon secondary small");
        anchorButton.addClickListener(exportAction);
        this.add(anchorButton);
    }
}
