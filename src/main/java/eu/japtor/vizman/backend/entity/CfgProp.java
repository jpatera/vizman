package eu.japtor.vizman.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CFGPROP", schema = "VIZMAN", catalog = "VIZMANDB")
public class CfgProp extends AbstractEntity {

    private String name;
    private String value;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



    public enum CfgPropName {
        APP_LOCALE("app.locale", "Národní třídění a formáty"),
        PROJECT_ROOT("project.root", "Kořenový adresář pro projekty"),
        DOCUMENT_ROOT("document.root", "Kořenový adresář pro dokumeny")
        ;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        private final String name;
        private final String description;

        CfgPropName(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
