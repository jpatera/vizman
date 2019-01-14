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
import eu.japtor.vizman.backend.utils.FormatUtils;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractEditorDialog <T extends Serializable>  extends Dialog {

    private final HorizontalLayout titleLayout = new HorizontalLayout();
    private final H3 titleMain = new H3();
    private Div titleMiddle = new Div();
    private final H6 titleEnd = new H6();
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;
//    private final Button saveButton = new Button("Uložit");
//    private final Button cancelButton = new Button("Zpět");
//    private final Button deleteButton = new Button("Zrušit " + getNounForTitle(isItemMale) );
    private Registration registrationForSave;

    private final FormLayout formLayout = new FormLayout();
    private final VerticalLayout upperGridContainer = new VerticalLayout();
    private final VerticalLayout lowerGridContainer = new VerticalLayout();
    HorizontalLayout upperPane = new HorizontalLayout();
    HorizontalLayout buttonBar = new HorizontalLayout();
    VerticalLayout dialogPane = new VerticalLayout();

    private Binder<T> binder = new Binder<>();
    private T currentItem;

    private final ConfirmationDialog<T> confirmationDialog = new ConfirmationDialog<>();

    private GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;
    private BiConsumer<T, Operation> itemSaver;
    private Consumer<T> itemDeleter;

    protected Operation currentOperation;

//    Map<GrammarShapes, String> itemNameMap;


    protected AbstractEditorDialog(BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {
        this(false, false, itemSaver, itemDeleter);
    }

    /**
     * Constructs a new instance.
     *
//     * @param itemGender
//     *            Gender of the item name
//     * @param itemNameMap
//     *            Map of readable names/shapes for titles, buttons, etc
     * @param itemSaver
     *            Callback to save the edited item
     * @param itemDeleter
     *            Callback to delete the edited item
     */
    protected AbstractEditorDialog(boolean useUpperGrid, boolean useLowerGrid,
                    BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {
//    protected AbstractEditorDialog(
//            final GrammarGender itemGender, final Map<GrammarShapes, String> itemNameMap
//            , BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {
//    protected AbstractEditorDialog(
//            GrammarGender itemGender , final String itemNameNominativeS
//            , final String itemNameGenitiveS, final String itemNameAccusativeS
//            , BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {

//        this.itemNameMap = itemNameMap;
//        this.itemGender = itemGender;

        this.itemSaver = itemSaver;
        this.itemDeleter = itemDeleter;

//        initDialogTitle();
        initFormLayout();
        initUpperGridContainer();
        initLowerGridContainer();
        initDialogButtonBar();

        dialogPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogPane.add(initDialogTitle(), new Hr());
        upperPane.add(formLayout);
        if (useUpperGrid) {
            upperPane.add(new Ribbon());
            upperPane.add(upperGridContainer);
        }
        dialogPane.add(upperPane);
        dialogPane.add(new Paragraph());
        if (useLowerGrid) {
//            HorizontalLayout lowerPane = new HorizontalLayout();
//            lowerPane.add(lowerGridContainer);
//            dialogPane.add(lowerPane);
            dialogPane.add(lowerGridContainer);
        }
        dialogPane.add(buttonBar);
        this.add(dialogPane);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);
    }

//    private String getNounForTitle(final boolean isItemMale) {
//        return isItemMale ?
//                currentOperation.getTitleOperName(GrammarGender.MASCULINE)
//                : currentOperation.getTitleNounForFeminine();
//    }


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

    private void initUpperGridContainer() {
        upperGridContainer.setClassName("view-container");
        upperGridContainer.setSpacing(false);
        upperGridContainer.setPadding(false);
//        upperGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
    }

    private void initLowerGridContainer() {
        lowerGridContainer.setClassName("view-container");
        lowerGridContainer.setSpacing(false);
        lowerGridContainer.setPadding(false);

//        gridContainer.getStyle().set("padding-right", "0em");
//        gridContainer.getStyle().set("padding-left", "0em");
//        gridContainer.getStyle().set("padding-top", "2.5em");
//        gridContainer.getStyle().set("padding-bottom", "2.5em");

//        lowerGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
    }

    private void initDialogButtonBar() {

        cancelButton = new Button("Zpět");
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Uložit");
        saveButton.setAutofocus(true);
        saveButton.getElement().setAttribute("theme", "primary");

//        deleteButton = new Button("Zrušit " + getNounForTitle(isItemMale) );
        deleteButton = new Button("Zrušit");
        deleteButton.getElement().setAttribute("theme", "error");
        deleteButton.addClickListener(e -> deleteClicked());
//        deleteButton.addClickListener(e -> deleteClicked());

        HorizontalLayout leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(saveButton, deleteButton);

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

    protected final VerticalLayout getUpperGridCont() {
        return upperGridContainer;
    }

    protected final VerticalLayout getLowerGridCont() {
        return lowerGridContainer;
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


    public void open(T item, final Operation operation) {
        openInternal(item, operation, null, null, null);
    }

    public void open(T item, final Operation operation, String titleItemNameText) {
        openInternal(item, operation, titleItemNameText, null, null);
    }

    public void open(T item, final Operation operation, String titleItemNameText, String titleEndText) {
        openInternal(item, operation, titleItemNameText, null, titleEndText);
    }

    public void open(T item, final Operation operation, String titleItemNameText, Component titleMiddleComponent, String titleEndText) {
        openInternal(item, operation, titleItemNameText, titleMiddleComponent, titleEndText);
    }

    /**
     * Opens the given item for editing in the dialog.
     */
    private void openInternal(T item, final Operation operation
            , String titleItemNameText, Component titleMiddleComponent, String titleEndText)
    {

        setDefaultItemNames();  // Set general default names
        currentOperation = operation;
        currentItem = item;

        openSpecific();

        if ((null == titleItemNameText) && (currentItem instanceof HasItemType)) {
            setItemNames(((HasItemType) currentItem).getTyp());  // Set general default names
//            titleItemNameText = ItemNames.getNomS(((HasItemType) currentItem).getTyp());
        }

        if ((null == titleEndText) && (currentItem instanceof HasModifDates)) {
            if (operation == Operation.ADD) {
                titleEndText = "";
            } else {
                titleEndText = "[ Vytvořeno: " + ((HasModifDates) currentItem).getDateCreate().format(FormatUtils.basicDateFormatter)
                        + ", Poslední změna: " + ((HasModifDates) currentItem).getDatetimeUpdate().format(FormatUtils.titleModifDateFormatter) + " ]";
            }
        }

//        titleLayout.setText(buildDialogTitle(currentOperation));
        titleMain.setText(currentOperation.getDialogTitle(getItemName(currentOperation), itemGender));
        titleMiddle.removeAll();
        if (null != titleMiddleComponent) {
            titleMiddle.add(titleMiddleComponent);
        }
        titleEnd.setText(titleEndText);


        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveButton
                .addClickListener(e -> saveClicked(currentOperation));

        saveButton.setText("Uložit " + itemTypeAccuS.toLowerCase());
//        saveButton.setEnabled();

        deleteButton.setText("Zrušit " + itemTypeAccuS.toLowerCase());
        deleteButton.setEnabled(currentOperation.isDeleteEnabled());

        if (currentOperation != Operation.ADD) {
            binder.readBean(currentItem);
        } else {
            binder.removeBean();
            binder.readBean(currentItem);
        }

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

    private String getItemName(final Operation operation) {
        switch (operation) {
            case ADD : return itemTypeNomS;
            case EDIT : return itemTypeGenS;
            case DELETE : return itemTypeAccuS;
            default : return itemTypeNomS;
        }
    }

//    private String buildDialogTitle(final Operation operation) {
//        switch (operation) {
//            case ADD :
//        }
//
//        return (currentOperation.getTitleOperName(itemGender))
//                + " " + (operation == Operation.ADD ? itemTypeNomS.toLowerCase() : itemTypeAccuS.toLowerCase());
//    }

    protected abstract void openSpecific();

    private void saveClicked(Operation operation) {

        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (isValid) {
            itemSaver.accept(currentItem, operation);
            close();
        } else {
            BinderValidationStatus<T> status = binder.validate();
        }
    }

    private void deleteClicked() {
        if (confirmationDialog.getElement().getParent() == null) {
            getUI().ifPresent(ui -> ui.add(confirmationDialog));
        }
        confirmDelete();
    }

    protected abstract void confirmDelete();

    /**
     * Opens the confirmation dialog before deleting the current item.
     *
     * The dialog will display the given title and message(s), then call
     * {@link #deleteItemConfirmed(Serializable)} if the Delete button is clicked.
     *
     * @param title
     *            The title text
     * @param message
     *            Detail message (optional, may be empty)
     * @param additionalMessage
     *            Additional message (optional, may be empty)
     */
    protected final void openConfirmDeleteDialog(String title, String message,
                                                 String additionalMessage) {
        close();
        confirmationDialog.open(title, message, additionalMessage, "Zrušit",
                true, getCurrentItem(), this::deleteItemConfirmed, this::open);
    }

    /**
     * Removes the {@code item} from the backend and close the dialog.
     *
     * @param item
     *            the item to delete
     */
    protected void doDelete(T item) {
        itemDeleter.accept(item);
        close();
    }

    private void deleteItemConfirmed(T item) {
        doDelete(item);
    }


    // ================================================================

    /**
     * The operations supported by this dialog. Delete is enabled when editing
     * an already existing item.
     */
    public enum Operation {
        ADD("Nový", "Nová", "Nové", "Přidat", "zadat", false),
        EDIT("Editace", "Editace", "Editace", "Změnit", "editovat", true),
        DELETE("Zrušení", "Zrušení", "Zrušení", "Odebrat", "zrušit", true);

        private final String titleOperNameForMasculine;
        private final String titleOperNameForFeminine;
        private final String titleOperNameForNeuter;
        private final String titleOperNameForUnknown;
        private final String opNameInText;
        private final boolean deleteEnabled;

        Operation(String titleOperNameForMasculine, String titleOperNameForFeminine,
                  String titleOperNameForNeuter, String titleOperNameForUnknown,
                  String opNameInText, boolean deleteEnabled) {
            this.titleOperNameForMasculine = titleOperNameForMasculine;
            this.titleOperNameForFeminine = titleOperNameForFeminine;
            this.titleOperNameForNeuter = titleOperNameForNeuter;
            this.titleOperNameForUnknown = titleOperNameForUnknown;
            this.opNameInText = opNameInText;
            this.deleteEnabled = deleteEnabled;
        }

        public String getDialogTitle(final String itemName, final GrammarGender itemGender) {
            return getTitleOperName(itemGender) + " " + itemName.toUpperCase();
        }

        public String getTitleOperName(final GrammarGender gender) {
            switch (gender) {
                case MASCULINE : return titleOperNameForMasculine;
                case FEMININE : return titleOperNameForFeminine;
                case NEUTER : return titleOperNameForNeuter;
                default : return titleOperNameForUnknown;
            }
        }

        public boolean isDeleteEnabled() {
            return deleteEnabled;
        }
    }
}
