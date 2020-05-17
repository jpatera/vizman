package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ReloadButton extends Button {
    private static final String DEFAULT_TOOLTIP_TEXT = "Načte znova data";

//    Tooltip tooltip;

    public ReloadButton(final ComponentEventListener reloadListener) {
        this(null, reloadListener);
    }

    public ReloadButton(final String tooltipText, final ComponentEventListener reloadView) {
        super();

        Icon reloadIcon = VaadinIcon.REFRESH.create();
        reloadIcon.getStyle()
                .set("theme", "small")
        ;

        this.setIcon(reloadIcon);
        this.getElement().setAttribute("theme", "icon secondary small");

        // TODO: add tooltips
//        This does  not work:
//        this.getElement().setAttribute("tooltip", null == tooltipText ? DEFAULT_TOOLTIP_TEXT : tooltipText);

// Asi potrebuje prime licenci
//        Tooltip tooltip = new Tooltip();
//        tooltip.setPosition(TooltipPosition.RIGHT);
//        tooltip.setAlignment(TooltipAlignment.LEFT);
//        tooltip.add(new H5("Hello"));
//        tooltip.add(new Paragraph("This is an example of how to use it"));
//        tooltip.attachToComponent(this);
//        tooltip.setEnabled(true);

        // Simple, no formatting posiible:
        this.getElement().setProperty("title", DEFAULT_TOOLTIP_TEXT);

        this.addClickListener(reloadView);
    }
}
