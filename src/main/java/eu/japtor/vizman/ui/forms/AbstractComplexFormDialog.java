package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.OperationResult;
import eu.japtor.vizman.ui.components.ResizeBtn;
import eu.japtor.vizman.ui.components.Ribbon;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractComplexFormDialog<T extends Serializable & HasItemType>  extends Dialog {

    private static final String DIALOG_WIDTH = "1000px";
    private static final String DIALOG_HEIGHT = "800px";
    private final static String REVERT_STR = "Vrátit změny";
    private final static String CANCEL_STR = "Zpět";
    private final static String DELETE_STR = "Zrušit";
    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";

    private FormLayout formLayout;
    private Div dialogCanvas;
    private VerticalLayout dialogContent;

    private GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;

    private String dialogWidth;
    private String dialogHeight;
    private String dialogMinWidth;
    private String dialogMinHeight;

    private HorizontalLayout dialogHeader;
    private HorizontalLayout buttonBar;
    private HtmlComponent headerDevider;

    private H3 mainTitle;
    private FlexLayout headerLeftBox;
    private Div headerMiddleBox;
    private H5 headerRightBox;
    private Button mainResizeBtn;

    private Button saveAndCloseButton;
    private Button cancelButton;
    private Button deleteAndCloseButton;
    private Button revertButton;
    private Registration registrationForSave;
    private Registration registrationForDelete;

    private T currentItem;
//    private T origItem;
    private boolean readOnly;
    private boolean canDelete;
    private boolean deleteAllowed;

    private VerticalLayout upperRightPane;
    private VerticalLayout lowerPane;
    private HorizontalLayout upperPane;
    private VerticalLayout upperLeftPane;
    private HorizontalLayout leftBarPart;

    private Binder<T> binder = new Binder<>();
    private boolean closeAfterSave;

    private BiConsumer<T, Operation> itemSaver;
    private Consumer<T> itemDeleter;

    protected Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;

    protected AbstractComplexFormDialog(
            BiConsumer<T, Operation> itemSaver,
            Consumer<T> itemDeleter
    ){
        this(DIALOG_WIDTH, DIALOG_HEIGHT, false, false, itemSaver, itemDeleter, true);
    }

    protected AbstractComplexFormDialog(
            BiConsumer<T, Operation> itemSaver,
            Consumer<T> itemDeleter,
            boolean closeAfterSave
    ){
        this(DIALOG_WIDTH, DIALOG_HEIGHT, false, false, itemSaver, itemDeleter, closeAfterSave);
    }


    protected AbstractComplexFormDialog(
            String dialogWidth,
            String dialogHeight,

            boolean useUpperRightPane,
            boolean useLowerPane,
            BiConsumer<T, Operation> itemSaver,
            Consumer<T> itemDeleter
    ) {
        this(dialogWidth, dialogHeight, useUpperRightPane, useLowerPane, itemSaver, itemDeleter, true);
    }


    protected AbstractComplexFormDialog(
            String dialogWidth,
            String dialogHeight,

            boolean useUpperRightPane,
            boolean useLowerPane,
            BiConsumer<T, Operation> itemSaver,
            Consumer<T> itemDeleter,
            boolean closeAfterSave
    ){
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
        setWidth(this.dialogWidth);
        setHeight(this.dialogHeight);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        setDefaultItemNames();  // Set general default names

        this.itemSaver = itemSaver;
        this.itemDeleter = itemDeleter;
        this.closeAfterSave = closeAfterSave;

        // Because underlying dialog container is not accessible (only width and height can be set),
        // we need following additional flexible dialogContainer make child components grow/shrink
        // as required:

        dialogContent = new VerticalLayout();
        dialogContent.getStyle().set("flex", "auto");
        dialogContent.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogContent.add(
                initHeaderDevider()
                , initUpperPane(useUpperRightPane)
        );
        if (useLowerPane) {
            dialogContent.add(initLowerPane());
        }

        dialogCanvas = new Div();
        dialogCanvas.setSizeFull();
        dialogCanvas.getStyle().set("display", "flex");
        dialogCanvas.getStyle().set("flex-direction", "column");
        dialogCanvas.add(
                initDialogHeader()
                , dialogContent
        );

        dialogMinHeight = headerLeftBox.getHeight();
        dialogMinWidth = headerLeftBox.getHeight();

        this.add(dialogCanvas);
        setupEventListeners();
    }


    private HtmlComponent initHeaderDevider() {
        headerDevider = new Hr();
        headerDevider.setHeight("2px");
        return headerDevider;
    }

    private Component initUpperLeftPane() {
        upperLeftPane = new VerticalLayout();
        upperLeftPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        upperLeftPane.setSpacing(false);
        upperLeftPane.setPadding(false);
        upperLeftPane.add(
                initFormLayout()
                , new Paragraph("")
                , initDialogButtonBar()
        );
        return upperLeftPane;
    }

    private Component initUpperPane(boolean useUpperRightPane) {
        upperPane = new HorizontalLayout();
//        upperPane.setSizeFull();
        upperPane.add(initUpperLeftPane());
        if (useUpperRightPane) {
            upperPane.add(
                    new Ribbon()
                    , initUpperRightPane()
            );
        } else  {
            upperRightPane = null;
        }
        return upperPane;
    }

    private Component initFormLayout() {
        formLayout = new FormLayout();
        formLayout.addClassName("has-padding");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("25em", 2)
        );
        return formLayout;
    }

    public void setupEventListeners() {
        binder.addValueChangeListener(e -> onValueChange(isDirty()));
    }

    public void onValueChange(boolean isDirty) {
        saveAndCloseButton.setEnabled(isDirty);
        revertButton.setEnabled(isDirty);
    }

    public boolean isDirty() {
        return binder.hasChanges();
    }

    public HorizontalLayout getDialogLeftBarPart() {
        return leftBarPart;
    }

    private Component initDialogHeader() {
        dialogHeader = new HorizontalLayout();
        dialogHeader.getStyle().set("margin-left", "-2em");
        dialogHeader.setSpacing(false);
        dialogHeader.setPadding(false);
        dialogHeader.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        dialogHeader.add(
                initHeaderLeftBox()
                , initHeaderMiddleBox()
                , initHeaderRightBox()
        );
        return dialogHeader;
    }

    private Component initHeaderLeftBox() {
        headerLeftBox = new FlexLayout(
                initDialogResizeBtn()
                , initDialogTitle()
        );
        headerLeftBox.setAlignItems(FlexComponent.Alignment.BASELINE);
        return headerLeftBox;
    }

    protected Component getHeaderLeftBox() {
        return headerLeftBox;
    }


    private Component initHeaderMiddleBox() {
        headerMiddleBox = new Div();
        return headerMiddleBox;
    }

    protected Div getHeaderMiddleBox() {
        return headerMiddleBox;
    }


    private Component initHeaderRightBox() {
        headerRightBox = new H5();
        headerRightBox.getStyle()
            .set("margin-right","1.2em")
        ;
        return headerRightBox;
    }

    protected HtmlContainer getHeaderRightBox() {
        return headerRightBox;
    }

    private Button initDialogResizeBtn() {
        mainResizeBtn = new ResizeBtn(getDialogResizeAction(), false);
        return mainResizeBtn;
    }

    public Consumer<Boolean> getDialogResizeAction() {
        return isExpanded -> {
            dialogContent.setVisible(!isExpanded);
            headerRightBox.setVisible(!isExpanded);
            headerMiddleBox.setVisible(!isExpanded);
            this.setHeight(isExpanded ? dialogMinHeight : dialogHeight);
            this.setWidth(isExpanded ? dialogMinWidth : dialogWidth);
        };
    }


    private Component initDialogTitle() {
        mainTitle = new H3();
        mainTitle.getStyle()
                .set("marginTop", "0.2em")
                .set("margin-right", "1em");
        return mainTitle;
    }


    private Component initUpperRightPane() {
        upperRightPane = new VerticalLayout();
        upperRightPane.setClassName("view-container");
        upperRightPane.setSpacing(false);
        upperRightPane.setPadding(false);
        upperRightPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        return upperRightPane;
    }

    private Component initLowerPane() {
        lowerPane = new VerticalLayout();
        lowerPane.setClassName("view-container");
        lowerPane.setSpacing(false);
        lowerPane.setPadding(false);
        lowerPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        return lowerPane;
    }


    public Consumer<Boolean> getLowerPaneResizeAction() {
        return isExpanded -> {
            upperPane.setVisible(isExpanded);
        };
    }

    private Component initDialogButtonBar() {
        buttonBar = new HorizontalLayout();

        cancelButton = new Button(CANCEL_STR);
        cancelButton.addClickListener(e -> close());

        revertButton = new Button(REVERT_STR);
        revertButton.addClickListener(e -> revertClicked());

        saveAndCloseButton = new Button(SAVE_AND_CLOSE_STR);
        saveAndCloseButton.setAutofocus(true);
        saveAndCloseButton.getElement().setAttribute("theme", "primary");
        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveAndCloseButton.addClickListener(e -> saveClicked(currentOperation));

        deleteAndCloseButton = new Button(DELETE_STR + " " + itemTypeAccuS.toLowerCase());
        deleteAndCloseButton.getElement().setAttribute("theme", "error");
        if (registrationForDelete != null) {
            registrationForDelete.remove();
        }
        registrationForDelete = deleteAndCloseButton.addClickListener(e -> deleteClicked());
//        deleteAndCloseButton.setEnabled(currentOperation.isDeleteAllowed());

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(revertButton, deleteAndCloseButton);

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(saveAndCloseButton, cancelButton);

//        buttonBar.getStyle().set("margin-top", "0.2em");
        buttonBar.setSpacing(false);
        buttonBar.setPadding(false);
        buttonBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        buttonBar.add(leftBarPart, rightBarPart);
        buttonBar.setClassName("buttons");
//        buttonBar.setSpacing(true);

        return buttonBar;
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

    protected final VerticalLayout getUpperRightPane() {
        return upperRightPane;
    }

    protected final VerticalLayout getLowerPane() {
        return lowerPane;
    }


    /**
     * Opens the given item for editing in the dialog.
     */
    protected void openInternal(
            T item
            , final Operation operation
            , final boolean readOnly
            , final boolean canDelete
            , Component titleMiddleComponent
            , String titleEndText
    ) {
        this.headerDevider.getStyle().set("background-color", VzmFormatUtils.getItemTypeColorBrighter(item.getTyp()));
        this.currentOperation = operation;
        this.currentItem = item;
//        this.origItem = item;
        this.readOnly = readOnly;
        this.canDelete = canDelete;
        this.deleteAllowed = currentOperation.isDeleteAllowed();

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format

        setItemNames((currentItem).getTyp());  // Set default generic names

        initControlsForItemAndOperation(currentItem, this.currentOperation, titleMiddleComponent, titleEndText);
        initControlsOperability(this.readOnly, this.deleteAllowed, this.canDelete);
        populateItemToControls(currentItem);
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
            case FAKTUROVAT: return itemTypeAccuS;
            case EXPORT : return itemTypeAccuS;
            default : return itemTypeNomS;
        }
    }

    private String getHeaderEndComponentValue(final String titleEndText) {
        String value = "";
        if ((null == titleEndText) && (currentItem instanceof HasModifDates)) {
            if (currentOperation == Operation.ADD) {
                value = "";
            } else {
                LocalDate dateCreate = ((HasModifDates) currentItem).getDateCreate();
                String dateCreateStr = null == dateCreate ? "" : dateCreate.format(VzmFormatUtils.basicDateFormatter);
                LocalDateTime dateTimeUpdate = ((HasModifDates) currentItem).getDatetimeUpdate();
                String dateUpdateStr = null == dateTimeUpdate ? "" : dateTimeUpdate.format(VzmFormatUtils.titleUpdateDateFormatter);
                value = "Vytvořeno: " + dateCreateStr + ", Změna: " + dateUpdateStr;
            }
        }
        return value;
    }

    private void populateItemToControls(final T item) {
        deactivateListeners();

        if (currentOperation == Operation.ADD) {
            binder.removeBean();
            binder.readBean(item);
        } else {
            binder.removeBean();
            binder.readBean(item);
        }

        activateListeners();
    }

    protected abstract void refreshHeaderMiddleBox(T item);

    protected abstract void activateListeners();

    protected abstract void deactivateListeners();

    private void initControlsForItemAndOperation(
            final T item
            , final Operation operation
            , final Component  titleMiddleComponent
            , final String titleEndText
    ) {
        refreshHeaderMiddleBox(item);

//        headerMiddleBox.removeAll();
//        if (null != titleMiddleComponent) {
//            headerMiddleBox.add(titleMiddleComponent);
//        }
        headerRightBox.setText(getHeaderEndComponentValue(titleEndText));


        mainTitle.setText(getDialogTitle(operation, itemGender));
        headerDevider.getStyle().set(
                "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
    }

    public String getDialogTitle(Operation operation, final GrammarGender itemGender) {
        return operation.getTitleOperName(itemGender) + " " + getItemName(operation).toUpperCase();
    }

    public void initControlsOperability(boolean readOnly, boolean deleteAllowed, boolean canDelete) {
        saveAndCloseButton.setEnabled(false);
        revertButton.setEnabled(false);
        deleteAndCloseButton.setEnabled(!readOnly && deleteAllowed && canDelete);
        cancelButton.setEnabled(true);
    }

    public void adjustControlsOperability(
            final boolean readOnly
            , final boolean deleteAllowed
            , final boolean canDelete
            , final boolean hasChanges
            , final boolean isValid
    ) {
        saveAndCloseButton.setEnabled(!readOnly && hasChanges && isValid);
        revertButton.setEnabled(!readOnly && hasChanges);
        deleteAndCloseButton.setEnabled(!readOnly && deleteAllowed && canDelete);
    };

    private void saveClicked(Operation operation) {
        if (!isItemValidForSave()) {
            binder.validate();
            return;
        };
        try {
            itemSaver.accept(currentItem, operation);
            if (closeAfterSave) {
                close();
            } else {
//                initControlsForItemAndOperation(currentItem, operation, ...);
                populateItemToControls(currentItem);
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private boolean isItemValidForSave() {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (!isValid) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace " + getItemName(Operation.SAVE))
                    .withMessage(itemTypeAccuS + " nelze uložit, některá pole nejsou správně vyplněna.")
                    .open();
            return false;
        }
        return true;
    }

    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace" + getItemName(Operation.SAVE))
                .withMessage(itemTypeAccuS + " se nepodařilo uložit.")
                .open();
    }

    private void deleteClicked() {
// TODO: is it necessary?
//        if (confirmDialog.getElement().getParent() == null) {
//            getUI().ifPresent(ui -> ui.add(confirmDialog));
//        }
        confirmDelete();
    }

    private void revertClicked() {
        revertFormChanges();
        initControlsOperability(readOnly, deleteAllowed, canDelete);
    }

    private void revertFormChanges() {
        binder.removeBean();
        binder.readBean(currentItem);
        lastOperationResult = OperationResult.NO_CHANGE;
    }


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
        ConfirmDialog
                .createQuestion()
                .withCaption(title)
                .withMessage("Opravdu zrušit?")
                .withOkButton(() -> {
                        deleteItemConfirmed(getCurrentItem());
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                )
                .withCancelButton(ButtonOption.caption("ZPĚT"))
                .open();
    }


    /**
     * Removes the {@code item} from the backend and close the dialog.
     *
     * @param item
     *            the item to delete
     */
    protected void doDelete(T item) {
        itemDeleter.accept(item);
        this.close();
    }

    private void deleteItemConfirmed(T item) {
        doDelete(item);
    }

    public Button getSaveAndCloseButton() {
        return saveAndCloseButton;
    }

    public Button getRevertButton() {
        return revertButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getDeleteAndCloseButton() {
        return deleteAndCloseButton;
    }

//  --------------------------------------------

    /**
     * @return a current state of an item currently being edited
     */
    public final T getCurrentItem() {
        return currentItem;
    }

//    public final T getOrigItem()  {
//        return origItem;
//    }

    public final OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    public final Operation getCurrentOperation() {
        return currentOperation;
    }

    public final Binder<T> getBinder() {
        return binder;
    }

    protected abstract void confirmDelete();

}
