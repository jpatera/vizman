package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.forms.ZakFormDialog;
import eu.japtor.vizman.ui.forms.ZakNaklSingleDialog;
import eu.japtor.vizman.ui.forms.ZakRozpracSingleDialog;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.claspina.confirmdialog.ConfirmDialog;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static eu.japtor.vizman.backend.entity.Mena.EUR;


public class ZakRozpracGrid extends Grid<Zakr> {

    private static final String RX_REGEX = "[0-9]{1,3}";

    // Zak Compact Grid field keys:
    public static final String CKZ_COL_KEY = "zakr-bg-kzcislo";
    public static final String ROK_COL_KEY = "zakr-bg-rok";
    public static final String SKUPINA_COL_KEY = "zakr-bg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zakr-bg-objednatel";
    public static final String KZTEXT_COL_KEY = "zakr-bg-kztext";
    public static final String SEL_COL_KEY = "zakr-bg-select";
    public static final String MENA_COL_KEY = "zakr-bg-mena";
    public static final String ARCH_COL_KEY = "zakr-bg-arch";
    public static final String HONOR_CISTY_COL_KEY = "zakr-bg-honor-cisty";
    public static final String RP_COL_KEY = "zakr-bg-rp";
    public static final String RM4_COL_KEY = "zakr-bg-rm4";
    public static final String RM3_COL_KEY = "zakr-bg-rm3";
    public static final String RM2_COL_KEY = "zakr-bg-rm2";
    public static final String RM1_COL_KEY = "zakr-bg-rm1";
    public static final String R0_COL_KEY = "zakr-bg-r0";
    public static final String R1_COL_KEY = "zakr-bg-r1";
    public static final String R2_COL_KEY = "zakr-bg-r2";
    public static final String R3_COL_KEY = "zakr-bg-r3";
    public static final String R4_COL_KEY = "zakr-bg-r4";
    public static final String PERFORMANCE_COL_KEY = "zakr-bg-performance";
    public static final String REMAINS_COL_KEY = "zakr-bg-remains";
    public static final String FINISHED_COL_KEY = "zakr-bg-finished";
    public static final String RESULT_COL_KEY = "zakr-bg-result";
    public static final String RESULTP8_COL_KEY = "zakr-bg-result-p8";
    public static final String MZDY_COL_KEY = "mzdy-bg-result";
    public static final String MZDY_POJ_REZ_COL_KEY = "mzdy-poj-rez-bg-result";

//    Grid<Zak> zakGrid;
    private ZakFormDialog zakFormDialog;
    private ZakRozpracSingleDialog zakRozpracSingleDialog;
    private ZakNaklSingleDialog zakNaklSingleDialog;
    private TextField ckzFilterField;
    private Select<Boolean> archFilterField;
    private Select<Integer> rokFilterField;
    private Select<String> skupinaFilterField;
    private TextField objednatelFilterField;
    private TextField kzTextFilterField;

    private Boolean archFilterValue;
    private String ckzFilterValue;
    private Integer rokFilterValue;
    private String skupinaFilterValue;
    private String kzTextFilterValue;
    private String objednatelFilterValue;

    private Boolean initFilterArchValue;
    private boolean archFieldVisible;
    private boolean selectFieldVisible;

//    private BigDecimal kurzEur;

    private HeaderRow descHeaderRow;
    private HeaderRow filterHeaderRow;
    private FooterRow sumFooterRow;
//    private Registration zakrGridEditRegistration = null;
    private Zakr editedItem;
    private boolean editedItemChanged;
    private BiConsumer<Zakr, Operation> itemSaver;

    private ZakrService zakrService;
    private ZakService zakService;
    private ZaqaService zaqaService;
    private FaktService faktService;
    private ZakNaklVwService zakNaklVwService;
    private CfgPropsCache cfgPropsCache;
    private ZakrListView.ZakrParams zakrParams;

    public ZakRozpracGrid(
            boolean selectFieldVisible
            , boolean archFieldVisible
            , Boolean initFilterArchValue
            , BiConsumer<Zakr, Operation> itemSaver
            , ZakrListView.ZakrParams zakrParams
            , ZakrService zakrService
            , ZakService zakService
            , ZaqaService zaqaService
            , FaktService faktService
            , ZakNaklVwService zakNaklVwService
            , CfgPropsCache cfgPropsCache
    ) {
        this.initFilterArchValue = initFilterArchValue;
        this.archFieldVisible = archFieldVisible;
        this.selectFieldVisible = selectFieldVisible;
        this.itemSaver = itemSaver;
//        this.kurzEur = params.getKurzEur();
        this.zakrParams = zakrParams;

        this.zakrService = zakrService;
        this.zakService = zakService;
        this.zaqaService = zaqaService;
        this.faktService = faktService;
        this.zakNaklVwService = zakNaklVwService;
        this.cfgPropsCache = cfgPropsCache;

        zakRozpracSingleDialog = new ZakRozpracSingleDialog(
                this.zakrService
        );
        zakFormDialog = new ZakFormDialog(
                this.zakService, this.zaqaService, this.faktService, this.cfgPropsCache
        );
        zakNaklSingleDialog = new ZakNaklSingleDialog(
                this.zakNaklVwService
        );


//        Grid<Zak> zakGrid = new Grid<>();
        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setClassName("vizman-simple-grid");
        this.addThemeNames("column-borders", "row-stripes");

        this.setSelectionMode(SelectionMode.SINGLE);    // MUST be SINGLE, automatic changes saving is based  onit
        this.addSelectionListener(event -> {
            attemptSaveFromEditor();
            editedItem = event.getFirstSelectedItem().orElse(null);   // Note: grid selection mode is supposed to be SINGLE
        });

        this.getDataProvider().addDataProviderListener(e -> {
            updateFooterFields();
        });
//        zakGrid.getElement().addEventListener("keypress", e -> {
//            JsonObject eventData = e.getEventData();
//            String enterKey = eventData.getString("event.key");
//            if ("Enter".equals(enterKey)) {
//                new Notification("ENTER pressed - will open form", 1500).open();
//            }
//        })
//                .addEventData("event.key")
//        ;

//        DomEventListener gridKeyListener = new DomEventListener() {
//            @Override
//            public void handleEvent(DomEvent domEvent) {
//                JsonObject eventData = domEvent.getEventData();
//                String enterKey = eventData.getString("event.key");
//                if ("Enter".equals(enterKey)) {
//                    new Notification("ENTER pressed - will open form", 1500).open();
//                }
//
//            }
//        };

        // =============
        // Grid editor:
        // =============
        Editor<Zakr> zakrEditor = this.getEditor();
        Binder<Zakr> zakrEditorBinder = new Binder<>(Zakr.class);
        zakrEditor.setBinder(zakrEditorBinder);
        zakrEditor.setBuffered(false);
        zakrEditor.addSaveListener(event -> {
           System.out.println("=== editor SAVING...");
//           this.itemSaver.accept(event.getItem(), Operation.EDIT);
           attemptSaveFromEditor();
        });

        this.addItemDoubleClickListener(event -> {
            // TODO keyPress listeners...??
            zakrEditor.editItem(event.getItem());
//            field.focus();
        });
//        zakrEditorBinder.addStatusChangeListener(event -> {
//            event.getBinder().hasChanges();
//        });

        // =============
        // Columns:
        // =============
        this.addColumn(archRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
                .setVisible(this.archFieldVisible)
        //                .setFrozen(true)
        ;
//        if (isZakFormsAccessGranted()) {
         this.addColumn(new ComponentRenderer<>(this::buildZakViewBtn))
                    .setHeader("Zak")
                    .setFlexGrow(0)
                    .setWidth("3em")
         ;
//        }
        this.addColumn(Zakr::getKzCislo)
                .setHeader("ČK-ČZ")
                .setFlexGrow(0)
                .setWidth("9em")
                .setSortable(true)
                .setKey(CKZ_COL_KEY)
        ;
        this.addColumn(Zakr::getRok)
                .setHeader("Rok zak.")
                .setFlexGrow(0)
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(ROK_COL_KEY)
        ;
        this.addColumn(Zakr::getSkupina)
                .setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("4em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(SKUPINA_COL_KEY)
        ;
        this.addColumn(selectFieldRenderer)
                .setHeader(("Výběr"))
                .setFlexGrow(0)
                .setWidth("4.5em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setKey(SEL_COL_KEY)
                .setVisible(this.selectFieldVisible)
        ;
        this.addColumn(menaValueProvider)
                .setHeader("Měna")
                .setFlexGrow(0)
                .setWidth("4em")
//                .setResizable(true)
                .setKey(MENA_COL_KEY)
        ;

        // Tooltips for labels fixed in Vaadin major version 18, https://github.com/vaadin/vaadin-grid/issues/1841
        Grid.Column<Zakr> colHonorCisty = this.addColumn(honorCistyValueProvider)
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(getHonorCistyByKurz(zakr1), getHonorCistyByKurz(zakr2)))
                .setHeader(new ColumnHeader(
                        true, "Hon.č. [CZK]"
                        , "[Honorář čistý] = (SUM(fakt) - SUM(sub)) * kurz"
                ))
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(HONOR_CISTY_COL_KEY)
                .setResizable(true)
        ;

        this.addColumn(new ComponentRenderer<>(this::buildZaqaOpenBtn))
                .setHeader("QX")
                .setFlexGrow(0)
                .setWidth("3em")
        ;

        Grid.Column<Zakr> colRm4 = this.addColumn(rm4GridValueProvider)
                .setHeader("R-4")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(RM4_COL_KEY)
                .setResizable(false)
                ;
        colRm4.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getRm4, Zakr::setRm4));

        Grid.Column<Zakr> colRm3 = this.addColumn(rm3GridValueProvider)
                .setHeader("R-3")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(RM3_COL_KEY)
                .setResizable(false)
                ;
        colRm3.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getRm3, Zakr::setRm3));

        Grid.Column<Zakr> colRm2 = this.addColumn(rm2GridValueProvider)
                .setHeader("R-2")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(RM2_COL_KEY)
                .setResizable(false)
                ;
        colRm2.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getRm2, Zakr::setRm2));

        Grid.Column<Zakr> colRm1 = this.addColumn(rm1GridValueProvider)
                .setHeader("R-1")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(RM1_COL_KEY)
                .setResizable(false)
                ;
        colRm1.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getRm1, Zakr::setRm1));

        Grid.Column<Zakr> colR0 = this.addColumn(r0GridValueProvider)
//        Grid.Column<Zakr> colR0 = this.addColumn(Zakr::getR0)
//                new ComponentRenderer<>(pruhZak ->
//                        VzmFormatUtils.getDecHodComponent(pzHodValProv.apply(pruhZak)))
                .setHeader("R0")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(R0_COL_KEY)
                .setResizable(false)
                ;
        colR0.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getR0, Zakr::setR0));

        Grid.Column<Zakr> colR1 = this.addColumn(r1GridValueProvider)
                .setHeader("R1")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(R1_COL_KEY)
                .setResizable(false)
                ;
        colR1.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getR1, Zakr::setR1));

        Grid.Column<Zakr> colR2 = this.addColumn(r2GridValueProvider)
                .setHeader("R2")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(R2_COL_KEY)
                .setResizable(false)
                ;
        colR2.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getR2, Zakr::setR2));

        Grid.Column<Zakr> colR3 = this.addColumn(r3GridValueProvider)
                .setHeader("R3")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(R3_COL_KEY)
                .setResizable(false)
                ;
        colR3.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getR3, Zakr::setR3));

        Grid.Column<Zakr> colR4 = this.addColumn(r4GridValueProvider)
                .setHeader("R4")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(R4_COL_KEY)
                .setResizable(false)
                ;
        colR4.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getR4, Zakr::setR4));

        Grid.Column<Zakr> colRxRyVykon = this.addColumn(rxRyVykonGridValueProvider)
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(getRxRyVykonByKurz(zakr1), getRxRyVykonByKurz(zakr2)))
                .setHeader(new ColumnHeader(
                        true, "Výk. rx..ry"
                        , "[Výkon rx..ry] = [Honorář čistý] * ([RY] - [RX])"
                ))
                .setFlexGrow(0)
                .setWidth("7.3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(PERFORMANCE_COL_KEY)
                .setResizable(true)
        ;

        this.addColumn(new ComponentRenderer<>(this::buildZaknOpenBtn))
                .setHeader("Nak")
                .setFlexGrow(0)
                .setWidth("3em")
        ;

        Grid.Column<Zakr> colRp = this.addColumn(rpGridValueProvider)
                .setHeader("RP")
                .setFlexGrow(0)
                .setWidth("3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(false)
                .setKey(RP_COL_KEY)
                .setResizable(false)
                ;

//        colRP.setEditorComponent(buildRxEditorComponent(zakrEditorBinder, Zakr::getR0, Zakr::setR0));

        Grid.Column<Zakr> colRpHotovo = this.addColumn(rpHotovoGridValueProvider)
                .setHeader(new ColumnHeader(
                        true, "Hotovo RP"
                        , "[Hotovo RP] = [Honorář čistý] * [RP]"
                ))
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(
                        zakr1.getRpHotovoByKurz(), zakr2.getRpHotovoByKurz()
                ))
                .setFlexGrow(0)
                .setWidth("7.5em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(FINISHED_COL_KEY)
                .setResizable(true)
                ;
//        this.getDefaultHeaderRow().getCell(colRpHotovo).setComponent(hotovoHeaderLabel);

        Grid.Column<Zakr> colRpZbyva = this.addColumn(rpZbyvaByKurzGridValueProvider)
                .setHeader(new ColumnHeader(
                         true, "Zbývá RP"
                        , "[Zbývá RP] = [Honorář čistý] * (100% - [RP])"
                ))
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(
                        zakr1.getRpZbyvaByKurz(), zakr2.getRpZbyvaByKurz()
                ))
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("7.3em")
                .setTextAlign(ColumnTextAlign.END)
                .setKey(REMAINS_COL_KEY)
                .setResizable(true)
        ;

        Grid.Column<Zakr> colVysledek = this.addColumn(vysledekGridValueProvider)
                .setHeader(new ColumnHeader(
                        true, "Výsledek"
                        , "[Výsledek] = [Hotovo RP] - [Mzdy] * (1 + koef_pojist) * (1 + koef_rezie)"
                ))
                .setFlexGrow(0)
                .setWidth("7.3em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(RESULT_COL_KEY)
                .setResizable(true)
                ;

        Grid.Column<Zakr> colVysledekP8 = this.addColumn(vysledekP8GridValueProvider)
                .setHeader(new ColumnHeader(
                        true, "Výsledek P8"
                        , "[Výsledek P8] = [Hotovo RP] - [Mzdy P8] * (1 + koef_pojist) * (1 + koef_rezie)"
                ))
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(RESULTP8_COL_KEY)
                .setResizable(true)
                ;

        Grid.Column<Zakr> colNaklMzdy = this.addColumn(naklMzdyValueProvider)
                .setHeader("Mzdy")
                .setFlexGrow(0)
                .setWidth("7em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(MZDY_COL_KEY)
                .setResizable(true)
                ;

        Grid.Column<Zakr> colNaklMzdyPojistRezie = this.addColumn(naklMzdyPojistRezieValueProvider)
                .setHeader(new ColumnHeader(
                        true, "Mzdy * P*R"
                        , "[Mzdy * P*R] = [Mzdy] * (1 + koef_pojist) * (1 + koef_rezie)"
                ))
                .setFlexGrow(0)
                .setWidth("7.7em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(MZDY_POJ_REZ_COL_KEY)
                .setResizable(true)
                ;

        this.addColumn(Zakr::getKzText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setWidth("25em")
                .setSortable(true)
                .setKey(KZTEXT_COL_KEY)
        ;
        this.addColumn(Zakr::getObjednatel)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("12em")
                .setSortable(true)
                .setKey(OBJEDNATEL_COL_KEY)
        ;


        // =================
        // Headers, Footers
        // =================

//        descHeaderRow = this.appendHeaderRow();
        filterHeaderRow = this.appendHeaderRow();

        sumFooterRow = this.appendFooterRow();


        // =============
        // Filters
        // =============

        archFilterField = buildSelectorFilterField();
//        archFilterField.setItemLabelGenerator(this::archFilterLabelGenerator);
        archFilterField.setTextRenderer(this::archFilterLabelGenerator);
        filterHeaderRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);

        ckzFilterField = buildTextFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(CKZ_COL_KEY))
                .setComponent(ckzFilterField);

        rokFilterField = buildSelectorFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);

        skupinaFilterField = buildSelectorFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField)
        ;

        kzTextFilterField = buildTextFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(KZTEXT_COL_KEY))
                .setComponent(kzTextFilterField)
        ;

        objednatelFilterField = buildTextFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(OBJEDNATEL_COL_KEY))
                .setComponent(objednatelFilterField)
        ;

        for (Column colXx : getColumns()) {
            setResizable(colXx);
        }
    }

    private void updateHotovoSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(FINISHED_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcHotovokSum())));
    }

    private void updateZbyvaSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(REMAINS_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcZbyvaSum())));
    }

    private void updateVykonSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(PERFORMANCE_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcVykonSum())));
    }

    private void updateMzdySumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(MZDY_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcMzdySum())));
    }

    private void updateMzdyPojRezSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(MZDY_POJ_REZ_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcMzdyPojRezSum())));
    }

    private void updateVysledekSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(RESULT_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcVysledekSum())));
    }

    private void updateVysledekP8SumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(RESULTP8_COL_KEY))
                .setText("" + (VzmFormatUtils.MONEY_FORMAT.format(calcVysledekP8Sum())));
    }

    private BigDecimal calcHotovokSum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zakr -> zakr.getRpHotovo() != null)
                .map(zakr -> EUR == zakr.getMena() ? zakr.getRpHotovo().multiply(zakrParams.getKurzEur()) : zakr.getRpHotovo())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcZbyvaSum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .filter(zakr -> zakr.getRpZbyva() != null)
                .map(zakr -> EUR == zakr.getMena() ? zakr.getRpZbyva().multiply(zakrParams.getKurzEur()) : zakr.getRpZbyva())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcVykonSum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
//                .filter(zakr -> zakr.getRxRyVykon(zakrParams.getRx(), zakrParams.getRy()) != null)
                .map(zakr -> EUR == zakr.getMena() ?
                        (null == zakr.getRxRyVykon() ? null : zakr.getRxRyVykon().multiply(zakrParams.getKurzEur())) :
                        zakr.getRxRyVykon())
//                        zakr.getRxRyVykon(zakrParams.getRx(), zakrParams.getRy()).multiply(zakrParams.getKurzEur()) :
//                        zakr.getRxRyVykon(zakrParams.getRx(), zakrParams.getRy()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdySum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .map(zakr -> zakr.getNaklMzda())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdyP8Sum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .map(zakr -> zakr.getNaklMzdaP8())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdyPojRezSum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .map(zakr -> zakr.calcNaklMzdyPojistRezie(zakrParams.getKoefPojist(), zakrParams.getKoefRezie()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcMzdyP8PojRezSum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .map(zakr -> zakr.calcNaklMzdyP8PojistRezie(zakrParams.getKoefPojist(), zakrParams.getKoefRezie()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcVysledekSum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .map(zakr -> zakr.calcVysledekByKurz(zakrParams.getKoefPojist(), zakrParams.getKoefRezie()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private BigDecimal calcVysledekP8Sum() {
        return this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .map(zakr -> zakr.calcVysledekP8ByKurz(zakrParams.getKoefPojist(), zakrParams.getKoefRezie()))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        ;
    }

    private void updateRecCountField() {
        sumFooterRow
                .getCell(this.getColumnByKey(CKZ_COL_KEY))
                .setText("Počet: " + (calcRecCount()));
    }

    private Integer calcRecCount() {
        Integer count = this.getDataProvider()
                .withConfigurableFilter()
                .fetch(new Query<>())
                .collect(Collectors.toList())
                .size();
        return count;
    }


    Component buildZakViewBtn(Zakr zakr) {
        return new GridItemBtn(
                event -> zakFormDialog.openDialog(true, zakService.fetchOne(zakr.getId()), Operation.EDIT)
                , new Icon(VaadinIcon.EYE)
                , VzmFormatUtils.getItemTypeColorName(zakr.getTyp())
                , "Náhled na zakázku"
        );
    }

    Component buildZaknOpenBtn(Zakr zakr) {
        return new GridItemBtn(
                event -> zakNaklSingleDialog.openDialog(zakr, zakrParams, getSingleRepParamSubtitleText())
                , new Icon(VaadinIcon.COIN_PILES)
                , VzmFormatUtils.getItemTypeColorName(zakr.getTyp())
                , "Detailní náklady"
        );
    }

    Component buildZaqaOpenBtn(Zakr zakr) {
        return new GridItemBtn(
                event -> zakRozpracSingleDialog.openDialog(zakrService.fetchOne(zakr.getId()), "")
                , new Icon(VaadinIcon.LINES_LIST)
                , null
                , "Historie rozpracovanosti"
        );
    }

    public String getSingleRepParamSubtitleText() {
        return  "Parametry:"
                + "  Koef.pojištení=" + (null == zakrParams.getKoefPojist() ? "" : zakrParams.getKoefPojist())
                + "  Koef.režie=" + (null == zakrParams.getKoefRezie() ? "" : zakrParams.getKoefRezie())
        ;
    }

    private void attemptSaveFromEditor() {

        if (this.getEditor().isOpen()) {
            if (getEditor().isBuffered()) {
                this.getEditor().save();
            } else {
                editedItem = this.getEditor().getItem();
                this.getEditor().closeEditor();
            }
        }
        String ckzEdit = "N/A";
        try {
            if (null != editedItem) {
                ckzEdit = String.format("%s / %s", editedItem.getCkont(), editedItem.getCzak());
                if (editedItemChanged) {
                    this.itemSaver.accept(editedItem, Operation.EDIT);
                    updateFooterFields();

//                    tariffGridValueProvider.apply(editedItem);
                }
            }
//            editedItem = event.getFirstSelectedItem().orElse(null);   // Note: grid selection mode is supposed to be SINGLE
            editedItemChanged = false;
        } catch(Exception ex) {
            ConfirmDialog
                    .createError()
                    .withCaption("Editace rozpracovanosti.")
                    .withMessage(String.format("Rozpracovanost %s se nepodařilo uložit.", ckzEdit))
                    .open()
            ;
        }
    }


    private ValueProvider<Zakr, String> rpGridValueProvider =
            zakr -> null == zakr.getRp() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getRp())
    ;

    private ValueProvider<Zakr, String> rm4GridValueProvider =
            zakr -> null == zakr.getRm4() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getRm4())
    ;

    private ValueProvider<Zakr, String> rm3GridValueProvider =
            zakr -> null == zakr.getRm3() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getRm3())
    ;

    private ValueProvider<Zakr, String> rm2GridValueProvider =
            zakr -> null == zakr.getRm2() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getRm2())
    ;

    private ValueProvider<Zakr, String> rm1GridValueProvider =
            zakr -> null == zakr.getRm1() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getRm1())
    ;

    private ValueProvider<Zakr, String> r0GridValueProvider =
            zakr -> null == zakr.getR0() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getR0())
    ;

    private ValueProvider<Zakr, String> r1GridValueProvider =
            zakr -> null == zakr.getR1() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getR1())
    ;

    private ValueProvider<Zakr, String> r2GridValueProvider =
            zakr -> null == zakr.getR2() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getR2())
    ;

    private ValueProvider<Zakr, String> r3GridValueProvider =
            zakr -> null == zakr.getR3() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getR3())
    ;

    private ValueProvider<Zakr, String> r4GridValueProvider =
            zakr -> null == zakr.getR4() ? "" : VzmFormatUtils.PROC_INT_FORMAT.format(zakr.getR4())
    ;


    private ValueProvider<Zakr, String> honorCistyValueProvider = zakr -> {
        BigDecimal honorCistyByKurz = getHonorCistyByKurz(zakr);
        if (null == honorCistyByKurz) {
            return "";
        } else {
            return VzmFormatUtils.MONEY_FORMAT.format(honorCistyByKurz);
        }
    };
    private  BigDecimal getHonorCistyByKurz(Zakr zakr) {
        BigDecimal honorCisty = zakr.getHonorCisty();
        if (null == honorCisty) {
            return null;
        } else {
            return zakr.getMena() == EUR ?
                    honorCisty.multiply(zakrParams.getKurzEur()) :
                    honorCisty;
        }
    }

    private ValueProvider<Zakr, String> rxRyVykonGridValueProvider = zakr -> {
        BigDecimal rxRyVykonByKurz = getRxRyVykonByKurz(zakr);
        if (null == rxRyVykonByKurz) {
            return "";
        } else {
            return VzmFormatUtils.MONEY_FORMAT.format(rxRyVykonByKurz);
        }
    };
    private  BigDecimal getRxRyVykonByKurz(Zakr zakr) {
        BigDecimal rxRyVykon = zakr.getRxRyVykon();
        if (null == rxRyVykon) {
            return null;
        } else {
            return zakr.getMena() == EUR ?
                    rxRyVykon.multiply(zakrParams.getKurzEur()) :
                    rxRyVykon;
        }
    }

    private ValueProvider<Zakr, String> rpZbyvaByKurzGridValueProvider = zakr -> {
        BigDecimal rpZbyvaByKurz = zakr.getRpZbyvaByKurz();
        if (null == rpZbyvaByKurz) {
            return "";
        } else {
            return VzmFormatUtils.MONEY_FORMAT.format(rpZbyvaByKurz);
        }
    };

    public ValueProvider<Zakr, String> rpHotovoGridValueProvider = zakr -> {
        if (null == zakr.getRpHotovo()) {
            return "";
        } else {
            BigDecimal rpVysledek = zakr.getRpHotovoByKurz();
            return null == rpVysledek ? "" : VzmFormatUtils.MONEY_FORMAT.format(rpVysledek);
        }
    };

    private ValueProvider<Zakr, String> vysledekGridValueProvider = zakr ->
            null == zakr.getVysledekByKurz() ? "" : VzmFormatUtils.MONEY_FORMAT.format(zakr.getVysledekByKurz());

    private ValueProvider<Zakr, String> vysledekP8GridValueProvider = zakr ->
            null == zakr.getVysledekP8ByKurz() ? "" : VzmFormatUtils.MONEY_FORMAT.format(zakr.getVysledekP8ByKurz());

    private ValueProvider<Zakr, String> naklMzdyValueProvider = zakr ->
            null == zakr.getNaklMzda() ? "" : VzmFormatUtils.MONEY_FORMAT.format(zakr.getNaklMzda());

    private ValueProvider<Zakr, String> naklMzdyPojistRezieValueProvider = zakr -> {
            BigDecimal naklMzdy = zakr.calcNaklMzdyPojistRezie(zakrParams.getKoefPojist(), zakrParams.getKoefRezie());
            if (null == naklMzdy) {
                return "";
            } else {
                return VzmFormatUtils.MONEY_FORMAT.format(naklMzdy);
            }
    };

    private ValueProvider<Zakr, String> menaValueProvider = zakr ->
            null == zakr.getMena() ? null : zakr.getMena().name();


    // ===========================
    // Rx Field editor  component
    // ===========================
    private Component buildRxEditorComponent(
            Binder<Zakr> zakrEditorBinder
            , ValueProvider<Zakr, BigDecimal> rxEditorValueProvider
            , Setter<Zakr, BigDecimal> rxEditorSetter
    ) {
        TextField editComp = new TextField();
        editComp.addValueChangeListener(event -> {
            if (event.isFromClient() && !Objects.equals(event.getValue(), (event.getOldValue()))) {
                editedItemChanged = true;
                this.getEditor().getBinder().writeBeanIfValid(this.getEditor().getItem());
//                this.getDataProvider().refreshItem(this.getEditor().getItem());
            }
        });
        // TODO: remove margins
        editComp.getStyle()
                .set("margin", "0")
                .set("padding", "0")
//                .set("width", HOD_COL_WIDTH)
                .set("width", "3.5em")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("height", "1.8m")
                .set("min-height", "1.8em")
                .set("--lumo-text-field-size", "var(--lumo-size-s)")
        ;
        editComp.setPattern(RX_REGEX);
        editComp.setPreventInvalidInput(true);
        zakrEditorBinder.forField(editComp)
                .withNullRepresentation("")
                .withConverter(VzmFormatUtils.VALIDATED_PROC_INT_TO_STRING_CONVERTER)
                .bind(rxEditorValueProvider, rxEditorSetter);

        return editComp;
    }

    // TODO Probably  obsolete - remove ??
    public static BigDecimal getActiveRxValue(Zakr zakr) {
        return getActiveRxValue(zakr.getR0(), zakr.getR1(), zakr.getR2(), zakr.getR3(), zakr.getR4());
    }

    // TODO Probably  obsolete - remove
    public static BigDecimal getActiveRxValue(BigDecimal r0, BigDecimal r1, BigDecimal r2, BigDecimal r3, BigDecimal r4) {
        if ((null != r4) && (0 != r4.compareTo(BigDecimal.ZERO))) {
            return r4;
        } else if ((null != r3) && (0 != r3.compareTo(BigDecimal.ZERO))) {
            return r3;
        } else if ((null != r2) && (0 != r2.compareTo(BigDecimal.ZERO))) {
            return r2;
        } else if ((null != r1) && (0 != r1.compareTo(BigDecimal.ZERO))) {
            return r1;
        } else if ((null != r0) && (0 != r0.compareTo(BigDecimal.ZERO))) {
            return r0;
        }
        return BigDecimal.ZERO;
    }

    private void setResizable(Column column) {
        column.setResizable(true);
        Element parent = column.getElement().getParent();
        while (parent != null
                && "vaadin-grid-column-group".equals(parent.getTag())) {
            parent.setProperty("resizable", "true");
            parent = parent.getParent();
        }
    }

    private TextField buildTextFilterField() {
        TextField textFilterField = new TextField();
        textFilterField.setClearButtonVisible(true);
        textFilterField.setSizeFull();
        textFilterField.setPlaceholder("Filtr");
        textFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        textFilterField.addValueChangeListener(event -> doFilter());
        return textFilterField;
    }

    private <T> Select buildSelectorFilterField() {
        Select <T> selector = new SelectorFilterField<>();
        selector.addValueChangeListener(event -> doFilter());
        return selector;
    }

    private String archFilterLabelGenerator(Boolean arch) {
        if  (null == arch) {
            return "Vše";
        } else {
            return arch ? "Ano" : "Ne";
        }
    }

    public void populateGridDataAndRebuildFilterFields(List<Zakr> zakrList) {
        setItems(zakrList);
        setRokFilterItems(zakrList.stream()
                .filter(z -> null != z.getRok())
                .map(Zakr::getRok)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        setSkupinaFilterItems(zakrList.stream()
                .map(Zakr::getSkupina)
                .filter(s -> null != s)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        setArchFilterItems(zakrList.stream()
                .map(Zakr::getArch)
                .filter(a -> null != a)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        initFilterValues();
        doFilter();
        updateFooterFields();
    }

    public void recalcGrid() {
//    public void recalcGrid(List<Zakr> zakrList) {
//        setItems(zakrList);
        this.getColumnByKey(PERFORMANCE_COL_KEY).setHeader(
                "Výk. "
                + (null == zakrParams.getRx() ? "rx" : zakrParams.getRx())
                + ".."
                + (null == zakrParams.getRy() ? "ry" : zakrParams.getRy())
        );
        updateFooterFields();
    }

    private void updateFooterFields() {
        updateRecCountField();
        updateHotovoSumField();
        updateZbyvaSumField();
        updateVykonSumField();
        updateVysledekSumField();
        updateVysledekP8SumField();
        updateMzdySumField();
        updateMzdyPojRezSumField();
    }

    public void populateGridDataAndRestoreFilters(List<Zakr> zakrList) {
        saveFilterFieldValues();
        setItems(zakrList);
        restoreFilterFieldValues();
        doFilter();
        updateFooterFields();
    }

    public void initFilterValues() {
        ((ListDataProvider<Zakr>) this.getDataProvider()).clearFilters();
        if (null == this.initFilterArchValue) {
            archFilterField.clear();
        } else {
            archFilterField.setValue(this.initFilterArchValue);
        }
        ckzFilterField.clear();
        rokFilterField.clear();
        skupinaFilterField.clear();
        kzTextFilterField.clear();
        objednatelFilterField.clear();
    }

    public void saveFilterFieldValues() {
        archFilterValue = archFilterField.getValue();
        ckzFilterValue = ckzFilterField.getValue();
        rokFilterValue = rokFilterField.getValue();
        skupinaFilterValue = skupinaFilterField.getValue();
        objednatelFilterValue = objednatelFilterField.getValue();
        kzTextFilterValue = kzTextFilterField.getValue();
    }

    public void restoreFilterFieldValues() {
        ((ListDataProvider<Zakr>) this.getDataProvider()).clearFilters();

        if (null == archFilterValue) {
            archFilterField.clear();
        } else {
            archFilterField.setValue(archFilterValue);
        }
        ckzFilterField.setValue(ckzFilterValue);
        rokFilterField.setValue(rokFilterValue);
        skupinaFilterField.setValue(skupinaFilterValue);
        kzTextFilterField.setValue(kzTextFilterValue);
        objednatelFilterField.setValue(objednatelFilterValue);
    }

    public void doFilter() {
        ListDataProvider<Zakr> listDataProvider = ((ListDataProvider<Zakr>) this.getDataProvider());
        listDataProvider.clearFilters();

        Boolean archFilterValue = archFilterField.getValue();
        String ckzFilterValue = ckzFilterField.getValue();
        Integer rokFilterValue = rokFilterField.getValue();
        String skupinaFilterValue = skupinaFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
        String kzTextFilterValue = kzTextFilterField.getValue();

        if (null != archFilterValue) {
            listDataProvider.addFilter(Zakr::getArch
                    , arch -> arch.equals(archFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(ckzFilterValue)) {
            listDataProvider.addFilter(Zakr::getKzCislo
                    , ckz -> StringUtils.containsIgnoreCase(ckz, ckzFilterValue)
            );
        }
        if (null != rokFilterValue) {
            listDataProvider.addFilter(Zakr::getRok
                    , rok -> rok.equals(rokFilterValue)
            );
        }
        if (null != skupinaFilterValue) {
            listDataProvider.addFilter(Zakr::getSkupina
                    , sk -> skupinaFilterValue.equals("") ?
                            skupinaFilterValue.equals(sk)
                            : StringUtils.containsIgnoreCase(sk, skupinaFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(objednatelFilterValue)) {
            listDataProvider.addFilter(Zakr::getObjednatel
                    , obj -> StringUtils.containsIgnoreCase(obj, objednatelFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(kzTextFilterValue)) {
            listDataProvider.addFilter(Zakr::getKzText
                    , kzt -> StringUtils.containsIgnoreCase(kzt, kzTextFilterValue)
            );
        }
        updateFooterFields();
    }

    private ComponentRenderer<Component, Zakr> archRenderer = new ComponentRenderer<>(zakr -> {
        ArchIconBox archBox = new ArchIconBox();
        archBox.showIcon(zakr.getTyp(), zakr.getArch() ? ArchIconBox.ArchState.ARCHIVED : ArchIconBox.ArchState.EMPTY);
        return archBox;
    });

    private ComponentRenderer<Component, Zakr> selectFieldRenderer = new ComponentRenderer<>(zakr -> {
        Checkbox zakSelectBox = new Checkbox();
        zakSelectBox.addValueChangeListener(event -> {
            zakr.setChecked(event.getValue());
        });
//        if ((ItemType.ZAK == zakb.getTyp()) || (ItemType.REZ == zakb.getTyp()) || (ItemType.LEK == zakb.getTyp())) {
        if ((ItemType.ZAK == zakr.getTyp()) || (ItemType.REZ == zakr.getTyp())) {
            zakSelectBox.addValueChangeListener(event -> {
                zakr.setChecked(event.getValue());
            });
            return zakSelectBox;
        } else {
            return new Span(zakr.getTyp().name());
        }
    });

    public void setArchFilterItems(final List<Boolean> archItems) {
        archFilterField = buildSelectorFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);
        archFilterField.setItems(archItems);
    }

    public void setRokFilterItems(final List<Integer> rokItems) {
        rokFilterField = buildSelectorFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);
        rokFilterField.setItems(rokItems);
    }

    public void setSkupinaFilterItems(final List<String> skupinaItems) {
        skupinaFilterField = buildSelectorFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField);
        skupinaFilterField.setItems(skupinaItems);
    }


    public Boolean getArchFilterValue() {
        return archFilterValue;
    }

    public String getCkzFilterValue() {
        return ckzFilterValue;
    }

    public Integer getRokFilterValue() {
        return rokFilterValue;
    }

    public String getSkupinaFilterValue() {
        return skupinaFilterValue;
    }

    public String getObjednatelFilterValue() {
        return objednatelFilterValue;
    }

    public String getKzTextFilterValue() {
        return kzTextFilterValue;
    }
}

