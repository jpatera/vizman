package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.AppCfg;
import eu.japtor.vizman.app.CfgPropName;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.CfgProp;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.CfgPropRepo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;


@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
})
@SpringComponent
@UIScope
public class CfgPropsForm extends VerticalLayout {

    private Map<String, TextField> fieldMap = new LinkedHashMap<>();
    private Binder<AppCfg> binder = new Binder<>();
    private AppCfg appCfg = new AppCfg();

    @Autowired
    public CfgPropRepo cfgPropRepo;


    public CfgPropsForm() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);
//        this.setWidth("100%");
    }

    @PostConstruct
    public void init() {
        initFieldMap();
        buildPropsContainer();
        binder.readBean(appCfg);
    }

    private void initFieldMap() {

        for (CfgPropName cfgPropName : CfgPropName.values()) {
            String propName = cfgPropName.getName();
            CfgProp cfgProp = cfgPropRepo.findByName(propName);

            if (null != cfgProp) {
                TextField field = new TextField();
                field.setId(propName);
                field.setLabel(cfgProp.getLabel());
                field.setReadOnly(cfgProp.getRo());
                populateBeanAndBindField(cfgPropName, cfgProp.getValue(), field);
                fieldMap.put(propName, field);
            }
        }
    }

    private void populateBeanAndBindField(CfgPropName cfgPropName, String value, TextField field) {

        switch (cfgPropName) {
            case APP_LOCALE:
                appCfg.setAppLocale(value);
                binder.forField(field).bind(AppCfg::getAppLocale, null);
                break;
            case APP_DOC_ROOT_LOCAL:
                appCfg.setAppDocRootLocal(value);
                binder.forField(field).bind(AppCfg::getAppDocRootLocal, AppCfg::setAppDocRootLocal);
                break;
            case APP_DOC_ROOT_SERVER:
                appCfg.setAppDocRootServer(value);
                binder.forField(field).bind(AppCfg::getAppDocRootServer, AppCfg::setAppDocRootServer);
                break;
            case APP_PROJ_ROOT_LOCAL:
                appCfg.setAppProjRootLocal(value);
                binder.forField(field).bind(AppCfg::getAppProjRootLocal, AppCfg::setAppProjRootLocal);
                break;
            case APP_PROJ_ROOT_SERVER:
                appCfg.setAppProjRootServer(value);
                binder.forField(field).bind(AppCfg::getAppProjRootServer, AppCfg::setAppProjRootServer);
                break;
            case APP_KOEF_POJIST:
                appCfg.setAppKoefPojist(value);
                binder.forField(field).bind(AppCfg::getAppKoefPojist, AppCfg::setAppKoefPojist);
                break;
            case APP_KOEF_REZIE:
                appCfg.setAppKoefRezie(value);
                binder.forField(field).bind(AppCfg::getAppKoefRezie, AppCfg::setAppKoefRezie);
                break;
        }
    }


    private void buildPropsContainer() {

//        setClassName("view-container");
        setAlignItems(Alignment.STRETCH);

        Component propsHeader = new H4("APLIKACE");

        VerticalLayout propContainer = new VerticalLayout();
            propContainer.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
            propContainer.setAlignItems(Alignment.STRETCH);
        propContainer.add(fieldMap.get(CfgPropName.APP_LOCALE.getName()));
        propContainer.add(fieldMap.get(CfgPropName.APP_PROJ_ROOT_LOCAL.getName()));
        propContainer.add(fieldMap.get(CfgPropName.APP_PROJ_ROOT_SERVER.getName()));
        propContainer.add(fieldMap.get(CfgPropName.APP_DOC_ROOT_LOCAL.getName()));
        propContainer.add(fieldMap.get(CfgPropName.APP_DOC_ROOT_SERVER.getName()));
        propContainer.add(fieldMap.get(CfgPropName.APP_KOEF_POJIST.getName()));
        propContainer.add(fieldMap.get(CfgPropName.APP_KOEF_REZIE.getName()));

        HorizontalLayout propsFooter = new HorizontalLayout();
        propsFooter.add(new Button("Uložit"));
        propsFooter.add(new Button("Vrátit zpět"));

        this.add(propsHeader, propContainer, propsFooter);
    }
}
