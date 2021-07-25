package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.*;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.LabelPosition;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

/**
 Vypis mesicni dochazky
 */
public class DochMonthXlsReportBuilder extends FastReportBuilder {

    private AbstractColumn personId;
    private AbstractColumn fullNameAndDochYmCol;
    private AbstractColumn prijmeniCol;
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

    private java.text.Format hodsMinsFormat;
    private HodsMinsDJFormatter hodsMinsDJFormatter;
    private WeekFormat weekFormat;

    public DochMonthXlsReportBuilder() {
        super();

        hodsMinsFormat = new DochMonthReport.HodsMinsFormat();
        hodsMinsDJFormatter = new HodsMinsDJFormatter();
        weekFormat = new WeekFormat();

        buildReportColumns();
        buildReportGroups();

        this
                .setTitle("MĚSÍČNÍ DOCHÁZKA")
                .setPageSizeAndOrientation(new Page(9999,999))
                .setIgnorePagination(false) // FALSE is needed if splitting reports to more XLS lists (by groups)
                .setHeaderHeight(20)
                .setMargins(0, 0, 0, 0)
                .setWhenNoData("(no data)", new Style())
                .setReportLocale(new Locale("cs", "CZ"))
                .setPrintColumnNames(true)

                // Styles (needed only styles used as parent)
                .setDefaultStyles(TITLE_STYLE, SUBTITLE_STYLE, HEADER_STYLE, DEFAULT_STYLE)

                .addStyle(DEFAULT_STYLE)
                .addStyle(DEFAULT_GRID_STYLE)
                .addStyle(HEADER_STYLE)
                .addStyle(MONEY_NO_FRACT_GRID_STYLE)
                .addStyle(WORK_HOUR_GRID_STYLE)

                // Columns
                .addColumn(fullNameAndDochYmCol)
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

                .addField(FROM_MANUAL_REP_FIELD_NAME, Boolean.class.getName())
                .addField(TO_MANUAL_REP_FIELD_NAME, Boolean.class.getName())
                .addField(SLUZ_MINS_REP_FIELD_NAME, Long.class.getName())

                // Groups
                .addGroup(userGroup)
                .addGroup(weekGroup)

                // Sub-reports

                // Grand Totals
        ;
    }


    private void buildReportGroups() {

        // USER group
        userGroup = new GroupBuilder()
                .setCriteriaColumn((PropertyColumn) fullNameAndDochYmCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .setStartInNewPage(true)
                .setFooterLabel(new DJGroupLabel("Celkem za osobu", GROUP_HEADER_WEEK_STYLE, LabelPosition.LEFT))
                .addFooterVariable(obedCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(pracDobaCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(praceCelkCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(praceWendCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(lekCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(nemCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(dovCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
                .addFooterVariable(volnoCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
        .build();

        // WEEK group
        GroupBuilder weekGroupBuilder = new GroupBuilder();
        weekGroup = weekGroupBuilder
                .setCriteriaColumn((PropertyColumn) dochWeekCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;
    }

    private void buildReportColumns() {

        personId = ColumnBuilder.getNew()
                .setColumnProperty("personId", Long.class)
                .setTitle("PER ID")
                .setStyle(GROUP_HEADER_DOCH_PERSON_STYLE)
                .setWidth(80)
                .setFixedWidth(true)
                .build();

        fullNameAndDochYmCol = ColumnBuilder.getNew()
                .setColumnProperty("fullNameAndDochYm", String.class)
                .setTitle("Jméno, Rok-měs")
                .setStyle(GROUP_HEADER_DOCH_PERSON_STYLE)
                .setWidth(250)
                .setFixedWidth(true)
                .build();

        prijmeniCol = ColumnBuilder.getNew()
                .setColumnProperty("prijmeni", String.class)
                .setTitle("Příjmení")
                .setStyle(GROUP_HEADER_DOCH_PERSON_STYLE)
                .setWidth(250)
                .setFixedWidth(true)
                .build();

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
                .setStyle(SHORT_DATE_GRID_STYLE)
                .addConditionalStyles(condWeekendSluzDateStyles)
                .setWidth(55)
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
                .addConditionalStyles(condWeekendToStyles)
                .setWidth(45)
                .build();

        obedCol = ColumnBuilder.getNew()
                .setColumnProperty("obedMins", Long.class)
                .setTitle("Oběd")
                .setTextFormatter(hodsMinsFormat)    // ..overrides a time pattern from style
//                .setCustomExpressionForCalculation(getObedDurAsLongExp())
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(45)
                .build();

        pracDobaCol = ColumnBuilder.getNew()
                .setColumnProperty("pracDobaMins", Long.class)
                .setTitle("Pr. doba")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(45)
//                .setFixedWidth(true)
                .build();

        praceCelkCol = ColumnBuilder.getNew()
                .setColumnProperty("pracCelkMins", Long.class)
                .setTitle("Práce")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(45)
                .build();

        praceWendCol = ColumnBuilder.getNew()
                .setColumnProperty("pracWendMins", Long.class)
                .setTitle("Víkend")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(45)
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
                .setWidth(45)
                .build();

        nemCol = ColumnBuilder.getNew()
                .setColumnProperty("nemMins", Long.class)
                .setTitle("Nemoc")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(45)
                .build();

        volnoCol = ColumnBuilder.getNew()
                .setColumnProperty("volnoMins", Long.class)
                .setTitle("Volno")
                .setTextFormatter(hodsMinsFormat)
                .setStyle(WORK_HOUR_GRID_STYLE)
                .setWidth(45)
                .build();
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
                return fmt.parseObject(source);
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
            sbuf.append((obj)).append(". týden");
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
        long dur = 3600L * hours + 60L * minutes;
        return Duration.ofMinutes(dur);
    }

    public static String getAsHodsMins(Long mins) {
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

    public static String getAsHodsMins(Duration dur) {
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
}
