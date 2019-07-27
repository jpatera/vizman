package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import eu.japtor.vizman.fsdataprovider.FileTypeResolver;
import eu.japtor.vizman.tools.EmbeddedDocComponent;
import eu.japtor.vizman.ui.components.VerticalScrollLayout;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.*;

public class FileViewerDialog extends Dialog {

    VerticalLayout dialogCanvas;
    VerticalScrollLayout docContainer;
    EmbeddedDocComponent docComponent;
    HorizontalLayout titleBar;
    HorizontalLayout toolBar;
    Paragraph viewerTitle;
    FileDownloadWrapper downloadButtonWrapper;
    File docFile;

    public FileViewerDialog()
    {
        super();

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        this.setWidth("1200px");
        this.setHeight("800px");
        this.getElement().setAttribute("theme", "viewer-dialog");

        dialogCanvas = new VerticalLayout();
        dialogCanvas.setSizeFull();
        dialogCanvas.getStyle().set("display", "flex");
        dialogCanvas.getStyle().set("flex-direction", "column");
        dialogCanvas.setMargin(false);
//        dialogCanvas.setSpacing(false);
        dialogCanvas.setPadding(false);


        Button closeButton = (new Button("Zavřít"));
        closeButton.setAutofocus(true);
        closeButton.getElement().setAttribute("theme", "primary");
        closeButton.addClickListener(e -> this.close());

        Button downloadButton = (new Button("Stáhnout"));
        downloadButton.getElement().setAttribute("theme", "secondary");
        downloadButtonWrapper = new FileDownloadWrapper("fakeFile", new File("fakeFile"));
//                new StreamResource("foo.txt", () -> new ByteArrayInputStream("foo".getBytes())));
        downloadButtonWrapper.wrapComponent(downloadButton);

        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.add(
                downloadButtonWrapper
                , closeButton
        );

        viewerTitle = new Paragraph("");

        titleBar = new HorizontalLayout();
        titleBar.add(
                viewerTitle
        );

        toolBar = new HorizontalLayout();
        toolBar.setWidth("100%");
        toolBar.getStyle()
                .set("padding-left", "10px")
                .set("padding-right", "10px")
        ;
        toolBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolBar.setSpacing(false);
        toolBar.add(
                titleBar
                , buttonBar
        );

        docContainer = new VerticalScrollLayout();
        docContainer.setHeightFull();
//        docContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
        docContainer.getStyle()
                .set("display", "flex")
        ;

        dialogCanvas.add(
                toolBar
                , docContainer
        );

        this.add(
                dialogCanvas
        );
    }

    public void openDialog(File file) {
        docFile = file;
        viewerTitle.setText(null == file ? "N/A" : file.getName());

        String mimeType = FileTypeResolver.getMIMEType(file);
        if (StringUtils.isEmpty(mimeType)) {
            mimeType = "text/plain";
        }
        if (mimeType.contains("comma-separated-values")) {
            mimeType = "text/plain";
        }
        StreamResource resource = new StreamResource(file.getName(), () -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    return new ByteArrayInputStream(new byte[]{});
                }
        });
        resource.setContentType(mimeType);

        if (null != docComponent) {
            docContainer.removeContent(docComponent);
        }
        docComponent = new EmbeddedDocComponent(resource, mimeType);
        docContainer.addContent(docComponent);
        setDocContainerHeightByMime(mimeType, docContainer);

        downloadButtonWrapper.setResource(resource);
        this.open();
    }

    private void setDocContainerHeightByMime(final String mimeType, VerticalScrollLayout container) {
        if (null == mimeType) {
            container.setFixedHeight();
            return;
        }
        if (mimeType.contains("pdf")) {
            container.setFixedHeight();
            container.setAlignStretch();
        } else if (mimeType.contains("excel") || mimeType.contains("word") || mimeType.contains("powerpoint")) {
            container.setMaxHeight();
            container.setAlignStretch();
        } else if (mimeType.startsWith("image")) {
            container.setUndefinedHeight();
            container.setAlignCenter();
        } else {
            container.setUndefinedHeight();
            container.setAlignStretch();
        }
    }
}
