package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.LabelPosition;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import org.vaadin.reports.PrintPreviewReport;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static ar.com.fdvs.dj.domain.constants.Font.PDF_ENCODING_CP1250_Central_European;
import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;


public class DochYearReport extends PrintPreviewReport {

    private AbstractColumn fullNameCol;
    private AbstractColumn dochYmCol;
    private AbstractColumn praceCelkCol;
    private AbstractColumn pracDobaCol;
    private AbstractColumn praceWendCol;
    private AbstractColumn lekCol;
    private AbstractColumn dovCol;
    private AbstractColumn nemCol;
    private AbstractColumn volnoCol;

    private DJGroup userGroup;
    private DJGroup yearGroup;

    private java.text.Format shortDurFormat;
    private java.text.Format hodsMinsFormat;
    private HodsMinsDJFormatter hodsMinsDJFormatter;

    public DochYearReport() {
        super();

        shortDurFormat = new ShortDurFormat();
        hodsMinsFormat = new HodsMinsFormat();
        hodsMinsDJFormatter = new HodsMinsDJFormatter();

        buildReportColumns();
        buildReportGroups();

        this.getReportBuilder()
                .setTitle("ROČNÍ DOCHÁZKA")
//                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                .setUseFullPageWidth(false)
//                .setSubtitleHeight(200)
                .setPrintColumnNames(true)
                .setHeaderHeight(20)
//                .setSubtitle("Text kontraktu... / Text zakázky...")
                .setReportLocale(new Locale("cs", "CZ"))
                .setDefaultEncoding(PDF_ENCODING_CP1250_Central_European)
//                .setDefaultEncoding("ISO-8859-2")
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
                .addStyle(MONEY_NO_FRACT_GRID_STYLE)
                .addStyle(WORK_HOUR_GRID_STYLE)

                // Add columns
                .addColumn(fullNameCol)
                .addColumn(dochYmCol)
                .addColumn(pracDobaCol)
                .addColumn(praceCelkCol)
                .addColumn(praceWendCol)
                .addColumn(lekCol)
                .addColumn(nemCol)
                .addColumn(dovCol)
                .addColumn(volnoCol)

//                .addField("fromManual", Boolean.class.getName())
//                .addField("toManual", Boolean.class.getName())
//                .addField("sluzMins", Long.class.getName())

                // Add groups
                .addGroup(userGroup)
//                .addGroup(yearGroup)

                // Add totals
                .setGrandTotalLegend("Celkem")
                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
                .addGlobalFooterVariable(pracDobaCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(praceCelkCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(praceWendCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(lekCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(nemCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(dovCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(volnoCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
        ;
    }

    public static class ShortDurFormat extends java.text.Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null == obj) {
                return sbuf;
            }
            sbuf.append(getAsHodsMins((Duration)obj));
            return sbuf;
        }
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (null == source) {
                return null;
            }
            return getAsDuration(source);
        }
    }

    public static class HodsMinsFormat extends java.text.Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null == obj) {
                return sbuf;
            }
            sbuf.append(getAsHodsMins((Long)obj));
            return sbuf;
        }
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (null == source) {
                return null;
            }
            return getAsDuration(source);
        }
    }

    public static class HodsMinsDJFormatter implements DJValueFormatter {
        @Override
        public Object evaluate(Object value, Map fields, Map variables, Map parameters) {
            if (null == value) {
                return "";
            } else {
                return getAsHodsMins((Long)value);
            }
        }
        @Override
        public String getClassName() {
            return String.class.getName();
        }
    }

    public static class WeekFormat extends java.text.Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null == obj) {
                return sbuf;
            }
            sbuf.append((obj).toString()).append(". týden");
            return sbuf;
        }
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (null == source) {
                return null;
            }
            return Long.valueOf(source);
        }
    }

    public static final Duration getAsDuration(String shortDurStr) {
        if (null == shortDurStr) {
            return null;
        }
        String[] tokens = shortDurStr.split(":");
        int hours = Integer.parseInt(tokens[0]);
        int minutes = Integer.parseInt(tokens[1]);
        long dur = 3600 * hours + 60 * minutes;
        return Duration.ofMinutes(dur);
    }

    public static final String getAsHodsMins(Long mins) {
        if (null == mins) {
            return null;
        }
        if (mins == 0) {
            return "";
        }
        long absMins = Math.abs(mins);
        String hodsMins = String.format(
                "%d:%02d",
                absMins / 60,
                absMins % 60
        );
        return mins <  0 ? "-" + hodsMins : hodsMins;
    }

    public static final String getAsHodsMins(Duration dur) {
        if (null == dur) {
            return null;
        }
        long seconds = dur.getSeconds();
        long absSeconds = Math.abs(seconds);
        String hodsMins = String.format(
                "%d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60);
        return seconds < 0 ? "-" + hodsMins : hodsMins;
    }


    private void buildReportColumns() {

        fullNameCol = ColumnBuilder.getNew()
                .setColumnProperty("fullNameAndDochYear", String.class)
                .setTitle("Jméno")
                .setStyle(GROUP_HEADER_DOCH_PERSON_STYLE)
                .setWidth(300)
                .setFixedWidth(true)
                .build()
        ;
        dochYmCol = ColumnBuilder.getNew()
                .setColumnProperty("dochYm", YearMonth.class)
                .setTitle("Rok-Měs")
                .setStyle(CENTER_GRID_STYLE)
                .setWidth(90)
                .build()
        ;
        pracDobaCol = ColumnBuilder.getNew()
                .setColumnProperty("pracDobaMins", Long.class)
                .setTitle("Pr. doba")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()

        ;
        praceCelkCol = ColumnBuilder.getNew()
                .setColumnProperty("pracCelkMins", Long.class)
                .setTitle("Práce")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()
        ;
        praceWendCol = ColumnBuilder.getNew()
                .setColumnProperty("pracWendMins", Long.class)
                .setTitle("Víkend")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()
        ;
        lekCol = ColumnBuilder.getNew()
                .setColumnProperty("lekMins", Long.class)
                .setTitle("Lékař")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()
        ;
        dovCol = ColumnBuilder.getNew()
                .setColumnProperty("dovMins", Long.class)
                .setTitle("Dovolená")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()
        ;
        nemCol = ColumnBuilder.getNew()
                .setColumnProperty("nemMins", Long.class)
                .setTitle("Nemoc")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()
        ;
        volnoCol = ColumnBuilder.getNew()
                .setColumnProperty("volnoMins", Long.class)
                .setTitle("Volno")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build()
        ;
    }


    private void buildReportGroups() {

        // User group
        GroupBuilder userGroupBuilder = new GroupBuilder();
        userGroup = userGroupBuilder
                .setCriteriaColumn((PropertyColumn) fullNameCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;

//        textField = new JRDesignTextField();
//        textField.setX(leftPosition);
//        textField.setY(4);
//        textField.setWidth(50);
//        textField.setHeight(15);
//        textField.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_RIGHT);
//        textField.setStyle(normalStyle);
//        expression = new JRDesignExpression();
//        expression.setValueClass(java.lang.String.class);
//        expression.setText("$F{array[i]}");
//        textField.setExpression(expression);
//        band.addElement(textField);
//        leftPosition = leftPosition+100;
    }


    public void setSubtitleText(String subtitleText) {
        this.getReportBuilder().setSubtitle(subtitleText);
    }


    // Following expressions should work, but there is probably a bug in Vaadin component
    private CustomExpression getFormattedDuration() {
        return new CustomExpression() {
            @Override
            public Object evaluate(Map fields, Map variables, Map parameters) {
                return "aaa:bbb";
            }
            @Override
            public String getClassName() {
                return Duration.class.getName();
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
