
package eu.japtor.vizman.ui.components;

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
import eu.japtor.vizman.backend.entity.GrammarGender;
import eu.japtor.vizman.backend.entity.HasModifDates;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public abstract class AbstractKzDialog<T extends Serializable>  extends Dialog {

    private Div dialogCanvas;
    private VerticalLayout dialogContent;
    private FormLayout formLayout;
    private Component buttonBar;
    private HorizontalLayout dialogHeader;
    private HtmlComponent headerDevider;
    private VerticalLayout upperLeftPane;
    private VerticalLayout upperRightPane;
    private HorizontalLayout upperPane;
    private VerticalLayout lowerPane;
//    private VerticalLayout upperGridContainer;
    private Div headerMiddleBox;

    protected GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;

    private String dialogWidth;
    private String dialogHeight;
    private String dialogMinWidth;
    private String dialogMinHeight;

    private FlexLayout headerLeftBox;
    private Button mainResizeBtn;
    private H3 mainTitle;
    private H5 headerEndBox;


    public AbstractKzDialog(
            String dialogWidth,
            String dialogHeight,
            boolean useUpperRightPane,
            boolean useLowerPane
    ) {

        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
        setWidth(this.dialogWidth);
        setHeight(this.dialogHeight);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        formLayout = initFormLayout();
        buttonBar = initDialogButtonBar();

        upperLeftPane = initUpperLeftPane();
        upperLeftPane.add(
                formLayout
                , new Paragraph("")
                , buttonBar
        );

        if (useUpperRightPane) {
            upperRightPane = initUpperRightPane();
        } else {
            upperRightPane = null;
        }

        upperPane = initUpperContentPane(upperLeftPane, upperRightPane);

        dialogContent = new VerticalLayout();
        dialogContent.getStyle().set("flex", "auto");
        dialogContent.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogContent.add(
                initHeaderDevider()
                , upperPane
        );
        if (useLowerPane) {
//            HorizontalLayout lowerPane = new HorizontalLayout();
//            lowerPane.add(lowerPane);
//            dialogContent.add(lowerPane);
            dialogContent.add(initLowerPane());
        }

        this.getElement().getStyle().set("padding", "0");
        this.getElement().getStyle().set("margin", "0");

        dialogCanvas = new Div();
        dialogCanvas.setSizeFull();
        dialogCanvas.getStyle().set("display", "flex");
        dialogCanvas.getStyle().set("flex-direction", "column");
        dialogCanvas.add(
//                initDialogTitlePane()
                initDialogHeader()
                , dialogContent
        );

        dialogMinHeight = headerLeftBox.getHeight();
        dialogMinWidth = headerLeftBox.getHeight();

        this.add(dialogCanvas);
    }

    public abstract Component initDialogButtonBar();

    private HtmlComponent initHeaderDevider() {
        headerDevider = new Hr();
        headerDevider.setHeight("2px");
        return headerDevider;
    }

    private Component initDialogHeader() {
//        Button btnCompressExpand = new ResizeBtn(isExpanded -> {
//            dialogContent.setVisible(!isExpanded);
//            setHeight(isExpanded ? "0" : dialogHeight);
////            upperPane.setVisible(isExpanded);
//        });
//        btnCompressExpand.getStyle()
//                .set("margin-right", "8px")
//                .set("padding", "0")
//                .set("max-width", "20px")
//        ;

        dialogHeader = new HorizontalLayout();
        dialogHeader.getStyle().set("margin-left", "-2em");
        dialogHeader.setSpacing(false);
        dialogHeader.setPadding(false);
//        dialogHeader.getStyle()
////                    .set("background-color", color)
////                    .set("theme", "icon small")
//            .set("margin", "0");
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


    private FormLayout initFormLayout() {
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("25em", 2));
//        Div div = new Div(formLayout);
//        div.addClassName("has-padding");
        layout.addClassName("has-padding");
//        add(div);
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2),
                new FormLayout.ResponsiveStep("20em", 4)
        );

        return layout;
    }

    protected final FormLayout getFormLayout() {
        return formLayout;
    }

    private VerticalLayout initUpperLeftPane() {
        VerticalLayout pane = new VerticalLayout();
        pane.setAlignItems(FlexComponent.Alignment.STRETCH);
        pane.setSpacing(false);
        pane.setPadding(false);
        return pane;
    }

    public HorizontalLayout initUpperContentPane(Component upperLeftPane, Component upperRightPane) {
        HorizontalLayout contentPane = new HorizontalLayout();
        contentPane.add(upperLeftPane);
        if (null != upperRightPane) {
            contentPane.add(
                    new Ribbon()
                    , upperRightPane
            );
        }
        return contentPane;
    }


    private VerticalLayout  initUpperRightPane() {
        VerticalLayout container = new VerticalLayout();
        container.setClassName("view-container");
        container.setSpacing(false);
        container.setPadding(false);
        container.setAlignItems(FlexComponent.Alignment.STRETCH);
//        upperGridContainer.setAlignSelf(FlexComponent.Alignment.STETCH);
//        container.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        return container;
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

    public abstract T getCurrentItem();

    public abstract Operation getCurrentOperation();

    protected final VerticalLayout getUpperRightPane() {
        return upperRightPane;
    }

    protected Consumer<Boolean> getLowerPaneResizeAction() {
        return isExpanded -> {
            upperPane.setVisible(isExpanded);
        };
    }

    protected final VerticalLayout getLowerPane() {
        return lowerPane;
    }

    protected final HtmlContainer getMainTitle() {
        return mainTitle;
    }

    protected final HtmlComponent getHeaderDevider() {
        return headerDevider;
    }

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

    protected String getItemName(final Operation operation) {
        switch (operation) {
            case ADD : return itemTypeNomS;
            case EDIT : return itemTypeGenS;
            case DELETE : return itemTypeAccuS;
            case SAVE : return itemTypeAccuS;
            case FAKTUROVAT: return itemTypeAccuS;
            case EXPORT : return itemTypeAccuS;
            default : return itemTypeNomS;
        }
    }

    protected void setItemNames(ItemType itemType) {
        this.itemGender = ItemNames.getItemGender(itemType);
        this.itemTypeNomS = ItemNames.getNomS(itemType);
        this.itemTypeGenS = ItemNames.getGenS(itemType);
        this.itemTypeAccuS = ItemNames.getAccuS(itemType);
    }

    protected void setDefaultItemNames() {
        setItemNames(ItemType.UNKNOWN);
    }
}
