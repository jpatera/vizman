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
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.service.DochService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.Ribbon;
import org.apache.commons.lang3.StringUtils;
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


    private Button prichodBtn;
    private Button prichodAltBtn;
    private RadioButtonGroup<String> odchodRadio;
    private Button odchodButton;
    private Button odchodAltButton;

    Button dovolBtn = new Button("Dovolená (8h)");
    Button dovolHalfBtn = new Button("Dovolená (4h)");
    Button dovolZrusBtn = new Button("Zrušit dovolenou");
    Button sluzebkaBtn = new Button("Služebka (8h)");
    Button sluzebkaZrusBtn = new Button("Zrušit služebku");
    Button nemocBtn = new Button("Nemoc (8h)");
    Button nemocZrusBtn = new Button("Zrušit nemoc");
    Button volnoBtn = new Button("Neplacené volno (8h)");
    Button volnoZrusBtn = new Button("Zrušit neplac. volno");

    HorizontalLayout upperDochHeader;
    Grid<Doch> upperDochGrid;
    List<Doch> upperDochList = new ArrayList<>();
    HorizontalLayout dochRecUpperFooter = new HorizontalLayout();

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
    DateTimeFormatter dochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy");

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
        dochLowerDateInfo.setText(dochDatePrev.format(dochDateHeaderFormatter));

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

        initPersonList();
//        initDochCallendar();

        if (null == dochPerson) {
            dochPerson = dochPersonList.stream()
                    .filter(person -> person.getUsername().toLowerCase().equals("vancik"))
                    .findFirst().orElse(null);
            dochPersonCombo.setValue(dochPerson);
        }

        if (null == dochDate) {
            dochDate = LocalDate.of(2019, 1, 15);
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

//        HorizontalLayout dochHeader = new HorizontalLayout();
        dochHeader.add(
//                new H4("Uživatel(ka): ")
                initPersonCombo()
//                , initDochDatePicker()
                , dochMonthReportBtn
                , dochYearReportBtn)
        ;


//        dochControl = new FormLayout();
//        dochControl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
//        dochControl.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("15em", 2));
        dochControl.setWidth("30em");
        dochControl.add(initDochDatePicker());
        dochControl.add(initClockComponent());
        dochControl.add(initPrichodButton());
        dochControl.add(initPrichodAltButton());
        dochControl.add(buildVertSpace());
        dochControl.add(initOdchodRadio());
        dochControl.add(initOdchodButton());
        dochControl.add(initOdchodAltButton());


        nepritControl.setWidth("30em");
        nepritControl.add(dovolBtn);
        nepritControl.add(dovolHalfBtn);
        nepritControl.add(dovolZrusBtn);
        nepritControl.add(buildVertSpace());
        nepritControl.add(sluzebkaBtn);
        nepritControl.add(sluzebkaZrusBtn);
        nepritControl.add(buildVertSpace());
        nepritControl.add(nemocBtn);
        nepritControl.add(nemocZrusBtn);
        nepritControl.add(buildVertSpace());
        nepritControl.add(volnoBtn);
        nepritControl.add(volnoZrusBtn);

        dochLowerDateInfo.setText("Předchozí den docházky...");

        dochRecLowerHeader.add(dochLowerDateInfo);

        VerticalLayout dochRecPane = new VerticalLayout();
        dochRecPane.add(initUpperDochHeader());
        dochRecPane.add(initUpperDochGrid());
        dochRecPane.add(dochRecUpperFooter);
        dochRecPane.add(dochRecLowerHeader);
        dochRecPane.add(initLowerDochGrid());
        dochRecPane.add(dochRecLowerFooter);

        HorizontalLayout dochPanel = new HorizontalLayout();
        dochPanel.add(dochControl);
        dochPanel.add(dochRecPane);
        dochPanel.add(nepritControl);

        this.add(new H3("DOCHÁZKA"), dochHeader, dochPanel);
    }


    private Component initClockComponent() {
        clockContainer = new VerticalLayout();
        clockContainer.setSizeFull();
        clockContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        clockContainer.setFlexGrow(1);
        clockContainer.getElement().
            setAttribute("colspan","2")
//                .set("width", "stretch")
//                .set("align", "center")
    ;
//        clockDisplay.getStyle().set("colspan", "2");
//        clockDisplay.setAlignSelf();
        clockDisplay.getStyle()
//                .set("font-size", "var(--lumo-font-size-l)")
                .set("colspan", "2")
                .set("font-size","2.5em")
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
        upperDochDateInfo.setText("Vybraný den docházky...");
        return upperDochDateInfo;
    }

    private Component initPersonCombo() {
        dochPersonCombo = new ComboBox();
        dochPersonCombo.setLabel(null);
        dochPersonCombo.setWidth("100%");
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
        dochDatePicker.setWidth("100%");
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
            addPrichod(dochStamp);
        });
        return prichodBtn;
    }

    private Button initPrichodAltButton() {
        prichodAltBtn = new Button("Příchod jiný čas");
        prichodAltBtn.getElement().setAttribute("theme", "secondary");
        return prichodAltBtn;
    }

    private RadioButtonGroup initOdchodRadio() {
        odchodRadio = new RadioButtonGroup();
        odchodRadio.addValueChangeListener(event -> odchodRadionChanged(event));
        odchodRadio.setItems("Odchod na oběd", "Odchod pracovně", "Odchod k lékaři", "Ukončení/přerušení práce");
//        odchodRadio.getElement().getStyle().set("display", "flex");
        odchodRadio.getElement().setAttribute("theme", "vertical");
        //                getStyle().set("flex-direction", "column");
        return odchodRadio;
    }

    private Button initOdchodButton() {
        odchodButton = new Button("Odchod");
        odchodButton.setText("Odchod");
        odchodButton.addClickListener(event -> odchodButtonClicked(event));
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


    private ValueProvider<Doch, String> casOdValProv =
            doch -> null == doch.getDCasOd() ? null : doch.getDCasOd().format(VzmFormatUtils.shortTimeFormatter);

    private ValueProvider<Doch, String> casDoValProv =
            doch -> null == doch.getToTime() ? null : doch.getToTime().format(VzmFormatUtils.shortTimeFormatter);

    private ValueProvider<Doch, String> casDochHodin =
            doch -> null == doch.getDochDuration() ? null : formatDuration(doch.getDochDuration());


    private Component initUpperDochHeader() {
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
        );

        upperDochHeader.add(
                initUpperDochDateInfo()
                , new Ribbon()
                , buttonBox
        );
        return upperDochHeader;
    }

    private void setDochNavigButtonsEnabled(boolean enabled) {
        loadTodayButton.setEnabled(enabled);
        loadPrevDateButton.setEnabled(enabled);
        loadNextDateButton.setEnabled(enabled);
        loadLastDateButton.setEnabled(enabled);
    }

    private Component initLoadTodayButton() {
//        loadTodayButton = new Button("Dnes");
        loadTodayButton = new Button(VaadinIcon.LIFEBUOY.create());
        loadTodayButton.addClickListener(event -> {
            if (null != dochPerson) {
                dochDate = LocalDate.now();
                updateUpperDochGridPane(dochPerson, dochDate);
//                dochDatePicker.setValue(dochDate);
//                fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                loadUpperDochGridData(dochPerson, dochDate);
//                loadPrevDateButton.setEnabled(true);
                setDochNavigButtonsEnabled(true);
            } else {
                setDochNavigButtonsEnabled(false);
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
                    updateUpperDochGridPane(dochPerson, dochDate);
//                    dochDatePicker.setValue(dochDate);
//                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    loadUpperDochGridData(dochPerson, dochDate);
                    setDochNavigButtonsEnabled(true);
                }
            } else {
                setDochNavigButtonsEnabled(false);
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
                    updateUpperDochGridPane(dochPerson, dochDate);
//                    dochDatePicker.setValue(dochDate);
//                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    loadUpperDochGridData(dochPerson, dochDate);
                    setDochNavigButtonsEnabled(true);
                }
            } else {
                setDochNavigButtonsEnabled(false);
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
                    updateUpperDochGridPane(dochPerson, dochDate);
//                    dochDatePicker.setValue(dochDate);
//                    fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
//                    loadUpperDochGridData(dochPerson, dochDate);
                    setDochNavigButtonsEnabled(true);
                }
            } else {
                setDochNavigButtonsEnabled(false);
            }
        });
        return loadLastDateButton;
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

//        Grid.Column<Doch> casOdColumn = upperDochGrid.addColumn(casOdValProv)
        upperDochGrid.addColumn(casOdValProv)
                .setHeader("Od")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;
//        casOdColumn.setEditorComponent(casOdField);

//        upperDochGrid.addComponentColumn(new ComponentRenderer<>(doch -> getCasDoComponent(doch)))
//        upperDochGrid.addComponentColumn(doch -> getCasDoComponent(doch))
        upperDochGrid.addColumn(casDoValProv)
                .setHeader("Do")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;
//        upperDochGrid.addComponentColumn(new ComponentRenderer<>(doch -> getHodinComponent(doch)))
//        upperDochGrid.addComponentColumn(doch -> getHodinComponent(doch))


        upperDochGrid.addColumn(casDochHodin)
                .setHeader("Hod.")
//                .setFooter("S: " + formatDuration(getDurationSum()))
                .setFooter("S: ")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setKey(DOCH_DURATION_KEY)
                .setResizable(true)
        ;
        upperDochGrid.addColumn(Doch::getCinnost)
                .setHeader("Činnost")
                .setWidth("4em")
                .setFlexGrow(1)
                .setResizable(true);

        return upperDochGrid;
    }

    private Duration getDurationSum() {
        return upperDochList.stream()
                .map(Doch::getDochDuration)
                .reduce(
                        Duration.ZERO,
                        (a, b) -> a.plus(b));
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
        lowerDochGrid.addColumn(Doch::getDCasOd)
                .setHeader("Od")
                .setWidth("3em")
//                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);
        lowerDochGrid.addColumn(Doch::getToTime)
                .setHeader("Do")
                .setWidth("3em")
//                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);
        lowerDochGrid.addColumn(Doch::getDochDuration)
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
        return new Paragraph(null == doch.getDCasOd() ? "" : doch.getDCasOd().format( VzmFormatUtils.shortTimeFormatter));
    }

    public static HtmlComponent getCasDoComponent(Doch doch) {
        return new Paragraph(null == doch.getToTime() ? "" : doch.getToTime().format( VzmFormatUtils.shortTimeFormatter));
    }

    public static HtmlComponent getHodinComponent(Doch doch) {
//        return new Paragraph(null == doch.getDochDuration() ? "" : doch.getDochDuration().format( VzmFormatUtils.shortTimeFormatter));
        return new Paragraph(null == doch.getDochDuration() ? "" : formatDuration(doch.getDochDuration()));
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
        dochDatePicker.setValue(dochDate);
        fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(dochDatePicker, false));
        loadUpperDochGridData(dochPerson, dochDate);
        upperDochDateInfo.setText(null == dochDate ? "" : dochDate.format(dochDateHeaderFormatter));
    }

    private void loadUpperDochGridData(Person dochPerson, LocalDate dochDate) {
        if (null == dochPerson || null == dochPerson.getId() || null == dochDate) {
            upperDochList = new ArrayList();
        } else {
            upperDochList = dochService.fetchDochForPersonAndDate(dochPerson.getId(), dochDate);
        }
        upperDochGrid.setItems(upperDochList);
        upperDochGrid.getColumnByKey(DOCH_DURATION_KEY)
                .setFooter("S: " + formatDuration(getDurationSum()));
        upperDochGrid.getDataProvider().refreshAll();
    }

    private void addFirstPrichod(final LocalDateTime dochStamp) {

        if (upperDochList.size() == 0) {
            Doch dochPrich = new Doch(cinCache.getByCinKod("P"), dochStamp);
            upperDochList.add(dochService.addFirstPrichod(dochPrich));
        }

        if (checkDochClosed() || checkContainsNemoc() || checkContainsNahradniVolno()) {
            return;
        }

        if (null == getLastZkDoch() )
//        Doch doch = new Doch(getCin());
    }

    private boolean checkDochClosed() {
        if (upperDochList.stream().anyMatch(Doch::isClosed)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Záznam docházky")
                    .withMessage("Denní docházka je uzavřena - nelze upravovat")
                    .withOkButton()
                    .open();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkContainsNemoc() {
        if (upperDochList.stream().anyMatch(Doch::isNemoc)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Záznam docházky")
                    .withMessage("Denní docházka obsahuje nemoc - nelze upravovat")
                    .withOkButton()
                    .open();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkContainsNahradniVolno() {
        if (upperDochList.stream().anyMatch(Doch::isNahradniVolno)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Záznam docházky")
                    .withMessage("Denní docházka obsahuje náhradní volno - nelze upravovat")
                    .withOkButton()
                    .open();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkIsLastPrichod() {
        if (upperDochList.stream().anyMatch(Doch::isNahradniVolno)) {
            ConfirmDialog
                    .createInfo()
                    .withCaption("Záznam docházky")
                    .withMessage("Denní docházka obsahuje náhradní volno - nelze upravovat")
                    .withOkButton()
                    .open();
            return false;
        } else {
            return true;
        }
    }

    private Doch getLastDoch() {
        return upperDochList.get(upperDochList.size() - 1);
    }

    private Doch getLastZkDoch() {
        // use ListIterator to iterate List in reverse order
        ListIterator<Doch> dochRevIter = upperDochList.listIterator(upperDochList.size());

        // hasPrevious() returns true if the list has previous element
        while (dochRevIter.hasPrevious()) {
            Doch doch = dochRevIter.previous();
            if (doch.isZk()) {
                return doch;
            }
        }
        return null;
    }

    private void odchodRadionChanged(HasValue.ValueChangeEvent event) {
        System.out.println("--------------- odchod radio changed");
        System.out.println(event.toString());
        if (null == event.getValue()) {
            odchodAltButton.setEnabled(false);
            odchodButton.setEnabled(false);
        } else {
            odchodAltButton.setEnabled(true);
            odchodButton.setEnabled(true);
        }
    }

    private void odchodButtonClicked(ClickEvent event) {
        initOddchodRadio();
    }

    private void odchodAltButtonClicked(ClickEvent event) {
        initOddchodRadio();
    }

    private void initOddchodRadio() {
//        odchodRadio.setValue(null);
        odchodRadio.clear();    // Probably a bug -> workaround...
        odchodRadio.getElement().getChildren()
                .filter(element -> element.hasProperty("checked"))
                .forEach(checked -> checked.removeProperty("checked"));
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
