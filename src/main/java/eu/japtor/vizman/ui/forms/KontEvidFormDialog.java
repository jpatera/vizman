package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.SerializablePredicate;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.TwinColGrid;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontEvidFormDialog extends AbstractEditorDialog<Kont> {

    private TextField ckontField;
    private TextField textField;
    private TextField docDirField;
    private TextField projDirField;

    private KontService kontService;


    public KontEvidFormDialog(BiConsumer<Kont, Operation> itemSaver, KontService kontService) {

        super(itemSaver, null);
        this.setWidth("700px");
//        this.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        this.setSizeFull();;
//        setHeight("600px");

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.kontService = kontService;

        getFormLayout().add(initCkontField());
        getFormLayout().add(initTextField());
        getFormLayout().add(initDocDirField());
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    @Override
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        nastupField.setLocale(new Locale("cs", "CZ"));

    }

    @Override
    protected void confirmDelete() {

    }

    private Component initCkontField() {
        ckontField = new TextField("Číslo kontraktu");
        getBinder().forField(ckontField)
                .withValidator(new StringLengthValidator(
                        "Číslo kontraktu musí mít 1-16 znaků",
                        1, 16))
                .withValidator(
                        ckont -> kontService.getByCkont(ckont) == null,
                        "Kontrakt s tímto číslem již existuje, zvol jiné číslo")
                .bind(Kont::getCkont, Kont::setCkont);
        return ckontField;
    }

    private Component initTextField() {
        textField = new TextField("Text kontraktu");
        getBinder().forField(ckontField)
                .withValidator(new StringLengthValidator(
                        "Text kontraktu musí mít alespoň jeden znak",
                        1, null))
                .withValidator(
                        text -> kontService.getByText(text) == null,
                        "Kontrakt se stejným textem již existuje, zadej jiný text")
                .bind(Kont::getText, Kont::setText);
        return textField;
    }

    private Component initDocDirField() {
        docDirField = new TextField("Adresář pro dokumenty kontraktu");
        getBinder().forField(docDirField)
                .withValidator(
                        docdir -> kontService.getByDocdir(docdir) == null,
                        "Kontrakt se stejným dokumentovým adresářem již existuje, zadej jiný text")
                .bind(Kont::getDocdir, Kont::setDocdir);
        docDirField.setReadOnly(true);
        return docDirField;
    }


//    private void addStatusField() {
//        statusField.setDataProvider(DataProvider.ofItems(PersonStatus.values()));
//        getFormLayout().add(statusField);
//        getBinder().forField(statusField)
////                .withConverter(
////                        new StringToIntegerConverter("Must be a number"))
//                .bind(Person::getStatus, Person::setStatus);
//    }


//    private void addSazbaField() {
//        getFormLayout().add(sazbaField);

//        sazbaField = new TextField("Sazba");
//        sazbaField.setPattern("[0-9]*");
//        sazbaField.setPreventInvalidInput(true);
//        sazbaField.setSuffixComponent(new Span("CZK"));

//        getBinder().forField(sazbaField)
//                .withConverter(
//                        new StringToBigDecimalConverter("Must enter a number"))
//                .bind(Person::getSazba, Person::setSazba);
//    }

}
