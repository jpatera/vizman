package eu.japtor.vizman.ui.components;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


public class ReportXlsExporter<T> {

    public enum Format {
        XLS;
    }

    public StreamResource getXlsStreamResource(
            DynamicReportBuilder drb
            , String fileName
            , SerializableSupplier<List<? extends T>> itemsSupplier
            , String[] sheetNames
    ) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
            configuration.setRemoveEmptySpaceBetweenRows(true);
            configuration.setRemoveEmptySpaceBetweenColumns(true);
            configuration.setWhitePageBackground(false);
            configuration.setDetectCellType(true);
            configuration.setOnePagePerSheet(true);
            configuration.setWrapText(true);
            configuration.setColumnWidthRatio(1.1f);
//            configuration.setCellHidden(true);
            if (null != sheetNames) {
                configuration.setSheetNames(sheetNames);
            }
//                configuration.setForcePageBreaks(true);

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

    private JasperPrint buildJasperPrint(List<? extends T> items, DynamicReport report) throws JRException {
        JasperPrint print = DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), items);
        VaadinSession.getCurrent().getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, print);
        return print;
    }
}
