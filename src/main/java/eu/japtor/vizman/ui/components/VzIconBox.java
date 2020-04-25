package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class VzIconBox extends FlexLayout {

    private Icon icoPublic;
    private Icon icoNotPublic;


    public enum VzState {
        PUBLIC, NOTPUBLIC
    }

    public VzIconBox() {
        icoPublic = VaadinIcon.CHECK.create();
        icoPublic.setColor("black");
        icoPublic.setSize("0.8em");
        icoPublic.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoNotPublic = VaadinIcon.MINUS.create();
        icoNotPublic.setSize("1px");
        icoNotPublic.getStyle()
                .set("theme", "small icon secondary")
        ;

        this.add(
                icoPublic
                , icoNotPublic
        );
        this.setAlignItems(Alignment.CENTER);
    }

    public void showIcon(final VzState vzState) {
        if (VzState.NOTPUBLIC == vzState) {
            icoPublic.setVisible(false);
            icoNotPublic.setVisible(true);
        } else {
            icoNotPublic.setVisible(false);
            icoPublic.setVisible(true);
        }
    }
}
