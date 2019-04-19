package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;

import java.math.BigDecimal;
import java.util.Map;

public abstract class AbstractPruh implements HasLogger, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    Map<Integer, BigDecimal> hods;

    public BigDecimal getHod(int day) {
        return hods.get(day);
    }
    public void setHod(int day, BigDecimal hod) {
        hods.put(day, hod);
    }
}
