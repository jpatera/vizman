package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class AlertModifIconBox extends FlexLayout {

    private final Icon icoActive;
    private final Icon icoInactive;


    public enum AlertModifState {
        ACTIVE, INACTIVE
    }

    public AlertModifIconBox() {
        icoActive = VaadinIcon.WARNING.create();
        icoActive.setColor("red");
        icoActive.setSize("0.8em");
        icoActive.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoInactive = VaadinIcon.WARNING.create();
        icoInactive.setColor("silver");
        icoInactive.setSize("1em");
        icoInactive.getStyle()
                .set("theme", "small icon secondary")
        ;

        this.add(
                icoActive
                , icoInactive
        );
        this.setAlignItems(Alignment.CENTER);
    }

    public void showIcon(final AlertModifState alertModifState) {
        if (alertModifState.INACTIVE == alertModifState) {
            icoActive.setVisible(false);
            icoInactive.setVisible(true);
        } else {
            icoInactive.setVisible(false);
            icoActive.setVisible(true);
        }
    }
}
