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
import eu.japtor.vizman.backend.entity.Mena;
import org.vaadin.reports.PrintPreviewReport;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class ZakNaklReport extends PrintPreviewReport {

    static final Font DEFAULT_FONT_PDF = Font.ARIAL_MEDIUM;
    static final Font HEADER_FONT_PDF = Font.ARIAL_MEDIUM_BOLD;
    static final Font TITLE_FONT_PDF = Font.ARIAL_BIG_BOLD;
    static final Style DEFAULT_STYLE;
    static final Style TITLE_STYLE;
    static final Style HEADER_STYLE;
    static final Style GROUP_HEADER_STYLE;
    static final Style TEXT_STYLE;
    static final Style WORK_HOUR_STYLE;
    static final Style WORK_HOUR_STYLE_SUM;
    static final Style MONEY_STYLE;
    static final Style MONEY_NO_FRACT_STYLE;
    static final Style PROC_STYLE;
    static {
        DEFAULT_FONT_PDF.setPdfFontEmbedded(true);
        DEFAULT_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        DEFAULT_FONT_PDF.setPdfFontName("/Windows/Fonts/arial.ttf");

        HEADER_FONT_PDF.setPdfFontEmbedded(true);
        HEADER_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        HEADER_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        TITLE_FONT_PDF.setPdfFontEmbedded(true);
        TITLE_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        TITLE_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        DEFAULT_STYLE = new StyleBuilder(false)
                .setFont(DEFAULT_FONT_PDF)
                .setPaddingLeft(Integer.valueOf(3))
//                .setBorderLeft(Border.PEN_1_POINT())
//                .setBorderRight(Border.PEN_1_POINT())
//                .setStretchWithOverflow(false)
//                .setStretching(Stretching.NO_STRETCH)
                .build();

        TITLE_STYLE = new StyleBuilder(false)
                .setFont(TITLE_FONT_PDF)
                .build();

        HEADER_STYLE = new StyleBuilder(false)
                .setFont(HEADER_FONT_PDF)
                .setHorizontalAlign(HorizontalAlign.CENTER)
                .setBorder(Border.THIN())
//        sb.setBorderBottom(Border.PEN_2_POINT());
                .setBorderColor(Color.BLACK)
                .setBorderTop(Border.PEN_1_POINT())
                .setBorderBottom(Border.PEN_1_POINT())
                .build();

        TEXT_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingLeft(Integer.valueOf(3))
                .setBorderLeft(Border.THIN())
                .setBorderRight(Border.THIN())
                .build();

        GROUP_HEADER_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setPaddingLeft(Integer.valueOf(3))
//                .setBorderLeft(Border.THIN())
//                .setBorderRight(Border.THIN())
                .setFont(TITLE_FONT_PDF)
                .build();

        WORK_HOUR_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(3))
                .setBorderLeft(Border.THIN())
                .setBorderRight(Border.THIN())
                .setPattern("#,##0.0;-#,##0.0")
                .build();

//        WORK_HOUR_STYLE_SUM = WORK_HOUR_STYLE;
//        WORK_HOUR_STYLE_SUM
//                .setTextColor(Color.BLUE);

        WORK_HOUR_STYLE_SUM = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(3))
                .setBorderLeft(Border.THIN())
                .setBorderRight(Border.THIN())
                .setPattern("#,##0.0;-#,##0.0")
                .setTextColor(Color.BLUE)
                .build();

        MONEY_NO_FRACT_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(3))
                .setBorderLeft(Border.THIN())
                .setBorderRight(Border.THIN())
                .setPattern("#,##0;-#,##0")
                .build();

        MONEY_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(3))
                .setBorderLeft(Border.THIN())
                .setBorderRight(Border.THIN())
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        PROC_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(3))
                .setBorderLeft(Border.THIN())
                .setBorderRight(Border.THIN())
                .setPattern("##0;-##0")
                .build();
    }


    public ZakNaklReport() {
        super();

        AbstractColumn prijmeniCol = ColumnBuilder.getNew()
                .setColumnProperty("prijmeni", String.class)
                .setTitle("Příjmení")
//                .setStyle(TEXT_STYLE)
                .setStyle(GROUP_HEADER_STYLE)
                .setWidth(12)
                .build();

//        AbstractColumn datePruhCol = ColumnBuilder.getNew()
//                .setColumnProperty("datePruh", LocalDate.class)
//                .setTitle("Datum")
////                .setStyle(TEXT_STYLE)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
//                .setWidth(8)
//                .build();

        AbstractColumn ymPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("ymPruh", YearMonth.class)
                .setTitle("YM")
//                .setStyle(TEXT_STYLE)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .setWidth(8)
                .build();

        AbstractColumn workPruhCol = ColumnBuilder.getNew()
                .setColumnProperty("workPruh", BigDecimal.class)
                .setTitle("Hodin")
                .setStyle(WORK_HOUR_STYLE)
                .setWidth(9)
                .build();

        AbstractColumn naklMzdaCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzda", BigDecimal.class)
                .setTitle("Mzda")
                .setStyle(MONEY_NO_FRACT_STYLE)
                .setWidth(9)
                .build();

        AbstractColumn naklMzdaPojistCol = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdaPojist", BigDecimal.class)
                .setTitle("Mzda+Poj.")
                .setStyle(MONEY_NO_FRACT_STYLE)
                .setWidth(9)
                .build();

        AbstractColumn sazbaCol = ColumnBuilder.getNew()
                .setColumnProperty("sazba", BigDecimal.class)
                .setTitle("Sazba")
                .setStyle(MONEY_STYLE)
                .setWidth(9)
                .build();

//        AbstractColumn naklMzda = ColumnBuilder.getNew()
//                .setColumnProperty("naklMzda", BigDecimal.class)
//                .setTitle("Mzda")
//                .setStyle(MONEY_NO_FRACT_STYLE)
//                .setWidth(9)
//                .build();
//
//        AbstractColumn naklPojist = ColumnBuilder.getNew()
//                .setColumnProperty("naklPojist", BigDecimal.class)
//                .setTitle("Pojištění")
//                .setStyle(MONEY_NO_FRACT_STYLE)
//                .setWidth(9)
//                .build();



        GroupBuilder userGroupBuilder = new GroupBuilder();

        Style groupLabelStyle = new StyleBuilder(false).setFont(Font.ARIAL_SMALL)
            .setHorizontalAlign(HorizontalAlign.RIGHT)
            .setBorderTop(Border.THIN())
            .setStretchWithOverflow(false)
            .build();
        DJGroupLabel userGroupWorkHourLabel = new DJGroupLabel("", groupLabelStyle, LabelPosition.LEFT);
        DJGroupLabel userGroupMzdaLabel = new DJGroupLabel("", groupLabelStyle, LabelPosition.LEFT);
        DJGroupLabel userGroupWorkMzdaPojistLabel = new DJGroupLabel("", groupLabelStyle, LabelPosition.LEFT);

        //		 define the criteria column to group by (columnState)
        DJGroup userGroup = userGroupBuilder.setCriteriaColumn((PropertyColumn) prijmeniCol)
              	.addFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_STYLE, null, userGroupWorkHourLabel) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
          		.addFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_STYLE, null, userGroupMzdaLabel) // idem for the columnaQuantity column
          		.addFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_STYLE, null, userGroupWorkMzdaPojistLabel) // idem for the columnaQuantity column
         		.setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are many posibilities, see the GroupLayout for more.
//         		.setGroupLayout(GroupLayout.DEFAULT)
//         		.setGroupLayout(GroupLayout.VALUE_IN_HEADER_WITH_HEADERS)
   				.build();

//        GroupBuilder gb2 = new GroupBuilder(); // Create another group (using another column as criteria)
//        DJGroup g2 = gb2.setCriteriaColumn((PropertyColumn) columnBranch) // and we add the same operations for the columnAmount and
//                .addFooterVariable(honorarCistyCol,DJCalculation.SUM) // columnaQuantity columns
//                .addFooterVariable(columnaQuantity,	DJCalculation.SUM)
//                .build();


        this.getReportBuilder()
                .setTitle("NÁKLADY NA ZAKÁZKU")
                .setSubtitle("Text kontraktu... / Text zakázky...")
                .setReportLocale(new Locale("cs", "CZ"))
//                .setSubtitle("Rok: " + paramRokStr)
//                .addParameter("PARAM_ROK", String.class.getName())
                .setMargins(10, 10, 10, 10)
                .setPrintBackgroundOnOddRows(true)
                .setDefaultStyles(TITLE_STYLE, DEFAULT_STYLE, HEADER_STYLE, DEFAULT_STYLE)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setUseFullPageWidth(true)

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

                .setGrandTotalLegend("Nagruzka Total")

                .addColumn(prijmeniCol)
//                .addColumn(datePruhCol)
                .addColumn(ymPruhCol)
                .addColumn(workPruhCol)
                .addColumn(naklMzdaCol)
                .addColumn(naklMzdaPojistCol)
                .addColumn(sazbaCol)

                // Grouping by users
                .setPrintColumnNames(true)
                .addGroup(userGroup)

                // Total summaries
                .addGlobalFooterVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_STYLE_SUM)
                .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_STYLE)
                .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_STYLE)
        ;

        //                .addColumn(ColumnBuilder.getNew()
        //                        .setColumnProperty("startTime", LocalDateTime.class)
        //                        .setTitle("Date")
        //                        .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
        //                        .build())
        //                .addColumn(ColumnBuilder.getNew()
        //                        .setColumnProperty("startTime", LocalDateTime.class)
        //                        .setTextFormatter(DateTimeFormatter.ISO_LOCAL_TIME.toFormat())
        //                        .setTitle("Start time")
        //                        .build())
        //                .addColumn(ColumnBuilder.getNew()
        //                        .setColumnProperty("duration", Integer.class)
        //                        .setTitle("Duration (seconds)")
        //                        .build())
        //                .addColumn(ColumnBuilder.getNew()
        //                        .setColumnProperty("status", Status.class)
        //                        .setTitle("Status").build());
//        return report;
    }

    private CustomExpression getCustomExpression() {
        return new CustomExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                BigDecimal r1 = ( BigDecimal) fields.get("r1");
                return r1.toString();
            }

            public String getClassName() {
                return String.class.getName();
            }
        };
    }

    private CustomExpression getCustomExpression2() {
        return new CustomExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return fields.get("r1");
            }

            public String getClassName() {
                return BigDecimal   .class.getName();
            }
        };
    }

    //    public void setParamRok(Integer paramRok) {
//        this.paramRok = paramRok;
//        this.paramRokStr = null == paramRok ? "Vše" : paramRok.toString();;
//    }
}
