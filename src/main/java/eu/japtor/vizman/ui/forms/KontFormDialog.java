package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import eu.japtor.vizman.backend.bean.EvidKont;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.FormatUtils;
import eu.japtor.vizman.ui.components.*;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractEditorDialog<Kont> {

    private final static String ZAK_EDIT_COL_KEY = "zak-edit-col";

    final ValueProvider<Zak, String> honorProvider;
    final ComponentRenderer<HtmlComponent, Zak> moneyCellRenderer;

    private TextField ckontField;
    private Button kontEvidButton;
    private Checkbox archCheck;
    private TextField objednatelField;
    private TextField investorField;
    private TextField textField;
    private TextField honorarField;
    private ComboBox<Mena> menaCombo;

    private Kont kontOrig;
//    private String ckontOrig;
//    private String textOrig;
//    private String folderOrig;

//    private TextField menaField = new TextField("Měna");

//    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

//    private Grid<Zak> zakazkyGrid;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

    private Grid<KontDoc> docGrid;
    private FlexLayout docDirComponent;
    private KzFolderField kontDocFolderField;
    private Button openDocDirBtn;
    private Button registerDocButton;

    private Grid<Zak> zakGrid;
    private Button newZakButton;
    private Button newAkvButton;
    private Button newSubButton;

    private KontEvidFormDialog kontEvidFormDialog;
    private ZakFormDialog zakFormDialog;
    private ConfirmationDialog<KontDoc> confirmDocUnregisterDialog;
//    private final ConfirmationDialog<Zak> confirmZakOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private KontService kontService;
    private ZakService zakService;
    private FaktService faktService;


////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;



    public KontFormDialog(BiConsumer<Kont, Operation> itemSaver,
                          Consumer<Kont> itemDeleter,
                          KontService kontService,
                          ZakService zakService,
                          FaktService faktService)
    {
        super(true, true, itemSaver, itemDeleter);
        setWidth("1300px");
//        setHeight("600px");

        this.kontService = kontService;
        this.zakService = zakService;
        this.addOpenedChangeListener(event -> {
            if (Operation.ADD == currentOperation) {
                kontEvidButton.click();
            }
        });

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

//        bigDecimalMoneyConverter = new StringToBigDecimalConverter("Špatný formát čísla") {
//            @Override
//            protected java.text.NumberFormat getFormat(Locale locale) {
//                NumberFormat numberFormat = super.getFormat(locale);
//                numberFormat.setGroupingUsed(true);
//                numberFormat.setMinimumFractionDigits(2);
//                numberFormat.setMaximumFractionDigits(2);
//                return numberFormat;
//            }
//        };

        honorProvider = (zak) -> FormatUtils.moneyFormat.format(zak.getHonorar());

//        moneyCellRenderer = new ComponentRenderer<>(kontZak -> {
        moneyCellRenderer = new ComponentRenderer<>(zak -> {
            Div comp = new Div();
                if ((null != zak) && (zak.getHonorar().compareTo(BigDecimal.ZERO) < 0)) {
                    comp.getStyle()
                            .set("color", "red")
                    ;
                }
                comp.setText(FormatUtils.moneyFormat.format(zak.getHonorar()));
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


        kontEvidFormDialog = new KontEvidFormDialog(this::saveKontEvid, kontService);
        zakFormDialog = new ZakFormDialog(this::saveZak, this::deleteZak, zakService, faktService);
        confirmDocUnregisterDialog = new ConfirmationDialog<>();

        getFormLayout().add(
                initCkontField()
                , initEvidArchComponent()
                , initTextField()
                , initObjednatelField()
                , initInvestorField()
                , initHonorarField()
                , initMenaCombo()
        );

        getUpperGridCont().add(
                initDocDirComponent()
                , initDocGridBar()
                , initDocGrid()
        );

        getLowerGridCont().add(
                initZakGridBar()
                , initZakGrid()
        );
    }


    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {

        // Mandatory, should be first
        setItemNames(getCurrentItem().getTyp());

        kontOrig = getCurrentItem();

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

        zakGrid.setItems(getCurrentItem().getNodes());
        docGrid.setItems(getCurrentItem().getKontDocs());
        kontDocFolderField.setParentFolder(null);
//        kontFolderText.setText(getCurrentItem().getFolder());
    }

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
            openConfirmDeleteDialog("Zrušení kontraktu",
                    "Opravdu zrušit kontrakt " + getCurrentItem().getCkont() + " ?",
                    "Poznámka: Projektové a dokumentové adresáře včetně souborů zůstanou nezměněny.");
        }
    }


    private void saveKontEvid(EvidKont evidKont, Operation operation) {
        getCurrentItem().setCkont(evidKont.getCkont());
        getCurrentItem().setText(evidKont.getText());
        getCurrentItem().setFolder(evidKont.getFolder());
        getBinder().readBean(getCurrentItem());
        formFieldValuesChanged();
//        setChangeDependantControlsEnabled(false);

        Notification.show(
//                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Nové číslo a text kontraktu uloženy", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }

    private void setChangeDependantControlsEnabled(boolean enable) {
//        zakGrid.getColumnByKey(ZAK_EDIT_COL_KEY).getElement(). fff
        newAkvButton.setEnabled(enable);
        newZakButton.setEnabled(enable);
        newSubButton.setEnabled(enable);
    }

    private void saveZak(Zak zak, Operation operation) {
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

        Notification.show("Rušení zakázek není implementováno.", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }


//    public static class MoneyFormat extends DecimalFormat {
//
//        public MoneyFormat (Locale locale) {
//            super();
////        moneyFormat = DecimalFormat.getInstance();
////        if (moneyFormat instanceof DecimalFormat) {
////            ((DecimalFormat)moneyFormat).setParseBigDecimal(true);
////        }
//            NumberFormat numberFormat = NumberFormat.getInstance(locale);
//
//            this.setGroupingUsed(true);
//            this.setMinimumFractionDigits(2);
//            this.setMaximumFractionDigits(2);
//        }
//    }



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
        ckontField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(ckontField)
                .bind(Kont::getCkont, Kont::setCkont);
        return ckontField;
    }


    private Component initKontEvidButton() {
        kontEvidButton = new Button("Evidence");
        kontEvidButton.addClickListener(event -> {
            EvidKont evidKont = new EvidKont(
                    getCurrentItem().getCkont()
                    , getCurrentItem().getText()
                    , getCurrentItem().getFolder()
            );
            if (null == getCurrentItem().getId()) {
                kontEvidFormDialog.open(
                        evidKont
                        , Operation.ADD
                        , "Zadání EVIDENCE KONTRAKTU");
            } else {
                kontEvidFormDialog.open(
                        evidKont
                        , Operation.EDIT
                        , "Změna EVIDENCE KONTRAKTU");
            }
        });
        return kontEvidButton;
    }

    private Component initArchCheck() {
        archCheck = new Checkbox("Archiv");
        archCheck.getElement().setAttribute("theme", "secondary");
        archCheck.setReadOnly(true);
        getBinder().forField(archCheck)
                .bind(Kont::getArch, null);
        return archCheck;
    }

    private Component initEvidArchComponent() {
        FlexLayout evidArchCont = new FlexLayout();
        evidArchCont.setAlignItems(FlexComponent.Alignment.BASELINE);
        evidArchCont.add(
                initKontEvidButton()
                , new Ribbon("3em")
                , initArchCheck()
        );
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
        objednatelField.setValueChangeMode(ValueChangeMode.EAGER);
        return objednatelField;
    }

    private Component initInvestorField() {
        investorField = new TextField("Investor");
        getBinder().forField(investorField)
//                .withConverter(String::trim, String::trim)    // TODO: Gives NPE for null
//                .bind(Kont::getInvestor, Kont::setInvestor);
                .bind(Kont::getInvestor, Kont::setInvestor);
        investorField.setValueChangeMode(ValueChangeMode.EAGER);
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
                .withConverter(FormatUtils.bigDecimalMoneyConverter)
                .bind(Kont::getHonorar, null);
        return honorarField;
    }

    // ------------------------------------------


    private Component initDocDirComponent() {
        docDirComponent = new FlexLayout();
        docDirComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        docDirComponent.setWidth("100%");
        docDirComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        docDirComponent.add(initKontDocFolderField());
        return docDirComponent;
    }

    private Component initKontDocFolderField() {
        kontDocFolderField = new KzFolderField(
                null
                , "D:\\vizman-doc-root"
                , "D:\\vizman-proj-root"
        );
        kontDocFolderField.setWidth("100%");
        kontDocFolderField.getStyle().set("padding-top", "0em");
//        kontDocFolderField.setReadOnly(true);
        getBinder().forField(kontDocFolderField)
                .bind(Kont::getFolder, null);

        return kontDocFolderField;
    }


    private Component initRegisterDocButton() {
        registerDocButton = new NewItemButton("Dokument", event -> {});
        return registerDocButton;
    }

    private Component initDocGridBar() {
        FlexLayout docGridBar = new FlexLayout();
        docGridBar.setWidth("100%");
        docGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        docGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        docGridBar.add(
                initDocGridTitle(),
                new Ribbon(),
                initRegisterDocButton()
        );
        return docGridBar;
    }

    private Component initDocGridTitle() {
        H4 docGridTitle = new H4();
        docGridTitle.setText("Dokumenty");
        return docGridTitle;
    }

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
            confirmDocUnregisterDialog.open("Zrušit registraci dokumentu ?",
                    "", "", "Zrušit",
                    true, kontDoc, this::removeDocRegistration, this::open);
        });
    }

    private void removeDocRegistration(KontDoc kontDoc) {
        close();
    }

    // ----------------------------------------------

    private Component initZakGridTitle() {
        H4 zakTitle = new H4();
        zakTitle.setText(ItemNames.getNomP(ItemType.ZAK));
        zakTitle.getStyle().set("margin", "0");
        return zakTitle;
    }

    private Component initNewZakButton() {
        newZakButton = new NewItemButton(ItemNames.getNomP(ItemType.ZAK), event ->
                zakFormDialog.open(new Zak(ItemType.ZAK)
                    , Operation.ADD, ItemNames.getNomS(ItemType.ZAK))
        );
//                        new Zak(ItemType.ZAK), AbstractEditorDialog.Operation.ADD);
        return newZakButton;
    }

    private Component initNewAkvButton() {
        newAkvButton = new NewItemButton(ItemNames.getNomS(ItemType.AKV), event ->
                zakFormDialog.open(new Zak(ItemType.AKV)
                    , Operation.ADD, ItemNames.getNomS(ItemType.AKV)));
        return newAkvButton;
    }

    private Component initNewSubButton() {
        newSubButton = new NewItemButton(ItemNames.getNomS(ItemType.SUB), event ->
                zakFormDialog.open(new Zak(ItemType.SUB)
                    , Operation.ADD, ItemNames.getNomS(ItemType.SUB)));
        return newSubButton;
    }

//    Button newAkvButton = new NewItemButton("Akvizice", null);
//    Button newSubButton = new NewItemButton("Subdodávka", null);

    private Component initZakGridBar() {
        FlexLayout zakGridBar = new FlexLayout();
        zakGridBar.setWidth("100%");
        zakGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        zakGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        zakGridBar.add(
                initZakGridTitle(),
                new Ribbon(),
                new FlexLayout(
                    initNewZakButton(),
                    new Ribbon(),
                    initNewAkvButton(),
                    new Ribbon(),
                    initNewSubButton()
                )
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
        zakGrid.getStyle().set("marginTop", "0.5em");
        zakGrid.setHeight("15em");

        ComponentRenderer<Component, Zak> zakArchRenderer = new ComponentRenderer<>(zak -> {
            // Note: following icons MUST NOT be created outside this renderer (the KonFormDialog cannot be reopened)
            Icon icoTrue = new Icon(VaadinIcon.CHECK);
            icoTrue.setSize("0.8em");
            icoTrue.getStyle().set("theme", "small icon secondary");
            Icon icoFalse = new Icon(VaadinIcon.MINUS);
            icoFalse.setSize("0.8em");
            icoFalse.getStyle().set("theme", "small icon secondary");
            return zak.getArch() ? icoTrue : icoFalse;
        });

        zakGrid.addColumn(Zak::getTyp).setHeader("Typ")
                .setWidth("5em").setFlexGrow(0)
        ;
        zakGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenBtn))
                .setFlexGrow(0)
                .setKey(ZAK_EDIT_COL_KEY)
        ;
        zakGrid.addColumn(zakArchRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
        ;

        zakGrid.addColumn(Zak::getCzak).setHeader("ČZ")
                .setWidth("3em").setFlexGrow(0)
        ;

        zakGrid.addColumn(Zak::getRokzak).setHeader("Rok")
                .setWidth("5em").setFlexGrow(0)
        ;

        zakGrid.addColumn(moneyCellRenderer).setHeader("Honorář")
                .setResizable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("10em").setFlexGrow(0)
        ;
        zakGrid.addColumn(Zak::getFaktsOver).setHeader("Fakt.")
                .setFlexGrow(0)
        ;
        zakGrid.addColumn(Zak::getText).setHeader("Text")
                .setFlexGrow(1)
        ;

        return zakGrid;
    }


    private Component buildZakOpenBtn(Zak zak) {
        Button btn = new GridItemOpenBtn(event -> openZakForm(zak));
//        btn.setEnabled(false);
        return btn;
    }

    private void openZakForm(Zak zak) {
//        this.close();
        zakFormDialog.open(
            zak, Operation.EDIT,
            "[ Vytvořeno: " + (zak).getDateCreate().toString()
                    + " , Poslední změna: " + (zak.getDatetimeUpdate().toString() + " ]"));

//                confirmZakOpenDialog.open("Otevrit zakázku ?",
//                        "", "", "Zrušit",
////                        true, zak, this::openZakForm, this::open);
//                        true, zak, this::openZakForm, this::close);
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

}
