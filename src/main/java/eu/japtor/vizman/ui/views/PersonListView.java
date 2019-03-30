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
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.PersonEditorDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

//@Route(value = ROUTE_PERSON, layout = MainView.class)
//@PageTitle(PAGE_TITLE_PERSON)
//@Tag(TAG_PERSON)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.PERSON_BASIC_READ, Perm.PERSON_EXT_READ
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class PersonListView extends VerticalLayout implements BeforeEnterObserver {

    private PersonEditorDialog personEditForm;
//    private final SearchField searchField;
    private final Button newItemButton;
    private final Button reloadViewButton;
    private Grid<Person> personGrid;


    @Autowired
    public PersonService personService;

    @Autowired
    public RoleService roleService;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public PersonListView() {

        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);
//        searchField = new SearchField(
////                "Hledej uživatele...", event ->
////                "Hledej uživatele...", event -> updateGridContent()
//                "Hledej uživatele...", null
////                event -> ((ConfigurableFilterDataProvider) personGrid.getDataProvider()).setFilter(event.getValue())
////                event -> ((CallbackDataProvider) personGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getValue()))
////                event -> ((CallbackDataProvider) personGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getValue()))
//        );

        newItemButton = new NewItemButton("Nový uživatel",
                event -> personEditForm.open(new Person(), Operation.ADD, "")
        );

        reloadViewButton = new ReloadViewButton("Inicializovat tabulku",
                event -> reloadView()
        );

        this.add(
                buildViewToolBar(reloadViewButton, newItemButton)
                , buildGridContainer()
        );
    }

    @PostConstruct
    public void init() {

        DataProvider<Person, String> personDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    query.getOffset();
                    query.getLimit();
                    List<Person> persons = personService.fetchBySearchFilter(
                            query.getFilter().orElse(null),
                            query.getSortOrders());
                    return persons.stream();

//                    int offset = query.getOffset();
//                    int limit = query.getLimit();
//                    personService.fetchBySearchFilter(filter)
//                    findByExample(repository, query.getFilter())
//                            .skip(query.getOffset())
//                            .take(query.getLimit())
//                    query.getFilter().orElse(null),
                },
                query -> (int) personService.countByFilter(query.getFilter().orElse(null))
        );
        // We don't need this as long as the filter function remains the same:
        // ConfigurableFilterDataProvider<Person, Void, String> personDataProvider = personDataProv.withConfigurableFilter();

// ------------------------------
//        ConfigurableFilterDataProvider<Person, Void, String> wrapper =
//                personDataProvider.withConfigurableFilter();
//        personDataProvider =
//                DataProvider.ofCollection(personService.fetchAll());
//
//        personGrid.setDataProvider(personDataProvider);
////                DataProvider.ofCollection(personService.fetchBySearchFilter(searchField.getValue()));
//        personDataProvider.setSortOrder(Person::getUsername, SortDirection.ASCENDING);
//
// ------------------------------
//
//        ConfigurableFilterDataProvider<Person, Void, String> buildDataProvider() {
//            CallbackDataProvider<Person, String> dataProvider = DataProvider.fromFilteringCallbacks(
//                    q -> q.getFilter()
//                            .map(document -> personService.findAll(buildExample(document), ChunkRequest.of(q, defaultSort)).getContent())
//                            .orElseGet(() -> personService.findAll(ChunkRequest.of(q, defaultSort)).getContent())
//                            .stream(),
////                    q -> Ints.checkedCast(q
//                    q -> (int) q
//                            .getFilter()
//                            .map(document -> personService.count(buildExample(document)))
//                            .orElseGet(personService::count)));
//            return dataProvider.withConfigurableFilter((q, c) -> c);
//        }
// ------------------------------

//        personDataProvider = DataProvider.ofCollection(personService.fetchBySearchFilter(searchField.getValue()));
//        personDataProvider.setSortOrder(Person::getUsername, SortDirection.ASCENDING);
//        wrapper.setSortOrder(Person::getUsername, SortDirection.ASCENDING);

        personGrid.setDataProvider(personDataProvider);

        personEditForm = new PersonEditorDialog(
                this::savePerson, this::deletePerson, personService, roleService.fetchAllRoles(), passwordEncoder);
//        initPersonGrid();

//        updateGridContent();

//        super(EntityUtil.getName(Person.class), personEditForm);
//        this.presenter = presenter;
//        personEditForm.setBinder(binder);
//        setupEventListeners();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  ZaklListView.beforeEnter");
    }

    private VerticalLayout buildGridContainer() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setClassName("view-container");
        gridContainer.setAlignItems(Alignment.STRETCH);
        gridContainer.add(initPersonGrid());
        return gridContainer;
    }

    private Grid initPersonGrid() {
        personGrid = new Grid<>();
        personGrid.setMultiSort(true);
        personGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        personGrid.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module

        personGrid.addColumn(Person::getId)
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("ID")
                .setSortProperty("id")
                .setWidth("5em")
                .setResizable(true)
                .setFrozen(true)
        ;
        personGrid.addColumn(Person::getState)
                .setHeader("State")
                .setSortProperty("state")
                .setWidth("5em")
                .setResizable(true)
                .setFrozen(true)
        ;
        personGrid.addColumn(new ComponentRenderer<>(this::buildEditBtn))
                .setFlexGrow(0)
        ;
        personGrid.addColumn(Person::getUsername)
                .setHeader("Username")
                .setSortProperty("username")
                .setWidth("8em")
                .setResizable(true)
                .setFrozen(true)
        ;
        personGrid.addColumn(Person::getJmeno)
                .setHeader("Jméno")
                .setSortProperty("jmeno")
                .setWidth("8em")
                .setResizable(true)
        ;
        personGrid.addColumn(Person::getPrijmeni)
                .setHeader("Příjmení")
                .setSortProperty("prijmeni")
                .setWidth("8em")
                .setResizable(true)
        ;
        personGrid.addColumn(new NumberRenderer<>(Person::getSazba, VzmFormatUtils.moneyFormat))
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Sazba")
                .setWidth("8em")
                .setResizable(true)
        ;
        personGrid.addColumn(Person::getNastup)
                .setHeader("Nástup")
                .setWidth("8em")
                .setResizable(true)
        ;
        personGrid.addColumn(Person::getVystup)
                .setHeader("Ukončení")
                .setWidth("8em")
                .setResizable(true)
        ;
        return personGrid;
    }

    private Button buildEditBtn(Person person) {
        Button editBtn = new GridItemEditBtn(event -> personEditForm.open(
                person, Operation.EDIT, ""));
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

        // TODO: person serach field
        //        HorizontalLayout searchToolBar = new HorizontalLayout(viewTitle, searchField);
        HorizontalLayout searchToolBar = new HorizontalLayout(viewTitle);
        searchToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout gridToolBar = new HorizontalLayout(reloadViewButton);
        gridToolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout toolBarItem = new HorizontalLayout(newItemButton);
        toolBarItem.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Ribbon ribbon = new Ribbon();
        viewToolBar.add(searchToolBar, gridToolBar, ribbon, toolBarItem);
        viewToolBar.expand(ribbon);

        return viewToolBar;
    }

//    private void updateGridContent() {
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
//    }


    private void reloadView() {
//        personDataProvider.clearFilters();
//        personDataProvider.refreshAll();
        personGrid.getDataProvider().refreshAll();
//        personDataProvider.refreshItem(personToRefresh);
    }


    private void savePerson(Person person, Operation operation) {
        Person newInstance = personService.savePerson(person);
        personGrid.getDataProvider().refreshItem(newInstance);
        Notification.show(
                "Změny uživatele uloženy", 2000, Notification.Position.BOTTOM_END);
    }

    private void deletePerson(Person person) {
        personService.deletePerson(person);
        personGrid.getDataCommunicator().getKeyMapper().removeAll();
        personGrid.getDataProvider().refreshAll();
        Notification.show("Uživatel zrušen.", 2000, Notification.Position.BOTTOM_END);
    }
}
