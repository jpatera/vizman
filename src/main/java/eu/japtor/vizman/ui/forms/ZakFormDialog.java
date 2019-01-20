package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.FormatUtils;
import eu.japtor.vizman.ui.components.*;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakFormDialog extends AbstractEditorDialog<Zak> implements BeforeEnterObserver {

    final ValueProvider<Fakt, String> castkaProvider;
    final ComponentRenderer<HtmlComponent, Fakt> moneyCellRenderer;

    private TextField czakField;
    private Button evidChangeBtn;
    private Checkbox archCheck;
    private TextField textField;
    private TextField skupinaField;
    private TextField honorarField;
    private TextField menaField;

    private Integer czakOrig;
    private String textOrig;
    private String folderOrig;

    //    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

    private Grid<ZakDoc> docGrid;
//    private HorizontalLayout docDirComponent;
//    private FormLayout.FormItem docDirComponent;
    private FlexLayout docDirComponent;
//    private Paragraph zakDocFolderField;
    private KzFolderField zakDocFolderField;
    private Button openDocDirBtn;
    private Button registerDocButton;


    private Grid<Fakt> faktGrid;
    private Button newFaktButton;

    private FaktFormDialog faktFormDialog;
    private final ConfirmationDialog<ZakDoc> confirmDocUnregisterDialog = new ConfirmationDialog<>();
    private ZakEvidFormDialog zakEvidFormDialog;
//    private final ConfirmationDialog<Fakt> confirmFaktOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private ZakService zakService;
    private FaktService faktService;


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    public ZakFormDialog(BiConsumer<Zak, Operation> itemSaver,
                         Consumer<Zak> itemDeleter,
                         ZakService zakService,
                         FaktService faktService)
    {
        super(true, true, itemSaver, itemDeleter);

        this.zakService = zakService;
        this.faktService = faktService;

        setWidth("1200px");
        //        setHeight("600px");

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
//
        castkaProvider = (fakt) -> FormatUtils.moneyFormat.format(fakt.getCastka());

        moneyCellRenderer = new ComponentRenderer<>(fakt -> {
            Div comp = new Div();
            if ((null != fakt) && (fakt.getCastka().compareTo(BigDecimal.ZERO) < 0)) {
                comp.getStyle()
                        .set("color", "red")
                ;
            }
            comp.setText(FormatUtils.moneyFormat.format(fakt.getCastka()));
            return comp;
        });


        zakEvidFormDialog = new ZakEvidFormDialog(this::saveZakEvid, zakService);
        faktFormDialog = new FaktFormDialog(this::saveFakt, this::deleteFakt, faktService);

        getFormLayout().add(
                initCzakField()
                , initEvidArchComponent()
                , initTextField()
                , initSkupinaField()
                , new Paragraph("")
                , initHonorarField()
                , initMenaField()
        );

//        getUpperGridCont().add(new Ribbon(Ribbon.Orientation.VERTICAL, "1em"));
        getUpperGridCont().add(
                initDocDirComponent()
                , initDocGridBar()
                , initDocGrid()
        );

        getLowerGridCont().add(
                initFaktGridBar()
                , initFaktGrid()
        );
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {

        // Mandatory, should be first
        setItemNames(getCurrentItem().getTyp());

        czakOrig = getCurrentItem().getCzak();
        textOrig = getCurrentItem().getText();
        folderOrig = getCurrentItem().getFolder();

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());

        Mena zakMena = getCurrentItem().getMena();

        faktGrid.setItems(getCurrentItem().getFakts());
        docGrid.setItems(getCurrentItem().getZakDocs());
        zakDocFolderField.setParentFolder(getCurrentItem().getKont().getFolder());

    }

    private void saveZakEvid(EvidZak evidZak, Operation operation) {
        getCurrentItem().setCzak(evidZak.getCzak());
        getCurrentItem().setText(evidZak.getText());
        getCurrentItem().setFolder(evidZak.getFolder());
        getBinder().readBean(getCurrentItem());

        Notification.show(
//                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Číslo a text zakázky změněny", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }

    private void saveFakt(Fakt fakt, Operation operation) {
        Fakt newInstance = faktService.saveFakt(fakt);
        faktGrid.getDataProvider().refreshItem(newInstance);
        Notification.show(
//                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
                "Změny fakturace uloženy", 3000, Notification.Position.BOTTOM_END);
//        updateGridContent();
    }

    private void deleteFakt(Fakt fakt) {
//        faktService.deleteFakt(fakt);
//        faktGrid.getDataCommunicator().getKeyMapper().removeAll();
//        faktGrid.getDataProvider().refreshAll();

        Notification.show("Rušení fakturací není implementováno.", 3000, Notification.Position.BOTTOM_END);
//        updateFaktGridContent();
    }


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

    private Component initCzakField() {
        czakField = new TextField("Číslo zakázky");
        czakField.setReadOnly(true);
        czakField.getStyle()
//                .set("background-color", "yellow")
//                    .set("text-indent", "1em");
                .set("padding-top", "0em");
        getBinder().forField(czakField)
                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .bind(Zak::getCzak, Zak::setCzak);
        return czakField;
    }

    private Component initEvidChangeButton() {
        evidChangeBtn = new Button("Evidence");
        evidChangeBtn.addClickListener(event -> {
            EvidZak evidZak = new EvidZak(
                    getCurrentItem().getKontId()
                    , getCurrentItem().getCzak()
                    , getCurrentItem().getText()
                    , getCurrentItem().getFolder()
            );
            if (null == getCurrentItem().getId()) {
                zakEvidFormDialog.open(
                        evidZak
                        , Operation.ADD
                        , "Zadání EVIDENCE ZAKÁZKY");
            } else {
                zakEvidFormDialog.open(
                        evidZak
                        , Operation.EDIT
                        , "Změna EVIDENCE ZAKÁZKY");
            }
        });
        return evidChangeBtn;
    }

    private Component initArchCheck() {
        archCheck = new Checkbox("Archiv"); // = new TextField("Username");
        archCheck.getElement().setAttribute("theme", "secondary");
        getBinder().forField(archCheck)
                .bind(Zak::getArch, Zak::setArch);
        return archCheck;
    }

    private Component initEvidArchComponent() {
        FlexLayout evidArchCont = new FlexLayout();
        evidArchCont.setAlignItems(FlexComponent.Alignment.BASELINE);
        evidArchCont.add(
                initEvidChangeButton()
                , new Ribbon("3em")
                , initArchCheck()
        );
        return evidArchCont;
    }

    private Component initTextField() {
        textField = new TextField("Text zakázky");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Zak::getText, Zak::setText);
        textField.setReadOnly(true);
        return textField;
    }

    private Component initSkupinaField() {
        skupinaField = new TextField("Skupina");
        getBinder().forField(skupinaField)
                .bind(Zak::getSkupina, Zak::setSkupina);
        return skupinaField;
    }

    private Component initMenaField() {
        menaField = new TextField("Měna");
        getBinder().forField(menaField)
            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .bind(Zak::getMena, null);
        return menaField;
    }

    private Component initHonorarField() {
        honorarField = new TextField("Honorář");
//        honorarField.setReadOnly(true);
        honorarField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarField)
                .withConverter(FormatUtils.bigDecimalMoneyConverter)
                .bind(Zak::getHonorar, Zak::setHonorar);
        return honorarField;
    }

    // ----------------------------------------------

    private Component initDocDirComponent() {
        docDirComponent = new FlexLayout();
        docDirComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        docDirComponent.setWidth("100%");
        docDirComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        docDirComponent.add(initZakDocFolderField());
        return docDirComponent;
    }

    private Component initZakDocFolderField() {
        zakDocFolderField = new KzFolderField(
                null
                , "D:\\vizman-doc-root"
                , "D:\\vizman-proj-root"
        );
        zakDocFolderField.setWidth("100%");
        zakDocFolderField.getStyle().set("padding-top", "0em");
//        zakDocFolderField.setReadOnly(true);
        getBinder().forField(zakDocFolderField)
                .bind(Zak::getFolder, null);

        return zakDocFolderField;
    }


    private Component initRegisterDocButton() {
        registerDocButton = new Button("+ Dokument");
        registerDocButton.addClickListener(event -> {});
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
        docGrid = new Grid();
        docGrid.setColumnReorderingAllowed(true);
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
        return docGrid;
    }

    private Component buildDocRemoveButton(ZakDoc kontDoc) {
        return new GridItemOpenBtn(event -> {
            close();
            confirmDocUnregisterDialog.open("Registrace dokumentu",
                    "Zrušit registraci dokumentu?", "", "Zrušit",
                    true, kontDoc, this::removeDocRegistration, this::open);
        });
    }

    private void removeDocRegistration(ZakDoc zakDoc) {
        close();
    }


    // ----------------------------------------------

    private Component initFaktGridTitle() {
        H4 zakTitle = new H4();
        zakTitle.setText(ItemNames.getNomP(ItemType.FAKT));
        zakTitle.getStyle().set("margin", "0");
        return zakTitle;
    }

    private Component initNewFaktButton() {
        newFaktButton = new NewItemButton(ItemNames.getNomS(ItemType.FAKT), event ->
// TODO: add ItemType to Fakt !!!
                faktFormDialog.open(new Fakt(), Operation.ADD, "Fakturace")
//                zakFormDialog.open(new Zak(ItemType.FAKT), AbstractEditorDialog.Operation.ADD)
        );
        return newFaktButton;
    }

    private Component initFaktGridBar() {
        FlexLayout faktGridBar = new FlexLayout();
        faktGridBar.setWidth("100%");
        faktGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        faktGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        faktGridBar.add(
                initFaktGridTitle(),
                new Ribbon(),
                initNewFaktButton()
        );
        return faktGridBar;
    }

    private Component initFaktGrid() {
        faktGrid = new Grid();
        faktGrid.setColumnReorderingAllowed(true);
        faktGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        faktGrid.setId("fakt-grid");
        faktGrid.setClassName("vizman-simple-grid");
        faktGrid.getStyle().set("marginTop", "0.5em");
        faktGrid.setWidth( "100%" );
        faktGrid.setHeight("12em");
//        faktGrid.setHeight(null);

//        zakGrid.getElement().setAttribute("colspan", "2");
//        zakGrid.getStyle().set("padding-right", "0em");
//        zakGrid.getStyle().set("padding-left", "0em");
//        zakGrid.getStyle().set("padding-top", "2.5em");
//        zakGrid.getStyle().set("padding-bottom", "2.5em");

        faktGrid.addColumn(Fakt::getCfakt).setHeader("ČF")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getPlneni).setHeader("Plnění [%]")
                .setFlexGrow(0)
        ;
//        faktGrid.addColumn(Fakt::getCastka).setHeader("Částka [" + getCurrentItem().getMena().name() + "]")
//                .setFlexGrow(0)
//        ;
//        faktGrid.addColumn(Fakt::getZaklad).setHeader("Základ [" + getCurrentItem().getMena().name() + "]")
//                .setFlexGrow(0)
//        ;
        faktGrid.addColumn(Fakt::getDateDuzp).setHeader("DUZP")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getText).setHeader("Text")
                .setFlexGrow(1)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktVystavBtn))
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getDateVystav).setHeader("Vystaveno")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getCastka).setHeader("Částka")
                .setResizable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("10em").setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getZaklad).setHeader("Základ")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getDateTimeExport).setHeader("Exportováno")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktOpenBtn))
                .setFlexGrow(0)
        ;
        return faktGrid;
    }

    private Component buildFaktVystavBtn(Fakt fakt) {
        return new GridFaktVystavBtn(event -> {
//                this.close();
            faktFormDialog.open(
                    fakt, Operation.VYSTAV,
                    "[ Vytvořeno: " + fakt.getDateCreate().toString()
                            + " , Poslední změna: " + fakt.getDatetimeUpdate().toString() + " ]");

//                confirmFaktOpenDialog.open("Otevřít fakturaci ?",
//                        "", "", "Zrušit",
////                        true, zak, this::openZakForm, this::open);
//                        true, fakt, this::openFaktForm, this::open);
        });
    }

    private Component buildFaktOpenBtn(Fakt fakt) {
            return new GridItemOpenBtn(event -> {
//                this.close();
                faktFormDialog.open(
                        fakt, Operation.EDIT,
                        "[ Vytvořeno: " + fakt.getDateCreate().toString()
                                + " , Poslední změna: " + fakt.getDatetimeUpdate().toString() + " ]");

//                confirmFaktOpenDialog.open("Otevřít fakturaci ?",
//                        "", "", "Zrušit",
////                        true, zak, this::openZakForm, this::open);
//                        true, fakt, this::openFaktForm, this::open);
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
