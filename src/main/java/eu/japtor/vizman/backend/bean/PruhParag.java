package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PruhParag extends AbstractPruh implements HasLogger, java.io.Serializable {

    private static final long serialVersionUID = 1L;
//    private NumberFormat pruhDayFormatter = new DecimalFormat("00");

    Long paragId;
    String cparag;
//    ItemType itemType;
    String paragText;

    public PruhParag(String cparag, String paragText) {
        this.cparag = cparag;
        this.paragText = this.paragText;
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

    public String getParagText() {
        return paragText;
    }
    public void setParagText(String paragText) {
        this.paragText = paragText;
    }
}
