package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class ZakBasicReportBuilder extends FastReportBuilder {

    static final Style AMOUNT_STYLE;
    static {
        AMOUNT_STYLE = new StyleBuilder(false)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setPaddingRight(Integer.valueOf(10))
                .build();
    }

    private java.text.Format digiStringFormat;
    private java.text.Format archStringFormat;

    private AbstractColumn archCol;
    private AbstractColumn digiCol;
    private AbstractColumn rokCol;
    private AbstractColumn ckontCol;
    private AbstractColumn czakCol;
    private AbstractColumn objednatelCol;
    private AbstractColumn kzTextCol;

    public ZakBasicReportBuilder(
            String titleText
            , String subtitleText
    ) {
        super();

        digiStringFormat = new DigiStringFormat();
        archStringFormat = new ArchStringFormat();

        buildReportColumns();
        buildReportGroups();

        this
                .setTitle(titleText)
                .setSubtitle(subtitleText)

                .setPageSizeAndOrientation(new Page(6666,1300))
                .setUseFullPageWidth(true)
                .setIgnorePagination(true) // FALSE is needed if splitting reports to more XLS lists (by groups)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
                .setPrintBackgroundOnOddRows(true)
                .setPrintColumnNames(true)

                .setDefaultStyles(TITLE_STYLE, DEFAULT_STYLE, HEADER_STYLE, DEFAULT_STYLE)
                .addStyle(DEFAULT_STYLE)
                .addStyle(DEFAULT_GRID_STYLE)
                .addStyle(HEADER_STYLE)

                // Columns
                .addColumn(rokCol)
                .addColumn(archCol)
                .addColumn(digiCol)
                .addColumn(ckontCol)
                .addColumn(czakCol)
                .addColumn(objednatelCol)
                .addColumn(kzTextCol)

                // Groups

                // Sub-reports

                // Totals
                .setGrandTotalLegend("National Total")
        ;
    }

    private void buildReportColumns() {
        archCol = ColumnBuilder.getNew()
                .setColumnProperty("arch", Boolean.class)
                .setTitle("Arch")
                .setTextFormatter(archStringFormat)
                .setWidth(4)
                .build();

        digiCol = ColumnBuilder.getNew()
                .setColumnProperty("digi", Boolean.class)
                .setTitle("DIGI")
                .setTextFormatter(digiStringFormat)
                .setWidth(4)
                .build();

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

        czakCol = ColumnBuilder.getNew()
                .setColumnProperty("czak", Integer.class)
                .setWidth(3)
                .setTitle("ČZ")
                .build();

        objednatelCol = ColumnBuilder.getNew()
                .setColumnProperty("objednatel", String.class)
                .setTitle("Objednatel")
                .setWidth(17)
                .build();

        kzTextCol = ColumnBuilder.getNew()
                .setColumnProperty("kzTextFull", String.class)
                .setTitle("Text KONT/ZAK")
                .setFixedWidth(false)
//                .setWidth(500)
                .build();
    }


    private void buildReportGroups() {
    }

    public static class DigiStringFormat extends java.text.Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null == obj) {
                return sbuf;
            }
            sbuf.append(getAsDigiChar((Boolean)obj));
            return sbuf;
        }
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (null == source) {
                return null;
            }
            return getAsBoolean(source);
        }
    }

    public static class ArchStringFormat extends java.text.Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null == obj) {
                return sbuf;
            }
            sbuf.append(getAsArchChar((Boolean)obj));
            return sbuf;
        }
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (null == source) {
                return null;
            }
            return getAsBoolean(source);
        }
    }

    public static final String getAsDigiChar(Boolean digi) {
        if (null == digi) {
            return null;
        }
        return digi ? "D" : "";
    }

    public static final String getAsArchChar(Boolean arch) {
        if (null == arch) {
            return null;
        }
        return arch ? "A" : "";
    }

    public static final Boolean getAsBoolean(String digiStr) {
        if (null == digiStr) {
            return null;
        } else {
            return ("D".equals(digiStr));
        }
    }
}
