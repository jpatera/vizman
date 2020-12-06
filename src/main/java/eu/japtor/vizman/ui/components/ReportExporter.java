package eu.japtor.vizman.ui.components;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.ListLayoutManager;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;


public class ReportExporter<T> {

    public enum Format {
        // TODO: HTML(() -> new JRHtml...
        PDF(() -> new JRPdfExporter(), os -> new SimpleOutputStreamExporterOutput(os)),
        XLS(() -> new JRXlsExporter(), os -> new SimpleOutputStreamExporterOutput(os)),
        DOCX(() -> new JRDocxExporter(), os -> new SimpleOutputStreamExporterOutput(os)),
        PPTX(() -> new JRPptxExporter(), os -> new SimpleOutputStreamExporterOutput(os)),
        RTF(() -> new JRRtfExporter(), os -> new SimpleWriterExporterOutput(os)),
        ODT(() -> new JROdtExporter(), os -> new SimpleOutputStreamExporterOutput(os)),
        CSV(() -> new JRCsvExporter(), os -> new SimpleWriterExporterOutput(os)),
        XML(() -> new JRXmlExporter(), os -> new SimpleXmlExporterOutput(os));

        private final SerializableSupplier<JRAbstractExporter> exporterSupplier;
        private final SerializableFunction<OutputStream, ExporterOutput> exporterOutputFunction;

        Format(SerializableSupplier<JRAbstractExporter> exporterSupplier, SerializableFunction<OutputStream, ExporterOutput> exporterOutputFunction) {
            this.exporterSupplier = exporterSupplier;
            this.exporterOutputFunction = exporterOutputFunction;
        }
    }

    public StreamResource getStreamResource(
            DynamicReportBuilder drb
            , String fileName
            , SerializableSupplier<List<? extends T>> itemsSupplier
//            , Format format
            , String[] sheetNames
    ) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
            configuration.setOnePagePerSheet(true);
            configuration.setRemoveEmptySpaceBetweenRows(true);
            configuration.setRemoveEmptySpaceBetweenColumns(true);
            configuration.setWhitePageBackground(false);
            configuration.setDetectCellType(true);
            configuration.setOnePagePerSheet(true);
            if (null != sheetNames) {
                configuration.setSheetNames(sheetNames);
            }

            JRAbstractExporter exporter = new JRXlsExporter();
            exporter.setConfiguration(configuration);
            exporter.setExporterInput(new SimpleExporterInput(getNewJasperPrint(drb, itemsSupplier.get())));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            exporter.exportReport();
            outputStream.flush();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return new StreamResource(fileName, () -> inputStream);

        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JasperPrint getNewJasperPrint(DynamicReportBuilder drb, List<? extends T> items) {
        try {
            return buildJasperPrint(items, drb.build());
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    protected DynamicReportBuilder buildReportBuilder() {
        return new FastReportBuilder()
                .setUseFullPageWidth(true)
                .setWhenNoData("(no data)", new Style());
    }

    protected JasperPrint buildJasperPrint(List<? extends T> items, DynamicReport report) throws JRException {
        JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), items);
        VaadinSession.getCurrent().getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, print);
        return print;
    }
}
