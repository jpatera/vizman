package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Operation;

import java.io.Serializable;
import java.util.function.BiConsumer;


public abstract class AbstractSimpleEditorDialog<T extends Serializable> extends Dialog {

    private final HorizontalLayout titleLayout = new HorizontalLayout();
    private final H3 titleMain = new H3();
    private Div titleMiddle = new Div();
    private final H6 titleEnd = new H6();
    private Button saveButton;
    private Button cancelButton;
    private Registration registrationForSave;

    private final FormLayout formLayout = new FormLayout();
    HorizontalLayout upperPane = new HorizontalLayout();
    HorizontalLayout buttonBar = new HorizontalLayout();
    VerticalLayout dialogPane = new VerticalLayout();

    private Binder<T> binder = new Binder<>();
    private T currentItem;

    private Operation operation;
    private BiConsumer<T, Operation> itemSaver;

    /**
     * Constructs a new instance.
     *
     * @param itemSaver
     *            Callback to save the edited item
     */
    protected AbstractSimpleEditorDialog(BiConsumer<T, Operation> itemSaver) {

        this.itemSaver = itemSaver;

        initFormLayout();
        initDialogButtonBar();

        dialogPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogPane.add(initDialogTitle(), new Hr());
        upperPane.add(formLayout);
        dialogPane.add(upperPane);
        dialogPane.add(new Paragraph());
        dialogPane.add(buttonBar);
        this.add(dialogPane);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        setupEventListeners();
    }

    public void setupEventListeners() {
        binder.addValueChangeListener(e -> onValueChange(isDirty()));
    }

    public boolean isDirty() {
        return binder.hasChanges();
    }

    private Component initDialogTitle() {
        titleMain.getStyle().set("marginTop", "0.2em");
        titleLayout.setSpacing(false);
        titleLayout.setPadding(false);
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        titleLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        titleLayout.add(titleMain, titleMiddle, titleEnd);
        return titleLayout;
    }

    private void initFormLayout() {
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("25em", 2));
        formLayout.addClassName("has-padding");
    }

    public void onValueChange(boolean isDirty) {
        saveButton.setEnabled(isDirty);
    }

    private void initDialogButtonBar() {

        cancelButton = new Button("Zpět");
        cancelButton.setAutofocus(true);
        // TODO: closeDialog?
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Uložit");
        saveButton.getElement().setAttribute("theme", "primary");
        saveButton.addClickShortcut(Key.ENTER);

        HorizontalLayout leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(saveButton);

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(cancelButton);

        buttonBar.setSpacing(false);
        buttonBar.setPadding(false);
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        buttonBar.add(leftBarPart, rightBarPart);
        buttonBar.setClassName("buttons");
    }


    /**
     * Gets the form layout, where additional components can be added for
     * displaying or editing the item's properties.
     *
     * @return the form layout
     */
    protected final FormLayout getFormLayout() {
        return formLayout;
    }


    /**
     * Gets the binder.
     *
     * @return the binder
     */
    protected final Binder<T> getBinder() {
        return binder;
    }

    /**
     * Gets the item currently being edited.
     *
     * @return the item currently being edited
     */
    protected final T getCurrentItem() {
        return currentItem;
    }


    /**
     * Opens the given item for editing in the dialog.
     */
    protected void openInternal(
            T item
            , Operation operation
            , String titleMainText
            , Component titleMiddleComponent
            , String titleEndText)
    {
//        setDefaultItemNames();  // Set general default names
        this.currentItem = item;
        this.operation = operation;
        binder.removeBean();
        binder.readBean(currentItem);

        String titleMainTextInternal = "";
        if (null == titleMainText) {
            titleMainTextInternal =
                    operation.getTitleOperName(GrammarGender.FEMININE)
                    + " " + ItemNames.getGenS(ItemType.UNKNOWN);
        } else {
            titleMainTextInternal = titleMainText;
        }


        if ((null == titleEndText) && (currentItem instanceof HasModifDates)) {
                titleEndText = "Vytvořeno ii: " + ((HasModifDates) currentItem).getDateCreate().format(VzmFormatUtils.basicDateFormatter)
                        + ", Poslední změna: " + ((HasModifDates) currentItem).getDatetimeUpdate().format(VzmFormatUtils.titleUpdateDateFormatter);
        }

        titleMain.setText(titleMainTextInternal);
        if (null != titleMiddleComponent) {
            titleMiddle.removeAll();
            titleMiddle.add(titleMiddleComponent);
        }
        titleEnd.setText(titleEndText);


        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveButton.addClickListener(e -> saveClicked(operation));
//        saveButton.setText("Uložit " + itemTypeAccuS.toLowerCase());
        saveButton.setText("Uložit");

        this.open();
    }

    private void saveClicked(Operation operation) {
        revalidateFields();
        if (binder.isValid()) {
            try {
                binder.writeBean(currentItem);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            itemSaver.accept(currentItem, operation);
            close();
        }
    }

    private boolean revalidateFields() {
        // This command shows warnings in UI:
        BinderValidationStatus status = binder.validate();
        return !status.hasErrors();
    }

    public Operation getOperation() {
        return operation;
    }
}
