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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontFormDialog extends AbstractKzDialog<Kont> implements HasLogger {
//public class KontFormDialog extends AbstractEditorDialog<Kont> implements HasLogger {
//public class KontFormDialog extends AbstractEditorDialog<Kont> implements BeforeEnterObserver {

    private final static String ZAK_EDIT_COL_KEY = "zak-edit-col";
    private final static String DELETE_STR = "Zrušit";
    private final static String SAVE_STR = "Uložit";
    public static final String DIALOG_WIDTH = "1300px";
    public static final String DIALOG_HEIGHT = "800px";

    //    final ValueProvider<Zak, String> honorProvider;
//    final ValueProvider<Zak, String> yearProvider;
    private final ComponentRenderer<HtmlComponent, Zak> zakHonorarCellRenderer =
            new ComponentRenderer<>(zak ->
                    VzmFormatUtils.getMoneyComponent(zak.getHonorar())
            );
    private final ComponentRenderer<HtmlComponent, Zak> zakHonorarCistyCellRenderer =
            new ComponentRenderer<>(zak ->
                    VzmFormatUtils.getMoneyComponent(zak.getHonorarCisty())
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
    private ArchIconBox archIconBox;

    private String kontFolderOrig;
//    private Kont kontOrig;
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


//    private Button saveButton;
    private Button revertButton;
    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
    private Button deleteAndCloseButton;
    private HorizontalLayout leftBarPart;

    private Binder<Kont> binder = new Binder<>();
    private Kont currentItem;
    private KzTreeAware kzItemOrig;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;
    private boolean zaksChanged = false;

    private Registration binderChangeListener = null;
    private Registration textFieldListener = null;
    private Registration ckontFieldListener = null;

    private KontService kontService;
    private ZakService zakService;
    private FaktService faktService;
    private KlientService klientService;
    private List<Klient> listOfKlients;
    private CfgPropsCache cfgPropsCache;

//    private Registration registrationForSave;
//    private BiConsumer<T, Operation> itemSaver;
//    private Consumer<T> itemDeleter;
//    private BiConsumer<Kont, Operation> itemSaver;

//    private boolean closeAfterSave;
//    private Consumer<Kont> newItemSaver;
//    private Consumer<Kont> modItemSaver;
//    private Consumer<Kont> itemDeleter;
//    private Consumer<Kont> formCloser;


    public KontFormDialog(
//            BiConsumer<Kont, Operation> kontSaver,
//                          Consumer<Kont> kontDeleter,
//                          Consumer<Kont> kontFormCloser,
//                          BiConsumer<Zak, Operation> zakSaver,
//                          Consumer<Kont> zakDeleter,
            KontService kontService,
            ZakService zakService,
            FaktService faktService,
            KlientService klientService,
            DochsumZakService dochsumZakService,
            CfgPropsCache cfgPropsCache
    ) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT, true, true);

//        this.dialogWidth = "1300px";
//        this.dialogHeight = "800px";
//        setWidth(dialogWidth);
//        setHeight(dialogHeight);
//        boolean useUpperRightPane = true;
//        boolean useLowerPane  = true;

//        this.newItemSaver = saveKont();
//        this.modItemSaver = this::saveKont;
//        this.itemDeleter = kontDeleter;
//        this.formCloser = kontFormCloser;

//        this.closeAfterSave = false;

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

        zakFormDialog = new ZakFormDialog(
                zakService, faktService, dochsumZakService, cfgPropsCache
        );
        zakFormDialog.addOpenedChangeListener(event -> {
//            System.out.println("OPEN-CHANGED: " + event.toString());
            if (!event.isOpened()) {
                finishZakEdit((ZakFormDialog) event.getSource());
            }
        });
        zakFormDialog.addDialogCloseActionListener(event -> {
//            System.out.println("DIALOG-CLOSE: " + event.toString());
            zakFormDialog.close();
//            finishKontEdit((KontFormDialog)event.getSource());
        });

        confirmDocUnregisterDialog = new ConfirmationDialog<>();
    }


    public void openDialog(Kont kont, Operation operation) {

        this.currentOperation = operation;
        this.currentItem = kont;
        this.zaksChanged = false;
        this.kzItemOrig = kont;

        listOfKlients = klientService.fetchAll();
//        // Following series of commands replacing combo box is because of a bug
//        // Initialize $connector if values were not set in ComboBox element prior to page load. #188
        binder.removeBinding(objednatelCombo);
        getFormLayout().remove(objednatelCombo);
        getFormLayout().addComponentAtIndex(3, initObjednatelCombo());
        objednatelCombo.setItems(this.listOfKlients);
        getBinder().forField(objednatelCombo)
                .bind(Kont::getKlient, Kont::setKlient);
        objednatelCombo.setPreventInvalidInput(true);

//        this.objednatelCombo.setItems(new ArrayList<>());
//        this.objednatelCombo.setItems(listOfKlients);

//        getFormLayout().remove(menaCombo);
//        getFormLayout().addComponentAtIndex(3, initMenaCombo());
//        menaCombo.setItems(EUR, CZK);

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

        this.zakGrid.setItems(kontItem.getNodes());
        this.docGrid.setItems(kontItem.getKontDocs());
        this.kontFolderField.setParentFolder(null);
        this.kontFolderField.setItemType(kontItem.getTyp());

        initHeaderMiddleComponent(kontItem);
        getHeaderEndComponent().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(kontItem, kontOperation);
        initControlsOperability(kontOperation, kontItem);

        activateListeners();
    }

    private void initHeaderMiddleComponent(Kont kontItem) {
        FlexLayout headerMiddleComponent = new FlexLayout();
        headerMiddleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerMiddleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerMiddleComponent.add(
                buildArchBox()
                , new Gap("5em")
                , VzmFormatUtils.buildAvizoComponent(kontItem.getBeforeTerms(), kontItem.getAfterTerms(), true)
        );
        getMiddleComponentBox().removeAll();
        if (null != headerMiddleComponent) {
            getMiddleComponentBox().add(headerMiddleComponent);
        }
        archIconBox.showIcon(kontItem.getTyp(), kontItem.getArchState());
    }

    private void deactivateListeners() {
        if (null != binderChangeListener) {
            binderChangeListener.remove();
        }
        if (null != textFieldListener) {
            textFieldListener.remove();
        }
        if (null != ckontFieldListener) {
            ckontFieldListener.remove();
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

    private void initControlsOperability(final Operation kontOperation, final Kont  kontItem) {
        saveAndCloseButton.setEnabled(false);
//        saveButton.setEnabled(false);
        revertButton.setEnabled(false);
        newAkvButton.setEnabled(true);
        newZakButton.setEnabled(true);
        deleteAndCloseButton.setEnabled(kontOperation.isDeleteEnabled() && canDeleteKont(kontItem));
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
        Zak zak = zakFormDialog.getCurrentItem(); // Modified, just added or just deleted
        String ckz = String.format("%s / %s", zak.getCkont(), zak.getCzak());
        Operation oper = zakFormDialog.getCurrentOperation();
        OperationResult zakOperRes = zakFormDialog.getLastOperationResult();
        boolean faktsChanged = zakFormDialog.isFaktsChanged();

        if (OperationResult.NO_CHANGE == zakOperRes) {
            return;
        }
        if (OperationResult.ITEM_SAVED == zakOperRes || faktsChanged) {
            Notification.show(String.format("Zakázka %s uložena", ckz)
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == zakOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace zakázky")
                    .withMessage(String.format("Zakázka %s zrušena.", ckz))
                    .open();
        }

        syncFormGridAfterZakEdit(zak, oper, zakOperRes, faktsChanged);

    }

    private void syncFormGridAfterZakEdit(Zak zakAfter, Operation oper, OperationResult operRes, boolean  faktsChanged) {

        if ((OperationResult.NO_CHANGE == operRes) && !faktsChanged) {
            return;
        }

        List<Zak> zaks = getCurrentItem().getZaks();

//        if (Operation.EDIT == oper) {
//            zaks.removeItem(modZak);
//            zak.removeItem(modZak);
//        }
//
//        if (OperationResult.ITEM_DELETED != operRes) {
//            int itemIndex = zaks.indexOf(modZak);
//            if (itemIndex != -1) {
//                getCurrentItem().getZaks().set(itemIndex, dialogZak);
//            }
//

//        Zak dialogZak = zakFormDialog.getCurrentItem(); // Modified, just added or just deleted
//        Operation oper = zakFormDialog.getCurrentOperation();
//        OperationResult operRes = zakFormDialog.getLastOperationResult();
//        if (OperationResult.NO_CHANGE == operRes) {
//            return;
//        }
//        String ckz = String.format("%s / %s", modZak.getCkont(), modZak.getCzak());
        if (Operation.ADD == oper) {
//            zaks.add(0, modZak);
            currentItem.addZakOnTop(zakAfter);
            zaksChanged = true;
        } else if (Operation.EDIT == oper) {
            if (OperationResult.ITEM_DELETED == operRes) {
                currentItem.removeZak(zakAfter);
                zaksChanged = true;
            } else if (OperationResult.ITEM_SAVED == operRes) {
                int itemIndex = zaks.indexOf(zakAfter);
                if (itemIndex != -1) {
                    zaks.set(itemIndex, zakAfter);
                    zaksChanged = true;
                }
//                int itemIndex = zaks.indexOf(modZak);
//                if (itemIndex != -1) {
//                    zaks.remove(itemIndex);
//                }
            }
        }
        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
        zakGrid.setItems(zaks);
        zakGrid.getDataProvider().refreshAll();
        zakGrid.select(zakAfter);

        if (faktsChanged || (OperationResult.NO_CHANGE != operRes)) {
            // TODO: update KONT calculated fields
            binder.readBean(currentItem);
        }

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


    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            this.close();
        } else {
            initControlsOperability(currentOperation, currentItem);
        }
    }

    private void revertFormChanges() {
        binder.removeBean();
        binder.readBean(currentItem);
        lastOperationResult = OperationResult.NO_CHANGE;
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
                    .withOkButton(() -> deleteKont(currentItem)
                            , ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                    )
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
            this.close();
        } catch (VzmServiceException e) {
            showDeleteErrMessage();
        }
    }

    private void saveClicked(boolean closeAfterSave) {
        if (!isKontValid()) {
            return;
        }
        try {
            currentItem = saveKont(currentItem, currentOperation);
            if (closeAfterSave) {
                this.close();
            } else {
                initKontDataAndControls(currentItem, currentOperation);
//                binder.removeBean();
//                binder.readBean(currentItem);
//                initControlsOperability(currentOperation, currentItem);
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
            currentItem = saveKont(currentItem, currentOperation);
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
        try {
            kontService.deleteKont(itemToDelete);
            lastOperationResult = OperationResult.ITEM_DELETED;
            return true;
        } catch (VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení kontraktu.")
                    .withMessage("Kontrakt " + itemToDelete.getCkont() + " se nepodařilo zrušit.")
                    .open()
            ;
            return false;
        }

        //            GenericModel bean = myGrid.getSelectedRow();
        //            ListDataProvider<GenericModel> dataProvider=(ListDataProvider<GenericModel>) myGrid.getDataProvider();
        //            List<GenericModel> ItemsList=(List<GenericModel>) dataProvider.getItems();

        //            kzTreeGrid.getDataCommunicator().getKeyMapper().removeAll();
        //            kzTreeGrid.getDataProvider().refreshAll();
        //
        //            HierarchicalDataProvider dataProvider = kzTreeGrid.getDataProvider();
        //            List<KzTreeAware> itemsList =(List<KzTreeAware>) dataProvider. getItems();
        //            int index=itemsList.indexOf(bean);//index of the selected item
        //            GenericModel newSelectedBean=itemsList.get(index+1);
        //            dataProvider.getItems().remove(bean);
        //            dataProvider.refreshAll();
        //            myGrid.select(newSelectedBean);
        //            myGrid.scrollTo(index+1);

    }


    public Kont
    saveKont(Kont kontToSave, Operation oper) throws VzmServiceException {
        try {
            if (StringUtils.isBlank(kontToSave.getFolder())) {
//                fireEvent(new AbstractField.ComponentValueChangeEvent(
//                        textField, textField, textField.getValue(), false)
//                );
//                fireEvent(new GeneratedVaadinTextField.ChangeEvent(textField, false));
                String textValue = textField.getValue();
                textField.clear();
                textField.setValue(textValue);
                kontToSave.setFolder(kontFolderField.getValue());
            }

            currentItem = kontService.saveKont(kontToSave, oper);
//            kontToSave = kontService.saveKont(kontToSave, oper);
//            currentItem = kontSaved;
            if (kontDirsToBeCreated(currentItem, oper)) {
                createKontDirs(currentItem);
            }
            lastOperationResult = OperationResult.ITEM_SAVED;
            return currentItem;
        } catch(VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            throw(e);
        }
    }

    private boolean kontDirsToBeCreated(final Kont itemToSave, final Operation oper) {
        return  (Operation.ADD == oper) ||
                ((Operation.EDIT == oper) && !(itemToSave.getFolder()).equals(evidKontOrig.getFolder()));

//                                ((null != folder) && (null != evidKontOrig) && (folder.equals(evidKontOrig.getFolder())) ||
//                        !VzmFileUtils.kontDocRootExists(cfgPropsCache.getDocRootServer(), folder))
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

    public boolean isZaksChanged()  {
        return zaksChanged;
    }

    public KzTreeAware getKzItemOrig()  {
        return kzItemOrig;
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

//    private void saveZakForForm(Zak zak, Operation operation) {
//
//        Zak savedZak = zakFormDialog.saveZak(zak, operation);
//
////        if (Operation.EDIT == operation && null != zakFolderOrig && !zakFolderOrig.equals(zak.getFolder())) {
////            if (!VzmFileUtils.kontDocRootExists(getDocRootServer(), zak.getFolder())) {
////                new OkDialog().open("Adresáře zakázky"
////                        , "POZOR, dokumentový ani projektový adresář se automaticky nepřejmenovávají.", "");
////            }
////            if (!VzmFileUtils.kontProjRootExists(getProjRootServer(), zak.getFolder())) {
////                new OkDialog().open("Projektový adresáře zakázky"
////                        , "POZOR, projektový adresář zakázky nenalezen, měl by se přejmenovat ručně", "");
////            }
////        } else if (Operation.ADD == operation){
////            if (!VzmFileUtils.createZakDocDirs(
////                        cfgPropsCache.getDocRootServer(), zak.getKontFolder(), zak.getFolder())) {
////                new OkDialog().open("Dokumentové adresáře zakázky"
////                        , "Adresářovou strukturu se nepodařilo vytvořit", "");
////            };
////            if (!VzmFileUtils.createZakProjDirs(
////                        cfgPropsCache.getProjRootServer(), zak.getKontFolder(), zak.getFolder())) {
////                new OkDialog().open("Projektové adresáře zakázky"
////                        , "Adresářovou strukturu se nepodařilo vytvořit", "");
////            };
////    //            File kontProjRootDir = Paths.get(getProjRootServer(), kont.getFolder()).toFile();
////    //            kontProjRootDir.setReadOnly();
////        } else {
////            new OkDialog().open("Adresáře zakázky"
////                    , "NEZNÁMÁ OPERACE", "")
////            ;
////        }
////
////        Zak savedZak = zakService.saveZak(zak);
////        new OkDialog().open("Zakázka " + savedZak.getKont().getCkont() + " / " + savedZak.getCzak() + " uložena"
////                , "", "");
//
//
//
////        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
//        if (Operation.ADD == operation) {
//            getCurrentItem().getZaks().add(0, savedZak);
//        } else {
//            int zakItemIndex = getCurrentItem().getZaks().indexOf(savedZak);
//            if (zakItemIndex != -1) {
//                getCurrentItem().getZaks().set(zakItemIndex, savedZak);
//            }
//        }
//        zakGrid.setItems(getCurrentItem().getZaks());
//
//        zakGrid.getDataCommunicator().getKeyMapper().removeAll();
//        zakGrid.getDataProvider().refreshAll();
//        zakGrid.select(savedZak);
//
//        Notification.show("Zakázka " + savedZak.getKont().getCkont() + " / " + savedZak.getCzak() + " uložena"
//                , 2500, Notification.Position.TOP_CENTER);
//
////        Notification.show(
////                "Změny zakázky uloženy", 3000, Notification.Position.TOP_CENTER);
//    }

//    private void deleteZakForForm(Zak zak) {
//        String ckzDel = String.format("%s / %d", zak.getCkont(), zak.getCzak());
//        try {
//            boolean zakWasDeleted = zakService.deleteZak(zak);
//
//            if (!zakWasDeleted) {
//                ConfirmDialog
//                        .createError()
//                        .withCaption("Zrušení zakázky")
//                        .withMessage(String.format("Chyba při rušení zakázky %s .", ckzDel))
//                        .open();
//            } else {
//                getCurrentItem().getZaks().removeIf(z -> z.getId().equals(zak.getId()));
//                zakGrid.setItems(getCurrentItem().getZaks());
//                zakGrid.getDataCommunicator().getKeyMapper().removeAll();
//                zakGrid.getDataProvider().refreshAll();
//                getLogger().info(String.format("ZAKAZKA %s deleted", ckzDel));
//                Notification.show(String.format("Zakázka %s zrušena.", ckzDel)
//                        , 2500, Notification.Position.TOP_CENTER)
//                ;
//                ConfirmDialog
//                        .createInfo()
//                        .withCaption("Zrušení zakázky")
//                        .withMessage(String.format("Zakázka %s byla zrušena.", ckzDel))
//                        .open()
//                ;
//            }
//        } catch (Exception e) {
//            getLogger().error(String.format("Error during deletion ZAKAZKA %s / %d", zak.getCkont(), zak.getCzak()), e);
//            ConfirmDialog
//                    .createError()
//                    .withCaption("Zrušení zakázky")
//                    .withMessage(String.format("Chyba při rušení zakázky %s .", ckzDel))
//                    .open()
//            ;
//        }
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
//        archBox.showIcon(ItemType.KONT == kz.getTyp() ?
//                (kz.getArch() ? icoKontArchived : icoKontActive)
//                : kz.getArch() ? icoZakArchived : new Span();
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
//        archBox.showIcon(ItemType.KONT == kz.getTyp() ?
//                (kz.getArch() ? icoKontArchived : icoKontActive)
//                : kz.getArch() ? icoZakArchived : new Span();
//        return archCheck;
    }


//    private Component initArchCheck() {
//        archCheck = new Checkbox("Archiv");
//        archCheck.getElement().setAttribute("theme", "secondary");
//        archCheck.setReadOnly(true);
//        getBinder().forField(archCheck)
//                .bind(Kont::getArch, null);
//        return archCheck;
//    }


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

//        getBinder().forField(objednatelCombo)
////            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
////                .withValidator(klient -> (null != klient) && listOfKlients.contains(klient)
////                        ,"Klient musí být zadán")
//                .bind(Kont::getKlient, Kont::setKlient);
//        objednatelCombo.setPreventInvalidInput(true);

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

//                .withValidator(
//                    folder ->
//                        ((Operation.ADD == currentOperation) &&
//                                !VzmFileUtils.kontDocRootExists(cfgPropsCache.getDocRootServer(), folder))
//                        ||
//                        ((Operation.EDIT == currentOperation) &&
////                                ((null != folder) && (null != evidKontOrig) && (folder.equals(evidKontOrig.getFolder())) ||
//                                ((folder.equals(evidKontOrig.getFolder())) ||
//                                        !VzmFileUtils.kontDocRootExists(cfgPropsCache.getDocRootServer(), folder))
//                        )
//                    , "Dokumentový adresář kontraktu stejného jména již existuje, změň číslo kontraktu nebo text."
//                )
//                .withValidator(
//                    folder ->
//                        ((Operation.ADD == currentOperation) &&
//                                !VzmFileUtils.kontProjRootExists(cfgPropsCache.getProjRootServer(), folder))
//                        ||
//                        ((Operation.EDIT == currentOperation) &&
////                                ((null != folder) && (null != evidKontOrig) && (folder.equals(evidKontOrig.getFolder())) ||
//                                ((folder.equals(evidKontOrig.getFolder())) ||
//                                        !VzmFileUtils.kontProjRootExists(cfgPropsCache.getProjRootServer(), folder)
//                                )
//                        )
//                    , "Projektový adresář kontraktu stejného jména již existuje, číslo kontraktu nebo text."
//                )

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
        newZakButton = new NewItemButton(ItemNames.getNomS(ItemType.ZAK), event -> {
            if (saveWithoutClose()) {
                zakFormDialog.openDialog(new Zak(ItemType.ZAK, getCurrentItem().getNewCzak(), getCurrentItem())
                        , Operation.ADD);
//                , Operation.ADD, ItemNames.getNomS(ItemType.ZAK), new FlexLayout(), "")
            }
        });
        return newZakButton;
    }

    private Component initNewAkvButton() {
        newAkvButton = new NewItemButton(ItemNames.getNomS(ItemType.AKV), event -> {
            if (saveWithoutClose()) {
                zakFormDialog.openDialog(new Zak(ItemType.AKV, getCurrentItem().getNewCzak(), getCurrentItem())
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
                        zakFormDialog.openDialog(zak, Operation.EDIT);
                    }
                }
                , VzmFormatUtils.getItemTypeColorName(zak.getTyp())
            );
            return btn;
//        }
    }

}
