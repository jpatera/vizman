package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.ValueProvider;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_DOCH;

@Route(value = ROUTE_DOCH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.DOCH_USE
})
@SpringComponent
@UIScope
//@Push
// public class DochView extends VerticalLayout implements HasLogger, BeforeEnterListener {
public class DochView extends VerticalLayout implements HasLogger {

    private List<Person> dochPersonList;
    private ComboBox<Person> dochPersonCombo;
    private DatePicker dochDatePicker;

    private Button dochMonthReportBtn = new Button();
    private Button dochYearReportBtn = new Button();

    private HorizontalLayout dochHeader = new HorizontalLayout();

    FormLayout dochControl = new FormLayout();
    FormLayout nepritControl = new FormLayout();
    private Span clockDisplay = new Span();
    private Span dochUpperDateInfo = new Span();
    private Span dochLowerDateInfo = new Span();

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

    HorizontalLayout dochRecUpperHeader = new HorizontalLayout();
    Grid<Doch> upperDochGrid;
    List<Doch> upperDochList;
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
        this.setAlignSelf(Alignment.CENTER);
        loadStableData();
        initDochData();
    }

    private void loadStableData() {
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


    private void initWhenOpened() {
        dochPerson = personService.getById(13L);
        dochDate = LocalDate.of(2019, 01, 17);
        loadUpperDochGridData(dochPerson, dochDate);
    }

    private void buildForm() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setWidth("1200px");

        dochMonthReportBtn.setText("Měsíční přehled");
//        dochMonthReportBtn.setWidth("100%");

//        dochYearReportBtn = new Button("Roční přehled");
        dochYearReportBtn.setText("Roční přehled");
//        dochYearReportBtn.setWidth("100%");

//        HorizontalLayout dochHeader = new HorizontalLayout();
        dochHeader.add(new H4("Uživatel(ka): ")
                , initPersonCombo()
                , initDochDatePicker()
                , dochMonthReportBtn
                , dochYearReportBtn)
        ;

        VerticalLayout clockContainer = new VerticalLayout();
        clockContainer.setSizeFull();
        clockContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        clockContainer.setFlexGrow(1);
        clockContainer.getElement().setAttribute("colspan", "2")
//                .set("width", "stretch")
//                .set("align", "center")
        ;
//        clockDisplay.getStyle().set("colspan", "2");
//        clockDisplay.setAlignSelf();
        clockDisplay.getStyle()
//                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-size", "3em")
                .set("font-weight", "600")
//                .set("padding-right", "0.75em")
        ;
        updateDochClockTime();
        clockContainer.add(clockDisplay);


//        dochControl = new FormLayout();
//        dochControl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
//        dochControl.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("15em", 2));
        dochControl.setWidth("30em");
        dochControl.add(clockContainer);
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

        dochUpperDateInfo.setText("Vybraný den docházky...");
        dochLowerDateInfo.setText("Předchozí den docházky...");

        dochRecUpperHeader.add(dochUpperDateInfo);
        dochRecLowerHeader.add(dochLowerDateInfo);

        VerticalLayout dochRecPane = new VerticalLayout();
        dochRecPane.add(dochRecUpperHeader);
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


    private Component initPersonCombo() {
        dochPersonCombo = new ComboBox();
        dochPersonCombo.setLabel(null);
        dochPersonCombo.setWidth("100%");
        dochPersonCombo.setItems(new ArrayList<>());
        dochPersonCombo.setItemLabelGenerator(this::getPersonLabel);
        dochPersonCombo.addValueChangeListener(event -> {
            dochPerson = event.getValue();
            loadUpperDochGridData(dochPerson, dochDate);
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
            loadUpperDochGridData(dochPerson, dochDate);
        });
        return dochDatePicker;
    }


    private Component initPrichodButton() {
        prichodBtn = new Button("Příchod");
        prichodBtn.getElement().setAttribute("theme", "primary");
        prichodBtn.addClickListener(event -> {
            LocalDateTime now = LocalDateTime.now(minuteClock);
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
            doch -> null == doch.getDCasDo() ? null : doch.getDCasDo().format(VzmFormatUtils.shortTimeFormatter);

    private ValueProvider<Doch, String> casDochHodin =
            doch -> null == doch.getDHodin() ? null : doch.getDHodin().format(VzmFormatUtils.shortTimeFormatter);

    private Component initUpperDochGrid() {
        upperDochGrid = new Grid<>();
        upperDochGrid.setHeight("20em");
//        upperDochGrid.setHeight("0");
        upperDochGrid.setColumnReorderingAllowed(false);
        upperDochGrid.setClassName("vizman-simple-grid");
        upperDochGrid.setSelectionMode(Grid.SelectionMode.NONE);

        Binder<Doch> upperBinder = new Binder<>(Doch.class);

        upperDochGrid.addColumn(Doch::getCinSt)
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
//        upperDochGrid.addComponentColumn(new ComponentRenderer<>(doch -> getdHodinComponent(doch)))
//        upperDochGrid.addComponentColumn(doch -> getdHodinComponent(doch))
        upperDochGrid.addColumn(casDochHodin)
                .setHeader("Hod.")
                .setWidth("5em")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true)
        ;
        upperDochGrid.addColumn(Doch::getCinnost)
                .setHeader("Činnost")
                .setWidth("4em")
                .setFlexGrow(1)
                .setResizable(true);

        return upperDochGrid;
    }

    private Component initLowerDochGrid() {
        lowerDochGrid = new Grid<>();
        lowerDochGrid.setHeight("20em");
//        lowerDochGrid.setHeight("0");
        lowerDochGrid.setColumnReorderingAllowed(false);
        lowerDochGrid.setClassName("vizman-simple-grid");
        lowerDochGrid.setSelectionMode(Grid.SelectionMode.NONE);
        lowerDochGrid.addColumn(Doch::getCinSt)
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
        lowerDochGrid.addColumn(Doch::getDCasDo)
                .setHeader("Do")
                .setWidth("3em")
//                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);
        lowerDochGrid.addColumn(Doch::getDHodin)
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
        return new Paragraph(null == doch.getDCasDo() ? "" : doch.getDCasDo().format( VzmFormatUtils.shortTimeFormatter));
    }

    public static HtmlComponent getdHodinComponent(Doch doch) {
        return new Paragraph(null == doch.getDHodin() ? "" : doch.getDHodin().format( VzmFormatUtils.shortTimeFormatter));
    }

    private void loadUpperDochGridData(Person dochPerson, LocalDate dochDate) {
//        upperDochGrid.getDataProvider().refreshAll();
        if (null == dochPerson || null == dochPerson.getId() || null == dochDate) {
            upperDochList = new ArrayList();
        } else {
            upperDochList = dochService.fetchDochsByPersonIdAndDate(dochPerson.getId(), dochDate);
        }
        upperDochGrid.setItems(upperDochList);
        upperDochGrid.getDataProvider().refreshAll();
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


    @Override
    protected void onAttach(AttachEvent attachEvent) {

        getLogger().info("## ON ATTACH DochView ##");

        dochDate = LocalDate.now();
        dochUpperDateInfo.setText(dochDate.format(dochDateHeaderFormatter));

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

    public void initDochData() {
        getLogger().info("## Initializing doch data");
        if (null == upperDochList) {
            loadStableData();
        }
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
        loadUpperDochGridData(dochPerson, dochDate);
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
