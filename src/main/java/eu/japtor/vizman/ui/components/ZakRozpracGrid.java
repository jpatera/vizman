package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Label;
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
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.ArchIconBox;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.service.*;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.forms.ZakFormDialog;
import eu.japtor.vizman.ui.forms.ZaqaGridDialog;
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
    public static final String KZCISLO_COL_KEY = "zakr-bg-kzcislo";
    public static final String ROK_COL_KEY = "zakr-bg-rok";
    public static final String SKUPINA_COL_KEY = "zakr-bg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zakr-bg-objednatel";
    public static final String KZTEXT_COL_KEY = "zakr-bg-kztext";
    public static final String SEL_COL_KEY = "zakr-bg-select";
    public static final String MENA_COL_KEY = "zakr-bg-mena";
    public static final String ARCH_COL_KEY = "zakr-bg-arch";
    public static final String HONOR_CISTY_COL_KEY = "zakr-bg-honor-cisty";
    public static final String RP_COL_KEY = "zakr-bg-rp";
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
    private ZaqaGridDialog zaqaGridDialog;

    private TextField kzCisloFilterField;
    private Select<Boolean> archFilterField;
    private Select<Integer> rokFilterField;
    private Select<String> skupinaFilterField;
    private TextField objednatelFilterField;
    private TextField kzTextFilterField;

    private Boolean archFilterValue;
    private Integer rokFilterValue;
    private String skupinaFilterValue;
    private String kzCisloFilterValue;
    private String kzTextFilterValue;
    private String objednatelFilterValue;

    private Boolean initFilterArchValue;
    private boolean archFieldVisible;
    private boolean selectFieldVisible;

//    private BigDecimal kurzEur;

    private HeaderRow descHeaderRow;
    private HeaderRow filterHeaderRow;
    private FooterRow sumFooterRow;
    private Registration zakrGridEditRegistration = null;
    private Zakr editedItem;
    private boolean editedItemChanged;
    private BiConsumer<Zakr, Operation> itemSaver;

    private ZakrService zakrService;
    private ZakService zakService;
    private FaktService faktService;
    private ZaqaService zaqaService;
    private CfgPropsCache cfgPropsCache;
    private ZakrListView.ZakrParams zakrParams;

    public ZakRozpracGrid(
            boolean selectFieldVisible
            , boolean archFieldVisible
            , Boolean initFilterArchValue
            , BiConsumer<Zakr, Operation> itemSaver
//            , BigDecimal kurzEur
            , ZakrListView.ZakrParams zakrParams
            , ZakrService zakrService
            , ZakService zakService
            , FaktService faktService
            , ZaqaService zaqaService
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
        this.faktService = faktService;
        this.zaqaService = zaqaService;
        this.cfgPropsCache = cfgPropsCache;

        zaqaGridDialog = new ZaqaGridDialog(
                this.zaqaService, this.zakrService
        );
        zakFormDialog = new ZakFormDialog(
                this.zakService, this.faktService, this.cfgPropsCache
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

        zakrGridEditRegistration = this.addItemDoubleClickListener(event -> {
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
                .setHeader("ČK/ČZ")
                .setFlexGrow(0)
                .setWidth("7em")
                .setSortable(true)
                .setKey(KZCISLO_COL_KEY)
        ;
        this.addColumn(Zakr::getRok)
                .setHeader("Rok")
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
        Grid.Column<Zakr> colHonorCisty = this.addColumn(honorCistyValueProvider)
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(getHonorCistyByKurz(zakr1), getHonorCistyByKurz(zakr2)))
                .setHeader("Hon.č. [CZK]")
                .setFlexGrow(0)
                .setWidth("7em")
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
                .setHeader("Výkon rx-ry")
                .setFlexGrow(0)
                .setWidth("7em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(PERFORMANCE_COL_KEY)
                .setResizable(true)
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
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(
//                        zakr1.getRpHotovoByKurz(zakrParams.getKurzEur()), zakr2.getRpHotovoByKurz(zakrParams.getKurzEur())
                        zakr1.getRpHotovoByKurz(), zakr2.getRpHotovoByKurz()
                ))
                .setHeader("Hotovo RP")
                .setFlexGrow(0)
                .setWidth("7em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(FINISHED_COL_KEY)
                .setResizable(true)
                ;
//        this.getDefaultHeaderRow().getCell(colRpHotovo).setComponent(hotovoHeaderLabel);


        Grid.Column<Zakr> colRpZbyva = this.addColumn(rpZbyvaByKurzGridValueProvider)
//                .setComparator((zakr1, zakr2) -> zakr1.getRpZbyva()
//                        .compareTo(zakr2.getRpZbyva()))
//                .setComparator(ObjectUtils.compare(zakr.1firstComparable, secondComparable))
                .setHeader("Zbývá RP")
//                .setHeader(headerLabel)
//                .setComparator((p1, p2) -> getRpZbyvaByKurz(p1).compareTo(getRpZbyvaByKurz(p2)))
                .setComparator((zakr1, zakr2) -> ObjectUtils.compare(
//                        zakr1.getRpZbyvaByKurz(zakrParams.getKurzEur()), zakr2.getRpZbyvaByKurz(zakrParams.getKurzEur())
                        zakr1.getRpZbyvaByKurz(), zakr2.getRpZbyvaByKurz()
                ))
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("7em")
                .setTextAlign(ColumnTextAlign.END)
                .setKey(REMAINS_COL_KEY)
                .setResizable(true)
        ;

        Grid.Column<Zakr> colVysledek = this.addColumn(vysledekGridValueProvider)
                .setHeader("Výsledek")
                .setFlexGrow(0)
                .setWidth("7em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(RESULT_COL_KEY)
                .setResizable(true)
                ;

        Grid.Column<Zakr> colVysledekP8 = this.addColumn(vysledekP8GridValueProvider)
                .setHeader("Výsledek P8")
                .setFlexGrow(0)
                .setWidth("7em")
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
                .setHeader("Mzdy+P+R")
                .setFlexGrow(0)
                .setWidth("7em")
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

        sumFooterRow = this.appendFooterRow();


        // =============
        // Headers
        // =============

        descHeaderRow = this.appendHeaderRow();
        filterHeaderRow = this.appendHeaderRow();

        // =============
        // Description
        // =============

//        String htmlWithTooltip = String.format(
//                "<span title=\"%s\">%s</span>", cell.getText(),
//                cell.getText());
//                column.getPropertyId());

        Label honorarCistyLabel = new Label("Def.");
        honorarCistyLabel.getElement()
                .setProperty("title", "[Honorář čistý] = (SUM(fakt) - SUM(sub)) * kurz")
//                .setProperty("text-align", "center")
        ;
        descHeaderRow.getCell(colHonorCisty).setComponent(honorarCistyLabel);

        Label rxRyVykonyDescLabel = new Label("Def.");
        rxRyVykonyDescLabel.getElement()
                .setProperty("title", "[Výkon rx-ry] = [Honorář čistý] * ([RY] - [RX])")
//                .setProperty("text-align", "center")
        ;
        descHeaderRow.getCell(colRxRyVykon).setComponent(rxRyVykonyDescLabel);

        Label rpZbyvaDescLabel = new Label("Def.");
        rpZbyvaDescLabel.getElement()
                .setProperty("title", "[Zbývá RP] = [Honorář čistý] * (100% - [RP])")
//                .setProperty("text-align", "center")
        ;
        descHeaderRow.getCell(colRpZbyva).setComponent(rpZbyvaDescLabel);
//        rpZbyvaDescLabel.getStyle()
//                .set("text-align", "center");
//        this.getDefaultHeaderRow().getCell(colRpZbyva).setComponent(zbyvaHeaderLabel);
//        String htmlWithTooltip = String.format(
//                "<span title=\"%s\">%s</span>", cell.getText(),
//                cell.getText());
//                column.getPropertyId());

        Label hotovoHeaderLabel = new Label("Def.");
        hotovoHeaderLabel.getElement()
                .setProperty("title", "[Hotovo RP] = [Honorář čistý] * [RP]");
        descHeaderRow.getCell(colRpHotovo).setComponent(hotovoHeaderLabel);


        Label vysledekHeaderLabel = new Label("Def.");
        vysledekHeaderLabel.getElement()
                .setProperty("title", "[Výsledek] = [Honorář čistý] - [Mzdy] * (1 + koef_pojist) * (1 + koef_rezie)");
        descHeaderRow.getCell(colVysledek).setComponent(vysledekHeaderLabel);

        // =============
        // Filters
        // =============



        archFilterField = buildSelectorFilterField();
//        archFilterField.setItemLabelGenerator(this::archFilterLabelGenerator);
        archFilterField.setTextRenderer(this::archFilterLabelGenerator);
        filterHeaderRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);

        kzCisloFilterField = buildTextFilterField();
        filterHeaderRow.getCell(this.getColumnByKey(KZCISLO_COL_KEY))
                .setComponent(kzCisloFilterField);

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

    private void updateVysledekSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(FINISHED_COL_KEY))
                .setText("" + (VzmFormatUtils.moneyFormat.format(calcVysledekSum())));
    }

    private void updateZbyvaSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(REMAINS_COL_KEY))
                .setText("" + (VzmFormatUtils.moneyFormat.format(calcZbyvaSum())));
    }

    private void updateVykonSumField() {
        sumFooterRow
                .getCell(this.getColumnByKey(PERFORMANCE_COL_KEY))
                .setText("" + (VzmFormatUtils.moneyFormat.format(calcVykonSum())));
    }

    private BigDecimal calcVysledekSum() {
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

    private void updateRecCountField() {
        sumFooterRow
                .getCell(this.getColumnByKey(KZCISLO_COL_KEY))
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

    private void setVysledekSum() {
//        sumTextComponent.setText(String.format("%s  [ Fond: %s ]", ZAK_TEXT_SUM, monthHourFond));

//        sumTextComponent.setText(String.format("%s", ZAK_TEXT_SUM));
//        sumHodsFooterRow.getCell(pruhZakGrid.getColumnByKey(ZAK_TEXT_COL_KEY))
//                .setComponent(sumTextComponent);



//        sumFooterRow.getCell(this.getColumnByKey(FINISHED_COL_KEY))
//                .setText(getVysledekSumString());



//        for (int day = 1; day <= 31; day++) {
//            Grid.Column col = pruhZakGrid.getColumnByKey(DZ_KEY_PREF + String.valueOf(day));
//            if (null != col) {
//                sumHodsFooterRow.getCell(col).setText(getSumHodString(day));
//            }
//        }
    }

//    private String getVysledekSumString(int day) {
//        BigDecimal sumVysledek = getDaySumHodSum(day);
//        return (null == sumVysledek || sumVysledek.compareTo(BigDecimal.ZERO) == 0) ?
//                "" : VzmFormatUtils.decHodFormat.format(sumVysledek);
//    }


//    private void calcAndSetPruhMissingHods() {
//        for (int day = 1; day <= 31; day++) {
//            Grid.Column col = pruhZakGrid.getColumnByKey(DZ_KEY_PREF + String.valueOf(day));
//            if (null != col) {
//                getMissingHodString(day);
//                missingHodsFooterRow.getCell(col).setText(getMissingHodString(day));
//            }
//        }
//    }

    Component buildZakViewBtn(Zakr zakr) {
        return new GridItemBtn(event -> zakFormDialog.openDialog(
                true, zakService.fetchOne(zakr.getId()), Operation.EDIT)
                , new Icon(VaadinIcon.EYE), VzmFormatUtils.getItemTypeColorName(zakr.getTyp())
        );
    }

    Component buildZaqaOpenBtn(Zakr zakr) {
        return new GridItemBtn(event -> zaqaGridDialog.openDialog(
                zakrService.fetchOne(zakr.getId()))
                , new Icon(VaadinIcon.LINES_LIST), null
        );
//        Button btn = new GridItemEditBtn(event -> zaqaFormDialog.openDialog(
//                zaqaService.fetchAllByZakId(zakr.getId()), Operation.EDIT)
//                , VzmFormatUtils.getItemTypeColorName(zakr.getTyp()));
    }

    public void setZakrParams(ZakrListView.ZakrParams zakrParams) {
        this.zakrParams = zakrParams;

    }

    public ZakrListView.ZakrParams getZakrParams() {
        return zakrParams;
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

//                    rpHotovoGridValueProvider.apply(editedItem);
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


//    private boolean writeZakrToBeanIfValid(Binder zakrEditorBinder, Editor<Zakr> zakrEditor) {
//        boolean isValid = zakrEditor.save();
////        boolean isValid = zakrEditorBinder.writeBeanIfValid(zakrEditor.getItem());
//        if (!isValid) {
//            ConfirmDialog
//                    .createWarning()
//                    .withCaption("Editace zakázky")
//                    .withMessage("Zakázku nelze uložit, některá pole zřejmě nejsou správně vyplněna.")
//                    .open();
//            return false;
//        }
//        editedItem = zakrEditor.getItem();
//        return true;
//    }

    private ValueProvider<Zakr, String> rpGridValueProvider =
            zakr -> null == zakr.getRp() ? "" : VzmFormatUtils.procIntFormat.format(zakr.getRp())
    ;

    private ValueProvider<Zakr, String> r0GridValueProvider =
            zakr -> null == zakr.getR0() ? "" : VzmFormatUtils.procIntFormat.format(zakr.getR0())
    ;

    private ValueProvider<Zakr, String> r1GridValueProvider =
            zakr -> null == zakr.getR1() ? "" : VzmFormatUtils.procIntFormat.format(zakr.getR1())
    ;

    private ValueProvider<Zakr, String> r2GridValueProvider =
            zakr -> null == zakr.getR2() ? "" : VzmFormatUtils.procIntFormat.format(zakr.getR2())
    ;

    private ValueProvider<Zakr, String> r3GridValueProvider =
            zakr -> null == zakr.getR3() ? "" : VzmFormatUtils.procIntFormat.format(zakr.getR3())
    ;

    private ValueProvider<Zakr, String> r4GridValueProvider =
            zakr -> null == zakr.getR4() ? "" : VzmFormatUtils.procIntFormat.format(zakr.getR4())
    ;


    private ValueProvider<Zakr, String> honorCistyValueProvider = zakr -> {
        BigDecimal honorCistyByKurz = getHonorCistyByKurz(zakr);
        if (null == honorCistyByKurz) {
            return "";
        } else {
            return VzmFormatUtils.moneyFormat.format(honorCistyByKurz);
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
            return VzmFormatUtils.moneyFormat.format(rxRyVykonByKurz);
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
//        BigDecimal rpZbyvaByKurz = zakr.getRpZbyvaByKurz(zakrParams.getKurzEur());
        BigDecimal rpZbyvaByKurz = zakr.getRpZbyvaByKurz();
        if (null == rpZbyvaByKurz) {
            return "";
        } else {
            return VzmFormatUtils.moneyFormat.format(rpZbyvaByKurz);
        }
    };
//    private  BigDecimal getRpZbyvaByKurz(Zakr zakr) {
//        BigDecimal rpZbyva = zakr.getRpZbyva();
//        if (null == rpZbyva) {
//            return null;
//        } else {
//            return zakr.getMena() == EUR ?
//                    rpZbyva.multiply(zakrParams.getKurzEur()) :
//                    rpZbyva;
//        }
//    }

    public ValueProvider<Zakr, String> rpHotovoGridValueProvider = zakr -> {
        if (null == zakr.getRpHotovo()) {
            return "";
        } else {
//            BigDecimal rpVysledek = zakr.getRpHotovoByKurz(zakrParams.getKurzEur());
            BigDecimal rpVysledek = zakr.getRpHotovoByKurz();
            return null == rpVysledek ? "" : VzmFormatUtils.moneyFormat.format(rpVysledek);
        }
    };
//    private  BigDecimal getRpHotovokByKurz(Zakr zakr) {
//        BigDecimal rpHotovo = zakr.getRpHotovo();
//        if (null == rpHotovo) {
//            return null;
//        } else {
//            return zakr.getMena() == EUR ?
//                    rpHotovo.multiply(zakrParams.getKurzEur()) :
//                    rpHotovo;
//        }
//    }

    private ValueProvider<Zakr, String> vysledekGridValueProvider = zakr -> {
            BigDecimal vysledek = zakr.getVysledekByKurz();
            return null == vysledek ? "" : VzmFormatUtils.moneyFormat.format(vysledek);
    };

    private ValueProvider<Zakr, String> vysledekP8GridValueProvider = zakr -> {
//            BigDecimal rpVysledek = getRpVysledekP8(zakr);
//            return null == rpVysledek ? "" : VzmFormatUtils.moneyFormat.format(rpVysledek);
            return "";
    };

    private ValueProvider<Zakr, String> naklMzdyValueProvider = zakr -> {
            BigDecimal naklMzdy = zakr.getNaklMzdy();
            return null == naklMzdy ? "" : VzmFormatUtils.moneyFormat.format(naklMzdy);
    };

    private ValueProvider<Zakr, String> naklMzdyPojistRezieValueProvider = zakr -> {
            BigDecimal naklMzdy = zakr.calcNaklMzdyPojistRezie(zakrParams.getKoefPojist(), zakrParams.getKoefRezie());
            if (null == naklMzdy) {
                return "";
            } else {
                return VzmFormatUtils.moneyFormat.format(naklMzdy);
            }
    };

    private ValueProvider<Zakr, String> menaValueProvider = zakr -> {
        return null == zakr.getMena() ? null : zakr.getMena().name();
    };


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

//    private BigDecimal getRxRyVykon(final Zakr zakr) {
//        BigDecimal activeRxValue = getActiveRxValue(zakr);
//        return null == activeRxValue || null == zakr.getHonorCisty() ?
//                null : activeRxValue.multiply(zakr.getHonorCisty()).divide(BigDecimal.valueOf(100L));
//    }

//    private BigDecimal getRpZbyva(final Zakr zakr) {
//        BigDecimal lastRxValue = zakr.getRP();
//        return null == lastRxValue || null == zakr.getHonorCisty() ? null :
//                zakr.getHonorCisty().subtract(
//                        lastRxValue.multiply(zakr.getHonorCisty()).divide(BigDecimal.valueOf(100L))
//        );
//    }
//
//    private BigDecimal getRpHotovo(final Zakr zakr) {
//        BigDecimal lastRxValue = zakr.getRP();
//        return null == lastRxValue || null == zakr.getHonorCisty() ? null :
//                lastRxValue.multiply(zakr.getHonorCisty()).divide(BigDecimal.valueOf(100L)
//        );
//    }

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
        Select <T> selector = new Select<>();
        selector.setSizeFull();
        selector.setEmptySelectionCaption("Vše");
        selector.setEmptySelectionAllowed(true);
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
                "Výkon "
                + (null == zakrParams.getRx() ? "rx" : zakrParams.getRx())
                + "-"
                + (null == zakrParams.getRy() ? "ry" : zakrParams.getRy())
        );
        updateFooterFields();
    }

    private void updateFooterFields() {
        updateVysledekSumField();
        updateZbyvaSumField();
        updateVykonSumField();
        updateRecCountField();
    }

    public void populateGridDataAndRestoreFilters(List<Zakr> zakrList) {
        saveFilterValues();
        setItems(zakrList);
        restoreFilterValues();
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
        rokFilterField.clear();
        skupinaFilterField.clear();
        kzCisloFilterField.clear();
        kzTextFilterField.clear();
        objednatelFilterField.clear();
    }

    public void saveFilterValues() {
        archFilterValue = archFilterField.getValue();
        rokFilterValue = rokFilterField.getValue();
        skupinaFilterValue = skupinaFilterField.getValue();
        kzCisloFilterValue = kzCisloFilterField.getValue();
        kzTextFilterValue = kzTextFilterField.getValue();
        objednatelFilterValue = objednatelFilterField.getValue();
    }

    public void restoreFilterValues() {
        ((ListDataProvider<Zakr>) this.getDataProvider()).clearFilters();
//        if (null == this.initFilterArchValue) {
//            archFilterField.clear();
//        } else {
//            archFilterField.setValue(this.initFilterArchValue);
//        }
        if (null == archFilterValue) {
            archFilterField.clear();
        } else {
            archFilterField.setValue(archFilterValue);
        }
        rokFilterField.setValue(rokFilterValue);
        skupinaFilterField.setValue(skupinaFilterValue);
        kzCisloFilterField.setValue(kzCisloFilterValue);
        kzTextFilterField.setValue(kzTextFilterValue);
        objednatelFilterField.setValue(objednatelFilterValue);
    }

    public void doFilter() {
        ListDataProvider<Zakr> listDataProvider = ((ListDataProvider<Zakr>) this.getDataProvider());
        listDataProvider.clearFilters();

        Boolean archFilterValue = archFilterField.getValue();
        Integer rokFilterValue = rokFilterField.getValue();
        String kzCisloFilterValue = kzCisloFilterField.getValue();
        String skupinaFilterValue = skupinaFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
        String kzTextFilterValue = kzTextFilterField.getValue();

        if (null != archFilterValue) {
            listDataProvider.addFilter(Zakr::getArch
                    , arch -> arch.equals(archFilterValue)
            );
        }
        if (null != rokFilterValue) {
            listDataProvider.addFilter(Zakr::getRok
                    , rok -> rok.equals(rokFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(kzCisloFilterValue)) {
            listDataProvider.addFilter(Zakr::getKzCislo
                    , kzc -> StringUtils.containsIgnoreCase(kzc, kzCisloFilterValue)
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

//    private static Comparator<BigDecimal> bigDecimalSafeComparator = Comparator.nullsFirst(BigDecimal::compareTo);
//
//    public int safeCompareTo(BigDecimal that) {
//        return bigDecimalSafeComparator.compare(this, that);
//    }

    public Boolean getArchFilterValue() {
        return archFilterValue;
    }

    public Integer getRokFilterValue() {
        return rokFilterValue;
    }

    public String getSkupinaFilterValue() {
        return skupinaFilterValue;
    }
}

