package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import eu.japtor.vizman.backend.entity.Mena;

import java.math.BigDecimal;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class ZakRozpracAgregXlsReportBuilder extends FastReportBuilder {

    static Page Page_xls(){
        return new Page(666,1300,true);
    }

    private AbstractColumn ckontCol;
    private AbstractColumn czakCol;
    private AbstractColumn rokCol;
    private AbstractColumn skupCol;
    private AbstractColumn menaCol;
    private AbstractColumn honorCistyByKurzCol;
    private AbstractColumn kzTextShortCol;
    private AbstractColumn rm4Col;
    private AbstractColumn rm3Col;
    private AbstractColumn rm2Col;
    private AbstractColumn rm1Col;
    private AbstractColumn r0Col;
    private AbstractColumn r1Col;
    private AbstractColumn r2Col;
    private AbstractColumn r3Col;
    private AbstractColumn r4Col;
    private AbstractColumn rxRyVykonByKurzCol;
    private AbstractColumn rpCol;
    private AbstractColumn rpHotovoCol;
    private AbstractColumn rpHotovoByKurzCol;
    private AbstractColumn rpZbyvaByKurzCol;
    private AbstractColumn vysledekByKurzCol;
    private AbstractColumn vysledekP8ByKurzCol;


    public ZakRozpracAgregXlsReportBuilder(final String subtitleText) {
        super();

        buildReportColumns();
        buildReportGroups();

        this
                .setTitle("ROZPRACOVANOST ZAKÁZEK")
                .setSubtitle(subtitleText)
                .setUseFullPageWidth(true)
                .setIgnorePagination(true) // For Excel, we don't want pagination, just a plain list
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
//                .setPrintBackgroundOnOddRows(true)
                .setPageSizeAndOrientation(Page_xls())

//                .setDefaultStyles(TITLE_STYLE, DEFAULT_STYLE, HEADER_STYLE, DEFAULT_STYLE)
//                .addStyle(DEFAULT_STYLE)
//                .addStyle(DEFAULT_GRID_STYLE)
//                .addStyle(HEADER_STYLE)
//                .addStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .addStyle(WORK_HOUR_GRID_STYLE)

                .setDefaultStyles(DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_TEXT_STYLE, DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .addStyle(DEFAULT_GRID_XLS_NUM_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
//                .addStyle(DEFAULT_GRID_STYLE)

                .setGrandTotalLegend("National Total")
                .setPrintColumnNames(true)

                // Add basic columns
                .addColumn(ckontCol)
                .addColumn(czakCol)
                .addColumn(rokCol)
                .addColumn(skupCol)
                .addColumn(menaCol)
                .addColumn(honorCistyByKurzCol)
                .addColumn(rm4Col)
                .addColumn(rm3Col)
                .addColumn(rm2Col)
                .addColumn(rm1Col)
                .addColumn(r0Col)
                .addColumn(r1Col)
                .addColumn(r2Col)
                .addColumn(r3Col)
                .addColumn(r4Col)
                .addColumn(rxRyVykonByKurzCol)
                .addColumn(rpCol)
                .addColumn(rpHotovoByKurzCol)
                .addColumn(rpZbyvaByKurzCol)
                .addColumn(vysledekByKurzCol)
                .addColumn(vysledekP8ByKurzCol)
                .addColumn(kzTextShortCol)

                // Add groups

                // Add sub-reports

                // Add totals
                .setGrandTotalLegend("National Total")
                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
                .addGlobalFooterVariable(honorCistyByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(rpHotovoByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(rpZbyvaByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(rxRyVykonByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
                .addGlobalFooterVariable(vysledekByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_GRID_XLS_STYLE)
        ;
    }

    private void buildReportColumns() {
        ckontCol = ColumnBuilder.getNew()
                .setColumnProperty("ckont", String.class)
                .setTitle("Č.kont.")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(5)
                .build();

        czakCol = ColumnBuilder.getNew()
                .setColumnProperty("czak", Integer.class)
                .setTitle("ČZ")
                .setStyle(INT_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        rokCol = ColumnBuilder.getNew()
                .setColumnProperty("rok", Integer.class)
                .setTitle("Rok")
                .setStyle(INT_GRID_XLS_STYLE)
                .setWidth(4)
                .build();

        skupCol = ColumnBuilder.getNew()
                .setColumnProperty("skupina", String.class)
                .setTitle("Sk")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(3)
                .build();

        menaCol = ColumnBuilder.getNew()
                .setColumnProperty("mena", Mena.class)
                .setTitle("Měna")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(4)
                .build();

        honorCistyByKurzCol = ColumnBuilder.getNew()
                .setColumnProperty("honorCistyByKurz", BigDecimal.class)
                .setTitle("Honorář č.")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(7)
                .build();

        kzTextShortCol = ColumnBuilder.getNew()
                .setColumnProperty("kzTextShort", String.class)
                .setTitle("Text")
                .setStyle(DEFAULT_GRID_XLS_TEXT_STYLE)
                .setWidth(25)
//                .setFixedWidth(false)
                .build();

        rm4Col = ColumnBuilder.getNew()
                .setColumnProperty("rm4", BigDecimal.class)
                .setTitle("R-4")
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        rm3Col = ColumnBuilder.getNew()
                .setColumnProperty("rm3", BigDecimal.class)
                .setTitle("R-3")
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        rm2Col = ColumnBuilder.getNew()
                .setColumnProperty("rm2", BigDecimal.class)
                .setTitle("R-2")
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        rm1Col = ColumnBuilder.getNew()
                .setColumnProperty("rm1", BigDecimal.class)
                .setTitle("R-1")
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        r0Col = ColumnBuilder.getNew()
                .setColumnProperty("r0", BigDecimal.class)
                .setTitle("R0")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        r1Col = ColumnBuilder.getNew()
                .setColumnProperty("r1", BigDecimal.class)
                .setTitle("R1")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        r2Col = ColumnBuilder.getNew()
                .setColumnProperty("r2", BigDecimal.class)
                .setTitle("R2")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        r3Col = ColumnBuilder.getNew()
                .setColumnProperty("r3", BigDecimal.class)
                .setTitle("R3")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        r4Col = ColumnBuilder.getNew()
                .setColumnProperty("r4", BigDecimal.class)
                .setTitle("R4")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        rxRyVykonByKurzCol = ColumnBuilder.getNew()
                .setColumnProperty("rxRyVykonByKurz", BigDecimal.class)
                .setTitle("Výk. rx-ry")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(7)
                .build();

        rpCol = ColumnBuilder.getNew()
                .setColumnProperty("rp", BigDecimal.class)
                .setTitle("RP")
//                .setHeaderStyle(HEADER_STYLE)
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(3)
                .build();

        rpHotovoCol = ColumnBuilder.getNew()
                .setColumnProperty("rpHotovo", BigDecimal.class)
                .setTitle("Hotovo RP")
                .setStyle(PROC_GRID_XLS_STYLE)
                .setWidth(7)
                .build();

        rpHotovoByKurzCol = ColumnBuilder.getNew()
                .setColumnProperty("rpHotovoByKurz", BigDecimal.class)
                .setTitle("Hotovo RP")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(7)
                .build();

        rpZbyvaByKurzCol = ColumnBuilder.getNew()
                .setColumnProperty("rpZbyvaByKurz", BigDecimal.class)
                .setTitle("Zbývá RP")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(7)
                .build();

        vysledekByKurzCol = ColumnBuilder.getNew()
                .setColumnProperty("vysledekByKurz", BigDecimal.class)
                .setTitle("Výsledek")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(7)
                .build();

        vysledekP8ByKurzCol = ColumnBuilder.getNew()
                .setColumnProperty("vysledekP8ByKurz", BigDecimal.class)
                .setTitle("Výsledek P8")
                .setStyle(MONEY_NO_FRACT_GRID_XLS_STYLE)
                .setWidth(7)
                .build();
    }

    private void buildReportGroups() {

    }
}
