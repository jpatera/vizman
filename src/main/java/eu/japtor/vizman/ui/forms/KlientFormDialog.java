package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KlientFormDialog extends AbstractComplexFormDialog<Klient> {

    private TextField nameField;
    private TextField noteField;

//    @Autowired
    private KlientService klientService;

    public KlientFormDialog(BiConsumer<Klient, Operation> itemSaver
            , Consumer<Klient> itemDeleter
            , KlientService klientService
    ){
        super("800px", null
                , false, false
                , itemSaver, itemDeleter, false
        );


//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.klientService = klientService;

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
//        long zakKlientsCount = klientService.countZakKlients(Long klientId);
//        if (personCount > 0) {
            openConfirmDeleteDialog("Zrušení klienta",
                    "Opravdu zrušit klienta “" + getCurrentItem().getName() + "“ ?", ""
            );
//                    "Pokud bude uživatel zrušen, budou zrušena i další s ním související data.");
//        } else {
//            deleteKont(getCurrentItem());
//        }
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