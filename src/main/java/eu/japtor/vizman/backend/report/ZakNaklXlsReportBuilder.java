package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.DynamicReport;
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

public class ZakNaklXlsReportBuilder {

//    static Page Page_xls(){
//        return new Page(1111,1300,true);
//    }

    protected DynamicReportBuilder reportBuilder;

    private AbstractColumn ckzTextRepCol;
    private AbstractColumn prijmeniCol;
    private AbstractColumn ymPruhCol;
    private AbstractColumn workPruhCol;
    private AbstractColumn workPruhP8Col;
    private AbstractColumn naklMzdaCol;
    private AbstractColumn naklMzdaP8Col;
    private AbstractColumn naklMzdaPojistCol;
    private AbstractColumn naklMzdaPojistP8Col;
    private AbstractColumn sazbaCol;
    private AbstractColumn koefP8Col;

    private DJGroup userGroup;
    private DJGroup zakGroup;

    public ZakNaklXlsReportBuilder(boolean withMoneyColumns) {
        super();

        buildReportColumns();
        buildReportGroups(withMoneyColumns);

        reportBuilder = (new FastReportBuilder())
                .setUseFullPageWidth(false) // Otherwise "some" width is  counted for sheet
                .setIgnorePagination(true)  // For Excel, we don't want pagination, just a plain list
                .setWhenNoData("(no data)", new Style())
//                .setTitle("NÁKLADY NA ZAKÁZKU")
                .setReportLocale(new Locale("cs", "CZ"))
//                .setSubtitle("Rok: " + paramRokStr)
//                .addParameter("PARAM_ROK", String.class.getName())
//                .setMargins(0, 0, 0, 0)
//                .setPrintBackgroundOnOddRows(true)
                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
//                .setPageSizeAndOrientation(Page_xls())

                .setPrintColumnNames(true)

                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)

                // Add basic  columns
                .addColumn(ckzTextRepCol)
                .addColumn(prijmeniCol)
                .addColumn(ymPruhCol)
                .addColumn(workPruhCol)

                // Add groups
                .addGroup(zakGroup)
                .addGroup(userGroup)

                // Add basic totals
                .setGrandTotalLegend("Nagruzka Total")
                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
        ;
        if (withMoneyColumns) {
            reportBuilder
                    .addColumn(naklMzdaCol)
                    .addColumn(naklMzdaPojistCol)
                    .addColumn(sazbaCol)
                    .addColumn(koefP8Col)
                    .addColumn(workPruhP8Col)
                    .addColumn(naklMzdaP8Col)
                    .addColumn(naklMzdaPojistP8Col)

                    .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
                    .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
                    .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
                    .addGlobalFooterVariable(naklMzdaPojistP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE)
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
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        workPruhP8Col = ColumnBuilder.getNew()
                .setColumnProperty("workPruhP8", BigDecimal.class)
                .setTitle("Hodin P8")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzda [CZK]")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzda + Poj.")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzda P8")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
                .setTitle("Mzda + P. P8")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        koefP8Col = ColumnBuilder.getNew()
                .setColumnProperty("koefP8", BigDecimal.class)
                .setTitle("Koef P8")
                .setStyle(DEFAULT_GRID_XLS_NUM_STYLE)
//                .setWidth(60)
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
                .addHeaderVariable(workPruhCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkHourLabel)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
        ;
        if (withMoneyColumns) {
            userGroupBuilder
                    .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupMzdaLabel)
                    .addHeaderVariable(naklMzdaPojistCol, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, userGroupWorkMzdaPojistLabel)
                    .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, null)
                    .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, null)
                    .addHeaderVariable(naklMzdaPojistP8Col, DJCalculation.SUM, DEFAULT_GRID_XLS_NUM_STYLE, null, null)
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

    public DynamicReport buildReport()  {
        return reportBuilder.build();
    }
}
