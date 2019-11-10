package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.select.Select;

public class FilterSelectionField<T> extends Select<T> {

    public FilterSelectionField() {
        super();
        setSizeFull();
        setEmptySelectionCaption("Vše");
        setEmptySelectionAllowed(true);
//        addValueChangeListener(event -> doFilter());
    }

    public FilterSelectionField(T... items) {
        super(items);
    }
}
