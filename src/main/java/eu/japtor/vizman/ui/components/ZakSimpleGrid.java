package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.service.ZakBasicService;
import eu.japtor.vizman.backend.service.ZakNaklVwService;
import eu.japtor.vizman.backend.service.ZakrService;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.forms.ZakBasicFormDialog;
import eu.japtor.vizman.ui.forms.ZakNaklSingleDialog;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ZakSimpleGrid extends Grid<ZakBasic> {

    // Zak simple grid field keys:
    private static final String TYP_COL_KEY = "zak-bg-typ";
    private static final String CKZ_COL_KEY = "zak-bg-ckz";
    private static final String ROK_COL_KEY = "zak-bg-rok";
    private static final String SKUPINA_COL_KEY = "zak-bg-skupina";
    private static final String OBJEDNATEL_COL_KEY = "zak-bg-objednatel";
    private static final String TEXT_KONT_COL_KEY = "zak-bg-kont-text";
    private static final String TEXT_ZAK_COL_KEY = "zak-bg-zak-text";
    private static final String SEL_COL_KEY = "zak-bg-select";
    private static final String ARCH_COL_KEY = "zak-bg-arch";
    private static final String DIGI_COL_KEY = "zak-bg-digi";

    private TextField ckzFilterField;
    private Select<Boolean> archFilterField;
    private Select<Boolean> digiFilterField;
    private Select<ItemType> typFilterField;
    private Select<Integer> rokFilterField;
    private Select<String> skupinaFilterField;
    private TextField objednatelFilterField;
    private TextField textKontFilterField;
    private TextField textZakFilterField;

    private Boolean archFilterValue;
    private Boolean digiFilterValue;
    private ItemType typeFilterValue;
    private String ckzFilterValue;
    private Integer rokFilterValue;
    private String skupinaFilterValue;
    private String textKontFilterValue;
    private String textZakFilterValue;
    private String objednatelFilterValue;

    private Boolean initFilterArchValue;
    private Boolean initFilterDigiValue;

    private boolean archFieldVisible;
    private boolean digiFieldVisible;
    private boolean selectFieldVisible;
    private boolean zaknViewBtnVisible;
    private boolean archDigiEditBtnVisible;

    private Consumer<Integer> selectionChanger;
    private Function<ZakBasic, Boolean> checkBoxEnabler;

    private ZakNaklVwService zakNaklVwService;
    private ZakrService zakrService;
    private ZakNaklSingleDialog zakNaklSingleDialog;
    private CfgPropsCache cfgPropsCache;
    private ZakBasicService zakBasicService;
    private int selCount;

    private HeaderRow filterRow;
    private ZakBasicFormDialog zakBasicFormDialog;

//    private List<GridSortOrder<ZakBasic>> initialSortOrder = Arrays.asList(
//            new GridSortOrder(
//                    this.getColumnByKey(CKZ_COL_KEY), SortDirection.DESCENDING)
//            , new GridSortOrder(
//                    this.getColumnByKey(ROK_COL_KEY), SortDirection.DESCENDING)
//    );


    public ZakSimpleGrid(
            boolean selectFieldVisible
            , Function<ZakBasic, Boolean> checkBoxEnabler
            , Consumer<Integer> selectionChanger
            , boolean archFieldVisible
            , boolean digiFieldVisible
            , boolean zaknViewBtnVisible
            , boolean archDigiEditBtnVisible
            , Boolean initFilterArchValue
            , Boolean initFilterDigiValue
            , ZakNaklVwService zakNaklVwService
            , ZakrService zakrService
            , CfgPropsCache cfgPropsCache
            , ZakBasicService zakBasicService
    ) {
        this.zakNaklVwService = zakNaklVwService;
        this.zakrService = zakrService;
        this.cfgPropsCache = cfgPropsCache;
        this.zakBasicService = zakBasicService;
        this.initFilterArchValue = initFilterArchValue;
        this.initFilterDigiValue = initFilterDigiValue;
        this.checkBoxEnabler = checkBoxEnabler;
        this.archFieldVisible = archFieldVisible;
        this.digiFieldVisible = digiFieldVisible;
        this.zaknViewBtnVisible = zaknViewBtnVisible;
        this.archDigiEditBtnVisible = archDigiEditBtnVisible;
        this.selectFieldVisible = selectFieldVisible;
        this.selectionChanger = selectionChanger;

        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setMultiSort(false);
        this.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.setId("zak-simple-grid");  // .. same ID as is used in shared-styles grid's dom module

        zakNaklSingleDialog = new ZakNaklSingleDialog(
                this.zakNaklVwService
        );

        zakBasicFormDialog = new ZakBasicFormDialog(
                this.zakBasicService
        );
        zakBasicFormDialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                updateGridAfterEdit(((ZakBasicFormDialog)(event.getSource())).getCurrentItem());
            }
        });

        this.addColumn(archRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
                .setVisible(this.archFieldVisible)
        //                .setFrozen(true)
        ;
        if (this.archDigiEditBtnVisible) {
        this.addColumn(new ComponentRenderer<>(this::buildArchDigiEditBtn))
                .setHeader("A-D")
                .setFlexGrow(0)
                .setWidth("6em")
        ;
        }
        this.addColumn(digiRenderer)
                .setHeader(("DIGI"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(DIGI_COL_KEY)
                .setVisible(this.digiFieldVisible)
        //                .setFrozen(true)
        ;
        if (this.zaknViewBtnVisible) {
            this.addColumn(new ComponentRenderer<>(this::buildZaknViewBtn))
                    .setHeader("Nak")
                    .setFlexGrow(0)
                    .setWidth("3em")
            ;
        }
        this.addColumn(ZakBasic::getTyp)
                .setHeader("Typ")
                .setFlexGrow(0)
                .setWidth("6em")
                .setSortable(true)
                .setKey(TYP_COL_KEY)
        ;
        this.addColumn(ZakBasic::getCkz)
                .setHeader("ČK-ČZ")
                .setFlexGrow(0)
                .setWidth("11em")
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true)
                .setKey(CKZ_COL_KEY)
        ;
        this.addColumn(ZakBasic::getRok)
                .setHeader("Rok zak.")
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(ROK_COL_KEY)
        ;
        this.addColumn(ZakBasic::getSkupina)
                .setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(SKUPINA_COL_KEY)
        ;
        this.addColumn(ZakBasic::getObjednatel)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setSortable(true)
                .setKey(OBJEDNATEL_COL_KEY)
        ;
        this.addColumn(checkFieldRenderer)
                .setHeader(("Výběr"))
                .setFlexGrow(0)
                .setWidth("4.5em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setKey(SEL_COL_KEY)
                .setVisible(this.selectFieldVisible)
        ;
        this.addColumn(ZakBasic::getTextKont)
                .setHeader("Kontrakt")
                .setFlexGrow(1)
                .setWidth("13em")
                .setSortable(true)
                .setKey(TEXT_KONT_COL_KEY)
        ;
        this.addColumn(ZakBasic::getTextZak)
                .setHeader("Zakázka")
                .setFlexGrow(1)
                .setWidth("13em")
                .setSortable(true)
                .setKey(TEXT_ZAK_COL_KEY)
        ;


        filterRow = this.appendHeaderRow();

        archFilterField = buildSelectorFilterField();
        archFilterField.setTextRenderer(this::archFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);

        digiFilterField = buildSelectorFilterField();
        digiFilterField.setTextRenderer(this::digiFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(DIGI_COL_KEY))
                .setComponent(digiFilterField);

        typFilterField = buildSelectorFilterField();
        typFilterField.setTextRenderer(this::typFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(TYP_COL_KEY))
                .setComponent(typFilterField);

        ckzFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(CKZ_COL_KEY))
                .setComponent(ckzFilterField);

        rokFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);

        skupinaFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField)
        ;

        textKontFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(TEXT_KONT_COL_KEY))
                .setComponent(textKontFilterField)
        ;

        textZakFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(TEXT_ZAK_COL_KEY))
                .setComponent(textZakFilterField)
        ;

        objednatelFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(OBJEDNATEL_COL_KEY))
                .setComponent(objednatelFilterField)
        ;

        for (Grid.Column col : getColumns()) {
            setResizable(col);
        }
    }


    private void updateGridAfterEdit(ZakBasic modifiedZakBasic) {
        this.setItems(zakBasicService.fetchByFiltersDescOrder(buildZakBasicFilter()));
        this.select(modifiedZakBasic);
    }

    public ZakBasicFilter buildZakBasicFilter() {
        this.saveFilterFieldValues();
        ZakBasicFilter filter = ZakBasicFilter.getEmpty();
        filter.setArch(this.getArchFilterValue());
        filter.setDigi(this.getDigiFilterValue());
        filter.setTyp(this.getTypFilterValue());
        filter.setCkz(this.getCkzFilterField());
        filter.setRokZak(this.getRokFilterValue());
        filter.setSkupina(this.getSkupinaFilterValue());
        filter.setObjednatel(this.getObjednatelFilterValue());
        filter.setTextKont(this.getTextKontFilterValue());
        filter.setTextZak(this.getTextZakFilterValue());
        return filter;
    }

    public static class ZakBasicFilter {

        String ckz;
        Integer rokZak;
        String skupina;
        Boolean arch;
        Boolean digi;
        ItemType typ;
        String objednatel;
        String textKont;
        String textZak;

        public static final ZakBasicFilter getEmpty() {
            ZakBasicFilter filter = new ZakBasicFilter();
            filter.setArch(null);
            filter.setDigi(null);
            filter.setTyp(null);
            filter.setCkz(null);
            filter.setRokZak(null);
            filter.setSkupina(null);
            filter.setObjednatel(null);
            filter.setTextKont(null);
            filter.setTextZak(null);
            return filter;
        }

        public Boolean getArch() {
            return arch;
        }
        public void setArch(Boolean arch) {
            this.arch = arch;
        }

        public Boolean getDigi() {
            return digi;
        }
        public void setDigi(Boolean digi) {
            this.digi = digi;
        }

        public ItemType getTyp() {
            return typ;
        }
        public void setTyp(ItemType typ) {
            this.typ = typ;
        }

        public String getCkz() {
            return ckz;
        }
        public void setCkz(String ckz) {
            this.ckz = ckz;
        }

        public Integer getRokZak() {
            return rokZak;
        }
        public void setRokZak(Integer rokZak) {
            this.rokZak = rokZak;
        }

        public String getSkupina() {
            return skupina;
        }
        public void setSkupina(String skupina) {
            this.skupina = skupina;
        }

        public String getObjednatel() {
            return objednatel;
        }
        public void setObjednatel(String objednatel) {
            this.objednatel = objednatel;
        }

        public String getTextKont() {
            return textKont;
        }
        public void setTextKont(String textKont) {
            this.textKont = textKont;
        }

        public String getTextZak() {
            return textZak;
        }
        public void setTextZak(String textZak) {
            this.textZak = textZak;
        }
    }

    public void reloadGridData() {
        doFilter();
        getDataProvider().refreshAll();
    }

    private void setResizable(Grid.Column column) {
        column.setResizable(true);
        Element parent = column.getElement().getParent();
        while (parent != null
                && "vaadin-grid-column-group".equals(parent.getTag())) {
            parent.setProperty("resizable", "true");
            parent = parent.getParent();
        }
    }

    private Component buildArchDigiEditBtn(ZakBasic zakBasic) {
        return new GridItemBtn(event -> {
                    this.select(zakBasic);
                    zakBasicFormDialog.openDialog(
                            false
                            , zakBasicService.fetchByIdLazy(zakBasic.getId())
                            , Operation.EDIT
                    );
                }
                , new Icon(VaadinIcon.ARCHIVE), VzmFormatUtils.getItemTypeColorName(zakBasic.getTyp())
                , "Arch & Digi edit"
        );
    }

    private Component buildZaknViewBtn(ZakBasic zakBasic) {
        return new GridItemBtn(event -> {
                    this.select(zakBasic);
                    zakNaklSingleDialog.openDialog(
                            zakrService.fetchOne(zakBasic.getId())
                            , ZakrListView.ZakrParams.getDefaultInstance(cfgPropsCache)
                            , ""
                    );
                }
                , new Icon(VaadinIcon.COIN_PILES), VzmFormatUtils.getItemTypeColorName(zakBasic.getTyp())
                , "Detailní náklady"
        );
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
        Select <T> selectorFilterField = new SelectorFilterField<>();
        selectorFilterField.addValueChangeListener(event -> doFilter());
        return selectorFilterField;
    }

    private String archFilterLabelGenerator(Boolean arch) {
        if  (null == arch) {
            return "Vše";
        } else {
            return arch ? "Ano" : "Ne";
        }
    }

    private String digiFilterLabelGenerator(Boolean digi) {
        if  (null == digi) {
            return "Vše";
        } else {
            return digi ? "Ano" : "Ne";
        }
    }

    private String typFilterLabelGenerator(ItemType typ) {
        if  (null == typ) {
            return "Vše";
        } else {
            return typ.name();
        }
    }

    public void rebuildSelectableFilterFields(List<ZakBasic> zakBasicList) {
        setArchFilterItems(zakBasicList.stream()
                .map(ZakBasic::getArch)
                .filter(Objects::nonNull)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        setDigiFilterItems(zakBasicList.stream()
                .map(ZakBasic::getDigi)
                .filter(Objects::nonNull)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        setTypFilterItems(zakBasicList.stream()
                .map(ZakBasic::getTyp)
                .filter(Objects::nonNull)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );

        setRokFilterItems(zakBasicList.stream()
                .filter(z -> null != z.getRok())
                .map(ZakBasic::getRok)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toCollection(LinkedList::new))
        );
        setSkupinaFilterItems(zakBasicList.stream()
                .map(ZakBasic::getSkupina)
                .filter(Objects::nonNull)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
    }

    public void initFilterFieldValues() {
        ((ListDataProvider<ZakBasic>) this.getDataProvider()).clearFilters();
        if (null == this.initFilterArchValue) {
            archFilterField.clear();
        } else {
            archFilterField.setValue(this.initFilterArchValue);
        }
        if (null == this.initFilterDigiValue) {
            digiFilterField.clear();
        } else {
            digiFilterField.setValue(this.initFilterDigiValue);
        }
        typFilterField.clear();
        ckzFilterField.clear();
        rokFilterField.clear();
        skupinaFilterField.clear();
        textKontFilterField.clear();
        textZakFilterField.clear();
        objednatelFilterField.clear();
    }

    public void saveFilterFieldValues() {
        archFilterValue = archFilterField.getValue();
        digiFilterValue = digiFilterField.getValue();
        ckzFilterValue = ckzFilterField.getValue();
        rokFilterValue = rokFilterField.getValue();
        skupinaFilterValue = skupinaFilterField.getValue();
        textKontFilterValue = textKontFilterField.getValue();
        textZakFilterValue = textZakFilterField.getValue();
        objednatelFilterValue = objednatelFilterField.getValue();
    }

    public void restoreFilterFieldValues() {
        ((ListDataProvider<ZakBasic>) this.getDataProvider()).clearFilters();
        if (null == archFilterValue) {
            archFilterField.clear();
        } else {
            archFilterField.setValue(archFilterValue);
        }
        if (null == digiFilterValue) {
            digiFilterField.clear();
        } else {
            digiFilterField.setValue(digiFilterValue);
        }
        ckzFilterField.setValue(ckzFilterValue);
        rokFilterField.setValue(rokFilterValue);
        skupinaFilterField.setValue(skupinaFilterValue);
        textKontFilterField.setValue(textKontFilterValue);
        textZakFilterField.setValue(textZakFilterValue);
        objednatelFilterField.setValue(objednatelFilterValue);
    }

    public void doFilter() {
        ListDataProvider<ZakBasic> listDataProvider = ((ListDataProvider<ZakBasic>) this.getDataProvider());
        listDataProvider.clearFilters();

        Boolean archFilterValue = archFilterField.getValue();
        Boolean digiFilterValue = digiFilterField.getValue();
        ItemType typFilterValue = typFilterField.getValue();
        Integer rokFilterValue = rokFilterField.getValue();
        String ckzFilterValue = ckzFilterField.getValue();
        String skupinaFilterValue = skupinaFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
        String textKontFilterValue = textKontFilterField.getValue();
        String textZakFilterValue = textZakFilterField.getValue();

        if (null != archFilterValue) {
            listDataProvider.addFilter(ZakBasic::getArch
                    , arch -> arch.equals(archFilterValue)
            );
        }
        if (null != digiFilterValue) {
            listDataProvider.addFilter(ZakBasic::getDigi
                    , digi -> digi.equals(digiFilterValue)
            );
        }
        if (null != typFilterValue) {
            listDataProvider.addFilter(ZakBasic::getTyp
                    , typ -> typ.equals(typFilterValue)
            );
        }
        if (null != rokFilterValue) {
            listDataProvider.addFilter(ZakBasic::getRok
                    , rok -> rok.equals(rokFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(ckzFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getCkz
                    , kzc -> StringUtils.containsIgnoreCase(kzc, ckzFilterValue)
            );
        }
        if (null != skupinaFilterValue) {
            listDataProvider.addFilter(ZakBasic::getSkupina
                    , sk -> skupinaFilterValue.equals("") ?
                            skupinaFilterValue.equals(sk)
                            : StringUtils.containsIgnoreCase(sk, skupinaFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(objednatelFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getObjednatel
                    , obj -> StringUtils.containsIgnoreCase(obj, objednatelFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(textKontFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getTextKont
                    , tk -> StringUtils.containsIgnoreCase(tk, textKontFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(textZakFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getTextZak
                    , tz -> StringUtils.containsIgnoreCase(tz, textZakFilterValue)
            );
        }
    }

    private ComponentRenderer<Component, ZakBasic> archRenderer = new ComponentRenderer<>(zakb -> {
        ArchIconBox archBox = new ArchIconBox();
        archBox.showIcon(zakb.getTyp(), zakb.getArch() ? ArchIconBox.ArchState.ARCHIVED : ArchIconBox.ArchState.EMPTY);
        return archBox;
    });

    private ComponentRenderer<Component, ZakBasic> digiRenderer = new ComponentRenderer<>(zakb -> {
        DigiIconBox digiBox = new DigiIconBox();
        digiBox.showIcon(zakb.getTyp(), zakb.getDigi() ? DigiIconBox.DigiState.DIGI_ONLY : DigiIconBox.DigiState.EMPTY);
        return digiBox;
    });

    private ComponentRenderer<Component, ZakBasic> checkFieldRenderer = new ComponentRenderer<>(zakb -> {
        Checkbox zakCheckBox = new Checkbox();
        zakCheckBox.addValueChangeListener(event -> {
            zakb.setChecked(event.getValue());
            if (event.getValue()) {
                selCount++;
            } else {
                selCount--;
            }
            if  (null != selectionChanger) {
                selectionChanger.accept(selCount);
            }
        });
        if ( zakb.isSelectableForPruh() && ((null != checkBoxEnabler && (checkBoxEnabler.apply(zakb)))) ) {
            zakCheckBox.addValueChangeListener(event -> zakb.setChecked(event.getValue()));
            return zakCheckBox;
        } else {
            return new Span("X");
        }
    });

    private void setArchFilterItems(final List<Boolean> archItems) {
        archFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);
        archFilterField.setItems(archItems);
    }

    private void setDigiFilterItems(final List<Boolean> digiItems) {
        digiFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(DIGI_COL_KEY))
                .setComponent(digiFilterField);
        digiFilterField.setItems(digiItems);
    }

    private void setTypFilterItems(final List<ItemType> typItems) {
        typFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(TYP_COL_KEY))
                .setComponent(typFilterField);
        typFilterField.setItems(typItems);
    }

    private void setRokFilterItems(final List<Integer> rokItems) {
        rokFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);
        rokFilterField.setItems(rokItems);
    }

    private void setSkupinaFilterItems(final List<String> skupinaItems) {
        skupinaFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField);
        skupinaFilterField.setItems(skupinaItems);
    }

    public int getSelCount() {
        return selCount;
    }

    public void setSelCount(int selCount) {
        this.selCount = selCount;
    }

    public Boolean getArchFilterValue() {
        return archFilterField.getValue();
    }

    public Boolean getDigiFilterValue() {
        return digiFilterField.getValue();
    }

    public String getCkzFilterField() {
        return ckzFilterField.getValue();
    }

    public ItemType getTypFilterValue() {
        return typFilterField.getValue();
    }

    public Integer getRokFilterValue() {
        return rokFilterField.getValue();
    }

    public String getSkupinaFilterValue() {
        return skupinaFilterField.getValue();
    }

    public String getObjednatelFilterValue() {
        return objednatelFilterField.getValue();
    }

    public String getTextKontFilterValue() {
        return textKontFilterField.getValue();
    }

    public String getTextZakFilterValue() {
        return textZakFilterField.getValue();
    }
}
