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
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.ResizeBtn;
import eu.japtor.vizman.ui.components.Ribbon;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractComplexFormDialog<T extends Serializable>  extends Dialog {

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

    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;
    private Registration registrationForSave;

    private T currentItem;

    private VerticalLayout upperGridContainer;
    private VerticalLayout lowerPane;
    private HorizontalLayout upperPane = new HorizontalLayout();
    private VerticalLayout upperLeftPane;
    private HorizontalLayout leftBarPart;

    private Div dialogTitlePane;
    private Binder<T> binder = new Binder<>();
    private boolean closeAfterSave;

    private BiConsumer<T, Operation> itemSaver;
    private Consumer<T> itemDeleter;

    protected Operation currentOperation;


    protected AbstractComplexFormDialog(
            BiConsumer<T, Operation> itemSaver,
            Consumer<T> itemDeleter
    ){
        this("1000px", "800px", false, false, itemSaver, itemDeleter, true);
    }

    protected AbstractComplexFormDialog(
            BiConsumer<T, Operation> itemSaver,
            Consumer<T> itemDeleter,
            boolean closeAfterSave
    ){
        this("1000px", "800px", false, false, itemSaver, itemDeleter, closeAfterSave);
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
                , initUpperContentPane(useUpperRightPane)
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

    private Component initUpperContentPane(boolean useUpperRightPane) {
        upperLeftPane = new VerticalLayout();
        upperLeftPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        upperLeftPane.setSpacing(false);
        upperLeftPane.setPadding(false);
        upperLeftPane.add(initFormLayout());
        upperLeftPane.add(new Paragraph(""));
        upperLeftPane.add(initDialogButtonBar());

        upperPane.add( upperLeftPane);
        if (useUpperRightPane) {
            upperPane.add(new Ribbon());
            upperPane.add(initUpperGridContainer());
        }
        return upperPane;
    }

    private Component initFormLayout() {
        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("25em", 2));
//        Div div = new Div(formLayout);
//        div.addClassName("has-padding");
        formLayout.addClassName("has-padding");
//        add(div);
        return formLayout;
    }

    public void setupEventListeners() {

        binder.addValueChangeListener(e -> onValueChange(isDirty()));

//        getGrid().addSelectionListener(e -> {
//            e.getF``irstSelectedItem().ifPresent(entity -> {
//                navigateToEntity(entity.getId().toString());
//                getGrid().deselectAll();
//            });
//        });
//
//        getForm().getButtons().addSaveListener(e -> getPresenter().save());
//        getForm().getButtons().addCancelListener(e -> getPresenter().cancel());
//
//        getDialog().getElement().addEventListener("opened-changed", e -> {
//            if (!getDialog().isOpened()) {
//                getPresenter().cancel();
//            }
//        });
//
//        getForm().getButtons().addDeleteListener(e -> getPresenter().delete());
//
//        getSearchBar().addActionClickListener(e -> getPresenter().createNew());
//        getSearchBar()
//                .addFilterChangeListener(e -> getPresenter().filter(getSearchBar().getFilter()));
//
//        getSearchBar().setActionText("New " + entityName);
//        getBinder().addValueChangeListener(e -> getPresenter().onValueChange(isDirty()));
    }

    public void onValueChange(boolean isDirty) {
        saveButton.setEnabled(isDirty);
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
                , initHeaderEndBox()
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

    private Component initHeaderEndBox() {
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


    private Component initUpperGridContainer() {
        upperGridContainer = new VerticalLayout();
        upperGridContainer.setClassName("view-container");
        upperGridContainer.setSpacing(false);
        upperGridContainer.setPadding(false);
        upperGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
//        upperGridContainer.setAlignSelf(FlexComponent.Alignment.STETCH);
//        upperGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
//        container.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        return upperGridContainer;
    }

    private Component initLowerPane() {
        lowerPane = new VerticalLayout();
        lowerPane.setClassName("view-container");
        lowerPane.setSpacing(false);
        lowerPane.setPadding(false);
        lowerPane.setAlignItems(FlexComponent.Alignment.STRETCH);

//        gridContainer.getStyle().set("padding-right", "0em");
//        gridContainer.getStyle().set("padding-left", "0em");
//        gridContainer.getStyle().set("padding-top", "2.5em");
//        gridContainer.getStyle().set("padding-bottom", "2.5em");
//        lowerPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        return lowerPane;
    }


    public Consumer<Boolean> getLowerPaneResizeAction() {
        return isExpanded -> {
            upperPane.setVisible(isExpanded);
        };
    }

    private Component initDialogButtonBar() {
        buttonBar = new HorizontalLayout();

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

        leftBarPart = new HorizontalLayout();
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

    protected final VerticalLayout getUpperGridContainer() {
        return upperGridContainer;
    }

    protected final VerticalLayout getLowerPane() {
        return lowerPane;
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

    public void open(T item, final Operation operation, String titleItemNameText) {
        openInternal(item, operation, titleItemNameText, null, null);
    }

    /**
     * Opens the given item for editing in the dialog.
     */
    protected void openInternal(T item, final Operation operation
            , String titleItemNameText, Component titleMiddleComponent, String titleEndText)
    {
        if (item instanceof HasItemType)
        headerDevider.getStyle().set("background-color", VzmFormatUtils.getItemTypeColorBrighter(((HasItemType)item).getTyp()));

        currentOperation = operation;
        currentItem = item;

        openSpecific();

        if ((null == titleItemNameText) && (currentItem instanceof HasItemType)) {
            setItemNames(((HasItemType) currentItem).getTyp());  // Set general default names
        }

        mainTitle.setText(currentOperation.getDialogTitle(getItemName(currentOperation), itemGender));

        headerMiddleBox.removeAll();
        if (null != titleMiddleComponent) {
            headerMiddleBox.add(titleMiddleComponent);
        }

        headerRightBox.setText(getHeaderEndComponentValue(titleEndText));

        if (currentOperation == Operation.ADD) {
            binder.removeBean();
            binder.readBean(currentItem);
        } else {
            binder.removeBean();
            binder.readBean(currentItem);
        }

        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveButton.addClickListener(e -> saveClicked(currentOperation));
        saveButton.setText("Uložit " + itemTypeAccuS.toLowerCase());

        deleteButton.setText("Zrušit " + itemTypeAccuS.toLowerCase());
        deleteButton.setEnabled(currentOperation.isDeleteEnabled());

        this.open();
    }

    public void formFieldValuesChanged() {
        saveButton.setEnabled(true);
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
                String dateUpdateStr = null == dateTimeUpdate ? "" : dateTimeUpdate.format(VzmFormatUtils.titleModifDateFormatter);
                value = "[ Vytvořeno: " + dateCreateStr + ", Změna: " + dateUpdateStr + " ]";
            }
        }
        return value;
    }

    protected abstract void openSpecific();

    private void saveClicked(Operation operation) {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (isValid) {
            itemSaver.accept(currentItem, operation);
            if (closeAfterSave) {
                close();
            }
        } else {
            BinderValidationStatus<T> status = binder.validate();
        }
    }

    private void deleteClicked() {
// TODO: to be or not to be?
//        if (confirmDialog.getElement().getParent() == null) {
//            getUI().ifPresent(ui -> ui.add(confirmDialog));
//        }
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

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }
}
