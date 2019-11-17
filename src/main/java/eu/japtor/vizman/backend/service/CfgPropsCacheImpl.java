package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CfgProp;
import eu.japtor.vizman.backend.repository.CfgPropRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CfgPropsCacheImpl implements CfgPropsCache {

    public static final LocalTime PRAC_DOBA_START = LocalTime.of(8, 0, 0);
    public static final LocalTime PRAC_DOBA_END = LocalTime.of(18, 0, 0);

    private Map<String, CfgProp> propsByName = new HashMap<>();
    private List<CfgProp> propList;

    @Autowired
    CfgPropRepo cfgPropRepo;

//    @Autowired
//    public CfgProps() {
//
////        propList = cfgPropRepo.findAll();
////        for (CfgProp cfgProp : propList) {
////            propsByName.put(cfgProp.getName(), cfgProp);
////        }
////
////        this.appLocale = cfgPropRepo.findByName(CfgProp.CfgPropName.APP_LOCALE.toString());
////        this.documentRoot = cfgPropRepo.findByName(CfgProp.CfgPropName.APP_DOCUMENT_ROOT.toString());
////        this.projectRoot = cfgPropRepo.findByName(CfgProp.CfgPropName.APP_PROJECT_ROOT.toString());
//    }

    @PostConstruct
    public void init() {
        loadPropsFromDb();
    }

    @Override
    public void loadPropsFromDb() {
        propList = cfgPropRepo.findAll();
        propsByName.clear();
        for (CfgProp cfgProp : propList) {
            propsByName.put(cfgProp.getName(), cfgProp);
        }
    }

    @Override
    public String getStringValue(final String propName) {
        return (String)propsByName.get(propName).getValue();
    }

    @Override
    public BigDecimal getBigDecimalValue(final String propName) {
//        return new BigDecimal(propsByName.get(propName).getValue());
        return propsByName.get(propName).getValueDecimal();
    }

    @Override
    public String getProjRootServer() {
        return getStringValue("app.project.root.server");
    }

    @Override
    public String getDocRootServer() {
        return getStringValue("app.document.root.server");
    }

    @Override
    public String getProjRootLocal() {
        return getStringValue("app.project.root.local");
    }

    @Override
    public String getDocRootLocal() {
        return getStringValue("app.document.root.local");
    }

}
