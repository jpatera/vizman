package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.KontView;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.NabViewService;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconNameProvider;
import static eu.japtor.vizman.backend.utils.VzmFormatUtils.vzmFileIconStyleProvider;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NabFormDialog extends AbstractComplexFormDialog<Nab> {

//    private ComboBox<PersonState> statusField; // = new ComboBox("Status");
    private Checkbox vzCheckBox;
    private TextField rokField;
    private TextField cnabField;
    private TextField textField;
    private ComboBox<Klient> objednatelCombo;
    private ComboBox<KontView> ckontCombo;
    private TextField poznamkaField;
    private DatePicker nastupField; // = new DatePicker("Nástup");
    private DatePicker vystupField; // = new DatePicker("Výstup");

//    private Binder<Nab> binder = new Binder<>();
    private Registration binderChangeListener = null;
//    private Nab currentItem;
    private Nab origItemCopy;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;
    private boolean readOnly;


    private NabViewService nabViewService;
    private KontService kontService;
    private KlientService klientService;
    private List<Klient> klientList;
    private List<KontView> kontViewList;

    private FlexLayout nabDocFolderComponent;
    private NabFolderField nabFolderField;
    private TreeGrid<VzmFileUtils.VzmFile> nabDocGrid;
    private Button docRefreshButton;
    private FileViewerDialog fileViewerDialog;
    //    private List<GridSortOrder<VzmFileUtils.VzmFile>> initialZakDocSortOrder;
    private CfgPropsCache cfgPropsCache;


    public NabFormDialog(
            BiConsumer<Nab, Operation> itemSaver
            , Consumer<Nab> itemDeleter
            , NabViewService nabViewService
            , KontService kontService
            , KlientService klientService
            , CfgPropsCache cfgPropsCache
    ){
        super("1100px", null
                , true
                , false
                , itemSaver
                , itemDeleter
                , true
        );

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.nabViewService = nabViewService;
        this.kontService = kontService;
        this.klientService = klientService;
        this.cfgPropsCache = cfgPropsCache;

        getFormLayout().add(
                initRokField()
                , initCnabField()
                , initTextField()
                , initObjednatelCombo()
                , initCkontCombo()
                , initPoznamkaField()
        );
        initVzCheckBox();

        getUpperRightPane().add(
                initNabDocFolderComponent()
                , initDocGridBar()
                , initDocGrid()
        );
        fileViewerDialog = new FileViewerDialog();
    }

    private Component initNabDocFolderComponent() {
        nabDocFolderComponent = new FlexLayout();
        nabDocFolderComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        nabDocFolderComponent.setWidth("100%");
        nabDocFolderComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        nabDocFolderComponent.add(initNabFolderField());
        return nabDocFolderComponent;
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
        docGridTitle.setText("Tady budou pédéefka");
        return docGridTitle;
    }

    private Component initDocRefreshButton() {
        docRefreshButton = new ReloadButton("Načte adresáře", event -> {
            updateNabDocViewContent(null);
        });
        return docRefreshButton;
    }

    private void updateNabDocViewContent(final VzmFileUtils.VzmFile itemToSelect) {
//        nabDocGrid.deselectAll();
//        Path kontDocRootPath = getKontDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getKontFolder());
//        TreeData<VzmFileUtils.VzmFile> zakDocTreeData
//                = VzmFileUtils.getExpectedZakDocFolderFilesTree(kontDocRootPath.toString(), currentItem);
//
//        Path zakDocRootPath = getZakDocRootPath(cfgPropsCache.getDocRootServer(), currentItem.getKontFolder(), currentItem.getFolder());
//        File zakDocRootDir = new File(zakDocRootPath.toString());
//        addFilesToExpectedVzmTreeData(zakDocTreeData, zakDocRootDir.listFiles(), null);
//
//        addNotExpectedKontSubDirs(zakDocTreeData
//                , new VzmFileUtils.VzmFile(zakDocRootPath, true, VzmFolderType.OTHER, 0)
//        );
//        addNotExpectedKontSubDirs(zakDocTreeData
//                , new VzmFileUtils.VzmFile(getExpectedZakFolder(currentItem), true, VzmFolderType.OTHER, 0));
//
//        assignDataProviderToGridAndSort(zakDocTreeData);
//        nabDocGrid.getDataProvider().refreshAll();
////        if (null != itemToSelect) {
////            kontDocGrid.getSelectionModel().select(itemToSelect);
////        }
    }

    private Component initDocGrid() {
        nabDocGrid = new TreeGrid<>();
        nabDocGrid.setHeight("3em");
        nabDocGrid.setColumnReorderingAllowed(false);
        nabDocGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        nabDocGrid.setId("nab-doc-grid");
        nabDocGrid.setClassName("vizman-simple-grid");

        nabDocGrid.addItemDoubleClickListener(event -> {
            VzmFileUtils.VzmFile vzmFile = event.getItem();
            fileViewerDialog.openDialog(vzmFile);
        });

        Grid.Column hCol = nabDocGrid.addColumn(fileIconTextRenderer);
        hCol.setHeader("Název")
                .setFlexGrow(1)
                .setWidth("30em")
                .setKey("nab-doc-file-name")
                .setResizable(true)
        ;

        return nabDocGrid;
    }

    private TemplateRenderer fileIconTextRenderer = TemplateRenderer.<VzmFileUtils.VzmFile> of("<vaadin-grid-tree-toggle "
            + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
            + "<iron-icon style=\"[[item.icon-style]]\" icon=\"[[item.icon-name]]\"></iron-icon>&nbsp;&nbsp;"
            + "[[item.name]]"
            + "</vaadin-grid-tree-toggle>")
            .withProperty("leaf", file -> !nabDocGrid.getDataCommunicator().hasChildren(file))
            .withProperty("icon-name", file -> String.valueOf(vzmFileIconNameProvider.apply(file)))
            .withProperty("icon-style", file -> String.valueOf(vzmFileIconStyleProvider.apply(file)))
            .withProperty("name", File::getName)
            ;

    private Component initNabFolderField() {
        nabFolderField = new NabFolderField(
                null
                , ItemType.UNKNOWN
                , cfgPropsCache.getDocRootLocal()
                , cfgPropsCache.getProjRootLocal()
        );
        nabFolderField.setWidth("100%");
        nabFolderField.getStyle().set("padding-top", "0em");
        nabFolderField.setReadOnly(true);
        getBinder().forField(nabFolderField)
//                .withValidator(
//                        folder ->
////                                ((Operation.ADD == currentOperation) && StringUtils.isNotBlank(folder))
//                                // TODO: check add to beta 1.3
//                                (StringUtils.isNotBlank(getCurrentItem().getCkont()) && null != getCurrentItem().getCzak())
//                                        || (StringUtils.isNotBlank(folder))
//                        , "Složka zakázky není definována, je třeba zadat číslo a text zakázky"
//                )
//                .withValidator(
//                        folder ->
//                                // TODO: check add to beta 1.3
//                                (StringUtils.isBlank(folder)) ||
//                                        ((Operation.ADD == currentOperation) &&
//                                                !VzmFileUtils.zakDocRootExists(cfgPropsCache.getDocRootServer(), kontFolder, folder))
//                                        ||
//                                        ((Operation.EDIT == currentOperation) &&
//                                                ((folder.equals(evidZakOrig.getFolder())) ||
//                                                        !VzmFileUtils.zakDocRootExists(cfgPropsCache.getDocRootServer(), kontFolder, folder))
//                                        )
//                        , "Dokumentový adresář zakázky stejného jména již existuje, změň text zakázky."
//                )
//                .withValidator(
//                        folder ->
//                                // TODO: check add to beta 1.3
//                                (StringUtils.isBlank(folder)) ||
//                                        ((Operation.ADD == currentOperation) &&
//                                                !VzmFileUtils.zakProjRootExists(cfgPropsCache.getProjRootServer(), kontFolder, folder))
//                                        ||
//                                        ((Operation.EDIT == currentOperation) &&
//                                                ((folder.equals(evidZakOrig.getFolder())) ||
//                                                        !VzmFileUtils.zakProjRootExists(cfgPropsCache.getProjRootServer(), kontFolder, folder))
//                                        )
//                        , "Projektový adresář zakázky stejného jména již existuje, změň text zakázky."
//                )

                .bind(Nab::getFolder, Nab::setFolder);

        return nabFolderField;
    }


    public void openDialog(
            boolean readonly
            , Nab nab
            , Operation operation
    ){
        this.readOnly = readonly;
//        this.currentItem = nab;
        this.origItemCopy = Nab.getNewInstance(nab);
        this.currentOperation = operation;

//        setItemNames(nabView.getTyp());

        klientList = klientService.fetchAll();
        kontViewList = kontService.fetchAllFromView();

//        // Following series of commands replacing combo box are here because of a bug
//        // Initialize $connector if values were not set in ComboBox element prior to page load. #188
        // TODO: checkout if the bug is fixed
        fixObjednatelComboOpening();
        fixCkontComboOpening();

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

//        this.kontFolderOrig = kontFolderOrig;

        openInternal(nab, operation, false, true, new Gap(), null);
    }


    private Component initVzCheckBox() {
        vzCheckBox = new Checkbox("Veřejná zak."); // = new TextField("Username");
        vzCheckBox.getElement().setAttribute("theme", "secondary");
        getBinder().forField(vzCheckBox)
                .bind(Nab::getVz, Nab::setVz);
        return vzCheckBox;
    }

    private Component initRokField() {
        rokField = new TextField("Rok nabídky");
        getBinder().forField(rokField)
                .withConverter(new VzmFormatUtils.ValidatedIntegerYearConverter())
                .bind(Nab::getRok, Nab::setRok);
        rokField.setValueChangeMode(ValueChangeMode.LAZY);
        return rokField;
    }

    private Component initCnabField() {
        cnabField = new TextField("Číslo nabídky");
        getBinder().forField(cnabField)
                .withValidator(new StringLengthValidator(
                        "Číslo nabídky musí mít 7 znaků",
                        7, 7))
                .withValidator(
                        cnab -> (currentOperation != Operation.ADD) ?
                            true : nabViewService.fetchNabByCnab(cnab) == null,
                        "Nabídka s tímto číslem již existuje, zvol jiné")
                .bind(Nab::getCnab, Nab::setCnab);
        cnabField.setValueChangeMode(ValueChangeMode.LAZY);
        return cnabField;
    }

    private Component initObjednatelCombo() {
        objednatelCombo = new ComboBox<>("Objednatel");
        objednatelCombo.getElement().setAttribute("colspan", "1");
        objednatelCombo.setItems(new ArrayList<>());
        objednatelCombo.setItemLabelGenerator(Klient::getName);
        return objednatelCombo;
    }

    private void fixObjednatelComboOpening() {
        getBinder().removeBinding(objednatelCombo);
        getFormLayout().remove(objednatelCombo);
        getFormLayout().addComponentAtIndex(3, initObjednatelCombo());
        objednatelCombo.setItems(this.klientList);
        getBinder().forField(objednatelCombo)
                .bind(Nab::getKlient, Nab::setKlient);
        objednatelCombo.setPreventInvalidInput(true);
    }

    private Component initCkontCombo() {
        ckontCombo = new ComboBox<>("Kontrakt");
        ckontCombo.getElement().setAttribute("colspan", "1");
        ckontCombo.setItems(new ArrayList<>());
        ckontCombo.setItemLabelGenerator(KontView::getCkont);
        return ckontCombo;
    }

    private void fixCkontComboOpening() {
        getBinder().removeBinding(ckontCombo);
        getFormLayout().remove(ckontCombo);
        getFormLayout().addComponentAtIndex(4, initCkontCombo());
        ckontCombo.setItems(this.kontViewList);
        getBinder().forField(ckontCombo)
                .bind(Nab::getKont, Nab::setKont);
        ckontCombo.setPreventInvalidInput(true);
    }

    private Component initTextField() {
        textField = new TextField("Text");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Nab::getText, Nab::setText);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        return textField;
    }

    private Component initPoznamkaField() {
        poznamkaField = new TextField("Poznámka");
        poznamkaField.getElement().setAttribute("colspan", "2");
        getBinder().forField(poznamkaField)
                .bind(Nab::getPoznamka, Nab::setPoznamka);
        poznamkaField.setValueChangeMode(ValueChangeMode.EAGER);
        return poznamkaField;
    }


    @Override
    protected void confirmDelete() {
        openConfirmDeleteDialog("Zrušení nabídky",
                "Opravdu zrušit nabídku “" + getCurrentItem().getCnab() + "“ ?", ""
        );
    }



//    private void revertClicked(boolean closeAfterRevert) {
//        revertFormChanges();
//        if (closeAfterRevert) {
//            closeDialog();
//        } else {
//            initControlsOperability();
//        }
//    }

//    private void saveClicked(boolean closeAfterSave) {
//        if (!isFaktValid()) {
//            return;
//        }
//        try {
//            currentItem = saveFakt(currentItem);
//            if (closeAfterSave) {
//                closeDialog();
//            } else {
//                initDataAndControls(currentItem, currentOperation);
//            }
//        } catch (VzmServiceException e) {
//            showSaveErrMessage();
//        }
//    }

//    private void deleteClicked() {
//        String cnDel = String.format("%s", currentItem.getCnab());
//        if (!canDeleteNab(currentItem)) {
//            ConfirmDialog
//                    .createInfo()
//                    .withCaption("Zrušení nabídky")
//                    .withMessage(String.format("Nabídku %s nelze zrušit.", cnDel))
//                    .open()
//            ;
//            return;
//        }
//        try {
//            revertFormChanges();
//            ConfirmDialog.createQuestion()
//                    .withCaption("Zrušení nabídky")
//                    .withMessage(String.format("Zrušit nabídku %s ?", cnDel))
//                    .withOkButton(() -> {
//                                if (deleteFakt(currentItem)) {
//                                    closeDialog();
//                                }
//                            }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
//                    )
//                    .withCancelButton(ButtonOption.caption("ZPĚT"))
//                    .open()
//            ;
//        } catch (VzmServiceException e) {
//            showDeleteErrMessage();
//        }
//    }

//    private void showSaveErrMessage() {
//        ConfirmDialog.createError()
//                .withCaption("Editace nabídky")
//                .withMessage("Nabídku se nepodařilo uložit.")
//                .open();
//    }
//
//    private void showDeleteErrMessage() {
//        ConfirmDialog.createError()
//                .withCaption("Editace nabídky")
//                .withMessage("Nabídku se nepodařilo zrušit")
//                .open()
//        ;
//    }

//    private void revertFormChanges() {
//        binder.removeBean();
//        binder.readBean(currentItem);
//        lastOperationResult = OperationResult.NO_CHANGE;
//    }

//    private boolean deleteFakt(NabVw itemToDelete) {
//        String cnDel = String.format("%s", currentItem.getCnab());
//        OperationResult lastOperResOrig = lastOperationResult;
//        try {
//            nabViewService.deleteNab(itemToDelete);
//            lastOperationResult = OperationResult.ITEM_DELETED;
//            return true;
//        } catch (VzmServiceException e) {
//            this.lastOperationResult = lastOperResOrig;
//            ConfirmDialog
//                    .createWarning()
//                    .withCaption("Zrušení nabídky.")
//                    .withMessage(String.format("Nabídku %s se nepodařilo zrušit.", cnDel))
//                    .open()
//            ;
//            return false;
//        }
//    }

//    public NabVw saveFakt(NabVw nabToSave) throws VzmServiceException {
//        try {
//            currentItem = nabViewService.saveNab(nabToSave, currentOperation);
//            lastOperationResult = OperationResult.ITEM_SAVED;
//            return currentItem;
//        } catch(VzmServiceException e) {
//            lastOperationResult = OperationResult.NO_CHANGE;
//            throw(e);
//        }
//    }

//    private boolean isFaktValid() {
//        boolean isValid = binder.writeBeanIfValid(currentItem);
//        if (!isValid) {
//            ConfirmDialog
//                    .createWarning()
//                    .withCaption("Editace subdodávky")
//                    .withMessage("Subdodávku nelze uložit, některá pole nejsou správně vyplněna.")
//                    .open();
//            return false;
//        }
//        return true;
//    }

//    private boolean canDeleteNab(final NabVw itemToDelete) {
//        return true;
//    }

    @Override
    protected void refreshHeaderMiddleBox(Nab item) {
        FlexLayout headerMiddleComponent = new FlexLayout();
        headerMiddleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerMiddleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerMiddleComponent.add(
                vzCheckBox
        );
        getHeaderMiddleBox().removeAll();
        if (null != headerMiddleComponent) {
            getHeaderMiddleBox().add(headerMiddleComponent);
        }
    }

    @Override
    protected void activateListeners() {
        if (null != getBinder()) {
            binderChangeListener = getBinder().addValueChangeListener(e -> {
                adjustControlsOperability(false, true, true, isDirty(),  getBinder().isValid());
            });
        }
    }

    @Override
    protected void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
    }


    @Override
    public void initControlsOperability(final boolean readOnly, final boolean deleteAllowed, final boolean canDelete) {
        super.initControlsOperability(readOnly, deleteAllowed, canDelete);
        rokField.setReadOnly(readOnly);
        cnabField.setReadOnly(readOnly);
        vzCheckBox.setReadOnly(readOnly);
        textField.setReadOnly(readOnly);
        objednatelCombo.setReadOnly(readOnly);
        poznamkaField.setReadOnly(readOnly);
    }

    @Override
    public void adjustControlsOperability(
            final boolean readOnly
            , final boolean deleteAllowed
            , final boolean canDelete
            , final boolean hasChanges
            , final boolean isValid
    ) {
        super.adjustControlsOperability(readOnly, deleteAllowed, canDelete, hasChanges, isValid);
    }

    private boolean canDeleteItem(final Nab itemToDelete) {
        return true;
    }


    public Nab getOrigItemCopy() {
        return origItemCopy;
    }

//    private void closeDialog() {
//        this.close();
//    }

//  --------------------------------------------

}