package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.japtor.vizman.backend.entity.GrammarGender;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractGridDialog<T extends Serializable>  extends Dialog {

    private Div dialogCanvas;
    private VerticalLayout dialogContent;
//    private FormLayout formLayout;
    private VerticalLayout gridContainer;
    private Component buttonBar;
    private HorizontalLayout dialogHeader;
    private HtmlComponent headerDevider;

    protected GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;

    private String dialogWidth;
    private String dialogHeight;
    private String dialogMinWidth;
    private String dialogMinHeight;

    private Button mainResizeBtn;
    private FlexLayout headerLeftBox;
    private H3 mainTitle;
    private H5 headerEndBox;
    private Div headerMiddleBox;


    protected AbstractGridDialog() {
        this("1000px", "800px");
    }

    protected AbstractGridDialog(
            String dialogWidth,
            String dialogHeight
    ){
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
        setWidth(this.dialogWidth);
        setHeight(this.dialogHeight);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        gridContainer = initGridContainer();
        buttonBar = initDialogButtonBar();

        dialogContent = new VerticalLayout();
        dialogContent.getStyle().set("flex", "auto");
        dialogContent.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialogContent.add(
                initHeaderDevider()
                , gridContainer
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


    private Component initHeaderMiddleBox() {
        headerMiddleBox = new Div();
        return headerMiddleBox;
    }

    protected Div getHeaderMiddleBox() {
        return headerMiddleBox;
    }

    protected final HtmlContainer getMainTitle() {
        return mainTitle;
    }

    protected final HtmlComponent getHeaderDevider() {
        return headerDevider;
    }

    private VerticalLayout initGridContainer() {
        gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
//        gridContainer.addClassName("has-padding");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

//        gridContainer.add(initKzToolBar());
//        gridContainer.add(initKzTreeGrid());

        return gridContainer;
    }

    protected final HasComponents getGridContainer() {
        return gridContainer;
    }

//    private FormLayout buildFormLayout() {
//        FormLayout layout = new FormLayout();
//        layout.addClassName("has-padding");
//        layout.setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("20em", 2)
//        );
//        return layout;
//    }

//    protected final FormLayout getFormLayout() {
//        return formLayout;
//    }

    private Component initHeaderLeftBox() {
        headerLeftBox = new FlexLayout(
                initDialogResizeBtn()
                , initDialogTitle()
        );
        headerLeftBox.setAlignItems(FlexComponent.Alignment.BASELINE);
        return headerLeftBox;
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
//        mainTitle.getElement().setProperty("flexGrow", (double)1);
        return mainTitle;
    }

    public abstract List<T> getCurrentItemList();

//    public abstract Operation getCurrentOperation();

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
//        if ((null == titleEndText) && (getCurrentItem() instanceof HasModifDates)) {
//            if (getCurrentOperation() == Operation.ADD) {
//                value = "";
//            } else {
//                LocalDate dateCreate = ((HasModifDates) getCurrentItem()).getDateCreate();
//                String dateCreateStr = null == dateCreate ? "" : dateCreate.format(VzmFormatUtils.basicDateFormatter);
//                LocalDateTime dateTimeUpdate = ((HasModifDates) getCurrentItem()).getDatetimeUpdate();
//                String dateUpdateStr = null == dateTimeUpdate ? "" : dateTimeUpdate.format(VzmFormatUtils.titleModifDateFormatter);
//                value = "[ Vytvořeno: " + dateCreateStr + ", Změna: " + dateUpdateStr + " ]";
//            }
//        }
        return value;
    }
}
