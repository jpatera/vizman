package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.backend.entity.GenderGrammar;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Mena;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.util.Assert;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractEditorDialog<Kont> {

    private TextField objednatelField = new TextField("Objednatel"); // = new TextField("Username");
    private TextField investorField = new TextField("Investor"); // = new TextField("Username");
    private TextField textField = new TextField("Text"); // = new TextField("Jméno");
    private TextField menaField = new TextField("Měna");
    private TextField honorarField = new TextField("Honorář");
    private TextField datZadField = new TextField("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

//    private Grid<Zak> zakazkyGrid;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

    Grid<Zak> zakGrid = new Grid();

//    @Autowired
    private KontService kontService;
    private ZakService zakService;


////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;

    public KontFormDialog(BiConsumer<Kont, Operation> itemSaver,
                          Consumer<Kont> itemDeleter,
                          KontService kontService)
    {
        super(GenderGrammar.MASCULINE, Kont.NOMINATIVE_SINGULAR, Kont.GENITIVE_SINGULAR, Kont.ACCUSATIVE_SINGULAR, itemSaver, itemDeleter);

        setWidth("900px");
//        setHeight("600px");

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.kontService = kontService;
//        this.zakService = zakService;

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

        initObjednatelField();
        initInvestorField();
        initTextField();
        initMenaField();
        initHonorarField();
        initDatZadField();
        initZakGrid();

        getFormLayout().add(objednatelField);
        getFormLayout().add(investorField);
        getFormLayout().add(textField);
        getFormLayout().add(menaField);
        getFormLayout().add(honorarField);
        getFormLayout().add(datZadField);
        getFormLayout().add(buildZakGridContainer(zakGrid));
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadField.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());

    }


    private VerticalLayout buildZakGridContainer(Grid<Zak> grid) {
        VerticalLayout zakGridContainer = new VerticalLayout();
        zakGridContainer.setClassName("view-container");
        zakGridContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
        zakGridContainer.add(grid);
        return zakGridContainer;
    }

//    private void initRoleGrid() {
////        roleTwinGrid.setId("person-grid");  // .. same ID as is used in shared-styles grid's dom module
//        roleTwinGrid.addColumn(Role::getName).setHeader("Název").setWidth("3em").setResizable(true);
//        roleTwinGrid.addColumn(Role::getDescription).setHeader("Popis").setWidth("8em").setResizable(true);
//    }

//    private void addStatusField() {
//        statusField.setDataProvider(DataProvider.ofItems(PersonStatus.values()));
//        getFormLayout().add(statusField);
//        getBinder().forField(statusField)
////                .withConverter(
////                        new StringToIntegerConverter("Must be a number"))
//                .bind(Person::getStatus, Person::setStatus);
//    }

    private void initObjednatelField() {
        getBinder().forField(objednatelField)
                .withConverter(String::trim, String::trim)
//                .withValidator(new StringLengthValidator(
//                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
//                        3, null))
//                .withValidator(
//                        objednatel -> (currentOperation != Operation.ADD) ?
//                            true : kontService.getByObjednatel(objednatel) == null,
//                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Kont::getObjednatel, Kont::setObjednatel);
    }

    private void initInvestorField() {
        getBinder().forField(investorField)
//                .withConverter(String::trim, String::trim)
                .bind(Kont::getInvestor, Kont::setInvestor);
    }


    private void initTextField() {
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Kont::getText, Kont::setText);
    }

    private void initDatZadField() {
    }


    private void initMenaField() {
        getBinder().forField(menaField)
            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .bind(Kont::getMena, Kont::setMena);

//        final BeanItemContainer<Status> container = new BeanItemContainer<>(Status.class);
//        container.addAll(EnumSet.allOf(Status.class));
//        cStatus.setContainerDataSource(container);
//        cStatus.setItemCaptionPropertyId("caption");
//        basicContent.addComponent(cStatus);
    }

    // TODO: shouldn't have been calculated from Zaks <
    private void initHonorarField() {
        honorarField.setReadOnly(true);
        honorarField.setPreventInvalidInput(true);
        honorarField.setSuffixComponent(new Span("CZK"));
        honorarField.setValue("123 456,70");
    }

    private void initZakGrid() {
        Assert.notNull(zakGrid, "ZakGrid must not be null");
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
        zakGrid.addColumn(Zak::getCzak, "ČZ");
        zakGrid.addColumn(Zak::getHonorar, "Honorář");
//        zakGrid.addColumn("Honorář CZK");
        zakGrid.addColumn(Zak::getText, "Text");
    }

//    private void addTerminField() {
//
//        // Nastup field binder:
//        Binder.Binding<Person, LocalDate> nastupBinder = getBinder().forField(nastupField)
//                .withValidator(nastupNullCheck(),"Nástup nemuže být prázdný pokud je zadáno ukončení")
//                .withValidator(nastupBeforeVystupCheck(),"Nástup nemuže následovat po ukončení")
//                .bind(Person::getNastup, Person::setNastup);
//
//        // Vystup field binder:
//        Binder.Binding<Person, LocalDate> vystupBinder = getBinder().forField(vystupField)
//                .withValidator(vystupNotNullCheck(), "Ukončení nemůže být zadáno pokud není zadán nástup")
//                .withValidator(vystupAfterNastupCheck(),"Ukončení nemůže předcházet nástup")
//                .bind(Person::getVystup, Person::setVystup);
//
//        nastupField.addValueChangeListener(event -> vystupBinder.validate());
//        vystupField.addValueChangeListener(event -> nastupBinder.validate());
//
//        // Add fields to the form:
//        getFormLayout().add(nastupField);
//        getFormLayout().add(vystupField);
//    }

//    private SerializablePredicate<LocalDate> nastupNullCheck() {
//        return nastup -> ((null == vystupField.getValue()) || (null != nastup));
//    }
//
//    private SerializablePredicate<LocalDate> nastupBeforeVystupCheck() {
//        return nastup ->
//                (null == vystupField.getValue())
//                || ((null != nastup) && nastup.isBefore(vystupField.getValue()));
//    }
//
//    private SerializablePredicate<LocalDate> vystupNotNullCheck() {
//        return vystup -> ((null != nastupField.getValue()) || (null == vystup));
//    }
//
//    private SerializablePredicate<LocalDate> vystupAfterNastupCheck() {
//        return vystup ->
//            (null == vystup)
//            || ((null != nastupField.getValue()) && vystup.isAfter(nastupField.getValue()));
//    }


    @Override
    protected void confirmDelete() {

        long nodesCount = getCurrentItem().getNodes().size();
        if (nodesCount > 0) {
            new OkDialog().open(
                    "Zrušení kontraktu"
                    , "Kontrakt " + getCurrentItem().getCkont() + " nelze zrušit, obsahuje zakázky/poddodávky"
                    , ""
            );
        } else {
            openConfirmDeleteDialog("Zrušit kontrakt",
                    "Opravdu zrušit kontrakt “" + getCurrentItem().getCkont() + "“ ?",
                    "Pokud bude kontrakt zrušen, budou zrušena i další s ním související data.");
//        } else {
//            doDelete(getCurrentItem());
        }
    }
}
