package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.NabService;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KlientFormDialog extends AbstractComplexFormDialog<Klient> {

    private TextField nameField;
    private TextField noteField;

//    @Autowired
    private KlientService klientService;
    private KontService kontService;
    private NabService nabService;

    public KlientFormDialog(BiConsumer<Klient, Operation> itemSaver
            , Consumer<Klient> itemDeleter
            , KlientService klientService
            , KontService kontService
            , NabService nabService
    ){
        super("800px", null
                , false, false
                , itemSaver, itemDeleter, false
        );

        this.klientService = klientService;
        this.kontService = kontService;
        this.nabService = nabService;

        noteField = new TextField("Poznámka");

        getFormLayout().add(
                initNameField()
                , initNoteField()
        );
    }

    public void openDialog(Klient klient, Operation operation) {
        openInternal(klient, operation, false, true, new Gap(), null);
    }


    private Component initNameField() {
        nameField = new TextField("Název firmy");
        nameField.getElement().setAttribute("colspan", "4");
        getBinder().forField(nameField)
                .withValidator(new StringLengthValidator(
                        "Název firmy klienta musí obsahovat aspoň 2-127 znaků",
                        2, 127))
                .withValidator(
                        name -> (currentOperation != Operation.ADD) ?
                            true : klientService.fetchKlientByName(name) == null,
                        "Firma klienta s tímto názvem již existuje, zvol jiný")
                .bind(Klient::getName, Klient::setName);
        return nameField;
    }


    private Component initNoteField() {
        noteField = new TextField("Poznámka");
        noteField.getElement().setAttribute("colspan", "4");
        getBinder().forField(noteField)
                .bind(Klient::getNote, Klient::setNote);
        return noteField;
    }


    @Override
    public void confirmDelete() {

        long countAssignedToKonts = getKlientCountAssignedToKonts(getCurrentItem());
        if (countAssignedToKonts > 0) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení klienta")
                    .withMessage(String.format("Klienta “%s“ nelze zrušit, je přiřazen k %s kontraktům."
                            , getCurrentItem().getName(), countAssignedToKonts)
                    )
                    .open()
            ;
            return;
        }
        long countAssignedToNabs = getKlientCountAssignedToNabs(getCurrentItem());
        if (countAssignedToNabs > 0) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Zrušení klienta")
                    .withMessage(String.format("Klienta “%s“ nelze zrušit, je přiřazen k %s nabídkám."
                            , getCurrentItem().getName(), countAssignedToNabs)
                    )
                    .open()
            ;
            return;
        }
        try {
            revertFormChanges();
            ConfirmDialog.createQuestion()
                    .withCaption("Zrušení klienta")
                    .withMessage(String.format("Zrušit klienta “" + getCurrentItem().getName() + "“ ?", ""))
                    .withOkButton(() -> {
                                doDelete(getCurrentItem());
                                closeDialog();
                            }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT")
                    )
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        } catch (VzmServiceException e) {
            showDeleteErrMessage();
        }

    }

    private long getKlientCountAssignedToKonts(final Klient itemToDelete) {
        return kontService.getAssignedByKontsCount(itemToDelete);
    }

    private long getKlientCountAssignedToNabs(final Klient itemToDelete) {
        return nabService.getCountOfNabsWithObjednatel(itemToDelete);
    }

    private void revertFormChanges() {
        getBinder().removeBean();
        getBinder().readBean(getCurrentItem());
//        lastOperationResult = OperationResult.NO_CHANGE;
    }

    private void showDeleteErrMessage() {
        ConfirmDialog.createError()
                .withCaption("Editace klienta")
                .withMessage("Klienta se nepodařilo zrušit")
                .open()
        ;
    }

    private void closeDialog() {
        this.close();
    }

    @Override
    protected void refreshHeaderMiddleBox(Klient item) {

    }

    @Override
    protected void activateListeners() {

    }

    @Override
    protected void deactivateListeners() {

    }
}