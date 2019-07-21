package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.SerializablePredicate;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.TwinColGrid;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static eu.japtor.vizman.app.security.SecurityUtils.isWagesAccessGranted;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PersonEditorDialog extends AbstractEditorDialog<Person> {

//    private ComboBox<PersonState> statusField; // = new ComboBox("Status");
    private TextField usernameField; // = new TextField("Username");
//    private PasswordField passwordField; // = new TextField("Password");
    private TextField passwordField; // = new TextField("Password");
    private TextField jmenoField; // = new TextField("Jméno");
    private TextField prijmeniField; // = new TextField("Příjmení");

    private TextField sazbaOldField;
    private TextField blindSazbaOldField;

    private TextField sazbaCurrentField;
    private TextField fakeSazbaCurrentField;
    private Button sazbaEditButton;

    private DatePicker nastupField; // = new DatePicker("Nástup");
    private DatePicker vystupField; // = new DatePicker("Výstup");
    private TwinColGrid<Role> twinRolesGridField;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

//    @Autowired
    private PersonService personService;

    private Set<Role> rolesPool;

//    @Autowired
    private PasswordEncoder passwordEncoder;

////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;

    public PersonEditorDialog(BiConsumer<Person, Operation> itemSaver,
                              Consumer<Person> itemDeleter,
                              PersonService personService,
                              List<Role> allRoles,
                              PasswordEncoder passwordEncoder)
    {
//        super(GrammarGender.MASCULINE, Person.NOMINATIVE_SINGULAR, Person.GENITIVE_SINGULAR, Person.ACCUSATIVE_SINGULAR, itemSaver, itemDeleter);
        super(itemSaver, itemDeleter);

        setWidth("900px");
//        setHeight("600px");

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.personService = personService;
        this.rolesPool = new HashSet<>(allRoles);
        this.passwordEncoder = passwordEncoder;

//        getBinder().forField(jmenoField)
//                .bind(Person::getJmeno, Person::setJmeno);
//        this.passwordEncoder = new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence rawPassword) {
//                return null;
//            }
//
//            @Override
//            public boolean matches(CharSequence rawPassword, String encodedPassword) {
//                return false;
//            }
//        };

        usernameField = new TextField("Přihlašovací jméno");
//        passwordField = new PasswordField("Heslo");
        passwordField = new TextField("Heslo");
        jmenoField = new TextField("Jméno");
        prijmeniField = new TextField("Příjmení");

        nastupField = new DatePicker("Nástup");
        vystupField = new DatePicker("Ukončení");

//        sazbaOldField = new TextField("Sazba");
//        sazbaOldField.setPattern("[0-9]*");
//        sazbaOldField.setPreventInvalidInput(true);
//        sazbaOldField.setSuffixComponent(new Span("CZK"));
//
//        sazbaCurrentField = new TextField("Sazba aktuální");
//        sazbaCurrentField.setSuffixComponent(new Span("CZK"));
//        sazbaCurrentField.setReadOnly(true);


        addUsernameField();
        addPasswordField();
        addJmenoField();
        addPrijmeniField();
        addNastupAndVystupField();

        getFormLayout().add(
                initPermittedCurrentSazbaField(),
                initSazbaEditButton(),
                initPermittedSazbaOldField()
        );

//        getFormLayout().add(initRolesField(rolesPool));
        getFormLayout().add(initRolesField(rolesPool));

//        roleGridContainer = buildRoleGridContainer(roleTwinGrid);
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
        nastupField.setLocale(new Locale("cs", "CZ"));
        vystupField.setLocale(new Locale("cs", "CZ"));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());

    }


//    private VerticalLayout buildRoleGridContainer(Grid<Role> grid) {
//        VerticalLayout roleGridContainer = new VerticalLayout();
//        roleGridContainer.setClassName("view-container");
//        roleGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
//        roleGridContainer.add(grid);
//        return roleGridContainer;
//    }

//    private void initRoleGrid() {
////        roleTwinGrid.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module
//        roleTwinGrid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true);
//        roleTwinGrid.addColumn(Role::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);
//    }

//    private void addStatusField() {
//        statusField.setDataProvider(DataProvider.ofItems(PersonState.values()));
//        getFormLayout().add(statusField);
//        getBinder().forField(statusField)
////                .withConverter(
////                        new StringToIntegerConverter("Must be a number"))
//                .bind(Person::getStatus, Person::setStatus);
//    }

    private void addUsernameField() {
        getFormLayout().add(usernameField);

        getBinder().forField(usernameField)
                .withConverter(String::trim, String::trim)
                .withValidator(new StringLengthValidator(
                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
                        3, null))
                .withValidator(
                        username -> (currentOperation != Operation.ADD) ?
                            true : personService.getByUsername(username) == null,
                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Person::getUsername, Person::setUsername);
    }

    private void addPasswordField() {
        getFormLayout().add(passwordField);
        getBinder().forField(passwordField)
                .bind(Person::getPassword, Person::setPassword);

        // TODO: start  using  password encoder
//        getBinder().forField(passwordField)
//                .withValidator(pass -> {return pass.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$"); }
//                    , "need 6 or more chars, mixing digits, lowercase and uppercase letters")
//                .bind(user -> passwordField.getEmptyValue(), (user, pass) -> {
//                    if (!passwordField.getEmptyValue().equals(pass)) {
//                        user.setPassword(passwordEncoder.encode(pass));
//                    }
//                });
    }

    private void addJmenoField() {
        getFormLayout().add(jmenoField);
        getBinder().forField(jmenoField)
                .bind(Person::getJmeno, Person::setJmeno);
    }

    private void addPrijmeniField() {
        getFormLayout().add(prijmeniField);
        getBinder().forField(prijmeniField)
                .bind(Person::getPrijmeni, Person::setPrijmeni);
    }

    private Component initPermittedCurrentSazbaField() {
        if (!isWagesAccessGranted()) {
            return initBlindSazbaCurrentField();
        } else {
            return initSazbaCurrentField();
        }
    }

    private Component initSazbaCurrentField() {
        sazbaCurrentField = new TextField("Sazba aktuální");
        sazbaCurrentField.setSuffixComponent(new Span("CZK"));
        sazbaCurrentField.setReadOnly(true);
        getBinder().forField(sazbaCurrentField)
                .withConverter(
                        new StringToBigDecimalConverter("Špatný formát čísla"))
                .bind(Person::getWageCurrent, null);
        return sazbaCurrentField;
    }

    private Component initBlindSazbaCurrentField() {
        fakeSazbaCurrentField = new TextField("");
        fakeSazbaCurrentField.setVisible(false);
        return fakeSazbaCurrentField;
    }

    private Component initSazbaEditButton() {
        sazbaEditButton = new Button("Sazby");
        return sazbaEditButton;
    }

    private Component initPermittedSazbaOldField() {
        if (!isWagesAccessGranted()) {
            return initBlindSazbaOldField();
        } else {
            return initSazbaOldField();
        }
    }

    private Component initSazbaOldField() {
        sazbaOldField = new TextField("Sazba (old ?)");
        sazbaOldField.setPattern("[0-9]*");
        sazbaOldField.setPreventInvalidInput(true);
        sazbaOldField.setSuffixComponent(new Span("CZK"));
        getBinder().forField(sazbaOldField)
                .withConverter(
                        new StringToBigDecimalConverter("Špatný formát čísla"))
                .bind(Person::getSazba, null);
        return sazbaOldField;
    }

    private Component initBlindSazbaOldField() {
        blindSazbaOldField = new TextField("");
        blindSazbaOldField.setVisible(false);
        return blindSazbaOldField;
    }

    private Component initRolesField(final Set<Role> allRoles) {
//        this.allRolesDataProvider = DataProvider.ofCollection(roleRepo.findAll());
//        this.personRoles = DataProvider.ofCollection(getCurrentItem().getRoles());

//        twinGrid = new Grid<>();
//        roleTwinGrid.setLeftDataProvider(personRoles);
//        initRoleGrid();
//        roleTwinGrid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true);
//        roleTwinGrid.addColumn(Role::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);
//        this.add(roleGridContainer);

//        twinRolesGridField = new TwinColGrid<>(allRolesDataProvider)
//        twinRolesGridField = new TwinColGrid<>(roleRepo.findAll())
        twinRolesGridField = new TwinColGrid<>(allRoles)
                .addColumn(Role::getName, "Role")
//            .addColumn(Role::getDescription, "Popis")
//            .withLeftColumnCaption("Available books")
//            .withRightColumnCaption("Added books")
//            .showAddAllButton()
                .withSizeFull()
                .withHeight("200px")
//            .withRows(4)
//            .withRows(availableBooks.size() - 3)
//            .withDragAndDropSupport()
        ;
//        twinRolesGridField.setValue(getCurrentItem().getRoles());
//        FormLayout.FormItem formItem = getFormLayout().addFormItem(twinRolesGridField, "Label");
        twinRolesGridField.setId("twin-col-grid");
        twinRolesGridField.getElement().setAttribute("colspan", "2");
        twinRolesGridField.getContent().setAlignItems(FlexComponent.Alignment.STRETCH);
        twinRolesGridField.getContent().getStyle().set("padding-right", "0em");
        twinRolesGridField.getContent().getStyle().set("padding-left", "0em");
        twinRolesGridField.getContent().getStyle().set("padding-top", "2.5em");
        twinRolesGridField.getContent().getStyle().set("padding-bottom", "2.5em");
//        getBinder().forField(twinRolesGridField).bind(Person::getRoles, Person::setRoles);
        getBinder().bind(twinRolesGridField, Person::getRoles, Person::setRoles);
        return twinRolesGridField;
    }

    private void addNastupAndVystupField() {

        // Nastup field binder:
        Binder.Binding<Person, LocalDate> nastupBinder = getBinder().forField(nastupField)
                .withValidator(nastupNullCheck(),"Nástup nemuže být prázdný pokud je zadáno ukončení")
                .withValidator(nastupBeforeVystupCheck(),"Nástup nemuže následovat po ukončení")
                .bind(Person::getNastup, Person::setNastup);

        // Vystup field binder:
        Binder.Binding<Person, LocalDate> vystupBinder = getBinder().forField(vystupField)
                .withValidator(vystupNotNullCheck(), "Ukončení nemůže být zadáno pokud není zadán nástup")
                .withValidator(vystupAfterNastupCheck(),"Ukončení nemůže předcházet nástup")
                .bind(Person::getVystup, Person::setVystup);

        nastupField.addValueChangeListener(event -> vystupBinder.validate());
        vystupField.addValueChangeListener(event -> nastupBinder.validate());

        // Add fields to the form:
        getFormLayout().add(nastupField);
        getFormLayout().add(vystupField);
    }

    private SerializablePredicate<LocalDate> nastupNullCheck() {
        return nastup -> ((null == vystupField.getValue()) || (null != nastup));
    }

    private SerializablePredicate<LocalDate> nastupBeforeVystupCheck() {
        return nastup ->
                (null == vystupField.getValue())
                || ((null != nastup) && nastup.isBefore(vystupField.getValue()));
    }

    private SerializablePredicate<LocalDate> vystupNotNullCheck() {
        return vystup -> ((null != nastupField.getValue()) || (null == vystup));
    }

    private SerializablePredicate<LocalDate> vystupAfterNastupCheck() {
        return vystup ->
            (null == vystup)
            || ((null != nastupField.getValue()) && vystup.isAfter(nastupField.getValue()));
    }


    @Override
    protected void confirmDelete() {
        long personCount = personService.countAll();
//        if (personCount > 0) {
            openConfirmDeleteDialog("Zrušit uživatele",
                    "Opravdu zrušit uživatele “" + getCurrentItem().getUsername() + "“ ?",
                    "Pokud bude uživatel zrušen, budou zrušena i další s ním související data.");
//        } else {
//            deleteKont(getCurrentItem());
//        }
    }
}
