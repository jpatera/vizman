package eu.japtor.vizman.backend.entity;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ArchIconBox extends FlexLayout {

    private Icon icoKontArchived;
    private Icon icoKontActive;
    private Icon icoZakArchived;

//    static {
//        icoKontActive = VaadinIcon.HAMMER.create();
//        icoKontActive.setColor("green");
//        icoKontActive.setSize("0.8em");
//        icoKontActive.getStyle()
//                .set("theme", "small icon secondary")
//        ;
//
//        icoKontArchived = new Icon(VaadinIcon.CHECK_SQUARE_O);
//        icoKontArchived.setSize("1em");
//        icoKontArchived.getStyle()
//                .set("theme", "small icon secondary")
//        ;
//
//        icoZakArchived = new Icon(VaadinIcon.CHECK);
//        icoZakArchived.setSize("0.8em");
//        icoZakArchived.getStyle()
//                .set("theme", "small icon secondary")
//                .set("padding-left", "1em");
//        ;
//    }

    public ArchIconBox() {
//        FlexLayout box = new FlexLayout();
//        box.setWidth("3em");
//        box.setMinWidth("3em");
//        box.setVerticalComponentAlignment(Alignment.END);
//        box.getStyle()
//                .set("margin-top", "0.7em");
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
                icoKontActive
                ,icoKontArchived
                ,icoZakArchived
        );
    }

    public void showIcon(final ItemType itemType, final boolean archived) {
        icoKontArchived.setVisible(false);
        icoKontActive.setVisible(false);
        icoZakArchived.setVisible(false);

        if (ItemType.KONT == itemType) {
            if (archived) {
                icoKontArchived.setVisible(true);
            } else {
                icoKontActive.setVisible(true);
            }
        } else {
            if (archived) {
                icoZakArchived.setVisible(true);
            }
        }
    }
}
