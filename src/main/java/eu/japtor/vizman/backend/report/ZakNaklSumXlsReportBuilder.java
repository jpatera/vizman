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

// Souhrnny 1D report po zakazakch, aktualne se nepouziva.
// Nemazat, muze se hodit!!
public class ZakNaklSumXlsReportBuilder extends FastReportBuilder {

    private AbstractColumn kzCisloCol;
    private AbstractColumn kzTextFullCol;
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

    public ZakNaklSumXlsReportBuilder(
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
                .setPageSizeAndOrientation(new Page(666,1000))

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                .setPrintColumnNames(true)

                // Columns
                .addColumn(kzCisloCol)

                // Groups

                // Sub-reports

                // Add totals
                .setGrandTotalLegend("Celkem")
        ;
        if (withMoneyColumns) {
            this
                    .addColumn(naklMzdaCol)
                    .addColumn(naklMzdaPojCol)
                    .addColumn(naklMzdaPojRezCol)
                    .addColumn(naklMzdaP8Col)
                    .addColumn(naklMzdaP8PojCol)
                    .addColumn(naklMzdaP8PojRezCol)

                    .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojRezCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8PojRezCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
            ;
        }
        // Columns II
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
                .setTitle("Zakázka")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(600)
//                .setFixedWidth(true)
                .build();

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
                .build();

        naklMzdaP8PojCol = ColumnBuilder.getNew()
                .setCustomExpression(naklMzdaP8PojExp)
                .setTitle("Mzdy P8 * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaP8PojRezCol = ColumnBuilder.getNew()
                .setCustomExpression(naklMzdaP8PojRezExp)
                .setTitle("Mzdy P8 * P*R")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();
    }

    private void buildReportGroups(boolean withMoneyColumns) {

    }
}
