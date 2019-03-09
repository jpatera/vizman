package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.backend.entity.ItemType;

import java.math.BigDecimal;

public class PruhZak implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    Long zakId;
    String ckontCzak;
    ItemType itemType;
    String zakText;
    BigDecimal d01;
    BigDecimal d02;


    public PruhZak(String ckontCzak, String zakText) {
        this.ckontCzak = ckontCzak;
        this.zakText = zakText;
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


    public boolean isRezieZak() {
        return ItemType.REZ == itemType;
    }
}
