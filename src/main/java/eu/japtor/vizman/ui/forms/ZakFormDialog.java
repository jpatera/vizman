package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.DochsumZakService;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakFormDialog extends AbstractEditorDialog<Zak> implements HasLogger {
    //public class ZakFormDialog extends AbstractEditorDialog<Zak> implements BeforeEnterObserver {

    private final static String FAKT_EDIT_COL_KEY = "fakt-edit-col";
    private final static String FAKT_VYSTAV_COL_KEY = "fakt-vystav-col";
    private final static String FAKT_EXPORT_COL_KEY = "fakt-export-col";
    public static final String DIALOG_WIDTH = "1250px";
    public static final String DIALOG_HEIGHT = "760px";

//    final private ValueProvider<Fakt, String> castkaProvider;
    final private ComponentRenderer<HtmlComponent, Fakt> faktPlneniCellRenderer;
    final private ComponentRenderer<HtmlComponent, Fakt> faktCastkaCellRenderer;
    final private ComponentRenderer<HtmlComponent, Fakt> faktZakladCellRenderer;

    private TextField ckontField;
    private TextField czakField;
    private TextField rokField;
//    private Button zakEvidButton;
    private Button akvToZakButton;
    private Checkbox archCheck;
    private TextField textField;
    private TextField skupinaField;
    private TextField honorarField;
    private TextField honorarCistyField;
    private TextField menaField;

//    private Kont kontOrig;
//    private Zak zakOrig;
    private String kontFolder;
    EvidZak evidZakOrig;

    //    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

    private Grid<ZakDoc> docGrid;
//    private HorizontalLayout zakDocFolderComponent;
//    private FormLayout.FormItem zakDocFolderComponent;
    private FlexLayout zakDocFolderComponent;
//    private Paragraph zakFolderField;
    private KzFolderField zakFolderField;
    private Button openDocDirBtn;
    private Button registerDocButton;


    private Grid<Fakt> faktGrid;
    private Button newFaktButton;
    private Button newSubButton;
    private FlexLayout faktGridTitleComponent;
    private Button faktGridResizeBtn;

    private FaktFormDialog faktFormDialog;
    private SubFormDialog subFormDialog;
    private final ConfirmationDialog<ZakDoc> confirmDocUnregisterDialog;
//    private ZakEvidFormDialog zakEvidFormDialog;
//    private final ConfirmationDialog<Fakt> confirmFaktOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private ZakService zakService;
    private FaktService faktService;
    private DochsumZakService dochsumZakService;
    private CfgPropsCache cfgPropsCache;


//    @Override
//    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
//
//    }

    public ZakFormDialog(BiConsumer<Zak, Operation> itemSaver,
                         Consumer<Zak> itemDeleter,
                         ZakService zakService,
                         FaktService faktService,
                         DochsumZakService dochsumZakService,
                         CfgPropsCache cfgPropsCache
    ){
        super(DIALOG_WIDTH, DIALOG_HEIGHT, true, true, itemSaver, itemDeleter, false);
        getFormLayout().setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2),
                new FormLayout.ResponsiveStep("20em", 4)
        );


        getDialogLeftBarPart().add(initAkvToZakButton());

//        getDialogLeftBarPart().add(initZakEvidButton());
//        this.addOpenedChangeListener(event -> {
//            if (Operation.ADD == currentOperation) {
//                zakEvidButton.click();
//            }
//        });

        this.zakService = zakService;
        this.faktService = faktService;
        this.dochsumZakService = dochsumZakService;
        this.cfgPropsCache = cfgPropsCache;


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
//        castkaProvider = (fakt) -> VzmFormatUtils.moneyFormat.format(fakt.getCastka());

        faktPlneniCellRenderer = new ComponentRenderer<>(fakt ->
                VzmFormatUtils.getPercentComponent(fakt.getPlneni())
        );

        faktCastkaCellRenderer = new ComponentRenderer<>(fakt ->
                VzmFormatUtils.getMoneyComponent(fakt.getCastka())
        );

        faktZakladCellRenderer = new ComponentRenderer<>(fakt ->
                VzmFormatUtils.getMoneyComponent(fakt.getZaklad())
        );

//        zakEvidFormDialog = new ZakEvidFormDialog(
//                this::saveZakEvid, zakService
//        );

        faktFormDialog = new FaktFormDialog(
                this::saveFaktForForm, this::deleteFaktForForm, faktService
        );

        subFormDialog = new SubFormDialog(
                this::saveSubForForm, this::deleteSub
        );

        confirmDocUnregisterDialog = new ConfirmationDialog<>();

        getFormLayout().add(
                initCkontField()
                , initCzakField()
                , initRokField()
                , initSkupinaField()
//                , initEvidArchComponent()
//                new Paragraph(" ")
//                , initArchCheck()
                , initTextField()
//                , initSkupinaField()
//                , new Paragraph("")
                , initMenaField()
                , initHonorarField()
                , initHonorarCistyField()
        );

//        getUpperGridContainer().add(new Ribbon(Ribbon.Orientation.VERTICAL, "1em"));
        getUpperGridContainer().add(
                initZakDocFolderComponent()
                , initDocGridBar()
                , initDocGrid()
        );

        getLowerPane().add(
                new Hr()
                , initFaktGridBar()
                , initFaktGrid()
        );
    }


//    public void openDialog(Zak item, final Operation operation
//                    , String titleItemNameText, Component zakFaktFlags, String titleEndText) {
//        this.parentKont = parentKont;
//        openDialog(item, parentKont, operation, titleItemNameText, zakFaktFlags, titleEndText);
//    }

    public void openDialog(
            Zak zak, Operation operation
            , String titleItemNameText, String titleEndText
    ){
        setItemNames(zak.getTyp());

        honorarField.setReadOnly(ItemType.AKV == zak.getTyp());
        akvToZakButton.setVisible(ItemType.AKV == zak.getTyp());

        evidZakOrig = new EvidZak(
                zak.getKontId()
                , zak.getCzak()
                , zak.getText()
                , zak.getFolder()
                , zak.getKontFolder()
        );


        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

        this.kontFolder = zak.getKontFolder();

        this.faktGrid.setItems(zak.getFakts());
        this.docGrid.setItems(zak.getZakDocs());
        this.zakFolderField.setParentFolder(kontFolder);
        this.zakFolderField.setItemType(zak.getTyp());

        getLowerPane().setVisible(ItemType.SUB != zak.getTyp());

        FlexLayout middleComponent = new FlexLayout();
        middleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        middleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        middleComponent.add(
                initArchCheck()
                , new Gap("5em")
                , VzmFormatUtils.buildAvizoComponent(zak.getBeforeTerms(), zak.getAfterTerms(), true)
        );

        openInternal(zak, operation, titleItemNameText
                , middleComponent
                , titleEndText);
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


    public Zak saveZak(Zak zak, Operation operation) {

        try {
            Zak savedZak = zakService.saveZak(zak);

            if (Operation.EDIT == operation) {
                if (null != evidZakOrig.getFolder() && !evidZakOrig.getFolder().equals(zak.getFolder())) {
                    ConfirmDialog
                            .createWarning()
                            .withCaption("Adresáře zakázky")
                            .withMessage("Dokumentové ani projektové adresáře se automaticky nepřejmenovávají.")
                            .open();
                }

//            if (!VzmFileUtils.kontDocRootExists(getDocRootServer(), zak.getFolder())) {
//                new OkDialog().open("Adresáře zakázky"
//                        , "POZOR, dokumentový ani projektový adresář se automaticky nepřejmenovávají.", "");
//            }
//            if (!VzmFileUtils.kontProjRootExists(getProjRootServer(), zak.getFolder())) {
//                new OkDialog().open("Projektový adresáře zakázky"
//                        , "POZOR, projektový adresář zakázky nenalezen, měl by se přejmenovat ručně", "");

            } else if (Operation.ADD == operation) {
                if (StringUtils.isBlank(zak.getKontFolder()) || StringUtils.isBlank(zak.getFolder())) {
                    if (StringUtils.isBlank(zak.getKontFolder())) {
                        ConfirmDialog
                                .createWarning()
                                .withCaption("Adresáře zakázky")
                                .withMessage("Složka kontraktu není definována, nelze vytvořit adresáře zakázky")
                                .open();
                    } else if(StringUtils.isBlank(zak.getFolder())) {
                        ConfirmDialog
                                .createError()
                                .withCaption("Adresáře zakázky")
                                .withMessage("Složka zakázky není zadána, nelze vytvořit adresáře")
                                .open();
                    }
                } else {
                    boolean zakDocDirsOk = VzmFileUtils.createZakDocDirs(
                            cfgPropsCache.getDocRootServer(), zak.getKontFolder(), zak.getFolder());
                    boolean zakProjDirsOk = VzmFileUtils.createZakProjDirs(
                            cfgPropsCache.getProjRootServer(), zak.getKontFolder(), zak.getFolder());
                    //            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
                    //            kontProjRootDir.setReadOnly();
                    String errMsg = null;
                    if (!zakDocDirsOk && !zakProjDirsOk) {
                        errMsg = "Projektové ani dokumentové adresáře se nepodařilo vytvořit";
                    } else if (!zakDocDirsOk) {
                        errMsg = "Dokumentové adresáře se nepodařilo vytvořit";
                    } else if (!zakProjDirsOk) {
                        errMsg = "Projektové adresáře se nepodařilo vytvořit";
                    }
                    if (null != errMsg) {
                        ConfirmDialog
                                .createError()
                                .withCaption("Adresáře zakázky")
                                .withMessage(errMsg)
                                .open();
                    }
                }
            } else {
                getLogger().warn("Saving {}: unknown operation {} appeared", zak.getTyp().name(), operation.name());
            }

            getLogger().info("{} saved: {} / {} [operation: {}]", getCurrentItem().getTyp().name()
                    , getCurrentItem().getCkont(), getCurrentItem().getCzak(), operation.name());
            return savedZak;

        } catch(Exception e) {
            getLogger().error("Error when saving {} {} / {} [operation: {}]", getCurrentItem().getTyp().name()
                    , getCurrentItem().getCkont(), getCurrentItem().getCzak(), operation.name());
            throw e;
        }
    }


//    private void saveZakEvid(EvidZak evidZak, Operation operation) {
//        getCurrentItem().setCzak(evidZak.getCzak());
//        getCurrentItem().setText(evidZak.getText());
//        getCurrentItem().setFolder(evidZak.getFolder());
//        getBinder().readBean(getCurrentItem());
//        formFieldValuesChanged();
//
//        Notification.show("Číslo a text zakázky akceptovány", 2500, Notification.Position.TOP_CENTER);
//    }

    private void saveFaktForForm(Fakt faktToSave, Operation operation) {

        try {
            Fakt savedFakt = faktService.saveFakt(faktToSave);

            if (Operation.ADD == operation) {
                getCurrentItem().getFakts().add(0, savedFakt);
            } else {
                int itemIndex = getCurrentItem().getFakts().indexOf(savedFakt);
                if (itemIndex != -1) {
                    getCurrentItem().getFakts().set(itemIndex, savedFakt);
                }
            }
            faktGrid.setItems(getCurrentItem().getFakts());

            faktGrid.getDataCommunicator().getKeyMapper().removeAll();
            faktGrid.getDataProvider().refreshAll();
            faktGrid.select(savedFakt);
            Notification.show("Záznam fakturace uložen", 2000, Notification.Position.TOP_CENTER);

        } catch(Exception e) {
            getLogger().error("Error when saving {} {} / {} [operation: {}]", getCurrentItem().getTyp().name()
                    , getCurrentItem().getCkont(), getCurrentItem().getCzak(), operation.name());
            throw e;
        }

    }

    private void deleteFaktForForm(Fakt faktToDelete) {
        Fakt faktDel = faktToDelete;
        boolean isDeleted = faktService.deleteFakt(faktToDelete);
        if (!isDeleted) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení fakturačního záznamu.")
                    .withMessage("Fakturační záznam " + faktToDelete.getZakEvid() + " se nepodařilo zrušit.")
                    .open();
        } else {
            getCurrentItem().getFakts().remove(faktToDelete);
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení fakturačního záznamu.")
                    .withMessage("Fakturační záznam " + faktToDelete.getZakEvid() + " zrušen.")
                    .open();
        }
        reloadFaktGridData();
    }

    private void saveSubForForm(Fakt faktToSave, Operation operation) {

        try {
            Fakt savedFakt = faktService.saveFakt(faktToSave);

            if (Operation.ADD == operation) {
                getCurrentItem().getFakts().add(0, savedFakt);
            } else {
                int itemIndex = getCurrentItem().getFakts().indexOf(savedFakt);
                if (itemIndex != -1) {
                    getCurrentItem().getFakts().set(itemIndex, savedFakt);
                }
            }
            faktGrid.setItems(getCurrentItem().getFakts());

            faktGrid.getDataCommunicator().getKeyMapper().removeAll();
            faktGrid.getDataProvider().refreshAll();
            faktGrid.select(savedFakt);
            Notification.show("Subdodávka uložena", 2000, Notification.Position.TOP_CENTER);

        } catch(Exception e) {
            getLogger().error("Error when saving {} {} / {} [operation: {}]", getCurrentItem().getTyp().name()
                    , getCurrentItem().getCkont(), getCurrentItem().getCzak(), operation.name());
            throw e;
        }
    }

    private void deleteSub(Fakt subToDelete) {
//        Fakt subDel = subToDelete;
//        int kontDelIdx = faktGrid.getDataCommunicator().getIndex(kontDel);
//        Stream<KzTreeAware> stream = kzTreeGrid.getDataCommunicator()
//                .fetchFromProvider(kontDelIdx + 1, 1);
//        KzTreeAware newSelectedKont = stream.findFirst().orElse(null);

        boolean isDeleted = faktService.deleteFakt(subToDelete);
        if (!isDeleted) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení subdodávky.")
                    .withMessage("Subdodávku " + subToDelete.getZakEvid() + " se nepodařilo zrušit.")
                    .open();
        } else {
            getCurrentItem().getFakts().remove(subToDelete);
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení subdodávky.")
                    .withMessage("Subdodávka " + subToDelete.getZakEvid() + " zrušena.")
                    .open();

        }
        reloadFaktGridData();
    }

    private void reloadFaktGridData() {
        faktGrid.getDataCommunicator().getKeyMapper().removeAll();
        faktGrid.setItems(getCurrentItem().getFakts());
//        klients = klientService.fetchAll();
//        klientGrid.setDataProvider(new ListDataProvider<>(klients));
        faktGrid.getDataProvider().refreshAll();
    }

    @Override
    protected void confirmDelete() {

//        new OkDialog().open("Zrušení zakázky", "Rušení zakázek není implementováno", "");

        String ckzDel = String.format("%s / %d", getCurrentItem().getCkont(), getCurrentItem().getCzak());
        long nodesCount = getCurrentItem().getFakts().size();
        if (nodesCount > 0) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení zakázky")
                    .withMessage(String.format("Zakázku %s nelze zrušit, obsahuje subdodávky / fakturace .", ckzDel))
                    .open()
            ;
            return;
        }
        Long dochsumZakCount = dochsumZakService.getCountDochsumZak(getCurrentItem().getId());
        if (dochsumZakCount > 0) {
            List<DochsumZak> lastDochsumZaks = dochsumZakService.fetchLatestDochsumZaks(getCurrentItem().getId());
//            if (CollectionUtils.isEmpty(lastDochsumZaks)) {
                ConfirmDialog
                        .createInfo()
                        .withCaption("Zrušení zakázky")
                        .withMessage(String.format("Zakázku %d nelze zrušit, má %d záznamů na proužcích."
                                ,  getCurrentItem().getCzak(), dochsumZakCount))
                        .open()
                ;
//            }
            return;
        }
        openConfirmDeleteDialog("Zrušení zakázky"
                ,String.format("Opravdu zrušit zakázku %s ?", ckzDel)
                ,"Poznámka: Projektové a dokumentové adresáře včetně souborů zůstanou nezměněny."
        );
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
//        statusField.setDataProvider(DataProvider.ofItems(PersonState.values()));
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

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        ckontField.setReadOnly(true);
        ckontField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(ckontField)
                .bind(Zak::getCkont, null);
        return ckontField;
    }

    private Component initCzakField() {
        czakField = new TextField("Číslo zakázky");
        czakField.setReadOnly(true);
        czakField.setWidth("8em");
        czakField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(czakField)
//                .withValidator(new StringLengthValidator(
//                        "Číslo zakázky musí mít 1-4 číslice",
//                        1, 4)
//                )
                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
//                .withValidator(czak ->
//                        ((Operation.EDIT == currentOperation)
//                                && (czak.equals(czakOrig) || (!zakService.zakIdExistsInKont(getCurrentItem().getKontId(), czak)))
//                        )
//                        ||
//                        ((Operation.ADD == currentOperation)
//                                && (!zakService.zakIdExistsInKont(getCurrentItem().getKontId(), czak))
//                        )
//                        , "Toto číslo zakázky již existuje, zvol jiné"
//                )
                .bind(Zak::getCzak, null);
        return czakField;
    }

    private Component initRokField() {
        rokField = new TextField("Rok zakázky");
        rokField.setWidth("8em");
        rokField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(rokField)
                .withConverter(new VzmFormatUtils.IntegerYearConverter("Neplatný formát roku"))
                .bind(Zak::getRok, Zak::setRok);
        return rokField;
    }

    private Component initAkvToZakButton() {
        akvToZakButton = new Button("AKV -> ZAK");
        akvToZakButton.addClickListener(event -> {
            getCurrentItem().setTyp(ItemType.ZAK);
            ConfirmDialog
                    .createInfo()
                    .withCaption("Akvizice -> Zakázka")
                    .withMessage("Akvizice " + getCurrentItem().getCkont() + " / " + getCurrentItem().getCzak() + " převedena na zakázku.")
                    .open();
            getSaveButton().click();
        });

//        akvToZakButton.addClickListener(event -> {
//            EvidZak evidZak = new EvidZak(
//                    getCurrentItem().getKontId()
//                    , getCurrentItem().getCzak()
//                    , getCurrentItem().getText()
//                    , getCurrentItem().getFolder()
//                    , getCurrentItem().getKontFolder()
//            );
//            zakEvidFormDialog.openDialog(
//                    evidZak
//                    , currentOperation
//                    , getDialogTitle(currentOperation, getCurrentItem().getTyp())
//                    , cfgPropsCache.getDocRootServer(), cfgPropsCache.getProjRootServer()
//            );
//            } else {
//                zakEvidFormDialog.openDialog(
//                        evidZak
//                        , Operation.EDIT
//                        , getDialogTitle(Operation.EDIT, getCurrentItem().getTyp())
//                        , getDocRootServer(), getProjRootServer()
//                );
//            }
//        });
        return akvToZakButton;
    }

//    private Component initZakEvidButton() {
//        zakEvidButton = new Button("Evidence");
//        zakEvidButton.addClickListener(event -> {
//            EvidZak evidZak = new EvidZak(
//                    getCurrentItem().getKontId()
//                    , getCurrentItem().getCzak()
//                    , getCurrentItem().getText()
//                    , getCurrentItem().getFolder()
//                    , getCurrentItem().getKontFolder()
//            );
////            if (null == getCurrentItem().getId()) {
//                zakEvidFormDialog.openDialog(
//                        evidZak
//                        , currentOperation
//                        , getDialogTitle(currentOperation, getCurrentItem().getTyp())
//                        , cfgPropsCache.getDocRootServer(), cfgPropsCache.getProjRootServer()
//                );
////            } else {
////                zakEvidFormDialog.openDialog(
////                        evidZak
////                        , Operation.EDIT
////                        , getDialogTitle(Operation.EDIT, getCurrentItem().getTyp())
////                        , getDocRootServer(), getProjRootServer()
////                );
////            }
//        });
//        return zakEvidButton;
//    }

//    private String getDialogTitle(Operation oper, ItemType itemType) {
//        String title;
//        if (Operation.ADD == oper) {
//            if (ItemType.ZAK == itemType) {
//                title = "Nová EVIDENCE ZAKÁZKY";
//            } else if (ItemType.AKV == itemType) {
//                title = "Nová EVIDENCE AKVIZICE";
//            } else if (ItemType.SUB == itemType) {
//                title = "Nová EVIDENCE SUBDODÁVKY";
//            } else {
//                title = "Nová EVIDENCE POLOŽKY";
//            }
//        } else {
//            if (ItemType.ZAK == itemType) {
//                title = "Změna EVIDENCE ZAKÁZKY";
//            } else if (ItemType.AKV == itemType) {
//                title = "Změna EVIDENCE AKVIZICE";
//            } else if (ItemType.SUB == itemType) {
//                title = "Změna EVIDENCE SUBDODÁVKY";
//            } else {
//                title = "Změna EVIDENCE POLOŽKY";
//            }
//        }
//        return title;
//    }


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
        textField.setRequiredIndicatorVisible(true);
        textField.getElement().setAttribute("colspan", "4");

        textField.addValueChangeListener(event -> {
            zakFolderField.setValue(
                    VzmFileUtils.NormalizeDirnamesAndJoin(czakField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        getBinder().forField(textField)
                .withValidator(new StringLengthValidator(
                        "Text zakázky musí mít 3-127 znaků",
                        3, 127)
                )
                .bind(Zak::getText, Zak::setText);
//        textField.setReadOnly(true);
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
            .withConverter(Mena::valueOf, Enum::name)
            .bind(Zak::getMena, null);
        return menaField;
    }

    private Component initHonorarField() {
        honorarField = new TextField("Honorář");
//        honorarField.setReadOnly(true);
        honorarField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarField)
                .asRequired("Honorář nesmí být prázdný")
//                .withValidator(new StringLengthValidator(
//                        "Honorář nesmí být prázdný",
//                        1, null))
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .withValidator(honorar -> null != honorar && honorar.compareTo(BigDecimal.ZERO) >= 0
                        , "Honorář nesmí být záporný")
                .bind(Zak::getHonorar, Zak::setHonorar);
        honorarField.setValueChangeMode(ValueChangeMode.EAGER);
        return honorarField;
    }

    private Component initHonorarCistyField() {
        honorarCistyField = new TextField("Honorář čistý");
//        honorarField.setReadOnly(true);
        honorarCistyField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        honorarCistyField.getStyle()
                .set("font-weight", "bold");
        getBinder().forField(honorarCistyField)
//                .asRequired("Honorář nesmí být prázdný")
//                .withValidator(new StringLengthValidator(
//                        "Honorář nesmí být prázdný",
//                        1, null))
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
//                .withValidator(honorar -> null != honorar && honorar.compareTo(BigDecimal.ZERO) >= 0
//                        , "Honorář nesmí být záporný")
                .bind(Zak::getHonorarCisty, null);
        honorarCistyField.setValueChangeMode(ValueChangeMode.EAGER);
        return honorarCistyField;
    }

    // ----------------------------------------------


    private Component initZakDocFolderComponent() {
        zakDocFolderComponent = new FlexLayout();
        zakDocFolderComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        zakDocFolderComponent.setWidth("100%");
        zakDocFolderComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        zakDocFolderComponent.add(initZakFolderField());
        return zakDocFolderComponent;
    }

    private Component initZakFolderField() {
        zakFolderField = new KzFolderField(
                null
                , ItemType.UNKNOWN
                , cfgPropsCache.getDocRootLocal()
                , cfgPropsCache.getProjRootLocal()
        );
        zakFolderField.setWidth("100%");
        zakFolderField.getStyle().set("padding-top", "0em");
        zakFolderField.setReadOnly(true);
        getBinder().forField(zakFolderField)
                .withValidator(
                        folder ->
//                                ((Operation.ADD == currentOperation) && StringUtils.isNotBlank(folder))
                                // TODO: check add to beta 1.3
                                (StringUtils.isNotBlank(getCurrentItem().getCkont()) && null != getCurrentItem().getCzak())
                                        || (StringUtils.isNotBlank(folder))
                        , "Složka zakázky není definována, je třeba zadat číslo a text zakázky"
                )
                .withValidator(
                    folder ->
                        // TODO: check add to beta 1.3
                        (StringUtils.isBlank(folder)) ||
                        ((Operation.ADD == currentOperation) &&
                                !VzmFileUtils.zakDocRootExists(cfgPropsCache.getDocRootServer(), kontFolder, folder))
                        ||
                        ((Operation.EDIT == currentOperation) &&
                                ((folder.equals(evidZakOrig.getFolder())) ||
                                        !VzmFileUtils.zakDocRootExists(cfgPropsCache.getDocRootServer(), kontFolder, folder))
                        )
                    , "Dokumentový adresář zakázky stejného jména již existuje, změň text zakázky."
                )
                .withValidator(
                    folder ->
                        // TODO: check add to beta 1.3
                        (StringUtils.isBlank(folder)) ||
                        ((Operation.ADD == currentOperation) &&
                                !VzmFileUtils.zakProjRootExists(cfgPropsCache.getProjRootServer(), kontFolder, folder))
                        ||
                        ((Operation.EDIT == currentOperation) &&
                                ((folder.equals(evidZakOrig.getFolder())) ||
                                        !VzmFileUtils.zakProjRootExists(cfgPropsCache.getProjRootServer(), kontFolder, folder))
                        )
                    , "Projektový adresář zakázky stejného jména již existuje, změň text zakázky."
                )

                .bind(Zak::getFolder, Zak::setFolder);

        return zakFolderField;
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
//        docGrid.setWidth( "100%" );
//        docGrid.setHeight( null );
        docGrid.setHeight("3em");
        docGrid.setColumnReorderingAllowed(true);
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("zak-doc-grid");
        docGrid.setClassName("vizman-simple-grid");

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

    private Component initFaktGridTitleComponent() {
        faktGridTitleComponent = new FlexLayout(
                initFaktGridResizeBtn()
                , initFaktGridTitle()
        );
        faktGridTitleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        return faktGridTitleComponent;
    }

    private Component initFaktGridResizeBtn() {
        faktGridResizeBtn = new ResizeBtn(getLowerPaneResizeAction(), true);
        return faktGridResizeBtn;
    }

    private Component initFaktGridTitle() {
        H4 faktTitle = new H4();
        faktTitle.setText("PLATEBNÍ KALENDÁŘ");
        faktTitle.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-right", "1em");
//        mainTitle.getElement().setProperty("flexGrow", (double)1);
        return faktTitle;
    }

    private Component initNewFaktButton() {
        newFaktButton = new NewItemButton(ItemNames.getNomS(ItemType.FAKT), event -> {
//            Fakt fakt = new Fakt(ItemType.FAKT, getCurrentItem().getNewCfakt(), getCurrentItem());
//            faktFormDialog.open(fakt, Operation.ADD, "Fakturace");
            faktFormDialog.openDialog(new Fakt(ItemType.FAKT, getCurrentItem().getNewCfakt(), getCurrentItem())
                    , getCurrentItem(), Operation.ADD, null, null, null);
        });
        return newFaktButton;
    }

    private Component initNewSubButton() {
        newSubButton = new NewItemButton(ItemNames.getNomS(ItemType.SUB), event -> {
//            Fakt fakt = new Fakt(ItemType.FAKT, getCurrentItem().getNewCfakt(), getCurrentItem());
//            faktFormDialog.open(fakt, Operation.ADD, "Fakturace");
            subFormDialog.openDialog(new Fakt(ItemType.SUB, getCurrentItem().getNewCfakt(), getCurrentItem())
                    , getCurrentItem(), Operation.ADD, null, null, null);
        });
        return newSubButton;
    }



    private Component initFaktGridBar() {
        HorizontalLayout faktGridBar = new HorizontalLayout();
//        FlexLayout faktGridBar = new FlexLayout();
        faktGridBar.setSpacing(false);
        faktGridBar.setPadding(false);
        faktGridBar.getStyle().set("margin-left", "-3em");
//        faktGridBar.setWidth("100%");
        faktGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        faktGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        faktGridBar.add(
                initFaktGridTitleComponent(),
                new Ribbon(),
                new FlexLayout(
                    initNewFaktButton(),
                    new Ribbon(),
                    initNewSubButton()
                )
        );
        return faktGridBar;
    }

    private Component initFaktGrid() {
        faktGrid = new Grid<>();
        faktGrid.setHeight("3em");
//        faktGrid.getElement().setProperty("flexGrow", (double)0);
//        alignSelf auto
//        align items stretch
//        zakGrid.setHeight(null);
//        faktGrid.setWidth( "100%" );
        faktGrid.setColumnReorderingAllowed(true);
        faktGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        faktGrid.setId("zak-fakt-grid");
        faktGrid.setClassName("vizman-simple-grid");
        faktGrid.getStyle().set("marginTop", "0.5em");

        faktGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getItemTypeColoredTextComponent))
                .setHeader("Typ")
                .setWidth("5em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        faktGrid.addColumn(Fakt::getCfakt)
                .setHeader("ČF/ČS")
                .setWidth("3.5em")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktEditBtn))
                .setHeader("Edit")
                .setWidth("3em")
                .setFlexGrow(0)
                .setKey(FAKT_EDIT_COL_KEY)
        ;
        faktGrid.addColumn(faktPlneniCellRenderer)
                .setHeader("Plnění [%]")
                .setWidth("5em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
        ;
//        faktGrid.addColumn(Fakt::getDateDuzp)
        faktGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getDuzpComponent))
                .setHeader("DUZP")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getColoredTextComponent))
                .setHeader("Text")
                .setFlexGrow(1)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFakStornoBtn))
                .setHeader("Fakt!")
                .setWidth("3em")
                .setFlexGrow(0)
                .setKey(FAKT_VYSTAV_COL_KEY)
        ;
        faktGrid.addColumn(faktCastkaCellRenderer)
                .setHeader("Částka")
                .setResizable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("10em")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(Fakt::getDateVystav)
                .setHeader("Fakturováno")
                .setFlexGrow(0)
        ;
//        faktGrid.addColumn(faktZakladCellRenderer)
//                .setHeader("Základ")
//                .setResizable(true)
//                .setTextAlign(ColumnTextAlign.END)
//                .setWidth("10em")
//                .setFlexGrow(0)
//        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildExportBackBtn))
                .setHeader("Exp!")
                .setFlexGrow(0)
                .setKey(FAKT_EXPORT_COL_KEY)
        ;
        faktGrid.addColumn(Fakt::getDateTimeExport)
                .setHeader("Exportováno")
                .setFlexGrow(0)
        ;

        return faktGrid;
    }

    private Component buildFaktEditBtn(Fakt fakt) {
        if (ItemType.FAKT == fakt.getTyp()) {
            Button faktEditBtn = new GridItemEditBtn(event ->
                    faktFormDialog.openDialog(fakt, getCurrentItem()
                            , Operation.EDIT, null, null, null
                    )
                    , VzmFormatUtils.getItemTypeColorName(fakt.getTyp()));
            return faktEditBtn;
        } else {
            Button subEditBtn = new GridItemEditBtn(event ->
                    subFormDialog.openDialog(fakt, getCurrentItem()
                            , Operation.EDIT, null, null, null
                    )
                    , VzmFormatUtils.getItemTypeColorName(fakt.getTyp()));
            return subEditBtn;
        }
    }

    private Component buildFakStornoBtn(Fakt fakt) {
        boolean isFakturovano = fakt.isFakturovano();
        if (ItemType.FAKT == fakt.getTyp()) {
            Button fakStornoBtn = new GridFakturovatBtn(event -> {
                //                this.close();
                if (isFakturovano) {
                    faktFormDialog.openDialog(
                            fakt, getCurrentItem(), Operation.STORNO
                            , null, null, null
                    );
                } else {
                    if (null == fakt.getPlneni() || fakt.getPlneni().compareTo(BigDecimal.ZERO) <= 0) {
                        ConfirmDialog.createInfo()
                                .withCaption("Fakturace")
                                .withMessage("Nelze fakturovat, není zadáno plnění")
                                .open();
                    } else if (null != fakt.getDateTimeExport()) {
                        ConfirmDialog.createInfo()
                                .withCaption("Fakturace")
                                .withMessage("Nelze fakturovat, již bylo exportováno")
                                .open();

                    } else {
                        faktFormDialog.openDialog(
                                fakt, getCurrentItem(), Operation.FAKTUROVAT
                                , null, null, null
                        );
                    }
                }
            }, isFakturovano);

            return fakStornoBtn;
        } else {
            return new Span();
        }
    }

    private Component buildExportBackBtn(Fakt fakt) {

        if (ItemType.FAKT == fakt.getTyp()) {
            Button exportBackBtn = new GridFaktExportBtn(event -> {
                //            Fakt fakt = new Fakt(ItemType.FAKT, getCurrentItem().getNewCfakt(), getCurrentItem());
                //            faktFormDialog.open(fakt, Operation.ADD, "Fakturace");
                faktFormDialog.openDialog(
                        fakt, getCurrentItem(), Operation.EXPORT
                        , null, new FlexLayout(), null
                );
            });
            exportBackBtn.setEnabled(false);
            return exportBackBtn;
        } else {
            return new Span();
        }
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
