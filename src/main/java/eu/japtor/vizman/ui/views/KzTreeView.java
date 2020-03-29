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
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import elemental.json.JsonObject;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.*;
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

//import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
//import com.vaadin.flow.data.provider.ListDataProvider;

@Route(value = ROUTE_KZ_TREE, layout = MainView.class)
@PageTitle(PAGE_TITLE_KZ_TREE)
//@Tag(TAG_ZAK)    // Kurvi layout
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
//        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
//public class KzTreeView extends VerticalLayout implements  AfterNavigationObserver, BeforeEnterObserver, HasLogger {
public class KzTreeView extends VerticalLayout implements HasLogger {

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archiv";
    private static final String RADIO_KONT_EMPTY = "Prázdné";
    private static final String RADIO_KONT_ALL = "Vše";

    private static final String CKZ_COL_KEY = "ckz-col";
    private static final String ARCH_COL_KEY = "arch-col";
    private static final String DIGI_COL_KEY = "digi-col";
    private static final String MENA_COL_KEY = "mena-col";
    private static final String HONORAR_COL_KEY = "honorar-col";
    private static final String AVIZO_COL_KEY = "avizo-col";
    private static final String TEXT_COL_KEY = "text-col";
    private static final String OBJEDNATEL_COL_KEY = "objednatel-col";
    private static final String SKUPINA_COL_KEY = "skupina-col";
    private static final String ROK_COL_KEY = "rok-col";

    private KontFormDialog kontFormDialog;
    private ZakFormDialog zakFormDialog;

    private TreeGrid<KzTreeAware> kzTreeGrid;
    private TreeData<KzTreeAware> kzTreeData;
    private TreeDataProvider<KzTreeAware> inMemoryKzTreeProvider;
    private Button reloadButton;
    private Button newKontButton;
    private FilterTextField ckontFilterField;
    private RadioButtonGroup<String> archFilterRadio;
    private ComponentRenderer<HtmlComponent, KzTreeAware> kzTextRenderer;
    private ComponentRenderer<Component, KzTreeAware> avizoRenderer;
    Select<Integer> rokFilterField;

    private List<GridSortOrder<KzTreeAware>> initialSortOrder;

    @Autowired
    public KontService kontService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public FaktService faktService;

    @Autowired
    public KlientService klientService;

    @Autowired
    public DochsumZakService dochsumZakService;

    @Autowired
    private CfgPropsCache cfgPropsCache;

//    @Override
//    public void afterNavigation(AfterNavigationEvent event) {
//        System.out.println("#### KZ Tree VIEW - after navigation");
////        assignDataProviderToGridAndSort(inMemoryKzTreeProvider);
////        inMemoryKzTreeProvider.refreshAll();
//    }
//
//    @Override
//    protected void onAttach(AttachEvent attachEvent) {
//        System.out.println("#### KZ Tree VIEW - onAttach");
//    }

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
        initView();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);

//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-treeGrid items="[[items]]" id="treeGrid" style="width: 100%;"></vaadin-treeGrid>
    }

//    private Consumer<Kont> deleteKontConsumer = kont -> deleteKontForGrid(kont);
//    private BiConsumer<Kont, Operation> saveKontBiConsumer = (kont, oper) ->  saveKontForGrid(kont, oper);



    @PostConstruct
    public void postInit() {

        kontFormDialog = new KontFormDialog(
                kontService, zakService, faktService, klientService, dochsumZakService
                , cfgPropsCache
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
                zakService, faktService, cfgPropsCache
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

//        archFilterRadio.getDataProvider().refreshItem();
//        updateZakGridContent();
    }

//    @Override
//    public void beforeEnter(BeforeEnterEvent event) {
//        // Navigation first goes here, then to the beforeEnter of MainView
////        System.out.println("###  KzTreeView.beforeEnter");
//        archFilterRadio.setValue(RADIO_KONT_ACTIVE);
//        reloadButton.click();
//    }
//
//    @Override
//    public void afterNavigation(AfterNavigationEvent event) {
//        // Triggers an event which will loadKzTreeData:
//        archFilterRadio.setValue(RADIO_KONT_ACTIVE);
//        reloadButton.click();
//    }
//
//    @Override
//    protected void onAttach(AttachEvent attachEvent) {
//        // Triggers an event which will loadKzTreeData:
//        archFilterRadio.setValue(RADIO_KONT_ACTIVE);
//        reloadButton.click();
//    }


    private void finishKontEdit(KontFormDialog kontFormDialog) {
        Kont kontAfter = kontFormDialog.getCurrentItem(); // Kont modified, just added or just deleted
        Operation kontOper = kontFormDialog.getCurrentOperation();
        OperationResult kontOperRes = kontFormDialog.getLastOperationResult();
        boolean kontZaksChanged = kontFormDialog.isKontZaksChanged();
        boolean kontZaksFaktsChanged = kontFormDialog.isKontZaksFaktsChanged();
        Kont kontOrig = kontFormDialog.getKontItemOrig();

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

    private void finishZakEdit(ZakFormDialog zakFormDialog) {
        Zak zakAfter = zakFormDialog.getCurrentItem(); // Modified, just added or just deleted
        String ckz = String.format("%s / %s", zakAfter.getCkont(), zakAfter.getCzak());
        Operation oper = zakFormDialog.getCurrentOperation();
        OperationResult zakOperRes = zakFormDialog.getLastOperationResult();

        boolean zakFaktsChanged = zakFormDialog.isZakFaktsChanged();
//        KzTreeAware kzItemOrig = zakFormDialog.getOrigItem();
        Zak zakOrig = zakFormDialog.getOrigItem();

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


//        if (zaksChanged && OperationResult.ITEM_DELETED != operRes) {
////            kzTreeData.getChildren(modKont).clear();
////            List<KzTreeAware> kontKzs = kzTreeData.getChildren(modKont);
////            if (null != kontKzs) {
////                for (KzTreeAware kz : kontKzs) {
////                    kzTreeData.removeItem(kz);
////                }
////            }
////            kzTreeData.addItems(modKont, ((KzTreeAware)modKont).getNodes());
//        }

//        kzTreeGrid.collapseRecursively(kzTreeGrid.getTreeData().getRootItems(),0);

//        KzTreeAware itemForSelection = (OperationResult.ITEM_DELETED == operRes) ?
//                getNeighborItemFromTree(kontAfter) : kontAfter;
//        if (null != itemForSelection) {
////            kzTreeGrid.expand(itemForSelection);
//            kzTreeGrid.getSelectionModel().select(itemForSelection);
//        }

//        if (OperationResult.ITEM_DELETED != operRes) {
//            KzTreeAware parent = kzTreeData.getParent(kzItemOrig);
//            if (null == parent) {
//                kzTreeGrid.expand(kzItemOrig);
//                kzTreeGrid.getSelectionModel().select(kzItemOrig);
//            } else {
//                kzTreeGrid.expand(parent);
//                kzTreeGrid.getSelectionModel().select(kzItemOrig);
//            }
//        }

//        kzTreeGrid.getDataProvider().refreshAll();

////        List<KzTreeAware> zaks = kzTreeGrid.getDataProvider()
//        Stream<KzTreeAware> zakStream = kzTreeGrid.getDataProvider()
//                .fetchChildren(new HierarchicalQuery(null, modKont));
//        List<KzTreeAware> zaks = zakStream.collect(Collectors.toList());
//        ((KzTreeAware)modKont).getNodes() Zaks(zaks);
//        Stream<KzTreeAware> stream = kzTreeGrid.getDataProvider().fetch(new Query<>(kz -> kz.getNodeId().equals(modKont.getNodeId())));


//        Stream<KzTreeAware> stream = kzTreeGrid.getDataProvider().fetch(new HierarchicalQuery(null, modKont));
//        Stream<KzTreeAware> childrenStream = kzTreeGrid.getDataProvider().fetchChildren(new HierarchicalQuery(null, modKont));
//
//        List<KzTreeAware> list = stream.collect(Collectors.toList());
//        List<KzTreeAware> childrenList = childrenStream.collect(Collectors.toList());

//        kzTreeGrid.getDataProvider().
//        } else {
////            if (null == archRadioValue || )
//            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//            kzTreeGrid.getDataProvider().refreshAll();
//
//        }
    }

    private void syncTreeGridAfterZakEdit(
            Zak zakOrig, Operation zakOper
            , OperationResult zakOperRes, boolean zakFaktsChanged
    ){
        if ((OperationResult.NO_CHANGE == zakOperRes) && !zakFaktsChanged) {
            selectItem(zakOrig);
            return;
        }

//        kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//        kzTreeGrid.getDataCommunicator().getDataProvider().refreshAll();
//        kzTreeGrid.getDataProvider().refreshAll();

        if (zakFaktsChanged) {
            updateViewContent();    // Workaround after fakts are deleted -> TreeGrid  is not updatetd  properly
        }

        Zak zakToSync = zakService.fetchOne(zakOrig.getId());
        KzTreeAware itemToReselect = (zakToSync == null) || (OperationResult.ITEM_DELETED == zakOperRes) ?
                getNeighborItemFromTree(zakOrig) : zakToSync;

//        Kont kontOrig = kontService.fetchOne(zakOrig.getKontId());
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
//        kzTreeGrid.getDataCommunicator().reset();
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




//    @DomEvent(value = "keypress", filter = "event.key == 'Enter'")
//    public class EnterPressEvent extends ComponentEvent<TextField> {
//        public EnterPressEvent(TextField source, boolean fromClient) {
//            super(source, fromClient);
//        }
//    }


    private ComponentRenderer initKzTextRenderer() {

        kzTextRenderer = new ComponentRenderer<>(kontZak -> {
            KzText kzText = new KzText(kontZak.getText());
            kzText.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(kontZak.getTyp()));
            if (ItemType.KONT != kontZak.getTyp()) {
                kzText.getStyle().set("text-indent", "1em");
            }

//        Paragraph comp = new Paragraph(kontZak.getText());
//        comp.addClickListener();
//        Paragraph comp = new Paragraph(kontZak.getText());
//        comp.addDoubleClickListener();
//        comp.addKeyPressListener(Key.ENTER, new ComponentEventListener<KeyPressEvent>() {
            //                }() -> {
//                    new Notification("ENTER pressed III - will open form", 1500).open();
//                })

            kzText.addKeyPressListener((ComponentEventListener<KeyPressEvent>) keyPressEvent -> {
                //                Notification.show("ENTER pressed III - will open form", 1500, Notification.Position.BOTTOM_END);
//                Notification.show("ENTER pressed III - will open form");
            });

            return kzText;
        });

        return kzTextRenderer;
    }


    private ValueProvider<KzTreeAware, String> ckzValProv =
            kz -> kz.getTyp() == ItemType.KONT ? kz.getCkont() : kz.getCzak().toString();

    private ValueProvider<KzTreeAware, String> menaValProv =
        kz -> kz.getTyp() == ItemType.KONT ? (null == kz.getMena() ? null : kz.getMena().name()) : null;

    private ValueProvider<KzTreeAware, String> klientValProv =
        kz -> kz.getTyp() == ItemType.KONT ? (null == kz.getKlient() ? null : kz.getKlient().getName()) : null;

//    ValueProvider<KzTreeAware, Boolean> archBooleanProv =
//            kz -> kz.getArch();


//    ValueProvider<KzTreeAware, String> RokzakProv = new ValueProvider() {
//        @Override
//        public Object apply(Object o) {
//            if (((KzTreeAware)o).getTyp() == ItemType.KONT) {
//                return null;
//            } else {
//                return ((KzTreeAware)o).getMena().name();
////                return "";
////                return ((KzTreeAware)o).getCkz().toString();
//            }
//        }
//    };

//    private ComponentRenderer<HtmlComponent, KzTreeAware> honorarCellRenderer = new ComponentRenderer<>(kz -> {
//        HtmlComponent comp =  VzmFormatUtils.getMoneyComponent(kz.getHonorar());
//        if (ItemType.KONT == kz.getTyp()) {
//////            comp.getStyle().set("color", "darkmagenta");
//////            return new Emphasis(kontZak.getHonorar().toString());
////            comp.getElement().appendChild(ElementFactory.createEmphasis(
////                    VzmFormatUtils.moneyFormat.format(kz.getHonorar())));
//            comp.getStyle()
////                    .set("color", "red")
////                    .set("text-indent", "1em");
//                    .set("padding-right", "1em");
//        } else {
//            comp.getStyle()
////                        .set("color", "red")
//                    .set("text-indent", "1em");
////            if ((null != kz) && (kz.getHonorar().compareTo(BigDecimal.ZERO) < 0)) {
////                comp.getStyle()
////                        .set("color", "red")
////                        .set("text-indent", "1em");
////            }
////            comp.getElement().appendChild(ElementFactory.createSpan(
////                    VzmFormatUtils.moneyFormat.format(kz.getHonorar())));
//        }
//        return comp;
//    });

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

  //    TextField searchField = new TextField("Hledej kontrakty...");
////    SearchField searchField = new SearchField("Hledej uživatele..."
////            , event -> ((ConfigurableFilterDataProvider) treeGrid.getDataProvider()).setFilter(event.getStringValue()));
//
//    ListDataProvider<Kont> kontDataProvider;
//
//



    private Component buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(buildGridToolBar());
        gridContainer.add(initKzTreeGrid());
        return gridContainer;
    }


//    ComponentRenderer<HtmlComponent, BigDecimal> moneyCellRenderer = new ComponentRenderer<>(money -> {
//        Div comp = new Div();
//        if ((null != money) && (money.compareTo(BigDecimal.ZERO) < 0)) {
//            comp.getStyle()
//                    .set("color", "red")
////                            .set("text-indent", "1em")
//            ;
//        }
//        comp.setText(VzmFormatUtils.moneyFormat.format(money));
//        return comp;
//    });

    private Component initKzTreeGrid() {
//        gridContainer.setClassName("view-container");
//        gridContainer.setAlignItems(Alignment.STRETCH);

        kzTreeGrid = new TreeGrid<>();
        kzTreeGrid.getStyle().set("marginTop", "0.5em");
//        kzTreeGrid.setWidth( "100%" );
//        kzTreeGrid.setHeight( null );

        kzTreeGrid.setColumnReorderingAllowed(true);
        kzTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        kzTreeGrid.setMultiSort(true);

//        GridSelectionModel<?> selectionMode = kzTreeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
//        ((GridMultiSelectionModel<?>) selectionMode).setSelectionColumnFrozen(true);
//        GridSelectionModel<?> selectionMode = kzTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        GridSelectionModel<?> selectionMode = kzTreeGrid.setSelectionMode(Grid.SelectionMode.NONE);


//        kzTreeGrid.addItemDoubleClickListener()setHeight( null );
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

//        treeGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
//        treeGrid.addColumn(Node::getId).setHeader("ID").setWidth("3em").setResizable(true);
//        treeGrid.addColumn(Node::getText).setHeader("Text").setWidth("8em").setResizable(true);
//        treeGrid.removeColumn(treeGrid.getColumnByKey("subNodes"));

        kzTreeGrid.addColumn(TemplateRenderer.of("[[index]]"))
                .setHeader("Řádek")
//                .setFooter("Zobrazeno položek: ")
                .setFlexGrow(0)
//                .setFrozen(true)
        ;
        kzTreeGrid.addColumn(kzArchRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("5em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
//                .setFrozen(true)
//                .setId("arch-column")
        ;
        kzTreeGrid.addColumn(kzDigiRenderer)
                .setHeader(("DIGI"))
                .setFlexGrow(0)
                .setWidth("5em")
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
                .setWidth("9em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(CKZ_COL_KEY)
        ;
        kzTreeGrid.addColumn(rokCellRenderer)
                .setHeader("Rok K|Z")
                .setFlexGrow(0)
                .setWidth("8em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(ROK_COL_KEY)
        ;
        kzTreeGrid.addColumn(KzTreeAware::getSkupina).setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("4em")
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
        kzTreeGrid.addColumn(kzTextRenderer)
                .setHeader("Text")
                .setFlexGrow(1)
                .setKey(TEXT_COL_KEY)
                .setResizable(true)
        ;
        kzTreeGrid.addColumn(klientValProv)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setKey(OBJEDNATEL_COL_KEY)
                .setResizable(true)
        ;


// Timto se da nejak manipulovat s checboxem:
//        treeGrid.addColumn(new ComponentRenderer<>(bean -> {
//            Button status = new Button(VaadinIcon.CIRCLE.create());
////            status.setClassName("hidden");
//            status.getElement().setAttribute("style", "color:#28a745");
//            return status;
//        }));

//        treeGrid.setItemDetailsRenderer(new ComponentRenderer<>(item -> {
//            Label label = new Label("Details opened! " + item);
//            label.setId("details-label");
//            return label;
//        }));

//        TreeData<Node> data = new TreeData<>();
//        data.addItems(null, rootNodes);
//        rootNodes.forEach(node -> data.addItems(node, node.getSubNodes()));
//        TreeDataProvider<Node> dataProvider = new TreeDataProvider<>(data);
//        treeGrid.setDataProvider(dataProvider);


//        rootNodes.forEach(rn -> treeGrid.getTreeData().addItem(null, rn));
//        rootNodes.forEach(rn -> rn.getSubNodes().forEach(
//                sn -> treeGrid.getTreeData().addItem(rn, sn)
//                )
//        );

//        kzTreeGrid.setSizeFull();
//        kzTreeGrid.setHeightByRows(true);

//        List<T extends KzTreeAware> kontList = kontService.fetchAll();
//        Set<? extends KzTreeAware> konts = null;
//        kzTreeGrid.setItems((Collection<KzTreeAware>) kontList, KzTreeAware::getNodes);



//        ConfigurableFilterDataProvider...
//        LazyHierarchicalKontProvider lazyDataProvider = new LazyHierarchicalKontProvider(kontService);
//        kzTreeGrid.setItems((Collection<KzTreeAware>) kontList, KzTreeAware::getNodes);
//        kzTreeGrid.setDataProvider(lazyDataProvider);


// =========  Filter row  ===========
//        HeaderRow filterRow = kzTreeGrid.appendHeaderRow();
//
//        TextField objednatelFilterField = new TextField();
//
////        ValueProvider<KzTreeAware, String> kzObjednatelValueProvider
////                        = KzTreeAware::getKlient;
//        objednatelFilterField.addValueChangeListener(event -> {});
//
////        objednatelFilterField.addValueChangeListener(event ->
////                        ((TreeDataProvider<KzTreeAware>)kzTreeGrid.getDataProvider())
//////                            .addFilter(KzTreeAware::getObjednatel, t ->
//////                                    StringUtils.containsIgnoreCase(t, objednatelFilterField.getStringValue())
//////                            )
////                            .addFilter(kz -> ItemType.KONT != kz.getTyp() || StringUtils.containsIgnoreCase(
////                                kz.getObjednatel(), objednatelFilterField.getStringValue())
////                            )
////        );
//        objednatelFilterField.setValueChangeMode(ValueChangeMode.EAGER);
//        filterRow.getCell(kzTreeGrid.getColumnByKey(OBJEDNATEL_COL_KEY)).setComponent(objednatelFilterField);
//        objednatelFilterField.setSizeFull();
//        objednatelFilterField.setPlaceholder("Filtr (rozbitý)");
//
//
//        TextField textFilterField = new TextField();
//        ValueProvider<KzTreeAware, String> kzTextValueProvider
//                = KzTreeAware::getText;
//        textFilterField.addValueChangeListener(event -> {});
////        textFilterField.addValueChangeListener(event ->
////                        ((TreeDataProvider<KzTreeAware>)kzTreeGrid.getDataProvider())
//////                            .addFilter(KzTreeAware::getText, t ->
//////                                    StringUtils.containsIgnoreCase(t, textFilterField.getStringValue())
//////                            )
////                                .addFilter(kz -> ItemType.KONT != kz.getTyp() || StringUtils.containsIgnoreCase(
////                                        kz.getText(), textFilterField.getStringValue())
////                                )
////        );
//        textFilterField.setValueChangeMode(ValueChangeMode.EAGER);
//        filterRow.getCell(kzTreeGrid.getColumnByKey(TEXT_COL_KEY)).setComponent(textFilterField);
//        textFilterField.setSizeFull();
//        textFilterField.setPlaceholder("Filtr (rozbitý)");
// =========  Filter row  ===========


        for (Grid.Column col : kzTreeGrid.getColumns()) {
            setResizable(col);
        }
        kzTreeGrid.getColumnByKey(CKZ_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(OBJEDNATEL_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(ROK_COL_KEY).setSortable(true);
        // TODO: nefunguje:
//        kzTreeGrid.getColumnByKey(TEXT_COL_KEY).setSortable(true);


        initialSortOrder = Arrays.asList(
//                new GridSortOrder(
//                        kzTreeGrid.getColumnByKey(ROK_COL_KEY), SortDirection.DESCENDING)
//                , new GridSortOrder(
//                        kzTreeGrid.getColumnByKey(CKZ_COL_KEY), SortDirection.DESCENDING)
                new GridSortOrder(
                        kzTreeGrid.getColumnByKey(CKZ_COL_KEY), SortDirection.DESCENDING)
                , new GridSortOrder(
                        kzTreeGrid.getColumnByKey(ROK_COL_KEY), SortDirection.DESCENDING)
        );

//        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;

//        List<? super Kont> kzList = kontService.fetchAll();
//        inMemoryKzTreeProvider
//                = new TreeDataProvider<KzTreeAware>((new TreeData()).addItems(kzList, kzNodesProvider));
//
//        inMemoryKzTreeProvider.setSortOrder(KzTreeAware::getCkont, SortDirection.DESCENDING);
////        inMemoryKzTreeProvider.setFilter(kz -> kz.getArch());
//        kzTreeGrid.setDataProvider(inMemoryKzTreeProvider);
//
////        treeGrid.getDataProvider().refreshItem(pojoItem);
//        kzTreeGrid.getDataProvider().refreshAll();
////        kzTreeGrid.expand(konts.get(0));

//        treeGrid.getTreeData().getRootItems().contains(item);
//        kzTreeGrid.getSelectedItems();


//        kontDataProvider = new ListDataProvider<>(kontService.fetchAll());
//
//        treeGrid.addCollapseListener(event -> {
//            Notification.show(
//                    "Project '" + event.getCollapsedItem().getName() + "' collapsed.",
//                    Type.TRAY_NOTIFICATION);
//        });
//        treeGrid.addExpandListener(event -> {
//            Notification.show(
//                    "Project '" + event.getExpandedItem().getName()+ "' expanded.",
//                    Type.TRAY_NOTIFICATION);
//        });


//        TreeGrid<Person> personGrid = new TreeGrid<>(Person.class);
//        personGrid.addColumn(Person::getName).setHeader("X-NAME");
//        personGrid.setHierarchyColumn("name");
//
//
////        List<Person> all = generatePersons();
////
//        Person dad = new Person("dad", null);
//        Person son = new Person("son", dad);
//        Person daughter = new Person("daughter", dad);
////        List<Person> all = Arrays.asList(dad, son, daughter);
////        return all;
////        all.forEach(p -> personGrid.getTreeData().addItem(p.getParent(), p));
//        personGrid.getTreeData().addItem(null, dad);
//        personGrid.getTreeData().addItem(dad, son);
//        personGrid.getTreeData().addItem(dad, daughter);




//        // some listeners for interaction
//        treeGrid.addCollapseListener(event -> Notification
//                .show("Kont. '" + event.getCollapsedItem().getName() + "' collapsed.", Notification.Type.TRAY_NOTIFICATION));
//        treeGrid.addExpandListener(event -> Notification
//                .show("Project '" + event.getExpandedItem().getName() + "' expanded.", Notification.Type.TRAY_NOTIFICATION));

//        treeGrid.setItemDetailsRenderer(new ComponentRenderer<>(kont -> {

//        initViewToolBar();

        return kzTreeGrid;
    }


    private TreeData<KzTreeAware> loadKzTreeData(
            final String ckontFilterValue, final Integer rokFilterValue, final String archFilterValue
    ) {
        List<? super Kont> kzList;
        if (null != rokFilterValue) {
            kzList = kontService.fetchByRokFilter(rokFilterValue);
        } else if (null != rokFilterValue) {
            if (RADIO_KONT_ACTIVE.equals(archFilterValue)) {
                kzList = kontService.fetchHavingSomeZaksActiveFilter();
            } else if (RADIO_KONT_ARCH.equals(archFilterValue)) {
                kzList = kontService.fetchHavingAllZaksArchivedFilter();
            } else if (RADIO_KONT_EMPTY.equals(archFilterValue)) {
                kzList = kontService.fetchHavingNoZaksFilter();
            } else {
                kzList = kontService.fetchAll();
            }
        } else if (StringUtils.isNotBlank(ckontFilterValue)) {
            kzList = kontService.fetchByCkontFilter(ckontFilterValue);
        } else {
            kzList = kontService.fetchAll();
        }

        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;
        return (new TreeData()).addItems(kzList, kzNodesProvider);
    }

    private void assignDataProviderToGridAndSort(TreeDataProvider<KzTreeAware> kzTreeDataProvider) {
        List<GridSortOrder<KzTreeAware>> sortOrderOrig = kzTreeGrid.getSortOrder();
        kzTreeGrid.setDataProvider(kzTreeDataProvider);
//        inMemoryKzTreeProvider.setFilter(kz -> kz.getArch());
        if (CollectionUtils.isEmpty(sortOrderOrig)) {
            kzTreeGrid.sort(initialSortOrder);
        } else  {
            kzTreeGrid.sort(sortOrderOrig);
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
        avizoRenderer  = new ComponentRenderer<>(kz ->
            VzmFormatUtils.buildAvizoComponent(kz.getBeforeTerms(), kz.getAfterTerms(), false)
        );
        return avizoRenderer;
    }


    private Component buildKzOpenBtn(KzTreeAware kz) {

//        ComponentEventListener listener =  null;
//        if (ItemType.KONT == kz.getTyp()) {
//            listener = event -> {
////                kontFolderOrig = ((Kont)kz).getFolder();
//                kontFormDialog.openDialog((Kont)kz, Operation.EDIT);
////                kontFormDialog.getLastOperationResult();
//
//            };
//        } else if (ItemType.ZAK == kz.getTyp() || ItemType.AKV == kz.getTyp()) {
//            listener = event -> zakFormDialog.openDialog((Zak)kz, Operation.EDIT);
//        }

//        Button btn = new GridItemEditBtn(listener, VzmFormatUtils.getItemTypeColorName(kz.getTyp()));

        Button btn = null;
        if (ItemType.KONT == kz.getTyp()) {
            btn = new GridItemEditBtn(event -> kontFormDialog.openDialog((Kont)kz, Operation.EDIT)
                    , VzmFormatUtils.getItemTypeColorName(kz.getTyp()));
            if (ItemType.KONT != kz.getTyp()) {
                btn.getStyle()
                        .set("padding-left", "1em")
                ;
            }
        } else if (ItemType.ZAK == kz.getTyp() || ItemType.AKV == kz.getTyp()) {
            btn = new GridItemEditBtn(event -> zakFormDialog.openDialog(false, (Zak)kz, Operation.EDIT)
                    , VzmFormatUtils.getItemTypeColorName(kz.getTyp()));
            if (ItemType.KONT != kz.getTyp()) {
                btn.getStyle()
                        .set("padding-left", "1em")
                ;
            }
        }

//        Button btn = new GridItemEditBtn(event ->
//                , VzmFormatUtils.getItemTypeColorName(kz.getTyp()));
//        if (ItemType.KONT != kz.getTyp()) {
//            btn.getStyle()
//                    .set("padding-left", "1em")
//            ;
//        }
        return btn;
    }


//    private void initZakGridWithDataProvider() {
//        treeGrid.setDataProvider(
//            (sortOrders, offset, limit) -> {
//                Map<String, Boolean> sortOrder = sortOrders.stream().collect(
//                        Collectors.toMap(sort -> sort.getSorted()
//                                , sort -> SortDirection.ASCENDING.equals( sort.getDirection())));
//            }
//            return service.findAll(offset, limit, sortOrder).stream(); }, () -> service.count() );
//    }

//    private void initZakProvider() {
//        DataProvider<Kont, String> kontDataProvider = DataProvider.fromFilteringCallbacks(
//                query -> {
//                    query.getOffset();
//                    query.getLimit();
//                    List<Kont> zaks = kontService.fetchBySearchFilter(
//                            query.getFilter().orElse(null),
//                            query.getSortOrders());
//                    return zaks.stream();
//
////                    int offset = query.getOffset();
////                    int limit = query.getLimit();
////                    personService.fetchBySearchFilter(filter)
////                    findByExample(repository, query.getFilter())
////                            .skip(query.getOffset())
////                            .take(query.getLimit())
////                    query.getFilter().orElse(null),
//                },
//                query -> (int) kontService.countBySearchFilter(query.getFilter().orElse(null))
//        );



//        kontDataProvider = new ListDataProvider<>(kontService.fetchAll());



//        DataProvider<Kont, String> kontDataProvider = DataProvider.fromFilteringCallbacks(
//                query -> {
//                    query.getOffset();
//                    query.getLimit();
//                    List<Kont> zaks = kontService.fetchBySearchFilter(
//                            query.getFilter().orElse(null),
//                            query.getSortOrders());
//                    return zaks.stream();
//
////                    int offset = query.getOffset();
////                    int limit = query.getLimit();
////                    personService.fetchBySearchFilter(filter)
////                    findByExample(repository, query.getFilter())
////                            .skip(query.getOffset())
////                            .take(query.getLimit())
////                    query.getFilter().orElse(null),
//                },
//                query -> (int) kontService.countBySearchFilter(query.getFilter().orElse(null))
//        );

//        treeGrid.setDataProvider(kontDataProvider);

//        personEditForm = new PersonFormDialog(
//                this::savePerson, this::deletePerson, personService, roleService.fetchAllRoles(), passwordEncoder);

//    }



//    protected void saveKontForGrid(Kont kont, Operation operation) {
//
////        Kont savedKont = kontFormDialog.saveKont(kont, operation);
////
//////        event -> {
//////            try {
//////                binder.writeBean(person);
//////                // A real application would also save the updated person
//////                // using the application's backend
//////            } catch (ValidationException e) {
//////                notifyValidationException(e);
//////            }
////
////        if (Operation.EDIT == operation && null != kontFolderOrig && !kontFolderOrig.equals(kont.getFolder())) {
//////            if (!VzmFileUtils.renameKontProjRoot(
//////                    getProjRootServer(), kont.getFolder(), kontFolderOrig)) {
//////                new OkDialog().open("Projektový adresář kontraktu"
//////                        , "Adresář se nepodařilo přejmenovat", "");
//////            };
//////            if (!VzmFileUtils.renameKontDocRoot(
//////                    getDocRootServer(), kont.getFolder(), kontFolderOrig)) {
//////                new OkDialog().open("Dokumentový adresář kontraktu"
//////                        , "Adresář se nepodařilo přejmenovat", "");
//////            };
////            if (!VzmFileUtils.kontDocRootExists(getDocRootServer(), kont.getFolder())) {
////                new OkDialog().open("Dokumentový adresář kontraktu"
////                        , "POZOR, dokumentový adresář kontraktu nenalezen, měl by se přejmenovat ručně", "");
////            }
////            if (!VzmFileUtils.kontProjRootExists(getProjRootServer(), kont.getFolder())) {
////                new OkDialog().open("Projektový adresáře kontraktu"
////                        , "POZOR, projektový adresář kontraktu nenalezen, měl by se přejmenovat ručně", "");
////            }
////
////        } else if (Operation.ADD == operation){
////            if (!VzmFileUtils.createKontProjDirs(getProjRootServer(), kont.getFolder())) {
////                new OkDialog().open("Projektové adresáře kontraktu"
////                        , "Adresářovou strukturu se nepodařilo vytvořit", "");
////            };
////            if (!VzmFileUtils.createKontDocDirs(getDocRootServer(), kont.getFolder())) {
////                new OkDialog().open("Dokumentové adresáře kontraktu"
////                        , "Adresářovou strukturu se nepodařilo vytvořit", "");
////            };
//////            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
//////            kontProjRootDir.setReadOnly();
////        } else {
////            new OkDialog().open("Adresáře zakázky"
////                    , "NEZNÁMÁ OPERACE", "")
////            ;
////        }
////
////        Kont savedKont = kontService.saveKont(kont);
//
//
////        if (Operation.EDIT == operation) {
////            kzTreeGrid.getDataProvider().refreshItem(savedKont);
////        } else {
//////            if (null == archRadioValue || )
////            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
////            kzTreeGrid.getDataProvider().refreshAll();
////
////        }
//
//
//
//        Kont savedKont = kontFormDialog.saveKont(kont, operation);
////
////        Notification.show("Kontrakt " + savedKont.getCkont() + " uložen"
////                , 2500, Notification.Position.TOP_CENTER);
////
////        if ((Operation.ADD == operation) && (archFilterRadio.getStringValue().equals(RADIO_KONT_ACTIVE))) {
////                archFilterRadio.setValue(RADIO_KONT_ARCH);
////        } else {
////                loadKzTreeData(archFilterRadio.getStringValue());
////        }
////
////        kzTreeGrid.expand(savedKont);
////        kzTreeGrid.select(savedKont);
//
//
//    }

//    private void refreshTreeAfterEdit(final KzTreeAware neibourghItem) {
//        loadKzTreeData(archFilterRadio.getStringValue());
//        kzTreeGrid.getSelectionModel().select(neibourghItem);
//    }

//    private void deleteKontForGrid(final Kont kontToDelete) {
//        int kontDelIdx = kzTreeGrid.getDataCommunicator().getIndex(kontToDelete);
//        Stream<KzTreeAware> stream = kzTreeGrid.getDataCommunicator()
//                .fetchFromProvider(kontDelIdx + 1, 1);
//        KzTreeAware newSelectedKont = getNeighborItemFromTree(kontToDelete);
//
//        try {
//            kontService.deleteKont(kontToDelete);
//            loadKzTreeData(archFilterRadio.getStringValue());
//            kzTreeGrid.getSelectionModel().select(newSelectedKont);
//
//            ConfirmDialog
//                    .createInfo()
//                    .withCaption("Zrušení kontraktu")
//                    .withMessage("Kontrakt " + kontToDelete.getCkont() + " byl zrušen.")
//                    .open()
//            ;
//
//        } catch(Exception e) {
////            getLogger().error("Error when deleting {} {} [operation: {}]", kontToDelete.getTyp().name()
////                    , kontToDelete.getCkont(), Operation.DELETE.name());
//            ConfirmDialog
//                    .createError()
//                    .withCaption("Zrušení kontraktu")
//                    .withMessage("Kontrakt " + kontToDelete.getCkont() + " se nepodařilo zrušit.")
//                    .open();
//            throw e;
//        }
//    }


//    private Grid<Zak> initZakGrid() {
//        zakGrid.setSizeFull();
//        zakGrid.getElement().setAttribute("colspan", "2");
//        zakGrid.setMultiSort(false);
//        zakGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        zakGrid.setHeightByRows(true);
//        zakGrid.setWidth("900px");
////        zakGrid.setHeight(null);
////        zakGrid.getElement().setAlignItems(FlexComponent.Alignment.STRETCH);
//
//        // TODO: ID -> CSS ?
//        zakGrid.setId("zak-grid");  // .. same ID as is used in shared-styles grid's dom module
//        zakGrid.addColumn(Zak::getCkz).setHeader("ČZ").setWidth("3em").setResizable(true)
//                .setSortProperty("poradi");
////        zakGrid.addColumn(new ComponentRenderer<>(this::createOpenDirButton)).setFlexGrow(0);
//        zakGrid.addColumn(Zak::getRokmeszad).setHeader("Zadáno").setWidth("3em").setResizable(true);
//        zakGrid.addColumn(Zak::getText).setHeader("Text zak.").setWidth("5em").setResizable(true);
//        zakGrid.addColumn(Zak::getHonorc).setHeader("Honorář").setWidth("3em").setResizable(true);
//        zakGrid.addColumn(Zak::getSkupina).setHeader("Skupina").setWidth("4em").setResizable(true);
//
//        return zakGrid;
//    }

//    private void deleteZakForGrid(Zak zak) {
//        String ckzDel = String.format("%s / %d", zak.getCkont(), zak.getCkz());
//        try {
//            boolean zakWasDeleted = zakService.deleteZak(zak);
//
//            if (!zakWasDeleted) {
//                ConfirmDialog
//                        .createError()
//                        .withCaption("Zrušení zakázky")
//                        .withMessage(String.format("Chyba při rušení zakázky %s .", ckzDel))
//                        .open();
//            } else {
//                kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//                kzTreeGrid.getDataProvider().refreshAll();
//                getLogger().info(String.format("ZAKAZKA %s deleted", ckzDel));
//                Notification.show(String.format("Zakázka %s zrušena.", ckzDel)
//                        , 2500, Notification.Position.TOP_CENTER)
//                ;
//                ConfirmDialog
//                        .createInfo()
//                        .withCaption("Zrušení zakázky")
//                        .withMessage("Zakázka " + ckzDel + " byla zrušena.")
//                        .open()
//                ;
//            }
//        } catch (Exception e) {
//            getLogger().error(String.format("Error during deletion ZAKAZKA %s / %d", zak.getCkont(), zak.getCkz()), e);
//            ConfirmDialog
//                    .createError()
//                    .withCaption("Zrušení zakázky")
//                    .withMessage(String.format("Chyba při rušení zakázky %s .", ckzDel))
//                    .open()
//            ;
//        }
//    }


    private Component buildGridToolBar() {

//        kzToolBar = new HorizontalLayout();
//        FlexLayout kzToolBar;
        HorizontalLayout kzToolBar = new HorizontalLayout();
        kzToolBar.setSpacing(false);
        kzToolBar.setAlignItems(Alignment.END);
        kzToolBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);


        Button expandAllBtn = new Button("Rozbalit vše", VaadinIcon.CHEVRON_DOWN.create()
                , e -> kzTreeGrid.expandRecursively(kzTreeGrid.getTreeData().getRootItems(),2));
//                , e -> kzTreeGrid.expand());

        Button collapseAllBtn = new Button("Sbalit vše", VaadinIcon.CHEVRON_UP.create()
                , e -> kzTreeGrid.collapseRecursively(kzTreeGrid.getTreeData().getRootItems(),2));

        Span ckontFilterLabel = new Span("Č.kont.:");
        ckontFilterField = new FilterTextField();
        ckontFilterField.setValue("");
        ckontFilterField.getStyle().set("alignItems", "center");
        ckontFilterField.getStyle().set("theme", "small");
        ckontFilterField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        ckontFilterField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                rokFilterField.clear();
                archFilterRadio.clear();
            }
            updateViewContent();
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
        rokFilterField = buildSelectorField();
        rokFilterField.setItems(kontService.fetchKontRoks());
        rokFilterField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                ckontFilterField.clear();
                archFilterRadio.clear();
            }
            updateViewContent();
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


        Span archFilterLabel = new Span("Archiv:");
        archFilterRadio = new RadioButtonGroup<>();
        archFilterRadio.setItems(RADIO_KONT_ACTIVE, RADIO_KONT_ARCH, RADIO_KONT_EMPTY, RADIO_KONT_ALL);
        archFilterRadio.getStyle().set("alignItems", "center");
        archFilterRadio.getStyle().set("theme", "small");
        archFilterRadio.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                ckontFilterField.clear();
                rokFilterField.clear();
            }
            updateViewContent();
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

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle("KONTRAKTY / ZAKÁZKY")
                , new Ribbon()
                , initReloadButton()
        );

        kzToolBar.add(
                titleComponent
                , new Ribbon()
                , ckontFilterComponent
                , new Ribbon()
                , rokFilterComponent
                , new Ribbon()
                , archFilterComponent
                , new Ribbon()
                , initNewKontButton()
//                , initZakRepButton()
        );
//        kzToolBar.expand(ribbonExp);


//        Button buttonPrevious = new Button("Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
//        Button buttonNext = new Button("Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
//        buttonNext.setIconAfterText(true);
//
//
//        // simulate the date picker light that we can use in polymer
//        DatePicker gotoDate = new DatePicker();
//        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getStringValue()));
//        gotoDate.getElement().getStyle().set("visibility", "hidden");
//        gotoDate.getElement().getStyle().set("position", "fixed");
//        gotoDate.setWidth("0px");
//        gotoDate.setHeight("0px");
//        gotoDate.setWeekNumbersVisible(true);
//        buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
//        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
//        buttonDatePicker.addClickListener(event -> gotoDate.open());
//
//        Button buttonHeight = new Button("Calendar height", event -> new HeightDialog().open());
//
//        Checkbox cbWeekNumbers = new Checkbox("Week numbers", event -> calendar.setWeekNumbersVisible(event.getStringValue()));
//
//        ComboBox<Locale> comboBoxLocales = new ComboBox<>();
//
//        List<Locale> items = Arrays.asList(CalendarLocale.getAvailableLocales());
//        comboBoxLocales.setItems(items);
//        comboBoxLocales.setValue(CalendarLocale.getDefault());
//        comboBoxLocales.addValueChangeListener(event -> calendar.setLocale(event.getStringValue()));
//        comboBoxLocales.setRequired(true);
//        comboBoxLocales.setPreventInvalidInput(true);
//
//        ComboBox<GroupEntriesBy> comboBoxGroupBy = new ComboBox<>("");
//        comboBoxGroupBy.setPlaceholder("Group by...");
//        comboBoxGroupBy.setItems(GroupEntriesBy.values());
//        comboBoxGroupBy.setItemLabelGenerator(item -> {
//            switch (item) {
//                default:
//                case NONE:
//                    return "none";
//                case RESOURCE_DATE:
//                    return "group by resource / date";
//                case DATE_RESOURCE:
//                    return "group by date / resource";
//            }
//        });
//        comboBoxGroupBy.addValueChangeListener(event -> ((Scheduler) calendar).setGroupEntriesBy(event.getStringValue()));
//
//        timezoneComboBox = new ComboBox<>("");
//        timezoneComboBox.setItemLabelGenerator(Timezone::getClientSideValue);
//        timezoneComboBox.setItems(Timezone.getAvailableZones());
//        timezoneComboBox.setValue(Timezone.UTC);
//        timezoneComboBox.addValueChangeListener(event -> {
//            Timezone value = event.getStringValue();
//            calendar.setTimezone(value != null ? value : Timezone.UTC);
//        });
//
//        Button addThousand = new Button("Add 1000 entries", event -> {
//            Button source = event.getSource();
//            source.setEnabled(false);
//            source.setText("Creating...");
//            Optional<UI> optionalUI = getUI();
//            optionalUI.ifPresent(ui -> {
//                Executors.newSingleThreadExecutor().execute(() -> {
//                    Timezone timezone = new Timezone(ZoneId.systemDefault());
//                    Instant start = timezone.convertToUTC(LocalDate.now());
//                    Instant end = timezone.convertToUTC(LocalDate.now().plusDays(1));
//                    List<Entry> list = IntStream.range(0, 1000).mapToObj(i -> {
//                        Entry entry = new Entry();
//                        entry.setStart(start);
//                        entry.setEnd(end);
//                        entry.setAllDay(true);
//                        entry.setTitle("Generated " + (i + 1));
//                        return entry;
//                    }).collect(Collectors.toList());
//
//                    ui.access(() -> {
//                        calendar.addEntries(list);
//                        source.setVisible(false);
//                        Notification.show("Added 1,000 entries for today");
//                    });
//                });
//            });
//        });
        return kzToolBar;
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

//        inMemoryKzTreeProvider = new TreeDataProvider<>(treeData);
//        kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//        inMemoryKzTreeProvider.setFilter(kz -> kz.getArch());
//        inMemoryKzTreeProvider.refreshAll();
        List<GridSortOrder<KzTreeAware>> sortOrder = kzTreeGrid.getSortOrder();
//        kzTreeGrid.setDataProvider(inMemoryKzTreeProvider);

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
        String arch = archFilterRadio.getValue();
        kzTreeData = loadKzTreeData(ckont, rok, arch);

        inMemoryKzTreeProvider = new TreeDataProvider<>(kzTreeData);
        assignDataProviderToGridAndSort(inMemoryKzTreeProvider);
        inMemoryKzTreeProvider.refreshAll();
        if (null != itemToSelect) {
            kzTreeGrid.select(itemToSelect);
        }
    }


//    private void updateViewContent(String archFilter) {
//        loadKzTreeData(archFilter);
//        inMemoryKzTreeProvider.refreshAll();

//        KzTreeAware itemForSelection = kzTreeData.getRootItems().get(0);
//        if (null != itemForSelection) {
//            kzTreeGrid.getSelectionModel().select(itemForSelection);
//        }

//        kzTreeGrid.setInitialFilterValues();
//        kzTreeGrid.doFilter();
//        kzTreeGrid.getDataProvider().refreshAll();
//    }


//    //    private void initViewToolBar(final Button reloadViewButton, final Button newItemButto)
//    private Component initViewToolBar() {
//        // Build view toolbar
//        viewToolBar.setWidth("100%");
//        viewToolBar.setPadding(true);
//        viewToolBar.getStyle()
//                .set("padding-bottom","5px");
//
//        Span viewTitle = new Span(TITLE_KZ_TREE.toUpperCase());
//        viewTitle.getStyle()
//                .set("font-size","var(--lumo-font-size-l)")
//                .set("font-weight","600")
//                .set("padding-right","0.75em");
//
////        searchField = new SearchField(
//////                "Hledej uživatele...", event ->
//////                "Hledej uživatele...", event -> updateZakGridContent()
////                "Hledej uživatele...",
////                event -> ((ConfigurableFilterDataProvider) treeGrid.getDataProvider()).setFilter(event.getStringValue())
////        );        toolBarSearch.add(viewTitle, searchField);
//
////        HorizontalLayout searchToolBar = new HorizontalLayout(viewTitle, searchField);
////        searchToolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
//
////        HorizontalLayout kzToolBar = new HorizontalLayout(reloadViewButton);
////        kzToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
////
////        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
////        toolBarItem.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
//
////        kzToolBar.add(searchToolBar, kzToolBar, ribbon, toolBarItem);
//        Ribbon ribbon = new Ribbon("3em");
//        viewToolBar.expand(ribbon);
//        return viewToolBar;
//    }

    private Component initNewKontButton() {
        newKontButton = new NewItemButton("Kontrakt"
                , event -> {
                    Kont kont = new Kont( ItemType.KONT);
//                    kont.setInvestor("Inv 1");
//                    kont.setObjednatel("Obj 1");
                    kont.setMena(Mena.CZK);
                    kont.setDateCreate(LocalDate.now());
//                    kont.setCkont("01234");
                    kontFormDialog.openDialog(kont, Operation.ADD);
        });
        return newKontButton;
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
// ===============================================================================


//    public static class Person {
//        private String name;
//        private Person parent;
//
//        public String getName() {
//            return name;
//        }
//
//        public Person getParent() {
//            return parent;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setParent(Person parent) {
//            this.parent = parent;
//        }
//
//        public Person(String name, Person parent) {
//            this.name = name;
//            this.parent = parent;
//        }
//
////        @Override
////        public String toString() {
////            return name;
////        }
////
////        @Override
////        public boolean equals(Object o) {
////            if (this == o) return true;
////            if (o == null || getClass() != o.getClass()) return false;
////
////            Person person = (Person) o;
////
////            if (name != null ? !name.equals(person.name) : person.name != null) return false;
////            return parent != null ? parent.equals(person.parent) : person.parent == null;
////        }
////
////        @Override
////        public int hashCode() {
////            int result = name != null ? name.hashCode() : 0;
////            result = 31 * result + (parent != null ? parent.hashCode() : 0);
////            return result;
////        }
//    }
//
//
////    private void initSimplePersonGrid() {
////        TreeGrid<Person> personGrid = new TreeGrid<>(Person.class);
////        personGrid.addColumn(Person::getName).setHeader("X-NAME");
////        personGrid.setHierarchyColumn("name");
////
////
//////        List<Person> all = generatePersons();
//////
////        Person dad = new Person("dad", null);
////        Person son = new Person("son", dad);
////        Person daughter = new Person("daughter", dad);
//////        List<Person> all = Arrays.asList(dad, son, daughter);
//////        return all;
//////        all.forEach(p -> personGrid.getTreeData().addItem(p.getParent(), p));
////        personGrid.getTreeData().addItem(null, dad);
////        personGrid.getTreeData().addItem(dad, son);
////        personGrid.getTreeData().addItem(dad, daughter);
////
////    }
////
////
////    private List<Person> generatePersons() {
////
////        Person dad = new Person("dad", null);
////        Person son = new Person("son", dad);
////        Person daughter = new Person("daughter", dad);
////        List<Person> all = Arrays.asList(dad, son, daughter);
////        return all;
////    }
//
//
//
////    public class HeightDialog extends Dialog {
////        HeightDialog() {
////            VerticalLayout dialogContainer = new VerticalLayout();
////            add(dialogContainer);
////
////            TextField heightInput = new TextField("", "500", "e. g. 300");
////            Button byPixels = new Button("Set by pixels", e -> {
////                kzTreeGrid.setHeight(Integer.valueOf(heightInput.getStringValue()));
////
////                this.setSizeUndefined();
////                setFlexStyles(false);
////            });
////            byPixels.getElement().setProperty("title", "Calendar height is fixed by pixels.");
////            dialogContainer.add(new HorizontalLayout(heightInput, byPixels));
////
//////            Button autoHeight = new Button("Auto height", e -> {
//////                kzTreeGrid.setHeightAuto();
//////
//////                this.setSizeUndefined();
//////                setFlexStyles(false);
//////            });
//////            autoHeight.getElement().setProperty("title", "Calendar height is set to auto.");
//////            dialogContainer.add(autoHeight);
////
//////            Button heightByBlockParent = new Button("Height by block parent", e -> {
//////                kzTreeGrid.setHeightByParent();
//////                kzTreeGrid.setSizeFull();
//////
//////                this.setSizeFull();
//////                setFlexStyles(false);
//////            });
//////            heightByBlockParent.getElement().setProperty("title", "Container is display:block + setSizeFull(). Calendar height is set to parent + setSizeFull(). Body element kept unchanged.");
//////            dialogContainer.add(heightByBlockParent);
////
//////            Button heightByBlockParentAndCalc = new Button("Height by block parent + calc()", e -> {
//////                calendar.setHeightByParent();
//////                calendar.getElement().getStyle().set("height", "calc(100vh - 450px)");
//////
//////                Demo.this.setSizeFull();
//////                setFlexStyles(false);
//////            });
//////            heightByBlockParentAndCalc.getElement().setProperty("title", "Container is display:block + setSizeFull(). Calendar height is set to parent + css height is calculated by calc(100vh - 450px) as example. Body element kept unchanged.");
//////            dialogContainer.add(heightByBlockParentAndCalc);
////
////            Button heightByFlexParent = new Button("Height by flex parent", e -> {
////                calendar.setHeightByParent();
////
////                Demo.this.setSizeFull();
////                setFlexStyles(true);
////            });
////            heightByFlexParent.getElement().setProperty("title", "Container is display:flex + setSizeFull(). Calendar height is set to parent + flex-grow: 1. Body element kept unchanged.");
////            dialogContainer.add(heightByFlexParent);
////
////            Button heightByFlexParentAndBody = new Button("Height by flex parent and flex body", e -> {
////                calendar.setHeightByParent();
////
////                Demo.this.setSizeUndefined();
////                setFlexStyles(true);
////
////                UI.getCurrent().getElement().getStyle().set("display", "flex");
////            });
////            heightByFlexParentAndBody.getElement().setProperty("title", "Container is display:flex. Calendar height is set to parent + flex-grow: 1. Body element is set to display: flex.");
////            dialogContainer.add(heightByFlexParentAndBody);
////        }
////    }

// ===============================================================================

//    public interface TreeAware {
//
//        String getName();
//        int getHoursDone();
//        Date getLastModified();
//        Collection<TreeAware> getSubNodes();
//        void setSubNodes(List<TreeAware> subNodes);
//
//    }
//
//    public static class Node implements TreeAware {
//
////        Long id;
//        String name;
////        Long parentId;
//        private Collection<TreeAware> subNodes = new ArrayList<>();
//
////        public Node(Long id, String text, Long parentId) {
//        public Node(String name) {
////            this.id = id;
//            this.name = name;
////            this.parentId = parentId;
//        }
//
////        public Long getId() {
////          return id;
////        }
////
////        public void setId(Long id) {
////          this.id = id;
////        }
//
//        public String getName() {
//          return name;
//        }
//
//        public void setName(String name) {
//          this.name = name;
//        }
//
//        public void setSubNodes(List<TreeAware> subNodes) {
//            this.subNodes = subNodes;
//        }
//
//        @Override
//        public Collection<TreeAware> getSubNodes() {
//            return this.subNodes;
//        }
//
//        public void addSubNode(TreeAware subNode) {
//           subNodes.add(subNode);
//        }
//
//        @Override
//        public int getHoursDone() {
//            return getSubNodes().stream()
//                    .map(TreeAware::getHoursDone)
//                    .reduce(0, Integer::sum);
//        }
//
//        @Override
//        public Date getLastModified() {
//            return getSubNodes().stream()
//                    .map(TreeAware::getLastModified)
//                    .max(Date::compareTo).orElse(null);
//        }

//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            Node node = (Node) o;
//
//            if (name != null ? !name.equals(node.name) : node.name != null) return false;
//            return subNodes != null ? subNodes.equals(node.subNodes) : node.subNodes == null;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = name != null ? name.hashCode() : 0;
//            result = 31 * result + (subNodes != null ? subNodes.hashCode() : 0);
//            return result;
//        }
//        public Long getParentId() {
//          return parentId;
//        }
//
//        public void setParentId(Long parentId) {
//          this.parentId = parentId;
//        }
//    }

//    private List<TreeAware> generateNodes() {
//        List<TreeAware> rootNodes = new ArrayList<>();
//
//        for (int year = 2010; year <= 2016; year++) {
//            Node rootNode = new Node("Node " + year);
//
//            for (int i = 1; i < 2 + rand.nextInt(5); i++) {
////                Node nextNode = new LeafNode("Sub node " + year + " - " + i, rand.nextInt(100), year);
//                TreeAware nextNode = new Node("Sub node " + year + " - " + i);
//                nextNode.setSubNodes(Arrays.asList(
//                        new LeafNode("Implementation", rand.nextInt(100), year),
//                        new LeafNode("Planning", rand.nextInt(10), year),
//                        new LeafNode("Prototyping", rand.nextInt(20), year)));
//                rootNode.addSubNode(nextNode);
//            }
//            rootNodes.add(rootNode);
//        }
//        return rootNodes;
//    }
//
//    class LeafNode extends Node {
//
//        private int hoursDone;
//        private Date lastModified;
//
//
//        private LeafNode(String name, int hoursDone, int year) {
//            super(name);
//            this.hoursDone = hoursDone;
//            lastModified = new Date(year - 1900, rand.nextInt(12), rand.nextInt(10));
//        }
//
//        @Override
//        public int getHoursDone() {
//            return hoursDone;
//        }
//
//        @Override
//        public Date getLastModified() {
//            return lastModified;
//        }
//    }
//
//
//    private void initNodeTreeGrid() {
//        TreeGrid<TreeAware> treeGrid;
//
//        treeGrid = new TreeGrid<>();
//        treeGrid.setWidth( "100%" );
//        treeGrid.setHeight( null );
//        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
//
//        treeGrid.addHierarchyColumn(TreeAware::getName).setHeader(("Name"))
//                .setFlexGrow(0).setWidth("340px")
//                .setResizable(true).setFrozen(true).setId("name-column");
//        treeGrid.addColumn(TreeAware::getHoursDone).setHeader("Hours Done");
//        treeGrid.addColumn(TreeAware::getLastModified).setHeader("Last Modified");
////        treeGrid.setHierarchyColumn("name");
//
//        treeGrid.setItemDetailsRenderer(new ComponentRenderer<>(item -> {
//            Label label = new Label("Details opened! " + item);
//            label.setId("details-label");
//            return label;
//        }));
//
//        List<TreeAware> rootNodes = generateNodes();
//        treeGrid.setItems(rootNodes, TreeAware::getSubNodes);
//
//        treeGrid.getDataProvider().refreshAll();
//        treeGrid.expand(rootNodes.get(0));
//        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
////        treeGrid.getTreeData().getRootItems().contains(item);
//    }


}
