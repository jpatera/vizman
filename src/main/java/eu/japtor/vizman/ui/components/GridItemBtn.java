package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class GridItemBtn extends Button {

    public GridItemBtn(final ComponentEventListener openItemEditDialog) {
        this(openItemEditDialog, null, null, null);
    }

    public GridItemBtn(final ComponentEventListener openItemEditDialog, Icon btnIco) {
        this(openItemEditDialog, btnIco, null, null);
    }

    public GridItemBtn(
            final ComponentEventListener openItemEditDialog
            , final Icon btnIco
            , final String icoColor
            , final String toolTip
        ) {
        super();
        Icon ico = btnIco == null ? new Icon(VaadinIcon.EDIT) : btnIco;
        if (null != icoColor) {
            ico.setColor(icoColor);
        }
        this.setIcon(ico);
        this.getElement().setAttribute("theme", "tertiary small icon");
        if (null != toolTip) {
            this.getElement().setProperty("title", toolTip);
        }
        this.addClickListener(openItemEditDialog);
    }

}
