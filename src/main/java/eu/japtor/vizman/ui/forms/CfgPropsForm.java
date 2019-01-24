package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.japtor.vizman.app.AppCfg;
import eu.japtor.vizman.app.CfgPropName;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.app.security.Permissions;
import eu.japtor.vizman.backend.entity.CfgProp;
import eu.japtor.vizman.backend.entity.Perm;
import eu.japtor.vizman.backend.repository.CfgPropRepo;
import eu.japtor.vizman.backend.service.CfgPropsCache;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;


@Permissions({Perm.VIEW_ALL, Perm.MODIFY_ALL
})
@SpringComponent
@UIScope
public class CfgPropsForm extends VerticalLayout implements HasLogger {

    private Map<String, TextField> fieldMap = new LinkedHashMap<>();
    private Binder<AppCfg> binder = new Binder<>();
    private AppCfg appCfg = new AppCfg();

    private FormLayout form;

    @Autowired
    public CfgPropRepo cfgPropRepo;

    @Autowired
    public CfgPropsCache cfgPropsCache;


    public CfgPropsForm() {
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        setAlignItems(Alignment.STRETCH);
//        this.setWidth("100%");

        form = new FormLayout();
        form.setWidth("600px");
//        form.setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1)
////                , new FormLayout.ResponsiveStep("20em", 4)
//        );
    }

    @PostConstruct
    public void init() {
        try {
            initFieldMap();
            buildPropsContainer();
            binder.readBean(appCfg);
        } catch (Exception e) {
            getLogger().error("Error while initializing application configuration form", e);
            new OkDialog().open("Konfigurace aplikace"
                    , "Chyba při inicializaci formláře konfigurace aplikace", "");
        }

    }

    private void initFieldMap() {

        for (CfgPropName cfgPropName : CfgPropName.values()) {
            String propName = cfgPropName.getName();
            CfgProp cfgProp = cfgPropRepo.findByName(propName);

            if (null != cfgProp) {
                TextField field = new TextField();
                field.setId(propName);
                field.setReadOnly(cfgProp.getRo());
                field.setWidth("20em");
                field.setWidth("20em");
//                field.setLabel(cfgProp.getLabel());
                populateBeanAndBindField(cfgPropName, cfgProp.getValue(), field);
                fieldMap.put(propName, field);
            }
        }
    }

    private Component buildSaveButton() {
        Button saveButton = new Button("Uložit");
        saveButton.addClickListener(event -> {
            saveAllCfgProps();
            syncCfgPropCache();
        });
        return saveButton;
    }

    private Component buildCancelButton() {
        Button cancelButton = new Button("Vrátit změny");
        cancelButton.addClickListener(event -> {
            try {
                initFieldMap();
                binder.readBean(appCfg);
                Notification.show("Změny v konfiguraci aplikace vráceny zpět.", 2000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                getLogger().error("Error while reading original application configuration from database", e);
                new OkDialog().open("Konfigurace aplikace"
                        , "Chyba při načítání původní konfigurace aplikace z databáze", "");
            }
        });
        return cancelButton;
    }

    private void syncCfgPropCache() {
        try {
            cfgPropsCache.loadPropsFromDb();
        } catch(Exception e) {
            getLogger().error("Error while reading application configuration from database", e);
            new OkDialog().open("Konfigurace aplikace"
                    , "Chyba při načítání konfigurace aplikace z databáze", "");
        }
    }

    private void saveAllCfgProps() {
        try {
            for (CfgPropName cfgPropName : CfgPropName.values()) {
                saveCfgProp(cfgPropName.getName());
            }
            Notification.show("Konfigurace aplikace uložena.", 2000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            getLogger().error("Error while saving config property", e);
            new OkDialog().open("Konfigurace aplikace"
                    , "Chyba při ukládání konfigurace aplikace", "");
        }
    }

    private CfgProp saveCfgProp(String propName) {
        TextField field = fieldMap.get(propName);
        CfgProp cfgProp = cfgPropRepo.findByName(propName);
        if (null != field && null != cfgProp) {
            cfgProp.setValue(field.getValue());
            try {
                cfgPropRepo.saveAndFlush(cfgProp);
//                Notification.show("Nastavení aplikace uloženo.", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                getLogger().error("Error while saving config property {}", propName);
                throw e;
            }
        }
        return cfgProp;
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

//        VerticalLayout propContainer = new VerticalLayout();
//            propContainer.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
//            propContainer.setAlignItems(Alignment.STRETCH);
//        propContainer.add(fieldMap.get(CfgPropName.APP_LOCALE.getName()));
//        propContainer.add(fieldMap.get(CfgPropName.APP_PROJ_ROOT_LOCAL.getName()));
//        propContainer.add(fieldMap.get(CfgPropName.APP_PROJ_ROOT_SERVER.getName()));
//        propContainer.add(fieldMap.get(CfgPropName.APP_DOC_ROOT_LOCAL.getName()));
//        propContainer.add(fieldMap.get(CfgPropName.APP_DOC_ROOT_SERVER.getName()));
//        propContainer.add(fieldMap.get(CfgPropName.APP_KOEF_POJIST.getName()));
//        propContainer.add(fieldMap.get(CfgPropName.APP_KOEF_REZIE.getName()));

        addFormItem(CfgPropName.APP_LOCALE.getName());
        addFormItem(CfgPropName.APP_DOC_ROOT_SERVER.getName());
        addFormItem(CfgPropName.APP_DOC_ROOT_LOCAL.getName());
        addFormItem(CfgPropName.APP_PROJ_ROOT_SERVER.getName());
        addFormItem(CfgPropName.APP_PROJ_ROOT_LOCAL.getName());
        addFormItem(CfgPropName.APP_KOEF_POJIST.getName());
        addFormItem(CfgPropName.APP_KOEF_REZIE.getName());

        HorizontalLayout propsButtonBar = new HorizontalLayout();
        propsButtonBar.add(buildSaveButton());
        propsButtonBar.add(buildCancelButton());

//        this.add(propsHeader, propContainer, propsFooter);
        this.add(propsHeader, form, propsButtonBar);
    }


    private void addFormItem(final String propName) {
        form.addFormItem(fieldMap.get(propName), propName)
                .getElement().getStyle()
                .set("--vaadin-form-item-label-width", "15em");
    }
}
