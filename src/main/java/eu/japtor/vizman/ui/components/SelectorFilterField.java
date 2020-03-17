package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.select.Select;

public class SelectorFilterField<T> extends Select<T> {

    public SelectorFilterField() {
        super();
        setSizeFull();
        setEmptySelectionCaption("Vše");
        setEmptySelectionAllowed(true);
//        addValueChangeListener(event -> doFilter());
    }

    public SelectorFilterField(T... items) {
        super(items);
    }
}
