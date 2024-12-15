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

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.*;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.AbstractStreamResource;
import elemental.json.JsonObject;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.report.KontTreeXlsReportBuilder;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.KontFormDialog;
import eu.japtor.vizman.ui.forms.ZakFormDialog;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.*;

import static eu.japtor.vizman.app.security.SecurityUtils.isHonorareAccessGranted;
import static eu.japtor.vizman.app.security.SecurityUtils.isZakFormsAccessGranted;
import static eu.japtor.vizman.ui.util.VizmanConst.*;


@Route(value = ROUTE_KZ_TREE, layout = MainView.class)
@PageTitle(PAGE_TITLE_KZ_TREE)
//@Tag(TAG_ZAK)    // Kurvi layout
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class KzTreeView extends VerticalLayout implements HasLogger {

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archiv";
    private static final String RADIO_KONT_EMPTY = "Prázdné";
    private static final String RADIO_KONT_ALL = "Vše";

    private static final String RADIO_AVIZO_GREEN = "Zelené";
    private static final String RADIO_AVIZO_RED = "Červené";
    private static final String LIGHTS_ALL = "Vše";

    private static final String CKZ_COL_KEY = "ckz-col";
    private static final String ARCH_COL_KEY = "arch-col";
    private static final String DIGI_COL_KEY = "digi-col";
    private static final String MENA_COL_KEY = "mena-col";
    private static final String HONORAR_COL_KEY = "honorar-col";
    private static final String AVIZO_COL_KEY = "avizo-col";
    private static final String TEXT_COL_KEY = "text-col";
    private static final String OBJEDNATEL_COL_KEY = "objednatel-col";
    private static final String INVESTOR_COL_KEY = "investor-col";
    private static final String DATETIME_UPDATE_COL_KEY = "datetime-update-col";
    private static final String UPDATED_BY_COL_KEY = "updated-by-col";
    private static final String SKUPINA_COL_KEY = "skupina-col";
    private static final String ROK_COL_KEY = "rok-col";
    private static final String ALERT_COL_KEY = "alert-col";

    private static final String REP_KONT_SEL_FILE_NAME = "vzm-rep-kont";
    private static final String KONT_DOWN_ANCHOR_ID = "kont-down-anchor-id";

    private KontFormDialog kontFormDialog;
    private ZakFormDialog zakFormDialog;

    private TreeGrid<KzTreeAware> kzTreeGrid;
    private TreeData<KzTreeAware> kzTreeData;
    private TreeDataProvider<KzTreeAware> inMemoryKzTreeProvider;
    private Button reloadButton;
    private Button newKontButton;
    private FilterTextField ckontFilterField;
    private FilterTextField objednatelFilterField;
    private RadioButtonGroup<String> archFilterRadio;
    private RadioButtonGroup<String> avizoFilterRadio;
    private ComponentRenderer<HtmlComponent, KzTreeAware> kzTextRenderer;
    private ComponentRenderer<Component, KzTreeAware> avizoRenderer;
    private Select<Integer> rokFilterField;
    private List<GridSortOrder<KzTreeAware>> initialSortOrder;

    private ReportXlsExporter<Kont> reportXlsExporter;
    private Anchor downloadAnchor;

    @Autowired
    public KontService kontService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public ZaqaService zaqaService;

    @Autowired
    public FaktService faktService;

    @Autowired
    public KlientService klientService;

    @Autowired
    public DochsumZakService dochsumZakService;

    @Autowired
    private CfgPropsCache cfgPropsCache;


    static class KzText extends Paragraph implements KeyNotifier {
        public KzText(String text) {
            super(text);
        }
    }


    private <T> Select buildSelectorField() {
        Select <T> selector = new Select<>();
        selector.setSizeFull();
        selector.setEmptySelectionCaption("Vše");
        selector.setEmptySelectionAllowed(true);
        return selector;
    }

    public KzTreeView() {
        reportXlsExporter = new ReportXlsExporter();
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);
    }

    @PostConstruct
    public void postInit() {

        kontFormDialog = new KontFormDialog(
                kontService, zakService, zaqaService, faktService,
                klientService, dochsumZakService, cfgPropsCache
        );
        kontFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishKontEdit((KontFormDialog)event.getSource());
            }
        });
        kontFormDialog.addDialogCloseActionListener(event -> {
            kontFormDialog.close();
        });

        zakFormDialog = new ZakFormDialog(
                zakService, zaqaService, faktService, cfgPropsCache
        );
        zakFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishZakEdit((ZakFormDialog)event.getSource());
            }
        });
        zakFormDialog.addDialogCloseActionListener(event -> {
            zakFormDialog.close();
        });

        initKzTextRenderer();
        this.add(buildGridContainer());

//        // Triggers an event which will loadKzTreeData:
        archFilterRadio.setValue(RADIO_KONT_ACTIVE);
        updateViewContent();
    }

    private void finishKontEdit(KontFormDialog kontFormDialog) {
        Kont kontAfter = kontFormDialog.getCurrentItem(); // Kont modified, just added or just deleted
        Operation kontOper = kontFormDialog.getCurrentOperation();
        OperationResult kontOperRes = kontFormDialog.getLastOperationResult();
        boolean kontZaksChanged = kontFormDialog.isKontZaksChanged();
        boolean kontZaksFaktsChanged = kontFormDialog.isKontZaksFaktsChanged();
//        Kont kontOrig = kontFormDialog.getOrigItem();

        syncTreeGridAfterKontEdit(kontOrig, kontOper, kontOperRes, kontZaksChanged, kontZaksFaktsChanged);

        if (OperationResult.ITEM_SAVED == kontOperRes || kontZaksChanged) {
            Notification.show("Kontrakt " + kontAfter.getCkont() + " uložen"
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == kontOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace kontraktu")
                    .withMessage("Kontrakt " + kontAfter.getCkont() + " zrušen.")
                    .open();
        }
    }


    Kont kontOrig;
    Zak zakOrig;
    private void finishZakEdit(ZakFormDialog zakFormDialog) {
        Zak zakAfter = zakFormDialog.getCurrentItem(); // Modified, just added or just deleted
        String ckz = String.format("%s / %s", zakAfter.getCkont(), zakAfter.getCzak());
        Operation oper = zakFormDialog.getCurrentOperation();
        OperationResult zakOperRes = zakFormDialog.getLastOperationResult();

        boolean zakFaktsChanged = zakFormDialog.isZakFaktsChanged();
//        KzTreeAware kzItemOrig = zakFormDialog.getOrigItem();

        // TODO: save before opening dialog
//        Zak zakOrig = zakFormDialog.getOrigItem();

//        syncTreeGridAfterZakEdit(zakOrig, oper, zakOperRes, zakFaktsChanged);
        syncTreeGridAfterZakEdit(zakOrig, oper, zakOperRes, zakFaktsChanged);

        if (OperationResult.ITEM_SAVED == zakOperRes || zakFaktsChanged) {
            Notification.show(String.format("Zakázka %s uložena", ckz)
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == zakOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace zakázky")
                    .withMessage(String.format("Zakázka %s zrušena.", ckz))
                    .open();
        }
    }

    private void syncTreeGridAfterKontEdit(
            Kont kontOrig, Operation kontOper
            , OperationResult kontOperRes, boolean kontZaksChanged, boolean kontZaksFaktsChanged
    ){
        if ((OperationResult.NO_CHANGE == kontOperRes) && !kontZaksChanged && !kontZaksFaktsChanged) {
            selectItem(kontOrig);
            return;
        }

        kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
        kzTreeGrid.getDataCommunicator().getDataProvider().refreshAll();
        kzTreeGrid.getDataProvider().refreshAll();

        Kont kontToSync = kontService.fetchOne(kontOrig.getId());
//        KzTreeAware itemToReselect = OperationResult.ITEM_DELETED == kontOperRes ?
        KzTreeAware itemToReselect = (kontToSync == null)
                || (OperationResult.ITEM_DELETED == kontOperRes) ?
                        getNeighborItemFromTree(kontOrig) : kontToSync;

        if (Operation.ADD != kontOper) {
            kzTreeData.removeItem(kontOrig);
        }
        if (Operation.DELETE != kontOper && null != kontToSync) {
            if (null == getItemFromTree(kontToSync)) {
                kzTreeData.addItem(null, kontToSync);
            }
            if (!CollectionUtils.isEmpty(kontToSync.getZaks())) {
                kzTreeData.addItems(kontToSync, ((KzTreeAware)kontToSync).getNodes());
            }
        }

        kzTreeGrid.getDataCommunicator().reset();
        resortAndReselectTreeGrid(itemToReselect);

    }

    private void syncTreeGridAfterZakEdit(
//            Zak zakOrig, Operation zakOper
            Zak zakOrig, Operation zakOper
            , OperationResult zakOperRes, boolean zakFaktsChanged
    ){
        if ((OperationResult.NO_CHANGE == zakOperRes) && !zakFaktsChanged) {
            selectItem(zakOrig);
            return;
        }
        if (zakFaktsChanged) {
            updateViewContent();    // Workaround after fakts are deleted -> TreeGrid  is not updated  properly
        }

        Zak zakToSync = zakService.fetchOne(zakOrig.getId());
        KzTreeAware itemToReselect = (zakToSync == null) || (OperationResult.ITEM_DELETED == zakOperRes) ?
                getNeighborItemFromTree(zakOrig) : zakToSync;

        Kont kontOrig = zakOrig.getKont();
        kzTreeData.removeItem(kontOrig);

        Kont kontToSync = kontService.fetchOne(kontOrig.getId());
        if (null == getItemFromTree(kontToSync)) {
            kzTreeData.addItem(null, kontToSync);
        }
        if (!CollectionUtils.isEmpty(kontToSync.getZaks())) {
            kzTreeData.addItems(kontToSync, ((KzTreeAware)kontToSync).getNodes());
        }

        kzTreeGrid.expand(kontToSync);
        resortAndReselectTreeGrid(itemToReselect);
    }

    private KzTreeAware getItemFromTree(final KzTreeAware item) {
        Integer itemIdx = getItemRowIdx(item);
        if (null == itemIdx) {
            return null;
        }

        return kzTreeGrid.getDataCommunicator()
                .fetchFromProvider(itemIdx, 1)
                .findFirst().orElse(null);
    }

    private KzTreeAware getNeighborItemFromTree(final KzTreeAware item) {
        Integer itemIdx = getItemRowIdx(item);
        if (null == itemIdx) {
            return null;
        }

        KzTreeAware newSelectedItem;
        newSelectedItem = kzTreeGrid.getDataCommunicator()
                .fetchFromProvider(itemIdx + 1, 1)
                .findFirst().orElse(null);
//        KzTreeAware newSelectedItem = stream.findFirst().orElse(null);
        if (null == newSelectedItem) {
            newSelectedItem = kzTreeGrid.getDataCommunicator()
                    .fetchFromProvider(itemIdx - 1, 1)
                    .findFirst().orElse(null);
        }
        return newSelectedItem;
    }

    private Integer getItemRowIdx(final KzTreeAware item) {
        return kzTreeGrid.getDataCommunicator().getIndex(item);
    }

    private ComponentRenderer initKzTextRenderer() {

        kzTextRenderer = new ComponentRenderer<>(kontZak -> {
            KzText kzText = new KzText(kontZak.getText());
            kzText.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(kontZak.getTyp()));
            if (ItemType.KONT == kontZak.getTyp()) {
                kzText.getStyle().set("font-size" , "var(--lumo-font-size-s)");
            } else {
                kzText.getStyle().set("text-indent", "1em");
            }
            kzText.addKeyPressListener((ComponentEventListener<KeyPressEvent>) keyPressEvent -> {
            });
            return kzText;
        });

        return kzTextRenderer;
    }


    private ValueProvider<KzTreeAware, String> ckzValProv =
            kz -> kz.getTyp() == ItemType.KONT ? kz.getCkont() : kz.getCzak().toString();

    private ValueProvider<KzTreeAware, String> menaValProv =
        kz -> kz.getTyp() == ItemType.KONT ? (null == kz.getMena() ? null : kz.getMena().name()) : null;

    private ValueProvider<KzTreeAware, String> objednatelValProv =
        kz -> kz.getTyp() == ItemType.KONT ? kz.getObjednatelName() : null;

    private ValueProvider<KzTreeAware, String> investorValProv =
        kz -> kz.getTyp() == ItemType.KONT ? kz.getInvestorName() : null;

    private ValueProvider<KzTreeAware, String> investorOrigValProv =
        kz -> kz.getTyp() == ItemType.KONT ? kz.getInvestorOrigName() : null;

    private ComponentRenderer<HtmlComponent, KzTreeAware> datetimeUpdateCellRenderer = new ComponentRenderer<>(kz -> {
        if (ItemType.KONT == kz.getTyp() || ItemType.ZAK == kz.getTyp()) {
            return VzmFormatUtils.getDatetimeUpdateComponent(kz.getDatetimeUpdate(), kz.getUpdatedBy());
        } else {
            return VzmFormatUtils.getEmptyComponent();
        }
    });

    private ValueProvider<KzTreeAware, String> updatedByValProv =
            kz -> kz.getTyp() == ItemType.KONT ||  kz.getTyp() == ItemType.ZAK ? kz.getUpdatedBy() : null;

    private ComponentRenderer<HtmlComponent, KzTreeAware> rokCellRenderer = new ComponentRenderer<>(kz -> {
        HtmlComponent comp =  VzmFormatUtils.getRokComponent(kz.getRok());
        if (ItemType.KONT == kz.getTyp()) {
            comp.getStyle()
                    .set("padding-right", "1em");
        } else {
            comp.getStyle()
                    .set("text-indent", "1em");
        }
        return comp;
    });

    private ComponentRenderer<HtmlComponent, KzTreeAware> honorarCistyCellRenderer = new ComponentRenderer<>(kz -> {
        HtmlComponent comp =  VzmFormatUtils.getMoneyComponent(kz.getHonorarCisty());
        if (ItemType.KONT == kz.getTyp()) {
            comp.getStyle()
                    .set("padding-right", "1em");
        } else {
            comp.getStyle()
                    .set("text-indent", "1em");
        }
        return comp;
    });

    private ComponentRenderer<Component, KzTreeAware> kzArchRenderer = new ComponentRenderer<>(kz -> {
        ArchIconBox archBox = new ArchIconBox();
        archBox.showIcon(kz.getTyp(), kz.getArchState());
        return archBox;
    });

    private ComponentRenderer<Component, KzTreeAware> kzDigiRenderer = new ComponentRenderer<>(kz -> {
        DigiIconBox digiBox = new DigiIconBox();
        digiBox.showIcon(kz.getTyp(), kz.getDigiState());
        return digiBox;
    });

    private Component buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                initDownloadAnchor()
                , buildGridBarComponent()
                , initKzTreeGrid()
        );
        return gridContainer;
    }

    private Component initDownloadAnchor() {
        downloadAnchor = new Anchor();
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setId(KONT_DOWN_ANCHOR_ID);
        downloadAnchor.setText("Invisible KONT download link");    // setVisible  also disables a server part - cannot be useed
        downloadAnchor.getStyle().set("display", "none");
        return downloadAnchor;
    }

    private Component initKzTreeGrid() {
        kzTreeGrid = new TreeGrid<>();
        kzTreeGrid.getStyle().set("marginTop", "0.5em");

        kzTreeGrid.setColumnReorderingAllowed(true);
        kzTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        kzTreeGrid.setMultiSort(true);
        kzTreeGrid.getElement().addEventListener("keypress", e -> {
                JsonObject eventData = e.getEventData();
                String enterKey = eventData.getString("event.key");
                if ("Enter".equals(enterKey)) {
                    new Notification("ENTER pressed - will open form", 1500).open();
                }
            })
            .addEventData("event.key")
        ;

        DomEventListener gridKeyListener = new DomEventListener() {
            @Override
            public void handleEvent(DomEvent domEvent) {
                JsonObject eventData = domEvent.getEventData();
                String enterKey = eventData.getString("event.key");
                if ("Enter".equals(enterKey)) {
                    new Notification("ENTER pressed - will open form", 1500).open();
                }

            }
        };

        kzTreeGrid.addColumn(TemplateRenderer.of("[[index]]"))
                .setHeader("Řádek")
//                .setFooter("Zobrazeno položek: ")
                .setFlexGrow(0)
                .setWidth("4.5em")
//                .setFrozen(true)
        ;
        kzTreeGrid.addColumn(kzArchRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
        ;
        kzTreeGrid.addColumn(kzDigiRenderer)
                .setHeader(("DIGI"))
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
                .setKey(DIGI_COL_KEY)
        ;
        if (isZakFormsAccessGranted()) {
            kzTreeGrid.addColumn(new ComponentRenderer<>(this::buildKzOpenBtn))
                    .setHeader("Edit")
                    .setFlexGrow(0)
                    .setWidth("4em")
            ;
        }
        kzTreeGrid.addHierarchyColumn(ckzValProv)
                .setHeader("ČK|ČZ")
                .setFlexGrow(0)
                .setWidth("10em")
                .setResizable(true)
                .setKey(CKZ_COL_KEY)
        ;
        kzTreeGrid.addColumn(rokCellRenderer)
                .setHeader("Rok K|Z")
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(ROK_COL_KEY)
                .setComparator((kz1, kz2) ->
                        kz1.getRok().compareTo(kz2.getRok())
                )
        ;
        kzTreeGrid.addColumn(KzTreeAware::getSkupina).setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("2em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(SKUPINA_COL_KEY)
        ;
        if (isHonorareAccessGranted()) {
            kzTreeGrid.addColumn(honorarCistyCellRenderer)
                    .setHeader("Honorář č.")
                    .setFlexGrow(0)
                    .setWidth("9em")
                    .setResizable(true)
                    .setTextAlign(ColumnTextAlign.END)
                    .setKey(HONORAR_COL_KEY)
            ;
            kzTreeGrid.addColumn(menaValProv)
                    .setHeader("Měna")
                    .setFlexGrow(0)
                    .setWidth("6em")
                    .setResizable(true)
                    .setKey(MENA_COL_KEY)
            ;
        }
        kzTreeGrid.addColumn(initKzAvizoRenderer())
                .setHeader("Avízo")
                .setFlexGrow(0)
                .setWidth("6em")
                .setKey(AVIZO_COL_KEY)
                .setResizable(true)
        ;
//        kzTreeGrid.addColumn(alertRenderer)
        kzTreeGrid.addColumn(initKzAlertRenderer())
                .setHeader("Alert")
                .setFlexGrow(0)
                .setWidth("6em")
                .setKey(ALERT_COL_KEY)
                .setResizable(true)
        ;
        kzTreeGrid.addColumn(kzTextRenderer)
                .setHeader("Text")
                .setFlexGrow(0)
                .setWidth("22em")
                .setKey(TEXT_COL_KEY)
                .setResizable(true)
                .setComparator((kz1, kz2) ->
                        kz1.getText().compareTo(kz2.getText())
                )
        ;
        kzTreeGrid.addColumn(objednatelValProv)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setKey(OBJEDNATEL_COL_KEY)
                .setResizable(true)
        ;
        kzTreeGrid.addColumn(investorValProv)
                .setHeader("Investor")
                .setFlexGrow(0)
                .setWidth("9em")
                .setKey(INVESTOR_COL_KEY)
                .setResizable(true)
        ;
        kzTreeGrid.addColumn(datetimeUpdateCellRenderer)
                .setHeader("Změněno")
                .setFlexGrow(0)
                .setWidth("8em")
                .setKey(DATETIME_UPDATE_COL_KEY)
                .setResizable(true)
                .setComparator((kz1, kz2) ->
                        kz1.getDatetimeUpdate()
                                .compareTo(kz2.getDatetimeUpdate()))
        ;
        kzTreeGrid.addColumn(updatedByValProv)
                .setHeader("Změnil")
                .setFlexGrow(0)
                .setWidth("12em")
                .setKey(UPDATED_BY_COL_KEY)
                .setResizable(true)
        ;

        for (Grid.Column col : kzTreeGrid.getColumns()) {
            setResizable(col);
        }
        kzTreeGrid.getColumnByKey(CKZ_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(OBJEDNATEL_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(INVESTOR_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(ROK_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(DATETIME_UPDATE_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(UPDATED_BY_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(TEXT_COL_KEY).setSortable(true);


        initialSortOrder = Arrays.asList(
                new GridSortOrder(
                        kzTreeGrid.getColumnByKey(CKZ_COL_KEY), SortDirection.DESCENDING)
                , new GridSortOrder(
                        kzTreeGrid.getColumnByKey(ROK_COL_KEY), SortDirection.DESCENDING)
        );
        return kzTreeGrid;
    }


    private TreeData<KzTreeAware> loadKzTreeData(
            final String ckontFilterValue
            , final Integer rokFilterValue
            , final String objednatelFilterValue
            , final String archFilterValue
            , final String avizoFilterValue
    ) {
        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;
        return (new TreeData()).addItems(
                getFilteredKzList(
                        ckontFilterValue
                        , rokFilterValue
                        , objednatelFilterValue
                        , archFilterValue
                        , avizoFilterValue
                )
                , kzNodesProvider
        );
    }

    private List<? super Kont> getFilteredKzList(
            final String ckontFilterValue
            , final Integer rokFilterValue
            , final String objednatelFilterValue
            , final String archFilterValue
            , final String avizoFilterValue
    ) {
        List<? super Kont> kzList;
        if (null != rokFilterValue) {
            kzList = kontService.fetchByRokFilter(rokFilterValue);
        } else if (null != archFilterValue) {
            if (RADIO_KONT_ACTIVE.equals(archFilterValue)) {
                kzList = kontService.fetchHavingSomeZaksActiveFilter();
            } else if (RADIO_KONT_ARCH.equals(archFilterValue)) {
                kzList = kontService.fetchHavingAllZaksArchivedFilter();
            } else if (RADIO_KONT_EMPTY.equals(archFilterValue)) {
                kzList = kontService.fetchHavingNoZaksFilter();
            } else {
                kzList = kontService.fetchAll();
            }
        } else if (null != avizoFilterValue) {
            if (RADIO_AVIZO_GREEN.equals(avizoFilterValue)) {
                kzList = kontService.fetchHavingSomeZakAvizosGreen();
            } else if (RADIO_AVIZO_RED.equals(avizoFilterValue)) {
                kzList = kontService.fetchHavingSomeZakAvizosRed();
            } else {
                kzList = kontService.fetchAll();
            }
        } else if (StringUtils.isNotBlank(ckontFilterValue)) {
            kzList = kontService.fetchByCkontFilter(ckontFilterValue);
        } else if (StringUtils.isNotBlank(objednatelFilterValue)) {
            kzList = kontService.fetchByObjednatelFilter(objednatelFilterValue);
        } else {
            kzList = kontService.fetchAll();
        }
        return kzList;
    }

    private List<? extends Kont> getTopFilteredKzListForReport(
            final String ckontFilterValue
            , final Integer rokFilterValue
            , final String objednatelFilterValue
            , final String archFilterValue
            , final String avizoFilterValue
    ) {
        List<? extends Kont> kzList;
        if (null != rokFilterValue) {
            kzList = kontService.fetchTopByRokFilter(rokFilterValue);
        } else if (null != archFilterValue) {
            if (RADIO_KONT_ACTIVE.equals(archFilterValue)) {
                kzList = kontService.fetchTopHavingSomeZaksActiveFilter();
            } else if (RADIO_KONT_ARCH.equals(archFilterValue)) {
                kzList = kontService.fetchTopHavingAllZaksArchivedFilter();
            } else if (RADIO_KONT_EMPTY.equals(archFilterValue)) {
                kzList = kontService.fetchTopHavingNoZaksFilter();
            } else {
                kzList = kontService.fetchTop();
            }
        } else if (null != avizoFilterValue) {
            if (RADIO_AVIZO_GREEN.equals(avizoFilterValue)) {
                kzList = kontService.fetchTopHavingSomeZakAvizosGreen();
            } else if (RADIO_AVIZO_RED.equals(avizoFilterValue)) {
                kzList = kontService.fetchTopHavingSomeZakAvizosRed();
            } else {
                kzList = kontService.fetchTop();
            }
        } else if (StringUtils.isNotBlank(ckontFilterValue)) {
            kzList = kontService.fetchTopByCkontFilter(ckontFilterValue);
        } else if (StringUtils.isNotBlank(objednatelFilterValue)) {
            kzList = kontService.fetchTopByObjednatelFilter(objednatelFilterValue);
        } else {
            kzList = kontService.fetchTop();
        }
        return kzList;
    }

    private List<? extends Kont> getSelectedKontForReport(final String ckont) {
        return kontService.fetchTopByCkont(ckont);
    }

    private void assignDataProviderToGridAndSort(TreeDataProvider<KzTreeAware> kzTreeDataProvider) {
        List<GridSortOrder<KzTreeAware>> sortOrderRequired = kzTreeGrid.getSortOrder();
        kzTreeGrid.setDataProvider(kzTreeDataProvider);
//        inMemoryKzTreeProvider.setFilter(kz -> kz.getArch());
        if (CollectionUtils.isEmpty(sortOrderRequired)) {
            kzTreeGrid.sort(initialSortOrder);
        } else  {
            kzTreeGrid.sort(sortOrderRequired);
        }
    }

    private void setResizable(Grid.Column column) {
        column.setResizable(true);
        Element parent = column.getElement().getParent();
        while (parent != null
                && "vaadin-grid-column-group".equals(parent.getTag())) {
            parent.setProperty("resizable", "true");
            parent = parent.getParent();
        }
    }

//    <style>
//    #example1 {
//        border: 2px solid red;
//        padding: 10px;
//        border-radius: 25px;
//    }

    private ComponentRenderer initKzAvizoRenderer() {
//        avizoRenderer  = new ComponentRenderer<>(kz ->
        ComponentRenderer<Component, KzTreeAware> avizoRenderer = new ComponentRenderer<>(kz ->
            VzmFormatUtils.buildAvizoComponent(kz.getBeforeTerms(), kz.getAfterTerms(), false)
        );
        return avizoRenderer;
    }

    private ComponentRenderer initKzAlertRenderer() {
//        private ComponentRenderer<Component, KzTreeAware> alertRenderer = new ComponentRenderer<>(kzTreeAware -> {
        ComponentRenderer<Component, KzTreeAware> alertRenderer = new ComponentRenderer<>(kzTreeAware -> {
            AlertModifIconBox alertBox = new AlertModifIconBox();
            alertBox.showIcon(kzTreeAware.isAlerted() ?
                    AlertModifIconBox.AlertModifState.ACTIVE : AlertModifIconBox.AlertModifState.INACTIVE);
            return alertBox;
        });
        return alertRenderer;
    }

    private Component buildKzOpenBtn(KzTreeAware kz) {
        Button btn = null;
        if (ItemType.KONT == kz.getTyp()) {
            kontOrig = (Kont)kz;
            btn = new GridItemEditBtn(event -> kontFormDialog.openDialog((Kont)kz, Operation.EDIT)
                    , VzmFormatUtils.getItemTypeColorName(kz.getTyp()));
//            if (ItemType.KONT != kz.getTyp()) {
//                btn.getStyle()
//                        .set("padding-left", "1em")
//                ;
//            }
        } else if (ItemType.ZAK == kz.getTyp() || ItemType.AKV == kz.getTyp()) {
            zakOrig = (Zak)kz;
            btn = new GridItemEditBtn(event -> zakFormDialog.openDialog(false, (Zak)kz, Operation.EDIT)
                    , VzmFormatUtils.getItemTypeColorName(kz.getTyp()));
//            if (ItemType.KONT != kz.getTyp()) {
                btn.getStyle()
                        .set("padding-left", "1em")
                ;
//            }
        }
        return btn;
    }

    private Component buildGridBarComponent() {

        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setAlignItems(Alignment.END);
        gridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        Span ckontFilterLabel = new Span("Č.kont.:");
        ckontFilterLabel.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        ckontFilterField = new FilterTextField();
        ckontFilterField.setValue("");
        ckontFilterField.getStyle().set("alignItems", "center");
        ckontFilterField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        ckontFilterField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                rokFilterField.clear();
                archFilterRadio.clear();
                objednatelFilterField.clear();
                avizoFilterRadio.clear();
                updateViewContent();
            }
        });

        HorizontalLayout ckontFilterComponent = new HorizontalLayout();
        ckontFilterComponent.setMargin(false);
        ckontFilterComponent.setPadding(false);
        ckontFilterComponent.setAlignItems(Alignment.CENTER);
        ckontFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        ckontFilterComponent.add(
                ckontFilterLabel
                , ckontFilterField
        );


        Span rokFilterLabel = new Span("Rok:");
        rokFilterLabel.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        rokFilterField = buildSelectorField();
        rokFilterField.setItems(kontService.fetchKontRoks());
        rokFilterField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                ckontFilterField.clear();
                archFilterRadio.clear();
                objednatelFilterField.clear();
                avizoFilterRadio.clear();
                updateViewContent();
            }
        });

        HorizontalLayout rokFilterComponent = new HorizontalLayout();
        rokFilterComponent.setMargin(false);
        rokFilterComponent.setPadding(false);
        rokFilterComponent.setAlignItems(Alignment.CENTER);
        rokFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rokFilterComponent.add(
                rokFilterLabel
                , rokFilterField
        );


        Span objednatelFilterLabel = new Span("Obj.:");
        objednatelFilterLabel.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        objednatelFilterField = new FilterTextField();
        objednatelFilterField.setValue("");
        objednatelFilterField.getStyle().set("alignItems", "center");
        objednatelFilterField.getStyle().set("theme", "small");
        objednatelFilterField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        objednatelFilterField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                ckontFilterField.clear();
                rokFilterField.clear();
                archFilterRadio.clear();
                avizoFilterRadio.clear();
                updateViewContent();
            }
        });

        HorizontalLayout objednatelFilterComponent = new HorizontalLayout();
        objednatelFilterComponent.setMargin(false);
        objednatelFilterComponent.setPadding(false);
        objednatelFilterComponent.setAlignItems(Alignment.CENTER);
        objednatelFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        objednatelFilterComponent.add(
                objednatelFilterLabel
                , objednatelFilterField
        );

        Span archFilterLabel = new Span("Archiv:");
        archFilterLabel.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        archFilterRadio = new RadioButtonGroup<>();
        archFilterRadio.setItems(RADIO_KONT_ACTIVE, RADIO_KONT_ARCH, RADIO_KONT_EMPTY, RADIO_KONT_ALL);
        archFilterRadio.getStyle().set("alignItems", "center");
        archFilterRadio.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        archFilterRadio.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                ckontFilterField.clear();
                rokFilterField.clear();
                objednatelFilterField.clear();
                avizoFilterRadio.clear();
                updateViewContent();
            }
        });

        HorizontalLayout archFilterComponent = new HorizontalLayout();
        archFilterComponent.setMargin(false);
        archFilterComponent.setPadding(false);
        archFilterComponent.setAlignItems(Alignment.CENTER);
        archFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        archFilterComponent.add(
                archFilterLabel
                , archFilterRadio
        );


        Span avizoFilterLabel = new Span("Avízo:");
        avizoFilterLabel.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        avizoFilterRadio = new RadioButtonGroup<>();
        avizoFilterRadio.setItems(RADIO_AVIZO_GREEN, RADIO_AVIZO_RED, LIGHTS_ALL);
        avizoFilterRadio.getStyle().set("alignItems", "center");
        avizoFilterRadio.getStyle().set("font-size", "var(--lumo-font-size-xxs");
        avizoFilterRadio.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                ckontFilterField.clear();
                rokFilterField.clear();
                objednatelFilterField.clear();
                archFilterRadio.clear();
                updateViewContent();
            }
        });

        HorizontalLayout avizoFilterComponent = new HorizontalLayout();
        avizoFilterComponent.setMargin(false);
        avizoFilterComponent.setPadding(false);
        avizoFilterComponent.setAlignItems(Alignment.CENTER);
        avizoFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        avizoFilterComponent.add(
                avizoFilterLabel
                , avizoFilterRadio
        );


        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.getStyle().set("font-size", "var(--lumo-font-size-l");
        titleComponent.add(
                new GridTitle("KONT/ZAK")
                , new Ribbon()
                , initReloadButton()
        );

        gridBar.add(
                titleComponent
                , new Ribbon()
                , ckontFilterComponent
                , new Ribbon()
                , rokFilterComponent
                , new Ribbon()
                , objednatelFilterComponent
                , new Ribbon()
                , archFilterComponent
                , new Ribbon()
                , avizoFilterComponent
                , new Ribbon()
                , initNewKontButton()
                , new Ribbon()
                , initXlsReportMenu()
        );
//        kzToolBar.expand(ribbonExp);
        return gridBar;
    }

    Component initReloadButton() {
        reloadButton = new ReloadButton(event -> updateViewContent());
        return reloadButton;
    }

    /**
     * Refreshes tree grid without reloading data from DB
     */
    private void resortAndReselectTreeGrid() {
        resortAndReselectTreeGrid(null);
    }

    /**
     * Refreshes tree grid without reloading data from DB
     */
    private void resortAndReselectTreeGrid(final KzTreeAware itemToSelect) {
        List<GridSortOrder<KzTreeAware>> sortOrder = kzTreeGrid.getSortOrder();
        if (CollectionUtils.isEmpty(sortOrder)) {
            kzTreeGrid.sort(initialSortOrder);
        } else  {
            kzTreeGrid.sort(sortOrder);
        }

        selectItem(itemToSelect);
    }

    private void selectItem(final KzTreeAware itemToSelect) {
        kzTreeGrid.deselectAll();
        if (null != itemToSelect) {
            kzTreeGrid.select(itemToSelect);
        }
    }

    private void updateViewContent() {
        updateViewContent(null);
    }

    private void updateViewContent(final KzTreeAware itemToSelect) {
        String ckont = ckontFilterField.getValue();
        Integer rok = rokFilterField.getValue();
        String objednatel = objednatelFilterField.getValue();
        String arch = archFilterRadio.getValue();
        String avizo = avizoFilterRadio.getValue();
        kzTreeData = loadKzTreeData(ckont, rok, objednatel, arch, avizo);

        inMemoryKzTreeProvider = new TreeDataProvider<>(kzTreeData);
        assignDataProviderToGridAndSort(inMemoryKzTreeProvider);
        inMemoryKzTreeProvider.refreshAll();
        if (null != itemToSelect) {
            kzTreeGrid.select(itemToSelect);
        }
    }

    private Component initNewKontButton() {
        newKontButton = new NewItemButton("Kontrakt"
                , event -> {
                    Kont kont = new Kont( ItemType.KONT);
                    kont.setMena(Mena.CZK);
                    kont.setDateCreate(LocalDate.now());
                    kontFormDialog.openDialog(kont, Operation.ADD);
        });
        return newKontButton;
    }


    private String getReportFileName(ReportExporter.Format format) {
        return REP_KONT_SEL_FILE_NAME + "." + format.name().toLowerCase();
    }

    private SerializableSupplier<List<? extends Kont>> kontListReportSupplier =
            () -> {
                String ckont = ckontFilterField.getValue();
                Integer rok = rokFilterField.getValue();
                String arch = archFilterRadio.getValue();
                String avizo = avizoFilterRadio.getValue();
                String objednatel = objednatelFilterField.getValue();
                return getTopFilteredKzListForReport(ckont, rok, objednatel, arch, avizo);  // ..(buildZakBasicFilterParams());
            };

    private SerializableSupplier<List<? extends Kont>> kontSelectionReportSupplier =
            () -> {
                Set<KzTreeAware> itemSelection = kzTreeGrid.getSelectedItems();  // Suppose: SingleSelectionModel is set, only one (or none) item is present
                if (null == itemSelection || itemSelection.isEmpty()) {
                    return Collections.EMPTY_LIST;
                }
                return getSelectedKontForReport(itemSelection.iterator().next().getCkont());
            };

    private Component initXlsReportMenu() {
        Button btn = new Button(new Image("img/xls_down_24b.png", ""));
        btn.getElement().setAttribute("theme", "icon secondary small");
        btn.getElement().setProperty("title", "Kontrakty - report");

        ContextMenu menu = new ContextMenu();
        menu.addItem("Aktuální kontrakt", e -> updateXlsRepResourceAndDownload(kontSelectionReportSupplier));
        menu.addItem("Zobrazené kontrakty (max 50)", e -> updateXlsRepResourceAndDownload(kontListReportSupplier));
        menu.setOpenOnClick(true);

        menu.setTarget(btn);
        return btn;
    }

    private void updateXlsRepResourceAndDownload(SerializableSupplier<List<? extends Kont>> itemsSupplier) {
        ReportExporter.Format expFormat = ReportExporter.Format.XLS;
        String[] sheetNames = itemsSupplier.get().stream()
                .map(item -> item.getCkont())
                .toArray(String[]::new)
        ;
        final AbstractStreamResource xlsResource =
            reportXlsExporter.getXlsStreamResource(
                    new KontTreeXlsReportBuilder(), getReportFileName(expFormat), itemsSupplier, sheetNames
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

    public List<Map<String, Object>> getReportData() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Zak zak : zakService.fetchAll()) {
            Map<String, Object> item = new HashMap<>();
            item.put("ckz", zak.getCzak());
            item.put("text", zak.getText());
            item.put("skupina", zak.getSkupina());
            result.add(item);
        }
        return result;
    }
}
