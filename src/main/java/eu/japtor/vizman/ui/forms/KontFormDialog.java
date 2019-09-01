package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
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
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.japtor.vizman.backend.utils.VzmFileUtils.*;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconNameProvider;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconStyleProvider;
import static eu.japtor.vizman.ui.components.OperationResult.NO_CHANGE;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractKzDialog<Kont> implements HasLogger {
//public class KontFormDialog extends AbstractEditorDialog<Kont> implements BeforeEnterObserver {

    private final static String ZAK_EDIT_COL_KEY = "zak-edit-col";
    private final static String DELETE_STR = "Zrušit";
    private final static String SAVE_STR = "Uložit";
    public static final String DIALOG_WIDTH = "1300px";
    public static final String DIALOG_HEIGHT = "800px";

    private final ComponentRenderer<HtmlComponent, Zak> zakHonorarCellRenderer =
            new ComponentRenderer<>(zak ->
                    VzmFormatUtils.getMoneyComponent(zak.getHonorar())
            );
    private final ComponentRenderer<HtmlComponent, Zak> zakHonorarCistyCellRenderer =
            new ComponentRenderer<>(zak ->
                    VzmFormatUtils.getMoneyComponent(zak.getHonorarCisty())
            );
    private final ComponentRenderer<HtmlComponent, Zak> zakHonorarHrubyCellRenderer =
            new ComponentRenderer<>(zak ->
                    VzmFormatUtils.getMoneyComponent(zak.getHonorarHruby())
            );

    private TextField ckontField;
    private TextField rokField;
    private Checkbox archCheck;
    private TextField objednatelField;
    private TextField investorField;
    private TextField textField;
    private TextField honorarField;
    private TextField honorarHrubyFieldCalc;
    private TextField honorarCistyFieldCalc;
    private ComboBox<Mena> menaCombo;
    private ComboBox<Klient> objednatelCombo;
    private ArchIconBox archIconBox;

//    private String kontFolderOrig;
    private FlexLayout kontDocFolderComponent;
    private KzFolderField kontFolderField;

    private Anchor kontFolderAnchor;

    private TreeGrid<VzmFileUtils.VzmFile> kontDocGrid;
    private Button docRefreshButton;
    private List<GridSortOrder<VzmFileUtils.VzmFile>> initialKontDocSortOrder;

    private Grid<Zak> zakGrid;
    private Button newZakButton;
    private Button newAkvButton;
    private FlexLayout zakGridTitleComponent;
    private ComponentRenderer<Component, Zak> zakArchRenderer;
    private ComponentRenderer<Component, Zak> avizoRenderer;

    EvidKont evidKontOrig;
    private ZakFormDialog zakFormDialog;
    private FileViewerDialog fileViewerDialog;

    private Button revertButton;
    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
    private Button deleteAndCloseButton;
    private HorizontalLayout leftBarPart;

    private Binder<Kont> binder = new Binder<>();
    private Kont currentItem;
    private Kont kontItemOrig;
    private Operation currentOperation;
    private OperationResult lastOperationResult = NO_CHANGE;
    private boolean kontZaksChanged = false;
    private boolean kontZaksFaktsChanged = false;

    private Registration binderChangeListener = null;
    private Registration textFieldListener = null;
    private Registration ckontFieldListener = null;

    private KontService kontService;
    private ZakService zakService;
    private FaktService faktService;
    private KlientService klientService;
    private List<Klient> klientList;
    private CfgPropsCache cfgPropsCache;

    public KontFormDialog(
            KontService kontService,
            ZakService zakService,
            FaktService faktService,
            KlientService klientService,
            DochsumZakService dochsumZakService,
            CfgPropsCache cfgPropsCache
    ) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT, true, true);

        this.kontService = kontService;
        this.zakService = zakService;
        this.klientService = klientService;
        this.cfgPropsCache = cfgPropsCache;

//        deactivateListeners();

        getFormLayout().add(
                initCkontField()
                , initRokField()
//                , initArchCheck()
                , initTextField()
                , initObjednatelCombo()   // Because of a bug -> call it in OpenDialog
                , initInvestorField()
                , initMenaCombo()
//                , initHonorarField()
                , initHonorarHrubyField()
                , initHonorarCistyField()
        );

        getUpperRightPane().add(
                initKontDocFolderComponent()
                // TODO
//                , initKontFolderAnchor()
                , initDocGridBar()
                , initDocGrid()
        );

        getLowerPane().add(
                new Hr()
                , initZakGridBar()
                , initZakGrid()
        );

        zakFormDialog = new ZakFormDialog(
                zakService, faktService, cfgPropsCache
        );

        zakFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishZakEdit((ZakFormDialog) event.getSource());
            }
        });

        fileViewerDialog = new FileViewerDialog();
    }


    public void openDialog(Kont kont, Operation operation) {

        this.currentOperation = operation;
        this.currentItem = kont;
        this.kontZaksChanged = false;
        this.kontItemOrig = kont;

        klientList = klientService.fetchAll();
//        // Following series of commands replacing combo box are here because of a bug
//        // Initialize $connector if values were not set in ComboBox element prior to page load. #188
        binder.removeBinding(objednatelCombo);
        getFormLayout().remove(objednatelCombo);
        getFormLayout().addComponentAtIndex(3, initObjednatelCombo());
        objednatelCombo.setItems(this.klientList);
        getBinder().forField(objednatelCombo)
                .bind(Kont::getKlient, Kont::setKlient);
        objednatelCombo.setPreventInvalidInput(true);

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

        initKontDataAndControls(currentItem, currentOperation);
        this.open();
    }

    private void initKontDataAndControls(final Kont kontItem, final Operation kontOperation) {

        deactivateListeners();

        setDefaultItemNames();  // Set general default names
        evidKontOrig = new EvidKont(
                kontItem.getCkont()
                , kontItem.getText()
                , kontItem.getFolder()
        );

        binder.removeBean();
        binder.readBean(kontItem);

        this.zakGrid.deselectAll();
        this.zakGrid.setItems(kontItem.getZaks());
//        this.kontDocGrid.setItems(kontItem.getKontDocs());
//        this.kontDocGrid.setDataProvider(VzmFileUtils.getExpectedKontFolderTree(cfgPropsCache.getDocRootServer(), kontItem));

        if  (Operation.ADD != kontOperation) {
            updateKontDocViewContent(null);
        }

        this.kontFolderField.setParentFolder(null);
        this.kontFolderField.setItemType(kontItem.getTyp());

        refreshHeaderMiddleBox(kontItem);
        getHeaderEndBox().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(kontItem, kontOperation);
        initControlsOperability();

        activateListeners();
    }

    private void refreshControls(Kont kontItem, final Operation kontOperation) {
        deactivateListeners();
        refreshHeaderMiddleBox(kontItem);
        getHeaderEndBox().setText(getHeaderEndComponentValue(null));
//        adjustControlsOperability(hasChanges, isValid);
        activateListeners();
    }

    private void refreshHeaderMiddleBox(Kont kontItem) {
        FlexLayout headerMiddleComponent = new FlexLayout();
        headerMiddleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerMiddleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerMiddleComponent.add(
                buildArchBox()
                , new Gap("5em")
                , VzmFormatUtils.buildAvizoComponent(kontItem.getBeforeTerms(), kontItem.getAfterTerms(), true)
        );
        getHeaderMiddleBox().removeAll();
        if (null != headerMiddleComponent) {
            getHeaderMiddleBox().add(headerMiddleComponent);
        }
        archIconBox.showIcon(kontItem.getTyp(), kontItem.getArchState());
    }

    private void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
        if (null != textFieldListener) {
            try {
                textFieldListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
        if (null != ckontFieldListener) {
            try {
                ckontFieldListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
    }

    private void activateListeners() {
        // kontFolderField, ckontField and textField must be initialized prior calling this method
        textFieldListener = textField.addValueChangeListener(event -> {
            kontFolderField.setValue(
                    VzmFileUtils.NormalizeDirnamesAndJoin(ckontField.getValue(), event.getValue())
            );
        });
        textField.setValueChangeMode(ValueChangeMode.EAGER);

        ckontFieldListener = ckontField.addValueChangeListener(event -> {
            kontFolderField.setValue(
                    VzmFileUtils.NormalizeDirnamesAndJoin(event.getValue(), textField.getValue())
            );
        });
        ckontField.setValueChangeMode(ValueChangeMode.EAGER);

        binderChangeListener = binder.addValueChangeListener(e -> {
            adjustControlsOperability(true, binder.isValid());
        });

//        binder.addStatusChangeListener(event -> {
//            boolean isValid = event.getBinder().isValid();
//            boolean hasChanges = event.getBinder().hasChanges();
//            initControlsForItemAndOperation(currentItem, currentOperation);
//        });
    }

    private boolean isDirty() {
        return binder.hasChanges();
    }

    private void initControlsOperability() {
        saveAndCloseButton.setEnabled(false);
//        saveButton.setEnabled(false);
        revertButton.setEnabled(false);
        newAkvButton.setEnabled(true);
        newZakButton.setEnabled(true);
        deleteAndCloseButton.setEnabled(currentOperation.isDeleteEnabled() && canDeleteKont(currentItem));
    }

    private void adjustControlsOperability(final boolean hasChanges, final boolean isValid) {
        saveAndCloseButton.setEnabled(hasChanges && isValid);
//        saveButton.setEnabled(hasChanges && isValid);
        revertButton.setEnabled(hasChanges);
        newAkvButton.setEnabled(isValid);
        newZakButton.setEnabled(isValid);
//        saveAndCloseButton.setEnabled(!hasChanges ||!isValid);
//        saveButton.setEnabled(!hasChanges ||!isValid);
//        revertButton.setEnabled(!hasChanges);
    }


    void finishZakEdit(ZakFormDialog zakFormDialog) {
        Zak zakAfter = zakFormDialog.getCurrentItem(); // Modified, just added or just deleted
        String ckz = String.format("%s / %s", zakAfter.getCkont(), zakAfter.getCzak());
        Operation zakOper = zakFormDialog.getCurrentOperation();
        OperationResult zakOperRes = zakFormDialog.getLastOperationResult();

        boolean lastZakFaktsChanged = (zakFormDialog.isZakFaktsChanged());
        if (OperationResult.NO_CHANGE != zakOperRes) {
            kontZaksChanged = true;
        }
        if (lastZakFaktsChanged) {
            kontZaksFaktsChanged = true;
            kontZaksChanged = true;
        }
        Zak zakItemOrig = zakFormDialog.getOrigItem();
//        currentItem = kontService.fetchOne(zakItemOrig.getKontId());
//        getBinder().readBean(currentItem);

        syncFormGridAfterZakEdit(zakAfter, zakOper, zakOperRes, lastZakFaktsChanged, zakItemOrig);
        updateKontDocViewContent(null);

//        if (OperationResult.NO_CHANGE == zakOperRes) {
//            return;
//        }
        if (OperationResult.ITEM_SAVED == zakOperRes || lastZakFaktsChanged) {
            Notification.show(String.format("Zakázka %s uložena", ckz)
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == zakOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace zakázky")
                    .withMessage(String.format("Zakázka %s zrušena.", ckz))
                    .open();
        }
    }

    private void syncFormGridAfterZakEdit(Zak zakAfter, Operation zakOper
            , OperationResult zakOperRes, boolean lastZakFaktsChanged, Zak zakItemOrig) {

        if ((NO_CHANGE == zakOperRes) && !lastZakFaktsChanged) {
            return;
        }

        if (Operation.ADD == zakOper) {
            currentItem.addZakOnTop(zakAfter);
        } else if (Operation.EDIT == zakOper) {
            if (OperationResult.ITEM_DELETED == zakOperRes) {
                currentItem.removeZak(zakItemOrig);
            } else if (lastZakFaktsChanged || (OperationResult.ITEM_SAVED == zakOperRes)) {
                int itemIndex = getCurrentItem().getZaks().indexOf(zakItemOrig);
                if (itemIndex != -1) {
                    getCurrentItem().getZaks().set(itemIndex, zakAfter);
                }
            }
        }
        currentItem = kontService.fetchOne(currentItem.getId());
        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
        zakGrid.setItems(currentItem.getZaks());
        zakGrid.getDataProvider().refreshAll();
        zakGrid.select(zakAfter);

        binder.removeBean();
        binder.readBean(currentItem);
        refreshControls(currentItem, currentOperation);
        updateKontDocViewContent(null);
    }

    @Override
    public final Kont getCurrentItem() {
        return currentItem;
    }

    @Override
    public final Operation getCurrentOperation() {
        return currentOperation;
    }


    protected final Binder<Kont> getBinder() {
        return binder;
    }


    @Override
    public Component initDialogButtonBar() {
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
        );

        HorizontalLayout rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                saveAndCloseButton
                , revertAndCloseButton
        );

        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(leftBarPart, rightBarPart);
        bar.setClassName("buttons");

        return bar;
    }


    public void setDefaultItemNames() {
        setItemNames(ItemType.UNKNOWN);
    }


    protected void initControlsForItemAndOperation(final Kont item, final Operation oper) {
        setItemNames(item.getTyp());
        getMainTitle().setText(oper.getDialogTitle(getItemName(oper), itemGender));
        if (getCurrentItem() instanceof HasItemType) {
            getHeaderDevider().getStyle().set(
                    "background-color", VzmFormatUtils.getItemTypeColorBrighter(((HasItemType) item).getTyp()));
        }
        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
//        saveButton.setText(SAVE_STR + " " + getItemName(Operation.SAVE).toLowerCase());
    }


//    private String getHeaderEndComponentValue(final String titleEndText) {
//        String value = "";
//        if ((null == titleEndText) && (currentItem instanceof HasModifDates)) {
//            if (currentOperation == Operation.ADD) {
//                value = "";
//            } else {
//                LocalDate dateCreate = ((HasModifDates) currentItem).getDateCreate();
//                String dateCreateStr = null == dateCreate ? "" : dateCreate.format(VzmFormatUtils.basicDateFormatter);
//                LocalDateTime dateTimeUpdate = ((HasModifDates) currentItem).getDatetimeUpdate();
//                String dateUpdateStr = null == dateTimeUpdate ? "" : dateTimeUpdate.format(VzmFormatUtils.titleModifDateFormatter);
//                value = "[ Vytvořeno: " + dateCreateStr + ", Změna: " + dateUpdateStr + " ]";
//            }
//        }
//        return value;
//    }

    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
            initControlsOperability();
        }
    }

    private void revertFormChanges() {
        binder.removeBean();
        binder.readBean(currentItem);
        lastOperationResult = NO_CHANGE;
    }

    private void deleteClicked() {
        String ckDel = String.format("%s", currentItem.getCkont());
        if (!canDeleteKont(currentItem)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení kontraktu")
                    .withMessage(String.format("Kontrakt %s nelze zrušit, obsahuje zakázky / akvizice."
                            , ckDel))
                    .open()
            ;
            return;
        }
        revertFormChanges();
        try {
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení kontraktu")
                    .withMessage(String.format("Zrušit kontrakt %s ?", ckDel))
    //                .with...(,"Poznámka: Projektové a dokumentové adresáře včetně souborů zůstanou nezměněny.")
                    .withOkButton(() -> {
                            if (deleteKont(currentItem)) {
                                closeDialog();
                            }
                        }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        } catch (VzmServiceException e) {
            showDeleteErrMessage();
        }
    }

    private void saveClicked(boolean closeAfterSave) {
        if (!isKontValid()) {
            return;
        }
        try {
            currentItem = saveKont(currentItem);
            if (closeAfterSave) {
                closeDialog();
            } else {
                initKontDataAndControls(currentItem, currentOperation);
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private boolean saveWithoutClose() {
        if (!isKontValid()) {
            return false;
        }
        try {
            currentItem = saveKont(currentItem);
            if (Operation.ADD ==  currentOperation) {
                currentOperation = Operation.EDIT;
            }
            initKontDataAndControls(currentItem, currentOperation);
//            binder.removeBean();
//            binder.readBean(currentItem);
//            initControlsOperability(currentOperation, currentItem);
            return true;
        } catch (VzmServiceException e) {
            showSaveErrMessage();
            return false;
        }
    }

    private void closeDialog() {
        zakGrid.deselectAll(); // ..otherwise during next openDialog "$0.connector..." error appears
        kontDocGrid.deselectAll(); // ..otherwise during next openDialog "$0.connector..." error appears
        this.close();
    }

    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace kontraktu")
                .withMessage("Kontrakt se nepodařilo uložit.")
                .open();
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace kontraktu")
                .withMessage("Kontrakt se nepodařilo zrušit")
                .open()
        ;
    }

    private boolean isKontValid() {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (!isValid) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace kontraktu")
                    .withMessage("Kontrakt nelze uložit, některá pole nejsou správně vyplněna.")
                    .open();
            return false;
        }
        return true;
    }

    protected boolean canDeleteKont(final Kont itemToDelete) {
        return itemToDelete.getNodes().size() == 0;
    }

    protected boolean deleteKont(Kont itemToDelete) {
        String ckDel = String.format("%s", currentItem.getCkont());
        OperationResult lastOperResOrig = lastOperationResult;
        try {
            kontService.deleteKont(itemToDelete);
            lastOperationResult = OperationResult.ITEM_DELETED;
            return true;
        } catch (VzmServiceException e) {
            this.lastOperationResult = lastOperResOrig;
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení kontraktu.")
                    .withMessage(String.format("Kontrakt %s se nepodařilo zrušit.", ckDel))
                    .open()
            ;
            return false;
        }
    }


    public Kont saveKont(Kont kontToSave) throws VzmServiceException {
        OperationResult lastOperResOrig = lastOperationResult;
        try {
            if (StringUtils.isBlank(kontToSave.getFolder())) {
//                fireEvent(new AbstractField.ComponentValueChangeEvent(
//                        textField, textField, textField.getStringValue(), false)
//                );
//                fireEvent(new GeneratedVaadinTextField.ChangeEvent(textField, false));
                String textValue = textField.getValue();
                textField.clear();
                textField.setValue(textValue);
                kontToSave.setFolder(kontFolderField.getValue());
            }

            currentItem = kontService.saveKont(kontToSave, currentOperation);
//            kontToSave = kontService.saveKont(kontToSave, oper);
//            currentItem = kontSaved;
            if (needCreateKontDirs(currentItem, currentOperation)) {
                createKontDirs(currentItem);
            }
            lastOperationResult = OperationResult.ITEM_SAVED;
            return currentItem;
        } catch(VzmServiceException e) {
            lastOperationResult = lastOperResOrig;
            throw(e);
        }
    }

    private boolean needCreateKontDirs(final Kont itemToSave, final Operation oper) {
        return  (Operation.ADD == oper) ||
                ((Operation.EDIT == oper) && !(itemToSave.getFolder()).equals(evidKontOrig.getFolder()));
    }

    private boolean createKontDirs(final Kont kont) {
        if (StringUtils.isBlank(kont.getFolder())) {
            ConfirmDialog
                    .createError()
                    .withCaption("Adresáře kontraktu")
                    .withMessage("Složka kontraktu není zadána, nelze vytvořit adresáře")
                    .open();
            return false;
        }

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
        if (null == errMsg) {
            return true;
        } else {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Adresáře kontraktu")
                    .withMessage(errMsg)
                    .open()
            ;
            return false;
        }
    }

    public OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    public boolean isKontZaksChanged()  {
        return kontZaksChanged;
    }

    public boolean isKontZaksFaktsChanged()  {
        return kontZaksFaktsChanged;
    }

    public Kont getKontItemOrig()  {
        return kontItemOrig;
    }

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
                .withConverter(new VzmFormatUtils.ValidatedIntegerYearConverter())
                .bind(Kont::getRok, Kont::setRok);
        rokField.setValueChangeMode(ValueChangeMode.EAGER);
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

    private ComponentRenderer<Component, Kont> kontArchRenderer = new ComponentRenderer<>(kont -> {
        ArchIconBox archCheck = new ArchIconBox();
        archCheck.showIcon(kont.getTyp(), kont.getArchState());
        return archCheck;
    });

    private Component buildArchBox() {
        HorizontalLayout archBox = new HorizontalLayout();
        archBox.setAlignItems(FlexComponent.Alignment.BASELINE);
        archBox.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        archBox.add(initArchIconBox());
        archBox.add(new Span("Archiv"));
        return archBox;
    }

    private Component initArchIconBox() {
        archIconBox = new ArchIconBox();
        return archIconBox;
    }

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

    private Component initObjednatelCombo() {
        objednatelCombo = new ComboBox<>("Objednatel");
        objednatelCombo.getElement().setAttribute("colspan", "2");
        objednatelCombo.setItems(new ArrayList<>());
        objednatelCombo.setItemLabelGenerator(Klient::getName);
        return objednatelCombo;
    }

    private Component initInvestorField() {
        investorField = new TextField("Investor");
        investorField.getElement().setAttribute("colspan", "2");
        getBinder().forField(investorField)
//                .withConverter(String::trim, String::trim)    // TODO: Gives NPE for null
//                .bind(Kont::getInvestor, Kont::setInvestor);
                .withValidator(new StringLengthValidator(
                        "Investor může mít max. 127 znaků",
                        0, 127)
                )
                .bind(Kont::getInvestor, Kont::setInvestor);
        investorField.setValueChangeMode(ValueChangeMode.EAGER);
        return investorField;
    }

    private Component initMenaCombo() {
        menaCombo = new ComboBox<>("Měna");
        menaCombo.setItems(Mena.values());
        getBinder().forField(menaCombo)
//            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .withValidator(mena -> (null != mena) && Arrays.asList(Mena.values()).contains(mena)
                          ,"Měna musí být zadána")
            .bind(Kont::getMena, Kont::setMena);
        menaCombo.setPreventInvalidInput(true);
        return menaCombo;
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
        honorarCistyFieldCalc = new TextField("Honorář čistý");
        honorarCistyFieldCalc.setReadOnly(true);
        honorarCistyFieldCalc.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarCistyFieldCalc)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Kont::getHonorarCisty, null);
        return honorarCistyFieldCalc;
    }

    private Component initHonorarHrubyField() {
        honorarHrubyFieldCalc = new TextField("Honorář hrubý");
        honorarHrubyFieldCalc.setReadOnly(true);
        honorarHrubyFieldCalc.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarHrubyFieldCalc)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Kont::getHonorarHruby, null);
        return honorarHrubyFieldCalc;
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
                .bind(Kont::getFolder, Kont::setFolder)
        ;
        return kontFolderField;
    }

    private Component initKontFolderAnchor() {
        kontFolderAnchor = new Anchor("file:///C:/_Install/", "DIR");
        return kontFolderAnchor;
    }

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

    private Component initDocRefreshButton() {
        docRefreshButton = new ReloadButton("Načte adresáře", event -> {
            updateKontDocViewContent(null);
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
//                initUploadDocButton()
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
        kontDocGrid = new TreeGrid<>();
        kontDocGrid.setHeight("4em");
        kontDocGrid.setColumnReorderingAllowed(false);
        kontDocGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        kontDocGrid.setId("kont-doc-grid");
        kontDocGrid.setClassName("vizman-simple-grid");

        kontDocGrid.addItemDoubleClickListener(event -> {
            VzmFile vzmFile = event.getItem();
//            Notification.show(String.format("Folder %s Soubor: %s", event.getItem().getName(), event.getItem().getName())
//                    , 2500, Notification.Position.TOP_CENTER);
            fileViewerDialog.openDialog(vzmFile);
        });

        Grid.Column hCol = kontDocGrid.addColumn(fileIconTextRenderer);
        hCol.setHeader("Název")
                .setFlexGrow(1)
                .setWidth("30em")
                .setKey("kont-doc-file-name")
                .setResizable(true)
        ;
        return kontDocGrid;
    }

    TemplateRenderer fileIconTextRenderer = TemplateRenderer.<VzmFileUtils.VzmFile> of("<vaadin-grid-tree-toggle "
            + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
            + "<iron-icon style=\"[[item.icon-style]]\" icon=\"[[item.icon-name]]\"></iron-icon>&nbsp;&nbsp;"
            + "[[item.name]]"
            + "</vaadin-grid-tree-toggle>")
            .withProperty("leaf", file -> !kontDocGrid.getDataCommunicator().hasChildren(file))
            .withProperty("icon-name", file -> String.valueOf(vzmFileIconNameProvider.apply(file)))
            .withProperty("icon-style", file -> String.valueOf(vzmFileIconStyleProvider.apply(file)))
            .withProperty("name", File::getName)
            // Following dobleclick listener did notwork; grid listener used instead of it
//            .withEventHandler("doubleClick", item -> {
//                Notification.show(String.format("Soubor: %s", item.getName())
//                        , 2500, Notification.Position.TOP_CENTER);
//            })
        ;

    private void updateKontDocViewContent(final VzmFileUtils.VzmFile itemToSelect) {
//        kontDocTreeData = loadKzTreeData(archFilterRadio.getStringValue());
        kontDocGrid.deselectAll();
        TreeData<VzmFileUtils.VzmFile> kontDocTreeData
                = VzmFileUtils.getExpectedKontFolderTree(cfgPropsCache.getDocRootServer(), currentItem);

        Path kontDocRootPath = getKontDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getFolder());
        File kontDocRootDir = new File(kontDocRootPath.toString());
        addFilesToExpectedVzmTreeData(kontDocTreeData, kontDocRootDir.listFiles(), null);
//        addFilesToExpectedVzmTreeData(kontDocTreeData, kontDocTreeData.getChildren(null), null);

        addNotExpectedKontSubDirs(kontDocTreeData
                , new VzmFileUtils.VzmFile(kontDocRootPath, true, VzmFolderType.OTHER, 0)
        );
        addNotExpectedKontSubDirs(kontDocTreeData
                , new VzmFileUtils.VzmFile(getExpectedKontFolder(currentItem), true, VzmFolderType.OTHER, 0));
//        TreeDataProvider<File> kontDocTreeDataProvider = new TreeDataProvider(kontDocTreeData);

//        kontDocGrid.setTreeData(kontDocTreeData);

        assignDataProviderToGridAndSort(kontDocTreeData);
        kontDocGrid.getDataProvider().refreshAll();
//        if (null != itemToSelect) {
//            kontDocGrid.getSelectionModel().select(itemToSelect);
//        }
    }

    private void assignDataProviderToGridAndSort(TreeData<VzmFileUtils.VzmFile> kontDocTreeData) {
        List<GridSortOrder<VzmFileUtils.VzmFile>> sortOrderOrig = kontDocGrid.getSortOrder();
        kontDocGrid.setTreeData(kontDocTreeData);
        if (CollectionUtils.isEmpty(sortOrderOrig)) {
            kontDocGrid.sort(initialKontDocSortOrder);
        } else  {
            kontDocGrid.sort(sortOrderOrig);
        }
    }

    // ----------------------------------------------

    private Component initTitleComponent() {
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
        newZakButton = new NewItemButton(ItemNames.getNomS(ItemType.ZAK), event -> {
            if (saveWithoutClose()) {
                zakFormDialog.openDialog(false, new Zak(ItemType.ZAK, getCurrentItem().getNewCzak(), getCurrentItem())
                        , Operation.ADD);
//                , Operation.ADD, ItemNames.getNomS(ItemType.ZAK), new FlexLayout(), "")
            }
        });
        return newZakButton;
    }

    private Component initNewAkvButton() {
        newAkvButton = new NewItemButton(ItemNames.getNomS(ItemType.AKV), event -> {
            if (saveWithoutClose()) {
                zakFormDialog.openDialog(false, new Zak(ItemType.AKV, getCurrentItem().getNewCzak(), getCurrentItem())
                        , Operation.ADD);
            }
        });
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
                initTitleComponent(),
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
        zakGrid.addColumn(zakHonorarCistyCellRenderer)
                .setHeader("Honorář č.")
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
                    if (saveWithoutClose()) {
                        zakFormDialog.openDialog(false, zak, Operation.EDIT);
                    }
                }
                , VzmFormatUtils.getItemTypeColorName(zak.getTyp())
            );
            return btn;
//        }
    }

}
