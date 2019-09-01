package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CfgProp;

import java.math.BigDecimal;

public interface CfgPropsCache {

    void loadPropsFromDb();
    String getStringValue(final String propName);
    BigDecimal getBigDecimalValue(final String propName);

    String getDocRootServer();
    String getProjRootServer();
    String getDocRootLocal();
    String getProjRootLocal();


}
