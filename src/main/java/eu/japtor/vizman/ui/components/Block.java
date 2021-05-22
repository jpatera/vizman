package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Span;

public class Block extends Span {

    public Block() {
        this("1em");
    }

    private Block(final String width) {
        super();
        getStyle()
                .set("display", "inline-block")
                .set("width", width);
    }
}
