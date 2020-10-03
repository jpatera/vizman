package eu.japtor.vizman.backend.report;

import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.*;
import ar.com.fdvs.dj.domain.constants.*;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import eu.japtor.vizman.backend.entity.Mena;

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static eu.japtor.vizman.backend.utils.VzmFormatReport.*;

public class KontTreeReportBuilder {

    static Page Page_xls(){
        return new Page(1111,1300,true);
    }

    protected DynamicReportBuilder reportBuilder;

    public KontTreeReportBuilder() {
        super();

        reportBuilder = (new FastReportBuilder()).setUseFullPageWidth(true).setWhenNoData("(no data)", new Style());
    }

    public DynamicReport buildReport()  {
        return reportBuilder.build();
    }

}
