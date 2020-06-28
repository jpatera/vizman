package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.VaadinSession;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.report.ZakNaklXlsReportBuilder;
import eu.japtor.vizman.backend.service.ZaknService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.AbstractGridDialog;
import eu.japtor.vizman.ui.components.ReportExpButtonAnchor;
import eu.japtor.vizman.ui.components.ReportExporter;
import eu.japtor.vizman.ui.components.Ribbon;
import eu.japtor.vizman.ui.views.ZakrListView;
import net.sf.jasperreports.engine.JRException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static eu.japtor.vizman.app.security.SecurityUtils.isNaklCompleteAccessGranted;

public class ZakNaklGridDialog extends AbstractGridDialog<Zakn> implements HasLogger {

    public static final String DIALOG_WIDTH = "900px";
//    public static final String DIALOG_HEIGHT = "850px";
    public static final String DIALOG_HEIGHT = null;
    private static final String CLOSE_STR = "Zavřít";

    public static final String HODIN_COL_KEY = "zakn-bg-hodin";
    public static final String MZDA_COL_KEY = "zakn-bg-mzda";
    public static final String MZDA_POJ_COL_KEY = "zakn-bg-mzda-poj";

    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Button closeButton;
    private Anchor expXlsAnchor;
    private ReportExporter<Zakn> xlsReportExporter;

    private TextField rezieParamField;
    private Label rezieParamLabel;
    private TextField pojistParamField;
    private Label pojistParamLabel;
    private ZakrListView.ZakrParams zakrParams;
    private Binder<ZakrListView.ZakrParams> paramsBinder;

    private List<Zakn> currentItemList;
    private Zakr zakr;
    private Label zakInfoBox;

    Grid<Zakn> grid;
    private FooterRow sumFooterRow;

    private ZakNaklReportDialog zakNaklRepDialog;
    private ZaknService zaknService;

    private Long zakId;
    private String ckzTextFull;

    private final static String REPORT_FILE_NAME = "vzm-exp-zakn";

    private SerializableSupplier<List<? extends Zakn>> itemsSupplier = () ->
            zaknService.fetchByZakIdSumByYm(zakId, zakrParams)
    ;
    private ComponentEventListener anchorExportListener = event -> {
        try {
            updateExpXlsAnchorResource(itemsSupplier);
        } catch (JRException e) {
            e.printStackTrace();
        }
    };


    /**
     * Constructor
     *
     * @param zaknService
     */
    public ZakNaklGridDialog(ZaknService zaknService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.UNKNOWN);
        getMainTitle().setText("NÁKLADY NA ZAKÁZKU");

        this.zaknService = zaknService;
        this.paramsBinder = new Binder<>();
        xlsReportExporter = new ReportExporter(
                (new ZakNaklXlsReportBuilder(SecurityUtils.isNaklCompleteAccessGranted())).buildReport()
        );

        zakNaklRepDialog = new ZakNaklReportDialog(zaknService);

        getGridInfoBar().add(
                initZakInfoBox()
        );

        getGridToolBar().add(
                buildGridBarComponent()
        );

        getGridContainer().add(
                initGrid()
        );
    }

    private Component initZakInfoBox() {
        zakInfoBox = new Label();
        zakInfoBox.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em");
        return zakInfoBox;
    }

    public void openDialogFromZakBasicView(ZakBasic zakBasic, ZakrListView.ZakrParams zakrParams) {
//        this.zakId = zakBasic.getId();
//        this.ckzTextFull = zakBasic.getCkzTextFull();
        this.zakrParams = zakrParams;
        openDialog(zakBasic.getId(), ckzTextFull);
    }

    public void openDialogFromZakr(Zakr zakr, ZakrListView.ZakrParams zakrParams) {
//        this.zakId = zakr.getId();
//        this.ckzTextFull = zakr.getCkzTextFull();
        this.zakrParams = zakrParams;
        openDialog(zakr.getId(), ckzTextFull);
    }

    private void openDialog(Long zakId, String ckzTextFull) {
        this.zakId = zakId;
        this.ckzTextFull = ckzTextFull;
        paramsBinder.setBean(zakrParams);
//        paramsBinder.addValueChangeListener(event -> calcButton.setIconDirty());
        initDataAndControls();
        this.open();
    }

    private void closeDialog() {
        this.close();
    }


    // Title for grid bar
    // -------------------------------------------------------
    private Component buildTitleComponent() {
        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
//        titleComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        titleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        titleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        titleComponent.add(
//                new GridTitle(ItemNames.getNomP(ItemType.ZAKR))
                initTitle()
                , new Ribbon()
//                , new ReloadButton(event -> {
//                    reloadViewContentPreserveFilters();
//                })
        );
        return titleComponent;
    }

    private Component buildGridBarControlsComponent() {
        HorizontalLayout controlsComponent = new HorizontalLayout();
        controlsComponent.setMargin(false);
        controlsComponent.setPadding(false);
        controlsComponent.setSpacing(false);
        controlsComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        controlsComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        controlsComponent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        controlsComponent.add(
                initRezieLabel()
                , initRezieField()
                , new Ribbon("3em")
                , initPojistLabel()
                , initPojistField()
        );
        return controlsComponent;
    }

    private Component initRezieField() {
//        rezieParamField = new TextField("Koef. režie");
        rezieParamField = new TextField();
        rezieParamField.setWidth("5em");
        rezieParamField.setReadOnly(true);
        rezieParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        rezieParamField.getElement().setAttribute("theme", "small");
        rezieParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(rezieParamField)
                .asRequired("Koeficient režie musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalPercent2Converter)
                .bind(ZakrListView.ZakrParams::getKoefRezie, ZakrListView.ZakrParams::setKoefRezie)
        ;
        return rezieParamField;
    }

    private Component initRezieLabel() {
        rezieParamLabel = new Label("Koef. režie");
        rezieParamLabel.setWidth("5em");
        rezieParamLabel.getElement().setAttribute("theme", "small");
//        rezieParamLabel.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        return rezieParamLabel;
    }

    private Component initPojistField() {
//        pojistParamField = new TextField("Koef. poj.");
        pojistParamField = new TextField();
        pojistParamField.setWidth("5em");
        pojistParamField.setReadOnly(true);
        pojistParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        pojistParamField.getElement().setAttribute("theme", "small");
        pojistParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(pojistParamField)
                .asRequired("Koeficient pojištění musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalPercent2Converter)
                .bind(ZakrListView.ZakrParams::getKoefPojist, ZakrListView.ZakrParams::setKoefPojist)
        ;
        return pojistParamField;
    }

    private Component initPojistLabel() {
        pojistParamLabel = new Label("Koef. poj.");
        pojistParamLabel.setWidth("5em");
        pojistParamLabel.getElement().setAttribute("theme", "small");
//        rezieParamLabel.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        return pojistParamLabel;
    }

    private Component initTitle() {
        H4 zakTitle = new H4();
//        zakTitle.setText(ItemNames.getNomP(ItemType.WAGE));
        zakTitle.setText("NÁKLADŮ");
        zakTitle.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-right", "1em");
        return zakTitle;
    }
    // -------------------------------------------------------



    private Component initZakNaklXlsExpAnchor() {
        expXlsAnchor = new ReportExpButtonAnchor(ReportExporter.Format.XLS, anchorExportListener);
        return expXlsAnchor;
//        zakNaklExpButton = new Button("Export"
//                , event -> {
//            openZakNaklRepDialog();
//        });
////        this.addClassName("view-toolbar__button");
//        zakNaklExpButton.getElement().setAttribute("theme", "small secondary");
//        return zakNaklExpButton;
    }

    private String getReportFileName(ReportExporter.Format format) {
        return REPORT_FILE_NAME + "." + format.name().toLowerCase();
    }

//    private void updateExpXlsAnchorResource(List<Zakn> items) throws JRException {
    private void updateExpXlsAnchorResource(SerializableSupplier<List<? extends Zakn>> supplier) throws JRException {
        ReportExporter.Format expFormat = ReportExporter.Format.XLS;
        AbstractStreamResource xlsResource =
                xlsReportExporter.getStreamResource(getReportFileName(expFormat), supplier, expFormat);
        expXlsAnchor.setHref(xlsResource);

        // Varianta 1
        UI.getCurrent().getPage().executeJs("$0.click();", expXlsAnchor.getElement());

//        // Varianta 2 - Has an issue: after returning to the parent dialog [Close] button does nothing
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }


//    private Button zakNaklRepButton;
//
//    private Component initZakNaklRepButton() {
//        zakNaklRepButton = new Button("Report"
//                , event -> {
//            openZakNaklRepDialog();
//        });
////        this.addClassName("view-toolbar__button");
//        zakNaklRepButton.getElement().setAttribute("theme", "small secondary");
//        return zakNaklRepButton;
//    }

    private void openZakNaklRepDialog() {
//        zakrParams.setRokZak(zakrGrid.getRokFilterValue());
//        zakrParams.setSkupina(zakrGrid.getSkupinaFilterValue());
        zakNaklRepDialog.openDialog(zakr, zakrParams);
        zakNaklRepDialog.generateAndShowReport();
    }


    private void initDataAndControls() {
        deactivateListeners();

        zakInfoBox.setText(ckzTextFull);

        this.currentItemList = zaknService.fetchByZakId(zakId, zakrParams);
        grid.setItems(currentItemList);
        updateFooterFields();
        initControlsOperability();

        activateListeners();
    }


//    private void refreshHeaderMiddleBox(Zak zakItem) {
//    // Do nothing
//    }

    private void deactivateListeners() {
//        if (null != binderChangeListener) {
//            try {
//                binderChangeListener.remove();
//            } catch (Exception e)  {
//                // do nothing
//            }
//        }
    }

    private void activateListeners() {
    }


    private void initControlsOperability() {
        closeButton.setEnabled(true);
    }

    private void adjustControlsOperability(final boolean hasChanges) {
        closeButton.setEnabled(true);
    }


    private Component buildGridBarComponent() {
        HorizontalLayout gridBarComp = new HorizontalLayout();
        gridBarComp.setSpacing(false);
        gridBarComp.setWidthFull();
//        gridBar.setPadding(false);
//        gridBar.setAlignItems(FlexComponent.Alignment.END);
        gridBarComp.setAlignItems(FlexComponent.Alignment.BASELINE);
        gridBarComp.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        gridBar.getStyle().set("marginTop", "0.2em");
//        gridBar.getStyle().set("margin-left", "-3em");
        gridBarComp.add(
//                buildTitleComponent()
//                , new Ribbon()
                buildGridBarControlsComponent()
                , new Ribbon()
                , buildToolBarComponent()
        );
        return gridBarComp;
    }


    private Component buildToolBarComponent() {
        HorizontalLayout toolBar = new HorizontalLayout();
        toolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        toolBar.setSpacing(false);
        toolBar.add(
                initZakNaklXlsExpAnchor()
//                , new Ribbon()
///                , initZakNaklRepButton()
        );
        return toolBar;
    }

    private Component initGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setId("person-wage-grid");
        grid.setClassName("vizman-simple-grid");
        grid.getStyle().set("marginTop", "0.5em");

        grid.addColumn(zakn -> zakn.getPerson().getPrijmeni())
                .setHeader("Příjmení")
                .setResizable(true)
                .setWidth("10em")
                .setFlexGrow(0)
        ;
        grid.addColumn(Zakn::getDatePruh)
                .setHeader("Datum")
                .setFlexGrow(0)
        ;
//        grid.addColumn(Zakn::getYmPruh)
//                .setHeader("Rok-měs")
//                .setFlexGrow(0)
//        ;
        grid.addColumn(Zakn::getWorkPruh)
                .setHeader("Hodin")
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(HODIN_COL_KEY)
                .setResizable(true)
        ;
        if (isNaklCompleteAccessGranted()) {
            grid.addColumn(zn -> {
                BigDecimal mzda = zn.getNaklMzda();
                return null == mzda ? "" : VzmFormatUtils.moneyNoFractFormat.format(mzda);
            })
                    .setHeader("Mzda")
                    .setWidth("8em")
                    .setTextAlign(ColumnTextAlign.END)
                    .setFlexGrow(0)
                    .setKey(MZDA_COL_KEY)
                    .setResizable(true)
            ;
            grid.addColumn(zn -> {
                BigDecimal mzdaPojist = zn.getNaklMzdaPojist();
                return null == mzdaPojist ? "" : VzmFormatUtils.moneyNoFractFormat.format(mzdaPojist);
            })
                    .setHeader("Mzda+Poj.")
                    .setWidth("8em")
                    .setTextAlign(ColumnTextAlign.END)
                    .setFlexGrow(0)
                    .setKey(MZDA_POJ_COL_KEY)
                    .setResizable(true)
            ;
            grid.addColumn(Zakn::getSazba)
                    .setHeader("Sazba")
                    .setWidth("8em")
                    .setTextAlign(ColumnTextAlign.END)
                    .setFlexGrow(0)
                    .setResizable(true)
            ;
        }

        sumFooterRow = grid.appendFooterRow();
        updateFooterFields();

        return grid;
    }

    private void updateFooterFields() {
        updateHodinSumField();
        if (isNaklCompleteAccessGranted()) {
            updateMzdaSumField();
            updateMzdaPojistSumField();
        }
    }

//    private void updateRecCountField() {
//        sumFooterRow
//                .getCell(this.getColumnByKey(CKZ_COL_KEY))
//                .setText("Počet: " + (calcRecCount()));
//    }

    private void updateHodinSumField() {
        sumFooterRow
                .getCell(grid.getColumnByKey(HODIN_COL_KEY))
                .setText("" + (VzmFormatUtils.decHodFormat.format(calcHodinSum())));
    }

    private void updateMzdaSumField() {
            sumFooterRow
                    .getCell(grid.getColumnByKey(MZDA_COL_KEY))
                    .setText("" + (VzmFormatUtils.moneyNoFractFormat.format(calcMzdaSum())));
    }

    private void updateMzdaPojistSumField() {
        sumFooterRow
                .getCell(grid.getColumnByKey(MZDA_POJ_COL_KEY))
                .setText("" + (VzmFormatUtils.moneyNoFractFormat.format(calcMzdaPojistSum())));
    }

    private BigDecimal calcHodinSum() {
        return grid.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zn -> zn.getWorkPruh() != null)
//                .map(zakr -> EUR == zakr.getMena() ? zakr.getRpZbyva().multiply(zakrParams.getKurzEur()) : zakr.getRpZbyva())
                .map(zn -> zn.getWorkPruh())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdaSum() {
        return grid.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zn -> zn.getNaklMzda() != null)
//                .map(zakr -> EUR == zakr.getMena() ? zakr.getRpZbyva().multiply(zakrParams.getKurzEur()) : zakr.getRpZbyva())
                .map(zn -> zn.getNaklMzda())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdaPojistSum() {
        return grid.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zn -> zn.getNaklMzdaPojist() != null)
                .map(zn -> zn.getNaklMzdaPojist())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
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

    private void closeClicked() {
        closeDialog();
    }
//  --------------------------------------------

    @Override
    public List<Zakn> getCurrentItemList() {
        return currentItemList;
    }
}
