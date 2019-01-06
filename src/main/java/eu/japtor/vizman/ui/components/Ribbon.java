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
        this(Orientation.HORIZONTAL, width);
    }

    public Ribbon(final Orientation orientation, final String size) {
        super();
        if (Orientation.HORIZONTAL == orientation) {
            this.setWidth(size);
        } else {
            this.setHeight(size);
        }
    }
}
