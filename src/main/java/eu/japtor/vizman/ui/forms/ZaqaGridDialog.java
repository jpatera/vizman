package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.ZakrService;
import eu.japtor.vizman.backend.service.ZaqaService;
import eu.japtor.vizman.ui.components.*;

import java.util.Collections;
import java.util.List;

import static eu.japtor.vizman.ui.components.OperationResult.NO_CHANGE;

public class ZaqaGridDialog extends AbstractGridDialog<Zaqa> implements HasLogger {

    public static final String DIALOG_WIDTH = "800px";
    public static final String DIALOG_HEIGHT = null;

    public static final String RX_EDIT_COL_KEY = "rx-edit-col-key";

//    private final static String DELETE_STR = "Zrušit";
//    private final static String REVERT_STR = "Vrátit změny";
//    private final static String REVERT_AND_CLOSE_STR = "Zpět";
//    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";
    private final static String CLOSE_STR = "Zavřít";

//    private Button revertButton;
//    private Button saveAndCloseButton;
//    private Button revertAndCloseButton;
//    private Button deleteAndCloseButton;
    private Button closeButton;
    private HorizontalLayout leftBarPart;
    private HorizontalLayout rightBarPart;


//    WageFormDialog wageFormDialog;

    private List<Zaqa> currentItemList;
    private List<Zaqa> origItemList;

    private Zakr zakr;

    Grid<Zaqa> grid;
    private Button newItemButton;
    private FlexLayout titleComponent;
    private boolean zaqasChanged = false;

    private ZaqaService zaqaService;
    private ZakrService zakrService;


    public ZaqaGridDialog(
            ZaqaService zaqaService
            , ZakrService zakrService
    ) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.UNKNOWN);
        getMainTitle().setText("Tabulka ROZPRACOVANOSTI");

        this.zakrService = zakrService;
        this.zaqaService = zaqaService;

//        zaqaFormDialog = new ZaqaFormDialog(this.zaqaService);
//        zaqaFormDialog.addOpenedChangeListener(event -> {
//            if (!event.isOpened()) {
//                finishWageEdit((ZaqaFormDialog) event.getSource());
//            }
//        });
        getGridContainer().add(
                initGridBar()
                , initGrid()
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


//    public void openDialog(LinkedList<PersonWage> personWages) {
    public void openDialog(Zakr zakr) {

//        this.origItemList = Collections.unmodifiableList(new LinkedList<>(currentItemList));

//        this.itemsChanged = false;
        this.zakr = zakr;
        initDataAndControls();
        this.origItemList = Collections.unmodifiableList(this.currentItemList);

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        dateVystavField.setLocale(new Locale("cs", "CZ"));
//        dateDuzpField.setLocale(new Locale("cs", "CZ"));
//
//        castkaField.setSuffixComponent(new Span(fakt.getMena().name()));

//        initFaktDataAndControls(currentItem, currentOperation);
        this.open();
    }

    private void closeDialog() {
        this.close();
    }

    private void initDataAndControls() {
        deactivateListeners();
//        this.currentItemList = wageService.fetchByPersonId(person.getId());
        this.currentItemList = zakr.getZaqas();
        grid.setItems(currentItemList);

//        setDefaultItemNames();  // Set general default names
//
////        evidZakOrig = new EvidZak(
////                currentItem.getKontId()
////                , currentItem.getCzak()
////                , currentItem.getText()
////                , currentItem.getFolder()
////                , currentItem.getKontFolder()
////        );
//
//        binder.removeBean();
//        binder.readBean(faktItem);
//
////        refreshHeaderMiddleBox(faktItem);
//        getHeaderEndBox().setText(getHeaderEndComponentValue(null));
//
//        initControlsForItemAndOperation(faktItem, faktOperation);

        initControlsOperability();
        activateListeners();
    }


    private void refreshHeaderMiddleBox(Zak zakItem) {
    // Do nothing
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
//        saveButton.setEnabled(false);
//        revertButton.setEnabled(false);
//        deleteAndCloseButton.setEnabled(currentOperation.isDeleteEnabled() && canDeleteFakt(currentItem));
    }

//    private void adjustControlsOperability(final boolean hasChanges, final boolean isValid) {
    private void adjustControlsOperability(final boolean hasChanges) {
//        saveAndCloseButton.setEnabled(hasChanges && isValid);
        closeButton.setEnabled(true);
//        revertButton.setEnabled(hasChanges);
    }


    private Component initGridBar() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSpacing(false);
        gridBar.setPadding(false);
        gridBar.getStyle().set("margin-left", "-3em");
//        zakGridBar.setWidth("100%");
        gridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        gridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        gridBar.add(
                new Ribbon(),
                new FlexLayout(
                        initNewItemButton()
                )
        );
        return gridBar;
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
        grid.setId("person-wage-grid");
        grid.setClassName("vizman-simple-grid");
        grid.getStyle().set("marginTop", "0.5em");

//        personWageGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getItemTypeColoredTextComponent))
//                .setHeader("Typ")
//                .setWidth("5em")
//                .setFlexGrow(0)
//                .setResizable(true)
//        ;
//        grid.addColumn(new ComponentRenderer<>(this::buildZaqaOpenBtn))
//                .setFlexGrow(0)
//                .setKey(RX_EDIT_COL_KEY)
//        ;
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
        grid.addColumn(Zaqa::getRx)
                .setHeader("RX")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
//        personWageGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenBtn))
//                .setFlexGrow(0)
//                .setKey(PERSON_WAGE_EDIT_COL_KEY)
//        ;

        return grid;
    }


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
//        String ckzfDel = String.format("%s / %s / %s", currentItem.getCkont(), currentItem.getCzak(), currentItem.getCfakt());
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

    public List<Zaqa> getOrigItemList()  {
        return origItemList;
    }

    @Override
    public List<Zaqa> getCurrentItemList() {
        return currentItemList;
    }

//    @Override
//    public Operation getCurrentOperation() {
//        return currentOperation;
//    }

}
