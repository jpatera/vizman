package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.DochsumZakService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.backend.service.WageService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.OperationResult;
import eu.japtor.vizman.ui.components.TwinColGrid;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static eu.japtor.vizman.app.security.SecurityUtils.isWagesAccessGranted;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// public class PersonFormDialog extends AbstractComplexFormDialog<Person> {
public class PersonFormDialog extends AbstractSimpleFormDialog<Person> {

    private static final String DIALOG_WIDTH = "900px";
    private static final String DIALOG_HEIGHT = "600x";

    private final static String DELETE_STR = "Zrušit";
    private final static String REVERT_STR = "Vrátit změny";
    private final static String REVERT_AND_CLOSE_STR = "Zpět";
    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";

    private Button revertButton;
    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
    private Button deleteAndCloseButton;

    private HorizontalLayout leftBarPart;

//    private ComboBox<PersonState> statusField; // = new ComboBox("Status");
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField jmenoField;
    private TextField prijmeniField;

    private Checkbox hiddenField;
    private TextField sazbaCurrentField;
    private TextField ymFromCurrentField;
    private TextField blindSazbaCurrentField;
    private TextField blindYmFromCurrentField;
    private Button wageGridOpenButton;
    private DatePicker nastupField;
    private DatePicker vystupField;
    private TwinColGrid<Role> twinRolesGridField;

    private PersonWageGridDialog personWageGridDialog;
    private PersonService personService;
    private WageService wageService;
    private DochsumZakService dochsumZakService;

    private Set<Role> rolesPool;

//    @Autowired
    private PasswordEncoder passwordEncoder;
    private Binder<Person> binder = new Binder<>();
    private Registration binderChangeListener = null;

    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;


//    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;


//    public PersonFormDialog(BiConsumer<Person, Operation> itemSaver,
//                            Consumer<Person> itemDeleter,
//                            PersonService personService,
//                            WageService wageService,
//                            List<Role> allRoles,
//                            PasswordEncoder passwordEncoder)
    public PersonFormDialog(PersonService personService,
                            WageService wageService,
                            DochsumZakService dochsumZakService,
                            List<Role> allRoles,
                            PasswordEncoder passwordEncoder)
    {
//        super(GrammarGender.MASCULINE, Person.NOMINATIVE_SINGULAR, Person.GENITIVE_SINGULAR, Person.ACCUSATIVE_SINGULAR, itemSaver, itemDeleter);
//        super(itemSaver, itemDeleter);
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.PERSON);

        this.personService = personService;
        this.wageService = wageService;
        this.dochsumZakService = dochsumZakService;
        this.rolesPool = new HashSet<>(allRoles);
        this.passwordEncoder = passwordEncoder;

        personWageGridDialog  = new PersonWageGridDialog(wageService, personService, dochsumZakService);
        personWageGridDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishWageGridEdit();
            }
        });

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

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        getFormLayout().add (
                initHiddenField()
                , new Span("")
                , initUsernameField()
                , initPasswordField()
                , initJmenoField()
                , initPrijmeniField()
                , initNastupField()
                , initVystupField()
                , initPermittedCurrentSazbaField()
                ,  initWagesGridOpenButton()
//                , initPermittedYmFromCurrentField()
                , initRolesField(rolesPool)
        );

        // Nastup field binder:
        Binder.Binding<Person, LocalDate> nastupBinder = binder.forField(nastupField)
                .withValidator(nastupNullCheck(),"Nástup nemuže být prázdný pokud je zadáno ukončení")
                .withValidator(nastupBeforeVystupCheck(),"Nástup nemuže následovat po ukončení")
                .bind(Person::getNastup, Person::setNastup)
                ;

        // Vystup field binder:
        Binder.Binding<Person, LocalDate> vystupBinder = binder.forField(vystupField)
                .withValidator(vystupNotNullCheck(), "Ukončení nemůže být zadáno pokud není zadán nástup")
                .withValidator(vystupAfterNastupCheck(),"Ukončení nemůže předcházet nástup")
                .bind(Person::getVystup, Person::setVystup)
                ;

        nastupField.addValueChangeListener(event -> vystupBinder.validate());
        vystupField.addValueChangeListener(event -> nastupBinder.validate());
    }

    public void openDialog(Person person, Operation operation) {

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
        nastupField.setLocale(new Locale("cs", "CZ"));
        vystupField.setLocale(new Locale("cs", "CZ"));

        this.currentOperation = operation;
        setCurrentItem(person);
        setOrigItem(person);

//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());

        initDataAndControls(getCurrentItem(), currentOperation);
        this.open();
    }

    private void closeDialog() {
        this.close();
    }


    protected final Binder<Person> getBinder() {
        return binder;
    }

    public OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    @Override
    public Operation getCurrentOperation() {
        return currentOperation;
    }

    private void finishWageGridEdit() {
        binder.removeBean();
        binder.readBean(personService.fetchOne(getCurrentItem().getId()));
    }

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

//        saveButton = new Button("Uložit");
//        saveButton.setAutofocus(true);
//        saveButton.getElement().setAttribute("theme", "primary");
//        saveButton.addClickListener(e -> saveClicked(false));

        saveAndCloseButton = new Button(SAVE_AND_CLOSE_STR);
        saveAndCloseButton.setAutofocus(true);
        saveAndCloseButton.getElement().setAttribute("theme", "primary");
        saveAndCloseButton.addClickListener(e -> saveClicked(true));

        deleteAndCloseButton = new Button(DELETE_STR);
        deleteAndCloseButton.getElement().setAttribute("theme", "error");
        deleteAndCloseButton.addClickListener(e -> deleteClicked());

        revertButton = new Button(REVERT_STR);
        revertButton.addClickListener(e -> revertClicked(false));

        revertAndCloseButton = new Button(REVERT_AND_CLOSE_STR);
        revertAndCloseButton.addClickListener(e -> revertClicked(true));

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(
//                saveButton
                revertButton
                , deleteAndCloseButton
        );

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                saveAndCloseButton
                , revertAndCloseButton
        );

//        buttonBar.getStyle().set("margin-top", "0.2em");
        bar.setClassName("buttons");
        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(
                leftBarPart
                , rightBarPart
        );
        return bar;
    }


    private void initDataAndControls(final Person item, final Operation operation) {

        deactivateListeners();

        binder.removeBean();
        binder.readBean(item);

        refreshHeaderMiddleBox(item);
        getHeaderEndBox().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(item, operation);
        initControlsOperability();

        activateListeners();
    }

    private void initControlsForItemAndOperation(final Person item, final Operation operation) {
//        setItemNames(item.getTyp());
        getMainTitle().setText(operation.getDialogTitle(getItemName(operation), itemGender));

//        if (getCurrentItem() instanceof HasItemType) {
        getHeaderDevider().getStyle().set(
                "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
//        }
        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
    }

    private void initControlsOperability() {
        saveAndCloseButton.setEnabled(false);
//        saveButton.setEnabled(false);
        revertButton.setEnabled(false);
        deleteAndCloseButton.setEnabled(currentOperation.isDeleteEnabled() && canDeleteItem(getCurrentItem()));
    }


    private void refreshHeaderMiddleBox(Person item) {
        // Do nothing
    }

    private void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
    }

    private void activateListeners() {
        binderChangeListener = binder.addValueChangeListener(e ->
                        adjustControlsOperability(true)
        );
    }

    private void adjustControlsOperability(final boolean hasChanges) {
        saveAndCloseButton.setEnabled(hasChanges);
        revertButton.setEnabled(hasChanges);
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

    private Checkbox initHiddenField() {
        hiddenField = new Checkbox("Skrytý");
        binder.forField(hiddenField)
//                .withConverter(Boolean::trim, String::trim)
//                .withValidator(new StringLengthValidator(
//                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
//                        3, null))
//                .withValidator(
//                        username -> (currentOperation != Operation.ADD) ?
//                                true : personService.getByUsername(username) == null,
//                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Person::getHidden, Person::setHidden);
        return hiddenField;
    }

    private Component initUsernameField() {
        usernameField = new TextField("Přihlašovací jméno");
        binder.forField(usernameField)
                .withConverter(String::trim, String::trim)
                .withValidator(new StringLengthValidator(
                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
                        3, null))
                .withValidator(
                        username -> (currentOperation != Operation.ADD) ?
                            true : personService.getByUsername(username) == null,
                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Person::getUsername, Person::setUsername);
        return usernameField;
    }

    private Component initPasswordField() {
        passwordField = new PasswordField("Heslo");
        binder.forField(passwordField)
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
        return passwordField;
    }

    private Component initJmenoField() {
        jmenoField = new TextField("Jméno");
        binder.forField(jmenoField)
                .bind(Person::getJmeno, Person::setJmeno);
        return jmenoField;
    }

    private Component initPrijmeniField() {
        prijmeniField = new TextField("Příjmení");
        binder.forField(prijmeniField)
                .bind(Person::getPrijmeni, Person::setPrijmeni);
        return prijmeniField;
    }

    private Component initPermittedCurrentSazbaField() {
        if (!isWagesAccessGranted()) {
            return initBlindSazbaCurrentField();
        } else {
            return initSazbaCurrentField();
        }
    }

//    private Component initPermittedYmFromCurrentField() {
//        if (!isWagesAccessGranted()) {
//            return initBlindSazbaCurrentField();
//        } else {
//            return initYmFromCurrentField();
//        }
//    }

    private Component initSazbaCurrentField() {
        sazbaCurrentField = new TextField("Sazba aktuální");
        sazbaCurrentField.setSuffixComponent(new Span("CZK"));
        sazbaCurrentField.setReadOnly(true);
        binder.forField(sazbaCurrentField)
                .withConverter(
                        new StringToBigDecimalConverter("Špatný formát čísla"))
                .bind(Person::getWageCurrent, null);
        return sazbaCurrentField;
    }

    private Component initYmFromCurrentField() {
        ymFromCurrentField = new TextField("Platnost od");
        ymFromCurrentField.setReadOnly(true);
//        ymFromCurrentField.setWidth("10em");
//        ymFromCurrentField.setPlaceholder("RRRR-MM");
//        ymFromCurrentField.setPattern("\\d{4}-\\d{2}");

        binder.forField(ymFromCurrentField)
                .withNullRepresentation("")
                .withConverter(
                        new VzmFormatUtils.ValidatedIntegerYearMonthConverter())
                .bind(Person::getYmFromCurrent, null)
        ;
        return ymFromCurrentField;
    }

    private Component initBlindSazbaCurrentField() {
        blindSazbaCurrentField = new TextField("");
        blindSazbaCurrentField.setVisible(false);
        return blindSazbaCurrentField;
    }

    private Component initBlindYmFromCurrentField() {
        blindYmFromCurrentField = new TextField("");
        blindYmFromCurrentField.setVisible(false);
        return blindYmFromCurrentField;
    }

    private Component initWagesGridOpenButton() {
        wageGridOpenButton = new Button("Mzdová tabulka"
            , event -> {
            binder.writeBeanIfValid(getCurrentItem());
            setCurrentItem(savePerson(getCurrentItem()));
            openWageGridDialog();
        });
        return wageGridOpenButton;
    }

    private Person savePerson(Person personToSave) {
        try {
            setCurrentItem(personService.savePerson(personToSave, currentOperation));
            lastOperationResult = OperationResult.ITEM_SAVED;
//            Notification.show(
//                    "Změny uživatele uloženy", 2000, Notification.Position.BOTTOM_END);
            return getCurrentItem();
        } catch(VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            throw(e);
        }
    }

    private void openWageGridDialog() {
//        PersonWageGridDialog personWageGridDialog  = new PersonWageGridDialog(
//                wageService
//        );
//        personWageGridDialog.openDialog(wageService.fetchByPersonId(getCurrentItem().getId()));
        personWageGridDialog.openDialog(getCurrentItem());
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
        binder.bind(twinRolesGridField, Person::getRoles, Person::setRoles);
        return twinRolesGridField;
    }

    private Component initNastupField() {
        nastupField = new DatePicker("Nástup");
        return nastupField;
    }

    private Component initVystupField() {
        vystupField = new DatePicker("Ukončení");
        return vystupField;
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


// -----------------------------------------------------

    private void saveClicked(boolean closeAfterSave) {
        if (!isItemValid()) {
            return;
        }
        try {
            binder.writeBeanIfValid(getCurrentItem());
            savePerson(getCurrentItem());
            binder.readBean(getCurrentItem());
            if (closeAfterSave) {
                closeDialog();
//            } else {
//                initFaktDataAndControls(currentItem, currentOperation);
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private boolean isItemValid() {
        return true;
    }

    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace uživatele")
                .withMessage("Uživatele se nepodařilo uložit.")
                .open();
    }

    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
            initControlsOperability();
        }
    }

    private void revertFormChanges() {
        binder.removeBean();
        binder.readBean(getCurrentItem());
        lastOperationResult = OperationResult.NO_CHANGE;
    }

    private void deleteClicked() {
        if (!canDeleteItem(getCurrentItem())) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení užvatele")
                    .withMessage(String.format("Uživatele nelze zrušit."))
                    .open()
            ;
            return;
        }
        try {
//            revertFormChanges();
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení uživatele")
                    .withMessage(String.format("Zrušit uživvatele ?"))
                    .withOkButton(() -> {
                                if (deleteItem(getCurrentItem())) {
                                    closeDialog();
                                }
                            }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                    )
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        } catch (VzmServiceException e) {
            showDeleteErrMessage();
        }
    }

    protected boolean deleteItem(Person itemToDelete) {
        OperationResult lastOperResOrig = lastOperationResult;
        try {
            personService.deletePerson(itemToDelete);
            lastOperationResult = OperationResult.ITEM_DELETED;
            return true;
        } catch (VzmServiceException e) {
            this.lastOperationResult = lastOperResOrig;
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení uživatele.")
                    .withMessage(String.format("Uživatele se nepodařilo zrušit."))
                    .open()
            ;
            return false;
        }
    }

    private boolean canDeleteItem(final Person itemToDelete) {
        return false;
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace uživatele")
                .withMessage("Uživatele se nepodařilo zrušit")
                .open()
        ;
    }
}
