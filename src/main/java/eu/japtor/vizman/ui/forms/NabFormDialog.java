package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.NabService;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.OperationResult;
import eu.japtor.vizman.ui.components.TwinColGrid;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NabFormDialog extends AbstractComplexFormDialog<Nab> {

//    private ComboBox<PersonState> statusField; // = new ComboBox("Status");
    private TextField cnabField;
    private PasswordField passwordField; // = new TextField("Password");
    private TextField poznamkaField; // = new TextField("Jméno");
    private TextField prijmeniField; // = new TextField("Příjmení");
    private TextField sazbaField; // = new TextField("Sazba");
    private DatePicker nastupField; // = new DatePicker("Nástup");
    private DatePicker vystupField; // = new DatePicker("Výstup");
    private TwinColGrid<Role> twinRolesGridField;

    private Binder<Nab> binder = new Binder<>();
    private Nab currentItem;
    private Nab origItem;
    private Operation currentOperation;
    private OperationResult lastOperationResult = OperationResult.NO_CHANGE;
    private boolean readonly;

    private Registration binderChangeListener = null;
    private NabService nabService;


    public NabFormDialog(
            BiConsumer<Nab, Operation> itemSaver
            , Consumer<Nab> itemDeleter
            , NabService nabService
    ){
//    public NabFormDialog(
//            NabService nabService
//    ){
        super("800px", "600px"
                , false, false
                , itemSaver, itemDeleter, false
        );

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.nabService = nabService;

        getFormLayout().add(
                initCnabField()
                , initPoznamkaField()
        );
    }

    public void openDialog(
            Nab nab, Operation operation
            , String titleItemNameText, String titleEndText
    ){
//        setItemNames(nab.getTyp());

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

//        this.kontFolderOrig = kontFolderOrig;

        openInternal(nab, operation, new Gap(), titleEndText);
    }

    private Component initCnabField() {
        cnabField = new TextField("Číslo nabídky");
        getBinder().forField(cnabField)
                .withValidator(new StringLengthValidator(
                        "Číslo nabídky musí mít 7 znaků",
                        7, 7))
                .withValidator(
                        cnab -> (currentOperation != Operation.ADD) ?
                            true : nabService.fetchNabByCnab(cnab) == null,
                        "Nabídka s tímto číslem již existuje, zvol jiné")
                .bind(Nab::getCnab, Nab::setCnab);
        return cnabField;
    }


    private Component initPoznamkaField() {
        poznamkaField = new TextField("Poznámka");
        getBinder().forField(poznamkaField)
                .bind(Nab::getPoznamka, Nab::setPoznamka);
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

//    private boolean deleteFakt(Nab itemToDelete) {
//        String cnDel = String.format("%s", currentItem.getCnab());
//        OperationResult lastOperResOrig = lastOperationResult;
//        try {
//            nabService.deleteNab(itemToDelete);
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

//    public Nab saveFakt(Nab nabToSave) throws VzmServiceException {
//        try {
//            currentItem = nabService.saveNab(nabToSave, currentOperation);
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

//    private boolean canDeleteNab(final Nab itemToDelete) {
//        return true;
//    }

    @Override
    protected void refreshHeaderMiddleBox(Nab item) {
        // Do nothing
    }

    @Override
    protected void activateListeners() {
        // Do nothing
    }

    @Override
    protected void deactivateListeners() {
        // Do nothing
    }

//    private void closeDialog() {
//        this.close();
//    }

//  --------------------------------------------

}