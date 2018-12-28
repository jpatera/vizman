package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.*;
import org.springframework.util.Assert;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractEditorDialog<Kont> {

    private TextField objednatelField = new TextField("Objednatel"); // = new TextField("Username");
    private TextField investorField = new TextField("Investor"); // = new TextField("Username");
    private TextField textField = new TextField("Text"); // = new TextField("Jméno");
    private TextField honorarField = new TextField("Honorář (suma ze zakázek/subdodávek)");
    private TextField menaField = new TextField("Měna");
//    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

//    private Grid<Zak> zakazkyGrid;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

    Grid<Zak> zakGrid = new Grid();
    Grid<KontDoc> docGrid = new Grid();

//    @Autowired
    private KontService kontService;
    private ZakService zakService;


////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;
    private final ConfirmationDialog<KontDoc> confirmDialog = new ConfirmationDialog<>();


    public KontFormDialog(BiConsumer<Kont, Operation> itemSaver,
                          Consumer<Kont> itemDeleter,
                          KontService kontService)
    {
        super(GenderGrammar.MASCULINE, Kont.NOMINATIVE_SINGULAR, Kont.GENITIVE_SINGULAR, Kont.ACCUSATIVE_SINGULAR, itemSaver, itemDeleter);

        setWidth("1200px");
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
//        initDatZadField();
        initZakGrid();
        initDocGrid();

        getFormLayout().add(objednatelField);
        getFormLayout().add(investorField);
        getFormLayout().add(textField);
        getFormLayout().add(honorarField);
        getFormLayout().add(menaField);
//        getFormLayout().add(datZadComp);
        getUpperGridLayout().add(docGrid);
        getLowerGridLayout().add(zakGrid);
//        getFormLayout().add(buildZakGridContainer(zakGrid));
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());
        zakGrid.setItems(getCurrentItem().getNodes());
        docGrid.setItems(getCurrentItem().getKontDocs());

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

//    private void initDatZadField() {
//    }


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
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        zakGrid.setId("zak-grid");
        zakGrid.setClassName("vizman-simple-grid");
        zakGrid.setHeight("12em");


//        zakGrid.getElement().setAttribute("colspan", "2");
//        zakGrid.getStyle().set("padding-right", "0em");
//        zakGrid.getStyle().set("padding-left", "0em");
//        zakGrid.getStyle().set("padding-top", "2.5em");
//        zakGrid.getStyle().set("padding-bottom", "2.5em");

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
        zakGrid.addColumn(Zak::getCzak).setHeader("ČZ");
        zakGrid.addColumn(Zak::getHonorar).setHeader("Honorář");
//        zakGrid.addColumn("Honorář CZK");
        zakGrid.addColumn(Zak::getText).setHeader("Text");
    }

    private void initDocGrid() {
        Assert.notNull(docGrid, "DocGrid must not be null");
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("doc-grid");
        docGrid.setClassName("vizman-simple-grid");
        docGrid.setHeight("12em");

        docGrid.addColumn(KontDoc::getFilename).setHeader("Soubor");
        docGrid.addColumn(KontDoc::getNote).setHeader("Poznámka");
//        docGrid.addColumn("Honorář CZK");
        docGrid.addColumn(KontDoc::getDateRegist).setHeader("Vloženo");
        docGrid.addColumn(new ComponentRenderer<>(this::buildDocRemoveButton))
                .setFlexGrow(0);
    }

    private Component buildDocRemoveButton(KontDoc kontDoc) {
            return new RemoveItemGridButton(event -> {
                close();
                confirmDialog.open("Registrace dokumentu",
                        "Zrušit registraci dokumentu?", "", "Zrušit",
                        true, kontDoc, this::removeDocRegistration, this::open);
            });
    }

    private void removeDocRegistration(KontDoc kontDoc) {

        close();

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
