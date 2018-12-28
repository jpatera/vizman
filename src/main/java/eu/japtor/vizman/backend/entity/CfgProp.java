package eu.japtor.vizman.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CFGPROP")
public class CfgProp extends AbstractEntity {

    private String name;
    private String value;
    private Integer ord;
    private String label;
    private String description;
    private Boolean ro;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Column(name = "VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "ORD")
    public Integer getOrd() {
        return ord;
    }

    protected void setOrd(Integer ord) {
        this.ord = ord;
    }

    @Column(name = "LABEL")
    public String getLabel() {
        return label;
    }

    protected void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "RO")
    public Boolean getRo() {
        return ro;
    }

    protected void setRo(Boolean ro) {
        this.ro = ro;
    }



//    public enum CfgPropName {
//        APP_LOCALE("app.locale"),
//        APP_PROJECT_ROOT("app.project.root"),
//        APP_DOCUMENT_ROOT("app.document.root"),
//        APP_KOEF_REZIE("app.rezie.koef"),
//        APP_KOEF_POJIST("app.pojist.koef")
//        ;
//
//        public String getName() {
//            return name;
//        }
//
//        private final String name;
//
//        CfgPropName(String name) {
//            this.name = name;
//        }
//    }
}
