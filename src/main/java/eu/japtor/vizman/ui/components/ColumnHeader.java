package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import org.apache.commons.lang3.StringUtils;

public class ColumnHeader extends Label {

    public ColumnHeader(boolean showInfoIcon, final String text, final String tooltip ) {
        super();
        this.setText(text);
        if (StringUtils.isNotBlank(tooltip)) {
            this.getElement().setProperty("title", tooltip);
        }
        if (showInfoIcon) {
            Icon infoIcon = new InfoIconForColumn("maroon");
//            if (StringUtils.isNotBlank(tooltip)) {
//                infoIcon.getElement().setProperty("title", tooltip);
//            }
            this.add(infoIcon);
        }
    }
}
