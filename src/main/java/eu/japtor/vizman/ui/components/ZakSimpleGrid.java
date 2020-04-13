package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import eu.japtor.vizman.backend.entity.*;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ZakSimpleGrid extends Grid<ZakBasic> {

    // Zak simple grid field keys:
    public static final String KZCISLO_COL_KEY = "zak-bg-kzcislo";
    public static final String ROK_COL_KEY = "zak-bg-rok";
    public static final String SKUPINA_COL_KEY = "zak-bg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zak-bg-objednatel";
//    public static final String KZTEXT_COL_KEY = "zak-bg-kztext";
    public static final String TEXT_KONT_COL_KEY = "zak-bg-kont-text";
    public static final String TEXT_ZAK_COL_KEY = "zak-bg-zak-text";
    public static final String SEL_COL_KEY = "zak-bg-select";
    public static final String ARCH_COL_KEY = "zak-bg-arch";
    public static final String DIGI_COL_KEY = "zak-bg-digi";

    TextField ckzFilterField;
    Select<Boolean> archFilterField;
    Select<Boolean> digiFilterField;
    Select<Integer> rokFilterField;
    Select<String> skupinaFilterField;
    TextField objednatelFilterField;
//    TextField kzTextFilterField;
    TextField textKontFilterField;
    TextField textZakFilterField;

    private Boolean initFilterArchValue;
    private Boolean initFilterDigiValue;
    private boolean archFieldVisible;
    private boolean digiFieldVisible;
    private boolean selectFieldVisible;
    private Consumer<Integer> selectionChanger;
    private Function<ZakBasic, Boolean> checkBoxEnabler;

    private int selCount;

    HeaderRow filterRow;

    public ZakSimpleGrid(
            boolean selectFieldVisible
            , Function<ZakBasic, Boolean> checkBoxEnabler
            , Consumer<Integer> selectionChanger
            , boolean archFieldVisible
            , boolean digiFieldVisible
            , Boolean initFilterArchValue
            , Boolean initFilterDigiValue
    ) {
        this.initFilterArchValue = initFilterArchValue;
        this.initFilterDigiValue = initFilterDigiValue;
        this.checkBoxEnabler = checkBoxEnabler;
        this.archFieldVisible = archFieldVisible;
        this.digiFieldVisible = digiFieldVisible;
        this.selectFieldVisible = selectFieldVisible;
        this.selectionChanger = selectionChanger;

        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setMultiSort(false);
        this.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.setId("zak-simple-grid");  // .. same ID as is used in shared-styles grid's dom module

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


        this.addColumn(archRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
                .setVisible(this.archFieldVisible);
        //                .setFrozen(true)
        ;
        this.addColumn(digiRenderer)
                .setHeader(("DIGI"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(DIGI_COL_KEY)
                .setVisible(this.digiFieldVisible);
        //                .setFrozen(true)
        ;
        this.addColumn(ZakBasic::getCkz)
                .setHeader("ČK-ČZ")
                .setFlexGrow(0)
                .setWidth("9em")
                .setSortable(true)
                .setKey(KZCISLO_COL_KEY)
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
                .setVisible(this.selectFieldVisible);
        ;
//        this.addColumn(ZakBasic::getKzText)
//                .setHeader("Text")
//                .setFlexGrow(1)
//                .setWidth("25em")
//                .setSortable(true)
//                .setKey(KZTEXT_COL_KEY)
//        ;
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

//        archFilterField = buildArchFilterField();
        archFilterField = buildSelectorFilterField();
//        archFilterField.setItemLabelGenerator(this::archFilterLabelGenerator);
        archFilterField.setTextRenderer(this::archFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);

        digiFilterField = buildSelectorFilterField();
//        archFilterField.setItemLabelGenerator(this::archFilterLabelGenerator);
        digiFilterField.setTextRenderer(this::digiFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(DIGI_COL_KEY))
                .setComponent(digiFilterField);

        ckzFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(KZCISLO_COL_KEY))
                .setComponent(ckzFilterField);

        rokFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);

        skupinaFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField)
        ;

//        kzTextFilterField = buildTextFilterField();
//        filterRow.getCell(this.getColumnByKey(KZTEXT_COL_KEY))
//                .setComponent(kzTextFilterField)
//        ;

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

    private void setResizable(Grid.Column column) {
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

    public void populateGridDataAndRebuildFilterFields(List<ZakBasic> zakBasicList) {
        this.setItems(zakBasicList);
        this.setRokFilterItems(zakBasicList.stream()
                .filter(z -> null != z.getRok())
                .map(ZakBasic::getRok)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        this.setSkupinaFilterItems(zakBasicList.stream()
                .map(ZakBasic::getSkupina)
                .filter(s -> null != s)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        this.setArchFilterItems(zakBasicList.stream()
                .map(ZakBasic::getArch)
                .filter(a -> null != a)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
    }

    public void setInitialFilterValues() {
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
        rokFilterField.clear();
        skupinaFilterField.clear();
        ckzFilterField.clear();
//        kzTextFilterField.clear();
        textKontFilterField.clear();
        textZakFilterField.clear();
        objednatelFilterField.clear();
    }

    public void doFilter() {
        ListDataProvider<ZakBasic> listDataProvider = ((ListDataProvider<ZakBasic>) this.getDataProvider());
        listDataProvider.clearFilters();

        Boolean archFilterValue = archFilterField.getValue();
        Boolean digiFilterValue = digiFilterField.getValue();
        Integer rokFilterValue = rokFilterField.getValue();
        String kzCisloFilterValue = ckzFilterField.getValue();
        String skupinaFilterValue = skupinaFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
//        String kzTextFilterValue = kzTextFilterField.getValue();
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
        if (null != rokFilterValue) {
            listDataProvider.addFilter(ZakBasic::getRok
                    , rok -> rok.equals(rokFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(kzCisloFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getCkz
                    , kzc -> StringUtils.containsIgnoreCase(kzc, kzCisloFilterValue)
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
//        if (StringUtils.isNotEmpty(kzTextFilterValue)) {
//            listDataProvider.addFilter(ZakBasic::getKzText
//                    , kzt -> StringUtils.containsIgnoreCase(kzt, kzTextFilterValue)
//            );
//        }
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
                selectionChanger.accept(Integer.valueOf(selCount));
            }
        });
//        if ((ItemType.ZAK == zakb.getTyp()) || (ItemType.REZ == zakb.getTyp()) || (ItemType.LEK == zakb.getTyp())) {
        if ( ((ItemType.ZAK == zakb.getTyp()) || (ItemType.REZ == zakb.getTyp()))
                && ((null != checkBoxEnabler && (checkBoxEnabler.apply(zakb)))) ) {
            zakCheckBox.addValueChangeListener(event -> {
                zakb.setChecked(event.getValue());
            });
            return zakCheckBox;
        } else {
            return new Span("X");
        }
    });

    public void setArchFilterItems(final List<Boolean> archItems) {
        archFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);
        archFilterField.setItems(archItems);
    }

    public void setDigiFilterItems(final List<Boolean> digiItems) {
        digiFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(DIGI_COL_KEY))
                .setComponent(digiFilterField);
        digiFilterField.setItems(digiItems);
    }

    public void setRokFilterItems(final List<Integer> rokItems) {
        rokFilterField = buildSelectorFilterField();
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);
        rokFilterField.setItems(rokItems);
    }

    public void setSkupinaFilterItems(final List<String> skupinaItems) {
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

    public Integer getRokFilterValue() {
        return rokFilterField.getValue();
    }

    public String getSkupinaFilterValue() {
        return skupinaFilterField.getValue();
    }
}

