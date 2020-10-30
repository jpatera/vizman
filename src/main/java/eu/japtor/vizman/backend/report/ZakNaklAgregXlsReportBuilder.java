package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class ZakNaklAgregXlsReportBuilder extends FastReportBuilder {

    static Page Page_xls(){
        return new Page(666,1000,true);
    }

//    protected DynamicReportBuilder reportBuilder;

    private AbstractColumn kzCisloCol;
    private AbstractColumn kzTextFullCol;
//    private AbstractColumn prijmeniCol;
//    private AbstractColumn ymPruhCol;
//    private AbstractColumn workPruhCol;
//    private AbstractColumn workPruhP8Col;
    private AbstractColumn naklMzdaCol;
    private AbstractColumn naklMzdaPojCol;
    private AbstractColumn naklMzdaPojRezCol;
    private AbstractColumn naklMzdaP8Col;
    private AbstractColumn naklMzdaP8PojCol;
    private AbstractColumn naklMzdaP8PojRezCol;

    private DJGroup userGroup;
    private DJGroup zakGroup;

    private BigDecimal koefPoj;
    private BigDecimal koefRez;
    private BigDecimal koefPojRez;

    public ZakNaklAgregXlsReportBuilder(
            String subtitleText
            , boolean withMoneyColumns
            , BigDecimal koefPojist
            , BigDecimal koefRezie
    ) {
        super();

        BigDecimal kp = null == koefPojist ? BigDecimal.ZERO : koefPojist;
        koefPoj = BigDecimal.ONE.add(kp);
        BigDecimal kr = null == koefRezie ? BigDecimal.ZERO : koefRezie;
        koefRez = BigDecimal.ONE.add(kr);
        koefPojRez = koefRez.multiply(koefRez);

        buildReportColumns();
        buildReportGroups(withMoneyColumns);

        this
                .setTitle("NÁKLADY NA ZAKÁZKY")
                .setSubtitle(subtitleText)
                .setUseFullPageWidth(false)
                .setIgnorePagination(true)  // FALSE  is needed if for splitting reports to more XLS lists (by groups)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
//                .setPrintBackgroundOnOddRows(true)
                .setPageSizeAndOrientation(Page_xls())

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                .setPrintColumnNames(true)

                // Add columns
                .addColumn(kzCisloCol)
//                .addColumn(prijmeniCol)
//                .addColumn(ymPruhCol)
//                .addColumn(workPruhCol)

                // Add groups
//                .addGroup(zakGroup)
//                .addGroup(userGroup)

                // Add sub-reports

                // Add totals
                .setGrandTotalLegend("Celkem")
//                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
        ;
        if (withMoneyColumns) {
            this
                    .addColumn(naklMzdaCol)
                    .addColumn(naklMzdaPojCol)
                    .addColumn(naklMzdaPojRezCol)
//                    .addColumn(naklMzdaPojistCol)
//                    .addColumn(sazbaCol)
//                    .addColumn(koefP8Col)
//                    .addColumn(workPruhP8Col)
                    .addColumn(naklMzdaP8Col)
                    .addColumn(naklMzdaP8PojCol)
                    .addColumn(naklMzdaP8PojRezCol)

                    .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojRezCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
//                    .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
//                    .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8PojRezCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
//                    .addGlobalFooterVariable(naklMzdaPojistP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
            ;
        }
        // Add basic columnsII
        this
            .addColumn(kzTextFullCol)
        ;
    }

    CustomExpression naklMzdaPojExp = new CustomExpression() {
        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return ((BigDecimal) fields.get("naklMzda")).multiply(koefPoj);
        }
        @Override
        public String getClassName() {
            return BigDecimal.class.getName();
        }
    };

    CustomExpression naklMzdaPojRezExp = new CustomExpression() {
        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return ((BigDecimal) fields.get("naklMzda")).multiply(koefPojRez);
        }
        @Override
        public String getClassName() {
            return BigDecimal.class.getName();
        }
    };

    CustomExpression naklMzdaP8PojExp = new CustomExpression() {
        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return ((BigDecimal) fields.get("naklMzdaP8")).multiply(koefRez);
        }
        @Override
        public String getClassName() {
            return BigDecimal.class.getName();
        }
    };

    CustomExpression naklMzdaP8PojRezExp = new CustomExpression() {
        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return ((BigDecimal) fields.get("naklMzdaP8")).multiply(koefPojRez);
        }
        @Override
        public String getClassName() {
            return BigDecimal.class.getName();
        }
    };


    private void buildReportColumns() {
        kzCisloCol = ColumnBuilder.getNew()
                .setColumnProperty("kzCislo", String.class)
                .setTitle("ČK-ČZ")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(70)
//                .setFixedWidth(true)
                .build();

        kzTextFullCol = ColumnBuilder.getNew()
                .setColumnProperty("kzTextFull", String.class)
//                .setCustomExpression(getCalcZakId())
//                .setCustomExpressionForCalculation(getCalcZakId2())
                .setTitle("Zakázka")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(600)
//                .setFixedWidth(true)
                .build();

//        prijmeniCol = ColumnBuilder.getNew()
//                .setColumnProperty("prijmeni", String.class)
//                .setTitle("Příjmení")
//                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
//                .setWidth(120)
////                .setFixedWidth(true)
//                .build();
//
//        ymPruhCol = ColumnBuilder.getNew()
//                .setColumnProperty("ymPruh", YearMonth.class)
//                .setTitle("Rok-Měs")
//                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
////                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
//                .setWidth(120)
////                .setFixedWidth(true)
//                .build();

//        workPruhCol = ColumnBuilder.getNew()
//                .setColumnProperty("workPruh", BigDecimal.class)
//                .setTitle("Hodin")
//                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
////                .setWidth(60)
////                .setFixedWidth(true)
//                .build();
//
//        workPruhP8Col = ColumnBuilder.getNew()
//                .setColumnProperty("workPruhP8", BigDecimal.class)
//                .setTitle("Hodin P8")
//                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
////                .setWidth(60)
////                .setFixedWidth(true)
//                .build();

        naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzdy [CZK]")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaPojCol = ColumnBuilder.getNew()
                .setCustomExpression(naklMzdaPojExp)
                .setTitle("Mzdy * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaPojRezCol = ColumnBuilder.getNew()
                .setCustomExpression(naklMzdaPojRezExp)
                .setTitle("Mzdy * P*R")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzdy P8 [CZK]")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8PojCol = ColumnBuilder.getNew()
                .setCustomExpression(naklMzdaP8PojExp)
                .setTitle("Mzdy P8 * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8PojRezCol = ColumnBuilder.getNew()
                .setCustomExpression(naklMzdaP8PojRezExp)
                .setTitle("Mzdy P8 * P*R")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

//        naklMzdaPojistP8Col = ColumnBuilder.getNew()
//                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
//                .setTitle("Mzdy P8 * ")
//                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
////                .setWidth(80)
////                .setFixedWidth(true)
//                .build();

//        sazbaCol = ColumnBuilder.getNew()
//                .setColumnProperty("sazba", BigDecimal.class)
//                .setTitle("Sazba")
//                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
////                .setWidth(60)
////                .setFixedWidth(true)
//                .build();
//
//        koefP8Col = ColumnBuilder.getNew()
//                .setColumnProperty("koefP8", BigDecimal.class)
//                .setTitle("Koef P8")
//                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
////                .setWidth(60)
////                .setFixedWidth(true)
//                .build();
    }

    private void buildReportGroups(boolean withMoneyColumns) {

        // User group
//        GroupBuilder userGroupBuilder = new GroupBuilder();
//        DJGroupLabel userGroupWorkHourLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
//        DJGroupLabel userGroupMzdaLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
//        DJGroupLabel userGroupWorkMzdaPojistLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
//        userGroupBuilder
//                .setCriteriaColumn((PropertyColumn) prijmeniCol)
//                .addHeaderVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkHourLabel)
//                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
//        ;
//        if (withMoneyColumns) {
//            userGroupBuilder
//                    .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupMzdaLabel)
//                    .addHeaderVariable(naklMzdaPojistCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkMzdaPojistLabel)
//                    .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, null)
//                    .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, null)
//                    .addHeaderVariable(naklMzdaPojistP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, null)
//            ;
//        }
//        userGroup = userGroupBuilder.build();

        // ZAK group
//        GroupBuilder zakGroupBuilder = new GroupBuilder();
//        zakGroup = zakGroupBuilder
//                .setCriteriaColumn((PropertyColumn) kzTextFullCol)
//                .setGroupLayout(GroupLayout.DEFAULT)
//                .build()
//        ;
    }

//    public DynamicReport buildReport()  {
//        return reportBuilder.build();
//    }
}
