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

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
//import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.KontRepo;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.Ribbon;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.Collection;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_KONT, layout = MainView.class)
@PageTitle(PAGE_TITLE_KONT_LIST)
//@Tag(TAG_ZAK)    // Kurvi layout
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL,
        Perm.ZAK_BASIC_READ, Perm.ZAK_BASIC_MODIFY,
        Perm.ZAK_EXT_READ, Perm.ZAK_EXT_MODIFY
})
public class KontListView extends VerticalLayout implements BeforeEnterObserver {

    private final H3 kontHeader = new H3(TITLE_KONT_LIST);
    private final Grid<Kont> kontGrid = new Grid<>();
    private final Grid<Zak> zakGrid = new Grid<>();
    ListDataProvider<Kont> kontDataProvider;

    VerticalLayout gridContainer = new VerticalLayout();
    HorizontalLayout viewToolBar = new HorizontalLayout();
    HorizontalLayout toolBarSearch = new HorizontalLayout();

    TextField searchField = new TextField("Hledej zakázky...");
//    SearchField searchField = new SearchField("Hledej uživatele..."
//            , event -> ((ConfigurableFilterDataProvider) kontGrid.getDataProvider()).setFilter(event.getValue()));


    @Autowired
    public KontService kontService;

//    public KontListView() {
//        initView();
//        initKontGrid();
//        updateKontGridContent();
//    }

    @PostConstruct
    public void init() {
        initView();
        initKontProvider();
        initKontGrid();
        initZakGrid();
        updateKontGridContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first goes here, then to the beforeEnter of MainView
        System.out.println("###  KontListView.beforeEnter");
    }

    private void initView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);


//        this.setWidth("100%");
//        this.setWidth("90vw");
//        this.setHeight("90vh");

//        <vaadin-vertical-layout style="width: 100%; height: 100%;" theme="padding">
//        <vaadin-text-field style="width: 100%;" placeholder="ID" id="idSearchTextField"></vaadin-text-field>
//        <vaadin-kontGrid items="[[items]]" id="kontGrid" style="width: 100%;"></vaadin-kontGrid>
    }

    private void initKontGrid() {
        gridContainer.setClassName("view-container");
        gridContainer.setAlignItems(Alignment.STRETCH);

//        kontGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#");
        kontGrid.addColumn(Kont::getCkont)
                .setHeader("ČK")
                .setWidth("7em")
                .setResizable(true)
        ;
        kontGrid.addColumn(Kont::getKlient)
                .setHeader("Objednatel")
                .setWidth("16em")
                .setResizable(true)
        ;
//        kontGrid.addColumn(Kont::getArch).setHeader("Arch").setWidth("4em").setResizable(true);
        kontGrid.addColumn(Kont::getText)
                .setHeader("Text")
                .setWidth("25em")
                .setResizable(true)
        ;
        kontGrid.addColumn(Kont::getDateCreate)
                .setHeader("Dat.zad.")
                .setWidth("7em")
                .setResizable(true)
        ;
        kontGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        kontGrid.setItemDetailsRenderer(new ComponentRenderer<>(kont -> {

            TextField textField = new TextField("Text kont.: ", kont.getText(), "");
            textField.setWidth(null);
            textField.setReadOnly(true);

            zakGrid.setDataProvider(new ListDataProvider<>(kont.getZaks()));

            FormLayout zakForm = new FormLayout();
            zakForm.setSizeFull();
            zakForm.add(textField);
            zakForm.add(zakGrid);

//            VerticalLayout gridLayout = new VerticalLayout();
//            gridLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

//            TextField zadanoField = new TextField("Zadáno: ", zak.getDatumzad().toString(), "");
//            zadanoField.setReadOnly(true);

//            layout.add(zadanoField);
//            layout.add(new TextField("Objednatel: ", zak.getObjednatel().toString(), "placeholder"));
//            layout.add(new Label("Text zak.: " + zak.getText()));
//            layout.add(new Label("Zadáno: " + zak.getDatumzad()));


            VerticalLayout layout = new VerticalLayout();
            layout.setAlignSelf(Alignment.START);

            layout.add(zakForm);
            return layout;
        }));

        initViewToolBar();
        gridContainer.add(kontHeader, kontGrid);
        add(viewToolBar, gridContainer);

    }

    private void initKontProvider() {
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

        kontDataProvider = new ListDataProvider<>((Collection<Kont>) kontService.fetchAll());

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

        kontGrid.setDataProvider(kontDataProvider);

//        personEditForm = new PersonEditorDialog(
//                this::savePerson, this::deletePerson, personService, roleService.fetchAllRoles(), passwordEncoder);

    }


    //    private void initViewToolBar(final Button reloadViewButton, final Button newItemButto)
    private void initViewToolBar() {
    // Build view toolbar
        viewToolBar.setWidth("100%");
        viewToolBar.setPadding(true);
        viewToolBar.getStyle()
            .set("padding-bottom","5px")
        ;

        Span viewTitle = new Span(TITLE_KONT_LIST.toUpperCase());
        viewTitle.getStyle()
                .set("font-size","var(--lumo-font-size-l)")
                .set("font-weight","600")
                .set("padding-right","0.75em");

//        searchField = new SearchField(
////                "Hledej uživatele...", event ->
////                "Hledej uživatele...", event -> updateKontGridContent()
//                "Hledej uživatele...",
//                event -> ((ConfigurableFilterDataProvider) kontGrid.getDataProvider()).setFilter(event.getValue())
//        );        toolBarSearch.add(viewTitle, searchField);

        HorizontalLayout searchToolBar = new HorizontalLayout(viewTitle, searchField);
        searchToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

//        HorizontalLayout kzToolBar = new HorizontalLayout(reloadViewButton);
//        kzToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
//
//        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
//        toolBarItem.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

//        kzToolBar.add(searchToolBar, kzToolBar, ribbon, toolBarItem);
        Ribbon ribbon = new Ribbon("3em");
        viewToolBar.add(searchToolBar, ribbon);
        viewToolBar.expand(ribbon);
    }

    private void updateKontGridContent() {
//        List<Kont> zaks = kontRepo.findAll();
//        kontGrid.setItems(zaks);
    }

    private Grid<Zak> initZakGrid() {
        zakGrid.setSizeFull();
        zakGrid.getElement().setAttribute("colspan", "2");
        zakGrid.setMultiSort(false);
        zakGrid.setSelectionMode(Grid.SelectionMode.NONE);
        zakGrid.setHeightByRows(true);
        zakGrid.setWidth("900px");
//        zakGrid.setHeight(null);
//        zakGrid.getElement().setAlignItems(FlexComponent.Alignment.STRETCH);

        // TODO: ID -> CSS ?
        zakGrid.setId("zak-grid");  // .. same ID as is used in shared-styles grid's dom module
        zakGrid.addColumn(Zak::getCzak).setHeader("ČZ").setWidth("3em").setResizable(true)
                .setSortProperty("poradi");
//        zakGrid.addColumn(new ComponentRenderer<>(this::createOpenDirButton)).setFlexGrow(0);
        zakGrid.addColumn(Zak::getRokmeszad).setHeader("Zadáno").setWidth("3em").setResizable(true);
        zakGrid.addColumn(Zak::getText).setHeader("Text zak.").setWidth("5em").setResizable(true);
        zakGrid.addColumn(Zak::getHonorar).setHeader("Honorář").setWidth("3em").setResizable(true);
        zakGrid.addColumn(Zak::getSkupina).setHeader("Skupina").setWidth("4em").setResizable(true);

        return zakGrid;
    }
}
