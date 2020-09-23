package eu.japtor.vizman.ui.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
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
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static eu.japtor.vizman.backend.utils.VzmFormatUtils.stringLocalToBigDecimal;


@Permissions(
        {Perm.VIEW_ALL, Perm.MODIFY_ALL}
)
@SpringComponent
@UIScope
public class CfgPropsFormView extends VerticalLayout implements HasLogger {

    private Map<String, TextField> fieldMap = new LinkedHashMap<>();
    private Binder<AppCfg> binder = new Binder<>();
    private AppCfg appCfg = new AppCfg();

    private FormLayout form;

    @Autowired
    public CfgPropRepo cfgPropRepo;

    @Autowired
    public CfgPropsCache cfgPropsCache;


    public CfgPropsFormView() {
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
//                field.setLabel(cfgProp.getLabel());
                populateBeanAndBindField(cfgPropName, cfgProp.getValue(), cfgProp.getValueDecimal(), field);
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
            if (cfgProp.getType().equals("DECIMAL")) {
                cfgProp.setValueDecimal(stringLocalToBigDecimal(field.getValue()));
            } else {
                cfgProp.setValue(field.getValue());
            }
            try {
                cfgPropRepo.saveAndFlush(cfgProp);
//                Notification.show("Nastavení aplikace uloženo.", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                getLogger().error("Error while saving config property {}", propName);
                throw e;
            }
        }
        cfgPropsCache.loadPropsFromDb();
        return cfgProp;
    }

    private void populateBeanAndBindField(CfgPropName cfgPropName, String value, BigDecimal valueDecimal, TextField field) {

        switch (cfgPropName) {
            case APP_LOCALE:
                appCfg.setAppLocale(value);
                field.setWidth("12em");
                binder.forField(field).bind(AppCfg::getAppLocale, null);
                break;
            case APP_DOC_ROOT_LOCAL:
                appCfg.setAppDocRootLocal(value);
                field.setWidth("30em");
                binder.forField(field).bind(AppCfg::getAppDocRootLocal, AppCfg::setAppDocRootLocal);
                break;
            case APP_DOC_ROOT_SERVER:
                appCfg.setAppDocRootServer(value);
                field.setWidth("30em");
                binder.forField(field).bind(AppCfg::getAppDocRootServer, AppCfg::setAppDocRootServer);
                break;
            case APP_PROJ_ROOT_LOCAL:
                appCfg.setAppProjRootLocal(value);
                field.setWidth("30em");
                binder.forField(field).bind(AppCfg::getAppProjRootLocal, AppCfg::setAppProjRootLocal);
                break;
            case APP_PROJ_ROOT_SERVER:
                appCfg.setAppProjRootServer(value);
                field.setWidth("30em");
                binder.forField(field).bind(AppCfg::getAppProjRootServer, AppCfg::setAppProjRootServer);
                break;
            case APP_KOEF_REZIE:
                appCfg.setAppKoefRezie(valueDecimal);
                field.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                field.setWidth("8em");
                binder.forField(field)
                        .asRequired("Koeficient režie musí být zadán")
                        .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                        .bind(AppCfg::getAppKoefRezie, AppCfg::setAppKoefRezie)
                ;
                break;
            case APP_KOEF_POJIST:
                appCfg.setAppKoefPojist(valueDecimal);
                field.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                field.setWidth("8em");
                binder.forField(field)
                        .asRequired("Koeficient  pojištění musí být zadán")
                        .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                        .bind(AppCfg::getAppKoefPojist, AppCfg::setAppKoefPojist)
                ;
                break;
            case APP_KURZ_CZK_EUR:
                appCfg.setAppKurzCzkEur(valueDecimal);
                field.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
                field.setWidth("8em");
                binder.forField(field)
                        .asRequired("Kurz musí být zadán")
                        .withConverter(VzmFormatUtils.bigDecimalMoneyConverter)
                        .bind(AppCfg::getAppKurzCzkEur, AppCfg::setAppKurzCzkEur);
                break;
        }
    }


    private void buildPropsContainer() {

//        setClassName("view-container");
        setAlignItems(Alignment.STRETCH);

        Component propsHeader = new H4("APLIKACE");

        addFormItem(CfgPropName.APP_LOCALE.getName());
        addFormItem(CfgPropName.APP_DOC_ROOT_SERVER.getName());
        addFormItem(CfgPropName.APP_DOC_ROOT_LOCAL.getName());
        addFormItem(CfgPropName.APP_PROJ_ROOT_SERVER.getName());
        addFormItem(CfgPropName.APP_PROJ_ROOT_LOCAL.getName());
        addFormItem(CfgPropName.APP_KOEF_POJIST.getName());
        addFormItem(CfgPropName.APP_KOEF_REZIE.getName());
        addFormItem(CfgPropName.APP_KURZ_CZK_EUR.getName());

        HorizontalLayout propsButtonBar = new HorizontalLayout();
        propsButtonBar.add(buildSaveButton());
        propsButtonBar.add(buildCancelButton());

        this.add(propsHeader, form, propsButtonBar);
    }


    private void addFormItem(final String propName) {
        form.addFormItem(fieldMap.get(propName), propName)
                .getElement().getStyle()
                .set("--vaadin-form-item-label-width", "15em");
    }
}
