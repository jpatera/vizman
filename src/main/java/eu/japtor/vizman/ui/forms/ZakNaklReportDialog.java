package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.report.ZakNaklReport;
import eu.japtor.vizman.backend.service.ZaknService;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.vaadin.reports.PrintPreviewReport;

import java.util.List;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakNaklReportDialog extends AbstractPrintDialog<Zakr> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "750px";
    private static final String CLOSE_STR = "Zavřít";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak-nakl";

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Button closeButton;

    private ZaknService zaknService;
    private Zakr zakr;
    private ZakrListView.ZakrParams zakrParams;
    private ZakNaklReport report;
    private HorizontalLayout expAnchorsBox;
    private HorizontalLayout reportParamBox;
//    private HorizontalLayout zakInfoBox;
//    private Select<Integer> rokZakParamField;
//    private Select<Boolean> archParamField;
//    private Select<String> skupinaParamField;
//    private Select<String> rxParamField;
//    private Select<String> ryParamField;
//    private Select<Boolean> archFilterRadio;
    private TextField rezieParamField;
    private TextField pojistParamField;
//    private TextField kurzParamField;

    private SerializableSupplier<List<? extends Zakn>> itemsSupplier = () ->
//        zaknService.fetchByZakId(zakr.getId(), zakrParams)
        zaknService.fetchByZakIdSumByYm(zakr.getId(), zakrParams)
    ;


    public ZakNaklReportDialog(ZaknService zaknService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("Report: NÁKLADY NA ZAKÁZKU");
//        getHeaderEndBox().setText("END text");
        this.zaknService = zaknService;
        initReportControls();
    }

    public void openDialog(Zakr zakr, ZakrListView.ZakrParams zakrParams) {
        this.zakr = zakr;
        this.zakrParams = zakrParams;
        rezieParamField.setValue(zakrParams.getKoefRezie().toString());
        pojistParamField.setValue(zakrParams.getKoefPojist().toString());
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


//        zakInfoBox = new HorizontalLayout();
//        zakInfoBox.getStyle()
//                .set("margin-top", "0.2em")
//                .set("margin-bottom", "0.2em");
//        zakInfoBox.add(
//                buildZakInfoComponent()
//        );
//
//        getReportInfoBox().add(
//                zakInfoBox
//        );

//        Button genButton = new Button("Generovat");
//        genButton.addClickListener(event -> generateAndShowReport());

        reportParamBox = new HorizontalLayout();
        reportParamBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em")
        ;
//        reportParamBox.add(
//                genButton
                initRezieParamComponent();
                initPojistParamComponent();
//        );

        getReportToolBar().add(
                reportParamBox
        );

        getHeaderEndBox().add(
                expAnchorsBox
        );

        report = new ZakNaklReport();
        activateListeners();
    }

    private <T> Select buildSelectorParamField() {
        Select <T> selector = new Select<>();
        selector.setSizeFull();
        selector.setEmptySelectionCaption("Vše");
        selector.setEmptySelectionAllowed(true);
        return selector;
    }


    private Component initRezieParamComponent() {
        rezieParamField = new TextField("Režie");
        rezieParamField.setWidth("5em");
        rezieParamField.setReadOnly(true);
        return rezieParamField;
    }

    private Component initPojistParamComponent() {
        pojistParamField = new TextField("Pojištění");
        pojistParamField.setWidth("5em");
        pojistParamField.setReadOnly(true);
        return pojistParamField;
    }

    private String getReportFileName(PrintPreviewReport.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    public void generateAndShowReport() {
        deactivateListeners();
        report.setSubtitleText(
                "Parametry: Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue())
                + "  Pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue())
        );
        report.setItems(itemsSupplier.get());
        expAnchorsBox.getChildren()
                .forEach(anch -> {
                    if (anch.getClass() == ReportExpAnchor.class) {
                        PrintPreviewReport.Format expFormat = ((ReportExpAnchor)anch).getExpFormat();
//                        if (expFormat == PrintPreviewReport.Format.XLS) {
//                            JRXlsExporter xlsExporter = new JRXlsExporter();
//                            SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
                        // Mel by poznat BigDecimal:
//                            xlsReportConfiguration.setDetectCellType(true);
//                            xlsExporter.setConfiguration(xlsReportConfiguration);
//                        }
//                        if (expFormat == PrintPreviewReport.Format.PDF) {
                            ((ReportExpAnchor) anch).setHref(
                                    report.getStreamResource(getReportFileName(expFormat), itemsSupplier, expFormat)
                            );
//                        }
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
