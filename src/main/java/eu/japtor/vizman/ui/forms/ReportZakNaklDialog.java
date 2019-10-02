package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
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
public class ReportZakNaklDialog extends AbstractPrintDialog<Zakr> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "700px";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak-nakl";

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


    public ReportZakNaklDialog(ZaknService zaknService, Zakr zakr, ZakrListView.ZakrParams zakrParams) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("Report: NÁKLADY NA ZAKÁZKU");
//        getHeaderEndBox().setText("END text");
        this.zaknService = zaknService;
        this.zakr = zakr;
        this.zakrParams = zakrParams;
        initReportControls();
    }

    public void openDialog() {
        this.open();
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

        Button genButton = new Button("Generovat");
        genButton.addClickListener(event -> generateAndShowReport());

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

        reportParamBox = new HorizontalLayout();
        reportParamBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        reportParamBox.add(
                buildRezieParamComponent()
                , buildPojistParamComponent()
        );

        getReportToolBar().add(
                reportParamBox
                , expAnchorsBox
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


    private Component buildRezieParamComponent() {
        rezieParamField = new TextField("Režie");
        rezieParamField.setWidth("5em");
        rezieParamField.setReadOnly(true);
        rezieParamField.setValue(zakrParams.getKoefRezie().toString());
        return rezieParamField;
    }

    private Component buildPojistParamComponent() {
        pojistParamField = new TextField("Pojištění");
        pojistParamField.setWidth("5em");
        pojistParamField.setReadOnly(true);
        pojistParamField.setValue(zakrParams.getKoefPojist().toString());
        return pojistParamField;
    }

    private String getReportFileName(PrintPreviewReport.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    public void generateAndShowReport() {
        deactivateListeners();
        report.getReportBuilder()
            .setSubtitle(
                "CKONT/CZAK , Kontrakt text... / Zakázka text...\\n" +
                "Parametry: Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue()) +
                "  Pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue())
            )
        ;
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

//        bar.setClassName("buttons");
//        bar.setSpacing(false);
//        bar.setPadding(false);
//        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
//
//        bar.add(
//                leftBarPart
//                , rightBarPart
//        );
        return bar;
    }
}
