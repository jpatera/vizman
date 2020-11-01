package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.report.ZakReport;
import eu.japtor.vizman.ui.components.AbstractPrintDialog;
import eu.japtor.vizman.ui.components.ReportExpAnchor;
import org.vaadin.reports.PrintPreviewReport;

import java.util.Arrays;
import java.util.List;


public class ZakReportDialog extends AbstractPrintDialog<Zak> implements HasLogger {

    public static final String DIALOG_WIDTH = "1200px";
    public static final String DIALOG_HEIGHT = "750px";
    private static final String CLOSE_STR = "Zavřít";

    private final static String REPORT_FILE_NAME = "vzm-rep-zak";

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Button closeButton;

//    private ZakService zakService;
    private Zak zak;
    private ZakReport report;
    private HorizontalLayout expAnchorsBox;


    private SerializableSupplier<List<? extends Fakt>> itemsSupplier = () -> {
//          zakNaklVwService.fetchByZakId(zakr.getId(), zakrParams)
//        return zakService.fetchRepDochYearForPersonAndYear(dochParams.getPersonId(), dochParams.getDochYear());
//        return Arrays.asList(zak);
        return zak.getFakts();
    };

//    private SerializableSupplier<List<? extends Zak>> itemsSupplier = () -> {
////          zakNaklVwService.fetchByZakId(zakr.getId(), zakrParams)
//            return zakService.fetchRepDochYearForPersonAndYear(dochParams.getPersonId(), dochParams.getDochYear());
//        }
//    ;
//

    public ZakReportDialog() {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setDialogTitle("Report: ZAKÁZKA");
//        getHeaderRightBox().setText("END text");
//        this.zakService = zakService;
        initReportControls();
    }

//    public void openDialog(DochView.DochParams dochParams) {
    public void openDialog(Zak zak) {
        this.zak = zak;
        this.open();
    }


    private void closeDialog() {
        this.close();
    }

    private void closeClicked() {
        closeDialog();
    }


    private void initReportControls() {

        expAnchorsBox = new HorizontalLayout();
        expAnchorsBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        for (PrintPreviewReport.Format format : VZM_SUPPORTED_EXP_FORMATS) {
            expAnchorsBox.add(new ReportExpAnchor(format));
        }

        getHeaderEndBox().add(
                expAnchorsBox
        );

        report = new ZakReport();
    }


    private String getReportFileName(PrintPreviewReport.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

    public void generateAndShowReport() {
        deactivateListeners();

        report = new ZakReport();
        report.getReportBuilder()
                .setTitle(zak.getKzCisloTextForRep())
                .setSubtitle(zak.getText())
        ;

//        report.setItems(itemsSupplier.get());
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
