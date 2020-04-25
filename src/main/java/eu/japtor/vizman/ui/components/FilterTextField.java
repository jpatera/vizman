package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class FilterTextField extends TextField {

    public FilterTextField() {
        super();
        setClearButtonVisible(true);
        setSizeFull();
        setPlaceholder("Filtr");
        setValueChangeMode(ValueChangeMode.EAGER);
    }

    public FilterTextField(ValueChangeListener changeListener) {
        this();
        addValueChangeListener(changeListener);
    }
}
