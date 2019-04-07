package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.GeneratedVaadinDatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.repository.PruhRepo;
import eu.japtor.vizman.backend.service.DochService;
import eu.japtor.vizman.backend.service.DochsumService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.Ribbon;
import eu.japtor.vizman.ui.forms.DochFormDialog;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static eu.japtor.vizman.app.security.SecurityUtils.canViewOtherUsers;
import static eu.japtor.vizman.backend.entity.Pruh.PRUH_STATE_LOCKED;
import static eu.japtor.vizman.backend.entity.Pruh.PRUH_STATE_UNLOCKED;
import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_DOCH;

@Route(value = ROUTE_DOCH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.DOCH_USE
})
@SpringComponent
@UIScope
//@Push
// public class DochView extends VerticalLayout implements HasLogger, BeforeEnterListener {
public class DochView extends HorizontalLayout implements HasLogger, BeforeEnterListener {

    private static final String DOCH_UPPER_DURATION_KEY = "doch-upper-duration-key";
    private static final String DOCH_UPPER_CINNOST_KEY = "doch-upper-cinnost-key";
    private static final String DOCH_UPPER_POZNAMKA_KEY = "doch-low-poznamka-key";

    private static final String DOCH_LOWER_DURATION_KEY = "doch-low-duration-key";
    private static final String DOCH_LOWER_CINNOST_KEY = "doch-low-cinnost-key";
    private static final String DOCH_LOWER_POZNAMKA_KEY = "doch-low-poznamka-key";

    private static final String DISABLED_ICON_COLOR = "silver";

    private Icon pruhStateIconUnlocked;
    private Icon pruhStateIconLocked;
    private Icon pruhStateIconNone;
    private Integer pruhState;

    private Icon dochStateIconOpened;
    private Icon dochStateIconClosed;

    private String authUsername;

    private Icon loadTodayIconEnabled;
    private Icon loadTodayIconDisabled;
    private Icon removeLastDochRecIconEnabled;
    private Icon removeLastDochRecIconDisabled;
    private Icon removeAllDochRecsIconEnabled;
    private Icon removeAllDochRecsIconDisabled;

    private static final DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter upperDochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE");
    private static final DateTimeFormatter lowerDochDateHeaderFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy, EEEE");

    private DochFormDialog dochFormDialog;
    private List<Person> dochPersonList;
    private ComboBox<Person> dochPersonSelector;
    private DatePicker dochDateSelector;
    private static final Locale czLocale = new Locale("cs", "CZ");

    private Button dochMonthReportBtn = new Button();
    private Button dochYearReportBtn = new Button();

    private HorizontalLayout dochHeader;

    private FormLayout dochControl = new FormLayout();
    private FormLayout nepritControl = new FormLayout();

    private VerticalLayout clockContainer;
    private Span clockDisplay = new Span();

    //    private Span upperDochDateInfo = new Span();
    private Paragraph upperDochDateInfo;
    private Span lowerDochDateInfo;

    private Button loadTodayButton;
    private Button loadPrevDateButton;
    private Button loadNextDateButton;
    private Button loadLastDateButton;
    private Button removeLastDochRecButton;
    private Button removeAllDochRecButton;

    private Button prenosPersonDateButton;
    private Button cancelPrenosPersonDateButton;
    private Button prenosPersonDateMonthButton;
    private Button prenosPersonDateAllButton;

    private Button prichodBtn;
    private Button prichodAltBtn;
    private RadioButtonGroup<Cin> odchodRadio;
    private Button odchodButton;
    private Button odchodAltButton;

    Button dovolenaButton;
    Button dovolenaHalfButton;
    Button dovolenaZrusButton;
    Button sluzebkaButton;
    Button sluzebkaZrusButton;
    Button nemocButton;
    Button nemocZrusButton;
    Button volnoButton;
    Button volnoZrusButton;

    HorizontalLayout upperDochHeader;
    HorizontalLayout upperDochFooterBar;
    HorizontalLayout lowerDochHeader;
    Grid<Doch> upperDochGrid;
    List<Doch> upperDochList = new ArrayList<>();

    Grid<Doch> lowerDochGrid;
    List<Doch> lowerDochList;
//    HorizontalLayout dochRecLowerHeader = new HorizontalLayout();
//    HorizontalLayout dochRecLowerFooter = new HorizontalLayout();

    private Person dochPerson;
    private LocalDate dochDate;
    private LocalDate dochDatePrev;

    private Clock minuteClock = Clock.tickMinutes(ZoneId.systemDefault());
    private TimeThread timeThread;

//    @Autowired
//    public DochRepo kontRepo;

    @Autowired
    public PersonService personService;

    @Autowired
    public CinRepo cinRepo;

    @Autowired
    public DochService dochService;

    @Autowired
    public DochsumService dochsumService;

    @Autowired
    public PruhRepo pruhRepo;


    //    @Autowired
//    public DochForm(Person dochPerson) {
    public DochView() {
//        super();
        buildForm();
//        initPruhData(dochPerson, dochDate);
    }

    @PostConstruct
    public void init() {
        authUsername = SecurityUtils.getUsername();
        initDochData();
        dochFormDialog = new DochFormDialog(
                this::stampDochManualFromDialog);
    }

    private void stampDochManualFromDialog(DochManual dochManual, Operation operation) {
        Doch recToClose;
        Doch recToOpen;
        LocalDateTime modifStamp = LocalDateTime.now();

        if (null == dochManual) {
            ConfirmDialog.createWarning()
                    .withCaption("DOCHÁZKA RUČNĚ")
                    .withMessage("Interní chyba při zpracování.")
                    .open()
                    ;
            return;
        }

        if (Operation.STAMP_SINGLE == operation) {
            // In dochManual is inside rec to be opened:
            recToOpen = new Doch(
                    dochDate
                    , dochPerson
                    , cinRepo.findByCinKod(dochManual.getCinCinKod())
                    , dochManual.getFromTime()
                    , modifStamp
                    , false
                    , dochManual.getPoznamka()
            );
            if (Cin.CinKod.SC == dochManual.getCinCinKod()) {
                recToOpen.setDochDur(Duration.ofMinutes(8*60+30));
            } else {
                recToOpen.setDochDur(Duration.ofHours(8));
            }

            recToClose = null;
//            recToClose = getLastZkDochRec();
//            if (recToClose != null) {
//                recToClose.setToTime(dochManual.getFromTime());
//                recToClose.setToModifDatetime(modifStamp);
//                recToClose.setToManual(!dochManual.getFromTime().equals(dochManual.getFromTimeOrig()));
//            }
        } else if (Operation.STAMP_PRICH_MAN == operation) {
            // In dochManual is inside rec to be opened:
            recToOpen = new Doch(
                    dochDate
                    , dochPerson
                    , cinRepo.findByCinKod(dochManual.getCinCinKod())
                    , dochManual.getFromTime()
                    , modifStamp
                    , !dochManual.getFromTime().equals(dochManual.getFromTimeOrig())
                    , dochManual.getPoznamka()
            );
            recToClose = getLastZkDochRec();
            if (recToClose != null) {
                recToClose.setToTime(dochManual.getFromTime());
                recToClose.setToModifDatetime(modifStamp);
                recToClose.setToManual(!dochManual.getFromTime().equals(dochManual.getFromTimeOrig()));
            }

        } else if ((Operation.STAMP_ODCH_MAN == operation) || (Operation.STAMP_ODCH_MAN_LAST == operation)) {
            // In dochManual is inside rec to be closed:
            recToOpen = null;
            if (Operation.STAMP_ODCH_MAN == operation){
                recToOpen = new Doch(
                        dochDate
                        , dochPerson
                        , cinRepo.findByCinKod(dochManual.getOutsideCinKod())
                        , dochManual.getFromTime()
                        , modifStamp
                        , !dochManual.getFromTime().equals(dochManual.getFromTimeOrig())
                        , dochManual.getPoznamka()
                );
            }
            recToClose = getLastZkDochRec();
            if (null == recToClose) {
                ConfirmDialog.createWarning()
                        .withCaption("DOCHÁZKA RUČNĚ")
                        .withMessage("Interní chyba při zpracování.")
                        .open()
                ;
                return;
            }
            if (Operation.STAMP_ODCH_MAN_LAST == operation) {
                recToClose.setToTime(dochManual.getToTime());
                recToClose.setToModifDatetime(modifStamp);
                recToClose.setToManual(!dochManual.getToTime().equals(dochManual.getToTimeOrig()));
                recToClose.setPoznamka(dochManual.getPoznamka());
            } else {
                recToClose.setToTime(dochManual.getFromTime());
                recToClose.setToModifDatetime(modifStamp);
                recToClose.setToManual(!dochManual.getFromTime().equals(dochManual.getFromTimeOrig()));
                recToClose.setPoznamka(dochManual.getPoznamka());
            }
        } else  {
            recToClose = null;
            recToOpen = null;
        }

        dochService.closeRecAndOpenNew(recToClose, recToOpen);
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {

//        getLogger().info("## ON ATTACH DochView ##");

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        dochDateSelector.setLocale(new Locale("cs", "CZ"));
        dochDateSelector.setLocale(czLocale);

        dochDatePrev = LocalDate.now().minusDays(1);
        lowerDochDateInfo.setText(dochDatePrev.format(lowerDochDateHeaderFormatter));

        timeThread = new TimeThread(attachEvent.getUI(), this);
        timeThread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
//        getLogger().info("## ON DETACH DochView ##");

        timeThread.interrupt();
        timeThread = null;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }


    private Component initPruhStateBox() {
        HorizontalLayout box = new HorizontalLayout();
//        box.setWidth("3em");
//        box.setMinWidth("3em");
        box.setVerticalComponentAlignment(Alignment.END);
        box.getStyle()
                .set("margin-top", "0.7em");


        pruhStateIconUnlocked = VaadinIcon.UNLOCK.create();
        pruhStateIconUnlocked.setColor("green");
        pruhStateIconUnlocked.getStyle().set("margin-right", "0.3em");
        pruhStateIconUnlocked.setVisible(false);

        pruhStateIconLocked = VaadinIcon.LOCK.create();
        pruhStateIconLocked.setColor("crimson");
        pruhStateIconLocked.getStyle().set("margin-right", "0.3em");
        pruhStateIconLocked.setVisible(false);

        pruhStateIconNone = VaadinIcon.GRID_SMALL_O.create();
        pruhStateIconNone.setColor("darkmagenta");
        pruhStateIconNone.getStyle().set("margin-right", "0.3em");
        pruhStateIconNone.setVisible(false);

        box.add(pruhStateIconLocked, pruhStateIconUnlocked, pruhStateIconNone);
        return box;
    }

    private Component initDochStateBox() {
        HorizontalLayout box = new HorizontalLayout();
        box.setWidth("3em");
        box.setMinWidth("3em");
        box.setVerticalComponentAlignment(Alignment.END);
        box.getStyle()
                .set("margin-top", "0.7em");

        dochStateIconOpened = VaadinIcon.FLAG.create();
        dochStateIconOpened.setColor("green");
        dochStateIconOpened.getStyle().set("margin-right", "1em");
        dochStateIconOpened.setVisible(false);

        dochStateIconClosed = VaadinIcon.FLAG_CHECKERED.create();
        dochStateIconClosed.setColor("crimson");
        dochStateIconClosed.getStyle().set("margin-right", "1em");
        dochStateIconClosed.setVisible(false);

        box.add(dochStateIconClosed, dochStateIconOpened);
        return box;
    }

    private void setDochControlsLocked(Integer pruhState) {

        boolean dochClosed = dochDayIsClosed();
        boolean ctrlsEnabled = !dochClosed && (null == pruhState || PRUH_STATE_UNLOCKED.equals(pruhState));

        pruhStateIconLocked.setVisible(PRUH_STATE_LOCKED.equals(pruhState));
        pruhStateIconUnlocked.setVisible(PRUH_STATE_UNLOCKED.equals(pruhState));
        pruhStateIconNone.setVisible(null == pruhState);

        dochStateIconClosed.setVisible(dochClosed);
        dochStateIconOpened.setVisible(!dochClosed);

        removeLastDochRecButton.setEnabled(ctrlsEnabled);
        removeAllDochRecButton.setEnabled(ctrlsEnabled);

        prichodBtn.setEnabled(ctrlsEnabled);
        prichodAltBtn.setEnabled(ctrlsEnabled);

        odchodRadio.setEnabled(ctrlsEnabled);
        odchodButton.setEnabled(ctrlsEnabled);
        odchodAltButton.setEnabled(ctrlsEnabled);

        sluzebkaButton.setEnabled(ctrlsEnabled);
        sluzebkaZrusButton.setEnabled(ctrlsEnabled);

        dovolenaButton.setEnabled(ctrlsEnabled);
        dovolenaHalfButton.setEnabled(ctrlsEnabled);
        dovolenaZrusButton.setEnabled(ctrlsEnabled);

        nemocButton.setEnabled(ctrlsEnabled);
        nemocZrusButton.setEnabled(ctrlsEnabled);

        volnoButton.setEnabled(ctrlsEnabled);
        volnoZrusButton.setEnabled(ctrlsEnabled);

        if (null == pruhState) {
            prenosPersonDateButton.setEnabled(false);
            cancelPrenosPersonDateButton.setEnabled(false);
        } else if (PRUH_STATE_UNLOCKED.equals(pruhState)) {
            prenosPersonDateButton.setEnabled(!dochClosed);
            cancelPrenosPersonDateButton.setEnabled(dochClosed);
        } else {
            prenosPersonDateButton.setEnabled(false);
            cancelPrenosPersonDateButton.setEnabled(false);
        }
        prenosPersonDateAllButton.setEnabled(false);
        prenosPersonDateMonthButton.setEnabled(false);
    }

    private boolean dochDayIsClosed() {
        return upperDochList.stream()
                .anyMatch(Doch::hasClosedState);
    }


    private void initDochData() {
//        getLogger().info("## Initializing doch data");

        odchodRadio.setItems(cinRepo.getCinsForDochOdchodRadio());

        loadPruhDataFromDb();
        Person pruhPersonByAuth = getDochPersonFromList(authUsername).orElse(null);
        dochPersonSelector.setValue(pruhPersonByAuth);

        if (null == dochDate) {
//            dochDate = LocalDate.of(2019, 1, 15);
            dochDate = LocalDate.now();
            dochDateSelector.setValue(dochDate);
        }
        updateDochControls();
    }

    private void loadPruhDataFromDb() {
        dochPersonList = personService.fetchAllNotHidden();
        dochPersonSelector.setValue(null);
        dochPersonSelector.setItems(dochPersonList);
    }

    private Optional<Person> getDochPersonFromList(String username) {
        return dochPersonList.stream()
                .filter(person -> person.getUsername().toLowerCase().equals(username.toLowerCase()))
                .findFirst();
    }

    //    public void updateDochClockTime(LocalTime time) {
    void updateDochClockTime() {
        clockDisplay.setText(LocalTime.now().format(dochTimeFormatter));
    }

    private String getPersonLabel(Person person) {
        return person.getUsername() + " (" + person.getJmeno() + " " + person.getPrijmeni() + ")";
    }


//    private void initWhenOpened() {
//        dochPerson = personService.getById(13L);
//        dochDate = LocalDate.of(2019, 01, 17);
//        loadUpperDochGridData(dochPerson, dochDate);
//    }

    private void buildForm() {
        this.setDefaultVerticalComponentAlignment(Alignment.STRETCH);
        this.setWidthFull();
        this.setHeightFull();
        this.setMargin(false);
        this.getStyle().set("margin-top", "2em");
        this.getStyle().set("margin-bottom", "1em");
        this.setPadding(false);
        this.setSpacing(false);
        this.setAlignSelf(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.BETWEEN);

        VerticalLayout mainDochPanel = new VerticalLayout();
        mainDochPanel.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        mainDochPanel.setWidth("1300px");
        mainDochPanel.setMargin(false);
        mainDochPanel.setSpacing(false);
        mainDochPanel.setAlignSelf(Alignment.CENTER);
        mainDochPanel.getStyle()
                .set("background-color", "#fefefd")
                .set("padding-bottom", "0")
        ;

        dochMonthReportBtn.setText("Měsíční přehled");
        dochMonthReportBtn.setEnabled(false);

        dochYearReportBtn.setText("Roční přehled");
        dochYearReportBtn.setEnabled(false);

        dochControl.setWidth("30em");
        dochControl.getStyle()
                .set("margin-top", "0.3em");
        dochControl.add(
                initClockComponent()
                , initPrichodButton()
                , initPrichodAltButton()
                , buildVertSpace()
                , buildVertSpace()
                , initOdchodRadio()
                , initOdchodButton()
                , initOdchodAltButton()
                , buildVertSpace()
                , buildVertSpace()
                , initSluzebkaButton()
                , initSluzebkaZrusButton()

        );


        nepritControl.setWidth("30em");
        nepritControl.getStyle()
                .set("margin-top", "4.2em")
                .set("margin-left", "1em")
        ;
        nepritControl.add(
                initDovolenaButton()
                , initDovolenaHalfButton()
                , initDovolenaZrusButton()
//                , buildVertSpace()
//                , buildVertSpace()
//                , initSluzebkaButton()
//                , initSluzebkaZrusButton()
                , buildVertSpace()
                , buildVertSpace()
                , initNemocButton()
                , initNemocZrusButton()
                , buildVertSpace()
                , buildVertSpace()
                , initVolnoButton()
                , initVolnoZrusButton()
        );

//        lowerDochDateInfo.setText("Předchozí den docházky...");
//        dochRecLowerHeader.getStyle()
//                .set("margin-top", "2em");
//        dochRecLowerHeader.add(lowerDochDateInfo);

        VerticalLayout dochRecPane = new VerticalLayout();
        dochRecPane.setHeight("650px");
        dochRecPane.setPadding(false);
        dochRecPane.getStyle()
                .set("margin-top", "1em")
                .set("margin-left", "1em")
//                .set("margin-right", "1em")
        ;
        dochRecPane.setClassName("view-container");
        dochRecPane.setAlignItems(Alignment.STRETCH);


        dochRecPane.add(initUpperDochHeaderBar());
        dochRecPane.add(initUpperDochGrid());
        dochRecPane.add(initUpperDochFooterBar());

//        dochRecPane.add(initLowerDochDateInfo());
        dochRecPane.add(initLowerDochHeaderBar());
        dochRecPane.add(initLowerDochGrid());
//        dochRecPane.add(dochRecLowerFooter);

        HorizontalLayout dochPanel = new HorizontalLayout();
        dochPanel.add(dochControl);
        dochPanel.add(dochRecPane);
        dochPanel.add(nepritControl);

        mainDochPanel.add(initDochHeader(), dochPanel);
        this.add(new Ribbon(), mainDochPanel, new Ribbon());
    }


    private Component initDochHeader() {
        H3 dochTitle = new H3("DOCHÁZKA");
        dochTitle.getStyle()
                .set("margin-left", "0")
                .set("margin-top", "10px")
                .set("margin-bottom", "20px")
        ;
        dochHeader = new HorizontalLayout();
        dochHeader.setSpacing(false);
        dochHeader.setWidthFull();
        dochHeader.setHeight("3.5em");
        dochHeader.getStyle()
            .set("margin-bottom", "0.3em");
        dochHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                dochMonthReportBtn
                , dochYearReportBtn
        );
        dochHeader.add(
                dochTitle
                , initDochStateBox()
                , initPersonSelector()
                , buttonBox
        );
        return dochHeader;
    }

    private Component initClockComponent() {
        clockContainer = new VerticalLayout();
        clockContainer.setWidthFull();
        clockContainer.setHeight("3em");
        clockContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        clockContainer.setFlexGrow(1);
//        clockContainer.getElement()
//            .setAttribute("colspan","2")
//                .set("width", "stretch")
//                .set("align", "center")
//    ;
//        clockDisplay.getStyle().set("colspan", "2");
//        clockDisplay.setAlignSelf();
        clockContainer.setMargin(false);
        clockContainer.setPadding(false);

        clockDisplay.getStyle()
//                .set("font-size", "var(--lumo-font-size-l)")
                .set("colspan", "2")
                .set("margin-top", "0.2em")
                .set("margin-botton", "0.2em")
                .set("font-size","2.1em")
                .set("font-weight","600")
//                .set("font-family", "monospace")
//                .set("font-family", "ariel")
                .set("font-variant-numeric", "tabular-nums")
        ;
//                .set("padding-right", "0.75em")

        clockContainer.add(clockDisplay);
        return clockContainer;
    }

    private Component initUpperDochDateInfo() {
        upperDochDateInfo = new Paragraph();
        upperDochDateInfo.getStyle()
                .set("font-weight", "bold")
                .set("margin-right", "0.8em")
        ;
        upperDochDateInfo.setText("Den docházky...");
        return upperDochDateInfo;
    }

    private Component initLowerDochDateInfo() {
        lowerDochDateInfo = new Span();
        lowerDochDateInfo.getStyle()
                .set("font-weight", "bold")
                .set("margin-right", "0.8em")
        ;
        lowerDochDateInfo.setText("Předchozí den docházky...");
        return lowerDochDateInfo;
    }

    private Component initPersonSelector() {
        dochPersonSelector = new ComboBox<>();
        dochPersonSelector.setLabel(null);
        dochPersonSelector.setWidth("20em");
        dochPersonSelector.setPlaceholder("Pracovník");
        dochPersonSelector.setItems(new ArrayList<>());
        dochPersonSelector.setItemLabelGenerator(this::getPersonLabel);
        dochPersonSelector.setEnabled(canViewOtherUsers());
        dochPersonSelector.addValueChangeListener(event -> {
            dochPerson = event.getValue();
            updateDochControls();
        });
        dochPersonSelector.addBlurListener(event -> {
//            loadUpperDochGridData(dochPerson.getId(), dochDate);
            upperDochGrid.getDataProvider().refreshAll();
        });
        return dochPersonSelector;
    }

    private Component initDochDateSelector() {
        dochDateSelector = new DatePicker();
        dochDateSelector.setLabel(null);
        dochDateSelector.setWidth("10em");
        dochDateSelector.setPlaceholder("Rok-měsíc");
//        dochDateSelector.getStyle().set("margin-right", "1em");
        dochDateSelector.addValueChangeListener(event -> {
            dochDate = event.getValue();
            updateDochControls();
        });
        return dochDateSelector;
    }

    private void updateDochControls() {
        updateUpperDochGridPane(dochPerson, dochDate);
        updateLowerDochGridPane(dochPerson, dochDate);
        Pruh pruh = null;
        if (null != dochDate && null != dochPerson) {
            pruh = pruhRepo.findFirstByYmAndPersonId(YearMonth.from(dochDate), dochPerson.getId());
        }
        pruhState = null == pruh ? null : pruh.getState();
        setPruhStateControls(pruhState);
    }


    private void setPruhStateControls(Integer newState) {
//        if (null == newState) {
//            setDochControlsLocked(null);
//        } else if (newState.equals(PRUH_STATE_LOCKED)) {
//            setDochControlsLocked(true);
//        } else {
//            setDochControlsLocked(false);
//        }
        setDochControlsLocked(newState);
    }

    private Component initPrichodButton() {
        prichodBtn = new Button("Příchod");
        prichodBtn.getElement().setAttribute("theme", "primary");
        prichodBtn.addClickListener(event -> {
                LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
                Cin.CinKod prichodWhereKod = Cin.CinKod.P;
                resetOdchodRadio();
                stampPrichodAndNewInsideRec(currentDateTime, prichodWhereKod);
        });
        return prichodBtn;
    }

    private Button initPrichodAltButton() {
        prichodAltBtn = new Button("Příchod jiný čas");
        prichodAltBtn.getElement().setAttribute("theme", "secondary");
        prichodAltBtn.addClickListener(event -> {
                LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
                Cin.CinKod prichodWhereKod = Cin.CinKod.P;
                resetOdchodRadio();
                stampPrichodAltAndNewInsideRec(currentDateTime, prichodWhereKod);
        });
        return prichodAltBtn;
    }

    private RadioButtonGroup<Cin> initOdchodRadio() {
        odchodRadio = new RadioButtonGroup<>();
//        odchodRadio.setItems("Odchod na oběd", "Odchod pracovně", "Odchod k lékaři", "Ukončení/přerušení práce");
        odchodRadio.setRenderer(new ComponentRenderer<>(cin -> new Span(cin.getAkce())));
        odchodRadio.addValueChangeListener(this::odchodRadioChanged);
//        odchodRadio.getElement().getStyle().set("display", "flex");
        odchodRadio.getElement().setAttribute("theme", "vertical");
        //                getStyle().set("flex-direction", "column");
        return odchodRadio;
    }

    private void resetOdchodRadio() {
//        odchodRadio.setValue(null);
        odchodRadio.clear();    // Probably a bug -> workaround...
        odchodRadio.getElement().getChildren()
                .filter(element -> element.hasProperty("checked"))
                .forEach(checked -> checked.removeProperty("checked"));
    }

    private Button initOdchodButton() {
        odchodButton = new Button("Odchod");
        odchodButton.getElement().setAttribute("theme", "primary");
        odchodButton.addClickListener(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
            Cin.CinKod odchodWhereKod = odchodRadio.getValue().getCinKod();
            resetOdchodRadio();
//            odchodButtonClicked(event);
            if (Cin.CinKod.PM == odchodWhereKod) {
                stampOdchodAltAndNewOutsideRec(currentDateTime, odchodWhereKod, "Odchod pracovně", false, false);
            } else if (Cin.CinKod.KP == odchodWhereKod || Cin.CinKod.XD == odchodWhereKod) {
                stampOdchod(currentDateTime);
            } else {
                stampOdchodAndNewOutsideRec(currentDateTime, odchodWhereKod);
            }
        });
        odchodButton.setEnabled(false);
        return odchodButton;
    }

    private Button initOdchodAltButton() {
        odchodAltButton = new Button("Odchod jiný čas");
//        odchodAltButton.addClickListener(event -> odchodAltButtonClicked(event));
        odchodAltButton.getElement().setAttribute("theme", "secondary");
        odchodAltButton.addClickListener(event -> {
            Cin.CinKod odchodWhereKod = odchodRadio.getValue().getCinKod();
            resetOdchodRadio();
//            odchodAltButtonClicked(event);
            LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
            if (Cin.CinKod.KP == odchodWhereKod || Cin.CinKod.XD == odchodWhereKod) {
                stampOdchodAlt(currentDateTime);
            } else if (Cin.CinKod.PM == odchodWhereKod) {
                stampOdchodAltAndNewOutsideRec(currentDateTime, odchodWhereKod, "Odchod pracovně jiný čas", true, false);
            } else {
                stampOdchodAltAndNewOutsideRec(currentDateTime, odchodWhereKod, "Odchod jiný čas", true, false);
            }

        });
        odchodAltButton.setEnabled(false);
        return odchodAltButton;
    }


    private Component initVolnoButton() {
        volnoButton = new Button("Neplacené volno (8h)");
        volnoButton.getElement().setAttribute("theme", "primary");
        volnoButton.addClickListener(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
            stampSingleRecord(currentDateTime, Cin.CinKod.nv);
        });
        return volnoButton;
    }

    private Component initVolnoZrusButton() {
        volnoZrusButton = new Button("Zrušit neplac.volno");
        volnoZrusButton
                .getElement().setAttribute("theme", "error secondary");
        ;
        volnoZrusButton.addClickListener(event -> {
            if (!checkDochContainsNahradniVolno("není co rušit")) {
                return;
            }
            ConfirmDialog.createQuestion()
                    .withCaption("Záznam docházky")
                    .withMessage("Zrušit náhradní volno?")
                    .withYesButton(() -> {
                            dochService.removeDochRec(dochPerson.getId(), dochDate, Cin.CinKod.nv);
                            updateUpperDochGridPane(dochPerson, dochDate);
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });

        return volnoZrusButton;
    }

    private Component initNemocButton() {
        nemocButton = new Button("Nemoc (8h)");
        nemocButton.getElement().setAttribute("theme", "primary");
        nemocButton.addClickListener(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
            stampSingleRecord(currentDateTime, Cin.CinKod.ne);
        });
        return nemocButton;
    }

    private Component initNemocZrusButton() {
        nemocZrusButton = new Button("Zrušit nemoc");
        nemocZrusButton
                .getElement().setAttribute("theme", "error secondary");
        ;
        nemocZrusButton.addClickListener(event -> {
            if (!checkDochContainsNemoc("není co rušit")) {
                return;
            }
            ConfirmDialog.createQuestion()
                    .withCaption("Záznam docházky")
                    .withMessage("Zrušit nemoc?")
                    .withYesButton(() -> {
                        dochService.removeDochRec(dochPerson.getId(), dochDate, Cin.CinKod.ne);
                        updateUpperDochGridPane(dochPerson, dochDate);
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        return nemocZrusButton;
    }

    private Component initDovolenaButton() {
        dovolenaButton = new Button("Dovolená (8h)");
        dovolenaButton.getElement().setAttribute("theme", "primary");
        dovolenaButton.addClickListener(event -> {
                LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
                stampSingleRecord(currentDateTime, Cin.CinKod.dc);
        });
//        dovolenaButton.setEnabled(false);
        return dovolenaButton;
    }

    private Component initDovolenaHalfButton() {
        dovolenaHalfButton = new Button("Dovolená (4h)");
        dovolenaHalfButton.addClickListener(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
            stampDovolenaHalf(currentDateTime, Cin.CinKod.dp);
        });
        return dovolenaHalfButton;
    }

    private Component initDovolenaZrusButton() {
        dovolenaZrusButton = new Button("Zrušit dovolenou");
        dovolenaZrusButton.addClickListener(event -> {
            if (!checkDochContainsDovolena("není co rušit")) {
                return;
            }
            ConfirmDialog.createQuestion()
                    .withCaption("Záznam docházky")
                    .withMessage("Zrušit dovolenou?")
                    .withYesButton(() -> {
                            dochService.removeDochRec(dochPerson.getId(), dochDate, Cin.CinKod.dc);
                            updateUpperDochGridPane(dochPerson, dochDate);
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        dovolenaZrusButton
                .getElement().setAttribute("theme", "error secondary");
        ;
        return dovolenaZrusButton;
    }

    private Component initSluzebkaButton() {
        sluzebkaButton = new Button("Služebka (celodenní)");
        sluzebkaButton.getElement().setAttribute("theme", "primary");
        sluzebkaButton.addClickListener(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now(minuteClock);
            stampSingleManualRecord(currentDateTime, Cin.CinKod.SC);
        });
        return sluzebkaButton;
    }

    private Component initSluzebkaZrusButton() {
        sluzebkaZrusButton = new Button("Zrušit služebku");
        sluzebkaZrusButton
                .getElement().setAttribute("theme", "error secondary");
        ;
        sluzebkaZrusButton.addClickListener(event -> {
            if (!checkDochContainsSluzebka("není co rušit")) {
                return;
            }
            ConfirmDialog.createQuestion()
                    .withCaption("Záznam docházky")
                    .withMessage("Zrušit služebku?")
                    .withYesButton(() -> {
                        dochService.removeDochRec(dochPerson.getId(), dochDate, Cin.CinKod.PM);
                        updateUpperDochGridPane(dochPerson, dochDate);
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        return sluzebkaZrusButton;
    }


    private Component buildVertSpace() {
        Div vertSpace = new Div();
        vertSpace.setHeight("1em");
        return vertSpace;
    }

    private ValueProvider<Doch, String> fromTimeValProv =
            doch -> null == doch.getFromTime() ? null : doch.getFromTime().format(VzmFormatUtils.shortTimeFormatter);

    private ValueProvider<Doch, String> toTimeValProv =
            doch -> null == doch.getToTime() ? null : doch.getToTime().format(VzmFormatUtils.shortTimeFormatter);

    private ValueProvider<Doch, String> durationValProv =
//            doch -> null == doch.getDochDurationUI() ? null : formatDuration(doch.getDochDurationUI());
//            doch -> null == doch.getDochDur() ? null : formatDuration(doch.getDochDur());
            doch -> null == doch.getDochDur() ? null : formatDuration(doch.getDochDur());

    private Component initUpperDochHeaderBar() {
        upperDochHeader = new HorizontalLayout();
        upperDochHeader.setWidth("100%");
        upperDochHeader.setHeight("5em");
        upperDochHeader.setSpacing(false);
        upperDochHeader.getStyle()
                .set("margin-top", "0.1em")
                .set("margin-bottom", "0.2em")
        ;
        upperDochHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initLoadTodayButton()
                , initLoadPrevDateButton()
                , initLoadNextDateButton()
                , initLoadLastDateButton()
                , initRemoveLastDochRecButton()
                , initRemoveAllDochRecsButton()
        );

        upperDochHeader.add(
                initDochDateSelector()
                , initUpperDochDateInfo()
                , buttonBox
        );
        return upperDochHeader;
    }

    private Component initUpperDochFooterBar() {
        upperDochFooterBar = new HorizontalLayout();
//        upperDochFooterBar.setWidth("100%");
        upperDochFooterBar.setWidthFull();
        upperDochFooterBar.getStyle()
                .set("margin-top", "0.5em")
        ;
        upperDochFooterBar.setSpacing(false);
        upperDochFooterBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initPruhStateBox()
                , initPrenosPersonDateButton()
                , initCancelPrenosPersonDateButton()
                , initPrenosPersonMonthButton()
                , initPrenosPersonAllButton()
        );

        upperDochFooterBar.add(
                buttonBox
        );
        return upperDochFooterBar;
    }

    private Component initLowerDochHeaderBar() {
        lowerDochHeader = new HorizontalLayout();
        lowerDochHeader.setWidthFull();
        lowerDochHeader.setSpacing(false);
        lowerDochHeader.getStyle()
                .set("margin-top", "2em")
                .set("margin-bottom", "0")
        ;
        lowerDochHeader.setJustifyContentMode(JustifyContentMode.BETWEEN);
        lowerDochHeader.add(
                initLowerDochDateInfo()
        );
        return lowerDochHeader;
    }

    private void setDochNaviButtonsEnabled(boolean enabled) {
        loadTodayButton.setIcon(enabled ? loadTodayIconEnabled :loadTodayIconDisabled);
        loadTodayButton.setEnabled(enabled);

        loadPrevDateButton.setEnabled(enabled);
        loadNextDateButton.setEnabled(enabled);
        loadLastDateButton.setEnabled(enabled);

        removeAllDochRecButton.setIcon(enabled ? removeAllDochRecsIconEnabled :removeAllDochRecsIconDisabled);
        removeAllDochRecButton.setEnabled(enabled);

        removeLastDochRecButton.setIcon(enabled ? removeLastDochRecIconEnabled :removeLastDochRecIconDisabled);
        removeLastDochRecButton.setEnabled(enabled);
    }

    private Component initLoadTodayButton() {
        loadTodayIconEnabled = VaadinIcon.BULLSEYE.create();
        loadTodayIconEnabled.setColor("green");
        loadTodayIconDisabled = VaadinIcon.BULLSEYE.create();
        loadTodayIconDisabled.setColor(DISABLED_ICON_COLOR);

        loadTodayButton = new Button(loadTodayIconEnabled);
        loadTodayButton.addClickListener(event -> {
            if (null != dochPerson) {
                dochDate = LocalDate.now();
                dochDateSelector.setValue(dochDate);
                fireEvent(new GeneratedVaadinDatePicker.ChangeEvent<>(dochDateSelector, false));
//                updateUpperDochGridPane(dochPerson, dochDate);

//                dochDateSelector.setValue(dochDate);
//                fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDateSelector, false));
//                loadUpperDochGridData(dochPerson, dochDate);
//                loadPrevDateButton.setEnabled(true);
                setDochNaviButtonsEnabled(true);
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadTodayButton;
    }

    private Component initLoadPrevDateButton() {
        loadPrevDateButton = new Button(VaadinIcon.STEP_BACKWARD.create());
        loadPrevDateButton.addClickListener(event -> {
            if (null != dochPerson && null != dochDate) {
                LocalDate prevDochDate = dochService.findPrevDochDate(dochPerson.getId(), dochDate);
                if (prevDochDate == null) {
                    loadPrevDateButton.setEnabled(false);
                } else {
                    dochDate = prevDochDate;
                    dochDateSelector.setValue(dochDate);
                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent<>(dochDateSelector, false));
                    setDochNaviButtonsEnabled(true);
                }
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadPrevDateButton;
    }

    private Component initLoadNextDateButton() {
        loadNextDateButton = new Button(VaadinIcon.STEP_FORWARD.create());
        loadNextDateButton.addClickListener(event -> {
            if (null != dochPerson && null != dochDate) {
                LocalDate nextDochDate = dochService.findNextDochDate(dochPerson.getId(), dochDate);
                if (nextDochDate == null) {
                    loadNextDateButton.setEnabled(false);
                } else {
                    dochDate = nextDochDate;
                    dochDateSelector.setValue(dochDate);
                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent<>(dochDateSelector, false));
                    setDochNaviButtonsEnabled(true);
                }
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadNextDateButton;
    }

    private Component initLoadLastDateButton() {
        loadLastDateButton = new Button(VaadinIcon.FAST_FORWARD.create());
        loadLastDateButton.addClickListener(event -> {
            if (null != dochPerson && null != dochDate) {
                LocalDate lastDochDate = dochService.findLastDochDate(dochPerson.getId());
                if (lastDochDate == null) {
                    loadLastDateButton.setEnabled(false);
                } else {
                    dochDate = lastDochDate;
                    dochDateSelector.setValue(dochDate);
                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent<>(dochDateSelector, false));
                    setDochNaviButtonsEnabled(true);
                }
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadLastDateButton;
    }

    private Component initRemoveLastDochRecButton() {
        removeLastDochRecIconEnabled = VaadinIcon.REPLY.create();
        removeLastDochRecIconEnabled.setColor("crimson");
        removeLastDochRecIconDisabled = VaadinIcon.REPLY.create();
        removeLastDochRecIconDisabled.setColor(DISABLED_ICON_COLOR);

        removeLastDochRecButton = new Button(removeAllDochRecsIconDisabled);
        removeLastDochRecButton.addClickListener(event -> {
            if (canRemoveDochRec()) {
                ConfirmDialog.createQuestion()
                    .withCaption("Záznam docházky")
                    .withMessage("Zrušit poslední záznam v docházce?")
                    .withYesButton(() -> {
                        dochService.removeLastZkDochAndReopenPrev(getLastDochRec());
                        updateUpperDochGridPane(dochPerson, dochDate);
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
                ;
            }
        });
        return removeLastDochRecButton;
    }


    private Component initRemoveAllDochRecsButton() {
        removeAllDochRecsIconEnabled = VaadinIcon.TRASH.create();
        removeAllDochRecsIconEnabled.setColor("crimson");
        removeAllDochRecsIconDisabled = VaadinIcon.TRASH.create();
        removeAllDochRecsIconDisabled.setColor(DISABLED_ICON_COLOR);

        removeAllDochRecButton = new Button(removeLastDochRecIconDisabled);
        removeAllDochRecButton.addClickListener(event -> {
            if (canRemoveAllDochRecs()) {
                ConfirmDialog.createQuestion()
                        .withCaption("Záznamy docházky")
                        .withMessage("Zrušit všechny záznamy v aktuální docházce?")
                        .withOkButton(() -> {
                            dochService.removeAllDochRecsForPersonAndDate(dochPerson.getId(), dochDate);
                            updateUpperDochGridPane(dochPerson, dochDate);
                        }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT"))
                        .withCancelButton(ButtonOption.caption("ZPĚT"))
                        .open()
                ;
            }
        });
        return removeAllDochRecButton;
    }

    private Component initPrenosPersonDateButton() {
        prenosPersonDateButton = new Button("Přenos do proužku");
        prenosPersonDateButton.getElement().setAttribute("theme", "primary");
        prenosPersonDateButton.addClickListener(event -> {
            if (pruhState.equals(PRUH_STATE_LOCKED)) {
                ConfirmDialog.createWarning()
                        .withCaption("PŘENOS DOCHÁZKY")
                        .withMessage("Nelze vykonat, příslušný proužek je uzamčen.")
                        .open()
                ;
                return;
            }
            ConfirmDialog.createQuestion()
                    .withCaption("PŘENOS DOCHÁZKY")
                    .withMessage("Přenést docházku do proužku a uzavřít den?")
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .withYesButton(() -> {
                        try {
                            updateDochsumAndCloseDochDay();
                            updateDochControls();
                        } catch (Exception e) {
                            getLogger().error("Error when closing  DOCH day", e);
                            ConfirmDialog
                                    .createError()
                                    .withCaption("CHYBA")
                                    .withMessage("Neočekávaná chyba při přenosu docházky do proužků")
                                    .open();
                        }
                    })
                    .open()
            ;
        });
        return prenosPersonDateButton;
    }

    private Component initCancelPrenosPersonDateButton() {
        cancelPrenosPersonDateButton = new Button("Zrušit přenos");
        cancelPrenosPersonDateButton
                .getElement().setAttribute("theme", "error secondary");
        ;
        cancelPrenosPersonDateButton.addClickListener(event -> {
            if (pruhState.equals(PRUH_STATE_LOCKED)) {
                ConfirmDialog.createWarning()
                        .withCaption("ZRUŠENÍ PŘENOSU DOCHÁZKY")
                        .withMessage("Nelze vykonat, příslušný proužek je uzamčen.")
                        .open()
                ;
                return;
            }
            ConfirmDialog.createInfo()
                    .withCaption("ZRUŠENÍ PŘENOSU DOCHÁZKY")
                    .withMessage("Vynulovat docházku v proužku a otevřít den?")
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .withYesButton(() -> {
                        deleteFromDochsumAndOpenDochDay();
                        updateDochControls();
                    })
                    .open()
            ;
        });
        return cancelPrenosPersonDateButton;
    }


    private void updateDochsumAndCloseDochDay() {
//        generateAutomaticLunch();

        final Duration obedRequired = Duration.ofMinutes(30);
        final Duration durForObedAuto = Duration.ofMinutes(4 * 60 + 30);

        Duration durPrac = sumOfDochPrac();
        Duration durObedMan = sumOfObedStamped();
        Duration durObedAutMinus = sumOfObedAutMinus();

        // Generate obed auto:
        Duration durPracProductive = sumOfDochPracProductive();
        if ((durPracProductive.compareTo(durForObedAuto) > 0)
                && (durObedMan.minus(durObedAutMinus).compareTo(obedRequired) < 0)) {
            Duration durObedAutNewMinus = Duration.ZERO.minus(obedRequired.minus(durObedMan));
            stampSingleObedAuto(durObedAutNewMinus);
            durObedAutMinus = durObedAutMinus.plus(durObedAutNewMinus);
        }

        Dochsum dochsum = new Dochsum(dochPerson, dochDate);

        LocalTime fromTimeMin = getDsFromFirst();
        LocalDateTime fromDateTimeMin = null;
        if (null != fromTimeMin) {
            fromDateTimeMin = LocalDateTime.of(dochDate.getYear(), dochDate.getMonth(), dochDate.getDayOfMonth()
                    , fromTimeMin.getHour(), fromTimeMin.getMinute());
        }
        dochsum.setDsFromFirst(fromDateTimeMin);

        LocalTime toTimeMax = getDsToLast();
        LocalDateTime toDateTimeMax = null;
        if (null != toTimeMax) {
            toDateTimeMax = LocalDateTime.of(dochDate.getYear(), dochDate.getMonth(), dochDate.getDayOfMonth()
                    , toTimeMax.getHour(), toTimeMax.getMinute());
        }
        dochsum.setDsToLast(toDateTimeMax);

        dochsum.setDsDov(durToDecNulled(sumOfDovolena()));
        dochsum.setDsNem(durToDecNulled(sumOfNemoc()));
        dochsum.setDsVol(durToDecNulled(sumOfVolno()));
        dochsum.setDsLek(durPracToDecRoundedNulled(sumOfLekar()));
        dochsum.setDsObedMan(durToDecNulled(durObedMan));
        dochsum.setDsObedAut(durToDecNulled(Duration.ZERO.minus(durObedAutMinus))); // Obedy v dochsum chceme mit v '+'
        dochsum.setDsObed(durToDecNulled(durObedMan.minus(durObedAutMinus)));
        dochsum.setObedKratky((null == durObedAutMinus) || (Duration.ZERO.compareTo(durObedAutMinus) != 0));
        dochsum.setDsWork(durToDecNulled(durPrac));
        dochsum.setDsWorkPruh(durPracToDecRounded(durPrac.plus(durObedAutMinus)));
//        dochsum.setDsWorkRed();

        try {
            dochsumService.updateDochsumCloseDoch(dochDate, dochPerson.getId(), dochsum);

        } catch (Exception e) {
            ConfirmDialog.createError()
                    .withCaption("Přenos docházky")
                    .withMessage("Chyba při přenosu docházky.")
                    .open()
            ;
        }
    }

    private void deleteFromDochsumAndOpenDochDay() {
        try {
            dochsumService.deleteDochsumOpenDoch(dochDate, dochPerson.getId());
        } catch (Exception e) {
            ConfirmDialog.createError()
                    .withCaption("Otevření docházky")
                    .withMessage("Chyba při otevírání docházky.")
                    .open()
            ;
        }
    }

//    private LocalTime getFromFirst() {
//        return upperDochList.stream()
//                .mapToInt(doch -> doch.getFromTime())
//                .max().orElseThrow(NoSuchElementException::new);
//                ;
//    }

    private BigDecimal durToDecNulled(Duration dur) {
        BigDecimal durDec = durToDec(dur);
        return (durDec.compareTo(BigDecimal.ZERO) == 0) ? null : durDec;
    }

    private BigDecimal durToDec(Duration dur) {
        if (null == dur) {
            return BigDecimal.ZERO;
        }
        Long hours  = dur.toHours();
        Long minutes = dur.toMinutes() - hours * 60;
        return BigDecimal.valueOf(hours).add(BigDecimal.valueOf((minutes % 60) / 60f));
    }

    private BigDecimal durPracToDecRoundedNulled(Duration dur) {
        BigDecimal durDec = durPracToDecRounded(dur);
        return (durDec.compareTo(BigDecimal.ZERO) == 0) ? null : durDec;
    }

    private BigDecimal durPracToDecRounded(Duration durPrac) {
        if (null == durPrac) {
            return null;
        }
        Long hours  = durPrac.toHours();
        Long minutes = durPrac.toMinutes() - hours * 60;
        BigDecimal hoursDecPart;

        if ((hours == 7 && minutes > 30) && (hours == 8 && minutes < 30)) {
            hours = 8L;
//            minutes = 0L;
            hoursDecPart = BigDecimal.valueOf(0.0);
        } else {
            if (minutes >= 30) {
//                minutes = 30L;
                hoursDecPart = BigDecimal.valueOf(0.5);
            } else {
//                minutes = 0L;
                hoursDecPart = BigDecimal.ZERO;
            }
        }

//        if (minutes < 15) {
//            minutes = 0L;
//            hoursDecPart = BigDecimal.ZERO;
//        } else if (minutes >= 15 && minutes <= 45) {
//            minutes = 30L;
//            hoursDecPart = BigDecimal.valueOf(0.5);
//        } else {
//            minutes = 0L;
//            hoursDecPart = BigDecimal.ZERO;
//            hours = hours + 1;
//        }

        return BigDecimal.valueOf(hours).add(hoursDecPart);
    }

    private LocalTime getDsFromFirst() {
        return upperDochList.stream()
                .filter(doch -> null != doch.getFromTime())
                .map(doch -> doch.getFromTime())
                .min(LocalTime::compareTo)
                .orElse(null)
                ;
    }

    private LocalTime getDsToLast() {
        return upperDochList.stream()
                .filter(doch -> null != doch.getToTime())
                .map(doch -> doch.getToTime())
                .max(LocalTime::compareTo)
                .orElse(null)
                ;
    }

    private Duration sumOfDochPrac() {
        return upperDochList.stream()
                .filter(doch -> null != doch.getDochDur() &&  doch.getCalcprac())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }

    private Duration sumOfDochPracProductive() {
        LocalTime pracProdTimeMin = LocalTime.of(7, 0);
        LocalTime pracProdTimeMax = LocalTime.of(18, 0);
        Duration durPracProd = Duration.ZERO;
        LocalTime timeFromProd;
        LocalTime timeToProd;
        for (Doch doch : upperDochList) {
            if (doch.getCalcprac()) {
                if (null == doch.getFromTime() || null == doch.getToTime()) {
                    // Fixed zaznamy bez od-do; momentalne Sluzebka celodenni (a nove taky Lekar ?)
                    durPracProd = doch.getDochDur();
                    durPracProd.plus(doch.getDochDur());
                } else {
                    if (pracProdTimeMin.compareTo(doch.getFromTime()) > 0) {
                        timeFromProd = pracProdTimeMin;
                    } else {
                        timeFromProd = doch.getFromTime();
                    }
                    if (pracProdTimeMax.compareTo(doch.getToTime()) < 0) {
                        timeToProd = pracProdTimeMax;
                    } else {
                        timeToProd = doch.getToTime();
                    }
                    durPracProd = durPracProd.plus(Duration.between(timeFromProd, timeToProd));
                }
            }
        }
        return durPracProd;
    }

    private Duration sumOfObedStamped() {
        return upperDochList.stream()
                .filter(doch -> Cin.CinKod.MO == doch.getCinCinKod())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }

    private Duration sumOfObedAutMinus() {
        return upperDochList.stream()
                .filter(doch -> Cin.CinKod.OA == doch.getCinCinKod())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }

    private Duration sumOfNemoc() {
        return upperDochList.stream()
                .filter(doch -> Cin.CinKod.ne == doch.getCinCinKod())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }

    private Duration sumOfDovolena() {
        return upperDochList.stream()
                .filter(doch -> Cin.CinKod.dc == doch.getCinCinKod() || Cin.CinKod.dp == doch.getCinCinKod())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }

    private Duration sumOfVolno() {
        return upperDochList.stream()
                // TODO: je nahradni volno vs neplacene  volno ?
                .filter(doch -> Cin.CinKod.nv == doch.getCinCinKod())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }

    private Duration sumOfLekar() {
        return upperDochList.stream()
                .filter(doch -> Cin.CinKod.L == doch.getCinCinKod())
                .map(doch -> doch.getDochDur())
                .reduce(Duration.ZERO, (p, q) -> p.plus(q))
                ;
    }


    private Component initPrenosPersonMonthButton() {
        prenosPersonDateMonthButton = new Button("Přenést měsíc");
        prenosPersonDateMonthButton.setEnabled(false);
        return prenosPersonDateMonthButton;
    }

    private Component initPrenosPersonAllButton() {
        prenosPersonDateAllButton = new Button("Zrušit přenos měsíce");
        prenosPersonDateAllButton.setEnabled(false);
        return prenosPersonDateAllButton;
    }


    private Component initUpperDochGrid() {
        upperDochGrid = new Grid<>();
        upperDochGrid.setHeight("55em");
        upperDochGrid.getStyle()
                .set("margin-top", "0.2em")
        ;
//        upperDochGrid.getElement().getStyle().set("height", height);
//        pruhZakGrid.setHeight("0");
        upperDochGrid.setColumnReorderingAllowed(false);
        upperDochGrid.setClassName("vizman-simple-grid");
        upperDochGrid.setSelectionMode(Grid.SelectionMode.NONE);

//        upperDochGrid.setItemDetailsRenderer(new ComponentRenderer<>(doch -> {
////            Emphasis poznamkaComp = new Emphasis(StringUtils.isBlank(doch.getPoznamka()) ? new Paragraph("") : new Paragraph(doch.getPoznamka()));
//            Emphasis poznamkaComp = new Emphasis(StringUtils.isBlank(doch.getPoznamka()) ? "" : doch.getPoznamka());
//            poznamkaComp.getStyle()
//                    .set("margin-left", "270px")
////                    .set("padding-left", "260px")
////                    .set("width", "300px")
////                    .set("text-align", "start")
//            ;
//            return poznamkaComp;
////            VerticalLayout layout = new VerticalLayout();
////            layout.add(new Label("Address: " + person.getAddress().getStreet()
////                    + " " + person.getAddress().getNumber()));
////            layout.add(new Label("Year of birth: " + person.getYearOfBirth()));
////            return layout;
//        }));
////        pruhZakGrid.setDetailsVisibleOnClick(false);

//        Binder<Doch> upperBinder = new Binder<>(Doch.class);

        upperDochGrid.addColumn(Doch::getDochState)
                .setHeader("St.")
                .setWidth("2em")
                .setFlexGrow(0)
                .setResizable(true)
        ;

//        pruhZakGrid.addColumn(new ComponentRenderer<>(doch -> {
////                pruhZakGrid.setDetailsVisible(doch, StringUtils.isNotBlank(doch.getPoznamka()));
//                pruhZakGrid.setDetailsVisible(doch, true);
//                return new Span("");
//            }))
//            .setFlexGrow(0)
//            .setVisible(false)
//        ;

//        upperDochGrid.addColumn((ValueProvider<Doch, String>) doch -> {
//                upperDochGrid.setDetailsVisible(doch, StringUtils.isNotBlank(doch.getPoznamka()));
//                return "";
//            })
//            .setFlexGrow(0)
//            .setVisible(false)
//        ;

        upperDochGrid.addColumn(fromTimeValProv)
                .setHeader("Od")
                .setWidth("3em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;

        upperDochGrid.addColumn(new ComponentRenderer<>(doch -> {
            // Note: following icons MUST NOT be created outside this renderer (the KontFormDialog cannot be reopened)
            Icon icoManualFlag = new Icon(VaadinIcon.DOT_CIRCLE);
            icoManualFlag.setSize("0.8em");
            icoManualFlag.getStyle().set("theme", "small icon secondary");
            icoManualFlag.setColor("crimson");
            return (null != doch.getFromManual() && doch.getFromManual()) ? icoManualFlag : new Span("");
        }))
//                .setHeader("Man.")
                .setWidth("2em")
                .setTextAlign(ColumnTextAlign.START)
                .setFlexGrow(0)
                .setResizable(false)
        ;

        upperDochGrid.addColumn(toTimeValProv)
                .setHeader("Do")
                .setWidth("3em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;
//        pruhZakGrid.addComponentColumn(new ComponentRenderer<>(doch -> getHodinComponent(doch)))
//        pruhZakGrid.addComponentColumn(doch -> getHodinComponent(doch))

        upperDochGrid.addColumn(new ComponentRenderer<>(doch -> {
            // Note: following icons MUST NOT be created outside this renderer (the KontFormDialog cannot be reopened)
            Icon icon = new Icon(VaadinIcon.DOT_CIRCLE);
            icon.setSize("0.8em");
            icon.getStyle().set("theme", "small icon secondary");
            icon.setColor("crimson");
            return (null != doch.getToManual() && doch.getToManual()) ? icon : new Span("");
        }))
                .setWidth("2em")
                .setTextAlign(ColumnTextAlign.START)
                .setFlexGrow(0)
                .setResizable(false)
        ;

        upperDochGrid.addColumn(durationValProv)
                .setHeader("Hod.")
//                .setFooter("S: " + formatDuration(getDurationSum()))
                .setFooter("")
                .setWidth("3em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setKey(DOCH_UPPER_DURATION_KEY)
                .setResizable(true)
        ;

//        upperDochGrid.addColumn(new ComponentRenderer<>(doch -> {
//            Paragraph cinnostComp = new Paragraph(doch.getCinnost());
//            cinnostComp.getElement().setAttribute("margin-left","20px");
//            return cinnostComp;
//        }))

        upperDochGrid.addColumn(Doch::getCinnost)
                .setHeader("Činnost")
//                .setWidth("4em")
//                .setFooter("")
                .setFlexGrow(1)
                .setKey(DOCH_UPPER_CINNOST_KEY)
                .setResizable(true);


        upperDochGrid.addColumn(TemplateRenderer.<Doch> of(
                "<div><i>[[item.poznamka]]</i></div>")
                .withProperty("poznamka", Doch::getPoznamka)
            )
            .setHeader("Poznámka")
            .setFlexGrow(2)
            .setKey(DOCH_UPPER_POZNAMKA_KEY)
            .setResizable(true)
        ;

        return upperDochGrid;
    }

    private Duration getDurationSum() {

        Duration durSum = Duration.ZERO;
        for (Doch doch : upperDochList) {
            if (null != doch && null != doch.getDochDur() && doch.getCalcprac()) {
                durSum = durSum.plus(doch.getDochDur());
            }
        }
        return durSum;

//        return pruhZakList.stream()
//                .map(Doch::getSignedDochDur)
//                .reduce(
//                        Duration.ZERO,
//                        (a, b) ->
////                        {
////                            if (null == b) {
////                                if (a.)return a;
////                            } else {
////
////                            }
//                            (null == b) ? a : a.plus(b));
    }

    private Component initLowerDochGrid() {
        lowerDochGrid = new Grid<>();
//        lowerDochGrid.setHeight("30em");
        lowerDochGrid.setColumnReorderingAllowed(false);
        lowerDochGrid.setClassName("vizman-simple-grid");
        lowerDochGrid.setSelectionMode(Grid.SelectionMode.NONE);
        lowerDochGrid.getStyle()
                .set("margin-bottom", "1em")
        ;

//        lowerDochGrid.setItemDetailsRenderer(new ComponentRenderer<>(doch -> {
//            Emphasis poznamkaComp = new Emphasis(StringUtils.isBlank(doch.getPoznamka()) ? "" : doch.getPoznamka());
//            poznamkaComp.getStyle()
//                    .set("margin-left", "270px")
//            ;
//            return poznamkaComp;
//        }));

        lowerDochGrid.addColumn(Doch::getDochState)
                .setHeader("St.")
                .setWidth("2em")
                .setFlexGrow(0)
                .setResizable(true)
        ;

        lowerDochGrid.addColumn((ValueProvider<Doch, String>) doch -> {
            lowerDochGrid.setDetailsVisible(doch, StringUtils.isNotBlank(doch.getPoznamka()));
            return "";
        })
                .setFlexGrow(0)
                .setVisible(false)
        ;

        lowerDochGrid.addColumn(fromTimeValProv)
                .setHeader("Od")
                .setWidth("3em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;

        lowerDochGrid.addColumn(new ComponentRenderer<>(doch -> {
            // Note: following icons MUST NOT be created outside this renderer (the KontFormDialog cannot be reopened)
            Icon icoManualFlag = new Icon(VaadinIcon.DOT_CIRCLE);
            icoManualFlag.setSize("0.8em");
            icoManualFlag.getStyle().set("theme", "small icon secondary");
            icoManualFlag.setColor("crimson");
            return (null != doch.getFromManual() && doch.getFromManual()) ? icoManualFlag : new Span("");
        }))
                .setWidth("2em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(false)
        ;

        lowerDochGrid.addColumn(toTimeValProv)
                .setHeader("Do")
                .setWidth("3em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;

        lowerDochGrid.addColumn(new ComponentRenderer<>(doch -> {
            // Note: following icons MUST NOT be created outside this renderer (the KontFormDialog cannot be reopened)
            Icon icon = new Icon(VaadinIcon.DOT_CIRCLE);
            icon.setSize("0.8em");
            icon.getStyle().set("theme", "small icon secondary");
            icon.setColor("crimson");
            return (null != doch.getToManual() && doch.getToManual()) ? icon : new Span("");
        }))
                .setWidth("2em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(false)
        ;

        lowerDochGrid.addColumn(durationValProv)
                .setHeader("Hod.")
//                .setFooter("")
                .setWidth("3em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setKey(DOCH_LOWER_DURATION_KEY)
                .setResizable(true)
        ;

        lowerDochGrid.addColumn(Doch::getCinnost)
                .setHeader("Činnost")
//                .setFooter("")
                .setFlexGrow(1)
                .setKey(DOCH_LOWER_CINNOST_KEY)
                .setResizable(true);


        lowerDochGrid.addColumn(TemplateRenderer.<Doch> of(
                "<div><i>[[item.poznamka]]</i></div>")
                .withProperty("poznamka", Doch::getPoznamka)
        )
                .setHeader("Poznámka")
                .setFlexGrow(2)
                .setKey(DOCH_LOWER_POZNAMKA_KEY)
                .setResizable(true)
        ;

        return lowerDochGrid;
    }

//    public static HtmlComponent getCasOdComponent(Doch doch) {
//        return new Paragraph(null == doch.getFromTime() ? "" : doch.getFromTime().format( VzmFormatUtils.shortTimeFormatter));
//    }
//
//    public static HtmlComponent getCasDoComponent(Doch doch) {
//        return new Paragraph(null == doch.getToTime() ? "" : doch.getToTime().format( VzmFormatUtils.shortTimeFormatter));
//    }
//
//    public static HtmlComponent getHodinComponent(Doch doch) {
////        return new Paragraph(null == doch.getDochDuration() ? "" : doch.getDochDuration().format( VzmFormatUtils.shortTimeFormatter));
////        return new Paragraph(null == doch.getDochDurationUI() ? "" : formatDuration(doch.getDochDurationUI()));
//        return new Paragraph(null == doch.getDochDur() ? "" : formatDuration(doch.getDochDur()));
//    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    private void updateUpperDochGridPane(final Person dochPerson, final LocalDate dochDate) {
        loadUpperDochGridData(dochPerson, dochDate);
        setDochNaviButtonsEnabled(null != dochPerson && null != dochDate);
        upperDochDateInfo.setText(null == dochDate ? "" : dochDate.format(upperDochDateHeaderFormatter));
    }

    private void updateLowerDochGridPane(final Person dochPerson, final LocalDate dochDate) {
        LocalDate prevDochDate = null;
        if (null != dochPerson) {
            prevDochDate = dochService.findPrevDochDate(dochPerson.getId(), dochDate);
        }

        loadLowerDochGridData(dochPerson, prevDochDate);
        lowerDochDateInfo.setText(null == prevDochDate ? "" : prevDochDate.format(lowerDochDateHeaderFormatter));
    }


    private Component getDurationFooter(Duration durationSum) {
        return new Paragraph(null == durationSum ?
                null : LocalTime.MIDNIGHT.plus(durationSum).format(DateTimeFormatter.ofPattern("H:mm")));
    }


    private Component getDurationLabelFooter() {
        Paragraph comp = new Paragraph("(odpracováno)");
        comp.getStyle().set("text-align", "start");
        return comp;
    }

    private void loadUpperDochGridData(Person dochPerson, LocalDate dochDate) {
        if (null == dochPerson || null == dochPerson.getId() || null == dochDate) {
            upperDochList = new ArrayList<>();
        } else {
            upperDochList = dochService.fetchDochForPersonAndDate(dochPerson.getId(), dochDate);
        }
        upperDochGrid.setItems(upperDochList);
//        for (Doch doch : pruhZakList) {
//            pruhZakGrid.setDetailsVisible(doch, StringUtils.isNotBlank(doch.getPoznamka()));
//        }
        upperDochGrid.getColumnByKey(DOCH_UPPER_DURATION_KEY)
                .setFooter(getDurationFooter(getDurationSum()));
        upperDochGrid.getColumnByKey(DOCH_UPPER_CINNOST_KEY)
                .setFooter(getDurationLabelFooter());

        upperDochGrid.getDataProvider().refreshAll();
    }

    private void loadLowerDochGridData(Person dochPerson, LocalDate dochDate) {
        if (null == dochPerson || null == dochPerson.getId() || null == dochDate) {
            lowerDochList = new ArrayList<>();
        } else {
            lowerDochList = dochService.fetchDochForPersonAndDate(dochPerson.getId(), dochDate);
        }
        lowerDochGrid.setItems(lowerDochList);

//        lowerDochGrid.getColumnByKey(DOCH_LOWER_DURATION_KEY)
//                .setFooter(getDurationFooter(getDurationSum()));
//        lowerDochGrid.getColumnByKey(DOCH_LOWER_CINNOST_KEY)
//                .setFooter(getDurationLabelFooter());

        lowerDochGrid.getDataProvider().refreshAll();
    }


//    private void XXstampPrichod(final LocalDateTime currentDateTime) {
//        if (!canStampPrichod()) {
//            return;
//        }
//        Doch firstDochPrich = new Doch(
//                dochDate
//                , dochPerson
//                , cinRepo.findByCinKod(Cin.CinKod.P)
//                , currentDateTime.toLocalTime()
//                , currentDateTime
//                , false
//                , null
//        );
//        pruhZakList.add(0, dochService.openFirstRec(firstDochPrich));
//        updateUpperDochGridPane(dochPerson, dochDate);
//        pruhZakGrid.getDataProvider().refreshAll();
//    }

//    private void stampPrichodAndNew(final LocalDateTime dochStamp) {
//        if (!canStampPrichod()) {
//            return;
//        }
//        Doch newDochPrich = new Doch(cinRepo.findByCinKod(Cin.CinKod.P), dochPerson, dochDate, dochStamp);
//        pruhZakList.add(0, dochService.closePrevZkDochAndOpenNew(newDochPrich));
//        updateUpperDochGridPane(dochPerson, dochDate);
//        pruhZakGrid.getDataProvider().refreshAll();
//    }

    private void stampPrichodAndNewInsideRec(
            final LocalDateTime currentDateTime
            , final Cin.CinKod prichodWhereKod
    ) {
        if (!canStampPrichod()) {
            return;
        }

        Doch newInsideRec = new Doch(
                dochDate
                , dochPerson
                , cinRepo.findByCinKod(prichodWhereKod)
                , currentDateTime.toLocalTime()
                , currentDateTime
                , false
                , null
        );

        Doch recToClose = getLastZkDochRec();
        if (null == recToClose) {
            dochService.openFirstRec(newInsideRec);
        } else {
            recToClose.setToTime(currentDateTime.toLocalTime());
            recToClose.setToModifDatetime(currentDateTime);
            dochService.closeRecAndOpenNew(recToClose, newInsideRec);
        }
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();

//        pruhZakList.add(0, dochService.closePrevZkDochAndOpenNew(newDochPrich));
//        stampDochManualFromDialog(newInsideRec, Operation.STAMP_PRICH);
//        dochService.closePrevZkDochAndOpenNew(newDochPrich);
//        updateUpperDochGridPane(dochPerson, dochDate);
//        pruhZakGrid.getDataProvider().refreshAll();
    }

    private void stampPrichodAltAndNewInsideRec(
            final LocalDateTime stampDateTime
            , final Cin.CinKod prichodWhereKod
    ) {
        if (!canStampPrichodAlt()) {
            return;
        }

        DochManual dochManual = new DochManual(
                dochDate
                , cinRepo.findByCinKod(prichodWhereKod)
                , stampDateTime.toLocalTime()
                , null
                , null
        );
        dochFormDialog.openDialog(
                dochManual
                , Operation.STAMP_PRICH_MAN
                , "Příchod jiný čas"
                , true
                , false
        );
//        pruhZakList.add(0, dochService.closePrevZkDochAndOpenNew(newDochPrich));
//        updateUpperDochGridPane(dochPerson, dochDate);
//        pruhZakGrid.getDataProvider().refreshAll();
    }


    private void stampSingleRecord(final LocalDateTime currentDateTime, Cin.CinKod cinKod) {
//        if (checkDochNotContainsRec("nelze provést požadovanou akci.")) {
//            return;
//        }
        if (!canStampStandaloneRec()) {
            return;
        }
        Doch singleRec = new Doch(
                dochDate
                , dochPerson
                , cinRepo.findByCinKod(cinKod)
                , currentDateTime.toLocalTime()
                , currentDateTime
                , false
                , null

        );
        singleRec.setDochDur(Duration.ofHours(8));
        dochService.openFirstRec(singleRec);
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }

    private void stampSingleObedAuto(Duration durObedAuto) {
//        if (!canStampStandaloneRec()) {
//            return;
//        }

        Doch singleRec = Doch.createSingleFixed (
                dochDate
                , dochPerson
                , Doch.STATE_KONEC
                , cinRepo.findByCinKod(Cin.CinKod.OA)
                , durObedAuto
                , null
        );

        dochService.addSingleRec(singleRec);
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }


    private void stampDovolenaHalf(final LocalDateTime currentDateTime, Cin.CinKod cinKod) {
//        if (!checkDochContainsRec(", nelze provést požadovanou akci.")) {
//            return;
//        }
        Doch standaloneRec = new Doch(
                dochDate
                , dochPerson
                , cinRepo.findByCinKod(cinKod)
                , currentDateTime.toLocalTime()
                , currentDateTime
                , false
                , null

        );
        if (Cin.CinKod.dp == cinKod) {
            standaloneRec.setDochDur(Duration.ofHours(4));
            dochService.openFirstRec(standaloneRec);
            updateUpperDochGridPane(dochPerson, dochDate);
            upperDochGrid.getDataProvider().refreshAll();
        } else {
            ConfirmDialog
                    .createWarning()
                    .withCaption("Záznam docházky")
                    .withMessage("Nelze provést, je očekávána půldenní dovolená")
                    .withOkButton()
                    .open();
        }
    }



    private void stampSingleManualRecord(final LocalDateTime currentDateTime, Cin.CinKod cinKod) {
//        if (checkDochNotContainsRec("nelze provést požadovanou akci.")) {
//            return;
//        }
        if (!canStampStandaloneRec()) {
            return;
        }

//        Doch standaloneRec = new Doch(
//                dochDate
//                , dochPerson
//                , cinRepo.findByCinKod(cinKod)
//                , currentDateTime.toLocalTime()
//                , currentDateTime
//                , false
//                , null
//
//        );

        // Currently only sluzebka 8:30
        if (Cin.CinKod.SC == cinKod) {
            DochManual dochManual = new DochManual(
                    dochDate
                    , cinRepo.findByCinKod(cinKod)
                    , currentDateTime.toLocalTime()
                    , null
                    , cinKod
            );
            dochFormDialog.openDialog(
                    dochManual
                    , Operation.STAMP_SINGLE
                    , "Služebka"
                    , false
                    , false
            );
        }

//        dochService.openFirstRec(standaloneRec);
//        updateUpperDochGridPane(dochPerson, dochDate);
//        upperDochGrid.getDataProvider().refreshAll();
    }


    private void stampOdchod(final LocalDateTime currentDateTime) {
        if (!canStampOdchod()) {
            return;
        }

        Doch lastZkDochRec = getLastZkDochRec();
        if (null != lastZkDochRec) {
            lastZkDochRec.setToTime(currentDateTime.toLocalTime());
            lastZkDochRec.setToModifDatetime(currentDateTime);
            dochService.closeLastRec(lastZkDochRec);

            updateUpperDochGridPane(dochPerson, dochDate);
            upperDochGrid.getDataProvider().refreshAll();
        }

    }

    private void stampOdchodAlt(
            final LocalDateTime currentDateTime
    ) {
        if (!canStampOdchodAlt()) {
            return;
        }
        Doch lastZkDochRec = getLastZkDochRec();
        if  (null != (lastZkDochRec)) {
            DochManual dochOdchManual = new DochManual(
                    dochDate
                    , cinRepo.findByCinKod(lastZkDochRec.getCinCinKod())
                    , lastZkDochRec.getFromTime()
                    , currentDateTime.toLocalTime()
                    , null
            );
            dochFormDialog.openDialog(
                    dochOdchManual
                    , Operation.STAMP_ODCH_MAN_LAST
                    , "Odchod jiný čas"
                    , false
                    , true
            );
        }
    }

    private void stampOdchodAndNewOutsideRec(final LocalDateTime currentDateTime, Cin.CinKod odchodWhereKod) {
        if (!canStampOdchod()) {
            return;
        }
        Doch newOutsideRec = new Doch(
                dochDate
                , dochPerson
                , cinRepo.findByCinKod(odchodWhereKod)
                , currentDateTime.toLocalTime()
                , currentDateTime
                , false
                , null
        );
//        stampDochManualFromDialog(newOutsideRec, Operation.STAMP_ODCH);

        Doch lastInsideRec = getLastZkDochRec();
        if (null == lastInsideRec) {
            lastInsideRec.setToTime(currentDateTime.toLocalTime());
            lastInsideRec.setToModifDatetime(currentDateTime);
            dochService.closeRecAndOpenNew(lastInsideRec, newOutsideRec);
        }

        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }

    private void stampOdchodAltAndNewOutsideRec(
            final LocalDateTime currentDateTime
            , Cin.CinKod odchodWhereKod
            , String dialogTitle
            , boolean fromTimeIsEditable
            , boolean toTimeIsEditable
    ) {
        if (!canStampOdchodAlt()) {
            return;
        }
//        Cin newCin = cinRepo.findByCinKod(odchodWhereKod);
//        Doch lastZkDochRec = getLastZkDochRec();
        DochManual dochOdchManual = new DochManual(
                dochDate
                , cinRepo.findByCinKod(odchodWhereKod)
                , currentDateTime.toLocalTime()
                , null
                , odchodWhereKod
        );
        dochFormDialog.openDialog(
                dochOdchManual
                , Operation.STAMP_ODCH_MAN
                , dialogTitle
                , fromTimeIsEditable
                , toTimeIsEditable
        );


//        Doch newDochOdch = new Doch(cinRepo.findByCinKod(odchodWhereKod), dochPerson, dochDate, dochStamp);
//        Doch newSavedDoch = dochService.closePrevZkDochAndOpenNew(newDochOdch);
//
////        pruhZakList.add(0, newSavedDoch);
//        updateUpperDochGridPane(dochPerson, dochDate);
//        pruhZakGrid.getDataProvider().refreshAll();
    }

//    private boolean canRecordFirstPrichod() {
//        return checkDochDateIsToday()
//                && checkDayDochIsEmpty()
//        ;
//    }

    private boolean canStampOdchod() {
        return checkDayDochIsOpened("nelze editovat.")
                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
                && checkPersonIsInOffice("nelze zaznamenat odchod.")
        ;
    }

    private boolean canStampOdchodAlt() {
        return checkDayDochIsOpened("nelze editovat.")
//                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
                && checkPersonIsInOffice("nelze zaznamenat odchod.")
        ;
    }

    private boolean canStampPrichod() {
        return checkDayDochIsOpened(", nelze editovat.")
                && checkDochDateIsToday(", je třeba použít 'Příchod jiný čas'.")
                && checkPersonIsOutOfOffice(", nelze zaznamenat další příchod.")
        ;
    }

    private boolean canStampPrichodAlt() {
        return checkDayDochIsOpened("nelze editovat.")
//                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
                && checkPersonIsOutOfOffice("nelze zaznamenat další příchod.")
        ;
    }

    private boolean canStampStandaloneRec() {
        return checkDayDochIsOpened(", nelze editovat.")
                && checkDochNotContainsRec("nelze provést požadovanou akci.")
                ;
    }

    private boolean canRemoveDochRec() {
        return checkDayDochIsOpened("nelze editovat.")
                && checkDochPersonIsSelected("nelze rušit záznamy")
                && checkDochDateIsSelected("nelze rušit záznamy")
                && checkDochHasRecords("není co rušit")
                && checkZkDochRecToDelExists()
        ;
    }

    private boolean canRemoveAllDochRecs() {
        return checkDayDochIsOpened("nelze editovat.")
                && checkDochPersonIsSelected("nelze rušit záznamy")
                && checkDochDateIsSelected("nelze rušit záznamy")
                && checkDochHasRecords("není co rušit")
        ;
    }

    private boolean checkDayDochIsOpened(final String additionalMsg) {
        if (upperDochList.stream().noneMatch(Doch::isClosed)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka je uzavřena%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochDateIsSelected(final String additionalMsg) {
        if (null != dochDate) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Datum docházky")
                .withMessage(String.format("Není vybráno datum docházky%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochPersonIsSelected(final String additionalMsg) {
        if (null != dochPerson) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Osoba docházky")
                .withMessage(String.format("Není vybrána osoba docházky%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochHasRecords(final String additionalMsg) {
        if (dochHasRecords()) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznamy docházky")
                .withMessage(String.format("V docházce není žádný záznam%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochDateIsToday(final String additionalMsg) {
        if (null != dochDate && dochDate.equals(LocalDate.now())) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Datum docházky")
                .withMessage(String.format("Docházka není dnešní%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkPersonIsOutOfOffice(final String additionalMsg) {
        boolean hasNoRecs = !dochHasRecords();
        boolean personIsOut = personIsOutOfOffice();

        if (hasNoRecs || personIsOut) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Osoba je evidována na pracovišti%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkPersonIsInOffice(final String additionalMsg) {
        if (personIsInOffice()) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Osoba není evidována na pracovišti%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkZkDochRecToDelExists() {
//        Doch lastZkDochRec = getLastZkDochRec();
        Doch lastZkDochRec = getLastDochRec();
        if (lastZkDochRec != null) {
            return true;
        }
        ConfirmDialog.createInfo()
                .withCaption("Záznam docházky")
                .withMessage("Nenalezen žádný záznam ke zrušení")
                .withOkButton()
                .open()
        ;
        return false;
    }

    private boolean checkDochContainsRec(final String additionalMsg) {
        if (!CollectionUtils.isEmpty(upperDochList)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka neobsahuje záznamy%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochNotContainsRec(final String additionalMsg) {
        if (CollectionUtils.isEmpty(upperDochList)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka obsahuje záznamy%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }


    private boolean checkDochContainsNemoc(final String additionalMsg) {
        if (upperDochList.stream().anyMatch(Doch::isNemoc)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka neobsahuje nemoc%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochContainsSluzebka(final String additionalMsg) {
        if (upperDochList.stream().anyMatch(Doch::isSluzebka)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka neobsahuje služebku%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkDochContainsDovolena(final String additionalMsg) {
        if (upperDochList.stream().anyMatch(Doch::isDovolenaFull)
                || upperDochList.stream().anyMatch(Doch::isDovolenaHalf)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka neobsahuje dovolenou %s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }


    private boolean checkDochContainsNahradniVolno(final String additionalMsg) {
        if (upperDochList.stream().anyMatch(Doch::isNahradniVolno)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Docházka obsahuje náhradní volno%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkLastDochRecIsPrichod(final String additionalMsg) {
        Doch lastZkDoch = getLastZkDochRec();
        if ((null != lastZkDoch) && (null == lastZkDoch.getToTime())) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Poslední záznam v docházce není příchod%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private String adjustAdditionalMsg(final String additionalMsg) {
        return StringUtils.isBlank(additionalMsg) ? "." : " - " + additionalMsg;
    }

    private boolean dochHasRecords() {
        return upperDochList.size() > 0;
    }

    private boolean personIsOutOfOffice() {
        Doch lastZkDochRec = getLastZkDochRec();
        return ((null == lastZkDochRec)
                || ((Cin.CinKod.P.equals(lastZkDochRec.getCinCinKod()))
                    && (null != lastZkDochRec.getToTime()))
                || ((!Cin.CinKod.P.equals(lastZkDochRec.getCinCinKod()))
                    && (null == lastZkDochRec.getToTime()))
        );
    }

    private boolean personIsInOffice() {
        Doch lastZkDochRec = getLastZkDochRec();
        return ((null != lastZkDochRec)
                && (((Cin.CinKod.P.equals(lastZkDochRec.getCinCinKod()))
                        && (null == lastZkDochRec.getToTime()))
                    || ((!Cin.CinKod.P.equals(lastZkDochRec.getCinCinKod()))
                        && (null == lastZkDochRec.getToTime()))
                   )
        );
    }

    private Doch getLastDochRec() {
        // use ListIterator to iterate List in reverse order
//        ListIterator<Doch> dochReversedTimeIter = pruhZakList.listIterator(pruhZakList.size());

        // hasPrevious() returns true if the list has previous element
//        while (dochReversedIter.hasPrevious()) {
        return upperDochList.get(0);
    }

    private Doch getLastZkDochRec() {
        // use ListIterator to iterate List in reverse order
//        ListIterator<Doch> dochReversedTimeIter = pruhZakList.listIterator(pruhZakList.size());

        // hasPrevious() returns true if the list has previous element
//        while (dochReversedIter.hasPrevious()) {
        for (Doch doch : upperDochList) {
            if (doch.isZk()) {
                return doch;
            }
        }
        return null;
    }

    private Doch getLastDoch() {
        return upperDochList.get(upperDochList.size() - 1);
    }

    private void odchodRadioChanged(HasValue.ValueChangeEvent event) {
//        System.out.println("--------------- odchod radio changed");
//        System.out.println(event.toString());
        if (null == event.getValue()) {
            odchodAltButton.setEnabled(false);
            odchodButton.setEnabled(false);
        } else {
            odchodAltButton.setEnabled(true);
            odchodButton.setEnabled(true);
        }
    }

//    private void odchodButtonClicked(ClickEvent event) {
//        resetOdchodRadio();
//    }
//
//    private void odchodAltButtonClicked(ClickEvent event) {
//        resetOdchodRadio();
//    }


    private static class TimeThread extends Thread {
        private final Logger LOG = LoggerFactory.getLogger(getClass());

        private final UI ui;
        private final DochView dochView;

        TimeThread(UI ui, DochView dochView) {
            this.ui = ui;
            this.dochView = dochView;
        }

        @Override
        public void run() {
            try {
                // Update the data for a while
                while (true) {
                    // Sleep to emulate background work
                    Thread.sleep(1000);
//                    String message = "This is update " + count++;

//                    ui.access(() -> dochForm.view.add(new Span(message)));
                    ui.access(dochView::updateDochClockTime);
                }

//                // Inform that we are done
//                ui.access(() -> {
//                    view.add(new Span("Done updating"));
//                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.debug("Doch clock timer interrupted. Orig.message: {}", e.getMessage() );
            }
        }
    }
}
