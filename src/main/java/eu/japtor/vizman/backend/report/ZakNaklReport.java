package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import org.vaadin.reports.PrintPreviewReport;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Map;

public class ZakNaklReport extends PrintPreviewReport {

    static final Color TOTAL_BG_COLOR = new Color(0xFFF4EE);
    static final Color GROUP_SUM_BG_COLOR = new Color(0xEEF4FF);
    static final Color HEADER_BG_COLOR = new Color(0xF4F4F4);

    static final Font ARIAL_MEDIUM_XL = new Font(11, Font._FONT_ARIAL, false, false, false);
    static final Font ARIAL_MEDIUM_XL_BOLD = new Font(11, Font._FONT_ARIAL, true, false, false);
    static final Font ARIAL_MEDIUM_XXL_BOLD = new Font(12, Font._FONT_ARIAL, true, false, false);
    static final Font DEFAULT_FONT_PDF = Font.ARIAL_MEDIUM;
    static final Font HEADER_FONT_PDF = ARIAL_MEDIUM_XL_BOLD;
//    static final Font GROUP_HEADER_ZAK_FONT_PDF = Font.ARIAL_MEDIUM_BOLD;
    static final Font GROUP_HEADER_ZAK_FONT_PDF = ARIAL_MEDIUM_XL_BOLD;
    static final Font GROUP_HEADER_USER_FONT_PDF = ARIAL_MEDIUM_XL_BOLD;
    static final Font SUBTITLE_FONT_PDF = ARIAL_MEDIUM_XL;
    static final Font TITLE_FONT_PDF = Font.ARIAL_BIG_BOLD;
    static final Style DEFAULT_STYLE;
    static final Style DEFAULT_GRID_STYLE;
    static final Style TITLE_STYLE;
    static final Style SUBTITLE_STYLE;
    static final Style HEADER_STYLE;
    static final Style GROUP_HEADER_ZAK_STYLE;
    static final Style GROUP_HEADER_USER_STYLE;
    static final Style GROUP_LABEL_STYLE;
    static final Style WORK_HOUR_STYLE;
    static final Style YM_STYLE;
    static final Style WORK_HOUR_SUM_STYLE;
    static final Style WORK_HOUR_TOT_STYLE;
    static final Style MONEY_STYLE;
    static final Style MONEY_NO_FRACT_STYLE;
    static final Style MONEY_NO_FRACT_SUM_STYLE;
    static final Style MONEY_NO_FRACT_TOT_STYLE;
    static final Style PERCENT_STYLE;
    static {
        DEFAULT_FONT_PDF.setPdfFontEmbedded(true);
        DEFAULT_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        DEFAULT_FONT_PDF.setPdfFontName("/Windows/Fonts/arial.ttf");

        HEADER_FONT_PDF.setPdfFontEmbedded(true);
        HEADER_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        HEADER_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        SUBTITLE_FONT_PDF.setPdfFontEmbedded(true);
        SUBTITLE_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        SUBTITLE_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");


        TITLE_FONT_PDF.setPdfFontEmbedded(true);
        TITLE_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        TITLE_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        DEFAULT_STYLE = new StyleBuilder(false, "default-style")
                .setFont(DEFAULT_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .build();

        DEFAULT_GRID_STYLE = new StyleBuilder(false, "default-grid-style")
                .setFont(DEFAULT_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingLeft(Integer.valueOf(3))
                .setPaddingRight(Integer.valueOf(5))
                .setBorder(Border.THIN())
                .build();

        TITLE_STYLE = new StyleBuilder(true,"title-style")
                .setParentStyleName("default-style")
                .setFont(TITLE_FONT_PDF)
                .build();

        SUBTITLE_STYLE = new StyleBuilder(true, "subtitle-style")
                .setParentStyleName("default-style")
                .setFont(SUBTITLE_FONT_PDF)
                .setPaddingTop(8)
                .setPaddingBottom(6)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .build();

        HEADER_STYLE = new StyleBuilder(true, "header-style")
                .setParentStyleName("default-grid-style")
                .setFont(HEADER_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.CENTER)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .setStretchWithOverflow(true)
                .setStretching(Stretching.RELATIVE_TO_BAND_HEIGHT)
                .setBackgroundColor(HEADER_BG_COLOR)
                .setTransparent(false)
                .build();

        GROUP_HEADER_ZAK_STYLE = new StyleBuilder(false, "group-header-zak-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
                .setPaddingTop(8)
                .setPaddingBottom(2)
                .setFont(GROUP_HEADER_ZAK_FONT_PDF)
                .build();

        GROUP_HEADER_USER_STYLE = new StyleBuilder(false, "group-header-user-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
                .setFont(GROUP_HEADER_USER_FONT_PDF)
                .build();

        GROUP_LABEL_STYLE = new StyleBuilder(false, "group-label-style")
                .setFont(Font.ARIAL_MEDIUM)
                .build();

        YM_STYLE = new StyleBuilder(true, "ym-grid-style")
                .setParentStyleName("default-grid-style")
                .setPaddingLeft(20)
                .build();

        WORK_HOUR_STYLE = new StyleBuilder(true, "work-hour-style")
                .setParentStyleName("default-grid-style")
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0.0;-#,##0.0")
                .build();

        WORK_HOUR_SUM_STYLE = new StyleBuilder(true, "work-hour-sum-style")
                .setParentStyleName("work-hour-style")
                .setFont(ARIAL_MEDIUM_XL_BOLD)
                .setBackgroundColor(GROUP_SUM_BG_COLOR)
                .setTransparent(false)
                .build()
        ;

        WORK_HOUR_TOT_STYLE = new StyleBuilder(true, "work-hour-tot-style")
                .setParentStyleName("work-hour-style")
                .setFont(ARIAL_MEDIUM_XL_BOLD)
                .setBackgroundColor(TOTAL_BG_COLOR)
                .setTransparent(false)
                .build()
        ;

        MONEY_NO_FRACT_STYLE = new StyleBuilder(true, "money-no-fract-style")
                .setParentStyleName("default-grid-style")
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0;-#,##0")
                .build()
        ;

        MONEY_NO_FRACT_SUM_STYLE = new StyleBuilder(true, "money-no-fract-sum-style")
                .setParentStyleName("money-no-fract-style")
                .setFont(ARIAL_MEDIUM_XL_BOLD)
                .setBackgroundColor(GROUP_SUM_BG_COLOR)
                .setTransparent(false)
                .build();

        MONEY_NO_FRACT_TOT_STYLE = new StyleBuilder(true, "money-no-fract-tot-style")
                .setParentStyleName("money-no-fract-style")
                .setFont(ARIAL_MEDIUM_XL_BOLD)
                .setBackgroundColor(TOTAL_BG_COLOR)
                .setTransparent(false)
                .build();

        MONEY_STYLE = new StyleBuilder(true, "money-style")
                .setParentStyleName("default-grid-style")
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        PERCENT_STYLE = new StyleBuilder(true, "percent-style")
                .setParentStyleName("default-grid-style")
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("##0;-##0")
                .build();
    }

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
                .addStyle(MONEY_NO_FRACT_STYLE)
                .addStyle(WORK_HOUR_STYLE)

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
                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_TOT_STYLE)
                .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_STYLE)
                .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_TOT_STYLE)
                .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_TOT_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_TOT_STYLE)
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
                .setStyle(YM_STYLE)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .setWidth(110)
//                .setFixedWidth(true)
                .build();

        workPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("workPruh", BigDecimal.class)
                .setTitle("Hodin")
                .setStyle(WORK_HOUR_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        workPruhP8Col = ColumnBuilder.getNew()
                .setColumnProperty("workPruhP8", BigDecimal.class)
                .setTitle("Hodin P8")
                .setStyle(WORK_HOUR_STYLE)
                .setWidth(60)
//                .setFixedWidth(true)
                .build();

        naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzda [CZK]")
                .setStyle(MONEY_NO_FRACT_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzda + Poj.")
                .setStyle(MONEY_NO_FRACT_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaP8", BigDecimal.class)
                .setTitle("Mzda P8")
                .setStyle(MONEY_NO_FRACT_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        naklMzdaPojistP8Col = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojistP8", BigDecimal.class)
                .setTitle("Mzda + P. P8")
                .setStyle(MONEY_NO_FRACT_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();

        sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(MONEY_STYLE)
                .setWidth(60)
                .setFixedWidth(true)
                .build();

        koefP8Col = ColumnBuilder.getNew()
                .setColumnProperty("koefP8", BigDecimal.class)
                .setTitle("Koef P8")
                .setStyle(MONEY_STYLE)
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
                .addHeaderVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_SUM_STYLE, null, userGroupWorkHourLabel)
                .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_SUM_STYLE, null, userGroupMzdaLabel)
                .addHeaderVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_SUM_STYLE, null, userGroupWorkMzdaPojistLabel)
                .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_SUM_STYLE, null, null)
                .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_SUM_STYLE, null, null)
                .addHeaderVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_SUM_STYLE, null, null)
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
