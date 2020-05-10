package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.service.CalService;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.Operation;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CalyHolFormDialog extends AbstractComplexFormDialog<CalyHol> {

    private DatePicker holDateField; // = new DatePicker("Nástup");
    private TextField holTextField; // = new TextField("Jméno");

//    @Autowired
    private CalService calService;
    private CalyHol itemOrig;

    public CalyHolFormDialog(BiConsumer<CalyHol, Operation> itemSaver
            , Consumer<CalyHol> itemDeleter
            , CalService calService
    ){
        super("800px", "300px"
                , false, false
                , itemSaver, itemDeleter, true
        );

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.calService = calService;

        holDateField = new DatePicker("Datum");

        getFormLayout().add(initHolDateField());
        getFormLayout().add(new Span());
        getFormLayout().add(initHolTextField());
    }

    public CalyHol getItemOrig() {
        return itemOrig;
    }

    public void openDialog(
            CalyHol calyHol, Operation operation
            , String titleItemNameText, String titleEndText
    ){
//        setItemNames(calyHol.getTyp());

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
        this.itemOrig = new CalyHol();
        this.itemOrig.setId(calyHol.getId());
        this.itemOrig.setVersion(calyHol.getVersion());
        this.itemOrig.setYr(calyHol.getYr());
        this.itemOrig.setHolDate(calyHol.getHolDate());
        this.itemOrig.setHolText(calyHol.getHolText());

        holDateField.setLocale(new Locale("cs", "CZ"));

        openInternal(calyHol, operation, new Gap(), titleEndText);
    }


    private Component initHolDateField() {
        holDateField = new DatePicker("Datum");
        getBinder().forField(holDateField)
                .withValidator(date -> (date.equals(itemOrig.getHolDate()) || !calService.calyHolExist(date)),
                        "Nelze uložit, svátek  pro toto datum je již zadán.")
                .withValidator(date -> calService.calyExist(date.getYear()),
                        "Nelze uložit, není vygenerován pracovní fond pro zadaný rok.")
                .bind(CalyHol::getHolDate, CalyHol::setHolDate);
        return holDateField;
    }

    private Component initHolTextField() {
        holTextField = new TextField("Název svátku");
        holTextField.getElement().setAttribute("colspan", "2");
        getBinder().forField(holTextField)
                .bind(CalyHol::getHolText, CalyHol::setHolText);
        return holTextField;
    }

    @Override
    protected void confirmDelete() {
//        if (LocalDate.now().isAfter(getCurrentItem().getHolDate())) {
            openConfirmDeleteDialog("Zrušení svátku",
                    "Opravdu zrušit svátek “" + getCurrentItem().getHolText() + "“ ?", ""
            );
//                    "Pokud bude uživatel zrušen, budou zrušena i další s ním související data.");
//        } else {
//            deleteKont(getCurrentItem());
//        }
    }

    @Override
    protected void refreshHeaderMiddleBox(CalyHol item) {

    }

    @Override
    protected void activateListeners() {

    }

    @Override
    protected void deactivateListeners() {

    }
}