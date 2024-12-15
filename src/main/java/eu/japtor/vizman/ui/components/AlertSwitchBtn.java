package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Consumer;

public class AlertSwitchBtn extends Button {

    public AlertSwitchBtn(final Consumer<Boolean> buttonActionConsumer, final boolean initActionAlert) {
        super();

        Icon alertActiveIcon = VaadinIcon.WARNING.create();
        Icon alertInactiveIcon = VaadinIcon.WARNING.create();
        alertActiveIcon.setColor("red");
        alertInactiveIcon.setColor("silver");

        this.setHeight("32px");
        this.setText("");
        this.setIcon(initActionAlert ? alertActiveIcon : alertInactiveIcon);
        this.getStyle()
//                .set("theme", "tertiary");
                .set("theme", "icon medium tertiary")
                .set("margin-right", "8px")
                .set("margin-left", "12px")
                .set("padding-left", "0")
                .set("padding-right", "0")
                .set("max-width", "32px")
                .set("min-width", "32px")
//        mainResizeBtn.getElement().setProperty("flexGrow", (double)0)
        ;
        this.addClickListener(event -> {
            buttonActionConsumer.accept(this.getIcon() == alertActiveIcon);
//            this.setIcon(this.getIcon() == alertInactiveIcon ? alertActiveIcon : alertInactiveIcon);
        });
    }
}
