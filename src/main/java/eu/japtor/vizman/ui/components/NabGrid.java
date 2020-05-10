package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import eu.japtor.vizman.backend.dataprovider.spring.FilterablePageableDataProvider;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.service.NabService;


import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NabGrid extends Grid<Nab> {

    // Nab grid field keys:
    public static final String ROK_COL_KEY = "nab-bg-rok";
    public static final String CNAB_COL_KEY = "nab-bg-cnab";
    public static final String TEXT_COL_KEY = "nab-bg-text";
    public static final String POZNAMKA_COL_KEY = "nab-bg-poznamka";
    public static final String VZ_COL_KEY = "nab-bg-vz";
    public static final String OBJEDNATEL_COL_KEY = "nab-bg-objednatel";
    public static final String CKONT_COL_KEY = "nab-bg-ckont";


//    private ConfigurableFilterDataProvider<Nab, Void, NabService.NabFilter> gridDataProvider;
    private FilterablePageableDataProvider<Nab, NabService.NabFilter> gridDataProvider;

    private Select<Boolean> vzFilterField;
    private Select<Integer> rokFilterField;
    private TextField cnabFilterField;
    private TextField ckontFilterField;
    private TextField textFilterField;
    private TextField objednatelFilterField;
    private TextField poznamkaFilterField;

    private boolean vzFieldVisible;
    private boolean selectFieldVisible;
    private Consumer<Integer> selectionChanger;
    private Function<Nab, Boolean> checkBoxEnabler;

    private int selCount;

    HeaderRow filterRow;
    BiConsumer<Nab, Operation> editDialogOpener;

    public NabGrid(
            boolean selectFieldVisible
            , Function<Nab, Boolean> checkBoxEnabler
            , Consumer<Integer> selectionChanger
            , boolean vzFieldVisible
            , BiConsumer<Nab, Operation> editDialogOpener
    ) {
        this.checkBoxEnabler = checkBoxEnabler;
        this.vzFieldVisible = vzFieldVisible;
        this.selectFieldVisible = selectFieldVisible;
        this.selectionChanger = selectionChanger;
        this.editDialogOpener = editDialogOpener;

        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setMultiSort(false);
        this.setSelectionMode(SelectionMode.SINGLE);
        this.setId("nab-grid");  // .. same ID as is used in shared-styles grid's dom module

        this.addColumn(new ComponentRenderer<>(this::buildEditBtn))
                .setHeader("Edit")
                .setFlexGrow(0)
        ;

        this.addColumn(vzRenderer)
                .setHeader(("VZ"))
                .setFlexGrow(0)
                .setWidth("6em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setResizable(true)
                .setKey(VZ_COL_KEY)
                .setVisible(this.vzFieldVisible);
        //                .setFrozen(true)
        ;
        this.addColumn(Nab::getRok)
                .setHeader("Rok nab.")
                .setFlexGrow(0)
                .setWidth("7em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(ROK_COL_KEY)
                .setSortProperty("rok")
        ;
        this.addColumn(Nab::getCnab)
                .setHeader("Č. nab.")
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(CNAB_COL_KEY)
                .setSortProperty("cnab")
        ;
        this.addColumn(Nab::getCkont)
                .setHeader("Č. kont.")
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.START)
                .setSortable(true)
                .setKey(CKONT_COL_KEY)
                .setSortProperty("ckont")
        ;
        this.addColumn(Nab::getText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setWidth("13em")
                .setSortable(true)
                .setKey(TEXT_COL_KEY)
                .setSortProperty("text")
        ;
        this.addColumn(Nab::getObjednatel)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setSortable(true)
                .setKey(OBJEDNATEL_COL_KEY)
                .setSortProperty("objednatel")
        ;
        this.addColumn(Nab::getPoznamka)
                .setHeader("Poznámka")
                .setFlexGrow(1)
                .setWidth("13em")
                .setSortable(true)
                .setKey(POZNAMKA_COL_KEY)
                .setSortProperty("poznamka")
        ;


        filterRow = this.appendHeaderRow();

        rokFilterField = new SelectorFilterField<>((event -> doFilter()));
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);

        cnabFilterField = new FilterTextField(event -> doFilter());
        filterRow.getCell(this.getColumnByKey(CNAB_COL_KEY))
                .setComponent(cnabFilterField)
        ;

        ckontFilterField = new FilterTextField(event -> doFilter());
        filterRow.getCell(this.getColumnByKey(CKONT_COL_KEY))
                .setComponent(ckontFilterField)
        ;

        textFilterField = new FilterTextField(event -> doFilter());
        filterRow.getCell(this.getColumnByKey(TEXT_COL_KEY))
                .setComponent(textFilterField)
        ;

        objednatelFilterField = new FilterTextField(event -> doFilter());
        filterRow.getCell(this.getColumnByKey(OBJEDNATEL_COL_KEY))
                .setComponent(objednatelFilterField)
        ;

        poznamkaFilterField = new FilterTextField(event -> doFilter());
        filterRow.getCell(this.getColumnByKey(POZNAMKA_COL_KEY))
                .setComponent(poznamkaFilterField)
        ;

        for (Column col : getColumns()) {
            setResizable(col);
        }
    }

    private Button buildEditBtn(Nab nab) {
        Button editBtn = new GridItemEditBtn(event -> editDialogOpener.accept(nab, Operation.EDIT));
        return editBtn;
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

    public void setInitialFilterValues() {
//        ((ListDataProvider<Nab>) this.getDataProvider()).clearFilters();
//        ((onfigurableFilterDataProvider<Nab, Void, NabService.NabFilter>) this.getDataProvider()).clearFilters();
//        gridDataProvider.setFilter(NabService.NabFilter.getEmpty());
        rokFilterField.clear();
        cnabFilterField.clear();
        ckontFilterField.clear();
        vzFilterField.clear();
        textFilterField.clear();
        poznamkaFilterField.clear();
        objednatelFilterField.clear();
    }

    public void doFilter() {
        doFilter(buildNabFilter());
    }

    public void doFilter(NabService.NabFilter filter) {
//        ListDataProvider<Nab> listDataProvider = ((ListDataProvider<Nab>) this.getDataProvider());

//        ((ConfigurableFilterDataProvider) this.getDataProvider()).setFilter(buildNabFilter());
        gridDataProvider.setFilter(filter);

        gridDataProvider.refreshAll();
        getDataProvider().refreshAll();

//        ConfigurableFilterDataProvider listDataProvider = ((ConfigurableFilterDataProvider) this.getDataProvider());
////        listDataProvider.clearFilters();
//        listDataProvider.clearFilters();
//
//        Integer rokFilterValue = rokFilterField.getValue();
//        Boolean vzFilterValue = vzFilterField.getValue();
//        String cnabFilterValue = cnabFilterField.getValue();
//        String ckontFilterValue = ckontFilterField.getValue();
//        String textFilterValue = textFilterField.getValue();
//        String objednatelFilterValue = objednatelFilterField.getValue();
//        String poznamkaFilterValue = poznamkaFilterField.getValue();
//
//        if (null != vzFilterValue) {
//            listDataProvider.addFilter(Nab::getVz
//                    , vz -> vz.equals(vzFilterValue)
//            );
//        }
//        if (null != rokFilterValue) {
//            listDataProvider.addFilter(Nab::getRok
//                    , rok -> rok.equals(rokFilterValue)
//            );
//        }
//        if (StringUtils.isNotEmpty(cnabFilterValue)) {
//            listDataProvider.addFilter(Nab::getCnab
//                    , cn -> StringUtils.containsIgnoreCase(cn, cnabFilterValue)
//            );
//        }
//        if (StringUtils.isNotEmpty(ckontFilterValue)) {
//            listDataProvider.addFilter(Nab::getCkont
//                    , ck -> StringUtils.containsIgnoreCase(ck, ckontFilterValue)
//            );
//        }
//        if (StringUtils.isNotEmpty(textFilterValue)) {
//            listDataProvider.addFilter(Nab::getText
//                    , tx -> StringUtils.containsIgnoreCase(tx, textFilterValue)
//            );
//        }
//        if (StringUtils.isNotEmpty(objednatelFilterValue)) {
//            listDataProvider.addFilter(Nab::getObjednatel
//                    , ob -> StringUtils.containsIgnoreCase(ob, objednatelFilterValue)
//            );
//        }
//        if (StringUtils.isNotEmpty(poznamkaFilterValue)) {
//            listDataProvider.addFilter(Nab::getPoznamka
//                    , po -> StringUtils.containsIgnoreCase(po, poznamkaFilterValue)
//            );
//        }
    }

    // Needed for getting items for report by current grid filter until Vaadin does not  have it implemented
    public NabService.NabFilter buildNabFilter() {
        return new NabService.NabFilter(
                getRokFilterValue()
                , getCnabFilterValue()
                , getCkontFilterValue()
                , getVzFilterValue()
                , getTextFilterValue()
                , getObjednatelFilterValue()
                , getPoznamkaFilterValue()
        );
    }

    private ComponentRenderer<Component, Nab> vzRenderer = new ComponentRenderer<>(nab -> {
        VzIconBox vzBox = new VzIconBox();
        vzBox.setAlignItems(FlexComponent.Alignment.CENTER);
        vzBox.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        vzBox.showIcon(nab.getVz() ? VzIconBox.VzState.PUBLIC : VzIconBox.VzState.NOTPUBLIC);
        return vzBox;
    });

    public void setRokFilterItems(final List<Integer> rokItems) {
        rokFilterField = new SelectorFilterField<>((event -> doFilter()));
        filterRow.getCell(this.getColumnByKey(ROK_COL_KEY))
                .setComponent(rokFilterField);
        rokFilterField.setItems(rokItems);
    }

    public void setVzFilterItems(final List<Boolean> vzItems) {
        vzFilterField = new SelectorFilterField<>((event -> doFilter()));
        filterRow.getCell(this.getColumnByKey(VZ_COL_KEY))
                .setComponent(vzFilterField);
        vzFilterField.setItems(vzItems);
    }


    public Integer getRokFilterValue() {
        return rokFilterField.getValue();
    }

    public String getCnabFilterValue() {
        return cnabFilterField.getValue();
    }

    public String getCkontFilterValue() {
        return ckontFilterField.getValue();
    }

    public Boolean getVzFilterValue() {
        return vzFilterField.getValue();
    }

    public String getTextFilterValue() {
        return textFilterField.getValue();
    }

    public String getPoznamkaFilterValue() {
        return poznamkaFilterField.getValue();
    }

    public String getObjednatelFilterValue() {
        return objednatelFilterField.getValue();
    }

//    public void setGridDataProvider(ConfigurableFilterDataProvider gridDataProvider) {
    public void setGridDataProvider(FilterablePageableDataProvider gridDataProvider) {
        this.gridDataProvider = gridDataProvider;
        setDataProvider(this.gridDataProvider);
    }
}
