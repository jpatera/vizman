package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.report.ZakRozpracReport;
import eu.japtor.vizman.backend.service.ZakrService;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.vaadin.reports.PrintPreviewReport;

import java.util.List;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakRozpracReportDialog extends AbstractPrintDialog<Zakr> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "750px";
    private static final String CLOSE_STR = "Zavřít";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak-rozprac";

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Button closeButton;

    private ZakrService zakrService;
    private ZakrListView.ZakrParams zakrParams;
    private ZakRozpracReport report;
    private HorizontalLayout expAnchorsBox;
    private HorizontalLayout reportParamBox;
    private Select<Boolean> archFilterField;
    private TextField ckzFilterField;
    private Select<Integer> rokZakFilterField;
    private Select<String> skupinaFilterField;
    private Select<String> rxParamField;
    private Select<String> ryParamField;
    private TextField rezieParamField;
    private TextField pojistParamField;
    private TextField kurzParamField;

    private SerializableSupplier<List<? extends Zakr>> itemsSupplier = () -> {
        return zakrService.fetchAndCalcByFiltersDescOrder(zakrParams);
    };


    public ZakRozpracReportDialog(ZakrService zakrService, ZakrListView.ZakrParams zakrParams) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("Report: ROZPRACOVANOST ZAKÁZEK");
        this.zakrService = zakrService;
        initReportControls();
    }

    public void openDialog(ZakrListView.ZakrParams zakrParams) {
        this.zakrParams = zakrParams;

        archFilterField.setValue(zakrParams.getArch());
        ckzFilterField.setValue(zakrParams.getCkz());
        rokZakFilterField.setValue(zakrParams.getRokZak());
        skupinaFilterField.setValue(zakrParams.getSkupina());
        rxParamField.setValue(zakrParams.getRx());
        ryParamField.setValue(zakrParams.getRy());
        kurzParamField.setValue(zakrParams.getKurzEur().toString());
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

//        Button genButton = new Button("Generovat");
//        genButton.addClickListener(event -> generateAndShowReport());

        reportParamBox = new HorizontalLayout();
        reportParamBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em")
        ;
//        reportParamBox.add(
//                genButton
                initArchiveParamComponent();
                initCkzFilterComponent();
                initZakRokFilterComponent();
                initSkupinaFilterComponent();
                initRxFilterComponent();
                initRyFilterComponent();
                initKurzParamComponent();
                initRezieParamComponent();
                initPojistParamComponent();
//        );

        getReportToolBar().add(
                reportParamBox
        );

        getHeaderEndBox().add(
                expAnchorsBox
        );

        report = new ZakRozpracReport();
        activateListeners();
    }

    private Component initArchiveParamComponent() {
        archFilterField = buildSelectorParamField();
        archFilterField.setLabel("Arch.");
        archFilterField.setWidth("5em");
        archFilterField.getStyle().set("theme", "small");
        archFilterField.setReadOnly(true);
        archFilterField.setItems(Boolean.valueOf(false), Boolean.valueOf(true));
        return archFilterField;
    }

    private Component initCkzFilterComponent() {
        ckzFilterField = new TextField("ČK/ČZ");
        ckzFilterField.setWidth("5em");
        ckzFilterField.getStyle().set("theme", "small");
        ckzFilterField.setReadOnly(true);

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
        return ckzFilterField;
    }

    private Component initZakRokFilterComponent() {
        rokZakFilterField = buildSelectorParamField();
        rokZakFilterField.setLabel("Rok zak.");
        rokZakFilterField.setWidth("5em");
        rokZakFilterField.getStyle().set("theme", "small");
        rokZakFilterField.setReadOnly(true);
//        List<Integer> zakRoks = zakrService.fetchZakrRoks();
//        rokZakFilterField.setItems(zakRoks);
//        Integer rokMax = roks.stream().reduce(Integer::max).orElse(null);
//        Integer rokMax = zakRoks.stream().max(Comparator.naturalOrder()).orElse(null);
        return rokZakFilterField;
    }

    private Component initSkupinaFilterComponent() {
        skupinaFilterField = buildSelectorParamField();
        skupinaFilterField.setLabel("Skupina");
        skupinaFilterField.setWidth("5em");
        skupinaFilterField.getStyle().set("theme", "small");
        skupinaFilterField.setReadOnly(true);
//        List<String> skups = new ArrayList<>(Arrays.asList("1", "2", "TBD!"));
//        skupinaFilterField.setItems(skups);
        return skupinaFilterField;
    }

    private Component initRxFilterComponent() {
        rxParamField = buildSelectorParamField();
        rxParamField.setLabel("RX");
        rxParamField.setWidth("5em");
        rxParamField.getStyle().set("theme", "small");
        rxParamField.setReadOnly(true);
//        List<String> rxList = new ArrayList<>(Arrays.asList("R1", "R2", "R3", "R4"));
//        rxParamField.setItems(rxList);
        return rxParamField;
    }

    private Component initRyFilterComponent() {
        ryParamField = buildSelectorParamField();
        ryParamField.setLabel("RY");
        ryParamField.setWidth("5em");
        ryParamField.getStyle().set("theme", "small");
        ryParamField.setReadOnly(true);
//        List<String> ryList = new ArrayList<>(Arrays.asList("R1", "R2", "R3", "R4"));
//        ryParamField.setItems(ryList);
        return ryParamField;
    }

    private Component initKurzParamComponent() {
        kurzParamField = new TextField("CZK/EUR");
        kurzParamField.setWidth("5em");
        kurzParamField.getStyle().set("theme", "small");
        kurzParamField.setReadOnly(true);
        return kurzParamField;
    }

    private Component initRezieParamComponent() {
        rezieParamField = new TextField("Režie");
        rezieParamField.setWidth("5em");
        rezieParamField.getStyle().set("theme", "small");
        rezieParamField.setReadOnly(true);

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
        return rezieParamField;
    }

    private Component initPojistParamComponent() {
        pojistParamField = new TextField("Pojištění");
        pojistParamField.setWidth("5em");
        pojistParamField.getStyle().set("theme", "small");
        pojistParamField.setReadOnly(true);
        return pojistParamField;
    }

    private <T> Select buildSelectorParamField() {
        Select <T> selector = new Select<>();
        selector.setSizeFull();
        selector.setEmptySelectionCaption("Vše");
        selector.setEmptySelectionAllowed(true);
        return selector;
    }

    private String getReportFileName(PrintPreviewReport.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    public void generateAndShowReport() {
        deactivateListeners();
        report.setSubtitleText(
                "Parametry: Arch=" + (null == archFilterField.getValue() ? "Vše" : archFilterField.getValue().toString()) +
                "  ČK/ČZ=" + (null == ckzFilterField.getValue() ? "Vše" : "*" + ckzFilterField.getValue() + "*") +
                "  Rok zak.=" + (null == rokZakFilterField.getValue() ? "Vše" : rokZakFilterField.getValue().toString()) +
                "  Skupina=" + (null == skupinaFilterField.getValue() ? "Vše" : skupinaFilterField.getValue().toString()) +
                "  rx=" + (null == rxParamField.getValue() ? "" : rxParamField.getValue().toString()) +
                "  ry=" + (null == ryParamField.getValue() ? "" : ryParamField.getValue().toString()) +
                "  Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue()) +
                "  Pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue()) +
                "  Kurz CZK/EUR=" + (null == kurzParamField.getValue() ? "" : kurzParamField.getValue())
        );

        // Tohle nefunguje:
        // report.getReportBuilder().setProperty("ireport.zoom", "2.0");
        // report.getReportBuilder().setProperty("net.sf.jasperreports.viewer.zoom", "2");

        report.setItems(itemsSupplier.get());   // ..also builds report if has not been built yet

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
}
