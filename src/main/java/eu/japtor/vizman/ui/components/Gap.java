package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Span;

public class Gap extends Span {

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public Gap() {
        this("1em");
    }

    public Gap(final String width) {
        this(Orientation.HORIZONTAL, width);
    }

    public Gap(final Orientation orientation, final String size) {
        super();
        if (Orientation.HORIZONTAL == orientation) {
            this.setWidth(size);
            this.getStyle()
                    .set("min-width", size)
                    .set("max-width", size)
            ;
        } else {
            this.setHeight(size);
        }
    }
}
