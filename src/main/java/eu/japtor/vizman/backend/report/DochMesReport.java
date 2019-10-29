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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;


public class DochMesReport extends PrintPreviewReport {

    private AbstractColumn fullNameAndDochYmCol;
//    private AbstractColumn dochYmCol;
    private AbstractColumn dochWeekCol;
    private AbstractColumn dochDateCol;
    private AbstractColumn fromPraceStartCol;
    private AbstractColumn toPraceEndCol;
    private AbstractColumn obedCol;
    private AbstractColumn praceCelkCol;
    private AbstractColumn pracDobaCol;
    private AbstractColumn praceWendCol;
    private AbstractColumn lekCol;
    private AbstractColumn dovCol;
    private AbstractColumn nemCol;
    private AbstractColumn volnoCol;

    private DJGroup userGroup;
    private DJGroup weekGroup;

    private java.text.Format shortDurFormat;
    private java.text.Format hodsMinsFormat;
    private HodsMinsDJFormatter hodsMinsDJFormatter;
    private WeekFormat weekFormat;

    public DochMesReport() {
        super();

        shortDurFormat = new ShortDurFormat();
        hodsMinsFormat = new HodsMinsFormat();
        hodsMinsDJFormatter = new HodsMinsDJFormatter();
        weekFormat = new WeekFormat();

        buildReportColumns();
        buildReportGroups();

        this.getReportBuilder()
                .setTitle("MĚSÍČNÍ DOCHÁZKA")
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
//                .setPageSizeAndOrientation(Page.Page_A4_Portrait())
                .setUseFullPageWidth(false)
//                .setSubtitleHeight(200)
                .setPrintColumnNames(true)
                .setHeaderHeight(20)

//                .setSubtitle("Text kontraktu... / Text zakázky...")
                .setReportLocale(new Locale("cs", "CZ"))
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
                .addColumn(fullNameAndDochYmCol)
//                .addColumn(dochYmCol)
                .addColumn(dochWeekCol)
                .addColumn(dochDateCol)
                .addColumn(fromPraceStartCol)
                .addColumn(toPraceEndCol)
                .addColumn(obedCol)
                .addColumn(pracDobaCol)
                .addColumn(praceCelkCol)
                .addColumn(praceWendCol)
                .addColumn(lekCol)
                .addColumn(nemCol)
                .addColumn(dovCol)
                .addColumn(volnoCol)

                // Add groups
                .addGroup(userGroup)
                .addGroup(weekGroup)

                // Add totals
                .setGrandTotalLegend("Suma sumárum")
                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
                .addGlobalFooterVariable(obedCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(pracDobaCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(praceCelkCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(praceWendCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(lekCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(nemCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(dovCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addGlobalFooterVariable(volnoCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)

//                .addGlobalFooterVariable(obedCol, DJCalculation.SUM)

//                .addGlobalFooterVariable(obedCol,
//                        new CustomExpression() {
//
//                            @Override
//                            public Object evaluate(Map fields, Map variables, Map parameters) {
////                                double totalOfColumnC = 0.00;
////                                totalOfColumnC = ( totalOfColumnB/ totalOfColumnA) * 100;
//                                return shortDurFormat.format(Duration.ofHours(7));
//
//                            }
//
//                            @Override
//                            public String getClassName() {
//                                return Duration.class.getName();
//                            }
//                        }
//                        , WORK_HOUR_TOT_GRID_STYLE
//                )

//                .addGlobalFooterVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
//                .addGlobalFooterVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_TOT_GRID_STYLE)
        ;
    }


    public static class NullAwareShortTimeFormatter extends java.text.Format {
        static final java.text.Format fmt = DateTimeFormatter.ofPattern("H:mm").toFormat();

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null != obj) {
                sbuf.append(fmt.format(obj));
            }
            return sbuf;
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (null == source) {
                return null;
            }
            try {
                return (LocalTime)(fmt.parseObject(source));
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class ShortDurFormat extends java.text.Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            StringBuffer sbuf = new StringBuffer("");
            if (null == obj) {
                return sbuf;
            }
            sbuf.append(getAsHodsMins((Duration)obj));
//            long seconds = ((Duration)obj).getSeconds();
//            long absSeconds = Math.abs(seconds);
//            String positive = String.format(
//                    "%d:%02d",
//                    absSeconds / 3600,
//                    (absSeconds % 3600) / 60);
//            sbuf.append(seconds < 0 ? "-" + positive : positive);
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


//    Formatter fmt = new Formatter(sbuf);
//    fmt.format("PI = %f%n", Math.PI);
//    System.out.print(sbuf.toString());

    Duration durOne = Duration.ofHours(1);
//    Formatter durFmt = new Formatter(durOne);


//    SimpleFormatter durFormatter = new SimpleFormatter();

//    class DurationFormatter extends Formatter {
//        StringBuffer sb = new StringBuffer(1000);
//
//        String sss = "sss";
//        String.for
//
//        @Override
//        public String format(LogRecord record) {
//            String sss = "sss";
//            String.format(sss);
//            return record.getLevel() + ":" + record.getMessage();
//        }
//
//        // give a red color to any messages with levels >= WARNING
//        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
//            sb.append("<font color=\"red\">");
//            sb.append(rec.getLevel());
//            sb.append("</font>");
//        } else {
//            sb.append(rec.getLevel());
//        }
//        sb.append(' ');
//        sb.append(formatMessage(rec));
//        sb.append('\n');
//        return sb.toString();
//    }
//    }

// you can continue to append data to sbuf here.

    private void buildReportColumns() {

//        fmt.format("PI = %f%n", Math.PI);
//        System.out.print(sbuf.toString());

//        String.format("abcde");


        fullNameAndDochYmCol = ColumnBuilder.getNew()
                .setColumnProperty("fullNameAndDochYm", String.class)
                .setTitle("Jméno, Rok-měs")
                .setStyle(GROUP_HEADER_DOCH_PERSON_STYLE)
                .setWidth(150)
                .setFixedWidth(true)
                .build();

//        dochYmCol = ColumnBuilder.getNew()
//                .setColumnProperty("dochYm", YearMonth.class)
//                .setTitle("Rok-Měs")
//                .setStyle(YM_GRID_STYLE)
////                .setTextFormatter(DateTimeFormatter.ISO_DATE.toFormat())
//                .setWidth(70)
////                .setFixedWidth(true)
//                .build();

        dochWeekCol = ColumnBuilder.getNew()
                .setColumnProperty("dochWeek", Integer.class)
                .setTitle("Týden")
                .setTextFormatter(weekFormat)
                .setStyle(GROUP_HEADER_WEEK_STYLE)
                .setWidth(130)
                .build();

        dochDateCol = ColumnBuilder.getNew()
                .setColumnProperty("compositeDate", String.class)
//                .setColumnProperty("compositeDate", LocalDate.class)
                .setTitle("Datum")
//                .setPattern("dd.MM")
                .addConditionalStyles(condWeekendDateStyles)
                .setStyle(SHORT_DATE_GRID_STYLE)
                .setWidth(50)
//                .setTextFormatter(DateTimeFormatter.ofPattern("dd.MM").toFormat())
                .build();

        fromPraceStartCol = ColumnBuilder.getNew()
                .setColumnProperty("fromPraceStart", LocalTime.class)
                .setTitle("Od")
//                .setTextFormatter(shortTimeFmt)
                .setStyle(WORK_HOUR_GRID_STYLE)
//                .addConditionalStyles(condWeekendDateStyles)
                .addConditionalStyles(condWeekendFromStyles)
                .setWidth(50)
                .build();

        toPraceEndCol = ColumnBuilder.getNew()
                .setColumnProperty("toPraceEnd", LocalTime.class)
                .setTitle("Do")
//                .setTextFormatter(shortTimeFmt)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .addConditionalStyles(condWeekendFromStyles)
                .setWidth(50)
                .build();

        obedCol = ColumnBuilder.getNew()
                .setColumnProperty("obedMins", Long.class)
                .setTitle("Obed")
                .setTextFormatter(hodsMinsFormat)    // ..overrides a time pattern from style
//                .setCustomExpressionForCalculation(getObedDurAsLongExp())
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();

//        obedCol = ColumnBuilder.getNew()
//                .setColumnProperty("durObedSum", Duration.class)
//                .setTitle("Obed")
//                .setTextFormatter(shortDurFormat)
//                .setCustomExpressionForCalculation(getObedDurAsLongExp())
//                .setStyle(WORK_HOUR_GRID_STYLE)
//                .setWidth(50)
//                .build();
//
        pracDobaCol = ColumnBuilder.getNew()
                .setColumnProperty("pracDobaMins", Long.class)
                .setTitle("Prac.doba")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
//                .setFixedWidth(true)
                .build();

        praceCelkCol = ColumnBuilder.getNew()
                .setColumnProperty("pracCelkMins", Long.class)
                .setTitle("Práce")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();

        praceWendCol = ColumnBuilder.getNew()
                .setColumnProperty("pracWendMins", Long.class)
                .setTitle("Víkend")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();

        lekCol = ColumnBuilder.getNew()
                .setColumnProperty("lekMins", Long.class)
                .setTitle("Lékař")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();

        dovCol = ColumnBuilder.getNew()
                .setColumnProperty("dovMins", Long.class)
                .setTitle("Dovolená")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();

        nemCol = ColumnBuilder.getNew()
                .setColumnProperty("nemMins", Long.class)
                .setTitle("Nemoc")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();

        volnoCol = ColumnBuilder.getNew()
                .setColumnProperty("volnoMins", Long.class)
                .setTitle("Volno")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(50)
                .build();
    }


    private CustomExpression getObedDurAsLongExp() {
        return new CustomExpression() {
            public Object evaluate(Map fields, Map variables, Map parameters) {
                Duration dur = (Duration)fields.get("durObedSum");
                return dur.toMinutes();
            }
            public String getClassName() {
                return Long.class.getName();
            }
        };
    }

    private void buildReportGroups() {

        // User group
        GroupBuilder userGroupBuilder = new GroupBuilder();
        DJGroupLabel userGroupMzdaLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        DJGroupLabel userGroupWorkMzdaPojistLabel = new DJGroupLabel("", GROUP_LABEL_STYLE, LabelPosition.LEFT);
        userGroup = userGroupBuilder
                .setCriteriaColumn((PropertyColumn) fullNameAndDochYmCol)
//                .addHeaderVariable(workPruhCol, DJCalculation.SUM, WORK_HOUR_SUM_GRID_STYLE, null, userGroupWorkHourLabel)
//                .addHeaderVariable(naklMzdaCol, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, userGroupMzdaLabel)
//                .addHeaderVariable(naklMzdaPojistCol, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, userGroupWorkMzdaPojistLabel)
//                .addHeaderVariable(workPruhP8Col, DJCalculation.SUM, WORK_HOUR_SUM_GRID_STYLE, null, null)
//                .addHeaderVariable(naklMzdaP8Col, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, null)
//                .addHeaderVariable(naklMzdaPojistP8Col, DJCalculation.SUM, MONEY_NO_FRACT_SUM_GRID_STYLE, null, null)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;

        // WEEK group
        GroupBuilder weekGroupBuilder = new GroupBuilder();
        weekGroup = weekGroupBuilder
                .setCriteriaColumn((PropertyColumn) dochWeekCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;
    }


    public void setSubtitleText(String subtitleText) {
        this.getReportBuilder().setSubtitle(subtitleText);
    }


    // Following expressions should work, but there is probbably a bug in Vaadin component
    private CustomExpression getFormatedDuration() {
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
