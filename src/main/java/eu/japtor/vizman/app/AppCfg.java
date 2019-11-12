package eu.japtor.vizman.app;

import java.math.BigDecimal;

public class AppCfg {

    private String appLocale;
    private String appProjRootLocal;
    private String appProjRootServer;
    private String appDocRootLocal;
    private String appDocRootServer;
    private BigDecimal appKoefRezie;
    private BigDecimal appKoefPojist;
    private BigDecimal appKurzCzkEur;

    public String getAppLocale() {
        return appLocale;
    }
    public void setAppLocale(String appLocale) {
        this.appLocale = appLocale;
    }

    public String getAppProjRootLocal() {
        return appProjRootLocal;
    }
    public void setAppProjRootLocal(String appProjRootLocal) {
        this.appProjRootLocal = appProjRootLocal;
    }

    public String getAppProjRootServer() {
        return appProjRootServer;
    }
    public void setAppProjRootServer(String appProjRootServer) {
        this.appProjRootServer = appProjRootServer;
    }

    public String getAppDocRootLocal() {
        return appDocRootLocal;
    }
    public void setAppDocRootLocal(String appDocRootLocal) {
        this.appDocRootLocal = appDocRootLocal;
    }

    public String getAppDocRootServer() {
        return appDocRootServer;
    }
    public void setAppDocRootServer(String appDocRootServer) {
        this.appDocRootServer = appDocRootServer;
    }

    public BigDecimal getAppKoefRezie() {
        return appKoefRezie;
    }
    public void setAppKoefRezie(BigDecimal appKoefRezie) {
        this.appKoefRezie = appKoefRezie;
    }

    public BigDecimal getAppKoefPojist() {
        return appKoefPojist;
    }
    public void setAppKoefPojist(BigDecimal appKoefPojist) {
        this.appKoefPojist = appKoefPojist;
    }

    public BigDecimal getAppKurzCzkEur() {
        return appKurzCzkEur;
    }
    public void setAppKurzCzkEur(BigDecimal appKurzCzkEur) {
        this.appKurzCzkEur = appKurzCzkEur;
    }
}
