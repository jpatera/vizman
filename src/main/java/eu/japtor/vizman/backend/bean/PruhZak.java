package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zak;
import org.apache.commons.lang3.StringUtils;

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

    public PruhZak(Zak zak) {
        this.zakId = zak.getId();
        this.itemType = zak.getTyp();
        this.ckont = zak.getCkont();
        this.czak = zak.getCzak();
        this.text = StringUtils.substring(zak.getKont().getText(), 0, 15) + " / "
                +  StringUtils.substring(zak.getText(), 0, 15);
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
        if (isNonZakItem()) {
            return String.format("%s, %s", ckont, text);
        } else {
            return String.format("%s / %d, %s", ckont, czak, text);
        }
    }

    public boolean isNonZakItem() {
        return (isRezieZak() || isLekarZak());
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
