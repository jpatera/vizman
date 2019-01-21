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
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakFormDialog extends AbstractEditorDialog<Zak> implements BeforeEnterObserver {

    private final static String FAKT_VYSTAV_COL_KEY = "fakt-vystav-col";
    private final static String FAKT_EXPORT_COL_KEY = "fakt-export-col";

    final ValueProvider<Fakt, String> castkaProvider;
    final ComponentRenderer<HtmlComponent, Fakt> moneyCellRenderer;

    private TextField czakField;
    private Button zakEvidButton;
    private Checkbox archCheck;
    private TextField textField;
    private TextField skupinaField;
    private TextField honorarField;
    private TextField menaField;

    private Zak zakOrig;
    private Kont parentKont;

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
    private final ConfirmationDialog<ZakDoc> confirmDocUnregisterDialog;
    private ZakEvidFormDialog zakEvidFormDialog;
//    private final ConfirmationDialog<Fakt> confirmFaktOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private ZakService zakService;
    private FaktService faktService;
    private CfgPropsCache cfgPropsCache;


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    public ZakFormDialog(BiConsumer<Zak, Operation> itemSaver,
                         Consumer<Zak> itemDeleter,
                         ZakService zakService,
                         FaktService faktService,
                         CfgPropsCache cfgPropsCache
    ){
        super(true, true, itemSaver, itemDeleter);
        getLeftBarPart().add(initZakEvidButton());

        this.zakService = zakService;
        this.faktService = faktService;
        this.cfgPropsCache = cfgPropsCache;

        this.addOpenedChangeListener(event -> {
            if (Operation.ADD == currentOperation) {
                zakEvidButton.click();
            }
        });

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
        castkaProvider = (fakt) -> VzmFormatUtils.moneyFormat.format(fakt.getCastka());

        moneyCellRenderer = new ComponentRenderer<>(fakt -> {
            Div comp = new Div();
            if ((null != fakt) && (fakt.getCastka().compareTo(BigDecimal.ZERO) < 0)) {
                comp.getStyle()
                        .set("color", "red")
                ;
            }
            comp.setText(VzmFormatUtils.moneyFormat.format(fakt.getCastka()));
            return comp;
        });


        zakEvidFormDialog = new ZakEvidFormDialog(this::saveZakEvid, zakService);
        faktFormDialog = new FaktFormDialog(this::saveFakt, this::deleteFakt, faktService);
        confirmDocUnregisterDialog = new ConfirmationDialog<>();

        getFormLayout().add(
                initCzakSkupinaComponent()
//                , initEvidArchComponent()
//                new Paragraph(" ")
                , initArchCheck()
                , initTextField()
//                , initSkupinaField()
//                , new Paragraph("")
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


//    public void openDialog(Zak item, final Operation operation
//                    , String titleItemNameText, Component zakFaktFlags, String titleEndText) {
//        this.parentKont = parentKont;
//        openDialog(item, parentKont, operation, titleItemNameText, zakFaktFlags, titleEndText);
//    }

    public void openDialog(
            Zak zak, Kont parentKont, Operation operation,
            String titleItemNameText, Component zakFaktFlags, String titleEndText) {

        // Mandatory, should be first
        setItemNames(zak.getTyp());

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

        this.zakOrig = zak;
        this.faktGrid.setItems(zak.getFakts());
        this.docGrid.setItems(zak.getZakDocs());

        this.parentKont = parentKont;
        zakDocFolderField.setParentFolder(parentKont.getFolder());

        getLowerGridCont().setVisible(ItemType.SUB != zak.getTyp());

        openInternal(zak, operation, titleItemNameText, zakFaktFlags, titleEndText);
    }


    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {

//        // Mandatory, should be first
////        setItemNames(getCurrentItem().getTyp());
//
//        this.zakOrig = getCurrentItem();
//
//        // Set locale here, because when it is set in constructor, it is effective only in first open,
//        // and next openings show date in US format
////        datZadComp.setLocale(new Locale("cs", "CZ"));
////        vystupField.setLocale(new Locale("cs", "CZ"));
//
//        faktGrid.setItems(getCurrentItem().getFakts());
//        docGrid.setItems(getCurrentItem().getZakDocs());
    }

    private void saveZakEvid(EvidZak evidZak, Operation operation) {
        getCurrentItem().setCzak(evidZak.getCzak());
        getCurrentItem().setText(evidZak.getText());
        getCurrentItem().setFolder(evidZak.getFolder());
        getBinder().readBean(getCurrentItem());
        formFieldValuesChanged();

        if (Operation.ADD == operation) {
            new OkDialog().open("Evidence zakázky"
                    , "Číslo a text nové zakázky akceptovány"
                    , "Adresáře budou vytvořeny až po uložení zakázky"
            );
        } else {
            new OkDialog().open("Evidence kontraktu"
                    , "Změněné číslo a text zakázky akceptovány"
                    , "Adresáře budou přejmenovány až po uložení zakázky"
            );
        }

//        Notification.show(
////                "User successfully " + operation.getOpNameInText() + "ed.", 3000, Position.BOTTOM_START);
//                "Nové číslo a text zakázky uloženy", 3000, Notification.Position.BOTTOM_END);
////        updateGridContent();
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

    @Override
    protected void confirmDelete() {

        new OkDialog().open("Zrušení zakázky", "Rušení zakázek není implementováno", "");

//        long nodesCount = getCurrentItem().getNodes().size();
//        if (nodesCount > 0) {
//            new OkDialog().open(
//                    "Zrušení zakázky"
//                    , "Zakázku " + getCurrentItem().getCkont() + " nelze zrušit, obsahuje fakturace"
//                    , ""
//            );
//        } else {
//            openConfirmDeleteDialog("Zrušit zakázku ?",
//                    "Opravdu zrušit zakázku “" + getCurrentItem().getCkont() + "“ ?",
//                    "Pokud bude kontrakt zrušen, budou zrušena i další s ním související data.");
////            doDelete(getCurrentItem());
//        }
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
        czakField.setWidth("8em");
        czakField.getStyle()
//                .set("background-color", "yellow")
//                    .set("text-indent", "1em");
                .set("padding-top", "0em");
        getBinder().forField(czakField)
                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .bind(Zak::getCzak, Zak::setCzak);
        return czakField;
    }

    private Component initZakEvidButton() {
        zakEvidButton = new Button("Evidence");
        zakEvidButton.addClickListener(event -> {
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
        return zakEvidButton;
    }

    private Component initCzakSkupinaComponent() {
        FlexLayout czakSkupinaComponent = new FlexLayout();
        czakSkupinaComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        czakSkupinaComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        Width("100%");
        czakSkupinaComponent.add(
                initCzakField()
                , new Ribbon()
                , initSkupinaField()
        );
        return czakSkupinaComponent;
    }

    private Component initArchCheck() {
        archCheck = new Checkbox("Archiv"); // = new TextField("Username");
        archCheck.getElement().setAttribute("theme", "secondary");
        getBinder().forField(archCheck)
                .bind(Zak::getArch, Zak::setArch);
        return archCheck;
    }
//    private Component initEvidArchComponent() {
//        FlexLayout evidArchCont = new FlexLayout();
//        evidArchCont.setAlignItems(FlexComponent.Alignment.BASELINE);
//        evidArchCont.add(
////                initZakEvidButton()
////                , new Ribbon("3em")
//                initArchCheck()
//        );
//        return evidArchCont;
//    }

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
        skupinaField.setWidth("8em");
        czakField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(skupinaField)
                .bind(Zak::getSkupina, Zak::setSkupina);
        skupinaField.setValueChangeMode(ValueChangeMode.EAGER);
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
                .withValidator(new StringLengthValidator(
                        "Honorář nesmí být prázdný",
                        1, null))
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Zak::getHonorar, Zak::setHonorar);
        honorarField.setValueChangeMode(ValueChangeMode.EAGER);
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
                , getProjRootServer()
                , getDocRootServer()
        );
        zakDocFolderField.setWidth("100%");
        zakDocFolderField.getStyle().set("padding-top", "0em");
//        zakDocFolderField.setReadOnly(true);
        getBinder().forField(zakDocFolderField)
                .bind(Zak::getFolder, null);

        return zakDocFolderField;
    }

    private String getProjRootServer() {
        return cfgPropsCache.getValue("app.project.root.server");
    }

    private String getDocRootServer() {
        return cfgPropsCache.getValue("app.document.root.server");
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
        docGrid = new Grid();
        docGrid.setColumnReorderingAllowed(true);
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("doc-grid");
        docGrid.setClassName("vizman-simple-grid");
        docGrid.setHeight("12em");

        docGrid.addColumn(ZakDoc::getFilename).setHeader("Soubor");
        docGrid.addColumn(ZakDoc::getNote).setHeader("Poznámka");
//        docGrid.addColumn("Honorář CZK");
        docGrid.addColumn(ZakDoc::getDateCreate).setHeader("Registrováno");
        docGrid.addColumn(new ComponentRenderer<>(this::buildDocRemoveButton))
                .setFlexGrow(0);
        return docGrid;
    }

    private Component buildDocRemoveButton(ZakDoc kontDoc) {
        return new GridItemOpenBtn(event -> {
            close();
            confirmDocUnregisterDialog.open("Zrušit registraci dokumentu ?",
                    "", "", "Zrušit",
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
        newFaktButton = new NewItemButton(ItemNames.getNomS(ItemType.FAKT), event -> {
            Fakt fakt = new Fakt(getCurrentItem(), ItemType.FAKT);
            faktFormDialog.open(fakt, Operation.ADD, "Fakturace");
        }
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

        faktGrid.addColumn(Fakt::getCfakt).setHeader("ČF")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktOpenBtn))
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getPlneni).setHeader("Plnění [%]")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getDateDuzp).setHeader("DUZP")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getText).setHeader("Text")
                .setFlexGrow(1)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktVystavBtn))
                .setFlexGrow(0)
                .setKey(FAKT_VYSTAV_COL_KEY)
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
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktExportBtn))
                .setFlexGrow(0)
                .setKey(FAKT_EXPORT_COL_KEY)
        ;
        faktGrid.addColumn(Fakt::getDateTimeExport).setHeader("Exportováno ")
                .setFlexGrow(0)
        ;

        return faktGrid;
    }

    private Component buildFaktVystavBtn(Fakt fakt) {
        Button btn = new GridFaktVystavBtn(event -> {
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
//        btn.setEnabled(false);
        return btn;
    }

    private Component buildFaktExportBtn(Fakt fakt) {
        Button btn = new GridFaktExportBtn(event -> {
            faktFormDialog.open(
                    fakt, Operation.VYSTAV,
                    "[ Vytvořeno: " + fakt.getDateCreate().toString()
                            + " , Poslední změna: " + fakt.getDatetimeUpdate().toString() + " ]");
        });
        btn.setEnabled(false);
        return btn;
    }

    private Component buildFaktOpenBtn(Fakt fakt) {
        Button btn = new GridItemOpenBtn(event -> openFaktForm(fakt));
//        btn.setEnabled(false);
        return btn;
    }

    private void openFaktForm(Fakt fakt) {
//        this.close();
        faktFormDialog.open(
            fakt, Operation.EDIT,
            "[ Vytvořeno: " + fakt.getDateCreate().toString()
                    + " , Poslední změna: " + fakt.getDatetimeUpdate().toString() + " ]");
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


}
