package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.WageService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ConfirmDialog;

import java.time.YearMonth;
import java.util.*;

import static eu.japtor.vizman.ui.components.OperationResult.NO_CHANGE;


//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PersonWageGridDialog extends AbstractGridDialog<PersonWage> implements HasLogger {

    public static final String DIALOG_WIDTH = "800px";
    public static final String DIALOG_HEIGHT = null;

    public static final String WAGE_EDIT_COL_KEY = "wage-edit-col-key";

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


    WageFormDialog wageFormDialog;

//    private Binder<Fakt> binder = new Binder<>();
//    private Fakt currentItem;
//    private Fakt origItem;
//    private Operation currentOperation;
//    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;

//    private Registration binderChangeListener = null;

    private List<PersonWage> currentItemList;
    private List<PersonWage> origItemList;

    private Person person;

    Grid<PersonWage> personWageGrid;
    private Button newItemButton;
    private FlexLayout titleComponent;
    private boolean personWagesChanged = false;

    private WageService wageService;
    private PersonService personService;
//    private CfgPropsCache cfgPropsCache;


    public PersonWageGridDialog(
            WageService wageService
            , PersonService personService
//            , CfgPropsCache cfgPropsCache
    ) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.WAGE);
        getMainTitle().setText("MZDOVÁ tabulka");

        this.personService = personService;
        this.wageService = wageService;
//        this.cfgPropsCache = cfgPropsCache;


//        wageEditDialog = new WageFormDialog(this.cfgPropsCache);
        wageFormDialog = new WageFormDialog(this.wageService);
        wageFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                finishWageEdit((WageFormDialog) event.getSource());
            }
        });
        getGridContainer().add(
                initPersonWageGridBar()
                , initPersonWageGrid()
        );

//        getFormLayout().add(
//                initZakEvidField()
//                , initCfaktField()
////                , initZakHonorarField()
//                , initTextField()
////                , initPlneniField()
//                , initCastkaField()
//                , initDateDuzpField()
////                , initDevider()
////                , initDevider()
////                , initZakladField()
//                , initFaktCisloField()
//                , initDateVystavField()
////                , initDateTimeExportField()
//        );
    }


    void finishWageEdit(WageFormDialog wageFormDialog) {
        PersonWage wageAfter = wageFormDialog.getCurrentItem(); // Modified, just added or just deleted
        Operation wageOper = wageFormDialog.getCurrentOperation();
        OperationResult wageOperRes = wageFormDialog.getLastOperationResult();

        if (OperationResult.NO_CHANGE != wageOperRes) {
            personWagesChanged = true;
        }
        PersonWage wageItemOrig = wageFormDialog.getOrigItem();

        syncFormGridAfterWageEdit(wageAfter, wageOper, wageOperRes, wageItemOrig);

        if (OperationResult.ITEM_SAVED == wageOperRes) {
            Notification.show(String.format("Mzda uložena")
                    , 2500, Notification.Position.TOP_CENTER);

        } else if (OperationResult.ITEM_DELETED == wageOperRes) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Editace mzdy")
                    .withMessage(String.format("Mzda zrušena."))
                    .open();
        }
    }

    private void syncFormGridAfterWageEdit(PersonWage personWageAfter, Operation wageOper
            , OperationResult wageOperRes, PersonWage wageItemOrig) {

        if (NO_CHANGE == wageOperRes) {
            return;
        }

        person = personService.fetchOne(person.getId());
        personWageGrid.getDataCommunicator().getKeyMapper().removeAll();
        personWageGrid.setItems(person.getWages());
        personWageGrid.getDataProvider().refreshAll();
        personWageGrid.select(personWageAfter);
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
    public void openDialog(Person person) {

//        this.origItemList = Collections.unmodifiableList(new LinkedList<>(currentItemList));

        this.personWagesChanged = false;
        this.person = person;
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
        this.currentItemList = person.getWages();
        personWageGrid.setItems(currentItemList);

//        setDefaultItemNames();  // Set general default names
//
////        evidZakOrig = new EvidZak(
////                currentItem.getKontId()
////                , currentItem.getCkz()
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


    private Component initPersonWageGridBar() {
        HorizontalLayout personWageGridBar = new HorizontalLayout();
//        FlexLayout zakGridBar = new FlexLayout();
        personWageGridBar.setSpacing(false);
        personWageGridBar.setPadding(false);
        personWageGridBar.getStyle().set("margin-left", "-3em");
//        zakGridBar.setWidth("100%");
        personWageGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        personWageGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        personWageGridBar.add(
                new Ribbon(),
                new FlexLayout(
                        initNewItemButton()
                )
        );
        return personWageGridBar;
    }

    private Component initNewItemButton() {
        newItemButton = new NewItemButton(ItemNames.getNomS(ItemType.WAGE), event -> {
            YearMonth ymToLast = getLastYmTo();
            YearMonth ymFromLast = getLastYmFrom();
            YearMonth ymNow = YearMonth.now();
            YearMonth ymFromNew;
            if (null == ymToLast) {
                if (ymNow.compareTo(ymFromLast) > 0) {
                    ymFromNew = ymNow;
                } else {
                    ymFromNew = ymFromLast.plusMonths(1);
                }
            } else {
                if (ymNow.compareTo(ymToLast) > 0) {
                    ymFromNew = ymNow;
                } else {
                    ymFromNew = ymToLast.plusMonths(1);
                }
            }

            PersonWage newPersonWage = new PersonWage();
            newPersonWage.setYmFrom(ymFromNew);

            wageFormDialog.openDialog(newPersonWage
                        , person
                        , Operation.ADD
//                        , "ZADÁNÍ SAZBY"
                        , "xxxx"
                        , ""
                );
            initDataAndControls();
        });
        return newItemButton;
    }


    private Component initPersonWageGrid() {
        personWageGrid = new Grid<>();
        personWageGrid.setColumnReorderingAllowed(true);
        personWageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
//        personWageGrid.setWidth( "100%" );
//        personWageGrid.setHeightFull();
//        personWageGrid.setHeight("0");

//        zakGrid.getElement().setProperty("flexGrow", (double)0);
//        alignSelf auto
//        align items stretch
//        zakGrid.setHeight(null);
//        faktGrid.setWidth( "100%" );
        personWageGrid.setColumnReorderingAllowed(true);
        personWageGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        personWageGrid.setId("person-wage-grid");
        personWageGrid.setClassName("vizman-simple-grid");
        personWageGrid.getStyle().set("marginTop", "0.5em");

//        personWageGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getItemTypeColoredTextComponent))
//                .setHeader("Typ")
//                .setWidth("5em")
//                .setFlexGrow(0)
//                .setResizable(true)
//        ;
        personWageGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenBtn))
                .setFlexGrow(0)
                .setKey(WAGE_EDIT_COL_KEY)
        ;
        personWageGrid.addColumn(PersonWage::getTariff)
                .setHeader("Sazba")
                .setResizable(true)
                .setWidth("10em")
                .setFlexGrow(0)
        ;
        personWageGrid.addColumn(PersonWage::getYmFrom)
                .setHeader("Platnost od")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
        personWageGrid.addColumn(PersonWage::getYmTo)
                .setHeader("Platnost do")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;
//        personWageGrid.addColumn(new ComponentRenderer<>(this::buildZakOpenBtn))
//                .setFlexGrow(0)
//                .setKey(PERSON_WAGE_EDIT_COL_KEY)
//        ;
//        personWageGrid.addColumn(new ComponentRenderer<>(VzmFormatUtils::getColoredTextComponent))
//                .setHeader("Text")
//                .setFlexGrow(1)
//                .setResizable(true)
//        ;

        return personWageGrid;
    }


    private Component buildZakOpenBtn(PersonWage wage) {
        return new GridItemEditBtn(event -> {
                wageFormDialog.openDialog(wage, wage.getPerson(), Operation.EDIT, null, null);
            }, VzmFormatUtils.getItemTypeColorName(wage.getTyp())
        );
    }



//    private boolean isDirty() {
//        return binder.hasChanges();
//    }

//    private boolean isFakturovano(final Fakt fakt) {
//        return null != fakt.getCastka() && fakt.getCastka().compareTo(BigDecimal.ZERO) > 0;
//    }

//    private boolean canFakturovat(final Fakt fakt) {
//        return (null != dateDuzpField.getStringValue()
//                && StringUtils.isNotBlank(fakt.getCkont())
//                && StringUtils.isNotBlank(textField.getStringValue()))
//                && StringUtils.isNotBlank(fakt.getZakText())
//                && StringUtils.isNotBlank(fakt.getKontText())
//                && StringUtils.isNotBlank(castkaField.getStringValue())
//                && null != dateDuzpField.getStringValue()
//                && null != fakt.getMena()
//        ;
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

    public List<PersonWage> getOrigItemList()  {
        return origItemList;
    }

    @Override
    public List<PersonWage> getCurrentItemList() {
        return currentItemList;
    }

//    @Override
//    public Operation getCurrentOperation() {
//        return currentOperation;
//    }


    private YearMonth getLastYmTo() {
        return currentItemList.get(0).getYmTo();
    }

    private YearMonth getLastYmFrom() {
        return currentItemList.get(0).getYmFrom();
    }

}
