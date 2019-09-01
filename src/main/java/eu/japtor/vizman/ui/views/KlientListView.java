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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.KlientFormDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_KLIENT;
import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_PRUH;
import static eu.japtor.vizman.ui.util.VizmanConst.TITLE_KLIENT;

//@Route(value = ROUTE_KLIENT, layout = MainView.class)
//@PageTitle(PAGE_TITLE_KLIENT)
//@Tag(TAG_KLIENT)
@Route(value = ROUTE_KLIENT, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.KLIENT_READ, Perm.KLIENT_MODIFY
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class KlientListView extends VerticalLayout implements BeforeEnterObserver {

    private KlientFormDialog klientFormDialog;
    private final SearchField searchField;
    private final Button newItemButton;
    private final Button reloadViewButton;
    private Grid<Klient> klientGrid;
    private List<Klient> klients;
    private final Component viewToolbar;


    @Autowired
    public KlientService klientService;

    @Autowired
    public KlientListView() {

        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);
        searchField = new SearchField(
//                "Hledej uživatele...", event ->
//                "Hledej uživatele...", event -> updateGridContent()
                "Hledej klienta...",
//                event -> ((ConfigurableFilterDataProvider) klientGrid.getDataProvider()).setFilter(event.getStringValue())
                event -> ((CallbackDataProvider) klientGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getValue()))
//                event -> ((CallbackDataProvider) klientGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getStringValue()))
        );

        newItemButton = new NewItemButton("Nový klient",
                event -> klientFormDialog.openDialog(new Klient(), Operation.ADD, null, null)
        );

        reloadViewButton = new ReloadButton("Znovu načíst tabulku",
                event -> reloadView()
        );

        viewToolbar = buildViewToolBar(reloadViewButton, newItemButton);

//        klientGrid = initKlientGrid();
//        gridContainer = buildGridContainer(klientGrid);
        this.add(viewToolbar, initKlientGrid());
    }

    @PostConstruct
    public void init() {

////        DataProvider<Person, Person> personDataProvider = DataProvider.fromFilteringCallbacks(
//        DataProvider<Klient, String> klientDataProvider = DataProvider.fromFilteringCallbacks(
//                query -> {
//                    query.getOffset();
//                    query.getLimit();
//                    List<Klient> klients = klientService.fetchBySearchFilter(
//                            query.getFilter().orElse(null),
//                            query.getSortOrders());
//                    return klients.stream();
//
////                    int offset = query.getOffset();
////                    int limit = query.getLimit();
////                    personService.fetchBySearchFilter(filter)
////                    findByExample(repository, query.getFilter())
////                            .skip(query.getOffset())
////                            .take(query.getLimit())
////                    query.getFilter().orElse(null),
//                },
//                query -> (int) klientService.countByFilter(query.getFilter().orElse(null))
//        );
// ------------------------------

//        klientGrid.setDataProvider(klientDataProvider);

        reloadKlientGridData();
        klientFormDialog = new KlientFormDialog(
                this::saveKlient, this::deleteKlient, klientService);
//        initKlientGrid();

//        updateGridContent();

//        super(EntityUtil.getName(Person.class), klientFormDialog);
//        this.presenter = presenter;
//        klientFormDialog.setBinder(binder);
//        setupEventListeners();
    }

    private void reloadKlientGridData() {
        klients = klientService.fetchAll();
        klientGrid.setDataProvider(new ListDataProvider<>(klients));
        klientGrid.getDataProvider().refreshAll();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  KlientListView.beforeEnter");
    }

    private VerticalLayout buildGridContainer(Grid<Klient> grid) {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.setAlignItems(Alignment.STRETCH);
        gridContainer.add(grid);
        return gridContainer;
    }

    private Grid initKlientGrid() {
        klientGrid = new Grid<>();
        klientGrid.setMultiSort(true);
        klientGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        klientGrid.setId("klient-grid");  // .. same ID as is used in shared-styles grid's dom module

        klientGrid.addColumn(new ComponentRenderer<>(this::buildEditBtn))
                .setHeader("Edit")
                .setFlexGrow(0)
        ;

        klientGrid.addColumn(Klient::getName)
                .setHeader("Název")
                .setWidth("25em")
                .setFlexGrow(0)
                .setResizable(true)
//                .setFrozen(true)
        ;

        klientGrid.addColumn(Klient::getNote)
                .setHeader("Poznámka")
                .setWidth("25em")
                .setFlexGrow(1)
                .setResizable(true)
//                .setFrozen(true)
        ;

//        getStyle().set("border", "1px solid #9E9E9E");    // Horizontal borders?

        return klientGrid;
    }

    private Button buildEditBtn(Klient klient) {
        Button editBtn = new GridItemEditBtn(event -> klientFormDialog.openDialog(
                klient, Operation.EDIT, null, null));
        return editBtn;
    }

    private Component buildViewToolBar(final Button reloadViewButton, final Button newItemButton) {

        HorizontalLayout viewToolBar = new HorizontalLayout();
        viewToolBar.setWidth("100%");
        viewToolBar.setPadding(true);
        viewToolBar.getStyle().set("padding-bottom", "5px");

        Span viewTitle = new Span(TITLE_KLIENT.toUpperCase());
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "600")
                .set("padding-right", "0.75em");

//        HorizontalLayout searchToolBar = new HorizontalLayout(viewTitle, searchField);
//        searchToolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

//        HorizontalLayout gridToolBar = new HorizontalLayout(reloadViewButton);
//        gridToolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
        toolBarItem.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Ribbon ribbon = new Ribbon();
        viewToolBar.add(viewTitle, ribbon, toolBarItem);
        viewToolBar.expand(ribbon);

        return viewToolBar;
    }

    private void updateGridAfterAdd(Klient newKlient) {
//        klientGrid.getDataCommunicator().getKeyMapper().removeAll();
        reloadKlientGridData();
        klientGrid.select(newKlient);
    }

    private void updateGridAfterEdit(Klient modifiedKlient) {
//        klientGrid.getDataCommunicator().getKeyMapper().removeAll();
        klientGrid.getDataProvider().refreshItem(modifiedKlient);
//        klientGrid.select(newSelectedKlient);
//        scrollToIndex(klientGrid, itemIndexOrig);
//        klientGrid.getDataProvider().refreshAll();
        klientGrid.select(modifiedKlient);
    }

    private void updateGridAfterDelete(int itemIndexNew) {
//        klientGrid.getDataCommunicator().getKeyMapper().removeAll();
//        reloadKlientGridData();
//        klientGrid.getDataProvider().refreshAll();
        reloadKlientGridData();
        klientGrid.select(klients.get(itemIndexNew));
//        klientGrid.getDataProvider().refreshAll();

//        scrollToIndex(klientGrid, itemIndexOrig);
    }

    private void scrollToIndex(Grid<?> grid, int index) {
        UI.getCurrent().getPage()
                .executeJavaScript(
                        "$0._scrollToIndex(" + index + ")", grid.getElement()
                );
    }

    private void reloadView() {
//        klientDataProvider.clearFilters();
        klientGrid.getDataProvider().refreshAll();
    }


    private void saveKlient(Klient klient, Operation operation) {
        int itemIndexOrig = klients.indexOf(klient);
        Klient klientSaved = klientService.saveKlient(klient);
//        klientGrid.getDataProvider().refreshItem(klientSaved);
        Notification.show(
                "Klient uložen", 2000, Notification.Position.MIDDLE);
        if (Operation.ADD == operation) {
            updateGridAfterAdd(klientSaved);
        } else {
            updateGridAfterEdit(klientSaved);
        }
    }

    private void deleteKlient(Klient klient) {
        int itemIndexOrig = klients.indexOf(klient);
        int itemIndexNew = itemIndexOrig >= klients.size() - 1 ? itemIndexOrig - 1 : itemIndexOrig;
        klientService.deleteKlient(klient);
//        Klient newSelectedKlient = klients.get(itemIndexNew);
//        klientGrid.getDataCommunicator().getKeyMapper().removeAll();
//        klientGrid.getDataProvider().refreshAll();
        Notification.show(
                "Klient zrušen.", 2000, Notification.Position.MIDDLE);
        updateGridAfterDelete(itemIndexNew);
    }
}
