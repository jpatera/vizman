package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.report.ZakRozpracReport;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import org.vaadin.reports.PrintPreviewReport;

import java.util.Comparator;
import java.util.List;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportZakTestDialog extends AbstractPrintDialog<Zak> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "700px";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak-test";

    private ZakService zakService;
    private ZakRozpracReport report;
    private HorizontalLayout expAnchorsBox;
    private HorizontalLayout reportParamBox;
    Select<Integer> rokFilterField;
    TextField rezieParamField;

    private SerializableSupplier<List<? extends Zak>> itemsSupplier = () -> {
        if (null == rokFilterField.getValue()) {
            return zakService.fetchAllDescOrder();
        } else {
            return zakService.fetchByRokDescOrder(rokFilterField.getValue());
        }
    };


    public ReportZakTestDialog(ZakService zakService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("REPORT: Zakázky - rozpracovanost");
//        getHeaderEndBox().setText("END text");
        this.zakService = zakService;
        initReportControls();
    }

    public void openDialog() {
//        showReport();
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

        reportParamBox = new HorizontalLayout();
        reportParamBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        reportParamBox.add(
                genButton
                , buildRokFilterComponent()
                , buildRezieParamComponent()
        );

        getReportToolBar().add(
                reportParamBox
                , expAnchorsBox
        );

        report = new ZakRozpracReport();

        activateListeners();
    }


    private Component buildRokFilterComponent() {
        Span rokFilterLabel = new Span("Rok:");
        rokFilterField = buildSelectorField();
        List<Integer> roks = zakService.fetchZakRoks();
//        Integer rokMax = roks.stream().reduce(Integer::max).orElse(null);
        Integer rokMax = roks.stream().max(Comparator.naturalOrder()).orElse(null);
        rokFilterField.setItems(roks);
        rokFilterField.setValue(rokMax);
//        rokFilterField.addValueChangeListener(event -> {
//            if (event.isFromClient()) {
//                archFilterRadio.clear();
//            }
//            updateViewContent();
//        });

        HorizontalLayout rokFilterComponent = new HorizontalLayout();
        rokFilterComponent.setMargin(false);
        rokFilterComponent.setPadding(false);
        rokFilterComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        rokFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rokFilterComponent.add(
                rokFilterLabel
                , rokFilterField
        );
        return rokFilterComponent;
    }

    private Component buildRezieParamComponent() {
        Span rezieParamLabel = new Span("Režie:");
        rezieParamField = new TextField();
        rezieParamField.setValue("0,8");

        HorizontalLayout rezieParamComponent = new HorizontalLayout();
        rezieParamComponent.setMargin(false);
        rezieParamComponent.setPadding(false);
        rezieParamComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        rezieParamComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rezieParamComponent.add(
                rezieParamLabel
                , rezieParamField
        );
        return rezieParamComponent;
    }

    private <T> Select buildSelectorField() {
        Select <T> selector = new Select<>();
        selector.setSizeFull();
        selector.setEmptySelectionCaption("Vše");
        selector.setEmptySelectionAllowed(true);
        return selector;
    }

    private String getReportFileName(PrintPreviewReport.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    private void generateAndShowReport() {
        deactivateListeners();
        report.getReportBuilder().setSubtitle(
                "Parametry: Rok=" + (null == rokFilterField.getValue() ? "Vše" : rokFilterField.getValue().toString())
                + "  Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue())
        );
        report.setItems(itemsSupplier.get());
        expAnchorsBox.getChildren()
                .forEach(anch -> {
                    if (anch.getClass() == ReportExpAnchor.class) {
                        PrintPreviewReport.Format expFormat = ((ReportExpAnchor)anch).getExpFormat();
//                        if (expFormat == PrintPreviewReport.Format.PDF)
                            ((ReportExpAnchor)anch).setHref(
                                    report.getStreamResource(getReportFileName(expFormat), itemsSupplier, expFormat)
                            );
//                        )
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
