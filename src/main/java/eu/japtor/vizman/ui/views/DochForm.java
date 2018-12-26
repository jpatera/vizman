package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.repository.PersonRepo;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_DOCH;

@Route(value = ROUTE_DOCH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.DOCH_USE
})
@SpringComponent
@UIScope
//@Push
public class DochForm  extends VerticalLayout {

    private ComboBox personSelectBox = new ComboBox();
    private DatePicker datePicker = new DatePicker();
    private Button dochMonthReportBtn = new Button();
    private Button dochYearReportBtn = new Button();

    private HorizontalLayout dochHeader = new HorizontalLayout();

    FormLayout dochControl = new FormLayout();
    FormLayout nepritControl = new FormLayout();
    private Span clockDisplay = new Span();
    private Span dochUpperDateInfo = new Span();
    private Span dochLowerDateInfo = new Span();
    private RadioButtonGroup<String> odchodRadio = new RadioButtonGroup();

    private Button prichodBtn = new Button("Příchod");
    private Button prichodAltBtn = new Button("Příchod jiný čas");
    private Button odchodBtn = new Button("Odchod");
    private Button odchodAltBtn = new Button("Odchod jiný čas");

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
    Grid<Doch> dochRecUpperGrid = new Grid();
    HorizontalLayout dochRecUpperFooter = new HorizontalLayout();

    HorizontalLayout dochRecLowerHeader = new HorizontalLayout();
    Grid<Doch> dochRecLowerGrid = new Grid();
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
    public PersonRepo personRepo;

    @Autowired
    public CinRepo cinRepo;

//    @Autowired
//    public DochForm(Person dochPerson) {
    public DochForm() {
//        super();
        buildForm();
//        initDochData(dochPerson, dochDate);
    }

    @PostConstruct
    public void init() {
        initUpperDochGrid();
        initLowerDochGrid();
        initData();
    }

//    public void updateDochClockTime(LocalTime time) {
    public void updateDochClockTime() {
        clockDisplay.setText(LocalTime.now().format(dochTimeFormatter));
    }

    private void initData() {
        personSelectBox.setDataProvider(new ListDataProvider(
                personRepo.findAllByOrderByUsername().stream()
                    .map(p -> p.getUsername() + " (" + p.getJmeno() + " " + p.getPrijmeni() + ")")
                    .collect(Collectors.toList())));
    }

    private void buildForm() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setWidth("1200px");

//        personSelectBox = new ComboBox();
//        personSelectBox.setLabel("Uživatel");
        personSelectBox.setLabel(null);
        personSelectBox.setWidth("100%");

        datePicker.setLabel(null);
        datePicker.setWidth("100%");

        dochMonthReportBtn.setText("Měsíční přehled");
//        dochMonthReportBtn.setWidth("100%");

//        dochYearReportBtn = new Button("Roční přehled");
        dochYearReportBtn.setText("Roční přehled");
//        dochYearReportBtn.setWidth("100%");

//        HorizontalLayout dochHeader = new HorizontalLayout();
        dochHeader.add(new H4("Uživatel(ka): "), personSelectBox, datePicker, dochMonthReportBtn, dochYearReportBtn);

        VerticalLayout clockContainer = new VerticalLayout();
        clockContainer.setSizeFull();
        clockContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        clockContainer.setFlexGrow(1);
        clockContainer.getStyle()
                .set("colspan", "2")
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


//        prichodBtn = new Button("Příchod");
        prichodBtn.setText("Příchod");
        prichodBtn.getElement().setAttribute("theme", "primary");
//        prichodBtn.getElement().getStyle().set("max-width", "8em");
        prichodBtn.addClickListener(event ->  {
            LocalDateTime now =  LocalDateTime.now(minuteClock);
        });


        //        prichodBtn.getElement().getStyle().set("max-width", "8em");
//        prichodAltBtn = new Button("Přích. jiný čas");
        prichodAltBtn.setText("Přích. jiný čas");
        prichodAltBtn.getElement().setAttribute("theme", "secondary");
//        prichodAltBtn.getElement().getStyle().set("max-width", "8em");

//        odchodBtn = new Button("Odchod");
        odchodBtn.setText("Odchod");
        odchodBtn.addClickListener(event -> odchodBtnClicked(event));
        odchodBtn.setEnabled(false);

//        odchodAltBtn = new Button("Odchod jiný čas");
        odchodAltBtn.setText("Odchod jiný čas");
        odchodAltBtn.addClickListener(event -> odchodBtnClicked(event));
        odchodAltBtn.getElement().setAttribute("theme", "secondary");
        odchodAltBtn.setEnabled(false);
//        odchodAltBtn.getElement().getStyle().set("max-width", "8em");


//        odchodRadio = new RadioButtonGroup();
        odchodRadio.addValueChangeListener(event -> odchodRadionChanged(event));
        odchodRadio.setItems("Odchod na oběd", "Odchod pracovně", "Odchod k lékaři", "Ukončení/přerušení práce");
//        odchodRadio.getElement().getStyle().set("display", "flex");
        odchodRadio.getElement().setAttribute("theme", "vertical");
//                getStyle().set("flex-direction", "column");

//        dochControl = new FormLayout();
//        dochControl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
//        dochControl.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("15em", 2));
        dochControl.setWidth("30em");
        dochControl.add(clockContainer);
        dochControl.add(prichodBtn);
        dochControl.add(prichodAltBtn);
        dochControl.add(buildVertSpace());
        dochControl.add(odchodRadio);
        dochControl.add(odchodBtn);
        dochControl.add(odchodAltBtn);


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

        VerticalLayout dochRecords = new VerticalLayout();
        dochRecords.add(dochRecUpperHeader);
        dochRecords.add(dochRecUpperGrid);
        dochRecords.add(dochRecUpperFooter);
        dochRecords.add(dochRecLowerHeader);
        dochRecords.add(dochRecLowerGrid);
        dochRecords.add(dochRecLowerFooter);

        HorizontalLayout dochDesk = new HorizontalLayout();
        dochDesk.add(dochControl);
        dochDesk.add(dochRecords);
        dochDesk.add(nepritControl);

        add(new H3("DOCHÁZKA"), dochHeader, dochDesk);
    }

    private Component buildVertSpace() {
        Div vertSpace = new Div();
        vertSpace.setHeight("1em");
        return vertSpace;
    }


    private void initDochData(Person dochPerson, LocalDate dochDate) {

    }

    private void initUpperDochGrid() {
        dochRecUpperGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        dochRecUpperGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#").setWidth("1em");
        dochRecUpperGrid.addColumn(Doch::getCinSt).setHeader("St.").setWidth("2em").setResizable(true);
//        dochRecUpperGrid.addColumn(Doch::getCinT1).setHeader("T1").setWidth("2em").setResizable(true);
//        dochRecUpperGrid.addColumn(Doch::getCinT2).setHeader("T2").setWidth("2em").setResizable(true);
        dochRecUpperGrid.addColumn(Doch::getdCasOd).setHeader("Od").setWidth("3em").setResizable(true);
        dochRecUpperGrid.addColumn(Doch::getdCasDo).setHeader("Do").setWidth("3em").setResizable(true);
        dochRecUpperGrid.addColumn(Doch::getdHodin).setHeader("Hod.").setWidth("3em").setResizable(true);
        dochRecUpperGrid.addColumn(Doch::getCinnost).setHeader("Činnost").setWidth("4em").setResizable(true);
    }

    private void initLowerDochGrid() {
        dochRecLowerGrid.setSelectionMode(Grid.SelectionMode.NONE);
//        dochRecUpperGrid.addColumn(TemplateRenderer.of("[[index]]")).setHeader("#").setWidth("1em");
        dochRecLowerGrid.addColumn(Doch::getCinSt).setHeader("St.").setWidth("2em").setResizable(true);
//        dochRecUpperGrid.addColumn(Doch::getCinT1).setHeader("T1").setWidth("2em").setResizable(true);
//        dochRecUpperGrid.addColumn(Doch::getCinT2).setHeader("T2").setWidth("2em").setResizable(true);
        dochRecLowerGrid.addColumn(Doch::getdCasOd).setHeader("Od").setWidth("3em").setResizable(true);
        dochRecLowerGrid.addColumn(Doch::getdCasDo).setHeader("Do").setWidth("3em").setResizable(true);
        dochRecLowerGrid.addColumn(Doch::getdHodin).setHeader("Hod.").setWidth("3em").setResizable(true);
        dochRecLowerGrid.addColumn(Doch::getCinnost).setHeader("Činnost").setWidth("4em").setResizable(true);
    }

    private void odchodRadionChanged(HasValue.ValueChangeEvent event) {
        System.out.println("--------------- odchod radio changed");
        System.out.println(event.toString());
        if (null == event.getValue()) {
            odchodAltBtn.setEnabled(false);
            odchodBtn.setEnabled(false);
        } else {
            odchodAltBtn.setEnabled(true);
            odchodBtn.setEnabled(true);
        }
    }

    private void odchodBtnClicked(ClickEvent event) {
        odchodRadio.setValue(null);
        odchodRadio.clear();    // Probably a bug -> workaround...
        odchodRadio.getElement().getChildren()
                .filter(element -> element.hasProperty("checked"))
                .forEach(checked -> checked.removeProperty("checked"));
    }

    private void initLastDochGrid() {

    }



    @Override
    protected void onAttach(AttachEvent attachEvent) {

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
        timeThread.interrupt();
        timeThread = null;
    }


    private static class TimeThread extends Thread {
        private final UI ui;
        private final DochForm dochForm;

//        private int count = 0;

        public TimeThread(UI ui, DochForm dochForm) {
            this.ui = ui;
            this.dochForm = dochForm;
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
                    ui.access(() -> dochForm.updateDochClockTime());
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
