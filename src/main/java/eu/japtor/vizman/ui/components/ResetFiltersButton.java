package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ResetFiltersButton extends Button {
    private static final String DEFAULT_TOOLTIP_TEXT = "Vynuluje filtry";

    public ResetFiltersButton(final ComponentEventListener reloadListener) {
        this(null, reloadListener);
    }

    public ResetFiltersButton(final String tooltipText, final ComponentEventListener reloadView) {
        super();

        Icon reloadIcon = VaadinIcon.CLOSE_CIRCLE_O.create();
        reloadIcon.getStyle()
                .set("theme", "small")
        ;

        this.setIcon(reloadIcon);
        this.getElement().setAttribute("theme", "icon secondary small");

        // Simple, no formatting posible:
        this.getElement().setProperty("title", DEFAULT_TOOLTIP_TEXT);

        this.addClickListener(reloadView);
    }
}
