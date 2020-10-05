package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import eu.japtor.vizman.backend.entity.Mena;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class NabListReportBuilder extends FastReportBuilder {

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

//    protected DynamicReportBuilder reportBuilder;

    private java.text.Format digiStringFormat;
    private java.text.Format archStringFormat;

    private AbstractColumn archCol;
    private AbstractColumn digiCol;
    private AbstractColumn rokCol;
    private AbstractColumn ckontCol;
    private AbstractColumn czakCol;
    private AbstractColumn objednatelCol;
    private AbstractColumn kzTextCol;

    private AbstractColumn dateCreateCol;
    private AbstractColumn honorarCol;
    private AbstractColumn honorarCistyCol;
    private AbstractColumn honorarHrubyCol;
    private AbstractColumn menaCol;


    public NabListReportBuilder() {
        super();

//        digiStringFormat = new DigiStringFormat();
//        archStringFormat = new ArchStringFormat();

        buildReportColumns();
//        buildReportGroups();

//        reportBuilder = (new FastReportBuilder()).setUseFullPageWidth(true).setWhenNoData("(no data)", new Style());
        GroupBuilder gb1 = new GroupBuilder();

        Style glabelStyle = new StyleBuilder(false).setFont(Font.ARIAL_SMALL)
            .setHorizontalAlign(HorizontalAlign.RIGHT)
            .setBorderTop(Border.THIN())
            .setStretchWithOverflow(false)
            .build();
        DJGroupLabel glabel1 = new DJGroupLabel("Total amount",glabelStyle, LabelPosition.TOP);
        DJGroupLabel glabel2 = new DJGroupLabel("Total quantity",glabelStyle,LabelPosition.TOP);

        //		 define a criteria column to group by (columnState)
        DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) rokCol)
//              	.addFooterVariable(honorarCistyCol, DJCalculation.SUM,headerVariables, null, glabel1) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
//          		.addFooterVariable(columnaQuantity,DJCalculation.SUM,headerVariables, null, glabel2) // idem for the columnaQuantity column
         		.setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
   				.build();

        this
                .setTitle("Zakázky - seznam")
                .setUseFullPageWidth(true)
                .setIgnorePagination(true) // For Excel, we don't want pagination, just a plain list
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
                .setPrintBackgroundOnOddRows(true)
                .setPageSizeAndOrientation(Page_xls())
                .setPrintColumnNames(true)

                .setDefaultStyles(TITLE_STYLE, DEFAULT_STYLE, HEADER_STYLE, DEFAULT_STYLE)
                .addStyle(DEFAULT_STYLE)
                .addStyle(DEFAULT_GRID_STYLE)
                .addStyle(HEADER_STYLE)
                .setGrandTotalLegend("National Total")

                // Add basic  columns
                .addColumn(rokCol)
                .addColumn(ckontCol)

                // Add groups

                // Add subreports

                // Add totals
        ;
    }

    private void buildReportColumns() {
//        archCol = ColumnBuilder.getNew()
//                .setColumnProperty("arch", Boolean.class)
//                .setTitle("Arch")
//                .setTextFormatter(archStringFormat)
//                .setWidth(4)
//                .build();

//        digiCol = ColumnBuilder.getNew()
//                .setColumnProperty("digi", Boolean.class)
//                .setTitle("DIGI")
//                .setTextFormatter(digiStringFormat)
//                .setWidth(4)
//                .build();

        ckontCol = ColumnBuilder.getNew()
                .setColumnProperty("ckont", String.class)
                .setWidth(7)
                .setTitle("Č.kont.")
                .build();

        rokCol = ColumnBuilder.getNew()
                .setColumnProperty("rok", Integer.class)
                .setWidth(4)
                .setTitle("Rok")
                .build();

//        czakCol = ColumnBuilder.getNew()
//                .setColumnProperty("czak", Integer.class)
//                .setWidth(3)
//                .setTitle("ČZ")
//                .build();

        dateCreateCol = ColumnBuilder.getNew()
                .setColumnProperty("dateCreate", LocalDate.class)
                .setTitle("Vytvořeno")
                .setWidth(7)
                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
                .build();

        honorarCistyCol = ColumnBuilder.getNew()
                .setColumnProperty("honorarCisty", BigDecimal.class)
                .setTitle("Honorář čistý")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        honorarHrubyCol = ColumnBuilder.getNew()
                .setColumnProperty("honorarHruby", BigDecimal.class)
                .setTitle("Honorář hrubý")
                .setStyle(AMOUNT_STYLE)
                .setWidth(9)
                .setPattern("#,##0.00;-#,##0.00")
                .build();

        menaCol = ColumnBuilder.getNew()
                .setColumnProperty("mena", Mena.class)
                .setTitle("Měna")
                .setWidth(5)
                .build();

//        objednatelCol = ColumnBuilder.getNew()
//                .setColumnProperty("objednatel", String.class)
//                .setTitle("Objednatel")
//                .setWidth(17)
//                .build();

//        kzTextCol = ColumnBuilder.getNew()
//                .setColumnProperty("kzTextFull", String.class)
//                .setTitle("Text KONT/ZAK")
//                .setFixedWidth(false)
////                .setWidth(500)
//                .build();
    }
}
