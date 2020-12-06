package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import eu.japtor.vizman.app.CfgPropName;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.report.ZakNaklSouhrnXlsReportBuilder;
import eu.japtor.vizman.backend.report.ZakRozpracSumXlsReportBuilder;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.backend.utils.VzmUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.RFNDF;
import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_ZAKR_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAKR_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_ROZPRAC_READ, Perm.ZAK_ROZPRAC_MODIFY
})
public class ZakrListView extends VerticalLayout {

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archivované";
    private static final String RADIO_KONT_ALL = "Všechny";

    private static final String REP_ZAKR_SUM_FILE_NAME = "vzm-rep-zakr-sum";
    private static final String REP_ZAKN_SUM_FILE_NAME = "vzm-rep-zakn-sum";
    private static final String REP_ZAKN_SOUHRN_FILE_NAME = "vzm-rep-zakn-souhrn";
    private static final String ZAKR_DOWN_ANCHOR_ID = "zakr-down-anchor-id";

    private List<Zakr> zakrList;
    private ZakRozpracGrid zakrGrid;
    private List<GridSortOrder<ZakBasic>> initialSortOrder;

    private RadioButtonGroup<String> archFilterRadio;
    private CalcButton calcButton;
    private TextField kurzParamField;
    private TextField rezieParamField;
    private TextField pojistParamField;
    private Select<String> rxParamField;
    private Select<String> ryParamField;
    private Checkbox activeParamField;
    private ZakrParams zakrParams;
    private Binder<ZakrParams> paramsBinder;

    private ReportXlsExporter<Zakr> rozpracSumXlsRepExporter;
    private ReportXlsExporter<ZakYmNaklVw> naklSouhrnXlsRepExporter;
    private Anchor expXlsAnchor;

    @Autowired
    public ZakrService zakrService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public FaktService faktService;

    @Autowired
    public ZakNaklVwService zakNaklVwService;

    @Autowired
    public CfgPropsCache cfgPropsCache;

    public ZakrListView() {
        rozpracSumXlsRepExporter = new ReportXlsExporter<>();
        naklSouhrnXlsRepExporter = new ReportXlsExporter<>();
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//        setHeight("90%");
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setPadding(false);
        this.setMargin(false);
        this.getStyle().set("marginTop", "0.1em");
//        this.add(
//                initGridContainer()
//        );
    }

    @PostConstruct
    public void postInit() {
        zakrParams = ZakrParams.getDefaultInstance(cfgPropsCache);
        paramsBinder = new Binder<>();
        paramsBinder.setBean(zakrParams);
        paramsBinder.addValueChangeListener(event -> calcButton.setIconDirty());

        this.add(
                initGridContainer(zakrParams)
        );
        loadInitialViewContent();
        // TODO: inital sort order markers
        //        zakrGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }

    private Component buildGridBarComponent() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
//        gridBar.setAlignItems(Alignment.END);
        gridBar.setAlignItems(Alignment.BASELINE);
        gridBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        gridBar.add(
                buildTitleComponent()
                , new Ribbon()
                , buildGridBarControlsComponent()
                , new Ribbon()
                , buildReportBtnBox()
        );
        return gridBar;
    }

    private Component buildTitleComponent() {
        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
//        titleComponent.setAlignItems(FlexComponent.Alignment.CENTER);
        titleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        titleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        titleComponent.add(
                new GridTitle(ItemNames.getNomP(ItemType.ZAKR))
                , new Ribbon()
                , new ReloadButton(event -> {
                    reloadViewContentPreserveFilters();
//                    loadGridDataAndRebuildFilterFields();
                })
//                , new ReloadButton(event -> loadInitialViewContent())
        );
        return titleComponent;
    }

    private Component buildGridBarControlsComponent() {
        HorizontalLayout controlsComponent = new HorizontalLayout();
        controlsComponent.setMargin(false);
        controlsComponent.setPadding(false);
        controlsComponent.setSpacing(false);
        controlsComponent.setAlignItems(Alignment.CENTER);
        controlsComponent.setJustifyContentMode(JustifyContentMode.START);
        controlsComponent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        controlsComponent.add(
                initActiveParamField()
                , new Ribbon("3em")
                , initRxParamField()
                , new Ribbon()
                , initRyParamField()
                , new Ribbon("3em")
                , initCalcButton()
                , new Ribbon("3em")
                , initKurzField()
                , new Ribbon("3em")
                , initRezieField()
                , new Ribbon("3em")
                , initPojistField()
        );

        return controlsComponent;
    }

    private Component initKurzField() {
        kurzParamField = new TextField("Kurz CZK/EUR");
        kurzParamField.setWidth("7em");
        kurzParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        kurzParamField.getElement().setAttribute("theme", "small");
//        kurzParamField.getStyle().set("theme", "icon secondary small");
        kurzParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(kurzParamField)
                .asRequired("Kurz musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalKurzConverter)
                .bind(ZakrParams::getKurzEur, ZakrParams::setKurzEur)
        ;
        return kurzParamField;
    }

    private Component initRezieField() {
        rezieParamField = new TextField("Koef. režie");
        rezieParamField.setWidth("5em");
        rezieParamField.setReadOnly(true);
        rezieParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        rezieParamField.getElement().setAttribute("theme", "small");
//        rezieParamField.getStyle().set("theme", "small");
        rezieParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(rezieParamField)
                .asRequired("Koeficient režie musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalPercent2Converter)
                .bind(ZakrParams::getKoefRezie, ZakrParams::setKoefRezie)
        ;
        return rezieParamField;
    }

    private Component initPojistField() {
        pojistParamField = new TextField("Koef. poj.");
        pojistParamField.setWidth("5em");
        pojistParamField.setReadOnly(true);
        pojistParamField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        pojistParamField.getElement().setAttribute("theme", "small");
//        pojistParamField.getStyle().set("theme", "small");
        pojistParamField.setValueChangeMode(ValueChangeMode.EAGER);
        paramsBinder.forField(pojistParamField)
                .asRequired("Koeficient pojištění musí být zadán")
                .withConverter(VzmFormatUtils.bigDecimalPercent2Converter)
                .bind(ZakrParams::getKoefPojist, ZakrParams::setKoefPojist)
        ;
        return pojistParamField;
    }

    private Select<String> initRxParamField() {
        rxParamField = new Select<>();
        rxParamField.setLabel("RX (od)");
        rxParamField.setWidth("5em");
        rxParamField.getElement().setAttribute("theme", "small");
//        rxParamField.getStyle().set("theme", "small");
        rxParamField.setEmptySelectionAllowed(true);
        rxParamField.setItems("R-4", "R-3", "R-2", "R-1", "R0", "R1", "R2", "R3", "R4");

        paramsBinder.forField(rxParamField)
                .bind(ZakrParams::getRx, ZakrParams::setRx)
        ;
        return rxParamField;
    }

    private Select<String> initRyParamField() {
        ryParamField = new Select<>();
        ryParamField.setLabel("RY (do)");
        ryParamField.setWidth("5em");
        ryParamField.getElement().setAttribute("theme", "small");
//        ryParamField.getStyle().set("theme", "small");
        ryParamField.setEmptySelectionAllowed(true);
        ryParamField.setItems("R-4", "R-3", "R-2", "R-1", "R0", "R1", "R2", "R3", "R4");

        paramsBinder.forField(ryParamField)
                .bind(ZakrParams::getRy, ZakrParams::setRy)
        ;
        return ryParamField;
    }

    private Checkbox initActiveParamField() {
        activeParamField = new Checkbox("Aktivní"); // = new TextField("Username");
        activeParamField.getElement().setAttribute("theme", "small");
        paramsBinder.forField(activeParamField)
                .bind(ZakrParams::isActive, ZakrParams::setActive);
        return activeParamField;
    }

    private Component initCalcButton() {
        calcButton = new CalcButton(event -> {
            BinderValidationStatus status = paramsBinder.validate();
            if (status.hasErrors()) {
                calcButton.setIconDirty();
            } else {
//                feedZakrParms();    // TODO: May be not necessary
                paramsBinder.writeBeanIfValid(zakrParams);
                zakrList = zakrService.fetchAndCalcByFiltersDescOrder(buildZakrFilter(), zakrParams);
                zakrGrid.populateGridDataAndRestoreFilters(zakrList);
                zakrGrid.recalcGrid();
                zakrGrid.getDataProvider().refreshAll();
                calcButton.setIconClean();
            }
        });
        calcButton.getElement().setAttribute("theme", "small");
        return calcButton;
    }

    private String getZakrSumReportFileName(ReportXlsExporter.Format format) {
        return REP_ZAKR_SUM_FILE_NAME + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private String getNaklSumReportFileName(ReportXlsExporter.Format format) {
        return REP_ZAKN_SUM_FILE_NAME + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private String getNaklSouhrnRepFileName(ReportXlsExporter.Format format) {
        return REP_ZAKN_SOUHRN_FILE_NAME + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private List<Zakr> getSelectedZakrForReport(final String zakrId) {
//        return zakrService.fetchTopById(zakrId);
        return Collections.emptyList();
    }

    private SerializableSupplier<List<? extends Zakr>> zakRozpracCurrentSumRepSupplier =
            () -> {
//                feedZakrParms();    // TODO: May be not necessary
                paramsBinder.writeBeanIfValid(zakrParams);
                Set<Zakr> itemSelection = zakrGrid.getSelectedItems();  // Suppose: SingleSelectionModel is set, only one (or none) item is present
                if (null == itemSelection || itemSelection.isEmpty()) {
                    return Collections.EMPTY_LIST;
                }
                Zakr zakrSel = itemSelection.iterator().next();
                return Collections.singletonList(
                        zakrService.fetchAndCalcOne(zakrSel.getId(), zakrParams)
                );
            };

    private SerializableSupplier<List<? extends Zakr>> zakRozpracRepFilteredSupplier =
            () -> {
//                feedZakrParms();    // TODO: May be not necessary
                paramsBinder.writeBeanIfValid(zakrParams);
                return zakrService.fetchAndCalcByFiltersDescOrder(buildZakrFilter(), zakrParams);
            };

    private SerializableSupplier<List<? extends Zakr>> zakRozpracRepAllSupplier =
            () -> {
//                feedZakrParms();    // TODO: May be not necessary
                paramsBinder.writeBeanIfValid(zakrParams);
                return zakrService.fetchAndCalcAllDescOrder(zakrParams);
            };

    private SerializableSupplier<List<? extends ZakYmNaklVw>> zakYmNaklSouhrnRepVisibleSupplier =
            () -> {
//                feedZakrParms();    // TODO: May be not necessary
                paramsBinder.writeBeanIfValid(zakrParams);
                List<Long> zakrIds = zakrService.fetchIdsByFiltersDescOrderWithLimit(buildZakrFilter(), zakrParams);
                return zakNaklVwService.fetchByZakIdsSumByYm(zakrIds, zakrParams);
            };

    private Component buildReportBtnBox() {
        HorizontalLayout reportBtnBox = new HorizontalLayout();
        reportBtnBox.setMargin(false);
        reportBtnBox.setPadding(false);
        reportBtnBox.setSpacing(false);
//        reportBtnBox.setAlignItems(FlexComponent.Alignment.CENTER);
        reportBtnBox.setAlignItems(FlexComponent.Alignment.BASELINE);
        reportBtnBox.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        reportBtnBox.add(
                initNaklReportButtonMenu()
                , new Ribbon("0.5em")
                , initRozpracReportButtonMenu()
        );
        return reportBtnBox;
    }

    private Component initNaklReportButtonMenu() {
        Icon ico = new Icon(VaadinIcon.COIN_PILES);
        ico.setColor("purple");
        Button btn = new Button(ico);
        btn.getElement().setAttribute("theme", "icon secondary small");
        btn.getElement().setProperty("title", "Souhrnné náklady zakázek - report");

        ContextMenu menu = new ContextMenu();
        menu.addItem("Zobrazené zakázky (max 20)", e -> updateNaklSouhrnXlsRepResourceAndDownload(
                zakYmNaklSouhrnRepVisibleSupplier
                , getZakrRepFilteredSubtitleText()
        ));

        menu.setOpenOnClick(true);
        menu.setTarget(btn);
        return btn;
    }

    private Component initRozpracReportButtonMenu() {
        Icon ico = new Icon(VaadinIcon.LINES_LIST);
        ico.setColor("blue");
        Button btn = new Button(ico);
        btn.getElement().setAttribute("theme", "icon secondary small");
        btn.getElement().setProperty("title", "Rozpracovanost zakázek - report");

        ContextMenu menu = new ContextMenu();
        menu.addItem("Zobrazené zakázky", e -> updateRozpracSumXlsRepResourceAndDownload(
                zakRozpracRepFilteredSupplier
                , getZakrRepFilteredSubtitleText()
        ));
        menu.addItem("Všechny zakázky", e -> updateRozpracSumXlsRepResourceAndDownload(
                zakRozpracRepAllSupplier
                , getZakrRepAllSubtitleText()
        ));
//        menu.addItem("Aktuální", e -> updateRozpracSumXlsRepResourceAndDownload(zakRozpracCurrentSumRepSupplier));

        menu.setOpenOnClick(true);
        menu.setTarget(btn);
        return btn;
    }

    private void updateRozpracSumXlsRepResourceAndDownload(
            SerializableSupplier<List<? extends Zakr>> itemsSupplier
            , final String subtitleText
    ) {
        final AbstractStreamResource xlsResource =
                rozpracSumXlsRepExporter.getXlsStreamResource(
                        new ZakRozpracSumXlsReportBuilder(subtitleText)
                        , getZakrSumReportFileName(ReportXlsExporter.Format.XLS)
                        , itemsSupplier
                        , null
                );

        // Varianta 1
        expXlsAnchor.setHref(xlsResource);
        UI.getCurrent().getPage().executeJs("$0.click();", expXlsAnchor.getElement());
//      or:  page.executeJs("document.getElementById('" + KONT_REP_ID + "').click();");

        // Varianta 2 - browsers can have pop-up opening disabled
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        Page page = UI.getCurrent().getPage();
//        page.executeJs("window.open($0, $1)", registration.getResourceUri().toString(), "_blank");

        // Varianta 3 - It is not clear how to activate source page again after download is finished
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        Page page = UI.getCurrent().getPage();
//        page.setLocation(registration.getResourceUri());
    }

    private void updateNaklSouhrnXlsRepResourceAndDownload(
            SerializableSupplier<List<? extends ZakYmNaklVw>> itemsSupplier
            , final String subtitleText
    ) {
        String[] sheetNames = itemsSupplier.get().stream()
                .filter(VzmUtils.distinctByKey(p -> p.getKzCisloRep()))
                .map(item -> item.getKzCisloRep())
                .toArray(String[]::new)
                ;
        final AbstractStreamResource xlsResource =
                naklSouhrnXlsRepExporter.getXlsStreamResource(
                        new ZakNaklSouhrnXlsReportBuilder(
                                "SOUHRNNÉ NÁKLADY NA ZAKÁZKY"
                                , subtitleText
                                , SecurityUtils.isNaklCompleteAccessGranted()
                        )
                        , getNaklSouhrnRepFileName(ReportXlsExporter.Format.XLS)
                        , itemsSupplier
                        , sheetNames
                );
        expXlsAnchor.setHref(xlsResource);
        Page page = UI.getCurrent().getPage();
        page.executeJs("$0.click();", expXlsAnchor.getElement());
    }

    private String getZakrRepFilteredSubtitleText() {
//        feedZakrParms();
        ZakrFilter zakrFilter = buildZakrFilter();
        return
            "Parametry: " +
            "  Aktivní=" + (null == zakrParams.isActive() ? "" : zakrParams.isActive().toString()) +
            "  Arch=" + (null == zakrFilter.getArch() ? "Vše" : zakrFilter.getArch().toString()) +
            "  ČK-ČZ=" + (null == zakrFilter.getCkz() ? "Vše" : zakrFilter.getCkz()) +
            "  Rok zak.=" + (null == zakrFilter.getRokZak() ? "Vše" : zakrFilter.getRokZak().toString()) +
            "  Skupina=" + (null == zakrFilter.getSkupina() ? "Vše" : zakrFilter.getSkupina().toString()) +
            "  Objednatel=" + (null == zakrFilter.getObjednatel() ? "Vše" : zakrFilter.getObjednatel().toString()) +
            "  Text=" + (null == zakrFilter.getKzText() ? "Vše" : zakrFilter.getKzText().toString()) +
            "  rx=" + (null == zakrParams.getRx() ? "" : zakrParams.getRx()) +
            "  ry=" + (null == zakrParams.getRy() ? "" : zakrParams.getRy()) +
            "  Koef.pojištění=" + (null == zakrParams.getKoefPojist() ? "" : zakrParams.getKoefPojist()) +
            "  Koef.režie=" + (null == zakrParams.getKoefRezie() ? "" : zakrParams.getKoefRezie()) +
            "  Kurz CZK/EUR=" + (null == zakrParams.getKurzEur() ? "" : zakrParams.getKurzEur());
    }

    private String getZakrRepAllSubtitleText() {
//        feedZakrParms();
        return
            "Parametry: Filtr=vše " +
            "  rx=" + (null == zakrParams.getRx() ? "" : zakrParams.getRx()) +
            "  ry=" + (null == zakrParams.getRy() ? "" : zakrParams.getRy()) +
            "  Koef.pojištění=" + (null == zakrParams.getKoefPojist() ? "" : zakrParams.getKoefPojist()) +
            "  Koef.režie=" + (null == zakrParams.getKoefRezie() ? "" : zakrParams.getKoefRezie()) +
            "  Kurz CZK/EUR=" + (null == zakrParams.getKurzEur() ? "" : zakrParams.getKurzEur());
    }

//    private void feedZakrParms() {
//        zakrParams.setKoefPojist(StringUtils.isEmpty(pojistParamField.getValue()) ?  null : new BigDecimal(pojistParamField.getValue()));
//        zakrParams.setKoefRezie(StringUtils.isEmpty(rezieParamField.getValue()) ?  null : new BigDecimal(rezieParamField.getValue()));
//        zakrParams.setKurzEur(StringUtils.isEmpty(kurzParamField.getValue()) ?  null : new BigDecimal(kurzParamField.getValue()));
//        zakrParams.setRx(rxParamField.getValue());
//        zakrParams.setRy(ryParamField.getValue());
//        zakrParams.setActive(activeParamField.getValue());
//    }

//    private void openRozpracRepDialog() {
////        List<Zakr> zakrRep1 = (List<Zakr>)((ListDataProvider)zakrGrid.getDataCommunicator().getDataProvider()).getItems();
////        List<Zakr> zakrRep2 = (List<Zakr>)((ListDataProvider)zakrGrid.getDataProvider()).getItems();
//        zakrGrid.saveFilterFieldValues();
//        zakrParams.setActive(activeParamField.getValue());
//        zakrParams.setArch(zakrGrid.getArchFilterValue());
//        zakrParams.setCkz(zakrGrid.getCkzFilterValue());
//        zakrParams.setRokZak(zakrGrid.getRokFilterValue());
//        zakrParams.setSkupina(zakrGrid.getSkupinaFilterValue());
//        ZakRozpracReportDialog zakRozpracReportDlg  = new ZakRozpracReportDialog(zakrService, zakrParams);
//        zakRozpracReportDlg.openDialog(zakrParams);
//        zakRozpracReportDlg.generateAndShowReport();
//    }

    private ZakrFilter buildZakrFilter() {
        zakrGrid.saveFilterFieldValues();
        ZakrFilter filter = ZakrFilter.getEmpty();
        filter.setArch(zakrGrid.getArchFilterValue());
        filter.setCkz(zakrGrid.getCkzFilterValue());
        filter.setRokZak(zakrGrid.getRokFilterValue());
        filter.setSkupina(zakrGrid.getSkupinaFilterValue());
        filter.setKzText(zakrGrid.getKzTextFilterValue());
        filter.setObjednatel(zakrGrid.getObjednatelFilterValue());
        return filter;
    }

    private Component initArchFilterRadio() {
        archFilterRadio = new RadioButtonGroup<>();
        archFilterRadio.setItems(RADIO_KONT_ACTIVE, RADIO_KONT_ARCH, RADIO_KONT_ALL);
//        buttonShowArchive.addValueChangeListener(event -> setArchiveFilter(event));
        archFilterRadio.getStyle().set("alignItems", "center");
        archFilterRadio.getStyle().set("theme", "small");
        archFilterRadio.addValueChangeListener(event -> loadInitialViewContent());
        return archFilterRadio;
    }


    private Component initGridContainer(ZakrParams zakrParams) {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.1em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                initExpXlsAnchor()
                , buildGridBarComponent()
                , initZakrGrid(zakrParams)
        );
        return gridContainer;
    }

    private void saveGridItem(Zakr itemForSave, Operation operation) {

        zakrService.saveZakr(itemForSave);
        Zakr savedItem = zakrService.fetchAndCalcOne(itemForSave.getId(), zakrParams);
        List<Zakr> zakrList = (List<Zakr>)((ListDataProvider)zakrGrid.getDataProvider()).getItems();
        int idx = zakrList.indexOf(itemForSave);
        if (idx >= 0) {
            zakrList.set(idx, savedItem);
        } else {
            calcButton.setIconDirty();
        }

//        zakrGrid.getDataCommunicator().getKeyMapper().remove(itemForSave);
        zakrGrid.getDataCommunicator().getKeyMapper().removeAll();
        zakrGrid.getDataCommunicator().getDataProvider().refreshAll();
        zakrGrid.getDataProvider().refreshAll();

        Notification.show(
                "Rozpracovanost uložena", 2000, Notification.Position.TOP_CENTER
        );
    }

    private Component initExpXlsAnchor() {
        expXlsAnchor = new ExpXlsAnchor();
        return expXlsAnchor;
    }

    private Component initZakrGrid(ZakrParams zakrParams) {
        zakrGrid = new ZakRozpracGrid(
                false,true, false
                , this::saveGridItem
                , zakrParams
                , zakrService
                , zakService
                , faktService
                , zakNaklVwService
                , cfgPropsCache
        );
        zakrGrid.setMultiSort(true);
        zakrGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return zakrGrid;
    }

    private void reloadViewContentPreserveFilters() {
        if (zakrGrid.getEditor().isOpen()) {
            zakrGrid.getEditor().closeEditor();
        }
        zakrList = zakrService.fetchAndCalcByActiveFilterDescOrder(zakrParams);
        zakrGrid.populateGridDataAndRestoreFilters(zakrList);
        calcButton.setIconClean();
        zakrGrid.getDataProvider().refreshAll();
    }

    private void loadInitialViewContent() {
        if (zakrGrid.getEditor().isOpen()) {
            zakrGrid.getEditor().closeEditor();
        }
        zakrList = zakrService.fetchAndCalcByActiveFilterDescOrder(zakrParams);
        zakrGrid.populateGridDataAndRebuildFilterFields(zakrList);
        calcButton.setIconClean();
        zakrGrid.getDataProvider().refreshAll();
    }

    public static class ZakrParams {
        Boolean active;
        BigDecimal kurzEur;
        String rx;
        String ry;
        BigDecimal koefRezie;
        BigDecimal koefPojist;

        public static ZakrParams getEmptyInstance() {
            ZakrParams zakrParams =  new ZakrParams();
            zakrParams.setActive(null);
            zakrParams.setKurzEur(null);
            zakrParams.setRx(null);
            zakrParams.setRy(null);
            zakrParams.setKoefRezie(null);
            zakrParams.setKoefPojist(null);
            return zakrParams;
        }

        public static ZakrParams getDefaultInstance(final CfgPropsCache cfgPropsCache) {
            ZakrParams zakrParams = new ZakrParams();
            zakrParams.setActive(true);
            zakrParams.setKurzEur(cfgPropsCache.getBigDecimalValue(CfgPropName.APP_KURZ_CZK_EUR.getName()));
            zakrParams.setRx(null);
            zakrParams.setRy(null);
            zakrParams.setKoefRezie(cfgPropsCache.getBigDecimalValue(CfgPropName.APP_KOEF_REZIE.getName()));
            zakrParams.setKoefPojist(cfgPropsCache.getBigDecimalValue(CfgPropName.APP_KOEF_POJIST.getName()));
            return zakrParams;
        }

        public Boolean isActive() {
            return active;
        }
        public void setActive(Boolean active) {
            this.active = active;
        }

        public BigDecimal getKurzEur() {
            return kurzEur;
        }
        public void setKurzEur(BigDecimal kurzEur) {
            this.kurzEur = kurzEur;
        }

        public String getRx() {
            return rx;
        }
        public void setRx(String rx) {
            this.rx = rx;
        }

        public String getRy() {
            return ry;
        }
        public void setRy(String ry) {
            this.ry = ry;
        }

        public BigDecimal getKoefRezie() {
            return koefRezie;
        }
        public void setKoefRezie(BigDecimal koefRezie) {
            this.koefRezie = koefRezie;
        }

        public BigDecimal getKoefPojist() {
            return koefPojist;
        }
        public void setKoefPojist(BigDecimal koefPojist) {
            this.koefPojist = koefPojist;
        }
    }

    public static class ZakrFilter {

        String ckz;
        Integer rokZak;
        String skupina;
        Boolean arch;
        String kzText;
        String objednatel;

        public static final ZakrFilter getEmpty() {
            ZakrListView.ZakrFilter filter = new ZakrFilter();
            filter.setArch(null);
            filter.setCkz(null);
            filter.setRokZak(null);
            filter.setSkupina(null);
            filter.setObjednatel(null);
            filter.setKzText(null);
            return filter;
        }

        public Boolean getArch() {
            return arch;
        }
        public void setArch(Boolean arch) {
            this.arch = arch;
        }

        public String getCkz() {
            return ckz;
        }
        public void setCkz(String ckz) {
            this.ckz = ckz;
        }

        public Integer getRokZak() {
            return rokZak;
        }
        public void setRokZak(Integer rokZak) {
            this.rokZak = rokZak;
        }

        public String getSkupina() {
            return skupina;
        }
        public void setSkupina(String skupina) {
            this.skupina = skupina;
        }

        public String getObjednatel() {
            return objednatel;
        }
        public void setObjednatel(String objednatel) {
            this.objednatel = objednatel;
        }

        public String getKzText() {
            return kzText;
        }
        public void setKzText(String kzText) {
            this.kzText = kzText;
        }
    }
}
