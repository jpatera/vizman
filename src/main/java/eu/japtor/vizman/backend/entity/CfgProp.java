package eu.japtor.vizman.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CFGPROP")
public class CfgProp extends AbstractGenIdEntity {

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "ORD")
    private Integer ord;

    @Column(name = "LABEL")
    private String label;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "RO")
    private Boolean ro;


    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOrd() {
        return ord;
    }

    protected void setOrd(Integer ord) {
        this.ord = ord;
    }

    public String getLabel() {
        return label;
    }

    protected void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRo() {
        return ro;
    }

    protected void setRo(Boolean ro) {
        this.ro = ro;
    }
}
