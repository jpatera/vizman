package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;
import eu.japtor.vizman.backend.entity.ArchIconBox;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ZakRozpracGrid extends Grid<Zakr> {

    private static final String RX_REGEX = "[0-9]{1,3}";

    // Zak Compact Grid field keys:
    public static final String KZCISLO_COL_KEY = "zakr-bg-kzcislo";
    public static final String ROK_COL_KEY = "zakr-bg-rok";
    public static final String SKUPINA_COL_KEY = "zakr-bg-skupina";
    public static final String OBJEDNATEL_COL_KEY = "zakr-bg-objednatel";
    public static final String KZTEXT_COL_KEY = "zakr-bg-kztext";
    public static final String SEL_COL_KEY = "zakr-bg-select";
    public static final String ARCH_COL_KEY = "zakr-bg-arch";
    public static final String R0_COL_KEY = "zakr-bg-r0";

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

    HeaderRow filterRow;
    Registration zakrGridEditRegistration = null;

    public ZakRozpracGrid(boolean selectFieldVisible, boolean archFieldVisible, Boolean initFilterArchValue) {

        this.initFilterArchValue = initFilterArchValue;
        this.archFieldVisible = archFieldVisible;
        this.selectFieldVisible = selectFieldVisible;

//        Grid<Zak> zakGrid = new Grid<>();
        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setClassName("vizman-simple-grid");
        this.addThemeNames("column-borders", "row-stripes");
        this.setSelectionMode(SelectionMode.SINGLE);

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

        // Grid editor:
        Binder<Zakr> zakrBinder = new Binder<>(Zakr.class);
        Editor<Zakr> zakrEditor = this.getEditor();
        zakrEditor.setBinder(zakrBinder);
        zakrGridEditRegistration = this.addItemDoubleClickListener(event -> {
            // TODO keyPress listeners...
            zakrEditor.editItem(event.getItem());
        });
        zakrBinder.addStatusChangeListener(event -> {
            event.getBinder().hasChanges();
        });


        // Columns:
        this.addColumn(archRenderer)
                .setHeader(("Arch"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setResizable(true)
                .setKey(ARCH_COL_KEY)
                .setVisible(this.archFieldVisible);
        //                .setFrozen(true)
        ;
        this.addColumn(Zakr::getKzCislo)
                .setHeader("ČK/ČZ")
                .setFlexGrow(0)
                .setWidth("9em")
                .setSortable(true)
                .setKey(KZCISLO_COL_KEY)
        ;
        this.addColumn(Zakr::getRok)
                .setHeader("Rok")
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(ROK_COL_KEY)
        ;
        this.addColumn(Zakr::getSkupina)
                .setHeader("Sk.")
                .setFlexGrow(0)
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(SKUPINA_COL_KEY)
        ;
        this.addColumn(Zakr::getObjednatel)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setSortable(true)
                .setKey(OBJEDNATEL_COL_KEY)
        ;
        this.addColumn(selectFieldRenderer)
                .setHeader(("Výběr"))
                .setFlexGrow(0)
                .setWidth("4.5em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setKey(SEL_COL_KEY)
                .setVisible(this.selectFieldVisible);
        ;


        Grid.Column<Zakr> colR0 = this.addColumn(Zakr::getR0)
//                new ComponentRenderer<>(pruhZak ->
//                        VzmFormatUtils.getDecHodComponent(pzHodValProv.apply(pruhZak)))

                .setHeader("R0")
                .setFlexGrow(0)
                .setWidth("5em")
                .setSortable(true)
                .setKey(R0_COL_KEY)
                .setResizable(false)
                ;
        colR0.setEditorComponent(buildRxEditor(zakrBinder, zakrEditor));

        this.addColumn(Zakr::getKzText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setWidth("25em")
                .setSortable(true)
                .setKey(KZTEXT_COL_KEY)
        ;


//        Binder<Zakr> r0Binder = new Binder<>(Zakr.class);
//        Editor<Zakr> r0Editor = this.getEditor();

//        TextField editComp = new TextField();
//        editComp.addValueChangeListener(event -> {
//            if (event.isFromClient() && (StringUtils.isNotBlank(event.getValue()) || StringUtils.isNotBlank(event.getOldValue()))
//                    && !event.getValue().equals(event.getOldValue())) {
//                try {
//                    // TODO: try to use localization instead of regex ?
////                    pzEditor.getItem().setValueToDayField(day
//                    zakrEditor.getItem().setR0(
//                            StringUtils.isBlank(event.getValue()) ?
//                                    null : new BigDecimal(event.getValue()));
////                                    null : event.getValue().replaceAll(",", "."));
////                                    null : new BigDecimal(event.getValue().replaceAll(",", ".")));
//                    zakrBinder.writeBean(zakrEditor.getItem());
////                    missingHodsFooterRow.getCell(col)
////                            .setText(getMissingHodString(day));
//
//                    // TODO: disable when pruh is loaded, enable when changed (either hodPrac changed, or zak added/deleted)
////                    saveEditButton.setEnabled(pzBinder.hasChanges());
//
//                } catch (ValidationException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        // TODO: remove margins
//        editComp.getStyle()
//                .set("margin", "0")
//                .set("padding", "0")
////                .set("width", HOD_COL_WIDTH)
//                .set("width", "3.5em")
//                .set("font-size", "var(--lumo-font-size-s)")
//                .set("height", "1.8m")
//                .set("min-height", "1.8em")
//                .set("--lumo-text-field-size", "var(--lumo-size-s)")
//        ;
//        editComp.setPattern(RX_REGEX);
//        editComp.setPreventInvalidInput(true);
//        zakrBinder.forField(editComp)
//                .withNullRepresentation("")
//                .withConverter(VzmFormatUtils.VALIDATED_DEC_HOD_TO_STRING_CONVERTER)
////                .bind(r0ValProv, r0Setter);
//                .bind(Zakr::getR0, Zakr::setR0);
//        col.setEditorComponent(editComp);
//        zakrBinder.addStatusChangeListener(event -> {
//            event.getBinder().hasChanges();
//        });



        filterRow = this.appendHeaderRow();

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

        for (Column colXx : getColumns()) {
            setResizable(colXx);
        }
    }


    private Component buildRxEditor(
            Binder<Zakr> zakrBinder
            , Editor<Zakr> zakrEditor
    ) {
        TextField editComp = new TextField();
        editComp.addValueChangeListener(event -> {
            if (event.isFromClient() && (StringUtils.isNotBlank(event.getValue()) || StringUtils.isNotBlank(event.getOldValue()))
                    && !event.getValue().equals(event.getOldValue())) {
                try {
                    // TODO: try to use localization instead of regex ?
//                    pzEditor.getItem().setValueToDayField(day
                    zakrEditor.getItem().setR0(
                            StringUtils.isBlank(event.getValue()) ?
                                    null : new BigDecimal(event.getValue()));
//                                    null : event.getValue().replaceAll(",", "."));
//                                    null : new BigDecimal(event.getValue().replaceAll(",", ".")));
                    zakrBinder.writeBean(zakrEditor.getItem());
//                    missingHodsFooterRow.getCell(col)
//                            .setText(getMissingHodString(day));

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
//                .set("width", HOD_COL_WIDTH)
                .set("width", "3.5em")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("height", "1.8m")
                .set("min-height", "1.8em")
                .set("--lumo-text-field-size", "var(--lumo-size-s)")
        ;
        editComp.setPattern(RX_REGEX);
        editComp.setPreventInvalidInput(true);
        zakrBinder.forField(editComp)
                .withNullRepresentation("")
                .withConverter(VzmFormatUtils.VALIDATED_DEC_HOD_TO_STRING_CONVERTER)
//                .bind(r0ValProv, r0Setter);
                .bind(Zakr::getR0, Zakr::setR0);

        return editComp;

//        col.setEditorComponent(editComp);
//        zakrBinder.addStatusChangeListener(event -> {
//            event.getBinder().hasChanges();
//        });
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

    public void populateGridDataAndRebuildFilterFields(List<Zakr> zakBasicList) {
        this.setItems(zakBasicList);
        this.setRokFilterItems(zakBasicList.stream()
                .filter(z -> null != z.getRok())
                .map(Zakr::getRok)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        this.setSkupinaFilterItems(zakBasicList.stream()
                .map(Zakr::getSkupina)
                .filter(s -> null != s)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
        this.setArchFilterItems(zakBasicList.stream()
                .map(Zakr::getArch)
                .filter(a -> null != a)
                .distinct().collect(Collectors.toCollection(LinkedList::new))
        );
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
        archFilterField = buildSelectionFilterField();
        filterRow.getCell(this.getColumnByKey(ARCH_COL_KEY))
                .setComponent(archFilterField);
        archFilterField.setItems(archItems);
    }

    public void setRokFilterItems(final List<Integer> rokItems) {
        rokFilterField = buildSelectionFilterField();
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);
        rokFilterField.setItems(rokItems);
    }

    public void setSkupinaFilterItems(final List<String> skupinaItems) {
        skupinaFilterField = buildSelectionFilterField();
        filterRow.getCell(this.getColumnByKey(SKUPINA_COL_KEY))
                .setComponent(skupinaFilterField);
        skupinaFilterField.setItems(skupinaItems);
    }
}

