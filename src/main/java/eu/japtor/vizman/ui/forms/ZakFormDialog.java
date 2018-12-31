package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.ConfirmationDialog;
import eu.japtor.vizman.ui.components.GridActionItemButton;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.util.Assert;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakFormDialog extends AbstractEditorDialog<Zak> {

    private Mena zakMena;
    private TextField objednatelField = new TextField("Objednatel"); // = new TextField("Username");
    private TextField investorField = new TextField("Investor"); // = new TextField("Username");
    private TextField textField = new TextField("Text"); // = new TextField("Jméno");
    private TextField honorarField = new TextField("Honorář");
    private TextField menaField = new TextField("Měna");
//    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

    Grid<Fakt> faktGrid = new Grid();
    Grid<ZakDoc> docGrid = new Grid();

//    @Autowired
    private ZakService zakService;
//    private FaktService faktService;


////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;
    private final ConfirmationDialog<ZakDoc> confirmDocUnregisterDialog = new ConfirmationDialog<>();
    private final ConfirmationDialog<Fakt> confirmFaktOpenDialog = new ConfirmationDialog<>();


    public ZakFormDialog(BiConsumer<Zak, Operation> itemSaver,
                         Consumer<Zak> itemDeleter)
//                         KontService kontService)
    {
        super(GenderGrammar.MASCULINE, Kont.NOMINATIVE_SINGULAR
                , Kont.GENITIVE_SINGULAR, Kont.ACCUSATIVE_SINGULAR
                , itemSaver, itemDeleter);

        setWidth("1200px");
//        setHeight("600px");

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.zakService = zakService;
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

//        initObjednatelField();
//        initInvestorField();
//        initTextField();
//        initMenaField();

        zakMena = getCurrentItem().getMena();
        initHonorarField();
//        initArchField();

        initFaktGrid();
        initDocGrid();

        getFormLayout().add(objednatelField);
        getFormLayout().add(investorField);
        getFormLayout().add(textField);
        getFormLayout().add(honorarField);
        getFormLayout().add(menaField);
//        getFormLayout().add(datZadComp);
        getUpperGridLayout().add(docGrid);
        getLowerGridLayout().add(faktGrid);
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
        faktGrid.setItems(getCurrentItem().getFakts());
        docGrid.setItems(getCurrentItem().getZakDocs());

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

//    private void initObjednatelField() {
//        getBinder().forField(objednatelField)
//                .withConverter(String::trim, String::trim)
////                .withValidator(new StringLengthValidator(
////                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
////                        3, null))
////                .withValidator(
////                        objednatel -> (currentOperation != Operation.ADD) ?
////                            true : kontService.getByObjednatel(objednatel) == null,
////                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
//                .bind(Zak::getObjednatel, Zak::setObjednatel);
//    }


    private void initTextField() {
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Zak::getText, Zak::setText);
    }


    private void initMenaField() {
        getBinder().forField(menaField)
            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .bind(Zak::getMena, null);

//        final BeanItemContainer<Status> container = new BeanItemContainer<>(Status.class);
//        container.addAll(EnumSet.allOf(Status.class));
//        cStatus.setContainerDataSource(container);
//        cStatus.setItemCaptionPropertyId("caption");
//        basicContent.addComponent(cStatus);
    }

    private void initHonorarField() {
//        honorarField.setSuffixComponent(new Span(zakMena.name()));
        honorarField.setPreventInvalidInput(true);
        honorarField.getElement().setProperty("textAlign", ColumnTextAlign.END.getPropertyValue());
//        honorarField.setSuffixComponent(new Span(getCurrentItem().getKont().getMena().name()));
    }

    private void initFaktGrid() {
        Assert.notNull(faktGrid, "ZakGrid must not be null");
        faktGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        faktGrid.setId("fakt-grid");
        faktGrid.setClassName("vizman-simple-grid");
        faktGrid.setHeight("12em");

//        zakGrid.getElement().setAttribute("colspan", "2");
//        zakGrid.getStyle().set("padding-right", "0em");
//        zakGrid.getStyle().set("padding-left", "0em");
//        zakGrid.getStyle().set("padding-top", "2.5em");
//        zakGrid.getStyle().set("padding-bottom", "2.5em");

        faktGrid.addColumn(Fakt::getPlneni).setHeader("Plnění [%]")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getCastka).setHeader("Částka [" + getCurrentItem().getMena().name() + "]")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getZaklad).setHeader("Zálad [" + getCurrentItem().getMena().name() + "]")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getText).setHeader("Text")
                .setFlexGrow(1);
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktOpenButton))
                .setFlexGrow(0);
    }

    private void initDocGrid() {
        Assert.notNull(docGrid, "DocGrid must not be null");
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("doc-grid");
        docGrid.setClassName("vizman-simple-grid");
        docGrid.setHeight("12em");

        docGrid.addColumn(ZakDoc::getFilename).setHeader("Soubor");
        docGrid.addColumn(ZakDoc::getNote).setHeader("Poznámka");
//        docGrid.addColumn("Honorář CZK");
        docGrid.addColumn(ZakDoc::getDateCreate).setHeader("Vloženo");
        docGrid.addColumn(new ComponentRenderer<>(this::buildDocRemoveButton))
                .setFlexGrow(0);
    }

    private Component buildDocRemoveButton(ZakDoc kontDoc) {
            return new GridActionItemButton(event -> {
                close();
                confirmDocUnregisterDialog.open("Registrace dokumentu",
                        "Zrušit registraci dokumentu?", "", "Zrušit",
                        true, kontDoc, this::removeDocRegistration, this::open);
            });
    }

    private void removeDocRegistration(ZakDoc zakDoc) {
        close();
    }


    private Component buildFaktOpenButton(Fakt fakt) {
            return new GridActionItemButton(event -> {
                close();
                confirmFaktOpenDialog.open("Otevřít fakturaci ?",
                        "", "", "Zrušit",
//                        true, zak, this::openZakForm, this::open);
                        true, fakt, this::openFaktForm, this::open);
            });
    }

    private void openFaktForm(Fakt fakt) {
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
                    "Zrušení zakázky"
                    , "Zakázku " + getCurrentItem().getCkont() + " nelze zrušit, obsahuje fakturace"
                    , ""
            );
        } else {
            openConfirmDeleteDialog("Zrušit zakázku ?",
                    "Opravdu zrušit kontrakt “" + getCurrentItem().getCkont() + "“ ?",
                    "Pokud bude kontrakt zrušen, budou zrušena i další s ním související data.");
//        } else {
//            doDelete(getCurrentItem());
        }
    }
}
