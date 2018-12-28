package eu.japtor.vizman.app;

import eu.japtor.vizman.backend.entity.CfgProp;
import eu.japtor.vizman.backend.repository.CfgPropRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CfgProps {

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

    public void loadPropsFromDb() {
        propList = cfgPropRepo.findAll();
        for (CfgProp cfgProp : propList) {
            propsByName.put(cfgProp.getName(), cfgProp);
        }
    }

    public String getValue(final String propName) {
        return propsByName.get(propName).getValue();
    }

}
