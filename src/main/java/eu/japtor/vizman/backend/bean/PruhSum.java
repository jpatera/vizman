package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;

import java.util.HashMap;

public class PruhSum extends AbstractPruh implements HasLogger {

    Long sumId;
    String sumText;

    public PruhSum(String sumText) {
        this.sumText = this.sumText;
        this.hods = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            hods.put(i, null);
        }
    }

    public Long getSumId() {
        return sumId;
    }
    public void setSumId(Long paragId) {
        this.sumId = sumId;
    }

    public String getSumText() {
        return sumText;
    }
    public void setSumText(String sumText) {
        this.sumText = sumText;
    }
}
