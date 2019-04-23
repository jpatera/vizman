package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;

import java.io.Serializable;
import java.util.function.BiConsumer;

public abstract class AbstractSimpleEditorDialog<T extends Serializable> extends Dialog {

    private final HorizontalLayout titleLayout = new HorizontalLayout();
    private final H3 titleMain = new H3();
    private Div titleMiddle = new Div();
    private final H6 titleEnd = new H6();
    private Button saveButton;
    private Button cancelButton;
//    private final Button saveButton = new Button("Uložit");
//    private final Button cancelButton = new Button("Zpět");
//    private final Button deleteButton = new Button("Zrušit " + getNounForTitle(isItemMale) );
    private Registration registrationForSave;

    private final FormLayout formLayout = new FormLayout();
    HorizontalLayout upperPane = new HorizontalLayout();
//    HorizontalLayout lowerPane = new HorizontalLayout();
    HorizontalLayout buttonBar = new HorizontalLayout();
    VerticalLayout dialogPane = new VerticalLayout();

    private Binder<T> binder = new Binder<>();
    private T currentItem;

    private Operation operation;

    private final ConfirmationDialog<T> confirmationDialog = new ConfirmationDialog<>();

    private GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;
    private BiConsumer<T, Operation> itemSaver;


//    Map<GrammarShapes, String> itemNameMap;


//    protected AbstractSimpleEditorDialog(Consumer<T> itemSaver) {
//        this(false, false, itemSaver);
//    }

    /**
     * Constructs a new instance.
     *
//     * @param itemGender
//     *            Gender of the item name
//     * @param itemNameMap
//     *            Map of readable names/shapes for titles, buttons, etc
     * @param itemSaver
     *            Callback to save the edited item
     */
    protected AbstractSimpleEditorDialog(BiConsumer<T, Operation> itemSaver) {

        this.itemSaver = itemSaver;

//        initDialogTitle();
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
//        titleLayout.getStyle()
////                    .set("background-color", color)
////                    .set("theme", "icon small")
//            .set("margin", "0");
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        titleLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        titleLayout.add(titleMain, titleMiddle, titleEnd);
        return titleLayout;
    }

    private void initFormLayout() {
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("25em", 2));
//        Div div = new Div(formLayout);
//        div.addClassName("has-padding");
        formLayout.addClassName("has-padding");
//        add(div);
    }

    public void onValueChange(boolean isDirty) {
        saveButton.setEnabled(isDirty);
    }

    private void initDialogButtonBar() {

        cancelButton = new Button("Zpět");
        cancelButton.setAutofocus(true);
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Uložit");
        saveButton.getElement().setAttribute("theme", "primary");
//        saveButton.setEnabled(false);

        HorizontalLayout leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(saveButton);

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(cancelButton);

//        buttonBar.getStyle().set("margin-top", "0.2em");
        buttonBar.setSpacing(false);
        buttonBar.setPadding(false);
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        buttonBar.add(leftBarPart, rightBarPart);
        buttonBar.setClassName("buttons");
//        buttonBar.setSpacing(true);
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


    protected abstract void openSpecific();

    public void open(T item, Operation operation) {
        openInternal(item, operation, null, null, null);
    }

    public void open(T item, Operation operation
                     , String titleItemNameText) {
        openInternal(item, operation, titleItemNameText, null, null);
    }

    public void open(T item, Operation operation
                     , String titleItemNameText, String titleEndText) {
        openInternal(item, operation, titleItemNameText, null, titleEndText);
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
        setDefaultItemNames();  // Set general default names
        this.currentItem = item;
        this.operation = operation;
        binder.removeBean();
        binder.readBean(currentItem);

        openSpecific();

        String titleMainTextInternal = "";
        if (null == titleMainText) {
//            if (currentItem instanceof HasItemType) {
//                setItemNames(((HasItemType) currentItem).getTyp());  // Set general default names
//                titleItemNameText = ItemNames.getNomS(((HasItemType) currentItem).getTyp());
//            } else {
            titleMainTextInternal =
                    operation.getTitleOperName(GrammarGender.FEMININE)
                    + " " + ItemNames.getGenS(ItemType.UNKNOWN);
        } else {
            titleMainTextInternal = titleMainText;
        }


        if ((null == titleEndText) && (currentItem instanceof HasModifDates)) {
                titleEndText = "[ Vytvořeno: " + ((HasModifDates) currentItem).getDateCreate().format(VzmFormatUtils.basicDateFormatter)
                        + ", Poslední změna: " + ((HasModifDates) currentItem).getDatetimeUpdate().format(VzmFormatUtils.titleModifDateFormatter) + " ]";
        }

//        titleLayout.setText(buildDialogTitle(currentOperation));
//        titleMain.setText(currentOperation.getDialogTitle(getItemName(currentOperation), itemGender));
        titleMain.setText(titleMainTextInternal);
        if (null != titleMiddleComponent) {
            titleMiddle.removeAll();;
            titleMiddle.add(titleMiddleComponent);
        }
        titleEnd.setText(titleEndText);


        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveButton.addClickListener(e -> saveClicked(operation));
        saveButton.setText("Uložit " + itemTypeAccuS.toLowerCase());
//        saveButton.setEnabled(false);

        this.open();
    }

    public void setDefaultItemNames() {
        setItemNames(ItemType.UNKNOWN);
    }

    public void setItemNames(ItemType itemType) {
        this.itemGender = ItemNames.getItemGender(itemType);
        this.itemTypeNomS = ItemNames.getNomS(itemType);
        this.itemTypeGenS = ItemNames.getGenS(itemType);
        this.itemTypeAccuS = ItemNames.getAccuS(itemType);
    }

    private String getItemName() {
        return itemTypeGenS;
    }

//    private String buildDialogTitle(final Operation operation) {
//        switch (operation) {
//            case ADD :
//        }
//
//        return (currentOperation.getTitleOperName(itemGender))
//                + " " + (operation == Operation.ADD ? itemTypeNomS.toLowerCase() : itemTypeAccuS.toLowerCase());
//    }

    private void saveClicked(Operation operation) {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (isValid) {
            itemSaver.accept(currentItem, operation);
            close();
        } else {
            BinderValidationStatus<T> status = binder.validate();
        }
    }

    public Operation getOperation() {
        return operation;
    }


//    private void deleteClicked() {
//        if (confirmationDialog.getElement().getParent() == null) {
//            getUI().ifPresent(ui -> ui.add(confirmationDialog));
//        }
//        confirmAndDelete();
//    }

//    protected abstract void confirmAndDelete();

}
