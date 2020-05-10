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
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.ResizeBtn;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public abstract class AbstractSimpleFormDialog<T extends Serializable> extends Dialog {

    private FormLayout formLayout;
    private Div dialogCanvas;
    private VerticalLayout dialogContent;

    protected GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;

    private String dialogWidth;
    private String dialogHeight;
    private String dialogMinWidth;
    private String dialogMinHeight;

    private HorizontalLayout dialogHeader;
    private Component buttonBar;
    private HtmlComponent headerDevider;

    private H3 mainTitle;
    private FlexLayout headerLeftBox;
    private Div headerMiddleBox;
    private H5 headerEndBox;
    private Button mainResizeBtn;

    private T currentItem;
    private T origItem;


    protected AbstractSimpleFormDialog() {
        this("1000px", "800px");
    }

    protected AbstractSimpleFormDialog(
            String dialogWidth,
            String dialogHeight
    ){
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
        setWidth(this.dialogWidth);
        setHeight(this.dialogHeight);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        setDefaultItemNames();  // Set general default names

        formLayout = buildFormLayout();
        buttonBar = initDialogButtonBar();

        dialogContent = new VerticalLayout();
        dialogContent.getStyle().set("flex", "auto");
        dialogContent.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialogContent.add(
                initHeaderDevider()
                , formLayout
                , new Paragraph("")
                , buttonBar
        );

        this.getElement().getStyle().set("padding", "0");
        this.getElement().getStyle().set("margin", "0");

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

//        setupEventListeners();
    }

    public abstract Component initDialogButtonBar();

    private HtmlComponent initHeaderDevider() {
        headerDevider = new Hr();
        headerDevider.setHeight("2px");
        return headerDevider;
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

    public String getDialogTitle(Operation operation, final GrammarGender itemGender) {
        return operation.getTitleOperName(itemGender) + " " + getItemName(operation).toUpperCase();
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
        headerEndBox = new H5();
        headerEndBox.getStyle()
                .set("margin-right","1.2em")
        ;
        return headerEndBox;
    }

    protected HtmlContainer getHeaderEndBox() {
        return headerEndBox;
    }

    protected final HtmlContainer getMainTitle() {
        return mainTitle;
    }

    protected final HtmlComponent getHeaderDevider() {
        return headerDevider;
    }

    private FormLayout buildFormLayout() {
        FormLayout layout = new FormLayout();
        layout.addClassName("has-padding");
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2)
        );
        return layout;
    }

    protected final FormLayout getFormLayout() {
        return formLayout;
    }

    private Button initDialogResizeBtn() {
        mainResizeBtn = new ResizeBtn(getDialogResizeAction(), false);
        return mainResizeBtn;
    }

    public Consumer<Boolean> getDialogResizeAction() {
        return isExpanded -> {
            dialogContent.setVisible(!isExpanded);
            headerEndBox.setVisible(!isExpanded);
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

    public T getCurrentItem() {
        return currentItem;
    };

    public void setCurrentItem(T currentItem) {
        this.currentItem = currentItem;
    }

    public T getOrigItem() {
        return origItem;
    };

    public void setOrigItem(T origItem) {
        this.origItem = origItem;
    }


    public abstract Operation getCurrentOperation();

    public void setDefaultItemNames() {
        setItemNames(ItemType.UNKNOWN);
    }

    public void setItemNames(ItemType itemType) {
        this.itemGender = ItemNames.getItemGender(itemType);
        this.itemTypeNomS = ItemNames.getNomS(itemType);
        this.itemTypeGenS = ItemNames.getGenS(itemType);
        this.itemTypeAccuS = ItemNames.getAccuS(itemType);
    }

    public String getItemName(final Operation operation) {
        switch (operation) {
            case ADD : return itemTypeNomS;
            case EDIT : return itemTypeGenS;
            case DELETE : return itemTypeAccuS;
            case FAKTUROVAT: return itemTypeAccuS;
            case EXPORT : return itemTypeAccuS;
            default : return itemTypeNomS;
        }
    }

    public String getHeaderEndComponentValue(final String titleEndText) {
        String value = "";
        if ((null == titleEndText) && (getCurrentItem() instanceof HasModifDates)) {
            if (getCurrentOperation() == Operation.ADD) {
                value = "";
            } else {
                LocalDate dateCreate = ((HasModifDates) getCurrentItem()).getDateCreate();
                String dateCreateStr = null == dateCreate ? "" : dateCreate.format(VzmFormatUtils.basicDateFormatter);
                LocalDateTime dateTimeUpdate = ((HasModifDates) getCurrentItem()).getDatetimeUpdate();
                String dateUpdateStr = null == dateTimeUpdate ? "" : dateTimeUpdate.format(VzmFormatUtils.titleModifDateFormatter);
                value = "[ Vytvořeno: " + dateCreateStr + ", Změna: " + dateUpdateStr + " ]";
            }
        }
        return value;
    }
}
