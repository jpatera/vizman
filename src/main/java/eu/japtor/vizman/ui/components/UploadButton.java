package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public class UploadButton {
    private MemoryBuffer buffer;
    private Upload upload;
    private Component initUploadDocButton() {
//        registerDocButton = new NewItemButton("Dokument", event -> {});
//        return registerDocButton;

//        buffer = new MultiFileMemoryBuffer();
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setHeight("1em");
        upload.setAutoUpload(false);
        upload.setDropAllowed(false);
//        upload.setUploadButton();

//        upload.setUploadButton(initRegisterDocButton());

        upload.addAttachListener(event -> {
            System.out.println("Attached: " + event.getSource());
        });


//        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addStartedListener(event -> {
            System.out.println("Started: " + event.getFileName());
        });

        upload.addFailedListener(event -> {
            System.out.println("Failed: " + event.getFileName());
        });

        upload.addFinishedListener(event -> {
            System.out.println("Finished: " + event.getFileName());
        });

        upload.addProgressListener(event -> {
            System.out.println("Progress: " + event.getContentLength());
        });

        upload.addSucceededListener(event -> {
            System.out.println("Succeeded: " + event.getFileName());
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(),
//                    buffer.getInputStream(event.getFileName()));
//            showOutput(event.getFileName(), component, output);
        });
//
//        upload.addSucceededListener(event -> {
//            event.getFileName(), buffer.getInputStream());
//
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(), buffer.getInputStream());
//            showOutput(event.getFileName(), component, output);
//        });
//        upload.

        return upload;
    }



    //    private Component createNonImmediateUpload() {
//        Div output = new Div();
//
//        // begin-source-example
//        // source-example-heading: Non immediate upload
//        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
//        Upload upload = new Upload(buffer);
//        upload.setAutoUpload(false);
//
//        upload.addSucceededListener(event -> {
//            Component component = createComponent(event.getMIMEType(),
//                    event.getFileName(),
//                    buffer.getInputStream(event.getFileName()));
//            showOutput(event.getFileName(), component, output);
//        });
//        // end-source-example
//        upload.setMaxFileSize(200 * 1024);
//
//        return output;
//        addCard("Non immediate upload", upload, output);
//    }
//
//    private Component createComponent(String mimeType, String fileName,
//                                      InputStream stream) {
//        if (mimeType.startsWith("text")) {
//            String text = "";
//            try {
//                text = IOUtils.toString(stream, "UTF-8");
//            } catch (IOException e) {
//                text = "exception reading stream";
//            }
//            return new Text(text);
//        } else if (mimeType.startsWith("image")) {
//            Image image = new Image();
//            try {
//
//                byte[] bytes = IOUtils.toByteArray(stream);
//                image.getElement().setAttribute("src", new StreamResource(
//                        fileName, () -> new ByteArrayInputStream(bytes)));
//                try (ImageInputStream in = ImageIO.createImageInputStream(
//                        new ByteArrayInputStream(bytes))) {
//                    final Iterator<ImageReader> readers = ImageIO
//                            .getImageReaders(in);
//                    if (readers.hasNext()) {
//                        ImageReader reader = readers.next();
//                        try {
//                            reader.setInput(in);
//                            image.setWidth(reader.getWidth(0) + "px");
//                            image.setHeight(reader.getHeight(0) + "px");
//                        } finally {
//                            reader.dispose();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return image;
//        }
//        Div content = new Div();
//        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
//                mimeType, MessageDigestUtil.sha256(stream.toString()));
//        content.setText(text);
//        return new Div();
//
//    }
//
//    private void showOutput(String text, Component content,
//                            HasComponents outputContainer) {
//        HtmlComponent p = new HtmlComponent(Tag.P);
//        p.getElement().setText(text);
//        outputContainer.add(p);
//        outputContainer.add(content);
//    }

}
