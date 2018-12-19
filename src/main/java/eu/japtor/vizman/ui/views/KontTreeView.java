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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.KontZakTreeAware;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.ZakTyp;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.*;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

//import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
//import com.vaadin.flow.data.provider.ListDataProvider;

@Route(value = ROUTE_KONT_TREE, layout = MainView.class)
@PageTitle(PAGE_TITLE_KONT_TREE)
//@Tag(TAG_ZAK)    // Kurvi layout
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL,
        Perm.KONT_VIEW_BASIC_READ, Perm.KONT_VIEW_BASIC_MANAGE,
        Perm.KONT_VIEW_EXT_READ, Perm.KONT_VIEW_EXT_MANAGE
})
public class KontTreeView extends VerticalLayout implements BeforeEnterObserver {

    Random rand = new Random();
//    int n = rand.nextInt(50) + 1;


    private final H3 kontHeader = new H3(TITLE_KONT_TREE);

//    private final Grid<Kont> kontGrid = new Grid<>();
//    private final Grid<Zak> zakGrid = new Grid<>();
    private TreeGrid<TreeAware> treeGrid;
    private TreeGrid<KontZakTreeAware> zakTreeGrid;

    VerticalLayout gridContainer = new VerticalLayout();
    HorizontalLayout viewToolBar = new HorizontalLayout();
    HorizontalLayout toolBarSearch = new HorizontalLayout();

    ValueProvider<KontZakTreeAware, String> zakTextValProv = new ValueProvider() {
        @Override
        public Object apply(Object o) {
            if (((KontZakTreeAware)o).getTyp() == ZakTyp.ZAK) {
                return "#  " + ((KontZakTreeAware)o).getText();
            } else {
                return ((KontZakTreeAware)o).getText();
            }
        }
    };

//    TextField searchField = new TextField("Hledej kontrakty...");
////    SearchField searchField = new SearchField("Hledej uživatele..."
////            , event -> ((ConfigurableFilterDataProvider) treeGrid.getDataProvider()).setFilter(event.getValue()));
//
//    ListDataProvider<Kont> kontDataProvider;
//
//
//    @Autowired
//    public KontRepo kontRepo;
//
    @Autowired
    public KontService kontService;

//    public KontListView() {
//        initView();
//        initZakTreeGrid();
//        updateZakGridContent();
//    }

    @PostConstruct
    public void init() {
        initView();
        initZakProvider();
        initGrid();
//        initZakGrid();
//        updateZakGridContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first goes here, then to the beforeEnter of MainView
        System.out.println("###  KontTreeView.beforeEnter");
    }

    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-treeGrid items="[[items]]" id="treeGrid" style="width: 100%;"></vaadin-treeGrid>
    }



    private void initGrid() {

//        initSimplePersonTreeGrid();
//        gridContainer.add(personGrid);


//        initNodeTreeGrid();
//        gridContainer.add(treeGrid);

        initZakTreeGrid();
        gridContainer.add(zakTreeGrid);

        this.add(gridContainer);

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


    private void initZakTreeGrid() {
//        gridContainer.setClassName("view-container");
//        gridContainer.setAlignItems(Alignment.STRETCH);

        zakTreeGrid = new TreeGrid<>();
        zakTreeGrid.setWidth( "100%" );
        zakTreeGrid.setHeight( null );

//        treeGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
//        treeGrid.addColumn(Node::getId).setHeader("ID").setWidth("3em").setResizable(true);
//        treeGrid.addColumn(Node::getText).setHeader("Text").setWidth("8em").setResizable(true);

//        treeGrid.removeColumn(treeGrid.getColumnByKey("subNodes"));

        zakTreeGrid.addColumn(KontZakTreeAware::getArch).setHeader(("Arch"))
                .setFlexGrow(0).setFrozen(true).setWidth("7em").setResizable(true).setId("arch-column");
//                .setFlexGrow(0).setWidth("7em").setResizable(true).setId("ckont-column");
        zakTreeGrid.addHierarchyColumn(KontZakTreeAware::getCkont).setHeader(("ČK"))
                .setFlexGrow(0).setFrozen(true).setWidth("10em").setResizable(true).setId("ckont-column");
//                .setFlexGrow(0).setWidth("7em").setResizable(true).setId("ckont-column");
        zakTreeGrid.addColumn(KontZakTreeAware::getCzak).setHeader("ČZ")
                .setFlexGrow(0).setFrozen(true).setWidth("4em").setResizable(true).setId("czak-column");
        zakTreeGrid.addColumn(KontZakTreeAware::getObjednatel).setHeader("Objednatel")
                .setFlexGrow(0).setWidth("8em").setResizable(true).setId("objednatel-column");
        zakTreeGrid.addColumn(new ComponentRenderer<>(kontZak -> {
                    Paragraph comp = new Paragraph();
//                    comp.setWidth("5em");
                    comp.getStyle()
                            .set("text-align", "right");
                    if (null == kontZak.getCzak()) {
                        comp.getStyle().set("color", "darkmagenta");
                        comp.getElement().appendChild(ElementFactory.createEmphasis(kontZak.getHonorar().toString()));
                    } else {
                        comp.getElement().appendChild(ElementFactory.createSpan(kontZak.getHonorar().toString()));
//                        comp.setText(kontZak.getHonorar().toString());
                    }
                    return comp;
                })
            ).setHeader("Honorář").setFlexGrow(0).setWidth("8em").setId("honorar-column");
        ;
        zakTreeGrid.addColumn(KontZakTreeAware::getMena).setHeader("Měna")
                .setFlexGrow(0).setWidth("6em").setResizable(false).setId("mena-column");

//        zakTreeGrid.addColumn(new ComponentRenderer<>(bean -> {
////            Button status = new Button(VaadinIcon.CIRCLE.create());
//////            status.setClassName("hidden");
//                bean.getElement().setAttribute("style", "color:#28a745");
////            return status;
////        }));
//                KontZakTreeAware::getHonorar).setHeader("Honorář");

//        zakTreeGrid.addColumn(KontZakTreeAware::getText).setHeader("Text")
//                .setFlexGrow(1).setResizable(true).setId("text-column");
////                .setFlexGrow(1).setWidth("6em").setResizable(false).setId("mena-column");

        zakTreeGrid.addColumn(zakTextValProv).setHeader("Text")
                .setFlexGrow(1).setResizable(true).setId("text-column");
//                .setFlexGrow(1).setWidth("6em").setResizable(false).setId("mena-column");


        GridSelectionModel<?> selectionMode = zakTreeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        ((GridMultiSelectionModel<?>) selectionMode).setSelectionColumnFrozen(true);

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



//        List<T extends KontZakTreeAware> kontList = kontService.fetchAll();
        List<? extends KontZakTreeAware> kontList = kontService.fetchAll();
//        Set<? extends KontZakTreeAware> konts = null;
//        zakTreeGrid.setItems((Collection<KontZakTreeAware>) kontList, KontZakTreeAware::getNodes);
        zakTreeGrid.setItems((Collection<KontZakTreeAware>) kontList, KontZakTreeAware::getNodes);

//        treeGrid.getDataProvider().refreshItem(pojoItem);
        zakTreeGrid.getDataProvider().refreshAll();
//        zakTreeGrid.expand(konts.get(0));

//        treeGrid.getTreeData().getRootItems().contains(item);
        zakTreeGrid.getSelectedItems();




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




////        treeGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
//        treeGrid.addColumn(Kont::getCkont).setHeader("ČK").setWidth("7em").setResizable(true);
//        treeGrid.addColumn(Kont::getObjednatel).setHeader("Objednatel").setWidth("16em").setResizable(true);
//        treeGrid.addColumn(Kont::getArch).setHeader("Arch").setWidth("4em").setResizable(true);
//        treeGrid.addColumn(Kont::getText).setHeader("Text").setWidth("25em").setResizable(true);
//        treeGrid.addColumn(Kont::getDatZad).setHeader("Dat.zad.").setWidth("7em").setResizable(true);
//        treeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

//        // some listeners for interaction
//        treeGrid.addCollapseListener(event -> Notification
//                .show("Kont. '" + event.getCollapsedItem().getName() + "' collapsed.", Notification.Type.TRAY_NOTIFICATION));
//        treeGrid.addExpandListener(event -> Notification
//                .show("Project '" + event.getExpandedItem().getName() + "' expanded.", Notification.Type.TRAY_NOTIFICATION));

//        treeGrid.setItems(kontService.fetchAll(), kont -> kontService.fetchAll());

//        treeGrid.setItemDetailsRenderer(new ComponentRenderer<>(kont -> {
//
//            TextField textField = new TextField("Text kont.: ", kont.getText(), "");
//            textField.setWidth(null);
//            textField.setReadOnly(true);
//
//            treeGrid.setDataProvider(new ListDataProvider<>(kont.getZaks()));
//
//            FormLayout zakForm = new FormLayout();
//            zakForm.setSizeFull();
//            zakForm.add(textField);
//            zakForm.add(treeGrid);
//
////            VerticalLayout gridLayout = new VerticalLayout();
////            gridLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
//
////            TextField zadanoField = new TextField("Zadáno: ", zak.getDatumzad().toString(), "");
////            zadanoField.setReadOnly(true);
//
////            layout.add(zadanoField);
////            layout.add(new TextField("Objednatel: ", zak.getObjednatel().toString(), "placeholder"));
////            layout.add(new Label("Text zak.: " + zak.getText()));
////            layout.add(new Label("Zadáno: " + zak.getDatumzad()));
//
//
//            VerticalLayout layout = new VerticalLayout();
//            layout.setAlignSelf(Alignment.START);
//
//            layout.add(zakForm);
//            return layout;
//        }));

//        initViewToolBar();

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



    private void updateZakGridContent() {
//        List<Kont> zaks = kontRepo.findAll();
//        treeGrid.setItems(zaks);
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

    //    private void initViewToolBar(final Button reloadViewButton, final Button newItemButto)
    private void initViewToolBar() {
        // Build view toolbar
        viewToolBar.setWidth("100%");
        viewToolBar.setPadding(true);
        viewToolBar.getStyle().

                set("padding-bottom","5px");

        Span viewTitle = new Span(TITLE_KONT_TREE.toUpperCase());
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

//        HorizontalLayout gridToolBar = new HorizontalLayout(reloadViewButton);
//        gridToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
//
//        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
//        toolBarItem.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Span ribbon = new Span();
//        viewToolBar.add(searchToolBar, gridToolBar, ribbon, toolBarItem);
//        viewToolBar.add(searchToolBar,ribbon);
        viewToolBar.expand(ribbon);
    }

}
