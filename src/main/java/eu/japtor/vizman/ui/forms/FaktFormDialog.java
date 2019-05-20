package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
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
public class FaktFormDialog extends AbstractFormDialog<Fakt> implements HasLogger {

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
//    private Button fakturovatButton;
//    private Button stornoButton;

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

//    @Autowired
    private FaktService faktService;


    public FaktFormDialog(FaktService faktService) {
        super(DIALOG_WIDTH, DIALOG_HEIGHT);
        this.faktService = faktService;

        getFormLayout().add(
                initZakEvidField()
                , initCfaktField()
//                , initZakHonorarField()
                , initTextField()
//                , initPlneniField()
                , initCastkaField()
                , initDateDuzpField()
//                , initDevider()
//                , initDevider()
//                , initZakladField()
                , initFaktCisloField()
                , initDateVystavField()
//                , initDateTimeExportField()
        );
    }


    public void openDialog(Fakt fakt, Operation operation) {

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

        setDefaultItemNames();  // Set general default names

//        evidZakOrig = new EvidZak(
//                currentItem.getKontId()
//                , currentItem.getCzak()
//                , currentItem.getText()
//                , currentItem.getFolder()
//                , currentItem.getKontFolder()
//        );

        binder.removeBean();
        binder.readBean(faktItem);

//        refreshHeaderMiddleBox(faktItem);
        getHeaderEndBox().setText(getHeaderEndComponentValue(null));

        initControlsForItemAndOperation(faktItem, faktOperation);
        initControlsOperability();

        activateListeners();
    }


    private void refreshHeaderMiddleBox(Zak zakItem) {
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
                adjustControlsOperability(true)
        );
    }

    protected void initControlsForItemAndOperation(final Fakt item, final Operation operation) {
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
//        saveAndCloseButton.setEnabled(hasChanges && isValid);
        saveAndCloseButton.setEnabled(hasChanges);
        revertButton.setEnabled(hasChanges);
    }


    private boolean canDeleteFakt(final Fakt itemToDelete) {
        return true;
    }

    private boolean isDirty() {
        return binder.hasChanges();
    }





    private boolean isFakturovano(final Fakt fakt) {
        return null != fakt.getCastka() && fakt.getCastka().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean canFakturovat(final Fakt fakt) {
//        return (null != dateDuzpField.getValue()
//                && StringUtils.isNotBlank(plneniField.getValue())
//                && StringUtils.isNotBlank(textField.getValue()));

        return true;
    }


//    private Component initFakturovatButton() {
//        fakturovatButton = new Button("Fakturovat");
//        fakturovatButton.addClickListener(event -> {
//
//            if (!canFakturovat(getCurrentItem())) {
//                new OkDialog().open("Nelze fakturovat", "Některé položky předpisu fakturace nejsou zadány.", "");
//                return;
//            }
//
//            boolean isValid = binder.writeBeanIfValid(getCurrentItem());
//            if (isValid) {
//                getCurrentItem().setZaklad(getCurrentItem().getZakHonorar());
//                getCurrentItem().setCastka(getCurrentItem().getZakHonorar()
//                        .multiply(getCurrentItem().getPlneni().divide(BigDecimal.valueOf(100))));
//                getCurrentItem().setDateVystav(LocalDate.now());
//                getBinder().readBean(getCurrentItem());
//                activateControls(true);
//                dateVystavField.setReadOnly(false);
//                getSaveButton().setEnabled(true);
//            }
//        });
//        return fakturovatButton;
//    }

//    private Component initStornoButton() {
//        stornoButton = new Button("Storno fakturace");
//        stornoButton.addClickListener(event -> {
//            getCurrentItem().setZaklad(null);
//            getCurrentItem().setCastka(null);
//            getCurrentItem().setDateVystav(null);
//            getBinder().readBean(getCurrentItem());
//
//            activateControls(false);
//            getSaveButton().setEnabled(true);
//        });
//        return stornoButton;
//    }

//    private Component initZakHonorarField() {
//        zakHonorarField = new TextField("Honorář zakázky");
//        zakHonorarField.setReadOnly(true);
//        zakHonorarField.setWidth("8em");
//        getBinder().forField(zakHonorarField)
//                .withNullRepresentation("")
//                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
//                .bind(Fakt::getZakHonorar, null);
//        return zakHonorarField;
//    }

//    private Component initPlneniField() {
//        plneniField = new TextField("Zde bylo Plnění [%]"); // = new TextField("Jméno");
//        plneniField.setPattern("^100(\\.(0{0,2})?)?$|^\\d{1,2}(\\.(\\d{0,2}))?$");
//        plneniField.setSuffixComponent(new Span("[%]"));
//        plneniField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
//        plneniField.setReadOnly(true);
//        plneniField.setValueChangeMode(ValueChangeMode.EAGER);
//        return plneniField;
//    }

//    private Component initZakladField() {
//        zakladField = new TextField("Zde bylo Ze základu");
//        zakladField.setReadOnly(true);
////        castkaField.setSuffixComponent(new Span(faktMena.name()));
////        getBinder().forField(zakladField)
////                .withNullRepresentation("")
////                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
////                .bind(Fakt::getZaklad, Fakt::setZaklad);
//        return zakladField;
//    }

//    private Component initDateTimeExportField() {
////        dateTimeExportField.setValue(getCurrentItem().getDateTimeExport().toString());
//        dateTimeExportField = new TextField("Exportováno");
//        dateTimeExportField.setReadOnly(true);
//        getBinder().forField(dateTimeExportField)
////                .withConverter(new LocalDateToDateConverter())
//                .bind(Fakt::getDateTimeExportStr, null);
//        return dateTimeExportField;
//    }

//    private Component initDevider() {
//        HtmlComponent gap = new Gap("1em");
//        return gap;
//    }



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
        cfaktField = new TextField("Číslo dílčího plnění");
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
        textField = new TextField("Text dílčího plnění");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Fakt::getText, Fakt::setText);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        return textField;
    }

    private Component initCastkaField() {
        castkaField = new TextField("Částka dílčího plnění");
//        castkaField.setReadOnly(true);
        castkaField.setReadOnly(false);
//        castkaField.setSuffixComponent(new Span(faktMena.name()));
        getBinder().forField(castkaField)
                .withNullRepresentation("")
                .withValidator(new StringLengthValidator(
                        "Částka dílčího plnění nesmí být prázdná",
                        1, null))
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .withValidator(castka -> (null != castka) && (castka.compareTo(BigDecimal.ZERO) > 0)
                        , "Částka plnění musí být kladná")
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
                    .withCaption("Zrušení plnění")
                    .withMessage(String.format("Plnění %s nelze zrušit.", ckzfDel))
                    .open()
            ;
            return;
        }
        try {
            revertFormChanges();
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení plnění")
                    .withMessage(String.format("Zrušit plnění %s ?", ckzfDel))
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
                .withCaption("Editace plnění")
                .withMessage("Plnění se nepodařilo uložit.")
                .open();
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace plnění")
                .withMessage("Plnění se nepodařilo zrušit")
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
                    .withCaption("Zrušení plnění.")
                    .withMessage(String.format("Plnění %s se nepodařilo zrušit.", ckzfDel))
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
                    .withCaption("Editace plnění")
                    .withMessage("Plnění nelze uložit, některá pole nejsou správně vyplněna.")
                    .open();
            return false;
        }
        return true;
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
