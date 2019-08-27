package eu.japtor.vizman.app;

public enum CfgPropName {

    APP_LOCALE("app.locale"),
    APP_PROJ_ROOT_LOCAL("app.project.root.local"),
    APP_PROJ_ROOT_SERVER("app.project.root.server"),
    APP_DOC_ROOT_LOCAL("app.document.root.local"),
    APP_DOC_ROOT_SERVER("app.document.root.server"),
    APP_KOEF_REZIE("app.koef.rezie"),
    APP_KOEF_POJIST("app.koef.pojist"),
    APP_KURZ_CZK_EUR("app.kurz.czk.eur"),
    ;

    private String propName;

    CfgPropName(String propName) {
        this.propName = propName;
    }

    public String getName() {
        return propName;
    }
}
