package eu.japtor.vizman.ui.forms;

import ar.com.fdvs.dj.domain.constants.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
//import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.report.ZakRozpracReport;
import eu.japtor.vizman.backend.service.ZakrService;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.vaadin.reports.PrintPreviewReport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportZakRozpracDialog extends AbstractPrintDialog<Zakr> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "700px";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak-rozprac";

    private ZakrService zakrService;
    private ZakrListView.ZakrParams zakrParams;
    private ZakRozpracReport report;
    private HorizontalLayout expAnchorsBox;
    private HorizontalLayout reportParamBox;
    private Select<Integer> rokZakParamField;
    private Select<Boolean> archParamField;
    private Select<String> skupinaParamField;
    private Select<String> rxParamField;
    private Select<String> ryParamField;
//    private Select<Boolean> archFilterRadio;
    private TextField rezieParamField;
    private TextField pojistParamField;
    private TextField kurzParamField;

    private SerializableSupplier<List<? extends Zakr>> itemsSupplier = () -> {
//        if (null == rokZakParamField.getValue()) {
            return zakrService.fetchByFiltersDescOrder(zakrParams);
//            return zakrService.fetchAllDescOrder();
//        } else {
//            return zakrService.fetchByRokDescOrder(rokZakParamField.getValue());
//        }
    };


    public ReportZakRozpracDialog(ZakrService zakrService, ZakrListView.ZakrParams zakrParams) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("Report: ROZPRACOVANOST ZAKÁZEK");
//        getHeaderEndBox().setText("END text");
        this.zakrService = zakrService;
        this.zakrParams = zakrParams;
        initReportControls();
//        this.addOpenedChangeListener(e -> generateAndShowReport());
//        this.addAttachListener(e -> generateAndShowReport());
    }

    public void openDialog() {
//        showReport();
        this.open();
//        this.addOpenedChangeListener(e -> generateAndShowReport());
//        generateAndShowReport();
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
//                genButton
                buildArchiveParamComponent()
                , buildZakRokFilterComponent()
                , buildSkupinaFilterComponent()
                , buildRxFilterComponent()
                , buildRyFilterComponent()
                , buildKurzParamComponent()
                , buildRezieParamComponent()
                , buildPojistParamComponent()
        );

        getReportToolBar().add(
                reportParamBox
                , expAnchorsBox
        );

        report = new ZakRozpracReport();

        activateListeners();
    }

    private <T> Select buildSelectorParamField() {
        Select <T> selector = new Select<>();
        selector.setSizeFull();
        selector.setEmptySelectionCaption("Vše");
        selector.setEmptySelectionAllowed(true);
        return selector;
    }

    private Component buildArchiveParamComponent() {
        archParamField = buildSelectorParamField();
        archParamField.setLabel("Arch.");
        archParamField.setWidth("5em");
        archParamField.setReadOnly(true);
        archParamField.setItems(Boolean.valueOf(false), Boolean.valueOf(true));
        archParamField.setValue(zakrParams.getArch());
        return archParamField;
    }

    private Component buildZakRokFilterComponent() {
        rokZakParamField = buildSelectorParamField();
        rokZakParamField.setLabel("Rok zak.");
        rokZakParamField.setWidth("5em");
        rokZakParamField.setReadOnly(true);
        List<Integer> zakRoks = zakrService.fetchZakrRoks();
//        Integer rokMax = roks.stream().reduce(Integer::max).orElse(null);
//        Integer rokMax = zakRoks.stream().max(Comparator.naturalOrder()).orElse(null);
        rokZakParamField.setItems(zakRoks);
        rokZakParamField.setValue(zakrParams.getRokZak());
//        rokZakParamField.setValue(rokMax);

//        rokZakParamField.addValueChangeListener(event -> {
//            if (event.isFromClient()) {
//                archFilterRadio.clear();
//            }
//            updateViewContent();
//        });

//        Span rokFilterLabel = new Span("Rok:");
//        HorizontalLayout rokFilterComponent = new HorizontalLayout();
//        rokFilterComponent.setMargin(false);
//        rokFilterComponent.setPadding(false);
//        rokFilterComponent.setAlignItems(FlexComponent.Alignment.CENTER);
//        rokFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        rokFilterComponent.add(
//                rokFilterLabel
//                , rokZakParamField
//        );
//        return rokFilterComponent;
        return rokZakParamField;
    }

    private Component buildSkupinaFilterComponent() {
        skupinaParamField = buildSelectorParamField();
        skupinaParamField.setLabel("Skupina");
        skupinaParamField.setWidth("5em");
        skupinaParamField.setReadOnly(true);
        List<String> skups = new ArrayList<>(Arrays.asList("1", "2", "TBD!"));
        skupinaParamField.setItems(skups);
        skupinaParamField.setValue(zakrParams.getSkupina());
        return skupinaParamField;
    }

    private Component buildRxFilterComponent() {
        rxParamField = buildSelectorParamField();
        rxParamField.setLabel("RX");
        rxParamField.setWidth("5em");
        rxParamField.setReadOnly(true);
        List<String> rxList = new ArrayList<>(Arrays.asList("R1", "R2", "R3", "R4"));
        rxParamField.setItems(rxList);
        rxParamField.setValue(zakrParams.getRx());
        return rxParamField;
    }

    private Component buildRyFilterComponent() {
        ryParamField = buildSelectorParamField();
        ryParamField.setLabel("RY");
        ryParamField.setWidth("5em");
        ryParamField.setReadOnly(true);
        List<String> ryList = new ArrayList<>(Arrays.asList("R1", "R2", "R3", "R4"));
        ryParamField.setItems(ryList);
        ryParamField.setValue(zakrParams.getRy());
        return ryParamField;
    }

    private Component buildKurzParamComponent() {
        kurzParamField = new TextField("CZK/EUR");
        kurzParamField.setWidth("5em");
        kurzParamField.setReadOnly(true);
        kurzParamField.setValue(zakrParams.getKurzEur().toString());
        return kurzParamField;
    }

    private Component buildRezieParamComponent() {
        rezieParamField = new TextField("Režie");
        rezieParamField.setWidth("5em");
        rezieParamField.setReadOnly(true);
        rezieParamField.setValue(zakrParams.getKoefRezie().toString());

//        Span rezieParamLabel = new Span("Režie:");
//        HorizontalLayout rezieParamComponent = new HorizontalLayout();
//        rezieParamComponent.setMargin(false);
//        rezieParamComponent.setPadding(false);
//        rezieParamComponent.setAlignItems(FlexComponent.Alignment.CENTER);
//        rezieParamComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        rezieParamComponent.add(
//                rezieParamLabel
//                , rezieParamField
//        );
//        return rezieParamComponent;
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
                "Parametry: Arch=" + (null == archParamField.getValue() ? "Vše" : archParamField.getValue().toString()) +
                "  Rok zak.=" + (null == rokZakParamField.getValue() ? "Vše" : rokZakParamField.getValue().toString()) +
                "  Skupina=" + (null == skupinaParamField.getValue() ? "Vše" : skupinaParamField.getValue().toString()) +
                "  rx=" + (null == rxParamField.getValue() ? "" : rxParamField.getValue().toString()) +
                "  ry=" + (null == ryParamField.getValue() ? "" : ryParamField.getValue().toString()) +
                "  Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue()) +
                "  Pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue()) +
                "  Kurz CZK/EUR=" + (null == kurzParamField.getValue() ? "" : kurzParamField.getValue())
            )
//            .setPageSizeAndOrientation(Page.Page_A4_Landscape())
//            .build()
        ;
        report.setItems(itemsSupplier.get());
        expAnchorsBox.getChildren()
                .forEach(anch -> {
                    if (anch.getClass() == ReportExpAnchor.class) {
                        PrintPreviewReport.Format expFormat = ((ReportExpAnchor)anch).getExpFormat();
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
