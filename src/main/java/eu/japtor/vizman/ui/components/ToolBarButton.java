package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

public class ToolBarButton extends Button {

    public ToolBarButton(final String btnText, final ComponentEventListener openNewItemDialog) {
        super();
        this.getElement().setAttribute("theme", "small secondary");
        this.setText(btnText);
        this.addClickListener(openNewItemDialog);
    }
}
