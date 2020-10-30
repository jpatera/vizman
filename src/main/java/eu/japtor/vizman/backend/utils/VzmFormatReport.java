package eu.japtor.vizman.backend.utils;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.Entity;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import static ar.com.fdvs.dj.domain.constants.Font.ARIAL_MEDIUM_BOLD;

public class VzmFormatReport {
    static final Color TOTAL_BG_COLOR = new Color(0xFFF4EE);
    static final Color GROUP_SUM_BG_COLOR = new Color(0xEEF4FF);
    static final Color HEADER_BG_COLOR = new Color(0xF4F4F4);

    //  Report file name date formatter
    public static final DateTimeFormatter RFNDF = DateTimeFormatter.ofPattern("__yyyy_MM_dd__HH_mm_ss");

    public static final ar.com.fdvs.dj.domain.constants.Font ARIAL_XL = new ar.com.fdvs.dj.domain.constants.Font(11, ar.com.fdvs.dj.domain.constants.Font._FONT_ARIAL, false, false, false);
    public static final ar.com.fdvs.dj.domain.constants.Font ARIAL_XL_BOLD = new ar.com.fdvs.dj.domain.constants.Font(11, ar.com.fdvs.dj.domain.constants.Font._FONT_ARIAL, true, false, false);
    public static final ar.com.fdvs.dj.domain.constants.Font ARIAL_XXL_BOLD = new ar.com.fdvs.dj.domain.constants.Font(13, ar.com.fdvs.dj.domain.constants.Font._FONT_ARIAL, true, false, false);

    //    public static final ar.com.fdvs.dj.domain.constants.Font DEFAULT_FONT_PDF = ar.com.fdvs.dj.domain.constants.Font.ARIAL_MEDIUM;
    public static final ar.com.fdvs.dj.domain.constants.Font DEFAULT_FONT_PDF = Font.ARIAL_SMALL;
    public static final ar.com.fdvs.dj.domain.constants.Font DEFAULT_FONT_XLS = Font.ARIAL_SMALL;

    //    public static final ar.com.fdvs.dj.domain.constants.Font HEADER_FONT_PDF = ARIAL_XL_BOLD;
    // public static final ar.com.fdvs.dj.domain.constants.Font HEADER_FONT_PDF = ARIAL_XL;
    public static final ar.com.fdvs.dj.domain.constants.Font HEADER_FONT_PDF = Font.ARIAL_SMALL_BOLD;

    //    public static final Font GROUP_HEADER_ZAK_FONT_PDF = Font.ARIAL_MEDIUM_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font GROUP_HEADER_FONT_PDF = ARIAL_XL_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font GROUP_HEADER_ZAK_FONT_PDF = ARIAL_XL_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font GROUP_HEADER_USER_FONT_PDF = ARIAL_XL_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font GROUP_HEADER_WEEK_FONT_PDF = ARIAL_XL_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font SUBTITLE_FONT_PDF = ARIAL_XL;
    public static final ar.com.fdvs.dj.domain.constants.Font TITLE_FONT_PDF = ar.com.fdvs.dj.domain.constants.Font.ARIAL_BIG_BOLD;

    public static final String DEFAULT_STYLE_NAME;
    public static final Style DEFAULT_STYLE;

//    public static final String DEFAULT_XLS_STYLE_NAME;
//    public static final Style DEFAULT_XLS_STYLE;

    public static final Style DEFAULT_GRID_STYLE;
    public static final String DEFAULT_GRID_STYLE_NAME;
    public static final Style CENTER_GRID_STYLE;
    public static final String CENTER_GRID_STYLE_NAME;

//    public static final Style DEFAULT_GRID_XLS_STYLE;
//    public static final String DEFAULT_GRID_XLS_STYLE_NAME;
    public static final Style DEFAULT_GRID_XLS_TEXT_STYLE;
    public static final String DEFAULT_GRID_XLS_TEXT_STYLE_NAME;
    public static final Style DEFAULT_GRID_XLS_NUM_STYLE;
    public static final String DEFAULT_GRID_XLS_NUM_STYLE_NAME;
//    public static final Style CENTER_GRID_XLS_STYLE;
//    public static final String CENTER_GRID_XLS_STYLE_NAME;

    public static final Style TITLE_STYLE;
    public static final Style TITLE_XLS_STYLE;
    public static final Style XXL_STYLE;
    public static final Style SUBTITLE_STYLE;

    public static final Style HEADER_STYLE;
    public static final Style GROUP_HEADER_STYLE;
    public static final Style GROUP_HEADER_ZAK_STYLE;
    public static final Style GROUP_HEADER_ZAK_XLS_STYLE;
    public static final Style GROUP_HEADER_USER_STYLE;
    public static final Style GROUP_HEADER_USER_XLS_STYLE;
    public static final Style GROUP_HEADER_DOCH_PERSON_STYLE;
    public static final Style GROUP_HEADER_WEEK_STYLE;
    public static final Style GROUP_LABEL_STYLE;

    public static final String WORK_HOUR_GRID_STYLE_NAME;
    public static final Style WORK_HOUR_GRID_STYLE;
    public static final String WORK_HOUR_GRID_XLS_STYLE_NAME;
    public static final Style WORK_HOUR_GRID_XLS_STYLE;
//    public static final String WORK_HOUR_GRID_XLS_STYLE_NAME;
//    public static final Style WORK_HOUR_GRID_XLS_STYLE;
    public static final Style WORK_HOUR_SUM_GRID_STYLE;
    public static final Style WORK_HOUR_TOT_GRID_STYLE;

    public static final String MONEY_NO_FRACT_GRID_STYLE_NAME;
    public static final Style MONEY_NO_FRACT_GRID_STYLE;
    public static final String MONEY_NO_FRACT_GRID_XLS_STYLE_NAME;
    public static final Style MONEY_NO_FRACT_GRID_XLS_STYLE;
//    public static final String MONEY_NO_FRACT_GRID_XLS_STYLE_NAME;
//    public static final Style MONEY_NO_FRACT_GRID_XLS_STYLE;
    public static final Style MONEY_NO_FRACT_SUM_GRID_STYLE;
    public static final Style MONEY_NO_FRACT_TOT_GRID_STYLE;

    public static final Style INT_GRID_STYLE;
    public static final Style INT_GRID_XLS_STYLE;
    public static final Style YM_GRID_STYLE;
//    public static final Style YM_GRID_XLS_STYLE;
    public static final Style PROC_GRID_STYLE;
    public static final Style PROC_GRID_XLS_STYLE;
    public static final Style PERCENT_GRID_STYLE;

    public static final Style MONEY_GRID_STYLE;
//    public static final Style MONEY_GRID_XLS_STYLE;
    public static final Style SHORT_DATE_GRID_STYLE;
//    public static final Style SHORT_DATE_WEEKEND_GRID_STYLE;
    public static final Style WEEKEND_BLACK_GRID_STYLE;
    public static final Style WEEKEND_RED_GRID_STYLE;
    public static final Style WEEKEND_MAGENTA_GRID_STYLE;
    public static final Style WORKDAY_BLACK_GRID_STYLE;
    public static final Style WORKDAY_RED_GRID_STYLE;
    public static final Style WORKDAY_MAGENTA_GRID_STYLE;
    public static final Style WORKDAY_GRID_STYLE;
    public static final Style WEEKEND_GRID_STYLE;
    public static final Style BLACK_STYLE;
    public static final Style RED_STYLE;

//    public static ArrayList condWeekendDateStyles = new ArrayList();
    public static ArrayList condWeekendSluzDateStyles = new ArrayList();
    public static ArrayList condWeekendFromStyles = new ArrayList();
    public static ArrayList condWeekendToStyles = new ArrayList();
    public static ArrayList condToStyles = new ArrayList();
    public static ArrayList condObedStyles = new ArrayList();

    static {
        DEFAULT_FONT_PDF.setPdfFontEmbedded(true);
        DEFAULT_FONT_PDF.setPdfFontEncoding(ar.com.fdvs.dj.domain.constants.Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        DEFAULT_FONT_PDF.setPdfFontName("/Windows/Fonts/arial.ttf");

        HEADER_FONT_PDF.setPdfFontEmbedded(true);
        HEADER_FONT_PDF.setPdfFontEncoding(ar.com.fdvs.dj.domain.constants.Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        HEADER_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        SUBTITLE_FONT_PDF.setPdfFontEmbedded(true);
        SUBTITLE_FONT_PDF.setPdfFontEncoding(ar.com.fdvs.dj.domain.constants.Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        SUBTITLE_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");


        TITLE_FONT_PDF.setPdfFontEmbedded(true);
        TITLE_FONT_PDF.setPdfFontEncoding(ar.com.fdvs.dj.domain.constants.Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        TITLE_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        DEFAULT_STYLE_NAME = "default-style";
        DEFAULT_STYLE = new StyleBuilder(false, "default-style")
                .setFont(DEFAULT_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .build();

//        DEFAULT_XLS_STYLE_NAME = "default-xls-style";
//        DEFAULT_XLS_STYLE = new StyleBuilder(false, DEFAULT_XLS_STYLE_NAME)
//                .setFont(DEFAULT_FONT_XLS)
////                .setHorizontalAlign(HorizontalAlign.LEFT)
//                .build();

        DEFAULT_GRID_STYLE_NAME = "default-grid-style";
        DEFAULT_GRID_STYLE = new StyleBuilder(false, DEFAULT_GRID_STYLE_NAME)
                .setFont(DEFAULT_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingLeft(3)
                .setPaddingRight(5)
                .setBorder(Border.THIN())
                .build();

        CENTER_GRID_STYLE_NAME = "center-grid-style";
        CENTER_GRID_STYLE = new StyleBuilder(false, CENTER_GRID_STYLE_NAME)
                .setFont(DEFAULT_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.CENTER)
                .setPaddingLeft(3)
                .setPaddingRight(5)
                .setBorder(Border.THIN())
                .build();

//        DEFAULT_GRID_XLS_STYLE_NAME = "default-grid-xls-style";
//        DEFAULT_GRID_XLS_STYLE = new StyleBuilder(false, DEFAULT_GRID_XLS_STYLE_NAME)
////                .setFont(DEFAULT_FONT_XLS)
////                .setHorizontalAlign(HorizontalAlign.LEFT)
////                .setPaddingLeft(3)
////                .setPaddingRight(5)
////                .setBorder(Border.THIN())
//                .build();

        DEFAULT_GRID_XLS_TEXT_STYLE_NAME = "default-grid-xls-text-style";
        DEFAULT_GRID_XLS_TEXT_STYLE = new StyleBuilder(false, DEFAULT_GRID_XLS_TEXT_STYLE_NAME)
//                .setFont(DEFAULT_FONT_XLS)
//                .setHorizontalAlign(HorizontalAlign.LEFT)
//                .setPaddingLeft(3)
//                .setPaddingRight(5)
//                .setBorder(Border.THIN())
                .build();

        DEFAULT_GRID_XLS_NUM_STYLE_NAME = "default-grid-xls-num-style";
        DEFAULT_GRID_XLS_NUM_STYLE = new StyleBuilder(false, DEFAULT_GRID_XLS_NUM_STYLE_NAME)
//                .setFont(DEFAULT_FONT_XLS)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
//                .setPaddingLeft(3)
//                .setPaddingRight(5)
//                .setBorder(Border.THIN())
                .build();

//        CENTER_GRID_XLS_STYLE_NAME = "center-grid-xls-style";
//        CENTER_GRID_XLS_STYLE = new StyleBuilder(false, CENTER_GRID_XLS_STYLE_NAME)
//                .setFont(DEFAULT_FONT_XLS)
//                .setHorizontalAlign(HorizontalAlign.CENTER)
//                .setPaddingLeft(3)
//                .setPaddingRight(5)
////                .setBorder(Border.THIN())
//                .build();

        TITLE_STYLE = new StyleBuilder(true,"title-style")
                .setParentStyleName(DEFAULT_STYLE_NAME)
                .setFont(TITLE_FONT_PDF)
                .build();

        TITLE_XLS_STYLE = new StyleBuilder(true,"title-xls-style")
                .setParentStyleName(DEFAULT_STYLE_NAME)
//                .setFont(TITLE_FONT_PDF)
                .build();

        XXL_STYLE = new StyleBuilder(true,"xxl-style")
                .setParentStyleName(DEFAULT_STYLE_NAME)
                .setFont(ARIAL_XXL_BOLD)
                .build();

        SUBTITLE_STYLE = new StyleBuilder(true, "subtitle-style")
                .setParentStyleName(DEFAULT_STYLE_NAME)
                .setFont(SUBTITLE_FONT_PDF)
                .setPaddingTop(8)
                .setPaddingBottom(6)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .build();

        HEADER_STYLE = new StyleBuilder(true, "header-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setFont(HEADER_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.CENTER)
                .setVerticalAlign(VerticalAlign.MIDDLE)
//                .setStretchWithOverflow(true)
//                .setStretching(Stretching.RELATIVE_TO_BAND_HEIGHT)
                .setBackgroundColor(HEADER_BG_COLOR)
                .setTransparent(false)
                .build();

        GROUP_HEADER_STYLE = new StyleBuilder(false, "group-header-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
//                .setPaddingTop(8)
//                .setPaddingBottom(124)
                .setStretching(Stretching.RELATIVE_TO_BAND_HEIGHT)
                .setFont(GROUP_HEADER_FONT_PDF)
                .build();

        GROUP_HEADER_ZAK_STYLE = new StyleBuilder(false, "group-header-zak-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
                .setPaddingTop(8)
                .setPaddingBottom(2)
                .setFont(GROUP_HEADER_ZAK_FONT_PDF)
                .build();

        GROUP_HEADER_ZAK_XLS_STYLE = new StyleBuilder(false, "group-header-zak-xls-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
                .setPaddingTop(8)
                .setPaddingBottom(2)
                .setFont(DEFAULT_FONT_XLS)
                .build();

        GROUP_HEADER_USER_STYLE = new StyleBuilder(false, "group-header-user-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
                .setFont(GROUP_HEADER_USER_FONT_PDF)
                .build();

        GROUP_HEADER_USER_XLS_STYLE = new StyleBuilder(false, "group-header-user-xls-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setBorder(Border.NO_BORDER())
                .setFont(DEFAULT_FONT_XLS)
                .build();

        GROUP_HEADER_DOCH_PERSON_STYLE = new StyleBuilder(false, "group-header-doch-person-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER())
                .setFont(GROUP_HEADER_USER_FONT_PDF)
                .build();

        GROUP_HEADER_WEEK_STYLE = new StyleBuilder(false, "group-header-week-style")
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingTop(8)
                .setBorder(Border.NO_BORDER())
                .setFont(GROUP_HEADER_WEEK_FONT_PDF)
                .setPattern("0 týden")
                .build();

        GROUP_LABEL_STYLE = new StyleBuilder(false, "group-label-style")
                .setFont(Font.ARIAL_MEDIUM)
                .build();

        YM_GRID_STYLE = new StyleBuilder(true, "ym-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setPaddingLeft(20)
                .build();

//        YM_GRID_XLS_STYLE = new StyleBuilder(true, "ym-grid-xls-style")
//                .setParentStyleName(DEFAULT_GRID_XLS_STYLE_NAME)
//                .setPaddingLeft(20)
//                .build();

        INT_GRID_STYLE = new StyleBuilder(true, "ym-grid-int-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .build();

        INT_GRID_XLS_STYLE = new StyleBuilder(true, "ym-grid-xls-int-style")
                .setParentStyleName(DEFAULT_GRID_XLS_NUM_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .build();

        PROC_GRID_STYLE = new StyleBuilder(true, "proc-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(3)
                .setPattern("##0;-##0")
                .build();

        PROC_GRID_XLS_STYLE = new StyleBuilder(true, "proc-grid-xls-style")
                .setParentStyleName(DEFAULT_GRID_XLS_NUM_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
//                .setPaddingRight(3)
                .setPattern("##0;-##0")
                .build();

        SHORT_DATE_GRID_STYLE = new StyleBuilder(true, "short-date-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.CENTER)
                .setPaddingRight(3)
                .setTransparent(true)
//                .setPattern("dd.MM")
                .build();

        WEEKEND_BLACK_GRID_STYLE = new Style();
        WEEKEND_BLACK_GRID_STYLE.setTextColor(Color.BLACK);
        WEEKEND_BLACK_GRID_STYLE.setBackgroundColor(new Color(0xFFFACD));
        WEEKEND_BLACK_GRID_STYLE.setTransparent(false);

        WEEKEND_RED_GRID_STYLE = new Style();
        WEEKEND_RED_GRID_STYLE.setTextColor(Color.RED);
        WEEKEND_RED_GRID_STYLE.setBackgroundColor(new Color(0xFFFACD));
        WEEKEND_RED_GRID_STYLE.setTransparent(false);

        WEEKEND_MAGENTA_GRID_STYLE = new Style();
        WEEKEND_MAGENTA_GRID_STYLE.setTextColor(Color.MAGENTA);
        WEEKEND_MAGENTA_GRID_STYLE.setBackgroundColor(new Color(0xFFFACD));
        WEEKEND_MAGENTA_GRID_STYLE.setTransparent(false);

        WORKDAY_BLACK_GRID_STYLE = new Style();
        WORKDAY_BLACK_GRID_STYLE.setTextColor(Color.BLACK);
        WORKDAY_BLACK_GRID_STYLE.setBackgroundColor(Color.WHITE);
        WORKDAY_BLACK_GRID_STYLE.setTransparent(false);

        WORKDAY_RED_GRID_STYLE = new Style();
        WORKDAY_RED_GRID_STYLE.setTextColor(Color.RED);
        WORKDAY_RED_GRID_STYLE.setBackgroundColor(Color.WHITE);
        WORKDAY_RED_GRID_STYLE.setTransparent(false);

        WORKDAY_MAGENTA_GRID_STYLE = new Style();
        WORKDAY_MAGENTA_GRID_STYLE.setTextColor(Color.MAGENTA);
        WORKDAY_MAGENTA_GRID_STYLE.setBackgroundColor(Color.WHITE);
        WORKDAY_MAGENTA_GRID_STYLE.setTransparent(false);

        WORKDAY_GRID_STYLE = new Style();
        WORKDAY_GRID_STYLE.setBackgroundColor(Color.WHITE);
        WORKDAY_GRID_STYLE.setTransparent(false);

        WEEKEND_GRID_STYLE = new Style();
        WEEKEND_GRID_STYLE.setBackgroundColor(new Color(0xFFFACD));
        WEEKEND_GRID_STYLE.setTransparent(false);

//        condWeekendDateStyles.add(new ConditionalStyle(new WeekendCondition(false), WORKDAY_GRID_STYLE));
//        condWeekendDateStyles.add(new ConditionalStyle(new WeekendCondition(true), WEEKEND_GRID_STYLE));
        condWeekendSluzDateStyles.add(new ConditionalStyle(new WeekendSluzDateCondition(false, false), WORKDAY_BLACK_GRID_STYLE));
        condWeekendSluzDateStyles.add(new ConditionalStyle(new WeekendSluzDateCondition(false, true), WORKDAY_MAGENTA_GRID_STYLE));
        condWeekendSluzDateStyles.add(new ConditionalStyle(new WeekendSluzDateCondition(true, false), WEEKEND_BLACK_GRID_STYLE));
        condWeekendSluzDateStyles.add(new ConditionalStyle(new WeekendSluzDateCondition(true, true), WEEKEND_MAGENTA_GRID_STYLE));


        BLACK_STYLE = new Style();
        BLACK_STYLE.setTextColor(Color.BLACK);
//        BLACK_STYLE.setTransparent(false);

        RED_STYLE = new Style();
        RED_STYLE.setTextColor(Color.RED);
//        RED_STYLE.setTransparent(false);

//        condWeekendFromStyles.add(new ConditionalStyle(new FromManualCondition(false), RED_STYLE));
//        condWeekendFromStyles.add(new ConditionalStyle(new FromManualCondition(true), BLACK_STYLE));
        condWeekendFromStyles.add(new ConditionalStyle(new WeekendFromCondition(false, false), WORKDAY_BLACK_GRID_STYLE));
        condWeekendFromStyles.add(new ConditionalStyle(new WeekendFromCondition(false, true), WORKDAY_RED_GRID_STYLE));
        condWeekendFromStyles.add(new ConditionalStyle(new WeekendFromCondition(true, false), WEEKEND_BLACK_GRID_STYLE));
        condWeekendFromStyles.add(new ConditionalStyle(new WeekendFromCondition(true, true), WEEKEND_RED_GRID_STYLE));

        condWeekendToStyles.add(new ConditionalStyle(new WeekendToCondition(false, false), WORKDAY_BLACK_GRID_STYLE));
        condWeekendToStyles.add(new ConditionalStyle(new WeekendToCondition(false, true), WORKDAY_RED_GRID_STYLE));
        condWeekendToStyles.add(new ConditionalStyle(new WeekendToCondition(true, false), WEEKEND_BLACK_GRID_STYLE));
        condWeekendToStyles.add(new ConditionalStyle(new WeekendToCondition(true, true), WEEKEND_RED_GRID_STYLE));

        WORK_HOUR_GRID_STYLE_NAME = "work-hour-grid-style";
        WORK_HOUR_GRID_STYLE = new StyleBuilder(true, "work-hour-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0.0;-#,##0.0")
                .build();

        WORK_HOUR_GRID_XLS_STYLE_NAME = "work-hour-grid-xls-style";
        WORK_HOUR_GRID_XLS_STYLE = new StyleBuilder(true, "work-hour-grid-xls-style")
                .setParentStyleName(DEFAULT_GRID_XLS_NUM_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("##0.0;-##0.0")
                .build();

        WORK_HOUR_SUM_GRID_STYLE = new StyleBuilder(true, "work-hour-sum-grid-style")
                .setParentStyleName(WORK_HOUR_GRID_STYLE_NAME)
                .setFont(ARIAL_MEDIUM_BOLD)
                .setBackgroundColor(GROUP_SUM_BG_COLOR)
                .setTransparent(false)
                .build()
        ;

        WORK_HOUR_TOT_GRID_STYLE = new StyleBuilder(true, "work-hour-tot-grid-style")
                .setParentStyleName(WORK_HOUR_GRID_STYLE_NAME)
                .setFont(ARIAL_MEDIUM_BOLD)
                .setBackgroundColor(TOTAL_BG_COLOR)
                .setTransparent(false)
                .build()
        ;

        MONEY_NO_FRACT_GRID_STYLE_NAME =  "money-no-fract-grid-style";
        MONEY_NO_FRACT_GRID_STYLE = new StyleBuilder(true, MONEY_NO_FRACT_GRID_STYLE_NAME)
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0;-#,##0")
                .build()
        ;

        MONEY_NO_FRACT_GRID_XLS_STYLE_NAME =  "money-no-fract-grid-xls-style";
        MONEY_NO_FRACT_GRID_XLS_STYLE = new StyleBuilder(true, MONEY_NO_FRACT_GRID_XLS_STYLE_NAME)
                .setParentStyleName(DEFAULT_GRID_XLS_NUM_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0;-#,##0")
                .build()
        ;

//        MONEY_NO_FRACT_GRID_XLS_STYLE_NAME =  "money-no-fract-grid-xls-style";
//        MONEY_NO_FRACT_GRID_XLS_STYLE = new StyleBuilder(true, MONEY_NO_FRACT_GRID_XLS_STYLE_NAME)
//                .setParentStyleName(DEFAULT_GRID_XLS_STYLE_NAME)
//                .setHorizontalAlign(HorizontalAlign.RIGHT)
//                .setPattern("#,##0;-#,##0")
//                .build()
//        ;

        MONEY_NO_FRACT_SUM_GRID_STYLE = new StyleBuilder(true, "money-no-fract-sum-grid-style")
                .setParentStyleName(MONEY_NO_FRACT_GRID_STYLE_NAME)
                .setFont(ARIAL_MEDIUM_BOLD)
                .setBackgroundColor(GROUP_SUM_BG_COLOR)
                .setTransparent(false)
                .build();

        MONEY_NO_FRACT_TOT_GRID_STYLE = new StyleBuilder(true, "money-no-fract-tot-grid-style")
                .setParentStyleName(MONEY_NO_FRACT_GRID_STYLE_NAME)
                .setFont(ARIAL_MEDIUM_BOLD)
                .setBackgroundColor(TOTAL_BG_COLOR)
                .setTransparent(false)
                .build();

        MONEY_GRID_STYLE = new StyleBuilder(true, "money-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

//        MONEY_GRID_XLS_STYLE = new StyleBuilder(true, "money-grid-xls-style")
//                .setParentStyleName(DEFAULT_GRID_XLS_STYLE_NAME)
//                .setHorizontalAlign(HorizontalAlign.RIGHT)
//                .setPattern("#,##0.00;-#,##0.00")
//                .build();

        PERCENT_GRID_STYLE = new StyleBuilder(true, "percent-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("##0;-##0")
                .build();

    }

    public static class WeekendSluzDateCondition extends ConditionStyleExpression {
        private static final long serialVersionUID = Entity.SERIAL_VERSION_UID;
        private boolean weekendExpected;
        private boolean sluzExpected;

        public WeekendSluzDateCondition(boolean weekendExpected, boolean sluzExpected) {
            this.weekendExpected = weekendExpected;
            this.sluzExpected = sluzExpected;
        }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            String dateValue;
            Boolean isSluz;
            Object dateObjValue = fields.get("compositeDate");
            Object sluzMinsObjValue = fields.get("sluzMins");
            if (null == dateObjValue) {
                dateValue = "n_o_t__w_e_e_k_e_n_d";
            } else {
                dateValue = (String)dateObjValue;
            }
            if (null == sluzMinsObjValue) {
                isSluz = false;
            } else {
                isSluz = (null != sluzMinsObjValue && ((Long)sluzMinsObjValue).compareTo(0L) > 0);
            }
            return !(weekendExpected ^ ((dateValue).toLowerCase().contains("ne") || (dateValue).toLowerCase().contains("so")))
                    &&
                    !(sluzExpected ^ isSluz);
        }

        @Override
        public String getClassName() {
            return Boolean.class.getName();
        }

    }

    public static class WeekendFromCondition extends ConditionStyleExpression {
        private static final long serialVersionUID = Entity.SERIAL_VERSION_UID;
        private boolean weekendExpected;
        private boolean fromManualExpected;

        public WeekendFromCondition(boolean weekendExpected, boolean fromManualExpected) {
                this.weekendExpected = weekendExpected;
                this.fromManualExpected = fromManualExpected;
        }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            String dateValue;
            Boolean fromManualValue;
            Object dateObjValue = fields.get("compositeDate");
            Object fromObjValue = fields.get("fromManual");
            if (null == dateObjValue) {
                dateValue = "n_o_t__w_e_e_k_e_n_d";
            } else {
                dateValue = (String)dateObjValue;
            }
            if (null == fromObjValue) {
                fromManualValue = false;
            } else {
                fromManualValue = (Boolean)fromObjValue;
            }
            return !(weekendExpected ^ ((dateValue).toLowerCase().contains("ne") || (dateValue).toLowerCase().contains("so")))
                    &&
                   !(fromManualExpected ^ fromManualValue);
        }

        @Override
        public String getClassName() {
            return Boolean.class.getName();
        }

    }

    public static class WeekendToCondition extends ConditionStyleExpression {
        private static final long serialVersionUID = Entity.SERIAL_VERSION_UID;
        private boolean weekendExpected;
        private boolean toManualExpected;

        public WeekendToCondition(boolean weekendExpected, boolean toManualExpected) {
            this.weekendExpected = weekendExpected;
            this.toManualExpected = toManualExpected;
        }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            String dateValue;
            Boolean toManualValue;
            Object dateObjValue = fields.get("compositeDate");
            Object toObjValue = fields.get("toManual");
            if (null == dateObjValue) {
                dateValue = "n_o_t__w_e_e_k_e_n_d";
            } else {
                dateValue = (String)dateObjValue;
            }
            if (null == toObjValue) {
                toManualValue = false;
            } else {
                toManualValue = (Boolean)toObjValue;
            }
            return !(weekendExpected ^ ((dateValue).toLowerCase().contains("ne") || (dateValue).toLowerCase().contains("so")))
                    &&
                    !(toManualExpected ^ toManualValue);
        }

        @Override
        public String getClassName() {
            return Boolean.class.getName();
        }

    }

    public static class WeekendCondition extends ConditionStyleExpression {
        private static final long serialVersionUID = Entity.SERIAL_VERSION_UID;
        private boolean weekendExpected;
        private boolean fromManualExpected;

        public WeekendCondition(boolean weekendExpected) {
                this.weekendExpected = weekendExpected;
        }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            String dateValue;
            Object objValue = fields.get("compositeDate");
            if (null == objValue) {
                return !weekendExpected;
            } else {
                dateValue = (String)objValue;
            }
            return !(weekendExpected ^ ((dateValue).toLowerCase().contains("ne") || (dateValue).toLowerCase().contains("so")));

//            if (weekendExpected) {
//                return (dateValue).toLowerCase().contains("ne") || (dateValue).toLowerCase().contains("so");
//            } else {
//                return !(dateValue).toLowerCase().contains("ne") && !(dateValue).toLowerCase().contains("so");
//            }
        }

        @Override
        public String getClassName() {
            return Boolean.class.getName();
        }

    }


    public static class FromManualCondition extends ConditionStyleExpression {
        private static final long serialVersionUID = Entity.SERIAL_VERSION_UID;
        private boolean manualExpected;

        public FromManualCondition(boolean manualExpexted) {
            this.manualExpected = manualExpexted;
        }

        @Override
        public Object evaluate(Map fields, Map variables, Map parameters) {
            Boolean fromManualValue;
            Object objValue = fields.get("fromManual");
            if (null == objValue) {
                return !manualExpected;
            } else {
                fromManualValue = (Boolean)objValue;
            }
            if (manualExpected) {
                return fromManualValue;
            } else {
                return !fromManualValue;
            }
        }

        @Override
        public String getClassName() {
            return Boolean.class.getName();
        }

    }

}
