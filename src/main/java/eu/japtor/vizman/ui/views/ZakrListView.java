/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
import eu.japtor.vizman.backend.report.ZakNaklAgregXlsReportBuilder;
import eu.japtor.vizman.backend.report.ZakRozpracAgregXlsReportBuilder;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
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

    private static final String REP_ZAKR_AGREG_FILE_NAME = "vzm-rep-zakr-agreg";
    private static final String REP_ZAKN_AGREG_FILE_NAME = "vzm-rep-zakn-agreg";
    private static final String ZAKR_DOWN_ANCHOR_ID = "zakr-down-anchor-id";

    private List<Zakr> zakrList;
    private ZakRozpracGrid zakrGrid;
    private List<GridSortOrder<ZakBasic>> initialSortOrder;

    private RadioButtonGroup<String> archFilterRadio;
    private Button rozpracRepButton;
    private CalcButton calcButton;
    private TextField kurzParamField;
    private TextField rezieParamField;
    private TextField pojistParamField;
    private Select<String> rxParamField;
    private Select<String> ryParamField;
    private Checkbox activeParamField;
    private ZakrParams zakrParams;
    private Binder<ZakrParams> paramsBinder;

    private ReportXlsExporter<Zakr> rozpracAgregXlsRepExporter;
    private ReportXlsExporter<Zakr> naklAgregXlsRepExporter;
    private Anchor downloadAnchor;

//    ZakNaklSingleDialog zakNaklGridDialog;

    @Autowired
    public ZakrService zakrService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public FaktService faktService;

    @Autowired
    public ZaknService zaknService;

    @Autowired
    public CfgPropsCache cfgPropsCache;

    public ZakrListView() {
        rozpracAgregXlsRepExporter = new ReportXlsExporter<>();
        naklAgregXlsRepExporter = new ReportXlsExporter<>();
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


//    private Component buildInitGridToolBar() {
//        initialSortOrder = Arrays.asList(
//                new GridSortOrder(
//                        zakrGrid.getColumnByKey(ZakSimpleGrid.ROK_COL_KEY), SortDirection.DESCENDING)
//                , new GridSortOrder(
//                        zakrGrid.getColumnByKey(ZakSimpleGrid.CKZ_COL_KEY), SortDirection.DESCENDING)
//        );
//    }


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

//    private Component initRxParamField() {
//        rxParamField = new TextField("RX (od)");
//        rxParamField.setValue("1");
//        rxParamField.setReadOnly(true);
//        return rxParamField;
//    }

//    private Component initRyParamField() {
//        ryParamField = new TextField("RY (do)");
//        ryParamField.setValue("2");
//        ryParamField.setReadOnly(true);
//        return ryParamField;
//    }

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

    private Checkbox initActiveParamField() {
        activeParamField = new Checkbox("Aktivní"); // = new TextField("Username");
        activeParamField.getElement().setAttribute("theme", "small");
        paramsBinder.forField(activeParamField)
                .bind(ZakrParams::isActive, ZakrParams::setActive);
        return activeParamField;
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

    private Component initCalcButton() {
        calcButton = new CalcButton(event -> {
            BinderValidationStatus status = paramsBinder.validate();
            if (status.hasErrors()) {
                calcButton.setIconDirty();
            } else {
                paramsBinder.writeBeanIfValid(zakrParams);
//                zakrGrid.setZakrParams(zakrParams);
                zakrList = zakrService.fetchAndCalcByActiveFilterDescOrder(zakrParams);
                zakrGrid.populateGridDataAndRestoreFilters(zakrList);
                zakrGrid.recalcGrid();
                zakrGrid.getDataProvider().refreshAll();
                calcButton.setIconClean();
            }
        });
        calcButton.getElement().setAttribute("theme", "small");
        return calcButton;
    }

    private String getZakrAgregReportFileName(ReportXlsExporter.Format format) {
        return REP_ZAKR_AGREG_FILE_NAME + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private String getNaklAgregReportFileName(ReportXlsExporter.Format format) {
        return REP_ZAKN_AGREG_FILE_NAME + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private List<Zakr> getSelectedZakrForReport(final String zakrId) {
//        return zakrService.fetchTopById(zakrId);
        return Collections.emptyList();
    }

    private SerializableSupplier<List<? extends Zakr>> zakrCurrentReportSupplier =
            () -> {
                Set<Zakr> itemSelection = zakrGrid.getSelectedItems();  // Suppose: SingleSelectionModel is set, only one (or none) item is present
                if (null == itemSelection || itemSelection.isEmpty()) {
                    return Collections.EMPTY_LIST;
                }
                zakrService.fetchAndCalcOne(itemSelection.iterator().next().getId(), getCurrentParms());
                return Collections.singletonList(itemSelection.iterator().next());
            };

    private SerializableSupplier<List<? extends Zakr>> zakrVisibleReportSupplier =
            () -> {
                return zakrService.fetchAndCalcByFiltersDescOrder(getCurrentParms());
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
        btn.getElement().setProperty("title", "Náklady na zakázku - report");

        ContextMenu menu = new ContextMenu();
        menu.addItem("Všechny zobrazené", e -> updateNaklAgregXlsRepResourceAndDownload(zakrVisibleReportSupplier));
        menu.addItem("Aktuální", e -> updateNaklAgregXlsRepResourceAndDownload(zakrCurrentReportSupplier));

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
        menu.addItem("Všechny zobrazené", e -> updateRozpracAgregXlsRepResourceAndDownload(zakrVisibleReportSupplier));
        menu.addItem("Aktuální", e -> updateRozpracAgregXlsRepResourceAndDownload(zakrCurrentReportSupplier));

        menu.setOpenOnClick(true);
        menu.setTarget(btn);
        return btn;
    }

    private void updateRozpracAgregXlsRepResourceAndDownload(SerializableSupplier<List<? extends Zakr>> itemsSupplier) {

        final AbstractStreamResource xlsResource =
                rozpracAgregXlsRepExporter.getXlsStreamResource(
                        new ZakRozpracAgregXlsReportBuilder(getSumaryRepParamSubtitleText())
                        , getZakrAgregReportFileName(ReportXlsExporter.Format.XLS)
                        , itemsSupplier
                        , null
                );

        // Varianta 1
        downloadAnchor.setHref(xlsResource);
        Page page = UI.getCurrent().getPage();
        page.executeJs("$0.click();", downloadAnchor.getElement());
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

    private void updateNaklAgregXlsRepResourceAndDownload(SerializableSupplier<List<? extends Zakr>> itemsSupplier) {

        final AbstractStreamResource xlsResource =
                naklAgregXlsRepExporter.getXlsStreamResource(
                        new ZakNaklAgregXlsReportBuilder(
                                getSumaryRepParamSubtitleText()
                                , SecurityUtils.isNaklCompleteAccessGranted()
                                , zakrParams.getKoefPojist()
                                , zakrParams.getKoefRezie()
                        )
                        , getNaklAgregReportFileName(ReportXlsExporter.Format.XLS)
                        , itemsSupplier
                        , null
                );

        // Varianta 1
        downloadAnchor.setHref(xlsResource);
        Page page = UI.getCurrent().getPage();
        page.executeJs("$0.click();", downloadAnchor.getElement());
//      or:  page.executeJs("document.getElementById('" + KONT_REP_ID + "').click();");

    }

    private String getSumaryRepParamSubtitleText() {
        ZakrParams zakrParams = getCurrentParms();
        String subtitleText =
                "Parametry: Ativní=" + (null == zakrParams.isActive() ? "false" : zakrParams.isActive().toString()) +
                        "  Arch=" + (null == zakrParams.getArch() ? "Vše" : zakrParams.getArch().toString()) +
                        "  ČK-ČZ=" + (null == zakrParams.getCkz() ? "Vše" : "*" + zakrParams.getCkz() + "*") +
                        "  Rok zak.=" + (null == zakrParams.getRokZak() ? "Vše" : zakrParams.getRokZak().toString()) +
                        "  Skupina=" + (null == zakrParams.getSkupina() ? "Vše" : zakrParams.getSkupina().toString()) +
                        "  rx=" + (null == rxParamField.getValue() ? "" : rxParamField.getValue().toString()) +
                        "  ry=" + (null == ryParamField.getValue() ? "" : ryParamField.getValue().toString()) +
                        "  Koef.pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue()) +
                        "  Koef.režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue()) +
                        "  Kurz CZK/EUR=" + (null == kurzParamField.getValue() ? "" : kurzParamField.getValue());
        return subtitleText;
    }

    private void openRozpracRepDialog() {
////        List<Zakr> zakrRep1 = (List<Zakr>)((ListDataProvider)zakrGrid.getDataCommunicator().getDataProvider()).getItems();
////        List<Zakr> zakrRep2 = (List<Zakr>)((ListDataProvider)zakrGrid.getDataProvider()).getItems();
//        zakrGrid.saveFilterValues();
//        zakrParams.setActive(activeParamField.getValue());
//        zakrParams.setArch(zakrGrid.getArchFilterValue());
//        zakrParams.setCkz(zakrGrid.getCkzFilterValue());
//        zakrParams.setRokZak(zakrGrid.getRokFilterValue());
//        zakrParams.setSkupina(zakrGrid.getSkupinaFilterValue());
//        ZakRozpracReportDialog zakRozpracReportDlg  = new ZakRozpracReportDialog(zakrService, zakrParams);
//        zakRozpracReportDlg.openDialog(zakrParams);
//        zakRozpracReportDlg.generateAndShowReport();
    }

    private ZakrParams getCurrentParms() {
        zakrGrid.saveFilterValues();
        zakrParams.setActive(activeParamField.getValue());
        zakrParams.setArch(zakrGrid.getArchFilterValue());
        zakrParams.setCkz(zakrGrid.getCkzFilterValue());
        zakrParams.setRokZak(zakrGrid.getRokFilterValue());
        zakrParams.setSkupina(zakrGrid.getSkupinaFilterValue());
        return zakrParams;
    }


    // Kopie z ZaRozpracReportDialog
    public void generateAndShowReport() {
//        deactivateListeners();
//        report.setSubtitleText(
//                "Parametry: Ativní=" + (null == activeFilterField.getValue() ? "false" : activeFilterField.getValue().toString()) +
//                        "  Arch=" + (null == archFilterField.getValue() ? "Vše" : archFilterField.getValue().toString()) +
//                        "  ČK-ČZ=" + (null == ckzFilterField.getValue() ? "Vše" : "*" + ckzFilterField.getValue() + "*") +
//                        "  Rok zak.=" + (null == rokZakFilterField.getValue() ? "Vše" : rokZakFilterField.getValue().toString()) +
//                        "  Skupina=" + (null == skupinaFilterField.getValue() ? "Vše" : skupinaFilterField.getValue().toString()) +
//                        "  rx=" + (null == rxParamField.getValue() ? "" : rxParamField.getValue().toString()) +
//                        "  ry=" + (null == ryParamField.getValue() ? "" : ryParamField.getValue().toString()) +
//                        "  Režie=" + (null == rezieParamField.getValue() ? "" : rezieParamField.getValue()) +
//                        "  Pojištění=" + (null == pojistParamField.getValue() ? "" : pojistParamField.getValue()) +
//                        "  Kurz CZK/EUR=" + (null == kurzParamField.getValue() ? "" : kurzParamField.getValue())
//        );
//
//        // Tohle nefunguje:
//        // report.getReportBuilder().setProperty("ireport.zoom", "2.0");
//        // report.getReportBuilder().setProperty("net.sf.jasperreports.viewer.zoom", "2");
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
                initDownloadAnchor()
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

    private Component initDownloadAnchor() {
        downloadAnchor = new Anchor();
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setId(ZAKR_DOWN_ANCHOR_ID);
        downloadAnchor.setText("Invisible ZAKR download link");    // setVisible  also disables a server part - cannot be useed
        downloadAnchor.getStyle().set("display", "none");
        return downloadAnchor;
    }

    private Component initZakrGrid(ZakrParams zakrParams) {
        zakrGrid = new ZakRozpracGrid(
                false,true, false
                , this::saveGridItem
                , zakrParams
                , zakrService
                , zakService
                , faktService
                , zaknService
                , cfgPropsCache
        );
        zakrGrid.setMultiSort(true);
        zakrGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

//        zakrGrid.addSelectionListener(event -> {
//            if (zakrGrid.getEditor().isOpen()) {
//                zakrGrid.getEditor().save();
//                zakrGrid.getEditor().closeEditor();
//            }
//            String ckzEdit = "N/A";
//            try {
//                if (null != selectedItem) {
//                    ckzEdit = String.format("%s / %s", selectedItem.getCkont(), selectedItem.getCkz());
//                    zakrService.saveZakr(selectedItem, Operation.EDIT);
//                }
//                selectedItem = event.getFirstSelectedItem().orElse(null);   // Note: grid selection mode is supposed to be SINGLE
//            } catch(Exception ex) {
//                ConfirmDialog
//                        .createError()
//                        .withCaption("Editace rozpracovanosti.")
//                        .withMessage(String.format("Zakázku %s se nepodařilo uložit.", ckzEdit))
//                        .open()
//                ;
//            }
//        });
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
//        kurzParamField.setValue(cfgPropsCache.getStringValue(CfgPropName.APP_KURZ_CZK_EUR.getName()));
        if (zakrGrid.getEditor().isOpen()) {
            zakrGrid.getEditor().closeEditor();
        }
        zakrList = zakrService.fetchAndCalcByActiveFilterDescOrder(zakrParams);
        zakrGrid.populateGridDataAndRebuildFilterFields(zakrList);
        calcButton.setIconClean();
        zakrGrid.getDataProvider().refreshAll();
    }

    public static class ZakrParams {

        String ckz;
        Integer rokZak;
        String skupina;
        BigDecimal kurzEur;
        BigDecimal koefRezie;
        BigDecimal koefPojist;
        String rx;
        String ry;
        Boolean arch;
        Boolean active;

        public static ZakrParams getEmptyInstance() {
            ZakrParams zakrParams =  new ZakrParams();
//            zakrParams.setKurzEur(cfgPropsCache.getBigDecimalValue(CfgPropName.APP_KURZ_CZK_EUR.getName()));
            zakrParams.setActive(false);
            zakrParams.setKurzEur(null);
            zakrParams.setRx(null);
            zakrParams.setRy(null);
//            zakrParams.setKoefRezie(cfgPropsCache.getBigDecimalValue(CfgPropName.APP_KOEF_REZIE.getName()));
            zakrParams.setKoefRezie(null);
//            zakrParams.setKoefPojist(cfgPropsCache.getBigDecimalValue(CfgPropName.APP_KOEF_POJIST.getName()));
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

        public BigDecimal getKurzEur() {
            return kurzEur;
        }
        public void setKurzEur(BigDecimal kurzEur) {
            this.kurzEur = kurzEur;
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
        
        public Boolean isActive() {
            return active;
        }
        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
