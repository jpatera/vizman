package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import eu.japtor.vizman.backend.entity.Mena;
//import org.vaadin.reports.PrintPreviewReport;

import java.math.BigDecimal;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

//public class ZakRozpracReport extends PrintPreviewReport {
public class ZakRozpracReport {

//    private AbstractColumn ckontCol;
//    private AbstractColumn czakCol;
//    private AbstractColumn rokCol;
//    private AbstractColumn skupCol;
//    private AbstractColumn menaCol;
//    private AbstractColumn honorCistyByKurzCol;
//    private AbstractColumn kzTextShortCol;
//    private AbstractColumn rm4Col;
//    private AbstractColumn rm3Col;
//    private AbstractColumn rm2Col;
//    private AbstractColumn rm1Col;
//    private AbstractColumn r0Col;
//    private AbstractColumn r1Col;
//    private AbstractColumn r2Col;
//    private AbstractColumn r3Col;
//    private AbstractColumn r4Col;
//    private AbstractColumn rxRyVykonByKurzCol;
//    private AbstractColumn rpCol;
//    private AbstractColumn rpHotovoCol;
//    private AbstractColumn rpHotovoByKurzCol;
//    private AbstractColumn rpZbyvaByKurzCol;
//    private AbstractColumn vysledekByKurzCol;
//
//
//    // TODO: obsolete - remove
//    public ZakRozpracReport() {
//        super();
//
//        buildReportColumns();
//        buildReportGroups();
//
//        this.getReportBuilder()
//                .setTitle("SUMÁRNÍ ROZPRACOVANOST ZAKÁZEK")
//                .setReportLocale(new Locale("cs", "CZ"))
////                .setSubtitle("Rok: " + paramRokStr)
////                .addParameter("PARAM_ROK", String.class.getName())
//                .setMargins(10, 10, 10, 10)
////                .setPrintBackgroundOnOddRows(true)
//                .setDefaultStyles(TITLE_STYLE, SUBTITLE_STYLE, HEADER_STYLE, DEFAULT_STYLE)
//                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
//                .setUseFullPageWidth(true)
//
////                .addAutoText("Rok: " + "\" + $P{PARAM_ROK} + \""
////                        , AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 80, DEFAULT_STYLE)
////                .addAutoText("For internal use only"
////                        , AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 200, DEFAULT_STYLE)
////                .addAutoText(LocalDateTime.now().toString())
////                .addAutoText(AutoText.AUTOTEXT_CREATED_ON
////                        , AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT)
//                .addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y
//                        , AutoText.POSITION_HEADER, AutoText.ALIGMENT_CENTER)
//
//                // Add styles (needed only styles used as parents)
//                .addStyle(DEFAULT_STYLE)
//                .addStyle(DEFAULT_GRID_STYLE)
//                .addStyle(HEADER_STYLE)
//                .addStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .addStyle(WORK_HOUR_GRID_STYLE)
//
//                // Add columns
//                .addColumn(ckontCol)
//                .addColumn(czakCol)
//                .addColumn(rokCol)
//                .addColumn(skupCol)
//                .addColumn(menaCol)
//                .addColumn(honorCistyByKurzCol)
//                .addColumn(rm4Col)
//                .addColumn(rm3Col)
//                .addColumn(rm2Col)
//                .addColumn(rm1Col)
//                .addColumn(r0Col)
//                .addColumn(r1Col)
//                .addColumn(r2Col)
//                .addColumn(r3Col)
//                .addColumn(r4Col)
//                .addColumn(rxRyVykonByKurzCol)
//                .addColumn(rpCol)
//                .addColumn(rpHotovoByKurzCol)
//                .addColumn(rpZbyvaByKurzCol)
//                .addColumn(vysledekByKurzCol)
//                .addColumn(kzTextShortCol)
//
//                // Add groups
//
//                // Add totals
//                .setGrandTotalLegend("National Total")
//                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
//                .addGlobalFooterVariable(honorCistyByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(rpHotovoByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(rpZbyvaByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(rxRyVykonByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(vysledekByKurzCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//        ;
//    }
//
//    private void buildReportColumns() {
//
//        ckontCol = ColumnBuilder.getNew()
//                .setColumnProperty("ckont", String.class)
//                .setTitle("Č.kont.")
//                .setStyle(DEFAULT_GRID_STYLE)
//                .setWidth(8)
//                .build();
//
//        czakCol = ColumnBuilder.getNew()
//                .setColumnProperty("czak", Integer.class)
//                .setTitle("ČZ")
//                .setStyle(INT_GRID_STYLE)
//                .setWidth(3)
//                .build();
//
//        rokCol = ColumnBuilder.getNew()
//                .setColumnProperty("rok", Integer.class)
//                .setTitle("Rok")
//                .setStyle(INT_GRID_STYLE)
//                .setWidth(5)
//                .build();
//
//        skupCol = ColumnBuilder.getNew()
//                .setColumnProperty("skupina", String.class)
//                .setTitle("Sk")
//                .setStyle(DEFAULT_GRID_STYLE)
//                .setWidth(3)
//                .build();
//
//        menaCol = ColumnBuilder.getNew()
//                .setColumnProperty("mena", Mena.class)
//                .setTitle("Měna")
//                .setStyle(DEFAULT_GRID_STYLE)
//                .setWidth(5)
//                .build();
//
//        honorCistyByKurzCol = ColumnBuilder.getNew()
//                .setColumnProperty("honorCistyByKurz", BigDecimal.class)
//                .setTitle("Honorář č.")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .setWidth(9)
//                .build();
//
//        kzTextShortCol = ColumnBuilder.getNew()
//                .setColumnProperty("kzTextShort", String.class)
//                .setTitle("Text")
//                .setStyle(DEFAULT_GRID_STYLE)
//                .setWidth(25)
////                .setFixedWidth(false)
//                .build();
//
//        rm4Col = ColumnBuilder.getNew()
//                .setColumnProperty("rm4", BigDecimal.class)
//                .setTitle("R-4")
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        rm3Col = ColumnBuilder.getNew()
//                .setColumnProperty("rm3", BigDecimal.class)
//                .setTitle("R-3")
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        rm2Col = ColumnBuilder.getNew()
//                .setColumnProperty("rm2", BigDecimal.class)
//                .setTitle("R-2")
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        rm1Col = ColumnBuilder.getNew()
//                .setColumnProperty("rm1", BigDecimal.class)
//                .setTitle("R-1")
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        r0Col = ColumnBuilder.getNew()
//                .setColumnProperty("r0", BigDecimal.class)
//                .setTitle("R0")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        r1Col = ColumnBuilder.getNew()
//                .setColumnProperty("r1", BigDecimal.class)
//                .setTitle("R1")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        r2Col = ColumnBuilder.getNew()
//                .setColumnProperty("r2", BigDecimal.class)
//                .setTitle("R2")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        r3Col = ColumnBuilder.getNew()
//                .setColumnProperty("r3", BigDecimal.class)
//                .setTitle("R3")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        r4Col = ColumnBuilder.getNew()
//                .setColumnProperty("r4", BigDecimal.class)
//                .setTitle("R4")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(4)
//                .build();
//
//        rxRyVykonByKurzCol = ColumnBuilder.getNew()
//                .setColumnProperty("rxRyVykonByKurz", BigDecimal.class)
//                .setTitle("Výk. rx-ry")
//                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .setWidth(9)
//                .build();
//
//        rpCol = ColumnBuilder.getNew()
//                .setColumnProperty("rp", BigDecimal.class)
//                .setTitle("RP")
////                .setHeaderStyle(HEADER_STYLE)
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(5)
//                .build();
//
//        rpHotovoCol = ColumnBuilder.getNew()
//                .setColumnProperty("rpHotovo", BigDecimal.class)
//                .setTitle("Hotovo RP")
//                .setStyle(PROC_GRID_STYLE)
//                .setWidth(5)
//                .build();
//
//        rpHotovoByKurzCol = ColumnBuilder.getNew()
//                .setColumnProperty("rpHotovoByKurz", BigDecimal.class)
//                .setTitle("Hotovo RP")
//                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .setWidth(10)
//                .build();
//
//        rpZbyvaByKurzCol = ColumnBuilder.getNew()
//                .setColumnProperty("rpZbyvaByKurz", BigDecimal.class)
//                .setTitle("Zbývá RP")
//                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .setWidth(9)
//                .build();
//
//        vysledekByKurzCol = ColumnBuilder.getNew()
//                .setColumnProperty("vysledekByKurz", BigDecimal.class)
//                .setTitle("Výsledek")
//                .setStyle(MONEY_NO_FRACT_GRID_STYLE)
//                .setWidth(9)
//                .build();
//    }
//
//
//    private void buildReportGroups() {
//
//    }
//
//    public void setSubtitleText(String subtitleText) {
//        this.getReportBuilder().setSubtitle(subtitleText);
//    }
}
