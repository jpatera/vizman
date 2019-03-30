package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;

import java.util.HashMap;

public class PruhZak extends AbstractPruh implements HasLogger, java.io.Serializable {

    private static final long serialVersionUID = 1L;
//    private NumberFormat pruhDayFormatter = new DecimalFormat("00");

    Long zakId;
    String ckont;
    Integer czak;
    ItemType itemType;
    String text;
    String tmp;

    public PruhZak(Long zakId, ItemType itemType, String ckont, Integer czak, String text) {
        this.zakId = zakId;
        this.itemType = itemType;
        this.ckont = ckont;
        this.czak = czak;
        this.text = text;
        hods = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            hods.put(i, null);
        }
    }

    public Long getZakId() {
        return zakId;
    }

    public ItemType getItemType() {
        return itemType;
    }
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getCkont() {
        return ckont;
    }
    public void setCkont(String ckont) {
        this.ckont = ckont;
    }

    public Integer getCzak() {
        return czak;
    }
    public void setCzak(Integer czak) {
        this.czak = czak;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getTmp() {
        return tmp;
    }
    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getPruhCellText() {
        if (isRezijniZak()) {
            return String.format("%s, %s", ckont, text);
        } else {
            return String.format("%s / %d, %s", ckont, czak, text);
        }
    }

    public boolean isRezijniZak() {
        return ("00001".equals(ckont) || "00004".equals(ckont));
    }

    public boolean isRezieZak() {
        return ItemType.REZ == itemType;
    }

    public boolean isLekarZak() {
        return ItemType.LEK == itemType;
    }


//    public void setValueToDayField(int day, BigDecimal value) {
//        String dayFieldName = "D" + pruhDayFormatter.format(day);
//        Class pzClass = this.getClass();
//        Field dayField;
//        try {
//            dayField = this.getClass().getField(dayFieldName);
//            dayField.set(pzClass, value.toString());
//        } catch (NoSuchFieldException e) {
//            getLogger().error("Day field for {} not found.", dayFieldName, e);
//            return;
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
}
