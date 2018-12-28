package eu.japtor.vizman.app;

public enum CfgPropName {

    APP_LOCALE("app.locale"),
    APP_PROJECT_ROOT("app.project.root"),
    APP_DOCUMENT_ROOT("app.document.root"),
    APP_KOEF_REZIE("app.koef.rezie"),
    APP_KOEF_POJIST("app.koef.pojist"),
    ;

    private String propName;

    CfgPropName(String propName) {
        this.propName = propName;
    }

    public String getName() {
        return propName;
    }
}
