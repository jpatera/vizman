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

import java.util.List;

public class ZakBasicGrid extends Grid<ZakBasic> {

    // Zak Compact Grid field keys:
    public static final String KZCISLO_COL_KEY = "zak-bg-kzcislo";
    public static final String ROK_COL_KEY = "zak-bg-rok";
    public static final String SKUPINA_COL_KEY = "zak-bg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zak-bg-objednatel";
    public static final String KZTEXT_COL_KEY = "zak-bg-kztext";
    public static final String SEL_COL_KEY = "zak-bg-select";
    public static final String ARCH_COL_KEY = "zak-bg-arch";

//    Grid<Zak> zakGrid;

    TextField kzCisloFilterField;
    Select<Boolean> archFilterField;
    Select<Integer> rokFilterField;
    Select<String> skupinaFilterField;
    TextField objednatelFilterField;
    TextField kzTextFilterField;

    private Boolean initFilterArchValue;
    private boolean archFieldVisible;
    private boolean selectFieldVisible;

    public ZakBasicGrid(boolean selectFieldVisible, boolean archFieldVisible, Boolean initFilterArchValue) {

        this.initFilterArchValue = initFilterArchValue;
        this.archFieldVisible = archFieldVisible;
        this.selectFieldVisible = selectFieldVisible;

//        Grid<Zak> zakGrid = new Grid<>();
        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setSelectionMode(Grid.SelectionMode.SINGLE);

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



//        this.addColumn(TemplateRenderer.of("[[index]]"))
//                .setHeader("Řádek")
//                .setFlexGrow(0)
//        ;

        this.addColumn(archRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
                .setVisible(this.archFieldVisible);
        //                .setFrozen(true)
        ;
        this.addColumn(ZakBasic::getKzCislo)
                .setHeader("ČK/ČZ")
                .setFlexGrow(0)
                .setWidth("9em")
                .setSortable(true)
                .setKey(KZCISLO_COL_KEY)
        ;
        this.addColumn(ZakBasic::getRok)
                .setHeader("Rok")
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
        this.addColumn(checkBoxRenderer)
                .setHeader(("Výběr"))
                .setFlexGrow(0)
                .setWidth("4.5em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setKey(SEL_COL_KEY)
                .setVisible(this.selectFieldVisible);
        ;
        this.addColumn(ZakBasic::getKzText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setWidth("25em")
                .setSortable(true)
                .setKey(KZTEXT_COL_KEY)
        ;


        HeaderRow filterRow = this.appendHeaderRow();

        archFilterField = buildSelectionFilterField();
//        archFilterField.setItemLabelGenerator(this::archFilterLabelGenerator);
        archFilterField.setTextRenderer(this::archFilterLabelGenerator);
        filterRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);

        kzCisloFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(KZCISLO_COL_KEY))
                .setComponent(kzCisloFilterField);

        rokFilterField = buildSelectionFilterField();
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);

        skupinaFilterField = buildSelectionFilterField();
        filterRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField)
        ;

        kzTextFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(KZTEXT_COL_KEY))
                .setComponent(kzTextFilterField)
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

    private <T> Select buildSelectionFilterField() {
        Select <T> selectFilterField = new Select<>();
//        selectFilterField.setLabel(null);
//        selectFilterField.setClearButtonVisible(true);
        selectFilterField.setSizeFull();
//        selectFilterField.setPlaceholder("Vše");
        selectFilterField.setEmptySelectionCaption("Vše");
        selectFilterField.setEmptySelectionAllowed(true);
//        selectFilterField.setItemLabelGenerator(this::getPersonLabel);
//        selectFilterField.setValueChangeMode(ValueChangeMode.EAGER);
//        List<String> skupinaList = Arrays.asList("1", "2");
//        selectFilterField.setItems(skupinaList);
        selectFilterField.addValueChangeListener(event -> doFilter());
        return selectFilterField;
    }

    private String archFilterLabelGenerator(Boolean arch) {
        if  (null == arch) {
            return "Vše";
        } else {
            return arch ? "Ano" : "Ne";
        }
    }

    public void initFilters() {
        if (null == initFilterArchValue) {
            archFilterField.clear();
        } else {
            archFilterField.setValue(this.initFilterArchValue);
        }
        rokFilterField.clear();
        skupinaFilterField.clear();
        kzCisloFilterField.clear();
        kzTextFilterField.clear();
        objednatelFilterField.clear();
        ((ListDataProvider<ZakBasic>) this.getDataProvider()).clearFilters();
    }

    public void doFilter() {
        Boolean archFilterValue = archFilterField.getValue();
        Integer rokFilterValue = rokFilterField.getValue();
        String kzCisloFilterValue = kzCisloFilterField.getValue();
        String skupinaFilterValue = skupinaFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
        String kzTextFilterValue = kzTextFilterField.getValue();

        ListDataProvider<ZakBasic> listDataProvider = ((ListDataProvider<ZakBasic>) this.getDataProvider());
        listDataProvider.clearFilters();

        if (null != archFilterValue) {
            listDataProvider.addFilter(ZakBasic::getArch
                    , arch -> arch.equals(archFilterValue)
            );
        }
        if (null != rokFilterValue) {
            listDataProvider.addFilter(ZakBasic::getRok
                    , rok -> rok.equals(rokFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(kzCisloFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getKzCislo
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
        if (StringUtils.isNotEmpty(kzTextFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getKzText
                    , kzt -> StringUtils.containsIgnoreCase(kzt, kzTextFilterValue)
            );
        }
    }

    private ComponentRenderer<Component, ZakBasic> archRenderer = new ComponentRenderer<>(zakb -> {
        ArchIconBox archBox = new ArchIconBox();
        archBox.showIcon(zakb.getTyp(), zakb.getArch());
        return archBox;
    });

    private ComponentRenderer<Component, ZakBasic> checkBoxRenderer = new ComponentRenderer<>(zakb -> {
        Checkbox zakSelectBox = new Checkbox();
        zakSelectBox.addValueChangeListener(event -> {
            zakb.setChecked(event.getValue());
        });
        if ((ItemType.ZAK == zakb.getTyp()) || (ItemType.REZ == zakb.getTyp()) || (ItemType.LEK == zakb.getTyp())) {
            zakSelectBox.addValueChangeListener(event -> {
                zakb.setChecked(event.getValue());
            });
            return zakSelectBox;
        } else {
            return new Span("");
        }
    });

    public void setArchFilterItems(final List<Boolean> archItems) {
//        archItems.add(0, null);
        archFilterField.setItems(archItems);
    }

    public void setRokFilterItems(final List<Integer> rokItems) {
//        rokItems.add(0, null);
        rokFilterField.setItems(rokItems);
    }

    public void setSkupinaFilterItems(final List<String> skupinaItems) {
//        skupinaItems.add(0, null);
        skupinaFilterField.setItems(skupinaItems);
    }

}

