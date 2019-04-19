package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.bean.EvidKont;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.fsdataprovider.FilesystemData;
import eu.japtor.vizman.fsdataprovider.FilesystemDataProvider;
import eu.japtor.vizman.ui.components.*;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog <T extends Serializable> extends AbstractKzDialog implements HasLogger {
//public class KontFormDialog extends AbstractEditorDialog<Kont> implements HasLogger {
//public class KontFormDialog extends AbstractEditorDialog<Kont> implements BeforeEnterObserver {

    private final static String ZAK_EDIT_COL_KEY = "zak-edit-col";
    private final static String DELETE_STR = "Zrušit";
    private final static String SAVE_STR = "Uložit";

//    final ValueProvider<Zak, String> honorProvider;
//    final ValueProvider<Zak, String> yearProvider;
    private final ComponentRenderer<HtmlComponent, Zak> zakHonorarCellRenderer =
        new ComponentRenderer<>(zak ->
            VzmFormatUtils.getMoneyComponent(zak.getHonorar())
    );

    private TextField ckontField;
    private TextField rokField;
//    private Button kontEvidButton;
    private Checkbox archCheck;
    private TextField objednatelField;
    private TextField investorField;
    private TextField textField;
    private TextField honorarField;
    private TextField honorarCistyField;
    private ComboBox<Mena> menaCombo;
    private ComboBox<Klient> objednatelCombo;

//    private String kontFolderOrig;
//    private Kont kontOrig;
//    private String kontFolderOrig;
//    private Zak zakOrig;
//    private String ckontOrig;
//    private String textOrig;
//    private String folderOrig;

//    private TextField menaField = new TextField("Měna");

//    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

//    private Grid<Zak> zakazkyGrid;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

    private FlexLayout kontDocFolderComponent;
    private KzFolderField kontFolderField;
    private Grid<KontDoc> docGrid;
    private Button registerDocButton;

    private Grid<Zak> zakGrid;
    private Button newZakButton;
    private Button newAkvButton;
    private FlexLayout zakGridTitleComponent;
//    private Button zakGridResizeBtn;
    private ComponentRenderer<Component, Zak> zakArchRenderer;
    private ComponentRenderer<HtmlComponent, Zak> zakTextRenderer;
    private ComponentRenderer<Component, Zak> avizoRenderer;

    EvidKont evidKontOrig;
//    private KontEvidFormDialog kontEvidFormDialog;
    private ZakFormDialog zakFormDialog;
//    private SubFormDialog subFormDialog;
    private ConfirmationDialog<KontDoc> confirmDocUnregisterDialog;
//    private final ConfirmationDialog<Zak> confirmZakOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private KontService kontService;
    private ZakService zakService;
    private FaktService faktService;
    private KlientService klientService;
    private List<Klient> listOfKlients;
    private CfgPropsCache cfgPropsCache;

////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;



    private GrammarGender itemGender;
    private String itemTypeNomS;
    private String itemTypeGenS;
    private String itemTypeAccuS;


    private HorizontalLayout buttonBar;
    private Button saveButton;
    private Button revertButton;
    private Button closeButton;
    private Button deleteButton;
    private HorizontalLayout leftBarPart;

//    private Binder<T> binder = new Binder<>();
//    private T currentItem;
    private Binder<Kont> binder = new Binder<>();
    private Kont currentItem;

    protected Operation currentOperation;
    private boolean closeAfterSave;
    private Registration registrationForSave;
//    private BiConsumer<T, Operation> itemSaver;
//    private Consumer<T> itemDeleter;
//    private BiConsumer<Kont, Operation> itemSaver;
    private Consumer<Kont> newItemSaver;
    private Consumer<Kont> modItemSaver;
    private Consumer<Kont> itemDeleter;
    private Consumer<Kont> formCloser;




    public KontFormDialog(
//            BiConsumer<Kont, Operation> kontSaver,
                          Consumer<Kont> kontDeleter,
//                          Consumer<Kont> kontFormCloser,
//                          BiConsumer<Zak, Operation> zakSaver,
//                          Consumer<Kont> zakDeleter,
                          KontService kontService,
                          ZakService zakService,
                          FaktService faktService,
                          KlientService klientService,
                          DochsumZakService dochsumZakService,
                          CfgPropsCache cfgPropsCache
    ){
        super("1300px", "800px", true, true);

//        this.dialogWidth = "1300px";
//        this.dialogHeight = "800px";
//        setWidth(dialogWidth);
//        setHeight(dialogHeight);
//        boolean useUpperRightPane = true;
//        boolean useLowerPane  = true;

//        this.newItemSaver = saveKont();
//        this.modItemSaver = this::saveKont;
        this.itemDeleter = kontDeleter;
//        this.formCloser = kontFormCloser;

        this.closeAfterSave = false;

        this.kontService = kontService;
        this.zakService = zakService;
        this.klientService = klientService;
        this.cfgPropsCache = cfgPropsCache;

        getFormLayout().add(
                initCkontField()
                , initRokField()
//                , initArchCheck()
                , initTextField()
//                , initObjednatelField()
                , initObjednatelCombo()   // Becauise of a bug -> call it in OpenDialog
                , initInvestorField()
                , initMenaCombo()
                , initHonorarField()
                , initHonorarCistyField()
        );

        getUpperRightPane().add(
                initKontDocFolderComponent()
                , initDocGridBar()
                , initDocGrid()
        );

        getLowerPane().add(
                new Hr()
                , initZakGridBar()
                , initZakGrid()
        );

//        this.getElement().getStyle().set("display", "flex");
//        getElement().getStyle().set("flex-direction", "column");
////        getElement().getStyle().set("flex","auto");


//        this.getDialogLeftBarPart().add(initKontEvidButton());
//        this.addOpenedChangeListener(event -> {
//            if (Operation.ADD == currentOperation) {
//                kontEvidButton.click();
//            }
//        });

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

//        honorProvider = (zak) -> VzmFormatUtils.moneyFormat.format(zak.getHonorar());
//        yearProvider = (zak) -> VzmFormatUtils.yearFormat.format(zak.getRok());


//        kontEvidFormDialog = new KontEvidFormDialog(this::saveKontEvid, kontService);
        zakFormDialog = new ZakFormDialog(
                this::saveZakForForm, this::deleteZakForForm
                , zakService, faktService, dochsumZakService, cfgPropsCache
        );
//        subFormDialog = new SubFormDialog(
//                this::saveZakForForm, this::deleteZakForForm
//                , zakService, faktService, cfgPropsCache
//        );
//        zakFormDialog.getDialogLeftBarPart().add(initKontEvidButton());


        confirmDocUnregisterDialog = new ConfirmationDialog<>();

        activateListeners();
    }


    private void activateListeners() {
        // Must be set only after upper kontFolderField, ckontField and textField are initialized
        textField.addValueChangeListener(event -> {
            kontFolderField.setValue(
                    VzmFileUtils.NormalizeDirnamesAndJoin(ckontField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        ckontField.addValueChangeListener(event -> {
            kontFolderField.setValue(
                    VzmFileUtils.NormalizeDirnamesAndJoin(event.getValue(), textField.getValue())
            );
        });
        ckontField.setValueChangeMode(ValueChangeMode.EAGER);
    }



//    protected final T getCurrentItem() {
    public final Kont getCurrentItem() {
        return currentItem;
    }

    public final Operation getCurrentOperation() {
        return currentOperation;
    }


    /**
     * Gets the binder.
     *
     * @return the binder
     */
//    protected final Binder<T> getBinder() {
    protected final Binder<Kont> getBinder() {
        return binder;
    }


    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

        saveButton = new Button("Uložit");
        saveButton.setAutofocus(true);
        saveButton.getElement().setAttribute("theme", "primary");

        deleteButton = new Button("Zrušit");
        deleteButton.getElement().setAttribute("theme", "error");
        deleteButton.addClickListener(e -> deleteClicked());

        revertButton = new Button("Vrátit změny");
        revertButton.addClickListener(e -> revertClicked());

        closeButton = new Button("Zavřít");
        closeButton.addClickListener(e -> close());

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(
                saveButton
                , deleteButton
                , revertButton
        );

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(closeButton);

//        buttonBar.getStyle().set("margin-top", "0.2em");
        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(leftBarPart, rightBarPart);
        bar.setClassName("buttons");
//        buttonBar.setSpacing(true);

        return bar;
    }

    private void deleteClicked() {
// TODO: to be or not to be?
//        if (confirmDialog.getElement().getParent() == null) {
//            getUI().ifPresent(ui -> ui.add(confirmDialog));
//        }
        confirmDelete();
    }

    private void revertClicked() {
// TODO: to be or not to be?
//        if (confirmDialog.getElement().getParent() == null) {
//            getUI().ifPresent(ui -> ui.add(confirmDialog));
//        }
        binder.removeBean();
        binder.readBean(currentItem);
    }

    public void setItemNames(ItemType itemType) {
        this.itemGender = ItemNames.getItemGender(itemType);
        this.itemTypeNomS = ItemNames.getNomS(itemType);
        this.itemTypeGenS = ItemNames.getGenS(itemType);
        this.itemTypeAccuS = ItemNames.getAccuS(itemType);
    }





    public void openDialog(
            Kont kont, Operation operation, String titleEndText
    ){

        currentOperation = operation;
        currentItem = kont;

        setDefaultItemNames();  // Set general default names
        evidKontOrig = new EvidKont(
                currentItem.getCkont()
                , currentItem.getText()
                , currentItem.getFolder()
        );
        setControlsForItemAndOperation(currentItem, currentOperation);

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

        if (Operation.ADD == operation) {
            kont.setRok(LocalDate.now().getYear());
            kont.setTyp(ItemType.KONT);
        }

        this.zakGrid.setItems(kont.getNodes());
        this.docGrid.setItems(kont.getKontDocs());
        this.kontFolderField.setParentFolder(null);
        this.kontFolderField.setItemType(kont.getTyp());

        this.listOfKlients = klientService.fetchAll();
        // Following series of commands replacing combo box is because of a bug
        // Initialize $connector if values were not set in ComboBox element prior to page load. #188
        getFormLayout().remove(objednatelCombo);
        getFormLayout().addComponentAtIndex(3, initObjednatelCombo());
        objednatelCombo.setItems(this.listOfKlients);

//        this.objednatelCombo.setItems(new ArrayList<>());
//        this.objednatelCombo.setItems(listOfKlients);


        FlexLayout headerMiddleComponent = new FlexLayout();
        headerMiddleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerMiddleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerMiddleComponent.add(
                initArchCheck()
                , new Gap("5em")
                , VzmFormatUtils.buildAvizoComponent(kont.getBeforeTerms(), kont.getAfterTerms(), true)
        );
        getMiddleComponentBox().removeAll();
        if (null != headerMiddleComponent) {
            getMiddleComponentBox().add(headerMiddleComponent);
        }

        getHeaderEndComponent().setText(getHeaderEndComponentValue(titleEndText));

        this.open();
    }

    //    protected void openInternal(T item, final Operation operation
    protected void setControlsForItemAndOperation(final Kont item, final Operation operation) {

        setItemNames(item.getTyp());

        deleteButton.setText(DELETE_STR + " " + itemTypeAccuS.toLowerCase());
        saveButton.setText(SAVE_STR + " " + itemTypeAccuS.toLowerCase());

        getMainTitle().setText(operation.getDialogTitle(getItemName(operation), itemGender));

        if (getCurrentItem() instanceof HasItemType) {
            getHeaderDevider().getStyle().set(
                    "background-color", VzmFormatUtils.getItemTypeColorBrighter(((HasItemType) item).getTyp()));
        }

        if (operation == Operation.ADD) {
            binder.removeBean();
            binder.readBean(item);
        } else {
            binder.removeBean();
            binder.readBean(item);
        }

        if (registrationForSave != null) {
            registrationForSave.remove();
        }
        registrationForSave = saveButton.addClickListener(e -> saveClicked(operation));
//        saveButton.setEnabled(false);

        deleteButton.setEnabled(operation.isDeleteEnabled());
    }



    public void setDefaultItemNames() {
        setItemNames(ItemType.UNKNOWN);
    }

    private String getItemName(final Operation operation) {
        switch (operation) {
            case ADD : return itemTypeNomS;
            case EDIT : return itemTypeGenS;
            case DELETE : return itemTypeAccuS;
            case FAKTUROVAT: return itemTypeAccuS;
            case EXPORT : return itemTypeAccuS;
            default : return itemTypeNomS;
        }
    }

    private String getHeaderEndComponentValue(final String titleEndText) {
        String value = "";
        if ((null == titleEndText) && (currentItem instanceof HasModifDates)) {
            if (currentOperation == Operation.ADD) {
                value = "";
            } else {
                LocalDate dateCreate = ((HasModifDates) currentItem).getDateCreate();
                String dateCreateStr = null == dateCreate ? "" : dateCreate.format(VzmFormatUtils.basicDateFormatter);
                LocalDateTime dateTimeUpdate = ((HasModifDates) currentItem).getDatetimeUpdate();
                String dateUpdateStr = null == dateTimeUpdate ? "" : dateTimeUpdate.format(VzmFormatUtils.titleModifDateFormatter);
                value = "[ Vytvořeno: " + dateCreateStr + ", Změna: " + dateUpdateStr + " ]";
            }
        }
        return value;
    }




//    /**
//     * Called by abstract parent dialog from its open(...) method.
//     */
//    protected void openSpecific() {
//
//        // Mandatory, should be first
////        setItemNames(getCurrentItem().getTyp());
//
//
//        // Set locale here, because when it is set in constructor, it is effective only in first open,
//        // and next openings show date in US format
////        datZadComp.setLocale(new Locale("cs", "CZ"));
////        vystupField.setLocale(new Locale("cs", "CZ"));
//
////        zakGrid.setItems(getCurrentItem().getNodes());
////        docGrid.setItems(getCurrentItem().getKontDocs());
////        kontFolderField.setParentFolder(null);
////        kontFolderText.setText(getCurrentItem().getFolder());
//    }

    private void saveClicked(Operation operation) {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (isValid) {
//            itemSaver.accept(currentItem, operation);
//            itemSaver.accept(getCurrentItem(), operation);
            saveKont(getCurrentItem(), operation);
            if (closeAfterSave) {
                close();
            }
        } else {
//            BinderValidationStatus<T> status = binder.validate();
            BinderValidationStatus<Kont> status = binder.validate();
        }
    }

    //    @Override
    protected void confirmDelete() {

        String ckDel = String.format("%s", getCurrentItem().getCkont());
        long nodesCount = getCurrentItem().getNodes().size();
        if (nodesCount > 0) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení kontraktu")
                    .withMessage("Kontrakt " + getCurrentItem().getCkont() + " nelze zrušit, obsahuje zakázky / akvizice")
                    .open()
            ;
            return;
        }
        ConfirmDialog
                .createQuestion()
                .withCaption("Zrušení kontraktu")
//                .withMessage("Opravdu zrušit?")
                .withMessage("Zrušit kontrakt " + getCurrentItem().getCkont() + " ?")
//                .with...(,"Poznámka: Projektové a dokumentové adresáře včetně souborů zůstanou nezměněny.")
                .withOkButton(() -> {
                            deleteItemConfirmed(getCurrentItem());
                        }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                )
                .withCancelButton(ButtonOption.caption("ZPĚT"))
                .open()
        ;

    }



    protected final void openConfirmDeleteDialog(String title, String message,
                                                 String additionalMessage) {
//        close();
//        confirmationDialog.open(title, message, additionalMessage, "Zrušit",
//                true, getCurrentItem(), this::deleteItemConfirmed, this::open);


        ConfirmDialog
                .createQuestion()
                .withCaption(title)
                .withMessage("Opravdu zrušit?")
                .withOkButton(() -> {
                            deleteItemConfirmed(getCurrentItem());
                        }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                )
                .withCancelButton(ButtonOption.caption("ZPĚT"))
                .open()
        ;
    }

//    private void deleteItemConfirmed(T item) {
    private void deleteItemConfirmed(Kont item) {
        doDelete(item);
    }

    /**
     * Removes the {@code item} from the backend and close the dialog.
     *
     * @param item
     *            the item to delete
     */
//    protected void doDelete(T item) {
    protected void doDelete(Kont item) {
        itemDeleter.accept(item);
        this.close();
    }





    public Kont saveKont(Kont kont, Operation operation) {

        try {
            Kont kontSaved = kontService.saveKont(kont);
            currentItem = kontSaved;

            if (Operation.EDIT == operation) {
                if (null != evidKontOrig.getFolder() && !evidKontOrig.getFolder().equals(kont.getFolder())) {
                    ConfirmDialog
                            .createWarning()
                            .withCaption("Adresáře kontraktu")
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

            } else if (Operation.ADD == operation){
                if (StringUtils.isBlank(kont.getFolder())) {
                    ConfirmDialog
                            .createError()
                            .withCaption("Adresáře kontraktu")
                            .withMessage("Složka kontraktu není zadána, nelze vytvořit adresáře")
                            .open();
                } else {
                    boolean kontDocDirsOk = VzmFileUtils.createKontDocDirs(
                            cfgPropsCache.getDocRootServer(), kont.getFolder());
                    boolean kontProjDirsOk = VzmFileUtils.createKontProjDirs(
                            cfgPropsCache.getProjRootServer(), kont.getFolder());
                    String errMsg = null;
                    if (!kontDocDirsOk && !kontProjDirsOk) {
                        errMsg = "Projektové ani dokumentové adresáře se nepodařilo vytvořit";
                    } else if (!kontDocDirsOk) {
                        errMsg = "Dokumentové adresáře se nepodařilo vytvořit";
                    } else if (!kontProjDirsOk) {
                        errMsg = "Projektové adresáře se nepodařilo vytvořit";
                    }
                    if (null != errMsg) {
                        ConfirmDialog
                                .createError()
                                .withCaption("Adresáře kontraktu")
                                .withMessage(errMsg)
                                .open();
                    }
                    //            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
                    //            kontProjRootDir.setReadOnly();
                }
            } else {
                getLogger().warn("Saving {}: unknown operation {} appeared", kont.getTyp().name(), operation.name());
            }

            getLogger().info("{} saved: {} / {} [operation: {}]", getCurrentItem().getTyp().name()
                    , getCurrentItem().getCkont(), getCurrentItem().getCzak(), operation.name());
            return kontSaved;

        } catch(Exception e) {
            getLogger().error("Error when saving {} {} / {} [operation: {}]", getCurrentItem().getTyp().name()
                    , getCurrentItem().getCkont(), getCurrentItem().getCzak(), operation.name());
            throw e;
        }
    }


//    private void saveKontEvid(EvidKont evidKont, Operation operation) {
//        getCurrentItem().setCkont(evidKont.getCkont());
//        getCurrentItem().setText(evidKont.getText());
//        getCurrentItem().setFolder(evidKont.getFolder());
//        getBinder().readBean(getCurrentItem());
//        formFieldValuesChanged();
//
//        Notification.show("Číslo a text kontraktu akceptovány", 2500, Notification.Position.TOP_CENTER);
//    }

    private void saveZakForForm(Zak zak, Operation operation) {

        Zak savedZak = zakFormDialog.saveZak(zak, operation);

//        if (Operation.EDIT == operation && null != zakFolderOrig && !zakFolderOrig.equals(zak.getFolder())) {
//            if (!VzmFileUtils.kontDocRootExists(getDocRootServer(), zak.getFolder())) {
//                new OkDialog().open("Adresáře zakázky"
//                        , "POZOR, dokumentový ani projektový adresář se automaticky nepřejmenovávají.", "");
//            }
//            if (!VzmFileUtils.kontProjRootExists(getProjRootServer(), zak.getFolder())) {
//                new OkDialog().open("Projektový adresáře zakázky"
//                        , "POZOR, projektový adresář zakázky nenalezen, měl by se přejmenovat ručně", "");
//            }
//        } else if (Operation.ADD == operation){
//            if (!VzmFileUtils.createZakDocDirs(
//                        cfgPropsCache.getDocRootServer(), zak.getKontFolder(), zak.getFolder())) {
//                new OkDialog().open("Dokumentové adresáře zakázky"
//                        , "Adresářovou strukturu se nepodařilo vytvořit", "");
//            };
//            if (!VzmFileUtils.createZakProjDirs(
//                        cfgPropsCache.getProjRootServer(), zak.getKontFolder(), zak.getFolder())) {
//                new OkDialog().open("Projektové adresáře zakázky"
//                        , "Adresářovou strukturu se nepodařilo vytvořit", "");
//            };
//    //            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
//    //            kontProjRootDir.setReadOnly();
//        } else {
//            new OkDialog().open("Adresáře zakázky"
//                    , "NEZNÁMÁ OPERACE", "")
//            ;
//        }
//
//        Zak savedZak = zakService.saveZak(zak);
//        new OkDialog().open("Zakázka " + savedZak.getKont().getCkont() + " / " + savedZak.getCzak() + " uložena"
//                , "", "");

        Notification.show("Zakázka " + savedZak.getKont().getCkont() + " / " + savedZak.getCzak() + " uložena"
                , 2500, Notification.Position.TOP_CENTER);


//        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
        if (Operation.ADD == operation) {
            getCurrentItem().getZaks().add(0, savedZak);
        } else {
            int zakItemIndex = getCurrentItem().getZaks().indexOf(savedZak);
            if (zakItemIndex != -1) {
                getCurrentItem().getZaks().set(zakItemIndex, savedZak);
            }
        }
        zakGrid.setItems(getCurrentItem().getZaks());

        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
        zakGrid.getDataProvider().refreshAll();
        zakGrid.select(savedZak);

//        Notification.show(
//                "Změny zakázky uloženy", 3000, Notification.Position.TOP_CENTER);
    }

    private void deleteZakForForm(Zak zak) {
        String ckzDel = String.format("%s / %d", zak.getCkont(), zak.getCzak());
        try {
            boolean zakWasDeleted = zakService.deleteZak(zak);

            if (!zakWasDeleted) {
                ConfirmDialog
                        .createError()
                        .withCaption("Zrušení zakázky")
                        .withMessage(String.format("Chyba při rušení zakázky %s .", ckzDel))
                        .open();
            } else {
                getCurrentItem().getZaks().removeIf(z -> z.getId().equals(zak.getId()));
                zakGrid.setItems(getCurrentItem().getZaks());
                zakGrid.getDataCommunicator().getKeyMapper().removeAll();
                zakGrid.getDataProvider().refreshAll();
                getLogger().info(String.format("ZAKAZKA %s deleted", ckzDel));
                Notification.show(String.format("Zakázka %s zrušena.", ckzDel)
                        , 2500, Notification.Position.TOP_CENTER)
                ;
                ConfirmDialog
                        .createInfo()
                        .withCaption("Zrušení zakázky")
                        .withMessage(String.format("Zakázka %s byla zrušena.", ckzDel))
                        .open()
                ;
            }
        } catch (Exception e) {
            getLogger().error(String.format("Error during deletion ZAKAZKA %s / %d", zak.getCkont(), zak.getCzak()), e);
            ConfirmDialog
                    .createError()
                    .withCaption("Zrušení zakázky")
                    .withMessage(String.format("Chyba při rušení zakázky %s .", ckzDel))
                    .open()
            ;
        }
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
//        statusField.setDataProvider(DataProvider.ofItems(PersonState.values()));
//        getFormLayout().add(statusField);
//        getBinder().forField(statusField)
////                .withConverter(
////                        new StringToIntegerConverter("Must be a number"))
//                .bind(Person::getStatus, Person::setStatus);
//    }

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        ckontField.setRequiredIndicatorVisible(true);
        ckontField.setPlaceholder("XXXXX.X-[1|2]");
        ckontField.getStyle()
                .set("padding-top", "0em")
        ;
        getBinder().forField(ckontField)
                .withValidator(ckont -> {return ckont.matches("^[0-9]{5}\\.[0-9]-[1-2]$"); }
                        , "Je očekáván formát XXXXX.X-[1|2].")
                .bind(Kont::getCkont, Kont::setCkont)
        ;
        return ckontField;
    }

    private Component initRokField() {
        rokField = new TextField("Rok kontraktu");
        rokField.setWidth("8em");
        rokField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(rokField)
                .withConverter(new VzmFormatUtils.IntegerYearConverter("Neplatný formát roku"))
                .bind(Kont::getRok, Kont::setRok);
        return rokField;
    }

//    private Component initKontEvidButton() {
//        kontEvidButton = new Button("Evidence");
//        kontEvidButton.addClickListener(event -> {
//            EvidKont evidKont = new EvidKont(
//                    getCurrentItem().getCkont()
//                    , getCurrentItem().getText()
//                    , getCurrentItem().getFolder()
//            );
////            if (null == getCurrentItem().getId()) {
//                kontEvidFormDialog.openDialog(
//                        evidKont
//                        , currentOperation
//                        , getDialogTitle(currentOperation, getCurrentItem().getTyp())
//                        , cfgPropsCache.getDocRootServer(), cfgPropsCache.getProjRootServer()
//                );
////            } else {
////                kontEvidFormDialog.openDialog(
////                        evidKont
////                        , Operation.EDIT
////                        , getDialogTitle(Operation.EDIT, getCurrentItem().getTyp())
////                        , getDocRootServer(), getProjRootServer()
////                );
////            }
//        });
//        return kontEvidButton;
//    }

//    private String getDialogTitle(Operation oper, ItemType itemType) {
//        String title;
//        if (Operation.ADD == oper) {
//            if (ItemType.KONT == itemType) {
//                title = "Nová EVIDENCE KONTRAKTU";
//            } else {
//                title = "Nová EVIDENCE POLOŽKY";
//            }
//        } else {
//            if (ItemType.KONT == itemType) {
//                title = "Změna EVIDENCE KONTRAKTU";
//            } else {
//                title = "Změna EVIDENCE POLOŽKY";
//            }
//        }
//        return title;
//    }


    private Component initArchCheck() {
        archCheck = new Checkbox("Archiv");
        archCheck.getElement().setAttribute("theme", "secondary");
        archCheck.setReadOnly(true);
        getBinder().forField(archCheck)
                .bind(Kont::getArch, null);
        return archCheck;
    }

//    private Component initEvidArchComponent() {
//        FlexLayout evidArchCont = new FlexLayout();
//        evidArchCont.setAlignItems(FlexComponent.Alignment.BASELINE);
//        evidArchCont.add(
//                initKontEvidButton()
//                , new Ribbon("3em")
//                , initArchCheck()
//        );
//        return evidArchCont;
//    }

    private Component initTextField() {
        textField = new TextField("Text kontraktu");
        textField.setRequiredIndicatorVisible(true);
        textField.getElement().setAttribute("colspan", "4");
        getBinder().forField(textField)
                .withValidator(new StringLengthValidator(
                        "Text kontraktu musí mít 3-127 znaků",
                        3, 127)
                )
                .bind(Kont::getText, Kont::setText);
        return textField;
    }

//    private Component initObjednatelField() {
//        objednatelField = new TextField("Objednatel");
//        objednatelField.getElement().setAttribute("colspan", "2");
//        getBinder().forField(objednatelField)
////                .withConverter(String::trim, String::trim)
////                .withValidator(new StringLengthValidator(
////                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
////                        3, null))
////                .withValidator(
////                        objednatel -> (currentOperation != Operation.ADD) ?
////                            true : kontService.getByObjednatel(objednatel) == null,
////                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
//                .bind(Kont::getObjednatel, Kont::setObjednatel);
//        objednatelField.setValueChangeMode(ValueChangeMode.EAGER);
//        return objednatelField;
//    }

    private Component initObjednatelCombo() {
        objednatelCombo = new ComboBox<>("Objednatel");
        objednatelCombo.getElement().setAttribute("colspan", "2");
        objednatelCombo.setItems(new ArrayList<>());
        objednatelCombo.setItemLabelGenerator(Klient::getName);
//        objednatelCombo.addValueChangeListener(event -> {
//            Klient klient = objednatelCombo.getValue();
////            if (klient != null) {
////                Notification.show("Vybraný klient: " + klient.getName());
////            } else {
////                Notification.show("No song is selected");
////            }
//        });

        getBinder().forField(objednatelCombo)
//            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
//                .withValidator(klient -> (null != klient) && listOfKlients.contains(klient)
//                        ,"Klient musí být zadán")
                .bind(Kont::getKlient, Kont::setKlient);
        objednatelCombo.setPreventInvalidInput(true);

        return objednatelCombo;
    }


    private Component initInvestorField() {
        investorField = new TextField("Investor");
        investorField.getElement().setAttribute("colspan", "2");
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

//        menaCombo.setRequired(true);  // Bug when using with validator
//        menaCombo.setDataProvider((DataProvider<Mena, String>) EnumSet.allOf(Mena.class));
//        menaCombo.setItems(EnumSet.allOf(Mena.class));
        getBinder().forField(menaCombo)
//            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .withValidator(mena -> (null != mena) && Arrays.asList(Mena.values()).contains(mena)
                          ,"Měna musí být zadána")
            .bind(Kont::getMena, Kont::setMena);
        menaCombo.setPreventInvalidInput(true);

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
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Kont::getHonorar, null);
        return honorarField;
    }

    private Component initHonorarCistyField() {
        honorarCistyField = new TextField("Honorář čistý");
        honorarCistyField.setReadOnly(true);
        honorarCistyField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarCistyField)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Kont::getHonorarCisty, null);
        return honorarCistyField;
    }

    // ------------------------------------------


    private Component initKontDocFolderComponent() {
        kontDocFolderComponent = new FlexLayout();
        kontDocFolderComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        kontDocFolderComponent.setWidth("100%");
        kontDocFolderComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        kontDocFolderComponent.add(initKontFolderField());
        return kontDocFolderComponent;
    }

    private Component initKontFolderField() {
        kontFolderField = new KzFolderField(
                null
                , ItemType.UNKNOWN
                , cfgPropsCache.getDocRootLocal()
                , cfgPropsCache.getProjRootLocal()
        );
        kontFolderField.setWidth("100%");
        kontFolderField.getStyle().set("padding-top", "0em");
        kontFolderField.setReadOnly(true);
        getBinder().forField(kontFolderField)
//                .withValidator(
//                        folder ->
//                                ((Operation.ADD == currentOperation) && StringUtils.isNotBlank(folder))
//                        , "Složka kontraktu není definována, je třeba zadat číslo a text kontraktu"
//                )
                .withValidator(
                    folder ->
                        ((Operation.ADD == currentOperation) &&
                                !VzmFileUtils.kontDocRootExists(cfgPropsCache.getDocRootServer(), folder))
                        ||
                        ((Operation.EDIT == currentOperation) &&
                                ((null != folder) && (null != evidKontOrig) && (folder.equals(evidKontOrig.getFolder())) ||
                                        !VzmFileUtils.kontDocRootExists(cfgPropsCache.getDocRootServer(), folder))
                        )
                    , "Dokumentový adresář kontraktu stejného jména již existuje, změň číslo kontraktu nebo text."
                )
                .withValidator(
                    folder ->
                        ((Operation.ADD == currentOperation) &&
                                !VzmFileUtils.kontProjRootExists(cfgPropsCache.getProjRootServer(), folder))
                            ||
                            ((Operation.EDIT == currentOperation) &&
                                    ((null != folder) && (null != evidKontOrig) && (folder.equals(evidKontOrig.getFolder())) ||
                                            !VzmFileUtils.kontProjRootExists(cfgPropsCache.getProjRootServer(), folder)
                                    )
                            )
                    , "Projektový adresář kontraktu stejného jména již existuje, číslo kontraktu nebo text."
                )

                .bind(Kont::getFolder, Kont::setFolder);

        return kontFolderField;
    }

//    private MultiFileMemoryBuffer buffer;
    private MemoryBuffer buffer;
    private Upload upload;


//    private Component initFileDialogButton() {
//        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
//        dialog.setMode(FileDialog.LOAD);
//        dialog.setVisible(true);
//        String file = dialog.getFile();
//        System.out.println(file + " chosen.");
//    }


    private HtmlComponent getDocFsTree() {
        // When using non-recursive mode, it is possible to browse entire file system
        // DataProvider pre-fetches root directory and rest is loaded progressively
        // lazily
        File rootFile = new File(cfgPropsCache.getDocRootServer());
        FilesystemData root = new FilesystemData(rootFile, false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(root);
        final TreeGrid<File> treeGrid = new TreeGrid<>();
        treeGrid.setDataProvider(fileSystem);

//        treeGrid.setItemIconGenerator(file -> {
//            return FileTypeResolver.getIcon(file);
//        });

        final Div layout1 = new Div();
        layout1.setSizeFull();
        layout1.add(treeGrid);
        return  layout1;
    }

    private Component initUploadDocButton() {
//        registerDocButton = new NewItemButton("Dokument", event -> {});
//        return registerDocButton;

//        buffer = new MultiFileMemoryBuffer();
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setHeight("1em");
        upload.setAutoUpload(false);
        upload.setDropAllowed(false);
//        upload.setUploadButton();

        upload.setUploadButton(initRegisterDocButton());

        upload.addAttachListener(event -> {
            System.out.println("Attached: " + event.getSource());
        });


//        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addStartedListener(event -> {
            System.out.println("Started: " + event.getFileName());
        });

        upload.addFailedListener(event -> {
            System.out.println("Failed: " + event.getFileName());
        });

        upload.addFinishedListener(event -> {
            System.out.println("Finished: " + event.getFileName());
        });

        upload.addProgressListener(event -> {
            System.out.println("Progress: " + event.getContentLength());
        });

        upload.addSucceededListener(event -> {
            System.out.println("Succeeded: " + event.getFileName());
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(),
//                    buffer.getInputStream(event.getFileName()));
//            showOutput(event.getFileName(), component, output);
        });
//
//        upload.addSucceededListener(event -> {
//            event.getFileName(), buffer.getInputStream());
//
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(), buffer.getInputStream());
//            showOutput(event.getFileName(), component, output);
//        });
//        upload.

        return upload;
    }

    private Component initRegisterDocButton() {
        registerDocButton = new NewItemButton("Dokument", event -> {});
        return registerDocButton;
    }

//    private Component createNonImmediateUpload() {
//        Div output = new Div();
//
//        // begin-source-example
//        // source-example-heading: Non immediate upload
//        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
//        Upload upload = new Upload(buffer);
//        upload.setAutoUpload(false);
//
//        upload.addSucceededListener(event -> {
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(),
//                    buffer.getInputStream(event.getFileName()));
//            showOutput(event.getFileName(), component, output);
//        });
//        // end-source-example
//        upload.setMaxFileSize(200 * 1024);
//
//        return output;
//        addCard("Non immediate upload", upload, output);
//    }
//
//    private Component createComponent(String mimeType, String fileName,
//                                      InputStream stream) {
//        if (mimeType.startsWith("text")) {
//            String text = "";
//            try {
//                text = IOUtils.toString(stream, "UTF-8");
//            } catch (IOException e) {
//                text = "exception reading stream";
//            }
//            return new Text(text);
//        } else if (mimeType.startsWith("image")) {
//            Image image = new Image();
//            try {
//
//                byte[] bytes = IOUtils.toByteArray(stream);
//                image.getElement().setAttribute("src", new StreamResource(
//                        fileName, () -> new ByteArrayInputStream(bytes)));
//                try (ImageInputStream in = ImageIO.createImageInputStream(
//                        new ByteArrayInputStream(bytes))) {
//                    final Iterator<ImageReader> readers = ImageIO
//                            .getImageReaders(in);
//                    if (readers.hasNext()) {
//                        ImageReader reader = readers.next();
//                        try {
//                            reader.setInput(in);
//                            image.setWidth(reader.getWidth(0) + "px");
//                            image.setHeight(reader.getHeight(0) + "px");
//                        } finally {
//                            reader.dispose();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return image;
//        }
//        Div content = new Div();
//        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
//                mimeType, MessageDigestUtil.sha256(stream.toString()));
//        content.setText(text);
//        return new Div();
//
//    }
//
//    private void showOutput(String text, Component content,
//                            HasComponents outputContainer) {
//        HtmlComponent p = new HtmlComponent(Tag.P);
//        p.getElement().setText(text);
//        outputContainer.add(p);
//        outputContainer.add(content);
//    }


    private Component initDocGridBar() {
        FlexLayout docGridBar = new FlexLayout();
        docGridBar.setWidth("100%");
        docGridBar.setHeight("3em");
        docGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        docGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        docGridBar.add(
                initDocGridTitle(),
                new Ribbon(),
//                initUploadDocButton()
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
        docGrid.setHeight("4em");
        docGrid.setColumnReorderingAllowed(true);
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("doc-grid");
        docGrid.setClassName("vizman-simple-grid");
//        docGrid.setSizeFull();
//        docGrid.setHeight("20vh");
//        docGrid.setHeight("30vh");
//        docGrid.setHeight("12em");


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
            confirmDocUnregisterDialog.open("Zrušit registraci dokumentu?",
                    "", "", "Zrušit",
                    true, kontDoc, this::removeDocRegistration, this::open);
        });
    }

    private void removeDocRegistration(KontDoc kontDoc) {
        close();
    }

    // ----------------------------------------------

    private Component initZakGridTitleComponent() {
        zakGridTitleComponent = new FlexLayout(
                initZakGridResizeBtn()
                , initZakGridTitle()
        );
        zakGridTitleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        return zakGridTitleComponent;
    }

    private Component initZakGridResizeBtn() {
        Button resizeBtn = new ResizeBtn(getLowerPaneResizeAction(), true);
        return resizeBtn;
    }

    private Component initZakGridTitle() {
        H4 zakTitle = new H4();
        zakTitle.setText(ItemNames.getNomP(ItemType.ZAK));
        zakTitle.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-right", "1em");
        return zakTitle;
    }

    private Component initNewZakButton() {
        newZakButton = new NewItemButton(ItemNames.getNomS(ItemType.ZAK), event ->
                zakFormDialog.openDialog(new Zak(ItemType.ZAK, getCurrentItem().getNewCzak(), getCurrentItem())
                , Operation.ADD, null, null)
//                , Operation.ADD, ItemNames.getNomS(ItemType.ZAK), new FlexLayout(), "")
        );
//                        new Zak(ItemType.ZAK), AbstractEditorDialog.Operation.ADD);
        return newZakButton;
    }

    private Component initNewAkvButton() {
        newAkvButton = new NewItemButton(ItemNames.getNomS(ItemType.AKV), event ->
                zakFormDialog.openDialog(new Zak(ItemType.AKV, getCurrentItem().getNewCzak(), getCurrentItem())
                , Operation.ADD, null, null)
        );
        return newAkvButton;
    }

//    private Component initNewSubButton() {
//        newSubButton = new NewItemButton(ItemNames.getNomS(ItemType.SUB), event ->
//                    subFormDialog.openDialog(new Zak(ItemType.SUB, getCurrentItem().getNewCzak(), getCurrentItem()),
//                    Operation.ADD, null, null)
//        );
//        return newSubButton;
//    }

//    Button newAkvButton = new NewItemButton("Akvizice", null);
//    Button newSubButton = new NewItemButton("Subdodávka", null);

    private Component initZakGridBar() {
        HorizontalLayout zakGridBar = new HorizontalLayout();
//        FlexLayout zakGridBar = new FlexLayout();
        zakGridBar.setSpacing(false);
        zakGridBar.setPadding(false);
        zakGridBar.getStyle().set("margin-left", "-3em");
//        zakGridBar.setWidth("100%");
        zakGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        zakGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        zakGridBar.add(
                initZakGridTitleComponent(),
                new Ribbon(),
                new FlexLayout(
                    initNewZakButton(),
                    new Ribbon(),
                    initNewAkvButton()
                )
        );
        return zakGridBar;
    }

    private Component initZakGrid() {
        zakGrid = new Grid<>();
        zakGrid.setHeight("0");

//        zakGrid.getElement().setProperty("flexGrow", (double)0);
//        alignSelf auto
//        align items stretch
//        zakGrid.setHeight(null);
//        faktGrid.setWidth( "100%" );
        zakGrid.setColumnReorderingAllowed(true);
        zakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        zakGrid.setId("kont-zak-grid");
        zakGrid.setClassName("vizman-simple-grid");
        zakGrid.getStyle().set("marginTop", "0.5em");

        zakGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getItemTypeColoredTextComponent))
                .setHeader("Typ")
                .setWidth("5em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        zakGrid.addColumn(Zak::getCzak)
                .setHeader("ČZ/ČA")
                .setWidth("3.5em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        zakGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenBtn))
                .setFlexGrow(0)
                .setKey(ZAK_EDIT_COL_KEY)
        ;
        zakGrid.addColumn(initZakArchRenderer())
                .setHeader(("Arch"))
                .setWidth("4em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        zakGrid.addColumn(Zak::getRok)
                .setHeader("Rok")
                .setResizable(true)
                .setWidth("5em")
                .setFlexGrow(0)
        ;
        zakGrid.addColumn(zakHonorarCellRenderer)
                .setHeader("Honorář")
                .setResizable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        zakGrid.addColumn(initZakAvizoRenderer())
                .setHeader("Avízo")
                .setWidth("6em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        zakGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getColoredTextComponent))
                .setHeader("Text")
                .setFlexGrow(1)
                .setResizable(true)
        ;

        return zakGrid;
    }


    private ComponentRenderer<Component, Zak> initZakArchRenderer() {
        zakArchRenderer = new ComponentRenderer<>(zak -> {
            // Note: following icons MUST NOT be created outside this renderer (the KonFormDialog cannot be reopened)
            Icon icoTrue = new Icon(VaadinIcon.CHECK);
            icoTrue.setSize("0.8em");
            icoTrue.getStyle().set("theme", "small icon secondary");
            Icon icoFalse = new Icon(VaadinIcon.MINUS);
            icoFalse.setSize("0.8em");
            icoFalse.getStyle().set("theme", "small icon secondary");
            return zak.getArch() ? icoTrue : icoFalse;
        });
        return zakArchRenderer;
    }

//    private ComponentRenderer initZakTextRenderer() {
//        zakTextRenderer = new ComponentRenderer<>(zak -> {
//            Paragraph zakText = new Paragraph(zak.getText());
//            zakText.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(zak.getTyp()));
//
////            if (ItemType.SUB == zak.getTyp()) {
////                zakText.getStyle().set("color", "red");
////            } else if (ItemType.AKV == zak.getTyp()) {
////                zakText.getStyle().set("color", "darkgreen");
////            } else if (ItemType.ZAK == zak.getTyp()) {
////                zakText.getStyle().set("color", "darkmagenta");
////            }
//            return zakText;
//        });
//        return zakTextRenderer;
//    }

    private ComponentRenderer initZakAvizoRenderer() {
        avizoRenderer  = new ComponentRenderer<>(zak ->
            VzmFormatUtils.buildAvizoComponent(zak.getBeforeTerms(), zak.getAfterTerms(), false)
        );
        return avizoRenderer;
    }


//    private Component buildFaktFlagComp() {
//        FlexLayout zakFaktFlags = new FlexLayout();
//        zakFaktFlags.add(
//                new Div(VzmFormatUtils.styleGreenFlag(new Span("1")))
//                , new Ribbon()
//                , new Div(VzmFormatUtils.styleRedFlag(new Span("2")))
//        );
//        return zakFaktFlags;
//    }

    private Component buildZakOpenBtn(Zak zak) {

//        if (ItemType.SUB == zak.getTyp()) {
//            Button btn = new GridItemEditBtn(event -> {
//                subFormDialog.openDialog(
//                        zak, Operation.EDIT, null, null);
//            }, VzmFormatUtils.getItemTypeColorName(zak.getTyp()));
//            return btn;
//        } else {
            Button btn = new GridItemEditBtn(event -> {
                zakFormDialog.openDialog(
                        zak, Operation.EDIT, null, null);
            }, VzmFormatUtils.getItemTypeColorName(zak.getTyp()));
            return btn;
//        }
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
