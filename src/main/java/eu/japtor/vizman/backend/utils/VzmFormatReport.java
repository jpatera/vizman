package eu.japtor.vizman.backend.utils;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

import java.awt.*;

import static ar.com.fdvs.dj.domain.constants.Font.ARIAL_MEDIUM_BOLD;

public class VzmFormatReport {
    static final Color TOTAL_BG_COLOR = new Color(0xFFF4EE);
    static final Color GROUP_SUM_BG_COLOR = new Color(0xEEF4FF);
    static final Color HEADER_BG_COLOR = new Color(0xF4F4F4);

    public static final ar.com.fdvs.dj.domain.constants.Font ARIAL_MEDIUM_XL = new ar.com.fdvs.dj.domain.constants.Font(11, ar.com.fdvs.dj.domain.constants.Font._FONT_ARIAL, false, false, false);
    public static final ar.com.fdvs.dj.domain.constants.Font ARIAL_MEDIUM_XL_BOLD = new ar.com.fdvs.dj.domain.constants.Font(11, ar.com.fdvs.dj.domain.constants.Font._FONT_ARIAL, true, false, false);
    public static final ar.com.fdvs.dj.domain.constants.Font ARIAL_MEDIUM_XXL_BOLD = new ar.com.fdvs.dj.domain.constants.Font(12, ar.com.fdvs.dj.domain.constants.Font._FONT_ARIAL, true, false, false);

    //    public static final ar.com.fdvs.dj.domain.constants.Font DEFAULT_FONT_PDF = ar.com.fdvs.dj.domain.constants.Font.ARIAL_MEDIUM;
    public static final ar.com.fdvs.dj.domain.constants.Font DEFAULT_FONT_PDF = Font.ARIAL_SMALL;

    //    public static final ar.com.fdvs.dj.domain.constants.Font HEADER_FONT_PDF = ARIAL_MEDIUM_XL_BOLD;
    // public static final ar.com.fdvs.dj.domain.constants.Font HEADER_FONT_PDF = ARIAL_MEDIUM_XL;
    public static final ar.com.fdvs.dj.domain.constants.Font HEADER_FONT_PDF = Font.ARIAL_SMALL_BOLD;

    //    public static final Font GROUP_HEADER_ZAK_FONT_PDF = Font.ARIAL_MEDIUM_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font GROUP_HEADER_ZAK_FONT_PDF = ARIAL_MEDIUM_XL_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font GROUP_HEADER_USER_FONT_PDF = ARIAL_MEDIUM_XL_BOLD;
    public static final ar.com.fdvs.dj.domain.constants.Font SUBTITLE_FONT_PDF = ARIAL_MEDIUM_XL;
    public static final ar.com.fdvs.dj.domain.constants.Font TITLE_FONT_PDF = ar.com.fdvs.dj.domain.constants.Font.ARIAL_BIG_BOLD;

    public static final String DEFAULT_STYLE_NAME;
    public static final Style DEFAULT_STYLE;

    public static final Style DEFAULT_GRID_STYLE;
    public static final String DEFAULT_GRID_STYLE_NAME;

    public static final Style TITLE_STYLE;
    public static final Style SUBTITLE_STYLE;

    public static final Style HEADER_STYLE;
    public static final Style GROUP_HEADER_ZAK_STYLE;
    public static final Style GROUP_HEADER_USER_STYLE;
    public static final Style GROUP_LABEL_STYLE;

    public static final String WORK_HOUR_GRID_STYLE_NAME;
    public static final Style WORK_HOUR_GRID_STYLE;
    public static final Style WORK_HOUR_SUM_GRID_STYLE;
    public static final Style WORK_HOUR_TOT_GRID_STYLE;

    public static final  String MONEY_NO_FRACT_GRID_STYLE_NAME;
    public static final Style MONEY_NO_FRACT_GRID_STYLE;
    public static final Style MONEY_NO_FRACT_SUM_GRID_STYLE;
    public static final Style MONEY_NO_FRACT_TOT_GRID_STYLE;

    public static final Style INT_GRID_STYLE;
    public static final Style YM_GRID_STYLE;
    public static final Style PROC_GRID_STYLE;
    public static final Style PERCENT_GRID_STYLE;
    public static final Style MONEY_GRID_STYLE;

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

        DEFAULT_GRID_STYLE_NAME = "default-grid-style";
        DEFAULT_GRID_STYLE = new StyleBuilder(false, DEFAULT_GRID_STYLE_NAME)
                .setFont(DEFAULT_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingLeft(3)
                .setPaddingRight(5)
                .setBorder(Border.THIN())
                .build();

        TITLE_STYLE = new StyleBuilder(true,"title-style")
                .setParentStyleName(DEFAULT_STYLE_NAME)
                .setFont(TITLE_FONT_PDF)
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

        YM_GRID_STYLE = new StyleBuilder(true, "ym-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setPaddingLeft(20)
                .build();

        INT_GRID_STYLE = new StyleBuilder(true, "ym-grid-int-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .build();

        PROC_GRID_STYLE = new StyleBuilder(true, "proc-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(3)
                .setPattern("##0;-##0")
                .build();

        WORK_HOUR_GRID_STYLE_NAME = "work-hour-grid-style";
        WORK_HOUR_GRID_STYLE = new StyleBuilder(true, "work-hour-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("#,##0.0;-#,##0.0")
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

        PERCENT_GRID_STYLE = new StyleBuilder(true, "percent-grid-style")
                .setParentStyleName(DEFAULT_GRID_STYLE_NAME)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPattern("##0;-##0")
                .build();
    }

}
