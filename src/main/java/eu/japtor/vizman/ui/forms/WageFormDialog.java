package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.service.WageService;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WageFormDialog extends AbstractSimpleFormDialog<PersonWage> {

    private static final String DIALOG_WIDTH = "700px";
    private static final String DIALOG_HEIGHT = "350x";

    private final static String DELETE_STR = "Zrušit";
    private final static String REVERT_STR = "Vrátit změny";
    private final static String REVERT_AND_CLOSE_STR = "Zpět";
    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";

    private Button revertButton;
    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
    private Button deleteAndCloseButton;

    private HorizontalLayout leftBarPart;

    private TextField tariffField;
//    private DatePicker ymFromField;
    private TextField ymFromField;
    private TextField ymToField;
//    private ComboBox<YearMonth> ymFromField;
//    List<YearMonth> ymFromList = new ArrayList<>(
//            Arrays.asList(
//                YearMonth.of(2019, 7)
//                , YearMonth.of(2019, 8)
//                , YearMonth.of(2019, 9)
//            )
//    );

    private Binder<PersonWage> binder = new Binder<>();
    private Registration binderChangeListener = null;

//    private PersonWage currentItem;
//    private PersonWage origItem;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;


    private WageService wageService;
//    private Person person;
    private PersonWage wageBefore;
    private PersonWage wageAfter;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

//    private KlientService klientService;


//    public WageFormDialog(BiConsumer<Klient, Operation> itemSaver
//            , Consumer<Klient> itemDeleter
//            , KlientService klientService
//    ){

    public WageFormDialog(final WageService wageService) {
//        super("800px", "600px", false, false, itemSaver, itemDeleter, false);
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        setItemNames(ItemType.WAGE);

        this.wageService = wageService;

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        getFormLayout().add(
            initTariffField()
            , new Span()
            , initYmFromField()
            , initYmToField()
        );
    }

    public void openDialog(
            PersonWage wage, PersonWage wageBefore, PersonWage wageAfter, Person person, Operation operation
            , String titleItemNameText, String titleEndText
    ){

        this.currentOperation = operation;
//        this.person = person;
        setCurrentItem(wage);
        setOrigItem(wage);
        this.wageBefore = wageBefore;
        this.wageAfter = wageAfter;

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        vystupField.setLocale(new Locale("cs", "CZ"));


//        YearMonth pruhYmByToday = getYmFromCalymListByYm(YearMonth.now())
//                .orElse(null);
//        pruhYmSelector.setValue(pruhYmByToday);
//        pruhYmSelector.setValue(null);
//        pruhYmSelector.setItems(calymYmList);

        initDataAndControls(getCurrentItem(), currentOperation);
        this.open();
    }

    private void closeDialog() {
        this.close();
    }


    private void refreshHeaderMiddleBox(PersonWage item) {
        // Do nothing

//        FlexLayout headerMiddleComponent = new FlexLayout();
//        headerMiddleComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
//        headerMiddleComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        getHeaderMiddleBox().removeAll();
//        if (null != headerMiddleComponent) {
//            getHeaderMiddleBox().add(headerMiddleComponent);
//        }
    }

    private void deactivateListeners() {
        if (null != binderChangeListener) {
            try {
                binderChangeListener.remove();
            } catch (Exception e)  {
                // do nothing
            }
        }
    }

    private void activateListeners() {
        // zakFolderField, ckontField, czakField and textField must be initialized prior calling this method

        binderChangeListener = binder.addValueChangeListener(e ->
//                adjustControlsOperability(true, binder.isValid())
                        adjustControlsOperability(true)
        );
    }

    private void adjustControlsOperability(final boolean hasChanges) {
        saveAndCloseButton.setEnabled(hasChanges);
        revertButton.setEnabled(hasChanges);
    }

    private void initDataAndControls(final PersonWage item, final Operation operation) {

        deactivateListeners();

        binder.removeBean();
        binder.readBean(item);

        refreshHeaderMiddleBox(item);
        getHeaderEndBox().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(item, operation);
        initControlsOperability();

        activateListeners();
    }


    private void initControlsForItemAndOperation(final PersonWage item, final Operation operation) {
//        setItemNames(item.getTyp());
        getMainTitle().setText(getDialogTitle(operation, itemGender));

//        if (getCurrentItem() instanceof HasItemType) {
            getHeaderDevider().getStyle().set(
                    "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
//        }
        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
    }

    private void initControlsOperability() {
        saveAndCloseButton.setEnabled(false);
//        saveButton.setEnabled(false);
        revertButton.setEnabled(false);
        deleteAndCloseButton.setEnabled(currentOperation.isDeleteEnabled() && canDeleteItem(getCurrentItem()));
    }

//    private Component initYmFromSelector() {
//        ymFromField = new ComboBox<>("Platnost od");
//        ymFromField.setItems(this.ymFromList);
//
//        ymFromField.getStyle().set("margin-right", "1em");
//        ymFromField.setWidth("10em");
//        ymFromField.setPlaceholder("Rok-měsíc");
//        ymFromField.setItems(new ArrayList<>());
//
//        binder.forField(ymFromField)
////            .withValidator(ym -> (null != ym) && Arrays.asList(Mena.values()).contains(mena)
//            .withValidator(ym -> (null != ym)
//                ,"Platnost od musí být zadána")
//            .bind(PersonWage::getYmFrom, PersonWage::setYmFrom);
////        ymFromField.setPreventInvalidInput(true);
//
//        // The empty selection item is the first item that maps to an null item.
//        // As the item is not selectable, using it also as placeholder
//
////        pruhYmSelector.setEmptySelectionCaption("Rok-měsíc proužku...");
////        pruhYmSelector.setEmptySelectionAllowed(true);
////        pruhYmSelector.setItemEnabledProvider(Objects::nonNull);
////        // add a divider after the empty selection item
////        pruhYmSelector.addComponents(null, new Hr());
//
//        ymFromField.setItemLabelGenerator(this::getYmLabel);
//        ymFromField.addValueChangeListener(event -> {
////            pruhYm = event.getStringValue();
////            updatePruhGrids(pruhPerson, pruhYm);
//        });
//
//        ymFromField.setValue(null);
//        ymFromField.setItems(ymFromList);
//        return ymFromField;
//    }

    private Component initYmFromField() {
        ymFromField = new TextField("Platnost od");
        ymFromField.setValueChangeMode(ValueChangeMode.EAGER);
//        ymFromField.getStyle().set("margin-right", "1em");
//        ymFromField.setWidth("10em");
        ymFromField.setPlaceholder("RRRR-MM");
        ymFromField.setPattern("^\\d{4}-\\d{2}$");

        binder.forField(ymFromField)
                .withNullRepresentation("")
                .withValidator(ymFrom -> (null != ymFrom)
                        ,"Platnost od musí být zadána")
                .withValidator(ymFrom -> (null == wageBefore)
                                || (null == wageBefore.getYmTo())
                                || (null != ymFrom && ymFrom.compareTo(wageBefore.getYmTo().toString()) > 0)
                        ,"Platnost OD nesmí být dřívější než platnost DO předchozího záznamu")

                .withConverter(
                        new VzmFormatUtils.ValidatedIntegerYearMonthConverter())
                .bind(PersonWage::getYmFrom, PersonWage::setYmFrom)
        ;
        ymFromField.addValueChangeListener(event -> {
//            pruhYm = event.getStringValue();
//            updatePruhGrids(pruhPerson, pruhYm);
        });
        return ymFromField;
    }

    private Component initYmToField() {
        ymToField = new TextField("Platnost do");
        ymToField.setValueChangeMode(ValueChangeMode.EAGER);
        ymToField.getStyle().set("margin-right", "1em");
        ymToField.setWidth("10em");
        ymToField.setPlaceholder("RRRR-MM");
        ymToField.setPattern("^\\d{4}-\\d{2}$");

        binder.forField(ymToField)
                .withNullRepresentation("")
//                .withValidator(ym -> (null != ym)
//                        ,"Platnost do musí být zadána")
                .withValidator(ymTo -> (null != wageAfter) || (null == ymTo)
                                || (null != ymTo && ymTo.compareTo(ymFromField.getValue()) > 0)
//                                ((null == wageAfter) && (null == ym) || (null != ym && ym.compareTo(ymFromField.getValue())) > 0)
                        ,"Platnost DO musí být buď prázdná nebo pozdější než platnost OD")
                .withValidator(ymTo -> (null == wageAfter)
                                || (null != ymTo && ymTo.compareTo(ymFromField.getValue()) > 0)
                        ,"Platnost DO musí být pozdější než platnost OD")
                .withValidator(ymTo -> (null == wageAfter)
                                || (null != ymTo && ymTo.compareTo(wageAfter.getYmFrom().toString()) < 0)
                        ,"Platnost DO nesmí být pozdější než platnost OD následujícího záznamu")
                .withConverter(new VzmFormatUtils.ValidatedIntegerYearMonthConverter())
                .bind(PersonWage::getYmTo, PersonWage::setYmTo);

        ymToField.addValueChangeListener(event -> {
//            pruhYm = event.getStringValue();
//            updatePruhGrids(pruhPerson, pruhYm);
        });
        return ymToField;
    }


    private Component initTariffField() {
        tariffField = new TextField("Sazba [CZK/hod]");
        tariffField.setValueChangeMode(ValueChangeMode.EAGER);
        binder.forField(tariffField)
                .withNullRepresentation("")
                .withValidator(tar -> (null != tar)
                        ,"Sazba musí být zadána")
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(PersonWage::getTariff, PersonWage::setTariff);

        return tariffField;
    }


//    private void initYmFromField() {
//        getFormLayout().add(ymFromField);
//
//        getBinder().forField(ymFromField)
//                .bind(PersonWage::getYmFrom, Klient::setNote);
//    }


////    @Override
//    protected void confirmDelete() {
////        long zakKlientsCount = klientService.countZakKlients(Long klientId);
////        if (personCount > 0) {
//            openConfirmDeleteDialog("Zrušení sazby",
//                    "Opravdu zrušit sazbu ?", ""
//            );
////                    "Pokud bude uživatel zrušen, budou zrušena i další s ním související data.");
////        } else {
////            deleteKont(getCurrentItem());
////        }
//    }

    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
            initControlsOperability();
        }
    }

    private void deleteClicked() {
        if (!canDeleteItem(getCurrentItem())) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení sazby")
                    .withMessage(String.format("Sazbu nelze zrušit."))
                    .open()
            ;
            return;
        }
        try {
//            revertFormChanges();
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení sazby")
                    .withMessage(String.format("Zrušit sazbu ?"))
                    .withOkButton(() -> {
                                if (deleteItem(getCurrentItem())) {
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

    private boolean deleteItem(PersonWage itemToDelete) {
        OperationResult lastOperResOrig = lastOperationResult;
        try {
            wageService.deleteWage(itemToDelete);
            lastOperationResult = OperationResult.ITEM_DELETED;
            return true;
        } catch (VzmServiceException e) {
            this.lastOperationResult = lastOperResOrig;
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení mzdy.")
                    .withMessage(String.format("Mzdu se nepodařilo zrušit."))
                    .open()
            ;
            return false;
        }
    }


    private boolean canDeleteItem(final PersonWage itemToDelete) {
        return true;
    }

    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace sazby")
                .withMessage("Sazbu se nepodařilo uložit.")
                .open();
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace sazby")
                .withMessage("Sazbu se nepodařilo zrušit")
                .open()
        ;
    }


    private void revertFormChanges() {
        binder.removeBean();
        binder.readBean(getCurrentItem());
        lastOperationResult = OperationResult.NO_CHANGE;
    }


//  --------------------------------------------

    @Override
    public Component initDialogButtonBar() {
        HorizontalLayout bar = new HorizontalLayout();

//        saveButton = new Button("Uložit");
//        saveButton.setAutofocus(true);
//        saveButton.getElement().setAttribute("theme", "primary");
//        saveButton.addClickListener(e -> saveClicked(false));

        saveAndCloseButton = new Button(SAVE_AND_CLOSE_STR);
        saveAndCloseButton.setAutofocus(true);
        saveAndCloseButton.getElement().setAttribute("theme", "primary");
        saveAndCloseButton.addClickListener(e -> saveClicked(true));

        deleteAndCloseButton = new Button(DELETE_STR);
        deleteAndCloseButton.getElement().setAttribute("theme", "error");
        deleteAndCloseButton.addClickListener(e -> deleteClicked());

        revertButton = new Button(REVERT_STR);
        revertButton.addClickListener(e -> revertClicked(false));

        revertAndCloseButton = new Button(REVERT_AND_CLOSE_STR);
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

    private void saveClicked(boolean closeAfterSave) {
        if (!isItemValid()) {
            return;
        }
        try {
            getCurrentItem().setPerson(getCurrentItem().getPerson());
            binder.writeBeanIfValid(getCurrentItem());
            saveWage(getCurrentItem());
            binder.readBean(getCurrentItem());
            if (closeAfterSave) {
                closeDialog();
//            } else {
//                initFaktDataAndControls(currentItem, currentOperation);
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private boolean isItemValid() {
        boolean areFieldValuesValid = binder.writeBeanIfValid(getCurrentItem());
        if (!areFieldValuesValid) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace mzdy")
                    .withMessage("Mzdu nelze uložit, některá pole nejsou správně vyplněna.")
                    .open();
            return false;
        }
        if (!wageService.hasValidDates(getCurrentItem(), getCurrentItem().getId())) {
//            setCurrentItem(getOrigItem());
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace mzdy")
                    .withMessage("Mzdu nelze uložit, zadaná platnost je již obsazena jinou mzdou.")
                    .open();
            return false;
        }
        if (null != wageBefore && null == wageBefore.getYmTo()
                && wageBefore.getYmFrom().plusMonths(2).compareTo(getCurrentItem().getYmFrom()) > 0) {
//            setCurrentItem(getOrigItem());
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace mzdy")
                    .withMessage("Mzdu nelze uložit, platnost OD musí být nejméně dva měsíce po předchozí platnosti OD.")
                    .open();
            return false;
        }
        if (null != wageAfter && null == getCurrentItem().getYmFrom()) {
//            setCurrentItem(getOrigItem());
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace mzdy")
                    .withMessage("Mzdu nelze uložit, platnost DO může být prázdná jen u posledního záznamu.")
                    .open();
            return false;
        }
        return true;
    }

    private PersonWage saveWage(PersonWage wageToSave) {
        try {
            setCurrentItem(wageService.saveWage(wageToSave, currentOperation));
            lastOperationResult = OperationResult.ITEM_SAVED;
            return getCurrentItem();
        } catch(VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            throw(e);
        }
    }


    protected final Binder<PersonWage> getBinder() {
        return binder;
    }

    public OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    @Override
    public Operation getCurrentOperation() {
        return currentOperation;
    }


}