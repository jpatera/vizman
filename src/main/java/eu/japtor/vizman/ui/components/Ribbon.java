package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Span;

public class Ribbon extends Span {

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public Ribbon() {
        this("1em");
    }

    public Ribbon(final String width) {
        this(Orientation.HORIZONTAL, width, "1em");
    }

    public Ribbon(final String width, final String widthMin) {
        this(Orientation.HORIZONTAL, width, widthMin);
    }

    private Ribbon(final Orientation orientation, final String size, final String sizeMin) {
        super();
        if (Orientation.HORIZONTAL == orientation) {
            this.setWidth(size);
            this.setMinWidth(sizeMin);
        } else {
            this.setHeight(size);
            this.setMinHeight(sizeMin);
        }
    }
}
