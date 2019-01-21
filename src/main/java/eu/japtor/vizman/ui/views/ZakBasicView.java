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
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.JsonObject;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.KontFormDialog;
import eu.japtor.vizman.ui.forms.ZakFormDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static eu.japtor.vizman.app.security.SecurityUtils.isMoneyAccessGranted;
import static eu.japtor.vizman.ui.util.VizmanConst.*;

//import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
//import com.vaadin.flow.data.provider.ListDataProvider;

@Route(value = ROUTE_KZ_TREE, layout = MainView.class)
@PageTitle(PAGE_TITLE_KZ_TREE)
//@Tag(TAG_ZAK)    // Kurvi layout
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class ZakBasicView extends VerticalLayout implements BeforeEnterObserver {

    private static final String RADIO_KONT_ACTIVE = "Aktivní";
    private static final String RADIO_KONT_ARCH = "Archivované";
    private static final String RADIO_KONT_ALL = "Všechny";

    private static final String CKZ_COL_KEY = "ckz-col";
    private static final String ARCH_COL_KEY = "arch-col";
    private static final String MENA_COL_KEY = "mena-col";
    private static final String HONORAR_COL_KEY = "honorar-col";
    private static final String OVERTERM_COL_KEY = "overterm-col";
    private static final String TEXT_COL_KEY = "text-col";
    private static final String OBJEDNATEL_COL_KEY = "objednatel-col";
    private static final String SKUPINA_COL_KEY = "skupina-col";
    private static final String ROK_COL_KEY = "rok-col";

    Random rand = new Random();
    private final H3 kontHeader = new H3(TITLE_KZ_TREE);

    private Kont kontOrig;
    private Zak zakOrig;

    private KontFormDialog kontFormDialog;
    private ZakFormDialog zakFormDialog;
//    private final Grid<Kont> kontGrid = new Grid<>();
//    private final Grid<Zak> zakGrid = new Grid<>();

    private TreeGrid<KzTreeAware> kzTreeGrid;
    private TreeDataProvider<KzTreeAware> inMemoryKzTreeProvider;
    private Button newKontButton;
    private RadioButtonGroup<String> archFilterRadio;
    private ComponentRenderer<HtmlComponent, KzTreeAware> kzTextRenderer;

    VerticalLayout gridContainer;
    HorizontalLayout viewToolBar = new HorizontalLayout();
//    HorizontalLayout toolBarSearch = new HorizontalLayout();

    //    @Autowired
//    public KontRepo kontRepo;
//
    @Autowired
    public KontService kontService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public FaktService faktService;

    @Autowired
    private CfgPropsCache cfgPropsCache;


//    class KzText extends HtmlComponent implements KeyNotifier {
    static class KzText extends Paragraph implements KeyNotifier {
        public KzText(String text) {
            super(text);
        }
    }



    @PostConstruct
    public void init() {

        kontFormDialog = new KontFormDialog(
                this::saveKont, this::deleteKont
                , kontService, zakService, faktService
                , cfgPropsCache
        );

        zakFormDialog = new ZakFormDialog(
                this::saveZak, this::deleteZak
                , zakService, faktService, cfgPropsCache);

        initKzTextRenderer();

        initView();
        initZakProvider();
        this.add(initGridContainer());

        archFilterRadio.setValue(RADIO_KONT_ACTIVE);    // Triggers an event which will reloadTreeProvider

//        archFilterRadio.getDataProvider().refreshItem();
//        updateZakGridContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first goes here, then to the beforeEnter of MainView
        System.out.println("###  ZakBasicView.beforeEnter");
    }


//    @DomEvent(value = "keypress", filter = "event.key == 'Enter'")
//    public class EnterPressEvent extends ComponentEvent<TextField> {
//        public EnterPressEvent(TextField source, boolean fromClient) {
//            super(source, fromClient);
//        }
//    }


//    ValueProvider<KzTreeAware, String> zakTextValProv = new ValueProvider() {
//    ComponentRenderer<HtmlComponent, KzTreeAware> kzTextRenderer = new ComponentRenderer<>(kontZak -> {

    private ComponentRenderer initKzTextRenderer() {

        kzTextRenderer = new ComponentRenderer<>(kontZak -> {
            KzText kzText = new KzText(kontZak.getText());
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
                Notification.show("ENTER pressed III - will open form");
            });

            return kzText;
        });

        return kzTextRenderer;
    }


    ValueProvider<KzTreeAware, String> ckzValProv =
            kz -> kz.getTyp() == ItemType.KONT ? kz.getCkont() : kz.getCzak().toString();


    ValueProvider<KzTreeAware, String> menaValProv =
        kz -> kz.getTyp() == ItemType.KONT ? kz.getMena().name() : null;

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
////                return ((KzTreeAware)o).getCzak().toString();
//            }
//        }
//    };

    ComponentRenderer<HtmlComponent, KzTreeAware> honorarCellRenderer = new ComponentRenderer<>(kz -> {
        Paragraph comp = new Paragraph();
        if (ItemType.KONT == kz.getTyp()) {
//            comp.getStyle().set("color", "darkmagenta");
//            return new Emphasis(kontZak.getHonorar().toString());
            comp.getElement().appendChild(ElementFactory.createEmphasis(
                    VzmFormatUtils.moneyFormat.format(kz.getHonorar())));
            comp.getStyle()
//                    .set("color", "red")
//                    .set("text-indent", "1em");
                    .set("padding-right", "1em");
        } else {
            if ((null != kz) && (kz.getHonorar().compareTo(BigDecimal.ZERO) < 0)) {
                comp.getStyle()
                        .set("color", "red")
                        .set("text-indent", "1em");
            }
            comp.getElement().appendChild(ElementFactory.createSpan(
                    VzmFormatUtils.moneyFormat.format(kz.getHonorar())));
        }
        return comp;
    });

    ComponentRenderer<Component, KzTreeAware> kzArchRenderer = new ComponentRenderer<>(kz -> {
//        this.getElement().setAttribute("theme", "small icon secondary");
        Icon icoZakArchived = new Icon(VaadinIcon.CHECK);
        icoZakArchived.setSize("0.8em");
        icoZakArchived.getStyle()
                .set("theme", "small icon secondary")
                .set("padding-left","1em");
        ;
        Icon icoKontActive = new Icon(VaadinIcon.ELLIPSIS_H);
        icoKontActive.setSize("0.8em");
        icoKontActive.getStyle()
                .set("theme", "small icon secondary")
        ;
        Icon icoKontArchived = new Icon(VaadinIcon.CHECK_CIRCLE_O);
        icoKontActive.setSize("0.8em");
        icoKontActive.getStyle()
                .set("theme", "small icon secondary")
        ;
        return ItemType.KONT == kz.getTyp() ?
                (kz.getArch() ? icoKontArchived : icoKontActive)
                : kz.getArch() ? icoZakArchived : new Span();
    });

    //    TextField searchField = new TextField("Hledej kontrakty...");
////    SearchField searchField = new SearchField("Hledej uživatele..."
////            , event -> ((ConfigurableFilterDataProvider) treeGrid.getDataProvider()).setFilter(event.getValue()));
//
//    ListDataProvider<Kont> kontDataProvider;
//
//

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



    private Component initGridContainer() {

//        initSimplePersonTreeGrid();
//        gridContainer.add(personGrid);


//        initNodeTreeGrid();
//        gridContainer.add(treeGrid);

        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);

        gridContainer.add(initKzToolBar());
        gridContainer.add(initKzTreeGrid());

//        this.add();
//        this.add(gridContainer);
        return gridContainer;
    }


    private List<TreeAware> generateNodes() {
        List<TreeAware> rootNodes = new ArrayList<>();

        for (int year = 2010; year <= 2016; year++) {
            Node rootNode = new Node("Node " + year);

            for (int i = 1; i < 2 + rand.nextInt(5); i++) {
//                Node nextNode = new LeafNode("Sub node " + year + " - " + i, rand.nextInt(100), year);
                TreeAware nextNode = new Node("Sub node " + year + " - " + i);
                nextNode.setSubNodes(Arrays.asList(
                        new LeafNode("Implementation", rand.nextInt(100), year),
                        new LeafNode("Planning", rand.nextInt(10), year),
                        new LeafNode("Prototyping", rand.nextInt(20), year)));
                rootNode.addSubNode(nextNode);
            }
            rootNodes.add(rootNode);
        }
        return rootNodes;
    }


    ComponentRenderer<HtmlComponent, BigDecimal> moneyCellRenderer = new ComponentRenderer<>(money -> {
        Div comp = new Div();
        if ((null != money) && (money.compareTo(BigDecimal.ZERO) < 0)) {
            comp.getStyle()
                    .set("color", "red")
//                            .set("text-indent", "1em")
            ;
        }
        comp.setText(VzmFormatUtils.moneyFormat.format(money));
        return comp;
    });

    private Component initKzTreeGrid() {
//        gridContainer.setClassName("view-container");
//        gridContainer.setAlignItems(Alignment.STRETCH);

        kzTreeGrid = new TreeGrid<>();
        kzTreeGrid.setWidth( "100%" );
        kzTreeGrid.setHeight( null );
        kzTreeGrid.getStyle().set("marginTop", "0.5em");

        kzTreeGrid.setColumnReorderingAllowed(true);

        kzTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
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
                .setHeader("řádek")
//                .setFooter("Zobrazeno položek: ")
                .setFlexGrow(0)
 //               .setFrozen(true)
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
        kzTreeGrid.addColumn(new ComponentRenderer<>(this::buildKzOpenButton))
                .setFlexGrow(0)
                .setWidth("4em")
        ;
        kzTreeGrid.addHierarchyColumn(ckzValProv).setHeader(("ČK/ČZ"))
                .setFlexGrow(0)
                .setWidth("9em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(CKZ_COL_KEY)
        ;
        kzTreeGrid.addColumn(KzTreeAware::getRokzak).setHeader(("Rok"))
                .setFlexGrow(0)
                .setWidth("8em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(ROK_COL_KEY)
        ;
        kzTreeGrid.addColumn(KzTreeAware::getSkupina).setHeader(("Sk."))
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(SKUPINA_COL_KEY)
        ;

        if (isMoneyAccessGranted()) {
            kzTreeGrid.addColumn(honorarCellRenderer).setHeader("Honorář")
                    .setFlexGrow(0)
                    .setWidth("9em")
                    .setResizable(true)
                    .setTextAlign(ColumnTextAlign.END)
                    .setKey(HONORAR_COL_KEY)
            ;
            kzTreeGrid.addColumn(menaValProv).setHeader("Měna")
                    .setFlexGrow(0)
                    .setWidth("6em")
                    .setResizable(true)
                    .setKey(MENA_COL_KEY)
            ;
        }

//        kzTreeGrid.addColumn(new ComponentRenderer<>(bean -> {
////            Button status = new Button(VaadinIcon.CIRCLE.create());
//////            status.setClassName("hidden");
//                bean.getElement().setAttribute("style", "color:#28a745");
////            return status;
////        }));
//                KzTreeAware::getHonorar).setHeader("Honorář");

//        kzTreeGrid.addColumn(KzTreeAware::getText).setHeader("Text")
//                .setFlexGrow(1).setResizable(true).setId("text-column");
////                .setFlexGrow(1).setWidth("6em").setResizable(false).setId("mena-column");


        kzTreeGrid.addColumn(KzTreeAware::getOverTerms).setHeader("Fakt.")
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
                .setKey(OVERTERM_COL_KEY)
        ;


        kzTreeGrid.addColumn(kzTextRenderer).setHeader("Text")
                .setFlexGrow(1)
                .setResizable(true)
                .setKey(TEXT_COL_KEY)
        ;

        kzTreeGrid.addColumn(KzTreeAware::getObjednatel).setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setResizable(true)
                .setKey(OBJEDNATEL_COL_KEY)
//                .setId("objednatel-column")
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


        HeaderRow filterRow = kzTreeGrid.appendHeaderRow();

        TextField objednatelFilterField = new TextField();
        ValueProvider<KzTreeAware, String> kzObjednatelValueProvider
                        = KzTreeAware::getObjednatel;
        objednatelFilterField.addValueChangeListener(event -> {});
//        objednatelFilterField.addValueChangeListener(event ->
//                        ((TreeDataProvider<KzTreeAware>)kzTreeGrid.getDataProvider())
////                            .addFilter(KzTreeAware::getObjednatel, t ->
////                                    StringUtils.containsIgnoreCase(t, objednatelFilterField.getValue())
////                            )
//                            .addFilter(kz -> ItemType.KONT != kz.getTyp() || StringUtils.containsIgnoreCase(
//                                kz.getObjednatel(), objednatelFilterField.getValue())
//                            )
//        );
        objednatelFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(kzTreeGrid.getColumnByKey(OBJEDNATEL_COL_KEY)).setComponent(objednatelFilterField);
        objednatelFilterField.setSizeFull();
        objednatelFilterField.setPlaceholder("Filtr (rozbitý)");


        TextField textFilterField = new TextField();
        ValueProvider<KzTreeAware, String> kzTextValueProvider
                = KzTreeAware::getText;
        textFilterField.addValueChangeListener(event -> {});
//        textFilterField.addValueChangeListener(event ->
//                        ((TreeDataProvider<KzTreeAware>)kzTreeGrid.getDataProvider())
////                            .addFilter(KzTreeAware::getText, t ->
////                                    StringUtils.containsIgnoreCase(t, textFilterField.getValue())
////                            )
//                                .addFilter(kz -> ItemType.KONT != kz.getTyp() || StringUtils.containsIgnoreCase(
//                                        kz.getText(), textFilterField.getValue())
//                                )
//        );
        textFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(kzTreeGrid.getColumnByKey(TEXT_COL_KEY)).setComponent(textFilterField);
        textFilterField.setSizeFull();
        textFilterField.setPlaceholder("Filtr (rozbitý)");


        for (Grid.Column col : kzTreeGrid.getColumns()) {
            setResizable(col);
        }
        kzTreeGrid.getColumnByKey(CKZ_COL_KEY).setSortable(true);
        kzTreeGrid.getColumnByKey(OBJEDNATEL_COL_KEY).setSortable(true);

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

    private void reloadTreeProvider(final String archFilter) {
//        if (null == inMemoryKzTreeProvider) {
//            return;
//        }
//
//        inMemoryKzTreeProvider.clearFilters();
        List<? super Kont> kzList;
        if (RADIO_KONT_ACTIVE.equals(archFilter)) {
            kzList = kontService.fetchHavingSomeZaksActive();
        } else if (RADIO_KONT_ARCH.equals(archFilter)) {
            kzList = kontService.fetchHavingAllZaksArchived();
        } else {
            kzList = kontService.fetchAll();
        }

        ValueProvider<KzTreeAware, Collection<KzTreeAware>> kzNodesProvider = KzTreeAware::getNodes;
        inMemoryKzTreeProvider
                = new TreeDataProvider<KzTreeAware>((new TreeData()).addItems(kzList, kzNodesProvider));
        inMemoryKzTreeProvider.setSortOrder(KzTreeAware::getCkont, SortDirection.DESCENDING);
//        inMemoryKzTreeProvider.setFilter(kz -> kz.getArch());
        kzTreeGrid.setDataProvider(inMemoryKzTreeProvider);
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

    private Span styleFlag(Span flag) {
//         Style stl = text.getElement().getStyle();
//            position: absolute;
        flag.getElement().getStyle().set("display", "inline-flex");
//            align-items: center;
//            justify-content: center;
        flag.getElement().getStyle().set("height", "20px");
//            min-width: 8px;
//            padding: 0 6px;
//            background: var(--lumo-base-color);
        flag.getElement().getStyle().set("color", "yellow");
//            top: -10px;
//            left: -10px;
        flag.getElement().getStyle().set("border-radius", "2px");
//            margin: 0;
//            font-size: 12px;
//            font-weight: 500;
//            box-shadow: 0 0 0 1px var(--lumo-contrast-20pct);
        return flag;
    }

    private Component buildKzOpenButton(KzTreeAware kz) {
        if (ItemType.KONT == kz.getTyp()) {
            GridItemEditBtn btn = new GridItemEditBtn(event -> {
                FlexLayout kontFaktFlags = new FlexLayout();
                kontFaktFlags.add(
                        new Div(styleFlag(new Span("5")))
                        , new Ribbon()
                        , new Div(new Text("1"))
                        , new Ribbon()
                        , new Div(new Text("10"))
                );

                kontOrig = (Kont)kz;
                zakOrig = null;
                kontFormDialog.openDialog(
                    (Kont)kz, Operation.EDIT
                    , null, kontFaktFlags, null);
//                    "[ Vytvořeno: " + ((Kont) kz).getDateCreate().toString()
//                            + " , Poslední změna: " + ((Kont) kz).getDatetimeUpdate().toString() + " ]")
            });
            return btn;
        } else {
            GridItemEditBtn btn = new GridItemEditBtn(event -> {
                Div zakFaktFlags = new Div();
                zakFaktFlags.add(new Span("5"), new Ribbon(), new Span("1"), new Ribbon(), new Span("10"));

                kontOrig = ((Zak)kz).getKont();
                zakOrig = (Zak)kz;
                zakFormDialog.openDialog(
                    (Zak) kz, kontOrig, Operation.EDIT
                    , null, zakFaktFlags, null);
//                    "[ Vytvořeno: " + ((Zak) kz).getDateCreate().toString()
//                            + " , Poslední změna: " + ((Zak) kz).getDatetimeUpdate().toString() + " ]")
            }, "darkmagenta");
            btn.getStyle().set("padding-left", "1em");
            return btn;
        }
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

    private void initZakProvider() {
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
//                query -> (int) kontService.countByFilter(query.getFilter().orElse(null))
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
//                query -> (int) kontService.countByFilter(query.getFilter().orElse(null))
//        );

//        treeGrid.setDataProvider(kontDataProvider);

//        personEditForm = new PersonEditorDialog(
//                this::savePerson, this::deletePerson, personService, roleService.fetchAllRoles(), passwordEncoder);

    }


    private void saveKont(Kont kont, Operation operation) {

//        event -> {
//            try {
//                binder.writeBean(person);
//                // A real application would also save the updated person
//                // using the application's backend
//            } catch (ValidationException e) {
//                notifyValidationException(e);
//            }

        //INSERT:

        // Check CKONT uniqueness

        // Create valid dir in-memory structure

        // Check if new kont dir does not exist

        // Create kont dir structures

        // Save kont



        // EDIT:

        // Check changed CKONT uniqueness

        // If CKONT changed, create valid dir in-memory structure

        // Check if current kont dir exists

        // Check if changed kont dir does not exist exist

        // Rename kont dir structures

        // Save kont



        if (Operation.EDIT == operation) {
//            VzmFileUtils.renameKontProjDirs(cfgPropsCache.getValue("app.project.root.server"), kont.getFolder());
        } else if (Operation.ADD == operation){
            if (!VzmFileUtils.createKontProjDirs(getProjRootServer(), kont.getFolder())) {
                new OkDialog().open("Adresáře kontraktu"
                        , "Projektové adresáře se nepodařilo vytvořit", "");
            };
            if (!VzmFileUtils.createKontDocDirs(getDocRootServer(), kont.getFolder())) {
                new OkDialog().open("Adresáře kontraktu"
                        , "Dokumentové adresáře se nepodařilo vytvořit", "");
            };
//            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
//            kontProjRootDir.setReadOnly();
        } else {
        }

        Kont newKont = kontService.saveKont(kont);
        new OkDialog().open("Kontrakt " + newKont.getCkont() + " uložen"
                , "", "");

        if (Operation.EDIT == operation) {
            kzTreeGrid.getDataProvider().refreshItem(newKont);
        } else {
//            if (null == archRadioValue || )
//            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//            kzTreeGrid.getDataProvider().refreshAll();

              if (archFilterRadio.getValue() == RADIO_KONT_ACTIVE) {
                  archFilterRadio.setValue(RADIO_KONT_ARCH);
              } else {
                  reloadTreeProvider(archFilterRadio.getValue());
              }
        }
        kzTreeGrid.select(newKont);
//        Notification.show(
//                "Kontrakt uložen", 4000, Notification.Position.BOTTOM_END);
    }

    private String getProjRootServer() {
        return cfgPropsCache.getValue("app.project.root.server");
    }

    private String getDocRootServer() {
        return cfgPropsCache.getValue("app.document.root.server");
    }

    private void deleteKont(final Kont kont) {
        Kont kontDel = kont;
        int kontDelIdx = kzTreeGrid.getDataCommunicator().getIndex(kontDel);
        Stream<KzTreeAware> stream = kzTreeGrid.getDataCommunicator()
                .fetchFromProvider(kontDelIdx + 1, 1);
        KzTreeAware newSelectedKont = stream.findFirst().orElse(null);

        boolean deleted = kontService.deleteKont(kontDel);
        if (!deleted) {
            new OkDialog().open("Kontrakt " + kontDel.getCkont() + " nebyl zrušen.", "","");
        } else {

//            GenericModel bean = myGrid.getSelectedRow();
//            ListDataProvider<GenericModel> dataProvider=(ListDataProvider<GenericModel>) myGrid.getDataProvider();
//            List<GenericModel> ItemsList=(List<GenericModel>) dataProvider.getItems();

//            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//            kzTreeGrid.getDataProvider().refreshAll();
//
//            HierarchicalDataProvider dataProvider = kzTreeGrid.getDataProvider();
//            List<KzTreeAware> itemsList =(List<KzTreeAware>) dataProvider. getItems();
//            int index=itemsList.indexOf(bean);//index of the selected item
//            GenericModel newSelectedBean=itemsList.get(index+1);
//            dataProvider.getItems().remove(bean);
//            dataProvider.refreshAll();
//            myGrid.select(newSelectedBean);
//            myGrid.scrollTo(index+1);

//            kzTreeGrid.getDataCommunicator().getKeyMapper().remove(kontDel);
//            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
//            kzTreeGrid.getDataProvider().refreshAll();

            reloadTreeProvider(archFilterRadio.getValue());
            kzTreeGrid.getSelectionModel().select(newSelectedKont);

            new OkDialog().open("Kontrakt " + kontDel.getCkont() + " je zrušen", "","");
        }
    }

    private void updateZakGridContent() {
//        List<Kont> zaks = kontRepo.findAll();
//        treeGrid.setItems(zaks);
    }

    private void saveZak(Zak zak, Operation operation) {

//        event -> {
//            try {
//                binder.writeBean(person);
//                // A real application would also save the updated person
//                // using the application's backend
//            } catch (ValidationException e) {
//                notifyValidationException(e);
//            }

        //INSERT:

        // Check CKONT uniqueness

        // Create valid dir in-memory structure

        // Check if new kont dir does not exist

        // Create kont dir structures

        // Save kont



        // EDIT:

        // Check changed CKONT uniqueness

        // If CKONT changed, create valid dir in-memory structure

        // Check if current kont dir exists

        // Check if changed kont dir does not exist exist

        // Rename kont dir structures

        // Save kont



        if (Operation.EDIT == operation) {
//            VzmFileUtils.renameZakProjDirs(cfgPropsCache.getValue("app.project.root.server"), kont.getFolder());
        } else if (Operation.ADD == operation){
            if (!VzmFileUtils.createZakProjDirs(getProjRootServer(), zak.getKont().getFolder(), zak.getFolder())) {
                new OkDialog().open("Adresáře zakázky"
                        , "Projektové adresáře se nepodařilo vytvořit", "");
            };
            if (!VzmFileUtils.createZakDocDirs(getDocRootServer(), zak.getKont().getFolder(), zak.getFolder())) {
                new OkDialog().open("Adresáře zakázky"
                        , "Dokumentové adresáře se nepodařilo vytvořit", "");
            };
//            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
//            kontProjRootDir.setReadOnly();
        } else {
        }

        File file = new File("location of file");
        file.setReadOnly();

        Zak savedZak = zakService.saveZak(zak);
        new OkDialog().open("Zakázka " + savedZak.getKont().getCkont() + "/" + savedZak.getCzak() + " uložena"
                , "", "");

        if (Operation.EDIT == operation) {
            kzTreeGrid.getDataProvider().refreshItem(savedZak);
        } else {
//            if (null == archRadioValue || )
            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
            kzTreeGrid.getDataProvider().refreshAll();

//            if (archFilterRadio.getValue() == RADIO_KONT_ACTIVE) {
//                archFilterRadio.setValue(RADIO_KONT_ARCH);
//            } else {
//                reloadTreeProvider(archFilterRadio.getValue());
//            }
        }

//        kzTreeGrid.getDataProvider().refreshAll();
//        Notification.show(
////                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
//                "Zakázka uložena", 3000, Notification.Position.BOTTOM_END);

        kzTreeGrid.select(savedZak);
    }


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
//        zakGrid.addColumn(Zak::getCzak).setHeader("ČZ").setWidth("3em").setResizable(true)
//                .setSortProperty("poradi");
////        zakGrid.addColumn(new ComponentRenderer<>(this::createOpenDirButton)).setFlexGrow(0);
//        zakGrid.addColumn(Zak::getRokmeszad).setHeader("Zadáno").setWidth("3em").setResizable(true);
//        zakGrid.addColumn(Zak::getText).setHeader("Text zak.").setWidth("5em").setResizable(true);
//        zakGrid.addColumn(Zak::getHonorc).setHeader("Honorář").setWidth("3em").setResizable(true);
//        zakGrid.addColumn(Zak::getSkupina).setHeader("Skupina").setWidth("4em").setResizable(true);
//
//        return zakGrid;
//    }

    private void deleteZak(Zak zak) {
        String ckzDel = zak.getCkont() + " - " + zak.getCzak();
        boolean deleted = zakService.deleteZak(zak);
        if (!deleted) {
            return;
        } else {
            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
            kzTreeGrid.getDataProvider().refreshAll();
            Notification.show("Zakázka " + ckzDel + " zrušena.", 3000, Notification.Position.BOTTOM_END);
            return;
        }
    }


    private Component initKzToolBar() {

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

        Span archFilterLabel = new Span("Kontrakty:");

        archFilterRadio = new RadioButtonGroup();
        archFilterRadio.setItems(RADIO_KONT_ACTIVE, RADIO_KONT_ARCH, RADIO_KONT_ALL);
//        buttonShowArchive.addValueChangeListener(event -> setArchiveFilter(event));
        archFilterRadio.getStyle().set("alignItems", "center");
        archFilterRadio.getStyle().set("theme", "small");
        archFilterRadio.addValueChangeListener(event -> {
                reloadTreeProvider(event.getValue());

//                if (RADIO_KONT_ALL.equals(event.getValue())) {
//                    inMemoryKzTreeProvider.setFilter(kz -> kz.getArch());
//                } else if (RADIO_KONT_ARCH.equals(event.getValue())) {
//                    inMemoryKzTreeProvider.setFilter(kz -> !kz.getArch());
//                } else {
//                    inMemoryKzTreeProvider.clearFilters();
//                }
////                inMemoryKzTreeProvider.filteringByPrefix(kz -> kz.getText());
                inMemoryKzTreeProvider.refreshAll();
            }
        );

//        FlexLayout archFilterComponent = new FlexLayout();
        HorizontalLayout archFilterComponent = new HorizontalLayout();
        archFilterComponent.setMargin(false);
        archFilterComponent.setPadding(false);
        archFilterComponent.setAlignItems(Alignment.CENTER);
        archFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        archFilterComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
//        archFilterComponent.setWidth("100%");
//        archFilterComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        archFilterComponent.add(archFilterLabel, archFilterRadio);

        H3 mainTitle = new H3("KONTRAKTY / ZAKÁZKY");
        mainTitle.getStyle().set("margin-top", "0.2em");

//        mainTitle.getElement().getStyle().set("fontSize", "1,7em").set("fontWeight", "bold");

//        Ribbon ribbonExp = new Ribbon();
//        kzToolBar.add(expandAllBtn, collapseAllBtn, archiveFilterRadio);
//        kzToolBar.add(mainTitle, ribbonExp, archFilterLabel, archFilterRadio, initNewKontButton());
        kzToolBar.add(mainTitle, new Ribbon(), archFilterComponent, new Ribbon(), initNewKontButton());
//        kzToolBar.expand(ribbonExp);


//        Button buttonPrevious = new Button("Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
//        Button buttonNext = new Button("Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
//        buttonNext.setIconAfterText(true);
//
//
//        // simulate the date picker light that we can use in polymer
//        DatePicker gotoDate = new DatePicker();
//        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
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
//        Checkbox cbWeekNumbers = new Checkbox("Week numbers", event -> calendar.setWeekNumbersVisible(event.getValue()));
//
//        ComboBox<Locale> comboBoxLocales = new ComboBox<>();
//
//        List<Locale> items = Arrays.asList(CalendarLocale.getAvailableLocales());
//        comboBoxLocales.setItems(items);
//        comboBoxLocales.setValue(CalendarLocale.getDefault());
//        comboBoxLocales.addValueChangeListener(event -> calendar.setLocale(event.getValue()));
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
//        comboBoxGroupBy.addValueChangeListener(event -> ((Scheduler) calendar).setGroupEntriesBy(event.getValue()));
//
//        timezoneComboBox = new ComboBox<>("");
//        timezoneComboBox.setItemLabelGenerator(Timezone::getClientSideValue);
//        timezoneComboBox.setItems(Timezone.getAvailableZones());
//        timezoneComboBox.setValue(Timezone.UTC);
//        timezoneComboBox.addValueChangeListener(event -> {
//            Timezone value = event.getValue();
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

    //    private void initViewToolBar(final Button reloadViewButton, final Button newItemButto)
    private Component initViewToolBar() {
        // Build view toolbar
        viewToolBar.setWidth("100%");
        viewToolBar.setPadding(true);
        viewToolBar.getStyle()
                .set("padding-bottom","5px");

        Span viewTitle = new Span(TITLE_KZ_TREE.toUpperCase());
        viewTitle.getStyle()
                .set("font-size","var(--lumo-font-size-l)")
                .set("font-weight","600")
                .set("padding-right","0.75em");

//        searchField = new SearchField(
////                "Hledej uživatele...", event ->
////                "Hledej uživatele...", event -> updateZakGridContent()
//                "Hledej uživatele...",
//                event -> ((ConfigurableFilterDataProvider) treeGrid.getDataProvider()).setFilter(event.getValue())
//        );        toolBarSearch.add(viewTitle, searchField);

//        HorizontalLayout searchToolBar = new HorizontalLayout(viewTitle, searchField);
//        searchToolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

//        HorizontalLayout kzToolBar = new HorizontalLayout(reloadViewButton);
//        kzToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
//
//        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
//        toolBarItem.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

//        kzToolBar.add(searchToolBar, kzToolBar, ribbon, toolBarItem);
        Ribbon ribbon = new Ribbon("3em");
        viewToolBar.expand(ribbon);
        return viewToolBar;
    }

    private Component initNewKontButton() {
        Button newKontButton = new NewItemButton("Kontrakt"
                , event -> {
                    Kont kont = new Kont( ItemType.KONT);
//                    kont.setInvestor("Inv 1");
//                    kont.setObjednatel("Obj 1");
                    kont.setMena(Mena.CZK);
                    kont.setDateCreate(LocalDate.now());
//                    kont.setCkont("01234");
                    kontFormDialog.open(kont, Operation.ADD);
        });
        return newKontButton;
    }
// ===============================================================================


    public static class Person {
        private String name;
        private Person parent;

        public String getName() {
            return name;
        }

        public Person getParent() {
            return parent;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setParent(Person parent) {
            this.parent = parent;
        }

        public Person(String name, Person parent) {
            this.name = name;
            this.parent = parent;
        }

//        @Override
//        public String toString() {
//            return name;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            Person person = (Person) o;
//
//            if (name != null ? !name.equals(person.name) : person.name != null) return false;
//            return parent != null ? parent.equals(person.parent) : person.parent == null;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = name != null ? name.hashCode() : 0;
//            result = 31 * result + (parent != null ? parent.hashCode() : 0);
//            return result;
//        }
    }


    private void initSimplePersonGrid() {
        TreeGrid<Person> personGrid = new TreeGrid<>(Person.class);
        personGrid.addColumn(Person::getName).setHeader("X-NAME");
        personGrid.setHierarchyColumn("name");


//        List<Person> all = generatePersons();
//
        Person dad = new Person("dad", null);
        Person son = new Person("son", dad);
        Person daughter = new Person("daughter", dad);
//        List<Person> all = Arrays.asList(dad, son, daughter);
//        return all;
//        all.forEach(p -> personGrid.getTreeData().addItem(p.getParent(), p));
        personGrid.getTreeData().addItem(null, dad);
        personGrid.getTreeData().addItem(dad, son);
        personGrid.getTreeData().addItem(dad, daughter);

    }


    private List<Person> generatePersons() {

        Person dad = new Person("dad", null);
        Person son = new Person("son", dad);
        Person daughter = new Person("daughter", dad);
        List<Person> all = Arrays.asList(dad, son, daughter);
        return all;
    }



//    public class HeightDialog extends Dialog {
//        HeightDialog() {
//            VerticalLayout dialogContainer = new VerticalLayout();
//            add(dialogContainer);
//
//            TextField heightInput = new TextField("", "500", "e. g. 300");
//            Button byPixels = new Button("Set by pixels", e -> {
//                kzTreeGrid.setHeight(Integer.valueOf(heightInput.getValue()));
//
//                this.setSizeUndefined();
//                setFlexStyles(false);
//            });
//            byPixels.getElement().setProperty("title", "Calendar height is fixed by pixels.");
//            dialogContainer.add(new HorizontalLayout(heightInput, byPixels));
//
////            Button autoHeight = new Button("Auto height", e -> {
////                kzTreeGrid.setHeightAuto();
////
////                this.setSizeUndefined();
////                setFlexStyles(false);
////            });
////            autoHeight.getElement().setProperty("title", "Calendar height is set to auto.");
////            dialogContainer.add(autoHeight);
//
////            Button heightByBlockParent = new Button("Height by block parent", e -> {
////                kzTreeGrid.setHeightByParent();
////                kzTreeGrid.setSizeFull();
////
////                this.setSizeFull();
////                setFlexStyles(false);
////            });
////            heightByBlockParent.getElement().setProperty("title", "Container is display:block + setSizeFull(). Calendar height is set to parent + setSizeFull(). Body element kept unchanged.");
////            dialogContainer.add(heightByBlockParent);
//
////            Button heightByBlockParentAndCalc = new Button("Height by block parent + calc()", e -> {
////                calendar.setHeightByParent();
////                calendar.getElement().getStyle().set("height", "calc(100vh - 450px)");
////
////                Demo.this.setSizeFull();
////                setFlexStyles(false);
////            });
////            heightByBlockParentAndCalc.getElement().setProperty("title", "Container is display:block + setSizeFull(). Calendar height is set to parent + css height is calculated by calc(100vh - 450px) as example. Body element kept unchanged.");
////            dialogContainer.add(heightByBlockParentAndCalc);
//
//            Button heightByFlexParent = new Button("Height by flex parent", e -> {
//                calendar.setHeightByParent();
//
//                Demo.this.setSizeFull();
//                setFlexStyles(true);
//            });
//            heightByFlexParent.getElement().setProperty("title", "Container is display:flex + setSizeFull(). Calendar height is set to parent + flex-grow: 1. Body element kept unchanged.");
//            dialogContainer.add(heightByFlexParent);
//
//            Button heightByFlexParentAndBody = new Button("Height by flex parent and flex body", e -> {
//                calendar.setHeightByParent();
//
//                Demo.this.setSizeUndefined();
//                setFlexStyles(true);
//
//                UI.getCurrent().getElement().getStyle().set("display", "flex");
//            });
//            heightByFlexParentAndBody.getElement().setProperty("title", "Container is display:flex. Calendar height is set to parent + flex-grow: 1. Body element is set to display: flex.");
//            dialogContainer.add(heightByFlexParentAndBody);
//        }
//    }

// ===============================================================================

    public interface TreeAware {

        String getName();
        int getHoursDone();
        Date getLastModified();
        Collection<TreeAware> getSubNodes();
        void setSubNodes(List<TreeAware> subNodes);

    }

    public static class Node implements TreeAware {

//        Long id;
        String name;
//        Long parentId;
        private Collection<TreeAware> subNodes = new ArrayList<>();

//        public Node(Long id, String text, Long parentId) {
        public Node(String name) {
//            this.id = id;
            this.name = name;
//            this.parentId = parentId;
        }

//        public Long getId() {
//          return id;
//        }
//
//        public void setId(Long id) {
//          this.id = id;
//        }

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }

        public void setSubNodes(List<TreeAware> subNodes) {
            this.subNodes = subNodes;
        }

        @Override
        public Collection<TreeAware> getSubNodes() {
            return this.subNodes;
        }

        public void addSubNode(TreeAware subNode) {
           subNodes.add(subNode);
        }

        @Override
        public int getHoursDone() {
            return getSubNodes().stream()
                    .map(project -> project.getHoursDone())
                    .reduce(0, Integer::sum);
        }

        @Override
        public Date getLastModified() {
            return getSubNodes().stream()
                    .map(project -> project.getLastModified())
                    .max(Date::compareTo).orElse(null);
        }

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
    }

    class LeafNode extends Node {

        private int hoursDone;
        private Date lastModified;


        public LeafNode(String name, int hoursDone, int year) {
            super(name);
            this.hoursDone = hoursDone;
            lastModified = new Date(year - 1900, rand.nextInt(12), rand.nextInt(10));
        }

        @Override
        public int getHoursDone() {
            return hoursDone;
        }

        @Override
        public Date getLastModified() {
            return lastModified;
        }
    }



    private TreeGrid<TreeAware> treeGrid;

    private void initNodeTreeGrid() {

        treeGrid = new TreeGrid<>();
        treeGrid.setWidth( "100%" );
        treeGrid.setHeight( null );
        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        treeGrid.addHierarchyColumn(TreeAware::getName).setHeader(("Name"))
                .setFlexGrow(0).setWidth("340px")
                .setResizable(true).setFrozen(true).setId("name-column");
        treeGrid.addColumn(TreeAware::getHoursDone).setHeader("Hours Done");
        treeGrid.addColumn(TreeAware::getLastModified).setHeader("Last Modified");
//        treeGrid.setHierarchyColumn("name");

        treeGrid.setItemDetailsRenderer(new ComponentRenderer<>(item -> {
            Label label = new Label("Details opened! " + item);
            label.setId("details-label");
            return label;
        }));

        List<TreeAware> rootNodes = generateNodes();
        treeGrid.setItems(rootNodes, TreeAware::getSubNodes);

        treeGrid.getDataProvider().refreshAll();
        treeGrid.expand(rootNodes.get(0));
        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
//        treeGrid.getTreeData().getRootItems().contains(item);
    }



// =========================================


}
