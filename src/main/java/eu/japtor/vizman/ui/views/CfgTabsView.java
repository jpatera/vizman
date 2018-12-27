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

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.ExtendedPagedTabs;
import eu.japtor.vizman.ui.forms.PersonEditorDialog;
import eu.japtor.vizman.ui.forms.SysPropsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.*;

import javax.annotation.PostConstruct;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static eu.japtor.vizman.ui.util.VizmanConst.*;

@Route(value = ROUTE_CFG, layout = MainView.class)
@PageTitle(PAGE_TITLE_CFG)
//@Tag(TAG_CFG)
// Note: @SpringComponent  ..must not be defined, otherwise refresh (in Chrome) throws exception
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL})
public class CfgTabsView extends VerticalLayout implements BeforeEnterObserver {

    private final TextField searchField = new TextField("", "Hleadat uživatele");

    private final H3 cfgTabsHeader = new H3(TITLE_CFG);
//    private final Grid<Person> grid = new Grid<>();

    private PersonEditorDialog personEditForm;

    @Autowired
    PersonListView personListView;

    @Autowired
    RoleListView roleListView;

    @Autowired
    CinListView cinListView;

    @Autowired
    SysPropsForm cfgSysForm;

//    @Autowired
//    public CfgTabsView() {
////	public ProductsView(CrudEntityPresenter<Product> presenter) {
//    }

    private FullCalendar cal = FullCalendarBuilder.create().build();


    @PostConstruct
    public void init() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

//        personEditForm = new PersonEditorDialog(
//                this::savePerson, this::deletePerson, personService);
//
//        addSearchBar();
//        setupGrid();
//        updateView();

        initTabs();

    }

    private void initTabs() {
//        VerticalLayout container = new VerticalLayout();
//        container.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        ExtendedPagedTabs cfgExtTabs = new ExtendedPagedTabs();
        Tab tabPerson = new Tab("Uživatelé");
        Tab tabRole = new Tab("Role");
        Tab tabCin = new Tab("Činnosti");
        Tab tabSys = new Tab("Systém");
        Tab tabCal = new Tab("Kalendář");

//        personListView = new UserListView();
        cfgExtTabs.add(personListView, tabPerson);
        cfgExtTabs.add(roleListView, tabRole);
        cfgExtTabs.add(cinListView, tabCin);
        cfgExtTabs.add(cfgSysForm, tabSys);
        cfgExtTabs.add(cal, tabCal);
            //container.setFlexGrow(1, calendar);


        Entry entry = new Entry();
        entry.setTitle("Svatýho Dyndy");
        entry.setStart(LocalDateTime.now());
//        entry.setStart(LocalDate.now().withDayOfMonth(3).atTime(0, 0));
        entry.setEnd(entry.getStart().plusDays(3));
//        entry.setColor("#ff3333");
        entry.setColor(Color.YELLOW.toString());
        entry.setAllDay(true);
        entry.setRenderingMode(Entry.RenderingMode.BACKGROUND);
        entry.setDescription("DESC-DESC");

        Entry entry2 = new Entry();
        entry2.setTitle("Svatýho Dyndy 222");
        entry2.setStart(LocalDateTime.now().plusDays(1));
//        entry2.setStart(LocalDate.now().withDayOfMonth(3).atTime(0, 0));
        entry2.setEnd(entry.getStart().plusDays(1));
//        entry.setColor("#ff3333");
        entry2.setColor(Color.RED.toString());
        entry2.setAllDay(true);
        entry2.setRenderingMode(Entry.RenderingMode.NORMAL);
        entry2.setDescription("DESC-DESC 222");

        cal.setLocale(CalendarLocale.CZECH);
        cal.setNowIndicatorShown(true);
        cal.setTimeslotsSelectable(true);
        cal.setWeekNumbersVisible(true);
        cal.setBusinessHours(
                new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0),BusinessHours.DEFAULT_BUSINESS_WEEK)
//                new BusinessHours(LocalTime.of(12, 0), LocalTime.of(15, 0), DayOfWeek.SATURDAY)
        );
        cal.addEntry(entry);
        cal.addEntry(entry2);
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
//        upload.addSucceededListener(event -> {
////            Component component = createComponent(event.getMIMEType(),
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(), buffer.getInputStream());
//            showOutput(event.getFileName(), component, output);
//        });

        VerticalLayout systemCfg =  new VerticalLayout();
//        systemCfg.add(new H4("SYSTÉM"));
//        systemCfg.add(new TextField("Application locale", Locale.getDefault().toString()));
//        systemCfg.add(new TextField("Project root", "P:\\projects"));
//        systemCfg.add(new TextField("Document root", "L:\\documents"));

//        cfgExtTabs.add(systemCfg, tabSys);


//        tabs.select(tabCin);  // -> Tohle rozhazuje UI layout
//        tabs.setOrientation(Tabs.Orientation.VERTICAL);
//        cfgExtTabs.setSelectedTab(tabCin);

//        HorizontalLayout menuPanel = new HorizontalLayout();
//        menuPanel.add(cfgTabsHeader)
//        container.add(cfgTabsHeader, tabs);

//        container.add(cfgTabs);

//        menuPanel.add(tabs);
//        add(container);
        add(cfgExtTabs);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Navigation first gos here, then to the beforeEnter of MainView
        System.out.println("###  CfgTabsView.beforeEnter");
    }



//    private void addSearchBar() {
//        Div viewToolbar = new Div();
//        viewToolbar.addClassName("view-toolbar");
//
//        searchField.setPrefixComponent(new Icon("lumo", "search"));
//        searchField.addClassName("view-toolbar__search-field");
//        searchField.addValueChangeListener(e -> updateView());
//        searchField.setValueChangeMode(ValueChangeMode.EAGER);
//
//        Button newButton = new Button("Nový uživatel", new Icon("lumo", "plus"));
//        newButton.getElement().setAttribute("theme", "primary");
//        newButton.addClassName("view-toolbar__button");
//        newButton.addClickListener(e -> personEditForm.open(new Person(),
//                AbstractEditorDialog.Operation.ADD));
//
//        viewToolbar.add(searchField, newButton);
//        add(viewToolbar);
//    }


//    private void setupGrid() {
//        VerticalLayout container = new VerticalLayout();
//        container.setClassName("view-container");
//        container.setAlignItems(Alignment.STRETCH);
//
//        grid.addColumn(Person::getStatus).setHeader("S").setWidth("8em").setResizable(true);
//        grid.addColumn(Person::getUsername).setHeader("Username").setWidth("8em").setResizable(true);
//        grid.addColumn(Person::getPassword).setHeader("Password").setWidth("8em").setResizable(true);
//        grid.addColumn(new ComponentRenderer<>(this::createEditButton))
//                .setFlexGrow(0);
//        grid.addColumn(Person::getJmeno).setHeader("Jméno").setWidth("8em").setResizable(true);
//        grid.addColumn(Person::getPrijmeni).setHeader("Příjmení").setWidth("8em").setResizable(true);
//
////        Grid.Column<Person> column = grid.addColumn(Person::getSazba)
//        Grid.Column<Person> column = grid.addColumn(new ComponentRenderer<>(() -> {
//            return new Icon(VaadinIcon.FEMALE);
//        }));
//        column.setHeader("Sazba").setWidth("8em").setResizable(true);
//
////        (new NumberRenderer("$%.5f")
////        NumberFormat nf = NumberFormat.getInstance();
////        column.set Renderer(new NumberRenderer("$%.5f"));
//
//        grid.addColumn(Person::getNastup).setHeader("Nástup").setWidth("8em").setResizable(true);
//        grid.addColumn(Person::getVystup).setHeader("Ukončení").setWidth("8em").setResizable(true);
//
//        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
//
//        // TODO:
////        grid.setDataProvider(filteredDataProvider);
//
//        container.add(header, grid);
//        add(container);
//    }
//
//    private Button createEditButton(Person person) {
//        Button edit = new Button("", event -> personEditForm.open(person,
//                AbstractEditorDialog.Operation.EDIT));
//        edit.setIcon(new Icon("lumo", "edit"));
//        edit.addClassName("review__edit");
//        edit.getElement().setAttribute("theme", "tertiary");
//        return edit;
//    }
//
//    private void updateView() {
//        List<Person> personList = personService.fetchAll();
//        grid.setItems(personList);
//        // TODO:
////        grid.setDataProvider(SpringDataProviderBuilder.forRepository(personRepo));
//
//        if (searchField.getValue().length() > 0) {
//            header.setText("Uživatelé - hledat “"+ searchField.getValue() +"”");
//        } else {
//            header.setText("Uživatelé");
//        }
//    }
//
//    private void savePerson(Person person, AbstractEditorDialog.Operation operation) {
//        personService.savePerson(person);
//
//        Notification.show(
////                "User successfully " + operation.getNameInText() + "ed.", 3000, Position.BOTTOM_START);
//                "Uživatel zrušen", 3000, Notification.Position.BOTTOM_START);
//        updateView();
//    }
//    private void deletePerson(Person person) {
////        List<Review> reviewsInPerson = ReviewService.getInstance()
////                .findReviews(person.getName());
//
////        reviewsInPersons.forEach(review -> {
////            review.setPerson(PersonService.getInstance()
////                    .find PersonOrThrow("Undefined"));
////            ReviewService.getInstance().saveReview(review);
////        });
//        personService.deletePerson(person);
//
//        Notification.show("Uživatel zrušen.", 3000, Notification.Position.BOTTOM_START);
//        updateView();
//    }
}
