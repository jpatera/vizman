package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConfirmationDialog<T extends Serializable> extends Dialog {
    private final H3 titleField = new H3();
    private final Div messageLabel = new Div();
    private final Div extraMessageLabel = new Div();
    private final Button confirmButton = new Button();
    private final Button cancelButton = new Button("Zpět");
    private Registration registrationForConfirm;
    private Registration registrationForCancel;

    /**
     * Constructor.
     */
    public ConfirmationDialog() {

        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        titleField.setClassName("confirm-title");

        Div labels = new Div(messageLabel, extraMessageLabel);
        labels.setClassName("confirm-text");

        confirmButton.addClickListener(e -> close());
        confirmButton.getElement().setAttribute("theme", "secondary");

        cancelButton.addClickListener(e -> close());
        cancelButton.getElement().setAttribute("theme", "tertiary");

        HorizontalLayout buttonBar = new HorizontalLayout(confirmButton, cancelButton);
        buttonBar.setClassName("buttons confirm-buttons");

        add(titleField, labels, new Hr(), buttonBar);
    }

    /**
     * Opens the confirmation dialog.
     *
     * The dialog will display the given title and message(s), then call
     * <code>confirmHandler</code> if the Confirm button is clicked, or
     * <code>cancelHandler</code> if the Cancel button is clicked.
     *
     * @param title
     *            The title text
     * @param message
     *            Detail message (optional, may be empty)
     * @param additionalMessage
     *            Additional message (optional, may be empty)
     * @param actionName
     *            The action name to be shown on the Confirm button
     * @param isDisruptive
     *            True if the action is disruptive, such as deleting an item
     * @param item
     *            The subject of the action
     * @param confirmHandler
     *            The confirmation handler function
     * @param cancelHandler
     *            The cancellation handler function
     */
    public void open(String title, String message, String additionalMessage,
                     String actionName, boolean isDisruptive, T item,
                     Consumer<T> confirmHandler, Runnable cancelHandler)
    {
        titleField.setText(title);
        messageLabel.setText(message);
        if (StringUtils.isEmpty(additionalMessage)) {
            extraMessageLabel.setVisible(false);
        } else {
            extraMessageLabel.setText(additionalMessage);
        }

        confirmButton.setText(actionName);
        if (isDisruptive) {
            confirmButton.getElement().setAttribute("theme", "secondary error");
        }

        if (registrationForConfirm != null) {
            registrationForConfirm.remove();
        }
        registrationForConfirm = confirmButton.addClickListener(e -> confirmHandler.accept(item));

        if (registrationForCancel != null) {
            registrationForCancel.remove();
        }
        registrationForCancel = cancelButton.addClickListener(e -> cancelHandler.run());

        this.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                cancelHandler.run();
            }
        });

        cancelButton.setAutofocus(true);
        open();
    }
}