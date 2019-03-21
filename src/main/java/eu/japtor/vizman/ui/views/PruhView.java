package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.Query;
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
import java.util.stream.Stream;

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
    private static final String COL_WIDTH = "4em";

    private static final DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter upperDochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE");
    private static final DateTimeFormatter lowerDochDateHeaderFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy, EEEE");
    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
    public static final String DZ_KEY_PREF = "dz";
    public static final String DS_KEY_PREF = "ds";
    public static final String DP_KEY_PREF = "dp";

    private ConfirmDialog zakSelectDialog;

    private String authUsername = "vancik";

    private List<Person> pruhPersonList;
    private Person pruhPerson;
    private ComboBox<Person> pruhPersonSelector;

    private List<Calym> pruhCalymList;
    private Calym pruhCalym;
    private int pruhDayMax;
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


    public PruhView() {
//        super();
        buildForm();
    }

    @PostConstruct
    public void init() {
        initPruhData();
        zakSelectDialog = ConfirmDialog.createInfo()
                .withCaption("Zakázky");
    }



    @Override
    protected void onAttach(AttachEvent attachEvent) {

//        getLogger().info("## ON ATTACH DochView ##");

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
//        getLogger().info("## ON DETACH DochView ##");

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }


    public void initPruhData() {
        loadPersonDataFromDb();
        Person pruhPersonByAuth = getPersonFromList(authUsername)
                .orElse(null);
        pruhPersonSelector.setValue(pruhPersonByAuth);
        loadCalymDataFromDb();
        Calym pruhCalymByToday = getCalymFromListByYm(YearMonth.now())
                .orElse(null);
        pruhCalymSelector.setValue(pruhCalymByToday);
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
        recalcMissingHodsAllMonthDays();

        pruhZakGrid.getDataProvider().refreshAll();
        pruhSumGrid.getDataProvider().refreshAll();
        pruhParagGrid.getDataProvider().refreshAll();

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
        setDayColumnsVisibility(pruhZakGrid, pruhDayMax, DZ_KEY_PREF);
    }

    private void recalcMissingHodsAllMonthDays() {
        for (int day = 1; day <= 31; day++) {
            Grid.Column col = pruhZakGrid.getColumnByKey(DZ_KEY_PREF + String.valueOf(day));
            if (null != col) {
                col.setFooter(buildDayHodSumComp(getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day))));
            }
        }
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
        setDayColumnsVisibility(pruhParagGrid, pruhDayMax, "dp");
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
        setDayColumnsVisibility(pruhSumGrid, pruhDayMax, "ds");
    }


    private void setDayColumnsVisibility(final Grid grid, int daysMax, String keyPrefix) {
        if (null == grid) {
            return;
        }
        for (int day = 1; day <= 31; day++) {
            Grid.Column col = grid.getColumnByKey(keyPrefix + String.valueOf(day));
            if (null != col){
                col.setVisible(day <= daysMax);
            }
        }
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

        VerticalLayout pruhPanel = new VerticalLayout();
        pruhPanel.add(initPruhTitleBar());
        pruhPanel.add(
                initZakGridTitle()
                , initZakGrid()
                , initSumGrid()
                , initZakGridButtonBar()
//                , initZakGridSumTitle()
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
            pruhDayMax = pruhCalym.getCalYm().lengthOfMonth();
            updatePruhGrids(pruhPerson, pruhCalym);
        });
        return pruhCalymSelector;
    }

    private String getYmLabel(Calym calym) {
        return null == calym || null == calym.getCalYm() ? "" : calym.getCalYm().toString();
    }


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
        pruhZakGrid.setHeight("50em");
        pruhZakGrid.setColumnReorderingAllowed(false);
        pruhZakGrid.setClassName("vizman-simple-grid");
        pruhZakGrid.setSelectionMode(Grid.SelectionMode.NONE);
        pruhZakGrid.addThemeNames("column-borders", "row-stripes");
//        pruhZakGrid.addThemeNames("no-border", "no-row-borders", "row-stripes");
//        pruhZakGrid.addThemeNames("border", "row-borders", "row-stripes");

        Binder<PruhZak> pzBinder = new Binder<>(PruhZak.class);
        Editor<PruhZak> pzEditor = pruhZakGrid.getEditor();
        pzEditor.setBinder(pzBinder);

        pruhZakGrid.addColumn(PruhZak::getCkontCzak)
                .setHeader("Kontrakt [Zakázka]")
                .setFooter("Zbývá vyplnit")
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

        pruhZakGrid.addItemDoubleClickListener(event -> {
            pruhZakGrid.getEditor().editItem(event.getItem());
//            field.focus();
        });

        for (int i = 1; i <= 31; i++) {
            int ii = i;
            addZakDayColumn(
                    ii
                    , pzBinder
                    , pzEditor
                    , pz -> pz.getHod(ii)
                    , (pz, hod) -> pz.setHod(ii, hod)
            );

        }

        return pruhZakGrid;
    }



    private Component initSumGrid() {
        pruhSumGrid = new Grid<>();
        pruhSumGrid.setHeight("8em");
        pruhSumGrid.setColumnReorderingAllowed(false);
        pruhSumGrid.setClassName("vizman-simple-grid");
        pruhSumGrid.setSelectionMode(Grid.SelectionMode.NONE);

//        Binder<PruhSum> sumBinder = new Binder<>(PruhSum.class);
//        pruhSumGrid.getEditor().setBinder(sumBinder);

        pruhSumGrid.addColumn(PruhSum::getSumText)
//                .setHeader("Důvod")
//                .setHeader((Component)null)
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

//        for (int i=1; i <= pruhDayMax; i++) {
        for (int i = 1; i <= 31; i++) {
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

//        Binder<PruhParag> paragBinder = new Binder<>(PruhParag.class);
//        pruhParagGrid.getEditor().setBinder(paragBinder);

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

        for (int i = 1; i <= 31; i++) {
            int ii = i;
            addParagDayColumn(ii, pp -> pp.getHod(ii));
        }

        return pruhParagGrid;
    }


    private Grid.Column<PruhZak> addZakDayColumn(
            int day
            , Binder<PruhZak> pzBinder
            , Editor<PruhZak> pzEditor
            , ValueProvider<PruhZak, BigDecimal> pzHodValProv
            , Setter<PruhZak, BigDecimal> pzHodSetter)
    {
        Grid.Column<PruhZak> col = pruhZakGrid.addColumn( new ComponentRenderer<>(pruhZak -> {
            HtmlComponent comp = VzmFormatUtils.getDecHodComponent(pzHodValProv.apply(pruhZak));
            return comp;
        }));


        col.setHeader(Integer.valueOf(day).toString())
            .setWidth(COL_WIDTH)
            .setTextAlign(ColumnTextAlign.END)
            .setFlexGrow(0)
            .setKey("dz" + String.valueOf(day))
            .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");



        TextField editComp = new TextField();
        editComp.addValueChangeListener(event -> {
            if (event.isFromClient() && (StringUtils.isNotBlank(event.getValue()) || StringUtils.isNotBlank(event.getOldValue()))
                    && !event.getValue().equals(event.getOldValue())) {
                try {
                    // TODO: try to use localization instead of regex ?
                    pzEditor.getItem().setValueToDayField(day
                            , StringUtils.isBlank(event.getValue()) ?
                                    null : new BigDecimal(event.getValue().replaceAll(",", ".")));
                    pzBinder.writeBean(pzEditor.getItem());
                    col.setFooter(buildDayHodSumComp(getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day))));
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            }
        });
        // TODO: remove margins
        editComp.getStyle().set("margin", "0");
        pzBinder.forField(editComp)
//            .withConverter(VzmFormatUtils.   ddd bigDecimalMoneyConverter)
                .withNullRepresentation("")
//            .withConverter(VzmFormatUtils.decHodToStringConverter)
                // TODO: add regex for dd.d
                .withConverter(VzmFormatUtils.decHodToStringConverter)
//                .withConverter(
//                        new StringToBigDecimalConverter("Špatný formát čísla, je očekáváno 'CC.C'"))
                .bind(pzHodValProv, pzHodSetter);
        col.setEditorComponent(editComp);
        pzBinder.addStatusChangeListener(event -> {
                event.getBinder().hasChanges();
//                event.getBinder().forField(editComp).getField().getValue().;
        });

        return col;
    }

    private BigDecimal getDayZakMissing(final BigDecimal zakHodSum, final BigDecimal sumHodSum) {
        BigDecimal zakHodMissing = null;
        if (null == sumHodSum) {
            if (null != zakHodSum) {
                zakHodMissing = BigDecimal.ZERO.subtract(zakHodSum);
            }
        } else {
            if (null == zakHodSum) {
                zakHodMissing = sumHodSum;
            } else {
                zakHodMissing = sumHodSum.subtract(zakHodSum);
            }
        }
        return zakHodMissing;
    }

    private BigDecimal getDayZakHodSum(Integer day) {
//        Stream<BigDecimal> hodStream = pruhZakGrid.getDataProvider()
        // TODO: avoid intermediate collecting to List and use stream directly ?
        List<BigDecimal> hodList = pruhZakGrid.getDataProvider()
                .fetch(new Query<>())
                .map(pz -> pz.getHod(day))
                .filter(hod -> null != hod)
                .collect(Collectors.toList())
        ;
        if (hodList.size() == 0) {
            return null;
        } else {
            return hodList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    private BigDecimal getDaySumHodSum(Integer day) {
        return ((pruhSumList.size() == 0) || (null == pruhSumList.get(0).getHod(day)) ?
                null : pruhSumList.get(0).getHod(day));
    }

    private Grid.Column<PruhSum> addSumDayColumn(
            int day, ValueProvider<PruhSum, BigDecimal> ppHodValProv)
    {
        Grid.Column<PruhSum> col = pruhSumGrid.addColumn( new ComponentRenderer<>(pruhSum ->
                VzmFormatUtils.getDecHodComponent(ppHodValProv.apply(pruhSum))));
        col.setWidth(COL_WIDTH)
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(DS_KEY_PREF + String.valueOf(day))
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
        col.setHeader(String.valueOf(day))
                .setWidth(COL_WIDTH)
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(DZ_KEY_PREF + String.valueOf(day))
                .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");

        return col;
    }

    private Component buildDayHodSumComp(BigDecimal dayHodSum) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        df.setGroupingUsed(false);
        Span span = new Span(null == dayHodSum ? null : df.format(dayHodSum));
        return span;
    }

    private Component buildPruhZakRemoveBtn(PruhZak pruhZak) {
        Button removePruhZakBtn = new GridItemRemoveBtn(event ->
                ConfirmDialog.createInfo()
                        .withCaption("Zákázka proužku")
                        .withMessage("Odstranit zakázku z proužku včetně vyplněných hodin?")
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
                    pzak.setValueToDayField(dayOm, dsZak.getDszWorkPruh());
                    pzak.setHod(dayOm, dsZak.getDszWorkPruh());
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
            for (int i = 1; i <= pruhDayMax; i++) {
                BigDecimal cellHod = pzak.getHod(i);
                if (null != cellHod && cellHod.compareTo(BigDecimal.ZERO) != 0) {
                    LocalDate cellDate = pruhCalym.getCalYm().atDay(i);
                    DochsumZak dsZak = new DochsumZak(pruhPerson.getId(), cellDate, pzak.getZakId());
                    dsZak.setDszWorkPruh(cellHod);
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
        List<PruhSum> pruhSums = new ArrayList();
//        for (Parag parag : parags) {
            PruhSum psum = new PruhSum("Odpracováno z docházky");
            for (Dochsum dsSum : dsSums) {
                int dayOm = dsSum.getDsDate().getDayOfMonth();
                psum.setHod(dayOm, dsSum.getDsWorkPruh());
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
                    ppar.setHod(dayOm, dsPar.getDspWorkOff());
                }
            }
            pruhParags.add(ppar);
        }
        return pruhParags;
    }


    private Component initCancelEditButton() {
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
}
