package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import org.vaadin.reports.PrintPreviewReport;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class ZakNaklReport extends PrintPreviewReport {


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


    public ZakNaklReport() {
        super();

        buildReportColumns();
        buildReportGroups();

        this.getReportBuilder()
                .setTitle("NÁKLADY NA ZAKÁZKU")
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
//                .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                .setUseFullPageWidth(false)
//                .setSubtitleHeight(200)
                .setPrintColumnNames(true)
                .setHeaderHeight(20)

//                .setSubtitle("Text kontraktu... / Text zakázky...")
                .setReportLocale(new Locale("cs", "CZ"))
//                .setSubtitle("Rok: " + paramRokStr)
//                .addParameter("PARAM_ROK", String.class.getName())
                .setMargins(20, 10, 25, 20)
//                .setPrintBackgroundOnOddRows(true)
                .setDefaultStyles(TITLE_STYLE, SUBTITLE_STYLE, HEADER_STYLE, DEFAULT_STYLE)

//                .addAutoText("Rok: " + "\" + $P{PARAM_ROK} + \""
//                        , AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 80, DEFAULT_STYLE)
//                .addAutoText("For internal use only"
//                        , AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 200, DEFAULT_STYLE)
//                .addAutoText(LocalDateTime.now().toString())
//                .addAutoText(AutoText.AUTOTEXT_CREATED_ON
//                        , AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT)
                .addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y
                        , AutoText.POSITION_HEADER, AutoText.ALIGMENT_CENTER)
//                        , AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 200, 200, DEFAULT_STYLE)

                // Add styles (needed only styles used as parent)
                .addStyle(DEFAULT_STYLE)
                .addStyle(DEFAULT_GRID_STYLE)
                .addStyle(HEADER_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_STYLE)
                .addStyle(WORK_HOUR_GRID_STYLE)

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
                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE)
                .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
                .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE)
                .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
        ;
    }


    private void buildReportColumns() {

        ckzTextRepCol = ColumnBuilder.getNew()
                .setColumnProperty("ckzTextRep", String.class)
//                .setCustomExpression(getCalcZakId())
//                .setCustomExpressionForCalculation(getCalcZakId2())
                .setTitle("Zakázka")
                .setStyle(GROUP_HEADER_ZAK_STYLE)
//                .setWidth(110)
//                .setFixedWidth(true)
                .build();

        prijmeniCol = ColumnBuilder.getNew()
                .setColumnProperty("prijmeni", String.class)
                .setTitle("Příjmení")
                .setStyle(GROUP_HEADER_USER_STYLE)
                .setWidth(110)
                .setFixedWidth(true)
                .build();

        ymPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("ymPruh", YearMonth.class)
                .setTitle("Rok-Měs")
                .setStyle(YM_GRID_STYLE)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .setWidth(110)
//                .setFixedWidth(true)
                .build();

        workPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("workPruh", BigDecimal.class)
                .setTitle("Hodin")
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        workPruhP8Col = ColumnBuilder.getNew()
                .setColumnProperty("workPruhP8", BigDecimal.class)
                .setTitle("Hodin P8")
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzda [CZK]")
                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzda + Poj.")
                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzda P8")
                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
                .setTitle("Mzda + P. P8")
                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(MONEY_GRID_STYLE)
                .setWidth(60)
                .setFixedWidth(true)
                .build();

        koefP8Col = ColumnBuilder.getNew()
                .setColumnProperty("koefP8", BigDecimal.class)
                .setTitle("Koef P8")
                .setStyle(MONEY_GRID_STYLE)
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
                .addHeaderVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_SUM_GRID_STYLE, null, userGroupWorkHourLabel)
                .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, userGroupMzdaLabel)
                .addHeaderVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, userGroupWorkMzdaPojistLabel)
                .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_SUM_GRID_STYLE, null, null)
                .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, null)
                .addHeaderVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, null)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
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


    public void setSubtitleText(String subtitleText) {
        this.getReportBuilder().setSubtitle(subtitleText);
    }


    // Following expressions should work, but there is probbably a bug in Vaadin component
    private CustomExpression getCalcZakId() {
        return new CustomExpression() {
            @Override
            public Object evaluate(Map fields, Map variables, Map parameters) {
                Long zakId = (Long)fields.get("zakId");
                return zakId.toString();
            }
            @Override
            public String getClassName() {
                return String.class.getName();
            }
        };
    }

    private CustomExpression getCalcZakId2() {
        return new CustomExpression() {
            @Override
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return fields.get("zakId");
            }
            @Override
            public String getClassName() {
                return Long.class.getName();
            }
        };
    }
}
