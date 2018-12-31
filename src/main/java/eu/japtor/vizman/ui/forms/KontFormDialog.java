package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
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
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractEditorDialog<Kont> {

//    final NumberRenderer<Zak> moneyRenderer;
    final NumberFormat moneyFormat;
//    final NumberFormat numFormat;
    final StringToBigDecimalConverter bigDecimalMoneyConverter;
    final ValueProvider<Zak, String> honorProvider;
//    final NumberRenderer<Zak> moneyRenderer;
    final ComponentRenderer<HtmlComponent, Zak> moneyCellRenderer;

    private TextField ckontField = new TextField("Číslo kontraktu"); // = new TextField("Username");
    private Button ckontChangeBtn = new Button("Změnit č. kontraktu"); // = new TextField("Username");
    private Checkbox archCheck = new Checkbox("Archiv"); // = new TextField("Username");
    private TextField objednatelField = new TextField("Objednatel"); // = new TextField("Username");
    private TextField investorField = new TextField("Investor"); // = new TextField("Username");
    private TextField textField = new TextField("Text"); // = new TextField("Jméno");
    private TextField honorarField = new TextField("Honorář (suma ze zakázek/subdodávek)");

    private ComboBox<Mena> menaCombo = new ComboBox("Měna");
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
    private final ConfirmationDialog<KontDoc> confirmDocDeregDialog = new ConfirmationDialog<>();
    private final ConfirmationDialog<Zak> confirmZakOpenDialog = new ConfirmationDialog<>();


    public KontFormDialog(BiConsumer<Kont, Operation> itemSaver,
                          Consumer<Kont> itemDeleter,
                          KontService kontService)
    {
        super(GenderGrammar.MASCULINE, Kont.NOMINATIVE_SINGULAR, Kont.GENITIVE_SINGULAR, Kont.ACCUSATIVE_SINGULAR, itemSaver, itemDeleter);


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

        moneyCellRenderer = new ComponentRenderer<>(kontZak -> {
            Div comp = new Div();
//            if (TypeZak.KONT == kontZak.getTyp()) {
////            comp.getStyle().set("color", "darkmagenta");
////            return new Emphasis(kontZak.getHonorar().toString());
//                comp.getElement().appendChild(ElementFactory.createEmphasis(kontZak.getHonorar().toString()));
//                comp.getStyle()
////                    .set("color", "red")
////                    .set("text-indent", "1em");
////                        .set("padding-right", "1em")
//                ;
//            } else {
                if ((null != kontZak) && (kontZak.getHonorar().compareTo(BigDecimal.ZERO) < 0)) {
                    comp.getStyle()
                            .set("color", "red")
//                            .set("text-indent", "1em")
                    ;
                }
//                comp.getElement().appendChild(ElementFactory.createSpan(numFormat.format(kontZak.getHonorar())));
                comp.setText(moneyFormat.format(kontZak.getHonorar()));
//            }
            return comp;
        });

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

        initCkontField();
        initArchCheck();
        initObjednatelField();
        initInvestorField();
        initTextField();
        initMenaCombo();
        initHonorarField();
//        initDatZadField();
        initZakGrid();
        initDocGrid();

        getFormLayout().add(ckontField);
//        AbstractCompositeField box = new Composite();
//        box.setMargin(false);
//        box.setPadding(false);
        FlexLayout box = new FlexLayout();
        Span ribbon = new Span();
        ribbon.setWidth("3em");
        box.setAlignItems(FlexComponent.Alignment.BASELINE);
        box.add(ckontChangeBtn, ribbon, archCheck);
        getFormLayout().add(box);
//        getFormLayout().addFormItem(ckontChangeBtn, archCheck);
        getFormLayout().add(objednatelField);
        getFormLayout().add(investorField);
        getFormLayout().add(textField);
        getFormLayout().add(honorarField);
        getFormLayout().add(menaCombo);
//        getFormLayout().add(datZadComp);
        getUpperGridLayout().add(docGrid);
        getLowerGridLayout().add(zakGrid);
//        getFormLayout().add(buildZakGridContainer(zakGrid));
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

    private void initCkontField() {
        ckontField.setReadOnly(true);
        getBinder().forField(ckontField)
                .bind(Kont::getCkont, Kont::setCkont);
    }

    private void initArchCheck() {
        getBinder().forField(archCheck)
                .bind(Kont::getArch, Kont::setArch);
    }

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


    private void initMenaCombo() {
        menaCombo.setItems(Mena.values());
//        menaCombo.setDataProvider((DataProvider<Mena, String>) EnumSet.allOf(Mena.class));
//        menaCombo.setItems(EnumSet.allOf(Mena.class));
        getBinder().forField(menaCombo)
//            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .bind(Kont::getMena, Kont::setMena);

//        final BeanItemContainer<Status> container = new BeanItemContainer<>(Status.class);
//        container.addAll(EnumSet.allOf(Status.class));
//        cStatus.setContainerDataSource(container);
//        cStatus.setItemCaptionPropertyId("caption");
//        basicContent.addComponent(cStatus);
    }


    private void initHonorarField() {
        honorarField.setReadOnly(true);
        honorarField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarField)
                .withConverter(bigDecimalMoneyConverter)
                .bind(Kont::getHonorar, null);
    }


    // ----------------------------------------------

    private void initZakGrid() {
        Assert.notNull(zakGrid, "ZakGrid must not be null");
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
                .setFlexGrow(0);
        ;

        zakGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenButton))
                .setFlexGrow(0);
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
        docGrid.addColumn(KontDoc::getDateCreate).setHeader("Registrováno");
        docGrid.addColumn(new ComponentRenderer<>(this::buildDocRemoveButton))
                .setFlexGrow(0);
    }

    private Component buildDocRemoveButton(KontDoc kontDoc) {
            return new GridActionItemButton(event -> {
                close();
                confirmDocDeregDialog.open("Zrušit registrace dokumentu ?",
                        "", "", "Zrušit",
                        true, kontDoc, this::removeDocRegistration, this::open);
            });
    }

    private void removeDocRegistration(KontDoc kontDoc) {
        close();
    }


    private Component buildZakOpenButton(Zak zak) {
            return new GridActionItemButton(event -> {
                close();
                confirmZakOpenDialog.open("Zrušit zakázku ?",
                        "", "", "Zrušit",
//                        true, zak, this::openZakForm, this::open);
                        true, zak, this::openZakForm, this::open);
            });
    }

    private void openZakForm(Zak z) {
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
