package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;
import java.util.function.Consumer;

public class OkDialog extends Dialog {
    private final H3 titleField = new H3();
    private final Div messageLabel = new Div();
    private final Div extraMessageLabel = new Div();
    private final Button okButton = new Button("Ok");

    /**
     * Constructor.
     */
    public OkDialog() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        okButton.addClickListener(e -> close());
        okButton.getElement().setAttribute("theme", "primary");
        okButton.setAutofocus(true);

        HorizontalLayout buttonBar = new HorizontalLayout(okButton);
        buttonBar.setClassName("buttons confirm-buttons");

        Div labels = new Div(messageLabel, extraMessageLabel);
        labels.setClassName("confirm-text");

        titleField.setClassName("confirm-title");

        add(titleField, labels, buttonBar);
    }

    /**
     * Opens the confirmation dialog.
     */
    public void open(String title, String message, String additionalMessage) {
        titleField.setText(title);
        messageLabel.setText(message);
        extraMessageLabel.setText(additionalMessage);
        okButton.setText("OK");
        open();
    }
}