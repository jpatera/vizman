package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.PersonRepo;
import eu.japtor.vizman.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_DOCH;

@Route(value = ROUTE_DOCH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MANAGE_ALL
        , Perm.DOCH_USE
})
@SpringComponent
@UIScope
public class DochForm  extends VerticalLayout {

    private ComboBox personSelectBox;
    private Button dochMonthReportBtn;
    private Button dochYearReportBtn;
    private Span timeBox;
    private Span dochUpperDateInfo;

    LocalDate dochDate;
    LocalDate dochDatePrevious;

    private TimeThread timeThread;
    DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy");

//    @Autowired
//    public DochRepo zakRepo;

    @Autowired
    public PersonRepo personRepo;

    public DochForm() {
        buildForm();
    }

    @PostConstruct
    public void init() {
//        initCurrentDochGrid();
//        initLastDochGrid();
        initData();
    }

//    public void updateTime(LocalTime time) {
    public void updateTime() {
        timeBox.setText(LocalTime.now().format(dochTimeFormatter));
    }

    private void initData() {
        personSelectBox.setDataProvider(new ListDataProvider(
                personRepo.findAllByOrderByUsername().stream()
                    .map(p -> p.getUsername() + " (" + p.getJmeno() + " " + p.getPrijmeni() + ")")
                    .collect(Collectors.toList())));
    }

    private void buildForm() {
        this.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        this.setWidth("900px");

        personSelectBox = new ComboBox();
//        personSelectBox.setLabel("Uživatel");
        personSelectBox.setLabel(null);
        personSelectBox.setWidth("100%");

        dochMonthReportBtn = new Button("Měsíční přehled");
//        dochMonthReportBtn.setWidth("100%");

        dochYearReportBtn = new Button("Roční přehled");
//        dochYearReportBtn.setWidth("100%");

        HorizontalLayout dochHeader = new HorizontalLayout();
        dochHeader.add(new H4("Uživatel(ka): "), personSelectBox, dochMonthReportBtn, dochYearReportBtn);

        timeBox = new Span("TIME");
//        timeBox.setAlignSelf();
        timeBox.getStyle()
//                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-size", "3em")
                .set("font-weight", "600")
//                .set("padding-right", "0.75em")
        ;

        FormLayout dochControl = new FormLayout();
//        dochControl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        dochControl.setWidth("600px");
        dochControl.add(timeBox);

        dochUpperDateInfo = new Span("Vybraný den docházky...");

        HorizontalLayout dochRecUpperHeader = new HorizontalLayout();
        dochRecUpperHeader.add(dochUpperDateInfo);

        Grid dochRecUpperGrid = new Grid();
        HorizontalLayout dochRecUpperFooter = new HorizontalLayout();

        HorizontalLayout dochRecLowerHeader = new HorizontalLayout();
        Grid dochRecLowerGrid = new Grid();
        HorizontalLayout dochRecLowerFooter = new HorizontalLayout();

        VerticalLayout dochRecords = new VerticalLayout();
        dochRecords.add(dochRecUpperHeader);
        dochRecords.add(dochRecUpperGrid);
        dochRecords.add(dochRecUpperHeader);
        dochRecords.add(dochRecLowerHeader);
        dochRecords.add(dochRecLowerGrid);
        dochRecords.add(dochRecLowerHeader);

        HorizontalLayout dochDesk = new HorizontalLayout();
        dochDesk.add(dochControl);
        dochDesk.add(dochRecords);

        add(new H3("DOCHÁZKA"), dochHeader, dochDesk);
    }

    private void initCurrentDochGrid() {

    }

    private void initLastDochGrid() {

    }



    @Override
    protected void onAttach(AttachEvent attachEvent) {

        dochDate = LocalDate.now();
        dochUpperDateInfo.setText(dochDate.format(dochDateHeaderFormatter));
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
                    ui.access(() -> dochForm.updateTime());
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
