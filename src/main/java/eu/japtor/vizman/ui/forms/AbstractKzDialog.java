
package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
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
import eu.japtor.vizman.ui.components.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public abstract class AbstractKzDialog<T extends Serializable>  extends Dialog {

    private Div dialogCanvas;
    private VerticalLayout dialogContent;
    private FormLayout formLayout;
    private Component upperLeftButtonBar;
    private HorizontalLayout dialogHeader;
    private HtmlComponent headerDevider;

    private VerticalLayout upperLeftPane;
    private VerticalLayout upperRightPane;
    private HorizontalLayout upperPane;
    private VerticalLayout lowerPane;
//    private VerticalLayout upperGridContainer;

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

        upperLeftButtonBar = buildDialogButtonBar();
        upperPane = initUpperPane(upperLeftButtonBar, useUpperRightPane);

        dialogContent = new VerticalLayout();
        dialogContent.getStyle().set("flex", "auto");
        dialogContent.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogContent.add(
                initHeaderDevider()
                , upperPane
        );
        if (useLowerPane) {
            dialogContent.add(initLowerPane());
        }

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
    }

    public abstract Component buildDialogButtonBar();

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


    public String getDialogTitle(Operation operation, final GrammarGender itemGender) {
        return operation.getTitleOperName(itemGender) + " " + getItemName(operation).toUpperCase();
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
        formLayout = new FormLayout();
        formLayout.addClassName("has-padding");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2),
                new FormLayout.ResponsiveStep("20em", 4)
        );
        return formLayout;
    }

    protected final FormLayout getFormLayout() {
        return formLayout;
    }

    private Component initUpperLeftPane(final Component buttonBar) {
        upperLeftPane = new VerticalLayout();
        upperLeftPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        upperLeftPane.setSpacing(false);
        upperLeftPane.setPadding(false);
        upperLeftPane.add(
                initFormLayout()
                , new Paragraph("")
                , buttonBar
        );
        return upperLeftPane;
    }

    public HorizontalLayout initUpperPane(Component upperLeftButtonBar, boolean useUpperRightPane) {
        upperPane = new HorizontalLayout();
        upperPane.add(initUpperLeftPane(upperLeftButtonBar));
        if (useUpperRightPane) {
            upperPane.add(
                    new Ribbon()
                    , initUpperRightPane()
            );
        } else {
            upperRightPane = null;
        }
        return upperPane;
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
//
//        lowerPane.setSizeFull();
        lowerPane.setAlignItems(FlexComponent.Alignment.STRETCH);
        return lowerPane;
    }

    public Consumer<Boolean> getAlertModifSwitchAction() {
        return isActive -> {
            // Override, by default does nothing.
        };
    };

    public HtmlContainer getHeaderEndComponent(final String titleEndText) {
        HtmlContainer comp = new Div();
        Component alertSwitchBtn = new AlertSwitchBtn(getAlertModifSwitchAction(), isItemAlerted());
        if ((null == titleEndText) && (getCurrentItem() instanceof HasModifDates)) {
            if (getCurrentOperation() != Operation.ADD) {
                LocalDate dateCreate = ((HasModifDates) getCurrentItem()).getDateCreate();
                Span createComponent = null == dateCreate ? new Span("")
                        : new Span("Vytvořeno QQ: " + dateCreate.format(VzmFormatUtils.basicDateFormatter));

                LocalDateTime dateTimeUpdate = ((HasModifDates) getCurrentItem()).getDatetimeUpdate();
                Span updateComponent = null == dateCreate ? new Span("")
                        : new Span("Změna: " + dateTimeUpdate.format(VzmFormatUtils.titleUpdateDateFormatter));
                if (ItemType.KONT == ((KzTreeAware<KzTreeAware>)getCurrentItem()).getTyp()) {
                    updateComponent.getElement().getStyle()
                            .set("color", VzmFormatUtils.getColorByUpdatedRule(dateTimeUpdate, ((KzTreeAware<?>) getCurrentItem()).getUpdatedBy()));
                }
                comp.add(alertSwitchBtn, new Block(), createComponent, new Block(), updateComponent);
            }
        }
        return comp;
    }

    private boolean isItemAlerted() {
        return ((HasAlertModif) getCurrentItem()).isAlertModif() || ((HasAlertModif) getCurrentItem()).hasAlertedItems();
    }

    public abstract T getCurrentItem();

    public abstract Operation getCurrentOperation();

    protected final VerticalLayout getUpperRightPane() {
        return upperRightPane;
    }

    protected Consumer<Boolean> getLowerPaneResizeAction() {
        return isExpanded -> {
            if (isExpanded) {
                upperPane.setVisible(isExpanded);
                if (null != lowerFlexComponent) {
                    lowerFlexComponent.setHeight(lowerFlexComponentDefaultHeight);
                }
            } else {
                upperPane.setVisible(isExpanded);
                if (null != lowerFlexComponent) {
                    lowerFlexComponent.setHeight(null); // Note: result of setHeightFull zero height of the gridd
                }
            }
        };
    }

    protected final VerticalLayout getLowerPane() {
        return lowerPane;
    }


    private HasSize lowerFlexComponent;
    private String lowerFlexComponentDefaultHeight = "100%";

    public void addLowerPaneFlexComponent(HasSize lowerResizableComponent, String defaultHeight) {
        this.lowerFlexComponentDefaultHeight = defaultHeight;
        this.lowerFlexComponent = lowerResizableComponent;
        this.lowerFlexComponent.setHeight(defaultHeight);
        lowerPane.add((Component) this.lowerFlexComponent);
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
