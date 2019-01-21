package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.FaktService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.Operation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FaktFormDialog extends AbstractEditorDialog<Fakt> {

    private TextField plneniField;
    private TextField zakladField;
    private TextField castkaField;
    private TextField dateTimeExportField;
    private DatePicker dateDuzpField;
    private TextField textField;
//    private TextField menaField = new TextField("Měna");

    Grid<Fakt> faktGrid = new Grid();
    Grid<ZakDoc> docGrid = new Grid();

//    @Autowired
    private FaktService faktService;


    public FaktFormDialog(BiConsumer<Fakt, Operation> itemSaver,
                          Consumer<Fakt> itemDeleter,
                          FaktService faktService)
    {
//        super(GrammarGender.MASCULINE, Kont.NOMINATIVE_SINGULAR
//                , Kont.GENITIVE_SINGULAR, Kont.ACCUSATIVE_SINGULAR
//                , itemSaver, itemDeleter);
        super(itemSaver, itemDeleter);

        setWidth("900px");
//        setHeight("600px");

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.faktService = faktService;

        getFormLayout().add(initPlneniField());
        getFormLayout().add(initDateDuzpField());
        getFormLayout().add(initTextField());
        getFormLayout().add(initZakladField());
        getFormLayout().add(initCastkaField());
        getFormLayout().add(initDateTimeExportField());
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));

        castkaField.setSuffixComponent(new Span(getCurrentItem().getMena().name()));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());
//        faktGrid.setItems(getCurrentItem().getNodes());
//        docGrid.setItems(getCurrentItem().getKontDocs());

    }

    private Component initPlneniField() {
        plneniField = new TextField("Plnění [%]"); // = new TextField("Jméno");
        plneniField.setPattern("^100(\\.(0{0,2})?)?$|^\\d{1,2}(\\.(\\d{0,2}))?$");
//        plneniField.setPreventInvalidInput(true);
        plneniField.setSuffixComponent(new Span("[%]"));
        getBinder().forField(plneniField)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)

//                .withConverter(String::trim, String::trim)
//                .withValidator(new StringLengthValidator(
//                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
//                        3, null))
//                .withValidator(
//                        objednatel -> (currentOperation != Operation.ADD) ?
//                            true : kontService.getByObjednatel(objednatel) == null,
//                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Fakt::getPlneni, Fakt::setPlneni);
        return plneniField;
    }


    private Component initDateDuzpField() {
        dateDuzpField = new DatePicker("DUZP");
        getBinder().forField(dateDuzpField)
//                .withConverter(String::trim, String::trim)
                .bind(Fakt::getDateDuzp, Fakt::setDateDuzp);
        return dateDuzpField;
    }

    private Component initTextField() {
        textField = new TextField("Text fakturace");
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Fakt::getText, Fakt::setText);
        return textField;
    }

    private Component initCastkaField() {
        castkaField = new TextField("Fakturovaná částka");
        castkaField.setReadOnly(true);
//        castkaField.setSuffixComponent(new Span(faktMena.name()));
        getBinder().forField(castkaField)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Fakt::getCastka, null);
        return castkaField;
    }

    private Component initZakladField() {
        zakladField = new TextField("Fakturovaná částka");
        zakladField.setReadOnly(true);
//        castkaField.setSuffixComponent(new Span(faktMena.name()));
        getBinder().forField(zakladField)
                .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                .bind(Fakt::getZaklad, null);
        return zakladField;
    }

    private Component initDateTimeExportField() {
//        dateTimeExportField.setValue(getCurrentItem().getDateTimeExport().toString());
        dateTimeExportField = new TextField("Export");
        dateTimeExportField.setReadOnly(true);
        getBinder().forField(dateTimeExportField)
//                .withConverter(new LocalDateToDateConverter())
                .bind(Fakt::getDateTimeExportStr, null);
        return dateTimeExportField;
    }

    @Override
    protected void confirmDelete() {

////        LocalTime dateExport = getCurrentItem().getDateExport();
//        // TODO: replace by real DateExport value
//        LocalTime dateExport = null;
//        if (null != dateExport) {
//            new OkDialog().open(
//                    "Nelze zrušit fakturaci, již byla exportována"
//                    , ""
//                    , ""
//            );
//        } else {
//            openConfirmDeleteDialog("Zrušit fakturaci [" + getCurrentItem().getCfakt() + "] ?",
//                    "Opravdu zrušit kontrakt “" + getCurrentItem().getCkont() + "“ ?",
//                    "Pokud bude kontrakt zrušen, budou zrušena i další s ním související data.");
////        } else {
////            doDelete(getCurrentItem());
//        }
    }
}
