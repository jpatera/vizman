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

    static Page Page_xls(){
        return new Page(1111,1300,true);
    }

    static final Style AMOUNT_STYLE;
    static {
        AMOUNT_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(10))
                .build();
    }

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

    public ZakNaklXlsReportBuilder() {
        super();

        buildReportColumns();
        buildReportGroups();

        reportBuilder = (new FastReportBuilder())
                .setUseFullPageWidth(true)
                .setIgnorePagination(true) // For Excel, we don't want pagination, just a plain list
                .setWhenNoData("(no data)", new Style())
//                .setTitle("NÁKLADY NA ZAKÁZKU")
                .setReportLocale(new Locale("cs", "CZ"))
//                .setSubtitle("Rok: " + paramRokStr)
//                .addParameter("PARAM_ROK", String.class.getName())
                .setMargins(0, 0, 0, 0)
//                .setPrintBackgroundOnOddRows(true)
                .setDefaultStyles(TITLE_XLS_STYLE, DEFAULT_XLS_STYLE, DEFAULT_XLS_STYLE, DEFAULT_XLS_STYLE)
                .setPageSizeAndOrientation(Page_xls())

                .setPrintColumnNames(true)
                .setHeaderHeight(20)

                .addStyle(DEFAULT_STYLE)
                .addStyle(DEFAULT_XLS_STYLE)
                .addStyle(DEFAULT_GRID_STYLE)
                .addStyle(DEFAULT_GRID_XLS_STYLE)
//                .addStyle(HEADER_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addStyle(WORK_HOUR_GRID_STYLE)
                .addStyle(WORK_HOUR_GRID_XLS_STYLE)
//                .setGrandTotalLegend("National Total")

                // Add columns
                .addColumn(ckzTextRepCol)
                .addColumn(prijmeniCol)
                .addColumn(ymPruhCol)
                .addColumn(workPruhCol)
                .addColumn(naklMzdaCol)
                .addColumn(naklMzdaPojistCol)
                .addColumn(sazbaCol)
                .addColumn(koefP8Col)
                .addColumn(workPruhP8Col)
                .addColumn(naklMzdaP8Col)
                .addColumn(naklMzdaPojistP8Col)

                // Add groups
                .addGroup(zakGroup)
                .addGroup(userGroup)

                // Add totals
                .setGrandTotalLegend("Nagruzka Total")
//                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE)
                .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE)
                .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
        ;
    }

    private void buildReportColumns() {
        ckzTextRepCol = ColumnBuilder.getNew()
                .setColumnProperty("ckzTextRep", String.class)
//                .setCustomExpression(getCalcZakId())
//                .setCustomExpressionForCalculation(getCalcZakId2())
                .setTitle("Zakázka")
                .setStyle(GROUP_HEADER_ZAK_XLS_STYLE)
//                .setWidth(110)
//                .setFixedWidth(true)
                .build();

        prijmeniCol = ColumnBuilder.getNew()
                .setColumnProperty("prijmeni", String.class)
                .setTitle("Příjmení")
                .setStyle(GROUP_HEADER_USER_XLS_STYLE)
                .setWidth(110)
                .setFixedWidth(true)
                .build();

        ymPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("ymPruh", YearMonth.class)
                .setTitle("Rok-Měs")
                .setStyle(YM_GRID_XLS_STYLE)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .setWidth(110)
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
                .setTitle("Mzda [CZK]")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzda + Poj.")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzda P8")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
                .setTitle("Mzda + P. P8")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(MONEY_GRID_XLS_STYLE)
                .setWidth(60)
                .setFixedWidth(true)
                .build();

        koefP8Col = ColumnBuilder.getNew()
                .setColumnProperty("koefP8", BigDecimal.class)
                .setTitle("Koef P8")
                .setStyle(MONEY_GRID_XLS_STYLE)
                .setWidth(60)
                .setFixedWidth(true)
                .build();
    }

    private void buildReportGroups() {

        // User group
        GroupBuilder userGroupBuilder = new GroupBuilder();
        DJGroupLabel userGroupWorkHourLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupMzdaLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupWorkMzdaPojistLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        userGroup = userGroupBuilder
                .setCriteriaColumn((PropertyColumn) prijmeniCol)
                .addHeaderVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE, null, userGroupWorkHourLabel)
                .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, userGroupMzdaLabel)
                .addHeaderVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, userGroupWorkMzdaPojistLabel)
                .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_GRID_XLS_STYLE, null, null)
                .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, null)
                .addHeaderVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE, null, null)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
//                .setGroupLayout(GroupLayout.DEFAULT)
                .build()
        ;


        // ZAK group
        GroupBuilder zakGroupBuilder = new GroupBuilder();
        zakGroup = zakGroupBuilder
                .setCriteriaColumn((PropertyColumn) ckzTextRepCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;
    }

    public DynamicReport buildReport()  {
        return reportBuilder.build();
    }
}
