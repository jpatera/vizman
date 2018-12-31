package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.GenderGrammar;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractEditorDialog <T extends Serializable>  extends Dialog {

    private final HorizontalLayout titleLayout = new HorizontalLayout();
    private final H3 titleMain = new H3();
    private final H6 titleExt = new H6();
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
    HorizontalLayout lowerPane = new HorizontalLayout();
    HorizontalLayout buttonBar = new HorizontalLayout();
    VerticalLayout dialogPane = new VerticalLayout();

    private Binder<T> binder = new Binder<>();
    private T currentItem;

    private final ConfirmationDialog<T> confirmationDialog = new ConfirmationDialog<>();

    private final GenderGrammar itemGender;
    private final String itemTypeNomS;
    private final String itemTypeGenS;
    private final String itemTypeAccS;
    private final BiConsumer<T, Operation> itemSaver;
    private final Consumer<T> itemDeleter;

    protected Operation currentOperation;

    /**
     * Constructs a new instance.
     *
     * @param itemNameNominativeS
     *            The readable name of the item type in NEW dialog
     * @param itemNameAccusativeS
     *            The readable name of the item type in EDIT dialog
     * @param itemSaver
     *            Callback to save the edited item
     * @param itemDeleter
     *            Callback to delete the edited item
     */
    protected AbstractEditorDialog(
            GenderGrammar itemGender , final String itemNameNominativeS
            , final String itemNameGenitiveS, final String itemNameAccusativeS
            , BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {

        this.itemGender = itemGender;
        this.itemTypeNomS = itemNameNominativeS;
        this.itemTypeGenS = itemNameGenitiveS;
        this.itemTypeAccS = itemNameAccusativeS;
        this.itemSaver = itemSaver;
        this.itemDeleter = itemDeleter;

        initDialogTitle();
        initFormLayout();
        initUpperGridContainer();
        initLowerGridContainer();
        initDialogButtonBar();

        upperPane.add(formLayout, upperGridContainer);
        lowerPane.add(lowerGridContainer);

        dialogPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogPane.add(titleLayout, upperPane, new Paragraph(), lowerPane, buttonBar);
        this.add(dialogPane);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);
    }

//    private String getNounForTitle(final boolean isItemMale) {
//        return isItemMale ?
//                currentOperation.getTitleOpName(GenderGrammar.MASCULINE)
//                : currentOperation.getTitleNounForFeminine();
//    }


    private void initDialogTitle() {
        titleMain.getStyle().set("margin-top", "0.2em");
        titleLayout.setSpacing(false);
        titleLayout.setPadding(false);
//        titleLayout.getStyle()
////                    .set("background-color", color)
////                    .set("theme", "icon small")
//            .set("margin", "0");
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        titleLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        titleLayout.add(titleMain, titleExt);
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

        saveButton = new Button("Uložit");
        saveButton.setAutofocus(true);
        saveButton.getElement().setAttribute("theme", "primary");

        cancelButton = new Button("Zpět");
        cancelButton.addClickListener(e -> close());

//        deleteButton = new Button("Zrušit " + getNounForTitle(isItemMale) );
        deleteButton = new Button("Zrušit");
        deleteButton.addClickListener(e -> deleteClicked());
        deleteButton.getElement().setAttribute("theme", "error");

        HorizontalLayout leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        leftBarPart.add(saveButton, deleteButton);
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

    protected final VerticalLayout getUpperGridLayout() {
        return upperGridContainer;
    }

    protected final VerticalLayout getLowerGridLayout() {
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

    /**
     * Opens the given item for editing in the dialog.
     *
     * @param item
     *            The item to edit; it may be an existing or a newly created
     *            instance
     * @param operation
     *            The operation being performed on the item
     */
    public void open(T item, final Operation operation, String titleExtText) {
        currentOperation = operation;
        currentItem = item;
//        titleLayout.setText(buildDialogTitle(currentOperation));
        titleMain.setText(currentOperation.getDialogTitle(getItemName(currentOperation), itemGender));
        titleExt.setText(titleExtText);

        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveButton
                .addClickListener(e -> saveClicked(currentOperation));

        binder.readBean(currentItem);

        deleteButton.setText("Zrušit " + itemTypeAccS.toLowerCase());
        deleteButton.setEnabled(currentOperation.isDeleteEnabled());

        openSpecific();
        open();
    }

    private String getItemName(final Operation operation) {
        switch (operation) {
            case ADD : return itemTypeNomS;
            case EDIT : return itemTypeGenS;
            case DELETE : return itemTypeAccS;
            default : return itemTypeNomS;
        }
    }

//    private String buildDialogTitle(final Operation operation) {
//        switch (operation) {
//            case ADD :
//        }
//
//        return (currentOperation.getTitleOpName(itemGender))
//                + " " + (operation == Operation.ADD ? itemTypeNomS.toLowerCase() : itemTypeAccS.toLowerCase());
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
        ADD("Nový", "Nová", "Nové", "zadat", false),
        EDIT("Editace", "Editace", "Editace", "editovat", true),
        DELETE("Zrušení", "Zrušení", "Zrušení", "zrušit", true);

        private final String titleOpNameForMasculine;
        private final String titleOpNameForFeminine;
        private final String titleOpNameForNeuter;
        private final String opNameInText;
        private final boolean deleteEnabled;

        Operation(String titleOpNameForMasculine, String titleOpNameForFeminine, String titleOpNameForNeuter,
                  String opNameInText, boolean deleteEnabled) {
            this.titleOpNameForMasculine = titleOpNameForMasculine;
            this.titleOpNameForFeminine = titleOpNameForFeminine;
            this.titleOpNameForNeuter = titleOpNameForNeuter;
            this.opNameInText = opNameInText;
            this.deleteEnabled = deleteEnabled;
        }

        public String getDialogTitle(final String itemName, final GenderGrammar itemGender) {
            return getTitleOpName(itemGender) + " " + itemName.toLowerCase();
        }

        private String getTitleOpName(final GenderGrammar gender) {
            switch (gender) {
                case MASCULINE : return titleOpNameForMasculine;
                case FEMININE : return titleOpNameForFeminine;
                default : return titleOpNameForNeuter;
            }
        }

        private String getOpNameInText() {
            return opNameInText;
        }


//        public String getTitleNounForMasculine() {
//            return titleOpNameForMasculine;
//        }
//
//        public String getTitleNounForFeminine() {
//            return titleOpNameForFeminine;
//        }


        public boolean isDeleteEnabled() {
            return deleteEnabled;
        }
    }
}
