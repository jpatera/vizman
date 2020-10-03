package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;


import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class KontTreeXlsReportBuilder extends FastReportBuilder {

//    private DynamicReportBuilder reportBuilder;

    private AbstractColumn ckontCol;
    private AbstractColumn kontTextCol;

    private DJGroup kontGroup;

    public KontTreeXlsReportBuilder() {
        super();

        buildReportColumns();
        buildReportGroups();

//        reportBuilder = (new FastReportBuilder())
//        reportBuilder = (new FastReportBuilder())
        this
                .setUseFullPageWidth(true)
                .setIgnorePagination(false)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)

                .setPrintColumnNames(true)

                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                // Add basic  columns
                .addColumn(ckontCol)
                .addColumn(kontTextCol)

                // Add groups
                .addGroup(kontGroup)
//                .addGroup(userGroup)

                // Add basic totals
//                .setGrandTotalLegend("Nagruzka Total")
//                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
        ;

    }

    private void buildReportColumns() {
        ckontCol = ColumnBuilder.getNew()
                .setColumnProperty("ckont", String.class)
                .setTitle("ČK")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .build();

        kontTextCol = ColumnBuilder.getNew()
                .setColumnProperty("text", String.class)
                .setTitle("KONT - text")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .build();
    }


    private void buildReportGroups() {

        // Kont group
        GroupBuilder kontGroupBuilder = new GroupBuilder();
        kontGroup = kontGroupBuilder
                .setCriteriaColumn((PropertyColumn) ckontCol)
//                .addHeaderVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkHourLabel)
//                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .setStartInNewPage(true)
                .build()
        ;

    }

//    public DynamicReport buildReport()  {
//        return this.build();
//    }
}
