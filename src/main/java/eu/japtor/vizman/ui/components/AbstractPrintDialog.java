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
import org.vaadin.reports.PrintPreviewReport;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractPrintDialog<T extends Serializable> extends Dialog {

//    private final static String REVERT_AND_CLOSE_STR = "Zpět";

    private Div dialogCanvas;
    private VerticalLayout dialogContent;
    private VerticalScrollLayout reportPanel;
    private Component buttonBar;
    private HorizontalLayout dialogHeader;
    private HorizontalLayout reportInfoBar;
    private HorizontalLayout reportToolBar;
    private HtmlComponent headerDevider;
//    private Button revertAndCloseButton;

//    protected GrammarGender itemGender;
//    private String itemTypeNomS;
//    private String itemTypeGenS;
//    private String itemTypeAccuS;

    private String dialogWidth;
    private String dialogHeight;
    private String dialogMinWidth;
    private String dialogMinHeight;

//    private FlexLayout headerLeftBox;

    private Button mainResizeBtn;
    private FlexLayout headerLeftBox;
    private H3 dialogTitle;
//    private H5 headerEndBox;
    private HorizontalLayout headerEndBox;
    private Div headerMiddleBox;

//    private Div headerMiddleComponent = new Div();
//    private H5 headerEndComponent;

    protected final static List<PrintPreviewReport.Format> VZM_SUPPORTED_EXP_FORMATS = Arrays.asList(
            PrintPreviewReport.Format.PDF
            , PrintPreviewReport.Format.XLS
//            , PrintPreviewReport.Format.CSV
    );

    protected AbstractPrintDialog() {
        this("1200px", "900px");
    }

    protected AbstractPrintDialog(
            String dialogWidth,
            String dialogHeight
    ){
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
        setWidth(this.dialogWidth);
        setHeight(this.dialogHeight);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        reportPanel = buildReportPane();
        buttonBar = initDialogButtonBar();

        dialogContent = new VerticalLayout();
        dialogContent.getStyle().set("flex", "auto");
        dialogContent.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogContent.getStyle()
                .set("padding-top", "0em")
                .set("padding-bottom", "0em")
        ;

        dialogContent.add(
//                initHeaderDevider()
                initReportToolBar()
                , initReportInfoBar()
                , reportPanel
//                , new Paragraph("")
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

    private Component initReportInfoBar() {
        reportInfoBar = new HorizontalLayout();
        reportInfoBar.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
//        reportToolBar.getStyle().set("margin-left", "-2em");
        reportInfoBar.setSpacing(false);
//        reportToolBar.setPadding(false);
        reportInfoBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        reportInfoBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return reportInfoBar;
    }

    private Component initReportToolBar() {
        reportToolBar = new HorizontalLayout();
        reportToolBar.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
//        reportToolBar.getStyle().set("margin-left", "-2em");
        reportToolBar.setSpacing(false);
//        reportToolBar.setPadding(false);
        reportToolBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        reportToolBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return reportToolBar;
    }

    public HasComponents getReportInfoBar() {
        return reportInfoBar;
    };

    public HasComponents getReportToolBar() {
        return reportToolBar;
    };

    public HasComponents getHeaderEndBox() {
        return headerEndBox;
    };

    private Component initHeaderEndBox() {
//        revertAndCloseButton = new Button(REVERT_AND_CLOSE_STR);
//        revertAndCloseButton.addClickListener(e -> revertClicked(true));

//        headerEndBox = new H5();
        headerEndBox = new HorizontalLayout();
        headerEndBox.getStyle()
                .set("margin-right","1.2em")
        ;
//        headerEndBox.add(
//                revertAndCloseButton
//        );
        return headerEndBox;
    }

    private void revertClicked(boolean closeAfterRevert) {
//        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
//            initControlsOperability();
        }
    }

    private void closeDialog() {
        this.close();
    }

//    protected HtmlContainer getHeaderEndBox() {
//        return headerEndBox;
//    }


    private Component initHeaderMiddleBox() {
        headerMiddleBox = new Div();
        return headerMiddleBox;
    }

    protected Div getHeaderMiddleBox() {
        return headerMiddleBox;
    }

    protected final HtmlContainer getDialogTitle() {
        return dialogTitle;
    }

    protected final HtmlComponent getHeaderDevider() {
        return headerDevider;
    }

    private VerticalScrollLayout buildReportPane() {
        VerticalScrollLayout repPane = new VerticalScrollLayout();
        repPane.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        repPane.setHeight(String.valueOf(getSizeInt(this.getHeight()) - 120) + "px");
//        repPane.getElement().setAttribute("theme", "");
//        Div div = new Div(repPane);
//
//        this.add(div);
//        this.setSizeFull();

//        repPane.setHeight("700px");
//        repPane.setMaxHeight("700px");
//        repPane.setWidth("900px");
//        repPane.addClassName("has-padding");
        return repPane;
    }

    private int getSizeInt(String sizePx) {
        int idx = sizePx.indexOf("px");
        String sizeNum = sizePx.substring(0, idx);
        return Integer.valueOf(sizeNum);
    }

    protected final VerticalScrollLayout getReportPanel() {
        return reportPanel;
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
        dialogTitle = new H3("Report...");
        dialogTitle.getStyle()
                .set("marginTop", "0.2em")
                .set("margin-right", "1em");
//        dialogTitle.getElement().setProperty("flexGrow", (double)1);
        return dialogTitle;
    }

    protected void setDialogTitle(final String titleText) {
        dialogTitle.setText(titleText);
    }

//    public abstract T getCurrentItem();
//
//    public abstract Operation getCurrentOperation();

//    public void setDefaultItemNames() {
//        setItemNames(ItemType.UNKNOWN);
//    }

//    public void setItemNames(ItemType itemType) {
//        this.itemGender = ItemNames.getItemGender(itemType);
//        this.itemTypeNomS = ItemNames.getNomS(itemType);
//        this.itemTypeGenS = ItemNames.getGenS(itemType);
//        this.itemTypeAccuS = ItemNames.getAccuS(itemType);
//    }

//    public String getItemName(final Operation operation) {
//        switch (operation) {
//            case ADD : return itemTypeNomS;
//            case EDIT : return itemTypeGenS;
//            case DELETE : return itemTypeAccuS;
//            case FAKTUROVAT: return itemTypeAccuS;
//            case EXPORT : return itemTypeAccuS;
//            default : return itemTypeNomS;
//        }
//    }

//    public String getHeaderEndComponentValue(final String titleEndText) {
//        String value = "";
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
//        return value;
//    }
}
