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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.ui.components.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.TITLE_PERSON;

@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL
        , Perm.ROLE_VIEW_READ, Perm.ROLE_VIEW_MANAGE
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class RoleListView extends VerticalLayout implements BeforeEnterObserver {

    private RoleEditorDialog roleEditForm;
//    private final TextField searchField;
    private final Button newItemButton;
    private final Button reloadViewButton;
    private Grid<Role> roleGrid;
    private Grid<Perm> permGrid;
    private final VerticalLayout gridContainer;
    private final Component viewToolbar;

    @Autowired
    public RoleService roleService;

    @Autowired
    public RoleListView() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);

        newItemButton = new NewItemButton("Nový uživatel",
                event -> roleEditForm.open(new Role(), AbstractEditorDialog.Operation.ADD)
        );

        reloadViewButton = new ReloadViewButton("Znovu na49st tabulku",
                event -> reloadView()
        );

        viewToolbar = buildViewToolBar(reloadViewButton, newItemButton);

        roleGrid = buildRoleGrid();
        permGrid = buildPermGrid();
        gridContainer = buildGridContainer(roleGrid, permGrid);
        add(viewToolbar, gridContainer);
    }

    @PostConstruct
    public void init() {

        DataProvider<Role, String> roleDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    query.getOffset();
                    query.getLimit();
//                          query.skip(query.getOffset())
//                            .take(query.getLimit())
//                    personService.fetchBySearchFilter(filter)
//                    findByExample(repository, query.getFilter())
//                            .skip(query.getOffset())
//                            .take(query.getLimit())

                            List<Role> roles = roleService.fetchAllRoles();
                            return roles.stream();
                },
                query -> (int) roleService.countAllRoles()
        );
//        ConfigurableFilterDataProvider<Role, Void, String> roleDataProvider = roleDataProv.withConfigurableFilter();
        roleGrid.setDataProvider(roleDataProvider);
        permGrid.setDataProvider(new ListDataProvider<>(new ArrayList<>()));
        roleEditForm = new RoleEditorDialog(
                this::saveRole, this::deleteRole, roleService, Arrays.asList(Perm.values()));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  RoleListView.beforeEnter");

    }

    private VerticalLayout buildGridContainer(Grid<Role> rGrid, Grid<Perm> pGrid) {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.setAlignItems(Alignment.STRETCH);
        gridContainer.add(rGrid, pGrid);
        return gridContainer;
    }

    private Grid<Role> buildRoleGrid() {
        Grid<Role> grid = new Grid<>();
        grid.setMultiSort(false);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        rGrid.setImmediate(true);


//        GridSingleSelectionModel<Role> singleSelectModel = (GridSingleSelectionModel<Role>) grid
//                .getSelectionModel();
//        singleSelectModel.addSingleSelectionListener(ssEvent -> {
//            ((ConfigurableFilterDataProvider)permGrid.getDataProvider()).setFilter(
//                    ssEvent.getSelectedItem());
////                    rGrid.getSelectedItems().stream().findFirst();
//        });
//        ConfigurableFilterDataProvider<Role, Void, String> roleDataProvider = roleDataProv.withConfigurableFilter();

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (null == e.getValue().getPerms()) {
                permGrid.setDataProvider(new ListDataProvider<>(new ArrayList<>()));
            } else {
                permGrid.setDataProvider(new ListDataProvider<>(e.getValue().getPerms()));
            }
        });


        // TODO:
        grid.setId("role-grid");  // .. same ID as is used in shared-styles grid's dom module

        grid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true)
            .setSortProperty("name");
        grid.addColumn(Role::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createEditButton)).setFlexGrow(0);
        return grid;
    }

    private Grid<Perm> buildPermGrid() {
        Grid<Perm> grid = new Grid<>();
        grid.setMultiSort(false);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        // TODO:
        grid.setId("perm-grid");  // .. same ID as is used in shared-styles grid's dom module

        grid.addColumn(Perm::getAuthority).setHeader("Název").setWidth("3em").setResizable(true)
                .setSortProperty("name");
        grid.addColumn(Perm::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);
//        pGrid.addColumn(new ComponentRenderer<>(this::createEditButton)).setFlexGrow(0);

//        grid.setDataProvider(new ListDataProvider(new ArrayList<>()));




//        DataProvider<Perm, String> personDataProv = DataProvider.fromFilteringCallbacks(
//                query -> {
////                            int offset = query.getOffset();
////                            int limit = query.getLimit();
//
////                    personService.fetchBySearchFilter(filter)
////                    findByExample(repository, query.getFilter())
////                            .skip(query.getOffset())
////                            .take(query.getLimit())
//
//                    List<Person> persons = personService.fetchByFilter(
//                            query.getFilter().orElse(null),
//                            query.getSortOrders());
//                    return persons.stream();
//                },
//                query -> (int) personService.countByFilter(query.getFilter().orElse(null))
//        );
//
//        ConfigurableFilterDataProvider<Person, Void, String> personDataProvider = personDataProv.withConfigurableFilter();
//
//        personGrid.setDataProvider(personDataProvider);
//


        return grid;
    }

    private Button createEditButton(Role role) {
        Button editBtn = new EditItemSmallButton(event -> roleEditForm.open(role,
                AbstractEditorDialog.Operation.EDIT));
        return editBtn;
    }

    private Component buildViewToolBar(final Button reloadViewButton, final Button newItemButton) {

        HorizontalLayout viewToolBar = new HorizontalLayout();
        viewToolBar.setWidth("100%");
        viewToolBar.setPadding(true);
        viewToolBar.getStyle().set("padding-bottom", "5px");

        Span viewTitle = new Span(TITLE_PERSON.toUpperCase());
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "600")
                .set("padding-right", "0.75em");

//        HorizontalLayout toolBarSearch = new HorizontalLayout(viewTitle, searchField);
//        toolBarSearch.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        HorizontalLayout toolBarGrid = new HorizontalLayout(reloadViewButton);
        toolBarGrid.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
        toolBarItem.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Span ribbon = new Span();
//        viewToolBar.add(toolBarSearch, toolBarGrid, ribbon, toolBarItem);
        viewToolBar.add(toolBarGrid, ribbon, toolBarItem);
        viewToolBar.expand(ribbon);

        return viewToolBar;
    }

    private void updateGridContent() {
//        personDataProvider.refreshAll();
//        grid.setItems(service.findAll())

//        personGrid.getDataProvider().fetch();

//        Person newInstance = personGrid.getDataCommunicator().getKeyMapper().removeAll();
//        Person newInstance = service.save(personToChange);
//        dataProvider.refreshItem(newInstance);
//        personGrid.getDataProvider().refreshAll();

//        List<Person> personList = personService.fetchBySearchFilter(searchField.getValue());

//        ListDataProvider<Person> personDataProvider =
//                DataProvider.ofCollection(personService.fetchBySearchFilter(searchField.getValue()));
//
//        personDataProvider.setSortOrder(Person::getUsername,
//                SortDirection.ASCENDING);

//        personGrid.setItems(personList);
//        personGrid.setDataProvider(filteredDataProvider);
//        personGrid.setDataProvider(SpringDataProviderBuilder.forRepository(personRepo));
//        personGrid.setDataProvider(personDataProvider);
    }


    private void reloadView() {
//        personDataProvider.clearFilters();
//        personDataProvider.refreshAll();
        roleGrid.getDataProvider().refreshAll();

        //        personDataProvider.refreshItem(personToRefresh);
    }


    private void saveRole(Role role, AbstractEditorDialog.Operation operation) {
        Role newInstance = roleService.saveRole(role);
        roleGrid.getDataProvider().refreshItem(newInstance);
        Notification.show(
//                "User successfully " + operation.getNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Změny role uloženy", 3000, Notification.Position.BOTTOM_END);
        updateGridContent();
    }

    private void deleteRole(Role role) {
        String roleName = role.getName();
        roleService.deleteRole(role);
        roleGrid.getDataCommunicator().getKeyMapper().removeAll();
        roleGrid.getDataProvider().refreshAll();

        Notification.show(String.format("Role '%s' zrušena.", roleName), 3000, Notification.Position.BOTTOM_END);
        updateGridContent();
    }

//    private Component createComponent(Integer id, String color) {
//
//        HorizontalLayout compWrap = new HorizontalLayout();
//        for (int i = 1; i < 4; i++) {
//            Button comp = new Button("Comp " + id + " - " + i);
//            comp.getStyle()
//                    .set("background-color", color)
//                    .set("theme", "icon small")
////                    .set("margin", "0")
//            ;
//            comp.setText("");
//            comp.setIcon(new Icon("lumo", "reload"));
//            comp.getElement().setAttribute("theme", "secondary small");
//            compWrap.add(comp);
//        }
//        return compWrap;
//    }

}
