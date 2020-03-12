package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.DochMonth;
import eu.japtor.vizman.backend.report.DochMonthReport;
import eu.japtor.vizman.backend.service.DochYearMonthService;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import eu.japtor.vizman.ui.views.DochView;
import org.vaadin.reports.PrintPreviewReport;

import java.util.List;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakListExcelReportDialog extends AbstractPrintDialog<DochMonth> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "750px";
    private static final String CLOSE_STR = "Zavřít";

    private final static String REPORT_FILE_NAME = "vzm-rep-doch-mes";

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Button closeButton;

    private DochYearMonthService dochYearMonthService;
    private DochView.DochParams dochParams;
    private DochMonthReport report;
    private HorizontalLayout expAnchorsBox;
    private HorizontalLayout reportParamBox;
    private TextField dochUserParamField;
    private TextField dochYmParamField;

    private SerializableSupplier<List<? extends DochMonth>> itemsSupplier = () -> {
//          zaknService.fetchByZakId(zakr.getId(), zakrParams)
            return dochYearMonthService.fetchRepDochMonthForPersonAndYm(dochParams.getPersonId(), dochParams.getDochYm());
        }
    ;


    public ZakListExcelReportDialog(DochYearMonthService dochYearMonthService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("Report: MĚSÍČNÍ DOCHÁZKA");
//        getHeaderEndBox().setText("END text");
        this.dochYearMonthService = dochYearMonthService;
        initReportControls();
    }

    public void openDialog(DochView.DochParams dochParams) {
        this.dochParams = dochParams;
//        rezieParamField.setValue(zakrParams.getKoefRezie().toString());
//        pojistParamField.setValue(zakrParams.getKoefPojist().toString());
        this.open();
    }

    private void closeDialog() {
        this.close();
    }

    private void closeClicked() {
        closeDialog();
    }

    private void initReportControls() {

        deactivateListeners();

        expAnchorsBox = new HorizontalLayout();
        expAnchorsBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        for (PrintPreviewReport.Format format : VZM_SUPPORTED_EXP_FORMATS) {
            expAnchorsBox.add(new ReportExpAnchor(format));
        }

        reportParamBox = new HorizontalLayout();
        reportParamBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em")
        ;

        initDochYmParamComponent();
        initUserNameParamComponent();

        getReportToolBar().add(
                reportParamBox
        );

        getHeaderEndBox().add(
                expAnchorsBox
        );

        report = new DochMonthReport();
        activateListeners();
    }


    private Component initDochYmParamComponent() {
        dochYmParamField = new TextField("Rok-Měs");
        dochYmParamField.setWidth("5em");
        dochYmParamField.setReadOnly(true);
        return dochYmParamField;
    }

    private Component initUserNameParamComponent() {
        dochUserParamField = new TextField("Uživatel");
        dochUserParamField.setWidth("5em");
        dochUserParamField.setReadOnly(true);
        return dochUserParamField;
    }

    private String getReportFileName(PrintPreviewReport.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    public void generateAndShowReport() {
        deactivateListeners();
//        report.setSubtitleText(
//                "Parametry: Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue())
//                + "  Pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue())
//        );
        report.setItems(itemsSupplier.get());
        expAnchorsBox.getChildren()
                .forEach(anch -> {
                    if (anch.getClass() == ReportExpAnchor.class) {
                        PrintPreviewReport.Format expFormat = ((ReportExpAnchor)anch).getExpFormat();
                        ((ReportExpAnchor) anch).setHref(
                                report.getStreamResource(getReportFileName(expFormat), itemsSupplier, expFormat)
                        );
                    }
                });

        this.getReportPanel().removeAllContent();
        this.getReportPanel().addContent(report);
        activateListeners();
    }


    private void deactivateListeners() {
    }

    private void activateListeners() {
    }

// ------------------------------------------------------------

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

        closeButton = new Button(CLOSE_STR);
        closeButton.setAutofocus(true);
        closeButton.getElement().setAttribute("theme", "primary");
        closeButton.addClickListener(e -> closeClicked());

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);

        rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                closeButton
//                , revertAndCloseButton
        );

        bar.setClassName("buttons");
        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(
                leftBarPart
                , rightBarPart
        );
        return bar;
    }

//  --------------------------------------------

}
