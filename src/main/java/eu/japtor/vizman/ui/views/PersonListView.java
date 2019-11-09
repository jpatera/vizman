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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.service.DochsumZakService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.WageService;
import eu.japtor.vizman.backend.service.RoleService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import eu.japtor.vizman.ui.forms.PersonFormDialog;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static eu.japtor.vizman.app.security.SecurityUtils.isWagesAccessGranted;
import static eu.japtor.vizman.ui.util.VizmanConst.*;


@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.PERSON_BASIC_READ, Perm.PERSON_EXT_READ
})
@SpringComponent
@UIScope    // Without this annotation browser refresh throws exception
public class PersonListView extends VerticalLayout implements BeforeEnterObserver {

    private static final String RADIO_PERSON_VISIBLE = "Viditelní";
    private static final String RADIO_PERSON_HIDDEN = "Skrytí";
    private static final String RADIO_PERSON_ALL = "Všichni";
    private RadioButtonGroup<String> hiddenFilterRadio;

    private PersonFormDialog personFormDialog;
    private final Button newItemButton;
    private final Button reloadViewButton;
    private Grid<Person> personGrid;

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

    @Autowired
    public PersonListView() {

        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);
//        searchField = new SearchField(
////                "Hledej uživatele...", event ->
////                "Hledej uživatele...", event -> updateGridContent()
//                "Hledej uživatele...", null
////                event -> ((ConfigurableFilterDataProvider) personGrid.getDataProvider()).setFilter(event.getStringValue())
////                event -> ((CallbackDataProvider) personGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getStringValue()))
////                event -> ((CallbackDataProvider) personGrid.getDataProvider()).fetchFromBackEnd(new Query(event.getStringValue()))
//        );

        newItemButton = new NewItemButton("Nový uživatel",
                event -> personFormDialog.openDialog(new Person(), Operation.ADD)
        );

        reloadViewButton = new ReloadButton("Inicializovat tabulku",
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
////                DataProvider.ofCollection(personService.fetchBySearchFilter(searchField.getStringValue()));
//        personDataProvider.setSortOrder(Person::getUsername, SortDirection.ASCENDING);
//
// ------------------------------
//
//        ConfigurableFilterDataProvider<Person, Void, String> buildDataProvider() {
//            CallbackDataProvider<Person, String> dataProvider = DataProvider.fromFilteringCallbacks(
//                    q -> q.getFilter()
//                            .map(document -> personService.findAll(buildExample(document), ChunkRequest.of(q, defaultSort)).getContentPane())
//                            .orElseGet(() -> personService.findAll(ChunkRequest.of(q, defaultSort)).getContentPane())
//                            .stream(),
////                    q -> Ints.checkedCast(q
//                    q -> (int) q
//                            .getFilter()
//                            .map(document -> personService.count(buildExample(document)))
//                            .orElseGet(personService::count)));
//            return dataProvider.withConfigurableFilter((q, c) -> c);
//        }
// ------------------------------

//        personDataProvider = DataProvider.ofCollection(personService.fetchBySearchFilter(searchField.getStringValue()));
//        personDataProvider.setSortOrder(Person::getUsername, SortDirection.ASCENDING);
//        wrapper.setSortOrder(Person::getUsername, SortDirection.ASCENDING);

        personGrid.setDataProvider(personDataProvider);

        personFormDialog = new PersonFormDialog (
                personService
                , wageService
                , dochsumZakService
                , roleService.fetchAllRoles()
                , passwordEncoder
        );
        personFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishPersonEdit((PersonFormDialog) event.getSource());
            }
        });

//        initPersonGrid();

//        updateGridContent();

//        super(EntityUtil.getName(Person.class), personFormDialog);
//        this.presenter = presenter;
//        personFormDialog.setBinder(binder);
//        setupEventListeners();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
//        System.out.println("###  ZaklListView.beforeEnter");
    }

// ----------------------------------

    private Component initArchFilterRadio() {
        hiddenFilterRadio = new RadioButtonGroup<>();
        hiddenFilterRadio.setItems(RADIO_PERSON_VISIBLE, RADIO_PERSON_HIDDEN, RADIO_PERSON_ALL);
        hiddenFilterRadio.getStyle().set("alignItems", "center");
        hiddenFilterRadio.getStyle().set("theme", "small");
//        hiddenFilterRadio.addValueChangeListener(event -> loadViewContent());
        return hiddenFilterRadio;
    }

//    private void loadViewContent() {
//        loadGridDataAndRebuildFilterFields();
//        personGrid.initFilterValues();
//        personGrid.doFilter();
//        personGrid.getDataProvider().refreshAll();
//    }
//
//    private void loadGridDataAndRebuildFilterFields() {
//        personList = personRepo.findAllByOrderByCkontDescCzakDesc();
//        personGrid.setItems(persList);
//        personGrid.setRokFilterItems(persList.stream()
//                .filter(z -> null != z.getRok())
//                .map(Person::getRok)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//        personGrid.setSkupinaFilterItems(persList.stream()
//                .map(Person::getSkupina)
//                .filter(s -> null != s)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//        personGrid.setHiddenFilterItems(persList.stream()
//                .map(Person::getHidden)
//                .filter(a -> null != a)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//    }
//
//    public void initFilterValues() {
//        ((ListDataProvider<Person>) this.getDataProvider()).clearFilters();
//        if (null == this.initFilterArchValue) {
//            archFilterField.clear();
//        } else {
//            archFilterField.setValue(this.initFilterArchValue);
//        }
//        rokFilterField.clear();
//        skupinaFilterField.clear();
//        kzCisloFilterField.clear();
//        kzTextFilterField.clear();
//        objednatelFilterField.clear();
//    }

// ----------------------------------

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
//        personGrid.addColumn(Person::getState)
//                .setHeader("State")
//                .setSortProperty("state")
//                .setWidth("5em")
//                .setResizable(true)
//                .setFrozen(true)
//        ;
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
        if (isWagesAccessGranted()) {
            personGrid.addColumn(new NumberRenderer<>(Person::getWageCurrent, VzmFormatUtils.moneyFormat))
                    .setTextAlign(ColumnTextAlign.END)
                    .setHeader("Sazba aktuální")
                    .setWidth("8em")
                    .setResizable(true)
            ;
        }
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
        Button editBtn = new GridItemEditBtn(event -> personFormDialog.openDialog(person, Operation.EDIT));
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

//        List<Person> personList = personService.fetchBySearchFilter(searchField.getStringValue());

//        ListDataProvider<Person> personDataProvider =
//                DataProvider.ofCollection(personService.fetchBySearchFilter(searchField.getStringValue()));
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

    private void deletePerson(Person person) {
        personService.deletePerson(person);
        personGrid.getDataCommunicator().getKeyMapper().removeAll();
        personGrid.getDataProvider().refreshAll();
        Notification.show("Uživatel zrušen.", 2000, Notification.Position.BOTTOM_END);
    }

    void finishPersonEdit(PersonFormDialog personFormDialog) {
        Person itemModified = personFormDialog.getCurrentItem(); // Modified, just added or just deleted
        Operation oper = personFormDialog.getCurrentOperation();
        OperationResult operResult = personFormDialog.getLastOperationResult();

//        if (NO_CHANGE != operResult) {
//            personChanged = true;
//        }
        Person itemOrig = personFormDialog.getOrigItem();

        syncGridAfterPersonEdit(itemModified, oper, operResult, itemOrig);

        if (OperationResult.ITEM_SAVED == operResult) {
            Notification.show(String.format("Uživatel uložen")
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == operResult) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace uživatele")
                    .withMessage(String.format("Uživatel zrušen."))
                    .open();
        }
    }

    private void syncGridAfterPersonEdit(Person itemModified, Operation oper
            , OperationResult operRes, Person itemOrig) {

//        if (NO_CHANGE == operRes) {
//            return;
//        }

//        Person person = personService.fetchOne(itemModified.getId());
        personGrid.getDataCommunicator().getKeyMapper().removeAll();
//        personGrid.setItems(person.getWages());
        personGrid.getDataProvider().refreshAll();
//        personWageGrid.select(personWageModified);
    }

}


