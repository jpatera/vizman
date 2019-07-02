package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.*;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.math.BigDecimal;
import java.util.Locale;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubFormDialog extends AbstractFormDialog<Fakt> implements HasLogger {

    public static final String DIALOG_WIDTH = "800px";
    public static final String DIALOG_HEIGHT = null;

    private final static String DELETE_STR = "Zrušit";
    private final static String REVERT_STR = "Vrátit změny";
    private final static String REVERT_AND_CLOSE_STR = "Zpět";
    private final static String SAVE_AND_CLOSE_STR = "Uložit a zavřít";

    private Button revertButton;
    private Button saveAndCloseButton;
    private Button revertAndCloseButton;
    private Button deleteAndCloseButton;

    private HorizontalLayout leftBarPart;

    private TextField zakEvidField;
    private TextField cfaktField;
    private TextField textField;
    private TextField castkaField;
    private DatePicker dateDuzpField;
    private DatePicker dateVystavField;
    private TextField dateTimeExportField;
    private TextField faktCisloField;

    private Binder<Fakt> binder = new Binder<>();
    private Fakt currentItem;
    private Fakt origItem;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;

    private Registration binderChangeListener = null;

    private FaktService faktService;

//    private Fakt faktOrig;
//    private CfgPropsCache cfgPropsCache;

    public SubFormDialog(FaktService faktService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        this.faktService = faktService;

        getFormLayout().add(
                initZakEvidField()
                , initCfaktField()
                , initTextField()
                , initCastkaField()
                , initDateDuzpField()
//                , initDevider()
//                , initZakladField()
                , initFaktCisloField()
                , initDateVystavField()
//                , initDateTimeExportField()
        );
    }


    public void openDialog(Fakt fakt, Operation operation) {
        setItemNames(fakt.getTyp());

        this.currentOperation = operation;
        this.currentItem = fakt;
        this.origItem = fakt;

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
        dateVystavField.setLocale(new Locale("cs", "CZ"));
        dateDuzpField.setLocale(new Locale("cs", "CZ"));

        castkaField.setSuffixComponent(new Span(fakt.getMena().name()));

        initFaktDataAndControls(currentItem, currentOperation);
        this.open();
    }

    private void closeDialog() {
        this.close();
    }


    private void initFaktDataAndControls(final Fakt faktItem, final Operation faktOperation) {

        deactivateListeners();

        setDefaultItemNames();
        binder.removeBean();
        binder.readBean(faktItem);

        refreshHeaderMiddleBox(faktItem);
        getHeaderEndBox().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(faktItem, faktOperation);
        initControlsOperability();

        activateListeners();
    }

    private void refreshHeaderMiddleBox(Fakt faktItem) {
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

    private void initControlsForItemAndOperation(final Fakt item, final Operation operation) {
        setItemNames(item.getTyp());
        getMainTitle().setText(operation.getDialogTitle(getItemName(operation), itemGender));

        if (getCurrentItem() instanceof HasItemType) {
            getHeaderDevider().getStyle().set(
                    "background-color", VzmFormatUtils.getItemTypeColorBrighter((item).getTyp()));
        }
        deleteAndCloseButton.setText(DELETE_STR + " " + getItemName(Operation.DELETE).toLowerCase());
    }

    private void initControlsOperability() {
        saveAndCloseButton.setEnabled(false);
//        saveButton.setEnabled(false);
        revertButton.setEnabled(false);
        deleteAndCloseButton.setEnabled(currentOperation.isDeleteEnabled() && canDeleteFakt(currentItem));
    }

//    private void adjustControlsOperability(final boolean hasChanges, final boolean isValid) {
    private void adjustControlsOperability(final boolean hasChanges) {
        saveAndCloseButton.setEnabled(hasChanges);
        revertButton.setEnabled(hasChanges);
    }

    private Component initZakEvidField() {
        zakEvidField = new TextField("Ze zakázky");
        zakEvidField.getStyle()
                .set("padding-top", "0em");
        zakEvidField.setReadOnly(true);
        zakEvidField.getElement().setAttribute("colspan", "2");
        getBinder().forField(zakEvidField)
                .bind(Fakt::getZakEvid, null);
        return zakEvidField;
    }

    private Component initCfaktField() {
        cfaktField = new TextField("Číslo subdodávky");
        cfaktField.setReadOnly(true);
        cfaktField.setWidth("8em");
        getBinder().forField(cfaktField)
//                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .withConverter(Integer::valueOf, String::valueOf,"Neplatný formát čísla")
                .bind(Fakt::getCfakt, null);
        return cfaktField;
    }

    private Component initDateDuzpField() {
        dateDuzpField = new DatePicker("DUZP");
        getBinder().forField(dateDuzpField)
//                .withConverter(String::trim, String::trim)
                .bind(Fakt::getDateDuzp, Fakt::setDateDuzp);
        return dateDuzpField;
    }

    private Component initTextField() {
        textField = new TextField("Text subdoddávky");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .withValidator(new StringLengthValidator(
                        "Text subdodávky může mít  max. 127 znaků",
                        0, 127)
                )
                .bind(Fakt::getText, Fakt::setText);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        return textField;
    }

    private Component initCastkaField() {
        castkaField = new TextField("Částka subdodávky");
//        castkaField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        castkaField.setReadOnly(false);
//        castkaField.setSuffixComponent(new Span(faktMena.name()));
        getBinder().forField(castkaField)
                .withNullRepresentation("")
                .withValidator(new StringLengthValidator(
                        "Částka subdodávky nesmí být prázdná",
                        1, null))
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .withValidator(castka -> (null != castka) && (castka.compareTo(BigDecimal.ZERO) <= 0)
                        , "Částka subdodávky musí být záporná")
                .bind(Fakt::getCastka, Fakt::setCastka);
        castkaField.setValueChangeMode(ValueChangeMode.EAGER);
        return castkaField;
    }

    private Component initDateVystavField() {
        dateVystavField = new DatePicker("Vystaveno, fakturovano...");
//        dateVystavField.setReadOnly(true);
        getBinder().forField(dateVystavField)
//                .withConverter(String::trim, String::trim)
                .bind(Fakt::getDateVystav, Fakt::setDateVystav);
        return dateVystavField;
    }

    private Component initFaktCisloField() {
        faktCisloField = new TextField("Číslo faktury");
        faktCisloField.setReadOnly(false);
        getBinder().forField(faktCisloField)
                .withValidator(new StringLengthValidator(
                        "Číslo faktury muže mít max. 40 znaků",
                        0, 40)
                )
                .bind(Fakt::getFaktCislo, Fakt::setFaktCislo)
        ;
        faktCisloField.setValueChangeMode(ValueChangeMode.EAGER);
        return faktCisloField;
    }


// ------------------------------------------------------------

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

    private void revertClicked(boolean closeAfterRevert) {
        revertFormChanges();
        if (closeAfterRevert) {
            closeDialog();
        } else {
            initControlsOperability();
        }
    }

    private void saveClicked(boolean closeAfterSave) {
        if (!isFaktValid()) {
            return;
        }
        try {
            currentItem = saveFakt(currentItem);
            if (closeAfterSave) {
                closeDialog();
            } else {
                initFaktDataAndControls(currentItem, currentOperation);
            }
        } catch (VzmServiceException e) {
            showSaveErrMessage();
        }
    }

    private void deleteClicked() {
        String ckzfDel = String.format("%s / %s / %s", currentItem.getCkont(), currentItem.getCzak(), currentItem.getCfakt());
        if (!canDeleteFakt(currentItem)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení subdodávky")
                    .withMessage(String.format("Subdodávku %s nelze zrušit.", ckzfDel))
                    .open()
            ;
            return;
        }
        try {
            revertFormChanges();
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení subdodávky")
                    .withMessage(String.format("Zrušit subdodávku %s ?", ckzfDel))
                    .withOkButton(() -> {
                                if (deleteFakt(currentItem)) {
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

    private void showSaveErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace subdodávky")
                .withMessage("Subdodávku se nepodařilo uložit.")
                .open();
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace subdodávky")
                .withMessage("Subdodávku se nepodařilo zrušit")
                .open()
        ;
    }

    private void revertFormChanges() {
        binder.removeBean();
        binder.readBean(currentItem);
        lastOperationResult = OperationResult.NO_CHANGE;
    }

    private boolean deleteFakt(Fakt itemToDelete) {
        String ckzfDel = String.format("%s / %s / %s", currentItem.getCkont(), currentItem.getCzak(), currentItem.getCfakt());
        OperationResult lastOperResOrig = lastOperationResult;
        try {
            faktService.deleteFakt(itemToDelete);
            lastOperationResult = OperationResult.ITEM_DELETED;
            return true;
        } catch (VzmServiceException e) {
            this.lastOperationResult = lastOperResOrig;
            ConfirmDialog
                    .createWarning()
                    .withCaption("Zrušení subdodávky.")
                    .withMessage(String.format("Subdodávku %s se nepodařilo zrušit.", ckzfDel))
                    .open()
            ;
            return false;
        }
    }

    public Fakt saveFakt(Fakt faktToSave) throws VzmServiceException {
        try {
            currentItem = faktService.saveFakt(faktToSave, currentOperation);
            lastOperationResult = OperationResult.ITEM_SAVED;
            return currentItem;
        } catch(VzmServiceException e) {
            lastOperationResult = OperationResult.NO_CHANGE;
            throw(e);
        }
    }

    private boolean isFaktValid() {
        boolean isValid = binder.writeBeanIfValid(currentItem);
        if (!isValid) {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Editace subdodávky")
                    .withMessage("Subdodávku nelze uložit, některá pole nejsou správně vyplněna.")
                    .open();
            return false;
        }
        return true;
    }

    private boolean canDeleteFakt(final Fakt itemToDelete) {
        return true;
    }

    private boolean isDirty() {
        return binder.hasChanges();
    }

//  --------------------------------------------

    protected final Binder<Fakt> getBinder() {
        return binder;
    }

    public OperationResult getLastOperationResult()  {
        return lastOperationResult;
    }

    public Fakt getOrigItem()  {
        return origItem;
    }

    @Override
    public Fakt getCurrentItem() {
        return currentItem;
    }

    @Override
    public Operation getCurrentOperation() {
        return currentOperation;
    }

}
