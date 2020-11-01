package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class ZakRozpracXlsReportBuilder extends FastReportBuilder {

    public static final String ZAQAS_REP_FIELD_NAME = "zaqas";

//    private AbstractColumn kzCisloCol;
//    private AbstractColumn kzTextCol;
    private AbstractColumn repKzCisloAndTextCol;
    private AbstractColumn rokCol;
    private AbstractColumn qaCol;
    private AbstractColumn rxCol;
    private AbstractColumn rozpracBlankCol;

    private DJGroup zaqasGroup;

    public ZakRozpracXlsReportBuilder(String subtitleText) {
        super();

//        DJVariable kzCisloVar = new DJVariable();
//        kzCisloVar.setName("kzCisloVar");
//        kzCisloVar.setExpression(new KzCisloExpression());
//        kzCisloVar.setClassName(String.class.getName());
//        this.addVariable(kzCisloVar);

        buildReportColumns();
        buildReportGroups();

        this
//                .setReportName("REPORT NAME")   // When exporting to Excel, this is going to be the sheet name. Be careful because Excel only allows 32 alphanumeric characters
//                .addField("repKzCisloAndText", String.class.getName())
//                .setTitle("$F{repKzCisloAndText}", true)
                .setTitle("ROPRACOVANOST ZAKÁZKY")
                .setSubtitle(subtitleText)

                .setPageSizeAndOrientation(new Page(333,700))
                .setUseFullPageWidth(false)
                .setIgnorePagination(true) // FALSE is needed if splitting reports to more XLS lists (by groups)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
//                .setPrintBackgroundOnOddRows(true)

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                .setPrintColumnNames(false)

                // Add columns
//                .addColumn(kzCisloCol)
                .addColumn(repKzCisloAndTextCol)

                // Add groups
                .addGroup(zaqasGroup)

                // Add sub-reports
                .addField(ZAQAS_REP_FIELD_NAME, Collection.class.getName())
                .addSubreportInGroupFooter(1, createZaqasSubreport(), new ClassicLayoutManager()
                        , ZAQAS_REP_FIELD_NAME, DJConstants.DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION
                )

        // Add totals
        ;

    }

    private DynamicReport createZaqasSubreport() {
        return new FastReportBuilder()
                .setUseFullPageWidth(false)
                .setIgnorePagination(true)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("no data", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .addStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)

                .setPrintColumnNames(true)
                .addColumn(rokCol)
                .addColumn(qaCol)
                .addColumn(rxCol)
//                .setGrandTotalLegend("Celkem")
//                .addGlobalFooterVariable(rxCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)

                .build()
                ;
    }



    private void buildReportColumns() {

        // ZAKR report columns
        // -------------------
//        kzCisloCol = ColumnBuilder.getNew()
//                .setColumnProperty("kzCislo", String.class)
//                .setTitle("ČK-ČZ")
//                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
//                .setWidth(60)
//                .setShowText(false)
//                .build();
//
//        kzTextCol = ColumnBuilder.getNew()
//                .setColumnProperty("kzTextFull", String.class)
//                .setTitle("Kontrakt / Zakázka")
//                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
//                .setWidth(600)
//                .build();

        repKzCisloAndTextCol = ColumnBuilder.getNew()
                .setColumnProperty("repKzCisloAndText", String.class)
                .setTitle("ČK-ČZ : Kontrakt / Zakázka")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(600)
                .build();


        // ROZPRAC sub-report columns
        // --------------------------
        rokCol = ColumnBuilder.getNew()
                .setColumnProperty("rok", Integer.class)
                .setTitle("Rok")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(60)
                .build();

        qaCol = ColumnBuilder.getNew()
                .setColumnProperty("qa", Integer.class)
                .setTitle("Kvartál")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(50)
                .build();

        rxCol = ColumnBuilder.getNew()
                .setColumnProperty("rx", BigDecimal.class)
                .setTitle("RX")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(60)
                .build();

//        blankCol = ColumnBuilder.getNew()
//                .setCustomExpression(new BlankExpression())
//                .setTitle("")
//                .setWidth(40)
//                .build();
    }

    private void buildReportGroups() {

        // ZAQAs group
        zaqasGroup = new GroupBuilder()
//                .setCriteriaColumn((PropertyColumn) kzCislotCol)
                .setCriteriaColumn((PropertyColumn) repKzCisloAndTextCol)
//                .setStartInNewColumn(true)
//                .addHeaderVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkHourLabel)
//                .setGroupLayout(GroupLayout.VALUE_FOR_EACH_WITH_HEADERS)
                .setGroupLayout(GroupLayout.VALUE_FOR_EACH)
                .setStartInNewPage(false)
                .build()
        ;
    }

//    public class KzCisloAndTextExpr implements CustomExpression {
//
//        public KzCisloAndTextExpr() {    }
//
//        @Override
//        public Object evaluate(Map fields, Map variables, Map parameters) {
//            return fields.get("kzCislo") + " " + fields.get("kzCislo");
//        }
//
//        @Override
//        public String getClassName() {
//            return String.class.getName();
//        }
//    }


//    public class BlankExpression implements CustomExpression {
//
//        public BlankExpression() {    }
//
//        @Override
//        public Object evaluate(Map fields, Map variables, Map parameters) {
//            return "";
//        }
//
//        @Override
//        public String getClassName() {
//            return String.class.getName();
//        }
//    }
}
