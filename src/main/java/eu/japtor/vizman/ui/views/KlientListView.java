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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.KlientFormDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.PAGE_TITLE_KLIENT;
import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_KLIENT;

//@Route(value = ROUTE_KLIENT, layout = MainView.class)
//@PageTitle(PAGE_TITLE_KLIENT)
//@Tag(TAG_KLIENT)
@Route(value = ROUTE_KLIENT, layout = MainView.class)
@PageTitle(PAGE_TITLE_KLIENT)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.KLIENT_READ, Perm.KLIENT_MODIFY
})
// @SpringComponent
// @UIScope    // Without this annotation browser refresh throws exception
public class KlientListView extends VerticalLayout implements BeforeEnterObserver {

    private Grid<Klient> klientGrid;
    private List<Klient> klients;
//    private final Component gridToolbar;

    private KlientFormDialog klientFormDialog;
    private Button reloadButton;
    private Button resetFiltersButton;
    private Button newItemButton;

    @Autowired
    public KlientService klientService;

//    @Autowired
    public KlientListView() {
        initView();
    }

    @PostConstruct
    public void init() {

        reloadKlientGridData();

        klientFormDialog = new KlientFormDialog(
                this::saveItem
                , this::deleteItem
                , klientService
        );

        // TODO: same approach as in NablistView
//        klientFormDialog.addOpenedChangeListener(event -> {
//            if (!event.isOpened()) {
//                finishKlientEdit((KlientFormDialog) event.getSource());
//            }
//        });

    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);
//        this.setAlignItems(Alignment.STRETCH);
        this.add(
                buildGridContainer()
        );
    }

    private Component buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                buildGridBarComponent()
                , initKlientGrid()
        );
        return gridContainer;
    }

    private void reloadKlientGridData() {
        klients = klientService.fetchAll();
        klientGrid.setDataProvider(new ListDataProvider<>(klients));
        klientGrid.getDataProvider().refreshAll();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
//        System.out.println("###  KlientListView.beforeEnter");
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
        Button editBtn = new GridItemEditBtn(event -> klientFormDialog.openDialog(klient, Operation.EDIT));
        return editBtn;
    }

    private Component buildGridBarComponent() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setAlignItems(Alignment.END);
        gridBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
//        gridBar.getStyle().set("padding-bottom", "5px");

        HorizontalLayout toolBar = new HorizontalLayout(
                initNewItemButton()
        );
        toolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

//        Ribbon ribbon = new Ribbon();
        gridBar.add(
                buildTitleComponent()
                , new Ribbon()
//                , buildGridBarControlsComponent()
//                , new Ribbon()
                , buildToolBarComponent()
        );
//        gridBar.expand(ribbon);

        return gridBar;
    }

    private Component buildTitleComponent() {
//        Span viewTitle = new Span(TITLE_KLIENT.toUpperCase());
//        viewTitle.getStyle()
//                .set("font-size", "var(--lumo-font-size-l)")
//                .set("font-weight", "600")
//                .set("padding-right", "0.75em");

        HorizontalLayout titleComponent = new HorizontalLayout();
        titleComponent.setMargin(false);
        titleComponent.setPadding(false);
        titleComponent.setSpacing(false);
        titleComponent.setAlignItems(Alignment.CENTER);
        titleComponent.setJustifyContentMode(JustifyContentMode.START);
        titleComponent.add(
                new GridTitle(ItemNames.getNomP(ItemType.KLI))
                , new Ribbon()
                , initReloadButton()
                , new Ribbon()
                , initResetFiltersButton()
        );
        return titleComponent;
    }

    private Component buildToolBarComponent() {
        HorizontalLayout toolBar = new HorizontalLayout();
        toolBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        toolBar.setSpacing(false);
        toolBar.add(
                initNewItemButton()
        );
        return toolBar;
    }

    private Component initNewItemButton() {
        newItemButton = new NewItemButton("Nový klient",
                event -> klientFormDialog.openDialog(new Klient(), Operation.ADD)
        );
        return  newItemButton;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> loadInitialViewContent());
        return reloadButton;
    }

    private Component initResetFiltersButton() {
        resetFiltersButton = new ResetFiltersButton(event -> doNothing());
        return resetFiltersButton;
    }

    private void doNothing() {
    }

    private void loadInitialViewContent() {
////        loadGridDataAndRebuildFilterFields();
//        klientGrid.setVzFilterItems(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
//        klientGrid.resetFilterValues();
//        klientGrid.doFilter(KlientService.NabFilter.getEmpty());
////        nabGrid.getDataProvider().refreshAll();
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
                        "$0._scrollToIndex($1)", grid, index
                );
    }

    private void reloadView() {
//        klientDataProvider.clearFilters();
        klientGrid.getDataProvider().refreshAll();
    }


    private void saveItem(Klient klient, Operation operation) {
//        int itemIndexOrig = klients.indexOf(klient);
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

    private void deleteItem(Klient klient) {
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
