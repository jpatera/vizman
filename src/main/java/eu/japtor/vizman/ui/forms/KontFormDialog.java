package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractEditorDialog<Kont> {

    final NumberFormat moneyFormat;
    final StringToBigDecimalConverter bigDecimalMoneyConverter;
    final ValueProvider<Zak, String> honorProvider;
    final ComponentRenderer<HtmlComponent, Zak> moneyCellRenderer;

//    final NumberRenderer<Zak> moneyRenderer;
//    final NumberFormat numFormat;
//    final NumberRenderer<Zak> moneyRenderer;

    private TextField ckontField;
    private Button evidChangeButton;
    private Checkbox archCheck;
    private TextField objednatelField;
    private TextField investorField;
    private TextField textField;
    private TextField honorarField = new TextField("Honorář (suma ze zakázek a subdodávek)");
    private ComboBox<Mena> menaCombo;
//    private TextField menaField = new TextField("Měna");

//    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

//    private Grid<Zak> zakazkyGrid;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

    private Grid<KontDoc> docGrid;
    private HorizontalLayout docDirCont;
    private FlexLayout docDirComponent;
    private TextField docDirField;
    private Button openDocDirButton;
    private HorizontalLayout docGridBar;
    private Button registerDocButton;

    private Grid<Zak> zakGrid;
    private HorizontalLayout zakGridBar;
    private Button newZakButton;
    private Button newAkvButton;
    private Button newSubButton;

    private KontEvidFormDialog kontEvidFormDialog;

    private ZakFormDialog zakFormDialog;
    private final ConfirmationDialog<KontDoc> confirmDocDeregDialog = new ConfirmationDialog<>();
    private final ConfirmationDialog<Zak> confirmZakOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private KontService kontService;
    private ZakService zakService;


////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;



    public KontFormDialog(BiConsumer<Kont, Operation> itemSaver,
                          Consumer<Kont> itemDeleter,
                          KontService kontService)
    {
        super(true, true, itemSaver, itemDeleter);

        this.kontService = kontService;

        setWidth("1200px");
//        setHeight("600px");


        moneyFormat = new MoneyFormat(Locale.getDefault());

//        moneyFormat = DecimalFormat.getInstance();
//        if (moneyFormat instanceof DecimalFormat) {
//            ((DecimalFormat)moneyFormat).setParseBigDecimal(true);
//        }
//        moneyFormat.setGroupingUsed(true);
//        moneyFormat.setMinimumFractionDigits(2);
//        moneyFormat.setMaximumFractionDigits(2);

//        numFormat = DecimalFormat.getInstance(Locale.getDefault());
//        if (numFormat instanceof DecimalFormat) {
//            ((DecimalFormat)numFormat).setParseBigDecimal(true);
//        }
//        numFormat.setGroupingUsed(true);
//        numFormat.setMinimumFractionDigits(2);
//        numFormat.setMaximumFractionDigits(2);

        bigDecimalMoneyConverter = new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(true);
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat;
            }
        };

        honorProvider = (zak) -> moneyFormat.format(zak.getHonorar());

//        moneyRenderer = new NumberRenderer(honorProvider, numFormat);

//        moneyCellRenderer = new ComponentRenderer<>(kontZak -> {
        moneyCellRenderer = new ComponentRenderer<>(zak -> {
            Div comp = new Div();
//            if (ItemType.KONT == kontZak.getTyp()) {
////            comp.getStyle().set("color", "darkmagenta");
////            return new Emphasis(kontZak.getHonorar().toString());
//                comp.getElement().appendChild(ElementFactory.createEmphasis(kontZak.getHonorar().toString()));
//                comp.getStyle()
////                    .set("color", "red")
////                    .set("text-indent", "1em");
////                        .set("padding-right", "1em")
//                ;
//            } else {
                if ((null != zak) && (zak.getHonorar().compareTo(BigDecimal.ZERO) < 0)) {
                    comp.getStyle()
                            .set("color", "red")
//                            .set("text-indent", "1em")
                    ;
                }
//                comp.getElement().appendChild(ElementFactory.createSpan(numFormat.format(kontZak.getHonorar())));
                comp.setText(moneyFormat.format(zak.getHonorar()));
//            }
            return comp;
        });


//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

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


        kontEvidFormDialog = new KontEvidFormDialog(this::saveEvid, kontService);
        zakFormDialog = new ZakFormDialog(this::saveZak, this::deleteZak, zakService);

//        initCkontField();
//        initObjednatelField();
//        initInvestorField();
//        initTextField();
//        initMenaCombo();
//        initHonorarField();
//        initDatZadField();

//        initDocGridBar();
//        initDocGrid();

        getFormLayout().add(initCkontField());
        getFormLayout().add(initEvidArchComponent());
        getFormLayout().add(initTextField());
        getFormLayout().add(initObjednatelField());
        getFormLayout().add(initInvestorField());
        getFormLayout().add(initHonorarField());
        getFormLayout().add(initMenaCombo());
//        getFormLayout().add(datZadComp);

        getUpperGridCont().add(initDocDirComponent());
//        getUpperGridCont().add(initDocGridBar());
        getUpperGridCont().add(initDocGrid());

        getLowerGridCont().add(initZakGridBar());
        getLowerGridCont().add(initZakGrid());
//        getFormLayout().add(buildZakGridContainer(zakGrid));

    }

    private void saveEvid(Kont kont, AbstractEditorDialog.Operation operation) {
//        Zak newInstance = zakService.saveZak(zak);
        this.getBinder().readBean(kont);
        Notification.show(
//                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Evidence kontraktu zadána", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }

    private void saveZak(Zak zak, AbstractEditorDialog.Operation operation) {
        Zak newInstance = zakService.saveZak(zak);
        zakGrid.getDataProvider().refreshItem(newInstance);
        Notification.show(
//                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Změny zakázky uloženy", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }

    private void deleteZak(Zak zak) {
//        zakService.deleteZak(zak);
//        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
//        zakGrid.getDataProvider().refreshAll();

        Notification.show("Zakázka (NE) zrušena.", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }




    public static class MoneyFormat extends DecimalFormat {

        public MoneyFormat (Locale locale) {
            super();
//        moneyFormat = DecimalFormat.getInstance();
//        if (moneyFormat instanceof DecimalFormat) {
//            ((DecimalFormat)moneyFormat).setParseBigDecimal(true);
//        }
            NumberFormat numberFormat = NumberFormat.getInstance(locale);

            this.setGroupingUsed(true);
            this.setMinimumFractionDigits(2);
            this.setMaximumFractionDigits(2);
        }
    }


    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {

        // Mandatory, should be first
        setItemNames(getCurrentItem().getTyp());

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

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        ckontField.setReadOnly(true);
        getBinder().forField(ckontField)
                .bind(Kont::getCkont, Kont::setCkont);
        return ckontField;
    }

    private Component initEvidChangeButton() {
        evidChangeButton = new Button("Změnit evidenci");
        evidChangeButton.addClickListener(event -> {kontEvidFormDialog.open();});
        return evidChangeButton;
    }

    private Component initArchCheck() {
        archCheck = new Checkbox("Archiv");
        archCheck.setReadOnly(true);
        getBinder().forField(archCheck)
                .bind(Kont::getArch, null);
        return archCheck;
    }

    private Component initEvidArchComponent() {
        FlexLayout evidArchCont = new FlexLayout();
        evidArchCont.setAlignItems(FlexComponent.Alignment.BASELINE);
        evidArchCont.add(initEvidChangeButton(), new Ribbon("3em"), initArchCheck());
        return evidArchCont;
    }

    private Component initTextField() {
        textField = new TextField("Text kontraktu");
        textField.setReadOnly(true);
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Kont::getText, Kont::setText);
        return textField;
    }

    private Component initObjednatelField() {
        objednatelField = new TextField("Objednatel");
        getBinder().forField(objednatelField)
//                .withConverter(String::trim, String::trim)
//                .withValidator(new StringLengthValidator(
//                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
//                        3, null))
//                .withValidator(
//                        objednatel -> (currentOperation != Operation.ADD) ?
//                            true : kontService.getByObjednatel(objednatel) == null,
//                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Kont::getObjednatel, Kont::setObjednatel);
        return objednatelField;
    }

    private Component initInvestorField() {
        investorField = new TextField("Investor");
        getBinder().forField(investorField)
//                .withConverter(String::trim, String::trim)    // TODO: Gives NPE for null
//                .bind(Kont::getInvestor, Kont::setInvestor);
                .bind(Kont::getInvestor, Kont::setInvestor);
        return investorField;
    }

    private Component initMenaCombo() {
        menaCombo = new ComboBox<>("Měna");
        menaCombo.setItems(Mena.values());
//        menaCombo.setDataProvider((DataProvider<Mena, String>) EnumSet.allOf(Mena.class));
//        menaCombo.setItems(EnumSet.allOf(Mena.class));
        getBinder().forField(menaCombo)
//            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .bind(Kont::getMena, Kont::setMena);
        return menaCombo;

//        final BeanItemContainer<Status> container = new BeanItemContainer<>(Status.class);
//        container.addAll(EnumSet.allOf(Status.class));
//        cStatus.setContainerDataSource(container);
//        cStatus.setItemCaptionPropertyId("caption");
//        basicContent.addComponent(cStatus);
    }


    private Component initHonorarField() {
        honorarField = new TextField("Honorář");
        honorarField.setReadOnly(true);
        honorarField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarField)
                .withConverter(bigDecimalMoneyConverter)
                .bind(Kont::getHonorar, null);
        return honorarField;
    }

    // ------------------------------------------

    private Component initDocDirComponent() {
//        docDirComponent = new HorizontalLayout();
        docDirComponent = new FlexLayout();
//        docDirComponent = new TextField();
        docDirComponent.setAlignItems(FlexComponent.Alignment.BASELINE);

        docDirComponent.setWidth("100%");
        docDirComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        docDirComponent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        docDirComponent.add(
//                new Label("Adresář"),
//                new Ribbon(),
                initDocDirField(),
//                new Ribbon(),
//                initOpenDocDirBtn(),
                new Ribbon(),
                initRegisterDocButton()
        );
        return docDirComponent;
    }

    private Component initDocDirField() {
//        docDirField = new Paragraph();
        docDirField = new OpenDirField(
                "Dokumenty kontraktu"
                , "Adresář není zadán"
                , event -> {}
//                , event -> FileUtils.validatePathname(event.getValue()

        );
        docDirField.getStyle()
                .set("padding-top", "0em");
        docDirField.setWidth("100%");
        docDirField.setPlaceholder("[zak-doc-root]\\neco\\...");
//        docDirField.setReadOnly(true);
        return docDirField;
    }

//    private Component initOpenDocDirBtn() {
//        openDocDirButton = new Button("Otevřít");
//        openDocDirButton.addClickListener(event -> {});
//        return openDocDirButton;
//    }

    private Component initRegisterDocButton() {
        registerDocButton = new Button("+ Dokument");
        registerDocButton.addClickListener(event -> {});
        return registerDocButton;
    }

//    private Component initDocGridBar() {
//        docGridBar = new HorizontalLayout();
//        docGridBar.setWidth("100%");
//        docGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        docGridBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
//        docGridBar.add(
//                initRegisterDocButton()
//        );
//        return docGridBar;
//    }

    private Component initDocGrid() {
        docGrid = new Grid<>();
        docGrid.setColumnReorderingAllowed(true);
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("doc-grid");
        docGrid.setClassName("vizman-simple-grid");
        docGrid.setHeight("12em");

        docGrid.addColumn(KontDoc::getFilename).setHeader("Soubor");
        docGrid.addColumn(KontDoc::getNote).setHeader("Poznámka");
//        docGrid.addColumn("Honorář CZK");
        docGrid.addColumn(KontDoc::getDateCreate).setHeader("Registrováno");
        docGrid.addColumn(new ComponentRenderer<>(this::buildDocRemoveButton))
                .setFlexGrow(0);
        return docGrid;
    }

    private Component buildDocRemoveButton(KontDoc kontDoc) {
        return new GridItemFileRemoveBtn(event -> {
            close();
            confirmDocDeregDialog.open("Zrušit registrace dokumentu ?",
                    "", "", "Zrušit",
                    true, kontDoc, this::removeDocRegistration, this::open);
        });
    }

    private void removeDocRegistration(KontDoc kontDoc) {
        close();
    }

    // ----------------------------------------------


    private Component initNewZakButton() {
        newZakButton = new NewItemButton("Zakázka", null);
        newZakButton.addClickListener(event -> zakFormDialog.open(new Zak(ItemType.ZAK)
                , AbstractEditorDialog.Operation.ADD, "Zakázka"));
        return newZakButton;
    }

    private Component initNewAkvButton() {
        newAkvButton = new NewItemButton("Akvizice", null);
        newAkvButton.addClickListener(event -> zakFormDialog.open(new Zak(ItemType.AKV)
                , AbstractEditorDialog.Operation.ADD, "Akvizice"));
        return newAkvButton;
    }

    private Component initNewSubButton() {
        newSubButton = new NewItemButton("Subdodávka", null);
        newSubButton.addClickListener(event -> zakFormDialog.open(new Zak(ItemType.SUB)
                , AbstractEditorDialog.Operation.ADD, "Subdodávka"));
        return newSubButton;
    }

//    Button newAkvButton = new NewItemButton("Akvizice", null);
//    Button newSubButton = new NewItemButton("Subdodávka", null);

    private Component initZakGridBar() {
        zakGridBar = new HorizontalLayout();
        zakGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        zakGridBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        zakGridBar.setWidth("100%");

        zakGridBar.add(
                initNewZakButton(),
                initNewAkvButton(),
                initNewSubButton()
//                new Button("Nová akvizice", event -> {}),
//                new Button("Nová subdodávka", event -> {})
        );
        return zakGridBar;
    }

    private Component initZakGrid() {
        zakGrid = new Grid<>();
        zakGrid.setColumnReorderingAllowed(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        zakGrid.setId("zak-grid");
        zakGrid.setClassName("vizman-simple-grid");
        zakGrid.setHeight("15em");


//        zakGrid.getElement().setAttribute("colspan", "2");
//        zakGrid.getStyle().set("padding-right", "0em");
//        zakGrid.getStyle().set("padding-left", "0em");
//        zakGrid.getStyle().set("padding-top", "2.5em");
//        zakGrid.getStyle().set("padding-bottom", "2.5em");

        zakGrid.addColumn(Zak::getCzak).setHeader("ČZ")
                .setWidth("3em").setFlexGrow(0)
        ;


//        zakGrid.addColumn(new ComponentRenderer<>(this::createEditButton))
//        zakGrid.addColumn(moneyRenderer).setHeader("Honorář")
//        new NumberRenderer<>(Zak::getHonorar, sazbaFormat)

//        zakGrid.addColumn(Zak::getHonorar).setHeader("Honorář")
//                .setWidth("8em").setResizable(true).setTextAlign(ColumnTextAlign.END)
//                .setFlexGrow(0)
//        ;
//        zakGrid.addColumn(moneyRenderer).setHeader("Honorář")
        zakGrid.addColumn(moneyCellRenderer).setHeader("Honorář")
                .setResizable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("10em").setFlexGrow(0)
        ;

        zakGrid.addColumn(Zak::getText).setHeader("Text")
                .setFlexGrow(1)
        ;

        zakGrid.addColumn(Zak::getFaktsOver).setHeader("Fakt.")
                .setFlexGrow(0)
        ;

        zakGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenBtn))
                .setFlexGrow(0)
        ;
        return zakGrid;
    }


    private Component buildZakOpenBtn(Zak zak) {
            return new GridItemOpenBtn(event -> {
//                this.close();
                zakFormDialog.open(
                        zak, AbstractEditorDialog.Operation.EDIT,
                        "[ Vytvořeno: " + (zak).getDateCreate().toString()
                                + " , Poslední změna: " + (zak.getDatetimeUpdate().toString() + " ]"));

//                confirmZakOpenDialog.open("Otevrit zakázku ?",
//                        "", "", "Zrušit",
////                        true, zak, this::openZakForm, this::open);
//                        true, zak, this::openZakForm, this::close);
            });
    }

    private void openZakForm(Zak zak) {
        zakFormDialog.open();
        close();
    }

//    private void openZakForm(Zak zak) {
//        close();
//    }

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
