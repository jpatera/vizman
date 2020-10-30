package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class ZakNaklXlsReportBuilder extends FastReportBuilder {

    static Page Page_xls(){
        return new Page(666,900,true);
    }

//    protected DynamicReportBuilder reportBuilder;

    private AbstractColumn ckzTextRepCol;
    private AbstractColumn prijmeniCol;
    private AbstractColumn ymPruhCol;
    private AbstractColumn workPruhCol;
    private AbstractColumn workPruhP8Col;
    private AbstractColumn naklMzdaCol;
    private AbstractColumn naklMzdaP8Col;
    private AbstractColumn naklMzdaPojCol;
    private AbstractColumn naklMzdaP8PojCol;
    private AbstractColumn sazbaCol;
    private AbstractColumn koefP8Col;

    private DJGroup userGroup;
    private DJGroup zakGroup;

    public ZakNaklXlsReportBuilder(boolean withMoneyColumns) {
        super();

        buildReportColumns();
        buildReportGroups(withMoneyColumns);

        this
                .setTitle("NÁKLADY NA ZAKÁZKU")
//                .setSubtitle("")
//                .setUseFullPageWidth(false) // Otherwise "some" width is  counted for sheet
                .setUseFullPageWidth(false) // Otherwise "some" width is  counted for sheet
                .setIgnorePagination(true)  // For Excel, we don't need here pagination because it is not multi-list XLS
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
//                .setPrintBackgroundOnOddRows(true)
                .setPageSizeAndOrientation(Page_xls())

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addStyle(WORK_HOUR_GRID_XLS_STYLE)

                .setGrandTotalLegend("National Total")
                .setPrintColumnNames(true)

                // Add basic columns
                .addColumn(ckzTextRepCol)
                .addColumn(prijmeniCol)
                .addColumn(ymPruhCol)
                .addColumn(workPruhCol)

                // Add groups
                .addGroup(zakGroup)
                .addGroup(userGroup)

                // Add sub-reports

                // Add totals
                .setGrandTotalLegend("Nagruzka Total")
                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE)
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

                    .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
            ;
        }
    }

    private void buildReportColumns() {
        ckzTextRepCol = ColumnBuilder.getNew()
                .setColumnProperty("ckzTextXlsRep", String.class)
//                .setCustomExpression(getCalcZakId())
//                .setCustomExpressionForCalculation(getCalcZakId2())
                .setTitle("Zakázka")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
//                .setWidth(110)
//                .setFixedWidth(true)
                .build();

        prijmeniCol = ColumnBuilder.getNew()
                .setColumnProperty("prijmeni", String.class)
                .setTitle("Příjmení")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(120)
//                .setFixedWidth(true)
                .build();

        ymPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("ymPruh", YearMonth.class)
                .setTitle("Rok-Měs")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .setWidth(120)
//                .setFixedWidth(true)
                .build();

        workPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("workPruh", BigDecimal.class)
                .setTitle("Hodin")
                .setStyle(WORK_HOUR_GRID_XLS_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        workPruhP8Col = ColumnBuilder.getNew()
                .setColumnProperty("workPruhP8", BigDecimal.class)
                .setTitle("Hodin P8")
                .setStyle(WORK_HOUR_GRID_XLS_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzdy [CZK]")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzdy * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzdy P8")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8PojCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
                .setTitle("Mzdy P8 * P")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        koefP8Col = ColumnBuilder.getNew()
                .setColumnProperty("koefP8", BigDecimal.class)
                .setTitle("Koef P8")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();
    }

    private void buildReportGroups(boolean withMoneyColumns) {

        // User group
        GroupBuilder userGroupBuilder = new GroupBuilder();
        DJGroupLabel userGroupWorkHourLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupMzdaLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupWorkMzdaPojistLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        userGroupBuilder
                .setCriteriaColumn((PropertyColumn) prijmeniCol)
                .addHeaderVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE, null, userGroupWorkHourLabel)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
        ;
        if (withMoneyColumns) {
            userGroupBuilder
                    .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, userGroupMzdaLabel)
                    .addHeaderVariable(naklMzdaPojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, userGroupWorkMzdaPojistLabel)
                    .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE, null, null)
                    .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, null)
                    .addHeaderVariable(naklMzdaP8PojCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, null)
            ;
        }
        userGroup = userGroupBuilder.build();

        // ZAK group
        GroupBuilder zakGroupBuilder = new GroupBuilder();
        zakGroup = zakGroupBuilder
                .setCriteriaColumn((PropertyColumn) ckzTextRepCol)
                .setGroupLayout(GroupLayout.DEFAULT)
                .build()
        ;
    }

//    public DynamicReport buildReport()  {
//        return reportBuilder.build();
//    }
}
