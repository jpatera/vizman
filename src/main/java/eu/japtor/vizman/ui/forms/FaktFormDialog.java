package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.KontService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.ui.components.AbstractEditorDialog;
import eu.japtor.vizman.ui.components.ConfirmationDialog;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FaktFormDialog extends AbstractEditorDialog<Fakt> {

    private Mena faktMena;
    private TextField plneniField = new TextField("Plnění [%]"); // = new TextField("Jméno");
    private TextField zakladField = new TextField("Ze základu (honoráře)"); // = new TextField("Jméno");
    private TextField castkaField = new TextField("Fakturovaná částka"); // = new TextField("Jméno");
    private TextField dateTimeExportField = new TextField("Export"); // = new TextField("Jméno");
    private DatePicker dateDuzpField = new DatePicker("DUZP"); // = new TextField("Jméno");
    private TextField textField = new TextField("Text"); // = new TextField("Jméno");
//    private TextField menaField = new TextField("Měna");

    Grid<Fakt> faktGrid = new Grid();
    Grid<ZakDoc> docGrid = new Grid();

//    @Autowired
    private KontService kontService;
    private ZakService zakService;


////    private ListDataProvider<Role> allRolesDataProvider;
//    private Collection<Role> personRoles;
    private final ConfirmationDialog<KontDoc> confirmDocDeregDialog = new ConfirmationDialog<>();
    private final ConfirmationDialog<Zak> confirmZakOpenDialog = new ConfirmationDialog<>();


    public FaktFormDialog(BiConsumer<Fakt, Operation> itemSaver,
                          Consumer<Fakt> itemDeleter)
//                          FaktService faktService)
    {
//        super(GrammarGender.MASCULINE, Kont.NOMINATIVE_SINGULAR
//                , Kont.GENITIVE_SINGULAR, Kont.ACCUSATIVE_SINGULAR
//                , itemSaver, itemDeleter);
        super(itemSaver, itemDeleter);

        setWidth("1200px");
//        setHeight("600px");

//        getFormLayout().setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("10em", 2),
//                new FormLayout.ResponsiveStep("12em", 3));

        this.kontService = kontService;

        faktMena = getCurrentItem().getMena();
        initPlneniField();
        initCastkaField();
        initDateDuzpField();
        initTextField();
        initDateTimeExportField();

        getFormLayout().add(plneniField);
        getFormLayout().add(zakladField);
        getFormLayout().add(castkaField);
        getFormLayout().add(dateDuzpField);
        getFormLayout().add(textField);
        getFormLayout().add(dateTimeExportField);
    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {
        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());
//        faktGrid.setItems(getCurrentItem().getNodes());
//        docGrid.setItems(getCurrentItem().getKontDocs());

    }


    private void initCastkaField() {
        castkaField.setSuffixComponent(new Span(faktMena.name()));
        castkaField.setReadOnly(true);
        getBinder().forField(castkaField)
                .withConverter(
                        new StringToBigDecimalConverter("Must enter a number"))
                .bind(Fakt::getCastka, null);
    }

    private void initPlneniField() {
        plneniField.setPattern("[0-9]3.[0-9]");
        plneniField.setPreventInvalidInput(true);
        plneniField.setSuffixComponent(new Span("[%]"));
        getBinder().forField(plneniField)
                .withConverter(
                        new StringToBigDecimalConverter("Must enter a number"))

//                .withConverter(String::trim, String::trim)
//                .withValidator(new StringLengthValidator(
//                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
//                        3, null))
//                .withValidator(
//                        objednatel -> (currentOperation != Operation.ADD) ?
//                            true : kontService.getByObjednatel(objednatel) == null,
//                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
                .bind(Fakt::getPlneni, Fakt::setPlneni);
    }


    private void initDateDuzpField() {
        getBinder().forField(dateDuzpField)
//                .withConverter(String::trim, String::trim)
                .bind(Fakt::getDateDuzp, Fakt::setDateDuzp);
    }

    private void initDateTimeExportField() {
        dateTimeExportField.setValue(getCurrentItem().getDateTimeExport().toString());
    }

    private void initTextField() {
        textField.getElement().setAttribute("colspan", "2");
        getBinder().forField(textField)
                .bind(Fakt::getText, Fakt::setText);
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
