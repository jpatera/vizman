package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class KontTreeXlsReportBuilder extends FastReportBuilder {

    private AbstractColumn ckontCol;
    private AbstractColumn kontTextCol;
    private AbstractColumn czakCol;
    private AbstractColumn zakTextCol;
    private AbstractColumn cfaktCol;
    private AbstractColumn faktTextCol;
    private AbstractColumn faktCastkaCol;
    private AbstractColumn faktCisloCol;
    private AbstractColumn faktBlankCol1;

    private DJGroup kontGroup;
    private DJGroup subZakGroup;

    public KontTreeXlsReportBuilder() {
        super();

        buildReportColumns();
        buildReportGroups();

        this
//                .setTitle("")
//                .setSubtitle("")
                .setUseFullPageWidth(true)
                .setIgnorePagination(false) // We need pagination to split report to mmore XLS lists
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
//                .setPrintBackgroundOnOddRows(true)
//                .setPageSizeAndOrientation(Page_xls())

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                // Add basic  columns
                .setPrintColumnNames(false)
                .addColumn(ckontCol)
                .addColumn(kontTextCol)

                // Add groups
                .addGroup(kontGroup)

                // Add sub-reports
                .addField("zaks", Collection.class.getName())
                .addSubreportInGroupFooter(1, createZaksSubreport(), new ClassicLayoutManager()
                        , "zaks", DJConstants.DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION
                )

                // Add totals
        ;

    }

    private DynamicReport createZaksSubreport() {
        return new FastReportBuilder()
                .setUseFullPageWidth(true)
                .setIgnorePagination(false)
                .setMargins(0, 0, 0, 0)
                .setWhenNoDataNoPages()
                .setReportLocale(new Locale("cs", "CZ"))
                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                .setPrintColumnNames(false)
                .addColumn(czakCol)
                .addColumn(zakTextCol)

                .addField("fakts", Collection.class.getName())
                .addGroup(subZakGroup)
                .setPrintColumnNames(false)

                .addSubreportInGroupFooter(1, createFaktsSubreport(), new ClassicLayoutManager()
                        , "fakts", DJConstants.DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION)

                .build()
                ;
    }

    private DynamicReport createFaktsSubreport() {
        return new FastReportBuilder()
                .setUseFullPageWidth(true)
                .setIgnorePagination(false)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("no data", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)

                .setPrintColumnNames(true)
                .addColumn(faktBlankCol1)
                .addColumn(cfaktCol)
                .addColumn(faktTextCol)
                .addColumn(faktCastkaCol)
                .addColumn(faktCisloCol)
                .setGrandTotalLegend("Zak. celkem")
                .addGlobalFooterVariable(faktCastkaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)

                .build()
                ;
    }



    private void buildReportColumns() {

        // KONT report columns
        ckontCol = ColumnBuilder.getNew()
                .setColumnProperty("ckont", String.class)
                .setTitle("Č.kont")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(40)
                .build();

        kontTextCol = ColumnBuilder.getNew()
                .setColumnProperty("text", String.class)
                .setTitle("Kontrakt - text")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(400)
                .build();

        // ZAK sub-report columns
        czakCol = ColumnBuilder.getNew()
                .setColumnProperty("czak", Integer.class)
                .setTitle("Č.zak")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(40)
                .build();

        zakTextCol = ColumnBuilder.getNew()
                .setColumnProperty("text", String.class)
                .setTitle("Zakázka - text")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(400)
                .build();

        // FAKT sub-report columns
        cfaktCol = ColumnBuilder.getNew()
                .setColumnProperty("cfakt", Integer.class)
                .setTitle("DP")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(50)
                .build();

        faktTextCol = ColumnBuilder.getNew()
                .setColumnProperty("text", String.class)
                .setTitle("DP - text")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(400)
                .build();

        faktCastkaCol = ColumnBuilder.getNew()
                .setColumnProperty("castka", BigDecimal.class)
                .setTitle("DP - Cena")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        faktCisloCol = ColumnBuilder.getNew()
                .setColumnProperty("faktCislo", String.class)
                .setTitle("DP - Faktura")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(200)
                .build();

        faktBlankCol1 = ColumnBuilder.getNew()
                .setCustomExpression(new BlankExpression())
                .setTitle("")
                .setWidth(40)
                .build();
    }

    public class BlankExpression implements CustomExpression {

        public BlankExpression() {    }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return "";
        }

        @Override
        public String getClassName() {
            return String.class.getName();
        }
    }

    private void buildReportGroups() {

        // KONT group by ckont
        kontGroup = new GroupBuilder()
                .setCriteriaColumn((PropertyColumn) ckontCol)
//                .addHeaderVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkHourLabel)
                .setGroupLayout(GroupLayout.VALUE_FOR_EACH_WITH_HEADERS)
                .setStartInNewPage(true)
                .build()
        ;

        // ZAK group by czak
        subZakGroup = new GroupBuilder()
                .setCriteriaColumn((PropertyColumn) czakCol)
//                .addHeaderVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkHourLabel)
                .setGroupLayout(GroupLayout.VALUE_FOR_EACH_WITH_HEADERS)
                .setStartInNewPage(false)
                .build()
        ;
    }


//    private Subreport createSubreport(String title) throws Exception {
//        SubReportBuilder srb = new SubReportBuilder();
//        srb.setDynamicReport(createHeaderSubreport(title), new ClassicLayoutManager())
////                .setStartInNewPage(true)
//                .setDataSource(DJConstants.DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION, "zaks");
//        return srb.build();
//    }

//    private DynamicReport createHeaderSubreport(String title) throws Exception {
//        FastReportBuilder rb = new FastReportBuilder();
//        DynamicReport dr = rb
//                .setUseFullPageWidth(true)
//                .setIgnorePagination(false)
//                .setMargins(0, 0, 0, 0)
////                .setWhenNoDataNoPages()
//                .setWhenNoDataNoPages()
//                .setReportLocale(new Locale("cs", "CZ"))
//                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
//                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
//
//                .addColumn(czakCol)
//                .addColumn(zakTextCol)
////                .setTitle(title)
//                .build();
//        return dr;
//    }

//    private DynamicReport createFooterSubreport() {
////        FastReportBuilder rb = new FastReportBuilder();
//        SubReportBuilder rb = new SubReportBuilder();
////        DynamicReport subreport = rb
////        DynamicReport subreport = rb
//        Subreport subreport = rb
//                .addColumn(czakCol)
////                .addGroups(1)
//                .setMargins(0, 0, 0, 0)
//                .setUseFullPageWidth(true)
//                .setTitle("Footer Subreport for this group")
//                .build();
//        return subreport;
//    }

}
