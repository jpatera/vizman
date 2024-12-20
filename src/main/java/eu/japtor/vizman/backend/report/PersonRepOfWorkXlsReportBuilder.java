package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.LabelPosition;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

/**
 Vykaz prace pracovnika (...tlacitko v JAKE tabulce ???)
 */
public class PersonRepOfWorkXlsReportBuilder extends FastReportBuilder {

    private AbstractColumn kzCisloTextRepCol;
    private AbstractColumn prijmeniCol;
    private AbstractColumn ymPruhCol;
    private AbstractColumn datePruhCol;
    private AbstractColumn workPruhCol;
    private AbstractColumn workPruhP8Col;
    private AbstractColumn naklMzdaCol;
    private AbstractColumn naklMzdaP8Col;
    private AbstractColumn naklMzdaPojCol;
    private AbstractColumn naklMzdaP8PojCol;
    private AbstractColumn sazbaCol;
    private AbstractColumn koefP8Col;
    private AbstractColumn blankCol;

    private DJGroup userGroup;
    private DJGroup ymGroup;
    private DJGroup zakGroup;

    public PersonRepOfWorkXlsReportBuilder(
            String titleText
            , String subtitleText
            , boolean withMoneyColumns) {
        super();

        buildReportColumns();
        buildReportGroups(withMoneyColumns);

        this
                .setTitle(titleText)
                .setSubtitle(subtitleText)

                .setPageSizeAndOrientation(new Page(6666,900))
                .setUseFullPageWidth(false)
                .setIgnorePagination(true) // FALSE is needed if splitting reports to more XLS lists (by groups)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
//                .setPrintBackgroundOnOddRows(true)

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_BOLD_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addStyle(WORK_HOUR_GRID_XLS_STYLE)

                .setPrintColumnNames(true)

                // Columns
                .addColumn(kzCisloTextRepCol)
                .addColumn(prijmeniCol)
                .addColumn(ymPruhCol)
                .addColumn(blankCol)
                .addColumn(datePruhCol)
                .addColumn(workPruhCol)

                // Groups
                .addGroup(userGroup)
                .addGroup(zakGroup)
                .addGroup(ymGroup)

        // Sub-reports

        // Totals
//                .setGrandTotalLegend("Zakázka celkem:")
//                .setGrandTotalLegendStyle(DEFAULT_GRID_XLS_TEXT_BOLD_STYLE)
//                .setGlobalFooterVariableHeight(30)
//                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_BOLD_GRID_XLS_STYLE)
        ;
        if (withMoneyColumns) {
            this
                    .addColumn(naklMzdaCol)
                    .addColumn(naklMzdaPojCol)
                    .addColumn(sazbaCol)
                    .addColumn(koefP8Col)
                    .addColumn(workPruhP8Col)
                    .addColumn(naklMzdaP8Col)
                    .addColumn(naklMzdaP8PojCol)

//                    .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE)
//                    .addGlobalFooterVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE)
//                    .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_BOLD_GRID_XLS_STYLE)
//                    .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE)
//                    .addGlobalFooterVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE)
            ;
        }
    }

    private void buildReportColumns() {
        kzCisloTextRepCol = ColumnBuilder.getNew()
                .setColumnProperty("kzCisloTextRep", String.class)
//                .setCustomExpression(getCalcZakId())
//                .setCustomExpressionForCalculation(getCalcZakId2())
                .setTitle("Zakázka")
                .setStyle(DEFAULT_GRID_XLS_TEXT_BOLD_STYLE)
//                .setWidth(110)
//                .setFixedWidth(true)
                .build();

        prijmeniCol = ColumnBuilder.getNew()
                .setColumnProperty("prijmeni", String.class)
                .setTitle("Příjmení")
                .setStyle(DEFAULT_GRID_XLS_TEXT_BOLD_STYLE)
                .setWidth(120)
//                .setFixedWidth(true)
                .build();

        ymPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("ymPruh", YearMonth.class)
                .setTitle("Rok-Měs")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(120)
                .build();

        datePruhCol = ColumnBuilder.getNew()
                .setColumnProperty("datePruh", LocalDate.class)
                .setTitle("Datum")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(80)
                .build();

        workPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("workPruh", BigDecimal.class)
                .setTitle("Hodin")
                .setStyle(WORK_HOUR_GRID_XLS_STYLE)
                .setWidth(60)
                .build();

        workPruhP8Col = ColumnBuilder.getNew()
                .setColumnProperty("workPruhP8", BigDecimal.class)
                .setTitle("Hodin P8")
                .setStyle(WORK_HOUR_GRID_XLS_STYLE)
                .setWidth(60)
                .build();

        naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzdy [CZK]")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaPojCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzdy * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzdy P8")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        naklMzdaP8PojCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
                .setTitle("Mzdy P8 * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
                .build();

        sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(60)
                .build();

        koefP8Col = ColumnBuilder.getNew()
                .setColumnProperty("koefP8", BigDecimal.class)
                .setTitle("Koef P8")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(60)
                .build();

        blankCol = ColumnBuilder.getNew()
                .setCustomExpression(new BlankExpression())
                .setTitle("")
                .setWidth(40)
                .build();
    }

    public class BlankExpression implements CustomExpression {
        public BlankExpression() {}

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return "";
        }
        @Override
        public String getClassName() {
            return String.class.getName();
        }
    }

    public class ZakGroupFooterLabelExpression implements CustomExpression {
        public ZakGroupFooterLabelExpression() {}

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            return "Zakázka celkem:";
        }
        @Override
        public String getClassName() {
            return String.class.getName();
        }
    }

    private void buildReportGroups(boolean withMoneyColumns) {

        // ZAK group
        // ---------
        DJGroupVariable zakGroupFooterLabelVar = new DJGroupVariable(
                prijmeniCol
                , new ZakGroupFooterLabelExpression()
                , DEFAULT_GRID_XLS_TEXT_BOLD_STYLE
        );
        GroupBuilder zakGroupBuilder = new GroupBuilder();
        zakGroupBuilder
                .setCriteriaColumn((PropertyColumn) kzCisloTextRepCol)
//                .setGroupLayout(GroupLayout.DEFAULT)
//                .setGroupLayout(GroupLayout.DEFAULT_WITH_HEADER)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
//                .setGroupLayout(GroupLayout.VALUE_FOR_EACH)
//                .setStartInNewColumn(true)
                .addFooterVariable(zakGroupFooterLabelVar)
                .addFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_BOLD_GRID_XLS_STYLE)
        ;

        if (withMoneyColumns) {
            zakGroupBuilder
                    .addFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, null)
                    .addFooterVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, null)
                    .addFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_BOLD_GRID_XLS_STYLE, null, null)
                    .addFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, null)
                    .addFooterVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, null)
            ;
        }
        zakGroup = zakGroupBuilder.build();

        // User group
        // ----------
        DJGroupLabel userGroupWorkHourLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupMzdaLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupWorkMzdaPojistLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        GroupBuilder userGroupBuilder = new GroupBuilder();
        userGroupBuilder
                .setCriteriaColumn((PropertyColumn) prijmeniCol)
                .addHeaderVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_BOLD_GRID_XLS_STYLE, null, userGroupWorkHourLabel)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .setStartInNewColumn(true)
        ;
        if (withMoneyColumns) {
            userGroupBuilder
                    .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, userGroupMzdaLabel)
                    .addHeaderVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, userGroupWorkMzdaPojistLabel)
                    .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_BOLD_GRID_XLS_STYLE, null, null)
                    .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, null)
                    .addHeaderVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_BOLD_GRID_XLS_STYLE, null, null)
            ;
        }
        userGroup = userGroupBuilder.build();

        // YM group
        // ---------
        GroupBuilder ymGroupBuilder = new GroupBuilder();
        ymGroup = ymGroupBuilder
                .setCriteriaColumn((PropertyColumn) ymPruhCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;
    }
}
