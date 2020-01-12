package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class HiddenIconBox extends FlexLayout {

    private Icon icoVisible;
    private Icon icoHidden;


    public enum HiddenState {
        VISIBLE, HIDDEN
    }

    public HiddenIconBox() {
        icoVisible = VaadinIcon.USER_CHECK.create();
        icoVisible.setColor("black");
        icoVisible.setSize("0.8em");
        icoVisible.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoHidden = VaadinIcon.EYE_SLASH.create();
        icoHidden.setSize("1em");
        icoHidden.getStyle()
                .set("theme", "small icon secondary")
        ;

        this.add(
                icoVisible
                , icoHidden
        );
        this.setAlignItems(Alignment.CENTER);
    }

    public void showIcon(final HiddenState hiddenState) {
        if (HiddenState.HIDDEN == hiddenState) {
            icoVisible.setVisible(false);
            icoHidden.setVisible(true);
        } else {
            icoHidden.setVisible(false);
            icoVisible.setVisible(true);
        }
    }
}
