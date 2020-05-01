package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.KlientService;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.TwinColGrid;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KlientFormDialog extends AbstractComplexFormDialog<Klient> {

//    private ComboBox<PersonState> statusField; // = new ComboBox("Status");
    private TextField nameField; // = new TextField("Username");
    private PasswordField passwordField; // = new TextField("Password");
    private TextField noteField; // = new TextField("Jméno");
    private TextField prijmeniField; // = new TextField("Příjmení");
    private TextField sazbaField; // = new TextField("Sazba");
    private DatePicker nastupField; // = new DatePicker("Nástup");
    private DatePicker vystupField; // = new DatePicker("Výstup");
    private TwinColGrid<Role> twinRolesGridField;

//    private final VerticalLayout roleGridContainer;
//    private Grid<Role> roleTwinGrid;

//    @Autowired
    private KlientService klientService;


    public KlientFormDialog(BiConsumer<Klient, Operation> itemSaver
            , Consumer<Klient> itemDeleter
            , KlientService klientService
    ){
        super("800px", "600px"
                , false, false
                , itemSaver, itemDeleter, false
        );


//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.klientService = klientService;

        nameField = new TextField("Název firmy");
        noteField = new TextField("Poznámka");

        initNameField();
        addNoteField();
    }

    public void openDialog(
            Klient klient, Operation operation
            , String titleItemNameText, String titleEndText
    ){
        setItemNames(klient.getTyp());

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

//        this.kontFolderOrig = kontFolderOrig;

        openInternal(klient, operation, titleItemNameText, new Gap(), titleEndText);
    }



    private void initNameField() {
        getFormLayout().add(nameField);

        getBinder().forField(nameField)
                .withValidator(new StringLengthValidator(
                        "Název firmy klienta musí obsahovat aspoň 2-127 znaků",
                        2, 127))
                .withValidator(
                        name -> (currentOperation != Operation.ADD) ?
                            true : klientService.fetchKlientByName(name) == null,
                        "Firma klienta s tímto názvem již existuje, zvol jiný")
                .bind(Klient::getName, Klient::setName);
    }


    private void addNoteField() {
        getFormLayout().add(noteField);

        getBinder().forField(noteField)
                .bind(Klient::getNote, Klient::setNote);
    }


    @Override
    protected void confirmDelete() {
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
}