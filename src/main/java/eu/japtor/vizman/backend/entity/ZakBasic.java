package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
import eu.japtor.vizman.ui.components.DigiIconBox;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
//@ReadOnly
@Entity
@Table(name = "ZAK_BASIC_VIEW")
public class ZakBasic implements Serializable, HasItemType, HasArchState {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CKONT")
    private String ckont;

    @Basic
    @Column(name = "CZAK")
    private Integer czak;

    @Basic
    @Column(name = "SKUPINA")
    private String skupina;

    @Basic
    @Column(name = "ROK")
    private Integer rok;

    @Basic
    @Column(name = "TEXT_KONT")
    private String textKont;

    @Basic
    @Column(name = "TEXT_ZAK")
    private String textZak;

    @Basic
    @Column(name = "OBJEDNATEL")
    private String objednatel;

    @Basic
    @Column(name = "ARCH")
    private Boolean arch;

    @Basic
    @Column(name = "DIGI")
    private Boolean digi;

    @Basic
    @Column(name = "ID_KONT")
    private Long idKont;

    @Transient
    private boolean checked;



    public Long getId() {
        return id;
    }

    public ItemType getTyp() {
        return typ;
    }

    public String getCkont() {
        return ckont;
    }

    public Integer getCzak() {
        return czak;
    }

    public Integer getRok() {
        return rok;
    }

    public String getSkupina() {
        return skupina;
    }

    public String getTextKont() {
        return textKont;
    }

    public String getTextZak() {
        return textZak;
    }

    public String getObjednatel() {
        return objednatel;
    }

    public ArchIconBox.ArchState getArchState() {
        return arch ? ArchIconBox.ArchState.ARCHIVED : ArchIconBox.ArchState.EMPTY;
    }

    public DigiIconBox.DigiState getDigiState() {
        return digi ? DigiIconBox.DigiState.DIGI_ONLY : DigiIconBox.DigiState.EMPTY;
    }

    public Boolean getArch() {
        return arch;
    }

    public void setArch(Boolean arch) {
        this.arch = arch;
    }

    public Boolean getDigi() {
        return digi;
    }

    public void setDigi(Boolean digi) {
        this.digi = digi;
    }

    public Long getIdKont() {
        return idKont;
    }

    @Transient
    public boolean isChecked() {
        return checked;
    }

    @Transient
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Transient
    public String getCkontNotNull() {
        return null == ckont ? "" : ckont;
    }

    @Transient
    public String getCzakNotNull() {
        return null == czak ? "" : czak.toString();
    }

    @Transient
    public String getTextKontNotNull() {
        return null == textKont ? "" : textKont;
    }

    @Transient
    public String getTextZakNotNull() {
        return null == textZak ? "" : textZak.toString();
    }

    @Transient
    public String getCkz() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkontNotNull())
                .append("-")
                .append(getCzakNotNull())
        ;
        return builder.toString();
    }

    @Transient
    public String getCkzTextFull() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkont())
                .append("-")
                .append(getCzak())
                .append(" : ")
                .append(getKzTextFull())
        ;
        return builder.toString();
    }

    @Transient
    public String getKzText() {
        StringBuilder builder = new StringBuilder();
        builder .append(StringUtils.substring(getTextKontNotNull(), 0, 25))
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

    @Transient
    public String getKzTextFull() {
        StringBuilder builder = new StringBuilder();
        builder .append(getTextKontNotNull())
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

    @Transient
    public boolean isSelectableForPruh() {
        return ItemType.ZAK == typ || ItemType.REZ == typ || ItemType.AKV == typ;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return null != id && id.equals(((ZakBasic) other).id);
    }
}
