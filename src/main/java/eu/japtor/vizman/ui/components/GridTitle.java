package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.H3;

import java.util.Locale;

public class GridTitle extends H3 {

    public GridTitle(String titleText) {
        this.setText(titleText.toUpperCase(Locale.ROOT));
        this.getStyle().set("margin-left", "0.5em");
        this.getStyle().set("margin-right", "0.5em");
        this.getStyle().set("margin-top", "0.2em");
        this.getStyle().set("margin-bottom", "0.2em");
    }
}
