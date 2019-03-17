package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.bean.PruhParag;
import eu.japtor.vizman.backend.bean.PruhSum;
import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.CalymRepo;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.repository.ParagRepo;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.Gap;
import eu.japtor.vizman.ui.components.GridItemRemoveBtn;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    private int pruhYmDaysCount;
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
    private Button saveEditButton;
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
    Grid<PruhParag> pruhParagGrid;
    List<PruhParag> pruhParagList = new ArrayList<>();
    Grid<PruhSum> pruhSumGrid;
    List<PruhSum> pruhSumList = new ArrayList<>();
    Comparator<PruhZak> pruhZakCkonComparator
            = (pz1, pz2) -> pz2.getCkontCzak().equals("00001") ? -1 : pz1.getCkontCzak().compareTo(pz2.getCkontCzak());

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
    public DochsumParagService dochsumParagService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public ParagRepo paragRepo;



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
        loadPruhSumDataFromDb(personId, ym);
        loadPruhParagDataFromDb(personId, ym);

        pruhZakGrid.getDataProvider().refreshAll();
        pruhSumGrid.getDataProvider().refreshAll();
        pruhParagGrid.getDataProvider().refreshAll();
        // TODO: refresh ZakGridFooter...

//        for (PruhZak pruhZak : pruhZakList) {
//            pruhZakGrid.setDetailsVisible(pruchZak, StringUtils.isNotBlank(pruhZak.getParagText()));
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
                pruhZakList.sort(pruhZakCkonComparator.reversed());
            }
        }
        pruhZakGrid.setItems(pruhZakList);


//        pruhZakGrid.getColumnByKey(D01_KEY)
//                .setFooter(getD01Footer(getD01Sum()));
//        pruhZakGrid.getColumnByKey(D02_KEY)
//                .setFooter(getD02Footer(getD02Sum()));

    }

    private void loadPruhParagDataFromDb(Long personId, YearMonth ym) {
        if (null == personId || null == ym) {
            pruhParagList = new ArrayList();
        } else {
            List<DochsumParag> dochsumParags = dochsumParagService.fetchDochsumParagsForPersonAndYm(personId, ym);
            if (null == dochsumParags) {
                pruhParagList = new ArrayList<>();
            } else {
                pruhParagList = transposeDochsumParagsToPruhParags(dochsumParags);
//                pruhParagList.sort(pruhParagCkonComparator.reversed());
            }
        }
        pruhParagGrid.setItems(pruhParagList);


//        pruhZakGrid.getColumnByKey(D01_KEY)
//                .setFooter(getD01Footer(getD01Sum()));
//        pruhZakGrid.getColumnByKey(D02_KEY)
//                .setFooter(getD02Footer(getD02Sum()));

    }

    private void loadPruhSumDataFromDb(Long personId, YearMonth ym) {
        if (null == personId || null == ym) {
            pruhSumList = new ArrayList();
        } else {
            List<Dochsum> dochsums = dochsumService.fetchDochsumForPersonAndYm(personId, ym);
            if (null == dochsums) {
                pruhSumList = new ArrayList<>();
            } else {
                pruhSumList = transposeDochsumsToPruhSums(dochsums);
            }
        }
        pruhSumGrid.setItems(pruhSumList);


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
                initZakGridTitle()
                , initZakGrid()
                , initZakGridButtonBar()
//                , initZakGridSumTitle()
                , initSumGrid()
                , initParagGridTitle()
                , initParagGrid()
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

    private Component initZakGridSumTitle() {
        gridZakSumTitle = new Span("Odpracováno z docházky: ");
        gridZakSumTitle.getStyle()
                .set("margin-top", "2em");
        return gridZakSumTitle;
    }

    private Component initZakGridTitle() {
        gridZakTitle = new Span("Odpracováno na zakázkách: ");
        gridZakTitle.getStyle()
                .set("margin-top", "2em");
        return gridZakTitle;
    }

    private Component initZakGridButtonBar() {
        gridZakButtonBar = new HorizontalLayout();
        gridZakButtonBar.setJustifyContentMode(JustifyContentMode.CENTER);
        gridZakButtonBar.setWidthFull();
        gridZakButtonBar.getStyle()
                .set("margin-top", "0.5em");
        gridZakButtonBar.add(
                initSaveEditButton()
                , new Gap("1em")
                , initCancelEditButton());
        return gridZakButtonBar;
    }

    private Component initParagGridTitle() {
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
            pruhYmDaysCount = pruhCalym.getCalYm().lengthOfMonth();
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
//            pruhYmDaysCount = pruhCalym.getCalYm().lengthOfMonth();
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


    private void removeZakFromPruh(Long zakId) {
        // TODO: remove zak, update DB, load from DB, update view
    }

    private Component initZakGrid() {
        pruhZakGrid = new Grid<>();
        pruhZakGrid.setHeight("20em");
        pruhZakGrid.setColumnReorderingAllowed(false);
        pruhZakGrid.setClassName("vizman-simple-grid");
        pruhZakGrid.setSelectionMode(Grid.SelectionMode.NONE);

        Binder<PruhZak> pzBinder = new Binder<>(PruhZak.class);
        pruhZakGrid.getEditor().setBinder(pzBinder);

//        pruhZakGrid.setItemDetailsRenderer(new ComponentRenderer<>(dochsum -> {
//            Emphasis zakTextComp = new Emphasis(StringUtils.isBlank(dochsum.getParagText()) ? new Span("") : new Span(dochsum.getParagText()));
//            zakTextComp.getStyle().set("margin-left", "16em");
//            return zakTextComp;
//        }));

//        Binder<PruhZak> pruhZakBinder = new Binder<>(PruhZak.class);

        pruhZakGrid.addColumn(PruhZak::getCkontCzak)
                .setHeader("Kontrakt [Zakázka]")
                .setWidth("10em")
                .setFlexGrow(1)
                .setFrozen(true)
                .setResizable(true)
        ;

        pruhZakGrid.addColumn(new ComponentRenderer<>(this::buildPruhZakRemoveBtn))
//        pruhZakGrid.addColumn(new ComponentRenderer<>(this::buildFaktEditBtn))
                .setWidth("5em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setFrozen(true)
                .setResizable(false)
        ;

//        TextField edt01 = new TextField();
//        addZakDayColumn(1, binder, PruhZak::getD01, PruhZak::setD01);
        addZakDayColumn(1, pzBinder, pz -> pz.getHod(1), (pz, hod) -> pz.setHod(1, hod));
//        addZakDayColumn(2, binder, pz -> pz.getD02(), (pruhZak, d02) -> pruhZak.setD02(d02));
        addZakDayColumn(2, pzBinder, pz -> pz.getHod(2), (pz, d02) -> pz.setD02(d02));
        addZakDayColumn(3, pzBinder, PruhZak::getD03, PruhZak::setD03);
        addZakDayColumn(4, pzBinder, PruhZak::getD04, PruhZak::setD04);
        addZakDayColumn(5, pzBinder, PruhZak::getD05, PruhZak::setD05);
        addZakDayColumn(6, pzBinder, PruhZak::getD06, PruhZak::setD06);
        addZakDayColumn(7, pzBinder, PruhZak::getD07, PruhZak::setD07);
        addZakDayColumn(8, pzBinder, PruhZak::getD08, PruhZak::setD08);
        addZakDayColumn(9, pzBinder, PruhZak::getD09, PruhZak::setD09);
        addZakDayColumn(10, pzBinder, PruhZak::getD10, PruhZak::setD10);
        addZakDayColumn(11, pzBinder, PruhZak::getD11, PruhZak::setD11);
        addZakDayColumn(12, pzBinder, PruhZak::getD12, PruhZak::setD12);
        addZakDayColumn(13, pzBinder, PruhZak::getD13, PruhZak::setD13);
        addZakDayColumn(14, pzBinder, PruhZak::getD14, PruhZak::setD14);
        addZakDayColumn(15, pzBinder, PruhZak::getD15, PruhZak::setD15);
        addZakDayColumn(16, pzBinder, PruhZak::getD16, PruhZak::setD16);
        addZakDayColumn(17, pzBinder, PruhZak::getD17, PruhZak::setD17);
        addZakDayColumn(18, pzBinder, PruhZak::getD18, PruhZak::setD18);
        addZakDayColumn(19, pzBinder, PruhZak::getD19, PruhZak::setD19);
        addZakDayColumn(20, pzBinder, PruhZak::getD20, PruhZak::setD20);
        addZakDayColumn(21, pzBinder, PruhZak::getD21, PruhZak::setD21);
        addZakDayColumn(22, pzBinder, PruhZak::getD22, PruhZak::setD22);
        addZakDayColumn(23, pzBinder, PruhZak::getD23, PruhZak::setD23);
        addZakDayColumn(24, pzBinder, PruhZak::getD24, PruhZak::setD24);
        addZakDayColumn(25, pzBinder, PruhZak::getD25, PruhZak::setD25);
        addZakDayColumn(26, pzBinder, PruhZak::getD26, PruhZak::setD26);
        addZakDayColumn(27, pzBinder, PruhZak::getD27, PruhZak::setD27);
        addZakDayColumn(28, pzBinder, PruhZak::getD28, PruhZak::setD28);
        if (pruhYmDaysCount > 28) {
            addZakDayColumn(29, pzBinder, PruhZak::getD29, PruhZak::setD29);
        }
        if (pruhYmDaysCount > 29) {
            addZakDayColumn(30, pzBinder, PruhZak::getD30, PruhZak::setD30);
        }
        if (pruhYmDaysCount > 30) {
            addZakDayColumn(31, pzBinder, PruhZak::getD31, PruhZak::setD31);
        }

//        Grid.Column<PruhZak> d01Col = pruhZakGrid.addColumn(PruhZak::getD01)
////                .setEditorComponent(nameEditor, pruhZak -> pruhZak.getD01())
////                .setEditorComponent(edt01)
//                .setHeader("1")
//                .setWidth("5em")
//                .setFlexGrow(0)
//                .setResizable(false)
//        ;
//        TextField editD01 = new TextField();
//        binder.forField(editD01)
//                .withNullRepresentation("")
//                .withConverter(VzmFormatUtils.decHodToStringConverter)
//                .bind(PruhZak::getD01, PruhZak::setD01);
//        d01Col.setEditorComponent(editD01);


//        TextField edt02 = new TextField();
//        Grid.Column<PruhZak> d02Col = pruhZakGrid.addColumn(PruhZak::getD02)
////                .setEditorComponent(nameEditor, pruhZak -> pruhZak.getD01())
////                .setEditorComponent(edt02)
//                .setHeader("2")
//                .setWidth("5em")
//                .setFlexGrow(0)
//                .setResizable(false)
//        ;



        // Close the editor in case of backward between components
//        fieldD01.getElement()
//                .addEventListener("keydown",
//                        event -> pruhZakGrid.getEditor().closeEditor())
//                .setFilter("event.key === 'Tab' && event.shiftKey");


//        TextField editD02 = new TextField();
//        editD02.getStyle().set("margin","0");
//        binder.forField(editD02)
////            .withConverter(VzmFormatUtils.   ddd bigDecimalMoneyConverter)
//            .withNullRepresentation("")
////            .withConverter(VzmFormatUtils.decHodToStringConverter)
//            .withConverter(
//                    new StringToBigDecimalConverter("Špatný formát čísla"))
//            .bind(PruhZak::getD02, PruhZak::setD02);
//        d02Col.setEditorComponent(editD02);


//        Grid.Column<PruhZak> tmpCol = pruhZakGrid.addColumn(PruhZak::getTmp)
//                .setHeader("tmp")
//                .setWidth("10em")
//                .setFlexGrow(0)
//                .setResizable(false)
//        ;
//        TextField editTmp = new TextField();
//        binder.bind(editTmp, "tmp");
//        tmpCol.setEditorComponent(editTmp);

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
//            pruhZakGrid.addColumn(pruhZak -> pruhZak.getHod(finalI))
//                    .setHeader("2")
//                    .setWidth("2em")
//                    .setFlexGrow(0)
//                    .setResizable(false)
//            ;
//        }

        return pruhZakGrid;
    }



    private Component initSumGrid() {
        pruhSumGrid = new Grid<>();
        pruhSumGrid.setHeight("20em");
        pruhSumGrid.setColumnReorderingAllowed(false);
        pruhSumGrid.setClassName("vizman-simple-grid");
        pruhSumGrid.setSelectionMode(Grid.SelectionMode.NONE);

        Binder<PruhSum> sumBinder = new Binder<>(PruhSum.class);
        pruhSumGrid.getEditor().setBinder(sumBinder);

        pruhSumGrid.addColumn(PruhSum::getSumText)
                .setHeader("Důvod")
                .setWidth("15em")
                .setFlexGrow(1)
                .setFrozen(true)
                .setResizable(true)
        ;

//        pruhSumGrid.addColumn(new ComponentRenderer<>(pruhSum ->
//                new Span(pruhSum.getSumText())))
//                .setWidth("5em")
//                .setTextAlign(ColumnTextAlign.END)
//                .setFlexGrow(0)
//                .setFrozen(true)
//                .setResizable(false)
//        ;

//        for (int i=1; i <= pruhYmDaysCount; i++) {
        for (int i=1; i <= 31; i++) {
            int ii = i;
            addSumDayColumn(ii, ps -> ps.getHod(ii));
        }
        return pruhSumGrid;
    }

    private Component initParagGrid() {
        pruhParagGrid = new Grid<>();
        pruhParagGrid.setHeight("20em");
        pruhParagGrid.setColumnReorderingAllowed(false);
        pruhParagGrid.setClassName("vizman-simple-grid");
        pruhParagGrid.setSelectionMode(Grid.SelectionMode.NONE);

        Binder<PruhParag> paragBinder = new Binder<>(PruhParag.class);
        pruhParagGrid.getEditor().setBinder(paragBinder);

        pruhParagGrid.addColumn(PruhParag::getCparag)
                .setHeader("Důvod")
                .setWidth("10em")
                .setFlexGrow(1)
                .setFrozen(true)
                .setResizable(true)
        ;

        pruhParagGrid.addColumn(new ComponentRenderer<>(pruhParag ->
                new Span(pruhParag.getParagText())))
                .setWidth("5em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setFrozen(true)
                .setResizable(false)
        ;

//        for (int i=1; i <= pruhYmDaysCount; i++) {
        for (int i=1; i <= 31; i++) {
            int ii = i;
            addParagDayColumn(ii, pp -> pp.getHod(ii));
        }
//        addZakDayColumn(1, pzBinder, pz -> pz.getHod(1), (pz, hod) -> pz.setHod(1, hod));

//        addZakDayColumn(1, binder, PruhParag::getD01, PruhParag::setD01);
//        addZakDayColumn(2, binder, PruhParag::getD02, PruhParag::setD02);
//        addZakDayColumn(3, binder, PruhParag::getD03, PruhParag::setD03);
//        addZakDayColumn(4, binder, PruhParag::getD04, PruhParag::setD04);
//        addZakDayColumn(5, binder, PruhParag::getD05, PruhParag::setD05);
//        addZakDayColumn(6, binder, PruhParag::getD06, PruhParag::setD06);
//        addZakDayColumn(7, binder, PruhParag::getD07, PruhParag::setD07);
//        addZakDayColumn(8, binder, PruhParag::getD08, PruhParag::setD08);
//        addZakDayColumn(9, binder, PruhParag::getD09, PruhParag::setD09);
//        addZakDayColumn(10, binder, PruhParag::getD10, PruhParag::setD10);
//        addZakDayColumn(11, binder, PruhParag::getD11, PruhParag::setD11);
//        addZakDayColumn(12, binder, PruhParag::getD12, PruhParag::setD12);
//        addZakDayColumn(13, binder, PruhParag::getD13, PruhParag::setD13);
//        addZakDayColumn(14, binder, PruhParag::getD14, PruhParag::setD14);
//        addZakDayColumn(15, binder, PruhParag::getD15, PruhParag::setD15);
//        addZakDayColumn(16, binder, PruhParag::getD16, PruhParag::setD16);
//        addZakDayColumn(17, binder, PruhParag::getD17, PruhParag::setD17);
//        addZakDayColumn(18, binder, PruhParag::getD18, PruhParag::setD18);
//        addZakDayColumn(19, binder, PruhParag::getD19, PruhParag::setD19);
//        addZakDayColumn(20, binder, PruhParag::getD20, PruhParag::setD20);
//        addZakDayColumn(21, binder, PruhParag::getD21, PruhParag::setD21);
//        addZakDayColumn(22, binder, PruhParag::getD22, PruhParag::setD22);
//        addZakDayColumn(23, binder, PruhParag::getD23, PruhParag::setD23);
//        addZakDayColumn(24, binder, PruhParag::getD24, PruhParag::setD24);
//        addZakDayColumn(25, binder, PruhParag::getD25, PruhParag::setD25);
//        addZakDayColumn(26, binder, PruhParag::getD26, PruhParag::setD26);
//        addZakDayColumn(27, binder, PruhParag::getD27, PruhParag::setD27);
//        addZakDayColumn(28, binder, PruhParag::getD28, PruhParag::setD28);
//        if (pruhYmDaysCount > 28) {
//            addZakDayColumn(29, binder, PruhParag::getD29, PruhParag::setD29);
//        }
//        if (pruhYmDaysCount > 29) {
//            addZakDayColumn(30, binder, PruhParag::getD30, PruhParag::setD30);
//        }
//        if (pruhYmDaysCount > 30) {
//            addZakDayColumn(31, binder, PruhParag::getD31, PruhParag::setD31);
//        }

//        pruhPragGrid.addItemDoubleClickListener(event -> {
//            pruhParagGrid.getEditor().editItem(event.getItem());
////            field.focus();
//        });

        return pruhParagGrid;
    }



//    public Binder.Binding<BEAN, TARGET> bind(ValueProvider<BEAN, TARGET> getter, Setter<BEAN, TARGET> setter) {
    private Grid.Column<PruhZak> addZakDayColumn(
            int day
            , Binder<PruhZak> pzBinder
            , ValueProvider<PruhZak, BigDecimal> pzHodValProv
            , Setter<PruhZak, BigDecimal> pzHodSetter)
    {
        Grid.Column<PruhZak> col = pruhZakGrid.addColumn( new ComponentRenderer<>(pruhZak ->
                VzmFormatUtils.getDecHodComponent(pzHodValProv.apply(pruhZak))));
//                StringToBigDecimalConverter(valProv));
        col.setHeader(Integer.valueOf(day).toString())
            .setWidth("5em")
            .setTextAlign(ColumnTextAlign.END)
            .setFlexGrow(0)
            .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");

        TextField edt = new TextField();
        // TODO: remove margins
        edt.getStyle().set("margin", "0");
        pzBinder.forField(edt)
//            .withConverter(VzmFormatUtils.   ddd bigDecimalMoneyConverter)
                .withNullRepresentation("")
//            .withConverter(VzmFormatUtils.decHodToStringConverter)
                // TODO: add regex for dd.d
                .withConverter(VzmFormatUtils.decHodToStringConverter)
//                .withConverter(
//                        new StringToBigDecimalConverter("Špatný formát čísla, je očekáváno 'CC.C'"))
                .bind(pzHodValProv, pzHodSetter);
        col.setEditorComponent(edt);
        col.setFooter(getDayHodSumComp(BigDecimal.valueOf(12.3)));
        return col;
    }

    private Grid.Column<PruhSum> addSumDayColumn(
            int day, ValueProvider<PruhSum, BigDecimal> ppHodValProv)
    {
        Grid.Column<PruhSum> col = pruhSumGrid.addColumn( new ComponentRenderer<>(pruhSum ->
                VzmFormatUtils.getDecHodComponent(ppHodValProv.apply(pruhSum))));
//                StringToBigDecimalConverter(valProv));
        col.setHeader(Integer.valueOf(day).toString())
                .setWidth("5em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");

        return col;
    }

    private Grid.Column<PruhParag> addParagDayColumn(
            int day, ValueProvider<PruhParag, BigDecimal> ppHodValProv)
    {
        Grid.Column<PruhParag> col = pruhParagGrid.addColumn( new ComponentRenderer<>(pruhParag ->
                VzmFormatUtils.getDecHodComponent(ppHodValProv.apply(pruhParag))));
//                StringToBigDecimalConverter(valProv));
        col.setHeader(Integer.valueOf(day).toString())
                .setWidth("5em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");

        return col;
    }

    private Component getDayHodSumComp(BigDecimal dayHodSum) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        df.setGroupingUsed(false);
        Span span = new Span(null == dayHodSum ? null : df.format(dayHodSum));
        return span;
    }

    private Component buildPruhZakRemoveBtn(PruhZak pruhZak) {
//        Icon icon = VaadinIcon.FILE_REMOVE.create();
//        icon.setSize("0.8em");
//        icon.getStyle().set("theme", "small icon secondary");
////        icon.setColor("crimson");

//        Button removePruhZakBtn = new Button(icon);
        Button removePruhZakBtn = new GridItemRemoveBtn(event ->
//            Button pruhZakRemoveBtn = new GridFakturovatBtn(event -> {
//        removePruhZakBtn.addClickListener(event ->
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
        removePruhZakBtn.setText(null);
        return removePruhZakBtn;
    }


    private BigDecimal getHodSum(Integer day) {
        BigDecimal hodSum = BigDecimal.ZERO;
        for (PruhZak pruhZak : pruhZakList) {
            if (null != pruhZak && null != pruhZak.getHod(day)) {
                hodSum = hodSum.add(pruhZak.getD01());
            }
        }
        return hodSum;
    }

//    private BigDecimal getD02Sum() {
//        BigDecimal d02Sum = BigDecimal.ZERO;
//        for (PruhZak pruhZak : pruhZakList) {
//            if (null != pruhZak && null != pruhZak.getD02()) {
//                d02Sum = d02Sum.add(pruhZak.getD02());
//            }
//        }
//        return d02Sum;
//    }


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


//    private Component getD01Footer(BigDecimal d01) {
//        Span comp = new Span(null == d01 ? "" : getD01Sum().toString());
//        return comp;
//    }
//
//    private Component getD02Footer(BigDecimal d02) {
//        Span comp = new Span(null == d02 ? "" : getD02Sum().toString());
//        return comp;
//    }


    private Component getDurationLabelFooter() {
        Paragraph comp = new Paragraph("Odpracováno: ");
//        comp.setWidth("5em");
        comp.getStyle().set("text-align", "end");
        return comp;
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
                    pzak.setHod(dayOm, dsZak.getDsWork());
                }
            }
            pzak.setTmp("TMP");
            pruhZaks.add(pzak);
        }
        return pruhZaks;
    }


    private List<DochsumZak> transposePruhZaksToDochsumZaks(List<PruhZak> pruhZaks) {

        List<Long> zakIds = new ArrayList<>();
        for (PruhZak pzak : pruhZaks) {
            zakIds.add(pzak.getZakId());
        }
        List<Zak> zaks = zakService.fetchByIds(zakIds);

        List<DochsumZak> dochsumZaks = new ArrayList();
        for (PruhZak pzak : pruhZaks) {
            Long zakId = pzak.getZakId();
            for (int i=1; i <= pruhYmDaysCount; i++) {
                BigDecimal cellHod = pzak.getHod(i);
                if (null != cellHod && cellHod.compareTo(BigDecimal.ZERO) != 0) {
                    LocalDate cellDate = pruhCalym.getCalYm().atDay(i);
                    DochsumZak dsZak = new DochsumZak(pruhPerson.getId(), cellDate, pzak.getZakId());
                    dsZak.setDsWork(cellHod);
                    // TODO mzda
                    // TODO pojistne
                    // TODO normo, skutecne...
                    dochsumZaks.add(dsZak);
                }
            }
        }
        return dochsumZaks;
    }


    private List<PruhSum> transposeDochsumsToPruhSums(List<Dochsum> dsSums) {

//        List<Long> sumIds = dsSums.stream()
//                .map(dsSum -> dsSum.getumId())
//                .distinct()
//                .collect(Collectors.toList())
//                ;

//        List<PruhSum> sums = paragRepo.findAllById(paragIds);
        List<PruhSum> pruhSums = new ArrayList();
//        for (Parag parag : parags) {
            PruhSum psum = new PruhSum("Odpracováno z docházky");
            for (Dochsum dsSum : dsSums) {
//                if (dsSum.getSumId().equals(parag.getId())) {
                    int dayOm = dsSum.getDsDate().getDayOfMonth();
                    psum.setHod(dayOm, dsSum.getDsWork());
//                }
            }
            pruhSums.add(psum);
//        }
        return pruhSums;
    }

    private List<PruhParag> transposeDochsumParagsToPruhParags(List<DochsumParag> dsParags) {

        List<Long> paragIds = dsParags.stream()
                .map(dsParag -> dsParag.getParagId())
                .distinct()
                .collect(Collectors.toList())
                ;

        List<Parag> parags = paragRepo.findAllById(paragIds);
        List<PruhParag> pruhParags = new ArrayList();
        for (Parag parag : parags) {
            PruhParag ppar = new PruhParag(parag.getCparag(), parag.getText());
            for (DochsumParag dsPar : dsParags) {
                if (dsPar.getParagId().equals(parag.getId())) {
                    int dayOm = dsPar.getDsDate().getDayOfMonth();
//                    ppar.setValueToDayField(dayOm, dsPar.getDsWorkOff());
                    ppar.setHod(dayOm, dsPar.getDsWorkOff());
                }
            }
            pruhParags.add(ppar);
        }
        return pruhParags;
    }


    private Component initCancelEditButton() {
//        Icon icon = VaadinIcon.REPLY_ALL.create();
//        icon.setColor("crimson");
//        cancelEditButton = new Button(icon);
        cancelEditButton = new Button("Vrátit změny");
        cancelEditButton.getElement().setAttribute("theme", "secondary error");
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



    private Component initSaveEditButton() {
        saveEditButton = new Button("Uložit");
        saveEditButton.getElement().setAttribute("theme", "primary");
        saveEditButton.addClickListener(event -> {
            ConfirmDialog.createQuestion()
                    .withCaption("Editace proužku")
                    .withMessage("Uložit proužek?")
                    .withOkButton(() -> {
//                        loadPruhZakDataFromDb(pruhPerson, pruhCalym);
                        List<DochsumZak> dsZaks = transposePruhZaksToDochsumZaks(pruhZakList);
                        // TODO: dochsumZakService.store(dsZaks, pruhYm, personiD)
                        updatePruhGrids(pruhPerson, pruhCalym);
                    }, ButtonOption.focus(), ButtonOption.caption("ULOŽIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        return saveEditButton;
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
