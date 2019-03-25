package eu.japtor.vizman.ui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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
import eu.japtor.vizman.ui.forms.ZakSelectFormDialog;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.*;
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

    private static final String COL_WIDTH = "2.5em";

//    private static final DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//    private static final DateTimeFormatter upperDochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE");
//    private static final DateTimeFormatter lowerDochDateHeaderFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy, EEEE");
//    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String DZ_KEY_PREF = "dz";
//    public static final String DS_KEY_PREF = "ds";
//    public static final String DP_KEY_PREF = "dp";
    private static final String ZAK_TEXT_COL_KEY = "zak-text-col";

//    private ConfirmDialog zakSelectDialog;

    private String authUsername = "vancik";

    private List<Person> pruhPersonList;
    private Person pruhPerson;
    private ComboBox<Person> pruhPersonSelector;

    private List<Calym> pruhCalymList;
    private Calym pruhCalym;
    private int pruhDayMax;
    private ComboBox<Calym> pruhCalymSelector;

    private  Icon pruhStatusIcon;

    private ZakSelectFormDialog zakSelectFormDialog;
    private static final Locale czLocale = new Locale("cs", "CZ");

    private HorizontalLayout gridZakTitleBar;
    private HorizontalLayout gridParagTitleBar;
    private HorizontalLayout gridZakButtonBar;

    private Button cancelEditButton;
    private Button saveEditButton;
    private Button zaksAddButton;
    private Button switchPruhStatusButton;

    private HorizontalLayout pruhTitleBar;

    private Grid<PruhZak> pruhZakGrid;
    private List<PruhZak> pruhZakList = new ArrayList<>();
    private PruhSum pruhSum;
    private FooterRow sumHodsFooterRow;
    private FooterRow missingHodsFooterRow;
    private Grid<PruhParag> pruhParagGrid;
    private List<PruhParag> pruhParagList = new ArrayList<>();
    private Comparator<PruhZak> pruhZakCkontComparator
            = (pz1, pz2) -> pz2.getCkont().equals("00001") ? -1 : pz1.getCkont().compareTo(pz2.getCkont());


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
    public KontService kontService;

    @Autowired
    public ZakService zakService;

    @Autowired
    public ParagRepo paragRepo;


    public PruhView() {
        super();
        buildForm();
    }

    @PostConstruct
    public void init() {
        initPruhData();
        zakSelectFormDialog = new ZakSelectFormDialog(
                this::addKzTreeAwareZaksToGrid
                , kontService
        );
    }

    private void addKzTreeAwareZaksToGrid(final List<KzTreeAware> kzZakList) {

        for (KzTreeAware kzZak : kzZakList) {
            kzZak.getCkont();
            boolean isZakInPruh = pruhZakList.stream()
                    .map(PruhZak::getZakId)
                    .anyMatch(zakId -> zakId.equals(kzZak.getItemId()))
            ;
            if (!isZakInPruh) {
                pruhZakList.add(new PruhZak(kzZak.getItemId(), kzZak.getTyp(), kzZak.getCkont(), kzZak.getCzak(), kzZak.getText()));
            }
        }

        Notification.show("Zakázky přidány"
                , 2500, Notification.Position.TOP_CENTER);

//        calcAndSetPruhMissingHods();
//        setPruhSumHods();
        pruhZakGrid.getDataProvider().refreshAll();
    }

    private void addPruhZaksToGrid(final List<PruhZak> pruhZakListToAdd) {

        if (pruhZakGrid.getEditor().isOpen()) {
            pruhZakGrid.getEditor().closeEditor();
        }
        for (PruhZak pzToAdd : pruhZakListToAdd) {
            pzToAdd.getCkont();
            boolean isZakInPruh = pruhZakList.stream()
                    .map(PruhZak::getZakId)
                    .anyMatch(zakId -> zakId.equals(pzToAdd.getZakId()))
            ;
            if (!isZakInPruh) {
                pruhZakList.add(new PruhZak(pzToAdd.getZakId(), pzToAdd.getItemType(), pzToAdd.getCkont(), pzToAdd.getCzak(), pzToAdd.getText()));
            }
        }

        Notification.show("Zakázky přidány"
                , 2500, Notification.Position.TOP_CENTER);

//        calcAndSetPruhMissingHods();
//        setPruhSumHods();
//        pruhZakGrid.setItems(pruhZakList);
        pruhZakGrid.getDataProvider().refreshAll();
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


    private void initPruhData() {
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
        calcAndSetPruhMissingHods();
        setPruhSumHods();

        pruhZakGrid.getDataProvider().refreshAll();
//        pruhSumGrid.getDataProvider().refreshAll();
        pruhParagGrid.getDataProvider().refreshAll();

//        for (PruhZak pruhZak : pruhZakList) {
//            pruhZakGrid.setDetailsVisible(pruchZak, StringUtils.isNotBlank(pruhZak.getParagText()));
//        }

    }

    private void loadPruhZakDataFromDb(Long personId, YearMonth ym) {
        if (null == personId || null == ym) {
            pruhZakList = new ArrayList<>();
        } else {


//            List<Dochsum> dochsums = dochsumService.fetchDochsumForPersonAndYm(dochPerson.getId(), pruhCalym);
////            List<DochsumZak> dochsumZaks = dochsumZakService.fetchDochsumForPersonAndYm(dochPerson.getId(), pruhDate)


            List<DochsumZak> dsZaks = dochsumZakService.fetchDochsumZaksForPersonAndYm(personId, ym);
            if (null == dsZaks) {
                pruhZakList = new ArrayList<>();
            } else {
                pruhZakList = transposeDochsumZaksToPruhZaks(dsZaks);
                pruhZakList.sort(pruhZakCkontComparator.reversed());
            }
        }
        pruhZakGrid.setItems(pruhZakList);
        setDayColumnsVisibility(pruhZakGrid, pruhDayMax, DZ_KEY_PREF);

//        ListDataProvider listDataProvider = (ListDataProvider) pruhZakGrid.getDataProvider();
//        ArrayList items = (new ArrayList(listDataProvider.getItems()));
//        int index = items.indexOf(item);
//        pruhZakGrid.scrollTo(index, ScrollDestination.END);

        pruhZakGrid.focus();
        UI.getCurrent().getPage().executeJavaScript("$0._scrollToIndex($1)", pruhZakGrid, 1);

    }

    private void calcAndSetPruhMissingHods() {
        for (int day = 1; day <= 31; day++) {
            Grid.Column col = pruhZakGrid.getColumnByKey(DZ_KEY_PREF + String.valueOf(day));
            if (null != col) {
                missingHodsFooterRow.getCell(col).setText(getMissingHodString(day));
            }
        }
    }

    private void setPruhSumHods() {
        for (int day = 1; day <= 31; day++) {
            Grid.Column col = pruhZakGrid.getColumnByKey(DZ_KEY_PREF + String.valueOf(day));
            if (null != col) {
                sumHodsFooterRow.getCell(col).setText(getSumHodString(day));
            }
        }
    }

    private void loadPruhParagDataFromDb(Long personId, YearMonth ym) {
        if (null == personId || null == ym) {
            pruhParagList = new ArrayList<>();
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
//            pruhSumList = new ArrayList();
            pruhSum = new PruhSum("suma z dochazky");
        } else {
            List<Dochsum> dochsums = dochsumService.fetchDochsumForPersonAndYm(personId, ym);
            if (null == dochsums) {
//                pruhSumList = new ArrayList<>();
                pruhSum = new PruhSum("suma z dochazky");
            } else {
//                pruhSumList = transposeDochsumsToPruhSums(dochsums);
                pruhSum = transposeDochsumsToPruhSums(dochsums);
//                pruhZakSum = new PruhZak("suma z dochazky", "");
            }
        }
//        pruhZakSumGrid.setItems(pruhSumList);
//        setDayColumnsVisibility(pruhSumGrid, pruhDayMax, "ds");
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
//        this.setWidth("1200px");
        this.setWidth("100%");
        this.setAlignSelf(Alignment.CENTER);
//        this.getStyle().set("margin-top", "2em");
//        this.getStyle().set("margin-bottom", "2em");
//        this.getStyle().set("background-color", "#fffcf5");
//        this.getStyle().set("background-color", "#e1dcd6");
//        this.getStyle().set("background-color", "#fcfffe");
//        this.getStyle().set("background-color", "LightYellow");
//        this.getStyle().set("background-color", "#fefefd");

        VerticalLayout pruhPanel = new VerticalLayout();
        pruhPanel.add(initPruhTitleBar());
        pruhPanel.add(
                initZakGridTitleBar()
                , initZakGrid()
//                , initSumGrid()
                , initZakGridButtonBar()
//                , initZakGridSumTitle()
                , initGridPargaTitleBar()
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
        // TODO: disable when pruh is loaded, enable when changed (either hodPrac changed, or zak added/deleted)
//        saveEditButton.setEnabled(false);
    }

//    private Component initZakGridSumTitle() {
//        gridZakSumTitle = new Span("Odpracováno z docházky: ");
//        gridZakSumTitle.getStyle()
//                .set("margin-top", "2em");
//        return gridZakSumTitle;
//    }

    private Component initZakGridTitleBar() {
        gridZakTitleBar = new HorizontalLayout();
//        gridZakTitleBar.setHeight("4em");
        Emphasis gridZakTitle = new Emphasis("Odpracováno na zakázkách");
        gridZakTitle.getStyle()
                .set("font-weight", "bold")
//                .set("margin-top", "2em")
                .set("margin-bottom", "0.2em")
        ;
        gridZakTitleBar.add(gridZakTitle);
        return gridZakTitleBar;
    }

    private Component initZakGridButtonBar() {
        gridZakButtonBar = new HorizontalLayout();
        gridZakButtonBar.setJustifyContentMode(JustifyContentMode.CENTER);
        gridZakButtonBar.setWidthFull();
        gridZakButtonBar.getStyle()
                .set("margin-top", "0.5em");
        gridZakButtonBar.add(
                initSaveEditButton()
                , new Gap()
                , initCancelEditButton());
        return gridZakButtonBar;
    }

    private Component initGridPargaTitleBar() {
        gridParagTitleBar = new HorizontalLayout();
//        gridParagTitleBar.setHeight("4em");
        Emphasis gridParagTitle = new Emphasis("Nepřítomnost");
        gridParagTitle.getStyle()
//                .set("margin-top", "2em")
                .set("font-weight", "bold")
                .set("margin-bottom", "0.2em")
        ;
        gridParagTitleBar.add(gridParagTitle);
        return gridParagTitleBar;
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
            pruhDayMax = (null == pruhCalym || null == pruhCalym.getCalYm()) ? 0 : pruhCalym.getCalYm().lengthOfMonth();
            updatePruhGrids(pruhPerson, pruhCalym);
        });
        return pruhCalymSelector;
    }

    private Component initPruhStatusIcon() {
        pruhStatusIcon = VaadinIcon.UNLOCK.create();
        pruhStatusIcon.setColor("green");
        pruhStatusIcon.getStyle().set("margin-right", "1em");
        return pruhStatusIcon;
    }

    private Component initPruhSwitchStatusButton() {
        String LOCK_PRUH_CAPTION = "Uzavřít proužek";
        String UNLOCK_PRUH_CAPTION = "Otevřít proužek";
        switchPruhStatusButton = new Button(LOCK_PRUH_CAPTION);
        switchPruhStatusButton.addClickListener(event -> {
            if (pruhZakGrid.getEditor().isOpen()) {
                pruhZakGrid.getEditor().closeEditor();
            }

            ConfirmDialog.createInfo()
                    .withCaption("UZAVŘENÍ PROUŽKU")
                    .withMessage("Zatím to nedělá nic. Ale bude.")
                    .open()
            ;
//            return;

//            if (null == pruhPerson || null == pruhCalym) {
//            }

            if (false) {
                ConfirmDialog.createInfo()
                        .withCaption("UZAVŘENÍ PROUŽKU")
                        .withMessage("V proužku jsou nevyplněné hodiny na zakázkách, proužek nelze uzavřít.")
                        .open()
                ;
                return;
            }

            if (false) {
                ConfirmDialog.createInfo()
                        .withCaption("OTEVŘENÍ PROUŽKU")
                        .withMessage("Je otevřen proužek YYYY-MM. Dokud nevbvude uzavřen, nelze otevřít jinný proužek.")
                        .open()
                ;
                return;
            }

            if (false) {
                ConfirmDialog.createInfo()
                        .withCaption("OTEVŘENÍ PROUŽKU")
                        .withMessage("Proužek už byl analyticky zpracován, otevřít ho může jen administrátor.")
                        .open()
                ;
                return;
            }
        });
        return switchPruhStatusButton;
    }

    private String getYmLabel(Calym calym) {
        return null == calym || null == calym.getCalYm() ? "" : calym.getCalYm().toString();
    }

//    private ValueProvider<Doch, String> durationValProv =
//            doch -> null == doch.getDochDur() ? null : formatDuration(doch.getDochDur());

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
                , initPruhStatusIcon()
                , initPruhSwitchStatusButton()
        );

        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.add(
                initZaksCopyButton()
                , initZaksAddButton()
        );

        pruhTitleBar.add(
                pruhTitle
                , selectorBox
                , buttonBox
        );
        return pruhTitleBar;
    }


    private void removeZakFromPruh(Long zakId) {
        // TODO: remove zak, update DB, load from DB, update view
    }

    private Component initZakGrid() {
        pruhZakGrid = new Grid<>();
        pruhZakGrid.setHeight("50em");
//        pruhZakGrid.setWidth("100%");
        pruhZakGrid.setColumnReorderingAllowed(false);
        pruhZakGrid.setClassName("vizman-pruh-grid");
        pruhZakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        pruhZakGrid.addThemeNames("column-borders", "row-stripes");
//        pruhZakGrid.addThemeNames("no-border", "no-row-borders", "row-stripes");
//        pruhZakGrid.addThemeNames("border", "row-borders", "row-stripes");

        Binder<PruhZak> pzBinder = new Binder<>(PruhZak.class);
        Editor<PruhZak> pzEditor = pruhZakGrid.getEditor();
        pzEditor.setBinder(pzBinder);

        pruhZakGrid.addColumn(PruhZak::getPruhCellText)
                .setHeader("ČK / ČZ, zakázka")
//                .setFooter("Zbývá vyplnit")
                .setWidth("10em")
                .setTextAlign(ColumnTextAlign.START)
                .setFlexGrow(1)
                .setFrozen(true)
                .setKey(ZAK_TEXT_COL_KEY)
                .setResizable(true)
        ;

        pruhZakGrid.addColumn(new ComponentRenderer<>(this::buildPruhZakRemoveBtn))
//        pruhZakGrid.addColumn(new ComponentRenderer<>(this::buildFaktEditBtn))
                .setWidth("5em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0)
                .setFrozen(true)
                .setResizable(false)
        ;

        pruhZakGrid.addItemDoubleClickListener(event -> {
            pzEditor.editItem(event.getItem());
//            field.focus();
        });

        missingHodsFooterRow = pruhZakGrid.appendFooterRow();
        sumHodsFooterRow = pruhZakGrid.appendFooterRow();
        missingHodsFooterRow.getCell(pruhZakGrid.getColumnByKey(ZAK_TEXT_COL_KEY))
                .setText("Zbývá vyplnit")
        ;
        Paragraph sumHodsTextComp = new Paragraph("Odpracováno z docházky");
        sumHodsTextComp.getStyle().set("text-align", ColumnTextAlign.START.toString());
        sumHodsFooterRow.getCell(pruhZakGrid.getColumnByKey(ZAK_TEXT_COL_KEY))
                .setComponent(sumHodsTextComp)
//                .setText("Odpracováno z docházky")
        ;

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


    private Component initParagGrid() {
        pruhParagGrid = new Grid<>();
        pruhParagGrid.setHeight("20em");
        pruhParagGrid.setColumnReorderingAllowed(false);
        pruhParagGrid.setClassName("vizman-pruh-grid");
        pruhParagGrid.setSelectionMode(Grid.SelectionMode.NONE);
        pruhParagGrid.addThemeNames("column-borders", "row-stripes");


//        Binder<PruhParag> paragBinder = new Binder<>(PruhParag.class);
//        pruhParagGrid.getEditor().setBinder(paragBinder);

        pruhParagGrid.addColumn(PruhParag::getPruhCellText)
                .setHeader("Důvod")
                .setWidth("10em")
                .setTextAlign(ColumnTextAlign.START)
                .setFlexGrow(1)
                .setFrozen(true)
                .setResizable(true)
        ;

        pruhParagGrid.addColumn(new ComponentRenderer<>(pruhParag -> new Span("")))
                .setWidth("5em")
                .setFlexGrow(0)
                .setFrozen(true)
                .setResizable(false)
        ;

//        pruhParagGrid.addColumn(new ComponentRenderer<>(pruhParag ->
//                new Span(pruhParag.getPruhCellText())))
//                .setWidth("5em")
//                .setTextAlign(ColumnTextAlign.START)
//                .setFlexGrow(0)
//                .setFrozen(true)
//                .setResizable(false)
//        ;

        for (int i = 1; i <= 31; i++) {
            int ii = i;
            addParagDayColumn(ii, pp -> pp.getHod(ii));
        }

        return pruhParagGrid;
    }


    private void addZakDayColumn(
            int day
            , Binder<PruhZak> pzBinder
            , Editor<PruhZak> pzEditor
            , ValueProvider<PruhZak, BigDecimal> pzHodValProv
            , Setter<PruhZak, BigDecimal> pzHodSetter)
    {
        Grid.Column<PruhZak> col = pruhZakGrid.addColumn(
                new ComponentRenderer<>(pruhZak ->
                        VzmFormatUtils.getDecHodComponent(pzHodValProv.apply(pruhZak)))
        );

        col.setHeader(Integer.valueOf(day).toString())
            .setWidth(COL_WIDTH)
//            .setTextAlign(ColumnTextAlign.END)
            .setFlexGrow(0)
            .setKey("dz" + String.valueOf(day))
            .setResizable(false)
        ;
        // TODO: remove margins
//        col.getElement().setProperty("margin", "0");
//        col.getElement().setAttribute("margin", "0");

//        Component sumFooterComp =
//        sumHodsFooterRow.getCell(col)
//                .setComponent();

        TextField editComp = new TextField();
        editComp.addValueChangeListener(event -> {
            if (event.isFromClient() && (StringUtils.isNotBlank(event.getValue()) || StringUtils.isNotBlank(event.getOldValue()))
                    && !event.getValue().equals(event.getOldValue())) {
                try {
                    // TODO: try to use localization instead of regex ?
//                    pzEditor.getItem().setValueToDayField(day
                    pzEditor.getItem().setHod(day
                            , StringUtils.isBlank(event.getValue()) ?
                                    null : new BigDecimal(event.getValue().replaceAll(",", ".")));
                    pzBinder.writeBean(pzEditor.getItem());
//                    col.setFooter(buildDayHodSumComp(getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day))));
//                    BigDecimal missingHods = getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day));
                    missingHodsFooterRow.getCell(col)
                            .setText(getMissingHodString(day));
                    saveEditButton.setEnabled(pzBinder.hasChanges());

                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            }
        });
        // TODO: remove margins
        editComp.getStyle()
                .set("margin", "0")
                .set("padding", "0")
//                .set("width", COL_WIDTH)
                .set("width", "3.5em")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("height", "1.8m")
                .set("min-height", "1.8em")
                .set("--lumo-text-field-size", "var(--lumo-size-s)")
        ;
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

    }

    private String getMissingHodString(int day) {
        BigDecimal missingHods = getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day));
        return null == missingHods ? "" : VzmFormatUtils.decHodFormat.format(missingHods);
    }

    private String getSumHodString(int day) {
        BigDecimal sumHods = getDayZakHodSum(day);
        return null == sumHods ? "" : VzmFormatUtils.decHodFormat.format(sumHods);
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
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        ;
        if (hodList.size() == 0) {
            return null;
        } else {
            return hodList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    private BigDecimal getDaySumHodSum(Integer day) {
        return pruhSum.getHod(day);
    }


    private void addParagDayColumn(
            int day, ValueProvider<PruhParag, BigDecimal> ppHodValProv)
    {
        Grid.Column<PruhParag> col = pruhParagGrid.addColumn( new ComponentRenderer<>(pruhParag ->
                VzmFormatUtils.getDecHodComponent(ppHodValProv.apply(pruhParag))));
//                StringToBigDecimalConverter(valProv));
        col.setHeader(String.valueOf(day))
                .setWidth(COL_WIDTH)
//                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0)
                .setKey(DZ_KEY_PREF + String.valueOf(day))
                .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");
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


//    private BigDecimal getHodSum(Integer day) {
//        BigDecimal hodSum = BigDecimal.ZERO;
//        for (PruhZak pruhZak : pruhZakList) {
//            if (null != pruhZak && null != pruhZak.getHod(day)) {
//                hodSum = hodSum.add(pruhZak.getD01());
//            }
//        }
//        return hodSum;
//    }


//    public static String formatDuration(Duration duration) {
//        long seconds = duration.getSeconds();
//        long absSeconds = Math.abs(seconds);
//        String positive = String.format(
////                "%d:%02d:%02d",
//                "%d:%02d",
//                absSeconds / 3600,
//                (absSeconds % 3600) / 60);
////                absSeconds % 60);
//        return seconds < 0 ? "-" + positive : positive;
//    }


    private List<PruhZak> transposeDochsumZaksToPruhZaks(List<DochsumZak> dsZaks) {

        List<Long> zakIds = dsZaks.stream()
                .map(DochsumZak::getZakId)
                .distinct()
                .collect(Collectors.toList())
        ;

        List<Zak> zaks = zakService.fetchByIds(zakIds);
        List<PruhZak> pruhZaks = new ArrayList<>();
        for (Zak zak : zaks) {
            PruhZak pzak = new PruhZak(zak.getId(), zak.getTyp(), zak.getCkont(), zak.getCzak(), zak.getText());
            for (DochsumZak dsZak : dsZaks) {
                if (dsZak.getZakId().equals(zak.getId())) {
                    int dayOm = dsZak.getDsDate().getDayOfMonth();
//                    pzak.setValueToDayField(dayOm, dsZak.getDszWorkPruh());
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

        List<DochsumZak> dsZaks = new ArrayList<>();
        for (PruhZak pzak : pruhZaks) {
//            Long zakId = pzak.getZakId();
            for (int i = 1; i <= pruhDayMax; i++) {
                BigDecimal cellHod = pzak.getHod(i);
                if (null != cellHod && cellHod.compareTo(BigDecimal.ZERO) != 0) {
                    LocalDate cellDate = pruhCalym.getCalYm().atDay(i);
                    DochsumZak dsZak = new DochsumZak(pruhPerson.getId(), cellDate, pzak.getZakId());
                    dsZak.setDszWorkPruh(cellHod);
                    // TODO mzda
                    // TODO pojistne
                    // TODO normo, skutecne...
                    dsZaks.add(dsZak);
                }
            }
        }
        return dsZaks;
    }


    private PruhSum transposeDochsumsToPruhSums(List<Dochsum> dsSums) {
//        List<PruhSum> pruhSums = new ArrayList();
//        for (Parag parag : parags) {
            PruhSum pruhSum = new PruhSum("Odpracováno z docházky");
            for (Dochsum dsSum : dsSums) {
                int dayOm = dsSum.getDsDate().getDayOfMonth();
                pruhSum.setHod(dayOm, dsSum.getDsWorkPruh());
            }
//            pruhSums.add(psum);
//        }
        return pruhSum;
    }

    private List<PruhParag> transposeDochsumParagsToPruhParags(List<DochsumParag> dsParags) {

        List<Long> paragIds = dsParags.stream()
                .map(DochsumParag::getParagId)
                .distinct()
                .collect(Collectors.toList())
                ;

        List<Parag> parags = paragRepo.findAllById(paragIds);
        List<PruhParag> pruhParags = new ArrayList<>();
        for (Parag parag : parags) {
            PruhParag pruhPar = new PruhParag(parag.getCparag(), parag.getTyp(), parag.getText());
            for (DochsumParag dsPar : dsParags) {
                if (dsPar.getParagId().equals(parag.getId())) {
                    int dayOm = dsPar.getDsDate().getDayOfMonth();
                    pruhPar.setHod(dayOm, dsPar.getDspWorkOff());
                }
            }
            pruhParags.add(pruhPar);
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
                        saveEditButton.setEnabled(false);
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


    private Component initZaksAddButton() {
        zaksAddButton = new Button("Přidat zakázky");
        zaksAddButton.addClickListener(event -> {
            if (pruhZakGrid.getEditor().isOpen()) {
                pruhZakGrid.getEditor().closeEditor();
            }
            if (null == pruhPerson || null == pruhCalym) {
                ConfirmDialog.createInfo()
                        .withCaption("PŘIDÁNÍ ZAKÁZEK")
                        .withMessage("Nejprve musí být vybrán proužek do něhož se bude přidávat.")
                        .open()
                ;
                return;
            }
            zakSelectFormDialog.openDialog("PŘIDÁNÍ ZAKÁZEK");
        });
        return zaksAddButton;
    }

    private Component initZaksCopyButton() {
        zaksAddButton = new Button("Nakopírovat zakázky");
        zaksAddButton.addClickListener(event -> {
            if (pruhZakGrid.getEditor().isOpen()) {
                pruhZakGrid.getEditor().closeEditor();
            }
            if (null == pruhPerson || null == pruhCalym) {
                ConfirmDialog.createInfo()
                        .withCaption("KOPÍROVÁNÍ ZAKÁZEK")
                        .withMessage("Nejprve musí být vybrán proužek do něhož se bude kopírovat.")
                        .open()
                ;
                return;
            }

            YearMonth lastYm = getLastUserPruhYm(pruhPerson.getId());
            if (null == lastYm) {
                ConfirmDialog.createInfo()
                        .withCaption("KOPÍROVÁNÍ ZAKÁZEK")
                        .withMessage(String.format("Nenalezen žádný proužek uživatele %s.", pruhPerson.getUsername()))
                        .open()
                ;
                return;
            }

            ConfirmDialog.createQuestion()
                    .withCaption("KOPÍROVÁNÍ ZAKÁZEK")
                    .withMessage(String.format("Nakopírovat zakázky z posledního známého proužku %s ?", lastYm))
                    .withCancelButton()
                    .withYesButton(() -> {
                        List<DochsumZak> lastDsZaks = dochsumZakService.fetchDochsumZaksForPersonAndYm(pruhPerson.getId(), lastYm);
                        if (null == lastDsZaks) {
                            ConfirmDialog.createInfo()
                                    .withCaption("KOPÍROVÁNÍ ZAKÁZEK")
                                    .withMessage(String.format("V  posledním proužku uživatele %s nenalezena žádná data.", pruhPerson.getUsername()))
                                    .open()
                            ;
                            return;
                        }
                        List<PruhZak> lastPruhZakList = transposeDochsumZaksToPruhZaks(lastDsZaks);
                        lastPruhZakList.sort(pruhZakCkontComparator.reversed());
                        addPruhZaksToGrid(lastPruhZakList);
                    })
                    .open()
            ;
        });
        return zaksAddButton;
    }

    private YearMonth getLastUserPruhYm(Long userId) {
        return YearMonth.of(2018, 12);
    }

    private boolean checkPruhZaksIsRezie(final String additionalMsg) {
        if (pruhZakList.stream().anyMatch(PruhZak::isRezieZak)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam proužku")
                .withMessage(String.format("Jedná se o režijní zakázku %s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }

    private boolean checkPruhZaksIsLekar(final String additionalMsg) {
        if (pruhZakList.stream().anyMatch(PruhZak::isLekarZak)) {
            return true;
        }
        ConfirmDialog
                .createInfo()
                .withCaption("Záznam proužku")
                .withMessage(String.format("Jedná se o návštěvu u lékaře %s", adjustAdditionalMsg(additionalMsg)))
                .withOkButton()
                .open();
        return false;
    }


    private String adjustAdditionalMsg(final String additionalMsg) {
        return StringUtils.isBlank(additionalMsg) ? "." : " - " + additionalMsg;
    }
}
