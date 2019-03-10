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
import com.vaadin.flow.component.select.Select;
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
import eu.japtor.vizman.backend.service.DochsumZakService;
import eu.japtor.vizman.backend.service.PersonService;
import eu.japtor.vizman.ui.MainView;
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
import java.util.Locale;
import java.util.Objects;

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

    private Person pruhPerson;
    private YearMonth pruhYm;
//    private LocalDate dochDatePrev;

    private List<Person> pruhPersonList;
    private ComboBox<Person> pruhPersonCombo;
//    private DatePicker pruhYmSelectList;
    private Select<YearMonth> pruhYmSelector;



    private static final Locale czLocale = new Locale("cs", "CZ");

//    private HorizontalLayout pruhHeader = new HorizontalLayout();

    VerticalLayout clockContainer;
    private Span clockDisplay = new Span();

    //    private Span middleDochDateInfo = new Span();
    private Paragraph middleDochDateInfo;
    private Span pruhLowerDateInfo = new Span();

    private Button loadTodayButton;
    private Button loadPrevDateButton;
    private Button loadNextDateButton;
    private Button loadLastDateButton;
//    private Button removePruhZakBtn;
    private Button cancelEditButton;

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

    @Autowired
    public DochsumZakService dochsumZakService;



    //    @Autowired
//    public DochForm(Person pruhPerson) {
    public PruhView() {
//        super();
        buildForm();
//        initPruhData(pruhPerson, pruhYm);
    }

    @PostConstruct
    public void init() {
        pruhPerson = personService.getByUsername("vancik");
        pruhYm = YearMonth.now();

        initPruhData();
        zakSelectDialog = ConfirmDialog.createInfo()
                .withCaption("Přidání zakázky");
    }



    @Override
    protected void onAttach(AttachEvent attachEvent) {

        getLogger().info("## ON ATTACH DochView ##");

        // Set locale here, because when it is set in constructor, it is effective only in first open,
        // and next openings show date in US format
//        pruhYmSelectList.setLocale(new Locale("cs", "CZ"));
//        pruhYmSelector.setLocale(czLocale);

//        dochDatePrev = LocalDate.now().minusDays(1);
//        pruhLowerDateInfo.setText(dochDatePrev.format(lowerDochDateHeaderFormatter));

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
        if (null == pruhPerson) {
            pruhPerson = pruhPersonList.stream()
                    .filter(person -> person.getUsername().toLowerCase().equals("vancik"))
                    .findFirst().orElse(null);
            pruhPersonCombo.setValue(pruhPerson);
        }

        if (null == pruhYm) {
//            pruhYm = LocalDate.of(2019, 1, 15);
            pruhYm = YearMonth.now();
            pruhYmSelector.setValue(pruhYm);
        }
        updatePruhGridPane(pruhPerson, pruhYm);
    }

    private void loadPersonDataFromDb() {
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

        pruhPanel.add(
                new Ribbon()
                , pruhRecPane
                , new Ribbon()
                , buildVertSpace()
                , buildVertSpace()
        );

        this.add(pruhPanel);
    }

    private Component initPruhYmSelector() {
        pruhYmSelector = new Select<>();
        pruhYmSelector.getStyle().set("margin-right", "1em");
        pruhYmSelector.setWidth("10em");
        pruhYmSelector.setItems(YearMonth.now());
        // The empty selection item is the first item that maps to an null item.
        // As the item is not selectable, using it also as placeholder
        pruhYmSelector.setPlaceholder("Vyber...");
        pruhYmSelector.setEmptySelectionCaption("Vyber...");
        pruhYmSelector.setEmptySelectionAllowed(true);
        pruhYmSelector.setItemEnabledProvider(Objects::nonNull);
        // add a divider after the empty selection item
        pruhYmSelector.addComponents(null, new Hr());
        pruhYmSelector.addValueChangeListener(event -> {
            pruhYm = event.getValue();
            updatePruhGridPane(pruhPerson, pruhYm);
        });
        return pruhYmSelector;
    }

//    private Component initPruhRokMesSelector() {
//        pruhYmSelectList = new DatePicker();
//        pruhYmSelectList.setLabel(null);
//        pruhYmSelectList.setWidth("10em");
////        pruhYmSelectList.getStyle().set("margin-right", "1em");
//        pruhYmSelectList.addValueChangeListener(event -> {
//            pruhYm = event.getValue();
//            updatePruhGridPane(pruhPerson, pruhYm);
//        });
//        return pruhYmSelectList;
//    }


    private Component initUpperDochDateInfo() {
        middleDochDateInfo = new Paragraph();
        middleDochDateInfo.getStyle()
                .set("font-weight", "bold")
                .set("margin-right", "0.8em")
        ;
        middleDochDateInfo.setText("Den docházky...");
        return middleDochDateInfo;
    }

    private Component initPersonCombo() {
        pruhPersonCombo = new ComboBox<>();
        pruhPersonCombo.setLabel(null);
        pruhPersonCombo.setWidth("20em");
        pruhPersonCombo.setItems(new ArrayList<>());
        pruhPersonCombo.setItemLabelGenerator(this::getPersonLabel);
        pruhPersonCombo.addValueChangeListener(event -> {
            pruhPerson = event.getValue();
            updatePruhGridPane(pruhPerson, pruhYm);
        });
        pruhPersonCombo.addBlurListener(event -> {
//            loadMiddleDochGridData(pruhPerson.getId(), pruhYm);
            middlePruhGrid.getDataProvider().refreshAll();
        });
        return pruhPersonCombo;
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

        H3 pruhTitle = new H3("PROUŽEK");
        pruhTitle.getStyle()
                .set("margin-top", "10px")
//                .set("margin-left", "20px")
        ;

        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initCancelEditButton()
        );

        middlePruhTitleBar.add(
                pruhTitle
                , initPersonCombo()
                , initPruhYmSelector()
//                , initUpperDochDateInfo()
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






    private void removeZakFromPruh(long zakId) {

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

        middlePruhGrid.addColumn(new ComponentRenderer<>(this::buildPruhZakRemoveBtn))
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
                                    updatePruhGridPane(pruhPerson, pruhYm);
                                }, ButtonOption.focus(), ButtonOption.caption("ODSTRANIT")
                        )
                        .withCancelButton(ButtonOption.caption("ZPĚT"))
                        .open()

        );
        return removePruhZakBtn;
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

    private void updatePruhGridPane(final Person pruhPerson, final YearMonth pruhYm) {
//        pruhYmSelectList.setValue(pruhYm);
//        fireEvent(new GeneratedVaadinDatePicker.ChangeEvent(pruhYmSelectList, false));
        loadMiddleDochGridData(pruhPerson, pruhYm);
//        middleDochDateInfo.setText(null == pruhYm ? "" : pruhYm.format(yearMonthFormatter));
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

    private void loadMiddleDochGridData(Person dochPerson, YearMonth pruhYm) {
        if (null == dochPerson || null == dochPerson.getId() || null == pruhYm) {
            middlePruhZakList = new ArrayList();
        } else {


//            List<Dochsum> dochsums = dochsumService.fetchDochsumForPersonAndYm(dochPerson.getId(), pruhYm);
////            List<DochsumZak> dochsumZaks = dochsumZakService.fetchDochsumForPersonAndYm(dochPerson.getId(), pruhDate)


            middlePruhZakList = new ArrayList<>();
            middlePruhZakList.add(new PruhZak("9545.1-1 / 1", "Zakazkovy text"));
        }
        middlePruhGrid.setItems(middlePruhZakList);
//        for (Doch doch : middlePruhZakList) {
//            middlePruhGrid.setDetailsVisible(doch, StringUtils.isNotBlank(doch.getPoznamka()));
//        }


//        middlePruhGrid.getColumnByKey(D01_KEY)
//                .setFooter(getD01Footer(getD01Sum()));
//        middlePruhGrid.getColumnByKey(D02_KEY)
//                .setFooter(getD02Footer(getD02Sum()));

        middlePruhGrid.getDataProvider().refreshAll();
    }

    private void transposeDochsumToPruhSum() {
        List<Dochsum> pruhSums = dochsumService.fetchDochsumForPersonAndYm(pruhPerson.getId(), pruhYm);
    }

    private void transposeDochsumZaksToPruhZaks() {
        List<DochsumZak> pruhZaks = dochsumZakService.fetchDochsumZaksForPersonAndYm(pruhPerson.getId(), pruhYm);
    }



    private Component initCancelEditButton() {
        Icon icon = VaadinIcon.REPLY_ALL.create();
        icon.setColor("crimson");
        cancelEditButton = new Button(icon);
        cancelEditButton.addClickListener(event -> {
            ConfirmDialog.createQuestion()
                    .withCaption("Editace proužku")
                    .withMessage("Zrušit všechny změny proužku od posledního uložení?")
                    .withOkButton(() -> {
//                        loadMiddleDochGridData(pruhPerson, pruhYm);
                        updatePruhGridPane(pruhPerson, pruhYm);
                    }, ButtonOption.focus(), ButtonOption.caption("ZRUŠIT ZMĚNY"))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .open()
            ;
        });
        return cancelEditButton;
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
//        if (middlePruhZakList.stream().noneMatch(Doch::isClosed)) {
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
//        if (null != pruhYm) {
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
//        if (null != pruhYm && pruhYm.equals(LocalDate.now())) {
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
