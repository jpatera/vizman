package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Consumer;

public class ResizeBtn extends Button {

    Icon compressIcon = new Icon (VaadinIcon.EDIT.COMPRESS_SQUARE);
    Icon expandIcon = new Icon (VaadinIcon.EDIT.EXPAND_SQUARE);

//    public ResizeBtn(final ComponentEventListener itemAction) {
    public ResizeBtn(final Consumer<Boolean> buttonActionConsumer, final boolean initActionExpand) {
        super();
        this.setHeight("26px");
        this.setText("");
        this.setIcon(initActionExpand ? expandIcon : compressIcon);
//        this.addClassName("review__edit");
        this.getStyle()
//                .set("theme", "tertiary");
                .set("theme", "icon small tertiary")
                .set("margin-right", "8px")
                .set("margin-left", "12px")
                .set("padding-left", "0")
                .set("padding-right", "0")
                .set("max-width", "26px")
                .set("min-width", "26px")
//        mainResizeBtn.getElement().setProperty("flexGrow", (double)0)
        ;
        this.addClickListener(event -> {
            buttonActionConsumer.accept(this.getIcon() == compressIcon);
            this.setIcon(this.getIcon() == compressIcon ? expandIcon : compressIcon);
        });
    }
}
