package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.ui.components.ZakRozpracGrid;
import org.vaadin.reports.PrintPreviewReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class ZakRozpracReport extends PrintPreviewReport {

    static final Font DEFAULT_FONT_PDF = Font.ARIAL_MEDIUM;
    static final Font HEADER_FONT_PDF = Font.ARIAL_MEDIUM_BOLD;
    static final Font TITLE_FONT_PDF = Font.ARIAL_BIG_BOLD;
    static final Style DEFAULT_STYLE;
    static final Style TITLE_STYLE;
    static final Style HEADER_STYLE;
    static final Style AMOUNT_STYLE;
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
//                .setFont(Font.VERDANA_MEDIUM)
//                .setStretchWithOverflow(false)
//                .setStretching(Stretching.NO_STRETCH)
                .build();

        TITLE_STYLE = new StyleBuilder(false)
                .setFont(TITLE_FONT_PDF)
                .build();

        HEADER_STYLE = new StyleBuilder(false)
                .setFont(HEADER_FONT_PDF)
                .setBorderTop(Border.PEN_1_POINT())
                .setBorderBottom(Border.PEN_1_POINT())
                .build();

        AMOUNT_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(10))
                .build();

        PROC_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(10))
                .build();
    }


    public ZakRozpracReport() {
        super();

        AbstractColumn ckontCol = ColumnBuilder.getNew()
                .setColumnProperty("ckont", String.class)
                .setWidth(7)
                .setTitle("Č.kont.")
                .build();

        AbstractColumn rokCol = ColumnBuilder.getNew()
                .setColumnProperty("rok", Integer.class)
                .setWidth(4)
                .setTitle("Rok")
                .build();

        AbstractColumn czakCol = ColumnBuilder.getNew()
                .setColumnProperty("czak", Integer.class)
                .setWidth(3)
                .setTitle("ČZ")
                .build();

//        AbstractColumn dateCreateCol = ColumnBuilder.getNew()
//                .setColumnProperty("dateCreate", LocalDate.class)
//                .setTitle("Vytvořeno")
//                .setWidth(7)
//                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
//                .build();

        AbstractColumn honorCistyCol = ColumnBuilder.getNew()
                .setColumnProperty("honorCisty", BigDecimal.class)
                .setTitle("Honorář čistý")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

//        AbstractColumn honorHrubyCol = ColumnBuilder.getNew()
//                .setColumnProperty("honorHruby", BigDecimal.class)
//                .setTitle("Honorář hrubý")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(AMOUNT_STYLE)
//                .setWidth(9)
//                .setPattern("#,##0.00;-#,##0.00")
//                .build();

//        AbstractColumn menaCol = ColumnBuilder.getNew()
//                .setColumnProperty("mena", Mena.class)
//                .setTitle("Měna")
//                .setWidth(5)
//                .build();

        AbstractColumn textZakCol = ColumnBuilder.getNew()
                .setColumnProperty("textZak", String.class)
                .setTitle("Text zakázky")
                .setWidth(26)
//                .setFixedWidth(false)
                .build();

        AbstractColumn r0Col = ColumnBuilder.getNew()
                .setColumnProperty("r0", BigDecimal.class)
                .setTitle("R0")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_STYLE)
                .setWidth(5)
                .setPattern("##0;-##0")
                .build();

        AbstractColumn r1Col = ColumnBuilder.getNew()
                .setColumnProperty("r1", BigDecimal.class)
                .setTitle("R1")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_STYLE)
                .setWidth(5)
                .setPattern("##0;-##0")
                .build();

        AbstractColumn r2Col = ColumnBuilder.getNew()
                .setColumnProperty("r2", BigDecimal.class)
                .setTitle("R2")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_STYLE)
                .setWidth(5)
                .setPattern("##0;-##0")
                .build();

        AbstractColumn r3Col = ColumnBuilder.getNew()
                .setColumnProperty("r3", BigDecimal.class)
                .setTitle("R3")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_STYLE)
                .setWidth(5)
                .setPattern("##0;-##0")
                .build();

        AbstractColumn r4Col = ColumnBuilder.getNew()
                .setColumnProperty("r4", BigDecimal.class)
                .setTitle("R4")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_STYLE)
                .setWidth(5)
                .setPattern("##0;-##0")
                .build();

        AbstractColumn naklMzdy = ColumnBuilder.getNew()
                .setColumnProperty("naklMzdy", BigDecimal.class)
                .setTitle("Mzdy")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        AbstractColumn naklPojist = ColumnBuilder.getNew()
                .setColumnProperty("naklPojist", BigDecimal.class)
                .setTitle("Pojištění")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

//        AbstractColumn vykonRxCol = ColumnBuilder.getNew()
//                .setCustomExpression(getCustomExpression())
//                .setCustomExpressionForCalculation(getCustomExpression2()
////                .setCustomExpression(
////                        new CustomExpression() {
////                            @Override
////                            public Object evaluate(Map fields, Map variables, Map parameters) {
//////                                return (String) fields.get("ckont");
////
//////                                BigDecimal honorCisty = (BigDecimal) fields.get("honorCisty");
////                                BigDecimal r0 = (BigDecimal) fields.get("r0");
//////                                BigDecimal r1 = (BigDecimal) fields.get("r1");
//////                                BigDecimal r2 = (BigDecimal) fields.get("r2");
//////                                BigDecimal r3 = (BigDecimal) fields.get("r3");
//////                                BigDecimal r4 = (BigDecimal) fields.get("r4");
////////                                Integer count = (Integer) variables.get("REPORT_COUNT");
//////                                BigDecimal activeRxValue  = ZakRozpracGrid.getActiveRxValue(r0, r1, r2, r3, r4);
//////                                BigDecimal vykon = null == activeRxValue || null == honorCisty ?
//////                                        null : honorCisty.subtract(activeRxValue.multiply(honorCisty).divide(BigDecimal.valueOf(100L)));
//////                                return null == vykon ? "" : vykon.toString();
////                                return r0;
////                            }
////                            @Override
////                            public String getClassName() {
////                                return BigDecimal.class.getName();
////                            }
////                        }
//                )
//                .setTitle("Výkony RX")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_STYLE)
//                .setWidth(5)
//                .build();


//        AbstractColumn honorarHrubyCol = ColumnBuilder.getNew()
//                .setCustomExpression(
//                        new CustomExpression() {
//                            public Object evaluate(Map fields, Map variables, Map parameters) {
//                                String state = (String) fields.get("state");
//                                String branch = (String) fields.get("branch");
//                                String productLine = (String) fields.get("productLine");
//                                Integer count = (Integer) variables.get("REPORT_COUNT");
//                                return count + ": " +state.toUpperCase() + " / " + branch.toUpperCase() + " / " + productLine;
//                            }
//                            public String getClassName() {
//                                return String.class.getName();
//                            }
//                        }
//                ).build();


        GroupBuilder gb1 = new GroupBuilder();

        Style glabelStyle = new StyleBuilder(false).setFont(Font.ARIAL_SMALL)
            .setHorizontalAlign(HorizontalAlign.RIGHT)
            .setBorderTop(Border.THIN())
            .setStretchWithOverflow(false)
            .build();
        DJGroupLabel glabel1 = new DJGroupLabel("Total amount",glabelStyle, LabelPosition.TOP);
        DJGroupLabel glabel2 = new DJGroupLabel("Total quantity",glabelStyle,LabelPosition.TOP);

        //		 define the criteria column to group by (columnState)
        DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) rokCol)
//              	.addFooterVariable(honorarCistyCol, DJCalculation.SUM,headerVariables, null, glabel1) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
//          		.addFooterVariable(columnaQuantity,DJCalculation.SUM,headerVariables, null, glabel2) // idem for the columnaQuantity column
         		.setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
   				.build();

//        GroupBuilder gb2 = new GroupBuilder(); // Create another group (using another column as criteria)
//        DJGroup g2 = gb2.setCriteriaColumn((PropertyColumn) columnBranch) // and we add the same operations for the columnAmount and
//                .addFooterVariable(honorarCistyCol,DJCalculation.SUM) // columnaQuantity columns
//                .addFooterVariable(columnaQuantity,	DJCalculation.SUM)
//                .build();


        this.getReportBuilder()
                .setTitle("Zakázky - rozpracovanost")
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

                .setGrandTotalLegend("National Total")

                .addColumn(ckontCol)
                .addColumn(rokCol)
                .addColumn(czakCol)
//                .addColumn(dateCreateCol)
//                .addColumn(honorarCol)
                .addColumn(honorCistyCol)
//                .addColumn(honorHrubyCol)
//                .addColumn(menaCol)
                .addColumn(textZakCol)
                .addColumn(r0Col)
                .addColumn(r1Col)
                .addColumn(r2Col)
                .addColumn(r3Col)
                .addColumn(r4Col)
                .addColumn(naklMzdy)
                .addColumn(naklPojist)
//                .addColumn(vykonRxCol)

                .addGlobalFooterVariable(honorCistyCol, DJCalculation.SUM, AMOUNT_STYLE)
//                .addGlobalFooterVariable(daysColumn, DJCalculation.AVERAGE, style)
                ;
//                    .build();

//        ;
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
