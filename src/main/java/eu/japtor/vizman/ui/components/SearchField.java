package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class SearchField extends TextField {

    public SearchField(final String placeholder, ValueChangeListener updateContent) {
        super();
        this.setPlaceholder(placeholder);
        this.setPrefixComponent(new Icon("lumo", "search"));
        this.setSuffixComponent(buildClearButton());
        this.addClassName("view-toolbar__search-field");
        this.setHeight("32px");
//        this.getStyle().set("theme", "small");
        this.addValueChangeListener(updateContent);
        this.setValueChangeMode(ValueChangeMode.EAGER);
    }

    private Button buildClearButton() {
        Button clearBtn = new Button();
        clearBtn.setText("");
//        clearBtn.setIcon(new Icon("lumo", "cross"));
        Icon iconCross = new Icon("lumo", "cross");
        iconCross.setSize("20px");
        clearBtn.setIcon(iconCross);
//        this.addClassName("review__edit");
        clearBtn.getStyle()
                .set("theme", "icon small primary")
                .set("margin", "0");
        clearBtn.setHeight("25px");

//        clearBtn.getElement().setAttribute("theme", "small");
//        clearBtn.getElement().setAttribute("size", "");
        clearBtn.addClickListener(event -> this.clear());
        return  clearBtn;
    }
}
