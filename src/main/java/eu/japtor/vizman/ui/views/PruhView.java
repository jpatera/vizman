package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.CinRepo;
import eu.japtor.vizman.backend.service.DochsumService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.ui.MainView;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.Ribbon;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

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

    private ConfirmDialog zekSelectDialog;

    private List<Person> pruhPersonList;
    private ComboBox<Person> pruhPersonCombo;
    private DatePicker pruhRokMesSelectList;

    private static final Locale czLocale = new Locale("cs", "CZ");

    private HorizontalLayout pruhHeader = new HorizontalLayout();

    VerticalLayout clockContainer;
    private Span clockDisplay = new Span();

    //    private Span upperDochDateInfo = new Span();
    private Paragraph upperDochDateInfo;
    private Span pruhLowerDateInfo = new Span();

    private Button loadTodayButton;
    private Button loadPrevDateButton;
    private Button loadNextDateButton;
    private Button loadLastDateButton;
    private Button removePruhZakBtn;
    private Button removeAllDochRecButton;

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




    HorizontalLayout middlePruhTitleBar;
    HorizontalLayout middleProhFooterBar;
    Grid<PruhZak> middlePruhGrid;
    List<PruhZak> middlePruhZakList = new ArrayList<>();
//    HorizontalLayout dochRecUpperFooter = new HorizontalLayout();

    HorizontalLayout pruhRecLowerHeader = new HorizontalLayout();
    Grid<Doch> lowerDochGrid;
    List<Doch> lowerDochList;
    HorizontalLayout dochRecLowerFooter = new HorizontalLayout();


    private Person pruhPerson;
    private LocalDate pruhRokMes;
    private LocalDate dochDatePrev;

//    Clock minuteClock = Clock.tickMinutes(ZoneId.systemDefault());
//    private TimeThread timeThread;

//    @Autowired
//    public DochRepo kontRepo;

    @Autowired
    public PersonService personService;

    @Autowired
    public CinRepo cinRepo;

    @Autowired
    public DochsumService dochsumService;



    //    @Autowired
//    public DochForm(Person pruhPerson) {
    public PruhView() {
//        super();
        buildForm();
//        initPruhData(pruhPerson, pruhRokMes);
    }

    @PostConstruct
    public void init() {
        initPruhData();
        zekSelectDialog = ConfirmDialog.createInfo()
                .withCaption("Přidání zakázky");
    }



    @Override
    protected void onAttach(AttachEvent attachEvent) {

        getLogger().info("## ON ATTACH DochView ##");

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        pruhRokMesSelectList.setLocale(new Locale("cs", "CZ"));
        pruhRokMesSelectList.setLocale(czLocale);

        dochDatePrev = LocalDate.now().minusDays(1);
        pruhLowerDateInfo.setText(dochDatePrev.format(lowerDochDateHeaderFormatter));

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
        getLogger().info("## Initializing pruh data");

        initPruhZakList();

        if (null == pruhPerson) {
            pruhPerson = pruhPersonList.stream()
                    .filter(person -> person.getUsername().toLowerCase().equals("vancik"))
                    .findFirst().orElse(null);
            pruhPersonCombo.setValue(pruhPerson);
        }

        if (null == pruhRokMes) {
//            pruhRokMes = LocalDate.of(2019, 1, 15);
            pruhRokMes = LocalDate.now();
            pruhRokMesSelectList.setValue(pruhRokMes);
        }
        updateMiddlePruhGridPane(pruhPerson, pruhRokMes);
    }

    private void initPruhZakList() {
        pruhPersonList = personService.fetchAllActive();
        pruhPersonCombo.setItems(pruhPersonList);
    }


    private String getPersonLabel(Person person) {
        return person.getUsername() + " (" + person.getJmeno() + " " + person.getPrijmeni() + ")";
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

        H3 pruhTitle = new H3("PROUŽEK");
        pruhTitle.getStyle()
                .set("margin-top", "10px")
                .set("margin-left", "20px")
        ;

        pruhHeader.add(
                pruhTitle
//                new H4("Uživatel(ka): ")
                , initPersonCombo()
//                , initPruhRokMesSelector()
        );


        pruhPanel.add(
                , buildVertSpace()
                , buildVertSpace()
        );


//        nepritControl.setWidth("30em");
//        nepritControl.getStyle().set("margin-top", "4.2em");

        pruhLowerDateInfo.setText("Odpracováno z docházky: ");

        pruhRecLowerHeader.getStyle()
                .set("margin-top", "2em");
        pruhRecLowerHeader.add(pruhLowerDateInfo);

        VerticalLayout pruhRecPane = new VerticalLayout();
        pruhRecPane.add(initMiddlePruhTitleBar());
        pruhRecPane.add(initMiddlePruhGrid());
//        pruhRecPane.add(initMiddlePruhFooterBar());

//        pruhRecPane.add(pruhRecLowerHeader);
//        pruhRecPane.add(initLowerPruhGrid());
//        pruhRecPane.add(dochRecLowerFooter);

        HorizontalLayout pruhPanel = new HorizontalLayout();
        pruhPanel.add(new Ribbon());
        pruhPanel.add(pruhRecPane);
        pruhPanel.add(new Ribbon());

        this.add(pruhHeader, pruhPanel);
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
        pruhPersonCombo = new ComboBox();
        pruhPersonCombo.setLabel(null);
        pruhPersonCombo.setWidth("20em");
        pruhPersonCombo.setItems(new ArrayList<>());
        pruhPersonCombo.setItemLabelGenerator(this::getPersonLabel);
        pruhPersonCombo.addValueChangeListener(event -> {
            pruhPerson = event.getValue();
            updateMiddlePruhGridPane(pruhPerson, pruhRokMes);
        });
        pruhPersonCombo.addBlurListener(event -> {
//            loadUpperDochGridData(pruhPerson.getId(), pruhRokMes);
            middlePruhGrid.getDataProvider().refreshAll();
        });
        return pruhPersonCombo;
    }

    private Component initPruhRokMesSelector() {
        pruhRokMesSelectList = new DatePicker();
        pruhRokMesSelectList.setLabel(null);
        pruhRokMesSelectList.setWidth("10em");
//        pruhRokMesSelectList.getStyle().set("margin-right", "1em");
        pruhRokMesSelectList.addValueChangeListener(event -> {
            pruhRokMes = event.getValue();
            updateMiddlePruhGridPane(pruhPerson, pruhRokMes);
        });
        return pruhRokMesSelectList;
    }

    private Component buildVertSpace() {
        Div vertSpace = new Div();
        vertSpace.setHeight("1em");
        return vertSpace;
    }

    private ValueProvider<Doch, String> durationValProv =
            doch -> null == doch.getDochDur() ? null : formatDuration(doch.getDochDur());

    private Component initMiddlePruhTitleBar() {
        middlePruhTitleBar = new HorizontalLayout();
        middlePruhTitleBar.setWidth("100%");
        middlePruhTitleBar.setSpacing(false);
        middlePruhTitleBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initPersonCombo()
        );

        middlePruhTitleBar.add(
                initPruhRokMesSelector()
                , initUpperDochDateInfo()
                , buttonBox
        );
        return middlePruhTitleBar;
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




    private Component initRemovePruhZakBtn() {
        Icon icon = VaadinIcon.REPLY.create();
        icon.setColor("crimson");
        removePruhZakBtn = new Button(icon);
        removePruhZakBtn.addClickListener(event -> {
//            if (canRemoveDochRec()) {
                ConfirmDialog.createQuestion()
                    .withCaption("Zákázka proužku")
                    .withMessage("Odstranit zakázku z proužku?")
                    .withOkButton(() -> {
                        removeZakFromPruh(pruhZak -> pruhZak.getZakId());
                        updateMiddlePruhGridPane(pruhPerson, pruhRokMes);
                    }, ButtonOption.focus(), ButtonOption.caption("ODSTRANIT"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
                ;
//            }
        });
        return removePruhZakBtn;
    }


    private Component initMiddlePruhGrid() {
        middlePruhGrid = new Grid<>();
        middlePruhGrid.setHeight("20em");
        middlePruhGrid.setColumnReorderingAllowed(false);
        middlePruhGrid.setClassName("vizman-simple-grid");
        middlePruhGrid.setSelectionMode(Grid.SelectionMode.NONE);

        middlePruhGrid.setItemDetailsRenderer(new ComponentRenderer<>(dochsum -> {
            Emphasis zakTextComp = new Emphasis(StringUtils.isBlank(dochsum.getZakText()) ? new Span("") : new Span(dochsum.getZakText()));
            zakTextComp.getStyle().set("margin-left", "16em");
            return zakTextComp;
        }));

        Binder<PruhZak> middleBinder = new Binder<>(PruhZak.class);

        middlePruhGrid.addColumn(PruhZak::getCkontCzak)
                .setHeader("CK/CZ")
                .setWidth("10em")
                .setFlexGrow(0)
                .setResizable(true)
        ;

        middlePruhGrid.addColumn(new ComponentRenderer<>(pruhZak -> {
            // Note: following icons MUST NOT be created outside this renderer (the KontFormDialog cannot be reopened)
            Icon icoItemDel = new Icon(VaadinIcon.FILE_REMOVE);
            icoItemDel.setSize("0.8em");
            icoItemDel.getStyle().set("theme", "small icon secondary");
            icoItemDel.setColor("crimson");
            return icoItemDel;
        }))
//                .setHeader("Man.")
                .setWidth("2em")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setResizable(false)
        ;

//        for (int i = 1; i < 32; i++) {
            middlePruhGrid.addColumn(PruhZak::getD01)
                    .setHeader("1")
                    .setWidth("2em")
                    .setFlexGrow(0)
                    .setResizable(false)
            ;

            middlePruhGrid.addColumn(PruhZak::getD02)
                    .setHeader("2")
                    .setWidth("2em")
                    .setFlexGrow(0)
                    .setResizable(false)
            ;
//        }
        return middlePruhGrid;
    }

    private BigDecimal getD01Sum() {
        BigDecimal d01Sum = BigDecimal.ZERO;
        for (PruhZak pruhZak : middlePruhZakList) {
            if (null != pruhZak && null != pruhZak.getD01()) {
                d01Sum = d01Sum.add(pruhZak.getD01());
            }
        }
        return d01Sum;
    }

    private BigDecimal getD02Sum() {
        BigDecimal d02Sum = BigDecimal.ZERO;
        for (PruhZak pruhZak : middlePruhZakList) {
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

    private void updateMiddlePruhGridPane(final Person pruhPerson, final LocalDate pruhDate) {
//        pruhRokMesSelectList.setValue(pruhRokMes);
//        fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(pruhRokMesSelectList, false));
        loadUpperDochGridData(pruhPerson, pruhDate);
        upperDochDateInfo.setText(null == pruhDate ? "" : pruhDate.format(upperDochDateHeaderFormatter));
    }

    private Component getD01Footer(BigDecimal d01) {
        Span comp = new Span(null == d01 ? null : getD01Sum().toString());
        return comp;
    }

    private Component getD02Footer(BigDecimal d02) {
        Span comp = new Span(null == d02 ? null : getD02Sum().toString());
        return comp;
    }


    private Component getDurationLabelFooter() {
        Paragraph comp = new Paragraph("Odpracováno: ");
//        comp.setWidth("5em");
        comp.getStyle().set("text-align", "end");
        return comp;
    }

    private void loadUpperDochGridData(Person dochPerson, LocalDate pruhDate) {
        if (null == dochPerson || null == dochPerson.getId() || null == pruhDate) {
            middlePruhZakList = new ArrayList();
        } else {
            middlePruhZakList = ...
        }
        middlePruhGrid.setItems(middlePruhZakList);
//        for (Doch doch : middlePruhZakList) {
//            middlePruhGrid.setDetailsVisible(doch, StringUtils.isNotBlank(doch.getPoznamka()));
//        }
        middlePruhGrid.getColumnByKey(D01_KEY)
                .setFooter(getD01Footer(getD01Sum()));
        middlePruhGrid.getColumnByKey(D02_KEY)
                .setFooter(getD02Footer(getD02Sum()));

        middlePruhGrid.getDataProvider().refreshAll();
    }

    private void transposeDochsumToPruhZak() {
        List<PruhZak> pruhZaks = dochsumService.fetchDochsumForPersonAndDate(pruhPerson.getId(), pruhDate);
    }


    private boolean canStampOdchod() {
        return checkDayDochIsOpened("nelze editovat.")
                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
//                && checkPersonIsInOffice("nelze zaznamenat odchod.")
        ;
    }

    private boolean canStampOdchodAlt() {
        return checkDayDochIsOpened("nelze editovat.")
//                && checkDochDateIsToday("je třeba použít 'Příchod jiný čas'.")
//                && checkPersonIsInOffice("nelze zaznamenat odchod.")
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
        if (middlePruhZakList.stream().noneMatch(Doch::isClosed)) {
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
        if (null != pruhRokMes) {
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
        if (null != pruhPerson) {
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
        if (null != pruhRokMes && pruhRokMes.equals(LocalDate.now())) {
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
        if (middlePruhZakList.stream().anyMatch(PruhZak::isRezieZak)) {
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
        return middlePruhZakList.size() > 0;
    }


}
