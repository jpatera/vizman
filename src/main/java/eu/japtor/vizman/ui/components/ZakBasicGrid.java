package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.ZakBasic;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ZakBasicGrid extends Grid<ZakBasic> {

    // Zak Compact Grid field keys:
    public static final String KZCISLO_COL_KEY = "zak-cg-kzcislo";
    public static final String ROK_COL_KEY = "zak-cg-rok";
    public static final String SKUPINA_COL_KEY = "zak-cg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zak-cg-objednatel";
    public static final String KZTEXT_COL_KEY = "zak-cg-kztext";

//    Grid<Zak> zakGrid;

    TextField kzCisloFilterField;
    ComboBox skupinaFilterField;
    TextField objednatelFilterField;
    TextField kzTextFilterField;

    public ZakBasicGrid() {

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
//        this.addColumn(checkBoxRenderer)
//                .setHeader(("Přidat"))
//                .setFlexGrow(0)
//                .setWidth("5em")
//                .setResizable(true)
//                .setKey(SEL_COL_KEY)
//        ;
        this.addColumn(ZakBasic::getKzCislo)
                .setHeader("ČK/ČZ")
                .setFlexGrow(0)
                .setWidth("9em")
                .setSortable(true)
                .setResizable(true)
                .setFrozen(true)
                .setKey(KZCISLO_COL_KEY)
        ;
        this.addColumn(ZakBasic::getRok)
                .setHeader("Rok")
                .setFlexGrow(0)
                .setWidth("6em")
                .setSortable(true)
                .setResizable(true)
                .setKey(ROK_COL_KEY)
        ;
        this.addColumn(ZakBasic::getSkupina).setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setResizable(true)
                .setKey(SKUPINA_COL_KEY)
        ;
        this.addColumn(ZakBasic::getObjednatel)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setSortable(true)
                .setResizable(true)
                .setKey(OBJEDNATEL_COL_KEY)
        ;
        this.addColumn(ZakBasic::getKzText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setWidth("20em")
                .setSortable(true)
                .setResizable(true)
                .setKey(KZTEXT_COL_KEY)
        ;

//        HeaderRow filterRow = this.appendHeaderRow();
//        TextField textFilterField = new TextField();
//        ValueProvider<KzTreeAware, String> kzTextValueProvider
//                = KzTreeAware::getText;
//        textFilterField.addValueChangeListener(event -> {});
//        textFilterField.setValueChangeMode(ValueChangeMode.EAGER);
//        filterRow.getCell(this.getColumnByKey(TEXT_COL_KEY)).setComponent(textFilterField);
//        textFilterField.setSizeFull();
//        textFilterField.setPlaceholder("Filtr (rozbitý)");


//        for (Grid.Column col : this.getColumns()) {
//            setResizable(col);
//        }

//        this.getColumnByKey(KZCISLO_COL_KEY).setSortable(true);
//        this.getColumnByKey(ROK_COL_KEY).setSortable(true);
//        this.getColumnByKey(SKUPINA_COL_KEY).setSortable(true);
//        this.getColumnByKey(KZTEXT_COL_KEY).setSortable(true);
//        this.getColumnByKey(OBJEDNATEL_COL_KEY).setSortable(true);

        HeaderRow filterRow = this.appendHeaderRow();

        kzCisloFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(KZCISLO_COL_KEY))
                .setComponent(kzCisloFilterField);

        skupinaFilterField = buildSelectFilterField();
        filterRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField);

        kzTextFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(KZTEXT_COL_KEY))
                .setComponent(kzTextFilterField);

        objednatelFilterField = buildTextFilterField();
        filterRow.getCell(this.getColumnByKey(OBJEDNATEL_COL_KEY))
                .setComponent(objednatelFilterField);
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

    private ComboBox buildSelectFilterField() {
        ComboBox selectFilterField = new ComboBox();
        selectFilterField.setLabel(null);
//        selectFilterField.setClearButtonVisible(true);
        selectFilterField.setSizeFull();
        selectFilterField.setPlaceholder("Filtr");
//        selectFilterField.setItemLabelGenerator(this::getPersonLabel);
//        selectFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        List<String> skupinaList = Arrays.asList("1", "2");
        selectFilterField.setItems(skupinaList);
        selectFilterField.addValueChangeListener(event -> doFilter());
        return selectFilterField;
    }

    public void initFilters() {
        kzCisloFilterField.clear();
        kzTextFilterField.clear();
        objednatelFilterField.clear();
        ((ListDataProvider<ZakBasic>) this.getDataProvider()).clearFilters();
    }

    private void doFilter() {
        String kzCisloFilterValue = kzCisloFilterField.getValue();
        String skupinaFilterValue = (String)skupinaFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
        String kzTextFilterValue = kzTextFilterField.getValue();

        ListDataProvider<ZakBasic> listDataProvider = ((ListDataProvider<ZakBasic>) this.getDataProvider());
        listDataProvider.clearFilters();

        if (StringUtils.isNotEmpty(kzCisloFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getKzCislo
                    , kzc -> StringUtils.containsIgnoreCase(kzc, kzCisloFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(skupinaFilterValue)) {
            listDataProvider.addFilter(ZakBasic::getSkupina
                    , kzc -> StringUtils.containsIgnoreCase(kzc, skupinaFilterValue)
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


    // =========  Filter row  ===========



    private ComponentRenderer<Component, Zak> checkBoxRenderer = new ComponentRenderer<>(zak -> {
        Checkbox zakSelectBox = new Checkbox();
        zakSelectBox.addValueChangeListener(event -> {
            zak.setChecked(event.getValue());
        });
        if ((ItemType.ZAK == zak.getTyp()) || (ItemType.REZ == zak.getTyp()) || (ItemType.LEK == zak.getTyp())) {
            zakSelectBox.addValueChangeListener(event -> {
                zak.setChecked(event.getValue());
            });
            return zakSelectBox;
        } else {
            return new Span("");
        }
    });
}

