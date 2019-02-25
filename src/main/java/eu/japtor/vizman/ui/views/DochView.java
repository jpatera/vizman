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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.service.DochService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_DOCH;

@Route(value = ROUTE_DOCH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.DOCH_USE
})
@SpringComponent
@UIScope
//@Push
// public class DochView extends VerticalLayout implements HasLogger, BeforeEnterListener {
public class DochView extends VerticalLayout implements HasLogger, BeforeEnterListener {

    private static final String DOCH_DURATION_KEY = "doch-duration-key";
    private static final String DOCH_CINNOST_KEY = "doch-cinnost-key";
    private List<Person> dochPersonList;
    private ComboBox<Person> dochPersonCombo;
    private DatePicker dochDatePicker;
    private static final Locale czLocale = new Locale("cs", "CZ");

    private Button dochMonthReportBtn = new Button();
    private Button dochYearReportBtn = new Button();

    private HorizontalLayout dochHeader = new HorizontalLayout();

    FormLayout dochControl = new FormLayout();
    FormLayout nepritControl = new FormLayout();

    VerticalLayout clockContainer;
    private Span clockDisplay = new Span();

    //    private Span upperDochDateInfo = new Span();
    private Paragraph upperDochDateInfo;
    private Span dochLowerDateInfo = new Span();

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

    private Component initVolnoButton() {
        volnoButton = new Button("Neplacené volno (8h)");
        volnoButton.getElement().setAttribute("theme", "primary");
        return volnoButton;
    }

    private Component initVolnoZrusButton() {
        volnoZrusButton = new Button("Zrušit neplac.volno");
        volnoZrusButton.getStyle()
                .set("color", "crimson")
        ;
        return volnoZrusButton;
    }

    private Component initNemocButton() {
        nemocButton = new Button("Nemoc (8h)");
        nemocButton.getElement().setAttribute("theme", "primary");
        return nemocButton;
    }

    private Component initNemocZrusButton() {
        nemocZrusButton = new Button("Zrušit nemoc");
        nemocZrusButton.getStyle()
                .set("color", "crimson")
        ;
        return nemocZrusButton;
    }

    private Component initDovolenaButton() {
        dovolenaButton = new Button("Dovolená (8h)");
        dovolenaButton.getElement().setAttribute("theme", "primary");
        return dovolenaButton;
    }

    private Component initDovolenaHalfButton() {
        dovolenaHalfButton = new Button("Dovolená (4h)");
        return dovolenaHalfButton;
    }

    private Component initDovolenaZrusButton() {
        dovolenaZrusButton = new Button("Zrušit dovolenou");
        dovolenaZrusButton.getStyle()
                .set("color", "crimson")
        ;
        return dovolenaZrusButton;
    }

    private Component initSluzebkaButton() {
        sluzebkaButton = new Button("Služebka (8h)");
        sluzebkaButton.getElement().setAttribute("theme", "primary");
        return sluzebkaButton;
    }

    private Component initSluzebkaZrusButton() {
        sluzebkaZrusButton = new Button("Zrušit služebku");
        sluzebkaZrusButton.getStyle()
                .set("color", "crimson")
        ;
        return sluzebkaZrusButton;
    }

    HorizontalLayout upperDochHeader;
    HorizontalLayout upperDochFooterBar;
    Grid<Doch> upperDochGrid;
    List<Doch> upperDochList = new ArrayList<>();
//    HorizontalLayout dochRecUpperFooter = new HorizontalLayout();

    HorizontalLayout dochRecLowerHeader = new HorizontalLayout();
    Grid<Doch> lowerDochGrid;
    List<Doch> lowerDochList;
    HorizontalLayout dochRecLowerFooter = new HorizontalLayout();


    private Person dochPerson;
    private LocalDate dochDate;
    private LocalDate dochDatePrev;

    Clock minuteClock = Clock.tickMinutes(ZoneId.systemDefault());
    private TimeThread timeThread;
    DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter upperDochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE");
    DateTimeFormatter lowerDochDateHeaderFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy, EEEE");

//    @Autowired
//    public DochRepo kontRepo;

    @Autowired
    public PersonService personService;

    @Autowired
    public CinRepo cinRepo;

    @Autowired
    public DochService dochService;



    //    @Autowired
//    public DochForm(Person dochPerson) {
    public DochView() {
//        super();
        buildForm();
//        initDochData(dochPerson, dochDate);
    }

    @PostConstruct
    public void init() {
        initDochData();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {

        getLogger().info("## ON ATTACH DochView ##");

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        dochDatePicker.setLocale(new Locale("cs", "CZ"));
        dochDatePicker.setLocale(czLocale);

        dochDatePrev = LocalDate.now().minusDays(1);
        dochLowerDateInfo.setText(dochDatePrev.format(lowerDochDateHeaderFormatter));

        timeThread = new TimeThread(attachEvent.getUI(), this);
        timeThread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        getLogger().info("## ON DETACH DochView ##");

        timeThread.interrupt();
        timeThread = null;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }



    public void initDochData() {
        getLogger().info("## Initializing doch data");

        odchodRadio.setItems(cinRepo.getCinsForDochOdchodRadio());

        initPersonList();
//        initDochCallendar();

        if (null == dochPerson) {
            dochPerson = dochPersonList.stream()
                    .filter(person -> person.getUsername().toLowerCase().equals("vancik"))
                    .findFirst().orElse(null);
            dochPersonCombo.setValue(dochPerson);
        }

        if (null == dochDate) {
//            dochDate = LocalDate.of(2019, 1, 15);
            dochDate = LocalDate.now();
            dochDatePicker.setValue(dochDate);
        }
        updateUpperDochGridPane(dochPerson, dochDate);
    }

    private void initPersonList() {
        dochPersonList = personService.fetchAllActive();
        dochPersonCombo.setItems(dochPersonList);
    }


    //    public void updateDochClockTime(LocalTime time) {
    public void updateDochClockTime() {
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
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setWidth("1200px");
        this.setAlignSelf(Alignment.CENTER);

        dochMonthReportBtn.setText("Měsíční přehled");
//        dochMonthReportBtn.setWidth("100%");

//        dochYearReportBtn = new Button("Roční přehled");
        dochYearReportBtn.setText("Roční přehled");
//        dochYearReportBtn.setWidth("100%");

        H3 dochTitle = new H3("DOCHÁZKA");
        dochTitle.getStyle()
                .set("margin-top", "10px")
                .set("margin-left", "20px")
        ;

//        Paragraph dochTitle = new Paragraph("DOCHÁZKA");
//        dochTitle.getStyle()
//                .set("font-size", "var(--lumo-font-size-xl)");
//
//        HorizontalLayout dochHeader = new HorizontalLayout();
        dochHeader.add(
                dochTitle
//                new H4("Uživatel(ka): ")
                , initPersonCombo()
//                , initDochDatePicker()
                , dochMonthReportBtn
                , dochYearReportBtn)
        ;


//        dochControl = new FormLayout();
//        dochControl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
//        dochControl.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("15em", 2));
        dochControl.setWidth("30em");
        dochControl.getStyle()
                .set("margin-top", "0.3em");
//        dochControl.add(initDochDatePicker());
        dochControl.add(
                initClockComponent()
                , initPrichodButton()
                , initPrichodAltButton()
                , buildVertSpace()
                , buildVertSpace()
                , initOdchodRadio()
                , initOdchodButton()
                , initOdchodAltButton()
        );


        nepritControl.setWidth("30em");
        nepritControl.getStyle().set("margin-top", "4.2em");
        nepritControl.add(
                initDovolenaButton()
                , initDovolenaHalfButton()
                , initDovolenaZrusButton()
                , buildVertSpace()
                , buildVertSpace()
                , initSluzebkaButton()
                , initSluzebkaZrusButton()
                , buildVertSpace()
                , buildVertSpace()
                , initNemocButton()
                , initNemocZrusButton()
                , buildVertSpace()
                , buildVertSpace()
                , initVolnoButton()
                , initVolnoZrusButton()
        );

        dochLowerDateInfo.setText("Předchozí den docházky...");

        dochRecLowerHeader.getStyle()
                .set("margin-top", "2em");
        dochRecLowerHeader.add(dochLowerDateInfo);

        VerticalLayout dochRecPane = new VerticalLayout();
        dochRecPane.add(initUpperDochHeaderBar());
        dochRecPane.add(initUpperDochGrid());
        dochRecPane.add(initUpperDochFooterBar());

        dochRecPane.add(dochRecLowerHeader);
        dochRecPane.add(initLowerDochGrid());
        dochRecPane.add(dochRecLowerFooter);

        HorizontalLayout dochPanel = new HorizontalLayout();
        dochPanel.add(dochControl);
        dochPanel.add(dochRecPane);
        dochPanel.add(nepritControl);

        this.add(dochHeader, dochPanel);
    }


    private Component initClockComponent() {
        clockContainer = new VerticalLayout();
        clockContainer.setSizeFull();
        clockContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        clockContainer.setFlexGrow(1);
//        clockContainer.getElement()
//            .setAttribute("colspan","2")
//                .set("width", "stretch")
//                .set("align", "center")
    ;
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
                .set("font-variant-numeric", "tabular-nums");
//                .set("padding-right", "0.75em")
        ;

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

    private Component initPersonCombo() {
        dochPersonCombo = new ComboBox();
        dochPersonCombo.setLabel(null);
        dochPersonCombo.setWidth("20em");
        dochPersonCombo.setItems(new ArrayList<>());
        dochPersonCombo.setItemLabelGenerator(this::getPersonLabel);
        dochPersonCombo.addValueChangeListener(event -> {
            dochPerson = event.getValue();
            updateUpperDochGridPane(dochPerson, dochDate);
        });
        dochPersonCombo.addBlurListener(event -> {
//            loadUpperDochGridData(dochPerson.getId(), dochDate);
            upperDochGrid.getDataProvider().refreshAll();
        });
        return dochPersonCombo;
    }

    private Component initDochDatePicker() {
        dochDatePicker = new DatePicker();
        dochDatePicker.setLabel(null);
        dochDatePicker.setWidth("10em");
//        dochDatePicker.getStyle().set("margin-right", "1em");
        dochDatePicker.addValueChangeListener(event -> {
            dochDate = event.getValue();
            updateUpperDochGridPane(dochPerson, dochDate);
        });
        return dochDatePicker;
    }


    private Component initPrichodButton() {
        prichodBtn = new Button("Příchod");
        prichodBtn.getElement().setAttribute("theme", "primary");
        prichodBtn.addClickListener(event -> {
                LocalDateTime dochStamp = LocalDateTime.now(minuteClock);
                stampPrichodAndNew(dochStamp);
        });
        return prichodBtn;
    }

    private Button initPrichodAltButton() {
        prichodAltBtn = new Button("Příchod jiný čas");
        prichodAltBtn.getElement().setAttribute("theme", "secondary");
        return prichodAltBtn;
    }

    private RadioButtonGroup<Cin> initOdchodRadio() {
        odchodRadio = new RadioButtonGroup<>();
//        odchodRadio.setItems("Odchod na oběd", "Odchod pracovně", "Odchod k lékaři", "Ukončení/přerušení práce");
        odchodRadio.setRenderer(new ComponentRenderer<>(cin -> new Span(cin.getAkce())));
        odchodRadio.addValueChangeListener(event -> odchodRadionChanged(event));
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
            Cin.CinKod odchodWhereKod = odchodRadio.getValue().getCinKod();
            odchodButtonClicked(event);
            LocalDateTime dochStamp = LocalDateTime.now(minuteClock);
            if (Cin.CinKod.KP == odchodWhereKod || Cin.CinKod.XD == odchodWhereKod) {
                stampAndCloseOdchod();
            } else {
                stampOdchodAndOpenNew(dochStamp, odchodWhereKod);
            }
        });
        odchodButton.setEnabled(false);
        return odchodButton;
    }

    private Button initOdchodAltButton() {
        odchodAltButton = new Button("Odchod jiný čas");
        odchodAltButton.addClickListener(event -> odchodAltButtonClicked(event));
        odchodAltButton.getElement().setAttribute("theme", "secondary");
        odchodAltButton.setEnabled(false);
        return odchodAltButton;
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
        upperDochHeader.setSpacing(false);
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
                initDochDatePicker()
                , initUpperDochDateInfo()
                , buttonBox
        );
        return upperDochHeader;
    };

    private Component initUpperDochFooterBar() {
        upperDochFooterBar = new HorizontalLayout();
        upperDochFooterBar.setWidth("100%");
        upperDochFooterBar.getStyle().set("margin-top", "0.5em");
        upperDochFooterBar.setSpacing(false);
        upperDochFooterBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initPrenosPersonDateButton()
                , initCancelPrenosPersonDateButton()
                , initPrenosPersonMonthButton()
                , initPrenosPersonAllButton()
        );

        upperDochFooterBar.add(
                buttonBox
        );
        return upperDochFooterBar;
    }

    private void setDochNaviButtonsEnabled(boolean enabled) {
        loadTodayButton.setEnabled(enabled);
        loadPrevDateButton.setEnabled(enabled);
        loadNextDateButton.setEnabled(enabled);
        loadLastDateButton.setEnabled(enabled);
    }

    private Component initLoadTodayButton() {
//        loadTodayButton = new Button("Dnes");
        Icon icon = VaadinIcon.BULLSEYE.create();
        icon.setColor("green");
        loadTodayButton = new Button(icon);
        loadTodayButton.addClickListener(event -> {
            if (null != dochPerson) {
                dochDate = LocalDate.now();
                dochDatePicker.setValue(dochDate);
                fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                updateUpperDochGridPane(dochPerson, dochDate);

//                dochDatePicker.setValue(dochDate);
//                fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
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
//        loadPrevDateButton = new Button("Předchozí");
        loadPrevDateButton = new Button(VaadinIcon.STEP_BACKWARD.create());
        loadPrevDateButton.addClickListener(event -> {
            if (null != dochPerson && null != dochDate) {
                LocalDate prevDochDate = dochService.findPrevDochDate(dochPerson.getId(), dochDate);
                if (prevDochDate == null) {
                    loadPrevDateButton.setEnabled(false);
                } else {
                    dochDate = prevDochDate;
                    dochDatePicker.setValue(dochDate);
                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    updateUpperDochGridPane(dochPerson, dochDate);

//                    dochDatePicker.setValue(dochDate);
//                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    loadUpperDochGridData(dochPerson, dochDate);
                    setDochNaviButtonsEnabled(true);
                }
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadPrevDateButton;
    }

    private Component initLoadNextDateButton() {
//        loadNextDateButton = new Button("Následující");
        loadNextDateButton = new Button(VaadinIcon.STEP_FORWARD.create());
        loadNextDateButton.addClickListener(event -> {
            if (null != dochPerson && null != dochDate) {
                LocalDate nextDochDate = dochService.findNextDochDate(dochPerson.getId(), dochDate);
                if (nextDochDate == null) {
                    loadNextDateButton.setEnabled(false);
                } else {
                    dochDate = nextDochDate;
                    dochDatePicker.setValue(dochDate);
                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    updateUpperDochGridPane(dochPerson, dochDate);

//                    dochDatePicker.setValue(dochDate);
//                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    loadUpperDochGridData(dochPerson, dochDate);
                    setDochNaviButtonsEnabled(true);
                }
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadNextDateButton;
    }

    private Component initLoadLastDateButton() {
//        loadLastDateButton = new Button("Poslední");
        loadLastDateButton = new Button(VaadinIcon.FAST_FORWARD.create());
        loadLastDateButton.addClickListener(event -> {
            if (null != dochPerson && null != dochDate) {
                LocalDate lastDochDate = dochService.findLastDochDate(dochPerson.getId());
                if (lastDochDate == null) {
                    loadLastDateButton.setEnabled(false);
                } else {
                    dochDate = lastDochDate;
                    dochDatePicker.setValue(dochDate);
                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    updateUpperDochGridPane(dochPerson, dochDate);

//                    dochDatePicker.setValue(dochDate);
//                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    loadUpperDochGridData(dochPerson, dochDate);
                    setDochNaviButtonsEnabled(true);
                }
            } else {
                setDochNaviButtonsEnabled(false);
            }
        });
        return loadLastDateButton;
    }

    private Component initRemoveLastDochRecButton() {
        Icon icon = VaadinIcon.REPLY.create();
        icon.setColor("crimson");
        removeLastDochRecButton = new Button(icon);
        removeLastDochRecButton.addClickListener(event -> {
            if (canRemoveDochRec()) {
                ConfirmDialog.createQuestion()
                    .withCaption("Záznam docházky")
                    .withMessage("Zrušit poslední záznam v docházce?")
                    .withOkButton(() -> {
                        dochService.removeLastZkDochAndReopenPrev(getLastZkDochRec());
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
        Icon icon = VaadinIcon.TRASH.create();
        icon.setColor("crimson");
        removeAllDochRecButton = new Button(icon);
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
        return prenosPersonDateButton;
    }

    private Component initCancelPrenosPersonDateButton() {
        cancelPrenosPersonDateButton = new Button("Zrušit přenos");
        cancelPrenosPersonDateButton.getStyle()
//                .set("font-color", "red")
                .set("color", "crimson")
        ;
        return cancelPrenosPersonDateButton;
    }

    private Component initPrenosPersonMonthButton() {
        prenosPersonDateMonthButton = new Button("Přenést měsíc");
        return prenosPersonDateMonthButton;
    }

    private Component initPrenosPersonAllButton() {
        prenosPersonDateAllButton = new Button("Přenést vše");
        return prenosPersonDateAllButton;
    }


    private Component initUpperDochGrid() {
        upperDochGrid = new Grid<>();
        upperDochGrid.setHeight("20em");
//        upperDochGrid.setHeight("0");
        upperDochGrid.setColumnReorderingAllowed(false);
        upperDochGrid.setClassName("vizman-simple-grid");
        upperDochGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        upperDochGrid.getDataProvider().addDataProviderListener(doch -> {
//            upperDochGrid.getColumnByKey("dochDuration").setFooter(formatDuration(getDurationSum()));
//        }

        upperDochGrid.setItemDetailsRenderer(new ComponentRenderer<>(doch -> {
            return StringUtils.isBlank(doch.getPoznamka()) ? null : new Paragraph(doch.getPoznamka());
//            VerticalLayout layout = new VerticalLayout();
//            layout.add(new Label("Address: " + person.getAddress().getStreet()
//                    + " " + person.getAddress().getNumber()));
//            layout.add(new Label("Year of birth: " + person.getYearOfBirth()));
//            return layout;
        }));
        upperDochGrid.setDetailsVisibleOnClick(false);
//        upperDochGrid.setDetailsVisible(doch.)

        Binder<Doch> upperBinder = new Binder<>(Doch.class);

        upperDochGrid.addColumn(Doch::getDochState)
                .setHeader("St.")
                .setWidth("2em")
                .setFlexGrow(0)
                .setResizable(true)
        ;

////        upperDochGrid.addColumn(Doch::getCinT1).setHeader("T1").setWidth("2em").setResizable(true);
////        upperDochGrid.addColumn(Doch::getCinT2).setHeader("T2").setWidth("2em").setResizable(true);
//        TextField casOdField = new TextField();
//        upperBinder.forField(casOdField)
////                .withValidator(name -> name.startsWith("Person"),
////                        "Name should start with Person")
//                .withConverter(new VzmFormatUtils.LocalDateTimeToHhMmStringConverter())
////                .withStatusLabel(validationStatus).bind("AAAAAA");
////                .withStatusLabel(validationStatus).bind("name");
//                .bind(Doch::getDCasOd, Doch::setDCasOd);
////        Grid.Column<Doch> casOdColumn = upperDochGrid.addColumn(new ComponentRenderer<>(doch -> getCasOdComponent(doch)))

//        Grid.Column<Doch> casOdColumn = upperDochGrid.addColumn(fromTimeValProv)
        upperDochGrid.addColumn(fromTimeValProv)
                .setHeader("Od")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;
//        casOdColumn.setEditorComponent(casOdField);

//        upperDochGrid.addComponentColumn(new ComponentRenderer<>(doch -> getCasDoComponent(doch)))
//        upperDochGrid.addComponentColumn(doch -> getCasDoComponent(doch))
        upperDochGrid.addColumn(toTimeValProv)
                .setHeader("Do")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;
//        upperDochGrid.addComponentColumn(new ComponentRenderer<>(doch -> getHodinComponent(doch)))
//        upperDochGrid.addComponentColumn(doch -> getHodinComponent(doch))


        upperDochGrid.addColumn(Doch::getCinnost)
                .setHeader("Činnost")
                .setWidth("4em")
//                .setFooter("")
                .setFlexGrow(1)
                .setKey(DOCH_CINNOST_KEY)
                .setResizable(true);

        upperDochGrid.addColumn(durationValProv)
                .setHeader("Hod.")
//                .setFooter("S: " + formatDuration(getDurationSum()))
                .setFooter("")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setKey(DOCH_DURATION_KEY)
                .setResizable(true)
        ;

        return upperDochGrid;
    }

    private Duration getDurationSum() {
        return upperDochList.stream()
                .map(Doch::getSignedDochDur)
                .reduce(
                        Duration.ZERO,
                        (a, b) ->
//                        {
//                            if (null == b) {
//                                if (a.)return a;
//                            } else {
//
//                            }
                            (null == b) ? a : a.plus(b));
    }

    private Component initLowerDochGrid() {
        lowerDochGrid = new Grid<>();
        lowerDochGrid.setHeight("20em");
//        lowerDochGrid.setHeight("0");
        lowerDochGrid.setColumnReorderingAllowed(false);
        lowerDochGrid.setClassName("vizman-simple-grid");
        lowerDochGrid.setSelectionMode(Grid.SelectionMode.NONE);
        lowerDochGrid.addColumn(Doch::getDochState)
                .setHeader("St.")
                .setWidth("2em")
                .setResizable(true);
//        upperDochGrid.addColumn(Doch::getCinT1).setHeader("T1").setWidth("2em").setResizable(true);
//        upperDochGrid.addColumn(Doch::getCinT2).setHeader("T2").setWidth("2em").setResizable(true);
        lowerDochGrid.addColumn(Doch::getFromTime)
                .setHeader("Od")
                .setWidth("3em")
//                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);
        lowerDochGrid.addColumn(Doch::getToTime)
                .setHeader("Do")
                .setWidth("3em")
//                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);
        lowerDochGrid.addColumn(Doch::getDochDur)
                .setHeader("Hod.")
                .setWidth("3em")
//                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);
        lowerDochGrid.addColumn(Doch::getCinnost)
                .setHeader("Činnost")
                .setWidth("4em")
                .setResizable(true);

        return lowerDochGrid;
    }

    public static HtmlComponent getCasOdComponent(Doch doch) {
        return new Paragraph(null == doch.getFromTime() ? "" : doch.getFromTime().format( VzmFormatUtils.shortTimeFormatter));
    }

    public static HtmlComponent getCasDoComponent(Doch doch) {
        return new Paragraph(null == doch.getToTime() ? "" : doch.getToTime().format( VzmFormatUtils.shortTimeFormatter));
    }

    public static HtmlComponent getHodinComponent(Doch doch) {
//        return new Paragraph(null == doch.getDochDuration() ? "" : doch.getDochDuration().format( VzmFormatUtils.shortTimeFormatter));
        return new Paragraph(null == doch.getDochDurationUI() ? "" : formatDuration(doch.getDochDurationUI()));
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

    private void updateUpperDochGridPane(final Person dochPerson, final LocalDate dochDate) {
//        dochDatePicker.setValue(dochDate);
//        fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
        loadUpperDochGridData(dochPerson, dochDate);
        upperDochDateInfo.setText(null == dochDate ? "" : dochDate.format(upperDochDateHeaderFormatter));
    }

    private Component getDurationFooter(Duration durationSum) {
//        LocalTime.MIDNIGHT.plus(d).format(DateTimeFormatter.ofPattern("HH+mm"));

//        Paragraph comp = new Paragraph(null == durationSum ? null : formatDuration(durationSum));
        Paragraph comp = new Paragraph(null == durationSum ? null : LocalTime.MIDNIGHT.plus(durationSum).format(DateTimeFormatter.ofPattern("H:mm")));
        return comp;
    }


    private Component getDurationLabelFooter() {
        Paragraph comp = new Paragraph("Odpracováno: ");
//        comp.setWidth("5em");
        comp.getStyle().set("text-align", "end");
        return comp;
    }

    private void loadUpperDochGridData(Person dochPerson, LocalDate dochDate) {
        if (null == dochPerson || null == dochPerson.getId() || null == dochDate) {
            upperDochList = new ArrayList();
        } else {
            upperDochList = dochService.fetchDochForPersonAndDate(dochPerson.getId(), dochDate);
        }
        upperDochGrid.setItems(upperDochList);
        upperDochGrid.getColumnByKey(DOCH_DURATION_KEY)
                .setFooter(getDurationFooter(getDurationSum()));
        upperDochGrid.getColumnByKey(DOCH_CINNOST_KEY)
                .setFooter(getDurationLabelFooter());

        upperDochGrid.getDataProvider().refreshAll();
    }

    private void stampPrichodAndNew(final LocalDateTime dochStamp) {
        if (!canStampPrichod()) {
            return;
        }
        Doch newDochPrich = new Doch(cinRepo.findByCinKod(Cin.CinKod.P), dochPerson, dochDate, dochStamp);
        upperDochList.add(0, dochService.closePrevZkDochAndOpenNew(newDochPrich));
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }

    private void stampOdchodAndOpenNew(final LocalDateTime dochStamp, Cin.CinKod odchodWhereKod) {
        if (!canStampOdchod()) {
            return;
        }
        Doch newDochOdch = new Doch(cinRepo.findByCinKod(odchodWhereKod), dochPerson, dochDate, dochStamp);
        upperDochList.add(0, dochService.closePrevZkDochAndOpenNew(newDochOdch));
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }

    private void stampAndCloseOdchod() {
        if (!canStampOdchod()) {
            return;
        }
        dochService.closeLastZkDoch(dochPerson.getId(), dochDate);
        updateUpperDochGridPane(dochPerson, dochDate);
        upperDochGrid.getDataProvider().refreshAll();
    }

//    private boolean canRecordFirstPrichod() {
//        return checkDochDateIsToday()
//                && checkDayDochIsEmpty()
//        ;
//    }

    private boolean canStampPrichod() {
        return checkDayDochIsOpened("nelze upravovat.")
                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
                && checkPersonIsOutOfOffice("nelze zaznamenat další příchod.")
        ;
    }

    private boolean canStampOdchod() {
        return checkDayDochIsOpened("nelze přidávat záznamy.")
                && checkDochDateIsToday("je třeba použít 'Odchod jiný čas'.")
                && checkPersonIsInOffice("nelze zaznamenat odchod.")
        ;
    }

    private boolean canRemoveDochRec() {
        return checkDayDochIsOpened("nelze rušit záznamy")
                && checkDochPersonIsSelected("nelze rušit záznamy")
                && checkDochDateIsSelected("nelze rušit záznamy")
                && checkDochHasRecords("není co rušit")
                && checkZkDochRecToDelExists()
        ;
    }

    private boolean canRemoveAllDochRecs() {
        return checkDayDochIsOpened("nelze rušit záznamy")
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
                .withMessage(String.format("Denní docházka je uzavřena%s", adjustAdditionalMsg(additionalMsg)))
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

        if (!hasNoRecs || personIsOut) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Přítomnost na pracovišti")
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
                .withCaption("Nepřítomnost na pracovišti")
                .withMessage(String.format("Osoba není evidována na pracovišti%s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkZkDochRecToDelExists() {
        Doch lastZkDochRec = getLastZkDochRec();
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

    private boolean checkDochContainsNemoc(final String additionalMsg) {
        if (upperDochList.stream().anyMatch(Doch::isNemoc)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam docházky")
                .withMessage(String.format("Denní docházka obsahuje nemoc%s", adjustAdditionalMsg(additionalMsg)))
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
                .withMessage(String.format("Denní docházka obsahuje náhradní volno%s", adjustAdditionalMsg(additionalMsg)))
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
                || ((!Cin.CinKod.P.equals(lastZkDochRec.getCinCinKod()))
                    && (null == lastZkDochRec.getToTime()))
        );
    }

    private boolean personIsInOffice() {
        Doch lastZkDochRec = getLastZkDochRec();
        return ((null != lastZkDochRec)
                && (Cin.CinKod.P.equals(lastZkDochRec.getCinCinKod()))
                && (null == lastZkDochRec.getToTime()));
    }

    private Doch getLastZkDochRec() {
        // use ListIterator to iterate List in reverse order
//        ListIterator<Doch> dochReversedTimeIter = upperDochList.listIterator(upperDochList.size());
        ListIterator<Doch> dochReversedTimeIter = upperDochList.listIterator();

        // hasPrevious() returns true if the list has previous element
//        while (dochReversedIter.hasPrevious()) {
        while (dochReversedTimeIter.hasNext()) {
            Doch doch = dochReversedTimeIter.next();
            if (doch.isZk()) {
                return doch;
            }
        }
        return null;
    }

    private Doch getLastDoch() {
        return upperDochList.get(upperDochList.size() - 1);
    }

    private void odchodRadionChanged(HasValue.ValueChangeEvent event) {
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

    private void odchodButtonClicked(ClickEvent event) {
        resetOdchodRadio();
    }

    private void odchodAltButtonClicked(ClickEvent event) {
        resetOdchodRadio();
    }


    private static class TimeThread extends Thread {
        private final UI ui;
        private final DochView dochView;

//        private int count = 0;

        public TimeThread(UI ui, DochView dochView) {
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
                    ui.access(() -> dochView.updateDochClockTime());
                }

//                // Inform that we are done
//                ui.access(() -> {
//                    view.add(new Span("Done updating"));
//                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
