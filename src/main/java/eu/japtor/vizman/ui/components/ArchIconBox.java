package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import eu.japtor.vizman.backend.entity.ItemType;

public class ArchIconBox extends FlexLayout {

    private Icon icoKontEmpty;
    private Icon icoKontArchived;
    private Icon icoKontActive;
    private Icon icoZakArchived;


    // TODO: extract and move to backend.bean
    public enum ArchState {
        EMPTY, ACTIVE, ARCHIVED
    }

    public ArchIconBox() {
        icoKontEmpty = VaadinIcon.CIRCLE_THIN.create();
        icoKontEmpty.setColor("peru");
        icoKontEmpty.setSize("0.8em");
        icoKontEmpty.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoKontActive = VaadinIcon.HAMMER.create();
        icoKontActive.setColor("green");
        icoKontActive.setSize("0.8em");
        icoKontActive.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoKontArchived = new Icon(VaadinIcon.CHECK_SQUARE_O);
        icoKontArchived.setSize("1em");
        icoKontArchived.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoZakArchived = new Icon(VaadinIcon.CHECK);
        icoZakArchived.setSize("0.8em");
        icoZakArchived.getStyle()
                .set("theme", "small icon secondary")
                .set("padding-left", "1em");
        ;

        this.add(
                icoKontEmpty
                , icoKontActive
                ,icoKontArchived
                ,icoZakArchived
        );
    }

    public void showIcon(final ItemType itemType, final ArchState archState) {
        icoKontEmpty.setVisible(false);
        icoKontArchived.setVisible(false);
        icoKontActive.setVisible(false);
        icoZakArchived.setVisible(false);

        if (ItemType.KONT == itemType) {
            if (ArchState.EMPTY == archState) {
                icoKontEmpty.setVisible(true);
            }  else if (ArchState.ARCHIVED == archState) {
                icoKontArchived.setVisible(true);
            } else {
                icoKontActive.setVisible(true);
            }
        } else {
            if (ArchState.ARCHIVED == archState) {
                icoZakArchived.setVisible(true);
            }
        }
    }
}
