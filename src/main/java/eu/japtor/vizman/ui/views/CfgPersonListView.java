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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.ItemNames;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.service.DochsumZakService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.WageService;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.PersonFormDialog;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Arrays;


@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.PERSON_BASIC_READ, Perm.PERSON_EXT_READ
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class CfgPersonListView extends VerticalLayout implements BeforeEnterObserver {

    private static final String RADIO_PERSON_VISIBLE = "Viditelní";
    private static final String RADIO_PERSON_HIDDEN = "Skrytí";
    private static final String RADIO_PERSON_ALL = "Všichni";
    private RadioButtonGroup<String> hiddenFilterRadio;

    private PersonFormDialog personFormDialog;
    private NewItemButton newItemButton;
//    private final ReloadButton reloadButton;

//    private List<Person> personList;
    private PersonGrid personGrid;
//    ComboBox<Person> usernameSearchCombo;

    private DataProvider<Person, String> searchUsernameDataProvider;
    private DataProvider<Person, PersonService.PersonFilter> gridDataProvider; // Second type  param  must not be Void
    private ConfigurableFilterDataProvider<Person, Void, PersonService.PersonFilter> filteredGridDataProvider;


    @Autowired
    public PersonService personService;

    @Autowired
    public WageService wageService;

    @Autowired
    public DochsumZakService dochsumZakService;

    @Autowired
    public RoleService roleService;

    @Autowired
    public PasswordEncoder passwordEncoder;

//    @Autowired
//    public CfgPersonListView() {
//        initView();
//    }

    @PostConstruct
    public void postInit() {

        initView();

        // Person provider for grid
        // -------------------------
        gridDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    return personService
                            .fetchByPersonFilter(query.getFilter().orElse(null), query.getSortOrders())
                            .stream();
                },
                query -> {
                    return (int) personService.countByPersonFilter(query.getFilter().orElse(null));
                }
        );
        filteredGridDataProvider = gridDataProvider.withConfigurableFilter();


        // Person provider for combo
        // -------------------------
        // TODO: implement service method returning only List<String> (ie. user names)
////        DataProvider<Person, String> usernameDataProvider = createSearchUsernameDataProvider(personService);
//        searchUsernameDataProvider = createSearchUsernameDataProvider(personService);
//        usernameSearchCombo.setDataProvider(searchUsernameDataProvider);
//        usernameSearchCombo.addValueChangeListener(event -> {
//            Boolean hidden = true;
//            if (null == event.getValue() && null == hidden) {
//                filteredGridDataProvider.setFilter(null);
//            } else {
//                PersonService.PersonFilter personFilter = new PersonService.PersonFilter(
//                        false
//                        , null == event.getValue() ? null : event.getValue().getUsername()
//                );
//                filteredGridDataProvider.setFilter(personFilter);    // Performs .refreshAll()
//            }
//        });


        personGrid.setGridDataProvider(filteredGridDataProvider);

        personFormDialog = new PersonFormDialog (
                personService
                , wageService
                , dochsumZakService
                , roleService.fetchAll()
                , passwordEncoder
        );
        personFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishPersonEdit((PersonFormDialog) event.getSource());
            }
        });

        loadInitialViewContent();
    }

    private void initView() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);
        this.setPadding(false);
        this.setMargin(false);
        this.add(
                buildGridContainer()
        );
    }

//    private Component initUsernameCombo() {
//        usernameSearchCombo = new ComboBox<>("Username");
//        usernameSearchCombo.setItemLabelGenerator(Person::getPrijmeni);
//        usernameSearchCombo.getElement().setAttribute("colspan", "2");
//        return usernameSearchCombo;
//    }

    DataProvider<Person, String> createSearchUsernameDataProvider(PersonService service) {
        return DataProvider.fromFilteringCallbacks(query -> {
            query.getOffset();
            query.getLimit();
            // getFilter returns Optional<String>
//            String filter = query.getFilter().orElse(null);
            String filter = query.getFilter().orElse(null);
//            return service.fetchBySearchFilter(filter, null).stream();
            return service.fetchBySearchFilter(filter, null).stream();
        }, query -> {
            String filter = query.getFilter().orElse(null);
            return (int)service.countBySearchFilter(filter);
        });
    }

    DataProvider<Person, PersonService.PersonFilter> createPersonFilterDataProvider(PersonService service) {
        return DataProvider.fromFilteringCallbacks(query -> {
            query.getOffset();
            query.getLimit();
            // getFilter returns Optional<String>
//            String filter = query.getFilter().orElse(null);
            PersonService.PersonFilter filter = query.getFilter().orElse(null);
//            return service.fetchBySearchFilter(filter, null).stream();
            return service.fetchByPersonFilter(filter, null).stream();
        }, query -> {
            PersonService.PersonFilter filter = query.getFilter().orElse(null);
            return (int)service.countByPersonFilter(filter);
        });
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
//        System.out.println("###  ZaklListView.beforeEnter");
    }

// ----------------------------------


    private void loadInitialViewContent() {
//        RebuildFilterFields();
        personGrid.setHiddenFilterItems(Arrays.asList(Boolean.FALSE, Boolean.TRUE));
        personGrid.setInitialFilterValues();
        personGrid.doFilter();
//        personGrid.getDataProvider().refreshAll();
    }

    private void rebuildFilterFields() {
        // TODO: load all persons is wrong for lazy provider
//        personList = personService.fetchAll();
//        personGrid.setItems(personList);

        personGrid.setHiddenFilterItems(Arrays.asList(null, Boolean.FALSE, Boolean.TRUE));
//        personGrid.setHiddenFilterItems(personList.stream()
//                .map(Person::getHidden)
//                .filter(h -> null != h)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
    }

// ----------------------------------

    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.getStyle().set("marginTop", "0.5em");
        gridContainer.setAlignItems(Alignment.STRETCH);

        gridContainer.add(
                buildGridToolBar()
//                , initUsernameCombo()
                , initPersonGrid()
        );
        return gridContainer;
    }


    private Component initPersonGrid() {
        personGrid = new PersonGrid(
                null
                , null
                , false
                , (person, operation) -> personFormDialog.openDialog(person, operation)
        );
        personGrid.setMultiSort(true);
        personGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        return personGrid;
    }


    private NewItemButton initNewItemButton() {
        newItemButton = new NewItemButton("Nový uživatel",
                event -> personFormDialog.openDialog(new Person(), Operation.ADD)
        );
        return newItemButton;
    }

    private Component buildGridToolBar() {

        HorizontalLayout gridToolBar = new HorizontalLayout();
        gridToolBar.setWidth("100%");
        gridToolBar.setPadding(true);
        gridToolBar.setSpacing(false);
        gridToolBar.getStyle().set("padding-bottom", "5px");
//        gridToolBar.setAlignItems(Alignment.END);
        gridToolBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Ribbon ribbon = new Ribbon();
        gridToolBar.add(
                buildTitleComponent()
//                , hiddenFilterComponent
//                , new Span("")
                , initNewItemButton()
        );
        gridToolBar.expand(ribbon);

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
                new GridTitle(ItemNames.getNomP(ItemType.PERSON))
                , new Ribbon()
                , new ReloadButton(event -> loadInitialViewContent())
        );
        return titleComponent;
    }


    private void deletePerson(Person person) {
        personService.deletePerson(person);
        personGrid.getDataCommunicator().getKeyMapper().removeAll();
        personGrid.getDataProvider().refreshAll();
        Notification.show("Uživatel zrušen.", 2000, Notification.Position.BOTTOM_END);
    }

    void finishPersonEdit(PersonFormDialog personFormDialog) {
        Person resultItem = personFormDialog.getCurrentItem(); // Modified, just added or just deleted
        Person origItem = personFormDialog.getOrigItem();
        OperationResult operResult = personFormDialog.getLastOperationResult();

//        syncGridAfterPersonEdit(
//                resultItem
//                , personFormDialog.getCurrentOperation()
//                , operResult
//                , origItem
//        );

        syncGridAfterPersonEdit();

        if (OperationResult.ITEM_SAVED == operResult) {
            Notification.show(String.format("Uživatel %s uložen", resultItem.getUsername())
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == operResult) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace uživatele")
                    .withMessage(String.format("Uživatel % zrušen.", origItem.getUsername()))
                    .open();
        }
    }

//    private void syncGridAfterPersonEdit(Person itemModified, Operation oper
//            , OperationResult operRes, Person itemOrig) {
//        personGrid.getDataCommunicator().getKeyMapper().removeAll();
//        personGrid.getDataProvider().refreshAll();
//    }

    private void syncGridAfterPersonEdit() {
        personGrid.getDataCommunicator().getKeyMapper().removeAll();
        personGrid.getDataProvider().refreshAll();
    }

}
