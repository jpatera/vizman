package eu.japtor.vizman.ui.components;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import eu.japtor.vizman.backend.entity.ItemType;

public class DigiIconBox extends FlexLayout {

    private Icon icoEmpty;
    private Icon icoDigiOnly;
    private Icon icoPaperAndDigi;
    private Icon icoZakDigiOnly;


    // TODO: extract and move to backend.bean
    public enum DigiState {
        EMPTY, DIGI_ONLY, PAPER_AND_DIGI
    }

    public DigiIconBox() {
        icoEmpty = VaadinIcon.CIRCLE_THIN.create();
        icoEmpty.setColor("peru");
        icoEmpty.setSize("0.8em");
        icoEmpty.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoDigiOnly = VaadinIcon.DISC.create();
        icoDigiOnly.setColor("mediumVioletRed");
        icoDigiOnly.setSize("0.8em");
        icoDigiOnly.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoPaperAndDigi = VaadinIcon.MINUS.create();
        icoPaperAndDigi.setSize("1em");
        icoPaperAndDigi.getStyle()
                .set("theme", "small icon secondary")
        ;

        icoZakDigiOnly = VaadinIcon.CHECK.create();
        icoZakDigiOnly.setColor("mediumVioletRed");
        icoZakDigiOnly.setSize("0.8em");
        icoZakDigiOnly.getStyle()
                .set("theme", "small icon secondary")
                .set("padding-left", "1em");
        ;

        this.add(
                icoEmpty
                , icoDigiOnly
                , icoPaperAndDigi
                , icoZakDigiOnly
        );
    }

    public void showIcon(final ItemType itemType, final DigiState digiState) {
        icoEmpty.setVisible(false);
        icoDigiOnly.setVisible(false);
        icoPaperAndDigi.setVisible(false);
        icoZakDigiOnly.setVisible(false);

        if (ItemType.KONT == itemType) {
            if (DigiState.EMPTY == digiState) {
                icoEmpty.setVisible(true);
            }  else if (DigiState.DIGI_ONLY == digiState) {
                icoDigiOnly.setVisible(true);
            } else {
                icoPaperAndDigi.setVisible(true);
            }
        } else {
            if (DigiState.DIGI_ONLY == digiState) {
                icoZakDigiOnly.setVisible(true);
            }
        }
    }
}
