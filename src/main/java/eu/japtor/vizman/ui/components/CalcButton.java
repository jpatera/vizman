package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class CalcButton extends Button {
    private static final String DEFAULT_TOOLTIP_TEXT = "Přepočítá tabulku dle parametrů";

    private Icon calcIconClean;
    private Icon calcIconDirty;

    public CalcButton(final ComponentEventListener reloadListener) {
        this(null, reloadListener);
    }

    public CalcButton(final String tooltipText, final ComponentEventListener reloadView) {
        super();

        calcIconClean = new Icon(VaadinIcon.CALC);
        calcIconClean.getStyle()
                .set("theme", "small");

        calcIconDirty = new Icon(VaadinIcon.CALC);
        calcIconDirty.setColor("magenta");
        calcIconDirty.getStyle()
                .set("theme", "small");

        this.setIcon(calcIconClean);
        this.getStyle()
                .set("theme", "icon secondary")
        ;

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

    public void setIconDirty() {
        this.setIcon(calcIconDirty);
    }

    public void setIconClean() {
        this.setIcon(calcIconClean);
    }
}
