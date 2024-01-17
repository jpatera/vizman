package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.GeneratedVaadinTextField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.backend.bean.EvidZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static eu.japtor.vizman.backend.utils.VzmFileUtils.*;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconNameProvider;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconStyleProvider;
import static eu.japtor.vizman.ui.components.OperationResult.NO_CHANGE;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakFormDialog extends AbstractKzDialog<Zak> implements HasLogger {

    private final static String FAKT_EDIT_COL_KEY = "fakt-edit-col";
    private final static String FAKT_CISLO_COL_KEY = "fakt-cislo-col";
    private final static String DELETE_STR = "Zrušit";
    public static final String DIALOG_WIDTH = "1250px";
    public static final String DIALOG_HEIGHT = "760px";

    private TextField ckontField;
    private TextField czakField;
    private TextField rokField;
    private TextField poznamkaField;
    private Button akvToZakButton;
    private Checkbox archCheckBox;
    private Checkbox digiCheckBox;
    private TextField textField;
    private TextField skupinaField;
    private TextField honorarCistyField;
    private TextField honorarHrubyField;
    private TextField menaField;

    final private ComponentRenderer<HtmlComponent, Fakt> faktCastkaCellRenderer
            = new ComponentRenderer<>(fakt ->
            VzmFormatUtils.getMoneyComponent(fakt.getCastka())
    );

    private String kontFolder;
    EvidZak evidZakOrig;

    private FlexLayout zakDocFolderComponent;
    private KzFolderField zakFolderField;
    private TreeGrid<VzmFileUtils.VzmFile> zakDocGrid;
    private Button docRefreshButton;
    private List<GridSortOrder<VzmFileUtils.VzmFile>> initialZakDocSortOrder;

    private Grid<Fakt> faktGrid;
    private Button zakReportButton;
    private Button newFaktButton;
    private Button newSubButton;
    private FlexLayout faktGridTitleComponent;
    private Button faktGridResizeBtn;

    private FaktFormDialog faktFormDialog;
    private SubFormDialog subFormDialog;
    private FileViewerDialog fileViewerDialog;
    private ZakReportDialog zakReportDialog;

//    private Button saveButton;
    private Button revertButton;
    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
    private Button deleteAndCloseButton;
    private HorizontalLayout leftBarPart;

    private Binder<Zak> binder = new Binder<>();
    private Zak currentItem;
    private Zak origItem;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;
    private boolean zakFaktsChanged = false;

    private String authUsername;

    private Registration binderChangeListener = null;
    private Registration textFieldListener = null;
    private Registration zakFolderFieldListener = null;

    private boolean readonly;
    private ZakService zakService;
    private FaktService faktService;
    private CfgPropsCache cfgPropsCache;

    public ZakFormDialog(
//                         BiConsumer<Zak, Operation> itemSaver,
//                         Consumer<Zak> zakDeleter,
                         ZakService zakService,
                         FaktService faktService,
                         CfgPropsCache cfgPropsCache
    ){
        super(DIALOG_WIDTH, DIALOG_HEIGHT, true, true);

        this.zakService = zakService;
        this.faktService = faktService;
        this.cfgPropsCache = cfgPropsCache;

        getFormLayout().add(
                initCkontField()
                , initCzakField()
                , initRokField()
                , initSkupinaField()
                , initTextField()
                , initPoznamkaField()
                , initMenaField()
//                , initHonorarField()
                , initHonorarHrubyField()
                , initHonorarCistyField()
                , initAkvToZakButton()
        );
        initArchCheckBox();
        initDigiCheckBox();

        getUpperRightPane().add(
                initZakDocFolderComponent()
                , initDocGridBar()
                , initDocGrid()
        );

        getLowerPane().add(
                new Hr()
                , initFaktGridBar()
        );
        addLowerPaneFlexComponent(initFaktGrid(readonly), "10.8em");


        faktFormDialog = new FaktFormDialog(this.faktService, this.cfgPropsCache);
        faktFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishFaktEdit((FaktFormDialog) event.getSource());
            }
        });
        subFormDialog = new SubFormDialog(this.faktService);
        subFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishSubEdit((SubFormDialog) event.getSource());
            }
        });

        fileViewerDialog = new FileViewerDialog();
    }


    public void openDialog(boolean readonly, Zak zak, Operation operation) {
        this.readonly = readonly;
        this.currentItem = zak;
        this.origItem = zak;    // TODO: to je asi blbe, musi se udelat new Zak
        this.currentOperation = operation;
        this.zakFaktsChanged = false;
        this.authUsername = SecurityUtils.getUsername();

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        someDateField.setLocale(new Locale("cs", "CZ"));

        initZakDataAndControls(currentItem, currentOperation, readonly);
        this.open();
    }


    private void initZakDataAndControls(final Zak zakItem, final Operation zakOperation, final boolean readonly) {

        deactivateListeners();

        setDefaultItemNames();  // Set general default names
        evidZakOrig = new EvidZak(
                zakItem.getKontId()
                , zakItem.getCzak()
                , zakItem.getText()
                , zakItem.getFolder()
                , zakItem.getKontFolder()
        );

        binder.removeBean();
        binder.readBean(zakItem);

        this.faktGrid.deselectAll();
        this.faktGrid.setItems(zakItem.getFakts());

//        this.zakDocGrid.setItems(zakItem.getZakDocs());
        if  (Operation.ADD != zakOperation) {
            updateZakDocViewContent(null);
        }

        this.kontFolder = zakItem.getKontFolder();
        this.zakFolderField.setParentFolder(kontFolder);
        this.zakFolderField.setItemType(zakItem.getTyp());

        getLowerPane().setVisible(ItemType.SUB != zakItem.getTyp());

        refreshHeaderMiddleBox(zakItem);
//        getHeaderEndBox().setText(getHeaderEndComponentValue(null));
        getHeaderEndBox().removeAll();
        getHeaderEndBox().add(getHeaderEndComponent(null));

        initControlsForItemAndOperation(zakItem, zakOperation, readonly);
        initControlsOperability();

        activateListeners();
    }


    private void refreshHeaderMiddleBox(Zak item) {
        FlexLayout headerMiddleComponent = new FlexLayout();
        headerMiddleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerMiddleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerMiddleComponent.add(
                archCheckBox
                , new Gap("2em")
                , digiCheckBox
                , new Gap("5em")
                , VzmFormatUtils.buildAvizoComponent(item.getBeforeTerms(), item.getAfterTerms(), true)
        );
        getHeaderMiddleBox().removeAll();
        if (null != headerMiddleComponent) {
            getHeaderMiddleBox().add(headerMiddleComponent);
        }
    }

    private void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
        if ((null != textFieldListener)) {
            try {
                textFieldListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
    }

    private void activateListeners() {
        // zakFolderField, ckontField, czakField and textField must be initialized prior calling this method
        textFieldListener = textField.addValueChangeListener(event -> zakFolderField.setValue(
                VzmFileUtils.NormalizeDirnamesAndJoin(czakField.getValue(), event.getValue())
        ));
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        binderChangeListener = binder.addValueChangeListener(e -> {
            adjustControlsOperability(true, binder.isValid());
        });
    }

    private boolean isDirty() {
        return binder.hasChanges();
    }

    private void initControlsOperability() {
        saveAndCloseButton.setEnabled(false);
//        saveButton.setEnabled(false);
        revertButton.setEnabled(false);
        zakReportButton.setEnabled(!readonly);
        newFaktButton.setEnabled(!readonly);
        newSubButton.setEnabled(!readonly);
        deleteAndCloseButton.setEnabled(!readonly && currentOperation.isDeleteAllowed() && canDeleteZak(currentItem));

        rokField.setReadOnly(readonly);
        skupinaField.setReadOnly(readonly);
        textField.setReadOnly(readonly);
        poznamkaField.setReadOnly(readonly);
        akvToZakButton.setEnabled(!readonly);
        archCheckBox.setEnabled(!readonly);
        digiCheckBox.setEnabled(!readonly);
    }

    private void adjustControlsOperability(final boolean hasChanges, final boolean isValid) {
        saveAndCloseButton.setEnabled(!readonly && hasChanges && isValid);
//        saveButton.setEnabled(!readonly && hasChanges && isValid);
        revertButton.setEnabled(!readonly && hasChanges);
        zakReportButton.setEnabled(!readonly && isValid);
        newFaktButton.setEnabled(!readonly && isValid);
        newSubButton.setEnabled(!readonly && isValid);

//        saveAndCloseButton.setEnabled(!hasChanges ||!isValid);
//        saveButton.setEnabled(!hasChanges ||!isValid);
//        revertButton.setEnabled(!hasChanges);``

//        rokField.setReadOnly(readonly);
//        skupinaField.setReadOnly(readonly);
//        textField.setReadOnly(readonly);
//        poznamkaField.setReadOnly(readonly);
//        akvToZakButton.setEnabled(!readonly);
//        archCheckBox.setEnabled(!readonly);
    }

//    void finishZakEdit(ZakFormDialog zakFormDialog) {
//        Zak zakAfter = zakFormDialog.getCurrentItem(); // Modified, just added or just deleted
//        String ckz = String.format("%s / %s", zakAfter.getCkont(), zakAfter.getCkz());
//        Operation zakOper = zakFormDialog.getCurrentOperation();
//        OperationResult zakOperRes = zakFormDialog.getLastOperationResult();
//        boolean lastZakFaktsChanged = (zakFormDialog.isZakFaktsChanged());
//        if (OperationResult.NO_CHANGE != zakOperRes) {
//            kontZaksChanged = true;
//        }
//        if (lastZakFaktsChanged) {
//            kontFaktsChanged = true;
//            kontZaksChanged = true;
//        }
//        Zak zakItemOrig = zakFormDialog.getOrigItem();
//
//        syncFormGridAfterZakEdit(zakAfter, zakOper, zakOperRes, lastZakFaktsChanged, zakItemOrig);
//
////        if (OperationResult.NO_CHANGE == zakOperRes) {
////            return;
////        }
//        if (OperationResult.ITEM_SAVED == zakOperRes || lastZakFaktsChanged) {
//            Notification.show(String.format("Zakázka %s uložena", ckz)
//                    , 2500, Notification.Position.TOP_CENTER);
//
//        } else if (OperationResult.ITEM_DELETED == zakOperRes) {
//            ConfirmDialog
//                    .createInfo()
//                    .withCaption("Editace zakázky")
//                    .withMessage(String.format("Zakázka %s zrušena.", ckz))
//                    .open();
//        }
//    }


    private void finishFaktEdit(FaktFormDialog faktFormDialog) {
        Fakt faktAfter = faktFormDialog.getCurrentItem(); // Modified, just added or just deleted
        String ckzf = String.format("%s / %s / %s", faktAfter.getCkont(), faktAfter.getCzak(), faktAfter.getCfakt());
        Operation faktOper = faktFormDialog.getCurrentOperation();
        OperationResult faktOperRes = faktFormDialog.getLastOperationResult();

        if (OperationResult.NO_CHANGE != faktOperRes) {
            zakFaktsChanged = true;
        }
        Fakt faktItemOrig = faktFormDialog.getOrigItem();

        syncFormGridAfterFaktEdit(faktAfter, faktOper, faktOperRes, faktItemOrig);
        updateZakDocViewContent(null);

        if (OperationResult.ITEM_SAVED == faktOperRes) {
            Notification.show(String.format("Plnění %s uloženo", ckzf)
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == faktOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace plnění")
                    .withMessage(String.format("Plnění %s zrušeno.", ckzf))
                    .open();
        }
    }


    private void finishSubEdit(SubFormDialog subFormDialog) {
        Fakt faktAfter = subFormDialog.getCurrentItem(); // ...modified, just added or just deleted
        String ckzf = String.format("%s / %s / %s", faktAfter.getCkont(), faktAfter.getCzak(), faktAfter.getCfakt());
        Operation faktOper = subFormDialog.getCurrentOperation();
        OperationResult faktOperRes = subFormDialog.getLastOperationResult();

        if (OperationResult.NO_CHANGE != faktOperRes) {
            zakFaktsChanged = true;
        }
        Fakt faktItemOrig = subFormDialog.getOrigItem();

        syncFormGridAfterFaktEdit(faktAfter, faktOper, faktOperRes, faktItemOrig);

        if (OperationResult.ITEM_SAVED == faktOperRes) {
            Notification.show(String.format("Subdodávka %s uložena", ckzf)
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == faktOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace subdodávky")
                    .withMessage(String.format("Subdodávka %s zrušena.", ckzf))
                    .open();
        }
    }


    private void syncFormGridAfterFaktEdit(Fakt faktAfter, Operation faktOper
            , OperationResult faktOperRes, Fakt faktItemOrig) {

        if (NO_CHANGE == faktOperRes) {
            return;
        }

//        if (Operation.ADD == faktOper) {
//            currentItem.addFaktOnTop(faktAfter);
//        } else if (Operation.EDIT == faktOper) {
//            if (OperationResult.ITEM_DELETED == faktOperRes) {
//                currentItem.removeFakt(faktItemOrig);
//            } else if (OperationResult.ITEM_SAVED == faktOperRes) {
//                int itemIndex = currentItem.getFakts().indexOf(faktItemOrig);
//                if (itemIndex != -1) {
//                    currentItem.getFakts().set(itemIndex, faktAfter);
//                }
//            }
//        }
        currentItem = zakService.fetchOne(currentItem.getId());
        faktGrid.getDataCommunicator().getKeyMapper().removeAll();
        faktGrid.setItems(currentItem.getFakts());
        faktGrid.getDataProvider().refreshAll();
        faktGrid.select(faktAfter);

        if (NO_CHANGE != faktOperRes) {
            binder.removeBean();
            binder.readBean(currentItem);
            refreshControls(currentItem, currentOperation);
        }
    }

    private void refreshControls(Zak zakItem, final Operation zakOperation) {
        deactivateListeners();
        refreshHeaderMiddleBox(zakItem);
//        getHeaderEndBox().setText(getHeaderEndComponentValue(null));
        getHeaderEndBox().removeAll();
        getHeaderEndBox().add(getHeaderEndComponent(null));
        activateListeners();
    }

    @Override
    public final Zak getCurrentItem() {
        return currentItem;
    }

    @Override
    public final Operation getCurrentOperation() {
        return currentOperation;
    }


    protected final Binder<Zak> getBinder() {
        return binder;
    }


    @Override
    public Component buildDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

//        saveButton = new Button("Uložit");
//        saveButton.setAutofocus(true);
//        saveButton.getElement().setAttribute("theme", "primary");
//        saveButton.addClickListener(e -> saveClicked(false));

        saveAndCloseButton = new Button("Uložit a zavřít");
        saveAndCloseButton.setAutofocus(true);
        saveAndCloseButton.getElement().setAttribute("theme", "primary");
        saveAndCloseButton.addClickListener(e -> saveClicked(true));

        deleteAndCloseButton = new Button("Zrušit");
        deleteAndCloseButton.getElement().setAttribute("theme", "error");
        deleteAndCloseButton.addClickListener(e -> deleteClicked());

        revertButton = new Button("Vrátit změny");
        revertButton.addClickListener(e -> revertClicked(false));

        revertAndCloseButton = new Button("Zpět");
        revertAndCloseButton.addClickListener(e -> revertClicked(true));

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
        leftBarPart.add(
//                saveButton
                revertButton
                , deleteAndCloseButton
//                , akvToZakButton
        );

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                saveAndCloseButton
                , revertAndCloseButton
        );

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

    public void setDefaultItemNames() {
        setItemNames(ItemType.UNKNOWN);
    }


    protected void initControlsForItemAndOperation(final Zak item, final Operation operation, final boolean readonly) {
        setItemNames(item.getTyp());
        getMainTitle().setText(getDialogTitle(operation, itemGender));

        if (getCurrentItem() instanceof HasItemType) {
            getHeaderDevider().getStyle().set(
                    "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
        }
        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
        honorarHrubyField.setReadOnly(true);
        akvToZakButton.setVisible(ItemType.AKV == item.getTyp());
        revertAndCloseButton.setEnabled(true);
        if (readonly) {
            deleteAndCloseButton.setEnabled(false);
            saveAndCloseButton.setEnabled(false);
            akvToZakButton.setEnabled(false);
            zakReportButton.setEnabled(false);
            newSubButton.setEnabled(false);
            newFaktButton.setEnabled(false);
        }
    }


    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
            initControlsOperability();
        }
    }

    private void revertFormChanges() {
//        if (null == currentItem) {
//            ConfirmDialog
//                    .createError()
//                    .withCaption("CHYBA OPERACE")
//                    .withMessage(String.format("Problém při načítání původních dat"))
//                    .withOkButton()
//                    .open();
//            closeDialog();
//        }
        binder.removeBean();
        binder.readBean(currentItem);
        lastOperationResult = OperationResult.NO_CHANGE;
    }

    private void deleteClicked() {
        String ckzDel = String.format("%s / %s", currentItem.getCkont(), currentItem.getCzak());
        if (!canDeleteZak(currentItem)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení zakázky")
                    .withMessage(String.format("Zakázku %s nelze zrušit, obsahuje faktury.", ckzDel))
                    .open()
            ;
            return;
        }
        try {
            revertFormChanges();
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení zakázky")
                    .withMessage(String.format("Zrušit zakázku %s ?", ckzDel))
                    //                .with...(,"Poznámka: Projektové a dokumentové adresáře včetně souborů zůstanou nezměněny.")
                    .withOkButton(() -> {
                            if (deleteZak(currentItem)) {
                                closeDialog();
                            }
                        }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                    )
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        } catch (VzmServiceException e) {
            showDeleteErrMessage();
        }
    }


    private void saveClicked(boolean closeAfterSave) {
        if (!writeZakToBeanIfValid()) {
            return;
        }
        try {
            currentItem = saveZak(currentItem);
            if (closeAfterSave) {
                closeDialog();
            } else {
                initZakDataAndControls(currentItem, currentOperation, readonly);
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private boolean saveWithoutClose() {
        if (!writeZakToBeanIfValid()) {
            return false;
        }
        try {
            currentItem = saveZak(currentItem);
            if (Operation.ADD ==  currentOperation) {
                currentOperation = Operation.EDIT;
            }
            initZakDataAndControls(currentItem, currentOperation, readonly);
//            binder.removeBean();
//            binder.readBean(currentItem);
//            initControlsOperability();
            return true;
        } catch (VzmServiceException e) {
            showSaveErrMessage();
            return false;
        }
    }

    private void closeDialog() {
        faktGrid.deselectAll(); // ..otherwise during next openDialog "$0.connector..." error appears
        zakDocGrid.deselectAll(); // ..otherwise during next openDialog "$0.connector..." error appears
        this.close();
    }

    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace zakázky")
                .withMessage("Zakázku se nepodařilo uložit.")
                .open();
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace zakázky")
                .withMessage("Zakázku se nepodařilo zrušit")
                .open()
        ;
    }

    private boolean writeZakToBeanIfValid() {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (!isValid) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace zakázky")
                    .withMessage("Zakázku nelze uložit, některá pole nejsou správně vyplněna.")
                    .open();
            return false;
        }
        return true;
    }


    private boolean canDeleteZak(final Zak itemToDelete) {
        return itemToDelete.getFakts().size() == 0;
    }

    private boolean deleteZak(Zak itemToDelete) {
        String ckzDel = String.format("%s / %s", currentItem.getCkont(), currentItem.getCzak());
        OperationResult lastOperResOrig = lastOperationResult;
        try {
            zakService.deleteZak(itemToDelete);
            lastOperationResult = OperationResult.ITEM_DELETED;
            return true;
        } catch (VzmServiceException e) {
            this.lastOperationResult = lastOperResOrig;
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení zakázky.")
                    .withMessage(String.format("Zakázku %s se nepodařilo zrušit.", ckzDel))
                    .open()
            ;
            return false;
        }
    }


    public Zak saveZak(Zak zakToSave) throws VzmServiceException {

        try {
            if (!StringUtils.isBlank(zakToSave.getKontFolder())) {
                if (StringUtils.isBlank(zakToSave.getFolder())) {
                    fireEvent(new GeneratedVaadinTextField.ChangeEvent(textField, false));
                }
            }
            zakToSave.setUpdatedBy(authUsername);
            zakToSave.setDatetimeUpdate(LocalDateTime.now());

            currentItem = zakService.saveZak(zakToSave, currentOperation);
            if (zakDirsToBeCreated(currentItem, currentOperation)) {
                createZakDirs(currentItem);
            }
            lastOperationResult = OperationResult.ITEM_SAVED;
            return currentItem;
        } catch(VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            throw(e);
        }

//            if (Operation.EDIT == operation) {
//                if (null != evidZakOrig.getFolder() && !evidZakOrig.getFolder().equals(zak.getFolder())) {
//                    ConfirmDialog
//                            .createWarning()
//                            .withCaption("Adresáře zakázky")
//                            .withMessage("Dokumentové ani projektové adresáře se automaticky nepřejmenovávají.")
//                            .open();
//                }
//
//            if (!VzmFileUtils.kontDocRootExists(getDocRootServer(), zak.getFolder())) {
//                new OkDialog().open("Adresáře zakázky"
//                        , "POZOR, dokumentový ani projektový adresář se automaticky nepřejmenovávají.", "");
//            }
//            if (!VzmFileUtils.kontProjRootExists(getProjRootServer(), zak.getFolder())) {
//                new OkDialog().open("Projektový adresáře zakázky"
//                        , "POZOR, projektový adresář zakázky nenalezen, měl by se přejmenovat ručně", "");

//            if (Operation.ADD == operation) {
//                if (StringUtils.isBlank(zak.getKontFolder()) || StringUtils.isBlank(zak.getFolder())) {
//                    if (StringUtils.isBlank(zak.getKontFolder())) {
//                        ConfirmDialog
//                                .createWarning()
//                                .withCaption("Adresáře zakázky")
//                                .withMessage("Složka kontraktu není definována, nelze vytvořit adresáře zakázky")
//                                .open();
//                    } else if(StringUtils.isBlank(zak.getFolder())) {
//                        ConfirmDialog
//                                .createError()
//                                .withCaption("Adresáře zakázky")
//                                .withMessage("Složka zakázky není zadána, nelze vytvořit adresáře")
//                                .open();
//                    }
//                } else {
//                    boolean zakDocDirsOk = VzmFileUtils.createZakDocDirs(
//                            cfgPropsCache.getDocRootServer(), zak.getKontFolder(), zak.getFolder());
//                    boolean zakProjDirsOk = VzmFileUtils.createZakProjDirs(
//                            cfgPropsCache.getProjRootServer(), zak.getKontFolder(), zak.getFolder());
//                    //            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
//                    //            kontProjRootDir.setReadOnly();
//                    String errMsg = null;
//                    if (!zakDocDirsOk && !zakProjDirsOk) {
//                        errMsg = "Projektové ani dokumentové adresáře se nepodařilo vytvořit";
//                    } else if (!zakDocDirsOk) {
//                        errMsg = "Dokumentové adresáře se nepodařilo vytvořit";
//                    } else if (!zakProjDirsOk) {
//                        errMsg = "Projektové adresáře se nepodařilo vytvořit";
//                    }
//                    if (null != errMsg) {
//                        ConfirmDialog
//                                .createError()
//                                .withCaption("Adresáře zakázky")
//                                .withMessage(errMsg)
//                                .open();
//                    }
//                }
//            } else {
//                getLogger().warn("Saving {}: unknown operation {} appeared", zak.getTyp().name(), operation.name());
//            }
//
//            getLogger().info("{} saved: {} / {} [operation: {}]", getCurrentItem().getTyp().name()
//                    , getCurrentItem().getCkont(), getCurrentItem().getCkz(), operation.name());
//            return zakSaved;
//
//        } catch(Exception e) {
//            getLogger().error("Error when saving {} {} / {} [operation: {}]", getCurrentItem().getTyp().name()
//                    , getCurrentItem().getCkont(), getCurrentItem().getCkz(), operation.name());
//            throw e;
//        }
    }

    private boolean zakDirsToBeCreated(final Zak itemToSave, final Operation oper) {
        return  (Operation.ADD == oper) ||
                ((Operation.EDIT == oper) && StringUtils.isNotBlank(itemToSave.getKontFolder())
                        && !(itemToSave.getFolder()).equals(evidZakOrig.getFolder()));
    }

    private boolean createZakDirs(final Zak zak) {
        if (StringUtils.isBlank(zak.getFolder())) {
            ConfirmDialog
                    .createError()
                    .withCaption("Adresáře zaázky")
                    .withMessage("Složka zakázky není zadána, nelze vytvořit adresáře")
                    .open();
            return false;
        }

        boolean zakDocDirsOk = VzmFileUtils.createZakDocDirs(
                cfgPropsCache.getDocRootServer(), zak.getKontFolder(), zak.getFolder());
        boolean zakProjDirsOk = VzmFileUtils.createZakProjDirs(
                cfgPropsCache.getProjRootServer(), zak.getKontFolder(), zak.getFolder());
        String errMsg = null;
        if (!zakDocDirsOk && !zakProjDirsOk) {
            errMsg = "Projektové ani dokumentové adresáře se nepodařilo vytvořit";
        } else if (!zakDocDirsOk) {
            errMsg = "Dokumentové adresáře se nepodařilo vytvořit";
        } else if (!zakProjDirsOk) {
            errMsg = "Projektové adresáře se nepodařilo vytvořit";
        }
        if (null == errMsg) {
            return true;
        } else {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Adresáře zakázky")
                    .withMessage(errMsg)
                    .open()
            ;
            return false;
        }
    }

    public OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    public boolean isZakFaktsChanged()  {
        return zakFaktsChanged;
    }

    public Zak getOrigItem()  {
        return origItem;
    }

// -----------------------------------------------------

    private Component initAkvToZakButton() {
        akvToZakButton = new Button("AKV -> ZAK");
            akvToZakButton.addClickListener(event -> {
            if (saveWithoutClose()) {
                currentItem.setTyp(ItemType.ZAK);
                if (saveWithoutClose()) {
                    ConfirmDialog
                            .createInfo()
                            .withCaption("AKV -> ZAK")
                            .withMessage("Akvizice " + currentItem.getCkont() + " / " + currentItem.getCzak() + " převedena na zakázku.")
                            .open();
                }
            }
        });
        return akvToZakButton;
    }

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        ckontField.setReadOnly(true);
        ckontField.getStyle()
                .set("padding-top", "0em")
                .set("font-size" , "var(--lumo-font-size-s)");
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
                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .bind(Zak::getCzak, null);
        return czakField;
    }

    private Component initRokField() {
        rokField = new TextField("Rok zakázky");
        rokField.setWidth("8em");
        rokField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(rokField)
                .withConverter(new VzmFormatUtils.ValidatedIntegerYearConverter())
                .bind(Zak::getRok, Zak::setRok);
        rokField.setValueChangeMode(ValueChangeMode.EAGER);
        return rokField;
    }


    private Component initArchCheckBox() {
        archCheckBox = new Checkbox("Archiv"); // = new TextField("Username");
        archCheckBox.getElement().setAttribute("theme", "secondary");
        getBinder().forField(archCheckBox)
                .bind(Zak::getArch, Zak::setArch);
        return archCheckBox;
    }

    private Component initDigiCheckBox() {
        digiCheckBox = new Checkbox("DIGI"); // = new TextField("Username");
        digiCheckBox.getElement().setAttribute("theme", "secondary");
        getBinder().forField(digiCheckBox)
                .bind(Zak::getDigi, Zak::setDigi);
        return digiCheckBox;
    }

    private Component initTextField() {
        textField = new TextField("Text zakázky");
        textField.setRequiredIndicatorVisible(true);
        textField.getElement().setAttribute("colspan", "4");
        getBinder().forField(textField)
                .withValidator(new StringLengthValidator(
                        "Text zakázky musí mít 3-127 znaků",
                        3, 127)
                )
                .bind(Zak::getText, Zak::setText);
        return textField;
    }

    private Component initPoznamkaField() {
        poznamkaField = new TextField("Poznámka");
        poznamkaField.getElement().setAttribute("colspan", "4");
        getBinder().forField(poznamkaField)
                .withValidator(new StringLengthValidator(
                        "Poznámka může mít max. 127 znaků",
                        0, 127)
                )
                .bind(Zak::getPoznamka, Zak::setPoznamka);
        poznamkaField.setValueChangeMode(ValueChangeMode.EAGER);
        return poznamkaField;
    }

    private Component initSkupinaField() {
        skupinaField = new TextField("Skupina");
        skupinaField.setWidth("8em");
        skupinaField.getStyle()
                .set("padding-top", "0em");
        getBinder().forField(skupinaField)
                .asRequired("Skupina nesmí být prázdná")
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

    private Component initHonorarCistyField() {
        honorarCistyField = new TextField("Honorář čistý");
        honorarCistyField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        honorarCistyField.getStyle()
                .set("font-weight", "bold");
        getBinder().forField(honorarCistyField)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Zak::getHonorarCisty, null);
        honorarCistyField.setValueChangeMode(ValueChangeMode.EAGER);
        return honorarCistyField;
    }

    private Component initHonorarHrubyField() {
        honorarHrubyField = new TextField("Honorář hrubý");
        honorarHrubyField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        honorarHrubyField.getStyle()
                .set("font-weight", "bold");
        getBinder().forField(honorarHrubyField)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Zak::getHonorarHruby, null);
        honorarHrubyField.setValueChangeMode(ValueChangeMode.EAGER);
        return honorarHrubyField;
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


    private Component initDocRefreshButton() {
        docRefreshButton = new ReloadButton("Načte adresáře", event -> {
            updateZakDocViewContent(null);
        });
        return docRefreshButton;
    }

    private Component initDocGridBar() {
        FlexLayout docGridBar = new FlexLayout();
        docGridBar.setWidth("100%");
//        docGridBar.setHeight("3em");
        docGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        docGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        docGridBar.add(
                initDocGridTitle(),
                new Ribbon(),
                initDocRefreshButton()
        );
        return docGridBar;
    }

    private Component initDocGridTitle() {
        H4 docGridTitle = new H4();
        docGridTitle.setText("Dokumenty");
        return docGridTitle;
    }

    private Component initDocGrid() {
        zakDocGrid = new TreeGrid<>();
        zakDocGrid.setHeight("3em");
        zakDocGrid.setColumnReorderingAllowed(false);
        zakDocGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        zakDocGrid.setId("zak-doc-grid");
        zakDocGrid.setClassName("vizman-simple-grid");

        zakDocGrid.addItemDoubleClickListener(event -> {
            VzmFile vzmFile = event.getItem();
            fileViewerDialog.openDialog(vzmFile);
        });

        Grid.Column hCol = zakDocGrid.addColumn(fileIconTextRenderer);
        hCol.setHeader("Název")
                .setFlexGrow(1)
                .setWidth("30em")
                .setKey("zak-doc-file-name")
                .setResizable(true)
        ;

        return zakDocGrid;
    }

    private TemplateRenderer fileIconTextRenderer = TemplateRenderer.<VzmFileUtils.VzmFile> of("<vaadin-grid-tree-toggle "
            + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
            + "<iron-icon style=\"[[item.icon-style]]\" icon=\"[[item.icon-name]]\"></iron-icon>&nbsp;&nbsp;"
            + "[[item.name]]"
            + "</vaadin-grid-tree-toggle>")
            .withProperty("leaf", file -> !zakDocGrid.getDataCommunicator().hasChildren(file))
            .withProperty("icon-name", file -> String.valueOf(vzmFileIconNameProvider.apply(file)))
            .withProperty("icon-style", file -> String.valueOf(vzmFileIconStyleProvider.apply(file)))
            .withProperty("name", File::getName)
        ;

    private void updateZakDocViewContent(final VzmFileUtils.VzmFile itemToSelect) {
        zakDocGrid.deselectAll();
        Path kontDocRootPath = getKontDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getKontFolder());
        TreeData<VzmFileUtils.VzmFile> zakDocTreeData
                = VzmFileUtils.getExpectedZakDocFolderFilesTree(kontDocRootPath.toString(), currentItem);

        Path zakDocRootPath = getZakDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getKontFolder(), currentItem.getFolder());
        File zakDocRootDir = new File(zakDocRootPath.toString());
        addFilesToExpectedVzmTreeData(zakDocTreeData, zakDocRootDir.listFiles(), null);

        addNotExpectedKontSubDirs(zakDocTreeData
                , new VzmFileUtils.VzmFile(zakDocRootPath, true, VzmFolderType.OTHER, 0)
        );
        addNotExpectedKontSubDirs(zakDocTreeData
                , new VzmFileUtils.VzmFile(getExpectedZakFolder(currentItem), true, VzmFolderType.OTHER, 0));

        assignDataProviderToGridAndSort(zakDocTreeData);
        zakDocGrid.getDataProvider().refreshAll();
//        if (null != itemToSelect) {
//            kontDocGrid.getSelectionModel().select(itemToSelect);
//        }
    }

    private void assignDataProviderToGridAndSort(TreeData<VzmFileUtils.VzmFile> kontDocTreeData) {
        List<GridSortOrder<VzmFileUtils.VzmFile>> sortOrderOrig = zakDocGrid.getSortOrder();
        zakDocGrid.setTreeData(kontDocTreeData);
        if (CollectionUtils.isEmpty(sortOrderOrig)) {
            zakDocGrid.sort(initialZakDocSortOrder);
        } else  {
            zakDocGrid.sort(sortOrderOrig);
        }
    }

    private Component buildDocRemoveButton(ZakDoc zakDoc) {
        return new ItemOpenBtn(event -> {
//            close();
            ConfirmDialog.createQuestion()
                    .withCaption("REGISTRACE DOKUMENTU")
                    .withMessage("Zrušit registraci dokumentu?")
                    .withYesButton(() ->
                            removeDocRegistration(zakDoc), ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                    )
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open();
//            confirmDocUnregisterDialog.open("Zrušit registraci dokumentu ?",
//                    "", "", "Zrušit",
//                    true, kontDoc, this::removeDocRegistration, this::open);
        });
    }

    private void removeDocRegistration(ZakDoc zakDoc) {
//        closeDialog();
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
            if (saveWithoutClose()) {
                faktFormDialog.openDialog(readonly, new Fakt(ItemType.FAKT, getCurrentItem().getNewCfakt(), getCurrentItem())
                        , Operation.ADD);
            }
        });
        newFaktButton.setEnabled(readonly);
        return newFaktButton;
    }

    private Component initNewSubButton() {
        newSubButton = new NewItemButton(ItemNames.getNomS(ItemType.SUB), event -> {
            if (saveWithoutClose()) {
                subFormDialog.openDialog(readonly, new Fakt(ItemType.SUB, getCurrentItem().getNewCfakt(), getCurrentItem())
                        , Operation.ADD);
            }
        });
        newSubButton.setEnabled(readonly);
        return newSubButton;
    }



    private Component initFaktGridBar() {
        HorizontalLayout faktGridBar = new HorizontalLayout();
        faktGridBar.setSpacing(false);
        faktGridBar.setPadding(false);
        faktGridBar.getStyle().set("margin-left", "-3em");
        faktGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        faktGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        faktGridBar.add(
                initFaktGridTitleComponent(),
                new Ribbon(),
                new FlexLayout(
                        initZakReportButton(),
                        new Ribbon(),
                        initNewFaktButton(),
                        new Ribbon(),
                        initNewSubButton()
                )
        );
        return faktGridBar;
    }

    private HasSize initFaktGrid(boolean readonly) {
        faktGrid = new Grid<>();
//        faktGrid.setHeight("3em");    // 2020-06 stopped stretching to the rest of dialog height
        faktGrid.setHeight("10.8em");
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
                .setHeader("Č. pol.")
                .setWidth("3.5em")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktEditBtn))
                .setHeader("Edit")
                .setWidth("3em")
                .setFlexGrow(0)
                .setKey(FAKT_EDIT_COL_KEY)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getDuzpComponent))
                .setHeader("DUZP")
                .setFlexGrow(0)
        ;
        faktGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getColoredTextComponent))
                .setHeader("Text")
                .setFlexGrow(1)
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
        faktGrid.addColumn(Fakt::getFaktCislo)
                .setHeader("Číslo faktury")
                .setFlexGrow(0)
                .setKey(FAKT_CISLO_COL_KEY)
        ;

        return faktGrid;
    }

    private Component buildFaktEditBtn(Fakt fakt) {
        if (ItemType.FAKT == fakt.getTyp()) {
            return new GridItemEditBtn(event ->
                    faktFormDialog.openDialog(readonly, fakt, Operation.EDIT)
                    , VzmFormatUtils.getItemTypeColorName(fakt.getTyp()));
        } else {
            return new GridItemEditBtn(event ->
                    subFormDialog.openDialog(readonly, fakt, Operation.EDIT)
                    , VzmFormatUtils.getItemTypeColorName(fakt.getTyp()));
        }
    }

    private Component buildFakStornoBtn(Fakt fakt) {
        boolean isFakturovano = fakt.isFakturovano();
        if (ItemType.FAKT == fakt.getTyp()) {
            return new FakturovatBtn(event -> {
                if (isFakturovano) {
                    faktFormDialog.openDialog(readonly, fakt, Operation.STORNO);
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
                        faktFormDialog.openDialog(readonly, fakt, Operation.FAKTUROVAT);
                    }
                }
            }, isFakturovano);
        } else {
            return new Span();
        }
    }

    private Component buildExportBackBtn(Fakt fakt) {

        if (ItemType.FAKT == fakt.getTyp()) {
            Button exportBackBtn = new GridFaktExportBtn(event -> {
                //            Fakt fakt = new Fakt(ItemType.FAKT, getCurrentItem().getNewCfakt(), getCurrentItem());
                //            faktFormDialog.open(fakt, Operation.ADD, "Fakturace");
                faktFormDialog.openDialog(readonly, fakt, Operation.EXPORT);
            });
            exportBackBtn.setEnabled(false);
            return exportBackBtn;
        } else {
            return new Span();
        }
    }


    private Component initZakReportButton() {

        zakReportButton = new Button("Report");
        zakReportButton.addClickListener(event -> {
                if (null == zakReportDialog) {
                    zakReportDialog = new ZakReportDialog();
                }
                zakReportDialog.openDialog(currentItem);
                zakReportDialog.generateAndShowReport();
        });
        zakReportButton.setEnabled(true);

        zakReportButton.getElement().setAttribute("theme", "small secondary");
        return zakReportButton;
    }
}
