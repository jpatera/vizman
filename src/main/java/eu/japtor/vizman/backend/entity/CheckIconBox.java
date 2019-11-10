package eu.japtor.vizman.backend.entity;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class CheckIconBox extends FlexLayout {

    private Icon icoUnchecked;
    private Icon icoChecked;


    public enum CheckState {
        CHECKED, UNCHECKED
    }

    public CheckIconBox() {
        icoUnchecked = VaadinIcon.THIN_SQUARE.create();
        icoUnchecked.setColor("black");
        icoUnchecked.setSize("0.8em");
        icoUnchecked.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoChecked = new Icon(VaadinIcon.CHECK_SQUARE_O);
        icoChecked.setSize("1em");
        icoChecked.getStyle()
                .set("theme", "small icon secondary")
        ;

        this.add(
                icoUnchecked
                , icoChecked
        );
    }

    public void showIcon(final CheckState checkState) {
        icoChecked.setVisible(false);
        icoUnchecked.setVisible(false);

        if (CheckState.UNCHECKED == checkState) {
            icoUnchecked.setVisible(true);
        } else {
            icoChecked.setVisible(true);
        }
    }
}
