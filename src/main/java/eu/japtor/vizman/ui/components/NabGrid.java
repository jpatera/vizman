package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import eu.japtor.vizman.backend.entity.Nab;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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

    Select<Boolean> vzFilterField;
    Select<Integer> rokFilterField;
    TextField cnabFilterField;
    TextField ckontFilterField;
    TextField textFilterField;
    TextField objednatelFilterField;
    TextField poznamkaFilterField;

    private boolean vzFieldVisible;
    private boolean selectFieldVisible;
    private Consumer<Integer> selectionChanger;
    private Function<Nab, Boolean> checkBoxEnabler;

    private int selCount;

    HeaderRow filterRow;

    public NabGrid(
            boolean selectFieldVisible
            , Function<Nab, Boolean> checkBoxEnabler
            , Consumer<Integer> selectionChanger
            , boolean vzFieldVisible
    ) {
        this.checkBoxEnabler = checkBoxEnabler;
        this.vzFieldVisible = vzFieldVisible;
        this.selectFieldVisible = selectFieldVisible;
        this.selectionChanger = selectionChanger;

        this.getStyle().set("marginTop", "0.5em");
        this.setColumnReorderingAllowed(true);
        this.setMultiSort(false);
        this.setSelectionMode(SelectionMode.SINGLE);
        this.setId("nab-grid");  // .. same ID as is used in shared-styles grid's dom module


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
        ;
        this.addColumn(Nab::getCnab)
                .setHeader("Č. nab.")
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setKey(CNAB_COL_KEY)
        ;
        this.addColumn(Nab::getCkont)
                .setHeader("Č. kont.")
                .setFlexGrow(0)
                .setWidth("8em")
                .setTextAlign(ColumnTextAlign.START)
                .setSortable(true)
                .setKey(CKONT_COL_KEY)
        ;
        this.addColumn(Nab::getText)
                .setHeader("Text")
                .setFlexGrow(1)
                .setWidth("13em")
                .setSortable(true)
                .setKey(TEXT_COL_KEY)
        ;
        this.addColumn(Nab::getObjednatel)
                .setHeader("Objednatel")
                .setFlexGrow(0)
                .setWidth("18em")
                .setSortable(true)
                .setKey(OBJEDNATEL_COL_KEY)
        ;
        this.addColumn(Nab::getPoznamka)
                .setHeader("Poznámka")
                .setFlexGrow(1)
                .setWidth("13em")
                .setSortable(true)
                .setKey(POZNAMKA_COL_KEY)
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
        ((ListDataProvider<Nab>) this.getDataProvider()).clearFilters();
        rokFilterField.clear();
        textFilterField.clear();
    }

    public void doFilter() {
        ListDataProvider<Nab> listDataProvider = ((ListDataProvider<Nab>) this.getDataProvider());
        listDataProvider.clearFilters();

        Integer rokFilterValue = rokFilterField.getValue();
        Boolean vzFilterValue = vzFilterField.getValue();
        String cnabFilterValue = cnabFilterField.getValue();
        String ckontFilterValue = ckontFilterField.getValue();
        String textFilterValue = textFilterField.getValue();
        String objednatelFilterValue = objednatelFilterField.getValue();
        String poznamkaFilterValue = poznamkaFilterField.getValue();

        if (null != vzFilterValue) {
            listDataProvider.addFilter(Nab::getVz
                    , vz -> vz.equals(vzFilterValue)
            );
        }
        if (null != rokFilterValue) {
            listDataProvider.addFilter(Nab::getRok
                    , rok -> rok.equals(rokFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(cnabFilterValue)) {
            listDataProvider.addFilter(Nab::getCnab
                    , cn -> StringUtils.containsIgnoreCase(cn, cnabFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(ckontFilterValue)) {
            listDataProvider.addFilter(Nab::getCkont
                    , ck -> StringUtils.containsIgnoreCase(ck, ckontFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(textFilterValue)) {
            listDataProvider.addFilter(Nab::getText
                    , tx -> StringUtils.containsIgnoreCase(tx, textFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(objednatelFilterValue)) {
            listDataProvider.addFilter(Nab::getObjednatel
                    , ob -> StringUtils.containsIgnoreCase(ob, objednatelFilterValue)
            );
        }
        if (StringUtils.isNotEmpty(poznamkaFilterValue)) {
            listDataProvider.addFilter(Nab::getPoznamka
                    , po -> StringUtils.containsIgnoreCase(po, poznamkaFilterValue)
            );
        }
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





    public int getSelCount() {
        return selCount;
    }

    public void setSelCount(int selCount) {
        this.selCount = selCount;
    }

    public Integer getRokFilterValue() {
        return rokFilterField.getValue();
    }

    public String getTextFilterValue() {
        return textFilterField.getValue();
    }
}

