package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PruhParag extends AbstractPruh implements HasLogger, java.io.Serializable {

    private static final long serialVersionUID = 1L;
//    private NumberFormat pruhDayFormatter = new DecimalFormat("00");

    Long paragId;
    ItemType itemType;
    String cparag;
    String text;

    public PruhParag(String cparag, ItemType itemType, String text) {
        this.cparag = cparag;
        this.text = text;
        this.hods = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            hods.put(i, null);
        }
    }

    public Long getParagId() {
        return paragId;
    }
    public void setParagId(Long paragId) {
        this.paragId = paragId;
    }

    public String getCparag() {
        return cparag;
    }
    public void setCparag(String cparag) {
        this.cparag = cparag;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getPruhCellText() {
        return String.format("%s, %s", cparag, text);
    }
}
