package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.FormatUtils;
import eu.japtor.vizman.ui.components.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@SpringComponent
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ZakFormDialog extends AbstractEditorDialog<Zak> implements BeforeEnterObserver {

    final StringToBigDecimalConverter bigDecimalMoneyConverter;
    final ValueProvider<Fakt, String> castkaProvider;
    final ComponentRenderer<HtmlComponent, Fakt> moneyCellRenderer;

    private Mena zakMena;

    private TextField czakField;
    private Button evidChangeBtn;
    private Checkbox archCheck;
    private TextField txtField;
    private TextField honorarField;
    private TextField menaField;

    //    private Span datZadComp = new Span("Datum zadání");
//    private Checkbox archiveCheckbox; // = new DatePicker("Nástup");

    private Grid<ZakDoc> docGrid;
//    private HorizontalLayout docDirComponent;
//    private FormLayout.FormItem docDirComponent;
    private FlexLayout docDirComponent;
//    private Paragraph docDirField;
    private TextField docDirField;
    private Button openDocDirBtn;
    private HorizontalLayout docGridBar;
    private Button registerDocButton;


    private Grid<Fakt> faktGrid;
    private Button newFaktButton;

    private ZakFormDialog zakFormDialog;
    private final ConfirmationDialog<ZakDoc> confirmDocUnregisterDialog = new ConfirmationDialog<>();
    private final ConfirmationDialog<Fakt> confirmFaktOpenDialog = new ConfirmationDialog<>();

//    @Autowired
    private ZakService zakService;


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    public ZakFormDialog(BiConsumer<Zak, Operation> itemSaver,
                         Consumer<Zak> itemDeleter,
                         ZakService zakService)
    {
//        super(Zak.ZAK_GENDER, Zak.ZAK_NOMINATIVE_SINGULAR
//                , Zak.ZAK_GENITIVE_SINGULAR, Zak.ZAK_ACCUSATIVE_SINGULAR
//                , itemSaver, itemDeleter);
        super(true, true, itemSaver, itemDeleter);

        this.zakService = zakService;

        setWidth("1200px");
        //        setHeight("600px");

        bigDecimalMoneyConverter = new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(true);
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat;
            }
        };

        castkaProvider = (fakt) -> FormatUtils.moneyFormat.format(fakt.getCastka());

        moneyCellRenderer = new ComponentRenderer<>(fakt -> {
            Div comp = new Div();
            if ((null != fakt) && (fakt.getCastka().compareTo(BigDecimal.ZERO) < 0)) {
                comp.getStyle()
                        .set("color", "red")
                ;
            }
            comp.setText(FormatUtils.moneyFormat.format(fakt.getCastka()));
            return comp;
        });

        ;

        getFormLayout().add(initCzakField());
        getFormLayout().add(initEvidArchComponent());
        getFormLayout().add(initTextField());
        getFormLayout().add(initHonorarField());
//        getFormLayout().add(new Paragraph(""));
        getFormLayout().add(initMenaField());

//        getUpperGridCont().add(new Ribbon(Ribbon.Orientation.VERTICAL, "1em"));
        getUpperGridCont().add(initDocDirComponent());
//        getUpperGridCont().add(initDocGridBar());
        getUpperGridCont().add(initDocGrid());

        getLowerGridCont().add(initFaktGridBar());
        getLowerGridCont().add(initFaktGrid());

    }

    /**
     * Called by abstract parent dialog from its open(...) method.
     */
    protected void openSpecific() {

        // Mandatory, should be first
        setItemNames(getCurrentItem().getTyp());


        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        datZadComp.setLocale(new Locale("cs", "CZ"));
//        vystupField.setLocale(new Locale("cs", "CZ"));


//        getBinder().forField(twinRolesGridField)
//                .bind(Person::getRoles, Person::setRoles);

//        twinRolesGridField.initLeftItems(getCurrentItem().getRoles());

        zakMena = getCurrentItem().getMena();

        faktGrid.setItems(getCurrentItem().getFakts());
        docGrid.setItems(getCurrentItem().getZakDocs());

    }


//    private void addStatusField() {
//        statusField.setDataProvider(DataProvider.ofItems(PersonStatus.values()));
//        getFormLayout().add(statusField);
//        getBinder().forField(statusField)
////                .withConverter(
////                        new StringToIntegerConverter("Must be a number"))
//                .bind(Person::getStatus, Person::setStatus);
//    }

//    private void initObjednatelField() {
//        getBinder().forField(objednatelField)
//                .withConverter(String::trim, String::trim)
////                .withValidator(new StringLengthValidator(
////                        "Uživatelské jméno musí obsahovat aspoň 3 znamky",
////                        3, null))
////                .withValidator(
////                        objednatel -> (currentOperation != Operation.ADD) ?
////                            true : kontService.getByObjednatel(objednatel) == null,
////                        "Uživatel s tímto jménem již existuje, zvol jiné jméno")
//                .bind(Zak::getObjednatel, Zak::setObjednatel);
//    }

    private Component initCzakField() {
        czakField = new TextField("Číslo zakázky");
        czakField.setReadOnly(true);
        czakField.getStyle()
//                .set("background-color", "yellow")
//                    .set("text-indent", "1em");
                .set("padding-top", "0em");
        getBinder().forField(czakField)
                .withConverter(new StringToIntegerConverter("Neplatný formát čísla"))
                .bind(Zak::getCzak, Zak::setCzak);
        return czakField;
    }

    private Component initEvidChangeButton() {
        evidChangeBtn = new Button("Evidence");
        evidChangeBtn.addClickListener(event -> {});
        return evidChangeBtn;
    }

    private Component initArchCheck() {
        archCheck = new Checkbox("Archiv"); // = new TextField("Username");
        getBinder().forField(archCheck)
                .bind(Zak::getArch, Zak::setArch);
        return archCheck;
    }

    private Component initEvidArchComponent() {
        FlexLayout evidArchCont = new FlexLayout();
        evidArchCont.setAlignItems(FlexComponent.Alignment.BASELINE);
        evidArchCont.add(initEvidChangeButton(), new Ribbon("3em"), initArchCheck());
        return evidArchCont;
    }

    private Component initTextField() {
        txtField = new TextField("Text zakázky");
        txtField.getElement().setAttribute("colspan", "2");
        getBinder().forField(txtField)
                .bind(Zak::getText, Zak::setText);
        txtField.setReadOnly(true);
        return txtField;
    }

    private Component initMenaField() {
        menaField = new TextField("Měna");
        getBinder().forField(menaField)
            .withConverter(mena -> Mena.valueOf(mena), menaEnum -> menaEnum.name())
            .bind(Zak::getMena, null);
        return menaField;
    }

    private Component initHonorarField() {
        honorarField = new TextField("Honorář");
//        honorarField.setReadOnly(true);
        honorarField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        getBinder().forField(honorarField)
                .withConverter(bigDecimalMoneyConverter)
                .bind(Zak::getHonorar, Zak::setHonorar);
        return honorarField;
    }

    // ----------------------------------------------

    private Component initDocDirComponent() {
        docDirComponent = new FlexLayout();
        docDirComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        docDirComponent.setWidth("100%");
        docDirComponent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        docDirComponent.add(
                initDocDirField(),
                new Ribbon(),
                initRegisterDocButton()
        );
        return docDirComponent;
    }

    private Component initDocDirField() {
//        docDirField = new Paragraph();
        docDirField = new OpenDirField(
                "Dokumenty zakázky"
                , "Adresář není zadán"
                , event -> {}
//                , event -> FileUtils.validatePathname(event.getValue()

        );
        docDirField.getStyle()
                .set("padding-top", "0em");
        docDirField.setWidth("100%");
        docDirField.setPlaceholder("[zak-doc-root]\\neco\\...");
//        docDirField.setReadOnly(true);
        return docDirField;
    }

//    private Component initOpenDocDirBtn() {
//        openDocDirBtn = new Button("Otevřít");
//        openDocDirBtn.addClickListener(event -> {});
//        return openDocDirBtn;
//    }

    private Component initRegisterDocButton() {
        registerDocButton = new Button("+ Dokument");
        registerDocButton.addClickListener(event -> {});
        return registerDocButton;
    }

//    private Component initDocGridBar() {
//        docGridBar = new HorizontalLayout();
//        docGridBar.setWidth("100%");
//        docGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        docGridBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
//        docGridBar.add(initRegisterDocButton());
//        return docGridBar;
//    }

    private Component initDocGrid() {
        docGrid = new Grid();
        docGrid.setColumnReorderingAllowed(true);
        docGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        docGrid.setId("doc-grid");
        docGrid.setClassName("vizman-simple-grid");
        docGrid.setHeight("12em");

        docGrid.addColumn(ZakDoc::getFilename).setHeader("Soubor");
        docGrid.addColumn(ZakDoc::getNote).setHeader("Poznámka");
//        docGrid.addColumn("Honorář CZK");
        docGrid.addColumn(ZakDoc::getDateCreate).setHeader("Vloženo");
        docGrid.addColumn(new ComponentRenderer<>(this::buildDocRemoveButton))
                .setFlexGrow(0);
        return docGrid;
    }

    private Component buildDocRemoveButton(ZakDoc kontDoc) {
        return new GridItemOpenBtn(event -> {
            close();
            confirmDocUnregisterDialog.open("Registrace dokumentu",
                    "Zrušit registraci dokumentu?", "", "Zrušit",
                    true, kontDoc, this::removeDocRegistration, this::open);
        });
    }

    private void removeDocRegistration(ZakDoc zakDoc) {
        close();
    }


    // ----------------------------------------------

    private Component initNewFaktButton() {
        newFaktButton = new Button("+ Fakturace");
        newFaktButton.addClickListener(event -> zakFormDialog.open(
                new Zak(ItemType.ZAK), AbstractEditorDialog.Operation.ADD)
        );
        return newFaktButton;
    }

    private Component initFaktGridTitle() {
        H4 faktTitle = new H4();
        faktTitle.setText(ItemNames.getNomS(ItemType.FAKT));
        return faktTitle;
    }

    private Component initFaktGridBar() {
        FlexLayout faktGridBar = new FlexLayout();
        faktGridBar.setAlignItems(FlexComponent.Alignment.BASELINE);
        faktGridBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        faktGridBar.setWidth("100%");

        faktGridBar.add(
                initFaktGridTitle(),
                new Ribbon(),
                initNewFaktButton()
        );
        return faktGridBar;
    }

    private Component initFaktGrid() {
        faktGrid = new Grid();

        faktGrid.setColumnReorderingAllowed(true);
        faktGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        faktGrid.setId("fakt-grid");
        faktGrid.setClassName("vizman-simple-grid");
        faktGrid.getStyle().set("marginTop", "0.5em");
        faktGrid.setWidth( "100%" );
        faktGrid.setHeight("12em");
//        faktGrid.setHeight(null);

//        zakGrid.getElement().setAttribute("colspan", "2");
//        zakGrid.getStyle().set("padding-right", "0em");
//        zakGrid.getStyle().set("padding-left", "0em");
//        zakGrid.getStyle().set("padding-top", "2.5em");
//        zakGrid.getStyle().set("padding-bottom", "2.5em");

        faktGrid.addColumn(Fakt::getPlneni).setHeader("Plnění [%]")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getCastka).setHeader("Částka")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getZaklad).setHeader("Základ")
                .setFlexGrow(0);
//        faktGrid.addColumn(Fakt::getCastka).setHeader("Částka [" + getCurrentItem().getMena().name() + "]")
//                .setFlexGrow(0);
//        faktGrid.addColumn(Fakt::getZaklad).setHeader("Základ [" + getCurrentItem().getMena().name() + "]")
//                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getText).setHeader("Text")
                .setFlexGrow(1);
        faktGrid.addColumn(Fakt::getDateDuzp).setHeader("DUZP")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getDateVystav).setHeader("Vystaveno")
                .setFlexGrow(0);
        faktGrid.addColumn(Fakt::getDateTimeExport).setHeader("Exportováno")
                .setFlexGrow(0);
        faktGrid.addColumn(new ComponentRenderer<>(this::buildFaktOpenButton))
                .setFlexGrow(0);
        return faktGrid;
    }



    private Component buildFaktOpenButton(Fakt fakt) {
            return new GridItemOpenBtn(event -> {
                close();
                confirmFaktOpenDialog.open("Otevřít fakturaci ?",
                        "", "", "Zrušit",
//                        true, zak, this::openZakForm, this::open);
                        true, fakt, this::openFaktForm, this::open);
            });
    }

    private void openFaktForm(Fakt fakt) {
        close();
    }


//    private void addTerminField() {
//
//        // Nastup field binder:
//        Binder.Binding<Person, LocalDate> nastupBinder = getBinder().forField(nastupField)
//                .withValidator(nastupNullCheck(),"Nástup nemuže být prázdný pokud je zadáno ukončení")
//                .withValidator(nastupBeforeVystupCheck(),"Nástup nemuže následovat po ukončení")
//                .bind(Person::getNastup, Person::setNastup);
//
//        // Vystup field binder:
//        Binder.Binding<Person, LocalDate> vystupBinder = getBinder().forField(vystupField)
//                .withValidator(vystupNotNullCheck(), "Ukončení nemůže být zadáno pokud není zadán nástup")
//                .withValidator(vystupAfterNastupCheck(),"Ukončení nemůže předcházet nástup")
//                .bind(Person::getVystup, Person::setVystup);
//
//        nastupField.addValueChangeListener(event -> vystupBinder.validate());
//        vystupField.addValueChangeListener(event -> nastupBinder.validate());
//
//        // Add fields to the form:
//        getFormLayout().add(nastupField);
//        getFormLayout().add(vystupField);
//    }

//    private SerializablePredicate<LocalDate> nastupNullCheck() {
//        return nastup -> ((null == vystupField.getValue()) || (null != nastup));
//    }
//
//    private SerializablePredicate<LocalDate> nastupBeforeVystupCheck() {
//        return nastup ->
//                (null == vystupField.getValue())
//                || ((null != nastup) && nastup.isBefore(vystupField.getValue()));
//    }
//
//    private SerializablePredicate<LocalDate> vystupNotNullCheck() {
//        return vystup -> ((null != nastupField.getValue()) || (null == vystup));
//    }
//
//    private SerializablePredicate<LocalDate> vystupAfterNastupCheck() {
//        return vystup ->
//            (null == vystup)
//            || ((null != nastupField.getValue()) && vystup.isAfter(nastupField.getValue()));
//    }


    @Override
    protected void confirmDelete() {

        long nodesCount = getCurrentItem().getNodes().size();
        if (nodesCount > 0) {
            new OkDialog().open(
                    "Zrušení zakázky"
                    , "Zakázku " + getCurrentItem().getCkont() + " nelze zrušit, obsahuje fakturace"
                    , ""
            );
        } else {
            openConfirmDeleteDialog("Zrušit zakázku ?",
                    "Opravdu zrušit kontrakt “" + getCurrentItem().getCkont() + "“ ?",
                    "Pokud bude kontrakt zrušen, budou zrušena i další s ním související data.");
//        } else {
//            doDelete(getCurrentItem());
        }
    }
}
