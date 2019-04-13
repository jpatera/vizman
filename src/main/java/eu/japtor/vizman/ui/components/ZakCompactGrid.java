package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zak;

public class ZakCompactGrid extends Grid<Zak> {

    // Zak Compact Grid field keys:
    private static final String KZCISLO_COL_KEY = "zak-cg-kzcislo";
    private static final String ROK_COL_KEY = "zak-cg-rok";
    private static final String SKUPINA_COL_KEY = "zak-cg-skupina";
    private static final String OBJEDNATEL_COL_KEY = "zak-cg-objednatel";
    private static final String KZTEXT_COL_KEY = "zak-cg-kztext";

//    Grid<Zak> zakGrid;

    public ZakCompactGrid() {

//        Grid<Zak> zakGrid = new Grid<>();
        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setSelectionMode(Grid.SelectionMode.NONE);

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
        this.addColumn(Zak::getKzCislo)
                .setHeader("ČK/ČZ")
                .setFlexGrow(0)
                .setWidth("9em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(KZCISLO_COL_KEY)
        ;
        this.addColumn(Zak::getRok)
                .setHeader("Rok")
                .setFlexGrow(0)
                .setWidth("8em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(ROK_COL_KEY)
//                .setFrozen(true)
//                .setId("arch-column")
        ;
        this.addColumn(Zak::getSkupina).setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("4em")
                .setResizable(true)
//                .setFrozen(true)
                .setKey(SKUPINA_COL_KEY)
        ;
//        zakGrid.addColumn(checkBoxRenderer)
//                .setHeader(("Přidat"))
//                .setFlexGrow(0)
//                .setWidth("5em")
//                .setResizable(true)
//                .setKey(SEL_COL_KEY)
//        ;
        this.addColumn(Zak::getKzText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setKey(KZTEXT_COL_KEY)
                .setResizable(true)
        ;
        this.addColumn(Zak::getKlientName)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setKey(OBJEDNATEL_COL_KEY)
                .setResizable(true)
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
        this.getColumnByKey(KZCISLO_COL_KEY).setSortable(true);
        this.getColumnByKey(ROK_COL_KEY).setSortable(true);
        this.getColumnByKey(SKUPINA_COL_KEY).setSortable(true);
        this.getColumnByKey(KZTEXT_COL_KEY).setSortable(true);
        this.getColumnByKey(OBJEDNATEL_COL_KEY).setSortable(true);

        HeaderRow filterRow = this.appendHeaderRow();
        filterRow.getCell(this.getColumnByKey(KZTEXT_COL_KEY))
                .setComponent(initKzTextFilterField());
        filterRow.getCell(this.getColumnByKey(OBJEDNATEL_COL_KEY))
                .setComponent(initObjednatelFilterFilter());

    }



    private Component initObjednatelFilterFilter() {
        TextField objednatelFilterField = new TextField();
//        ValueProvider<KzTreeAware, String> kzObjednatelValueProvider
//                        = KzTreeAware::getKlient;
        objednatelFilterField.addValueChangeListener(event -> {
        });
//        objednatelFilterField.addValueChangeListener(event ->
//                        ((TreeDataProvider<KzTreeAware>)kzTreeGrid.getDataProvider())
////                            .addFilter(KzTreeAware::getObjednatel, t ->
////                                    StringUtils.containsIgnoreCase(t, objednatelFilterField.getValue())
////                            )
//                            .addFilter(kz -> ItemType.KONT != kz.getTyp() || StringUtils.containsIgnoreCase(
//                                kz.getObjednatel(), objednatelFilterField.getValue())
//                            )
//        );
        objednatelFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        objednatelFilterField.setSizeFull();
        objednatelFilterField.setPlaceholder("Filtr");

        return objednatelFilterField;
    }

    private Component initKzTextFilterField() {
        TextField kzTextFilterField = new TextField();
        ValueProvider<Zak, String> kzTextValueProvider = Zak::getText;
        kzTextFilterField.addValueChangeListener(event -> {
        });
//        textFilterField.addValueChangeListener(event ->
//                        ((TreeDataProvider<KzTreeAware>)kzTreeGrid.getDataProvider())
////                            .addFilter(KzTreeAware::getText, t ->
////                                    StringUtils.containsIgnoreCase(t, textFilterField.getValue())
////                            )
//                                .addFilter(kz -> ItemType.KONT != kz.getTyp() || StringUtils.containsIgnoreCase(
//                                        kz.getText(), textFilterField.getValue())
//                                )
//        );
        kzTextFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        kzTextFilterField.setSizeFull();
        kzTextFilterField.setPlaceholder("Filtr");

        return kzTextFilterField;
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

