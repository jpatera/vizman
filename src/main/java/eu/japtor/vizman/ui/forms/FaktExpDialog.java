package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.backend.utils.VzmFileUtils;
import eu.japtor.vizman.ui.components.VerticalScrollLayout;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class FaktExpDialog extends Dialog {

    VerticalLayout dialogCanvas;
    VerticalScrollLayout docContainer;
//    EmbeddedDocComponent docComponent;
    Text dataComponent;
    HorizontalLayout titleBar;
    HorizontalLayout toolBar;
    Paragraph viewerTitle;
//    FileDownloadWrapper downloadButtonWrapper;
    File docFile;
    File outputFaktFile;
    FileOutputStream faktOutStream;
    String faktExpFilePath;
    File faktExpFile;
    String faktExpData = "";

//    @Autowired
    private CfgPropsCache cfgPropsCache;


    public FaktExpDialog(
            CfgPropsCache cfgPropsCache
    ) {
        super();

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(false);

        this.cfgPropsCache = cfgPropsCache;

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

//        Button downloadButton = (new Button("Stáhnout"));
//        downloadButton.getElement().setAttribute("theme", "secondary");
//        downloadButtonWrapper = new FileDownloadWrapper("fakeFile", new File("fakeFile"));
////                new StreamResource("foo.txt", () -> new ByteArrayInputStream("foo".getBytes())));
//        downloadButtonWrapper.wrapComponent(downloadButton);

        Button exportButton = (new Button("Exportovat", event -> exportFaktExpData()));
        exportButton.getElement().setAttribute("theme", "secondary");

        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.add(
                exportButton
//                , downloadButtonWrapper
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

//    public void openDialog(File inputFile) {
//        viewerTitle.setText(null == inputFile ? "N/A" : inputFile.getName());
//
//        String mimeType = FileTypeResolver.getMIMEType(inputFile);
//        if (StringUtils.isEmpty(mimeType)) {
//            mimeType = "text/plain";
//        }
//        if (mimeType.contains("comma-separated-values")) {
//            mimeType = "text/plain";
//        }
//        StreamResource resource = new StreamResource(inputFile.getName(), () -> {
//            try {
//                return new FileInputStream(inputFile);
//            } catch (FileNotFoundException e) {
//                return new ByteArrayInputStream(new byte[]{});
//            }
//        });
//        resource.setContentType(mimeType);
//
//        openDialog(resource, mimeType);
//    }

    public void openDialog(String faktExpData, String mimeType) {
        docContainer.removeAll();
        dataComponent = new Text(faktExpData);
        docContainer.add(dataComponent);

        setDocContainerHeightByMime(mimeType, docContainer);
        this.open();
    }

    public void openFaktExpDialog(Fakt fakt) {
        this.faktExpData = "123456\npolozka\tEUR\t100333";
        this.faktExpFile = new File(VzmFileUtils.getFaktExpPath(cfgPropsCache.getDocRootServer(), fakt));
        openDialog(faktExpData, "text/plain");
    }

    private void exportFaktExpData() {
        try {
            Files.write(faktExpFile.toPath(), faktExpData.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
