package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import eu.japtor.vizman.backend.entity.Mena;
import org.vaadin.reports.PrintPreviewReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ZakListReport extends PrintPreviewReport {

    static final Font DEFAULT_FONT_PDF = Font.ARIAL_MEDIUM;
    static final Font HEADER_FONT_PDF = Font.ARIAL_MEDIUM_BOLD;
    static final Style DEFAULT_STYLE;
    static final Style TITLE_STYLE;
    static final Style HEADER_STYLE;
    static final Style AMOUNT_STYLE;
    static {
        DEFAULT_FONT_PDF.setPdfFontEmbedded(true);
        DEFAULT_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        DEFAULT_FONT_PDF.setPdfFontName("/Windows/Fonts/arial.ttf");

        HEADER_FONT_PDF.setPdfFontEmbedded(true);
        HEADER_FONT_PDF.setPdfFontEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing);
        HEADER_FONT_PDF.setPdfFontName("/Windows/Fonts/arialbd.ttf");

        DEFAULT_STYLE = new StyleBuilder(false)
                .setFont(DEFAULT_FONT_PDF)
//                .setFont(Font.VERDANA_MEDIUM)
                .build();

        TITLE_STYLE = new StyleBuilder(false)
                .setFont(Font.ARIAL_BIG)
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
    }

//    static Font basicFont = new Font(12,"Ariel","/Windows/Fonts/ariel.ttf",
//            Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing, true);
//
//    static Font headerFont = new Font(12,"Ariel","/Windows/Fonts/ariel.ttf",
//            Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing, true);



//    static Style DEFAULT_STYLE;
//    static Style TITLE_STYLE;
//    static Style HEADER_STYLE;


    public ZakListReport() {
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

        AbstractColumn dateCreateCol = ColumnBuilder.getNew()
                .setColumnProperty("dateCreate", LocalDate.class)
                .setTitle("Vytvořeno")
                .setWidth(7)
                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .build();

        AbstractColumn honorarCistyCol = ColumnBuilder.getNew()
                .setColumnProperty("honorarCisty", BigDecimal.class)
                .setTitle("Honorář čistý")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        AbstractColumn honorarHrubyCol = ColumnBuilder.getNew()
                .setColumnProperty("honorarHruby", BigDecimal.class)
                .setTitle("Honorář hrubý")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        AbstractColumn menaCol = ColumnBuilder.getNew()
                .setColumnProperty("mena", Mena.class)
                .setTitle("Měna")
                .setWidth(5)
                .build();

        AbstractColumn textCol = ColumnBuilder.getNew()
                .setColumnProperty("text", String.class)
                .setTitle("Text zakázky")
                .setFixedWidth(false)
                .build();


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
                .setTitle("Zakázky - seznam")
                .setReportLocale(new Locale("cs", "CZ"))
//                .setSubtitle("Rok: " + paramRokStr)
//                .addParameter("PARAM_ROK", String.class.getName())
                .setMargins(10, 10, 10, 10)
                .setPrintBackgroundOnOddRows(true)
                .setDefaultStyles(TITLE_STYLE, DEFAULT_STYLE, HEADER_STYLE, DEFAULT_STYLE)
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())

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
                .addColumn(dateCreateCol)
//                .addColumn(honorarCol)
                .addColumn(honorarCistyCol)
                .addColumn(honorarHrubyCol)
                .addColumn(menaCol)
                .addColumn(textCol)

                .addGlobalFooterVariable(honorarCistyCol, DJCalculation.SUM, AMOUNT_STYLE)
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

//    public void setParamRok(Integer paramRok) {
//        this.paramRok = paramRok;
//        this.paramRokStr = null == paramRok ? "Vše" : paramRok.toString();;
//    }
}
