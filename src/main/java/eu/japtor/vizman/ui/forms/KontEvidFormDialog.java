package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;
import eu.japtor.vizman.backend.bean.EvidKont;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.ui.components.SimpleEditorDialog;

import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KontEvidFormDialog extends SimpleEditorDialog<EvidKont> {

    private TextField ckontField;
    private TextField textField;
    private TextField folderField;

    private KontService kontService;


    public KontEvidFormDialog(Consumer<EvidKont> itemSaver, KontService kontService) {

        super(itemSaver);
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
        getFormLayout().add(initFolderField());
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
//                .withValidator(new StringLengthValidator(
//                        "Číslo kontraktu musí mít 1-16 znaků",
//                        1, 16))
//                .withValidator(
//                        ckont -> kontService.getByCkont(ckont) == null,
//                        "Kontrakt s tímto číslem již existuje, zvol jiné číslo")
                .bind(EvidKont::getCkont, EvidKont::setCkont);
        return ckontField;
    }

    private Component initTextField() {
        textField = new TextField("Text kontraktu");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
//                .withValidator(new StringLengthValidator(
//                        "Text kontraktu musí mít alespoň jeden znak",
//                        1, null))
//                .withValidator(
//                        text -> kontService.getByText(text) == null,
//                        "Kontrakt se stejným textem již existuje, zadej jiný text")
                .bind(EvidKont::getText, EvidKont::setText);
        return textField;
    }


    private Component initFolderField() {
        folderField = new TextField("Složka kontraktu");
        folderField.getElement().setAttribute("colspan", "2");
        getBinder().forField(folderField)
//                .withValidator(
//                        docdir -> kontService.getByDocdir(docdir) == null,
//                        "Adresář stejného jména již existuje, zadej jiné číslo kontraktu nebo text")
                .bind(EvidKont::getFolder, EvidKont::setFolder);
        folderField.setReadOnly(true);
        return folderField;
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
