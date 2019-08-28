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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.CfgPropName;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.ReportZakRozpracDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_ZAKR_LIST, layout = MainView.class)
@PageTitle(PAGE_TITLE_ZAKR_LIST)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_ROZPRAC_READ, Perm.ZAK_ROZPRAC_MODIFY
})
//public class ZakEvalListView extends Div implements BeforeEnterObserver {
// ###***
public class ZakrListView extends VerticalLayout {

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archivované";
    private static final String RADIO_KONT_ALL = "Všechny";

    private List<Zakr> zakrList;
    private ZakRozpracGrid zakrGrid;
    private List<GridSortOrder<ZakBasic>> initialSortOrder;

    private RadioButtonGroup<String> archFilterRadio;
    private Button rozpracRepButton;
    private Button saveEditButton;
    private TextField kurzParamField;
    private TextField rxParamField;
    private TextField ryParamField;

    private ZakrParams zakrParams;

    @Autowired
    public ZakrService zakrService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public FaktService faktService;

    @Autowired
    public ZaqaService zaqaService;

    @Autowired
    public CfgPropsCache cfgPropsCache;

    public ZakrListView() {
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//        setHeight("90%");
//        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setPadding(false);
        this.setMargin(false);
//        this.add(
//                initGridContainer()
//        );
    }

    @PostConstruct
    public void postInit() {
        zakrParams = new ZakrParams();
        zakrParams.setKurz(new BigDecimal(cfgPropsCache.getValue(CfgPropName.APP_KURZ_CZK_EUR.getName())));

        this.add(
                initGridContainer()
        );
        loadInitialViewContent();
        // TODO: inital sort order markers
        //        zakrGrid.sort(initialSortOrder);
        //        UI.getCurrent().getPage().executeJavaScript("document.querySelectorAll(\"vaadin-grid-sorter\")[1].click()");
    }


//    private Component buildInitinitGridToolBar() {
//        initialSortOrder = Arrays.asList(
//                new GridSortOrder(
//                        zakrGrid.getColumnByKey(ZakBasicGrid.ROK_COL_KEY), SortDirection.DESCENDING)
//                , new GridSortOrder(
//                        zakrGrid.getColumnByKey(ZakBasicGrid.KZCISLO_COL_KEY), SortDirection.DESCENDING)
//        );
//    }


    private Component initGridToolBar() {
        HorizontalLayout gridToolBar = new HorizontalLayout();
        gridToolBar.setSpacing(false);
        gridToolBar.setAlignItems(Alignment.END);
        gridToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        gridToolBar.add(
                buildTitleComponent()
                , new Ribbon()
                , buildGridToolBarControlsComponent()
                , new Ribbon()
                , initRozpracRepButton()
        );
        return gridToolBar;
    }

    private Component buildTitleComponent() {
        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
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

    private Component buildGridToolBarControlsComponent() {
        HorizontalLayout controlsComponent = new HorizontalLayout();
        controlsComponent.setMargin(false);
        controlsComponent.setPadding(false);
        controlsComponent.setSpacing(false);
        controlsComponent.setAlignItems(Alignment.CENTER);
        controlsComponent.setJustifyContentMode(JustifyContentMode.START);
        controlsComponent.add(
                initKurzField()
                , new Ribbon("3em")
                , initRxParamField()
                , new Ribbon()
                , initRyParamField()
        );

        return controlsComponent;
    }

    private Component initRxParamField() {
        rxParamField = new TextField("RX (od)");
        rxParamField.setValue("1");
        rxParamField.setReadOnly(true);
        return rxParamField;
    }

    private Component initRyParamField() {
        ryParamField = new TextField("RY (do)");
        ryParamField.setValue("2");
        ryParamField.setReadOnly(true);
        return ryParamField;
    }

    private Component initKurzField() {
        kurzParamField = new TextField("Kurz CZK/EUR"
//                , event -> ((CallbackDataProvider) klientGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getValue()))
        );
////        kurzParamField.setStep(0.01);
//
//        BigDecimal kurz;

//        kurzParamField.addValueChangeListener(updateContent);
        kurzParamField.setValueChangeMode(ValueChangeMode.EAGER);
        kurzParamField.setReadOnly(true);

        Binder<ZakrParams> binder = new Binder<>();
        binder.forField(kurzParamField)
                .withConverter(new StringToBigDecimalConverter("Špatný formát"))
                .bind(ZakrParams::getKurz, ZakrParams::setKurz);
//
////        .bind("")
        binder.readBean(zakrParams);
        return kurzParamField;
    }

    public static class ZakrParams {
        public BigDecimal getKurz() {
            return kurz;
        }

        public void setKurz(BigDecimal kurz) {
            this.kurz = kurz;
        }

        BigDecimal kurz;
    }

    private Component initRozpracRepButton() {
        rozpracRepButton = new Button("Report"
            , event -> {
                openRozpracRepDialog();
        });
//        this.addClassName("view-toolbar__button");
//        this.getStyle().set("theme", "small");
        rozpracRepButton.getElement().setAttribute("theme", "small secondary");
        return rozpracRepButton;
    }

    private void openRozpracRepDialog() {
        ReportZakRozpracDialog repZakRozpracDlg  = new ReportZakRozpracDialog(zakrService);
        repZakRozpracDlg.openDialog();
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


//    private Component initSaveEditButton() {
//        saveEditButton = new Button("Uložit");
//        saveEditButton.getElement().setAttribute("theme", "primary");
//        saveEditButton.addClickListener(event -> {
//            ConfirmDialog.createQuestion()
//                    .withCaption("EDITACE PROUŽKU")
//                    .withMessage("Uložit proužek?")
//                    .withCancelButton(ButtonOption.caption("ZPĚT"))
//                    .withYesButton(() -> {
////                        dochsumZakService.updateDochsumZaksForPersonAndMonth(
////                                pruhPerson.getId(), pruhYm, transposePruhZaksToDochsumZaks(pruhZakList));
//                        if (zakrGrid.getEditor().isOpen()) {
//                            zakrGrid.getEditor().closeEditor();
//                        }
//                        boolean ok = zakrService.update...(
//                                pruhPerson.getId()
//                                , pruhYm
//                                , pruhDayMax
//                                , pruhZakList
//                        );
//                        update PruhGrids(pruhPerson, pruhYm);
//                    } , ButtonOption.focus(), ButtonOption.caption("ULOŽIT"))
//                    .open()
//            ;
//        });
//        return saveEditButton;
//    }


    private Component initGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                initGridToolBar()
                , initZakrGrid()
        );
        return gridContainer;
    }

    private void saveGridItem(Zakr itemForSave, Operation operation) {

        zakrService.saveZakr(itemForSave);
        Zakr savedItem = zakrService.fetchOne(itemForSave.getId());

//        Zakr savedItem = zakrService.fetchOne(itemToSave.getId());

//        zakrList = zakrService.fetchAllDescOrder();
//        zakrGrid.setItems(zakrList);

        List<Zakr> zakrList = (List<Zakr>)((ListDataProvider)zakrGrid.getDataProvider()).getItems();
        int idx = zakrList.indexOf(itemForSave);
        if (idx >= 0) {
            zakrList.set(idx, savedItem);
        }

//        zakrGrid.getDataCommunicator().getKeyMapper().remove(itemForSave);
        zakrGrid.getDataCommunicator().getKeyMapper().removeAll();
        zakrGrid.getDataCommunicator().getDataProvider().refreshAll();
        zakrGrid.getDataProvider().refreshAll();

//        zakrGrid.getDataCommunicator().getKeyMapper().refresh(savedItem);

//        ((ListDataProvider)zakrGrid.getDataProvider()).refreshItem(savedItem);

//        List<Zakr> zakrList = (List<Zakr>)((ListDataProvider)zakrGrid.getDataProvider()).getItems();
//        int idx = zakrList.indexOf(itemForSave);
//        if (idx >= 0) {
//            zakrList.set(idx, savedItem);
//        }


//        zakrGrid.getDataCommunicator().getKeyMapper().removeAll();
//        zakrGrid.getDataCommunicator().getDataProvider().refreshAll();

//        zakrGrid.getDataCommunicator().getKeyMapper().remove(itemForSave);
//        zakrGrid.getDataCommunicator().getDataProvider().refreshItem(itemForSave);
//        zakrGrid.getDataCommunicator().getDataProvider().refreshItem(savedItem);

//        zakrGrid.getDataCommunicator().getKeyMapper().refresh(savedItem);
//        zakrGrid.getDataProvider().refreshItem(savedItem);


//        zakrGrid.getDataCommunicator().getKeyMapper().removeAll();
//        zakrGrid.getDataCommunicator().getDataProvider().refreshAll();
//        zakrGrid.getDataProvider().refreshAll();

//        zakrGrid.getDataCommunicator().reset();
//        zakrGrid.getDataCommunicator().getKeyMapper().remove(savedItem);
//        zakrGrid.getDataCommunicator().getKeyMapper().remove(itemToSave);
//        zakrGrid.getDataProvider().refreshItem(itemToSave);
//        zakrGrid.getDataCommunicator().getDataProvider().refreshItem(savedItem);
//        zakrGrid.getDataCommunicator().getDataProvider().refreshAll();


//        zakrGrid.rpVysledekGridValueProvider.apply(savedItem);
//        zakrGrid.getDataProvider().refreshAll();
//        zakrList = zakrService.fetchAllDescOrder();
//        zakrGrid.setItems(zakrList);

        Notification.show(
                "Rozpracovanost uložena", 2000, Notification.Position.TOP_CENTER);
    }

    private Component initZakrGrid() {
        zakrGrid = new ZakRozpracGrid(
                false,true, null
                , this::saveGridItem
//                , BigDecimal.valueOf(25.5)
                // FIXME
                // , cfgPropsCache.getDecimalValue("")
//                , new BigDecimal(kurzParamField.getValue())
                , zakrParams
                , zakrService
                , zakService
                , faktService
                , zaqaService
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
//                    ckzEdit = String.format("%s / %s", selectedItem.getCkont(), selectedItem.getCzak());
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

//    private void openDir(String path) {
//        try {
////            Runtime.getRuntime().exec("explorer.exe /select," + path);
//            ProcessBuilder pb = new ProcessBuilder("explorer.exe", "/select," + path);
//            pb.redirectError();
//            pb.start();
////            Process proc = pb.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void reloadViewContentPreserveFilters() {
        if (zakrGrid.getEditor().isOpen()) {
            zakrGrid.getEditor().closeEditor();
        }
        zakrList = zakrService.fetchAllDescOrder();
        zakrGrid.populateGridDataAndRestoreFilters(zakrList);
        zakrGrid.getDataProvider().refreshAll();
    }

    private void loadInitialViewContent() {
//        kurzParamField.setValue(cfgPropsCache.getValue(CfgPropName.APP_KURZ_CZK_EUR.getName()));
        if (zakrGrid.getEditor().isOpen()) {
            zakrGrid.getEditor().closeEditor();
        }
        zakrList = zakrService.fetchAllDescOrder();
        zakrGrid.populateGridDataAndRebuildFilterFields(zakrList);
        zakrGrid.getDataProvider().refreshAll();
    }
}
