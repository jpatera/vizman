package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ItemType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class PruhZak implements HasLogger, java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private NumberFormat pruhDayFormatter = new DecimalFormat("00");

    Long zakId;
    String ckontCzak;
    ItemType itemType;
    String zakText;
    BigDecimal d01;
    BigDecimal d02;
    Map<Integer, BigDecimal> zakHods;
    String tmp;

    public PruhZak(String ckontCzak, String zakText) {
        this.ckontCzak = ckontCzak;
        this.zakText = zakText;
        zakHods = new HashMap<>();
        for (int i = 1; i <= 31; i++) {
            zakHods.put(i, null);
        }
    }

    public BigDecimal getZakHod(int day) {
        return zakHods.get(day);
    }

    public void setZakHod(int day, BigDecimal hod) {
        zakHods.put(day, hod);
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

    public BigDecimal getD01() {
        return d01;
    }
    public void setD01(BigDecimal d01) {
        this.d01 = d01;
    }

    public BigDecimal getD02() {
        return d02;
    }
    public void setD02(BigDecimal d02) {
        this.d02 = d02;
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

    public void setValueToDayField(int day, BigDecimal value) {
        switch (day) {
            case 1:
                d01 = value; break;
            case 2:
                d02 = value; break;
        }
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
