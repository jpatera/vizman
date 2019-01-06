package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.io.IOException;

public class OpenDirField extends TextField {

    private static Icon warningIcon = new Icon(VaadinIcon.WARNING);
    private static Icon okIcon = new Icon(VaadinIcon.CHECK);
    static{
        warningIcon.setColor("red");
        okIcon.setColor("green");
    }

    Button openDirBtn;


    public OpenDirField(
            final String label
            , final String placeholder
            , ValueChangeListener updateContent
    ) {
        super();
        this.setLabel(label);
        this.setPlaceholder(placeholder);
        this.setSuffixComponent(initOpenDirBtn());
//        this.addClassName("view-toolbar__search-field");
//        this.setHeight("32px");
//        this.getStyle().set("theme", "small");
        this.addValueChangeListener(updateContent);
        this.setValueChangeMode(ValueChangeMode.EAGER);

//        this.setPrefixComponent(warningIcon);
//        updateContent.valueChanged(new ValueChangeEvent<Object>() {
//        });
        this.setValue(this.getValue());
    }

    public void showDirNotFoundWarning(boolean showWarning) {
        this.setPrefixComponent(showWarning ? warningIcon : okIcon);
    }

    private Button initOpenDirBtn() {
//        openDirBtn = new Button();
        openDirBtn = new OpenDirBtn(event -> openDir(this.getValue()));
//        openDirBtn.setText("");
//        clearBtn.setIcon(new Icon("lumo", "cross"));
//        Icon openDirIcon = new Icon(VaadinIcon.FOLDER_OPEN);
//        openDirIcon.setSize("20px");
//        openDirBtn.setIcon(openDirIcon);
//        this.addClassName("review__edit");
        openDirBtn.getStyle()
                .set("theme", "icon small primary")
                .set("margin", "0");
        openDirBtn.setHeight("25px");

//        clearBtn.getElement().setAttribute("theme", "small");
//        clearBtn.getElement().setAttribute("size", "");
        return  openDirBtn;
    }

    private void openDir(String path) {
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
