package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.CalymRepo;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.service.DochsumService;
import eu.japtor.vizman.backend.service.DochsumZakService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.service.ZakService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_PRUH;

@Route(value = ROUTE_PRUH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.PRUH_USE
})
@SpringComponent
@UIScope
public class PruhView extends VerticalLayout implements HasLogger, BeforeEnterListener {

    private static final String D01_KEY = "d01";
    private static final String D02_KEY = "d02";

    private static final DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter upperDochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE");
    private static final DateTimeFormatter lowerDochDateHeaderFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy, EEEE");
    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    private ConfirmDialog zakSelectDialog;

    private String authUsername = "vancik";

    private List<Person> pruhPersonList;
    private Person pruhPerson;
    private ComboBox<Person> pruhPersonSelector;

    private List<Calym> pruhCalymList;
    private Calym pruhCalym;
    private ComboBox<Calym> pruhCalymSelector;
//    private Select<Calym> pruhCalymSelector;

    private static final Locale czLocale = new Locale("cs", "CZ");

//    private HorizontalLayout pruhHeader = new HorizontalLayout();

    VerticalLayout clockContainer;
    private Span clockDisplay = new Span();

    //    private Span gridZakTitle = new Span();
    private Span gridZakTitle;
    private Span gridZakSumTitle = new Span();

    private HorizontalLayout gridZakButtonBar;

    private Button loadTodayButton;
    private Button loadPrevDateButton;
    private Button loadNextDateButton;
    private Button loadLastDateButton;
//    private Button removePruhZakBtn;
    private Button cancelEditButton;
    private Button zakAddButton;

    private Button prenosPersonDateButton;
    private Button cancelPrenosPersonDateButton;
    private Button prenosToAnalyzaButton;
    private Button prenosPersonDateAllButton;

//    private Button prichodBtn;
//    private Button prichodAltBtn;
//    private RadioButtonGroup<Cin> odchodRadio;
//    private Button odchodButton;
//    private Button odchodAltButton;

//    Button dovolenaButton;
//    Button dovolenaHalfButton;
//    Button dovolenaZrusButton;
//    Button sluzebkaButton;
//    Button sluzebkaZrusButton;
//    Button nemocButton;
//    Button nemocZrusButton;
//    Button volnoButton;
//    Button volnoZrusButton;




    HorizontalLayout pruhTitleBar;
    HorizontalLayout pruhFooterBar;

    Grid<PruhZak> pruhZakGrid;
    List<PruhZak> pruhZakList = new ArrayList<>();
    Grid<PruhZak> pruhZakSumGrid;
    List<PruhZak> pruhZakSumList = new ArrayList<>();
    Grid<PruhZak> pruhParagGrid;
    List<PruhZak> pruhParagList = new ArrayList<>();

//    HorizontalLayout pruhRecLowerHeader = new HorizontalLayout();
//    Grid<Doch> lowerDochGrid;
//    List<Doch> lowerDochList;
//    HorizontalLayout dochRecLowerFooter = new HorizontalLayout();


//    Clock minuteClock = Clock.tickMinutes(ZoneId.systemDefault());
//    private TimeThread timeThread;

//    @Autowired
//    public DochRepo kontRepo;

    @Autowired
    public PersonService personService;

    @Autowired
    public CinRepo cinRepo;

    @Autowired
    public CalymRepo calymRepo;

    @Autowired
    public DochsumService dochsumService;

    @Autowired
    public DochsumZakService dochsumZakService;

    @Autowired
    public ZakService zakService;



    //    @Autowired
//    public DochForm(Person pruhPerson) {
    public PruhView() {
//        super();
        buildForm();
//        initPruhData(pruhPerson, pruhCalym);
    }

    @PostConstruct
    public void init() {
        initPruhData();
        zakSelectDialog = ConfirmDialog.createInfo()
                .withCaption("Zakázky");
    }



    @Override
    protected void onAttach(AttachEvent attachEvent) {

        getLogger().info("## ON ATTACH DochView ##");

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        pruhYmSelectList.setLocale(new Locale("cs", "CZ"));
//        pruhCalymSelector.setLocale(czLocale);

//        dochDatePrev = LocalDate.now().minusDays(1);
//        gridZakSumTitle.setText(dochDatePrev.format(lowerDochDateHeaderFormatter));

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        getLogger().info("## ON DETACH DochView ##");

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }



    public void initPruhData() {

        loadPersonDataFromDb();
        Person pruhPersonByAuth = getPersonFromList(authUsername)
                .orElse(null);
        pruhPersonSelector.setValue(pruhPersonByAuth);
//        fireEvent(new ComboBox.ComponentValueChangeEvent(pruhPersonSelector, false));

//        if (null == pruhPersonByAuth || null == pruhPerson) {
//            pruhPerson = pruhPersonByAuth;
//        } else {
//            pruhPerson = pruhPersonByAuth;
//        }

        loadCalymDataFromDb();
        Calym pruhCalymByToday = getCalymFromListByYm(YearMonth.now())
                .orElse(null);
        pruhCalymSelector.setValue(pruhCalymByToday);
//        fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(pruhYmSelectList, false));

        updatePruhGrids(pruhPerson, pruhCalym);
    }


    private Optional<Person> getPersonFromList(String username) {
        return pruhPersonList.stream()
                .filter(person -> person.getUsername().toLowerCase().equals(username.toLowerCase()))
                .findFirst();
//                .findFirst().orElse(null);
    }

    private  Optional<Calym> getCalymFromListByYm(final YearMonth ym) {
        return pruhCalymList.stream()
                .filter(calym -> calym.getCalYm().equals(ym))
                .findFirst();
//                .findFirst().orElse(null);

    }

    private void loadPersonDataFromDb() {
        pruhPersonList = personService.fetchAllActive();
        pruhPersonSelector.setValue(null);
        pruhPersonSelector.setItems(pruhPersonList);
    }

    private void loadCalymDataFromDb() {
        pruhCalymList = calymRepo.findAll(Sort.by(Sort.Direction.DESC, Calym.SORT_PROP_CALYM));
        pruhCalymSelector.setValue(null);
        pruhCalymSelector.setItems(pruhCalymList);
    }

    private void updatePruhGrids(final Person person, final Calym calym) {

        Long personId = null == person ? null : person.getId();
        YearMonth ym = null == calym ? null : calym.getCalYm();
        loadPruhZakDataFromDb(personId, ym);

        pruhZakGrid.getDataProvider().refreshAll();
        // TODO: refresh ZakGridFooter...

//        for (PruhZak pruhZak : pruhZakList) {
//            pruhZakGrid.setDetailsVisible(pruchZak, StringUtils.isNotBlank(pruhZak.getZakText()));
//        }

    }

    private void loadPruhZakDataFromDb(Long personId, YearMonth ym) {
        if (null == personId || null == ym) {
            pruhZakList = new ArrayList();
        } else {


//            List<Dochsum> dochsums = dochsumService.fetchDochsumForPersonAndYm(dochPerson.getId(), pruhCalym);
////            List<DochsumZak> dochsumZaks = dochsumZakService.fetchDochsumForPersonAndYm(dochPerson.getId(), pruhDate)


            List<DochsumZak> dochsumZaks = dochsumZakService.fetchDochsumZaksForPersonAndYm(personId, ym);
            if (null == dochsumZaks) {
                pruhZakList = new ArrayList<>();
            } else {
                pruhZakList = transposeDochsumZaksToPruhZaks(dochsumZaks);
//                pruhZakList.add(new PruhZak("9545.1-1 / 1", "Zakazkovy text"));
            }
        }
        pruhZakGrid.setItems(pruhZakList);


//        pruhZakGrid.getColumnByKey(D01_KEY)
//                .setFooter(getD01Footer(getD01Sum()));
//        pruhZakGrid.getColumnByKey(D02_KEY)
//                .setFooter(getD02Footer(getD02Sum()));

    }



    private void buildForm() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setWidth("1200px");
        this.setAlignSelf(Alignment.CENTER);
        this.getStyle().set("margin-top", "2em");
        this.getStyle().set("margin-bottom", "2em");
        this.getStyle().set("background-color", "#fffcf5");
        this.getStyle().set("background-color", "#e1dcd6");
        this.getStyle().set("background-color", "#fcfffe");
        this.getStyle().set("background-color", "LightYellow");
        this.getStyle().set("background-color", "#fefefd");


//        nepritControl.setWidth("30em");
//        nepritControl.getStyle().set("margin-top", "4.2em");


        VerticalLayout pruhPanel = new VerticalLayout();
        pruhPanel.add(initPruhTitleBar());
        pruhPanel.add(
                initGridZakTitle()
                , initPruhZakGrid()
                , initGridZakButtonBar()
                , initGridZakSumTitle()
                , initGridParagTitle()
        );
//        pruhRecPane.add(initMiddlePruhFooterBar());

//        pruhRecPane.add(pruhRecLowerHeader);
//        pruhRecPane.add(initLowerPruhGrid());
//        pruhRecPane.add(dochRecLowerFooter);

//        pruhPanel.add(
//                new Ribbon()
//                , pruhPanel
//                , new Ribbon()
//                , buildVertSpace()
//                , buildVertSpace()
//        );

        this.add(pruhPanel);
    }

    private Component initGridZakSumTitle() {
        gridZakSumTitle = new Span("Odpracováno z docházky: ");
        gridZakSumTitle.getStyle()
                .set("margin-top", "2em");
        return gridZakSumTitle;
    }

    private Component initGridZakTitle() {
        gridZakTitle = new Span("Odpracováno na zakázkách: ");
        gridZakTitle.getStyle()
                .set("margin-top", "2em");
        return gridZakTitle;
    }

    private Component initGridZakButtonBar() {
        gridZakButtonBar = new HorizontalLayout();
        gridZakButtonBar.getStyle()
                .set("margin-top", "2em");
        gridZakButtonBar.add(initCancelEditButton());
        return gridZakButtonBar;
    }

    private Component initGridParagTitle() {
        gridZakTitle = new Span("Nepřítomnost: ");
        gridZakTitle.getStyle()
                .set("margin-top", "2em");
        return gridZakTitle;
    }

    private Component initPersonSelector() {
        pruhPersonSelector = new ComboBox<>();
        pruhPersonSelector.setLabel(null);
        pruhPersonSelector.setWidth("20em");
        pruhPersonSelector.setPlaceholder("Pracovník");
        pruhPersonSelector.setItems(new ArrayList<>());
        pruhPersonSelector.setItemLabelGenerator(this::getPersonLabel);
        pruhPersonSelector.addValueChangeListener(event -> {
            pruhPerson = event.getValue();
            updatePruhGrids(pruhPerson, pruhCalym);
        });
        pruhPersonSelector.addBlurListener(event -> {
//            loadPruhZakDataFromDb(pruhPerson.getId(), pruhCalym);
            pruhZakGrid.getDataProvider().refreshAll();
        });
        return pruhPersonSelector;
    }

    private String getPersonLabel(Person person) {
        return person.getUsername() + " (" + person.getJmeno() + " " + person.getPrijmeni() + ")";
    }

    private Component initPruhYmSelector() {
//        pruhCalymSelector = new Select<>();
        pruhCalymSelector = new ComboBox<>();

        pruhCalymSelector.getStyle().set("margin-right", "1em");
        pruhCalymSelector.setWidth("10em");
        pruhCalymSelector.setPlaceholder("Rok-měsíc");
        pruhCalymSelector.setItems(new ArrayList<>());

//        pruhCalymSelector.setItems(YearMonth.now());
        // The empty selection item is the first item that maps to an null item.
        // As the item is not selectable, using it also as placeholder

//        pruhCalymSelector.setEmptySelectionCaption("Rok-měsíc proužku...");
//        pruhCalymSelector.setEmptySelectionAllowed(true);
//        pruhCalymSelector.setItemEnabledProvider(Objects::nonNull);
//        // add a divider after the empty selection item
//        pruhCalymSelector.addComponents(null, new Hr());

        pruhCalymSelector.setItemLabelGenerator(this::getYmLabel);
        pruhCalymSelector.addValueChangeListener(event -> {
            pruhCalym = event.getValue();
            updatePruhGrids(pruhPerson, pruhCalym);
        });
        return pruhCalymSelector;
    }

    private String getYmLabel(Calym calym) {
        return null == calym || null == calym.getCalYm() ? "" : calym.getCalYm().toString();
    }

//    private Component initPruhRokMesSelector() {
//        pruhYmSelectList = new DatePicker();
//        pruhYmSelectList.setLabel(null);
//        pruhYmSelectList.setWidth("10em");
////        pruhYmSelectList.getStyle().set("margin-right", "1em");
//        pruhYmSelectList.addValueChangeListener(event -> {
//            pruhCalym = event.getValue();
//            updatePruhGrids(pruhPerson, pruhCalym);
//        });
//        return pruhYmSelectList;
//    }


//    private Component initUpperDochDateInfo() {
//        gridZakTitle = new Paragraph();
//        gridZakTitle.getStyle()
//                .set("font-weight", "bold")
//                .set("margin-right", "0.8em")
//        ;
//        gridZakTitle.setText("Den docházky...");
//        return gridZakTitle;
//    }

    private Component buildVertSpace() {
        Div vertSpace = new Div();
        vertSpace.setHeight("1em");
        return vertSpace;
    }

    private ValueProvider<Doch, String> durationValProv =
            doch -> null == doch.getDochDur() ? null : formatDuration(doch.getDochDur());

    private Component initPruhTitleBar() {
        pruhTitleBar = new HorizontalLayout();
        pruhTitleBar.setWidth("100%");
        pruhTitleBar.setSpacing(false);
        pruhTitleBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H3 pruhTitle = new H3("PROUŽEK");
        pruhTitle.getStyle()
                .set("margin-top", "10px")
//                .set("margin-left", "20px")
        ;

        HorizontalLayout selectorBox = new HorizontalLayout();
        selectorBox.add(
                initPersonSelector()
                , initPruhYmSelector()
        );

        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initZakAddButton()
        );

        pruhTitleBar.add(
                pruhTitle
                , selectorBox
                , buttonBox
        );
        return pruhTitleBar;
    };

//    private Component initMiddlePruhFooterBar() {
//        middleProhFooterBar = new HorizontalLayout();
//        middleProhFooterBar.setWidth("100%");
//        middleProhFooterBar.getStyle().set("margin-top", "0.5em");
//        middleProhFooterBar.setSpacing(false);
//        middleProhFooterBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
//        HorizontalLayout buttonBox = new HorizontalLayout();
//        buttonBox.add(
//                initPrenosPersonDateButton()
//                , initCancelPrenosPersonDateButton()
//                , initPrenosToAnalyzaButton()
//                , initPrenosPersonAllButton()
//        );
//
//        middleProhFooterBar.add(
//                buttonBox
//        );
//        return middleProhFooterBar;
//    }

    private void setDochNaviButtonsEnabled(boolean enabled) {
        loadTodayButton.setEnabled(enabled);
        loadPrevDateButton.setEnabled(enabled);
        loadNextDateButton.setEnabled(enabled);
        loadLastDateButton.setEnabled(enabled);
    }


    private void removeZakFromPruh(long zakId) {
        // TODO: remove zak, update DB, load from DB, update view
    }

    private Component initPruhZakGrid() {
        pruhZakGrid = new Grid<>();
        pruhZakGrid.setHeight("20em");
        pruhZakGrid.setColumnReorderingAllowed(false);
        pruhZakGrid.setClassName("vizman-simple-grid");
        pruhZakGrid.setSelectionMode(Grid.SelectionMode.NONE);

//        pruhZakGrid.setItemDetailsRenderer(new ComponentRenderer<>(dochsum -> {
//            Emphasis zakTextComp = new Emphasis(StringUtils.isBlank(dochsum.getZakText()) ? new Span("") : new Span(dochsum.getZakText()));
//            zakTextComp.getStyle().set("margin-left", "16em");
//            return zakTextComp;
//        }));

//        Binder<PruhZak> pruhZakBinder = new Binder<>(PruhZak.class);

        pruhZakGrid.addColumn(PruhZak::getCkontCzak)
                .setHeader("Kontrakt [Zakázka]")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;

        pruhZakGrid.addColumn(new ComponentRenderer<>(this::buildPruhZakRemoveBtn))
                .setWidth("2em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(false)
        ;

//        TextField edt01 = new TextField();
        Grid.Column<PruhZak> d01Col = pruhZakGrid.addColumn(PruhZak::getD01)
//                .setEditorComponent(nameEditor, pruhZak -> pruhZak.getD01())
//                .setEditorComponent(edt01)
                .setHeader("1")
                .setWidth("5em")
                .setFlexGrow(0)
                .setResizable(false)
        ;

//        TextField edt02 = new TextField();
        Grid.Column<PruhZak> d02Col = pruhZakGrid.addColumn(PruhZak::getD02)
//                .setEditorComponent(nameEditor, pruhZak -> pruhZak.getD01())
//                .setEditorComponent(edt02)
                .setHeader("2")
                .setWidth("5em")
                .setFlexGrow(0)
                .setResizable(false)
        ;
        d02Col.getElement().setProperty("margin", "0");
        d02Col.getElement().setAttribute("margin", "0");

        Grid.Column<PruhZak> tmpCol = pruhZakGrid.addColumn(PruhZak::getTmp)
//                .setEditorComponent(nameEditor, pruhZak -> pruhZak.getD01())
//                .setEditorComponent(edt02)
                .setHeader("tmp")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(false)
        ;


        Binder<PruhZak> binder = new Binder<>(PruhZak.class);
        pruhZakGrid.getEditor().setBinder(binder);

        // Close the editor in case of backward between components
//        fieldD01.getElement()
//                .addEventListener("keydown",
//                        event -> pruhZakGrid.getEditor().closeEditor())
//                .setFilter("event.key === 'Tab' && event.shiftKey");

        TextField editD01 = new TextField();
        binder.forField(editD01)
            .withNullRepresentation("")
            .withConverter(VzmFormatUtils.decHodToStringConverter)
            .bind(PruhZak::getD01, PruhZak::setD01);
        d01Col.setEditorComponent(editD01);

        TextField editD02 = new TextField();
        editD02.getStyle().set("margin","0");
        binder.forField(editD02)
//            .withConverter(VzmFormatUtils.   ddd bigDecimalMoneyConverter)
            .withNullRepresentation("")
//            .withConverter(VzmFormatUtils.decHodToStringConverter)
            .withConverter(
                    new StringToBigDecimalConverter("Špatný formát čísla"))
            .bind(PruhZak::getD02, PruhZak::setD02);
        d02Col.setEditorComponent(editD02);

        TextField editTmp = new TextField();
        binder.bind(editTmp, "tmp");
        tmpCol.setEditorComponent(editTmp);

        pruhZakGrid.addItemDoubleClickListener(event -> {
            pruhZakGrid.getEditor().editItem(event.getItem());
//            field.focus();
        });

//        pruhZakGrid.addItemClickListener(event -> {
//            if (binder.getBean() != null) {
//                message.setText(binder.getBean().getfirstName() + ", "
//                        + binder.getBean().isSubscriber());
//            }
//        });

//        for (int i = 1; i <= 31; i++) {
//            int finalI = i;
//            pruhZakGrid.addColumn(pruhZak -> pruhZak.getZakHod(finalI))
//                    .setHeader("2")
//                    .setWidth("2em")
//                    .setFlexGrow(0)
//                    .setResizable(false)
//            ;
//        }

        return pruhZakGrid;
    }


    private Component buildPruhZakRemoveBtn(PruhZak pruhZak) {
        Icon icon = VaadinIcon.FILE_REMOVE.create();
        icon.setSize("0.8em");
        icon.getStyle().set("theme", "small icon secondary");
//        icon.setColor("crimson");

        Button removePruhZakBtn = new Button(icon);
//            Button pruhZakRemoveBtn = new GridFakturovatBtn(event -> {
        removePruhZakBtn.addClickListener(event ->
                ConfirmDialog.createInfo()
                        .withCaption("Zákázka proužku")
                        .withMessage("Odstranit zakázku z proužku?")
                        .withOkButton(() -> {
                                    removeZakFromPruh(pruhZak.getZakId());
                                    updatePruhGrids(pruhPerson, pruhCalym);
                                }, ButtonOption.focus(), ButtonOption.caption("ODSTRANIT")
                        )
                        .withCancelButton(ButtonOption.caption("ZPĚT"))
                        .open()

        );
        return removePruhZakBtn;
    }


    private BigDecimal getD01Sum() {
        BigDecimal d01Sum = BigDecimal.ZERO;
        for (PruhZak pruhZak : pruhZakList) {
            if (null != pruhZak && null != pruhZak.getD01()) {
                d01Sum = d01Sum.add(pruhZak.getD01());
            }
        }
        return d01Sum;
    }

    private BigDecimal getD02Sum() {
        BigDecimal d02Sum = BigDecimal.ZERO;
        for (PruhZak pruhZak : pruhZakList) {
            if (null != pruhZak && null != pruhZak.getD02()) {
                d02Sum = d02Sum.add(pruhZak.getD02());
            }
        }
        return d02Sum;
    }


    public static HtmlComponent getPracComponent(Doch doch) {
//        return new Paragraph(null == doch.getDochDuration() ? "" : doch.getDochDuration().format( VzmFormatUtils.shortTimeFormatter));
//        return new Paragraph(null == doch.getDochDurationUI() ? "" : formatDuration(doch.getDochDurationUI()));
        return new Paragraph(null == doch.getDochDur() ? "" : formatDuration(doch.getDochDur()));
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
//                "%d:%02d:%02d",
                "%d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60);
//                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }


    private Component getD01Footer(BigDecimal d01) {
        Span comp = new Span(null == d01 ? "" : getD01Sum().toString());
        return comp;
    }

    private Component getD02Footer(BigDecimal d02) {
        Span comp = new Span(null == d02 ? "" : getD02Sum().toString());
        return comp;
    }


    private Component getDurationLabelFooter() {
        Paragraph comp = new Paragraph("Odpracováno: ");
//        comp.setWidth("5em");
        comp.getStyle().set("text-align", "end");
        return comp;
    }

    private void transposeDochsumToPruhSum(Long personId, YearMonth ym) {
        List<Dochsum> pruhSums = dochsumService.fetchDochsumForPersonAndYm(personId, ym);
    }

    private List<PruhZak> transposeDochsumZaksToPruhZaks(List<DochsumZak> dsZaks) {

        List<Long> zakIds = dsZaks.stream()
                .map(dszak -> dszak.getZakId())
                .distinct()
                .collect(Collectors.toList())
        ;

        List<Zak> zaks = zakService.fetchByIds(zakIds);
        List<PruhZak> pruhZaks = new ArrayList();
        for (Zak zak : zaks) {
            PruhZak pzak = new PruhZak(zak.getCkont(), zak.getText());
            for (DochsumZak dsZak : dsZaks) {
                if (dsZak.getZakId().equals(zak.getId())) {
                    int dayOm = dsZak.getDsDate().getDayOfMonth();
                    pzak.setValueToDayField(dayOm, dsZak.getDsWork());
                }
            }
            pzak.setTmp("TMP");
            pruhZaks.add(pzak);
        }
        return pruhZaks;
    }


    private Component initCancelEditButton() {
//        Icon icon = VaadinIcon.REPLY_ALL.create();
//        icon.setColor("crimson");
//        cancelEditButton = new Button(icon);
        cancelEditButton = new Button("Vrátit změny");
        cancelEditButton.addClickListener(event -> {
            ConfirmDialog.createQuestion()
                    .withCaption("Editace proužku")
                    .withMessage("Vrátit všechny změny proužku od posledního uložení?")
                    .withOkButton(() -> {
//                        loadPruhZakDataFromDb(pruhPerson, pruhCalym);
                        updatePruhGrids(pruhPerson, pruhCalym);
                    }, ButtonOption.focus(), ButtonOption.caption("VRÁTIT ZMĚNY"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        return cancelEditButton;
    }

    private Component initZakAddButton() {
        zakAddButton = new Button("Přidat zakázku");
        zakAddButton.addClickListener(event -> {
            zakSelectDialog.createQuestion()
                    .withCaption("Zakázky")
                    .withMessage("Přidat zakázku do proužku?")
                    .withOkButton(() -> {
//                        loadPruhZakDataFromDb(pruhPerson, pruhCalym);
                        updatePruhGrids(pruhPerson, pruhCalym);
                    }, ButtonOption.focus(), ButtonOption.caption("VRÁTIT ZMĚNY"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        return zakAddButton;
    }

//    private boolean canStampOdchod() {
//        return checkDayDochIsOpened("nelze editovat.")
//                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
////                && checkPersonIsInOffice("nelze zaznamenat odchod.")
//        ;
//    }
//
//    private boolean canStampOdchodAlt() {
//        return checkDayDochIsOpened("nelze editovat.")
////                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
////                && checkPersonIsInOffice("nelze zaznamenat odchod.")
//        ;
//    }
//
//
//    private boolean canRemoveAllDochRecs() {
//        return checkDayDochIsOpened("nelze editovat.")
//                && checkDochPersonIsSelected("nelze rušit záznamy")
//                && checkDochDateIsSelected("nelze rušit záznamy")
//                && checkDochHasRecords("není co rušit")
//        ;
//    }
//
//    private boolean checkDayDochIsOpened(final String additionalMsg) {
//        if (pruhZakList.stream().noneMatch(Doch::isClosed)) {
//            return true;
//        }
//        ConfirmDialog
//                .createInfo()
//                .withCaption("Záznam docházky")
//                .withMessage(String.format("Denní docházka je uzavřena%s", adjustAdditionalMsg(additionalMsg)))
//                .withOkButton()
//                .open();
//        return false;
//    }
//
//    private boolean checkDochDateIsSelected(final String additionalMsg) {
//        if (null != pruhCalym) {
//            return true;
//        }
//        ConfirmDialog
//                .createInfo()
//                .withCaption("Datum docházky")
//                .withMessage(String.format("Není vybráno datum docházky%s", adjustAdditionalMsg(additionalMsg)))
//                .withOkButton()
//                .open();
//        return false;
//    }
//
//    private boolean checkDochPersonIsSelected(final String additionalMsg) {
//        if (null != pruhPerson) {
//            return true;
//        }
//        ConfirmDialog
//                .createInfo()
//                .withCaption("Osoba docházky")
//                .withMessage(String.format("Není vybrána osoba docházky%s", adjustAdditionalMsg(additionalMsg)))
//                .withOkButton()
//                .open();
//        return false;
//    }
//
//    private boolean checkDochHasRecords(final String additionalMsg) {
//        if (dochHasRecords()) {
//            return true;
//        }
//        ConfirmDialog
//                .createInfo()
//                .withCaption("Záznamy docházky")
//                .withMessage(String.format("V docházce není žádný záznam%s", adjustAdditionalMsg(additionalMsg)))
//                .withOkButton()
//                .open();
//        return false;
//    }
//
//    private boolean checkDochDateIsToday(final String additionalMsg) {
//        if (null != pruhCalym && pruhCalym.equals(LocalDate.now())) {
//            return true;
//        }
//        ConfirmDialog
//                .createInfo()
//                .withCaption("Datum docházky")
//                .withMessage(String.format("Docházka není dnešní%s", adjustAdditionalMsg(additionalMsg)))
//                .withOkButton()
//                .open();
//        return false;
//    }

//
//    private boolean checkZkDochRecToDelExists() {
//        Doch lastZkDochRec = getLastZkDochRec();
//        if (lastZkDochRec != null) {
//            return true;
//        }
//        ConfirmDialog.createInfo()
//                .withCaption("Záznam docházky")
//                .withMessage("Nenalezen žádný záznam ke zrušení")
//                .withOkButton()
//                .open()
//        ;
//        return false;
//    }

    private boolean checkPruhZaksIsRezie(final String additionalMsg) {
        if (pruhZakList.stream().anyMatch(PruhZak::isRezieZak)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam proužku")
                .withMessage(String.format("Jedná se o režijní zakázku%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }


    private String adjustAdditionalMsg(final String additionalMsg) {
        return StringUtils.isBlank(additionalMsg) ? "." : " - " + additionalMsg;
    }

    private boolean dochHasRecords() {
        return pruhZakList.size() > 0;
    }


}
