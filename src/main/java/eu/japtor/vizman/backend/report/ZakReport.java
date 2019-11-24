package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import eu.japtor.vizman.backend.entity.ItemType;
import org.vaadin.reports.PrintPreviewReport;

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import static ar.com.fdvs.dj.domain.constants.Font.PDF_ENCODING_CP1250_Central_European;
import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;


public class ZakReport extends PrintPreviewReport {

    private AbstractColumn kzCisloCol;
    private AbstractColumn zakTextCol;
    private AbstractColumn typCol;
    private AbstractColumn faktTextCol;
    private AbstractColumn castkaCol;
    private AbstractColumn faktCisloCol;

    private DJGroup kzCisloGroup;
    private DJGroup zakTextGroup;


    public ZakReport() {
        super();

        buildReportColumns();
        buildReportGroups();

        this.getReportBuilder()
                .setTitle("ZAKÁZKA")
//                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setPageSizeAndOrientation(Page.Page_A4_Landscape())
                .setUseFullPageWidth(false)
//                .setSubtitleHeight(200)
                .setPrintColumnNames(true)
//                .setHeaderHeight(20)
//                .setSubtitle("Text kontraktu... / Text zakázky...")
                .setReportLocale(new Locale("cs", "CZ"))
                .setDefaultEncoding(PDF_ENCODING_CP1250_Central_European)
                .setMargins(20, 10, 25, 20)
                .setDefaultStyles(XXL_STYLE, XXL_STYLE, HEADER_STYLE, DEFAULT_STYLE)
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
//                .addColumn(kzCisloCol)
//                .addColumn(zakTextCol)
                .addColumn(typCol)
                .addColumn(faktTextCol)
                .addColumn(castkaCol)
                .addColumn(faktCisloCol)

                // Add groups
//                .addGroup(kzCisloGroup)
//                .addGroup(zakTextGroup)

                // Add totals
//                .setGrandTotalLegend("Celkem")
//                .setGrandTotalLegendStyle(GROUP_HEADER_USER_STYLE)
//                .addGlobalFooterVariable(pracDobaCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
//                .addGlobalFooterVariable(praceCelkCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
//                .addGlobalFooterVariable(praceWendCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
//                .addGlobalFooterVariable(lekCol, DJCalculation.SUM, WORK_HOUR_TOT_GRID_STYLE, hodsMinsDJFormatter)
        ;
    }

    private void buildReportColumns() {

        kzCisloCol = ColumnBuilder.getNew()
                .setColumnProperty("zak.kzCislo", String.class)
                .setTitle("ČK/ČZ")
//                .setStyle(GROUP_HEADER_STYLE)
                .setStyle(GROUP_HEADER_STYLE)
                .setWidth(100)
                .setFixedWidth(true)
                .build()
        ;
        zakTextCol = ColumnBuilder.getNew()
                .setColumnProperty("zak.kzText", String.class)
                .setTitle("ext zakázky")
                .setStyle(GROUP_HEADER_STYLE)
                .setWidth(100)
                .setFixedWidth(true)
                .build()
        ;
        typCol = ColumnBuilder.getNew()
                .setColumnProperty("typ", ItemType.class)
                .setTitle("Typ")
                .setStyle(DEFAULT_GRID_STYLE)
//                .setStyle(GROUP_HEADER_DOCH_PERSON_STYLE)
                .setWidth(50)
                .setFixedWidth(true)
                .build()
        ;
        faktTextCol = ColumnBuilder.getNew()
                .setColumnProperty("text", String.class)
                .setTitle("Text dílčího plnění")
                .setStyle(DEFAULT_GRID_STYLE)
                .setWidth(400)
                .setFixedWidth(true)
                .build()
        ;
        castkaCol = ColumnBuilder.getNew()
                .setColumnProperty("castka", BigDecimal.class)
                .setTitle("Částka")
                .setStyle(MONEY_GRID_STYLE)
                .setWidth(80)
//                .setFixedWidth(true)
                .build();
        faktCisloCol = ColumnBuilder.getNew()
                .setColumnProperty("faktCislo", String.class)
                .setTitle("Číslo faktury")
                .setStyle(DEFAULT_GRID_STYLE)
                .setWidth(200)
                .setFixedWidth(true)
                .build()
        ;
    }


    private void buildReportGroups() {
        // kzCislo group
        GroupBuilder kzCisloGroupBuilder = new GroupBuilder();
        kzCisloGroup = kzCisloGroupBuilder
                .setCriteriaColumn((PropertyColumn) kzCisloCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER_WITH_HEADERS)
//                .setHeaderHeight(130)
                .setHeaderHeight(8)
                .build()
        ;
        // zakText group
        GroupBuilder zakTextGroupBuilder = new GroupBuilder();
        zakTextGroup = zakTextGroupBuilder
                .setCriteriaColumn((PropertyColumn) zakTextCol)
                .setGroupLayout(GroupLayout.VALUE_IN_HEADER)
                .build()
        ;
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