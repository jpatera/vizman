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
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.app.security.SecurityUtils;
import eu.japtor.vizman.backend.bean.PruhParag;
import eu.japtor.vizman.backend.bean.PruhSum;
import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.*;
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

import static eu.japtor.vizman.app.security.SecurityUtils.canViewOtherUsers;
import static eu.japtor.vizman.backend.entity.Pruh.PRUH_STATE_LOCKED;
import static eu.japtor.vizman.backend.entity.Pruh.PRUH_STATE_UNLOCKED;
import static eu.japtor.vizman.ui.util.VizmanConst.ROUTE_PRUH;

@Route(value = ROUTE_PRUH, layout = MainView.class)
@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
        , Perm.PRUH_USE
})
@SpringComponent
@UIScope
public class PruhView extends VerticalLayout implements HasLogger, BeforeEnterListener {

    private static final String COL_WIDTH = "2.4em";

//    private static final DateTimeFormatter dochTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//    private static final DateTimeFormatter upperDochDateHeaderFormatter = DateTimeFormatter.ofPattern("EEEE");
//    private static final DateTimeFormatter lowerDochDateHeaderFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy, EEEE");
//    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String DZ_KEY_PREF = "dz";
//    public static final String DS_KEY_PREF = "ds";
    public static final String DP_KEY_PREF = "dp";
    private static final String ZAK_TEXT_COL_KEY = "zak-text-col";

    String LOCK_PRUH_BUTTON_TEXT = "Uzavřít proužek";
    String UNLOCK_PRUH_BUTTON_TEXT = "Otevřít proužek";

//    private ConfirmDialog zakSelectDialog;

//    private String authUsername = "vancik";
    private String authUsername;
    private YearMonth currentYm;

    private List<Person> pruhPersonList;
    private Person pruhPerson;
    private ComboBox<Person> pruhPersonSelector;

//    private List<Calym> pruhCalymList;
    private List<YearMonth> pruhCalymList;
//    private Calym pruhYm;
    private YearMonth pruhYm;
    private int pruhDayMax;
//    private ComboBox<Calym> pruhYmSelector;
    private ComboBox<YearMonth> pruhYmSelector;

    private Integer pruhState;

    private  Icon pruhStateIconUnlocked;
    private  Icon pruhStateIconLocked;

    private ZakSelectFormDialog zakSelectFormDialog;
    private static final Locale czLocale = new Locale("cs", "CZ");

    private HorizontalLayout gridZakTitleBar;
    private HorizontalLayout gridParagTitleBar;
    private HorizontalLayout gridZakButtonBar;

    private Button cancelEditButton;
    private Button saveEditButton;
    private Button zaksCopyButton;
    private Button zaksAddButton;
    private Button togglePruhStateButton;

    private HorizontalLayout pruhTitleBar;

    private Grid<PruhZak> pruhZakGrid;
    private List<PruhZak> pruhZakList = new ArrayList<>();
    Registration zakGridEditRegistration = null;

    private PruhSum pruhSum;
    private FooterRow sumHodsFooterRow;
    private FooterRow missingHodsFooterRow;
    private Grid<PruhParag> pruhParagGrid;
    private List<PruhParag> pruhParagList = new ArrayList<>();
    private Comparator<PruhZak> pruhZakOrderComparator
            = (pz1, pz2) -> {
                if (pz1.equals(pz2))
                    return 0;
                if (pz1.isRezijniZak())
                    return -1;
                if (pz2.isRezijniZak())
                    return +1;
                return pz2.getCkont().compareTo(pz1.getCkont());
            }
    ;

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

    @Autowired
    public PruhRepo pruhRepo;

    @Autowired
    public PersonWageRepo personWageRepo;


    public PruhView() {
        super();
        buildForm();
    }


    @PostConstruct
    public void init() {
//        !SecurityUtils.isAccessGranted(event.getNavigationTarget())
        authUsername = SecurityUtils.getUsername();
//        pruhYm = YearMonth.now();
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
//        pruhYmSelector.setLocale(czLocale);

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
        Person pruhPersonByAuth = getPruhPersonFromList(authUsername).orElse(null);
        pruhPersonSelector.setValue(pruhPersonByAuth);

        loadCalymDataFromDb();
        YearMonth pruhYmByToday = getYmFromCalymListByYm(YearMonth.now())
                .orElse(null);
        pruhYmSelector.setValue(pruhYmByToday);

        updatePruhGrids(pruhPerson, pruhYm);
    }


    private Optional<Person> getPruhPersonFromList(String username) {
        return pruhPersonList.stream()
                .filter(person -> person.getUsername().toLowerCase().equals(username.toLowerCase()))
                .findFirst();
//                .findFirst().orElse(null);
    }

//    private  Optional<Calym> getYmFromCalymListByYm(final YearMonth ym) {
    private  Optional<YearMonth> getYmFromCalymListByYm(final YearMonth ym) {
        return pruhCalymList.stream()
                .filter(pruhYm -> pruhYm.equals(ym))
                .findFirst();
//                .findFirst().orElse(null);
    }

    private void loadPersonDataFromDb() {
        pruhPersonList = personService.fetchAllNotHidden();
        pruhPersonSelector.setValue(null);
        pruhPersonSelector.setItems(pruhPersonList);
    }

    private void loadCalymDataFromDb() {
        pruhCalymList = calymRepo.findAll(Sort.by(Sort.Direction.DESC, Calym.SORT_PROP_YM)).stream()
                .map(Calym::getYm)
                .collect(Collectors.toList())
        ;
        pruhYmSelector.setValue(null);
        pruhYmSelector.setItems(pruhCalymList);
    }

//    private void updatePruhGrids(final Person person, final Calym calym) {
    private void updatePruhGrids(final Person pruhPerson, final YearMonth ym) {

        Long pruhPersonId = null == pruhPerson ? null : pruhPerson.getId();
//        YearMonth ym = null == calym ? null : calym.getYm();
        loadPruhZakAndSumDataFromDb(pruhPersonId, ym);
//        loadPruhSumDataFromDb(personId, ym);
        loadPruhParagDataFromDb(pruhPersonId, ym);
        calcAndSetPruhMissingHods();
        setPruhSumHods();

        Pruh pruh = pruhRepo.findFirstByYmAndPersonId(ym, pruhPersonId);
        pruhState = null == pruh ? null : pruh.getState();
        setPruhStateControls(pruhState);

        pruhZakGrid.getDataProvider().refreshAll();
//        pruhSumGrid.getDataProvider().refreshAll();
        pruhParagGrid.getDataProvider().refreshAll();

//        for (PruhZak pruhZak : pruhZakList) {
//            pruhZakGrid.setDetailsVisible(pruchZak, StringUtils.isNotBlank(pruhZak.getParagText()));
//        }

        pruhZakGrid.focus();
//        UI.getCurrent().getPage().executeJavaScript("$0._scrollToIndex($1)", pruhZakGrid, 1);

    }

    private void loadPruhZakAndSumDataFromDb(Long personId, YearMonth ym) {

        // Pruh SUM:
        if (null == personId || null == ym) {
            pruhSum = new PruhSum("suma z dochazky");
        } else {
            List<Dochsum> dsSums = dochsumService.fetchDochsumForPersonAndYm(personId, ym);
            if (null == dsSums) {
                pruhSum = new PruhSum("suma z dochazky");
            } else {
                pruhSum = transposeDochsumsToPruhSums(dsSums);
            }
        }

        // Pruh ZAKS:
        if (null == personId || null == ym) {
            pruhZakList = new ArrayList<>();
        } else {
            List<DochsumZak> dsZaks = dochsumZakService.fetchDochsumZaksForPersonAndYm(personId, ym);
            if (null == dsZaks) {
                pruhZakList = new ArrayList<>();
            } else {
                pruhZakList = transposeDochsumZaksToPruhZaks(dsZaks);
                pruhZakList.sort(pruhZakOrderComparator);
//                pruhZakList.sort(pruhZakOrderComparator.reversed());
            }
        }

        pruhZakGrid.setItems(pruhZakList);
        setDayColumnsVisibility(pruhZakGrid, pruhDayMax, DZ_KEY_PREF);

//        pruhZakGrid.focus();
//        UI.getCurrent().getPage().executeJavaScript("$0._scrollToIndex($1)", pruhZakGrid, 1);
    }

    private void calcAndSetPruhMissingHods() {
        for (int day = 1; day <= 31; day++) {
            Grid.Column col = pruhZakGrid.getColumnByKey(DZ_KEY_PREF + String.valueOf(day));
            if (null != col) {
                getMissingHodString(day);
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
            List<DochsumParag> dsParags = dochsumParagService.fetchDochsumParagsForPersonAndYm(personId, ym);
            if (null == dsParags) {
                pruhParagList = new ArrayList<>();
            } else {
                pruhParagList = transposeDochsumParagsToPruhParags(dsParags);
//                pruhParagList.sort(pruhParagCkonComparator.reversed());
            }
        }
        pruhParagGrid.setItems(pruhParagList);
        setDayColumnsVisibility(pruhParagGrid, pruhDayMax, DP_KEY_PREF);

    }

//    private void loadPruhSumDataFromDb(Long personId, YearMonth ym) {
//        if (null == personId || null == ym) {
////            pruhSumList = new ArrayList();
//            pruhSum = new PruhSum("suma z dochazky");
//        } else {
//            List<Dochsum> dochsums = dochsumService.fetchDochsumForPersonAndYm(personId, ym);
//            if (null == dochsums) {
////                pruhSumList = new ArrayList<>();
//                pruhSum = new PruhSum("suma z dochazky");
//            } else {
////                pruhSumList = transposeDochsumsToPruhSums(dochsums);
//                pruhSum = transposeDochsumsToPruhSums(dochsums);
////                pruhZakSum = new PruhZak("suma z dochazky", "");
//            }
//        }
////        pruhZakSumGrid.setItems(pruhSumList);
////        setDayColumnsVisibility(pruhSumGrid, pruhDayMax, "ds");
//    }


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
        this.setPadding(false);

        // TODO:
        //        this.setMaxHeight("700px");

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
                , initGridParagTitleBar()
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
        gridZakTitleBar.getStyle()
                .set("margin-top", "0.5em")
                .set("margin-bottom", "0.2em")
        ;

//        gridZakTitleBar.setHeight("4em");
        Emphasis gridZakTitle = new Emphasis("Odpracováno na zakázkách");
        gridZakTitle.getStyle()
                .set("font-weight", "bold")
                .set("margin-right", "2em")
                .set("margin-top", "0.2em")
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

    private Component initGridParagTitleBar() {
        gridParagTitleBar = new HorizontalLayout();
        gridParagTitleBar.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em")
        ;
//        gridParagTitleBar.setHeight("4em");
        Emphasis gridParagTitle = new Emphasis("Nepřítomnost");
        gridParagTitle.getStyle()
//                .set("margin-top", "2em")
                .set("font-weight", "bold")
                .set("margin-top", "0")
                .set("margin-bottom", "0.2em")
                .set("margin-right", "2em")
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
        pruhPersonSelector.setEnabled(canViewOtherUsers());
        pruhPersonSelector.addValueChangeListener(event -> {
            pruhPerson = event.getValue();
            updatePruhGrids(pruhPerson, pruhYm);
        });
        pruhPersonSelector.addBlurListener(event -> {
//            loadPruhZakAndSumDataFromDb(pruhPerson.getId(), pruhYm);
            pruhZakGrid.getDataProvider().refreshAll();
        });
        return pruhPersonSelector;
    }

    private String getPersonLabel(Person person) {
        return person.getUsername() + " (" + person.getJmeno() + " " + person.getPrijmeni() + ")";
    }

    private Component initPruhYmSelector() {
//        pruhYmSelector = new Select<>();
        pruhYmSelector = new ComboBox<>();

        pruhYmSelector.getStyle().set("margin-right", "1em");
        pruhYmSelector.setWidth("10em");
        pruhYmSelector.setPlaceholder("Rok-měsíc");
        pruhYmSelector.setItems(new ArrayList<>());

//        pruhYmSelector.setItems(YearMonth.now());
        // The empty selection item is the first item that maps to an null item.
        // As the item is not selectable, using it also as placeholder

//        pruhYmSelector.setEmptySelectionCaption("Rok-měsíc proužku...");
//        pruhYmSelector.setEmptySelectionAllowed(true);
//        pruhYmSelector.setItemEnabledProvider(Objects::nonNull);
//        // add a divider after the empty selection item
//        pruhYmSelector.addComponents(null, new Hr());

        pruhYmSelector.setItemLabelGenerator(this::getYmLabel);
        pruhYmSelector.addValueChangeListener(event -> {
            pruhYm = event.getValue();
            pruhDayMax = (null == pruhYm) ? 0 : pruhYm.lengthOfMonth();
            updatePruhGrids(pruhPerson, pruhYm);
        });
        return pruhYmSelector;
    }

    private Component initPruhStateBox() {
        HorizontalLayout box = new HorizontalLayout();
        box.setWidth("3em");
        box.setMinWidth("3em");
        box.setVerticalComponentAlignment(Alignment.END);
        box.getStyle()
            .set("margin-top", "0.7em");


        pruhStateIconUnlocked = VaadinIcon.UNLOCK.create();
        pruhStateIconUnlocked.setColor("green");
        pruhStateIconUnlocked.getStyle().set("margin-right", "1em");
        pruhStateIconUnlocked.setVisible(false);

        pruhStateIconLocked = VaadinIcon.LOCK.create();
        pruhStateIconLocked.setColor("crimson");
        pruhStateIconLocked.getStyle().set("margin-right", "1em");
        pruhStateIconLocked.setVisible(false);

        box.add(pruhStateIconLocked, pruhStateIconUnlocked);
        return box;
    }

    private boolean pruhToBeLocked() {
        return togglePruhStateButton.getText().equals(LOCK_PRUH_BUTTON_TEXT);
    }

    private boolean pruhToBeUnlocked() {
        return togglePruhStateButton.getText().equals(UNLOCK_PRUH_BUTTON_TEXT);
    }

    private boolean pruhIsLockedOrUnknown() {
        return (null == pruhState) || pruhState.equals(PRUH_STATE_LOCKED);
    }

    private boolean pruhIsUnlocked() {
        return (null != pruhState) && pruhState.equals(PRUH_STATE_UNLOCKED);
    }

    private Component initTogglePruhStateButton() {

        togglePruhStateButton = new Button(LOCK_PRUH_BUTTON_TEXT);
        togglePruhStateButton.addClickListener(event -> {
            if (pruhZakGrid.getEditor().isOpen()) {
                pruhZakGrid.getEditor().closeEditor();
            }

//            ConfirmDialog.createInfo()
//                    .withCaption("UZAVŘENÍ PROUŽKU")
//                    .withMessage("Zatím to nedělá nic. Ale bude.")
//                    .open()
//            ;
////            return;

            String dialogCaption = togglePruhStateButton.getText().equals(LOCK_PRUH_BUTTON_TEXT) ?
                    "UZAVŘENÍ PROUŽKU": "OTEVŘENÍ PROUŽKU";

            Long personId = null == pruhPerson ? null : pruhPerson.getId();
//            YearMonth ym = null == pruhYm ? null : pruhYm.getYm();
//            YearMonth ym = pruhYm;
//            Integer state = null == pruhState ? PRUH_STATE_UNDEFINED : pruhState;

            if (null == pruhPerson || null == pruhYm) {
//                if (togglePruhStateButton.getText().equals(LOCK_PRUH_BUTTON_TEXT))
                    ConfirmDialog.createWarning()
                            .withCaption(dialogCaption)
                            .withMessage("Není vybrán proužek, nelze provést akci.")
                            .open()
                    ;
                return;
            }

            if (pruhToBeUnlocked() && pruhIsUnlocked()) {
                ConfirmDialog.createInfo()
                        .withCaption(dialogCaption)
                        .withMessage("Proužek je již otevřen, nelze znova.")
                        .open()
                        ;
                return;
            }

            if (pruhToBeLocked() && pruhIsLockedOrUnknown()) {
                ConfirmDialog.createInfo()
                        .withCaption(dialogCaption)
                        .withMessage("Proužek je již zavřen, nelze znova.")
                        .open()
                ;
                return;
            }

            if (pruhToBeLocked() && getMissingHodSum().compareTo(BigDecimal.ZERO) > 0) {
                ConfirmDialog.createWarning()
                        .withCaption(LOCK_PRUH_BUTTON_TEXT)
                        .withMessage("V proužku jsou nevyplněné hodiny na zakázkách, nelze uzavřít.")
                        .open()
                ;
                return;
            }

            Integer pruhYmInt = 100 * pruhYm.getYear() + pruhYm.getMonthValue();
//            PersonWage personWage = personWageRepo.findTopByPersonIdAndYmFromLessThanEqualOrderByYmFromDesc(pruhPerson.getId(), pruhYm);
            PersonWage personWage = personWageRepo.findPersonWageForMonth(pruhPerson.getId(), pruhYm);
            if (null == personWage) {
                ConfirmDialog.createWarning()
                        .withCaption("UZAVŘENÍ PROUŽKU")
                        .withMessage("Nenalezena platná sazba pro uživatele a měsíc, nelze uzavřít proužek.")
                        .open()
                ;
                return;
            }

            if (pruhToBeLocked()) {
                Pruh openedPruh = pruhRepo.findFirstByYmAndPersonId(pruhYm, personId);
                pruhState = Pruh.PRUH_STATE_LOCKED;
                openedPruh.setState(pruhState);
                pruhRepo.save(openedPruh);
                setPruhStateControls(pruhState);
            } else {
                Pruh openedPruh = pruhRepo.findFirstByPersonIdAndState(personId, PRUH_STATE_UNLOCKED);
                if (null != openedPruh) {
//                    if (!openedPruh.getYm().equals(ym)) {
                        ConfirmDialog.createWarning()
                                .withCaption(dialogCaption)
                                .withMessage("Je otevřen proužek " + openedPruh.getYm() + ". Dokud nebude uzavřen, nelze otevřít jiný proužek.")
                                .withCancelButton(ButtonOption.caption("ZPĚT"))
                            .open()
                        ;
                        return;
//                    }
                } else {
                    Pruh pruh = pruhRepo.findFirstByYmAndPersonId(pruhYm, personId);
                    if (null == pruh) {
                        pruh = new Pruh();
                        pruh.setYm(pruhYm);
                        pruh.setPersonId(personId);
                    }
                    pruhState = Pruh.PRUH_STATE_UNLOCKED;
                    pruh.setState(pruhState);
                    pruhRepo.save(pruh);
                    setPruhStateControls(pruhState);
                }
            }
        });
        return togglePruhStateButton;
    }

    private void setPruhStateControls(Integer newState) {
        if (null == newState) {
            setPruhControlsUnknown();
        } else if (newState.equals(PRUH_STATE_UNLOCKED)) {
            setPruhControlsUnlocked();
        } else if (newState.equals(PRUH_STATE_LOCKED)) {
            setPruhControlsLocked();
        }
    }

    private void setPruhControlsLocked() {
//        pruhStateIconLocked.setVisible(togglePruhStateButton.getText().equals(UNLOCK_PRUH_BUTTON_TEXT));
//        pruhStateIconUnlocked.setVisible(togglePruhStateButton.getText().equals(LOCK_PRUH_BUTTON_TEXT));
        pruhStateIconLocked.setVisible(true);
        pruhStateIconUnlocked.setVisible(false);
        togglePruhStateButton.setText(UNLOCK_PRUH_BUTTON_TEXT);
        saveEditButton.setEnabled(false);
        cancelEditButton.setEnabled(false);
        zaksCopyButton.setEnabled(false);
        zaksAddButton.setEnabled(false);

//        if (zakGridEditRegistration != null) {
//            zakGridEditRegistration.remove();
//        }
    }

    private void setPruhControlsUnlocked() {
        pruhStateIconLocked.setVisible(false);
        pruhStateIconUnlocked.setVisible(true);
        togglePruhStateButton.setText(LOCK_PRUH_BUTTON_TEXT);
        saveEditButton.setEnabled(true);
        cancelEditButton.setEnabled(true);
        zaksCopyButton.setEnabled(true);
        zaksAddButton.setEnabled(true);
//        zakGridEditRegistration = pruhZakGrid.addItemDoubleClickListener(event -> {
//            pruhZakGrid.getEditor().editItem(event.getItem());
//        });
    }

    private void setPruhControlsUnknown() {
        pruhStateIconLocked.setVisible(false);
        pruhStateIconUnlocked.setVisible(false);
        togglePruhStateButton.setText(UNLOCK_PRUH_BUTTON_TEXT);
        saveEditButton.setEnabled(false);
        cancelEditButton.setEnabled(false);
        zaksCopyButton.setEnabled(false);
        zaksAddButton.setEnabled(false);

//        if (zakGridEditRegistration != null) {
//            zakGridEditRegistration.remove();
//        }
    }

    private String getYmLabel(YearMonth ym) {
        return null == ym ? "" : ym.toString();
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
                .set("margin-top", "0.5em")
                .set("margin-right", "1em")
                .set("margin-bottom", "0.2em")
        ;

        HorizontalLayout selectorBox = new HorizontalLayout();
        selectorBox.add(
                initPersonSelector()
                , initPruhYmSelector()
                , initPruhStateBox()
                , initTogglePruhStateButton()
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
        pruhZakGrid.setHeight("24em");
        pruhZakGrid.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em")
        ;
//        pruhZakGrid.setWidth("100%");
        pruhZakGrid.setColumnReorderingAllowed(false);
        pruhZakGrid.setClassName("vizman-pruh-grid");
        pruhZakGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        pruhZakGrid.addThemeNames("column-borders", "row-stripes");
//        pruhZakGrid.addThemeNames("no-border", "no-row-borders", "row-stripes");
//        pruhZakGrid.addThemeNames("border", "row-borders", "row-stripes");

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
                .setWidth("4em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0)
                .setFrozen(true)
                .setResizable(false)
        ;
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
        Binder<PruhZak> pzBinder = new Binder<>(PruhZak.class);
        Editor<PruhZak> pzEditor = pruhZakGrid.getEditor();
        pzEditor.setBinder(pzBinder);
        zakGridEditRegistration = pruhZakGrid.addItemDoubleClickListener(event -> {
            // TODO keyPress listeners...
            if (event.getItem().isRezijniZak()) {
                ConfirmDialog.createInfo()
                        .withCaption("EDITACE proužku")
                        .withMessage("Režijní zakázky nelze editovat.")
                        .open()
                ;
                return;
            }
            if  (pruhState.equals(PRUH_STATE_UNLOCKED)) {
                pzEditor.editItem(event.getItem());
            } else {
                ConfirmDialog.createInfo()
                        .withCaption("EDITACE proužku")
                        .withMessage("Proužek není otevřený, nelze editovat")
                        .open()
                ;
            }
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

//    private ComponentEventListener initZakGridEditorListener() {
//
//        return (event -> {
//            pzEditor.editItem(event.getItem());
////            field.focus();
//    });

    private Component initParagGrid() {
        pruhParagGrid = new Grid<>();
        pruhParagGrid.setHeight("8em");
        pruhParagGrid.getStyle()
                .set("margin-top", "0.2em")
                .set("margin-bottom", "0.2em")
        ;
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
                .setWidth("4em")
                .setTextAlign(ColumnTextAlign.CENTER)
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
            .setKey(DZ_KEY_PREF + String.valueOf(day))
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

                    // TODO: disable when pruh is loaded, enable when changed (either hodPrac changed, or zak added/deleted)
//                    saveEditButton.setEnabled(pzBinder.hasChanges());

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

    private BigDecimal getMissingHodSum() {
        BigDecimal missingHodSum = BigDecimal.ZERO;
        for (int day = 1; day <= pruhDayMax; day++) {
            missingHodSum = missingHodSum.add(getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day)));
        }
        return missingHodSum;
    }

    private String getMissingHodString(int day) {
        BigDecimal missingHods = getDayZakMissing(getDayZakHodSum(day), getDaySumHodSum(day));
        return (null == missingHods || missingHods.compareTo(BigDecimal.ZERO) == 0) ?
                "" : VzmFormatUtils.decHodFormat.format(missingHods);
    }

    private String getSumHodString(int day) {
        BigDecimal sumHods = getDayZakHodSum(day);
        return null == sumHods ? "" : VzmFormatUtils.decHodFormat.format(sumHods);
    }

    private BigDecimal getDayZakMissing(final BigDecimal zakHodSum, final BigDecimal sumHodSum) {
        BigDecimal zakHodMissing = BigDecimal.ZERO;
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
                .setKey(DP_KEY_PREF + String.valueOf(day))
                .setResizable(false)
        ;
        // TODO: remove margins
        col.getElement().setProperty("margin", "0");
        col.getElement().setAttribute("margin", "0");
    }

    private Component buildPruhZakRemoveBtn(PruhZak pruhZak) {
        if (pruhZak.isRezijniZak()) {
            return new Span("");
        }
        Button removePruhZakBtn = new GridItemRemoveBtn(event ->
                ConfirmDialog.createQuestion()
                        .withCaption("Zákázka proužku")
                        .withMessage("Odstranit zakázku z proužku včetně vyplněných hodin?")
                        .withCancelButton(ButtonOption.caption("ZPĚT"))
                        .withOkButton(() -> {
                                removeZakFromPruh(pruhZak.getZakId());
                                updatePruhGrids(pruhPerson, pruhYm);
                            }, ButtonOption.focus(), ButtonOption.caption("ODSTRANIT")
                        )
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
            PruhZak pzak = new PruhZak(zak);
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


//    private List<Long> getDsZakIdsToDelete(List<PruhZak> pruhZaks) {
//        List<DochsumZak> dsZaksDb = dochsumZakService.fetchDochsumZaksForPersonAndYm(pruhPerson.getId(), pruhYm);
//        List<Long> dsZakIdsDb = dsZaksDb.stream()
//                .map(dszDb -> dszDb.getZakId()).distinct().collect(Collectors.toList());
//        List<Long> dsZakIdsPruh = pruhZaks.stream()
//                .map(zakPruh -> zakPruh.getZakId()).distinct().collect(Collectors.toList());
//        List<Long> dsZakIdsToDelete = dsZakIdsDb.stream()
//                .filter(dszZakIdDb -> !dsZakIdsPruh.contains(dszZakIdDb))
//                .collect(Collectors.toList());
//    }

    private List<DochsumZak> transposePruhZaksToDochsumZaks(List<PruhZak> pruhZaks) {

//        List<Long> zakIds = new ArrayList<>();
//        for (PruhZak pzak : pruhZaks) {
//            zakIds.add(pzak.getZakId());
//        }

//        List<DochsumZak> dsZaks = new ArrayList<>();
//        List<DochsumZak> dsZaksDb = dochsumZakService.fetchDochsumZaksForPersonAndYm(pruhPerson.getId(), pruhYm);
//
//        List<Long> dsZakIdsDb = dsZaksDb.stream()
//                .map(dszDb -> dszDb.getZakId()).distinct().collect(Collectors.toList());
//        List<Long> dsZakIdsPruh = pruhZaks.stream()
//                .map(zakPruh -> zakPruh.getZakId()).distinct().collect(Collectors.toList());
//        List<Long> dsZakIdsToDelete = dsZakIdsDb.stream()
//                .filter(dszZakIdDb -> !dsZakIdsPruh.contains(dszZakIdDb))
//                .collect(Collectors.toList());

//        Integer pruhYmInt = 100 * pruhYm.getYear() + pruhYm.getMonthValue();
//        PersonWage personWage = personWageRepo.findPersonWageForMonth(pruhPerson.getId(), pruhYm);
////        PersonWage personWage = personWageRepo.findPersonWageForMonth(pruhPerson.getId(), pruhYm);
//
//        for (PruhZak pzak : pruhZaks) {
//            Long pzakZakId = pzak.getZakId();
////            pzak.getZakId()...
//            for (int i = 1; i <= pruhDayMax; i++) {
//                BigDecimal newCellHod = pzak.getHod(i);
//                LocalDate pzakDate = LocalDate.of(pruhYm.getYear(), pruhYm.getMonth(), i);
//                DochsumZak dsZakDb = dsZaksDb.stream()
//                        .filter(zakDb -> zakDb.getZakId().equals(pzakZakId)
//                                && zakDb.getDsDate().equals(pzakDate)
//                        )
//                        .findFirst().orElse(null);
//                if (null != dsZakDb) {
//                    if (!dsZakDb.getDszWorkPruh().equals(newCellHod)) {
//                        dsZakDb.setDszWorkPruh(newCellHod);
//                        dsZaks.add(dsZakDb);
//                    }
//                } else {
//                    if (null != newCellHod && newCellHod.compareTo(BigDecimal.ZERO) != 0) {
//                        DochsumZak dsZakNew = new DochsumZak(
//                                pruhPerson.getId(), pzakDate, pzakZakId, newCellHod, personWage.getWage());
//                        dsZaks.add(dsZakNew);
//                    }
//                }
//            }
//        }
//        return dsZaks;
        return null;
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
                    .withCaption("EDITACE PROUŽKU")
                    .withMessage("Vrátit změny proužku od posledního uložení?")
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .withOkButton(() -> {
//                        loadPruhZakAndSumDataFromDb(pruhPerson, pruhYm);
                        updatePruhGrids(pruhPerson, pruhYm);
                        // TODO: disable when pruh is loaded, enable when changed (either hodPrac changed, or zak added/deleted)
//                        saveEditButton.setEnabled(false);
                    }, ButtonOption.focus(), ButtonOption.caption("VRÁTIT ZMĚNY"))
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
                    .withCaption("EDITACE PROUŽKU")
                    .withMessage("Uložit proužek?")
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
                    .withYesButton(() -> {
//                        dochsumZakService.updateDochsumZaksForPersonAndMonth(
//                                pruhPerson.getId(), pruhYm, transposePruhZaksToDochsumZaks(pruhZakList));
                        if (pruhZakGrid.getEditor().isOpen()) {
                            pruhZakGrid.getEditor().closeEditor();
                        }
                        boolean ok = dochsumZakService.updateDochsumZaksForPersonAndMonth(
                            pruhPerson.getId()
                            , pruhYm
                            , pruhDayMax
                            , pruhZakList
                        );
                        updatePruhGrids(pruhPerson, pruhYm);
                    } , ButtonOption.focus(), ButtonOption.caption("ULOŽIT"))
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
            if (null == pruhPerson || null == pruhYm) {
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
        zaksCopyButton = new Button("Nakopírovat zakázky");
        zaksCopyButton.addClickListener(event -> {
            if (pruhZakGrid.getEditor().isOpen()) {
                pruhZakGrid.getEditor().closeEditor();
            }
            if (null == pruhPerson || null == pruhYm) {
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
                    .withMessage(String.format("Nakopírovat zakázky z proužku %s ?", lastYm))
                    .withCancelButton(ButtonOption.caption("ZPĚT"))
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
//                        lastPruhZakList.sort(pruhZakOrderComparator.reversed());
                        lastPruhZakList.sort(pruhZakOrderComparator);
                        addPruhZaksToGrid(lastPruhZakList);
                    })
                    .open()
            ;
        });
        return zaksCopyButton;
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
