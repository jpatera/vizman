package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.dom.Element;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static eu.japtor.vizman.app.security.SecurityUtils.isWagesAccessGranted;

public class PersonGrid extends Grid<Person> {

    // Person Grid field keys:
    public static final String HIDDEN_COL_KEY = "pers-egr-hidden";
    public static final String USERNAME_COL_KEY = "pers-egr-username";


    private ConfigurableFilterDataProvider gridDataProvider;

    SelectorFilterField<Boolean> hiddenFilterField;
    FilterTextField usernameFilterField;

    private Boolean initialFilterHiddenValue;
//    private boolean hiddenFieldVisible;
//    private boolean selectFieldVisible;
    private Consumer<Integer> selectionChanger;
    private Function<Person, Boolean> checkBoxEnabler;

    private int selCount;

    HeaderRow filterRow;
    BiConsumer<Person, Operation> editDialogOpener;

    public PersonGrid(
            Function<Person, Boolean> checkBoxEnabler
            , Consumer<Integer> selectionChanger
            , Boolean initialFilterHiddenValue
            , BiConsumer<Person, Operation> editDialogOpener
    ) {
        this.initialFilterHiddenValue = initialFilterHiddenValue;
        this.checkBoxEnabler = checkBoxEnabler;
//        this.hiddenFieldVisible = hiddenFieldVisible;
//        this.selectFieldVisible = selectFieldVisible;
        this.selectionChanger = selectionChanger;
        this.editDialogOpener = editDialogOpener;

        this.getStyle().set("marginTop", "0.5em");
        this.setMultiSort(true);
        this.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.setColumnReorderingAllowed(true);
        this.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module

        addGridColumns();

        filterRow = this.appendHeaderRow();

        hiddenFilterField = initHiddenFilterField();
        hiddenFilterField.setTextRenderer(this::hiddenFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(HIDDEN_COL_KEY))
                .setComponent(hiddenFilterField)
        ;

        usernameFilterField = initUsernameFilterField();
//        usernameFilterField.setTextRenderer(this::hiddenFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(USERNAME_COL_KEY))
                .setComponent(usernameFilterField)
        ;

        for (Grid.Column col : getColumns()) {
            setResizable(col);
        }
    }

    private String hiddenFilterLabelGenerator(Boolean arch) {
        if  (null == arch) {
            return "Vše";
        } else {
            return arch ? "Ano" : "Ne";
        }
    }


    private void addGridColumns() {
        this.addColumn(hiddenRenderer)
                .setHeader(("Skrytý"))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("6em")
                .setResizable(true)
                .setFlexGrow(0)
                .setKey(HIDDEN_COL_KEY)
        //        .setVisible(this.hiddenFieldVisible);
        //                .setFrozen(true)
        ;
        addColumn(Person::getId)
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("ID")
                .setSortProperty("id")
                .setWidth("6em")
                .setResizable(true)
                .setFrozen(true)
                .setFlexGrow(0)
        ;
//        personGrid.addColumn(Person::getState)
//                .setHeader("State")
//                .setSortProperty("state")
//                .setWidth("5em")
//                .setResizable(true)
//                .setFrozen(true)
//                .setFlexGrow(0)
//        ;
        addColumn(new ComponentRenderer<>(this::buildEditBtn))
                .setFlexGrow(0)
        ;
        addColumn(Person::getUsername)
                .setHeader("Username")
                .setSortProperty("username")
                .setWidth("10em")
                .setResizable(true)
                .setFrozen(true)
                .setFlexGrow(0)
                .setKey(USERNAME_COL_KEY)
        ;
        addColumn(Person::getJmeno)
                .setHeader("Jméno")
                .setSortProperty("jmeno")
                .setWidth("10em")
                .setResizable(true)
                .setFlexGrow(0)
        ;
        addColumn(Person::getPrijmeni)
                .setHeader("Příjmení")
                .setSortProperty("prijmeni")
                .setWidth("10em")
                .setResizable(true)
                .setFlexGrow(0)
        ;
        if (isWagesAccessGranted()) {
            addColumn(new NumberRenderer<>(Person::getWageCurrent, VzmFormatUtils.MONEY_FORMAT))
                    .setTextAlign(ColumnTextAlign.END)
                    .setHeader("Sazba aktuální")
                    .setWidth("8em")
                    .setResizable(true)
                    .setFlexGrow(0)
            ;
        }
        addColumn(Person::getNastup)
                .setHeader("Nástup")
                .setWidth("8em")
                .setResizable(true)
                .setFlexGrow(0)
        ;
        addColumn(Person::getVystup)
                .setHeader("Ukončení")
                .setWidth("8em")
                .setResizable(true)
                .setFlexGrow(0)
        ;
    }

    private ComponentRenderer<Component, Person> hiddenRenderer = new ComponentRenderer<>(pers -> {
        HiddenIconBox hiddenBox = new HiddenIconBox();
        hiddenBox.showIcon(pers.getHidden() ? HiddenIconBox.HiddenState.HIDDEN : HiddenIconBox.HiddenState.VISIBLE);
        return hiddenBox;
    });

    private Button buildEditBtn(Person person) {
        Button editBtn = new GridItemEditBtn(event -> editDialogOpener.accept(person, Operation.EDIT));
//        Button editBtn = new GridItemEditBtn(event -> personFormDialog.openDialog(person, Operation.EDIT));
        return editBtn;
    }

//    private BiConsumer<Person, Operation> editDilagOpener = (p, o) -> personFormDialog.openDialog(p, o);

    private void setResizable(Grid.Column column) {
        column.setResizable(true);
        Element parent = column.getElement().getParent();
        while (parent != null
                && "vaadin-grid-column-group".equals(parent.getTag())) {
            parent.setProperty("resizable", "true");
            parent = parent.getParent();
        }
    }

//    private TextField buildTextFilterField() {
//        TextField textFilterField = new TextField();
//        textFilterField.setClearButtonVisible(true);
//        textFilterField.setSizeFull();
//        textFilterField.setPlaceholder("Filtr");
//        textFilterField.setValueChangeMode(ValueChangeMode.EAGER);
//        textFilterField.addValueChangeListener(event -> doFilter());
//        return textFilterField;
//    }

    private FilterTextField initUsernameFilterField() {
        usernameFilterField = new FilterTextField();
        usernameFilterField.addValueChangeListener(event -> doFilter(hiddenFilterField.getValue(), event.getValue()));
        return usernameFilterField;
    }

    private SelectorFilterField<Boolean> initHiddenFilterField() {
        hiddenFilterField = new SelectorFilterField<>();
        hiddenFilterField.addValueChangeListener(event -> doFilter(event.getValue(), usernameFilterField.getValue()));
        return hiddenFilterField;
    }

//    public void populateGridDataAndRebuildFilterFields(List<Person> personList) {
//        this.setItems(personList);
//        this.setHiddenFilterItems(personList.stream()
//                .map(Person::getHidden)
//                .filter(a -> null != a)
//                .distinct().collect(Collectors.toCollection(LinkedList::new))
//        );
//    }

    public void setHiddenFilterItems(final List<Boolean> hiddenItems) {
        hiddenFilterField = initHiddenFilterField();
        filterRow.getCell(this.getColumnByKey(HIDDEN_COL_KEY))
                .setComponent(hiddenFilterField);
        hiddenFilterField.setItems(hiddenItems);
    }

    public void setInitialFilterValues() {
//        ((ListDataProvider<Person>) this.getDataProvider()).clearFilters();
        if (null == initialFilterHiddenValue) {
            hiddenFilterField.clear();
        } else {
            hiddenFilterField.setValue(initialFilterHiddenValue);
        }

        usernameFilterField.clear();

//        rokFilterField.clear();
//        skupinaFilterField.clear();
//        ckzFilterField.clear();
//        kzTextFilterField.clear();
//        objednatelFilterField.clear();
    }

    public void doFilter() {
        doFilter(hiddenFilterField.getValue(), usernameFilterField.getValue());

    }

    public void doFilter(Boolean hidden, String username) {
////        ListDataProvider<Person> listDataProvider = ((ListDataProvider<Person>) this.getDataProvider());
//        CallbackDataProvider<Person, String> dataProvider = ((CallbackDataProvider<Person, String>) this.getDataProvider());
////        ConfigurableFilterDataProvider<Person, String> dataProvider = ((CallbackDataProvider<Person, String>) this.getDataProvider());
////        listDataProvider.clearFilters();
//
//        Boolean hiddenFilterValue = hiddenFilterField.getValue();
//        if (null != hiddenFilterValue) {
////            listDataProvider.addFilter(Person::getHidden
//            dataProvider.fetchFromBackEnd() setFilter(Person::getHidden
//            dataProvider.fetchFromBackEnd() setFilter(Person::getHidden
//                    , arch -> arch.equals(hiddenFilterValue)
//            );
//        }
//        personDataProvider.setFilter(event.getValue());
//        personDataProvider.refreshAll();

//        ConfigurableFilterDataProvider<Person, Void, PersonService.PersonFilter> dataProvider
//                = (ConfigurableFilterDataProvider<Person, Void, PersonService.PersonFilter>) this.getDataProvider().withConfigurableFilter();

//        dataProviderDataProvider<Person, Void, PersonService.PersonFilter> dataProvider

        if (null == hidden && null == username) {
            gridDataProvider.setFilter(null);
        } else {
            //            String usernameFilterValue = null == event.getValue() ? "" : event.getValue().getUsername();
            //            Boolean hiddenValue = null == hidden ? "" : event.getValue().getUsername();
            PersonService.PersonFilter personFilter = new PersonService.PersonFilter(
//                            null == hidden ? null : hidden
                    hidden, username
            );
            gridDataProvider.setFilter(personFilter);
        }
        gridDataProvider.refreshAll();
        getDataProvider().refreshAll();
    }


//    @Override
    public void setGridDataProvider(ConfigurableFilterDataProvider gridDataProvider) {
        this.gridDataProvider = gridDataProvider;
        setDataProvider(this.gridDataProvider);
    }
}
