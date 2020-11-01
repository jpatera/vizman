package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.AbstractStreamResource;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.report.ZakRozpracXlsReportBuilder;
import eu.japtor.vizman.backend.service.ZakrService;
import eu.japtor.vizman.ui.components.*;
import net.sf.jasperreports.engine.JRException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.RFNDF;
import static eu.japtor.vizman.ui.components.OperationResult.NO_CHANGE;

public class ZakRozpracSingleDialog extends AbstractGridDialog<Zaqa> implements HasLogger {

    public static final String DIALOG_WIDTH = "900px";
    public static final String DIALOG_HEIGHT = null;
    private final static String CLOSE_STR = "Zavřít";

    public static final String RX_COL_KEY = "rx-col-key";

    private Button closeButton;
    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;
    private Label zakInfo;

    private List<Zaqa> currentItemList;
    private Zakr zakr;

    Grid<Zaqa> grid;
    private Button newItemButton;
    private FlexLayout titleComponent;
    private boolean zaqasChanged = false;

    private ZakrService zakrService;

    private final static String REPORT_FILE_NAME = "vzm-exp-zakn";
    private String repSubtitleText;
    private Anchor expXlsAnchor;
    private ReportXlsExporter<Zakr> xlsReportExporter;
    private SerializableSupplier<List<? extends Zakr>> singleBaseItemSupplier = () -> {
//        List<Zakr> singleBaseItemList = new ArrayList<>();
//        singleBaseItemList.add(getCurrentBaseItem());
//        return singleBaseItemList;
        return Collections.singletonList(zakr);
    };
    private ComponentEventListener anchorExportListener = event -> {
        try {
            updateRozpracXlsRepAnchorResource(singleBaseItemSupplier);
        } catch (JRException e) {
            e.printStackTrace();
        }
    };


    public ZakRozpracSingleDialog(
            ZakrService zakrService
    ) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.UNKNOWN);
        getMainTitle().setText("ROZPRACOVANOST ZAKÁZKY");

        this.zakrService = zakrService;

        getGridInfoBar().add(
                initZakInfo()
        );
        getGridToolBar().add(
                buildGridBar()
        );
        getGridContainer().add(
                initGrid()
        );
    }

//    void finishItemEdit(ZaqaFormDialog zaqaFormDialog) {
//        Zaqa zaqaAfter = zaqaFormDialog.getCurrentItem(); // Modified, just added or just deleted
//        Operation itemOper = zaqaFormDialog.getCurrentOperation();
//        OperationResult operRes = wageFormDialog.getLastOperationResult();
//
//        if (OperationResult.NO_CHANGE != operRes) {
//            itemChanged = true;
//        }
//        PersonWage itemOrig = zaqaFormDialog.getOrigItem();
//
//        syncFormGridAfterZaqaEdit(zaqaAfter, itemOper, itemOperRes, wageItemOrig);
//
//        if (OperationResult.ITEM_SAVED == operRes) {
//            Notification.show(String.format("Rozpracovanost uložena")
//                    , 2500, Notification.Position.TOP_CENTER);
//
//        } else if (OperationResult.ITEM_DELETED == operRes) {
//            ConfirmDialog
//                    .createInfo()
//                    .withCaption("Editace rozpracovanosti")
//                    .withMessage(String.format("Rozpracovanost zrušena."))
//                    .open();
//        }
//    }

    private void syncFormGridAfterItemEdit(Zaqa ìtemAfter, Operation oper
            , OperationResult operRes, Zaqa itemOrig) {

        if (NO_CHANGE == operRes) {
            return;
        }

        zakr = zakrService.fetchOne(zakr.getId());
        grid.getDataCommunicator().getKeyMapper().removeAll();
        grid.setItems(zakr.getZaqas());
        grid.getDataProvider().refreshAll();
        grid.select(ìtemAfter);
    }


    // Title for grid bar - not needed for person wages dialog
    // -------------------------------------------------------
    private Component initTitleComponent() {
        titleComponent = new FlexLayout(
//                initZakGridResizeBtn()
                initTitle()
        );
        titleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        return titleComponent;
    }

    private Component initTitle() {
        H4 zakTitle = new H4();
        zakTitle.setText(ItemNames.getNomP(ItemType.WAGE));
        zakTitle.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-right", "1em");
        return zakTitle;
    }
    // -------------------------------------------------------


    public void openDialog(Zakr zakr, String repSubtitleText) {
        this.zakr = zakr;
        this.repSubtitleText = repSubtitleText;
        initDataAndControls();
        this.open();
    }

    private void closeDialog() {
        this.close();
    }

    private void initDataAndControls() {
        deactivateListeners();

        zakInfo.setText(zakr.getRepKzCisloAndText());
        this.xlsReportExporter = new ReportXlsExporter();

        this.currentItemList = zakr.getZaqas();
        grid.setItems(currentItemList);

        initControlsOperability();
        activateListeners();
    }


    private void deactivateListeners() {
//        if (null != binderChangeListener) {
//            try {
//                binderChangeListener.remove();
//            } catch (Exception e)  {
//                // do nothing
//            }
//        }
    }

    private void activateListeners() {
//        // zakFolderField, ckontField, czakField and textField must be initialized prior calling this method
//
//        binderChangeListener = binder.addValueChangeListener(e ->
//                adjustControlsOperability(true)
//        );
    }

//    protected void initControlsForItemAndOperation(final PersonWage item, final Operation operation) {
//        setItemNames(item.getTyp());
//        getMainTitle().setText(operation.getDialogTitle(getItemName(operation), itemGender));
//
////        if (getCurrentItem() instanceof HasItemType) {
//            getHeaderDevider().getStyle().set(
//                    "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
////        }
////        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
//    }

    private void initControlsOperability() {
        closeButton.setEnabled(true);
    }

    private void adjustControlsOperability(final boolean hasChanges) {
        closeButton.setEnabled(true);
    }


    private Component buildGridBar() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setPadding(false);
        gridBar.setMargin(false);
        gridBar.setWidthFull();
        gridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        gridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        gridBar.add(
                new Ribbon()
                , buildToolBarBox()
        );
        return gridBar;
    }

    private Component initZakInfo() {
        zakInfo = new Label("Update during open...");
//        zakInfo.getElement().setAttribute("theme", "small");
        return zakInfo;
    }


    private Component buildToolBarBox() {
        HorizontalLayout toolBar = new HorizontalLayout();
        toolBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        toolBar.setSpacing(false);
        toolBar.add(
                initZakNaklXlsExpAnchor()
        );
        return toolBar;
    }

    private Component initZakNaklXlsExpAnchor() {
        expXlsAnchor = new ReportExpButtonAnchor(ReportExporter.Format.XLS, anchorExportListener);
        return expXlsAnchor;
    }

    private String getReportFileName(ReportXlsExporter.Format format) {
        return REPORT_FILE_NAME + RFNDF.format(LocalDateTime.now()) + "." + format.name().toLowerCase();
    }

    private void updateRozpracXlsRepAnchorResource(SerializableSupplier<List<? extends Zakr>> itemsSupplier) throws JRException {
        AbstractStreamResource xlsResource =
                xlsReportExporter.getXlsStreamResource(
                        new ZakRozpracXlsReportBuilder(repSubtitleText)
                        , getReportFileName(ReportXlsExporter.Format.XLS)
                        , itemsSupplier
                        , null
                );
        expXlsAnchor.setHref(xlsResource);

        // Varianta 1
        UI.getCurrent().getPage().executeJs("$0.click();", expXlsAnchor.getElement());

//        // Varianta 2 - Has an issue: after returning to the parent dialog [Close] button does nothing
//        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(xlsResource);
//        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }

    private Component initNewItemButton() {
        newItemButton = new NewItemButton(ItemNames.getNomS(ItemType.UNKNOWN), event -> {
//            YearMonth ymToLast = getLastYmTo();
//            YearMonth ymFromLast = getLastYmFrom();
//            YearMonth ymNow = YearMonth.now();
//            YearMonth ymFromNew;
//            if (null == ymToLast) {
//                if (ymNow.compareTo(ymFromLast) > 0) {
//                    ymFromNew = ymNow;
//                } else {
//                    ymFromNew = ymFromLast.plusMonths(1);
//                }
//            } else {
//                if (ymNow.compareTo(ymToLast) > 0) {
//                    ymFromNew = ymNow;
//                } else {
//                    ymFromNew = ymToLast.plusMonths(1);
//                }
//            }

            Zaqa newItem = new Zaqa();
//            newItem.setYmFrom(ymFromNew);

//            wageFormDialog.openDialog(newPersonWage
//                        , person
//                        , Operation.ADD
////                        , "ZADÁNÍ SAZBY"
//                        , "xxxx"
//                        , ""
//                );
            initDataAndControls();
        });
        return newItemButton;
    }


    private Component initGrid() {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setId("single-zak-rozprac-grid");
        grid.setClassName("vizman-simple-grid");
        grid.getStyle().set("marginTop", "0.5em");

        grid.addColumn(Zaqa::getRok)
                .setHeader("Rok")
                .setResizable(true)
                .setWidth("10em")
                .setFlexGrow(0)
        ;
        grid.addColumn(Zaqa::getQa)
                .setHeader("Kvartál")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        Grid.Column<Zaqa> colRx = grid.addColumn(Zaqa::getRx)
                .setHeader("RX")
                .setWidth("10em")
                .setFlexGrow(0)
                .setKey(RX_COL_KEY)
                .setResizable(true)
        ;
//        colRx.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zaqa::getRx, Zaqa::setRx));


//        // =============
//        // Grid editor:
//        // =============
//        Editor<Zaqa> zaqaEditor = this.grid.getEditor();
//        Binder<Zaqa> zaqaEditorBinder = new Binder<>(Zaqa.class);
//        zaqaEditor.setBinder(zaqaEditorBinder);
//        zaqaEditor.setBuffered(false);
//        zaqaEditor.addSaveListener(event -> {
//            System.out.println("=== editor SAVING...");
//            this.itemSaver.accept(event.getItem(), Operation.EDIT);
//        });
//
        return grid;
    }

//    // ===========================
//    //  Rx Field editor  component
//    // ===========================
//    private Component buildRxEditorComponent(
//            Binder<Zaqa> zaqaEditorBinder
//            , ValueProvider<Zakr, BigDecimal> rxEditorValueProvider
//            , Setter<Zaqa, BigDecimal> rxEditorSetter
//    ) {
//        TextField editComp = new TextField();
//        editComp.addValueChangeListener(event -> {
//            if (event.isFromClient() && !Objects.equals(event.getStringValue(), (event.getOldValue()))) {
//                editedItemChanged = true;
//                this.getEditor().getBinder().writeBeanIfValid(this.getEditor().getItem());
////                this.getDataProvider().refreshItem(this.getEditor().getItem());
//            }
//        });
//        // TODO: remove margins
//        editComp.getStyle()
//                .set("margin", "0")
//                .set("padding", "0")
////                .set("width", HOD_COL_WIDTH)
//                .set("width", "3.5em")
//                .set("font-size", "var(--lumo-font-size-s)")
//                .set("height", "1.8m")
//                .set("min-height", "1.8em")
//                .set("--lumo-text-field-size", "var(--lumo-size-s)")
//        ;
//        editComp.setPattern(RX_REGEX);
//        editComp.setPreventInvalidInput(true);
//        zakrEditorBinder.forField(editComp)
//                .withNullRepresentation("")
//                .withConverter(VzmFormatUtils.VALIDATED_PROC_INT_TO_STRING_CONVERTER)
//                .bind(rxEditorValueProvider, rxEditorSetter);
//
//        return editComp;
//    }

//    private Component buildZaqaOpenBtn(Zaqa item) {
//        return new GridItemEditBtn(event -> {
//                zaqaFormDialog.openDialog(item, item.getZakr(), Operation.EDIT, null, null);
//            }, VzmFormatUtils.getItemTypeColorName(ItemType.UNKNOWN)
//        );
//    }

// ------------------------------------------------------------

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

//        saveButton = new Button("Uložit");
//        saveButton.setAutofocus(true);
//        saveButton.getElement().setAttribute("theme", "primary");
//        saveButton.addClickListener(e -> saveClicked(false));

        closeButton = new Button(CLOSE_STR);
        closeButton.setAutofocus(true);
        closeButton.getElement().setAttribute("theme", "primary");
        closeButton.addClickListener(e -> saveClicked(true));

//        saveAndCloseButton = new Button(SAVE_AND_CLOSE_STR);
//        saveAndCloseButton.setAutofocus(true);
//        saveAndCloseButton.getElement().setAttribute("theme", "primary");
//        saveAndCloseButton.addClickListener(e -> saveClicked(true));
//
//        deleteAndCloseButton = new Button(DELETE_STR);
//        deleteAndCloseButton.getElement().setAttribute("theme", "error");
//        deleteAndCloseButton.addClickListener(e -> deleteClicked());

//        revertButton = new Button(REVERT_STR);
//        revertButton.addClickListener(e -> revertClicked(false));

//        revertAndCloseButton = new Button(REVERT_AND_CLOSE_STR);
//        revertAndCloseButton.addClickListener(e -> revertClicked(true));

//        faktExpButton = initFaktExpButton();
//        faktExpButton.addClickListener(event -> faktExpClicked());

        leftBarPart = new HorizontalLayout();
        leftBarPart.setSpacing(true);
//        leftBarPart.add(
////                saveButton
//                revertButton
//                , revertAndCloseButton
////                , deleteAndCloseButton
////                , faktExpButton
//        );

        rightBarPart = new HorizontalLayout();
        rightBarPart.setSpacing(true);
        rightBarPart.add(
                closeButton
//                , revertAndCloseButton
        );

//        buttonBar.getStyle().set("margin-top", "0.2em");
        bar.setClassName("buttons");
        bar.setSpacing(false);
        bar.setPadding(false);
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        bar.add(
                leftBarPart
                , rightBarPart
        );
        return bar;
    }


//    private void revertClicked(boolean closeAfterRevert) {
//        revertFormChanges();
//        if (closeAfterRevert) {
//            closeDialog();
//        } else {
//            initControlsOperability();
//        }
//    }

    private void saveClicked(boolean closeAfterSave) {
//        if (!isWageListValid()) {
//            return;
//        }
//        try {
////            currentItem = saveFakt(currentItem);
            if (closeAfterSave) {
                closeDialog();
////            } else {
////                initFaktDataAndControls(currentItem, currentOperation);
            }
//        } catch (VzmServiceException e) {
//            showSaveErrMessage();
//        }
    }

//    private void deleteClicked() {
//        String ckzfDel = String.format("%s / %s / %s", currentItem.getCkont(), currentItem.getCkz(), currentItem.getCfakt());
//        if (!canDeleteFakt(currentItem)) {
//            ConfirmDialog
//                    .createInfo()
//                    .withCaption("Zrušení plnění")
//                    .withMessage(String.format("Plnění %s nelze zrušit.", ckzfDel))
//                    .open()
//            ;
//            return;
//        }
//        try {
//            revertFormChanges();
//            ConfirmDialog.createQuestion()
//                    .withCaption("Zrušení plnění")
//                    .withMessage(String.format("Zrušit plnění %s ?", ckzfDel))
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
//                .withCaption("Editace tabulky sazeb")
//                .withMessage("Změny v tabulce se nepodařilo uložit.")
//                .open();
//    }
//
//    private void showDeleteErrMessage() {
//        ConfirmDialog.createError()
//                .withCaption("Editace tabulky sazeb")
//                .withMessage("Sazbu se nepodařilo zrušit")
//                .open()
//        ;
//    }

//    private void revertFormChanges() {
//        currentItemList = new LinkedList(origItemList);
////        personWageGrid.setData
//    }

//    public List<PersonWage> savePersonWageList(LinkedList<PersonWage> personWageListToSave) throws VzmServiceException {
//        try {
//            currentItemList = (LinkedList)wageService.savePersonWageList(personWageListToSave);
//            return currentItemList;
//        } catch(VzmServiceException e) {
////            lastOperationResult = OperationResult.NO_CHANGE;
//            throw(e);
//        }
//    }

////    private boolean isWageListValid(PersonWage personWage) {
//    private boolean isWageListValid() {
//        return true;
//    }

//    private boolean isWageValid(PersonWage personWage) {
////        boolean isValid = personWage.hasDateAfterPrevious() && personWage.hasDateBeforeNext();
//        boolean isValid = false;
//        if (!isValid) {
//            ConfirmDialog
//                    .createWarning()
//                    .withCaption("Editace Sazbu")
//                    .withMessage("Sazbu nelze uložit, některá pole nejsou správně vyplněna.")
//                    .open();
//            return false;
//        }
//        return true;
//    }

//  --------------------------------------------

//    public OperationResult getLastOperationResult()  {
//        return lastOperationResult;
//    }

//    public List<Zaqa> getOrigItemList()  {
//        return origItemList;
//    }

    @Override
    public List<Zaqa> getCurrentItemList() {
        return currentItemList;
    }

//    @Override
//    public Operation getCurrentOperation() {
//        return currentOperation;
//    }

}
