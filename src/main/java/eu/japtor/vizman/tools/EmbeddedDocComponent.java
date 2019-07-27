package eu.japtor.vizman.tools;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.StreamResource;

@Tag("object")
public class EmbeddedDocComponent extends Div implements HasSize {

    public EmbeddedDocComponent(StreamResource resource, String mimeType) {
        this(mimeType);
        getElement().setAttribute("data", resource);
    }

    public EmbeddedDocComponent(String url, String mimeType) {
        this(mimeType);
        getElement().setAttribute("data", url);
    }

    protected EmbeddedDocComponent(String mimeType) {
//        getElement().setAttribute("type", "application/pdf");
        getElement().setAttribute("type", mimeType);
        setSizeFull();
    }
}
