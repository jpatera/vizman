package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;

import java.util.HashMap;

public class PruhZak extends AbstractPruh implements HasLogger, java.io.Serializable {

    private static final long serialVersionUID = 1L;
//    private NumberFormat pruhDayFormatter = new DecimalFormat("00");

    Long zakId;
    String ckontCzak;
    ItemType itemType;
    String zakText;
    String tmp;

    public PruhZak(String ckontCzak, String zakText) {
        this.ckontCzak = ckontCzak;
        this.zakText = zakText;
        hods = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            hods.put(i, null);
        }
    }

    public Long getZakId() {
        return zakId;
    }
    public void setZakId(Long zakId) {
        this.zakId = zakId;
    }

    public String getCkontCzak() {
        return ckontCzak;
    }
    public void setCkontCzak(String ckontCzak) {
        this.ckontCzak = ckontCzak;
    }

    public ItemType getItemType() {
        return itemType;
    }
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getZakText() {
        return zakText;
    }
    public void setZakText(String zakText) {
        this.zakText = zakText;
    }

    public String getTmp() {
        return tmp;
    }
    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public boolean isRezieZak() {
        return ItemType.REZ == itemType;
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
