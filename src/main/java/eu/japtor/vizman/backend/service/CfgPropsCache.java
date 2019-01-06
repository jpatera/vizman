package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CfgProp;

public interface CfgPropsCache {

    void loadPropsFromDb();
    String getValue(final String propName);
}
