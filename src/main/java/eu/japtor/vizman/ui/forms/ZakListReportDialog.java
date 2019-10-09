package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.report.ZakListReport;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import org.vaadin.reports.PrintPreviewReport;

import java.util.Comparator;
import java.util.List;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakListReportDialog extends AbstractPrintDialog<Zak> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "750px";

//    private final static String DELETE_STR = "Zrušit";
//    private final static String REVERT_STR = "Vrátit změny";
    private final static String REVERT_AND_CLOSE_STR = "Zpět";
//    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak-list";

//    private Button revertButton;
//    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
//    private Button deleteAndCloseButton;
    private HorizontalLayout leftBarPart;
//    private Button fakturovatButton;
//    private Button stornoButton;

//    private TextField zakEvidField;
//    private TextField cfaktField;
//    private TextField textField;
//    private TextField castkaField;
//    private DatePicker dateDuzpField;
//    private DatePicker dateVystavField;
//    private TextField dateTimeExportField;
//    private TextField faktCisloField;

//    private Binder<Fakt> binder = new Binder<>();
//    private Fakt currentItem;
//    private Fakt origItem;
//    private Operation currentOperation;
//    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;

//    private Registration binderChangeListener = null;

//    @Autowired
    private ZakService zakService;

//    private ZakListReport<Zak> report;
    private ZakListReport report;
    private HorizontalLayout expAnchorsBox;
    private HorizontalLayout reportParamBox;
//    private HorizontalLayout zakInfoBox;
    Select<Integer> rokFilterField;

    private SerializableSupplier<List<? extends Zak>> itemsSupplier = () -> {
        if (null == rokFilterField.getValue()) {
            return zakService.fetchAllDescOrder();
        } else {
            return zakService.fetchByRokDescOrder(rokFilterField.getValue());
        }
    };


    public ZakListReportDialog(ZakService zakService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("REPORT: Zakázky");
//        getHeaderEndBox().setText("END text");
        this.zakService = zakService;
        initReportControls();
    }


    public void openDialog() {
        showReport();
        this.open();
    }

//    private void closeDialog() {
//        this.close();
//    }


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

//        getReportInfoBox().add(
//                initZakInfoBox
//        );

        reportParamBox = new HorizontalLayout();
        reportParamBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        reportParamBox.add(
                genButton
                , buildRokFilterComponent()
        );

        getReportToolBar().add(
                reportParamBox
                , expAnchorsBox
        );
        report = new ZakListReport();

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
//        report.setParamRok(rokFilterField.getStringValue());
        report.getReportBuilder().setSubtitle(
                "Parametry: Rok=" + (null == rokFilterField.getValue() ? "Vše" : rokFilterField.getValue().toString())
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

    private void showReport() {

//        report = ZakListReport.getReportPreview();

//        this.getReportPanel().removeAllContent();
//        this.getReportPanel().addContent(report);

//        FastReportBuilder drb = new FastReportBuilder();
//        try {
//            drb.addColumn("CZ", "czak", Integer.class.getName(), 10)
//                    .addColumn("Text zakazky", "text", String.class.getName(), 40)
//                    .addColumn("Skup.", "skupina", String.class.getName(), 4)
//                    //                .addGroups(2)
//                    .setTitle("Zakazky - testovaci vypis")
//                    .setSubtitle("This report was generated at " + new Date())
//                    //                .setPrintBackgroundOnOddRows(true)
//                    .setUseFullPageWidth(true);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        JRDataSource jds = new JRBeanCollectionDataSource(zakService.fetchAll());
//        try {
//            JasperPrint jp = DynamicJasperHelper.generateJasperPrint(drb.build(), new ClassicLayoutManager(), jds);
//
//            JasperViewer jasperViewer = new JasperViewer(jp, false);
//            jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
//            jasperViewer.setTitle("TEST report");
//            jasperViewer.setZoomRatio((float) 1.25);
//            jasperViewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH);
//            //            jasperViewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
//            jasperViewer.setVisible(true);
//            jasperViewer.requestFocus();
//
//            //            JasperViewer.viewReport(jp, false);
//
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//        //            JasperPrint jPrint = JasperFillManager.fillReport(jRep, null, jDataSource);
//        //
//        ////            OutputStream os = new FileOutputStream(new File("d:\\reports"));
//        ////            JasperExportManager.exportReportToPdfStream(jp, os);
//        //
//        //        } catch (Exception e) {
//        //            e.printStackTrace();
//        //        }
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

//    private void revertClicked(boolean closeAfterRevert) {
////        revertFormChanges();
//        if (closeAfterRevert) {
//            closeDialog();
//        } else {
////            initControlsOperability();
//        }
//    }

//    private void saveClicked(boolean closeAfterSave) {
//        if (!isFaktValid()) {
//            return;
//        }
//        try {
//            currentItem = saveFakt(currentItem);
//            if (closeAfterSave) {
//                closeDialog();
//            } else {
//                initFaktDataAndControls(currentItem, currentOperation);
//            }
//        } catch (VzmServiceException e) {
//            showSaveErrMessage();
//        }
//    }
//
//    private void deleteClicked() {
//        String ckzfDel = String.format("%s / %s / %s", currentItem.getCkont(), currentItem.getCkz(), currentItem.getCfakt());
//        if (!canDeleteFakt(currentItem)) {
//            ConfirmDialog
//                    .createInfo()
//                    .withCaption("Zrušení plnění")
//                    .withMessage(String.format("Plnění %s nelze zrušit.", ckzfDel))
//                    .open()
//            ;
//            return;
//        }
//        try {
//            revertFormChanges();
//            ConfirmDialog.createQuestion()
//                    .withCaption("Zrušení plnění")
//                    .withMessage(String.format("Zrušit plnění %s ?", ckzfDel))
//                    .withOkButton(() -> {
//                                if (deleteFakt(currentItem)) {
//                                    closeDialog();
//                                }
//                            }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
//                    )
//                    .withCancelButton(ButtonOption.caption("ZPĚT"))
//                    .open()
//            ;
//        } catch (VzmServiceException e) {
//            showDeleteErrMessage();
//        }
//    }

//    private void showSaveErrMessage() {
//        ConfirmDialog.createError()
//                .withCaption("Editace plnění")
//                .withMessage("Plnění se nepodařilo uložit.")
//                .open();
//    }
//
//    private void showDeleteErrMessage() {
//        ConfirmDialog.createError()
//                .withCaption("Editace plnění")
//                .withMessage("Plnění se nepodařilo zrušit")
//                .open()
//        ;
//    }

//  --------------------------------------------
}
