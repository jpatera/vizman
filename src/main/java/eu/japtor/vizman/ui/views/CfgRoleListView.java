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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.dataprovider.RoleFiltPagDataProvider;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.PersonGridDialog;
import eu.japtor.vizman.ui.forms.RoleFormDialog;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;

import static eu.japtor.vizman.ui.util.VizmanConst.TITLE_PERSON;

@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.ROLE_READ, Perm.ROLE_MODIFY
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class CfgRoleListView extends VerticalLayout implements BeforeEnterObserver {

    private Grid<Role> roleGrid;
    private Grid<Perm> permGrid;
//    private List<Perm> perms;
    private RoleFiltPagDataProvider roleFiltPagDataProvider;


    private PersonGridDialog assignedPersonsDialog;
    private RoleFormDialog roleFormDialog;
    private Button newItemButton;
    private Button reloadButton;

    @Autowired
    public RoleService roleService;

    @Autowired
    public PersonService personService;

//    public CfgRoleListView() {
//    }

    @PostConstruct
    public void postInit() {

        initView();

        roleFiltPagDataProvider = new RoleFiltPagDataProvider(roleService);
        roleGrid.setDataProvider(roleFiltPagDataProvider);

        roleFormDialog = new RoleFormDialog(
                this::saveItem
                , this::deleteItem
                , roleService
                , personService
                , Arrays.asList(Perm.values())

        );
        roleFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishRoleEdit((RoleFormDialog) event.getSource());
            }
        });

        assignedPersonsDialog = new PersonGridDialog(
                personService
                , "Uživatelé s přidělenou rolí"
        );

        loadInitialViewContent();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//        setAlignItems(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);
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
                buildRoleGridBarComponent()
                , initRoleGrid()
                , buildPermTitleComponent()
                , initPermGrid()
        );
        return gridContainer;
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
        newItemButton = new NewItemButton("Nová role",
                event -> roleFormDialog.openDialog(false, new Role(ItemType.ROLE), Operation.ADD)
        );
        return  newItemButton;
    }

    private Component buildRoleGridBarComponent() {
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
                buildRoleTitleComponent()
                , new Ribbon()
//                , buildGridBarControlsComponent()
//                , new Ribbon()
                , buildToolBarComponent()
        );
//        gridBar.expand(ribbon);

        return gridBar;
    }

    private Component buildRoleTitleComponent() {
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
                new GridTitle(ItemNames.getNomP(ItemType.ROLE))
                , new Ribbon()
                , initReloadButton()
//                , new Ribbon()
//                , initResetFiltersButton()
        );
        return titleComponent;
    }

    private Component buildPermTitleComponent() {
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
                new GridTitle("PŘIDĚLENÁ OPRÁVNĚNÍ")
                , new Ribbon()
                , initReloadButton()
//                , new Ribbon()
//                , initResetFiltersButton()
        );
        return titleComponent;
    }

    private Component initReloadButton() {
        reloadButton = new ReloadButton(event -> reloadGridData());
        return reloadButton;
    }

    private void loadInitialViewContent() {
        // No filters
        //  No filter values
        reloadGridData();
    }

    private void reloadGridData() {
        doFilter(buildRoleFilter());
    }

    public void doFilter(RoleService.RoleFilter filter) {
        roleFiltPagDataProvider.setFilter(filter);
    }

    // Needed for getting items for report by current grid filter until Vaadin does not  have it implemented
    public RoleService.RoleFilter buildRoleFilter() {
        return new RoleService.RoleFilter(
                getNameValue()
                , getDescriptionValue()
        );
    }

    private String getNameValue() {
        return null;    //  Name filter not provided for roles
    }

    private String getDescriptionValue() {
        return null;    //  Description filter not provided for roles
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

    private Grid<Role> initRoleGrid() {
        roleGrid = new Grid<>();
        roleGrid.setHeight("15em");
        roleGrid.setMaxHeight("15em");
        roleGrid.setMultiSort(false);
        roleGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        roleGrid.asSingleSelect().addValueChangeListener(e -> {
            if (null == e.getValue() || null == e.getValue().getPerms()) {
                permGrid.setDataProvider(new ListDataProvider<>(new ArrayList<>()));
            } else {
                permGrid.setDataProvider(new ListDataProvider<>(e.getValue().getPerms()));
            }
        });

        roleGrid.setId("role-grid");
        roleGrid.setClassName("vizman-simple-grid");

        roleGrid.addColumn(new ComponentRenderer<>(this::buildPersonsViewBtn)).setFlexGrow(0);
        roleGrid.addColumn(new ComponentRenderer<>(this::buildEditBtn)).setFlexGrow(0);
        roleGrid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true)
            .setSortProperty("name");
        roleGrid.addColumn(Role::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);

        roleGrid.getColumns().forEach(column -> column.setAutoWidth(true));

        return roleGrid;
    }

    private Grid<Perm> initPermGrid() {
        permGrid = new Grid<>();
        permGrid.setHeightByRows(true);
        permGrid.setMaxHeight("20em");
        permGrid.setMultiSort(false);
        permGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        permGrid.setId("perm-grid");
        permGrid.setClassName("vizman-simple-grid");

        permGrid.addColumn(Perm::getAuthority).setHeader("Název").setWidth("3em").setResizable(true)
                .setSortProperty("name");
        permGrid.addColumn(Perm::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);

        permGrid.getColumns().forEach(column -> column.setAutoWidth(true));

        return permGrid;
    }

    private Button buildEditBtn(Role itemFromView) {
        return new GridItemEditBtn(event -> {
                roleGrid.select(itemFromView);
                roleFormDialog.openDialog(
                        false
                        , itemFromView
                        , Operation.EDIT
                );
            }
        );
    }

    Component buildPersonsViewBtn(Role itemFromView) {
        return new GridItemBtn(event -> {
                roleGrid.select(itemFromView);
                assignedPersonsDialog.openDialog(
                        roleService.fetchByIdWithLazyPersons(itemFromView.getId())
                );
            }
            , new Icon(VaadinIcon.USERS)
            , null
        );
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

//        kzToolBar.add(toolBarSearch, toolBarGrid, ribbon, toolBarItem);
        Ribbon ribbon = new Ribbon("3em");
        viewToolBar.add(toolBarGrid, ribbon, toolBarItem);
        viewToolBar.expand(ribbon);

        return viewToolBar;
    }

    void finishRoleEdit(RoleFormDialog roleFormDialog) {
        Role resultItem = roleFormDialog.getCurrentItem(); // Modified, just added or just deleted
        Role origItem = roleFormDialog.getOrigItemCopy();
        OperationResult operResult = roleFormDialog.getLastOperationResult();

        syncGridAfterRoleEdit();

        if (OperationResult.ITEM_SAVED == operResult) {
            Notification.show(String.format("Role %s uložena", resultItem.getName())
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == operResult) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace role")
                    .withMessage(String.format("Role % zrušena.", origItem.getName()))
                    .open();
        }
    }

    private void syncGridAfterRoleEdit() {
        roleGrid.getDataCommunicator().getKeyMapper().removeAll();
        roleGrid.getDataProvider().refreshAll();
    }

    private void updateGridAfterAdd(Role newRole) {
        reloadGridData();
        roleGrid.select(newRole);
    }

    private void updateGridAfterEdit(Role modifiedRole) {
        roleGrid.getDataProvider().refreshItem(modifiedRole);
        roleGrid.select(modifiedRole);
    }

//    private void updateGridAfterDelete(int itemIndexNew) {
    private void updateGridAfterDelete() {
        reloadGridData();
        roleGrid.getDataCommunicator().getKeyMapper().removeAll();
        roleGrid.getDataProvider().refreshAll();
//        roleGrid.select(roles.get(itemIndexNew));
//        klientGrid.getDataProvider().refreshAll();
//        scrollToIndex(klientGrid, itemIndexOrig);
    }

    private void reloadView() {
//        filterawarePersonDataProvider.clearFilters();
//        filterawarePersonDataProvider.refreshAll();
        roleGrid.getDataProvider().refreshAll();

        //        filterawarePersonDataProvider.refreshItem(personToRefresh);
    }


    private void saveItem(Role role, Operation operation) {
        Role roleSaved = roleService.saveRole(role);
        roleGrid.getDataProvider().refreshItem(roleSaved);
        Notification.show(
//                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Změny role uloženy", 2000, Notification.Position.BOTTOM_END);
        if (Operation.ADD == operation) {
            updateGridAfterAdd(roleSaved);
        } else {
            updateGridAfterEdit(roleSaved);
        }
    }

    private void deleteItem(Role role) {
//        int itemIndexOrig = roles.indexOf(role);
//        int itemIndexNew = itemIndexOrig >= klients.size() - 1 ? itemIndexOrig - 1 : itemIndexOrig;
        String roleName = role.getName();
        roleService.deleteRole(role);
        Notification.show(String.format("Role '%s' zrušena.", roleName), 2000, Notification.Position.BOTTOM_END);
        updateGridAfterDelete();
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
