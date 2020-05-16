package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.KontView;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.NabViewService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.OperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    private Binder<Nab> binder = new Binder<>();
    private Nab currentItem;
    private Nab origItem;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;
    private boolean readOnly;

    private Registration binderChangeListener = null;

    private NabViewService nabViewService;
    private KontService kontService;
    private KlientService klientService;
    private List<Klient> klientList;
    private List<KontView> kontViewList;


    public NabFormDialog(
            BiConsumer<Nab, Operation> itemSaver
            , Consumer<Nab> itemDeleter
            , NabViewService nabViewService
            , KontService kontService
            , KlientService klientService
    ){
        super("800px", null
                , false, false
                , itemSaver, itemDeleter, true
        );

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.nabViewService = nabViewService;
        this.kontService = kontService;
        this.klientService = klientService;

        getFormLayout().add(
                initRokField()
                , initCnabField()
                , initTextField()
                , initObjednatelCombo()
                , initCkontCombo()
                , initPoznamkaField()
        );
        initVzCheckBox();

    }

    public void openDialog(
            boolean readonly
            , Nab nab
            , Operation operation
    ){
        this.readOnly = readonly;
        this.currentItem = nab;
        this.origItem = Nab.getNewInstance(nab);
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
        rokField.setValueChangeMode(ValueChangeMode.EAGER);
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
        cnabField.setValueChangeMode(ValueChangeMode.EAGER);
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
        binder.removeBinding(objednatelCombo);
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
        binder.removeBinding(ckontCombo);
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

//    private boolean deleteFakt(NabView itemToDelete) {
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

//    public NabView saveFakt(NabView nabToSave) throws VzmServiceException {
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

//    private boolean canDeleteNab(final NabView itemToDelete) {
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
        if (null != binder) {
            binderChangeListener = binder.addValueChangeListener(e -> {
                adjustControlsOperability(false, true, true, isDirty(),  binder.isValid());
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


//    private void closeDialog() {
//        this.close();
//    }

//  --------------------------------------------

}